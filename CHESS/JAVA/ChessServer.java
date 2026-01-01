import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;

public class ChessServer {

    static boolean whiteTurn = true, gameOver = false;

    // ✅ FIXED ABSOLUTE PATH (your real location)
    static final String HTML_PATH = "D:/CHESS/HTML/index.html";

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(9090), 0);

        // Serve HTML (absolute path – no confusion possible)
        server.createContext("/", e -> sendFile(e, HTML_PATH));

        server.createContext("/move", ChessServer::handleMove);
        server.start();

        System.out.println("Server running at http://localhost:9090");
    }

    // -------- MOVE HANDLER --------
    static void handleMove(HttpExchange e) throws IOException {

        if (gameOver) { send(e, "Game Over!"); return; }

        String q = e.getRequestURI().getQuery();
        if (q == null) { send(e, "Invalid move"); return; }

        String color = get(q, "color");
        String piece = get(q, "piece");
        String from  = get(q, "from");
        String to    = get(q, "to");

        if (color == null || piece == null || from == null || to == null) {
            send(e, "Incomplete move data");
            return;
        }

        if (whiteTurn && !color.equalsIgnoreCase("white")) {
            send(e, "Black's turn!");
            return;
        }

        if (!whiteTurn && !color.equalsIgnoreCase("black")) {
            send(e, "White's turn!");
            return;
        }

        if (piece.equalsIgnoreCase("queen") && !validQueen(from, to)) {
            send(e, "Invalid queen move");
            return;
        }

        whiteTurn = !whiteTurn;
        send(e, color + " " + piece + " moved");
    }

    // -------- QUEEN RULE --------
    static boolean validQueen(String f, String t) {
        int fr = Integer.parseInt(f.split(",")[0]);
        int fc = Integer.parseInt(f.split(",")[1]);
        int tr = Integer.parseInt(t.split(",")[0]);
        int tc = Integer.parseInt(t.split(",")[1]);
        return fr == tr || fc == tc || Math.abs(fr - tr) == Math.abs(fc - tc);
    }

    // -------- HELPERS --------
    static String get(String q, String k) {
        for (String s : q.split("&"))
            if (s.startsWith(k + "=")) return s.split("=")[1];
        return null;
    }

    static void sendFile(HttpExchange e, String absolutePath) throws IOException {
        File f = new File(absolutePath);

        if (!f.exists()) {
            send(e, "HTML not found at:\n" + f.getAbsolutePath());
            return;
        }

        byte[] d = new FileInputStream(f).readAllBytes();
        e.getResponseHeaders().add("Content-Type", "text/html");
        e.sendResponseHeaders(200, d.length);
        e.getResponseBody().write(d);
        e.close();
    }

    static void send(HttpExchange e, String msg) throws IOException {
        e.sendResponseHeaders(200, msg.length());
        e.getResponseBody().write(msg.getBytes());
        e.close();
    }
}
