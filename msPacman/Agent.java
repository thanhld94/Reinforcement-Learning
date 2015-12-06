package msPacman;

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
		previousDirection = -1;
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

	public void actRandom( Board board ) {
		ArrayList <Integer> movesList = board.getMovesList( locationRow, locationCol );
		if ( previousDirection == -1 || movesList.size() > 2 || !checkLegalAction( movesList, previousDirection ) ) {
			Random rand = new Random();
			int action = rand.nextInt( Board.MAX_MOVES );
			boolean legalAction = false;
			while ( !checkLegalAction( movesList, action ) )
				action = rand.nextInt( Board.MAX_MOVES );
			previousDirection = action;
			locationRow += Board.MOVE_ROW[ action ];
			locationCol += Board.MOVE_COL[ action ];
		} else {
			locationRow += Board.MOVE_ROW[ previousDirection ];
			locationCol += Board.MOVE_COL[ previousDirection ]; 
		}

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
	private int previousDirection;
	private int locationRow, locationCol;

}