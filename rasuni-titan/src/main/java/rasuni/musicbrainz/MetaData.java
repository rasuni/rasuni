package rasuni.musicbrainz;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import rasuni.lang.Value;

@XmlRootElement(name = "metadata")
public class MetaData extends Value
{
	@XmlAttribute(name = "generator")
	public String _generator; // NO_UCD (use final)

	@XmlAttribute(name = "created")
	public String _created; // NO_UCD (use final)

	@XmlElement(name = "artist")
	public Artist _artist; // NO_UCD (use final)

	/*******/
	@XmlElement(name = "release")
	public Release _release; // NO_UCD (use final)

	@XmlElement(name = "release-group")
	public ReleaseGroup _releaseGroup; // NO_UCD (use final)

	/**
	 * The collection
	 */
	@XmlElement(name = "collection")
	public Collection _collection; // NO_UCD (use final)

	/**
	 * The recording
	 */
	@XmlElement(name = "recording")
	public Recording _recording; // NO_UCD (use final)

	/**
	 * Artist list (optional)
	 */
	@XmlElement(name = "artist-list")
	public ArtistList _artistList; // NO_UCD (use final)

	/**
	 * Release list (optional)
	 */
	@XmlElement(name = "release-list")
	public ReleaseList _releaseList; // NO_UCD (use final)

	/**
	 * Release Group List (optional)
	 */
	@XmlElement(name = "release-group-list")
	public ReleaseGroupList _releaseGroupList; // NO_UCD (use final)

	/**
	 * recording list
	 */
	@XmlElement(name = "recording-list")
	public RecordingList _recordingList; // NO_UCD (use final)

	/**
	 * the label list
	 */
	@XmlElement(name = "label-list")
	public LabelList _labelList; // NO_UCD (use final)

	/**
	 * The place
	 */
	@XmlElement(name = "place")
	public Place _place; // NO_UCD (use final)

	/**
	 * the work list
	 */
	@XmlElement(name = "work-list")
	public WorkList _workList; // NO_UCD (use final)

	/**
	 * the work
	 */
	@XmlElement(name = "work")
	public Work _work; // NO_UCD (use final)

	/**
	 * the url
	 *
	 */
	@XmlElement(name = "url")
	public Url _url; // NO_UCD (use final)

	// @XmlElement(name="disc")
	// private Disc _disc;
	/**
	 * label
	 */
	@XmlElement(name = "label")
	public Label _label; // NO_UCD (use final)

	/**
	 * area
	 */
	@XmlElement(name = "area")
	public Area _area; // NO_UCD (use final)

	/**
	 * the isrc
	 */
	@XmlElement(name = "isrc")
	public ISRC _isrc; // NO_UCD (use final)

	/**
	 * the collection list
	 */
	@XmlElement(name = "collection-list")
	private Object _collectionList; // NO_UCD (use final)

	/**
	 * the disc
	 */
	@XmlElement(name = "disc")
	public Disc _disc; // NO_UCD (use final)

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

	/**
	 * the event
	 */
	@XmlElement(name = "event-list")
	public EventList _eventList; // NO_UCD (use final)
}
