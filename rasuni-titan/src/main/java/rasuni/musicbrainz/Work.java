package rasuni.musicbrainz;

import javax.xml.bind.annotation.XmlElement;

/**
 * A work
 *
 * @author Ralph Sigrist
 *
 */
public class Work extends Entity
{
	/**
	 * The work title
	 */
	@XmlElement(name = "title")
	private String _title; // NO_UCD (use final)

	/**
	 * The ISWC list
	 */
	@XmlElement(name = "iswc-list")
	public IswcList _iswcList; // NO_UCD (use final)

	/**
	 * Return the title
	 * 
	 * @return the title
	 */
	public String getTitle()
	{
		return _title;
	}

	@Override
	public String toString()
	{
		return _title;
	}
}
