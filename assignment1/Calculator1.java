import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Calculator1 extends Remote {
    void pushValue(int operand, int i) throws RemoteException; //This method will take the value of operand and push it on to the top of the calculator stack.

    void pushOperator(String operator, int i) throws RemoteException; //This method will push a String containing an operator ("+","-","*","/") to the stack, which will cause the server to pop the two operands already on the stack, apply the operation and then push the result back on to the stack.

    int pop(int i) throws RemoteException; //This method will pop the top of the calculator stack and return it to the client.

    boolean isEmpty(int i) throws RemoteException; //This method will return true if the stack is empty, false otherwise.

    int delayPop(int millis, int i) throws RemoteException; //This method will wait millis milliseconds before carrying out the pop operation as above.

    int getIndex() throws RemoteException;

    void freeIndex(int i) throws RemoteException;
}
