package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Ralph Sigrist The label list
 */
public class LabelList extends EntityList
{
	/**
	 * A label list
	 */
	@XmlElement(name = "label")
	public LinkedList<Label> _labels = new LinkedList<>(); // NO_UCD (use final)
}
