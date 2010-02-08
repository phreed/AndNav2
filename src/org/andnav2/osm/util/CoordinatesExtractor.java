package org.andnav2.osm.util;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.andnav2.osm.adt.GeoPoint;

/**
 * 
 * @author Nicolas Gramlich
 *
 */
public class CoordinatesExtractor {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final Extractor[] EXTRACTORS = new Extractor[]{
		new Extractor(
				Pattern.compile("^\\s*\\-?[0-9]+\\.[0-9]+\\s*\\-?[0-9]+\\.[0-9]+\\s*$"),
				new ExtractorMethod(){
					public GeoPoint extract(final String in) {
						final StringTokenizer st = new StringTokenizer(in, " ");

						if(st.countTokens() != 2) {
							throw new IllegalArgumentException();
						}

						final int latE6 = (int)(1E6 * Double.parseDouble(st.nextToken()));
						final int lonE6 = (int)(1E6 * Double.parseDouble(st.nextToken()));
						return new GeoPoint(latE6, lonE6);
					}
				}),
				new Extractor(
						Pattern.compile("^\\s*\\-?[0-9]+\\.[0-9]+\\s*,\\s*\\-?[0-9]+\\.[0-9]+\\s*$"),
						new ExtractorMethod(){
							public GeoPoint extract(final String in) {
								final StringTokenizer st = new StringTokenizer(in, ", ");

								if(st.countTokens() != 2) {
									throw new IllegalArgumentException();
								}

								final int latE6 = (int)(1E6 * Double.parseDouble(st.nextToken()));
								final int lonE6 = (int)(1E6 * Double.parseDouble(st.nextToken()));
								return new GeoPoint(latE6, lonE6);
							}
						}),
						new Extractor(
								Pattern.compile("^\\s*\\-?[0-9]+,[0-9]+\\s*\\-?[0-9]+\\,[0-9]+\\s*$"),
								new ExtractorMethod(){
									public GeoPoint extract(final String in) {
										final StringTokenizer st = new StringTokenizer(in, " ");

										if(st.countTokens() != 2) {
											throw new IllegalArgumentException();
										}

										final int latE6 = (int)(1E6 * Double.parseDouble(st.nextToken().replace(',', '.')));
										final int lonE6 = (int)(1E6 * Double.parseDouble(st.nextToken().replace(',', '.')));
										return new GeoPoint(latE6, lonE6);
									}
								})
	};

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public static GeoPoint match(final String in){
		for (final Extractor e : EXTRACTORS){
			final Matcher m = e.mPattern.matcher(in);
			if(m.find()){
				try{
					return e.mExtractorMethod.extract(m.group());
				}catch(final Throwable t){ /* Nothing */ }
			}
		}

		return null;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	protected static interface ExtractorMethod {
		public GeoPoint extract(final String in) throws IllegalArgumentException;
	}

	protected static class Extractor {
		// ===========================================================
		// Fields
		// ===========================================================

		private final Pattern mPattern;
		private final ExtractorMethod mExtractorMethod;

		// ===========================================================
		// Constructors
		// ===========================================================

		public Extractor(final Pattern pattern, final ExtractorMethod extractorMethod) {
			this.mPattern = pattern;
			this.mExtractorMethod = extractorMethod;
		}
	}
}
