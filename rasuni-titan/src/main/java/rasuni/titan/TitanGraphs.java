package rasuni.titan;

import com.thinkaurelius.titan.core.schema.TitanManagement;
import java.util.function.Consumer;

public class TitanGraphs
{
	static void managementSystem(IGraph tg, Consumer<TitanManagement> consumer)
	{
		TitanManagement tm = tg.getManagementSystem();
		try
		{
			tm.set("schema.default", "none");
			consumer.accept(tm);
			tm.commit();
		}
		catch (Throwable t)
		{
			tm.rollback();
			throw t;
		}
	}
}
