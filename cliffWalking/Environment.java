package cliffWalking;

public class Environment {

	public Environment() {
		initialize();
		startingRow = maxRow - 1;
		startingCol = 0;
		goalRow = maxRow - 1;
		goalCol = maxCol - 1;
	}

	public Environment( int sRow, int sCol, int gRow, int gCol ) {
		initialize();
		startingRow = sRow;
		startingCol = sCol;
		goalRow = gRow;
		goalCol = gCol;
	}

	public boolean checkTerminate( int state ) {
		return (reward[ state / maxCol ][ state % maxCol ] <= -50 || reward[ state / maxCol ][ state % maxCol ] >= 50 );
	}

	public int getGoalState() {
		return getState( goalRow, goalCol );
	}

	public int getStartingState() {
		return getState( startingRow, startingCol );
	}

	public int getMaxRow() {
		return reward.length;
	}

	public int getMaxCol() {
		return reward[0].length;
	}

	public int getReward( int state) {
		int row = state / maxCol;
		int col = state % maxCol;
		return reward[ row ][ col ];
	}

	/*********************************
	*            PRIVATE             *
	**********************************/

	private int getState( int row, int col ) {
		return row*maxCol + col;
	}

	private void initialize() {
		maxRow = reward.length;
		maxCol = reward[0].length;
	}

	private int goalRow, goalCol;
	private int startingRow, startingCol;
	private int maxRow, maxCol;
	private int reward[][] = {{ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 
	              			  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 
	              			  { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }, 
	            			  { -1, -50, -50, -50, -50, -50, -50, -50, -50, -50, -50, 50 }};
}