import java.io.*;
import java.net.Socket;

/*todo colocar flush antes e depois de todos os envios pro server
 *  todo preciso tratar a escolha do usuário pra n ficar out of bounds
 *   escrever pro próximo player esperar o computador e outro player resolverem o problema*/

public class Cliente2 {

    public static void main(String[] args) throws Exception {

        BufferedReader doUsuario = new BufferedReader(new InputStreamReader(System.in));
        Socket socketCliente = new Socket("127.0.0.1", 6789);
        DataOutputStream paraServidor = new DataOutputStream(socketCliente.getOutputStream());
        ObjectInputStream doServidorObj = new ObjectInputStream(socketCliente.getInputStream());
        BufferedReader doServidor = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
        String escritaServ;
        int valorCarta;

        System.out.println("Espere pelo seu oponente...");

        while(true) {

            //Primeira mensagem

            StringBuilder mensagemInicial = (StringBuilder) doServidorObj.readObject();
            if (mensagemInicial.toString().equals("Até mais")) {
                System.out.println("Até mais");
                socketCliente.close();
                break;
            }
            else if (mensagemInicial.toString().equals("jogador1")) {
                System.out.println("Jogador 1 vence :)");
                socketCliente.close();
                break;
            }
            else if (mensagemInicial.toString().equals("jogador2")) {
                System.out.println("Jogador 2 vence ;)");
                socketCliente.close();
                break;
            }
            System.out.println(mensagemInicial);

            //Escolha das cartas
            paraServidor.flush();
            do {
                escritaServ = doUsuario.readLine();
            } while (escritaServ.isEmpty());
            paraServidor.flush();
            paraServidor.writeBytes(escritaServ + '\n');
            paraServidor.flush();
            //System.out.println(doServidorObj.readObject());


            //Checande se entra as cartas especiais e precisa colocar a cor
            String acao = doServidor.readLine();
            if (acao.equals("sim")){
                String step = doServidor.readLine();
                //System.out.println(step);
                valorCarta = Integer.parseInt(step);
                if (valorCarta == 13 || valorCarta == 14) {
                    System.out.println(doServidorObj.readObject());
                    paraServidor.flush();
                    escritaServ = doUsuario.readLine() + "\n";
                    paraServidor.writeBytes(escritaServ + '\n');
                    paraServidor.flush();
                    //System.out.println("resp quando escreve cor");
                    System.out.println(doServidorObj.readObject());
                }
            }
            else if (acao.equals("fim")) {
                System.out.println("Até mais");
                socketCliente.close();
                break;
            }

            System.out.println("Espere pelo seu oponente...");
        }
    }

}