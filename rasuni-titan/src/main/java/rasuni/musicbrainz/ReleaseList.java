package rasuni.musicbrainz;

import rasuni.functional.IExpression;
import java.util.LinkedList;
import javax.xml.bind.annotation.XmlElement;

/**
 * The release list
 *
 * @author Ralph Sigrist
 *
 */
public class ReleaseList extends EntityList
{
	/**
	 * The release list
	 */
	@XmlElement(name = "release")
	public final LinkedList<Release> _releases = new LinkedList<>();

	/**
	 * the entries getter
	 */
	public static final IExpression<LinkedList<Release>, ReleaseList> ENTRIES = releaseList -> releaseList._releases;
}
