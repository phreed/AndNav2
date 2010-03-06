// Created by plusminus on 17:34:39 - 25.01.2009
package org.andnav2.sys.ors.aps;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.views.util.StreamUtils;
import org.andnav2.sys.ors.adt.Error;
import org.andnav2.sys.ors.aps.util.constants.APSConstants;
import org.andnav2.sys.ors.exceptions.ORSException;
import org.xml.sax.SAXException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class APSRequester implements APSConstants {
	// ===========================================================
	// Constants
	// ===========================================================

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

	public static Bitmap request(final List<GeoPoint> pGeoPoints, final int pStartIndex) throws MalformedURLException, IOException, SAXException, ORSException{
		return request(pGeoPoints, APSRequestMethod.RESTFUL, pStartIndex);
	}

	public static Bitmap request(final List<GeoPoint> pGeoPoints, final APSRequestMethod pAPSRm, final int pStartIndex) throws MalformedURLException, IOException, SAXException, ORSException{
		switch(pAPSRm){
			case RESTFUL:
				return requestRestFulDirect(pGeoPoints, pStartIndex);
			case XMLPOST:
				return requestXMLPost(pGeoPoints, pStartIndex);
			default:
				throw new IllegalArgumentException();
		}
	}

	private static Bitmap requestRestFulViaURL(final List<GeoPoint> pGeoPoints, final int pStartIndex) throws ORSException {
		Reader inURL = null;
		try{
			/* Prepare URL. */
			final String urlString = APSRequestComposer.createRestFulRequestURL(APSConstants.URL_APS_RESTFUL_URL, pGeoPoints, pStartIndex);

			/* Read repsonse to StringBuilder. */
			inURL = new BufferedReader(new InputStreamReader(new URL(urlString).openStream()), StreamUtils.IO_BUFFER_SIZE);

			final StringBuilder sb = new StringBuilder();
			final char[] buf = new char[StreamUtils.IO_BUFFER_SIZE];
			int read = 0;
			while((read = inURL.read(buf)) != -1) {
				sb.append(buf, 0, read);
			}

			final String chartURL = sb.toString();

			InputStream chartStream = null;
			try{
				chartStream = new URL(chartURL).openStream();
				return StreamUtils.loadBitmapFromStream(chartStream);
			}finally{
				StreamUtils.closeStream(chartStream);
			}
		} catch (final Exception e) {
			throw new ORSException("Error during RestFul APS-query", e, null);
		} finally {
			StreamUtils.closeStream(inURL);
		}
	}

	private static Bitmap requestRestFulDirect(final List<GeoPoint> pGeoPoints, final int pStartIndex) throws ORSException {
		InputStream in = null;
		try{
			/* Prepare URL. */
			final String urlString = APSRequestComposer.createRestFulRequestURL(APSConstants.URL_APS_RESTFUL_DIRECT, pGeoPoints, pStartIndex);

			/* Read repsonse to StringBuilder. */
			in = new BufferedInputStream(new URL(urlString).openStream(), StreamUtils.IO_BUFFER_SIZE);

			return StreamUtils.loadBitmapFromStreamDetectingPNGMagicNumber(in);
		} catch (final Exception e) {
			throw new ORSException("Error during RestFul APS-query", e, null);
		} finally {
			StreamUtils.closeStream(in);
		}
	}

	private static Bitmap requestXMLPost(final List<GeoPoint> pGeoPoints, final int pStartIndex) throws MalformedURLException, IOException, SAXException, ORSException{
		final URL requestURL = new URL(URL_APS_XMLPOST);

		final HttpURLConnection acon = (HttpURLConnection) requestURL.openConnection();
		acon.setAllowUserInteraction(false);
		acon.setRequestMethod("POST");
		acon.setRequestProperty("Content-Type", "application/xml");
		acon.setDoOutput(true);
		acon.setDoInput(true);
		acon.setUseCaches(false);

		final BufferedWriter xmlOut;
		try{
			xmlOut = new BufferedWriter(new OutputStreamWriter(acon.getOutputStream()));
		}catch(final SocketException se){
			throw new ORSException(new Error(Error.ERRORCODE_UNKNOWN, Error.SEVERITY_ERROR, "org.andnav2.ors.rs.RSRequester.request(...)", "Host unreachable."));
		}catch(final UnknownHostException uhe){
			throw new ORSException(new Error(Error.ERRORCODE_UNKNOWN, Error.SEVERITY_ERROR, "org.andnav2.ors.rs.RSRequester.request(...)", "Host unresolved."));
		}

		final String apsRequest = APSRequestComposer.createXMLPostRequest(pGeoPoints, pStartIndex);
		//		Log.d(DEBUGTAG, routeRequest);
		xmlOut.write(apsRequest);
		xmlOut.flush();
		xmlOut.close();


		InputStream is = null;
		try{
			is = new BufferedInputStream(acon.getInputStream());
			return BitmapFactory.decodeStream(is);
		} catch (final Exception e) {
			throw new ORSException("Error during RestFul APS-query", e, null);
		}finally{
			StreamUtils.closeStream(is);
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
