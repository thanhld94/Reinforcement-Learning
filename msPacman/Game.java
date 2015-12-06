package msPacman;

import javax.swing.JFrame;
import java.util.ArrayList;

public class Game {

	public static final long AGENT_TIME_INTERVAL = 150L;
	public static final long GHOST_TIME_INTERVAL = 150L;
	public static final int SILENT_TRAINING = 100000;
	public static final long NUMBER_OF_GHOST = 2;

	public static void main( String[] args ) {
		ArrayList <Ghost> ghost = new ArrayList <Ghost> ();
		Board board = new Board( ghost );
		RAgent agent = new RAgent( board );
		for ( int i = 0; i < NUMBER_OF_GHOST; i++ )
			ghost.add( new Ghost( board.getNumberOfRows() - i - 2, board.getNumberOfCols() - 2 ) );

		Game myframe = new Game( board, agent, ghost );
		myframe.learning(SILENT_TRAINING);

	}

	public Game( Board b, RAgent a, ArrayList <Ghost> g ) {
		board = b;
		agent = a;
		ghost = g;

		frame = new JFrame( "Ms Pacman" );
		frame.setSize( ( board.getNumberOfCols() ) * GraphicsPanel.SQUARE_SIZE, ( board.getNumberOfRows() + 1 ) * GraphicsPanel.SQUARE_SIZE );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setVisible( false );
		myPanel = new GraphicsPanel( board, agent, ghost );
		frame.add( myPanel );
	}

	public void learning( int silent ) {
		int episode = 0;
		int paletteTotal = 0;
		winCount = 0;
		while ( true ) {
			episode++;
			for ( int i = 0; i < ghost.size(); i++ ) 
				ghost.get( i ).resetPosition( board.getNumberOfRows() - i - 2, board.getNumberOfCols() - 2 );
			board.resetBoard();
			agent.resetPosition(1,1);
			if ( episode <= silent ) {
				if ( episode == 1 ) {
					System.out.println( "Silent Training ..." );
					System.out.print( "[" );
				}
				if ( episode % ( silent / 100 ) == 0 )
					System.out.print( "|" );
				silentTraining();
				if ( episode == silent )
					System.out.println( "]" );
			} else {
				frame.setVisible( true );
				repaintPanel();
				playGame();
			}
			paletteTotal += board.paletteScore();
			if ( board.ateAllPoints() ) winCount++;
			double paletteAvg = (1.0) * paletteTotal / episode;
			if ( episode > silent ) {
				System.out.printf( "Episode %d: Score = %d, Avg = %5.2f, WinRate = %5.2f", episode, board.paletteScore(), paletteAvg, (100.0)*winCount/episode );
				System.out.println("%");
			}
		}
	}

	/**************************
	*         PRIVATE         *
	***************************/

	private void silentTraining() {
		while ( !agent.checkTerminated() ) {
    		agent.reinforceMove();
        }
	}

	private void repaintPanel() {
		myPanel.repaint();
	}

	private void playGame() {
		// time counter variables
        long tStartGhost = System.currentTimeMillis();
        long tStartAgent = System.currentTimeMillis();
        while ( !agent.checkTerminated() ) {
        	long tEndAgent = System.currentTimeMillis();
        	if ( tEndAgent - tStartAgent >= AGENT_TIME_INTERVAL ) {
        		tStartAgent = System.currentTimeMillis();
        		agent.reinforceMove();
        		repaintPanel();
        	}
        }
        if ( board.ateAllPoints() ) {
        	System.out.println("WIN!");
        }
	}

	private JFrame frame;
	private int winCount;
	private GraphicsPanel myPanel;
	private Board board;
	private ArrayList <Ghost> ghost;
	private RAgent agent;

}