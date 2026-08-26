import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ChessServe {
    
    static class Game {
        String whitePlayer = null;
        String blackPlayer = null;
        volatile String currentTurn = "white";
        List<String> moves = Collections.synchronizedList(new ArrayList<>());
        volatile long whiteTime = 600000;
        volatile long blackTime = 600000;
        volatile long lastTime = System.currentTimeMillis();
        volatile boolean started = false;
        volatile boolean ended = false;
        volatile String winner = null;
        volatile int moveVersion = 0;
        String[][] boardState = null;
        volatile String cachedStateJson = null;
        volatile long cacheTimestamp = 0;
        volatile String gameId;
        List<String> chats = Collections.synchronizedList(new ArrayList<>());
        
        // Synchronized room options
        volatile String gameVariant = "standard";
        volatile String boardTheme = "default";
        volatile int timeControl = 5;
        
        Game(String id) {
            this.gameId = id;
            this.boardState = initializeBoard(this.gameVariant);
        }
    }
    
    static class GameManager {
        private static final ConcurrentHashMap<String, Game> activeGames = new ConcurrentHashMap<>();
        private static final Map<String, Long> lastAccess = new ConcurrentHashMap<>();
        private static final long MAX_INACTIVE_TIME = 7200000;
        private static final int MAX_GAMES = 1000;
        
        public static Game getGame(String gameId) {
            lastAccess.put(gameId, System.currentTimeMillis());
            return activeGames.get(gameId);
        }
        
        public static Game createOrGetGame(String gameId) {
            lastAccess.put(gameId, System.currentTimeMillis());
            return activeGames.computeIfAbsent(gameId, id -> {
                if (activeGames.size() >= MAX_GAMES) {
                    cleanupOldGames();
                }
                return new Game(id);
            });
        }
        
        public static void removeGame(String gameId) {
            activeGames.remove(gameId);
            lastAccess.remove(gameId);
        }
        
        public static void cleanupOldGames() {
            long now = System.currentTimeMillis();
            Iterator<Map.Entry<String, Game>> it = activeGames.entrySet().iterator();
            
            while (it.hasNext()) {
                Map.Entry<String, Game> entry = it.next();
                Long lastAccessTime = lastAccess.get(entry.getKey());
                
                if (lastAccessTime != null && (now - lastAccessTime) > MAX_INACTIVE_TIME) {
                    it.remove();
                    lastAccess.remove(entry.getKey());
                    System.out.println("Removed inactive game: " + entry.getKey());
                }
            }
        }
        
        public static int getGameCount() {
            return activeGames.size();
        }
        
        public static Set<String> getAllGameIds() {
            return new HashSet<>(activeGames.keySet());
        }
    }
    
    static final ExecutorService executor = Executors.newFixedThreadPool(50);
    static final AtomicInteger requestCounter = new AtomicInteger(0);
    static final int PORT = System.getenv("PORT") != null ? Integer.parseInt(System.getenv("PORT")) : 9090;
    

    public static void main(String[] args) throws Exception {
        System.setProperty("sun.net.httpserver.maxReqTime", "30");
        System.setProperty("sun.net.httpserver.maxRspTime", "30");
        
        startCleanupThread();
        
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.setExecutor(executor);
        
        server.createContext("/", exchange -> {
            try {
                handleRootRequest(exchange);
            } catch (Exception e) {
                sendError(exchange, 500, "Internal server error: " + e.getMessage());
            }
        });
        
        server.createContext("/join", exchange -> {
            try {
                handleJoinRequest(exchange);
            } catch (Exception e) {
                sendError(exchange, 500, "Join error: " + e.getMessage());
            }
        });
        
        server.createContext("/validateMove", exchange -> {
            try {
                handleValidateMoveRequest(exchange);
            } catch (Exception e) {
                sendError(exchange, 500, "Validation error: " + e.getMessage());
            }
        });
        
        server.createContext("/sendMove", exchange -> {
            try {
                handleSendMoveRequest(exchange);
            } catch (Exception e) {
                sendError(exchange, 500, "Move error: " + e.getMessage());
            }
        });
        
        server.createContext("/getMoves", exchange -> {
            try {
                handleGetMovesRequest(exchange);
            } catch (Exception e) {
                sendError(exchange, 500, "Get moves error: " + e.getMessage());
            }
        });
        
        server.createContext("/getState", exchange -> {
            try {
                handleGetStateRequest(exchange);
            } catch (Exception e) {
                sendError(exchange, 500, "Get state error: " + e.getMessage());
            }
        });
        
        server.createContext("/resetGame", exchange -> {
            try {
                handleResetGameRequest(exchange);
            } catch (Exception e) {
                sendError(exchange, 500, "Reset error: " + e.getMessage());
            }
        });
        
        server.createContext("/health", exchange -> {
            try {
                handleHealthRequest(exchange);
            } catch (Exception e) {
                sendError(exchange, 500, "Health error: " + e.getMessage());
            }
        });
        
        server.createContext("/cleanup", exchange -> {
            try {
                handleCleanupRequest(exchange);
            } catch (Exception e) {
                sendError(exchange, 500, "Cleanup error: " + e.getMessage());
            }
        });
        
        server.createContext("/create", exchange -> {
            try {
                handleCreateGameRequest(exchange);
            } catch (Exception e) {
                sendError(exchange, 500, "Create error: " + e.getMessage());
            }
        });
        
        server.createContext("/listGames", exchange -> {
            try {
                handleListGamesRequest(exchange);
            } catch (Exception e) {
                sendError(exchange, 500, "List games error: " + e.getMessage());
            }
        });
        
        server.createContext("/ping", exchange -> {
            try {
                handlePingRequest(exchange);
            } catch (Exception e) {
                sendError(exchange, 500, "Ping error: " + e.getMessage());
            }
        });
        
        server.createContext("/sendChat", exchange -> {
            try {
                handleSendChatRequest(exchange);
            } catch (Exception e) {
                sendError(exchange, 500, "Send chat error: " + e.getMessage());
            }
        });
        
        server.createContext("/resign", exchange -> {
            try {
                handleResignRequest(exchange);
            } catch (Exception e) {
                sendError(exchange, 500, "Resign error: " + e.getMessage());
            }
        });
        
        server.start();
        System.out.println("Chess Server started successfully on port " + PORT);
        System.out.println("Server URL: http://localhost:" + PORT);
        System.out.println("Thread pool: 50 threads ready");
        System.out.println("Memory management: Active games limit: 1000");
        System.out.println("Available endpoints:");
        System.out.println("   - /health - Server health check");
        System.out.println("   - /ping - Quick server test");
        System.out.println("   - /create - Create new game");
        System.out.println("   - /join - Join existing game");
        System.out.println("   - /listGames - List all active games");
        System.out.println("   - /sendMove - Send a move");
        System.out.println("   - /getState - Get game state");
        System.out.println("   - /cleanup - Manual cleanup");
    }
    
    private static void startCleanupThread() {
        Thread cleanupThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(300000);
                    GameManager.cleanupOldGames();
                    System.out.println("Auto-cleanup completed. Active games: " + GameManager.getGameCount());
                } catch (InterruptedException e) {
                    System.out.println("Cleanup thread interrupted");
                    break;
                } catch (Exception e) {
                    System.out.println("Cleanup error: " + e.getMessage());
                }
            }
        }, "Auto-Cleanup-Thread");
        cleanupThread.setDaemon(true);
        cleanupThread.start();
    }
    
    private static void handleRootRequest(HttpExchange exchange) throws IOException {
        requestCounter.incrementAndGet();
        setCorsHeaders(exchange);
        
        String path = exchange.getRequestURI().getPath();
        
        if (path.equals("/") || path.equals("/index.html")) {
            try {
                File file = findIndexHtml();
                if (file == null) {
                    String html = "<html><body><h1>Chess Server</h1><p>Server is running!</p>" +
                                 "<p>Warning: index.html not found in frontend directory.</p></body></html>";
                    sendResponse(exchange, 200, html, "text/html");
                    return;
                }
                
                byte[] data = java.nio.file.Files.readAllBytes(file.toPath());
                exchange.getResponseHeaders().set("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(200, data.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(data);
                }
            } catch (Exception e) {
                String error = "File not found: " + e.getMessage();
                sendResponse(exchange, 404, error, "text/plain");
            }
        } else {
            sendResponse(exchange, 404, "Not Found", "text/plain");
        }
        exchange.close();
    }
    
    private static void handleJoinRequest(HttpExchange exchange) throws IOException {
        requestCounter.incrementAndGet();
        setCorsHeaders(exchange);
        
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = parseQuery(query);
        String gameId = params.get("gameId");
        String playerId = params.get("playerId");
        
        if (gameId == null || playerId == null) {
            sendResponse(exchange, 400, "Need gameId and playerId", "text/plain");
            return;
        }
        
        if (gameId.length() > 20 || playerId.length() > 50) {
            sendResponse(exchange, 400, "gameId or playerId too long", "text/plain");
            return;
        }
        
        Game game = GameManager.createOrGetGame(gameId);
        String role;
        
        synchronized(game) {
            if (game.whitePlayer == null) {
                game.whitePlayer = playerId;
                role = "white";
                
                String variantParam = params.get("gameVariant");
                if (variantParam != null) {
                    game.gameVariant = variantParam;
                }
                String themeParam = params.get("boardTheme");
                if (themeParam != null) {
                    game.boardTheme = themeParam;
                }
                String timeParam = params.get("timeControl");
                if (timeParam != null) {
                    try {
                        game.timeControl = Integer.parseInt(timeParam);
                    } catch (NumberFormatException e) {
                        game.timeControl = 5;
                    }
                }
                
                if (game.timeControl == 0) {
                    game.whiteTime = 99999999999L;
                    game.blackTime = 99999999999L;
                } else {
                    game.whiteTime = (long) game.timeControl * 60 * 1000;
                    game.blackTime = (long) game.timeControl * 60 * 1000;
                }
                
                game.boardState = initializeBoard(game.gameVariant);
                game.lastTime = System.currentTimeMillis();
                
                System.out.println("Player '" + playerId + "' joined game '" + gameId + "' as WHITE. Variant: " + game.gameVariant + ", Theme: " + game.boardTheme + ", TimeControl: " + game.timeControl);
            } else if (game.blackPlayer == null) {
                if (playerId.equals(game.whitePlayer)) {
                    playerId = playerId + "_2";
                }
                game.blackPlayer = playerId;
                role = "black:" + playerId;
                game.started = true;
                game.lastTime = System.currentTimeMillis();
                game.cachedStateJson = null;
                System.out.println("Player '" + playerId + "' joined game '" + gameId + "' as BLACK - Game STARTED!");
            } else if (playerId.equals(game.whitePlayer)) {
                role = "white";
                System.out.println("Player '" + playerId + "' reconnected to game '" + gameId + "' as WHITE");
            } else if (playerId.equals(game.blackPlayer)) {
                role = "black";
                System.out.println("Player '" + playerId + "' reconnected to game '" + gameId + "' as BLACK");
            } else {
                role = "full";
                System.out.println("Game '" + gameId + "' is full. Player '" + playerId + "' rejected");
            }
        }
        
        sendResponse(exchange, 200, role, "text/plain");
    }
    
    private static void handleCreateGameRequest(HttpExchange exchange) throws IOException {
        requestCounter.incrementAndGet();
        setCorsHeaders(exchange);
        
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = parseQuery(query);
        String gameId = params.get("gameId");
        String playerId = params.get("playerId");
        
        if (gameId == null) {
            gameId = generateGameId();
        }
        
        if (playerId == null) {
            playerId = "Player_" + System.currentTimeMillis() % 1000;
        }
        
        Game game = GameManager.createOrGetGame(gameId);
        
        synchronized(game) {
            if (game.whitePlayer == null) {
                game.whitePlayer = playerId;
            }
            game.moves.clear();
            game.chats.clear();
            game.currentTurn = "white";
            game.whiteTime = 600000;
            game.blackTime = 600000;
            game.lastTime = System.currentTimeMillis();
            game.started = false;
            game.ended = false;
            game.winner = null;
            game.moveVersion = 0;
            game.boardState = initializeBoard();
            game.cachedStateJson = null;
        }
        
        String response = "{\"gameId\":\"" + gameId + "\",\"player\":\"" + playerId + "\",\"color\":\"white\"}";
        sendResponse(exchange, 200, response, "application/json");
        System.out.println("Created new game: " + gameId + " for player: " + playerId);
    }
    
    private static void handleValidateMoveRequest(HttpExchange exchange) throws IOException {
        requestCounter.incrementAndGet();
        setCorsHeaders(exchange);
        
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = parseQuery(query);
        String gameId = params.get("gameId");
        String playerId = params.get("playerId");
        String move = params.get("move");
        
        if (gameId == null || playerId == null || move == null) {
            sendResponse(exchange, 400, "{\"valid\":false,\"reason\":\"Missing parameters\"}", "application/json");
            return;
        }
        
        Game game = GameManager.getGame(gameId);
        if (game == null) {
            sendResponse(exchange, 404, "{\"valid\":false,\"reason\":\"Game not found\"}", "application/json");
            return;
        }
        
        if (game.ended) {
            sendResponse(exchange, 400, "{\"valid\":false,\"reason\":\"Game ended\"}", "application/json");
            return;
        }
        
        boolean isWhite = playerId.equals(game.whitePlayer);
        boolean isBlack = playerId.equals(game.blackPlayer);
        String playerColor = isWhite ? "white" : (isBlack ? "black" : null);
        
        if (playerColor == null) {
            sendResponse(exchange, 403, "{\"valid\":false,\"reason\":\"Not a player in this game\"}", "application/json");
            return;
        }
        
        if (!playerColor.equals(game.currentTurn)) {
            sendResponse(exchange, 403, "{\"valid\":false,\"reason\":\"Not your turn\"}", "application/json");
            return;
        }
        
        String[] parts = move.split(",");
        if (parts.length != 4) {
            sendResponse(exchange, 400, "{\"valid\":false,\"reason\":\"Bad move format\"}", "application/json");
            return;
        }
        
        try {
            int fromRow = Integer.parseInt(parts[0]);
            int fromCol = Integer.parseInt(parts[1]);
            int toRow = Integer.parseInt(parts[2]);
            int toCol = Integer.parseInt(parts[3]);
            
            if (fromRow < 0 || fromRow > 7 || fromCol < 0 || fromCol > 7 ||
                toRow < 0 || toRow > 7 || toCol < 0 || toCol > 7) {
                sendResponse(exchange, 400, "{\"valid\":false,\"reason\":\"Invalid coordinates\"}", "application/json");
                return;
            }
            
            String piece = game.boardState[fromRow][fromCol];
            if (piece == null || piece.isEmpty()) {
                sendResponse(exchange, 400, "{\"valid\":false,\"reason\":\"No piece at source\"}", "application/json");
                return;
            }
            
            if ((playerColor.equals("white") && !piece.startsWith("w")) ||
                (playerColor.equals("black") && !piece.startsWith("b"))) {
                sendResponse(exchange, 400, "{\"valid\":false,\"reason\":\"Cannot move opponent's piece\"}", "application/json");
                return;
            }
            
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "{\"valid\":false,\"reason\":\"Invalid numbers in move\"}", "application/json");
            return;
        }
        
        sendResponse(exchange, 200, "{\"valid\":true,\"reason\":\"Move is valid\"}", "application/json");
    }
    
    private static void handleSendMoveRequest(HttpExchange exchange) throws IOException {
        requestCounter.incrementAndGet();
        setCorsHeaders(exchange);
        
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = parseQuery(query);
        String gameId = params.get("gameId");
        String playerId = params.get("playerId");
        String move = params.get("move");
        
        if (gameId == null || playerId == null || move == null) {
            sendResponse(exchange, 400, "Missing parameters", "text/plain");
            return;
        }
        
        Game game = GameManager.getGame(gameId);
        if (game == null) {
            sendResponse(exchange, 404, "Game not found", "text/plain");
            return;
        }
        
        boolean moveAccepted = false;
        synchronized(game) {
            if (game.ended) {
                sendResponse(exchange, 400, "Game ended", "text/plain");
                return;
            }
            
            boolean isWhite = playerId.equals(game.whitePlayer);
            boolean isBlack = playerId.equals(game.blackPlayer);
            String playerColor = isWhite ? "white" : (isBlack ? "black" : null);
            
            if (playerColor == null) {
                sendResponse(exchange, 403, "Not a player in this game", "text/plain");
                return;
            }
            
            if (!playerColor.equals(game.currentTurn)) {
                sendResponse(exchange, 403, "Not your turn", "text/plain");
                return;
            }
            
            String[] parts = move.split(",");
            if (parts.length != 4) {
                sendResponse(exchange, 400, "Bad move format", "text/plain");
                return;
            }
            
            try {
                int fromRow = Integer.parseInt(parts[0]);
                int fromCol = Integer.parseInt(parts[1]);
                int toRow = Integer.parseInt(parts[2]);
                int toCol = Integer.parseInt(parts[3]);
                
                if (fromRow < 0 || fromRow > 7 || fromCol < 0 || fromCol > 7 ||
                    toRow < 0 || toRow > 7 || toCol < 0 || toCol > 7) {
                    sendResponse(exchange, 400, "Invalid coordinates", "text/plain");
                    return;
                }
                
                String piece = game.boardState[fromRow][fromCol];
                if (piece == null || piece.isEmpty()) {
                    sendResponse(exchange, 400, "No piece at source", "text/plain");
                    return;
                }
                
                if ((playerColor.equals("white") && !piece.startsWith("w")) ||
                    (playerColor.equals("black") && !piece.startsWith("b"))) {
                    sendResponse(exchange, 400, "Cannot move opponent's piece", "text/plain");
                    return;
                }
                
                game.boardState[toRow][toCol] = piece;
                game.boardState[fromRow][fromCol] = "";
                
                long now = System.currentTimeMillis();
                long timePassed = now - game.lastTime;
                
                if (game.currentTurn.equals("white")) {
                    game.whiteTime -= timePassed;
                    if (game.whiteTime <= 0) {
                        game.whiteTime = 0;
                        game.ended = true;
                        game.winner = "black";
                        System.out.println("Time out! Game '" + gameId + "': BLACK wins (white ran out of time)");
                    }
                } else {
                    game.blackTime -= timePassed;
                    if (game.blackTime <= 0) {
                        game.blackTime = 0;
                        game.ended = true;
                        game.winner = "white";
                        System.out.println("Time out! Game '" + gameId + "': WHITE wins (black ran out of time)");
                    }
                }
                
                game.moves.add(move);
                game.currentTurn = game.currentTurn.equals("white") ? "black" : "white";
                game.lastTime = now;
                game.moveVersion++;
                game.cachedStateJson = null;
                
                moveAccepted = true;
                
                System.out.println("Move in game '" + gameId + "': " + move + " by " + playerId);
                
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "Invalid numbers in move", "text/plain");
                return;
            }
        }
        
        if (moveAccepted) {
            sendResponse(exchange, 200, "OK", "text/plain");
        }
    }
    
    private static void handleGetMovesRequest(HttpExchange exchange) throws IOException {
        requestCounter.incrementAndGet();
        setCorsHeaders(exchange);
        
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = parseQuery(query);
        String gameId = params.get("gameId");
        
        Game game = GameManager.getGame(gameId);
        if (game == null) {
            sendResponse(exchange, 404, "", "text/plain");
            return;
        }
        
        String moves;
        synchronized(game.moves) {
            moves = String.join(";", game.moves);
        }
        
        sendResponse(exchange, 200, moves, "text/plain");
    }
    
    private static void handleGetStateRequest(HttpExchange exchange) throws IOException {
        requestCounter.incrementAndGet();
        setCorsHeaders(exchange);
        
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = parseQuery(query);
        String gameId = params.get("gameId");
        
        Game game = GameManager.getGame(gameId);
        if (game == null) {
            sendResponse(exchange, 404, "{\"error\":\"Game not found\"}", "application/json");
            return;
        }
        
        String jsonResponse;
        
        if (game.cachedStateJson != null && 
            (System.currentTimeMillis() - game.cacheTimestamp) < 100) {
            jsonResponse = game.cachedStateJson;
        } else {
            StringBuilder sb = new StringBuilder();
            
            long whiteTimeNow = game.whiteTime;
            long blackTimeNow = game.blackTime;
            boolean gameEndedNow = game.ended;
            String winnerNow = game.winner;
            
            if (game.started && !game.ended) {
                long now = System.currentTimeMillis();
                long timePassed = now - game.lastTime;
                
                if (game.currentTurn.equals("white")) {
                    whiteTimeNow = Math.max(0, game.whiteTime - timePassed);
                    if (whiteTimeNow <= 0) {
                        whiteTimeNow = 0;
                        gameEndedNow = true;
                        winnerNow = "black";
                    }
                } else {
                    blackTimeNow = Math.max(0, game.blackTime - timePassed);
                    if (blackTimeNow <= 0) {
                        blackTimeNow = 0;
                        gameEndedNow = true;
                        winnerNow = "white";
                    }
                }
            }
            
            sb.append("{\"white\":\"");
            sb.append(game.whitePlayer != null ? escapeJson(game.whitePlayer) : "");
            sb.append("\",\"black\":\"");
            sb.append(game.blackPlayer != null ? escapeJson(game.blackPlayer) : "");
            sb.append("\",\"turn\":\"");
            sb.append(game.currentTurn);
            sb.append("\",\"moves\":\"");
            
            String movesStr;
            synchronized(game.moves) {
                movesStr = String.join(";", game.moves);
            }
            sb.append(escapeJson(movesStr));
            
            sb.append("\",\"whiteTime\":");
            sb.append(whiteTimeNow);
            sb.append(",\"blackTime\":");
            sb.append(blackTimeNow);
            sb.append(",\"gameStarted\":");
            sb.append(game.started);
            sb.append(",\"gameEnded\":");
            sb.append(gameEndedNow);
            sb.append(",\"winner\":\"");
            sb.append(winnerNow != null ? winnerNow : "");
            sb.append("\",\"moveVersion\":");
            sb.append(game.moveVersion);
            
            sb.append(",\"chats\":[");
            synchronized(game.chats) {
                for (int i = 0; i < game.chats.size(); i++) {
                    String chat = game.chats.get(i);
                    int colonIdx = chat.indexOf(":");
                    if (colonIdx > 0) {
                        String sender = chat.substring(0, colonIdx);
                        String msg = chat.substring(colonIdx + 1);
                        sb.append("{\"sender\":\"").append(escapeJson(sender))
                          .append("\",\"message\":\"").append(escapeJson(msg))
                          .append("\"}");
                    } else {
                        sb.append("{\"sender\":\"System\",\"message\":\"").append(escapeJson(chat)).append("\"}");
                    }
                    if (i < game.chats.size() - 1) {
                        sb.append(",");
                    }
                }
            }
            sb.append("]");
            
            sb.append(",\"board\":");
            
            sb.append("[");
            String[][] board = game.boardState;
            for (int i = 0; i < 8; i++) {
                sb.append("[");
                for (int j = 0; j < 8; j++) {
                    String piece = board[i][j];
                    if (piece != null && !piece.isEmpty()) {
                        sb.append("\"").append(piece).append("\"");
                    } else {
                        sb.append("\"\"");
                    }
                    if (j < 7) sb.append(",");
                }
                sb.append("]");
                if (i < 7) sb.append(",");
            }
            sb.append("]}");
            
            jsonResponse = sb.toString();
            
            game.cachedStateJson = jsonResponse;
            game.cacheTimestamp = System.currentTimeMillis();
        }
        
        sendResponse(exchange, 200, jsonResponse, "application/json");
    }
    
    private static void handleResetGameRequest(HttpExchange exchange) throws IOException {
        requestCounter.incrementAndGet();
        setCorsHeaders(exchange);
        
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = parseQuery(query);
        String gameId = params.get("gameId");
        
        if (gameId == null) {
            sendResponse(exchange, 400, "{\"success\":false,\"reason\":\"No gameId\"}", "application/json");
            return;
        }
        
        Game game = GameManager.getGame(gameId);
        if (game == null) {
            sendResponse(exchange, 404, "{\"success\":false,\"reason\":\"Game not found\"}", "application/json");
            return;
        }
        
        synchronized(game) {
            game.moves.clear();
            game.chats.clear();
            game.currentTurn = "white";
            game.whiteTime = 600000;
            game.blackTime = 600000;
            game.lastTime = System.currentTimeMillis();
            game.ended = false;
            game.winner = null;
            game.moveVersion = 0;
            game.boardState = initializeBoard();
            game.cachedStateJson = null;
            
            game.started = (game.whitePlayer != null && game.blackPlayer != null);
        }
        
        String response = "{\"success\":true,\"reason\":\"Game reset successfully\"}";
        sendResponse(exchange, 200, response, "application/json");
        System.out.println("Game '" + gameId + "' has been reset");
    }
    
    private static void handleHealthRequest(HttpExchange exchange) throws IOException {
        setCorsHeaders(exchange);
        
        String response = String.format(
            "{\"status\":\"OK\",\"timestamp\":%d,\"games\":%d,\"requests\":%d,\"memory\":\"%s\",\"uptime\":\"%s\"}",
            System.currentTimeMillis(),
            GameManager.getGameCount(),
            requestCounter.get(),
            getMemoryUsage(),
            getUptime()
        );
        
        sendResponse(exchange, 200, response, "application/json");
    }
    
    private static void handleCleanupRequest(HttpExchange exchange) throws IOException {
        setCorsHeaders(exchange);
        
        GameManager.cleanupOldGames();
        int removed = GameManager.getGameCount();
        
        String response = String.format(
            "{\"success\":true,\"message\":\"Cleanup completed\",\"gamesRemaining\":%d}",
            removed
        );
        
        sendResponse(exchange, 200, response, "application/json");
        System.out.println("Manual cleanup executed. Games remaining: " + removed);
    }
    
    private static void handleListGamesRequest(HttpExchange exchange) throws IOException {
        setCorsHeaders(exchange);
        
        Set<String> gameIds = GameManager.getAllGameIds();
        StringBuilder sb = new StringBuilder();
        sb.append("{\"games\":[");
        
        boolean first = true;
        for (String gameId : gameIds) {
            Game game = GameManager.getGame(gameId);
            if (game != null) {
                if (!first) sb.append(",");
                first = false;
                
                sb.append("{\"id\":\"").append(gameId).append("\"");
                sb.append(",\"white\":\"").append(game.whitePlayer != null ? game.whitePlayer : "").append("\"");
                sb.append(",\"black\":\"").append(game.blackPlayer != null ? game.blackPlayer : "").append("\"");
                sb.append(",\"started\":").append(game.started);
                sb.append(",\"ended\":").append(game.ended);
                sb.append(",\"moveCount\":").append(game.moves.size());
                sb.append("}");
            }
        }
        
        sb.append("],\"total\":").append(gameIds.size()).append("}");
        sendResponse(exchange, 200, sb.toString(), "application/json");
    }
    
    private static void handlePingRequest(HttpExchange exchange) throws IOException {
        setCorsHeaders(exchange);
        sendResponse(exchange, 200, "{\"status\":\"pong\",\"timestamp\":" + System.currentTimeMillis() + "}", "application/json");
    }
    
    private static Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return map;
        }
        
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                try {
                    String key = java.net.URLDecoder.decode(pair.substring(0, idx), "UTF-8");
                    String value = java.net.URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
                    map.put(key, value);
                } catch (Exception e) {
                    map.put(pair.substring(0, idx), pair.substring(idx + 1));
                }
            }
        }
        return map;
    }
    
    private static File findIndexHtml() {
    File f = new File("frontend/index.html");

    if (f.exists()) {
        return f;
    }

    return null;
}
    
    private static void handleSendChatRequest(HttpExchange exchange) throws IOException {
        requestCounter.incrementAndGet();
        setCorsHeaders(exchange);
        
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = parseQuery(query);
        String gameId = params.get("gameId");
        String playerId = params.get("playerId");
        String message = params.get("message");
        
        if (gameId == null || playerId == null || message == null) {
            sendResponse(exchange, 400, "Missing parameters", "text/plain");
            return;
        }
        
        Game game = GameManager.getGame(gameId);
        if (game == null) {
            sendResponse(exchange, 404, "Game not found", "text/plain");
            return;
        }
        
        synchronized(game.chats) {
            game.chats.add(playerId + ":" + message);
        }
        game.cachedStateJson = null;
        
        sendResponse(exchange, 200, "OK", "text/plain");
    }
    
    private static void handleResignRequest(HttpExchange exchange) throws IOException {
        requestCounter.incrementAndGet();
        setCorsHeaders(exchange);
        
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = parseQuery(query);
        String gameId = params.get("gameId");
        String playerId = params.get("playerId");
        
        if (gameId == null || playerId == null) {
            sendResponse(exchange, 400, "Missing parameters", "text/plain");
            return;
        }
        
        Game game = GameManager.getGame(gameId);
        if (game == null) {
            sendResponse(exchange, 404, "Game not found", "text/plain");
            return;
        }
        
        synchronized(game) {
            if (!game.ended) {
                game.ended = true;
                if (playerId.equals(game.whitePlayer)) {
                    game.winner = "black";
                } else if (playerId.equals(game.blackPlayer)) {
                    game.winner = "white";
                }
                game.cachedStateJson = null;
                System.out.println("Player '" + playerId + "' resigned in game '" + gameId + "'");
            }
        }
        
        sendResponse(exchange, 200, "OK", "text/plain");
    }
    
    private static String[][] initializeBoard() {
        return initializeBoard("standard");
    }
    
    private static String[][] initializeBoard(String variant) {
        String[][] board = new String[8][8];
        
        String[] blackBackRank = {"brook", "bknight", "bbishop", "bqueen", "bking", "bbishop", "bknight", "brook"};
        String[] whiteBackRank = {"wrook", "wknight", "wbishop", "wqueen", "wking", "wbishop", "wknight", "wrook"};
        
        System.arraycopy(blackBackRank, 0, board[0], 0, 8);
        System.arraycopy(whiteBackRank, 0, board[7], 0, 8);
        
        for (int i = 0; i < 8; i++) {
            board[1][i] = "bpawn";
            board[6][i] = "wpawn";
        }
        
        return board;
    }
    
    private static void setCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().set("Access-Control-Max-Age", "3600");
    }
    
    private static void sendResponse(HttpExchange exchange, int statusCode, String response, String contentType) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        byte[] responseBytes = response.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
    
    private static void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        sendResponse(exchange, statusCode, "{\"error\":\"" + escapeJson(message) + "\"}", "application/json");
    }
    
    private static String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
    
    private static String generateGameId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
    
    private static String getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        return String.format("%.1fMB/%.1fMB", 
            usedMemory / (1024.0 * 1024.0), 
            maxMemory / (1024.0 * 1024.0));
    }
    
    private static String getUptime() {
        long uptime = System.currentTimeMillis() - startTime;
        long seconds = uptime / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        return String.format("%02d:%02d:%02d", hours % 24, minutes % 60, seconds % 60);
    }
    
    private static final long startTime = System.currentTimeMillis();
}