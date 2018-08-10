package Model;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientThread extends Thread {

    private Socket clientSocket = null;
    private final ClientThread[] threads;
    private int maxClientsCount;
    private BlockingQueue<Requisicao> listaRequisicoes = new ArrayBlockingQueue<>(5);
    private BlockingQueue<Resposta> listaRespostas = new ArrayBlockingQueue<>(5);
    private BlockingQueue<Resposta> listaRespostasDedicado = new ArrayBlockingQueue<>(5);
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;
    private Long jogadorId;

    public ClientThread(Socket clientSocket, ClientThread[] threads, BlockingQueue<Requisicao> listaReq, BlockingQueue<Resposta> listaRes, Long jogadorId, BlockingQueue<Resposta> listaResDed) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
        this.listaRequisicoes = listaReq;
        this.listaRespostas = listaRes;
        this.jogadorId = jogadorId;
        this.listaRespostasDedicado = listaResDed;
    }

    public void run() {
        try {
            int maxClientsCount = this.maxClientsCount;
            ClientThread[] threads = this.threads;

            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            
            //pega uma requisicao de inicio do jogador
            Requisicao req = (Requisicao) in.readObject();
            req.jogadorId = jogadorId;
            listaRequisicoes.put(req);
            
            //envia uma primeira resposta ao jogador, com seu id e os jogadores que estao no server
            Resposta resp1 = new Resposta();
            resp1 = listaRespostasDedicado.take();
            out.writeObject(resp1);
            
   
            while (true) {
                //pega a requisicao de jogador pronto
            	Requisicao reqJ = (Requisicao) in.readObject();
                listaRequisicoes.put(reqJ);
                
                //envia resposta de inicio de partida
                Resposta resp2 = new Resposta();
                resp2 = listaRespostasDedicado.take();
                System.out.println("ID - " + resp2.jogadorId);
                for (int k = 0; k < resp2.jogadores.length ; k++) {
                	for (int l = 0 ; l < resp2.jogadores[0].length; l++)
                		System.out.print(resp2.jogadores[k][l] + " ");
                	
                	System.out.println("");
                }
                System.out.println("----------------------------------");
                
                out.writeObject(resp2);
                
                break;
            }

            clientSocket.close();

            // Limpa o thread para outros clientes
            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] == this) {
                    threads[i] = null;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
