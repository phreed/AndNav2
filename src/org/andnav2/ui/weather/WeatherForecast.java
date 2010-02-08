// Created by plusminus on 15:55:01 - 23.05.2008
package org.andnav2.ui.weather;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.andnav2.R;
import org.andnav2.adt.UnitSystem;
import org.andnav2.preferences.Preferences;
import org.andnav2.ui.common.views.SingleWeatherInfoView;
import org.andnav2.util.constants.Constants;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.api.weather.GoogleWeatherHandler;
import com.google.api.weather.WeatherCurrentCondition;
import com.google.api.weather.WeatherForecastCondition;
import com.google.api.weather.WeatherSet;

public class WeatherForecast extends Activity{
	// ===========================================================
	// Final Fields
	// ===========================================================

	public static final String WEATHERQUERY_GEOPOINTSTRING_ID = "weather_geopoint_id";

	protected static final String GOOGLE_WEATHERBYGPS_BASEURL = "http://www.google.com/ig/api?weather=,,,";

	// ===========================================================
	// Fields
	// ===========================================================

	protected UnitSystem mUnitSystem;

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		this.setTheme(android.R.style.Theme_Dialog);
		setContentView(R.layout.weather_dialog);
		this.mUnitSystem = Preferences.getUnitSystem(this);

		final String query = this.getIntent().getStringExtra(WEATHERQUERY_GEOPOINTSTRING_ID);

		this.findViewById(R.id.weather_ok).setOnClickListener(new OnClickListener(){
			public void onClick(final View arg0) {
				WeatherForecast.this.finish();
			}
		});

		new Thread(new Runnable(){
			public void run() {
				WeatherForecast.this.kickOffWeatherQuery(query);
			}
		}, "WeatherQuery-Thread").start();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	protected void kickOffWeatherQuery(final String mapPointString){
		URL url;
		try {
			/* Get what user typed to the EditText. */
			final String queryString = GOOGLE_WEATHERBYGPS_BASEURL + mapPointString;
			/* Replace blanks with HTML-Equivalent. */
			url = new URL(queryString.replace(" ", "%20"));

			Log.d(Constants.DEBUGTAG, url.toString());

			/* Get a SAXParser from the SAXPArserFactory. */
			final SAXParserFactory spf = SAXParserFactory.newInstance();
			final SAXParser sp = spf.newSAXParser();

			/* Get the XMLReader of the SAXParser we created. */
			final XMLReader xr = sp.getXMLReader();

			/* Create a new ContentHandler and apply it to the XML-Reader. */
			final GoogleWeatherHandler gwh = new GoogleWeatherHandler();
			xr.setContentHandler(gwh);

			/* Parse the xml-data our URL-call returned. */
			xr.parse(new InputSource(url.openStream()));

			/* Our Handler now provides the parsed weather-data to us. */
			if(gwh.isErrorous()){
				Toast.makeText(WeatherForecast.this, "Error", Toast.LENGTH_LONG).show();
				WeatherForecast.this.finish();
				return;
			}

			final WeatherSet ws = gwh.getWeatherSet();

			runOnUiThread(new Runnable(){
				public void run() {
					/* Update the SingleWeatherInfoView with the parsed data. */
					try {
						final WeatherCurrentCondition wcc = ws.getWeatherCurrentCondition();
						if(wcc != null) {
							updateWeatherInfoView(R.id.weather_today, wcc);
						} else {
							((SingleWeatherInfoView)findViewById(R.id.weather_today)).reset();
						}

						WeatherForecastCondition wfc;
						wfc = ws.getWeatherForecastConditions().get(0);
						if(wfc != null) {
							updateWeatherInfoView(R.id.weather_1, wfc);
						} else {
							((SingleWeatherInfoView)findViewById(R.id.weather_1)).reset();
						}

						wfc = ws.getWeatherForecastConditions().get(1);
						if(wfc != null) {
							updateWeatherInfoView(R.id.weather_2, wfc);
						} else {
							((SingleWeatherInfoView)findViewById(R.id.weather_2)).reset();
						}

						wfc = ws.getWeatherForecastConditions().get(2);
						if(wfc != null) {
							updateWeatherInfoView(R.id.weather_3, wfc);
						} else {
							((SingleWeatherInfoView)findViewById(R.id.weather_3)).reset();
						}

						wfc = ws.getWeatherForecastConditions().get(3);
						if(wfc != null) {
							updateWeatherInfoView(R.id.weather_4, wfc);
						} else {
							((SingleWeatherInfoView)findViewById(R.id.weather_4)).reset();
						}


						updateWeatherInfoView(R.id.weather_2, ws.getWeatherForecastConditions().get(1));
						updateWeatherInfoView(R.id.weather_3, ws.getWeatherForecastConditions().get(2));
						updateWeatherInfoView(R.id.weather_4, ws.getWeatherForecastConditions().get(3));
					} catch (final MalformedURLException e) {
						runOnUiThread(new Runnable(){
							public void run() {
								resetWeatherInfoViews();
								Log.e(Constants.DEBUGTAG, "WeatherQueryError", e);
							}
						});
					}
				}
			});

		} catch (final Exception e) {
			runOnUiThread(new Runnable(){
				public void run() {
					resetWeatherInfoViews();
					Toast.makeText(WeatherForecast.this, "Error", Toast.LENGTH_LONG).show();
				}
			});

			Log.e(Constants.DEBUGTAG, "WeatherQueryError", e);
		}
	}

	private void updateWeatherInfoView(final int aResourceID, final WeatherCurrentCondition aWCIS) throws MalformedURLException {
		final SingleWeatherInfoView swiv = (SingleWeatherInfoView) findViewById(aResourceID);
		/* Construct the Image-URL. */
		final URL imgURL = new URL("http://www.google.com" + aWCIS.getIconURL());

		swiv.setRemoteImage(imgURL);

		/* Convert from Celsius to Fahrenheit if necessary. */
		swiv.setTempString(Math.round(this.mUnitSystem.convertTemperatureFromCelsius(aWCIS.getTempCelcius())) + " " + this.mUnitSystem.mAbbrTemperature);
		swiv.setDayString(aWCIS.getDayofWeek());
	}

	private void updateWeatherInfoView(final int aResourceID, final WeatherForecastCondition aWFIS) throws MalformedURLException {
		final SingleWeatherInfoView swiv = (SingleWeatherInfoView) findViewById(aResourceID);
		/* Construct the Image-URL. */
		final URL imgURL = new URL("http://www.google.com" + aWFIS.getIconURL());

		swiv.setRemoteImage(imgURL);

		final int tempMin = Math.round(this.mUnitSystem.convertTemperatureFromCelsius(aWFIS.getTempMinCelsius()));
		final int tempMax = Math.round(this.mUnitSystem.convertTemperatureFromCelsius(aWFIS.getTempMaxCelsius()));
		final String tempString = "" + tempMin + "/" + tempMax + " " + this.mUnitSystem.mAbbrTemperature;

		/* Convert from Celsius to Fahrenheit if necessary. */
		swiv.setTempString(tempString);
		swiv.setDayString(aWFIS.getDayofWeek());
	}

	private void resetWeatherInfoViews() {
		((SingleWeatherInfoView)findViewById(R.id.weather_today)).reset();
		((SingleWeatherInfoView)findViewById(R.id.weather_1)).reset();
		((SingleWeatherInfoView)findViewById(R.id.weather_2)).reset();
		((SingleWeatherInfoView)findViewById(R.id.weather_3)).reset();
		((SingleWeatherInfoView)findViewById(R.id.weather_4)).reset();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
