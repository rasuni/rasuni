package rasuni.acoustid;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * AcoustID service response
 *
 */
@XmlRootElement(name = "response")
public final class Response
{
	/**
	 * The tracks
	 */
	@XmlElement(name = "tracks")
	public Tracks _tracks; // NO_UCD (use final)
	/**
	 * the results
	 */
	@XmlElement(name = "results")
	public Results _results; // NO_UCD (use final)
	// /**
	// * Add the acoust ids to the recording
	// *
	// * @param entity
	// * the recording
	// */
	// public void addAcoustIds(Recording entity)
	// {
	// if (_tracks != null)
	// {
	// _tracks.addAcoustIds(entity);
	// }
	// }
	//
	// /**
	// * Process the response
	// * @param processor the processor
	// */
	// public void process(IEntityProcessor processor)
	// {
	// _results.process(processor);
	// }
}
