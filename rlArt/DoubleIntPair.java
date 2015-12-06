package rlArt;

public class DoubleIntPair implements Comparable <DoubleIntPair> {

	public DoubleIntPair() {
		value = -1;
		index = -1;
	}

	public DoubleIntPair( double val, int idx ) {
		value = val;
		index = idx;
	}

	public double getVal() {
		return value;
	}

	public int getCategory() {
		return index;
	}

	public void setVal( double val ) {
		value = val;
	}

	public void setCategory( int j ) {
		index = j;
	}

	public void reset() {
		value = -1;
	}

	@Override
	public int compareTo( DoubleIntPair other ) {
		if ( other.value > this.value ) return 1;
		if ( other.value < this.value ) return -1;
		return 0;
	}

	private double value;
	private int index;
}