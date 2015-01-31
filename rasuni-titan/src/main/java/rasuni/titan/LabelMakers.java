package rasuni.titan;

import com.thinkaurelius.titan.core.Multiplicity;
import com.thinkaurelius.titan.core.schema.EdgeLabelMaker;
import rasuni.functional.IExpression;

/**
 * LabelMaker utilities
 *
 */
class LabelMakers
{
	/**
	 * The many to one expression
	 */
	public static final IExpression<EdgeLabelMaker, EdgeLabelMaker> MANY_TO_ONE = lm -> lm.multiplicity(Multiplicity.MANY2ONE);

	/**
	 * one to one expression
	 */
	public static final IExpression<EdgeLabelMaker, EdgeLabelMaker> ONE_TO_ONE = lm -> lm.multiplicity(Multiplicity.ONE2ONE);
}
