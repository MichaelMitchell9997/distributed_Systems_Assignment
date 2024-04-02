import java.net.*;

public class C_mutex extends Thread{
    C_buffer buffer;
   // Socket   s;
    int      port;

    // ip address and port number of the node requesting the token.
    // They will be fetched from the buffer
    String n_host;
    int    n_port;

    public C_mutex (C_buffer b, int p){
        buffer = b;
        port = p;
    }

    public void run(){
        try{
            //  >>>  Listening from the server socket on port 7001
            // from where the TOKEN will be returned later.
            ServerSocket ss_back = new ServerSocket(7001);

            while (true){
                // >>> Print some info on the current buffer content for debugging purposes.
                // >>> please look at the available methods in C_buffer
                if(buffer.size() !=0) {
                    System.out.println("C:mutex   Buffer size is " + buffer.size());
                    buffer.show();
                }

                // if the buffer is not empty
                if (buffer.size() !=0) {
                    // >>>   Getting the first (FIFO) node that is waiting for a TOKEN form the buffer
                    //       Type conversions may be needed.

                    n_host =(String) buffer.get();
                            n_port = Integer.parseInt((String) buffer.get());


                    // >>>  **** Granting the token
                    //
                    try{
                        Socket s = new Socket(n_host,n_port);
                        System.out.println("token granted to the node on port : "+n_port);
                    }
                    catch (java.io.IOException e) {
                        System.out.println(e);
                        System.out.println("CRASH Mutex connecting to the node for granting the TOKEN" + e);
                    }


                    //  >>>  **** Getting the token back
                    try{
                        ss_back.accept();
                    }
                    catch (java.io.IOException e) {
                        System.out.println(e);
                        System.out.println("CRASH Mutex waiting for the TOKEN back" + e);
                    }
                }// endif
            }// endwhile
        }catch (Exception e) {System.out.print(e);}

    }
}
