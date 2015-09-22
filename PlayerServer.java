import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Created by Jeremy on 9/22/2015.
 */
public class PlayerServer {
    private ServerSocket serverSocket;
    private Socket serv;
    private ArrayList<User> allUsers;
    private ArrayList<User> activeUsers;
    private File f;

    public PlayerServer() throws IOException{
        int port = (int)(Math.random() * 64510 + 1025);
        serverSocket = new ServerSocket(port);
        System.out.println("Server is bound to port " + port);
        serverSocket.setReuseAddress(true);
        serverSocket.setSoTimeout(300000);
        allUsers = new ArrayList<>();
        activeUsers = new ArrayList<>();
    }

    public PlayerServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server is bound to port " + port);
        serverSocket.setReuseAddress(true);
        serverSocket.setSoTimeout(3000000);
        allUsers = new ArrayList<>();
        activeUsers = new ArrayList<>();
    }


    public void run() throws IOException {
        System.out.println("Waiting for client...");
        Socket serv = serverSocket.accept();
        System.out.println("Connected to " + serv.getRemoteSocketAddress());
        PrintWriter out = new PrintWriter(serv.getOutputStream(),true);
        Scanner in = new Scanner(serv.getInputStream());
        String request = in.nextLine();
        String delimited[] = request.split("-");
        analyzeRequest(delimited);
    }

    public String analyzeRequest(String[] request) throws IOException {
        String command;
        String user;
        String pass;
        String args;
        if (request.length == 4) {
            command = request[0];
            user = request[1];
            pass = request[2];
            args = request[3];
        } else if (request.length == 3) {
            command = request[0];
            user = request[1];
            pass = request[2];
        }
        else
            return "Usage Error: Not enough args";

        if (request.equals("ADD-USER")) {
            if (addUser(user, pass))
                return "User successfully added";
            return "User was not added";
        }
        else if (request.equals("LOGIN")) {
            if (login(user, pass))
                return "User is logged in";
            return "User was not logged in";
            }
             else
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
