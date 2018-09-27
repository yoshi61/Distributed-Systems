import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CalculatorImplementation1 extends UnicastRemoteObject implements Calculator1 {

    ArrayList<ArrayList<Integer>> stack = new ArrayList<ArrayList<Integer>>();

    protected CalculatorImplementation1() throws RemoteException {
        super();
        //initialize 100 stacks with the value "1" means available, "0" means currently using
        for(int i = 0; i < 100; i++)  {
            stack.add(new ArrayList<Integer>());
            stack.get(i).add(1);
        }
    }

    //get an ID number to identify the client
    //the ID number will be the available stack serial number
    public int getIndex(){
        int res = -1;
        synchronized(stack){
            //find an available stack, set its first value to 0(means using), return the stack ID
            for(int i = 0; i < 100; i++){
                if(stack.get(i).get(0) == 1){
                    res = i;
                    stack.get(i).set(0, 0);
                    break;
                }
            }
        }
        return res;
    }

    //make the parameter ID available for other users
    public void freeIndex(int i){
        synchronized(stack){
            //change the ID's stack's first value to 1, means available
            for(int j = 0; j < stack.get(i).size(); j++){
                if(j == 0){
                    stack.get(i).set(0, 1);
                }
                else{
                    stack.get(i).remove(j);
                }
            }
        }
    }



    //This method will take the value of operand and push it on to the top of the calculator stack.
    public void pushValue(int operand, int i) throws RemoteException {
        stack.get(i).add(operand);
    }

    //This method will push a String containing an operator ("+","-","*","/") to the stack,
    // which will cause the server to pop the two operands already on the stack,
    //apply the operation and then push the result back on to the stack.
    public void pushOperator(String operator, int i) throws RemoteException {
        int res;
        int length = stack.get(i).size();
        char op = operator.charAt(0);
        //if the stack is empty, do nothing
        if(!this.isEmpty(i)){
            //if there is only one number in the stack, do the calculation with itself. example: 2 * = 4
            if(length == 2){
                int value = stack.get(i).get(1);
                res = calc(value, value, op);
                stack.get(i).set(1, res);
            }
            else{
                //System.out.println(stack.get(i).get(length - 2));
                //System.out.println(stack.get(i).get(length - 1));
                //get the top two value and do the calculation
                res = calc(stack.get(i).get(length - 2), stack.get(i).get(length - 1), operator.charAt(0));
                //remove the top one and change the second one to the result
                stack.get(i).set(length - 2, res);
                stack.get(i).remove(length - 1);
            }
        }
    }

    //This method will pop the top of the calculator stack and return it to the client.
    public int pop(int i) throws RemoteException {
        int res = 0;
        if(!this.isEmpty(i)){
            int index = stack.get(i).size()-1;
            //copy the top stack frame
            res = stack.get(i).get(index);
            //delete the top stack frame
            stack.get(i).remove(index);
        }
        return res;
    }

    //This method will return true if the stack is empty, false otherwise.
    public boolean isEmpty(int i) throws RemoteException {
        if(stack.get(i).size() == 1){
            return true;
        }
        return false;
    }

    //This method will wait millis milliseconds before carrying out the pop operation as above.
    public int delayPop(int millis, int i) throws RemoteException {
        try{
            Thread.sleep(millis);
        }catch(InterruptedException e){}

        return this.pop(i);
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
}
