/**
* @version 2.0
*/

package cliffWalking;

import java.util.ArrayList;

public class Agent2 {

	public Agent2( Environment2 e ) {
		environment = e;
		startingState = environment.getStartingState();
		sameMoveSequence = 0;
		previousMovesList = new ArrayList <Integer>();
		newMovesList = new ArrayList <Integer>();
		q = new double[ environment.getNumberOfStates() ][ Environment2.MAX_ACTION ];
		n = new int[ environment.getNumberOfStates() ][ Environment2.MAX_ACTION ];
	}

	public double getStateValue( int state ) {
		double result = -Environment2.INF;
		ArrayList <Integer> movesList = environment.getMoveList( state );
		for ( int i = 0; i < movesList.size(); i++ )
			result = max( result, q[ state ][ movesList.get(i) ] );
		return result;
	}

	public void qLearning() {
		episode = 0;
		sameMoveSequence = 0;
		previousMovesList.clear();
		while ( true ) {
			episode++;
			newMovesList.clear();
			qLearningEpisode( startingState );
			if ( sameMoveSequence == FINISH_LEARNING ) {
				System.out.println( "Episode = " + episode );
				for ( int i = 0; i < previousMovesList.size(); i++ )
					System.out.print( previousMovesList.get(i) );
				System.out.println();
				break;
			}
		}
	}

	/********************************
	*          PRIVATE              *
	*********************************/

	private double expectedNewState( int state ) {
		double result = -Environment2.INF;
		ArrayList <Integer> movesList = environment.getMoveList( state );
		for ( int i = 0; i < movesList.size(); i++ ) {
			//System.out.println( state/12 + " " + state%12 + " " + movesList.get(i) );
			result = max( result, f( q[ state ][ movesList.get(i) ], n[ state ][ movesList.get(i) ] ) );
		}
		return result;
	}

	private double max( double a, double b ) {
		if ( a > b ) return a;
		return b;
	}

	private double f( double u, int n ) {
		return u + EXPLORATION_VALUE / ( n + 1 );
	}

	private void qLearningEpisode( int state ) {
		ArrayList <Integer> movesList = environment.getMoveList( state );
		int action = movesList.get(0);
		for ( int i = 0; i < movesList.size(); i++ )
			if ( q[ state ][ action ] < q[ state ][ movesList.get(i) ] ) 
				action = movesList.get(i);
		int newState = environment.getNewState( state, action );
		//printQTable();
		n[ state ][ action ]++;
		if ( environment.checkTerminal( state ) )
			for ( int i = 0; i < movesList.size(); i++ ) {
				int a = movesList.get(i);
				q[ state ][ action ] = q[ state ][ action ] +  ALPHA * ( environment.getReward( state ) - q[ state ][ action ] );	
			} 
		else {
			newMovesList.add( action );
			q[ state ][ action ] = q[ state ][ action ] + ALPHA * ( environment.getReward( state ) + GAMMA * expectedNewState( newState ) - q[ state ][ action ] );
		}
		if ( environment.checkTerminal( state ) ) {
			if ( previousMovesList.equals( newMovesList ) ) 
				sameMoveSequence++;
			else {
				sameMoveSequence = 0;
				previousMovesList = new ArrayList <Integer> ( newMovesList );
			}
			return;
		}
		qLearningEpisode( newState );
	}

	private Environment2 environment;
	private int episode;
	private double q[][];
	private int n[][];
	private int startingState;
	private ArrayList <Integer> previousMovesList, newMovesList;
	private int sameMoveSequence;

	private static final double ALPHA = 0.1;
	private static final double GAMMA = 0.8;
	private static final double EXPLORATION_VALUE = 0.9;
	private static final int FINISH_LEARNING = 1000;
}