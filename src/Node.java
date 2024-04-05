import java.net.*;
import java.io.*;
import java.util.*;

public class Node {

    private Random ra;
    private Socket s;
    private PrintWriter pout = null;
    private ServerSocket n_ss;
    private Socket n_token;
    String c_host = "127.0.0.1";
    int c_request_port = 7000;
    int c_return_port = 7001;
    String n_host = "127.0.0.1";
    String n_host_name;
    int n_port;
    boolean n_shutdown;

    private static final String SHUTDOWN_FLAG = "shutdown.flag";

    private void connectToCoordinator() {
        while (true) {
            try {
                // Attempt to connect to the coordinator
                s = new Socket(c_host, c_request_port); // ai used here to debug, realised I was passing n_host and n_host_name
                System.out.println("Successfully connected to the coordinator.");

                break; // Exit the loop if the connection is successful
            } catch (IOException e) {
                int waitTime =5000;
                System.out.println("Failed to connect to coordinator. Retrying in " + waitTime / 1000 + " seconds...");
                try {
                    Thread.sleep(waitTime); // Wait before retrying
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }



    public Node(String nam, int por, int sec, boolean shutdown) throws InterruptedException {
        ra = new Random();
        this.n_host_name = nam;
        this.n_port = por;
        n_shutdown = shutdown;
        connectToCoordinator();


        System.out.println("Node " + n_host_name + ":" + n_port + " of DME is active ....");

        String nodeId = n_host_name + ":" + n_port; // Unique ID for the node


        if(new File(SHUTDOWN_FLAG).exists()){
            new File(SHUTDOWN_FLAG).delete();
        }

        try {
            n_ss = new ServerSocket(n_port);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("here 1");
        }

        while (true) {

            int min = sec;
            int max = 5000;
            int sleepTime = ra.nextInt((max - min) + 1) + min;
            Thread.sleep(sleepTime);

            //formats the sleepTime into seconds and milliseconds purely to make the output more readable.
            int seconds = sleepTime / 1000;
            int milliseconds = sleepTime % 1000;
            String sleepTimeOutput = String.format("%d.%03d", seconds, milliseconds);
            System.out.println("Sleeping for: " + sleepTimeOutput + " Seconds.");


            try {

                //stops the node from starting a new request if the shutdown flag exists
                if(new File(SHUTDOWN_FLAG).exists()){
                    System.out.println("Global node shut down request recieved...");
                    System.out.println("Node " + nodeId + " has shut down gracefully.");
                    System.exit(0);
                }
                // **** Send to the coordinator a token request.
                // send your ip address and port number
                n_token = new Socket(c_host, c_request_port);
                pout = new PrintWriter(n_token.getOutputStream(), true);
                pout.println(n_host);
                pout.println(n_port);
                System.out.println("**- Request Token -**");
                System.out.println(n_host + " is requesting a token on port " + c_request_port);
                n_token.close();

                // **** Then Wait for the token
                // Accept the token and print suitable messages
                n_ss.accept();
                System.out.println("**-Critical Section start for port : " + n_port + " : at [" + new Date().toString() + "]-**"); //prints time the critical section started
                System.out.println("Token received by " + n_host + " on port: " + c_request_port);
                Logger.getInstance("log.txt").log("**-Critical Section start for port : " +n_port+" : at [" + new Date().toString() + "]-**");

                // **** Sleep for a while
                // This simulates the critical session
                Thread.sleep(sleepTime);

                // **** Return the token
                // Create a new socket for returning the token and print suitable messages, also considering communication failures
                s = new Socket(c_host, c_return_port);
                System.out.println("**-Critical Section end for port : " + n_port + " : at [" + new Date().toString() + "]-**"); //prints time the critical section ended
                System.out.println(n_host + " is returning a token on port " + c_return_port);
                Logger.getInstance("log.txt").log("**-Critical Section end for port : " + n_port + " : at [" + new Date().toString() + "]-**");

                // runs if the shutdown flag is received
                if(n_shutdown){
                    try {
                        //prints that a shutdown request has been received and closes all sockets
                        System.out.println("Request to shutdown received....");
                        n_ss.close();
                        s.close();
                        pout.close();
                        //creates the shutdown flag file
                        File shutdownFlag = new File(SHUTDOWN_FLAG);
                        try{
                            if(shutdownFlag.createNewFile()){
                                System.out.println("Shutdown flag created");
                            }else
                                System.out.println("Shutdown flag already created");
                        }catch (IOException e){
                            System.out.println(e);
                        }
                        System.exit(0);
                    }catch (IOException e){
                        System.out.println(e);
                    }
                }

            } catch (IOException e) {
                System.out.println(e);
                connectToCoordinator();
            }
        }
    }


    public static void main(String args[]) throws InterruptedException {
        String n_host_name = "";
        int n_port;


        // port and millisec (average waiting time) are specific of a node
        if ((args.length < 2) || (args.length > 3)) {
            System.out.print("Usage: Node [port number] [millisecs] [y / n]");
            System.exit(1);
        }

        // get the IP address and the port number of the node
        try {
            InetAddress n_inet_address = InetAddress.getLocalHost();
            n_host_name = n_inet_address.getHostName();
            System.out.println("node hostname is " + n_host_name + ":" + n_inet_address);
        } catch (UnknownHostException e) {
            System.out.println(e);
            System.exit(1);
        }

        n_port = Integer.parseInt(args[0]);
        System.out.println("node port is " + n_port);
        Node n = new Node(n_host_name, n_port, Integer.parseInt(args[1]),"y".equalsIgnoreCase(args[2]));

    }


}
