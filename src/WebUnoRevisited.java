import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/* todo
 *   Fazer a validação pra n deixar o usuário escolher uma opção out-of-bounds
 * todo
 *  Preciso fazer uma muleta pra próxima rodada entrar ou não no loop -> provavelmente fazer algo válido pro final e quando entrar na jogada do player 1 ou 2 ele zera
 * todo
 *  Fazer o jogo ser impossível de conmeçar com uma carta especial*/
public class WebUnoRevisited
{
    public static void main(String[] args) throws Exception
    {
        ArrayList<CartaUno> baralhoJogador1 = new ArrayList<CartaUno>();
        ArrayList<CartaUno> baralhoJogador2 = new ArrayList<CartaUno>();
        int vitorias; // 0 - sem resultado; 1 - vitória do jogador 1; -1 - vitória do jogador 2.
        //Scanner input;
        String entrada;
        CartaUno topoDaPilha; // carta no topo da pilha
        int adxEscolha; // Index da carta escolhida para os dois jogadores
        String corAtual; // Mais usado para cartas Coringa
        boolean cartaPulo = false;
        int contadorPulo = 0;

        ServerSocket socketRecepcao = new ServerSocket(6789);

        gameLoop:
        while (true)
        {
            Socket jogador1Socket = socketRecepcao.accept();
            Socket jogador2Socket = socketRecepcao.accept();

            BufferedReader doJogador1 = new BufferedReader(new InputStreamReader(jogador1Socket.getInputStream()));
            BufferedReader doJogador2 = new BufferedReader(new InputStreamReader(jogador2Socket.getInputStream()));

            DataOutputStream paraJogador1 = new DataOutputStream(jogador1Socket.getOutputStream());
            ObjectOutputStream paraJogador1StrBuilder = new ObjectOutputStream(jogador1Socket.getOutputStream());
            DataOutputStream paraJogador2 = new DataOutputStream(jogador2Socket.getOutputStream());
            ObjectOutputStream paraJogador2StrBuilder = new ObjectOutputStream(jogador2Socket.getOutputStream());

            StringBuilder Jogador1Mensagem = new StringBuilder("");
            StringBuilder Jogador2Mensagem = new StringBuilder("");


            baralhoJogador1.clear();
            baralhoJogador2.clear();
            vitorias = 0;
            topoDaPilha = new CartaUno();
            corAtual = topoDaPilha.cor;

            System.out.println("\nBem-Vindo ao UNO! Tirando as mãos...");
            comprar(7, baralhoJogador1);
            comprar(7, baralhoJogador2);

            Jogador1Mensagem.append("\nBem-Vindo ao UNO! Tirando as mãos...");
            Jogador2Mensagem.append("\nBem-Vindo ao UNO! Tirando as mãos...");


            /*****************Turns*****************/
            while (vitorias == 0)
            {
                /*
                player1Message = new StringBuilder("");
                player2Message = "";
                */

                /*****Player1's turn******/
                if (!cartaPulo)
                {
                    //cartaPulo = false;
                    adxEscolha = 0;
                    System.out.println("\nA carta no topo da pilha é: " + topoDaPilha.getFace());

                    Jogador1Mensagem.append("\nA carta no topo da pilha é: ").append(topoDaPilha.getFace());

                    if (topoDaPilha.valor >= 10) {
                        System.out.println("\nA cor atual é: " + corAtual);


                        Jogador1Mensagem.append("\nA cor atual é: ").append(corAtual);
                        //player2Message.append("\n The current color is: ").append(currentColor);
                    }



                    // Displaying user's deck
                    if (topoDaPilha.valor >= 10)
                    {
                        switch (topoDaPilha.valor)
                        {
                            case 12: // Draw 2
                                System.out.println("\nComprando duas cartas...");


                                Jogador1Mensagem.append("\nComprando duas cartas...");
                                break;

                            case 14: // Wild cards
                                System.out.println("\nComprando 4 cartas...");

                                Jogador1Mensagem.append("\nComprando 4 cartas...");
                                break;
                        }
                    }

                    System.out.println("\nJogada do jogador 1! Suas opções:");


                    Jogador1Mensagem.append("\nJogada do jogador 1! Suas opções:\n");
                    for (int i = 0; i < baralhoJogador1.size(); i++)
                    {
                        System.out.print(String.valueOf(i + 1) + ". " +
                                ((CartaUno) baralhoJogador1.get(i) ).getFace() + "\n");

                        Jogador1Mensagem.append(String.valueOf(i + 1)).append(". ").append(((CartaUno) baralhoJogador1.get(i)).getFace()).append("\n");
                    }
                    System.out.println(String.valueOf(baralhoJogador1.size() + 1 ) + ". " + "Comprar carta" + "\n" +
                            String.valueOf(baralhoJogador1.size() + 2) + ". " + "Sair");

                    Jogador1Mensagem.append(String.valueOf(baralhoJogador1.size() + 1)).append(". ").append("Comprar carta").append("\n").append(String.valueOf(baralhoJogador1.size() + 2)).append(". ").append("Sair");
                    // Repeats every time the user doesn't input a number
                    //do
                    //{
                    System.out.print("\nPor favor escreva o número de sua escolha: ");

                    Jogador1Mensagem.append("\nPor favor escreva o número de sua escolha: ");
                    paraJogador1StrBuilder.flush();
                    //paraPlayer1.writeBytes(player1Message + '\n');
                    paraJogador1StrBuilder.writeObject(Jogador1Mensagem);
                    paraJogador1StrBuilder.flush();
                    Jogador1Mensagem = new StringBuilder("");

                    do{
                        entrada = doJogador1.readLine();
                        System.out.println(entrada);
                    } while (entrada.equals(""));



                    //input = new Scanner(System.in);
                    //} while (!entrada.isBlank() );
                    // The choices were incremented to make them seem more natural (i.e not starting with 0)
                    //choiceIndex = input.nextInt() - 1;
                    adxEscolha = Integer.parseInt(entrada) - 1;

                    // Taking action
                    if (adxEscolha == baralhoJogador1.size() ) {
                        paraJogador1.writeBytes("no\n");
                        comprar(1, baralhoJogador1);
                        cartaPulo = false;
                    }
                    else if (adxEscolha == baralhoJogador1.size() + 1) {
                        paraJogador1.writeBytes("fim\n");
                        paraJogador1StrBuilder.flush();
                        StringBuilder terminal = new StringBuilder("Até mais");
                        paraJogador1StrBuilder.writeObject(terminal);
                        paraJogador1StrBuilder.flush();
                        paraJogador2StrBuilder.flush();
                        paraJogador2StrBuilder.writeObject(terminal);
                        paraJogador2StrBuilder.flush();
                        jogador1Socket.close();
                        jogador2Socket.close();
                        break gameLoop;
                    }
                    else if ( ((CartaUno) baralhoJogador1.get(adxEscolha)).podeJogar(topoDaPilha, corAtual) )
                    {
                        paraJogador1.writeBytes("sim\n");
                        topoDaPilha = (CartaUno) baralhoJogador1.get(adxEscolha);
                        baralhoJogador1.remove(adxEscolha);
                        corAtual = topoDaPilha.cor;
                        paraJogador1.flush();
                        paraJogador1.writeBytes(String.valueOf(topoDaPilha.valor + "\n"));
                        paraJogador1.flush();
                        // Producing the action of special cards
                        if (topoDaPilha.valor >= 10)
                        {
                            //playersTurn = false; // Skipping turn

                            switch (topoDaPilha.valor)
                            {
                                case 10: case 11:
                                cartaPulo = true;
                                contadorPulo = 0;
                                break;

                                case 12: // Draw 2
                                    //System.out.println("Drawing 2 cards...");
                                    comprar(2,baralhoJogador2);
                                    break;

                                case 13: case 14: // Wild cards
                                //do // Repeats every time the user doesn't input a valid color
                                //{
                                System.out.print("\nEscreva sua cor de escolha: ");

                                Jogador1Mensagem.append("\nEscreva sua cor de escolha: ");
                                //paraPlayer1.writeBytes(player1Message + "\n");
                                paraJogador1StrBuilder.flush();
                                paraJogador1StrBuilder.writeObject(Jogador1Mensagem);
                                paraJogador1StrBuilder.flush();
                                Jogador1Mensagem = new StringBuilder();

                                //input = new Scanner(System.in);

                                entrada = doJogador1.readLine();

                                //} while (!entrada.toLowerCase().contains("r") || !entrada.toLowerCase().contains("g") || !entrada.toLowerCase().contains("y") || !entrada.toLowerCase().contains("b") ); //Something I learned recently
                                if (entrada.toLowerCase().contains("verm") )
                                    corAtual = "Vermelho";
                                else if (entrada.toLowerCase().contains("verd") )
                                    corAtual = "Verde";
                                else if (entrada.toLowerCase().contains("az") )
                                    corAtual = "Azul";
                                else if (entrada.toLowerCase().contains("am") )
                                    corAtual = "Amarelo";

                                System.out.println("Você escolheu " + corAtual);

                                Jogador1Mensagem.append("Você escolheu ").append(corAtual);
                                paraJogador1StrBuilder.flush();
                                paraJogador1StrBuilder.writeObject(Jogador1Mensagem);
                                paraJogador1StrBuilder.flush();
                                Jogador1Mensagem = new StringBuilder();

                                if (topoDaPilha.valor == 14) // Coringa compra 4
                                {
                                    //System.out.println("Drawing 4 cards...");
                                    comprar(4,baralhoJogador2);
                                }
                                break;
                            }
                        }
                    } else {
                        paraJogador1.writeBytes("no\n");

                        System.out.println("Escolha inválida. Turno pulado.");

                        Jogador1Mensagem.append("Escolha inválida. Turno pulado. \n");

                        cartaPulo = false;
                    }

                    // If decks are empty
                    if (baralhoJogador1.size() == 0) {
                        vitorias = 1;
                        StringBuilder vict = new StringBuilder("jogador1");
                        paraJogador1StrBuilder.flush();
                        paraJogador1StrBuilder.writeObject(vict);
                        paraJogador1StrBuilder.flush();
                        paraJogador2StrBuilder.flush();
                        paraJogador2StrBuilder.writeObject(vict);
                        paraJogador2StrBuilder.flush();
                        jogador1Socket.close();
                        jogador2Socket.close();
                        break gameLoop;
                    }
                    else if (baralhoJogador2.size() == 0) {
                        vitorias = -1;
                        StringBuilder vict = new StringBuilder("jogador2");
                        paraJogador1StrBuilder.flush();
                        paraJogador1StrBuilder.writeObject(vict);
                        paraJogador1StrBuilder.flush();
                        paraJogador2StrBuilder.flush();
                        paraJogador2StrBuilder.writeObject(vict);
                        paraJogador2StrBuilder.flush();
                        jogador1Socket.close();
                        jogador2Socket.close();
                        break gameLoop;
                    }


                }

                if (contadorPulo >= 1) {
                    cartaPulo = false;
                }
                contadorPulo++;

                /************ Player2's turn **************/
                if (!cartaPulo)
                {
                    //cartaPulo = false;
                    adxEscolha = 0;
                    System.out.println("\nA carta no topo da pilha é: " + topoDaPilha.getFace());

                    Jogador2Mensagem.append("\nA carta no topo da pilha é: ").append(topoDaPilha.getFace());

                    if (topoDaPilha.valor >= 10) {
                        System.out.println("\nA cor atual é: " + corAtual);


                        Jogador2Mensagem.append("\nA cor atual é: ").append(corAtual);
                        //player2Message.append("\n The current color is: ").append(currentColor);
                    }



                    // Displaying user's deck
                    if (topoDaPilha.valor >= 10)
                    {
                        switch (topoDaPilha.valor)
                        {
                            case 12: // Draw 2
                                System.out.println("\nComprando duas cartas...");


                                Jogador2Mensagem.append("\nComprando duas cartas...");
                                break;

                            case 14: // Wild cards
                                System.out.println("\nComprando 4 cartas...");

                                Jogador2Mensagem.append("\nComprando 4 cartas...");
                                break;
                        }
                    }

                    System.out.println("\nJogada do jogador 1! Suas opções:");


                    Jogador2Mensagem.append("\nJogada do jogador 1! Suas opções::\n");
                    for (int i = 0; i < baralhoJogador2.size(); i++)
                    {
                        System.out.print(String.valueOf(i + 1) + ". " +
                                ((CartaUno) baralhoJogador2.get(i) ).getFace() + "\n");

                        Jogador2Mensagem.append(String.valueOf(i + 1)).append(". ").append(((CartaUno) baralhoJogador2.get(i)).getFace()).append("\n");
                    }
                    System.out.println(String.valueOf(baralhoJogador2.size() + 1 ) + ". " + "Comprar carta" + "\n" +
                            String.valueOf(baralhoJogador2.size() + 2) + ". " + "Sair");

                    Jogador2Mensagem.append(String.valueOf(baralhoJogador2.size() + 1)).append(". ").append("Comprar carta").append("\n").append(String.valueOf(baralhoJogador2.size() + 2)).append(". ").append("Sair");
                    // Repeats every time the user doesn't input a number
                    //do
                    //{
                    System.out.print("\nPor favor escreva o número de sua escolha: ");

                    Jogador2Mensagem.append("\nPor favor escreva o número de sua escolha: ");
                    paraJogador2StrBuilder.flush();
                    //paraPlayer1.writeBytes(player1Message + '\n');
                    paraJogador2StrBuilder.writeObject(Jogador2Mensagem);
                    paraJogador2StrBuilder.flush();
                    Jogador2Mensagem = new StringBuilder("");

                    do{
                        entrada = doJogador2.readLine();
                        System.out.println(entrada);
                    } while (entrada.equals(""));



                    //input = new Scanner(System.in);
                    //} while (!entrada.isBlank() );
                    // The choices were incremented to make them seem more natural (i.e not starting with 0)
                    //choiceIndex = input.nextInt() - 1;
                    adxEscolha = Integer.parseInt(entrada) - 1;

                    // Taking action
                    if (adxEscolha == baralhoJogador2.size() ) {
                        paraJogador2.writeBytes("no\n");
                        comprar(1, baralhoJogador2);
                        cartaPulo = false;
                    }
                    else if (adxEscolha == baralhoJogador2.size() + 1) {
                        paraJogador2.writeBytes("fim\n");
                        paraJogador1StrBuilder.flush();
                        StringBuilder terminal = new StringBuilder("Até logo");
                        paraJogador1StrBuilder.writeObject(terminal);
                        paraJogador1StrBuilder.flush();
                        paraJogador2StrBuilder.flush();
                        paraJogador2StrBuilder.writeObject(terminal);
                        paraJogador2StrBuilder.flush();
                        jogador1Socket.close();
                        jogador2Socket.close();
                        break gameLoop;
                    }
                    else if ( ((CartaUno) baralhoJogador2.get(adxEscolha)).podeJogar(topoDaPilha, corAtual) )
                    {
                        paraJogador2.writeBytes("sim\n");
                        topoDaPilha = (CartaUno) baralhoJogador2.get(adxEscolha);
                        baralhoJogador2.remove(adxEscolha);
                        corAtual = topoDaPilha.cor;
                        paraJogador2.flush();
                        paraJogador2.writeBytes(String.valueOf(topoDaPilha.valor + "\n"));
                        paraJogador2.flush();
                        // Producing the action of special cards
                        if (topoDaPilha.valor >= 10)
                        {
                            //playersTurn = false; // Skipping turn

                            switch (topoDaPilha.valor)
                            {
                                case 10: case 11:
                                cartaPulo = true;
                                contadorPulo = 0;
                                break;

                                case 12: // Draw 2
                                    //System.out.println("Drawing 2 cards...");
                                    comprar(2,baralhoJogador1);
                                    break;

                                case 13: case 14: // Wild cards
                                //do // Repeats every time the user doesn't input a valid color
                                //{
                                System.out.print("\nEscreva sua cor de escolha: ");

                                Jogador2Mensagem.append("\nEscreva sua cor de escolha: ");
                                //paraPlayer1.writeBytes(player1Message + "\n");
                                paraJogador2StrBuilder.flush();
                                paraJogador2StrBuilder.writeObject(Jogador2Mensagem);
                                paraJogador2StrBuilder.flush();
                                Jogador2Mensagem = new StringBuilder();

                                //input = new Scanner(System.in);

                                entrada = doJogador2.readLine();

                                //} while (!entrada.toLowerCase().contains("r") || !entrada.toLowerCase().contains("g") || !entrada.toLowerCase().contains("y") || !entrada.toLowerCase().contains("b") ); //Something I learned recently
                                if (entrada.toLowerCase().contains("verm") )
                                    corAtual = "Vermelho";
                                else if (entrada.toLowerCase().contains("verd") )
                                    corAtual = "Verde";
                                else if (entrada.toLowerCase().contains("az") )
                                    corAtual = "Azul";
                                else if (entrada.toLowerCase().contains("am") )
                                    corAtual = "Amarelo";

                                System.out.println("Você escolheu " + corAtual);

                                Jogador2Mensagem.append("Você escolheu ").append(corAtual);
                                paraJogador2StrBuilder.flush();
                                paraJogador2StrBuilder.writeObject(Jogador2Mensagem);
                                paraJogador2StrBuilder.flush();
                                Jogador2Mensagem = new StringBuilder();

                                if (topoDaPilha.valor == 14) // Wild draw 4
                                {
                                    //System.out.println("Drawing 4 cards...");
                                    comprar(4,baralhoJogador1);
                                }
                                break;
                            }
                        }
                    } else {
                        paraJogador2.writeBytes("no\n");

                        System.out.println("Escolha inválida. Turno pulado.");

                        Jogador2Mensagem.append("Escolha inválida. Turno pulado. \n");

                        cartaPulo = false;
                    }

                    // If decks are empty
                    if (baralhoJogador1.size() == 0) {
                        vitorias = 1;
                        StringBuilder vict = new StringBuilder("jogador1");
                        paraJogador1StrBuilder.flush();
                        paraJogador1StrBuilder.writeObject(vict);
                        paraJogador1StrBuilder.flush();
                        paraJogador2StrBuilder.flush();
                        paraJogador2StrBuilder.writeObject(vict);
                        paraJogador2StrBuilder.flush();
                        jogador1Socket.close();
                        jogador2Socket.close();
                        break gameLoop;
                    }
                    else if (baralhoJogador2.size() == 0) {
                        vitorias = -1;
                        StringBuilder vict = new StringBuilder("jogador2");
                        paraJogador1StrBuilder.flush();
                        paraJogador1StrBuilder.writeObject(vict);
                        paraJogador1StrBuilder.flush();
                        paraJogador2StrBuilder.flush();
                        paraJogador2StrBuilder.writeObject(vict);
                        paraJogador2StrBuilder.flush();
                        jogador1Socket.close();
                        jogador2Socket.close();
                        break gameLoop;
                    }


                }

                if (contadorPulo >= 1) {
                    cartaPulo = false;
                }
                contadorPulo++;

            } // turns loop end

            /*************Results**************/
            /*if (win == 1)
                System.out.println("Player 1 wins :)");
            else
                System.out.println("Player 2 wins ;)");

            System.out.print("\nPlay again? ");
            input = new Scanner(System.in);

            if (input.next().toLowerCase().contains("n") )
                break;

             */
        } // game loop end

        System.out.println("Até mais");
    }
    // For drawing cards
    public static void comprar(int cards, ArrayList<CartaUno> deck)
    {
        for (int i = 0; i < cards; i++)
            deck.add(new CartaUno() );
    }
}