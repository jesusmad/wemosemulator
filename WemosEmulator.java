
import java.io.IOException;
import java.net.*;
import java.util.StringTokenizer;

public class Main {

    private static final String URL = "yourdomain.com";
    private static final String LOCAL = "0.0.0.0";
    private static final int port = 1234;
    private static final String MAC = getMAC();

    private static InetAddress ip;
    private static DatagramSocket socket = null;
    private static DatagramPacket sent;
    private static DatagramPacket received;

    private static boolean flag = true;

    private static int[] relays = {0, 0};

    public static void main(String[] args) {

        openConnection();

        while (flag) {

            try {

                System.out.println("******* Enviando paquete al servidor *********" + URL);
                sent = new DatagramPacket(MAC.getBytes(), MAC.getBytes().length, ip , port);
                System.out.println("Enviando "+ MAC.getBytes().length+ " bytes al servidor.");
                System.out.println("Mensaje: "+ MAC);

                socket.send(sent);

                System.out.println("Puerto de envio: " + socket.getLocalPort());

                received = new DatagramPacket(new byte[1024],1024);

                socket.setSoTimeout(5000);
                socket.receive(received);
                String receivedMsg = new String(received.getData());


                configureRelays(receivedMsg.trim());


                System.out.println("******* Paquete recibido *********");
                InetAddress remoteIP = received.getAddress();
                int remotePort = received.getPort();
                System.out.println("Desde: "+remoteIP+ ":"+remotePort);
                System.out.println("Mensaje: "+ receivedMsg.trim());


                showRelayStatus();

                Thread.sleep(10000);
            }

            catch (IOException e) {  }
            catch (InterruptedException e) {  }

        }

        socket.close();
    }


    private static void configureRelays(String receivedMsg) {

        StringTokenizer st = new StringTokenizer(receivedMsg, ",");

        String token;
        int rnum;
        int rstatus;

        while(st.hasMoreTokens()) {
            token = st.nextToken();

            rnum = Character.getNumericValue(token.charAt(0));
            rstatus = Character.getNumericValue(token.charAt(2));

            relays[rnum] = rstatus;
        }

    }


    private static void showRelayStatus() {

        System.out.println("*** Estado de los relés ***");
        String status;

        for (int i = 0; i < relays.length; i++) {
            System.out.println("Relé número: " + i);
            status = (relays[i] == 0) ? "Desactivado (0)" : "Activado (1)";
            System.out.println("Estado: " + status);
        }

    }

    private static void openConnection() {
        try {
            System.out.println("Abriendo conexión...");
            ip = InetAddress.getByName(URL);
            socket = new DatagramSocket();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
            flag = false;
        }
    }

    private static String getMAC() {

        String MAC = "";

        try {
            InetAddress address = InetAddress.getByName(LOCAL2);
            NetworkInterface ni =  NetworkInterface.getByInetAddress(address);
            System.out.println("IP address: " + address.getHostAddress());
            System.out.print("MAC address: ");

            byte[] mac = ni.getHardwareAddress();

            if (mac != null) {

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mac.length; i++) {
                    sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
                }
                System.out.println(sb.toString());
                MAC = sb.toString();

            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return MAC;

    }
}
