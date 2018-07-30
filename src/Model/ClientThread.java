package Model;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientThread extends Thread{

    private Socket clientSocket = null;
    private final ClientThread[] threads;
    private int maxClientsCount;
	
	public ClientThread(Socket clientSocket, ClientThread[] threads){
		this.clientSocket = clientSocket;
		this.threads = threads;
		maxClientsCount = threads.length;
	}
	
	public void run () {
		int maxClientsCount = this.maxClientsCount;
		ClientThread[] threads = this.threads;
		 //Get Port
		int port = clientSocket.getPort();
		
				 
		
		while (true){
			
			 // create a BufferedReader object to read strings from
			 // the socket. (read strings FROM CLIENT)
			try{
			BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			 String input = br.readLine();
			 
			 //create output stream to write to/send TO CLINET
			 DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
			 
			 output.writeBytes(input.toUpperCase() + "\n");
			 if (input .equalsIgnoreCase("tchau")){
				 System.out.println("Conexão estabelecida de " + clientSocket.getInetAddress() 
			 + " |  Cliente desconectado");
			 }
			 else{
				 System.out.println("Conexão estabelecida de " + clientSocket.getInetAddress() 
				 +" |  Porta: " + port + " |  Saida: " + input.toUpperCase());
			 }
			 break;
			}
			catch (IOException e){
				System.out.println(e);
			}
			 
			
			
		}
		try{
			// close current connection
			clientSocket.close();
		}
		catch (IOException e){
			System.out.println(e);
		}
		// Limpa o thread para outros clientes
		for (int i = 0; i < maxClientsCount; i++) {
			if (threads[i] == this) {
				threads[i] = null;
			}
		}
	}
}