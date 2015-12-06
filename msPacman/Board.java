package msPacman;

import java.util.Random;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

public class Board {

	public static final int POINT_SQUARE = 0;
	public static final int WALL_SQUARE = 1;
	public static final int EMPTY_SQUARE = 2;
	public static final int GHOST_SQUARE = 3;
	public static final int MAX_MOVES = 4;
	// 0: DOWN
	// 1: UP
	// 2: RIGHT
	// 3: LEFT
	public static final int MOVE_ROW[] = { 1, -1, 0, 0 };
	public static final int MOVE_COL[] = { 0, 0, 1, -1 };
	public static final double POINT_REWARD = 100;
	public static final double WIN_REWARD = 500;
	public static final double LOSE_PENALTY = -500.0;
	public static final double REVISIT_PENALTY = -50;
	public static final double MOVING_PENALTY = -20; 
	public static final double EXPLORATION_VALUE = 10;

	public Board( ArrayList <Ghost> g ) {
		numberOfRows = board.length;
		numberOfCols = board[0].length;
		distance = new int [ numberOfRows ][ numberOfCols ][ numberOfRows ][ numberOfCols ];
		ghost = g;
		for ( int i = 0; i < numberOfRows; i++ )
			for ( int j = 0; j < numberOfCols; j++ )
				bfs( i, j );
	}

	public ArrayList <Integer> getGhostLocationRow() {
		ArrayList <Integer> vectorRow = new ArrayList <Integer>();
		for ( int i = 0; i < ghost.size(); i++ )
			vectorRow.add( ghost.get(i).getLocationRow() );
		return vectorRow;
	}

	public ArrayList <Integer> getGhostLocationCol() {
		ArrayList <Integer> vectorCol = new ArrayList <Integer>();
		for ( int i = 0; i < ghost.size(); i++ )
			vectorCol.add( ghost.get(i).getLocationCol() );
		return vectorCol;
	}

	public void resetBoard() {
		for ( int i = 0; i < numberOfRows; i++ )
			for ( int j = 0; j < numberOfCols; j++ )
				if ( board[ i ][ j ] == ' ' )
					board[ i ][ j ] = '.';
	}

	public int getNumberOfRows() {
		return numberOfRows;
	}

	public int getNumberOfCols() {
		return numberOfCols;
	}

	public void eatPoint( int row, int col ) {
		if ( board[ row ][ col ] != '#' )
			board[ row ][ col ] = ' ';
	}

	public void moveGhosts( int row, int col ) {
		for ( int i = 0; i < ghost.size(); i++ ) 
			if ( ghost.get( i ).getLocationRow() != row || ghost.get( i ).getLocationCol() != col )
				ghost.get( i ).actRandom( this );
		
	}

	public double reward( int row, int col, int action, int previousAction ) {
		if ( ghostAt( row, col ) ) return LOSE_PENALTY;
		double result = MOVING_PENALTY;
		if ( action == 0 && previousAction == 1 )
			result += REVISIT_PENALTY;
		if ( action == 1 && previousAction == 0 )
			result += REVISIT_PENALTY;
		if ( action == 2 && previousAction == 3 )
			result += REVISIT_PENALTY;
		if ( action == 3 && previousAction == 2 )
			result += REVISIT_PENALTY;
		if ( getEnvironmentAt( row, col ) == POINT_SQUARE )
			result += POINT_REWARD;
		if ( ateAllPoints() ) 
			result += WIN_REWARD;
		return result;
	}

	public int getEnvironmentAt( int row, int col ) {
		if ( board[ row ][ col ] == '#' ) return WALL_SQUARE;
		if ( board[ row ][ col ] == '.' ) return POINT_SQUARE;
		return EMPTY_SQUARE;
	}

	public boolean ghostAt( int row, int col ) {
		for ( int i = 0; i < ghost.size(); i++ )
			if ( ghost.get(i).getLocationRow() == row && ghost.get(i).getLocationCol() == col ) 
				return true;
		return false;
	}

	public int paletteScore() {
		int score = 0;
		for ( int i = 0; i < numberOfRows; i++ ) 
			for ( int j = 0; j < numberOfCols; j++ )
				if ( getEnvironmentAt( i, j ) == EMPTY_SQUARE )
					score++;
		return score;
	}

	public boolean ateAllPoints() {
		for ( int i = 0; i < numberOfRows; i++ ) 
			for ( int j = 0; j < numberOfCols; j++ )
				if ( getEnvironmentAt( i, j ) == POINT_SQUARE ) return false;
		return true;
	}

	public ArrayList <Integer> getMovesList( int row, int col ) {
		ArrayList <Integer> list = new ArrayList <Integer>();
		for ( int i = 0; i < MAX_MOVES; i++ ) {
			int nrow = row + MOVE_ROW[ i ];
			int ncol = col + MOVE_COL[ i ];
			if ( checkValid( nrow, ncol ) ) 
				list.add( i );
		}
		return list;
	}

	public int getNearestGhost( int row, int col ) {
		int result = Agent.INF;
		for ( int i = 0; i < ghost.size(); i++ )
			if ( result > distance[ row ][ col ][ ghost.get( i ).getLocationRow() ][ ghost.get( i ).getLocationCol() ] ) 
				result = distance[ row ][ col ][ ghost.get( i ).getLocationRow() ][ ghost.get( i ).getLocationCol() ];
		return result;
	}

	public int getNearestPalette( int row, int col ) {
		int result = Agent.INF;
		for ( int i = 0; i < numberOfRows; i++ )
			for ( int j = 0; j < numberOfCols; j++ )
				if ( getEnvironmentAt( i, j ) == POINT_SQUARE && result > distance[ row ][ col ][ i ][ j ] )
					result = distance[ row ][ col ][ i ][ j ];
		return result;
	}
	
	/***************************
	*          PRIVATE         *
	****************************/

	private void bfs( int row, int col ) {
		Queue <Integer> rowQueue = new LinkedList <Integer> ();
		Queue <Integer> colQueue = new LinkedList <Integer> ();
		rowQueue.add( row );
		colQueue.add( col );
		while ( rowQueue.peek() != null ) {
			int topRow = rowQueue.poll();
			int topCol = colQueue.poll();
			ArrayList <Integer> movesList = getMovesList( topRow, topCol );
			for ( int i = 0; i < movesList.size(); i++ ) {
				int nrow = topRow + MOVE_ROW[ movesList.get( i ) ];
				int ncol = topCol + MOVE_COL[ movesList.get( i ) ];
				if ( distance[ row ][ col ][ nrow ][ ncol ] == 0 ) {
					distance[ row ][ col ][ nrow ][ ncol ] = distance[ row ][ col ][ topRow ][ topCol ] + 1;
					rowQueue.add( topRow );
					colQueue.add( topCol );
				}
			}
		}
	}


	private boolean checkValid( int row, int col ) {
		if ( row >= 0 && col >= 0 && row < numberOfRows && col < numberOfCols )
			if ( board[ row ][ col ] != '#' )
				return true;
		return false;
	}

	private int distance[][][][];
	private int numberOfRows, numberOfCols;
	private ArrayList <Ghost> ghost;
	private char board[][] = {
	//* 7 x 10
		{ '#', '#', '#', '#', '#', '#', '#', '#', '#', '#' },
		{ '#', '.', '.', '.', '.', '.', '.', '.', '.', '#' },
		{ '#', '.', '#', '#', '#', '#', '#', '#', '.', '#' },
		{ '#', '.', '.', '.', '.', '.', '.', '.', '.', '#' },
		{ '#', '.', '#', '#', '#', '#', '#', '#', '.', '#' },
		{ '#', '.', '.', '.', '.', '.', '.', '.', '.', '#' },
		{ '#', '#', '#', '#', '#', '#', '#', '#', '#', '#' }
	};
	/*/
	//19x20 board
	//*	
		{ '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#' },
		{ '#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#' },
		{ '#', '.', '#', '.', '#', '#', '#', '#', '#', '.', '#', '#', '#', '#', '#', '#', '.', '#', '.', '#' },
		{ '#', '.', '#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#', '.', '#' },
		{ '#', '.', '#', '.', '#', '#', '#', '#', '#', '#', '.', '#', '#', '#', '#', '#', '.', '#', '.', '#' },
		{ '#', '.', '#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#', '.', '#' },
		{ '#', '.', '#', '.', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '.', '#', '.', '#' },
		{ '#', '.', '#', '.', '#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#', '.', '#', '.', '#' },
		{ '#', '.', '#', '.', '#', '.', '#', '#', '#', '#', '#', '#', '#', '#', '.', '#', '.', '#', '.', '#' },
		{ '#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#' },
		{ '#', '.', '#', '.', '#', '.', '#', '#', '#', '#', '#', '#', '#', '#', '.', '#', '.', '#', '.', '#' },
		{ '#', '.', '#', '.', '#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#', '.', '#', '.', '#' },
		{ '#', '.', '#', '.', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '.', '#', '.', '#' },
		{ '#', '.', '#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#', '.', '#' },
		{ '#', '.', '#', '.', '#', '#', '#', '#', '#', '.', '#', '#', '#', '#', '#', '#', '.', '#', '.', '#' },
		{ '#', '.', '#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#', '.', '#' },
		{ '#', '.', '#', '.', '#', '#', '#', '#', '#', '#', '.', '#', '#', '#', '#', '#', '.', '#', '.', '#' },
		{ '#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#' },
		{ '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#' }
	};
	//*/
}