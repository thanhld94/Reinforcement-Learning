package rlArt;

import java.util.ArrayList;
import java.util.Collections;

public class Art {

	public static final Double RO = 0.9;
	public static final Double ALPHA = 0.1;
	
	public Art( int vSize ) {
		vectorSize = vSize;
		noCategories = 0;
		weight = new ArrayList < Double[] >();
		uncommitedNode = new ArrayList <Boolean>();
		choiceVector = new ArrayList <DoubleIntPair> ();
		addUncommitedNode();
	}

	public int learn( Double[] normalizedInput ) {
		calChoiceVector( normalizedInput );
		while ( true ) {
			/*System.out.print( "Input = " );
			for ( int i = 0; i < normalizedInput.length; i++ )
				System.out.printf( "%5.2f ", normalizedInput[ i ] );
			System.out.println();*/

			Collections.sort( choiceVector );
			int category = choiceVector.get( 0 ).getCategory();
			if ( vigilanceTest( normalizedInput, weight.get( category ) ) >= RO ) {
				if ( uncommitedNode.get( category ) ) {
					uncommitedNode.set( category, false );
					addUncommitedNode();
				}
				weight.set( category, fuzzyAnd( weight.get( category ), normalizedInput ) );
				//System.out.println( "-> Category = " + category );
				return category;
			} else {
				choiceVector.get( 0 ).reset();
			}
		}
	}

	/***************************
	*          PRIVATE         *  
	****************************/

	private Double vigilanceTest( Double[] i, Double[] w ) {
		Double result = l1Norm( fuzzyAnd( i, w ) ) / l1Norm( i );
		return result;
	}

	private void calChoiceVector( Double[] input ) {
		for ( int j = 0; j < noCategories; j++ ) {
			choiceVector.get( j ).setCategory( j );
			if ( uncommitedNode.get( j ) )
				choiceVector.get( j ).setVal( (1.0) * vectorSize / ( ALPHA + 2 * vectorSize ) );
			else
				choiceVector.get( j ).setVal( l1Norm( fuzzyAnd( input, weight.get( j ) ) ) / ( ALPHA + l1Norm( weight.get( j ) ) ) );
		}
	}

	private Double l1Norm( Double[] vector ) {
		Double result = 0.0;
		for ( int i = 0; i < vector.length; i++ )
			result += vector[ i ];
		return result;
	}

	private Double[] fuzzyAnd( Double[] v1, Double[] v2 ) {
		//System.out.println( "Fuzzy, length = " + v1.length + " " + v2.length );
		Double[] result = new Double[ v1.length ];
		for ( int i = 0; i < result.length; i++ ) 
			result[ i ] = min( v1[ i ], v2[ i ] );
		return result;
	}

	private Double min( Double a, Double b ) {
		if ( a < b ) return a;
		return b;
	}

	private void addUncommitedNode() {
		noCategories++;
		Double[] w = new Double[ vectorSize ];
		uncommitedNode.add( true );
		choiceVector.add( new DoubleIntPair() );
		for ( int i = 0; i < vectorSize; i++ ) 
			w[ i ] = 1.0;
		weight.add( w );
	}

	private int noCategories;
	private int vectorSize;
	private ArrayList <Double[]> weight;
	private ArrayList <Boolean> uncommitedNode; 
	private ArrayList <DoubleIntPair> choiceVector;
}