package firsthttpserver;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Lars Mortensen
 */
public class FirstHtttpServer {

    static int port = 8080;
    static String ip = "127.0.0.1";

    public static void main(String[] args) throws Exception {
        if (args.length == 2) {
            port = Integer.parseInt(args[0]);
            ip = args[0];
        }
        HttpServer server = HttpServer.create(new InetSocketAddress(ip, port), 0);
        server.createContext("/welcome", new RequestHandler());
        server.createContext("/headers", new RequestHandler2());
        server.createContext("/pages/", new RequestHandler3());
        server.createContext("/parameters", new RequestHandler4());
        server.setExecutor(null); // Use the default executor
        server.start();
        System.out.println("Server started, listening on port: " + port);
    }

    static class RequestHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange he) throws IOException {
            String response = "Welcome to my very first almost home made Web Server :-)";

            StringBuilder sb = new StringBuilder();
            sb.append("<!DOCTYPE html>\n");
            sb.append("<html>\n");
            sb.append("<head>\n");
            sb.append("<title>My fancy Web Site</title>\n");
            sb.append("<meta charset='UTF-8'>\n");
            sb.append("</head>\n");
            sb.append("<body>\n");
            sb.append("<h2>Welcome to my very first home made Web Server :-)</h2>\n");
            sb.append("</body>\n");
            sb.append("</html>\n");
            response = sb.toString();

            Headers h = he.getResponseHeaders();
            h.add("Content-Type", "text/html");

            he.sendResponseHeaders(200, response.length());
            try (PrintWriter pw = new PrintWriter(he.getResponseBody())) {
                pw.print(response); //What happens if we use a println instead of print --> Explain

            }
        }
    }

    static class RequestHandler2 implements HttpHandler {

        @Override
        public void handle(HttpExchange he) throws IOException {
            String response = null;

            Headers h = he.getResponseHeaders();
            h.add("Content-Type", "text/html");

            h = he.getRequestHeaders();
            StringBuilder sb = new StringBuilder();
            sb.append("<!DOCTYPE html>\n");
            sb.append("<html>\n");
            sb.append("<head>\n");
            sb.append("<title>Headers</title>\n");
            sb.append("<meta charset='UTF-8'>\n");
            sb.append("</head>\n");
            sb.append("<body>\n");
            sb.append("<table>\n");

            for (Map.Entry<String, List<String>> entry : h.entrySet()) {

                sb.append("<tr>\n");
                sb.append("<td>" + entry.getKey() + "</td>\n");
                sb.append("<td>" + entry.getValue().toString() + "</td>\n");
                sb.append("</tr>\n");
            }
            sb.append("</table>\n");
            sb.append("</body>\n");
            sb.append("</html>\n");
            response = sb.toString();

            he.sendResponseHeaders(200, response.length());
            try (PrintWriter pw = new PrintWriter(he.getResponseBody())) {
                pw.print(response); //What happens if we use a println instead of print --> Explain

            }
        }
    }

    static class RequestHandler3 implements HttpHandler {

        @Override
        public void handle(HttpExchange he) throws IOException {

            File file = new File("public/" + "html.html");
            byte[] bytesToSend = new byte[(int) file.length()];
            try {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                bis.read(bytesToSend, 0, bytesToSend.length);
            } catch (IOException ie) {
                ie.printStackTrace();
            }
            he.sendResponseHeaders(200, bytesToSend.length);
            try (OutputStream os = he.getResponseBody()) {
                os.write(bytesToSend, 0, bytesToSend.length);

            }
        }
    }
    static class RequestHandler4 implements HttpHandler {

        @Override
        public void handle(HttpExchange he) throws IOException {
            String response;
            
            Scanner scan = new Scanner(he.getRequestBody());

            StringBuilder sb = new StringBuilder();
            sb.append("<!DOCTYPE html>\n");
            sb.append("<html>\n");
            sb.append("<head>\n");
            sb.append("<title>My fancy Web Site</title>\n");
            sb.append("<meta charset='UTF-8'>\n");
            sb.append("</head>\n");
            sb.append("<body>\n");
            sb.append("<h2>Method is:" + he.getRequestMethod() + "</h2>\n");
            sb.append("<h2>Get-Parameters is:" + he.getRequestURI().getQuery()+ "</h2>\n");
            while(scan.hasNext()){
            sb.append("Request body, with Post-parameters: "+scan.nextLine());
            sb.append("</br>");
            }
            sb.append("</body>\n");
            sb.append("</html>\n");
            response = sb.toString();

            Headers h = he.getResponseHeaders();
            h.add("Content-Type", "text/html");

            he.sendResponseHeaders(200, response.length());
            try (PrintWriter pw = new PrintWriter(he.getResponseBody())) {
                pw.print(response); //What happens if we use a println instead of print --> Explain

            }
        }
    }

}
