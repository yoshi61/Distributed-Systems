import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Calculator2 extends Remote {
    void pushValue(int operand, String tok) throws RemoteException; //This method will take the value of operand and push it on to the top of the calculator stack.

    //This method will push a String containing an operator ("+","-","*","/") to the stack, which will cause the server to pop the two operands already on the stack, apply the operation and then push the result back on to the stack.
    void pushOperator(String operator, String tok) throws RemoteException; 

    int pop(String tok) throws RemoteException; //This method will pop the top of the calculator stack and return it to the client.

    boolean isEmpty(String tok) throws RemoteException; //This method will return true if the stack is empty, false otherwise.

    int delayPop(int millis, String tok) throws RemoteException; //This method will wait millis milliseconds before carrying out the pop operation as above.

    String genToken() throws RemoteException;
}
