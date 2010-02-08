package org.andnav2.osm.util;

public class ValuePair implements Comparable<ValuePair>{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	final int a,b;

	// ===========================================================
	// Constructors
	// ===========================================================

	public ValuePair(final int[] reuse) {
		this.a = reuse[0];
		this.b = reuse[1];
	}

	public ValuePair(final int pA, final int pB) {
		this.a = pA;
		this.b = pB;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public int[] toArray() {
		return new int[]{this.a,this.b};
	}

	public int getValueA() {
		return this.a;
	}

	public int getValueB() {
		return this.b;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public boolean equals(final Object o) {
		return o instanceof ValuePair
		&& this.a == ((ValuePair)o).a
		&& this.b == ((ValuePair)o).b;
	}

	public int compareTo(final ValuePair another) {
		if(this.a != another.a) {
			return this.a - another.a;
		} else if(this.b != another.b) {
			return this.b - another.b;
		} else {
			return 0;
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================
}
