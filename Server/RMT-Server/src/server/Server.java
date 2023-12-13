package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import server.ServerKlijent;

public class Server {
	
	
	public static void main(String[] args) {
		
		ServerSocket 	serverSocket =  null;
		int 			portNumber = 3334;
		

		
		try {
			serverSocket =  new ServerSocket(portNumber); 

			while(true)
			{
				Socket clientSocket = serverSocket.accept(); 

				ServerKlijent newClient = new ServerKlijent(clientSocket);
				newClient.start(); 
			}
		} catch (IOException e) 
		{
			System.out.println("Greska u komunikaciji:");
			System.out.println(e.getMessage());
		}
		
		try {
			if(serverSocket != null)
				serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
