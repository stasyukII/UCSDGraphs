    
/**
 * @author UCSD MOOC development team and YOU
 * 
 * A class which reprsents a graph of geographic locations
 * Nodes in the graph are intersections between 
 *
 */
package roadgraph;


import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;

import geography.GeographicPoint;
import util.GraphLoader;

/**
 * @author UCSD MOOC development team and YOU
 * 
 * A class which represents a graph of geographic locations
 * Nodes in the graph are intersections between 
 *
 */
public class MapGraph {
	// Add your member variables here in WEEK 3
	// Maintain both nodes and edges as you will need to
	// be able to look up nodes by lat/lon or by streets
	// that contain those nodes.
	HashMap<GeographicPoint, MapNode> pointNodeMap;
	HashSet<MapEdge> edges;

	
	/** 
	 * Create a new empty MapGraph 
	 */
	public MapGraph()
	{
		// Implement in this constructor in WEEK 3
		pointNodeMap = new HashMap<GeographicPoint, MapNode>();
		edges = new HashSet<MapEdge>();
	}
	
	/**
	 * Get the number of vertices (road intersections) in the graph
	 * @return The number of vertices in the graph.
	 */
	public int getNumVertices()
	{
		// Implement this method in WEEK 3
		return pointNodeMap.values().size();
	}
	
	/**
	 * Return the intersections, which are the vertices in this graph.
	 * @return The vertices in this graph as GeographicPoints
	 */
	public Set<GeographicPoint> getVertices()
	{
		// Implement this method in WEEK 3
		return pointNodeMap.keySet();
	}
	
	/**
	 * Get the number of road segments in the graph
	 * @return The number of edges in the graph.
	 */
	public int getNumEdges()
	{
		// Implement this method in WEEK 3
		return edges.size();
	}
	
	/** Add a node corresponding to an intersection at a Geographic Point
	 * If the location is already in the graph or null, this method does 
	 * not change the graph.
	 * @param location  The location of the intersection
	 * @return true if a node was added, false if it was not (the node
	 * was already in the graph, or the parameter is null).
	 */
	public boolean addVertex(GeographicPoint location)
	{
		// Implement this method in WEEK 3
		if (location == null) {
			return false;
		}
		MapNode n = pointNodeMap.get(location);
		if (n == null) {
			n = new MapNode(location);
			pointNodeMap.put(location, n);
			return true;
		}
		else {
			System.out.println("Warning: Node at location " + location + " already exists in the graph.");
			return false;
		}
	}
	
	/**
	 * Adds a directed edge to the graph from pt1 to pt2.  
	 * Precondition: Both GeographicPoints have already been added to the graph
	 * @param from The starting point of the edge
	 * @param to The ending point of the edge
	 * @param roadName The name of the road
	 * @param roadType The type of the road
	 * @param length The length of the road, in km
	 * @throws IllegalArgumentException If the points have not already been
	 *   added as nodes to the graph, if any of the arguments is null,
	 *   or if the length is less than 0.
	 */
	public void addEdge(GeographicPoint from, GeographicPoint to, String roadName,
			String roadType, double length) throws IllegalArgumentException {

		// Implement this method in WEEK 3
		MapNode n1 = pointNodeMap.get(from);
		MapNode n2 = pointNodeMap.get(to);
		
		// check nodes are valid
		if (n1 == null)
			throw new NullPointerException("addEdge: from:" + from + "is not in graph");
		if (n2 == null)
			throw new NullPointerException("addEdge: to:" + to + "is not in graph");

		addEdge(n1, n2, roadName, roadType, length);
	}
	
	// Add an edge when you already know the nodes involved in the edge
	private void addEdge(MapNode n1, MapNode n2, String roadName,
			String roadType,  double length)
	{
		MapEdge edge = new MapEdge(n1, n2, roadName, roadType, length);
		edges.add(edge);
		n1.addEdge(edge);
	}
	

	/** Find the path from start to goal using breadth first search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest (unweighted)
	 *   path from start to goal (including both start and goal).
	 */
	public List<GeographicPoint> bfs(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
        Consumer<GeographicPoint> temp = (x) -> {};
        return bfs(start, goal, temp);
	}
	
	/** Find the path from start to goal using breadth first search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
	 * @return The list of intersections that form the shortest (unweighted)
	 *   path from start to goal (including both start and goal).
	 */
	public List<GeographicPoint> bfs(GeographicPoint start, GeographicPoint goal, Consumer<GeographicPoint> nodeSearched)
	{
		// Implement this method in WEEK 3
		// Setup - check validity of inputs
		if (start == null || goal == null)
			throw new NullPointerException("Cannot find route from or to null node");
		MapNode startNode = pointNodeMap.get(start);
		MapNode endNode = pointNodeMap.get(goal);
		if (startNode == null || endNode == null) {
			System.out.println("Start or goal node is null! No path exists.");
			return null;
		}

		// setup to begin BFS
		HashMap<MapNode, MapNode> parentMap = new HashMap<MapNode, MapNode>();
		boolean found = bfsSearch(startNode, endNode, parentMap, nodeSearched);
		
		if (!found) {
			System.out.println("No path found from " + start + " to " + goal);
			return null;
		}
		
		// Reconstruct the parent path
		List<GeographicPoint> path = constructPath(startNode, endNode, parentMap);

		return path;
	}
	
	private static boolean bfsSearch(MapNode start, MapNode goal, HashMap<MapNode, MapNode> parentMap, Consumer<GeographicPoint> nodeSearched) {
			HashSet<MapNode> visited = new HashSet<MapNode>();
			Queue<MapNode> toExplore = new LinkedList<MapNode>();
			toExplore.add(start);
			boolean found = false;

			while (!toExplore.isEmpty()) {
				MapNode curr = toExplore.remove();
				
				// Hook for visualization.  See writeup.
				nodeSearched.accept(curr.getLocation());
				
				if (curr.equals(goal)) {
					found = true;
					break;
				}
				Set<MapNode> neighbors = curr.getNeighbors();
				for (MapNode next : neighbors) {
					if (!visited.contains(next)) {
						visited.add(next);
						parentMap.put(next, curr);
						toExplore.add(next);
					}
				}
			}
			return found;
	}
	
	/** Reconstruct a path from start to goal using the parentMap
	 *
	 * @param parentMap the HashNode map of children and their parents
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest path from
	 *   start to goal (including both start and goal).
	 */
	private static List<GeographicPoint> constructPath(MapNode start, MapNode goal, HashMap<MapNode, MapNode> parentMap) {
		LinkedList<GeographicPoint> path = new LinkedList<GeographicPoint>();
		MapNode curr = goal;
		
		while (!curr.equals(start)) {
			path.addFirst(curr.getLocation());
			curr = parentMap.get(curr);
		}
		
		// add start
		path.addFirst(start.getLocation());
		return path;
	}

	/** Find the path from start to goal using Dijkstra's algorithm
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */


	public List<GeographicPoint> dijkstra(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
		// You do not need to change this method.
        Consumer<GeographicPoint> temp = (x) -> {};
        return dijkstra(start, goal, temp);
	}
	
	/** Find the path from start to goal using Dijkstra's algorithm
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> dijkstra(GeographicPoint start, 
										  GeographicPoint goal, Consumer<GeographicPoint> nodeSearched)
	{
		// Implement this method in WEEK 4
		if (start == null || goal == null)
			throw new NullPointerException("Cannot find route from or to null node");
		
		MapNode startNode = pointNodeMap.get(start);
		MapNode endNode = pointNodeMap.get(goal);
		if (startNode == null || endNode == null) {
			System.out.println("Start or goal node is null! No path exists.");
			return null;
		}
		
		PriorityQueue<MapNode> toExplore = new PriorityQueue<MapNode>();
		HashSet<MapNode> visited = new HashSet<MapNode>();
		HashMap<MapNode,MapNode> parentMap = new HashMap<MapNode,MapNode>();
		// initialize distance for all nodes
		for (MapNode n : pointNodeMap.values()) {
			//n.setDistance(Double.POSITIVE_INFINITY);
			n.setTime(Double.POSITIVE_INFINITY);
		}	
		
		boolean found = dijkstraAlgorithm(startNode, endNode, toExplore, visited, parentMap, nodeSearched);
		if (!found) {
			System.out.println("No path found from " + start + " to " + goal);
			return null;
		}
		
		// Reconstruct the parent path
		List<GeographicPoint> path = constructPath(startNode, endNode, parentMap);

		return path;
	}
	
	private static boolean dijkstraAlgorithm(MapNode startNode, MapNode endNode, PriorityQueue<MapNode> toExplore, 
		HashSet<MapNode> visited, HashMap<MapNode, MapNode> parentMap, Consumer<GeographicPoint> nodeSearched) {	
		//startNode.setDistance(0);
		startNode.setTime(0);
		toExplore.add(startNode);
		int count = 0; // count visited
		boolean found = false;
		
		while (!toExplore.isEmpty()) {
			MapNode curr = toExplore.remove();
			count++;
			
			// Hook for visualization.  See writeup.
			nodeSearched.accept(curr.getLocation());
			
			System.out.println("DIJKSTRA visiting" + curr);
			if (curr.equals(endNode)) {
				found = true;
				System.out.println("Nodes visited in search: " + count);
				break;
			}
			
			if(!visited.contains(curr)) {
				visited.add(curr);
				Set<MapEdge> edges = curr.getEdges();
				for (MapEdge edge : edges) {
					MapNode neighbor = edge.getEndNode();
					if (!visited.contains(neighbor)) {
						double currDist = edge.getLength() + curr.getDistance();
						double currTime = currDist/edge.getSpeed();
						//if (currDist < neighbor.getDistance()) {
						if (currTime < neighbor.getTime()) {
							parentMap.put(neighbor, curr);
							//neighbor.setDistance(currDist);
							neighbor.setTime(currTime);
							toExplore.add(neighbor);
						}
					}
				}
			}
		}
		return found;
	}

	/** Find the path from start to goal using A-Star search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> aStarSearch(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
        Consumer<GeographicPoint> temp = (x) -> {};
        return aStarSearch(start, goal, temp);
	}
	
	/** Find the path from start to goal using A-Star search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> aStarSearch(GeographicPoint start, 
											 GeographicPoint goal, Consumer<GeographicPoint> nodeSearched)
	{
		// Implement this method in WEEK 4
		// set up
		if (start == null || goal == null)
			throw new NullPointerException("Cannot find route from or to null node");
		MapNode startNode = pointNodeMap.get(start);
		MapNode endNode = pointNodeMap.get(goal);
		if (startNode == null || endNode == null) {
			System.out.println("Start or goal node is null! No path exists.");
			return null;
		}
		
		PriorityQueue<MapNode> toExplore = new PriorityQueue<MapNode>();
		HashSet<MapNode> visited = new HashSet<MapNode>();
		HashMap<MapNode,MapNode> parentMap = new HashMap<MapNode,MapNode>();
		// initialize distance for all nodes
		for (MapNode n : pointNodeMap.values()) {
			n.setDistance(Double.POSITIVE_INFINITY);
			n.setActualDistance(Double.POSITIVE_INFINITY);
		}	
		boolean found = aStarSearchHelper(startNode, endNode, toExplore, visited, parentMap, nodeSearched);
		if (!found) {
			System.out.println("No path found from " + start + " to " + goal);
			return null;
		}
		
		// Reconstruct the parent path
		List<GeographicPoint> path = constructPath(startNode, endNode, parentMap);

		return path;
	}
	
	private static boolean aStarSearchHelper(MapNode startNode, MapNode endNode, PriorityQueue<MapNode> toExplore, 
			HashSet<MapNode> visited, HashMap<MapNode, MapNode> parentMap, Consumer<GeographicPoint> nodeSearched) {
		startNode.setDistance(0);
		startNode.setActualDistance(0);

		toExplore.add(startNode);
		
		int count = 0;
		boolean found = false;
		
		while (!toExplore.isEmpty()) {
			MapNode next = toExplore.remove();
            count++;
            
			// Hook for visualization.  See writeup.
            nodeSearched.accept(next.getLocation());

            // debug
			System.out.println("\nA* visiting" + next+"\nActual = "+next.getActualDistance()+", Pred: "+next.getDistance());
			if (next.equals(endNode)) {
				found = true;
				System.out.println("Nodes visited in search: "+count);
				break;
			}
			if(!visited.contains(next)) {
				visited.add(next);
				Set<MapEdge> edges = next.getEdges();
				for (MapEdge edge : edges) {
					MapNode neighbor = edge.getEndNode();
					if (!visited.contains(neighbor)) {

						double currDist = edge.getLength()+next.getActualDistance();
						// core of A* is just to add to currDist the cost of getting to
						// the destination
						double predDist = currDist+ (neighbor.getLocation()).distance(endNode.getLocation());
						double predTime = predDist;
						if(predTime < neighbor.getDistance()){
							// debug
							// System.out.println("Adding to queue node at: "+neighbor.getLocation());
							// System.out.println("Curr dist: "+currDist+" Pred Distance: " + predDist);
							
							parentMap.put(neighbor, next);
							neighbor.setActualDistance(currDist);
							neighbor.setDistance(predDist);
							toExplore.add(neighbor);
						}
					}
				}
			}
		}
		return found;
	}

	
	
	public static void main(String[] args)
	{
		System.out.print("Making a new map...");
		MapGraph firstMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadRoadMap("data/testdata/simpletest.map", firstMap);
		System.out.println("DONE.");
		
		// You can use this method for testing.  
		
		
		/* Here are some test cases you should try before you attempt 
		 * the Week 3 End of Week Quiz, EVEN IF you score 100% on the 
		 * programming assignment.
		 */
/*
		MapGraph simpleTestMap = new MapGraph();
		GraphLoader.loadRoadMap("data/testdata/simpletest.map", simpleTestMap);
		
		GeographicPoint testStart = new GeographicPoint(1.0, 1.0);
		GeographicPoint testEnd = new GeographicPoint(8.0, -1.0);
		
		System.out.println("Test 1 using simpletest: Dijkstra should be 9 and AStar should be 5");
		List<GeographicPoint> testroute = simpleTestMap.dijkstra(testStart,testEnd);
		List<GeographicPoint> testroute2 = simpleTestMap.aStarSearch(testStart,testEnd);
		
		
		MapGraph testMap = new MapGraph();
		GraphLoader.loadRoadMap("data/maps/utc.map", testMap);
		
		// A very simple test using real data
		testStart = new GeographicPoint(32.869423, -117.220917);
		testEnd = new GeographicPoint(32.869255, -117.216927);
		System.out.println("Test 2 using utc: Dijkstra should be 13 and AStar should be 5");
		testroute = testMap.dijkstra(testStart,testEnd);
		testroute2 = testMap.aStarSearch(testStart,testEnd);
		
		
		// A slightly more complex test using real data
		testStart = new GeographicPoint(32.8674388, -117.2190213);
		testEnd = new GeographicPoint(32.8697828, -117.2244506);
		System.out.println("Test 3 using utc: Dijkstra should be 37 and AStar should be 10");
		testroute = testMap.dijkstra(testStart,testEnd);
		testroute2 = testMap.aStarSearch(testStart,testEnd);

		
		/* Use this code in Week 3 End of Week Quiz */
	    /*MapGraph simpleTestMap = new MapGraph();
			GraphLoader.loadRoadMap("data/testdata/simpletest.map", simpleTestMap);
			
			GeographicPoint testStart = new GeographicPoint(1.0, 1.0);
			GeographicPoint testEnd = new GeographicPoint(8.0, -1.0);
			
			System.out.println("Test 1 using simpletest: Dijkstra should be 9 and AStar should be 5");
			List<GeographicPoint> testroute = simpleTestMap.dijkstra(testStart,testEnd);
			List<GeographicPoint> testroute2 = simpleTestMap.aStarSearch(testStart,testEnd);
			
			
			MapGraph testMap = new MapGraph();
			GraphLoader.loadRoadMap("data/maps/utc.map", testMap);
			
			// A very simple test using real data
			testStart = new GeographicPoint(32.869423, -117.220917);
			testEnd = new GeographicPoint(32.869255, -117.216927);
			System.out.println("Test 2 using utc: Dijkstra should be 13 and AStar should be 5");
			testroute = testMap.dijkstra(testStart,testEnd);
			testroute2 = testMap.aStarSearch(testStart,testEnd);
			
			
			// A slightly more complex test using real data
			testStart = new GeographicPoint(32.8674388, -117.2190213);
			testEnd = new GeographicPoint(32.8697828, -117.2244506);
			System.out.println("Test 3 using utc: Dijkstra should be 37 and AStar should be 10");
			testroute = testMap.dijkstra(testStart,testEnd);
		testroute2 = testMap.aStarSearch(testStart,testEnd);
		*/
		
		MapGraph theMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadRoadMap("data/maps/utc.map", theMap);
		System.out.println("DONE.");

		GeographicPoint start = new GeographicPoint(32.8648772, -117.2254046);
		GeographicPoint end = new GeographicPoint(32.8660691, -117.217393);
		
		
		List<GeographicPoint> route = theMap.dijkstra(start,end);
		List<GeographicPoint> route2 = theMap.aStarSearch(start,end);

		
		
	}
	
}
