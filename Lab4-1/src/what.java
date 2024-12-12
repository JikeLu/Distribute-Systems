import java.net.*;
import java.io.*;
import java.util.Scanner;

public class what {
    public static void main(String args[]) {
        ServerSocket listenSocket = null;
        Socket clientSocket = null;

        try {
            int serverPort = 7777; // the server port we are using

            // Create a new server socket
            listenSocket = new ServerSocket(serverPort);
            System.out.println("Server started. Listening on port " + serverPort);

            while (true) {
                // Accept a socket connection
                clientSocket = listenSocket.accept();
                // If we get here, then we are now connected to a client.

                // Set up input and output streams to communicate with the client
                Scanner inFromSocket = new Scanner(clientSocket.getInputStream());
                PrintWriter outToSocket = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));

                // Read the first line of the HTTP request
                String request = inFromSocket.nextLine();
                String filePath = "";

                // Parse out the file path requested
                if (request.startsWith("GET")) {
                    filePath = request.split(" ")[1].substring(1); // Extracting the file path from the request
                    filePath = "E:/DistributedSystem/Lab4/" + filePath; // Appending root path
                }

                System.out.println("File requested: " + filePath);

                // Try to open the file requested on the local disk
                File file = new File(filePath);
                if (file.exists() && !file.isDirectory()) {
                    // Send HTTP OK status response
                    outToSocket.println("HTTP/1.1 200 OK");
                    outToSocket.println(); // Blank line to indicate end of headers
                    outToSocket.flush();

                    // Read and send the content of the file
                    BufferedReader fileReader = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = fileReader.readLine()) != null) {
                        outToSocket.println(line);
                    }
                    outToSocket.flush();
                    fileReader.close();
                } else {
                    // Report file not found using the correct HTTP status code (404)
                    outToSocket.println("HTTP/1.1 404 Not Found");
                    outToSocket.println(); // Blank line to indicate end of headers
                    outToSocket.flush();
                }

                // Close the socket
                outToSocket.close();
                inFromSocket.close();
                clientSocket.close();
            }
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());
        } finally {
            try {
                if (listenSocket != null) {
                    listenSocket.close();
                }
            } catch (IOException e) {
                // Ignore exception on close
            }
        }
    }
}
