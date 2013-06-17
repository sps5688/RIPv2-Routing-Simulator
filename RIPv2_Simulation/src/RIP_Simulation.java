import java.util.ArrayList;
import java.util.List;


/**
 * RIP_Simulation.java
 * 
 * The main driver class for the application. It parses the command line
 * arguments, generates the network graph, and starts the threads for 
 * every node (router).
 * 
 * @author Steven Shaw
 * @date 1/2/12
 *
 */
public class RIP_Simulation {
	public static boolean nodeFailureEnabled = false;
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		int numNodePairs = 0;
		List<Thread> routerThreads = new ArrayList<Thread>();
		
		// Validate command line arguments
		if(args.length < 1 || args.length > 2){
			System.err.print("Usage: java RIP_Simulation [num of pairs of routers (2 routers/pair)] OPTIONAL[enable]");
			System.exit(1);
		}else if(args.length == 1){
			numNodePairs = Integer.parseInt(args[0]);
		}else if(args.length == 2){
			numNodePairs = Integer.parseInt(args[0]);
			
			// Check if nodeFailure mode is enabled
			if(args[1].equals("enable")){
				nodeFailureEnabled = true;
			}else{
				System.err.print("Usage: java RIP_Simulation [num of pairs of routers (2 routers/pair)] OPTIONAL[enable]");
				System.exit(1);
			}
		}
		
		NetworkGraph x = new NetworkGraph(numNodePairs);
		
		System.out.println("Generated Network Topology:");
		System.out.print(x.toString());
		
		// Begin simulation
	    try{
	    	System.out.println("\n\nBeginning Simulation");
	    	
			Thread.sleep(5000);
			
			// Spawn thread's
			for(Node node : NetworkGraph.getNetworkGraph()){
				Thread curRouter = new Thread(node);
				routerThreads.add(curRouter);
			}
			
			// Start threads
			for(Thread curThread : routerThreads){
				curThread.start();
			}
			
			// Join threads
			for(Thread curThread : routerThreads){
				curThread.join();
			}
			
			System.out.println("\nAll routers have failed. Simulation terminated.");
		}catch (InterruptedException e){
			e.printStackTrace();
		}
	}
}