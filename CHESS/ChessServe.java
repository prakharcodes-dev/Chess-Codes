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
        String[][] boardState = initializeBoard();
        volatile String cachedStateJson = null;
        volatile long cacheTimestamp = 0;
    }
    
    static ConcurrentHashMap<String, Game> games = new ConcurrentHashMap<>();
    static final ExecutorService executor = Executors.newFixedThreadPool(50);
    static final AtomicInteger requestCounter = new AtomicInteger(0);
    
    public static void main(String[] args) throws Exception {
        System.setProperty("sun.net.httpserver.maxReqTime", "5");
        System.setProperty("sun.net.httpserver.maxRspTime", "5");
        
        HttpServer server = HttpServer.create(new InetSocketAddress(9090), 0);
        
        server.setExecutor(executor);
        
        server.createContext("/", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                requestCounter.incrementAndGet();
                try {
                    File file = new File("D:\\CHESS\\index.html");
                    byte[] data = java.nio.file.Files.readAllBytes(file.toPath());
                    
                    exchange.getResponseHeaders().set("Cache-Control", "max-age=3600");
                    exchange.getResponseHeaders().set("Content-Type", "text/html");
                    
                    exchange.sendResponseHeaders(200, data.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(data);
                    }
                } catch (Exception e) {
                    String error = "File not found";
                    exchange.sendResponseHeaders(404, error.length());
                    exchange.getResponseBody().write(error.getBytes());
                }
                exchange.close();
            }
        });
        
        server.createContext("/join", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                requestCounter.incrementAndGet();
                long startTime = System.currentTimeMillis();
                
                exchange.getResponseHeaders().set("Content-Type", "text/plain");
                
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> params = getParams(query);
                String gameId = params.get("gameId");
                String playerId = params.get("playerId");
                
                if (gameId == null || playerId == null) {
                    String error = "Need gameId and playerId";
                    exchange.sendResponseHeaders(400, error.length());
                    exchange.getResponseBody().write(error.getBytes());
                    exchange.close();
                    return;
                }
                
                Game game = games.computeIfAbsent(gameId, k -> new Game());
                String role;
                
                synchronized(game) {
                    if (game.whitePlayer == null) {
                        game.whitePlayer = playerId;
                        role = "white";
                    } else if (game.blackPlayer == null) {
                        game.blackPlayer = playerId;
                        role = "black";
                        game.started = true;
                        game.lastTime = System.currentTimeMillis();
                        game.cachedStateJson = null;
                    } else if (playerId.equals(game.whitePlayer)) {
                        role = "white";
                    } else if (playerId.equals(game.blackPlayer)) {
                        role = "black";
                    } else {
                        role = "full";
                    }
                }
                
                exchange.sendResponseHeaders(200, role.length());
                exchange.getResponseBody().write(role.getBytes());
                exchange.close();
                
                long endTime = System.currentTimeMillis();
                System.out.println("Join processed " + (endTime - startTime) + "ms");
            }
        });
        
        server.createContext("/validateMove", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                requestCounter.incrementAndGet();
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> params = getParams(query);
                String gameId = params.get("gameId");
                String playerId = params.get("playerId");
                String move = params.get("move");
                
                if (gameId == null || playerId == null || move == null) {
                    String error = "{\"valid\":false,\"reason\":\"Missing\"}";
                    exchange.sendResponseHeaders(400, error.length());
                    exchange.getResponseBody().write(error.getBytes());
                    exchange.close();
                    return;
                }
                
                Game game = games.get(gameId);
                if (game == null) {
                    String error = "{\"valid\":false,\"reason\":\"Game not found\"}";
                    exchange.sendResponseHeaders(404, error.length());
                    exchange.getResponseBody().write(error.getBytes());
                    exchange.close();
                    return;
                }
                
                if (game.ended) {
                    String error = "{\"valid\":false,\"reason\":\"Game ended\"}";
                    exchange.sendResponseHeaders(400, error.length());
                    exchange.getResponseBody().write(error.getBytes());
                    exchange.close();
                    return;
                }
                
                boolean isWhite = playerId.equals(game.whitePlayer);
                boolean isBlack = playerId.equals(game.blackPlayer);
                String playerColor = isWhite ? "white" : (isBlack ? "black" : null);
                
                if (playerColor == null) {
                    String error = "{\"valid\":false,\"reason\":\"Not player\"}";
                    exchange.sendResponseHeaders(403, error.length());
                    exchange.getResponseBody().write(error.getBytes());
                    exchange.close();
                    return;
                }
                
                if (!playerColor.equals(game.currentTurn)) {
                    String error = "{\"valid\":false,\"reason\":\"Not your turn\"}";
                    exchange.sendResponseHeaders(403, error.length());
                    exchange.getResponseBody().write(error.getBytes());
                    exchange.close();
                    return;
                }
                
                String[] parts = move.split(",");
                if (parts.length != 4) {
                    String error = "{\"valid\":false,\"reason\":\"Bad format\"}";
                    exchange.sendResponseHeaders(400, error.length());
                    exchange.getResponseBody().write(error.getBytes());
                    exchange.close();
                    return;
                }
                
                String response = "{\"valid\":true,\"reason\":\"Move ok\"}";
                exchange.sendResponseHeaders(200, response.length());
                exchange.getResponseBody().write(response.getBytes());
                exchange.close();
            }
        });
        
        server.createContext("/sendMove", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                requestCounter.incrementAndGet();
                long startTime = System.currentTimeMillis();
                
                exchange.getResponseHeaders().set("Content-Type", "text/plain");
                
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> params = getParams(query);
                String gameId = params.get("gameId");
                String playerId = params.get("playerId");
                String move = params.get("move");
                
                if (gameId == null || playerId == null || move == null) {
                    String error = "Missing data";
                    exchange.sendResponseHeaders(400, error.length());
                    exchange.getResponseBody().write(error.getBytes());
                    exchange.close();
                    return;
                }
                
                Game game = games.get(gameId);
                if (game == null) {
                    String error = "Game not found";
                    exchange.sendResponseHeaders(404, error.length());
                    exchange.getResponseBody().write(error.getBytes());
                    exchange.close();
                    return;
                }
                
                boolean moveAccepted = false;
                synchronized(game) {
                    if (game.ended) {
                        String error = "Game ended";
                        exchange.sendResponseHeaders(400, error.length());
                        exchange.getResponseBody().write(error.getBytes());
                        exchange.close();
                        return;
                    }
                    
                    boolean isWhite = playerId.equals(game.whitePlayer);
                    boolean isBlack = playerId.equals(game.blackPlayer);
                    String playerColor = isWhite ? "white" : (isBlack ? "black" : null);
                    
                    if (playerColor == null) {
                        String error = "Not in game";
                        exchange.sendResponseHeaders(403, error.length());
                        exchange.getResponseBody().write(error.getBytes());
                        exchange.close();
                        return;
                    }
                    
                    if (!playerColor.equals(game.currentTurn)) {
                        String error = "Not your turn";
                        exchange.sendResponseHeaders(403, error.length());
                        exchange.getResponseBody().write(error.getBytes());
                        exchange.close();
                        return;
                    }
                    
                    String[] parts = move.split(",");
                    if (parts.length != 4) {
                        String error = "Bad move";
                        exchange.sendResponseHeaders(400, error.length());
                        exchange.getResponseBody().write(error.getBytes());
                        exchange.close();
                        return;
                    }
                    
                    try {
                        int fromRow = Integer.parseInt(parts[0]);
                        int fromCol = Integer.parseInt(parts[1]);
                        int toRow = Integer.parseInt(parts[2]);
                        int toCol = Integer.parseInt(parts[3]);
                        
                        if (fromRow < 0 || fromRow > 7 || fromCol < 0 || fromCol > 7 ||
                            toRow < 0 || toRow > 7 || toCol < 0 || toCol > 7) {
                            String error = "Bad coords";
                            exchange.sendResponseHeaders(400, error.length());
                            exchange.getResponseBody().write(error.getBytes());
                            exchange.close();
                            return;
                        }
                        
                        String piece = game.boardState[fromRow][fromCol];
                        if (piece == null || piece.isEmpty()) {
                            String error = "No piece";
                            exchange.sendResponseHeaders(400, error.length());
                            exchange.getResponseBody().write(error.getBytes());
                            exchange.close();
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
                            }
                        } else {
                            game.blackTime -= timePassed;
                            if (game.blackTime <= 0) {
                                game.blackTime = 0;
                                game.ended = true;
                                game.winner = "white";
                            }
                        }
                        
                        game.moves.add(move);
                        game.currentTurn = game.currentTurn.equals("white") ? "black" : "white";
                        game.lastTime = now;
                        game.moveVersion++;
                        game.cachedStateJson = null;
                        
                        moveAccepted = true;
                        
                    } catch (NumberFormatException e) {
                        String error = "Bad numbers";
                        exchange.sendResponseHeaders(400, error.length());
                        exchange.getResponseBody().write(error.getBytes());
                        exchange.close();
                        return;
                    }
                }
                
                if (moveAccepted) {
                    exchange.sendResponseHeaders(200, 2);
                    exchange.getResponseBody().write("OK".getBytes());
                }
                
                exchange.close();
                long endTime = System.currentTimeMillis();
                System.out.println("Move done " + (endTime - startTime) + "ms");
            }
        });
        
        server.createContext("/getMoves", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                requestCounter.incrementAndGet();
                
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> params = getParams(query);
                String gameId = params.get("gameId");
                
                exchange.getResponseHeaders().set("Content-Type", "text/plain");
                exchange.getResponseHeaders().set("Cache-Control", "no-cache");
                
                Game game = games.get(gameId);
                if (game == null) {
                    exchange.sendResponseHeaders(404, 0);
                    exchange.close();
                    return;
                }
                
                String moves;
                synchronized(game.moves) {
                    moves = String.join(",", game.moves);
                }
                
                exchange.sendResponseHeaders(200, moves.length());
                exchange.getResponseBody().write(moves.getBytes());
                exchange.close();
            }
        });
        
        server.createContext("/getState", new HttpHandler() {
            private final ThreadLocal<StringBuilder> jsonBuilder = 
                ThreadLocal.withInitial(StringBuilder::new);
            
            public void handle(HttpExchange exchange) throws IOException {
                requestCounter.incrementAndGet();
                long startTime = System.currentTimeMillis();
                
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.getResponseHeaders().set("Cache-Control", "no-store, must-revalidate");
                
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> params = getParams(query);
                String gameId = params.get("gameId");
                
                Game game = games.get(gameId);
                if (game == null) {
                    exchange.sendResponseHeaders(404, 0);
                    exchange.close();
                    return;
                }
                
                String jsonResponse;
                
                if (game.cachedStateJson != null && 
                    (System.currentTimeMillis() - game.cacheTimestamp) < 100) {
                    jsonResponse = game.cachedStateJson;
                } else {
                    StringBuilder sb = jsonBuilder.get();
                    sb.setLength(0);
                    
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
                    sb.append(game.whitePlayer != null ? game.whitePlayer : "");
                    sb.append("\",\"black\":\"");
                    sb.append(game.blackPlayer != null ? game.blackPlayer : "");
                    sb.append("\",\"turn\":\"");
                    sb.append(game.currentTurn);
                    sb.append("\",\"moves\":\"");
                    
                    String movesStr;
                    synchronized(game.moves) {
                        movesStr = String.join(",", game.moves);
                    }
                    sb.append(movesStr);
                    
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
                
                exchange.sendResponseHeaders(200, jsonResponse.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(jsonResponse.getBytes("UTF-8"));
                }
                
                exchange.close();
                long endTime = System.currentTimeMillis();
                if ((endTime - startTime) > 10) {
                    System.out.println("State took " + (endTime - startTime) + "ms");
                }
            }
        });
        
        server.createContext("/resetGame", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                requestCounter.incrementAndGet();
                
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> params = getParams(query);
                String gameId = params.get("gameId");
                
                if (gameId == null) {
                    String error = "{\"success\":false,\"reason\":\"No gameId\"}";
                    exchange.sendResponseHeaders(400, error.length());
                    exchange.getResponseBody().write(error.getBytes());
                    exchange.close();
                    return;
                }
                
                Game game = games.get(gameId);
                if (game == null) {
                    String error = "{\"success\":false,\"reason\":\"Game missing\"}";
                    exchange.sendResponseHeaders(404, error.length());
                    exchange.getResponseBody().write(error.getBytes());
                    exchange.close();
                    return;
                }
                
                synchronized(game) {
                    game.moves.clear();
                    game.currentTurn = "white";
                    game.whiteTime = 600000;
                    game.blackTime = 600000;
                    game.lastTime = System.currentTimeMillis();
                    game.ended = false;
                    game.winner = null;
                    game.moveVersion = 0;
                    game.boardState = initializeBoard();
                    game.cachedStateJson = null;
                }
                
                String response = "{\"success\":true,\"reason\":\"Game reset\"}";
                exchange.sendResponseHeaders(200, response.length());
                exchange.getResponseBody().write(response.getBytes());
                exchange.close();
            }
        });
        
        server.createContext("/health", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                String response = "{\"status\":\"OK\",\"games\":\"" + games.size() + 
                                 "\",\"requests\":\"" + requestCounter.get() + "\"}";
                exchange.sendResponseHeaders(200, response.length());
                exchange.getResponseBody().write(response.getBytes());
                exchange.close();
            }
        });
        
        server.createContext("/cleanup", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                
                int removed = 0;
                long now = System.currentTimeMillis();
                Iterator<Map.Entry<String, Game>> it = games.entrySet().iterator();
                
                while (it.hasNext()) {
                    Map.Entry<String, Game> entry = it.next();
                    Game game = entry.getValue();
                    
                    synchronized(game) {
                        if ((now - game.lastTime) > 3600000 && game.ended) {
                            it.remove();
                            removed++;
                        }
                    }
                }
                
                String response = "{\"removed\":\"" + removed + "\",\"remaining\":\"" + games.size() + "\"}";
                exchange.sendResponseHeaders(200, response.length());
                exchange.getResponseBody().write(response.getBytes());
                exchange.close();
            }
        });
        
        server.start();
        System.out.println("Server started http://localhost:9090");
        System.out.println("Fast server ready");
        System.out.println("50 threads ready");
        System.out.println("Caching on");
        System.out.println("Endpoints:");
        System.out.println("  /health - check server");
        System.out.println("  /cleanup - clean old games");
        
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(300000);
                    int removed = 0;
                    long now = System.currentTimeMillis();
                    Iterator<Map.Entry<String, Game>> it = games.entrySet().iterator();
                    
                    while (it.hasNext()) {
                        Map.Entry<String, Game> entry = it.next();
                        Game game = entry.getValue();
                        
                        synchronized(game) {
                            boolean shouldRemove = false;
                            if (game.ended && (now - game.lastTime) > 1800000) {
                                shouldRemove = true;
                            } else if (!game.ended && (now - game.lastTime) > 7200000) {
                                shouldRemove = true;
                            }
                            
                            if (shouldRemove) {
                                it.remove();
                                removed++;
                            }
                        }
                    }
                    
                    if (removed > 0) {
                        System.out.println("Cleaned " + removed + " games");
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "CleanupThread").start();
    }
    
    static Map<String, String> getParams(String query) {
        if (query == null) return new HashMap<>();
        
        Map<String, String> map = new HashMap<>();
        int start = 0;
        int end;
        while ((end = query.indexOf('&', start)) != -1) {
            parseParam(query.substring(start, end), map);
            start = end + 1;
        }
        parseParam(query.substring(start), map);
        return map;
    }
    
    static void parseParam(String param, Map<String, String> map) {
        int eq = param.indexOf('=');
        if (eq != -1) {
            map.put(param.substring(0, eq), param.substring(eq + 1));
        }
    }
    
    static String[][] initializeBoard() {
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
}