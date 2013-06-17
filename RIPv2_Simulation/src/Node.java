import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Node.java
 * 
 * This class represents a node (router) in the network graph.
 * Each node has an IP address, a subnet mask, a type, a
 * set up edges, a value to determine if the node is operable or
 * not, and a routing table.
 * 
 * @author Steven Shaw
 * @date 1/2/12
 *
 */

public class Node implements Runnable{
	// Data storage
	private String ipAddress;
	private final String subnetMask = "255.255.255.0";
	private String type;
	private Set<Edge> edges = new HashSet<Edge>();
	private boolean isOperable = true;
	private RoutingTable table;
	
	// Random number generator
	private Random gen = new Random();
	
	/**
	 * Node: Creates a node with a type and an IP address
	 * 
	 * @param type
	 * @param ipAddress
	 */
	public Node(String type, String ipAddress) {
		this.type = type;
		this.ipAddress = ipAddress;
	}

	/**
	 * getAddress: Retrieves the node's IP address
	 * 
	 * @return the node's IP address
	 */
	public String getAddress(){
		return ipAddress;
	}

	/**
	 * getSubnetMask: Retrieves the node's IP subnet mask
	 * 
	 * @return the node's subnet mask
	 */
	public String getSubnetMask(){
		return subnetMask;
	}
	
	/**
	 * getType: Retrieves the node's type
	 * 
	 * @return the node's type
	 */
	public String getType(){
		return type;
	}

	/**
	 * getEdges: Retrieves the node's edges
	 * 
	 * @return the node's edges
	 */
	public Set<Edge> getEdges(){
		return edges;
	}
	
	/**
	 * getEdges: Adds an edge to this node
	 * 
	 * @param The edge to add
	 */
	public void addEdge(Edge edge){
		edges.add(edge);
	}
	
	/**
	 * isOperable: Retrieves the state of the node
	 * 
	 * @return the the state of the node
	 */
	public boolean isOperable(){
		return isOperable;
	}
	
	/**
	 * receive: Receives a neighbor's routing table and update current
	 * routing table if needed. Also resets the duration timers for the
	 * neighbor who sent the table.
	 * 
	 * @param neighbor The neighbor that is sending the table
	 * @param neighborTable The neighbor's table
	 */
	private void receive(Node neighbor, RoutingTable neighborTable){
		try{
			String neighborAddress = neighbor.getAddress();
			
			// Gets both neighbor and node destinations
			List<String> neighborDestinations = neighborTable.getDestinations();
			List<String> nodeDestinations = table.getDestinations();
			
			/* Compares neighbor's routing table with current routing table
			 and updates when a better value is found */
			for(String neighborDestination : neighborDestinations){
				for(String currentDestination : nodeDestinations){
					if(currentDestination.equals(neighborDestination)){
						int currentMetric = table.getDistanceMetric(currentDestination);
						int neighborMetric = neighborTable.getDistanceMetric(neighborDestination);			
						
						if(neighborMetric < currentMetric){
							int newMetric;
							int curNodeToNeighborDistance = table.getDistanceMetric(neighborAddress);
							
							// Resets current neighbor distance to 0 for correct arithmetic
							if(curNodeToNeighborDistance == Integer.MAX_VALUE){
								newMetric = neighborMetric;
							}else{
								newMetric = neighborMetric + curNodeToNeighborDistance;
							}
							// Updates node's routing table
							table.updateMetric(currentDestination, newMetric);
							table.updateNextHop(currentDestination, table.getDestination(neighborAddress));
					
							// Resets duration timers for destinations
							table.resetTimer(currentDestination);
							table.resetTimer(neighborDestination);
						}
					}
				}
			}
			// Resets duration timer for neighbor
			table.resetTimer(neighborAddress);
		}catch(NullPointerException e){
			// Ignore, table hasn't been initialized
		}	
	}
	
	/**
	 * broadcast: Broadcasts a node's routing table to its neighbors.
	 * Also increments the duration timer for each neighbor who is 
	 * being sent the table.
	 * 
	 * @param nodeTable The node's routing table to broadcast
	 */
	private void broadcast(RoutingTable nodeTable){
		// Broadcast routing table to neighbors
		Set<Edge> edges = getEdges();
		
		/* Determine which node of an edge is the neighbor and
		   send the node's routing table to that neighbor */
		for(Edge edge : edges){
			Node x = edge.getX();
			Node y = edge.getY();
			
			Node neighbor;
			if(x.getAddress().equals(ipAddress)){
				neighbor = y;
			}else{
				neighbor = x;
			}

			neighbor.receive(this, nodeTable);
					
			// Increments timer for neighbor
			nodeTable.incrementTimer(neighbor.getAddress());
		}
	}
	
	/**
	 * tryToFail: Changes the state of the node to
	 * inoperable 20% of the time
	 */
	private void tryToFail(){
		// Determine if Node fails or not
		if(gen.nextInt(5) <= 1){
			isOperable = false;
		}
	}
	
	/**
	 * run: Continuously prints out the router's routing table
	 * each second and attempts to fail. If it fails, the thread
	 * terminates.
	 */
	@Override
	public void run() {
		Set<Node> network = NetworkGraph.getNetworkGraph();
		int currentIteration = 0;
		
		// Generate's initial routing table
		table = new RoutingTable(this, network);
		
		while(true){
			try {
				/* If node has not failed, print table and 
				   broadcast to neighbors */
				if(isOperable()){
					table.printTable(currentIteration);
					broadcast(table);
				}else{
					synchronized (System.out){
						System.out.println("\n" + ipAddress + " has failed!");	
						break;
					}
				}
				
				// Determine if the node has failed or not
				if(RIP_Simulation.nodeFailureEnabled){
					tryToFail();
				}
				
				Thread.sleep(1000);
				currentIteration++;				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
		}	
	}
}