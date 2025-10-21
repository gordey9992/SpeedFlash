package com.example.speedflash.integration;

import com.example.speedflash.SpeedFlash;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class WebServer {
    private final SpeedFlash plugin;
    private HttpServer server;
    private int port;

    public WebServer(SpeedFlash plugin) {
        this.plugin = plugin;
        this.port = plugin.getConfig().getInt("web.port", 8080);
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            
            // Основной сайт
            server.createContext("/", new WebsiteHandler());
            
            // API endpoints
            server.createContext("/api/info", new ApiInfoHandler());
            server.createContext("/api/stats", new ApiStatsHandler());
            server.createContext("/api/players", new ApiPlayersHandler());
            server.createContext("/api/plugin/reload", new ApiReloadHandler());
            
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
            
            plugin.getLogger().info("🌐 Веб-сервер запущен на порту: " + port);
            plugin.getLogger().info("🌐 Сайт доступен: http://localhost:" + port);
            plugin.getLogger().info("🌐 API доступен: http://localhost:" + port + "/api/info");
            
        } catch (IOException e) {
            plugin.getLogger().warning("❌ Не удалось запустить веб-сервер: " + e.getMessage());
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            plugin.getLogger().info("🌐 Веб-сервер остановлен");
        }
    }

    private class WebsiteHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            
            if (path.equals("/") || path.equals("/index.html")) {
                serveIndexPage(exchange);
            } else {
                serve404(exchange);
            }
        }

        private void serveIndexPage(HttpExchange exchange) throws IOException {
            try {
                InputStream htmlStream = plugin.getResource("web/index.html");
                if (htmlStream == null) {
                    serve404(exchange);
                    return;
                }

                String htmlContent = new String(htmlStream.readAllBytes(), StandardCharsets.UTF_8);
                
                // Заменяем динамические данные
                htmlContent = htmlContent.replace("{version}", plugin.getDescription().getVersion())
                                       .replace("{author}", String.join(", ", plugin.getDescription().getAuthors()))
                                       .replace("{players}", String.valueOf(Bukkit.getOnlinePlayers().size()))
                                       .replace("{maxPlayers}", String.valueOf(Bukkit.getMaxPlayers()));

                sendResponse(exchange, htmlContent, "text/html; charset=utf-8");
                
            } catch (Exception e) {
                plugin.getLogger().warning("❌ Ошибка загрузки веб-страницы: " + e.getMessage());
                serve500(exchange);
            }
        }
    }

    private class ApiInfoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendError(exchange, 405, "Method Not Allowed");
                return;
            }

            JSONObject response = new JSONObject();
            response.put("plugin", "SpeedFlash");
            response.put("version", plugin.getDescription().getVersion());
            response.put("authors", plugin.getDescription().getAuthors());
            response.put("website", "http://localhost:" + port);
            response.put("serverVersion", Bukkit.getVersion());
            response.put("bukkitVersion", Bukkit.getBukkitVersion());

            sendJsonResponse(exchange, response.toJSONString());
        }
    }

    private class ApiStatsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendError(exchange, 405, "Method Not Allowed");
                return;
            }

            JSONObject response = new JSONObject();
            response.put("onlinePlayers", Bukkit.getOnlinePlayers().size());
            response.put("maxPlayers", Bukkit.getMaxPlayers());
            response.put("tps", getFormattedTPS());
            response.put("uptime", System.currentTimeMillis() - plugin.getStartTime());
            response.put("worlds", Bukkit.getWorlds().size());
            response.put("plugins", Bukkit.getPluginManager().getPlugins().length);

            sendJsonResponse(exchange, response.toJSONString());
        }
    }

    private class ApiPlayersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendError(exchange, 405, "Method Not Allowed");
                return;
            }

            JSONObject response = new JSONObject();
            JSONObject players = new JSONObject();
            
            Bukkit.getOnlinePlayers().forEach(player -> {
                JSONObject playerInfo = new JSONObject();
                playerInfo.put("name", player.getName());
                playerInfo.put("uuid", player.getUniqueId().toString());
                playerInfo.put("world", player.getWorld().getName());
                playerInfo.put("gamemode", player.getGameMode().name());
                playerInfo.put("health", player.getHealth());
                playerInfo.put("level", player.getLevel());
                playerInfo.put("ping", player.getPing());
                players.put(player.getName(), playerInfo);
            });

            response.put("players", players);
            response.put("total", Bukkit.getOnlinePlayers().size());

            sendJsonResponse(exchange, response.toJSONString());
        }
    }

    private class ApiReloadHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendError(exchange, 405, "Method Not Allowed");
                return;
            }

            // Проверка API ключа (можно добавить в конфиг)
            String apiKey = exchange.getRequestHeaders().getFirst("X-API-Key");
            if (apiKey == null || !apiKey.equals(plugin.getConfig().getString("web.api-key", "default-key"))) {
                sendError(exchange, 401, "Unauthorized");
                return;
            }

            // Перезагрузка плагина в основном потоке
            Bukkit.getScheduler().runTask(plugin, () -> {
                try {
                    plugin.getConfigManager().reloadConfig();
                    plugin.getMessages().reloadMessages();
                    plugin.getEffectsManager().loadEffects();
                    
                    JSONObject response = new JSONObject();
                    response.put("status", "success");
                    response.put("message", "Plugin reloaded successfully");
                    sendJsonResponse(exchange, response.toJSONString());
                    
                } catch (Exception e) {
                    try {
                        JSONObject error = new JSONObject();
                        error.put("status", "error");
                        error.put("message", e.getMessage());
                        sendJsonResponse(exchange, error.toJSONString(), 500);
                    } catch (IOException ex) {
                        plugin.getLogger().warning("❌ API Error: " + ex.getMessage());
                    }
                }
            });
        }
    }

    // Вспомогательные методы
    private void sendResponse(HttpExchange exchange, String content, String contentType) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        byte[] response = content.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, response.length);
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    private void sendJsonResponse(HttpExchange exchange, String json) throws IOException {
        sendResponse(exchange, json, "application/json; charset=utf-8");
    }

    private void sendJsonResponse(HttpExchange exchange, String json, int code) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        byte[] response = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, response.length);
        
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    private void sendError(HttpExchange exchange, int code, String message) throws IOException {
        JSONObject error = new JSONObject();
        error.put("error", true);
        error.put("code", code);
        error.put("message", message);
        sendJsonResponse(exchange, error.toJSONString(), code);
    }

    private void serve404(HttpExchange exchange) throws IOException {
        sendError(exchange, 404, "Page Not Found");
    }

    private void serve500(HttpExchange exchange) throws IOException {
        sendError(exchange, 500, "Internal Server Error");
    }

    private double getFormattedTPS() {
        try {
            // Получаем TPS через рефлексию (для Paper/Spigot)
            Object server = Bukkit.getServer();
            Object minecraftServer = server.getClass().getMethod("getServer").invoke(server);
            double[] tps = (double[]) minecraftServer.getClass().getField("recentTps").get(minecraftServer);
            return Math.min(20.0, tps[0]); // Берем последнее значение TPS
        } catch (Exception e) {
            return 20.0; // Значение по умолчанию
        }
    }
}