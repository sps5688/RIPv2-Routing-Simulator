/**
 * Edge.java
 * 
 * This class represents an between 2 nodes (routers) in the network graph.
 * Each edge contains the 2 nodes that it is connecting as well as
 * the weight of the edge for the connection.
 * 
 * @author Steven Shaw
 * @date 1/2/12
 *
 */
public class Edge {
	private final Node x;
	private final Node y;
	private final int weight;
	
	/**
	 * Creates an edge between x and y with a weight
	 * 
	 * @param x
	 * @param y
	 * @param weight
	 */
	public Edge(Node x, Node y, int weight){
		this.x = x;
		this.y = y;
		this.weight = weight;
	}
	
	/**
	 * getX: Retrieves the x node of the edge
	 * 
	 * @return The x node of the edge
	 */
	public Node getX(){
		return x;
	}

	/**
	 * getY: Retrieves the y node of the edge
	 * 
	 * @return The y node of the edge
	 */
	public Node getY(){
		return y;
	}
	
	/**
	 * getWeight: Retrieves the weight of the edge
	 * 
	 * @return The weight of the edge
	 */
	public int getWeight(){
		return weight;
	}
}