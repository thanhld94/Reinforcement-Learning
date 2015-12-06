package rlArt;

import java.util.ArrayList;
import java.util.Random;

public class ReinforcementAgent extends Agent {

	public static final double ALPHA = 0.1;
	public static final double GAMMA = 0.9;
	public static final int INF = 1000111000;
	
	public ReinforcementAgent( Environment e ) {
		super();
		environment = e;
		statesVisited = 0;
		noStates = 0;
		qValue = new ArrayList < Double[]>();
		visited = new ArrayList <Boolean>();
	}

	public void reinforcementMove() {
		int state = environment.getState( getLocationRow(), getLocationCol() );

		if ( state >= noStates ) 
			addState();

		if ( checkTerminatedState( getLocationRow(), getLocationCol() ) ) {
			setTerminate();
			for ( int a = 0; a < Environment.MAX_MOVES; a++ )
				qValue.get( state )[ a ] += ALPHA * ( environment.reward( getLocationRow(), getLocationCol() ) - qValue.get( state )[ a ] );
		} else {
			int action = epsilonGreedy( state );
			//System.out.println( "DONE EPS GREEDY!" );
			int nrow = getLocationRow() + Environment.MOVE_ROW[ action ];
			int ncol = getLocationCol() + Environment.MOVE_COL[ action ];
			environment.nextMove( action, nrow, ncol );
			//System.out.println( "DONE ENV MOVE GHOSTS" );
			int newState = environment.getState( nrow, ncol );
			//System.out.println( "DONE GET NEW STATE" );
			if ( newState >= noStates )
				addState();
			if ( !visited.get( state ) ) {
				statesVisited++;
				visited.set( state, true );
			}
			qValue.get( state )[ action ] += ALPHA * ( environment.reward( nrow, ncol ) + GAMMA * bestNextAction( newState, nrow, ncol ) - qValue.get( state )[ action ] );
			//System.out.println( "Action = " + action );
			//System.out.println( "state = " + state + " -> newState = " + newState + "\n\n");
			setLocationRow( nrow );
			setLocationCol( ncol );
			//environment.agentMoved( action, nrow, ncol );
		}
	}

	/*************************
	*        PRIVATE         *
	**************************/

	private void addState() {
		noStates++;
		Double[] a = new Double[ Environment.MAX_MOVES ];
		for ( int i = 0; i < a.length; i++ )
			a[ i ] = 0.0;
		qValue.add( a );
		visited.add( false );
	}

	private double bestNextAction( int state, int row, int col ) {
		double result = -INF;
		ArrayList <Integer> movesList = environment.getMovesList( row, col );
		for ( int i = 0; i < movesList.size(); i++ ) 
			result = max( result, qValue.get( state )[ movesList.get( i ) ] );
		//System.out.println( "DONE BEST NEXT!" );
		return result;
	}

	private double max( double a, double b ) {
		if ( a < b )
			return b;
		return a;
	}

	private int epsilonGreedy( int state ) {
		Random rand = new Random();
		int coin = rand.nextInt( 1000 );
		ArrayList <Integer> movesList = environment.getMovesList( getLocationRow(), getLocationCol() );
		if ( coin == 0 ) {
			int tmp = rand.nextInt( movesList.size() );
			//System.out.println( "RANDOM! " + statesVisited );
			return movesList.get( tmp );
		}
		//System.out.println( "NORMAL! State = " + state );
		int action = movesList.get( 0 );
		for ( int i = 0; i < movesList.size(); i++ ) {
			//System.out.println( "state = " + state + " action = " + movesList.get( i ) + " value = " + qValue.get( state )[ movesList.get( i ) ] );
			if ( qValue.get( state )[ action ] < qValue.get( state )[ movesList.get( i ) ] )
				action = movesList.get( i );
		}
		return action;
	}

	private boolean checkTerminatedState( int row, int col ) {
		return environment.checkTerminatedState( row, col );
	}

	private int noStates;
	private Environment environment;
	private ArrayList <Double[]> qValue;
	private ArrayList <Boolean> visited;
	private int statesVisited;
}