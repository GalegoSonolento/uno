import java.util.Random;

public class CartaUno
{
    public String cor;
    public int valor;
    private Random randomizador;
    private String face;

    public CartaUno(int v, String c)
    {
        valor = v;
        cor = c;
    }

    // Creates a random card
    public CartaUno()
    {
        randomizador = new Random();
        valor = randomizador.nextInt(28); // 108 cartas em um baralho normal de uno. Dá pra reduzir pra 27 se ignorar as cores
        // Agregando o valor
        if (valor >= 14) // Algumas cartas são mais comuns que outras
            valor -= 14;
        // Agregando a cor
        randomizador = new Random();
        switch(randomizador.nextInt(4) )
        {
            case 0: cor = "Vermelho";
                break;
            case 1: cor = "Verde";
                break;
            case 2: cor = "Azul";
                break;
            case 3: cor = "Amarelo";
                break;
        }
        // Se a carta é especial
        if (valor >= 13)
            cor = "none";
    }

    public String getFace()
    {
        /* Retorna a face da carta (aquilo que o jogador vê)
         * Ex. [Vermelho 5]
         */
        face = "[";
        if (cor != "none")
        {
            face += this.cor + " ";
        }

        switch(this.valor)
        {
            default: face += String.valueOf(this.valor);
                break;
            case 10: face += "Pular";
                break;
            case 11: face += "Reverter";
                break;
            case 12: face += "Compre +2";
                break;
            case 13: face += "Coringa cores";
                break;
            case 14: face += "Coringa +4";
                break;
        }
        face += "]";
        return face;
    }

    // Determina se o jogador pode colocar uma carta em cima de outra já existente
    // A cor precisa necessariamente ser colocada aqui porque pode ser que o jogador jogue um Coringa, mantendo a mesma carta, e só muda a cor
    public boolean podeJogar(CartaUno o, String c)
    {
        if (this.cor == c)
            return true;
        else if (this.valor == o.valor)
            return true;
        else if (this.cor == "none") // Cartas Coringa
            return true;
        return false;
    }
}
