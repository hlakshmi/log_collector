package com.log_collector.java;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogCollectorServer {

    private void runServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000),0);
        server.createContext("/", new LogCollectorHandler());
        server.setExecutor(null);
        server.start();
    }

    private static class LogCollectorHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            InputStream inStream = exchange.getRequestBody();
            String postData = getPostData(inStream);
            Map<String, Object> result = new ObjectMapper().readValue(postData, HashMap.class);
            String fileName = (String) result.get("filename");
            int numLines = (int) result.get("num_lines");
            List<String> keywords = (List<String>) result.get("keywords");
            System.out.println("Post Data: " + postData);
            System.out.println("filename: " + fileName + " numLines: " + numLines + " keywords: " + keywords);
            BufferedReverseLineStreamReader reader = new BufferedReverseLineStreamReader();

            try {
                List<String> lines = reader.readLines(fileName, numLines, keywords);
                StringBuilder builder = new StringBuilder();
                for(String line: lines) {
                    builder.append(line);
                    builder.append("\n");
                }

                String response = builder.toString();
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
            catch(InvalidPathException e) {
                String response = "Invalid File Path: " + fileName;
                exchange.sendResponseHeaders(406, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }

        private String getPostData(InputStream inputStream) throws IOException {
            String newLine = System.getProperty("line.separator");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream));
            StringBuilder result = new StringBuilder();
            for (String line; (line = reader.readLine()) != null; ) {
                if (result.length() > 0) {
                    result.append(newLine);
                }
                result.append(line);
            }
            return result.toString();
        }
    }

    public static void main(String[] args) {
        try {
            LogCollectorServer server = new LogCollectorServer();
            server.runServer();
            System.out.println("Started server on port 8000");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
