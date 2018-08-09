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
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;
    private Long jogadorId;

    public ClientThread(Socket clientSocket, ClientThread[] threads, BlockingQueue<Requisicao> listaReq, BlockingQueue<Resposta> listaRes, Long jogadorId) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
        this.listaRequisicoes = listaReq;
        this.listaRespostas = listaRes;
        this.jogadorId = jogadorId;
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
            System.out.println(req.jogadorNickname);
            listaRequisicoes.put(req);
            //envia uma primeira resposta ao jogador, com seu id e os jogadores que estao no server
            Resposta resp1 = listaRespostas.take();
            resp1.jogadorId = jogadorId;
            resp1.serverStatus = Long.valueOf(1);
            for(int i = 0; i < resp1.jogadores[0].length ; i++)
                resp1.jogadores[3][i] = "0";
            
            out.writeObject(resp1);
            
            //pega a requisicao de jogador pronto
            listaRequisicoes.put((Requisicao) in.readObject());
            //envia resposta de inicio de partida
            Resposta resp2 = listaRespostas.take();
            resp2.jogadorId = jogadorId;
            resp2.serverStatus = Long.valueOf(7);
            for(int i = 0; i < resp2.jogadores[0].length ; i++)
                resp2.jogadores[3][i] = "0";
            
            out.writeObject(resp2);
                
            while (true) {
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
