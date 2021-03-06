package Model;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import Service.ServidorService;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor {

	// jogadores no servidor// linha 1 - Id // linha 2 - Nickname // linha 3 -
	// corAvatar // linha 4 - Acao // linha 5 - alvo
	private static volatile String[][] jogadoresServidor = { { "1", "2", "3", "4", "5" }, { "0", "0", "0", "0", "0" },
			{ "0", "0", "0", "0", "0" }, { "0", "0", "0", "0", "0" }, { "0", "0", "0", "0", "0" } };
	private static volatile boolean inicioPartida = true;

	public static void main(String[] args) throws IOException {
		ServidorService serv = new ServidorService(9000);

		// vetor requisicoes
		ArrayList<Requisicao> listaREQ = new ArrayList<>();
		ArrayList<Jogador> listaJogs = new ArrayList<>();
		serv.start();

		Resposta res = new Resposta();

		// inicia thread de verificacao de inicio de partida
		(new verificadorDeInicio()).start();

		int numJogadores = serv.numJogadores;
		int j = 0;
		while (inicioPartida) {
			numJogadores = serv.numJogadores;

			if (j < 2 * numJogadores) {
				Requisicao primeiraReq = serv.getPrimeiraReq();
				jogadoresServidor[1][((int) (long) primeiraReq.jogadorId) - 1] = primeiraReq.jogadorNickname;
				jogadoresServidor[2][((int) (long) primeiraReq.jogadorId) - 1] = primeiraReq.jogadorCorAvatar;
				// se a acao do jogador for "9", o usuario esta conectado, se for "7" ele esta
				// pronto para jogar
				// atualiza a acao do jogador na matriz de jogadores
				jogadoresServidor[3][((int) (long) primeiraReq.jogadorId) - 1] = String.valueOf(primeiraReq.acao);

				
				
				if (primeiraReq.acao == 9) {
					Resposta primeiraResp = new Resposta();
					primeiraResp.jogadores = jogadoresServidor;
					primeiraResp.qtdJogadores = numJogadores;
					primeiraResp.serverStatus = Long.valueOf(1);
					primeiraResp.jogadorId = primeiraReq.jogadorId;
					
					serv.enviarRespDedicada(primeiraResp, (int) (long) primeiraReq.jogadorId);
				}

				j++;
			}
		}
		System.out.println("teste");
		for (int m = 1; m <= numJogadores; m++) {
			Resposta segundaResp = new Resposta();
			for (int k = 0; k < jogadoresServidor[0].length; k++)
				jogadoresServidor[3][k] = "0";
			
			segundaResp.jogadores = jogadoresServidor;
			segundaResp.qtdJogadores = numJogadores;
			segundaResp.serverStatus = Long.valueOf(7);
			segundaResp.jogadorId = Long.valueOf(m);
			serv.enviarRespDedicada(segundaResp, m);
		}
			

		while (listaREQ.size() > 1) {
			listaREQ = serv.getReq();
			listaJogs = PegaListaJogadores(listaREQ);
			/////////// INICIAR FASES DO TURNO///////////
			faseCarrega(listaJogs);
			faseDefende(listaJogs);
			faseAtira(listaJogs);
			numJogadores = contagemMortos(listaJogs, numJogadores); // Configura o Id do jogador para 0 caso ele esteja
																	// morto
			for (int z = 0; z < listaJogs.size(); z++) {
				ConverteJogadorToReq(listaJogs.get(z));
			}

			/////////// PREPARANDO RESPOSTA//////////////////////
			for (int i = 0; i < listaREQ.size(); i++) {
				res = setResp(res, listaREQ.get(i), i); // formulando a Matriz String com as respectivas informaçoes
				res = setStringResp(listaREQ.get(i), res, i); // formulando a String com as respectivas infos
			}

			////////// ENVIAR RESPOSTA (?)////////////////
			serv.enviarResp(res, numJogadores);
		}
	}

	public static void faseDefende(ArrayList<Jogador> listaJOG) {
		for (Jogador j : listaJOG) {
			if (j.action == 2) {
				j.defender();
			}
		}
	}

	public static void faseCarrega(ArrayList<Jogador> listaJOG) {
		for (Jogador j : listaJOG) {
			if (j.action == 3) {
				j.carregar();
			}
		}
	}

	public static void faseAtira(ArrayList<Jogador> listaJOG) {

		for (Jogador j : listaJOG) {
			if (j.action == 1) {
				for (Jogador x : listaJOG) {
					if (j.jogadorIdAlvo.equals(x.id)) {
						j.atirar(j, x);
					}
				}
			}
		}
	}

	public static int contagemMortos(ArrayList<Jogador> listaJOG, int numJogadores) {
		for (Jogador j : listaJOG) {
			if (!j.vida) {
				j.nickname = "0";
				j.id = Long.valueOf(0);
				numJogadores--;
			}
		}
		return numJogadores;
	}

	public static Resposta setResp(Resposta res, Requisicao req, int i) {
		res.jogadores[0][i] = Long.toString(req.jogadorId);
		res.jogadores[1][i] = req.jogadorNickname;
		res.jogadores[2][i] = req.jogadorCorAvatar;
		res.jogadores[3][i] = Long.toString(req.acao);
		if (req.acao == 1) {
			res.jogadores[4][i] = Long.toString(req.jogadorIdAlvo);
		} else {
			res.jogadores[4][i] = "0";
		}
		return res;

	}

	public static Resposta setStringResp(Requisicao req, Resposta res, int i) {
		if (req.acao != 1) {
			res.acaoesDaJogada = (res.jogadores[1][i]) + " " + setAcao(Long.parseLong(res.jogadores[3][i]));
		} else {
			res.acaoesDaJogada = (res.jogadores[1][i] + " " + setAcao(Long.parseLong(res.jogadores[3][i])) + " em "
					+ res.jogadores[(int) (long) Long.parseLong(res.jogadores[4][i]) - 1][1]);
		}
		return res;
	}

	public static String setAcao(Long a) {
		if (a == 1) {
			return "atirou";
		} else if (a == 2) {
			return "defendeu";
		} else {
			return "carregou";
		}
	}

	public static ArrayList<Jogador> PegaListaJogadores(ArrayList<Requisicao> ListaREQ) {
		ArrayList<Jogador> ListJOG = new ArrayList<>();
		for (int i = 0; i < ListaREQ.size(); i++) {
			Jogador player = new Jogador();
			player = ConverteReqToJogador(ListaREQ.get(i));
			ListJOG.add(i, player);
		}
		return ListJOG;
	}

	public static Jogador ConverteReqToJogador(Requisicao r) {
		Jogador j = new Jogador();
		j.id = r.jogadorId;
		j.nickname = r.jogadorNickname;
		j.corAvatar = r.jogadorCorAvatar;
		j.action = (int) (long) r.acao;
		j.jogadorIdAlvo = r.jogadorIdAlvo;
		return j;
	}

	public static Requisicao ConverteJogadorToReq(Jogador j) {
		Requisicao r = new Requisicao();
		r.jogadorId = j.id;
		r.jogadorNickname = j.nickname;
		r.jogadorCorAvatar = j.corAvatar;
		r.acao = (long) (int) j.action;
		r.jogadorIdAlvo = j.jogadorIdAlvo;
		return r;
	}

	static class verificadorDeInicio extends Thread {
		// thread para verificar se todos os jogadores na sala estao prontos (no minio 2
		// jogadores)

		public void run() {
			int cnumJogadores = 0;
			int cnumJogadoresProntos = 0;
			while (inicioPartida) {

				for (int i = 0; i < jogadoresServidor[0].length; i++) {				
					if (!jogadoresServidor[3][i].equals("0")) 
						cnumJogadores++;

					if (jogadoresServidor[3][i].equals("7")) 
						cnumJogadoresProntos++;

				}
				
				if (cnumJogadores >= 2 && cnumJogadoresProntos == cnumJogadores) 
					inicioPartida = false;	
				
				cnumJogadores = 0;
				cnumJogadoresProntos = 0;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
