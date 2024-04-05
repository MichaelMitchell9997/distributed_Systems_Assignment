import java.io.IOException;
import java.net.*;

public class C_receiver extends Thread{

    private C_buffer 	 buffer;
    private int 		port;
    private ServerSocket 	s_socket;
    private Socket		socketFromNode;
    private C_Connection_r	connect;

    public C_receiver (C_buffer b, int p){
        buffer = b;
        port = p;
    }

    public void run () {

        // >>> create the socket the server will listen to
        try {
            s_socket = new ServerSocket(port);
            System.out.println("Waiting for connection...");
        } catch (IOException e) {
            System.err.println("Can't create server socket: " + e);
            System.exit(1);
        }

        while (true) {
            try{
                // >>> get a new connection
                socketFromNode = s_socket.accept();
                System.out.println ("C:receiver    Coordinator has received a request ...") ;

                // >>> create a separate thread to service the request, a C_Connection_r thread.
                connect = new C_Connection_r(socketFromNode, buffer);
                connect.start();
            }
            catch (java.io.IOException e) {
                System.out.println("Exception when creating a connection "+e);
            }

        }
    }//end run
}
