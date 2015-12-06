package rlArt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Environment {

	public static final int INF = 1000111000;

	public static final int WINNING_REWARD = 500;
	public static final int LOSE_PENALTY = -500;
	public static final int GHOST_DESTROY_REWARD = 0;
	public static final int BLOCK_DESTROY_REWARD = 100;
	public static final int MOVEMENT_PENALTY = -20;

	public static final int EMPTY_SQUARE = 0;
	public static final int WALL_SQUARE = 1;
	public static final int GHOST_SQUARE = 2;
	public static final int BLOCK_SQUARE = 3;
	public static final int BOMB_SQUARE = 4;
	public static final int EXPLOSION_SQUARE = 5;

	public static final int UP = 0;
	public static final int DOWN = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	public static final int STAY = 4;
	public static final int PLANT = 5;

	public static final int MAX_BOMBS = 1;
	public static final int BOMB_RADIUS = 3;
	public static final int EXPLOSION_TIME = 5;

	public static final int MAX_MOVES = 6;
	public static final int[] MOVE_ROW = { -1, 1, 0, 0, 0, 0 };
	public static final int[] MOVE_COL = { 0, 0, -1, 1, 0, 0 };

	public Environment( Art a ) {
		art = a;
		board = new char[ presetBoard.length ][ presetBoard[ 0 ].length ];
		noRows = board.length;
		noCols = board[0].length;
		noGhosts = preNoGhosts = 0;
		noBlocks = preNoBlocks = 0;
		noBombs = 0;
		timer = new int[ noRows ][ noCols ];
		checkBit = 1;
		bit = new int[ noRows ][ noCols ];
		for ( int i = 0; i < noRows; i++ )
			for ( int j = 0; j < noCols; j++ ) {
				board[ i ][ j ] = presetBoard[ i ][ j ];
				if ( board[ i ][ j ] == 'E' )
					noGhosts++;
				if ( board[ i ][ j ] == 'o' )
					noBlocks++;
			}
		timer = new int[ board.length ][ board[ 0 ].length ];
		totalGhosts = noGhosts;
	}

	public void resetEnvironment() {
		noGhosts = preNoGhosts = 0;
		noBlocks = preNoBlocks = 0;
		noBombs = 0;
		for ( int i = 0; i < noRows; i++ )
			for ( int j = 0; j < noCols; j++ ) {
				timer[ i ][ j ] = 0;
				board[ i ][ j ] = presetBoard[ i ][ j ];
				if ( board[ i ][ j ] == 'E' )
					noGhosts++;
				if ( board[ i ][ j ] == 'o' )
					noBlocks++;
			}
	}

	public int getNoRows() {
		return noRows;
	}

	public int getNoCols() {
		return noCols;
	}

	public ArrayList <Integer> getMovesList( int row, int col ) {
		ArrayList <Integer> result = new ArrayList <Integer> ();
		for ( int action = 0; action < MAX_MOVES; action++ ) {
			if ( action == PLANT ) {
				if ( noBombs < MAX_BOMBS && getEnvironment( row, col ) != BOMB_SQUARE ) 
					result.add( action );
			} else {
				int nrow = row + MOVE_ROW[ action ];
				int ncol = col + MOVE_COL[ action ];
				if ( nrow >= 0 && ncol >= 0 && nrow < noRows && ncol < noCols )
					if ( board[ nrow ][ ncol ] != 'X' && board[ nrow ][ ncol ] != '#' && board[ nrow ][ ncol ] != 'o' )
						result.add( action );
			}
		}
		return result;
	}

	public boolean ghostAt( int row, int col ) {
		if ( board[ row ][ col ] == 'E' )
			return true;
		return false;
	}

	public int getState( int row, int col ) {
		Double[] normalizedVector = distanceVector( row, col );
		return art.learn( normalizedVector );
	}
	
	public int getEnvironment( int row, int col ) {
		if ( board[ row ][ col ] == 'X' ) 
			return BOMB_SQUARE;
		if ( board[ row ][ col ] == ' ' )
			return EMPTY_SQUARE;
		if ( board[ row ][ col ] == 'o' )
			return BLOCK_SQUARE;
		if ( board[ row ][ col ] == '*' )
			return EXPLOSION_SQUARE;
		if ( board[ row ][ col ] == 'E' )
			return GHOST_SQUARE;
		return WALL_SQUARE;
	}

	public void nextMove( int action, int row, int col ) {
		preNoGhosts = noGhosts;
		preNoBlocks = noBlocks;
		for ( int r = 0; r < noRows; r++ )
			for ( int c = 0; c < noCols; c++ )
				if ( getEnvironment( r, c ) ==  EXPLOSION_SQUARE )
					board[ r ][ c ] = ' ';

		for ( int r = 0; r < noRows; r++ )
			for ( int c = 0; c < noCols; c++ ) 
				if ( getEnvironment( r, c ) == BOMB_SQUARE ) {
					timer[ r ][ c ]--;
					if ( timer[ r ][ c ] == 0 ) {
						noBombs--;
						for ( int idx = 0; idx < EXPLOSION_SIZE; idx++ ) {
							int nr = r + EXPLOSION_ROW[ idx ];
							int nc = c + EXPLOSION_COL[ idx ];
							if ( nr < 0 || nc < 0 || nr >= noRows || nc >= noCols ) continue;
							if ( ghostAt( nr, nc ) )
								noGhosts--;
							if ( getEnvironment( nr, nc ) == BLOCK_SQUARE )
								noBlocks--;
							boolean skipCheck = false;
							if ( getEnvironment( nr, nc ) != EMPTY_SQUARE && ( idx % 2 == 1 ) )
								skipCheck = true;
							if ( getEnvironment( nr, nc ) != WALL_SQUARE )
								board[ nr ][ nc ] = '*';
							if ( skipCheck ) idx++;
						}
					}
				}
		
		if ( action == PLANT ) {
			noBombs++;
			board[ row ][ col ] = 'X';
			timer[ row ][ col ] = EXPLOSION_TIME;
		}

		// Random ghosts movement
		for ( int r = 0; r < noRows; r++ )
			for ( int c = 0; c < noCols; c++ )
				if ( ghostAt( r, c ) && bit[ r ][ c ] != checkBit ) {
					if ( r == row && c == col ) continue;
					bit[ r ][ c ] = checkBit;
					Random rand = new Random();
					ArrayList <Integer> ghostMoveList = legalGhostAction( r, c );
					if ( ghostMoveList.size() == 0 ) 
						continue;
					int idx = rand.nextInt( ghostMoveList.size() );
					board[ r ][ c ] = ' ';
					int nr = r + MOVE_ROW[ ghostMoveList.get( idx ) ];
					int nc = c + MOVE_COL[ ghostMoveList.get( idx ) ];
					bit[ nr ][ nc ] = checkBit;
					board[ nr ][ nc ] = 'E';
				}
		checkBit = 1 - checkBit;
	}

	public double reward( int row, int col ) {
		if ( ghostAt( row, col ) ) return LOSE_PENALTY;
		if ( getEnvironment( row, col ) == EXPLOSION_SQUARE ) return LOSE_PENALTY;
		double result = 0;
		if ( noBlocks == 0 )
			return WINNING_REWARD;
		if ( preNoBlocks > noBlocks )
			result += BLOCK_DESTROY_REWARD;
		if ( noGhosts == 0 ) 
			result += WINNING_REWARD;
		result += MOVEMENT_PENALTY;
		return result;
	}

	public boolean checkTerminatedState( int row, int col ) {
		/*System.out.println( row + " " + col );
		for ( int i = 0; i < noRows; i++ ) {
			for ( int j = 0; j < noCols; j++ ) 
				System.out.print( board[ i ][ j ] + " " );
			System.out.println();
		}
		System.out.println();
		System.out.println();*/
		if ( getEnvironment( row, col ) == EXPLOSION_SQUARE ) return true;
		if ( getEnvironment( row, col ) == GHOST_SQUARE ) return true;
		if ( noGhosts == 0 ) return true;
		for ( int i = 0; i < noRows; i++ )
			for ( int j = 0; j < noCols; j++ )
				if ( getEnvironment( i, j ) == BLOCK_SQUARE ) 
					return false;
		//System.out.println( "WON" );
		return true;
	}

	/***************************
	*          PRIVATE         *  
	****************************/

	private Double[] distanceVector( int row, int col ) {
		//System.out.println( "row = " + row + " col = " + col );
		Double[] features = new Double[ 18 ];
		int direction = STAY;
		int minDistance = INF;
		
		PairInt minGhostDistance = fordbellman( row, col, GHOST_SQUARE );
		minDistance = minGhostDistance.getFirst();
		direction = minGhostDistance.getSecond();
		if ( minDistance >= INF ) {
			minDistance = BOMB_RADIUS * ( noRows + noCols );
			direction = STAY;
		}
		features[ 0 ] = (1.0) * minDistance;
		//System.out.println( " feature[ 0 ] = " + features[ 0 ] );
		features[ 1 ] = (1.0) * direction / STAY;
		//System.out.println( "DIST = " + minDistance + " direction = " + direction );

		PairInt minBlockDistance = fordbellman( row, col, BLOCK_SQUARE );
		direction = minBlockDistance.getSecond();
		minDistance = minBlockDistance.getFirst();
		if ( minDistance >= INF ) {
			minDistance = BOMB_RADIUS * ( noRows + noCols );
			direction = STAY;
		}
		features[ 2 ] = (1.0) * minDistance;
		features[ 3 ] = (1.0) * direction / STAY;
		//System.out.println( "DIST = " + minDistance + " direction = " + direction );

		PairInt minBombDistance = fordbellman( row, col, BOMB_SQUARE );
		direction = minBombDistance.getSecond();
		minDistance = minBombDistance.getFirst();
		if ( minDistance >= INF ) {
			direction = STAY;
			minDistance = BOMB_RADIUS * ( noRows + noCols );
		}
		features[ 4 ] = (1.0) * minDistance;
		features[ 5 ] = (1.0) * direction / STAY;
		int bombTime;
		bombTime = nearestBombTime( row + MOVE_ROW[ direction ], col + MOVE_COL[ direction ] );
		if ( bombTime == INF )
			bombTime = EXPLOSION_TIME + 1;
		//System.out.println( "BOMB TIME =  " + bombTime + " direction = " + direction );
		features[ 8 ] = (1.0) * bombTime / ( EXPLOSION_TIME + 1 );


		PairInt maxEscape = maxEscapeDistance( row, col );
		minDistance = maxEscape.getFirst();
		direction = maxEscape.getSecond();
		
		if ( minDistance >= INF ) {
			minDistance = BOMB_RADIUS * ( noRows + noCols );
			direction = STAY;
		}
		features[ 6 ] = (1.0) * minDistance;
		features[ 7 ] = (1.0) * direction / STAY;
		

		
		
		features[ 0 ] /= ( BOMB_RADIUS * ( noRows + noCols ) );
		features[ 2 ] /= ( BOMB_RADIUS * ( noRows + noCols ) );
		features[ 4 ] /= ( BOMB_RADIUS * ( noRows + noCols ) );
		features[ 6 ] /= ( BOMB_RADIUS * ( noRows + noCols ) );

		for ( int i = 0; i < features.length / 2; i++ )
			features[ i + features.length / 2 ] = 1 - features[ i ];
		return features;
		
	}

	private int max( int a, int b ) {
		if ( a > b ) 
			return a;
		return b;
	}

	private int nearestBombTime( int row, int col ) {
		Queue rowQueue = new LinkedList();
		Queue colQueue = new LinkedList();
		boolean[][] visited = new boolean[ noRows ][ noCols ];
		rowQueue.add( row );
		colQueue.add( col );
		while ( rowQueue.peek() != null ) {
			int r = (int)rowQueue.poll();
			int c = (int)colQueue.poll();
			if ( getEnvironment( r, c ) == BOMB_SQUARE )
				return timer[ r ][ c ];
			if ( getEnvironment( r, c ) == EXPLOSION_SQUARE )
				return 0;

			for ( int i = 0; i < 4; i++ ) {
				int newr = r + MOVE_ROW[ i ];
				int newc = c + MOVE_COL[ i ];
				if ( newr >= 0 && newc >= 0 && newr < noRows && newc < noCols )
					if ( getEnvironment( newr, newc ) != WALL_SQUARE && getEnvironment( newr, newc ) != BLOCK_SQUARE ) 
						if ( !visited[ newr ][ newc ] ) {
							visited[ newr ][ newc ] = true;
							if ( getEnvironment( newr, newc ) == BOMB_SQUARE )
								return timer[ newr ][ newc ];
							if ( getEnvironment( newr, newc ) == EXPLOSION_SQUARE )
								return 0;
							rowQueue.add( newr );
							colQueue.add( newc );
						}
			}
		}
		return INF;
	}

	private PairInt fordbellman( int row, int col, int goal ) {
		//System.out.println( "STARTED FORD-BELLMAN" );
		Queue rowQueue = new LinkedList();
		Queue colQueue = new LinkedList();
		int[][] distance = new int[ noRows ][ noCols ];
		int[][] direction = new int[ noRows ][ noCols ];
		int[][] startingDirection = new int[ noRows ][ noCols ];
		boolean[][] inQueue = new boolean[ noRows ][ noCols ];

		for ( int i = 0; i < noRows; i++ )
			for ( int j = 0; j < noCols; j++ ) {
				distance[ i ][ j ] = INF;
				startingDirection[ i ][ j ] = -1;
			}

		for ( int i = 0; i <= 4; i++ ) {
			int r = row + MOVE_ROW[ i ];
			int c = col + MOVE_COL[ i ];
			boolean chk = true;
			if ( goal != BLOCK_SQUARE && getEnvironment( r, c ) == BLOCK_SQUARE ) 
				chk = false;
			if ( goal != BOMB_SQUARE && getEnvironment( r, c ) == BOMB_SQUARE )
				chk = false;
			
			if ( r >= 0 && c >= 0 && r < noRows && c < noCols ) 
				if ( getEnvironment( r, c ) != WALL_SQUARE && chk ) {
					rowQueue.add( r );
					colQueue.add( c );
					int tmp = 0;
					if ( i != STAY )
						tmp = 1;
					distance[ r ][ c ] = tmp;
					direction[ r ][ c ] = i;
					startingDirection[ r ][ c ] = i;
					inQueue[ r ][ c ] = true;
				}
		}

		while ( rowQueue.peek() != null ) {
			int r = ( int ) rowQueue.poll();
			int c = ( int ) colQueue.poll();
			inQueue[ row ][ col ] = false;

			for ( int i = 0; i < 4; i++ ) {
				int newr = r + MOVE_ROW[ i ];
				int newc = c + MOVE_COL[ i ];
				boolean chk = true;
				if ( goal != BLOCK_SQUARE && getEnvironment( newr, newc ) == BLOCK_SQUARE ) 
					chk = false;
				if ( goal != BOMB_SQUARE && getEnvironment( newr, newc ) == BOMB_SQUARE )
					chk = false;
				if ( newr >= 0 && newc >= 0 && newr < noRows && newc < noCols )
					if ( getEnvironment( newr, newc ) != WALL_SQUARE && chk ) {
						int d = 1;
						if ( ( ( direction[ r ][ c ] == UP || direction[ r ][ c ] == DOWN )  && ( i == LEFT || i == RIGHT ) ) || ( ( direction[ r ][ c ] == LEFT  || direction[ r ][ c ] == RIGHT ) && ( i == UP || i == DOWN ) ) )
							d = BOMB_RADIUS;
						if ( distance[ newr ][ newc ] > distance[ r ][ c ] + d ) {
							distance[ newr ][ newc ] = distance[ r ][ c ] + d;
							startingDirection[ newr ][ newc ] = startingDirection[ r ][ c ];
							direction[ newr ][ newc ] = i;
							if ( !inQueue[ newr ][ newc ] ) {
								rowQueue.add( newr );
								colQueue.add( newc );
							}
						}
					}
			}
		}

		//System.out.println("DONE FORD-BELLMAN");

		int result = INF;
		int dir = -1;
		for ( int i = 0; i < noRows; i++ )
			for ( int j = 0; j < noCols; j++ )
				if ( getEnvironment( i, j ) == goal || ( goal == BOMB_SQUARE && getEnvironment( i, j ) == EXPLOSION_SQUARE ) )
					if ( result > distance[ i ][ j ] ) {
						result = distance[ i ][ j ];
						dir = startingDirection[ i ][ j ];
					}
		return ( new PairInt( result, dir ) );
	}

	private PairInt maxEscapeDistance( int row, int col ) {
		//System.out.println( "Started maxEscape" );
		Queue rowQueue = new LinkedList();
		Queue colQueue = new LinkedList();
		int[][] distance = new int[ noRows ][ noCols ];
		int[][] direction = new int[ noRows ][ noCols ];
		boolean[][] inQueue = new boolean[ noRows ][ noCols ];

		for ( int i = 0; i < noRows; i++ )
			for ( int j = 0; j < noCols; j++ )
				distance[ i ][ j ] = INF;

		for ( int i = 0; i < noRows; i++ )
			for ( int j = 0; j < noCols; j++ )
				if ( getEnvironment( i, j ) == BOMB_SQUARE || getEnvironment( i, j ) == EXPLOSION_SQUARE ) {
					distance[ i ][ j ] = 0;
					distance[ i ][ j ] = -1;
					inQueue[ i ][ j ] = true;
					rowQueue.add( i );
					colQueue.add( j );
				}

		while ( rowQueue.peek() != null ) {
			int r = ( int ) rowQueue.poll();
			int c = ( int ) colQueue.poll();
			inQueue[ row ][ col ] = false;

			for ( int i = 0; i < 4; i++ ) {
				int newr = r + MOVE_ROW[ i ];
				int newc = c + MOVE_COL[ i ];
				boolean chk = true;
				if ( newr >= 0 && newc >= 0 && newr < noRows && newc < noCols )
					if ( getEnvironment( newr, newc ) != WALL_SQUARE && getEnvironment( newr, newc ) != BLOCK_SQUARE ) {
						int d = 1;
						if ( ( ( direction[ r ][ c ] == UP || direction[ r ][ c ] == DOWN )  && ( i == LEFT || i == RIGHT ) ) || ( ( direction[ r ][ c ] == LEFT  || direction[ r ][ c ] == RIGHT ) && ( i == UP || i == DOWN ) ) )
							d = BOMB_RADIUS;
						if ( distance[ newr ][ newc ] > distance[ r ][ c ] + d ) {
							distance[ newr ][ newc ] = distance[ r ][ c ] + d;
							direction[ newr ][ newc ] = i;
							if ( !inQueue[ newr ][ newc ] ) {
								rowQueue.add( newr );
								colQueue.add( newc );
							}
						}
					}
			}
		}

		//System.out.println( "Done maxEscape" );

		int result = -1;
		int resultCol = -1;
		int resultRow = -1;
		for ( int i = 0; i < noRows; i++ )
			for ( int j = 0; j < noCols; j++ ) 
				if ( distance[ i ][ j ] != INF && result < distance[ i ][ j ] ) {
					result = distance[ i ][ j ];
					resultRow = i;
					resultCol = j;
				}

		int[][] dir = new int[ noRows ][ noCols ];
		rowQueue = new LinkedList();
		colQueue = new LinkedList();
		boolean[][] visited = new boolean[ noRows ][ noCols ];
		for ( int i = 0; i <= 4; i++ ) {
			int r = row + MOVE_ROW[ i ];
			int c = col + MOVE_COL[ i ];
			if ( r >= 0 && c >= 0 && r < noRows && c < noCols ) 
				if ( getEnvironment( r, c ) != WALL_SQUARE && getEnvironment( r, c ) != BOMB_SQUARE && getEnvironment( r, c ) != BLOCK_SQUARE ) {
					dir[ r ][ c ] = i;
					visited[ r ][ c ] = true;
					rowQueue.add( r );
					colQueue.add( c );
				}
		}

		while ( rowQueue.peek() != null ) {
			int r = ( int ) rowQueue.poll();
			int c = ( int ) colQueue.poll();
			for ( int i = 0; i < 4; i++ ) {
				int newr = r + MOVE_ROW[ i ];
				int newc = c + MOVE_COL[ i ];
				if ( newr >= 0 && newc >= 0 && newr < noRows && newc < noCols )
					if ( getEnvironment( newr, newc ) != WALL_SQUARE && getEnvironment( newr, newc ) != BLOCK_SQUARE ) 
						if ( !visited[ newr ][ newc ] ) {
							dir[ newr ][ newc ] = dir[ r ][ c ];
							if ( newr == resultRow && newc == resultCol ) {
								PairInt finalResult = new PairInt( result, dir[ newr ][ newc ] );
								//System.out.println( "DONE BFS" );
								return finalResult;
							}
							visited[ newr ][ newc ] = true;
							rowQueue.add( newr );
							colQueue.add( newc );
						}
			}
		}

		//System.out.println( "DONE BFS" );
		return ( new PairInt( INF, -1 ) );
	}

	private ArrayList <Integer> legalGhostAction( int row, int col ) {
		ArrayList <Integer> moveList = new ArrayList <Integer> ();
		for ( int action = 0; action < MAX_MOVES - 2; action++ ) {
			int nrow = row + MOVE_ROW[ action ];
			int ncol = col + MOVE_COL[ action ];
			if ( nrow >= 0 && ncol >= 0 && nrow < noRows && ncol < noCols )
				if ( board[ nrow ][ ncol ] == ' ' )
					moveList.add( action );
		}
		return moveList;
	}

	private static final int[] EXPLOSION_ROW = { 0, -1, -2, 1, 2, 0, 0, 0, 0 };
	private static final int[] EXPLOSION_COL = { 0, 0, 0, 0, 0, -1, -2, 1, 2 };
	private static final int EXPLOSION_SIZE = 9;

	private int checkBit;
	private int[][] bit;
	private Art art;
	private int noRows, noCols;
	private int noGhosts, preNoGhosts, totalGhosts;
	private int noBlocks, preNoBlocks;
	private int noBombs;
	private int[][] timer;
	private char[][] board;
	private char[][] presetBoard = {
	

	//* Test bomb
		{ '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#' },
		{ '#', ' ', '#', 'o', '#', 'o', '#', 'o', '#', '#', 'E' },
		{ '#', ' ', ' ', ' ', ' ', ' ', ' ', 'E', ' ', ' ', '#' },
		{ '#', 'o', '#', 'o', '#', 'o', '#', 'o', '#', 'o', '#' },
		{ '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#' }
	/*/
	//* 11 x 11
		{ '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#' },
		{ '#', ' ', ' ', 'o', ' ', ' ', ' ', ' ', 'o', ' ', '#' },
		{ '#', ' ', '#', 'o', '#', 'o', '#', ' ', '#', ' ', '#' },
		{ '#', ' ', 'o', ' ', ' ', 'o', ' ', ' ', 'o', ' ', '#' },
		{ '#', ' ', '#', 'o', '#', 'o', '#', ' ', '#', 'o', '#' },
		{ '#', ' ', ' ', 'o', ' ', ' ', 'o', 'o', 'o', 'o', '#' },
		{ '#', 'o', '#', 'o', '#', 'o', '#', ' ', '#', ' ', '#' },
		{ '#', 'o', ' ', ' ', ' ', 'o', ' ', ' ', 'o', ' ', '#' },
		{ '#', 'o', '#', ' ', '#', 'o', '#', ' ', '#', ' ', '#' },
		{ '#', 'o', 'E', 'o', ' ', ' ', ' ', 'o', ' ', 'o', '#' },
		{ '#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#' }
	};
	/*/
	//19x20 board
	//*	
	};
	//*/

}