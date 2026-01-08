import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChessServe {

    static Map<String, List<String>> games = new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(9090), 0);

        // Serve HTML
        server.createContext("/", exchange -> {
            File file = new File("../HTML/index.html");
            byte[] bytes = java.nio.file.Files.readAllBytes(file.toPath());

            exchange.getResponseHeaders().add("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        });

        // Receive move
        server.createContext("/sendMove", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            Map<String,String> params = queryToMap(query);

            String gameId = params.get("gameId");
            String move = params.get("move");

            games.putIfAbsent(gameId, new ArrayList<>());
            games.get(gameId).add(move);

            byte[] response = "OK".getBytes();
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });

        // Send moves
        server.createContext("/getMoves", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            Map<String,String> params = queryToMap(query);

            String gameId = params.get("gameId");
            String response = String.join(",", games.getOrDefault(gameId, new ArrayList<>()));

            byte[] bytes = response.getBytes();
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });

        server.start();
        System.out.println("Server running on http://localhost:9090");
    }

    static Map<String,String> queryToMap(String query){
        Map<String,String> map = new HashMap<>();
        if(query == null) return map;
        for(String p : query.split("&")){
            String[] pair = p.split("=");
            if(pair.length > 1) map.put(pair[0], pair[1]);
        }
        return map;
    }
}
