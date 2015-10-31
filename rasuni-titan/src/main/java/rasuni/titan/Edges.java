package rasuni.titan;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

/**
 * Edges utilities
 *
 */
public final class Edges
{
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
	 * Get first outgoing edge for specified label
	 *
	 * @param vertex
	 *            the source vertex
	 * @param label
	 *            the label
	 * @return the first outgoing edge
	 */
	static Edge getReference(Vertex vertex, String label)
	{
		return TitanCollector.getReferenced(Vertex::getEdges, vertex, label);
	}

	/**
	 * Remove the edge and return its head vertex
	 *
	 * @param edge
	 *            the edge to remove
	 * @return the head vertex
	 */
	static Vertex remove(Edge edge)
	{
		// retrieve head vertex
		Vertex head = getHead(edge);
		// remove edge
		edge.remove();
		return head;
	}

	/**
	 * Remove a reference from a vertex
	 *
	 * @param vertex
	 *            the vertex
	 * @param label
	 *            the reference label
	 * @return the previously referenced vertex
	 */
	static Vertex removeReference(Vertex vertex, String label)
	{
		Edge edge = getReference(vertex, label);
		return edge == null ? null : remove(edge);
	}
}
