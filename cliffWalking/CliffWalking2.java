/**
* @version 2.0
*/

package cliffWalking;

import java.util.Random;

public class CliffWalking2 {
	
	public static void main( String args[] ) {
		myEnvironment = new Environment2( 36, DEFAULT );
		System.out.println( "DEFAULT TABLE: " );
		myAgent = new Agent2( myEnvironment );
		myAgent.qLearning();
		printTable();

		for ( int i = 0; i < 5; i++ ) {
			System.out.println("Generated Table " + (i+1) );
			int[][] rewardTable = generateMatrix(10, 10);
			for ( int r = 0; r < 10; r++ ) {
				for ( int c = 0; c < 10; c++ )
					if ( r == startRow && c == startCol ) 
						System.out.printf( "%4.0f ", 0.0 );
					else
						System.out.printf( "%4.0f ", (1.0)*rewardTable[ r ][ c ] );
				System.out.println();
			}
			myEnvironment = new Environment2( startRow * 10 + startCol, rewardTable );
			myAgent = new Agent2( myEnvironment );
			myAgent.qLearning();
			printTable();
			System.out.println();
		}
	}

	private static void printTable() {
		for ( int i = 0; i < myEnvironment.getNumberOfRows(); i++ ) {
			for ( int j = 0; j < myEnvironment.getNumberOfCols(); j++ ) {
				int state = i * myEnvironment.getNumberOfCols() + j;
				System.out.printf( "%5.1f ", myAgent.getStateValue( state ) );
			}
			System.out.println();
		}
		System.out.println();
	}

	private static int[][] generateMatrix( int n, int m ) {
		int[][] tmp = new int[ n ][ m ];
		for ( int i = 0; i < n; i++ ) 
			for ( int j = 0; j < m ; j++ )
				tmp[ i ][ j ] = -1;
		Random rand = new Random();
		startRow = rand.nextInt(n);
		//startCol = rand.nextInt(m);
		startCol = 0;
		int goalRow = -1;
		int goalCol = -1;
		while ( true ) {
			goalRow = rand.nextInt(n);
			goalCol = n-1;
			//goalCol = rand.nextInt(m);

			if ( goalCol != startCol && goalRow != startRow ) 
				break;
		}
		System.out.println( startRow + " " + startCol + " " + goalRow + " " + goalCol );
		tmp[ goalRow ][ goalCol ] = 50;
		int cnt = 0;
		while ( cnt < NUMBER_OF_HOLES ) {
			int tmpRow = rand.nextInt(n);
			int tmpCol = rand.nextInt(m);
			if ( tmpRow != startRow && tmpRow != goalRow && tmpCol != startCol && tmpCol != goalCol) {
				cnt++;
				tmp[ tmpRow ][ tmpCol ] = -50;
			}
		}
		return tmp;
	}
	
	private static Environment2 myEnvironment;
	private static Agent2 myAgent;
	private static int startRow, startCol;
	private static final int NUMBER_OF_HOLES = 40;
	private static final int[][] DEFAULT = {{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 
	              			 	 			{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 
	              							{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 
	            							{ -1, -50, -50, -50, -50, -50, -50, -50, -50, -50, -50, 50 }};
}