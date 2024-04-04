import java.net.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class Coordinator {



    public static void main (String args[]){
        int port = 7000;

        Coordinator c = new Coordinator ();
        Logger.resetLogFile();

        try {
            InetAddress c_addr = InetAddress.getLocalHost();
            String c_name = c_addr.getHostName();
            System.out.println ("Coordinator address is "+c_addr);
            System.out.println ("Coordinator host name is "+c_name+"\n\n");

        }
        catch (Exception e) {
            System.err.println(e);
            System.err.println("Error in coordinator");
        }

        // allows defining port at launch time
        if (args.length == 1) port = Integer.parseInt(args[0]);


        // Create and run a C_receiver and a C_mutex object sharing a C_buffer object
        C_buffer cb = new C_buffer();
        C_receiver cr = new C_receiver(cb,port);
        C_mutex cm = new C_mutex(cb,port);
        cm.start();
        cr.start();

    }

}
