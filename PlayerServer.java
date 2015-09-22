import java.io.*;
import java.io.PrintWriter;
import java.net.*;

/**
 * Created by Jeremy on 9/22/2015.
 */
public class PlayerServer {
    private ServerSocket serverSocket;
    private Socket serv;

    public PlayerServer() throws IOException{
        int port = (int)(Math.random() * 64510 + 1025);
        serverSocket = new ServerSocket(port);
        System.out.println("Server is bound to port " + port);
        serverSocket.setReuseAddress(true);
        serverSocket.setSoTimeout(300000);
    }

    public PlayerServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server is bound to port " + port);
        serverSocket.setReuseAddress(true);
        serverSocket.setSoTimeout(3000000);
    }


    public void run() throws IOException {
        System.out.println("Waiting for client...");
        Socket serv = serverSocket.accept();
        System.out.println("Connected to " + serv.getRemoteSocketAddress());
        PrintWriter out = new PrintWriter(serv.getOutputStream(),true);
        

    }


    public static void main(String args[]) {
        try {
            PlayerServer ps;
            if(args.length>0){
                ps = new PlayerServer(Integer.parseInt(args[0]));
            }
            else {
                ps = new PlayerServer();
            }
            while(true) {
                ps.run();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
