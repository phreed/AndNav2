// Created by plusminus on 10:12:09 - 04.02.2009
package org.andnav2.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.andnav2.R;
import org.andnav2.util.constants.TimeConstants;

import android.content.Context;


public class TimeUtils implements TimeConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	public static final SimpleDateFormat UTCSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	static{ // TODO Added 'static'... still working?
		UTCSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	public static final DateFormat mSimpleDateFormat = DateFormat.getDateTimeInstance();

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
	/**
	 * @param pAdditionalMinutes
	 * @return If pAdditionalMinutes is 5 it will return 0:05.
	 */
	public static String getTimeDurationString(final int pAdditionalMinutes) {
		final StringBuilder sb = new StringBuilder();
		if(pAdditionalMinutes < MINUTESPERHOUR * 10){ /* < 10 hours */
			sb.append(pAdditionalMinutes / MINUTESPERHOUR).append(':');
			final int mins = pAdditionalMinutes % MINUTESPERHOUR;
			if(mins < 10) {
				sb.append('0');
			}
			sb.append(mins);
		}else{ /* >= 10 hours */
			sb.append(pAdditionalMinutes / MINUTESPERHOUR).append("h");
		}
		final String timeString = sb.toString();
		return timeString;
	}

	public static final boolean isAMFromNow(final int pAdditionalMinutes){
		final GregorianCalendar g = new GregorianCalendar();
		g.add(Calendar.MINUTE, pAdditionalMinutes);
		return g.get(Calendar.AM_PM) == Calendar.AM;
	}

	/**
	 * @param pAdditionalMinutes
	 * @return If it is 22:00 and the pAdditionalMinutes is 5 it will return 22:05.
	 */
	public static String getTimeString(final int pAdditionalMinutes){
		final StringBuilder sb = new StringBuilder();
		final GregorianCalendar g = new GregorianCalendar();
		g.add(Calendar.MINUTE, pAdditionalMinutes);

		final int hourOfArrival = g.get(Calendar.HOUR_OF_DAY);
		final int minuteOfArrival = g.get(Calendar.MINUTE);

		sb.append(hourOfArrival).append(':');

		if(minuteOfArrival < 10) {
			sb.append('0');
		}
		sb.append(minuteOfArrival);

		return sb.toString();
	}

	public static final String convertTimestampToTimeString(final long aTimestamp) {
		return mSimpleDateFormat.format(new Date(aTimestamp));
	}

	public static final String convertTimestampToUTCString(final long aTimestamp) {
		return UTCSimpleDateFormat.format(new Date(aTimestamp));
	}

	public static final long convertUTCStringToTimestamp(final String aTimeString) throws ParseException {
		return UTCSimpleDateFormat.parse(aTimeString).getTime();
	}

	/**
	 * 
	 * @see http://www.w3schools.com/Schema/schema_dtypes_date.asp
	 * @param aDurationString
	 * @return duration in seconds.
	 */
	public static int durationTimeString(final String aDurationString){
		boolean inTimeSegment = false;

		int out = 0;

		final StringTokenizer st = new StringTokenizer(aDurationString, "PYMDTHS", true);
		final String[] tokens = new String[st.countTokens()];
		int i = 0;
		while(st.hasMoreTokens()) {
			tokens[i++] = st.nextToken();
		}


		for (int j = 0; j < tokens.length; j++) {
			final String cur = tokens[j];

			final char charAtZero = cur.charAt(0);
			switch(charAtZero){
				case 'T':
					inTimeSegment = true;
				case 'P':
				case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9':
					continue;
			}

			/* Now we can be sure that that the previous token is a number. */
			final int val = Integer.parseInt(tokens[j-1]);
			switch(charAtZero){
				case 'Y':
					out += val * SECONDSPERYEAR;
					break;
				case 'M':
					if(inTimeSegment) {
						out += val * SECONDSPERMINUTE;
					} else {
						out += val * SECONDSPERMONTH;
					}
					break;
				case 'D':
					out += val * SECONDSPERDAY;
					break;
				case 'H':
					out += val * SECONDSPERHOUR;
					break;
				case 'S':
					out += val;
					break;
				default:
					throw new IllegalArgumentException("Illegal Token('" + cur + "') at position: " + i + " (Origin: '" + aDurationString + "')");
			}
		}

		return out;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public static class DurationFormatter{
		final String FORMAT = "hh:mm:ss";
		final char MAXVALUE = 'd';

		private final String[] YEAR;
		private final String[] MONTH;
		private final String[] WEEK;
		private final String[] DAY;
		private final String[] HOUR;
		private final String[] MINUTE;
		private final String[] SECOND;

		public DurationFormatter(final Context ctx) {
			this.YEAR = new String[]{ctx.getString(R.string.year), ctx.getString(R.string.years)};
			this.MONTH = new String[]{ctx.getString(R.string.month), ctx.getString(R.string.months)};
			this.WEEK = new String[]{ctx.getString(R.string.week), ctx.getString(R.string.weeks)};
			this.DAY = new String[]{ctx.getString(R.string.day), ctx.getString(R.string.days)};
			this.HOUR = new String[]{ctx.getString(R.string.hour), ctx.getString(R.string.hours)};
			this.MINUTE = new String[]{ctx.getString(R.string.minute), ctx.getString(R.string.minutes)};
			this.SECOND = new String[]{ctx.getString(R.string.second), ctx.getString(R.string.seconds)};
		}

		public String format(final long pSeconds){
			final StringBuilder sb = new StringBuilder();
			long restSeconds = pSeconds;

			final int years;
			final int months;
			final int weeks;
			final int days;
			final int hours;
			final int minutes;
			final int seconds;

			/* Years */
			if(restSeconds >= SECONDSPERYEAR){
				years = (int)(restSeconds / SECONDSPERYEAR);
				restSeconds = restSeconds % SECONDSPERYEAR;
			}else{
				years = 0;
			}

			/* Months */
			if(restSeconds >= SECONDSPERMONTH){
				months = (int)(restSeconds / SECONDSPERMONTH);
				restSeconds = restSeconds % SECONDSPERMONTH;
			}else{
				months = 0;
			}

			/* Weeks */
			if(restSeconds >= SECONDSPERWEEK){
				weeks = (int)(restSeconds / SECONDSPERWEEK);
				restSeconds = restSeconds % SECONDSPERWEEK;
			}else{
				weeks = 0;
			}

			/* Days */
			if(restSeconds >= SECONDSPERDAY){
				days = (int)(restSeconds / SECONDSPERDAY);
				restSeconds = restSeconds % SECONDSPERDAY;
			}else{
				days = 0;
			}

			/* Hours */
			if(restSeconds >= SECONDSPERHOUR){
				hours = (int)(restSeconds / SECONDSPERHOUR);
				restSeconds = restSeconds % SECONDSPERHOUR;
			}else{
				hours = 0;
			}

			/* Minutes */
			if(restSeconds >= SECONDSPERMINUTE){
				minutes = (int)(restSeconds / SECONDSPERMINUTE);
				restSeconds = restSeconds % SECONDSPERMINUTE;
			}else{
				minutes = 0;
			}

			seconds = (int)restSeconds;

			if(years > 0){
				sb.append(years)
				.append(' ')
				.append(getSingularOrPlural(years, this.YEAR));
			}
			if(months > 0){
				sb.append(' ')
				.append(months)
				.append(' ')
				.append(getSingularOrPlural(months, this.MONTH));
			}
			if(weeks > 0){
				sb.append(' ')
				.append(weeks)
				.append(' ')
				.append(getSingularOrPlural(weeks, this.WEEK));
			}
			if(days > 0){
				sb.append(' ')
				.append(days)
				.append(' ')
				.append(getSingularOrPlural(days, this.DAY));
			}
			if(hours > 0){
				sb.append(' ')
				.append(hours)
				.append(' ')
				.append(getSingularOrPlural(hours, this.HOUR));
			}
			if(minutes > 0){
				sb.append(' ')
				.append(minutes)
				.append(' ')
				.append(getSingularOrPlural(minutes, this.MINUTE));
			}
			if(seconds > 0){
				sb.append(' ')
				.append(seconds)
				.append(' ')
				.append(getSingularOrPlural(seconds, this.SECOND));
			}

			return sb.toString().trim();
		}

		private String getSingularOrPlural(final int pYears, final String[] pArr) {
			if(pYears == 1) {
				return pArr[0];
			} else {
				return pArr[1];
			}
		}
	}
}
