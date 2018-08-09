package Model;

public class Jogador {
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // jogador eu peguei do Nelson, so acrescentei algumas variaveis, talvez ate desnecessariamente
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Long id;
    public Long jogadorIdAlvo;
    public String nickname;
    public String corAvatar;
    public Jogador alvo;
    public boolean vida;
    public boolean protecao;
    public boolean acaoValida;
    public int action;
    public boolean balas;

    public Jogador() {
        this.vida = true;
        this.protecao = false;
        this.balas = false;
        this.action = 0;
        this.acaoValida = false;
    }

    public void defender() {
        this.protecao = true;
    }

    public void carregar() {
        this.balas = true;
    }

    public void atirar(Jogador atirador, Jogador alvo) {
        atirador.balas = false;
        if (!alvo.protecao) {
            alvo.vida = false;
        }
    }

}
