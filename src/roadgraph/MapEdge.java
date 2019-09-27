package roadgraph;

import java.util.HashMap;
import java.util.Map;

public class MapEdge {
	/** Locations of start and end points */
	private MapNode start;
	private MapNode end;
	
	/** The name of the road */
	private String roadName;
	
	/** The type of the road */
	private String roadType;
	
	/** The length of the road segment, in km */
	private double length;
	
	static final double DEFAULT_LENGTH = 0.01;
	static final double PRIMARY_SPEED = 100.0*0.000277778;
	static final double MOTORWAY_LINK_SPEED = 60.0*0.000277778;
	static final double SECONDARY_SPEED = 80.0*0.000277778;
	static final double RESIDENTIAL_SPEED = 40.0*0.000277778;
	static final double UNCLASSIFIED_SPEED = 20.0*0.000277778;
	static final double TRUNK_SPEED = 50.0*0.000277778;

	
	MapEdge(MapNode n1, MapNode n2, String roadName, String roadType, double length) {
		start = n1;
		end = n2;
		this.roadName = roadName;
		this.roadType = roadType;
		this.length = length;
	}
	
	// return the MapNode for the end point
	MapNode getEndNode() {
	   return end;
	}
	double getSpeed() {
		Map <String,Double> speeds = new HashMap<String,Double>();
		speeds.put("primary",PRIMARY_SPEED);
		speeds.put("motorway_link",MOTORWAY_LINK_SPEED);
		speeds.put("secondary",SECONDARY_SPEED);
		speeds.put("residental",RESIDENTIAL_SPEED);
		speeds.put("unclassified",UNCLASSIFIED_SPEED);
		speeds.put("trunk",TRUNK_SPEED);
		if(speeds.containsKey(this.roadType))
			return speeds.get(this.roadType);
		else return 10.0;
	}
	// return the length
	double getLength()
	{
		return length;
	}
	
	// return road name
	public String getRoadName()
	{
		return roadName;
	}
	
	// given one node in an edge, return the other node
	MapNode getOtherNode(MapNode node)
	{
		if (node.equals(start)) 
			return end;
		else if (node.equals(end))
			return start;
		throw new IllegalArgumentException("Looking for " +"a point that is not in the edge");
	}
}