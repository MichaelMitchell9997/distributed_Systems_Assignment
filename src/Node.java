import java.net.*;
import java.io.*;
import java.util.*;

public class Node{

    private Random ra;
    private Socket	s;
    private PrintWriter pout = null;
    private ServerSocket n_ss;
    private Socket	n_token;
    String	c_host = "127.0.0.1";
    int 	c_request_port = 7000;
    int 	c_return_port = 7001;
    String	n_host = "127.0.0.1";
    String 	n_host_name;
    int     n_port;

    public Node(String nam, int por, int sec) throws InterruptedException {
        ra = new Random();
        n_host_name = nam;
        n_port = por;

        System.out.println(ra.nextInt(4));
        System.out.println("Node " +n_host_name+ ":" +n_port+ " of DME is active ....");

        try {
            n_ss = new ServerSocket(n_port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true){

            int sleepTime = ra.nextInt(4)*1000;
            Thread.sleep(sleepTime);
            System.out.println("Sleeping for "+ sleepTime/1000+" Seconds");

            try {
                // **** Send to the coordinator a token request.
                // send your ip address and port number
                n_token = new Socket(c_host, c_request_port);
                pout = new PrintWriter(n_token.getOutputStream());
                pout.println(n_host);
                pout.println(n_port);
                pout.flush();
                n_token.close();
                System.out.println(n_host + " is requesting a token on port " + c_request_port);

                // **** Then Wait for the token
                // Print suitable messages
                n_ss.accept();
                System.out.println("Token received on port " + c_request_port + " Critical section has begun");
                // **** Sleep for a while
                // This is the critical session
                Thread.sleep(sleepTime);

                // **** Return the token
                // Print suitable messages - also considering communication failures
                s = new Socket(c_host, c_return_port);
                System.out.println(n_host + " is returning a token on port " + c_return_port);

            }
            catch (IOException e) {
                System.out.println(e);
                System.exit(1);
            }
        }
    }

    public static void main (String args[]) throws InterruptedException {
        String n_host_name = "";
        int n_port;

        // port and millisec (average waiting time) are specific of a node
        if ((args.length < 1) || (args.length > 2)){
            System.out.print("Usage: Node [port number] [millisecs]");
            System.exit(1);
        }

        // get the IP address and the port number of the node
        try{
            InetAddress n_inet_address =  InetAddress.getLocalHost() ;
            n_host_name = n_inet_address.getHostName();
            System.out.println ("node hostname is " +n_host_name+":"+n_inet_address);
        }
        catch (java.net.UnknownHostException e){
            System.out.println(e);
            System.exit(1);
        }

        n_port = Integer.parseInt(args[0]);
        System.out.println ("node port is "+n_port);
        Node n = new Node(n_host_name, n_port, Integer.parseInt(args[1]));
    }


}
