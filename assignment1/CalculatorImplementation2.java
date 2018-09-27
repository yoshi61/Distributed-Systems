import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CalculatorImplementation2 extends UnicastRemoteObject implements Calculator2 {

    //this is the shared stack
    ArrayList<ArrayList<Integer>> stack = new ArrayList<ArrayList<Integer>>();
    //list of possible token chat
    String[] chars = {"1","2","3","4","5",
                      "6","7","8","9","A",
                      "B","C","D","E","F",
                      "G","H","I","J","K",
                      "L","M","N","O","P",
                      "Q","R","S","T","U",
                      "V","W","X","Y","Z"};
    //this is an ID reffer to a token, because token could be very long
    Integer userIds = 0;
    //this will store all the token id pair
    HashMap<String, Integer> tokenList = new HashMap<String, Integer>();

    //constructor
    protected CalculatorImplementation2() throws RemoteException {
        super();
    }

    //This method will take the value of operand and push it on to the top of the calculator stack.
    public void pushValue(int operand, String tok) throws RemoteException {
        //get id by token
        int id = getIdByTok(tok);

        //generates a stack frame with the id and value
        ArrayList<Integer> frame = genStackFrame(id, operand);
        //add frame to the shared stack
        synchronized(stack){
            stack.add(frame);
        }
    }

    //This method will push a String containing an operator ("+","-","*","/") to the stack,
    // which will cause the server to pop the two operands already on the stack,
    //apply the operation and then push the result back on to the stack.
    public void pushOperator(String operator, String tok) throws RemoteException {
        int res;
        int top1 = 0;
        int top2 = 0;
        int index1 = 0;
        int index2 = 0;
        int count = 0;
        //get id by token
        int id = getIdByTok(tok);
        synchronized(this.stack){
            int length = stack.size();
            for(int i = length-1; i >= 0; i--){
                if(stack.get(i).get(0) == id){
                    count++;
                    if(count == 1){
                        index1 = i;
                        top1 = stack.get(i).get(1);
                    }
                    if(count == 2){
                        index2 = i;
                        top2 = stack.get(i).get(1);
                        break;
                    }
                }
            }
            //calculate the result
            res = calc(top2, top1, operator.charAt(0));
            //set the res to the lower stack frame
            stack.get(index2).set(1, res);
            //remove the top stack frame
            stack.remove(index1);
        }
    }

    //This method will pop the top of the calculator stack and return it to the client.
    public int pop(String tok) throws RemoteException {
        int res;
        int top = 0;
        int index = 0;
        //get id by token
        int id = getIdByTok(tok);
        synchronized(this.stack){
            int length = stack.size();
            for(int i = length-1; i >= 0; i--){
                if(stack.get(i).get(0) == id){
                    index = i;
                    top = stack.get(i).get(1);
                    break;
                }
            }
            res = top;
            //remove the top stack frame
            stack.remove(index);
        }
        return res;
    }

    //This method will return true if the stack is empty, false otherwise.
    public boolean isEmpty(String tok) throws RemoteException {
        //get id by token
        int id = getIdByTok(tok);
        synchronized(this.stack){
            int length = stack.size();
            for(int i = length-1; i >= 0; i--){
                if(stack.get(i).get(0) == id){
                    return false;
                }
            }
        }
        return true;
    }

    //This method will wait millis milliseconds before carrying out the pop operation as above.
    public int delayPop(int millis, String tok) throws RemoteException {
        try{
            Thread.sleep(millis);
        }catch(InterruptedException e){}

        return this.pop(tok);
    }

    //calculate 2 values with the given operator
    private int calc(int a, int b, char s) throws RemoteException {
        int res = 0;
        if(s == '+'){
            res = a+b;
        }
        if(s == '-'){
            res = a-b;
        }
        if(s == '*'){
            res = a*b;
        }
        if(s == '/'){
            res = a/b;
        }
        return res;
    }

    //generates 8 digits token
    public String genToken(){
        String token = "";
        for(int i = 0; i < 8; i++){
            token += getRandom(chars);
        }
        //if the token is already exist, recursively regenerate and return the token
        if(tokenList.get(token) != null){
            return this.genToken();
        }

        //register the token to tokenList
        synchronized(this.userIds){
            tokenList.put(token, this.userIds);
            this.userIds++;
        }
        return token;
    }

    //get id by token
    private int getIdByTok(String tok){
        int id;
        synchronized(tokenList){
            id = tokenList.get(tok);
        }
        return id;
    }

    //generates a stack frame with id
    private ArrayList<Integer> genStackFrame(int id, int value){
        ArrayList<Integer> frame = new ArrayList<Integer>();
        frame.add(id);
        frame.add(value);
        return frame;
    }

    //return a random element in an given array
    private String getRandom(String[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }
}
