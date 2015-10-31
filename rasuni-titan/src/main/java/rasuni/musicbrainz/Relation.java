package rasuni.musicbrainz;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import rasuni.lang.Value;

/**
 * A relation
 *
 * @author Ralph Sigrist
 *
 */
public class Relation extends Value
{
	/**
	 * Type id
	 */
	@XmlAttribute(name = "type-id")
	public TypeId _typeId; // NO_UCD (use final)

	/**
	 * The target
	 */
	@XmlElement(name = "target")
	public Target _target; // NO_UCD (use final)

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
	 * begin
	 */
	@XmlElement(name = "begin")
	private Integer _begin; // NO_UCD (use final)

	/**
	 * release
	 */
	@XmlElement(name = "recording")
	public Recording _recording; // NO_UCD (use final)

	/**
	 * The direction
	 */
	@XmlElement(name = "direction")
	public RelationDirection _direction = RelationDirection.FORWARD; // NO_UCD (use final)

	/**
	 * The artist
	 */
	@XmlElement(name = "artist")
	public Artist _artist; // NO_UCD (use final)

	/**
	 * The release group
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
