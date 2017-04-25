package rasuni.graph.impl;

import java.util.Iterator;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

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
		return vertex(edge, Direction.IN);
	}

	private static Vertex vertex(Edge edge, Direction direction)
	{
		return edge.vertices(direction).next();
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
		final Vertex head = getHead(edge);
		// remove edge
		edge.remove();
		return head;
	}

	public static Vertex getTail(Edge edge)
	{
		return vertex(edge, Direction.OUT);
	}

	public static Vertex getTailFromNext(Iterator<Edge> edgesIterator, Vertex noNext)
	{
		return edgesIterator.hasNext() ? getTail(edgesIterator.next()) : noNext;
	}
}
