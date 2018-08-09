package Model;

public class Resposta implements java.io.Serializable{
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// classe de resposta, o mesmo comentario da classe de requisicao
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public Long servidorId, serverStatus; //serverStatus: 9 -> Servidor em espera // 8 -> servidor cheio // 7 - servidor em jogo // 1 -> entrou na sala // 0 -> nao entrou na sala // 6 -> resultado do turno
	public Long jogadorId; //id do jogador caso senha possivel entrar no servidor  // CASO O JOGADOR MORRA, ENVIAR ID DE COM 0
	public int qtdJogadores;
	public String[][] jogadores; 
	public String acaoesDaJogada; //mandar uma string no formato ("Michele atirou em Matheus\n" + "Matheus defendeu\n"+"Theo recarregou\n"+"Nelson defendeu\n")
	//matriz com Id dos jogadores na sala, nick, cor avatar e acao // Id = 0 -> Sem jogador
	//(Id primeira linha, Nick segunda linha, Cor avatar terceira linha, Acao quarta linha, Alvo quinta linha) 
	//acao que o jogador fez; // acao atual : 0 -> em espera ; 1 - Atirar ; 2 - Defender ; 3 - Recarregar ;
	//se o jogador atirou, a ultima linha corresponde ao id de quem ele atirou. Se nao atirou, deve ser valor 0
	//ATENCAO, CASO UM JOGADOR SEJA MORTO, ENVIAR '0' COMO ID E '0' COMO NICKNAME, AVATAR E TUDO NA VDD.
	//AO FAZER A LOGICA NO SERVIDOR, DAR PRIORIDADE NA HORA DE VERIFICAR A ACAO. APENAS OLHE A ULTIMA LINHA SE A ACAO DO JOGAR FOR DE ATIRAR
}