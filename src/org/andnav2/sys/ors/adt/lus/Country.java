package org.andnav2.sys.ors.adt.lus;

import java.util.ArrayList;

import org.andnav2.R;
import org.andnav2.exc.Exceptor;
import org.andnav2.osm.adt.BoundingBoxE6;
import org.andnav2.sys.ors.adt.rs.DirectionsLanguage;
import org.andnav2.sys.vehicleregistrationplates.VRPRegistry;
import org.andnav2.sys.vehicleregistrationplates.tables.VRP_DE;
import org.andnav2.util.Capitalizer;
import org.andnav2.util.constants.Constants;

import android.os.Parcel;
import android.os.Parcelable;

/** This enum associates a Country-Name,
 * its abbreviation (used) for YAHOO-GeoCode-API
 * and a resID pointing to the country's flag. */
public enum Country implements Parcelable {
	// ===========================================================
	// Elements
	// ===========================================================

	/* Countrycode-source:
	 * http://www.iso.org/iso/country_codes/iso_3166_code_lists/english_country_names_and_code_elements.htm
	 * 
	 * Note: Before "G" countries that had no Flag are missing. After (inclusive) G they are commented out, so they are ready to be enabled when a flag is found.
	 * */
	UNKNOWN(R.string.unknown, "", R.drawable.questionmark, null, null, false, null, Constants.NOT_SET),
	AFGHANISTAN(R.string.country_afghanistan, "AF", R.drawable.flag_afghanistan, null, null, false, null, Constants.NOT_SET),
	ALBANIA(R.string.country_albania, "AL", R.drawable.flag_albania, null, null, true, null, Constants.NOT_SET),
	ALGERIA(R.string.country_algeria, "DZ", R.drawable.flag_algeria, null, null, false, null, Constants.NOT_SET),
	ANDORRA(R.string.country_andorra, "AD", R.drawable.flag_andorra, null, null, true, null, Constants.NOT_SET),
	ANGOLA(R.string.country_angola, "AO", R.drawable.flag_angola, null, null, false, null, Constants.NOT_SET),
	ANGUILLA(R.string.country_anguilla, "AI", R.drawable.flag_anguilla, null, null, false, null, Constants.NOT_SET),
	ANTARCTICA(R.string.country_antarctica, "AQ", R.drawable.flag_antarctica, null, null, false, null, Constants.NOT_SET),
	ANTIGUAANDBARBUDA(R.string.country_antiguaandbarbuda, "AG", R.drawable.flag_antiguaandbarbuda, null, null, false, null, Constants.NOT_SET),
	ARGENTINA(R.string.country_argentina, "AR", R.drawable.flag_argentina, null, null, false, null, Constants.NOT_SET),
	ARMENIA(R.string.country_armenia, "AM", R.drawable.flag_armenia, null, null, false, null, Constants.NOT_SET),
	AUSTRIA(R.string.country_austria, "AT", R.drawable.flag_austria, null, null, true, null, Constants.NOT_SET),
	AUSTRALIA(R.string.country_australia, "AU", R.drawable.flag_australia, null, null, false, null, Constants.NOT_SET),
	AZERBAIJAN(R.string.country_azerbaijan, "AZ", R.drawable.flag_azerbaijan, null, null, false, null, Constants.NOT_SET),
	BAHAMAS(R.string.country_bahamas, "BS", R.drawable.flag_bahamas, null, null, false, null, Constants.NOT_SET),
	BAHRAIN(R.string.country_bahrain, "BH", R.drawable.flag_bahrain, null, null, false, null, Constants.NOT_SET),
	BANGLADESH(R.string.country_bangladesh, "BD", R.drawable.flag_bangladesh, null, null, false, null, Constants.NOT_SET),
	BARBADOS(R.string.country_barbados, "BB", R.drawable.flag_barbados, null, null, false, null, Constants.NOT_SET),
	BELARUS(R.string.country_belarus, "BY", R.drawable.flag_belarus, null, null, true, null, Constants.NOT_SET),
	BELGIUM(R.string.country_belgium, "BE", R.drawable.flag_belgium, null, null, true, null, Constants.NOT_SET),
	BELIZE(R.string.country_belize, "BZ", R.drawable.flag_belize, null, null, false, null, Constants.NOT_SET),
	BENIN(R.string.country_benin, "BJ", R.drawable.flag_benin, null, null, false, null, Constants.NOT_SET),
	BERMUDA(R.string.country_bermuda, "BM", R.drawable.flag_bermuda, null, null, false, null, Constants.NOT_SET),
	BHUTAN(R.string.country_bhutan, "BT", R.drawable.flag_bhutan, null, null, false, null, Constants.NOT_SET),
	BOLIVIA(R.string.country_bolivia, "BO", R.drawable.flag_bolivia, null, null, false, null, Constants.NOT_SET),
	BOSNIAANDHERZEGOVINA(R.string.country_bosniaandherzegovina, "BA", R.drawable.flag_bosniaandherzegovina, "bs", null, true, null, Constants.NOT_SET),
	BOTSWANA(R.string.country_botswana, "BW", R.drawable.flag_botswana, null, null, false, null, Constants.NOT_SET),
	BRAZIL(R.string.country_brazil, "BR", R.drawable.flag_brazil, null, null, false, null, Constants.NOT_SET),
	BRUNEI(R.string.country_brunei, "BN", R.drawable.flag_brunei, null, null, false, null, Constants.NOT_SET),
	BULGARIA(R.string.country_bulgaria, "BG", R.drawable.flag_bulgaria, null, null, true, null, Constants.NOT_SET),
	BURKINAFASO(R.string.country_burkinafaso, "BF", R.drawable.flag_burkinafaso, null, null, false, null, Constants.NOT_SET),
	BURUNDI(R.string.country_burundi, "BI", R.drawable.flag_burundi, null, null, false, null, Constants.NOT_SET),
	CAMBODIA(R.string.country_cambodia, "KH", R.drawable.flag_cambodia, null, null, false, null, Constants.NOT_SET),
	CAMEROON(R.string.country_cameroon, "CM", R.drawable.flag_cameroon, null, null, false, null, Constants.NOT_SET),
	CANADA(R.string.country_canada, "CA", R.drawable.flag_canada, null, null, false, null, Constants.NOT_SET),
	CAPEVERDE(R.string.country_capeverde, "CV", R.drawable.flag_capeverde, null, null, false, null, Constants.NOT_SET),
	CAYMANISLANDS(R.string.country_caymanislands, "KY", R.drawable.flag_caymanislands, null, null, false, null, Constants.NOT_SET),
	CHAD(R.string.country_chad, "TD", R.drawable.flag_chad, null, null, false, null, Constants.NOT_SET),
	CHILE(R.string.country_chile, "CL", R.drawable.flag_chile, null, null, false, null, Constants.NOT_SET),
	CHINA(R.string.country_china, "CN", R.drawable.flag_china, "zh", null, false, null, Constants.NOT_SET),
	COLOMBIA(R.string.country_colombia, "CO", R.drawable.flag_colombia, null, null, false, null, Constants.NOT_SET),
	COMOROS(R.string.country_comoros, "KM", R.drawable.flag_comoros, null, null, false, null, Constants.NOT_SET),
	CONGOREPUBLIC(R.string.country_congorepublic, "CG", R.drawable.flag_congorepublic, null, null, false, null, Constants.NOT_SET),
	CONGODEMOCRATICREPUBLIC(R.string.country_congodemocraticrepublic, "CD", R.drawable.flag_congodemocraticrepublic, null, null, false, null, Constants.NOT_SET),
	COOKISLANDS(R.string.country_cookislands, "CK", R.drawable.flag_cookislands, null, null, false, null, Constants.NOT_SET),
	COSTARICA(R.string.country_costarica, "CR", R.drawable.flag_costarica, null, null, false, null, Constants.NOT_SET),
	COTEDIVOIRE(R.string.country_cotedivoire, "CI", R.drawable.flag_cotedivoire, null, null, false, null, Constants.NOT_SET),
	CROATIA(R.string.country_croatia, "HR", R.drawable.flag_croatia, "hr", null, true, null, Constants.NOT_SET),
	CUBA(R.string.country_cuba, "CU", R.drawable.flag_cuba, null, null, false, null, Constants.NOT_SET),
	CYPRUS(R.string.country_cyprus, "CY", R.drawable.flag_cyprus, null, null, false, null, Constants.NOT_SET),
	CZECHREPUBLIC(R.string.country_czechrepublic, "CZ", R.drawable.flag_czech_republic, "cs", null, true, new DirectionsLanguage[]{DirectionsLanguage.CZECH}, Constants.NOT_SET), 
	DENMARK(R.string.country_denmark, "DK", R.drawable.flag_denmark, null, null, true, null, Constants.NOT_SET),
	DJIBOUTI(R.string.country_djibouti, "DJ", R.drawable.flag_djibouti, null, null, false, null, Constants.NOT_SET),
	DOMINICA(R.string.country_dominica, "DM", R.drawable.flag_dominica, null, null, false, null, Constants.NOT_SET),
	DOMINICANREPUBLIC(R.string.country_dominicanrepublic, "DO", R.drawable.flag_dominicanrepublic, null, null, false, null, Constants.NOT_SET),
	ECUADOR(R.string.country_ecuador, "EC", R.drawable.flag_ecuador, null, null, false, null, Constants.NOT_SET),
	EGYPT(R.string.country_egypt, "EG", R.drawable.flag_egypt, null, null, false, null, Constants.NOT_SET),
	ELSALVADOR(R.string.country_equatorialguinea, "SV", R.drawable.flag_elsalvador, null, null, false, null, Constants.NOT_SET),
	EQUATORIALGUINEA(R.string.country_equatorialguinea, "GQ", R.drawable.flag_equatorialguinea, null, null, false, null, Constants.NOT_SET),
	ERITREA(R.string.country_eritrea, "ER", R.drawable.flag_eritrea, null, null, false, null, Constants.NOT_SET),
	ESTONIA(R.string.country_estonia, "EE", R.drawable.flag_estonia, null, null, true, null, Constants.NOT_SET),
	ETHIOPOA(R.string.country_ethiopoa, "ET", R.drawable.flag_ethiopia, null, null, false, null, Constants.NOT_SET),
	FAROEISLANDS(R.string.country_faroeislands, "FO", R.drawable.flag_faroes, null, null, false, null, Constants.NOT_SET),
	FIJI(R.string.country_fiji, "FJ", R.drawable.flag_fiji, null, null, false, null, Constants.NOT_SET),
	FINLAND(R.string.country_finland, "FI", R.drawable.flag_finland, "fi", null, true, null, Constants.NOT_SET),
	FRANCE(R.string.country_france, "FR", R.drawable.flag_france, "fr", null, true, new DirectionsLanguage[]{DirectionsLanguage.FRENCH}, Constants.NOT_SET),
	GABON(R.string.country_gabon, "GA", R.drawable.flag_gabon, null, null, false, null, Constants.NOT_SET),
	GAMBIA(R.string.country_gambia, "GM", R.drawable.flag_gambia, null, null, false, null, Constants.NOT_SET),
	GEORGIA(R.string.country_georgia, "GE", R.drawable.flag_georgia, null, null, false, null, Constants.NOT_SET),
	GERMANY(R.string.country_germany, "DE", R.drawable.flag_germany, "de", null, true, new DirectionsLanguage[]{DirectionsLanguage.GERMAN, DirectionsLanguage.GERMAN_BERLINERISCH, DirectionsLanguage.GERMAN_PLATT, DirectionsLanguage.GERMAN_RHEINLAENDISCH, DirectionsLanguage.GERMAN_RUHRPOTT, DirectionsLanguage.GERMAN_SCHWAEBISCH}, VRP_DE.ID),
	GHANA(R.string.country_ghana, "GH", R.drawable.flag_ghana, null, null, false, null, Constants.NOT_SET),
	GIBRALTAR(R.string.country_gibraltar, "GI", R.drawable.flag_gibraltar, null, null, false, null, Constants.NOT_SET),
	GREECE(R.string.country_greece, "GR", R.drawable.flag_greece, "el", null, true, null, Constants.NOT_SET),
	//	GREENLAND(R.string.country_greenland, "GL", R.drawable.flag_greenland, false, null),
	GRENADA(R.string.country_grenada, "GO", R.drawable.flag_grenada, null, null, false, null, Constants.NOT_SET),
	//	GUDELOUPE(R.string.country_gudeloupe, "GP", R.drawable.flag_guadeloupe, false, null),
	GUAM(R.string.country_guam, "GU", R.drawable.flag_guam, null, null, false, null, Constants.NOT_SET),
	GUATEMALA(R.string.country_guatemala, "GT", R.drawable.flag_guatemala, null, null, false, null, Constants.NOT_SET),
	//	GUERNSEY(R.string.country_guernsey, "GG", R.drawable.flag_guernsey, false, null),
	GUINEA(R.string.country_guinea, "GN", R.drawable.flag_guinea, null, null, false, null, Constants.NOT_SET),
	GUINEABISSAU(R.string.country_guineabissau, "GW", R.drawable.flag_guineabissau, null, null, false, null, Constants.NOT_SET),
	GUYANA(R.string.country_guyana, "GY", R.drawable.flag_guyana, null, null, false, null, Constants.NOT_SET),
	HAITI(R.string.country_haiti, "HT", R.drawable.flag_haiti, null, null, false, null, Constants.NOT_SET),
	//	HEARDISLANDANDMCDONALDISLANDS(R.string.country_heardislandandmcdonaldislands, "HM", R.drawable.flag_heardislandandmcdonaldislands, false, null),
	HONDURAS(R.string.country_honduras, "HN", R.drawable.flag_honduras, null, null, false, null, Constants.NOT_SET),
	HONGKONG(R.string.country_hongkong, "HK", R.drawable.flag_hongkong, null, null, false, null, Constants.NOT_SET),
	HUNGARY(R.string.country_hungary, "HU", R.drawable.flag_hungary, "hu", null, true, null, Constants.NOT_SET),
	ICELAND(R.string.country_iceland, "IS", R.drawable.flag_iceland, "is", null, true, null, Constants.NOT_SET),
	INDIA(R.string.country_india, "IN", R.drawable.flag_india, "hi", null, false, null, Constants.NOT_SET),
	INDONESIA(R.string.country_indonesia, "ID", R.drawable.flag_indonesia, "id", null, false, null, Constants.NOT_SET),
	IRAN(R.string.country_iran, "IR", R.drawable.flag_iran, null, null, false, null, Constants.NOT_SET),
	IRAQ(R.string.country_iraq, "IQ", R.drawable.flag_iraq, null, null, false, null, Constants.NOT_SET),
	IRELAND(R.string.country_ireland, "IE", R.drawable.flag_ireland, null, null, true, null, Constants.NOT_SET),
	//	ISLEOFMAN(R.string.country_isleofman, "IM", R.drawable.flag_isleofman, false, null),
	ISRAEL(R.string.country_israel, "IL", R.drawable.flag_israel, null, null, false, null, Constants.NOT_SET),
	ITALY(R.string.country_italy, "IT", R.drawable.flag_italy, "it", null, true, new DirectionsLanguage[]{DirectionsLanguage.ITALIAN}, Constants.NOT_SET),
	JAMAICA(R.string.country_jamaica, "JM", R.drawable.flag_jamaica, null, null, false, null, Constants.NOT_SET),
	JAPAN(R.string.country_japan, "JP", R.drawable.flag_japan, null, null, false, null, Constants.NOT_SET),
	JERSEY(R.string.country_jersey, "JE", R.drawable.flag_jersey, null, null, false, null, Constants.NOT_SET),
	JORDAN(R.string.country_jordan, "JO", R.drawable.flag_jordan, null, null, false, null, Constants.NOT_SET),
	KAZAKHSTAN(R.string.country_kazakhstan, "KZ", R.drawable.flag_kazakhstan, null, null, false, null, Constants.NOT_SET),
	KENYA(R.string.country_kenya, "KE", R.drawable.flag_kenya, null, null, false, null, Constants.NOT_SET),
	KIRIBATI(R.string.country_kiribati, "KI", R.drawable.flag_kiribati, null, null, false, null, Constants.NOT_SET),
	KOREASOUTH(R.string.country_koreasouth, "KP", R.drawable.flag_koreasouth, null, null, false, null, Constants.NOT_SET),
	KOREANORTH(R.string.country_koreanorth, "KR", R.drawable.flag_koreasouth, null, null, false, null, Constants.NOT_SET),
	KUWAIT(R.string.country_kuwait, "KW", R.drawable.flag_kuwait, null, null, false, null, Constants.NOT_SET),
	KYRGYZSTAN(R.string.country_kyrgyzstan, "KG", R.drawable.flag_kyrgyzstan, null, null, false, null, Constants.NOT_SET),
	LAOPEOPLESDEMOCRATICREPUBLIC(R.string.country_laopeoplesdemocraticrepublic, "LA", R.drawable.flag_laos, null, null, false, null, Constants.NOT_SET),
	LATVIA(R.string.country_latvia, "LV", R.drawable.flag_latvia, null, null, true, null, Constants.NOT_SET),
	LEBANON(R.string.country_lebanon, "LB", R.drawable.flag_lebanon, null, null, false, null, Constants.NOT_SET),
	LESOTHO(R.string.country_lesotho, "LS", R.drawable.flag_lesotho, null, null, false, null, Constants.NOT_SET),
	LIBERIA(R.string.country_liberia, "LR", R.drawable.flag_liberia, null, null, false, null, Constants.NOT_SET),
	LIBYA(R.string.country_libya, "LY", R.drawable.flag_libya, null, null, false, null, Constants.NOT_SET),
	LIECHTENSTEIN(R.string.country_liechtenstein, "LI", R.drawable.flag_liechtenstein, null, null, true, null, Constants.NOT_SET),
	LITHUANIA(R.string.country_lithuania, "LT", R.drawable.flag_lithuania, null, null, true, null, Constants.NOT_SET),
	LUXEMBOURG(R.string.country_luxembourg, "LU", R.drawable.flag_luxembourg, null, null, true, null, Constants.NOT_SET),
	MACAO(R.string.country_macao, "MO", R.drawable.flag_macao, null, null, false, null, Constants.NOT_SET),
	MACEDONIA(R.string.country_macedonia, "MK", R.drawable.flag_macedonia, "mk", null, false, null, Constants.NOT_SET),
	MADAGASCAR(R.string.country_madagascar, "MG", R.drawable.flag_madagascar, null, null, false, null, Constants.NOT_SET),
	MALAWI(R.string.country_malawi, "MW", R.drawable.flag_malawi, null, null, false, null, Constants.NOT_SET),
	MALAYSIA(R.string.country_malaysia, "MY", R.drawable.flag_malaysia, null, null, false, null, Constants.NOT_SET),
	MALDIVES(R.string.country_maldives, "MV", R.drawable.flag_maldives, null, null, false, null, Constants.NOT_SET),
	MALI(R.string.country_mali, "ML", R.drawable.flag_mali, null, null, false, null, Constants.NOT_SET),
	MALTA(R.string.country_malta, "MT", R.drawable.flag_malta, null, null, false, null, Constants.NOT_SET),
	MARSHALLISLANDS(R.string.country_marshallislands, "MH", R.drawable.flag_marshallislands, null, null, false, null, Constants.NOT_SET),
	//	MARTINIQUE(R.string.country_martinique, "MQ", R.drawable.flag_martinique, false, null),
	MAURITANIA(R.string.country_mauritania, "MR", R.drawable.flag_mauritania, null, null, false, null, Constants.NOT_SET),
	MAURITIUS(R.string.country_mauritius, "MU", R.drawable.flag_mauritius, null, null, false, null, Constants.NOT_SET),
	//	MAYOTTE(R.string.country_mayotte, "YT", R.drawable.flag_mayotte, false, null),
	MEXICO(R.string.country_mexico, "MX", R.drawable.flag_mexico, null, null, false, null, Constants.NOT_SET),
	MIRCONESIAFEDERATEDSTATES(R.string.country_mirconesiafederatedstates, "FM", R.drawable.flag_micronesia, null, null, false, null, Constants.NOT_SET),
	MOLDOVA(R.string.country_moldova, "MD", R.drawable.flag_moldova, null, null, true, null, Constants.NOT_SET),
	MONACO(R.string.country_monaco, "MC", R.drawable.flag_monaco, null, null, false, null, Constants.NOT_SET),
	MONGOLIA(R.string.country_mongolia, "MN", R.drawable.flag_mongolia, null, null, false, null, Constants.NOT_SET),
	MONTENEGRO(R.string.country_montenegro, "ME", R.drawable.flag_montenegro, null, null, true, null, Constants.NOT_SET),
	MONTSERRAT(R.string.country_montserrat, "MS", R.drawable.flag_montserrat, null, null, false, null, Constants.NOT_SET),
	MOROCCO(R.string.country_morocco, "MA", R.drawable.flag_morocco, null, null, false, null, Constants.NOT_SET),
	MOZABIQUE(R.string.country_mozabique, "MZ", R.drawable.flag_mozambique, null, null, false, null, Constants.NOT_SET),
	MYANMAR(R.string.country_myanmar, "MM", R.drawable.flag_myanmar, null, null, false, null, Constants.NOT_SET),
	NAMIBIA(R.string.country_namibia, "NA", R.drawable.flag_namibia, null, null, false, null, Constants.NOT_SET),
	NAURU(R.string.country_nauru, "NR", R.drawable.flag_nauru, null, null, false, null, Constants.NOT_SET),
	NEPAL(R.string.country_nepal, "NP", R.drawable.flag_nepal, null, null, false, null, Constants.NOT_SET),
	NETHERLANDS(R.string.country_netherlands, "NL", R.drawable.flag_netherlands, "nl", null, true, new DirectionsLanguage[]{DirectionsLanguage.DUTCH}, Constants.NOT_SET),
	NETHERLANDSANTILLEA(R.string.country_netherlandsantillea, "AN", R.drawable.flag_netherlandsantilles, null, null, false, null, Constants.NOT_SET),
	//	NEWCALEDONIA(R.string.country_newcaledonia, "NC", R.drawable.flag_caledonianew, false, null),
	NEWZEALAND(R.string.country_newzealand, "NZ", R.drawable.flag_newzealand, null, null, false, null, Constants.NOT_SET),
	NICARAGUA(R.string.country_nicaragua, "NI", R.drawable.flag_nicaragua, null, null, false, null, Constants.NOT_SET),
	NIGER(R.string.country_niger, "NE", R.drawable.flag_niger, null, null, false, null, Constants.NOT_SET),
	NIGERIA(R.string.country_nigeria, "NG", R.drawable.flag_nigeria, null, null, false, null, Constants.NOT_SET),
	//	NIUE(R.string.country_niue, "NU", R.drawable.flag_niue, false, null),
	//	NORFOLKISLAN(R.string.country_norfolkislan, "NF", R.drawable.flag_norfolkisland, false, null),
	//	NORTHERNMARIANAISLANDS(R.string.country_northernmarianaislands, "MP", R.drawable.flag_northernmarianaislands, false, null),
	NORWAY(R.string.country_norway, "NO", R.drawable.flag_norway, "no", null, true, null, Constants.NOT_SET),
	OMAN(R.string.country_oman, "OM", R.drawable.flag_oman, null, null, false, null, Constants.NOT_SET),
	PAKISTAN(R.string.country_pakistan, "PK", R.drawable.flag_pakistan, null, null, false, null, Constants.NOT_SET),
	PALAU(R.string.country_palau, "PW", R.drawable.flag_palau, null, null, false, null, Constants.NOT_SET),
	PALESTINE(R.string.country_palestine, "PS", R.drawable.flag_palestine, null, null, false, null, Constants.NOT_SET),
	PANAMA(R.string.country_panama, "PA", R.drawable.flag_panama, null, null, false, null, Constants.NOT_SET),
	PAPUANEWGUINEA(R.string.country_papuanewguinea, "PG", R.drawable.flag_papuanewguinea, null, null, false, null, Constants.NOT_SET),
	PARAGUAY(R.string.country_paraguay, "PY", R.drawable.flag_paraguay, null, null, false, null, Constants.NOT_SET),
	PERU(R.string.country_peru, "PE", R.drawable.flag_peru, null, null, false, null, Constants.NOT_SET),
	PHILIPPINES(R.string.country_philippines, "PH", R.drawable.flag_philippines, null, null, false, null, Constants.NOT_SET),
	//	PITCAIRN(R.string.country_pitcairn, "PN", R.drawable.flag_pitcairn, false, null),
	POLAND(R.string.country_poland, "PL", R.drawable.flag_poland, "pl", null, true, null, Constants.NOT_SET),
	PORTUGAL(R.string.country_portugal, "PT", R.drawable.flag_portugal, "pt", null, true, null, Constants.NOT_SET),
	PUERTORICO(R.string.country_puertorico, "PR", R.drawable.flag_puertorico, null, null, false, null, Constants.NOT_SET),
	QATAR(R.string.country_qatar, "QA", R.drawable.flag_qatar, null, null, false, null, Constants.NOT_SET),
	//	REUNION(R.string.country_REUNION, "RE", R.drawable.flag_reunion, false, null),
	ROMANIA(R.string.country_romania, "RO", R.drawable.flag_romania, "ro", null, true, null, Constants.NOT_SET),
	RUSSIANFEDERATION(R.string.country_russianfederation , "RU", R.drawable.flag_russianfederation, "ru", null, false, null, Constants.NOT_SET),
	RWANDA(R.string.country_rwanda, "RW", R.drawable.flag_rwanda, null, null, false, null, Constants.NOT_SET),
	//	SAINTBARTHELEMY(R.string.country_saintbarthelemy, "BL", R.drawable.flag_saintbarthemelmy, false, null),
	//	SAINTHELENA("Saint Helena", "SH", R.drawable.flag_sainthelena, false, null),
	//	SAINTKITTSANDNEVIS(R.string.country_saintkittsandnevis, "KN", R.drawable.flag_saintkittsadnevis, false, null),
	SAINTLUCIA(R.string.country_saintlucia, "LC", R.drawable.flag_saintlucia, null, null, false, null, Constants.NOT_SET),
	//	SAINTMARTIN(R.string.country_saintmartin, "MF", R.drawable.flag_saintmartin, false, null),
	//	SAINTPIERREANDMIQUELON(R.string.country_saintpierreandmiquelon, "PM", R.drawable.flag_saintpierreandmiquelon, false, null),
	//	SAINTVINCENTANDTHEGRENADINES(R.string.country_saintvincentandthegrenadines, "VC", R.drawable.flag_saintvincentandthegrenadines, false, null),
	SAMOA(R.string.country_samoa, "WS", R.drawable.flag_samoa, null, null, false, null, Constants.NOT_SET),
	SANMARINO(R.string.country_sanmarino, "SM", R.drawable.flag_sanmarino, null, null, false, null, Constants.NOT_SET),
	SAOTOMEANDPRINCIPE(R.string.country_saotomeandprincipe, "ST", R.drawable.flag_saotomeandprincipe, null, null, false, null, Constants.NOT_SET),
	SAUDIARABIA(R.string.country_saudiarabia, "SA", R.drawable.flag_saudiarabia, null, null, false, null, Constants.NOT_SET),
	SENEGAL(R.string.country_senegal, "SN", R.drawable.flag_senegal, null, null, false, null, Constants.NOT_SET),
	SERBIA(R.string.country_serbia, "RS", R.drawable.flag_serbia, "sr", null, true, null, Constants.NOT_SET),
	SEYCHELLES(R.string.country_seychelles, "SC", R.drawable.flag_seychelles, null, null, false, null, Constants.NOT_SET),
	SIERRALEONE(R.string.country_sierraleone, "SL", R.drawable.flag_sierraleone, null, null, false, null, Constants.NOT_SET),
	SINGAPORE(R.string.country_singapore, "SG", R.drawable.flag_singapore, null, null, false, null, Constants.NOT_SET),
	SLOVAKIA(R.string.country_slovakia, "SK", R.drawable.flag_slovakia, "sk", null, false, null, Constants.NOT_SET),
	SLOVENIA(R.string.country_slovenia, "SI", R.drawable.flag_slovenia, null, null, false, null, Constants.NOT_SET),
	SOLOMONISLANDS(R.string.country_solomonislands, "SB", R.drawable.flag_solomonislands, null, null, false, null, Constants.NOT_SET),
	SOMALIA(R.string.country_somalia, "SO", R.drawable.flag_somalia, null, null, false, null, Constants.NOT_SET),
	SOUTHAFRICA(R.string.country_southafrica, "ZA", R.drawable.flag_southafrica, null, null, false, null, Constants.NOT_SET),
	//	SOUTHGEORGIAANDTHESOUTHSANDWICHISLANDS (R.string.country_southgeorgiaandthesouthsandwichislands, "GS", R.drawable.flag_southgeorgiaandthesouthsandwichislands, false, null),
	SPAIN(R.string.country_spain, "ES", R.drawable.flag_spain, "es", null, true, new DirectionsLanguage[]{DirectionsLanguage.SPANISH}, Constants.NOT_SET),
	SRILANKA(R.string.country_srilanka, "LK", R.drawable.flag_srilanka, null, null, false, null, Constants.NOT_SET),
	SUDAN(R.string.country_sudan, "SD", R.drawable.flag_sudan, null, null, false, null, Constants.NOT_SET),
	SURINAME(R.string.country_suriname, "SR", R.drawable.flag_suriname, null, null, false, null, Constants.NOT_SET),
	//	SVALBRANDANDJANMAYEN(R.string.country_svalbrandandjanmayen, "SJ", R.drawable.flag_svalbrandandjanmayen, false, null),
	SWAZILAND(R.string.country_swaziland, "SZ", R.drawable.flag_swaziland, "sw", null, false, null, Constants.NOT_SET),
	SWEDEN(R.string.country_sweden, "SE", R.drawable.flag_sweden, "sv", null, true, new DirectionsLanguage[]{DirectionsLanguage.SWEDISH}, Constants.NOT_SET),
	SWITZERLAND(R.string.country_switzerland, "CH", R.drawable.flag_switzerland, null, null, true, null, Constants.NOT_SET),
	SYRIA(R.string.country_syria, "SY", R.drawable.flag_syria, null, null, false, null, Constants.NOT_SET),
	TAIWAN(R.string.country_taiwan, "TW", R.drawable.flag_taiwan, null, null, false, null, Constants.NOT_SET),
	TAJIKISTAN(R.string.country_tajikistan, "TJ", R.drawable.flag_tajikistan, null, null, false, null, Constants.NOT_SET),
	TANZANIA(R.string.country_tanzania, "TZ", R.drawable.flag_tanzania, null, null, false, null, Constants.NOT_SET),
	THAILAND(R.string.country_thailand, "TH", R.drawable.flag_thailand, null, null, false, null, Constants.NOT_SET),
	TIMORLESTE(R.string.country_timorleste, "TL", R.drawable.flag_timorleste, null, null, false, null, Constants.NOT_SET),
	TOGO(R.string.country_togo, "TG", R.drawable.flag_togo, null, null, false, null, Constants.NOT_SET),
	//	TOKELAU(R.string.country_tokelau, "TK", R.drawable.flag_tokelau, false, null),
	TONGA(R.string.country_tonga, "TO", R.drawable.flag_tonga, null, null, false, null, Constants.NOT_SET),
	TRINIDADANDTOBAGO(R.string.country_trinidadandtobago, "TT", R.drawable.flag_trinidadandtobago, null, null, false, null, Constants.NOT_SET),
	TUNISIA(R.string.country_tunisia, "TN", R.drawable.flag_tunisia, null, null, false, null, Constants.NOT_SET),
	TURKEY(R.string.country_turkey, "TR", R.drawable.flag_turkey, "tr", null, true, null, Constants.NOT_SET),
	TURKMENISTAN(R.string.country_turkmenistan, "TM", R.drawable.flag_turkmenistan, null, null, false, null, Constants.NOT_SET),
	TURKSANDCAICOSISLANDS(R.string.country_turksandcaicosislands, "TC", R.drawable.flag_turksandcaicosislands, null, null, false, null, Constants.NOT_SET),
	TUVALU(R.string.country_tuvalu, "TV", R.drawable.flag_tuvalu, null, null, false, null, Constants.NOT_SET),
	UGANDA(R.string.country_uganda, "UG", R.drawable.flag_uganda, null, null, false, null, Constants.NOT_SET),
	UKRAINE(R.string.country_ukraine, "UA", R.drawable.flag_ukraine, null, null, true, null, Constants.NOT_SET),
	UNITEDARABEMIRATES(R.string.country_unitedarabemirates, "AE", R.drawable.flag_unitedarabemirates, null, null, false, null, Constants.NOT_SET),
	UNITEDKINGDOM(R.string.country_unitedkingdom, "UK", R.drawable.flag_unitedkingdom, "en-gb", new BoundingBoxE6(60.6311,1.7425,49.955269,-8.164723), true, new DirectionsLanguage[]{DirectionsLanguage.ENGLISH}, Constants.NOT_SET),
	USA(R.string.country_usa, "US", R.drawable.flag_usa, "en-us", null, true, new DirectionsLanguage[]{DirectionsLanguage.ENGLISH}, Constants.NOT_SET),
	URUGUAY(R.string.country_uruguay, "UY", R.drawable.flag_uruguay, null, null, false, null, Constants.NOT_SET),
	UZBEKISTAN(R.string.country_uzbekistan, "UZ", R.drawable.flag_uzbekistan, null, null, false, null, Constants.NOT_SET),
	VANUATU(R.string.country_vanuatu, "VU", R.drawable.flag_vanuatu, null, null, false, null, Constants.NOT_SET),
	VATICAN(R.string.country_vatican, "VA", R.drawable.flag_vaticancity, null, null, false, null, Constants.NOT_SET),
	VENEZUELA(R.string.country_venezuela, "VE", R.drawable.flag_venezuela, null, null, false, null, Constants.NOT_SET),
	VIETNAM(R.string.country_vietnam, "VN", R.drawable.flag_vietnam, "vi", null, false, null, Constants.NOT_SET),
	VIRGINISLANDSBRITISH(R.string.country_virginislandsbritish, "VG", R.drawable.flag_virginislandsbritish, null, null, false, null, Constants.NOT_SET),
	VIRGINISLANDSUS(R.string.country_virginislandsus, "VI", R.drawable.flag_virginislandsus, null, null, false, null, Constants.NOT_SET),
	//	WALLISANDFUTUNA(R.string.country_wallisandfutuna, "WF", R.drawable.flag_vallisandfutuna, false, null),
	WESTERNSAHARAV(R.string.country_westernsaharav, "EH", R.drawable.flag_westernsahara, null, null, false, null, Constants.NOT_SET),
	YEMEN(R.string.country_yemen, "YE", R.drawable.flag_yemen, null, null, false, null, Constants.NOT_SET),
	ZAMBIA(R.string.country_zambia, "ZM", R.drawable.flag_zambia, null, null, false, null, Constants.NOT_SET),
	ZIMBABWE(R.string.country_zimbabwe, "ZW", R.drawable.flag_zimbabwe, null, null, false, null, Constants.NOT_SET),

	/* US-States. */
	//	ARIZONA(R.string.state_us_arizona, "US", R.drawable.flag_arizona, null, null, true, null, Constants.NOT_SET),
	//	CALIFORNIA(R.string.state_us_california, "US", R.drawable.flag_california, null, null, true, null, Constants.NOT_SET),
	//	FLORIDA(R.string.state_us_florida, "US", R.drawable.flag_florida, null, null, true, null, Constants.NOT_SET),
	//	NEWYORK(R.string.state_us_newyork, "US", R.drawable.flag_newyork, null, null, true, null, Constants.NOT_SET),
	//	WASHINGTON(R.string.state_us_washington, "US", R.drawable.flag_washington, null, null, true, null, Constants.NOT_SET),

	/* Special. */
	EUROPEANUNION(R.string.country_europeanunion, "EU", R.drawable.flag_europeanunion, null, null, false, null, Constants.NOT_SET);
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	public final String COUNTRYCODE;
	public final int FLAGRESID, NAMERESID;
	public final boolean HASDRIVINGDIECTIONS;
	private final DirectionsLanguage[] DRIVINGDIECTIONSLANGUAGE;
	public final String IETFLANGUAGETAG;
	public final BoundingBoxE6 BOUNDINGBOXE6;
	public final int VRPTABLEID;

	// ===========================================================
	// Constructors
	// ===========================================================

	private Country(final int pNameResID, final String pAbbreviation, final int pFlagResID, final String pIETFLanguageTag, final BoundingBoxE6 pBoundingBoxE6, final boolean pHasDrivingDirections, final DirectionsLanguage[] pDrivingDirectionsLanguage, final int pVRPTableID){
		this.NAMERESID = pNameResID;
		this.COUNTRYCODE = pAbbreviation;
		this.FLAGRESID = pFlagResID;
		this.HASDRIVINGDIECTIONS = pHasDrivingDirections;
		this.DRIVINGDIECTIONSLANGUAGE = pDrivingDirectionsLanguage;
		this.IETFLANGUAGETAG = pIETFLanguageTag;
		this.BOUNDINGBOXE6 = pBoundingBoxE6;
		this.VRPTABLEID = pVRPTableID;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public boolean hasDrivingDirectionsLanguage(){
		return this.DRIVINGDIECTIONSLANGUAGE != null;
	}

	private boolean hasDrivingDirections() {
		return this.HASDRIVINGDIECTIONS;
	}

	public boolean hasVRPTableID(){
		return this.VRPTABLEID != Constants.NOT_SET;
	}

	public int getVRPTableID(){
		return this.VRPTABLEID;
	}

	public String getVRPCountrySignName(){
		return VRPRegistry.getCountrySignName(this);
	}

	public boolean hasDialect(final DirectionsLanguage directionsLanguage){
		if(this.DRIVINGDIECTIONSLANGUAGE == null) {
			return false;
		}


		for(final DirectionsLanguage d : this.DRIVINGDIECTIONSLANGUAGE) {
			if(d == directionsLanguage) {
				return true;
			}
		}

		return false;
	}

	public static ArrayList<Country> getAllWithDrivingDirections(){
		final ArrayList<Country> out = new ArrayList<Country>();
		for(final Country n : Country.values()) {
			if(n.hasDrivingDirections()) {
				out.add(n);
			}
		}
		return out;
	}

	public static ArrayList<Country> getAllWithDrivingDirectionsLanguage(){
		final ArrayList<Country> out = new ArrayList<Country>();
		for(final Country n : Country.values()) {
			if(n.hasDrivingDirectionsLanguage()) {
				out.add(n);
			}
		}
		return out;
	}

	public static ArrayList<Country> getAllWithVRPTable(){
		final ArrayList<Country> out = new ArrayList<Country>();
		for(final Country n : Country.values()) {
			if(n.hasVRPTableID()) {
				out.add(n);
			}
		}
		return out;
	}

	public static Country fromDialect(final DirectionsLanguage directionsLanguage) {
		if(directionsLanguage == null) {
			return null;
		}

		for(final Country n : Country.values()) {
			if(n.hasDialect(directionsLanguage)) {
				return n;
			}
		}

		Exceptor.d("Dialect not found", new IllegalArgumentException("Dialect: '" + directionsLanguage + "' not found."));
		return Country.UNKNOWN;
	}

	public static Country fromAbbreviation(final String abbreviation) {
		if(abbreviation == null) {
			return null;
		}

		for(final Country n : Country.values()) {
			if(n.COUNTRYCODE.compareToIgnoreCase(abbreviation) == 0) {
				return n;
			}
		}

		Exceptor.d("Abbreviation not found", new IllegalArgumentException("Abbreviation: '" + abbreviation + "' not found."));
		return Country.UNKNOWN;
	}

	public DirectionsLanguage[] getDrivingDiectionsLanguages() {
		if(this.DRIVINGDIECTIONSLANGUAGE == null) {
			return new DirectionsLanguage[]{DirectionsLanguage.ENGLISH};
		} else {
			return this.DRIVINGDIECTIONSLANGUAGE;
		}
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public String toString() {
		return Capitalizer.capitalizeAllWords(name());
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	// ===========================================================
	// Parcelable
	// ===========================================================

	public static final Parcelable.Creator<Country> CREATOR = new Parcelable.Creator<Country>() {
		public Country createFromParcel(final Parcel in) {
			return readFromParcel(in);
		}

		public Country[] newArray(final int size) {
			return new Country[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(final Parcel out, final int flags) {
		out.writeInt(this.ordinal());
	}

	private static Country readFromParcel(final Parcel in){
		return values()[in.readInt()];
	}
}