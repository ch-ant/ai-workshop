package minimax;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;


/**
 *      Button Class describing the functionality of a button within 
 *      the Board where the game is played.
 */
class Button extends JButton implements ActionListener {
  
    /* coordinates */
    private int x, y;

    /* default color */
    private Color color;
  

    /**
     *      Button constructor
     * 
     *      @param x: coordinate X
     *      @param y: coordinate Y
     */
    public Button(int x, int y) {

        super(x + "," + y);
        this.x = x;
        this.y = y;

        this.setOpaque(true);
        this.setBorderPainted(true);


        /* color the grid like a chess board */
        if ((x + y) % 2 == 1) {
            color = Color.GRAY;
        }
        else {
            color = new Color(255, 127, 80, 255);
        }


        this.setBackground(color);
        this.setForeground(Color.BLACK);

        /* Initially we want all the buttons to be disabled */
        this.setEnabled(false);

        /* Add an ActionListener to handle button presses */
        this.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                /* 
                    Get the button pressed to access it's coordinates
                    and call the method responsible for moving the player
                    with the respective coordinates.
                 */
                Button button = (Button) e.getSource();
                Logic.movePlayer(button.x, button.y);
                
            }
        });

        /* Add a MouseListener to handle mouse hovers */
        this.addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent e) {

                Button button = (Button) e.getSource();

                /* Highlight enabled button with light green color */
                if (button.isEnabled()) {
                    button.setBackground(new Color(63, 191, 63, 255));
                }
            }

            public void mouseExited(MouseEvent e) {

                Button button = (Button) e.getSource();

                /* Reset color to default value when mouse leaves button area*/
                if (button.isEnabled())
                    button.setBackground(color);
            }
        });
    }


    /**
     * Sets the text of a Button to display its x and y coordinates.
     */
    public void setDefaultText() {
        this.setText(this.x + "," + this.y);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        
    }
}


/**
 *      Board Class describing the GUI which consists of a JOptionPane
 *      to get the initial user input and a GridLayout Board with Buttons
 *      where the game is played.
 */
public class Board extends JPanel{
    
    /* 
        The Board consists of N rows and M columns.
        Higher values have a significant impact on the A.I.'s speed
        and therefore processing power as well.
    */
    public static final int N = 5;
    public static final int M = 5;
    public static final int SIZE = 75;

    private Button[][] buttons = new Button[N][M];
    private JFrame frame;
    
    /* Default initial vaalues */
    final int defaultCompX = N % 2 == 1 ? N / 2 : N / 2 - 1;
    final int defaultCompY = 0;
    final int defaultPlayerX = N / 2;
    final int defaultPlayerY = M-1;
    final int defaultBlackBlocksCount = 3;

    private int compStartX, compStartY;
    private int playerStartX, playerStartY;
    private int startUnavBlocks;

    


    /**
     * Board Constructor
     */
    public Board() {

        super(new GridLayout(N, M));
        this.setPreferredSize(new Dimension(N * SIZE, M * SIZE));
      

        /* Fill the board with Buttons */
        for (int x = 0; x < N; x++) {
            for (int y = 0; y < M; y++) {

                Button button = new Button(x, y);

                /* Also keep the buttons in an array for easier access */
                this.buttons[x][y] = button;
                this.add(button);
            }
        }

        /* Finally create the Jframe where the Board is added */
        frame = new JFrame();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Minimax Game");
        frame.add(this);
        frame.pack();

        /* Initially the JOptionPane is displayed to get user input
            therefore we set the Board to not visible  */
        frame.setVisible(false);
        frame.setLocationRelativeTo(null);
    }


    /* Getters to pass the initial input to the back-end logic */

    public int getCompStartX() {
        return this.compStartX;
    }

    public int getCompStartY() {
        return this.compStartY;
    }

    public int getPlayerStartX() {
        return this.playerStartX;
    }

    public int getPlayerStartY() {
        return this.playerStartY;
    }

    public int getStartUnavBlocks() {
        return this.startUnavBlocks;
    }


    /**
     * Sets the frame with the Board to visible
     */
    public void setVisible(boolean b) {
        this.frame.setVisible(b);
    }


    /**
     *      Main method responsible for updating the Board's Buttons using the given grid. 
     *      The player's coordinates are also needed to update (set to enabled) Buttons 
     *      that are a potential player moves.
     * 
     *      @param grid: An array used by the back-end logic to describe the different game states
     *      @param inputPlayerX: player X coordinates.
     *      @param inputPlayerY: player Y coordinate
     */
    public void updateButtonsGrid(int[][] grid, int inputPlayerX, int inputPlayerY) {


        /* Check every button */
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++ ) {

                Button button = this.buttons[i][j];
                int blockStatus = grid[i][j];
                
                /* Initially set to disabled to avoid inconsistencies */
                button.setEnabled(false);

                /* and update according to the status in the grid */
                if (blockStatus == Logic.FREE_BLOCK) {
                    button.setDefaultText();
                }

                else if (blockStatus == Logic.UNAVAILABLE_BLOCK) {
                    button.setBackground(new Color(45, 45, 55, 255));
                    button.setText(" ");
                }

                else if (blockStatus == Logic.AI) {
                    button.setText("A.I.");
                    button.setBackground(new Color(100, 150, 250, 255));
                }

                else if (blockStatus == Logic.PLAYER) {
                    button.setText("YOU");
                    button.setBackground(new Color(200, 75, 100, 255));
                }

            }
        }

        /* Finally update the potential player moves by enabling the respective buttons */
        updatePotentialPlayerMoves(grid, inputPlayerX, inputPlayerY);

    }


    /**
     *      Method responsible for enabling the Buttons on the Board that
     *      correspond to a potential player move, so that the user can click
     *      them and proceed with the game. 
     * 
     *      The destination block and any crossed block must be free.
     *      Potential moves are 1 or 2 blocks in the following directions:
     * 
     *      UP, 
     *      DOWN, 
     *      LEFT, 
     *      RIGHT,
     *      UP LEFT, 
     *      UP RIGHT,
     *      DOWN LEFT, 
     *      DOWN RIGHT
     * 
     *      Resulting in a total of 16 potential moves.
     *          
     * 
     *      @param grid: An array used by the back-end logic to describe the different game states
     *      @param x: player X coordinates.
     *      @param y: player Y coordinate
     */
    public void updatePotentialPlayerMoves(int[][] grid, int x, int y) {


        /* 1 block DOWN */
        if (Logic.isPotentialMove(grid, x + 1, y)) {
            this.buttons[x+1][y].setEnabled(true);

            /* 2 blocks DOWN */
            if (Logic.isPotentialMove(grid, x + 2, y)) {
                this.buttons[x+2][y].setEnabled(true);
            }
        }

        /* 1 block UP */
        if (Logic.isPotentialMove(grid, x - 1, y)) {
            this.buttons[x-1][y].setEnabled(true);

            /* 2 blocks UP */
            if(Logic.isPotentialMove(grid, x - 2, y)) {
                this.buttons[x-2][y].setEnabled(true);
            }
        }

        /* 1 block RIGHT */
        if (Logic.isPotentialMove(grid, x, y + 1)) {
            this.buttons[x][y+1].setEnabled(true);

            /* 2 blocks RIGHT */
            if(Logic.isPotentialMove(grid, x, y + 2)) {
                this.buttons[x][y+2].setEnabled(true);
            }
        }

        /* 1 block LEFT */
        if (Logic.isPotentialMove(grid, x, y - 1)) {
            this.buttons[x][y-1].setEnabled(true);

            /* 2 blocks LEFT */
            if(Logic.isPotentialMove(grid, x, y - 2)) {
                this.buttons[x][y-2].setEnabled(true);
            }
        }

        /* 1 block DOWN RIGHT */
        if (Logic.isPotentialMove(grid, x + 1, y + 1)) {
            this.buttons[x+1][y+1].setEnabled(true);

            /* 2 blocks DOWN RIGHT */
            if(Logic.isPotentialMove(grid, x + 2, y + 2)) {
                this.buttons[x+2][y+2].setEnabled(true);
            }
        }

        /* 1 block UP LEFT */
        if (Logic.isPotentialMove(grid, x - 1, y - 1)) {
            this.buttons[x-1][y-1].setEnabled(true);

            /* 2 blocks UP LEFT */
            if(Logic.isPotentialMove(grid, x - 2, y - 2)) {
                this.buttons[x-2][y-2].setEnabled(true);
            }
        }

        /* 1 block UP RIGHT */
        if (Logic.isPotentialMove(grid, x - 1, y + 1)) {
            this.buttons[x-1][y+1].setEnabled(true);

            /* 2 blocks UP RIGHT */
            if(Logic.isPotentialMove(grid, x - 2, y + 2)) {
                this.buttons[x-2][y+2].setEnabled(true);
            }
        }

        /* 1 block DOWN LEFT */
        if (Logic.isPotentialMove(grid, x + 1, y - 1)) {
            this.buttons[x+1][y-1].setEnabled(true);

            /* 2 blocks DOWN LEFT */
            if(Logic.isPotentialMove(grid, x + 2, y - 2)) {
                this.buttons[x+2][y-2].setEnabled(true);
            }
        }
    }


    /**
     *      Disables all the buttons of the board.
     *      Used while the AI is 'thinking' to prevent any further player moves.
     */
    public void disableAllButtons() {

        for (Button[] row : this.buttons)         
            for (Button button : row) {

                if (button.isEnabled()) 
                    button.setEnabled(false);
            }
    }


    /**
     *      Method responsible for handling the end of a game.
     *      Displays a message before the game is closed.
     * 
     *      @param msg: the message to be displayed
     *      @param title: the title of the JOptionPane
     */
    public void handleGameOver(String msg, String title) {
        JOptionPane.showMessageDialog(frame, msg,
            title, JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
    }

  
    /**
     *      Method responsible for displaying the initial JOptionPane 
     *      and getting the user input for the start of the game. 
     *      It features a randomize option where the Player and AI start
     *      positions as well as the number of predetermined unavailable blocks
     *      will be randomized. The user can input custom values. If any of the
     *      fields is left empty or has invalid input the default value will be used.
     */
    public void showStartOptionsPane() {

        /* Number of unavailable predetermined blocks */
        JTextField inputUnavBlocks = new JTextField();

        /* Player and AI coordinates */
        JTextField inputCompX = new JTextField();
        JTextField inputCompY = new JTextField();
        JTextField inputPlayerX = new JTextField();
        JTextField inputPlayerY = new JTextField();


        Object[] inputFields = {
            "\nPlease enter start options" +
            "\n(leave empty for default values)" +
            "\nor select randomize",
            "\nNumber of Unavailable Blocks:", inputUnavBlocks,
            "\nComputer X:", inputCompX,
            "Computer Y:", inputCompY,
            "Player X:", inputPlayerX,
            "Player Y:", inputPlayerY,
        };

        Object[] optionsText = { "Start", "Randomize" };


        int option =JOptionPane.showOptionDialog(frame, inputFields, "Start Positions", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, 
                null, optionsText, optionsText[0]);

        
        /* If Start button is selected */
        if (option == JOptionPane.YES_OPTION) {


            /* try to parse the input to int and if it doesn't work use default values */
            try {
                startUnavBlocks = Integer.parseInt(inputUnavBlocks.getText());
            }
            catch (Exception e) {
                startUnavBlocks = defaultBlackBlocksCount;
            }

            try {
                compStartX = Integer.parseInt(inputCompX.getText());
            }
            catch (Exception e) {
                compStartX = defaultCompX;   
            }
            
            try {
                compStartY = Integer.parseInt(inputCompY.getText());
            }
            catch (Exception e) {
                compStartY = defaultCompY;
            }

            try {
                playerStartX = Integer.parseInt(inputPlayerX.getText());
            }
            catch (Exception e) {
                playerStartX = defaultPlayerX;
            }

            try {
                playerStartY = Integer.parseInt(inputPlayerY.getText());
            }
            catch (Exception e) {
                playerStartY = defaultPlayerY;
            }
             
        }

        /* Randomize button is selected so randomize everything */
        else if (option == JOptionPane.NO_OPTION) {
            compStartX = Logic.random.nextInt(Board.N);
            compStartY = Logic.random.nextInt(Board.M);

            /* Player and AI cannot be at the same position */
            do {
                playerStartX = Logic.random.nextInt(Board.N);
                playerStartY = Logic.random.nextInt(Board.M);
                    }
            while(playerStartX == compStartX && 
            playerStartY == compStartY);
           
            startUnavBlocks = Logic.random.nextInt((Board.M*Board.N) / 3);
        }

        else System.exit(0);
    }

}
