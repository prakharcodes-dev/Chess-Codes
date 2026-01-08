import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChessServe {

    static ConcurrentHashMap<String, ArrayList<String>> allGames = new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception {

        HttpServer myServer = HttpServer.create(new InetSocketAddress(9090), 0);

        myServer.createContext("/", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                try {
                    File htmlFile = new File("D:\\CHESS\\index.html");
                    byte[] fileData = java.nio.file.Files.readAllBytes(htmlFile.toPath());
                    
                    exchange.getResponseHeaders().set("Content-Type", "text/html");
                    exchange.sendResponseHeaders(200, fileData.length);
                    
                    OutputStream output = exchange.getResponseBody();
                    output.write(fileData);
                    output.close();
                    
                } catch (Exception e) {
                    String errorMsg = "File not found: D:\\CHESS\\index.html";
                    exchange.sendResponseHeaders(404, errorMsg.length());
                    exchange.getResponseBody().write(errorMsg.getBytes());
                    exchange.close();
                }
            }
        });

        myServer.createContext("/sendMove", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                String queryString = exchange.getRequestURI().getQuery();
                HashMap<String, String> params = getParams(queryString);

                String gameId = params.get("gameId");
                String move = params.get("move");

                if (gameId == null || move == null) {
                    String error = "Need gameId and move";
                    exchange.sendResponseHeaders(400, error.length());
                    exchange.getResponseBody().write(error.getBytes());
                    exchange.close();
                    return;
                }

                if (!allGames.containsKey(gameId)) {
                    allGames.put(gameId, new ArrayList<String>());
                }

                allGames.get(gameId).add(move);

                String ok = "OK";
                exchange.sendResponseHeaders(200, ok.length());
                exchange.getResponseBody().write(ok.getBytes());
                exchange.close();
            }
        });

        myServer.createContext("/getMoves", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                String queryString = exchange.getRequestURI().getQuery();
                HashMap<String, String> params = getParams(queryString);

                String gameId = params.get("gameId");

                if (gameId == null) {
                    String error = "Need gameId";
                    exchange.sendResponseHeaders(400, error.length());
                    exchange.getResponseBody().write(error.getBytes());
                    exchange.close();
                    return;
                }

                ArrayList<String> movesList = allGames.get(gameId);
                if (movesList == null) {
                    movesList = new ArrayList<>();
                }

                String movesString = String.join(",", movesList);

                exchange.sendResponseHeaders(200, movesString.length());
                exchange.getResponseBody().write(movesString.getBytes());
                exchange.close();
            }
        });

        myServer.setExecutor(null);
        myServer.start();

        System.out.println("Chess server ready at http://localhost:9090");
    }

    static HashMap<String, String> getParams(String query) {
        HashMap<String, String> map = new HashMap<>();
        if (query == null) return map;

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                map.put(keyValue[0], keyValue[1]);
            }
        }
        return map;
    }
}