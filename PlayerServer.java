import java.io.*;
import java.net.*;
import java.util.*;


/**
 * Created by jwlehman on 8/11/15.
 * Server class to handle request from the client android app
 */
public class PlayerServer implements Runnable {


    private Socket serv;
    private ServerSocket serverSocket;
    private ArrayList<Socket> sockets;
    private ArrayList<User> allUsers;
    private ArrayList<User> activeUsers;
    File f;

    /*constructor if a particular port is specified
     *initialized users,sockets arraylists
     *creates serversocket
     */

    public PlayerServer(int port) throws IOException {
        sockets = new ArrayList<>();
        allUsers = new ArrayList<>();
        activeUsers = new ArrayList<>();
        serverSocket = new ServerSocket(port);
        System.out.println("Server is bound to port " + port);
        serverSocket.setReuseAddress(true);
        serverSocket.setSoTimeout(30000);
    }


    /*constructor if no port is specified
     *initialized users,sockets arraylists
     *creates serversocket
     */

    public PlayerServer() throws IOException {
        sockets = new ArrayList<>();
        allUsers = new ArrayList<>();
        activeUsers = new ArrayList<>();
        //create random port
        int range = (65534 - 1025) + 1;
        int port = (int) Math.random() * range + 1025;
        serverSocket = new ServerSocket(port);
        System.out.println("Server is bound to port " + port);
        serverSocket.setReuseAddress(true);
        serverSocket.setSoTimeout(300000);
    }

    //getter for local port
    public int getLocalPort() {
        return serverSocket.getLocalPort();
    }

    //TODO implement methods for different request


    /*
    takes command from client and calls appropriate command method
     */
    public String analyzeRequest(String request, String username, String password, String args) throws IOException {
        f = new File("passwords.txt");
        if (request.equals("ADD-USER")) {
            if (addUser(username, password))
                return "User successfully added";
            return "User was not added";

        } else if (request.equals("LOGIN")) {
            if (login(username, password))
                return "User is logged in";
            return "User was not logged in";
        } else
            return "Request not recognized";

    }

    /*add user to server and passwords document

     */
    public boolean addUser(String username, String password) throws IOException {
        User newUser = new User(username, password);
        for (User user : allUsers) {
            if (user.getName().equals(username))
                return false;
        }
        FileOutputStream fos = new FileOutputStream(f);
        PrintWriter pw = new PrintWriter(fos);
        pw.println(username + "-" + password);
        pw.close();
        allUsers.add(newUser);
        return true;
    }

    /*have user login to server

     */
    public boolean login(String username, String password) {
        if (!checkPassword(username, password)) {
            return false;
        }
        User user = findUser(username);
        activeUsers.add(user);
        return true;
    }

    public User findUser(String username) {
        for (User user : allUsers) {
            String check = user.getName();
            if (check.equals(username))
                return user;
        }
        return null;
    }


    //check password match for username
    public boolean checkPassword(String username, String password) {
        User check = null;
        for (User user : allUsers) {
            if (user.getName().equals(username)) {
                check = user;
                break;
            }
        }
        try {
            return check != null && check.getPassword().equals(password);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //initialize password list
    public void initialize() {
        String user;
        String pass;
        String delimited[];
        try {
            Scanner in = new Scanner(f);
            while (in.hasNextLine()) {
                delimited = in.nextLine().split("-");
                user = delimited[1];
                pass = delimited[2];
                addUser(user, pass);

            }
        } catch (FileNotFoundException fne) {
            fne.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    public void run() {
        try {
            System.out.println("Waiting for client...");
            serv = serverSocket.accept();
            sockets.add(serv);
            System.out.println("Connected to " + serv.getRemoteSocketAddress());
            PrintWriter out = new PrintWriter(serv.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(serv.getInputStream()));
            String clientInput = in.readLine();
            System.out.println(clientInput);
            String[] delimited = clientInput.split("-");
            in.close();


            String request = delimited[0];
            String username = delimited[1];
            String password = delimited[2];
            String args = delimited[3];
            String answer = analyzeRequest(request, username, password, args);
            System.out.println(answer);
        } catch (SocketTimeoutException ste) {
            System.out.println("You took too long");
            ste.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            out.println("Usage Error: Need more args");
            System.out.println("Not enough args");
        }
    }

}

    public static void main(String[] args) {
        int portNum;
        try {
            if(args.length>0) {
                portNum = Integer.parseInt(args[1]);
                new Thread(new PlayerServer(portNum)).start();
            }
            else
                new Thread(new PlayerServer().start());
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
