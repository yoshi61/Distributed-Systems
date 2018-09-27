import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;


public class CalculatorMultiClient2 {

    private CalculatorMultiClient2() {}

    private void debugger(int d, int l, String msg){
        if(d >= l){
            System.out.println(msg);
        }
    }

    public static void main(String[] args) {
        int debug = 0;
        //host is initialised to null (localhost)
    	String host = null;
        //number of threads is initialised to 1
        int numOfThread = 1;
        //get input from command line
        if(args.length == 1){
            numOfThread = Integer.parseInt(args[0]);
        }
        else if(args.length > 1){
            numOfThread = Integer.parseInt(args[0]);
            debug = Integer.parseInt(args[1]);
        }
        else{
            System.out.println("Usage: CalculatorMultiClient2 [number of threads] ([debug level])");
            return;
        }

        //create threads and run them
        for(int i = 1; i <= numOfThread; i++){
            String threadName = "thread_" + Integer.toString(i);
            Client2 temp = new Client2(threadName, host, debug);
            temp.start();
        }
    }
}


class Client2 implements Runnable {

    //for color output
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RESET = "\u001B[0m";
    private Thread t;
    private int debug = 0;
    private String threadName;
    private String host = null;

    Client2( String name, String hostname, int d) {
      threadName = name;
      host = hostname;
      debug = d;
      debugger(debug, 2, "Creating " +  threadName);
    }

    //each thread will run the following code
    public void run() {
        //timestamp for calculating the run time
        long startTime = System.nanoTime();
        debugger(debug, 2, "Running " +  threadName );
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            Calculator2 stub = (Calculator2) registry.lookup("Calculator2");
            String tok = stub.genToken();
            int response = 0;
            //keep all res in here
            ArrayList<Integer> resList = new ArrayList<Integer>();

            //do all the test case in test folder
            for(int i = 1; i <= 50; i++){
                String path = "./test/test_"+i+".txt";
                //set file path
                File file = new File(path);

                //if file not exist
                if (!file.exists()) {
                    System.out.print(path+" not exist!");
                    return;
                }

                //use readLine to read from file
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String data = bufferedReader.readLine();
                //split the data into tokens
                String[] parts = data.split(" ");
                for(int j = 0; j < parts.length; j++){
                    //if is num then pushValue else pushOperator
                    if(Character.isDigit(parts[j].charAt(0))){
                        stub.pushValue(Integer.parseInt(parts[j]), tok);
                    }
                    else{
                        stub.pushOperator(parts[j], tok);
                    }
                }
                //get the response
                response = stub.pop(tok);

                //add response to res List
                resList.add(response);

                debugger(debug, 1, threadName + " response: " + response);

                //release the file source
                fileReader.close();
            }

            //check all responses whith the answers
            for(int s = 1; s <= 50; s++){
                String path = "./ans/ans_"+s+".txt";
                //set file path
                File file = new File(path);

                //if file not exist
                if (!file.exists()) {
                    System.out.print(path+" not exist!");
                    return;
                }

                //use readLine to read from file
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                int data = Integer.parseInt(bufferedReader.readLine());

                if(data == resList.get(s-1)){
                    debugger(debug, 0, "** One shared stack multi-threaded client model ** " + threadName + " test_" + s + " " + ANSI_GREEN + "PASSED!" + ANSI_RESET);
                }
                else{
                    debugger(debug, 0, "** One shared stack multi-threaded client model ** " + threadName + " test_" + s + " " + ANSI_RED + "FAILED!" + ANSI_RESET);
                    debugger(debug, 0, "expect:" + data);
                    debugger(debug, 0, "output:" + resList.get(s-1));
                }
                //release the file source
                fileReader.close();
            }
        }catch (Exception e) {
          System.err.println(threadName + " Client exception: " + e.toString());
          e.printStackTrace();
        }

        //calculate the run time
        long endTime   = System.nanoTime();
        long totalTime = (endTime - startTime) / 1000000;

        //print out the run time
        System.out.println("Thread " +  threadName + " finished excution with run time:"+ ANSI_YELLOW + totalTime + "ms" + ANSI_RESET );

        debugger(debug, 2, "Thread " +  threadName + " exiting.");
    }

    public void start () {
       debugger(debug, 2, "Starting " +  threadName);
       if (t == null) {
           t = new Thread (this, threadName);
           t.start ();
      }
    }

    //print out msg if debug(d) is greater than the level(l)
    private void debugger(int d, int l, String msg){
       if(d >= l){
           System.out.println(msg);
       }
    }
}
