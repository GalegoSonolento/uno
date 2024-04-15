import java.io.*;
import java.net.Socket;

public class ClienteTeste {

    public static void main(String[] args) throws Exception {

        BufferedReader doUsuario = new BufferedReader(new InputStreamReader(System.in));
        ObjectOutputStream output;
        Socket socketCliente = new Socket("127.0.0.1", 6789);
        DataOutputStream paraServidor = new DataOutputStream(socketCliente.getOutputStream());
        BufferedReader doServidor = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));

        /*Servidor precisa dar uma resposta positiva ou negativa pra sair desse while e terminar o jogo dos dois lados
         * dá pra implementar - precisa implementar uma resposta do servidor pra isso*/
        System.out.println(doServidor.readLine());
        while (true) {

            //Carta topo
            System.out.println(doServidor.readLine());
            //Frase meu turno
            System.out.println(doServidor.readLine());
            //Definição da quantidade de opções pro carteado
            int qtdeCartas = Integer.parseInt(doServidor.readLine());
            //Opções pro carteado
            for (int i = 0; i < qtdeCartas; i++){
                System.out.println(doServidor.readLine());
            }
            //Opções de controle (comprar e sair)
            for (int i = 0; i < 2; i++) {
                System.out.println(doServidor.readLine());
            }

            //Linha de escrita de opção
            System.out.println(doServidor.readLine());
            //Escrever a escolha
            String opt = doUsuario.readLine();
            int opcao = Integer.parseInt(opt);
            opt += "\n";
            paraServidor.writeBytes(opt);

            //Recebendo um dos resultados
            String entrada = doServidor.readLine();
            if (entrada.equals("place")){

                int topCard = Integer.parseInt(doServidor.readLine());

                if (topCard >= 10) {
                    switch (topCard) {
                        case 12:
                            System.out.println(doServidor.readLine());
                            break;
                        case 13: case 14:
                            System.out.println(doServidor.readLine());
                            //Escolha de cor
                            paraServidor.writeBytes(doUsuario.readLine());
                            //Mensagem de cor escolhida
                            System.out.println(doServidor.readLine());

                            if (topCard >= 14) {
                                //Mostrar que comprou mais 4
                                System.out.println(doServidor.readLine());
                            }
                            break;
                    }
                }
            }
            else if (entrada.equals("noPlace")) {
                //Mensagem de escolha errada
                System.out.println(doServidor.readLine());
            }


            //Identificação de vitória
            int vict = Integer.parseInt(doServidor.readLine());
            if(vict == 1) {
                System.out.println(doServidor.readLine());

                System.out.println(doServidor.readLine());
                String resp = doUsuario.readLine();
                paraServidor.writeBytes(resp);
                if (resp.toLowerCase().contains("n")) {
                    System.out.println(doServidor.readLine());
                    break;
                }
            }
            else if (vict == -1){
                System.out.println(doServidor.readLine());

                System.out.println(doServidor.readLine());
                String resp = doUsuario.readLine();
                paraServidor.writeBytes(resp);
                if (resp.toLowerCase().contains("n")) {
                    System.out.println(doServidor.readLine());
                    break;
                }
            }


        }



    }

}