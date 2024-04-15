import java.util.ArrayList;
import java.util.Scanner;

public class Uno2
{
    public static void main(String[] args)
    {
        ArrayList<Unocard> player1deck = new ArrayList<Unocard>();
        ArrayList<Unocard> player2deck = new ArrayList<Unocard>();
        int win; // 0 - no result; 1 - win; -1 - loss. 
        Scanner input;
        Unocard topCard; // card on top of the "pile"
        int choiceIndex; // Index of chosen card for both player and computer
        String currentColor; // Mainly used for wild cards

        gameLoop:
        while (true)
        {
            player1deck.clear();
            player2deck.clear();
            win = 0;
            topCard = new Unocard();
            currentColor = topCard.color;

            System.out.println("\nWelcome to Uno! Initialising decks...");
            draw(7, player1deck);
            draw(7, player2deck);

            /*****************Turns*****************/
            for (boolean playersTurn = true; win == 0; playersTurn ^= true)
            {
                choiceIndex = 0;
                System.out.println("\nThe top card is: " + topCard.getFace());
                if (topCard.value >= 10) System.out.println("\n The current color is: " + currentColor);

                if (playersTurn) /*****Player1's turn******/
                {
                    // Displaying user's deck
                    if (topCard.value >= 10)
                    {
                        switch (topCard.value)
                        {
                            case 12: // Draw 2
                                System.out.println("Drawing 2 cards...");
                                break;

                            case 14: // Wild cards
                                System.out.println("Drawing 4 cards...");
                                break;
                        }
                    }

                    System.out.println("Player 1's turn! Your choices:");
                    for (int i = 0; i < player1deck.size(); i++)
                    {
                        System.out.print(String.valueOf(i + 1) + ". " +
                                ((Unocard) player1deck.get(i) ).getFace() + "\n");
                    }
                    System.out.println(String.valueOf(player1deck.size() + 1 ) + ". " + "Draw card" + "\n" +
                            String.valueOf(player1deck.size() + 2) + ". " + "Quit");
                    // Repeats every time the user doesn't input a number
                    do
                    {
                        System.out.print("\nPlaease input the number of your choice: ");
                        input = new Scanner(System.in);
                    } while (!input.hasNextInt() );
                    // The choices were incremented to make them seem more natural (i.e not starting with 0)
                    choiceIndex = input.nextInt() - 1;

                    // Taking action
                    if (choiceIndex == player1deck.size() )
                        draw(1, player1deck);
                    else if (choiceIndex == player1deck.size() + 1)
                        break gameLoop;
                    else if ( ((Unocard) player1deck.get(choiceIndex)).canPlace(topCard, currentColor) )
                    {
                        topCard = (Unocard) player1deck.get(choiceIndex);
                        player1deck.remove(choiceIndex);
                        currentColor = topCard.color;
                        // Producing the action of special cards                        
                        if (topCard.value >= 10)
                        {
                            playersTurn = false; // Skipping turn

                            switch (topCard.value)
                            {
                                case 12: // Draw 2
                                    //System.out.println("Drawing 2 cards...");
                                    draw(2,player2deck);
                                    break;

                                case 13: case 14: // Wild cards                         
                                do // Repeats every time the user doesn't input a valid color
                                {
                                    System.out.print("\nEnter the color you want: ");
                                    input = new Scanner(System.in);
                                } while (!input.hasNext("R..|r..|G....|g....|B...|b...|Y.....|y.....") ); //Something I learned recently
                                if (input.hasNext("R..|r..") )
                                    currentColor = "Red";
                                else if (input.hasNext("G....|g....") )
                                    currentColor = "Green";
                                else if (input.hasNext("B...|b...") )
                                    currentColor = "Blue";
                                else if (input.hasNext("Y.....|y.....") )
                                    currentColor = "Yellow";

                                System.out.println("You chose " + currentColor);
                                if (topCard.value == 14) // Wild draw 4
                                {
                                    //System.out.println("Drawing 4 cards...");
                                    draw(4,player2deck);
                                }
                                break;
                            }
                        }
                    } else System.out.println("Invalid choice. Turn skipped.");


                } else /************ Player2's turn **************/
                {
                    // Displaying user's deck
                    if (topCard.value >= 10)
                    {
                        switch (topCard.value)
                        {
                            case 12: // Draw 2
                                System.out.println("Drawing 2 cards...");
                                break;

                            case 14: // Wild cards
                                System.out.println("Drawing 4 cards...");
                                break;
                        }
                    }

                    System.out.println("Player 2's turn! Your choices:");
                    for (int i = 0; i < player2deck.size(); i++)
                    {
                        System.out.print(String.valueOf(i + 1) + ". " +
                                ((Unocard) player2deck.get(i) ).getFace() + "\n");
                    }
                    System.out.println(String.valueOf(player2deck.size() + 1 ) + ". " + "Draw card" + "\n" +
                            String.valueOf(player2deck.size() + 2) + ". " + "Quit");
                    // Repeats every time the user doesn't input a number
                    do
                    {
                        System.out.print("\nPlaease input the number of your choice: ");
                        input = new Scanner(System.in);
                    } while (!input.hasNextInt() );
                    // The choices were incremented to make them seem more natural (i.e not starting with 0)
                    choiceIndex = input.nextInt() - 1;

                    // Taking action
                    if (choiceIndex == player2deck.size() )
                        draw(1, player2deck);
                    else if (choiceIndex == player2deck.size() + 1)
                        break gameLoop;
                    else if ( ((Unocard) player2deck.get(choiceIndex)).canPlace(topCard, currentColor) )
                    {
                        topCard = (Unocard) player2deck.get(choiceIndex);
                        player2deck.remove(choiceIndex);
                        currentColor = topCard.color;
                        // Producing the action of special cards
                        if (topCard.value >= 10)
                        {
                            playersTurn = false; // Skipping turn

                            switch (topCard.value)
                            {
                                case 12: // Draw 2
                                    //System.out.println("Drawing 2 cards...");
                                    draw(2,player1deck);
                                    break;

                                case 13: case 14: // Wild cards
                                do // Repeats every time the user doesn't input a valid color
                                {
                                    System.out.print("\nEnter the color you want: ");
                                    input = new Scanner(System.in);
                                } while (!input.hasNext("R..|r..|G....|g....|B...|b...|Y.....|y.....") ); //Something I learned recently
                                if (input.hasNext("R..|r..") )
                                    currentColor = "Red";
                                else if (input.hasNext("G....|g....") )
                                    currentColor = "Green";
                                else if (input.hasNext("B...|b...") )
                                    currentColor = "Blue";
                                else if (input.hasNext("Y.....|y.....") )
                                    currentColor = "Yellow";

                                System.out.println("You chose " + currentColor);
                                if (topCard.value == 14) // Wild draw 4
                                {
                                    //System.out.println("Drawing 4 cards...");
                                    draw(4,player1deck);
                                }
                                break;
                            }
                        }
                    } else System.out.println("Invalid choice. Turn skipped.");

                    // If decks are empty
                    if (player1deck.size() == 0)
                        win = 1;
                    else if (player2deck.size() == 0)
                        win = -1;
                }

            } // turns loop end

            /*************Results**************/
            if (win == 1)
                System.out.println("Player 1 wins :)");
            else
                System.out.println("Player 2 wins ;)");

            System.out.print("\nPlay again? ");
            input = new Scanner(System.in);

            if (input.next().toLowerCase().contains("n") )
                break;
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