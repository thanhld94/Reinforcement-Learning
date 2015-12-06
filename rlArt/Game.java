package rlArt;

import java.util.ArrayList;
import javax.swing.JFrame;

public class Game {

	public static final long AGENT_TIME_INTERVAL = 200L;
	public static final int SILENT_TRAINING = 5000;

	public static void main( String[] args ) {
		/******************/
		Art art = new Art( 18 );
		/******************/


		Environment environment = new Environment( art );
		ReinforcementAgent agent = new ReinforcementAgent( environment );
		Game myframe = new Game( environment, agent );
		myframe.learning(SILENT_TRAINING);
	}

	public Game( Environment e, ReinforcementAgent a ) {
		environment = e;
		agent = a;
		frame = new JFrame( "Bomberman" );
		frame.setSize( ( environment.getNoCols() ) * GraphicsPanel.SQUARE_SIZE, ( environment.getNoRows() + 1 ) * GraphicsPanel.SQUARE_SIZE );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setVisible( false );
		myPanel = new GraphicsPanel( environment, agent );
		frame.add( myPanel );
	}

	public void learning( int silent ) {
		int episode = 0;
		int cnt = 0;
		while ( true ) {
			episode++;
			environment.resetEnvironment();
			agent.resetPosition(1,1);
			if ( episode <= silent ) {
				if ( episode == 1 ) {
					System.out.println( "Silent Training ..." );
					System.out.print( "[" );
				}
				double tmp = (100.0) * episode / silent;
				while ( tmp > (1.0) * cnt ) {
					System.out.print( "|" );
					cnt++;
				}
				silentTraining();
				if ( episode == silent )
					System.out.println( "]" );
			} else {
				frame.setVisible( true );
				repaintPanel();
				playGame();
			}

			if ( episode > silent )	
				System.out.println( "Episode = " + episode );
		}
	}

	/**************************
	*         PRIVATE         *
	***************************/

	private void silentTraining() {
		while ( !agent.checkTerminated() ) {
    		agent.reinforcementMove();
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
        		agent.reinforcementMove();
        		repaintPanel();
        	}
        }
	}

	private JFrame frame;
	private GraphicsPanel myPanel;
	private Environment environment;
	private ArrayList <Ghost> ghostList;
	private ReinforcementAgent agent;
	
}