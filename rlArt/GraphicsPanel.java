package rlArt;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;

public class GraphicsPanel extends JPanel {

	public static final int SQUARE_SIZE = 30;
	public static final int FRAME_SIZE = 1;
	public static final Color AGENT_COLOR = Color.red;
	public static final Color GHOST_COLOR = Color.blue;
	public static final Color BACKGROUND_COLOR = Color.gray;
	public static final Color WALL_COLOR = Color.darkGray;
	public static final Color BLOCK_COLOR = new Color(139,69,19);
	public static final Color GRID_COLOR = Color.black;
	public static final Color EXPLOSION_COLOR = Color.yellow;
	public static final Color BOMB_COLOR = Color.yellow;

	public static final int AGENT_RADIUS = 25;
	public static final int EXPLOSION_RADIUS = 10;


	public GraphicsPanel( Environment e, Agent a ) {
		environment = e;
		agent = a;
	}

	public void paintComponent( Graphics g ) {
		super.paintComponent(g);
		setBackground( BACKGROUND_COLOR );
		for ( int i = 0; i < environment.getNoRows(); i++ ) 
			for ( int j = 0; j < environment.getNoCols(); j++ ) {
				if ( environment.getEnvironment( i, j ) == Environment.WALL_SQUARE )
					g.setColor( WALL_COLOR );
				else if ( environment.getEnvironment( i, j ) == Environment.BLOCK_SQUARE )
					g.setColor( BLOCK_COLOR );
				else
					g.setColor( GRID_COLOR );
				g.fillRect( j * SQUARE_SIZE, i * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE );
                drawFrame(g,i,j);

                if ( environment.getEnvironment( i, j ) == Environment.BOMB_SQUARE ) {
                	g.setColor( BOMB_COLOR );
                	drawCenteredCircle( g, j, i, AGENT_RADIUS );
                }

                if ( environment.getEnvironment( i, j ) == Environment.GHOST_SQUARE ) {
                	g.setColor( GHOST_COLOR );
                	drawCenteredCircle( g, j, i, AGENT_RADIUS );
                }
                if ( environment.getEnvironment( i, j ) == Environment.EXPLOSION_SQUARE ) {
                	g.setColor( EXPLOSION_COLOR );
                	drawCenteredCircle( g, j, i, EXPLOSION_RADIUS );
                }
			}
		g.setColor( AGENT_COLOR );
		drawCenteredCircle( g, agent.getLocationCol() , agent.getLocationRow(), AGENT_RADIUS ) ;
	}

	/****************************
	*          PRIVATE          *
	*****************************/

	private void drawFrame( Graphics g, int row, int col ) {
		g.setColor( BACKGROUND_COLOR );
		g.fillRect( col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, FRAME_SIZE );
        g.fillRect( col * SQUARE_SIZE, row * SQUARE_SIZE, FRAME_SIZE, SQUARE_SIZE);
        g.fillRect( ( col + 1 ) * SQUARE_SIZE - FRAME_SIZE, row * SQUARE_SIZE, FRAME_SIZE, SQUARE_SIZE );
        g.fillRect( col * SQUARE_SIZE, ( row + 1 ) * SQUARE_SIZE - FRAME_SIZE, SQUARE_SIZE, FRAME_SIZE );
	}

	private void drawCenteredCircle( Graphics g, int x, int y, int r ) {
		int newX = x * SQUARE_SIZE + SQUARE_SIZE/2 - ( r/2 );
		int newY = y * SQUARE_SIZE + SQUARE_SIZE/2 - ( r/2 );
		g.fillOval( newX, newY, r, r );
	}

	private Environment environment;
	private Agent agent;
	private ArrayList <Ghost> ghost;
}