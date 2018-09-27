import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CalculatorImplementation extends UnicastRemoteObject implements Calculator {

    List<Integer> stack = new ArrayList<Integer>();

    protected CalculatorImplementation() throws RemoteException {
        super();
    }

    //This method will take the value of operand and push it on to the top of the calculator stack.
    public void pushValue(int operand) throws RemoteException {
        stack.add(operand);
    }

    //This method will push a String containing an operator ("+","-","*","/") to the stack,
    // which will cause the server to pop the two operands already on the stack,
    //apply the operation and then push the result back on to the stack.
    public void pushOperator(String operator) throws RemoteException {
        int res = 0;
        int length = stack.size();
        if(!this.isEmpty()){
            if(stack.size() == 1){
                int value = stack.get(0);
                res = calc(value, value, operator.charAt(0));
                stack.set(0, res);
            }
            else{
                res = calc(stack.get(length - 2), stack.get(length - 1), operator.charAt(0));
                stack.set(length - 2, res);
                stack.remove(length - 1);
            }
        }
    }

    //This method will pop the top of the calculator stack and return it to the client.
    public int pop() throws RemoteException {
        int res = 0;
        if(!this.isEmpty()){
            int index = stack.size()-1;
            res = stack.get(index);
            stack.remove(index);
        }
        return res;
    }

    //This method will return true if the stack is empty, false otherwise.
    public boolean isEmpty() throws RemoteException {
        if(stack.size() == 0){
            return true;
        }
        return false;
    }

    //This method will wait millis milliseconds before carrying out the pop operation as above.
    public int delayPop(int millis) throws RemoteException {
        try{
            Thread.sleep(millis);
        }catch(InterruptedException e){}

        return this.pop();
    }

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
