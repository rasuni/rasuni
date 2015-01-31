package rasuni.titan;

import com.thinkaurelius.titan.core.EdgeLabel;
import com.thinkaurelius.titan.core.PropertyKey;
import com.thinkaurelius.titan.core.schema.EdgeLabelMaker;
import com.thinkaurelius.titan.core.schema.SchemaManager;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import com.tinkerpop.blueprints.Direction;
import rasuni.functional.IExpression;

/**
 * Utilities for Titan Graphs
 *
 */
public final class TitanGraphs
{
	/**
	 * Register an edge label
	 *
	 * @param name
	 *            the name
	 * @param uniqueDirection
	 *            the unique direction expression
	 * @param primaryKey
	 *            the label key
	 * @param tg
	 *            the titan graph
	 */
	static void makeEdgeLabel(String name, IExpression<EdgeLabelMaker, EdgeLabelMaker> uniqueDirection, PropertyKey primaryKey, TitanManagement managementSystem)
	{
		EdgeLabelMaker tm = uniqueDirection.apply(makeLabel(managementSystem, name));
		EdgeLabel em = tm.make();
		if (primaryKey != null)
		{
			managementSystem.buildEdgeIndex(em, name + ".index", Direction.OUT, primaryKey);
		}
	}

	private static EdgeLabelMaker makeLabel(SchemaManager tg, String name)
	{
		return tg.makeEdgeLabel(name);
	}

	/**
	 * Register an association
	 *
	 * @param name
	 *            the association name
	 * @param uniqueDirection
	 *            the unique direction expression
	 * @param tg
	 *            the titan graph
	 */
	static void defineAssociation(String name, IExpression<EdgeLabelMaker, EdgeLabelMaker> uniqueDirection, TitanManagement tg)
	{
		makeEdgeLabel(name, uniqueDirection, null, tg);
	}
}
