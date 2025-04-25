package n1;


import java.net.Socket ;
import java.net.ServerSocket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;  
import java.io.DataOutputStream;




	

public class Server {

public static void main(String[] args) throws IOException {

int portnum = 7788;//  port number thats the server will listen() to the clints 

ServerSocket ServerSocket = new ServerSocket(portnum); // server socket thats receive clints req via port 7788

System.out.println("Server is working ") ;
	
	
	//  represents the connection between the server and that specific client. 
Socket Communication_Socket = ServerSocket.accept(); // accept the clients connection req and create a socket for the client for exchange msgs  
	
BufferedReader readFromClint = new BufferedReader(new InputStreamReader(Communication_Socket.getInputStream())); // reader to receive msgs from clients

DataOutputStream SendToClint = new DataOutputStream(Communication_Socket.getOutputStream()); //  for sending response to the clients 
	
// server always running untill the client send "quit"	
while (true)
	
{	
	
	String ClientMsg = readFromClint.readLine(); // read the msg from clint
	
	if (ClientMsg.equalsIgnoreCase("quit")) { // if the clint msg was quit break the loop
	System.out.println("Client closed the connection");
	
	break;
	
	}
	  
	// read the msg form client in this format  
	String[] divideMsg = ClientMsg.split(" // ");
	
	if (divideMsg.length != 2) { // checking the format 
		SendToClint.writeBytes("the message format is wrong the 'check sum or msg is missing missing' \n"); // inform the clint thats the msg format is wrong
		continue ;
}	
		
	
	 
	String Msg = divideMsg[0]; // Extract the  msg
	String receivedChecksum = divideMsg[1]; //   the  checksum
	System.out.println("the message : " + Msg + " Check Sum : " + receivedChecksum);
	
	//Calculate checksum of the received message
	String calculatedCheck = computeCheckSum(Msg);
	
	//ssssssssssssssssssssssssssssssss
	// Compare the check sum received with the cehck sum calculeted ( calculatedCheck)
	if (computeCheckSum(Msg).equals(receivedChecksum)) { // If checksums match
		SendToClint.writeBytes("MSG received correctly\n"); // inform the client that the msg received correctly
	} 
	else {
		SendToClint.writeBytes("error : MSG was not received correctly\n"); // inform the client that the msg was not received correctly
	}
	
	
} // loop ends



// Close the connection 
Communication_Socket.close();  
ServerSocket.close(); 

}



// calculate 16-bit one's complement checksum
private static String computeCheckSum(String msg) {
int CheckSum = 0; //  Initialize the checksum variable

// Loop through the msg  , each loop takes 2  char  (16 bit)
for (int i = 0; i < msg.length(); i += 2) {
	

// the first char(8 bits)
int word = msg.charAt(i) << 8; // shift the first char 8 bits to the left

// move to the second char
if (i + 1 < msg.length()) {
word += msg.charAt(i + 1); // Add the second character 
}

// Add word to the checksum
CheckSum += word;

// Handle carry (wrap around) if the sum exceeds 16 bits
if ((CheckSum & 0xF0000) > 0) { // check if there is a carry
	CheckSum &= 0xFFFF; // Keep only the lower 16 bits
	CheckSum++; // Add the carry
}

}
// One's complement: flip all bits (invert the bits)
CheckSum = ~CheckSum & 0xFFFF;

// Return the checksum as a hexadecimal string
return Integer.toHexString(CheckSum).toUpperCase();
}

}