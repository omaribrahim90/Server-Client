package cs330;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Random;




public class TCP_Client {

    private static int totalRequests = 0; 
    private static int erroneousRequests = 0; // Count of how many requests will trigger an error

    public static void main(String[] args) throws IOException {

        String serverAddress = "192.168.1.18"; 
        int serverPort = 7788; 
        Socket clientSocket = null;

        
        // Initiate a connection to the server
        try {
            clientSocket = new Socket(serverAddress, serverPort); 
        } catch (IOException e) {
            System.out.println("Server is down, please try later.”"); 
            return; 
        }
        
        // Create input and output streams for communication with the server
        BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in)); // Reading input from the user
        DataOutputStream serverOutputStream = new DataOutputStream(clientSocket.getOutputStream()); // Sending data to the server
        BufferedReader serverInputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // Receiving responses from the server
        
        
        // Loop to continuously interact with the server
        while (true) {

            System.out.print("Input your message: ");
            String userMessage = userInputReader.readLine(); // Read user's input message

            // Validate the message before sending
            if (userMessage.isEmpty()) { // Check for an empty message
                System.out.println("Error: A blank message is not valid."); // Notify user of invalid input
                continue; // Skip sending and prompt user again
            }

            // Terminate program if user enters "Quit"
            if (userMessage.equalsIgnoreCase("Quit")) { 
                serverOutputStream.writeBytes(userMessage + "\n"); // Send the "Quit" command to the server
                System.out.println("the connection is closed") ;
                break; // Exit the loop to end the program
            }
            
            
  
            // Calculate checksum for the user message
            String checksumValue = computeChecksum(userMessage);
             
            
            // Simulate random message errors (error probabilities: 30%, 50%, 80%)
            userMessage = createError(userMessage, 0.7); // The higher the probability, the more errors occur
            
             
            serverOutputStream.writeBytes(userMessage + " // " + checksumValue + "\n"); //sending msg with check sum to the server in this format 
            
            // Wait for the server's response and display it
            String serverReply = serverInputReader.readLine();
            System.out.println("Server's response: " + serverReply); 
            
            
           
        }

        clientSocket.close(); // Close the connection
    }

    
    
    // Method to compute checksum for a given message
    private static String computeChecksum(String input) {
        int checksum = 0;

        // Break the message into 16-bit words and calculate the checksum
        for (int i = 0; i < input.length(); i += 2) {
            int word = input.charAt(i) << 8; // Process high byte
            if (i + 1 < input.length()) {
                word += input.charAt(i + 1); // Process low byte
            }
            checksum += word;

            // Handle overflow (if the sum exceeds 16 bits)
            if ((checksum & 0xF0000) > 0) { 
                checksum &= 0xFFFF; 
                checksum++; 
            } 
        }

        // Apply one's complement (invert all bits)
        checksum = ~checksum & 0xFFFF;
        
        

        // Return the checksum as a hexadecimal string
        return Integer.toHexString(checksum).toUpperCase();
    }

    // Helper method to introduce errors in the message
    private static String createError(String input, double probability) {
        totalRequests++; // Track total number of requests
        int expectedErrors = (int) (totalRequests * probability); // Calculate the expected number of errors

        // Inject error into the message if necessary
        if (erroneousRequests < expectedErrors) { 
            erroneousRequests++; 
            return insertRandomError(input); 
        }
        return input; // Return the original message if no errors are added
    }

    // Method to insert a random error into a message
    private static String insertRandomError(String input) {
        Random randomGenerator = new Random();
        StringBuilder alteredMessage = new StringBuilder(input); 

        // Select a random position in the message and change the character
        int errorPosition = randomGenerator.nextInt(input.length()); 
        alteredMessage.setCharAt(errorPosition, (char) (randomGenerator.nextInt(26) + 'a')); // Replace with a random letter

        return alteredMessage.toString();
    }
}


