// Created by plusminus on 18:23:16 - 25.09.2008
package org.andnav2.osm.views.tiles;

import java.util.ArrayList;

import org.andnav2.osm.adt.BoundingBoxE6;
import org.andnav2.osm.adt.GeoPoint;
import org.andnav2.osm.views.tiles.adt.OSMTileInfo;
import org.andnav2.osm.views.tiles.caching.OSMMapTileFilesystemCache.StoragePolicy;

/**
 * 
 * @author Nicolas Gramlich
 * 
 */
public enum OSMMapTileProviderInfo {
	MAPNIK("http://tile.openstreetmap.org/",
			"Mapnik",
			"Default",
			".png",
			0,
			18,
			null,
			256,
			18000,
			OSMMapTileProviderType.DOWNLOAD_PROVIDER,
			true,
	"mapnik"),
	OSMARENDER("http://tah.openstreetmap.org/Tiles/tile/",
			"OsmaRender",
			"More Details",
			".png",
			0,
			17,
			null,
			256,
			18000,
			OSMMapTileProviderType.DOWNLOAD_PROVIDER,
			true,
	"osmarender"),
	CYCLEMAP("http://b.andy.sandbox.cloudmade.com/tiles/cycle/",
			"Cycle Map",
			"Emphasizes Cycle-Paths",
			".png",
			0,
			17,
			null,
			256,
			18000,
			OSMMapTileProviderType.DOWNLOAD_PROVIDER,
			true,
	"cyclemap"),
	OPENAERIELMAP("http://tile.openaerialmap.org/tiles/1.0.0/openaerialmap-900913/",
			"OpenAerialMap",
			"Satellite-Imagery",
			".jpg",
			0,
			13,
			null,
			256,
			18000,
			OSMMapTileProviderType.DOWNLOAD_PROVIDER,
			true,
	"openaerialmap"),
	CLOUDMADESTANDARDTILES("http://tile.cloudmade.com/7ded028e030c5929b28bf823486ce84f/1/256/",
			"Cloudmade",
			"Like Mapnik, stronger colors",
			".png",
			0,
			18,
			null,
			256,
			18000,
			OSMMapTileProviderType.DOWNLOAD_PROVIDER,
			true,
	"cloudmade"),
	OPENPISTEMAP("http://openpistemap.org/tiles/contours/",
			"OpenPisteMap",
			"Emphasizes ski-pists",
			".png",
			0,
			17,
			null,
			256,
			18000,
			OSMMapTileProviderType.DOWNLOAD_PROVIDER,
			true,
	"openpistemap"),
	PUBLIC_TRANSPORT_OPVN("http://91.143.81.171/tiles/",  // www.xn--pnvkarte-m4a.de
			"DE-Public Transport",
			"Emphasizes Trains & Trams",
			".png",
			5,
			13,
			new BoundingBoxE6(55, 17.37195, 45.02330, 3.66101),
			256,
			18000,
			OSMMapTileProviderType.DOWNLOAD_PROVIDER,
			true,
	"oepnv"),
	GOOGLEMAPS("http://mt3.google.com/vt/v=w2.97&",
			"GoogleMaps",
			"Googles MapTiles",
			".png",
			0,
			19,
			null,
			256,
			18000,
			OSMMapTileProviderType.DOWNLOAD_PROVIDER,
			false,
	"googlemaps"),
	GOOGLEMAPS_SATELLITE("http://khm1.google.com/kh/v=37&",
			"GoogleMaps Satellite",
			"Googles MapTiles",
			".png",
			0,
			19,
			null,
			256,
			18000,
			OSMMapTileProviderType.DOWNLOAD_PROVIDER,
			false,
	"googlemaps_satellite"),
	GOOGLEMAPS_TERRAIN("http://mt3.google.com/mt/v=w2p.87&",
			"GoogleMaps Terrain",
			"Googles Terrain MapTiles",
			".png",
			0,
			15,
			null,
			256,
			18000,
			OSMMapTileProviderType.DOWNLOAD_PROVIDER,
			false,
	"googlemaps_terrain");
	//	TRIVIAL_RENDERER("http://trivial/", // URL and name are still needed - they are used as keys by the FS/memory tile caches.
	//			"Trvial-R",
	//			"Trivial (rendererd)",
	//			".png",
	//			15,
	//			18,
	//			null,
	//			256,
	//			18000,
	//			OSMMapTileProviderType.LOCAL_PROVIDER,
	//			true,
	//			"trivial_renderer"),
	//	MAPNIK_RENDERER("http://mapnik/", // URL and name are still needed - they are used as keys by the FS/memory tile caches.
	//			"Mapnik-R",
	//			"Mapnik (rendererd)",
	//			".png",
	//			0,
	//			18,
	//			null,
	//			256,
	//			18000,
	//			OSMMapTileProviderType.LOCAL_PROVIDER,
	//			true,
	//			"mapnik_rendered");

	// ===========================================================
	// Fields
	// ===========================================================

	public final String BASEURL, NAME, DESCRIPTION, IMAGE_FILENAMEENDING, FSFOLDERNAME;
	public final int ZOOM_MINLEVEL, ZOOM_MAXLEVEL, MAPTILE_SIZEPX, EXPECTED_AVERAGE_MAPTILE_BYTESIZE;
	public final OSMMapTileProviderType PROVIDER_TYPE;
	public final boolean LICENSEFREE;
	public final BoundingBoxE6 BOUNDINGBOXE6;

	// ===========================================================
	// Constructors
	// ===========================================================

	private OSMMapTileProviderInfo(final String aBaseUrl, final String aName,
			final String aDescription, final String aImageFilenameEnding, final int aZoomMin,
			final int aZoomMax, final BoundingBoxE6 aBoundingBoxE6, final int aTileSizePX,
			final int aAverageTileByteSize, final OSMMapTileProviderType aProviderType,
			final boolean aLicenseFree, final String aFSFolderName) {
		this.BASEURL = aBaseUrl;
		this.NAME = aName;
		this.ZOOM_MINLEVEL = aZoomMin;
		this.ZOOM_MAXLEVEL = aZoomMax;
		this.BOUNDINGBOXE6 = aBoundingBoxE6;
		this.IMAGE_FILENAMEENDING = aImageFilenameEnding;
		this.MAPTILE_SIZEPX = aTileSizePX;
		this.EXPECTED_AVERAGE_MAPTILE_BYTESIZE = aAverageTileByteSize;
		this.PROVIDER_TYPE = aProviderType;
		this.LICENSEFREE = aLicenseFree;
		this.DESCRIPTION = aDescription;
		this.FSFOLDERNAME = aFSFolderName;
	}

	public static OSMMapTileProviderInfo getDefault() {
		return MAPNIK;
	}

	public static OSMMapTileProviderInfo fromName(final String pName) {
		for(final OSMMapTileProviderInfo p : OSMMapTileProviderInfo.values()) {
			if(p.NAME.equals(pName)) {
				return p;
			}
		}

		return getDefault();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public boolean hasBoundingBox() {
		return this.BOUNDINGBOXE6 != null;
	}

	public GeoPoint bringToBoundingBox(final int aLatitudeE6, final int aLongitudeE6) {
		return this.BOUNDINGBOXE6.bringToBoundingBox(aLatitudeE6, aLongitudeE6);
	}

	public static OSMMapTileProviderInfo[] getLicenseFreeProviders() {
		// TODO check all occurences of OSMMapTileProviderInfo.values[]; so
		// ensure it is not possible to see route with googlemap-tiles!
		final ArrayList<OSMMapTileProviderInfo> tmp = new ArrayList<OSMMapTileProviderInfo>();
		for (final OSMMapTileProviderInfo p : OSMMapTileProviderInfo.values()) {
			if (p.LICENSEFREE) {
				tmp.add(p);
			}
		}

		return tmp.toArray(new OSMMapTileProviderInfo[tmp.size()]);
	}

	public String getTileURLString(final OSMTileInfo aTileInfo) {
		switch (this) {
			case GOOGLEMAPS:
			case GOOGLEMAPS_SATELLITE:
			case GOOGLEMAPS_TERRAIN:
				// "x=1&y=4&z=4"
				return new StringBuilder().append(this.BASEURL).append("x=").append(aTileInfo.x)
				.append("&y=").append(aTileInfo.y).append("&z=").append(aTileInfo.zoom)
				.toString();
			default:
				return new StringBuilder().append(this.BASEURL).append(aTileInfo.zoom).append("/")
				.append(aTileInfo.x).append("/").append(aTileInfo.y).append(
						this.IMAGE_FILENAMEENDING).toString();
		}
	}

	// ===========================================================
	// Tilename-Formatting
	// ===========================================================

	private static final char[] FILENAME_APPENDIX = ".andnav".toCharArray();

	/**
	 * Formats a URL to a String that it can be saved to a file, without
	 * problems of special chars.
	 * 
	 * <PRE>
	 * &lt;b&gt;Example:&lt;/b&gt;
	 * 
	 * &lt;code&gt;http://a.tile.openstreetmap.org/0/0/0.png&lt;/code&gt;
	 * would become
	 * &lt;code&gt;a.tile.openstreetmap.org_0_0_0.png.andnav&lt;/code&gt;
	 * </PRE>
	 * 
	 * @return saveable formatted URL as a String
	 */
	public String getSaveableTileURLString(final OSMTileInfo aTileInfo, final StoragePolicy aStoragePolicy) {
		switch (aStoragePolicy) {
			case EXTERNAL:
				return new StringBuilder().append(this.FSFOLDERNAME).append("/")
				.append(aTileInfo.zoom).append("/")
				.append(aTileInfo.x).append("/")
				.append(aTileInfo.y)
				.append(this.IMAGE_FILENAMEENDING).append(FILENAME_APPENDIX).toString();
			case INTERNALROM:
			default:
				return new StringBuilder().append(this.FSFOLDERNAME).append('_')
				.append(aTileInfo.zoom).append('_')
				.append(aTileInfo.x).append('_')
				.append(aTileInfo.y)
				.append(this.IMAGE_FILENAMEENDING).append(FILENAME_APPENDIX).toString();
		}
	}
}
