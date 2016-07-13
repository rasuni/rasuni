package rasuni.graph.impl;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

/**
 * Edges utilities
 *
 */
public final class Edges
{
	private Edges()
	{
	}

	/**
	 * Get the head of an edge
	 *
	 * @param edge
	 *            the edge
	 * @return the head vertex
	 */
	public static Vertex getHead(Edge edge)
	{
		return edge.getVertex(Direction.IN);
	}

	/**
	 * Remove the edge and return its head vertex
	 *
	 * @param edge
	 *            the edge to remove
	 * @return the head vertex
	 */
	public static Vertex remove(Edge edge)
	{
		// retrieve head vertex
		Vertex head = getHead(edge);
		// remove edge
		edge.remove();
		return head;
	}

	public static Vertex getTail(Edge edge)
	{
		return edge.getVertex(Direction.OUT);
	}
}
