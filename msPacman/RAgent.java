package msPacman;

import java.util.ArrayList;
import java.util.Random;

public class RAgent extends Agent {

	public static final int MAX_STATES = 66000; // 4^8 states
	public static final int INF = 1000111000;

	public RAgent( Board b ) {
		super();
		qValue = new double[ MAX_STATES ][2][2][2][2][2][ Board.MAX_MOVES ][ Board.MAX_MOVES + 1 ]; // maximum of 66k * 4 * 32 = 8.5M states, but we are not going to have that many states
		visited = new boolean[ MAX_STATES ];
		stateVisited = 0;
		board = b;
		debug_previousState = -1;
		previousAction = Board.MAX_MOVES;
	}

	public void reinforceMove() {
		int state = getState( getLocationRow(), getLocationCol() );
		int topLeft = checkGhostAt( getLocationRow(), getLocationCol(), TOP_LEFT );
		int topRight = checkGhostAt( getLocationRow(), getLocationCol(), TOP_RIGHT );
		int botRight = checkGhostAt( getLocationRow(), getLocationCol(), BOT_RIGHT );
		int botLeft = checkGhostAt( getLocationRow(), getLocationCol(), BOT_LEFT );
		int status = checkGhostAt( getLocationRow(), getLocationCol(), CENTER );
		
		if ( checkTerminalState( getLocationRow(), getLocationCol() ) ) {
			setTerminate();
			//System.out.println( "Pre-terminated state = " + debug_previousState );
			//System.out.println( "Pre-terminated action = " + previousAction );
			for ( int a = 0; a < Board.MAX_MOVES; a++ ) 
				qValue[ state ][ status ][ topLeft ][ topRight ][ botRight ][ botLeft ][ a ][ previousAction ] += ALPHA * ( board.reward( getLocationRow(), getLocationCol(), a, previousAction ) - qValue[ state ][ status ][ topLeft ][ topRight ][ botRight ][ botLeft ][ a ][ previousAction ] );
		} else {
			ArrayList <Integer> movesList = board.getMovesList( getLocationRow(), getLocationCol() );
			int action = epsilonGreedy( getLocationRow(), getLocationCol(), status, topLeft, topRight, botRight, botLeft, previousAction );
			int nrow = getLocationRow() + Board.MOVE_ROW[ action ];
			int ncol = getLocationCol() + Board.MOVE_COL[ action ];
			int newState = getState( nrow, ncol );

			board.moveGhosts( nrow, ncol );
			int newTopLeft = checkGhostAt( nrow, ncol, TOP_LEFT );
			int newTopRight = checkGhostAt( nrow, ncol, TOP_RIGHT );
			int newBotRight = checkGhostAt( nrow, ncol, BOT_RIGHT );
			int newBotLeft = checkGhostAt( nrow, ncol, BOT_LEFT );
			int newStatus = checkGhostAt( nrow, ncol, CENTER );

			if ( !visited[ state ] ) {
					stateVisited++;
					visited[ state ] = true;
					//System.out.println( "New state! Total states = " + stateVisited );
				}

			qValue[ state ][ status ][ topLeft ][ topRight ][ botRight ][ botLeft ][ action ][ previousAction ] += ALPHA * ( board.reward( nrow, ncol, action, previousAction ) + GAMMA * bestNextAction( nrow, ncol, newStatus, newTopLeft, newTopRight, newBotRight, newBotLeft, action ) - qValue[ state ][ status ][ topLeft ][ topRight ][ botRight ][ botLeft ][ action ][ previousAction ] );
			setLocationRow( nrow );
			setLocationCol( ncol );
			previousAction = action;
			debug_previousState = state;
			board.eatPoint( nrow, ncol );
			//System.out.println("DONE");
		}
	}
	
	/***************************
	*          PRIVATE         *  
	****************************/

	private int getState( int row, int col ) {
		int state = 0;
		for ( int idx = 0; idx < SENSOR_SIZE; idx++ ) {
			int nrow = row + SENSOR_ROW[ idx ];
			int ncol = col + SENSOR_COL[ idx ];
			int environmentSquare = Board.WALL_SQUARE;
			if ( nrow >= 0 && nrow < board.getNumberOfRows() && ncol >= 0 && ncol < board.getNumberOfCols() ) {
				if ( board.ghostAt( nrow, ncol ) ) 
					environmentSquare = Board.GHOST_SQUARE;
				else
					environmentSquare = ( board.getEnvironmentAt( nrow, ncol ) );
			}
			state += ( environmentSquare * pow4( idx ) );
			/*if ( idx%2 == 0 && ( environmentSquare == Board.WALL_SQUARE || environmentSquare == Board.GHOST_SQUARE ) ) {
				idx++;
				state += ( Board.WALL_SQUARE * pow4( idx ) );
			}*/
		}
		return state;
	}


	private int checkGhostAt( int row, int col, int corner ) {
		int nrow = row + CORNER_ROW[ corner ];
		int ncol = col + CORNER_COL[ corner ];
		if ( nrow < 0 || ncol < 0 || nrow >= board.getNumberOfRows() || ncol >= board.getNumberOfCols() )
			return 0;
		if ( board.ghostAt( nrow, ncol ) )
			return 1;
		return 0;
	}

	private boolean checkTerminalState( int row, int col ) {
		if ( board.ghostAt( row, col ) ) return true;
		if ( board.ateAllPoints() ) return true;
		return false;
	}

	private int epsilonGreedy( int row, int col, int status, int topLeft, int topRight, int botRight, int botLeft, int previousAction ) {
		int state = getState( row, col );
		Random rand = new Random();
		int coin = rand.nextInt( stateVisited / 10 + 1 );
		ArrayList <Integer> movesList = board.getMovesList( row, col );
		if ( coin == 0 ) {
			int tmp = rand.nextInt( movesList.size() );
			return movesList.get( tmp );
		}
		int action = movesList.get( 0 );
		for ( int i = 0; i < movesList.size(); i++ ) {
			if ( qValue[ state ][ status ][ topLeft ][ topRight ][ botRight ][ botLeft ][ action ][ previousAction ] < qValue[ state ][ status ][ topLeft ][ topRight ][ botRight ][ botLeft ][ movesList.get( i ) ][ previousAction ] )
				action = movesList.get( i );
		}
		//System.out.println( "Selected " + action );
		return action;
	}

	private double bestNextAction( int row, int col, int status, int topLeft, int topRight, int botRight, int botLeft, int previousAction ) {
		int state = getState( row, col );
		double result = -INF;
		ArrayList <Integer> movesList = board.getMovesList( row, col );
		for ( int i = 0; i < movesList.size(); i++ ) 
			result = max( result, qValue[ state ][ status ][ topLeft ][ topRight ][ botRight ][ botLeft ][ movesList.get( i ) ][ previousAction ] );
		return result;
	}

	private int pow4( int exp ) {
		if ( exp == 0 ) return 1;
		int result = 1;
		for ( int i = 1; i <= exp; i++ ) 
			result *= 4;
		return result;
	}

	private double max( double a, double b ) {
		if ( a > b ) return a;
		return b;
	}

	private int debug_previousState;
	private int previousAction;
	private Board board;
	private double qValue[][][][][][][][];
	private boolean visited[];
	private int stateVisited;
	private static final double ALPHA = 0.1;
	private static final double GAMMA = 0.9;

	private static final int TOP_LEFT = 0;
	private static final int TOP_RIGHT = 1;
	private static final int BOT_RIGHT = 2;
	private static final int BOT_LEFT = 3;
	private static final int CENTER = 4;

	private static final int CORNER_ROW[] = {-1,-1,1,1,0};
	private static final int CORNER_COL[] = {-1,1,1,-1,0};

	private static final int SENSOR_SIZE = 8;
	private static final int SENSOR_ROW[] = {-1,-2,0,0,0,0,1,2};
	private static final int SENSOR_COL[] = {0,0,-1,-2,1,2,0,0};
}