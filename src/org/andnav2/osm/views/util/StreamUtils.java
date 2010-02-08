// Created by plusminus on 19:14:08 - 20.10.2008
package org.andnav2.osm.views.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.util.ByteArrayBuffer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class StreamUtils {
	// ===========================================================
	// Constants
	// ===========================================================

	public static final int IO_BUFFER_SIZE = 8 * 1024;
	public static final byte[] PNG_MAGIC_NUMBERS = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10}; // 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A

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

	public static Bitmap loadBitmapFromStreamDetectingPNGMagicNumber(final InputStream in) throws IOException, NoMagicNumberException {
		final ByteArrayBuffer baf = new ByteArrayBuffer(StreamUtils.IO_BUFFER_SIZE);

		final byte[] tmp = new byte[StreamUtils.IO_BUFFER_SIZE];
		int len;
		while((len = in.read(tmp)) != -1) {
			baf.append(tmp, 0, len);
		}

		final byte[] data = baf.toByteArray();
		final Bitmap bmp;
		try { // TODO Experimental, might be inefficient
			final int offset = KMPMatcher.indexOf(data, PNG_MAGIC_NUMBERS);
			if(offset != -1){
				bmp = BitmapFactory.decodeByteArray(data, offset, data.length - offset);
			}else{
				throw new NoMagicNumberException();
			}
		} catch (final OutOfMemoryError oome) {
			throw new IOException("OutOfMemoryError: data.length: " + data.length);
		}
		return bmp;
	}

	public static Bitmap loadBitmapFromStream(final InputStream in) throws IOException {
		final ByteArrayBuffer baf = new ByteArrayBuffer(StreamUtils.IO_BUFFER_SIZE);

		final byte[] tmp = new byte[StreamUtils.IO_BUFFER_SIZE];
		int len;
		while((len = in.read(tmp)) != -1) {
			baf.append(tmp, 0, len);
		}

		final byte[] data = baf.toByteArray();
		final Bitmap bmp;
		try { // TODO Experimental, might be inefficient
			bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
		} catch (final OutOfMemoryError oome) {
			throw new IOException("OutOfMemoryError: data.length: " + data.length);
		}
		return bmp;
	}

	/**
	 * Copy the content of the input stream into the output stream, using a temporary
	 * byte array buffer whose size is defined by {@link #IO_BUFFER_SIZE}.
	 *
	 * @param in The input stream to copy from.
	 * @param out The output stream to copy to.
	 *
	 * @throws IOException If any error occurs during the copy.
	 */
	public static void copy(final InputStream in, final OutputStream out) throws IOException {
		final byte[] b = new byte[IO_BUFFER_SIZE];
		int read;
		while ((read = in.read(b)) != -1) {
			out.write(b, 0, read);
		}
	}

	/**
	 * Closes the specified stream.
	 *
	 * @param stream The stream to close.
	 */
	public static void closeStream(final Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (final IOException e) {
				android.util.Log.e("IO", "Could not close stream", e);
			}
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
