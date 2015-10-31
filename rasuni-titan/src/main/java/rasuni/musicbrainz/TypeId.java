package rasuni.musicbrainz;

import javax.xml.bind.annotation.XmlEnumValue;

/**
 * Releation type
 *
 */
public enum TypeId
{
	/**
	 * Artist - Release - Liner Notes
	 */
	@XmlEnumValue("01323b4f-7aba-410c-8c91-cb224b963a40")
	ARTIST_RELEASE_LINER_NOTES,
	/**
	 * Label - published
	 */
	@XmlEnumValue("05ee6f18-4517-342d-afdf-5897f64276e3")
	LABEL_PUBLISHED, // NO_UCD (unused code)
	/**
	 * Release - misc
	 */
	@XmlEnumValue("0b63af5e-85b2-4891-8234-bddab251399d")
	ARTIST_RELEASE_MISC, // NO_UCD (unused code)
	/**
	 * URL - WORK - Score
	 */
	@XmlEnumValue("0cc8527e-ea40-40dd-b144-3b7588e759bf")
	URL_WORK_SCORE,
	/**
	 * Artist Recording Performer Vocal
	 */
	@XmlEnumValue("0fdbe3c6-7700-4a31-ae54-b53f06ae1cfa")
	ARTIST_RECORDING_VOCAL,
	/**
	 * work - lyricist
	 */
	@XmlEnumValue("3e48faba-ec01-47fd-8e89-30e81161661c")
	ARTIST_WORK_LYRICIST,
	/**
	 * Work - Work - Revision of
	 */
	@XmlEnumValue("4d0d6491-3c41-42c6-883f-d6c7e825b052")
	WORK_REVISION_OF,
	/**
	 * Label recording publisher
	 */
	@XmlEnumValue("51e4a303-8215-4db6-9a9f-ebe95442dbef")
	LABEL_RECORDING_PUBLISHER,
	/**
	 * @XmlEnumValue("542f8484-8bc7-3ce5-a022-747850b2b928")
	 */
	@XmlEnumValue("542f8484-8bc7-3ce5-a022-747850b2b928")
	AREA_EVENT_HELD_EVENTS,
	/**
	 * work work other version
	 */
	@XmlEnumValue("7440b539-19ab-4243-8c03-4f5942ca2218")
	WORK_WORK_OTHER_VERSION,
	/**
	 * artist - work - librettist
	 */
	@XmlEnumValue("7474ab81-486f-40b5-8685-3a4f8ea624cb")
	ARTIST_WORK_LIBRETTIST,
	/**
	 * @XmlEnumValue("936c7c95-3156-3889-a062-8a0cd57f8946")
	 */
	@XmlEnumValue("936c7c95-3156-3889-a062-8a0cd57f8946")
	ARTIST_EVENT_MAIN_PERFORMER_AT,
	/**
	 * Artist - Recording: recorded
	 */
	@XmlEnumValue("a01ee869-80a8-45ef-9447-c59e91aa7926")
	ARTIST_RECORDING_RECORDED, // NO_UCD (unused code)
	/**
	 * the writer
	 */
	@XmlEnumValue("a255bca1-b157-4518-9108-7b147dc3fc68")
	ARTIST_WORK_WRITER,
	/**
	 * work - performance
	 */
	@XmlEnumValue("a3005666-a872-32c3-ad06-98af558e99b0")
	WORK_RECORDING_PERFORMANCE, /**
	 *
	 */
	@XmlEnumValue("a50a1d20-2b20-4d2c-9a29-eb771dd78386")
	RELEASE_GROUP_URL_ALLMUSIC,
	/**
	 * place - recorded-at
	 */
	@XmlEnumValue("ad462279-14b0-4180-9b58-571d0eef7c51")
	PLACE_RECORDED_AT,
	/**
	 *
	 */
	@XmlEnumValue("b6eaef52-68a0-4b50-b875-8acd7d9212ba")
	URL_WORK_VIAF_ID_FOR,
	/**
	 * work - parts
	 */
	@XmlEnumValue("ca8d3642-ce5f-49f8-91f2-125d72524e6a")
	WORK_PARTS,
	/**
	 * work - composer
	 */
	@XmlEnumValue("d59d99ea-23d4-4a80-b066-edca32ee158f")
	ARTIST_WORK_COMPOSER,
	/**
	 *
	 */
	@XmlEnumValue("99e550f3-5ab4-3110-b5b9-fe01d970b126")
	RELEASE_GROUP_URL_DISCOGS,
	/**
	 *
	 */
	@XmlEnumValue("e2c6f697-07dc-38b1-be0b-83d740165532")
	EVENT_PLACE_HELD_IN,
	/**
	 *
	 */
	@XmlEnumValue("6bb1df6b-57f3-434d-8a39-5dc363d2eb78")
	WORK_WORK_IS_BASIS_FOR,
	/**
	 *
	 */
	@XmlEnumValue("04a5b104-a4c2-4bac-99a1-7b837c37d9e4")
	ARTIST_URL_DISCOGS,
	/**
	 *
	 */
	@XmlEnumValue("59054b12-01ac-43ee-a618-285fd397e461")
	ARTIST_RECORDING_INSTRUMENT,
}
