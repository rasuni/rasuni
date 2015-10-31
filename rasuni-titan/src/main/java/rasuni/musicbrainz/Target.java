package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Target
 *
 * @author Ralph Sigrist
 *
 */
public class Target implements IEntity
{
	/**
	 * id
	 */
	@XmlAttribute(name = "id")
	public String _id; // NO_UCD (use final)

	/**
	 * content
	 */
	@XmlValue
	public String _target; // NO_UCD (use final)

	@Override
	public String getName()
	{
		return _target;
	}

	@Override
	public String getId()
	{
		return _id;
	}

	@Override
	public LinkedList<RelationList> getRelationLists()
	{
		return null;
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
