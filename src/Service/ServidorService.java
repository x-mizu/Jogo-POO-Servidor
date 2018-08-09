package Service;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import Model.ClientThread;
import Model.Requisicao;
import Model.Resposta;
import Model.Servidor;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServidorService {

    //private Servidor server;
    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;
    private int portNumber;
    private static final int maxClientsCount = 5;
    private static final ClientThread[] threads = new ClientThread[maxClientsCount];
    public int numJogadores = 0;
    BlockingQueue<Requisicao> listaRequisicoes = new ArrayBlockingQueue<>(5);
    BlockingQueue<Resposta> listaRespostas = new ArrayBlockingQueue<>(5);

    public ServidorService(int portNumber) throws IOException {
        this.portNumber = portNumber;
        serverSocket = new ServerSocket(this.portNumber);
    }

    public void abrirServidor() throws IOException {
        int i;
        while (true) {
            clientSocket = serverSocket.accept();
            //Procura um thread vazio e aloca o novo cliente nele
            for (i = 0; i < maxClientsCount; i++) {
                if (threads[i] == null) {
                    (threads[i] = new ClientThread(clientSocket, threads, listaRequisicoes, listaRespostas, Long.valueOf(i + 1))).start();
                    System.out.println("Conex�o estabelecida de " + clientSocket.getInetAddress()
                            + " |  Aguardadndo entrada de cliente...");
                    //apos encontrar o thread vazio, sai do loop
                    this.numJogadores++;
                    break;

                }
            }
        }
    }

    public void enviarResp(Resposta resp, int numeroJogadores) {
        //adicionar na blockqueue uma resposta apra cada thread 
        for (int i = 0; i < numeroJogadores; i++) {
            try {
                listaRespostas.put(resp);
            } catch (InterruptedException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public ArrayList<Requisicao> getReq() {
        ArrayList<Requisicao> listaREQ = new ArrayList<>();
        for (int i = 0; i < numJogadores; i++) {
            try {
                //take requisicoes
                listaREQ.add(listaRequisicoes.take());
            } catch (InterruptedException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return listaREQ;
    }

    public Requisicao getPrimeiraReq() {

        try {
            Requisicao teste = listaRequisicoes.take();
            System.out.println(teste.jogadorNickname);
            return teste;
        } catch (InterruptedException ex) {
            Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void enviarPrimeiraResp(Resposta resp) {

        try {
            listaRespostas.put(resp);
        } catch (InterruptedException ex) {
            Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//        public Requisicao getReq()//Mandará um objeto do tipo Requisicao para o Servidor
//        {
//            return requisicao;
//        }
}
