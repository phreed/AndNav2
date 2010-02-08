// Created by plusminus on 21:33:07 - 15.05.2008
package org.andnav2.adt.keyboardlayouts;


public class KeyBoardLayoutImpls {
	// ===========================================================
	// Methods
	// ===========================================================

	public static AbstractKeyBoardLayout getNumberedVersion(final AbstractKeyBoardLayout aLayout){

		if(aLayout instanceof ABCKeyboardLayout) {
			return new ABC123KeyboardLayout();
		} else if(aLayout instanceof QWERTYKeyBoardLayout) {
			return new QWERTY123KeyBoardLayout();
		} else if(aLayout instanceof QWERTZKeyBoardLayout) {
			return new QWERTZ123KeyBoardLayout();
		} else if(aLayout instanceof CyrillicKeyBoardLayout) {
			return new Cyrillic123KeyBoardLayout();
		} else if(aLayout instanceof ABC123KeyboardLayout) {
			return aLayout;
		} else if(aLayout instanceof QWERTY123KeyBoardLayout) {
			return aLayout;
		} else if(aLayout instanceof QWERTZ123KeyBoardLayout) {
			return aLayout;
		} else if(aLayout instanceof Cyrillic123KeyBoardLayout) {
			return aLayout;
		}

		throw new IllegalArgumentException();
	}


	// ===========================================================
	// Without numbers
	// ===========================================================

	public static final class ABCKeyboardLayout extends AutoFitKeyBoardLayout{
		public static final int ID = 1;
		public ABCKeyboardLayout(){
			super("ABCDEFGHIJKLMNOPQRSTUVWXYZ -"  + BUTTONGRID_BACKCAPTION);
			super.id = ID;
		}
	}

	public static final class QWERTYKeyBoardLayout extends FixedWidthKeyBoardLayout{
		public static final int ID = 2;
		public QWERTYKeyBoardLayout(){
			super(new String[]{
					"QWERTYUIOP ",
					"ASDFGHJKL ",
					"ZXCVBNM -"  + BUTTONGRID_BACKCAPTION
			},10);
			super.id = ID;
		}
	}

	public static final class QWERTZKeyBoardLayout extends FixedWidthKeyBoardLayout{
		public static final int ID = 3;
		public QWERTZKeyBoardLayout(){
			super(new String[]{
					"QWERTZUIOPÜ",
					"ASDFGHJKLÖÄ",
					"YXCVBNMß -" + BUTTONGRID_BACKCAPTION
			},11);
			super.id = ID;
		}
	}

	public static final class CyrillicKeyBoardLayout extends FixedWidthKeyBoardLayout{
		public static final int ID = 4;
		public CyrillicKeyBoardLayout(){
			super(new String[]{
					"\u0410\u0411\u0412\u0413\u0414\u0415\u0401\u0416\u0417\u0418\u0419 ",
					"\u041a\u041b\u041c\u041d\u041e\u041f\u0420\u0421\u0422\u0423\u0424 ",
					"\u0425\u0426\u0427\u0428\u0429\u042a\u042b\u042c\u042d\u042e\u042f" + BUTTONGRID_BACKCAPTION
			},12);
			super.id = ID;
		}
	}

	// ===========================================================
	// With numbers
	// ===========================================================

	public static final class ABC123KeyboardLayout extends AutoFitKeyBoardLayout{
		public static final int ID = ABCKeyboardLayout.ID + 10;
		public ABC123KeyboardLayout(){
			super("1234567890" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ -"  + BUTTONGRID_BACKCAPTION);
			super.id = ID;
		}
	}

	public static final class QWERTY123KeyBoardLayout extends FixedWidthKeyBoardLayout{
		public static final int ID = QWERTYKeyBoardLayout.ID + 10;
		public QWERTY123KeyBoardLayout(){
			super(new String[]{
					"1234567890",
					"QWERTYUIOP ",
					"ASDFGHJKL ",
					"ZXCVBNM -"  + BUTTONGRID_BACKCAPTION
			},10);
			super.id = ID;
		}
	}

	public static final class QWERTZ123KeyBoardLayout extends FixedWidthKeyBoardLayout{
		public static final int ID = QWERTZKeyBoardLayout.ID + 10;
		public QWERTZ123KeyBoardLayout(){
			super(new String[]{"1234567890ß", "QWERTZUIOPÜ","ASDFGHJKLÖÄ", "YXCVBNM  -" + BUTTONGRID_BACKCAPTION},11);
			super.id = ID;
		}
	}

	public static final class Cyrillic123KeyBoardLayout extends FixedWidthKeyBoardLayout{
		public static final int ID = CyrillicKeyBoardLayout.ID + 10;
		public Cyrillic123KeyBoardLayout(){
			super(new String[]{
					"1234567890  ",
					"\u0410\u0411\u0412\u0413\u0414\u0415\u0401\u0416\u0417\u0418\u0419 ",
					"\u041a\u041b\u041c\u041d\u041e\u041f\u0420\u0421\u0422\u0423\u0424 ",
					"\u0425\u0426\u0427\u0428\u0429\u042a\u042b\u042c\u042d\u042e\u042f" + BUTTONGRID_BACKCAPTION},12);
			super.id = ID;
		}
	}
}