import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class UnoRede
{
    public static void main(String[] args) throws IOException {

        ServerSocket socketRecepcao = new ServerSocket(6789);

        /*
        Socket socketConexao = socketRecepcao.accept();
        DataOutputStream paraCliente = new DataOutputStream(socketConexao.getOutputStream());
        BufferedReader doCliente = new BufferedReader(new InputStreamReader(socketConexao.getInputStream()));
        */


        ArrayList<Unocard> playerdeck = new ArrayList<Unocard>();
        ArrayList<Unocard> compdeck = new ArrayList<Unocard>();
        int win; // 0 - no result; 1 - win; -1 - loss. 
        String input;
        Unocard topCard; // card on top of the "pile"
        int choiceIndex; // Index of chosen card for both player and computer
        String currentColor; // Mainly used for wild cards

        gameLoop:
        while (true)
        {
            Socket socketConexao = socketRecepcao.accept();
            DataOutputStream paraCliente = new DataOutputStream(socketConexao.getOutputStream());
            BufferedReader doCliente = new BufferedReader(new InputStreamReader(socketConexao.getInputStream()));

            playerdeck.clear();
            compdeck.clear();
            win = 0;
            topCard = new Unocard();
            currentColor = topCard.color;

            /*Mensagem 1 pro cliente*/
            //System.out.println("\nWelcome to Uno! Initialising decks...");
            paraCliente.writeBytes("Welcome to Uno! Initialising decks...\n");
            draw(7, playerdeck);
            draw(7, compdeck);

            /*****************Turns*****************/
            for (boolean playersTurn = true; win == 0; playersTurn ^= true)
            {
                choiceIndex = 0;
                /*Mensagem 2 pro cliente*/
                //System.out.println("\nThe top card is: " + topCard.getFace());
                paraCliente.writeBytes("The top card is: " + topCard.getFace() + "\n");

                if (playersTurn) /*****Player's turn******/
                {
                    // Displaying user's deck
                    /*Mensagem 3 pro cliente*/
                    //System.out.println("Your turn! Your choices:");
                    paraCliente.writeBytes("Your turn! Your choices: \n");
                    paraCliente.writeBytes(String.valueOf(playerdeck.size()) + "\n");
                    for (int i = 0; i < playerdeck.size(); i++)
                    {
                        /*Mensagem 4 pro cliente*/
                        //System.out.print(String.valueOf(i + 1) + ". " +
                        //((Unocard) playerdeck.get(i) ).getFace() + "\n");
                        paraCliente.writeBytes(String.valueOf(i + 1) + ". " +
                                ((Unocard) playerdeck.get(i) ).getFace() + "\n");
                    }
                    /*Mensagem 5 pro cliente*/
                    //System.out.println(String.valueOf(playerdeck.size() + 1 ) + ". " + "Draw card" + "\n" +
                    //String.valueOf(playerdeck.size() + 2) + ". " + "Quit");
                    paraCliente.writeBytes(String.valueOf(playerdeck.size() + 1) + ". " + "Draw card"+ "\n" +
                            String.valueOf(playerdeck.size() + 2) + ". " + "Quit \n");
                    /*try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }*/
                    // Repeats every time the user doesn't input a number
                    /*do
                    {*/
                        /*Mensagem 6 pro cliente*/
                        //System.out.print("\nPlaease input the number of your choice: ");
                        paraCliente.writeBytes("Please input the number of your choice: \n");
                        //input = new Scanner(System.in);
                        input = doCliente.readLine();
                    /*} while (!input.isEmpty() );*/
                    // The choices were incremented to make them seem more natural (i.e not starting with 0)
                    choiceIndex = Integer.parseInt(input.strip()) - 1;

                    // Taking action
                    if (choiceIndex == playerdeck.size() ) {
                        draw(1, playerdeck);
                        paraCliente.writeBytes("susPlace");
                    }
                    else if (choiceIndex == playerdeck.size() + 1) {
                        paraCliente.writeBytes("susPlace");
                        break gameLoop;
                    }
                    else if ( ((Unocard) playerdeck.get(choiceIndex)).canPlace(topCard, currentColor) )
                    {
                        paraCliente.writeBytes("place");
                        topCard = (Unocard) playerdeck.get(choiceIndex);
                        playerdeck.remove(choiceIndex);
                        currentColor = topCard.color;
                        // Producing the action of special cards
                        paraCliente.writeBytes(String.valueOf(topCard.value));
                        if (topCard.value >= 10)
                        {
                            playersTurn = false; // Skipping turn

                            switch (topCard.value)
                            {
                                case 12: // Draw 2
                                    /*Mensagem 7 pro cliente*/
                                    //System.out.println("Drawing 2 cards...");
                                    paraCliente.writeBytes("Drawing 2 cards... \n");
                                    draw(2,compdeck);
                                    break;

                                case 13: case 14: // Wild cards
                                do // Repeats every time the user doesn't input a valid color
                                {
                                    /*Mensagem 8 pro cliente*/
                                    //System.out.print("\nEnter the color you want: ");
                                    paraCliente.writeBytes("Enter the color you want: \n");
                                    //input = new Scanner(System.in);
                                    input = doCliente.readLine();
                                } while (!input.contains("R..|r..|G....|g....|B...|b...|Y.....|y.....") ); //Something I learned recently
                                if (input.contains("R..|r..") )
                                    currentColor = "Red";
                                else if (input.contains("G....|g....") )
                                    currentColor = "Green";
                                else if (input.contains("B...|b...") )
                                    currentColor = "Blue";
                                else if (input.contains("Y.....|y.....") )
                                    currentColor = "Yellow";

                                /*Mensagem 9 pro cliente*/
                                //System.out.println("You chose " + currentColor);
                                paraCliente.writeBytes("You chose " + currentColor + "\n");
                                if (topCard.value == 14) // Wild draw 4
                                {
                                    /*Mensagem 10 pro cliente*/
                                    //System.out.println("Drawing 4 cards...");
                                    paraCliente.writeBytes("Drawing 4 cards... \n");
                                    draw(4,compdeck);
                                }
                                break;
                            }
                        }
                    } else {
                        paraCliente.writeBytes("noPlace");
                        /*Mensagem 11 pro cliente*/
                        //System.out.println("Invalid choice. Turn skipped.");
                        paraCliente.writeBytes("Invalid choice. Turn skipped");
                    }


                } else /************ computer's turn **************/
                {
                    System.out.println("My turn! I have " + String.valueOf(compdeck.size() )
                            + " cards left!" + ((compdeck.size() == 1) ? "...Uno!":"") );
                    // Finding a card to place
                    for (choiceIndex = 0; choiceIndex < compdeck.size(); choiceIndex++)
                    {
                        if ( ((Unocard) compdeck.get(choiceIndex)).canPlace(topCard, currentColor) ) // Searching for playable cards
                            break;
                    }

                    if (choiceIndex == compdeck.size() )
                    {
                        System.out.println("I've got nothing! Drawing cards...");
                        draw(1,compdeck);
                    } else
                    {
                        topCard = (Unocard) compdeck.get(choiceIndex);
                        compdeck.remove(choiceIndex);
                        currentColor = topCard.color;
                        System.out.println("I choose " + topCard.getFace() + " !");

                        // Must do as part of each turn because topCard can stay the same through a round
                        if (topCard.value >= 10)
                        {
                            playersTurn = true; // Skipping turn

                            switch (topCard.value)
                            {
                                case 12: // Draw 2
                                    System.out.println("Drawing 2 cards for you...");
                                    draw(2,playerdeck);
                                    break;

                                case 13: case 14: // Wild cards
                                do // Picking a random color that's not none
                                {
                                    currentColor = new Unocard().color;
                                } while (currentColor == "none");

                                System.out.println("New color is " + currentColor);
                                if (topCard.value == 14) // Wild draw 4
                                {
                                    System.out.println("Drawing 4 cards for you...");
                                    draw(4,playerdeck);
                                }
                                break;
                            }
                        }
                    }

                    // If decks are empty
                    if (playerdeck.size() == 0)
                        win = 1;
                    else if (compdeck.size() == 0)
                        win = -1;
                }

                paraCliente.writeBytes(String.valueOf(win) + "\n");

            } // turns loop end

            /*************Results**************/
            if (win == 1) {
                /*Mensagem 12 pro cliente*/
                paraCliente.writeBytes("You win :) \n");
                //System.out.println("You win :)");
            }
            else if (win == 0) {
                paraCliente.writeBytes("noWins");
            }

            else {
                /*Mensagem 13 pro cliente*/
                paraCliente.writeBytes("loss");
                //System.out.println("You lose :(");
                paraCliente.writeBytes("You lose :( \n");
            }


            /*Mensagem 14 pro cliente*/
            //System.out.print("\nPlay again? ");
            paraCliente.writeBytes("Play again? \n");
            //input = new Scanner(System.in);
            input = doCliente.readLine();

            if (input.toLowerCase().contains("n") )
                break;
        } // game loop end

        /*Mensagem 15 pro cliente*/
        //System.out.println("Bye bye");
        //paraCliente.writeBytes("Bye bye");
    }
    // For drawing cards
    public static void draw(int cards, ArrayList<Unocard> deck)
    {
        for (int i = 0; i < cards; i++)
            deck.add(new Unocard() );
    }
}