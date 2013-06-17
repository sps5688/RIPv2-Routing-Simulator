import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * RoutingTable.java
 * 
 * This class represents a router's routing table for the network.
 * A routing table consists of multiple destinations. It also contains
 * the subnet mask, next hop address, distance metric, and entry duration
 * for each destination in the table.
 * 
 * @author Steven Shaw
 * @date 1/7/13
 *
 */
public class RoutingTable {
	private Node curNode; // The router the routing table is for
	
	// Data storage for each destination
	private List<String> destinationIP = new ArrayList<String>();
	private List<String> subnetMask = new ArrayList<String>();
	private List<String> nextHopIP = new ArrayList<String>();
	private List<Integer> distanceMetric= new ArrayList<Integer>();
	private List<Node> neighborNodes = new ArrayList<Node>();
	private List<Integer> destinationTimes = new ArrayList<Integer>();
	
	// Final integer to represent the value, infinity
	private final int infinity = Integer.MAX_VALUE;
	
	/**
	 * RoutingTable: Creates a routing table for a node (router)
	 * 
	 * @param node
	 * @param networkGraph
	 */
	public RoutingTable(Node node, Set<Node> networkGraph){
		this.curNode = node;
		Set<Edge> neighborsEdges = node.getEdges();
		
		// Construct neighbor node list
		for(Edge curEdge : neighborsEdges){
			Node x = curEdge.getX();
			Node y = curEdge.getY();
			
			Node neighbor;
			if(curNode.equals(x)){
				neighbor = y;
			}else{
				neighbor = x;
			}
			neighborNodes.add(neighbor);
		}
		
		// Initializes routing table
		for(Node n : networkGraph){
			String curAddress = n.getAddress();
			
			/* If the current node is not a neighbor of this router,
			 * initialize the distance metric to infinity, and the next hop to 0.0.0.0 
			 * 
			 * Else if the current node is a neighbor of this router,
			 * Initialize the distance metric to the weight value and correctly set the
			 * next hop value*/
			if( !curAddress.equals(curNode.getAddress()) && !neighborNodes.contains(n) ){
				destinationIP.add(curAddress);
				subnetMask.add(n.getSubnetMask());
				distanceMetric.add(infinity);
				nextHopIP.add("0.0.0.0");
				destinationTimes.add(0);
			}else if(neighborNodes.contains(n)){
				int weight = 0;
				
				// Determine the edge weight to the neighbor
				for(Edge curEdge : neighborsEdges){
					Node x = curEdge.getX();
					Node y = curEdge.getY();
					
					if(x.equals(n) || y.equals(n)){
						weight = curEdge.getWeight();
					}
				}
				
				destinationIP.add(curAddress);
				subnetMask.add(n.getSubnetMask());
				distanceMetric.add(weight);
				nextHopIP.add(n.getAddress());
				destinationTimes.add(0);
			}
		}
	}
	
	/**
	 * updateMetric: Updates the distance metric value for a router
	 * 
	 * @param address The address to update
	 * @param newHop The new distance metric value
	 */
	public void updateMetric(String address, int newMetric){		
		int index = destinationIP.indexOf(address); 
		distanceMetric.set(index, newMetric);
	}
	
	/**
	 * updateNextHop: Updates the next hop value for a router
	 * 
	 * @param address The address to update
	 * @param newHop The new next hop value
	 */
	public void updateNextHop(String address, String newHop){
		int index = destinationIP.indexOf(address);
		nextHopIP.set(index, newHop);
	}
	
	/**
	 * incrementTimer: Increments the duration timer for a specific router
	 * If the duration is greater than 4, it updates the next hop
	 * and distance metric to infinity.
	 * 
	 * @param address The address to increment
	 */
	public void incrementTimer(String destinationAddress){
		int index = destinationIP.indexOf(destinationAddress);
		
		int duration = destinationTimes.get(index);	
		duration++;
		
		if(duration < 5){
			destinationTimes.set(index, duration);
		}else{
			updateMetric(destinationAddress, infinity);
			updateNextHop(destinationAddress, "0.0.0.0");
			destinationTimes.set(index, duration);
		}
	}
	
	/**
	 * resetTimer: Resets the duration timer for a specific router
	 * 
	 * @param address The address to reset
	 */
	public void resetTimer(String address){
		int index = destinationIP.indexOf(address);
		destinationTimes.set(index, 0);
	}
	
	/**
	 * getDestination: Retrieves a specific destination router address
	 * 
	 * @param targetDestination The target router
	 * @return The router address
	 */
	public String getDestination(String targetDestination){
		for(String curDestination : destinationIP){
			if(curDestination.equals(targetDestination)){
				return curDestination;
			}
		}
		
		return null;
	}
	
	/**
	 * getNextHop: Retrieves the next hop router to a destination
	 * 
	 * @param targetDestination The target router
	 * @return The current next hop router to a target router
	 */
	public String getNextHop(String targetDestination){
		int destinationIndex = destinationIP.indexOf(targetDestination);
		
		if(destinationIndex != -1){
			return nextHopIP.get(destinationIndex);
		}else{
			return null;
		}
	}
	
	/**
	 * getDistanceMetric: Retrieves a distance metric to a router
	 * 
	 * @param targetDestination The target router
	 * @return The current distance metric to a target router
	 */
	public int getDistanceMetric(String targetDestination){
		int destinationIndex = destinationIP.indexOf(targetDestination);
		
		if(destinationIndex != -1){
			return distanceMetric.get(destinationIndex);
		}else{
			return 0;
		}
	}
	
	/**
	 * 
	 * @param targetDestination The IP address of the destination router
	 * @return The current duration value for a specific destination
	 */
	public int getDuration(String targetDestination){
		int destinationIndex = destinationIP.indexOf(targetDestination);
		
		if(destinationIndex != -1){
			return destinationTimes.get(destinationIndex);
		}else{
			return 0;
		}
	}
	
	/**
	 * getDestinations: Retrieves a router's destinations
	 * 
	 * @return All of the destinations for the router
	 */
	public List<String> getDestinations() {
		return destinationIP;
	}
	
	/**
	 * printTable: Displays the router's routing table
	 * 
	 * @param duration the time duration that is currently being executed
	 */
	public synchronized void printTable(int duration){
		synchronized(System.out){
			// Header
			System.out.println("\n<time>" + "\t" + "<node IP>" + "\t" + "<destination IP>"
					+ "\t" + "<destination subnet mask>" + "\t" + "<next hop>" +
					"\t\t\t" + "<metric>" + "\t" + "<Timeout Duration>");
			
			// Prints table
			for(int i = 0; i < destinationIP.size(); i++){
				String currentDestination = destinationIP.get(i);
				
				System.out.print(duration + "\t" + curNode.getAddress() + "\t" + currentDestination +
						"\t\t" + "255.255.255.0" +  "\t\t\t" + getNextHop(destinationIP.get(i)) + "\t\t");
				
				// For formatting purposes
				if(nextHopIP.get(i).equals("0.0.0.0")){
					System.out.print("\t");
				}
				
				// Takes care of initial metric and failed nodes
				if(getDistanceMetric(currentDestination) == infinity){
					System.out.print("\tinfinity" + "\t" + getDuration(currentDestination));
				}else{
					System.out.print("\t" + getDistanceMetric(currentDestination) + "\t\t" + getDuration(currentDestination));
				}
				
				System.out.println();
			}
		}
	}
}