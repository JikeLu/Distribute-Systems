package ds;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.HttpURLConnection;

public class CurrencyConverterServer {
    private static final String API_KEY = "0c7228c6b9f97d288bf752dab94a68be";
    private static final String API_URL = "http://api.exchangeratesapi.io/v1/latest?access_key=" + API_KEY;

    public static void main(String[] args) throws IOException {
        int port = 8000; // Port to listen on
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/convert", new CurrencyConversionHandler());
        server.setExecutor(null); // Use default executor
        server.start();
        System.out.println("Server started on port " + port);
    }

    private static class CurrencyConversionHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                // Read request body
                BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                StringBuilder requestBody = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }

                // Parse JSON request body
                Gson gson = new Gson();
                JsonObject jsonRequest = gson.fromJson(requestBody.toString(), JsonObject.class);
                String fromCurrency = jsonRequest.get("fromCurrency").getAsString();
                String toCurrency = jsonRequest.get("toCurrency").getAsString();
                double amount = jsonRequest.get("amount").getAsDouble();

                try {
                    double convertedAmount = convertCurrency(amount, fromCurrency, toCurrency);
                    String response = String.format("%.2f %s is equal to %.2f %s", amount, fromCurrency, convertedAmount, toCurrency);
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception e) {
                    String errorMessage = "Error converting currency: " + e.getMessage();
                    exchange.sendResponseHeaders(500, errorMessage.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(errorMessage.getBytes());
                    os.close();
                }
            } else {
                exchange.sendResponseHeaders(405, 0); // Method not allowed
                exchange.close();
            }
        }
    }

    private static double convertCurrency(double amount, String fromCurrency, String toCurrency) throws IOException {
        JsonObject rates = getExchangeRates();
        double fromRate = rates.get(fromCurrency).getAsDouble();
        double toRate = rates.get(toCurrency).getAsDouble();
        return (amount / fromRate) * toRate;
    }

    private static JsonObject getExchangeRates() throws IOException {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
        }
        return JsonParser.parseString(response.toString()).getAsJsonObject().getAsJsonObject("rates");
    }
}
