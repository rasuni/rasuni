package rasuni.musicbrainz;

import javax.xml.bind.annotation.XmlEnumValue;

/**
 * Releation type
 *
 */
public enum TypeId
{
	/**
	 * Artist - URL - Discogs
	 */
	@XmlEnumValue("04a5b104-a4c2-4bac-99a1-7b837c37d9e4")
	ARTIST_URL_DISCOGS,
	/**
	 * Label - published
	 */
	@XmlEnumValue("05ee6f18-4517-342d-afdf-5897f64276e3")
	LABEL_PUBLISHED, // NO_UCD (unused code)
	/**
	 * Artist - Release - Photography
	 */
	@XmlEnumValue("0b58dc9b-9c49-4b19-bb58-9c06d41c8fbf")
	ARTIST_RELEASE_PHOTOGRAPHY,
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
	ARTIST_RECORDING_VOCAL, // NO_UCD (unused code)
	/**
	 * mixed at
	 */
	@XmlEnumValue("11d74801-1493-4a5d-bc0f-4ddc537acddb")
	PLACE_MIXED_AT,
	/**
	 * artist - release - design/illustration
	 */
	@XmlEnumValue("307e95dd-88b5-419b-8223-b146d4a0d439")
	ARTIST_RELEASE_DESIGN_ILLUSTRATION,
	/**
	 * work - lyricist
	 */
	@XmlEnumValue("3e48faba-ec01-47fd-8e89-30e81161661c")
	ARTIST_WORK_LYRICIST,
	/**
	 * Area event held in
	 */
	@XmlEnumValue("542f8484-8bc7-3ce5-a022-747850b2b928")
	AREA_EVENT_HELDIN,
	/**
	 * Artist - recording - producer
	 */
	@XmlEnumValue("5c0ceac3-feb4-41f0-868d-dc06f6e27fc0")
	ARTIST_RECORDING_PRODUCER,
	/**
	 * Artist member of band
	 */
	@XmlEnumValue("5be4c609-9afa-4ea0-910b-12ffb71e3821")
	ARTIST_MEMBER_OF_BAND, // NO_UCD (unused code)
	/**
	 * Artist - Recording - Engineer
	 */
	@XmlEnumValue("5dcc52af-7064-4051-8d62-7d80f4c3c907")
	ARTIST_RECORDING_ENGINEER, // NO_UCD (unused code)
	/**
	 * Artist - Recording - Instrument
	 */
	@XmlEnumValue("59054b12-01ac-43ee-a618-285fd397e461")
	ARTIST_RECORDING_INSTRUMENT, // NO_UCD (unused code)
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
	WORK_PERFORMANCE,
	/**
	 * place - recorded-at
	 */
	@XmlEnumValue("ad462279-14b0-4180-9b58-571d0eef7c51")
	PLACE_RECORDED_AT,
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
	 * place - place - parts
	 */
	@XmlEnumValue("ff683f48-eff1-40ab-a58f-b128098ffe92")
	PLACE_PLACE_PARTS
}
