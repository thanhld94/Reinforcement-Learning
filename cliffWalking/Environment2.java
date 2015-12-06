/**
* @version 2.0
*/

package cliffWalking;

import java.util.ArrayList;

public class Environment2 {

	public static final int INF = 1000111000;
	public static final int MAX_ACTION = 7;

	public Environment2( int startState, int[][] rewardTable ) {
		numberOfRows = rewardTable.length;
		numberOfCols = rewardTable[0].length;
		startingRow = startState / numberOfCols;
		startingCol = startState % numberOfCols;
		reward = rewardTable;
		/*for ( int i = 0; i < rewardTable.length; i++ )
			for ( int j = 0; j < rewardTable[i].length; j++ ) {
				reward[ i ][ j ] = rewardTable[ i ][ j ];
				System.out.println( numberOfRows + " " + numberOfCols );
			}	
			*/
	}

	public int getNumberOfStates() {
		return numberOfRows * numberOfCols;
	}

	public int getStartingState() {
		return startingRow * numberOfCols + startingCol;
	}

	public int getNumberOfRows() {
		return numberOfRows;
	}

	public int getNumberOfCols() {
		return numberOfCols;
	}

	public int getNewState( int oldState, int action ) {
		int row = oldState / numberOfCols;
		int col = oldState % numberOfCols;
		row += MOVE_ROW[ action ];
		col += MOVE_COL[ action ];
		return row * numberOfCols + col;
	}

	public ArrayList <Integer> getMoveList( int state ) {
		ArrayList < Integer > moveList = new ArrayList < Integer >();
		int row = state / numberOfCols;
		int col = state % numberOfRows;
		for ( int a = 0; a < MAX_MOVES; a++ )
			if ( legalMove( row,col,a ) )
				moveList.add(a);
		return moveList;
	}

	public int getReward( int state ) {
		int row = state / numberOfCols;
		int col = state % numberOfCols;
		return reward[ row ][ col ];
	}

	public boolean checkTerminal( int state ) {
		int row = state / numberOfCols;
		int col = state % numberOfCols;
		if ( reward[ row ][ col ] == GOAL_REWARD ) return true;
		if ( reward[ row ][ col ] == CLIFF_PENALTY ) return true;
		return false;
	}

	/*********************************
	*           PRIVATE              *
	**********************************/

	private boolean legalMove( int row, int col, int action ) {
		int newRow = row + MOVE_ROW[ action ];
		int newCol = col + MOVE_COL[ action ];
		if ( newRow >= 0 && newCol >= 0 && newRow < numberOfRows && newCol < numberOfCols )
			return true;
		return false;
	}


	private int startingRow, startingCol;
	private int numberOfRows, numberOfCols;
	private int reward[][];

	private static final int MAX_MOVES = 4;
	private static final int MOVE_ROW[] = {-1,1,0,0,-1,-1,1,1};
	private static final int MOVE_COL[] = {0,0,-1,1,-1,1,-1,1};
	private static final double GOAL_REWARD = 50.0;
	private static final double CLIFF_PENALTY = -50.0;
}