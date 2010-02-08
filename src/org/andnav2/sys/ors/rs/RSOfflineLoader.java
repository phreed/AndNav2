package org.andnav2.sys.ors.rs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.andnav2.osm.exceptions.ExternalStorageNotMountedException;
import org.andnav2.osm.util.Util;
import org.andnav2.osm.util.constants.OSMConstants;
import org.andnav2.sys.ors.adt.Error;
import org.andnav2.sys.ors.adt.rs.Route;
import org.andnav2.sys.ors.exceptions.ORSException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.util.Log;

/**
 * Class capable of loading Routes from the SD-Card.
 * @author plusminus
 */
public class RSOfflineLoader implements OSMConstants {
	// ===========================================================
	// Final Fields
	// ===========================================================

	protected static String EXTERNAL_STORAGE_BASEDIRECTORY;

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

	public static Route load(final Context ctx, final String aFileName) throws ExternalStorageNotMountedException, ORSException, IOException{
		try {
			if(!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
				throw new ExternalStorageNotMountedException();
			}else{
				EXTERNAL_STORAGE_BASEDIRECTORY = Util.getAndNavExternalStoragePath();

				// Ensure the routes-directory exists.
				new File(EXTERNAL_STORAGE_BASEDIRECTORY + SDCARD_SAVEDROUTES_PATH).mkdirs();
			}

			final File f = new File(EXTERNAL_STORAGE_BASEDIRECTORY + SDCARD_SAVEDROUTES_PATH + aFileName);
			final InputStream fileIn = new FileInputStream(f);

			/* Get a SAXParser from the SAXPArserFactory. */
			final SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp;
			try {
				sp = spf.newSAXParser();
			} catch (final ParserConfigurationException e) {
				throw new SAXException(e);
			}

			/* Get the XMLReader of the SAXParser we created. */
			final XMLReader xr = sp.getXMLReader();
			/* Create a new ContentHandler and apply it to the XML-Reader*/
			final RSParser openLSParser = new RSParser();
			xr.setContentHandler(openLSParser);

			/* Parse the xml-data from our URL. */
			//			final char[] c = new char[100000];
			//			new InputStreamReader(acon.getInputStream()).read(c, 0, 100000);
			//			String s = new String(c);
			xr.parse(new InputSource(new BufferedInputStream(fileIn)));

			/* The Handler now provides the parsed data to us. */
			return openLSParser.getRoute();
		} catch(final ORSException e){
			throw e;
		} catch (final SAXException e) {
			Log.e(DEBUGTAG, "Error", e);
			throw new ORSException(new Error(Error.ERRORCODE_UNKNOWN, Error.SEVERITY_ERROR, "org.andnav2.ors.rs.RSOfflineLoader.load(...)", "Malformed XML. Was file save in UTF-8 Encoding?"));
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
