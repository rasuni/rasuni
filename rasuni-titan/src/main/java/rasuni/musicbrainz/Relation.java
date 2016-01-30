package rasuni.musicbrainz;

import java.util.LinkedList;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import rasuni.lang.Value;

public class Relation extends Value
{
	@XmlAttribute(name = "type")
	public String _type; // NO_UCD (use final)

	@XmlAttribute(name = "type-id")
	public String _typeId; // NO_UCD (use final)

	@XmlElement(name = "target")
	public Target _target; // NO_UCD (use final)

	@XmlElement(name = "ordering-key")
	public Integer _orderingKey; // NO_UCD (use final)

	@XmlElement(name = "direction")
	public RelationDirection _direction = RelationDirection.FORWARD; // NO_UCD (use final)

	@XmlElementWrapper(name = "attribute-list")
	@XmlElement(name = "attribute")
	public final LinkedList<Attribute> _attributeList = new LinkedList<>();

	@XmlElement(name = "begin")
	public String _begin; // NO_UCD (use final)

	@XmlElement(name = "end")
	public String _end; // NO_UCD (use final)

	@XmlElement(name = "ended")
	public boolean _ended; // NO_UCD (use final)

	@XmlElement(name = "artist")
	public Artist _artist; // NO_UCD (use final)

	/**
	 * the work
	 */
	@XmlElement(name = "work")
	public Work _work; // NO_UCD (use final)

	/**
	 * release
	 */
	@XmlElement(name = "release")
	public Release _release; // NO_UCD (use final)

	/**
	 * release
	 */
	@XmlElement(name = "recording")
	public Recording _recording; // NO_UCD (use final)

	/**
	 * /** The release group
	 */
	@XmlElement(name = "release-group")
	public ReleaseGroup _releaseGroup; // NO_UCD (use final)

	/**
	 * The label
	 */
	@XmlElement(name = "label")
	public Label _label; // NO_UCD (use final)

	/**
	 * The area
	 */
	@XmlElement(name = "area")
	public Area _area; // NO_UCD (use final)

	/**
	 * the place
	 */
	@XmlElement(name = "place")
	public Place _place; // NO_UCD (use final)

	/**
	 * the series
	 */
	@XmlElement(name = "series")
	public Series _series; // NO_UCD (use final)

	/**
	 * the event
	 */
	@XmlElement(name = "event")
	public Event _event; // NO_UCD (use final)
}
