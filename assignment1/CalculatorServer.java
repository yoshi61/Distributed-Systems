import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.Naming;

public class CalculatorServer {

    public static void main(String args[]) {
    	try {
    	    CalculatorImplementation obj = new CalculatorImplementation();
            CalculatorImplementation1 obj1 = new CalculatorImplementation1();
            CalculatorImplementation2 obj2 = new CalculatorImplementation2();
    	    //Calculator stub = (Calculator) UnicastRemoteObject.exportObject(obj, 0);

    	    // Bind the remote object's stub in the registry
    	    //Registry registry = LocateRegistry.getRegistry();
    	    //registry.bind("Calculator", stub);
            Naming.rebind("Calculator", obj);
            Naming.rebind("Calculator1", obj1);
            Naming.rebind("Calculator2", obj2);

    	    System.err.println("Server ready!");
    	} catch (Exception e) {
    	    System.err.println("Server exception: " + e.toString());
    	    e.printStackTrace();
    	}
    }
}
