package rasuni.lang;

import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * Base class for value objects
 * 
 */
public class Value
{
	@Override
	public boolean equals(Object other)
	{
		return EqualsBuilder.reflectionEquals(this, other);
	}

	@Override
	public int hashCode()
	{
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
