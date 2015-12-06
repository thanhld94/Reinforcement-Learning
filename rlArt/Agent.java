package rlArt;

import java.util.ArrayList;
import java.util.Random;

public class Agent {

	public static final int INF = 1000111000;

	public Agent() {
		resetPosition( 1, 1 );
	}

	public void resetPosition( int row, int col ) {
		locationRow = row;
		locationCol = col;
		terminated = false;
	}

	public int getLocationRow() {
		return locationRow;
	}

	public int getLocationCol() {
		return locationCol;
	}

	public boolean checkTerminated() {
		return terminated;
	}

	protected void setTerminate() {
		terminated = true;
	}

	protected void setLocationRow( int x ) {
		locationRow = x;
	}

	protected void setLocationCol( int y ) {
		locationCol = y;
	}

	public void actRandom( Environment environment ) {
		ArrayList <Integer> movesList = environment.getMovesList( locationRow, locationCol );
		Random rand = new Random();
		int action = rand.nextInt( movesList.size() );
		locationRow += Environment.MOVE_ROW[ movesList.get( action ) ];
		locationCol += Environment.MOVE_COL[ movesList.get( action ) ];
		//environment.agentMoved( movesList.get( action ), locationRow, locationCol );
		environment.nextMove( movesList.get( action ), locationRow, locationCol );
	}
	
	/*************************
	*        PRIVATE         *
	**************************/

	private boolean checkLegalAction( ArrayList <Integer> movesList, int action ) {
		for ( int i = 0; i < movesList.size(); i++ )
			if ( movesList.get( i ) == action ) 
				return true;
		return false;
	}

	private boolean terminated;
	private int locationRow, locationCol;

}