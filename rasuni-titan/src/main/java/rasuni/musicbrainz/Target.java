package rasuni.musicbrainz;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Target
{
	@XmlAttribute(name = "id")
	public String _id; // NO_UCD (use final)

	@XmlValue
	public String _value; // NO_UCD (use final)

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
