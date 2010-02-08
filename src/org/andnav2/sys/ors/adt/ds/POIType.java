// Created by plusminus on 18:51:25 - 15.11.2008
package org.andnav2.sys.ors.adt.ds;

import java.util.ArrayList;

import org.andnav2.R;
import org.openstreetmap.api.constants.OSMNodeAPIConstants;

import android.os.Parcel;
import android.os.Parcelable;


public enum POIType implements OSMNodeAPIConstants, Parcelable {
	// ===========================================================
	// Elements
	// ===========================================================
	UNKNOWN("_unknown_", R.string.unknown, POIGroup.UNKNOWN, null, OSMRepresentation.NONE),

	EMERGENCY("emergency", R.string.sd_poi_category_emergency, POIGroup.MAINGROUP, null, OSMRepresentation.NONE),
	FOOD("food", R.string.sd_poi_category_food, POIGroup.MAINGROUP, null, OSMRepresentation.NONE),
	LEISURE("leisure", R.string.sd_poi_category_leisure, POIGroup.MAINGROUP, null, OSMRepresentation.NONE),
	LOCALITY("locality", R.string.sd_poi_category_locality, POIGroup.MAINGROUP, null, OSMRepresentation.NONE),
	PUBLIC_TRANSPORT("public_transport", R.string.sd_poi_category_public_transport, POIGroup.MAINGROUP, null, OSMRepresentation.NONE),
	SHOP("shop", R.string.sd_poi_category_shop, POIGroup.MAINGROUP, null, OSMRepresentation.NONE),
	TOURISM("tourism", R.string.sd_poi_category_tourism, POIGroup.MAINGROUP, null, OSMRepresentation.NONE),
	TRANSPORT("transport", R.string.sd_poi_category_transport, POIGroup.MAINGROUP, null, OSMRepresentation.NONE),


	DENTIST("dentist", R.string.sd_poi_category_dentist, POIGroup.EMERGENCY, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	DOCTORS("doctors", R.string.sd_poi_category_doctors, new POIGroup[]{POIGroup.MOSTUSED, POIGroup.EMERGENCY}, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	EMERGENCY_PHONE("emergency_phone", R.string.sd_poi_category_emergency_phone, POIGroup.EMERGENCY, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE),
	FIRE_STATION("fire_station", R.string.sd_poi_category_fire_station, POIGroup.EMERGENCY, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	HOSPITAL("hospital", R.string.sd_poi_category_hospital, new POIGroup[]{POIGroup.MOSTUSED, POIGroup.EMERGENCY}, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	PHARMACY("pharmacy", R.string.sd_poi_category_pharmacy, POIGroup.EMERGENCY, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	POLICE("police", R.string.sd_poi_category_police, POIGroup.EMERGENCY, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	TELEPHONE("telephone", R.string.sd_poi_category_telephone, POIGroup.EMERGENCY, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE),
	EMBASSY("embassy", R.string.sd_poi_category_embassy, POIGroup.EMERGENCY, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	VETERINARY("veterinary", R.string.sd_poi_category_veterinary, POIGroup.EMERGENCY, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),

	BIERGARTEN("biergarten", R.string.sd_poi_category_biergarten, new POIGroup[]{POIGroup.MOSTUSED, POIGroup.FOOD}, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	CAFE("cafe", R.string.sd_poi_category_cafe, POIGroup.FOOD, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	DRINKING_WATER("drinking_water", R.string.sd_poi_category_drinking_water, POIGroup.FOOD, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE),
	FAST_FOOD("fast_food", R.string.sd_poi_category_fast_food, POIGroup.FOOD, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	FOOD_COURT("food_court", R.string.sd_poi_category_food_court, POIGroup.FOOD, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	PUB("pub", R.string.sd_poi_category_pub, POIGroup.FOOD, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	RESTAURANT("restaurant", R.string.sd_poi_category_restaurant, POIGroup.FOOD, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	VENDING_MACHINE("vending_machine", R.string.sd_poi_category_vending_machine, POIGroup.FOOD, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE),

	BOWLING_10PIN("10pin", R.string.sd_poi_category_10pin, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	BOWLING_9PIN("9pin", R.string.sd_poi_category_9pin, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	ATHLETICS("athletics", R.string.sd_poi_category_athletics, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	AUSTRALIAN_FOOTBALL("australian_football", R.string.sd_poi_category_australian_football, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	BASEBALL("baseball", R.string.sd_poi_category_baseball, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	BASKETBALL("basketball", R.string.sd_poi_category_basketball, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	BEACHVOLLEYBALL("beachvolleyball", R.string.sd_poi_category_beachvolleyball, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	BOULES("boules", R.string.sd_poi_category_boules, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	CHESS("chess", R.string.sd_poi_category_chess, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	CLIMBING("climbing", R.string.sd_poi_category_climbing, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	CRICKET("cricket", R.string.sd_poi_category_cricket, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	CRICKET_NETS("cricket_nets", R.string.sd_poi_category_cricket_nets, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	CROQUET("croquet", R.string.sd_poi_category_croquet, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	CYCLING("cycling", R.string.sd_poi_category_cycling, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	EQUESTRIAN("equestrian", R.string.sd_poi_category_equestrian, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	FOOTBALL("football", R.string.sd_poi_category_football, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	FOUNTAIN("fountain", R.string.sd_poi_category_fountain, POIGroup.LEISURE, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	GARDEN("garden", R.string.sd_poi_category_garden, POIGroup.LEISURE, NODE_TAG_KEY_LEISURE, OSMRepresentation.NODE_OR_POLYGON),
	GOLF("golf", R.string.sd_poi_category_golf, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	GOLF_COURSE("golf_course", R.string.sd_poi_category_golf_course, POIGroup.LEISURE, NODE_TAG_KEY_LEISURE, OSMRepresentation.NODE_OR_POLYGON),
	GYMNASTICS("gymnastics", R.string.sd_poi_category_gymnastics, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	HOCKEY("hockey", R.string.sd_poi_category_hockey, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	HORSE_RACING("horse_racing", R.string.sd_poi_category_horse_racing, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	MOTOR("motor", R.string.sd_poi_category_motor, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	MULTI("multi", R.string.sd_poi_category_multi, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	PARK("park", R.string.sd_poi_category_park, POIGroup.LEISURE, NODE_TAG_KEY_LEISURE, OSMRepresentation.NODE_OR_POLYGON),
	PITCH("pitch", R.string.sd_poi_category_pitch, POIGroup.LEISURE, NODE_TAG_KEY_LEISURE, OSMRepresentation.NODE_OR_POLYGON),
	PLAYGROUND("playground", R.string.sd_poi_category_playground, POIGroup.LEISURE, NODE_TAG_KEY_LEISURE, OSMRepresentation.NODE_OR_POLYGON),
	RUGBY("rugby", R.string.sd_poi_category_rugby, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	SHOOTING("shooting", R.string.sd_poi_category_shooting, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	SKATEBOARD("skateboard", R.string.sd_poi_category_skateboard, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	SKATING("skating", R.string.sd_poi_category_skating, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	SKIING("skiing", R.string.sd_poi_category_skiing, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	SOCCER("soccer", R.string.sd_poi_category_soccer, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	SPORTS_CENTRE("sports_centre", R.string.sd_poi_category_sports_centre, POIGroup.LEISURE, NODE_TAG_KEY_LEISURE, OSMRepresentation.NODE_OR_POLYGON),
	STADIUM("stadium", R.string.sd_poi_category_stadium, POIGroup.LEISURE, NODE_TAG_KEY_LEISURE, OSMRepresentation.NODE_OR_POLYGON),
	SWIMMING("swimming", R.string.sd_poi_category_swimming, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	TABLE_TENNIS("table_tennis", R.string.sd_poi_category_table_tennis, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	TEAM_HANDBALL("team_handball", R.string.sd_poi_category_team_handball, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	TENNIS("tennis", R.string.sd_poi_category_tennis, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	VOLLEYBALL("volleyball", R.string.sd_poi_category_volleyball, POIGroup.LEISURE, NODE_TAG_KEY_SPORT, OSMRepresentation.NODE_OR_POLYGON),
	WATER_PARK("water_park", R.string.sd_poi_category_water_park, POIGroup.LEISURE, NODE_TAG_KEY_LEISURE, OSMRepresentation.NODE_OR_POLYGON),

	ATM("atm", R.string.sd_poi_category_atm, new POIGroup[]{POIGroup.LOCALITY, POIGroup.MOSTUSED}, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE),
	BANK("bank", R.string.sd_poi_category_bank, POIGroup.LOCALITY, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	BICYCLE_PARKING("bicycle_parking", R.string.sd_poi_category_bicycle_parking, POIGroup.LOCALITY, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	BROTHEL("brothel", R.string.sd_poi_category_brothel, POIGroup.LOCALITY, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	BUREAU_DE_CHANGE("bureau_de_change", R.string.sd_poi_category_bureau_de_change, POIGroup.LOCALITY, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE),
	CINEMA("cinema", R.string.sd_poi_category_cinema, POIGroup.LOCALITY, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	COLLEGE("college", R.string.sd_poi_category_college, POIGroup.LOCALITY, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	COURTHOUSE("courthouse", R.string.sd_poi_category_courthouse, POIGroup.LOCALITY, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	GRAVE_YARD("grave_yard", R.string.sd_poi_category_grave_yard, POIGroup.LOCALITY, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	KINDERGARTEN("kindergarten", R.string.sd_poi_category_kindergarten, POIGroup.LOCALITY, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	LIBRARY("library", R.string.sd_poi_category_library, POIGroup.LOCALITY, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	NIGHTCLUB("nightclub", R.string.sd_poi_category_nightclub, POIGroup.LOCALITY, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	POST_BOX("post_box", R.string.sd_poi_category_post_box, POIGroup.LOCALITY, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE),
	POST_OFFICE("post_office", R.string.sd_poi_category_post_office, POIGroup.LOCALITY, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	SCHOOL("school", R.string.sd_poi_category_school, POIGroup.LOCALITY, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	SERVICES("services", R.string.sd_poi_category_services, POIGroup.LOCALITY, NODE_TAG_KEY_HIGHWAY, OSMRepresentation.NODE_OR_POLYGON),
	THEATRE("theatre", R.string.sd_poi_category_theatre, POIGroup.LOCALITY, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	TOILETS("toilets", R.string.sd_poi_category_toilets, POIGroup.LOCALITY, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE),
	UNIVERSITY("university", R.string.sd_poi_category_university, POIGroup.LOCALITY, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),

	AERODROME("aerodrome", R.string.sd_poi_category_aerodrome, POIGroup.PUBLIC_TRANSPORT, NODE_TAG_KEY_AEROWAY, OSMRepresentation.NODE_OR_POLYGON),
	BUS_STATION("bus_station", R.string.sd_poi_category_bus_station, POIGroup.PUBLIC_TRANSPORT, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	BUS_STOP("bus_stop", R.string.sd_poi_category_bus_stop, POIGroup.PUBLIC_TRANSPORT, NODE_TAG_KEY_HIGHWAY, OSMRepresentation.NODE),
	FERRY_TERMINAL("ferry_terminal", R.string.sd_poi_category_ferry_terminal, POIGroup.PUBLIC_TRANSPORT, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	HALT("halt", R.string.sd_poi_category_halt, POIGroup.PUBLIC_TRANSPORT, NODE_TAG_KEY_RAILWAY, OSMRepresentation.NODE),
	RALIWAY_STATION("station", R.string.sd_poi_category_railway_station, new POIGroup[]{POIGroup.MOSTUSED, POIGroup.PUBLIC_TRANSPORT}, NODE_TAG_KEY_AERIALWAY, OSMRepresentation.NODE_OR_POLYGON),
	AERIALWAY_STATION("station", R.string.sd_poi_category_aerialway_station, POIGroup.PUBLIC_TRANSPORT, NODE_TAG_KEY_RAILWAY, OSMRepresentation.NODE_OR_POLYGON),
	SUBWAY_ENTRANCE("subway_entrance", R.string.sd_poi_category_subway_entrance, POIGroup.PUBLIC_TRANSPORT, NODE_TAG_KEY_RAILWAY, OSMRepresentation.NODE),
	TERMINAL("terminal", R.string.sd_poi_category_terminal, POIGroup.PUBLIC_TRANSPORT, NODE_TAG_KEY_AEROWAY, OSMRepresentation.NODE_OR_POLYGON),
	TRAM_STOP("tram_stop", R.string.sd_poi_category_tram_stop, POIGroup.PUBLIC_TRANSPORT, NODE_TAG_KEY_RAILWAY, OSMRepresentation.NODE),

	ALCOHOL("alcohol", R.string.sd_poi_category_alcohol, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	BAKERY("bakery", R.string.sd_poi_category_bakery, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	BEVERAGES("beverages", R.string.sd_poi_category_beverages, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	BICYCLE("bicycle", R.string.sd_poi_category_bicycle, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	BOOKS("books", R.string.sd_poi_category_books, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	BUTCHER("butcher", R.string.sd_poi_category_butcher, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	CAR("car", R.string.sd_poi_category_car, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	CHEMIST("chemist", R.string.sd_poi_category_chemist, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	CLOTHES("clothes", R.string.sd_poi_category_clothes, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	COMPUTER("computer", R.string.sd_poi_category_computer, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	CONVENIENCE("convenience", R.string.sd_poi_category_convenience, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	DEPARTMENT_STORE("department_store", R.string.sd_poi_category_department_store, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	DOITYOURSELF("doityourself", R.string.sd_poi_category_doityourself, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	DRY_CLEANING("dry_cleaning", R.string.sd_poi_category_dry_cleaning, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	ELECTRONICS("electronics", R.string.sd_poi_category_electronics, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	FLORIST("florist", R.string.sd_poi_category_florist, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	FURNITURE("furniture", R.string.sd_poi_category_furniture, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	GARDEN_CENTRE("garden_centre", R.string.sd_poi_category_garden_centre, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	HAIRDRESSER("hairdresser", R.string.sd_poi_category_hairdresser, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	HARDWARE("hardware", R.string.sd_poi_category_hardware, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	HIFI("hifi", R.string.sd_poi_category_hifi, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	KIOSK("kiosk", R.string.sd_poi_category_kiosk, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	LAUNDRY("laundry", R.string.sd_poi_category_laundry, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	MALL("mall", R.string.sd_poi_category_mall, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	MOTORCYCLE("motorcycle", R.string.sd_poi_category_motorcycle, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	OPTICIAN("optician", R.string.sd_poi_category_optician, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	ORGANIC("organic", R.string.sd_poi_category_organic, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	OUTDOOR("outdoor", R.string.sd_poi_category_outdoor, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	SHOES("shoes", R.string.sd_poi_category_shoes, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	SPORTS("sports", R.string.sd_poi_category_sports, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	STATIONERY("stationery", R.string.sd_poi_category_stationery, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	SUPERMARKET("supermarket", R.string.sd_poi_category_supermarket, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	TOYS("toys", R.string.sd_poi_category_toys, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	VIDEO("video", R.string.sd_poi_category_video, POIGroup.SHOP, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),

	ALPINE_HUT("alpine_hut", R.string.sd_poi_category_alpine_hut, POIGroup.TOURISM, NODE_TAG_KEY_TOURISM, OSMRepresentation.NODE_OR_POLYGON),
	ARTS_CENTRE("arts_centre", R.string.sd_poi_category_arts_centre, POIGroup.TOURISM, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	ATTRACTION("attraction", R.string.sd_poi_category_attraction, POIGroup.TOURISM, NODE_TAG_KEY_TOURISM, OSMRepresentation.NODE_OR_POLYGON),
	CAMP_SITE("camp_site", R.string.sd_poi_category_camp_site, POIGroup.TOURISM, NODE_TAG_KEY_TOURISM, OSMRepresentation.NODE_OR_POLYGON),
	CARAVAN_SITE("caravan_site", R.string.sd_poi_category_caravan_site, POIGroup.TOURISM, NODE_TAG_KEY_TOURISM, OSMRepresentation.NODE_OR_POLYGON),
	CASTLE("castle", R.string.sd_poi_category_castle, POIGroup.TOURISM, NODE_TAG_KEY_HISTORIC, OSMRepresentation.NODE_OR_POLYGON),
	CHALET("chalet", R.string.sd_poi_category_chalet, POIGroup.TOURISM, NODE_TAG_KEY_TOURISM, OSMRepresentation.NODE_OR_POLYGON),
	GUEST_HOUSE("guest_house", R.string.sd_poi_category_guest_house, POIGroup.TOURISM, NODE_TAG_KEY_TOURISM, OSMRepresentation.NODE_OR_POLYGON),
	HOSTEL("hostel", R.string.sd_poi_category_hostel, POIGroup.TOURISM, NODE_TAG_KEY_TOURISM, OSMRepresentation.NODE_OR_POLYGON),
	HOTEL("hotel", R.string.sd_poi_category_hotel, POIGroup.TOURISM, NODE_TAG_KEY_TOURISM, OSMRepresentation.NODE_OR_POLYGON),
	INFORMATION("information", R.string.sd_poi_category_information, POIGroup.TOURISM, NODE_TAG_KEY_TOURISM, OSMRepresentation.NODE_OR_POLYGON),
	LIGHTHOUSE("lighthouse", R.string.sd_poi_category_lighthouse, POIGroup.TOURISM, NODE_TAG_KEY_MANMADE, OSMRepresentation.NODE),
	MEMORIAL("memorial", R.string.sd_poi_category_memorial, POIGroup.TOURISM, NODE_TAG_KEY_HISTORIC, OSMRepresentation.NODE_OR_POLYGON),
	MOTEL("motel", R.string.sd_poi_category_motel, POIGroup.TOURISM, NODE_TAG_KEY_TOURISM, OSMRepresentation.NODE_OR_POLYGON),
	MUSEUM("museum", R.string.sd_poi_category_museum, POIGroup.TOURISM, NODE_TAG_KEY_TOURISM, OSMRepresentation.NODE_OR_POLYGON),
	PICNIC_SITE("picnic_site", R.string.sd_poi_category_picnic_site, POIGroup.TOURISM, NODE_TAG_KEY_TOURISM, OSMRepresentation.NODE_OR_POLYGON),
	RUINS("ruins", R.string.sd_poi_category_ruins, POIGroup.TOURISM, NODE_TAG_KEY_HISTORIC, OSMRepresentation.NODE_OR_POLYGON),
	THEME_PARK("theme_park", R.string.sd_poi_category_theme_park, POIGroup.TOURISM, NODE_TAG_KEY_TOURISM, OSMRepresentation.NODE_OR_POLYGON),
	VIEWPOINT("viewpoint", R.string.sd_poi_category_viewpoint, POIGroup.TOURISM, NODE_TAG_KEY_TOURISM, OSMRepresentation.NODE),
	ZOO("zoo", R.string.sd_poi_category_zoo, POIGroup.TOURISM, NODE_TAG_KEY_TOURISM, OSMRepresentation.NODE_OR_POLYGON),

	CAR_RENTAL("car_rental", R.string.sd_poi_category_car_rental, POIGroup.TRANSPORT, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	CAR_REPAIR("car_repair", R.string.sd_poi_category_car_repair, POIGroup.TRANSPORT, NODE_TAG_KEY_SHOP, OSMRepresentation.NODE_OR_POLYGON),
	CAR_SHARING("car_sharing", R.string.sd_poi_category_car_sharing, POIGroup.TRANSPORT, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	FUEL("fuel", R.string.sd_poi_category_fuel, new POIGroup[]{POIGroup.TRANSPORT, POIGroup.MOSTUSED}, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	PARKING("parking", R.string.sd_poi_category_parking, new POIGroup[]{POIGroup.TRANSPORT, POIGroup.MOSTUSED}, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON),
	TAXI("taxi", R.string.sd_poi_category_taxi, POIGroup.TRANSPORT, NODE_TAG_KEY_AMENITY, OSMRepresentation.NODE_OR_POLYGON);

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	public final POIGroup[] POIGROUPS;

	public final String RAWNAME;
	public final int READABLENAMERESID;
	public final String OSMKEYNAME;
	public final OSMRepresentation[] OSMREPRESENTATIONS;

	// ===========================================================
	// Constructors
	// ===========================================================

	private POIType(final String pRawName, final int pReadableNameResID, final POIGroup pGroup, final String pOSMKeyName, final OSMRepresentation pOSMRepresentation){
		this(pRawName, pReadableNameResID, new POIGroup[]{pGroup}, pOSMKeyName, new OSMRepresentation[]{pOSMRepresentation});
	}

	private POIType(final String pRawName, final int pReadableNameResID, final POIGroup pGroup, final String pOSMKeyName, final OSMRepresentation[] pOSMRepresentations){
		this(pRawName, pReadableNameResID, new POIGroup[]{pGroup}, pOSMKeyName, pOSMRepresentations);
	}

	private POIType(final String pRawName, final int pReadableNameResID, final POIGroup[] pGroups, final String pOSMKeyName, final OSMRepresentation pOSMRepresentation){
		this(pRawName, pReadableNameResID, pGroups, pOSMKeyName, new OSMRepresentation[]{pOSMRepresentation});
	}

	private POIType(final String pRawName, final int pReadableNameResID, final POIGroup[] pGroups, final String pOSMKeyName, final OSMRepresentation[] pOSMRepresentations){
		this.POIGROUPS = pGroups;
		this.RAWNAME = pRawName;
		this.READABLENAMERESID = pReadableNameResID;
		this.OSMKEYNAME = pOSMKeyName;
		this.OSMREPRESENTATIONS = pOSMRepresentations;
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

	public boolean isInGroup(final POIGroup pGroup){
		for(final POIGroup p : this.POIGROUPS) {
			if(p == pGroup) {
				return true;
			}
		}

		return false;
	}

	public static POIType fromRawName(final String pPoitypeName) {
		for(final POIType p : POIType.values()) {
			if(p.RAWNAME.compareToIgnoreCase(pPoitypeName) == 0) {
				return p;
			}
		}

		throw new IllegalArgumentException("No POIType found for " + pPoitypeName);
	}

	public static ArrayList<POIType> getAllOfGroup(final POIGroup pGroup) {
		return getAllOfGroup(pGroup, null);
	}

	public static ArrayList<POIType> getAllOfGroup(final POIGroup pGroup, final OSMRepresentation pMustFitRepresentations) {
		final ArrayList<POIType> out = new ArrayList<POIType>();
		for(final POIType p : POIType.values()) {
			if(p.isInGroup(pGroup)) {
				if(pMustFitRepresentations == null) {
					out.add(p);
				} else {
					for(final OSMRepresentation r : p.OSMREPRESENTATIONS) {
						if(pMustFitRepresentations == r) {
							out.add(p);
						}
					}
				}
			}
		}

		return out;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	// ===========================================================
	// Parcelable
	// ===========================================================

	public static final Parcelable.Creator<POIType> CREATOR = new Parcelable.Creator<POIType>() {
		public POIType createFromParcel(final Parcel in) {
			return readFromParcel(in);
		}

		public POIType[] newArray(final int size) {
			return new POIType[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(final Parcel out, final int arg1) {
		out.writeInt(this.ordinal());
	}

	private static POIType readFromParcel(final Parcel in){
		return POIType.values()[in.readInt()];
	}
}
