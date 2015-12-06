package cliffWalking;

import java.util.ArrayList;

public class Agent {

	public static final int inf = 1000111000;
	public static final int MOVE_UP = 0;
	public static final int MOVE_DOWN = 1;
	public static final int MOVE_LEFT = 2;
	public static final int MOVE_RIGHT = 3;
	public static final int MAX_ACTION = 4;

	public Agent() {
		environment = new Environment();
		initialize( environment );
	}

	public Agent( Environment environment ) {
		initialize( environment );
	}

	public void qLearning() {
		episode = 0;
		sameMoves = 0;
		while ( true ) {
			episode++;
			newMovesList.clear();
			qLearningEpisode( environment.getStartingState() );
			if ( sameMoves == 100 ) {
				System.out.println( "Episode = " + episode );
				System.out.print( "Move list = " );
				for ( int i = 0; i < previousMovesList.size(); i++ )
					System.out.print( previousMovesList.get(i) + " " );
				System.out.println();
				printQTable();
				return;
			}
		}
	}
	
	/*********************************
	*            PRIVATE             *
	**********************************/

	private void initialize( Environment environment ) {
		previousMovesList = new ArrayList < Integer >();
		newMovesList = new ArrayList < Integer >();
		maxRow = environment.getMaxRow();
		maxCol = environment.getMaxCol();
		q = new double[ maxRow * maxCol ][ MAX_ACTION ];
		n = new int[ maxRow * maxCol ][ MAX_ACTION ];
	}

	private boolean legalAction( int action ) {
		int nextLocationRow = locationRow + MOVE_ROW[ action ];
		int nextLocationCol = locationCol + MOVE_COL[ action ];
		return ( nextLocationRow >= 0 && nextLocationRow < maxRow && nextLocationCol >= 0 && nextLocationCol < maxCol );
	}

	private double alpha( int state, int action ) {
		//double a = (1.0) / n[ state ][ action ];
		//if ( a > MIN_ALPHA ) return a;
		return MIN_ALPHA;
	}

	private int getState( int row, int col ) {
		return row * maxCol + col;
	}
	
	private void act( int action ) {
		newMovesList.add( action );
		locationRow += MOVE_ROW[ action ];
		locationCol += MOVE_COL[ action ];
	}

	private double f( double u, int n ) {
		return u + EXPLORATION_VALUE / ( n + 1);
	}

	private double maxExplorationQ( int state ) {
		double result = -inf;
		for ( int action = 0; action < MAX_ACTION; action++ )
			if ( legalAction( action ) ) {
				result = max( result, f(q[ state ][ action ], n[ state ][ action ] ) );
			}
		return result;
	}

	private double max( double a, double b ) {
		if ( a > b ) return a;
		return b;
	}

	private void qLearningEpisode( int state ) {
		locationRow = state / maxCol;
		locationCol = state % maxCol;
		int action = -1;
		for ( int a = 0; a < MAX_ACTION; a++ )
			if ( legalAction( a ) ) {
				action = a;
				break;
			}

		for ( int a = 0; a < MAX_ACTION; a++ )
			if ( legalAction(a) )
				if ( q[ state ][ a ] >= q[ state ][ action ] )
					action = a;
		n[ state ][ action ]++;
		int newState = -1;

		if ( !environment.checkTerminate( state ) ) {
			act( action );
			newState = getState( locationRow, locationCol );
		}
		
		if ( environment.checkTerminate( state ) ) {
			for ( int a = 0; a < MAX_ACTION; a++ )
				q[ state ][ a ] = q[ state ][ a ] + alpha( state, a ) * ( environment.getReward( state ) - q[ state ][ a ] );	
		} else {
			q[ state ][ action ] = q[ state ][ action ] + alpha( state, action ) * ( environment.getReward( state ) + GAMMA * maxExplorationQ( newState ) - q[ state ][ action ] );
		}

		if ( environment.checkTerminate( state ) ) {
			if ( newMovesList.equals( previousMovesList ) ) 
				sameMoves++;
			else {
				sameMoves = 0;
				previousMovesList = new ArrayList <Integer> ( newMovesList );
			}
			return;
		}
		qLearningEpisode( newState );
	}

	private void printQTable() {
		for ( int row = 0; row < maxRow; row++ ) {
			for ( int col = 0; col < maxCol; col++ ) {
				double maxVal = -inf;
				for ( int a = 0; a < MAX_ACTION; a++ )
					maxVal = max(maxVal, q[ getState( row, col ) ][ a ] );
				System.out.printf( "%5.1f", maxVal );
				System.out.print( "   " );
			}
			System.out.print( "\n" );
		}
		System.out.print( "\n\n\n\n" );
	}

	private ArrayList <Integer> previousMovesList , newMovesList;
	private int sameMoves;
	private int previousMoveNumbers, newMoveNumber;
	private Environment environment;
	private int maxCol, maxRow;
	private int locationRow;
	private int locationCol;
	private double q[][];
	private int n[][];
	private int episode;

	private static final double MIN_ALPHA = 0.1;
	private static final double GAMMA = 0.8;
	private static final double EXPLORATION_VALUE = 0.9;
	private static final int MOVE_ROW[] = { -1, +1, 0, 0 };
	private static final int MOVE_COL[] = { 0, 0, -1, +1 };
}