import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;

public class ChessServe {
    
    static class Game {
        String whitePlayer = null;
        String blackPlayer = null;
        String currentTurn = "white";
        ArrayList<String> moves = new ArrayList<>();
        long whiteTime = 600000;
        long blackTime = 600000;
        long lastTime = System.currentTimeMillis();
        boolean started = false;
        boolean ended = false;
        String winner = null;
        int moveVersion = 0;
    }
    
    static HashMap<String, Game> games = new HashMap<>();
    
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(9090), 0);
        
        server.createContext("/", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                try {
                    File file = new File("D:\\CHESS\\index.html");
                    byte[] data = java.nio.file.Files.readAllBytes(file.toPath());
                    exchange.sendResponseHeaders(200, data.length);
                    exchange.getResponseBody().write(data);
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
                String query = exchange.getRequestURI().getQuery();
                HashMap<String, String> params = getParams(query);
                String gameId = params.get("gameId");
                String playerId = params.get("playerId");
                
                if (gameId == null || playerId == null) {
                    String error = "Need gameId and playerId";
                    exchange.sendResponseHeaders(400, error.length());
                    exchange.getResponseBody().write(error.getBytes());
                    exchange.close();
                    return;
                }
                
                if (!games.containsKey(gameId)) {
                    games.put(gameId, new Game());
                }
                Game game = games.get(gameId);
                
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
            }
        });
        
        server.createContext("/sendMove", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                String query = exchange.getRequestURI().getQuery();
                HashMap<String, String> params = getParams(query);
                String gameId = params.get("gameId");
                String playerId = params.get("playerId");
                String move = params.get("move");
                
                if (gameId == null || playerId == null || move == null) {
                    String error = "Missing parameters";
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
                
                synchronized(game) {
                    if (game.ended) {
                        String error = "Game has ended";
                        exchange.sendResponseHeaders(400, error.length());
                        exchange.getResponseBody().write(error.getBytes());
                        exchange.close();
                        return;
                    }
                    
                    String playerColor = null;
                    if (playerId.equals(game.whitePlayer)) {
                        playerColor = "white";
                    } else if (playerId.equals(game.blackPlayer)) {
                        playerColor = "black";
                    } else if (game.blackPlayer == null && playerId.equals(game.whitePlayer)) {
                        playerColor = "black";
                    }
                    
                    if (playerColor == null) {
                        String error = "Not a player in this game";
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
                    
                    long now = System.currentTimeMillis();
                    long timePassed = now - game.lastTime;
                    
                    if (game.currentTurn.equals("white")) {
                        game.whiteTime = game.whiteTime - timePassed;
                        if (game.whiteTime <= 0) {
                            game.whiteTime = 0;
                            game.ended = true;
                            game.winner = "black";
                        }
                    } else {
                        game.blackTime = game.blackTime - timePassed;
                        if (game.blackTime <= 0) {
                            game.blackTime = 0;
                            game.ended = true;
                            game.winner = "white";
                        }
                    }
                    
                    game.moves.add(move);
                    game.currentTurn = game.currentTurn.equals("white") ? "black" : "white";
                    game.lastTime = now;
                    game.moveVersion = game.moveVersion + 1;
                }
                
                exchange.sendResponseHeaders(200, 2);
                exchange.getResponseBody().write("OK".getBytes());
                exchange.close();
            }
        });
        
        server.createContext("/getMoves", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                String query = exchange.getRequestURI().getQuery();
                HashMap<String, String> params = getParams(query);
                String gameId = params.get("gameId");
                
                Game game = games.get(gameId);
                if (game == null) {
                    exchange.sendResponseHeaders(404, 0);
                    exchange.close();
                    return;
                }
                
                synchronized(game) {
                    String moves = String.join(",", game.moves);
                    exchange.sendResponseHeaders(200, moves.length());
                    exchange.getResponseBody().write(moves.getBytes());
                }
                exchange.close();
            }
        });
        
        server.createContext("/getState", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                String query = exchange.getRequestURI().getQuery();
                HashMap<String, String> params = getParams(query);
                String gameId = params.get("gameId");
                
                Game game = games.get(gameId);
                if (game == null) {
                    exchange.sendResponseHeaders(404, 0);
                    exchange.close();
                    return;
                }
                
                synchronized(game) {
                    long whiteTimeNow = game.whiteTime;
                    long blackTimeNow = game.blackTime;
                    boolean gameEndedNow = game.ended;
                    String winnerNow = game.winner;
                    
                    if (game.started && !game.ended) {
                        long now = System.currentTimeMillis();
                        long timePassed = now - game.lastTime;
                        
                        if (game.currentTurn.equals("white")) {
                            whiteTimeNow = game.whiteTime - timePassed;
                            if (whiteTimeNow <= 0) {
                                whiteTimeNow = 0;
                                gameEndedNow = true;
                                winnerNow = "black";
                            }
                        } else {
                            blackTimeNow = game.blackTime - timePassed;
                            if (blackTimeNow <= 0) {
                                blackTimeNow = 0;
                                gameEndedNow = true;
                                winnerNow = "white";
                            }
                        }
                    }
                    
                    String whiteName = game.whitePlayer != null ? game.whitePlayer : "";
                    String blackName = game.blackPlayer != null ? game.blackPlayer : "";
                    String allMoves = String.join(",", game.moves);
                    String winner = winnerNow != null ? winnerNow : "";
                    
                    String json = "{";
                    json += "\"white\":\"" + whiteName + "\",";
                    json += "\"black\":\"" + blackName + "\",";
                    json += "\"turn\":\"" + game.currentTurn + "\",";
                    json += "\"moves\":\"" + allMoves + "\",";
                    json += "\"whiteTime\":" + whiteTimeNow + ",";
                    json += "\"blackTime\":" + blackTimeNow + ",";
                    json += "\"gameStarted\":" + game.started + ",";
                    json += "\"gameEnded\":" + gameEndedNow + ",";
                    json += "\"winner\":\"" + winner + "\",";
                    json += "\"moveVersion\":" + game.moveVersion;
                    json += "}";
                    
                    exchange.sendResponseHeaders(200, json.length());
                    exchange.getResponseBody().write(json.getBytes());
                }
                exchange.close();
            }
        });
        
        server.setExecutor(null);
        server.start();
        System.out.println("Server started at http://localhost:9090");
    }
    
    static HashMap<String, String> getParams(String query) {
        HashMap<String, String> map = new HashMap<>();
        if (query == null) return map;
        
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] parts = pair.split("=");
            if (parts.length == 2) {
                map.put(parts[0], parts[1]);
            }
        }
        return map;
    }
}