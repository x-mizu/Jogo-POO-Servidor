package Service;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import Model.ClientThread;
import Model.Servidor;

public class ServidorService {

	private Servidor server;
	private ServerSocket serverSocket = null;
	private Socket clientSocket = null;
	private int portNumber;
	private static final int maxClientsCount = 5; 
	private static final ClientThread[] threads = new ClientThread[maxClientsCount];
	
	public ServidorService(int portNumber) throws IOException {
		this.portNumber = portNumber;
		serverSocket = new ServerSocket(this.portNumber);
	}
	
	public void abrirServidor() throws IOException {
		int i;
		while (true) 
		 {
            clientSocket = serverSocket.accept();
			 //Procura um thread vazio e aloca o novo cliente nele
			 for (i = 0; i < maxClientsCount; i++) {
					if (threads[i] == null) {
						(threads[i] = new ClientThread(clientSocket, threads)).start();
						System.out.println("Conexão estabelecida de " + clientSocket.getInetAddress() 
			 +" |  Aguardadndo entrada de cliente...");
					//apos encontrar o thread vazio, sai do loop
					break;
					}
			 }
			 //envia uma mensagem ao cliente caso o server esteja em sua capaciadde máxima
			 if (i == maxClientsCount){
				DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
				output.writeBytes("O server atingiu a capacidade máxima de clientes."+"\n");
			 }
		 }
	}
	
}
