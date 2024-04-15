import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

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
        ArrayList<Unocard> player1deck = new ArrayList<Unocard>();
        ArrayList<Unocard> player2deck = new ArrayList<Unocard>();
        int win; // 0 - no result; 1 - win; -1 - loss. 
        Scanner input;
        String entrada;
        Unocard topCard; // card on top of the "pile"
        int choiceIndex; // Index of chosen card for both player and computer
        String currentColor; // Mainly used for wild cards
        boolean cartaPulo = false;
        int puloCounter = 0;

        ServerSocket socketRecepcao = new ServerSocket(6789);

        gameLoop:
        while (true)
        {
            Socket player1Socket = socketRecepcao.accept();
            Socket player2Socket = socketRecepcao.accept();

            BufferedReader doPlayer1 = new BufferedReader(new InputStreamReader(player1Socket.getInputStream()));
            BufferedReader doPlayer2 = new BufferedReader(new InputStreamReader(player2Socket.getInputStream()));

            DataOutputStream paraPlayer1 = new DataOutputStream(player1Socket.getOutputStream());
            ObjectOutputStream paraPlayer1StrBuilder = new ObjectOutputStream(player1Socket.getOutputStream());
            DataOutputStream paraPlayer2 = new DataOutputStream(player2Socket.getOutputStream());
            ObjectOutputStream paraPlayer2StrBuilder = new ObjectOutputStream(player2Socket.getOutputStream());

            StringBuilder player1Message = new StringBuilder("");
            StringBuilder player2Message = new StringBuilder("");


            player1deck.clear();
            player2deck.clear();
            win = 0;
            topCard = new Unocard();
            currentColor = topCard.color;

            System.out.println("\nWelcome to Uno! Initialising decks...");
            draw(7, player1deck);
            draw(7, player2deck);

            player1Message.append("\nWelcome to Uno! Initialising decks...");
            player2Message.append("\nWelcome to Uno! Initialising decks...");


            /*****************Turns*****************/
            while (win == 0)
            {
                /*
                player1Message = new StringBuilder("");
                player2Message = "";
                */

                /*****Player1's turn******/
                if (!cartaPulo)
                {
                    //cartaPulo = false;
                    choiceIndex = 0;
                    System.out.println("\nThe top card is: " + topCard.getFace());

                    player1Message.append("\nThe top card is: ").append(topCard.getFace());

                    if (topCard.value >= 10) {
                        System.out.println("\n The current color is: " + currentColor);


                        player1Message.append("\n The current color is: ").append(currentColor);
                        //player2Message.append("\n The current color is: ").append(currentColor);
                    }



                    // Displaying user's deck
                    if (topCard.value >= 10)
                    {
                        switch (topCard.value)
                        {
                            case 12: // Draw 2
                                System.out.println("\nDrawing 2 cards...");


                                player1Message.append("\nDrawing 2 cards...");
                                break;

                            case 14: // Wild cards
                                System.out.println("\nDrawing 4 cards...");

                                player1Message.append("\nDrawing 4 cards...");
                                break;
                        }
                    }

                    System.out.println("\nPlayer 1's turn! Your choices:");


                    player1Message.append("\nPlayer 1's turn! Your choices:\n");
                    for (int i = 0; i < player1deck.size(); i++)
                    {
                        System.out.print(String.valueOf(i + 1) + ". " +
                                ((Unocard) player1deck.get(i) ).getFace() + "\n");

                        player1Message.append(String.valueOf(i + 1)).append(". ").append(((Unocard) player1deck.get(i)).getFace()).append("\n");
                    }
                    System.out.println(String.valueOf(player1deck.size() + 1 ) + ". " + "Draw card" + "\n" +
                            String.valueOf(player1deck.size() + 2) + ". " + "Quit");

                    player1Message.append(String.valueOf(player1deck.size() + 1)).append(". ").append("Draw card").append("\n").append(String.valueOf(player1deck.size() + 2)).append(". ").append("Quit");
                    // Repeats every time the user doesn't input a number
                    //do
                    //{
                    System.out.print("\nPlaease input the number of your choice: ");

                    player1Message.append("\nPlaease input the number of your choice: ");
                    paraPlayer1StrBuilder.flush();
                    //paraPlayer1.writeBytes(player1Message + '\n');
                    paraPlayer1StrBuilder.writeObject(player1Message);
                    paraPlayer1StrBuilder.flush();
                    player1Message = new StringBuilder("");

                    do{
                        entrada = doPlayer1.readLine();
                        System.out.println(entrada);
                    } while (entrada.equals(""));



                    //input = new Scanner(System.in);
                    //} while (!entrada.isBlank() );
                    // The choices were incremented to make them seem more natural (i.e not starting with 0)
                    //choiceIndex = input.nextInt() - 1;
                    choiceIndex = Integer.parseInt(entrada) - 1;

                    // Taking action
                    if (choiceIndex == player1deck.size() ) {
                        paraPlayer1.writeBytes("no\n");
                        draw(1, player1deck);
                        cartaPulo = false;
                    }
                    else if (choiceIndex == player1deck.size() + 1) {
                        paraPlayer1.writeBytes("end\n");
                        paraPlayer1StrBuilder.flush();
                        StringBuilder terminal = new StringBuilder("Bye bye");
                        paraPlayer1StrBuilder.writeObject(terminal);
                        paraPlayer1StrBuilder.flush();
                        paraPlayer2StrBuilder.flush();
                        paraPlayer2StrBuilder.writeObject(terminal);
                        paraPlayer2StrBuilder.flush();
                        player1Socket.close();
                        player2Socket.close();
                        break gameLoop;
                    }
                    else if ( ((Unocard) player1deck.get(choiceIndex)).canPlace(topCard, currentColor) )
                    {
                        paraPlayer1.writeBytes("yes\n");
                        topCard = (Unocard) player1deck.get(choiceIndex);
                        player1deck.remove(choiceIndex);
                        currentColor = topCard.color;
                        paraPlayer1.flush();
                        paraPlayer1.writeBytes(String.valueOf(topCard.value + "\n"));
                        paraPlayer1.flush();
                        // Producing the action of special cards
                        if (topCard.value >= 10)
                        {
                            //playersTurn = false; // Skipping turn

                            switch (topCard.value)
                            {
                                case 10: case 11:
                                cartaPulo = true;
                                puloCounter = 0;
                                break;

                                case 12: // Draw 2
                                    //System.out.println("Drawing 2 cards...");
                                    draw(2,player2deck);
                                    break;

                                case 13: case 14: // Wild cards
                                //do // Repeats every time the user doesn't input a valid color
                                //{
                                System.out.print("\nEnter the color you want: ");

                                player1Message.append("\nEnter the color you want: ");
                                //paraPlayer1.writeBytes(player1Message + "\n");
                                paraPlayer1StrBuilder.flush();
                                paraPlayer1StrBuilder.writeObject(player1Message);
                                paraPlayer1StrBuilder.flush();
                                player1Message = new StringBuilder();

                                //input = new Scanner(System.in);

                                entrada = doPlayer1.readLine();

                                //} while (!entrada.toLowerCase().contains("r") || !entrada.toLowerCase().contains("g") || !entrada.toLowerCase().contains("y") || !entrada.toLowerCase().contains("b") ); //Something I learned recently
                                if (entrada.toLowerCase().contains("r") )
                                    currentColor = "Red";
                                else if (entrada.toLowerCase().contains("g") )
                                    currentColor = "Green";
                                else if (entrada.toLowerCase().contains("b") )
                                    currentColor = "Blue";
                                else if (entrada.toLowerCase().contains("y") )
                                    currentColor = "Yellow";

                                System.out.println("You chose " + currentColor);

                                player1Message.append("You chose ").append(currentColor);
                                paraPlayer1StrBuilder.flush();
                                paraPlayer1StrBuilder.writeObject(player1Message);
                                paraPlayer1StrBuilder.flush();
                                player1Message = new StringBuilder();

                                if (topCard.value == 14) // Wild draw 4
                                {
                                    //System.out.println("Drawing 4 cards...");
                                    draw(4,player2deck);
                                }
                                break;
                            }
                        }
                    } else {
                        paraPlayer1.writeBytes("no\n");

                        System.out.println("Invalid choice. Turn skipped.");

                        player1Message.append("Invalid choice. Turn skipped. \n");

                        cartaPulo = false;
                    }

                    // If decks are empty
                    if (player1deck.size() == 0) {
                        win = 1;
                        StringBuilder vict = new StringBuilder("player1");
                        paraPlayer1StrBuilder.flush();
                        paraPlayer1StrBuilder.writeObject(vict);
                        paraPlayer1StrBuilder.flush();
                        paraPlayer2StrBuilder.flush();
                        paraPlayer2StrBuilder.writeObject(vict);
                        paraPlayer2StrBuilder.flush();
                        player1Socket.close();
                        player2Socket.close();
                        break gameLoop;
                    }
                    else if (player2deck.size() == 0) {
                        win = -1;
                        StringBuilder vict = new StringBuilder("player2");
                        paraPlayer1StrBuilder.flush();
                        paraPlayer1StrBuilder.writeObject(vict);
                        paraPlayer1StrBuilder.flush();
                        paraPlayer2StrBuilder.flush();
                        paraPlayer2StrBuilder.writeObject(vict);
                        paraPlayer2StrBuilder.flush();
                        player1Socket.close();
                        player2Socket.close();
                        break gameLoop;
                    }


                }

                if (puloCounter >= 1) {
                    cartaPulo = false;
                }
                puloCounter++;

                /************ Player2's turn **************/
                if (!cartaPulo)
                {
                    //cartaPulo = false;
                    choiceIndex = 0;
                    System.out.println("\nThe top card is: " + topCard.getFace());

                    player2Message.append("\nThe top card is: ").append(topCard.getFace());

                    if (topCard.value >= 10) {
                        System.out.println("\n The current color is: " + currentColor);


                        player2Message.append("\n The current color is: ").append(currentColor);
                        //player2Message.append("\n The current color is: ").append(currentColor);
                    }



                    // Displaying user's deck
                    if (topCard.value >= 10)
                    {
                        switch (topCard.value)
                        {
                            case 12: // Draw 2
                                System.out.println("\nDrawing 2 cards...");


                                player2Message.append("\nDrawing 2 cards...");
                                break;

                            case 14: // Wild cards
                                System.out.println("\nDrawing 4 cards...");

                                player2Message.append("\nDrawing 4 cards...");
                                break;
                        }
                    }

                    System.out.println("\nPlayer 2's turn! Your choices:");


                    player2Message.append("\nPlayer 2's turn! Your choices:\n");
                    for (int i = 0; i < player2deck.size(); i++)
                    {
                        System.out.print(String.valueOf(i + 1) + ". " +
                                ((Unocard) player2deck.get(i) ).getFace() + "\n");

                        player2Message.append(String.valueOf(i + 1)).append(". ").append(((Unocard) player2deck.get(i)).getFace()).append("\n");
                    }
                    System.out.println(String.valueOf(player2deck.size() + 1 ) + ". " + "Draw card" + "\n" +
                            String.valueOf(player2deck.size() + 2) + ". " + "Quit");

                    player2Message.append(String.valueOf(player2deck.size() + 1)).append(". ").append("Draw card").append("\n").append(String.valueOf(player2deck.size() + 2)).append(". ").append("Quit");
                    // Repeats every time the user doesn't input a number
                    //do
                    //{
                    System.out.print("\nPlaease input the number of your choice: ");

                    player2Message.append("\nPlaease input the number of your choice: ");
                    paraPlayer2StrBuilder.flush();
                    //paraPlayer1.writeBytes(player1Message + '\n');
                    paraPlayer2StrBuilder.writeObject(player2Message);
                    paraPlayer2StrBuilder.flush();
                    player2Message = new StringBuilder("");

                    do{
                        entrada = doPlayer2.readLine();
                        System.out.println(entrada);
                    } while (entrada.equals(""));



                    //input = new Scanner(System.in);
                    //} while (!entrada.isBlank() );
                    // The choices were incremented to make them seem more natural (i.e not starting with 0)
                    //choiceIndex = input.nextInt() - 1;
                    choiceIndex = Integer.parseInt(entrada) - 1;

                    // Taking action
                    if (choiceIndex == player2deck.size() ) {
                        paraPlayer2.writeBytes("no\n");
                        draw(1, player2deck);
                        cartaPulo = false;
                    }
                    else if (choiceIndex == player2deck.size() + 1) {
                        paraPlayer2.writeBytes("end\n");
                        paraPlayer1StrBuilder.flush();
                        StringBuilder terminal = new StringBuilder("Bye bye");
                        paraPlayer1StrBuilder.writeObject(terminal);
                        paraPlayer1StrBuilder.flush();
                        paraPlayer2StrBuilder.flush();
                        paraPlayer2StrBuilder.writeObject(terminal);
                        paraPlayer2StrBuilder.flush();
                        player1Socket.close();
                        player2Socket.close();
                        break gameLoop;
                    }
                    else if ( ((Unocard) player2deck.get(choiceIndex)).canPlace(topCard, currentColor) )
                    {
                        paraPlayer2.writeBytes("yes\n");
                        topCard = (Unocard) player2deck.get(choiceIndex);
                        player2deck.remove(choiceIndex);
                        currentColor = topCard.color;
                        paraPlayer2.flush();
                        paraPlayer2.writeBytes(String.valueOf(topCard.value + "\n"));
                        paraPlayer2.flush();
                        // Producing the action of special cards
                        if (topCard.value >= 10)
                        {
                            //playersTurn = false; // Skipping turn

                            switch (topCard.value)
                            {
                                case 10: case 11:
                                cartaPulo = true;
                                puloCounter = 0;
                                break;

                                case 12: // Draw 2
                                    //System.out.println("Drawing 2 cards...");
                                    draw(2,player1deck);
                                    break;

                                case 13: case 14: // Wild cards
                                //do // Repeats every time the user doesn't input a valid color
                                //{
                                System.out.print("\nEnter the color you want: ");

                                player2Message.append("\nEnter the color you want: ");
                                //paraPlayer1.writeBytes(player1Message + "\n");
                                paraPlayer2StrBuilder.flush();
                                paraPlayer2StrBuilder.writeObject(player2Message);
                                paraPlayer2StrBuilder.flush();
                                player2Message = new StringBuilder();

                                //input = new Scanner(System.in);

                                entrada = doPlayer2.readLine();

                                //} while (!entrada.toLowerCase().contains("r") || !entrada.toLowerCase().contains("g") || !entrada.toLowerCase().contains("y") || !entrada.toLowerCase().contains("b") ); //Something I learned recently
                                if (entrada.toLowerCase().contains("r") )
                                    currentColor = "Red";
                                else if (entrada.toLowerCase().contains("g") )
                                    currentColor = "Green";
                                else if (entrada.toLowerCase().contains("b") )
                                    currentColor = "Blue";
                                else if (entrada.toLowerCase().contains("y") )
                                    currentColor = "Yellow";

                                System.out.println("You chose " + currentColor);

                                player2Message.append("You chose ").append(currentColor);
                                paraPlayer2StrBuilder.flush();
                                paraPlayer2StrBuilder.writeObject(player2Message);
                                paraPlayer2StrBuilder.flush();
                                player2Message = new StringBuilder();

                                if (topCard.value == 14) // Wild draw 4
                                {
                                    //System.out.println("Drawing 4 cards...");
                                    draw(4,player1deck);
                                }
                                break;
                            }
                        }
                    } else {
                        paraPlayer2.writeBytes("no\n");

                        System.out.println("Invalid choice. Turn skipped.");

                        player2Message.append("Invalid choice. Turn skipped. \n");

                        cartaPulo = false;
                    }

                    // If decks are empty
                    if (player1deck.size() == 0) {
                        win = 1;
                        StringBuilder vict = new StringBuilder("player1");
                        paraPlayer1StrBuilder.flush();
                        paraPlayer1StrBuilder.writeObject(vict);
                        paraPlayer1StrBuilder.flush();
                        paraPlayer2StrBuilder.flush();
                        paraPlayer2StrBuilder.writeObject(vict);
                        paraPlayer2StrBuilder.flush();
                        player1Socket.close();
                        player2Socket.close();
                        break gameLoop;
                    }
                    else if (player2deck.size() == 0) {
                        win = -1;
                        StringBuilder vict = new StringBuilder("player2");
                        paraPlayer1StrBuilder.flush();
                        paraPlayer1StrBuilder.writeObject(vict);
                        paraPlayer1StrBuilder.flush();
                        paraPlayer2StrBuilder.flush();
                        paraPlayer2StrBuilder.writeObject(vict);
                        paraPlayer2StrBuilder.flush();
                        player1Socket.close();
                        player2Socket.close();
                        break gameLoop;
                    }


                }

                if (puloCounter >= 1) {
                    cartaPulo = false;
                }
                puloCounter++;

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

        System.out.println("Bye bye");
    }
    // For drawing cards
    public static void draw(int cards, ArrayList<Unocard> deck)
    {
        for (int i = 0; i < cards; i++)
            deck.add(new Unocard() );
    }
}