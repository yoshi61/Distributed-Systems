import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

public class CalculatorClient {
    //for color output
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RESET = "\u001B[0m";

    private CalculatorClient() {}

    //print out msg if debug(d) is greater than the level(l)
    private static void debugger(int d, int l, String msg){
       if(d >= l){
           System.out.println(msg);
       }
    }

    public static void main(String[] args) {
    	String host = null;
        int debug = 0;
        if(args.length>0){
            debug = Integer.parseInt(args[0]);
        }
        //timestamp for calculating the run time
        long startTime = System.nanoTime();
    	try {
    	    Registry registry = LocateRegistry.getRegistry(host);
    	    Calculator stub = (Calculator) registry.lookup("Calculator");

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
                        stub.pushValue(Integer.parseInt(parts[j]));
                        debugger(debug, 2, " push: " + Integer.parseInt(parts[j]));
                    }
                    else{
                        stub.pushOperator(parts[j]);
                        debugger(debug, 2,  " push: " + parts[j]);
                    }
                }
                //get the response
                response = stub.pop();

                //add response to res List
                resList.add(response);

                debugger(debug, 1, " response: " + response);

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
                    debugger(debug, 0, "** One stack One thread ** " + " test_" + s + " " + ANSI_GREEN + "PASSED!" + ANSI_RESET);
                }
                else{
                    debugger(debug, 0, "** One stack One thread ** "+ " test_" + s + " " + ANSI_RED + "FAILED!" + ANSI_RESET);
                    debugger(debug, 0, "expect:" + data);
                    debugger(debug, 0, "output:" + resList.get(s-1));
                }
                //release the file source
                fileReader.close();

            }

    	} catch (Exception e) {
    	    System.err.println("Client exception: " + e.toString());
    	    e.printStackTrace();
    	}

        //calculate the run time
        long endTime   = System.nanoTime();
        long totalTime = (endTime - startTime) / 1000000;
        
        //print out the run time
        System.out.println("Finished excution with run time:"+ ANSI_YELLOW + totalTime + "ms" + ANSI_RESET );

    }
}
