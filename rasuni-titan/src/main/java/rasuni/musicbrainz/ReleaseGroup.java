package rasuni.musicbrainz;

import javax.xml.bind.annotation.XmlElement;

/**
 * Release Group List
 *
 * @author Ralph Sigrist
 *
 */
public class ReleaseGroup extends Entity
{
	/**
	 * the title
	 */
	@XmlElement(name = "title")
	private String _title; // NO_UCD (use final)

	/**
	 * Return the title
	 * 
	 * @return the title
	 */
	public String getTitle()
	{
		return _title;
	}

	// empty
	@Override
	public String toString()
	{
		return _title;
	}
	// @Override
	// public void process(IEntityProcessor processor)
	// {
	// processor.write("title", _title);
	// processRelations (processor);
	//
	// }
}
