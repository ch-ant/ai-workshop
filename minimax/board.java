package minimax;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import javax.swing.JButton;
import javax.swing.JFrame;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;


class Button extends JButton implements ActionListener {
  
    static int N = board.N, M = board.M;
    private int x, y;
    Color color;
  

    public Button(int x, int y, JFrame frame) {

        super(x + "," + y);
        this.x = x;
        this.y = y;

        this.setOpaque(true);
        this.setBorderPainted(true);

        /*if (i == j) {
            color = Color.WHITE;
        }
        else if (i + j == (N+M)/2-1) {
            color = Color.WHITE;
        }*/
        if ((x + y) % 2 == 1) {
            color = Color.GRAY;
        }
        else {
            color = new Color(255, 127, 80, 255);
        }
        this.setBackground(color);
        this.setForeground(Color.BLACK);
        this.setEnabled(false);

        this.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {


                Button button = (Button) e.getSource();
                //board panel = (board) button.getComponent(0).getParent();
                //panel.setPlayerStartCoordinates(button.i, button.j);
                logic.movePlayer(button.x, button.y);
                
                /*if (button.isEnabled()) button.setEnabled(false);
                button.setBackground(new Color(45, 45, 55, 255));
                button.setText(" ");*/

                /*if (e.getSource() == button)
                    {
                        // Code To popup an ERROR_MESSAGE Dialog. showConfirmDialog
                        JOptionPane.showMessageDialog(frame, "Game Over!",
                                            "Oops!", JOptionPane.INFORMATION_MESSAGE);
                    }*/

                //System.out.println(button.position);
            }
        });

        this.addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent e) {

                Button button = (Button) e.getSource();

                if (button.isEnabled()) {
                    //button.setBackground(new Color(63, 191, 63, 255));
                }
            }
            public void mouseExited(MouseEvent e) {
                Button button = (Button) e.getSource();

                //if (button.isEnabled())
                    //button.setBackground(color);
            }
        });
    }



    public void setDefaultText() {
        this.setText(this.x + "," + this.y);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        
    }
}

public class board extends JPanel{
    
    public static final int N = 5;
    public static final int M = 5;
    public static final int SIZE = 75;

    private Button[][] buttons = new Button[N][M];

    
    final int defaultCompX = N % 2 == 1 ? N / 2 : N / 2 - 1;
    final int defaultCompY = 0;
    final int defaultPlayerX = N / 2;
    final int defaultPlayerY = M-1;
    final int defaultBlackBlocksCount = 4;
    


    JFrame frame;
    //static board bo;

    private int compStartX, compStartY;
    private int playerStartX, playerStartY;
    private int startUnavBlocks;

    



    public board() {

        super(new GridLayout(N, M));
        this.setPreferredSize(new Dimension(N * SIZE, M * SIZE));

        //UIManager.put("Button.disabledText", new ColorUIResource(Color.BLACK));
        //UIManager.put("Button.enabledText", new ColorUIResource(Color.BLACK));

        frame = new JFrame();

        for (int x = 0; x < N; x++) {
            for (int y = 0; y < M; y++) {

                Button button = new Button(x, y, frame);

                this.buttons[x][y] = button;
                this.add(button);
            }
        }

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Minimax Game");
        frame.add(this);
        frame.pack();
        frame.setVisible(false);
        frame.setLocationRelativeTo(null);
    }



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

    /*public void setCompStartCoordinates(int X, int Y) {
        this.compStartX = X;
        this.compStartY = Y;
    }

    public void setPlayerStartCoordinates(int X, int Y) {
        this.playerStartX = X;
        this.playerStartY = Y;
    }*/


    /* TESTED */
    public void setVisible(boolean b) {
        this.frame.setVisible(b);
    }


    /* TESTED */
    public void updateButtonsGrid(int[][] grid, int inputPlayerX, int inputPlayerY) {

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++ ) {

                Button button = this.buttons[i][j];
                int blockStatus = grid[i][j];
                
                button.setEnabled(false);

                if (blockStatus == logic.FREE_BLOCK) {
                    button.setDefaultText();
                }

                else if (blockStatus == logic.UNAVAILABLE_BLOCK) {
                    button.setBackground(new Color(45, 45, 55, 255));
                    button.setText(" ");
                }

                else if (blockStatus == logic.AI) {
                    button.setText("<html><font color=red>" + "A.I." + "</font></html>");
                    button.setBackground(new Color(100, 150, 250, 255));
                }

                else if (blockStatus == logic.PLAYER) {
                    button.setText("<html><font color = black>YOU</font></html>");
                    button.setBackground(new Color(200, 75, 100, 255));
                }

            }
        }

        updatePotentialPlayerMoves(grid, inputPlayerX, inputPlayerY);

    }


    /* TESTED */
    public void updatePotentialPlayerMoves(int[][] grid, int x, int y) {


        if (logic.isPotentialMove(grid, x + 1, y)) {
            this.buttons[x+1][y].setEnabled(true);

            if (logic.isPotentialMove(grid, x + 2, y)) {
                this.buttons[x+2][y].setEnabled(true);
            }
        }

        if (logic.isPotentialMove(grid, x - 1, y)) {
            this.buttons[x-1][y].setEnabled(true);

            if(logic.isPotentialMove(grid, x - 2, y)) {
                this.buttons[x-2][y].setEnabled(true);
            }
        }

        if (logic.isPotentialMove(grid, x, y + 1)) {
            this.buttons[x][y+1].setEnabled(true);

            if(logic.isPotentialMove(grid, x, y + 2)) {
                this.buttons[x][y+2].setEnabled(true);
            }
        }

        if (logic.isPotentialMove(grid, x, y - 1)) {
            this.buttons[x][y-1].setEnabled(true);

            if(logic.isPotentialMove(grid, x, y - 2)) {
                this.buttons[x][y-2].setEnabled(true);
            }
        }

        if (logic.isPotentialMove(grid, x + 1, y + 1)) {
            this.buttons[x+1][y+1].setEnabled(true);

            if(logic.isPotentialMove(grid, x + 2, y + 2)) {
                this.buttons[x+2][y+2].setEnabled(true);
            }
        }

        if (logic.isPotentialMove(grid, x - 1, y - 1)) {
            this.buttons[x-1][y-1].setEnabled(true);

            if(logic.isPotentialMove(grid, x - 2, y - 2)) {
                this.buttons[x-2][y-2].setEnabled(true);
            }
        }

        if (logic.isPotentialMove(grid, x - 1, y + 1)) {
            this.buttons[x-1][y+1].setEnabled(true);

            if(logic.isPotentialMove(grid, x - 2, y + 2)) {
                this.buttons[x-2][y+2].setEnabled(true);
            }
        }

        if (logic.isPotentialMove(grid, x + 1, y - 1)) {
            this.buttons[x+1][y-1].setEnabled(true);

            if(logic.isPotentialMove(grid, x + 2, y - 2)) {
                this.buttons[x+2][y-2].setEnabled(true);
            }
        }
    }


    /* TESTED */
    public void disableAllButtons() {

        for (Button[] row : this.buttons)         
            for (Button button : row) {

                if (button.isEnabled()) 
                    button.setEnabled(false);
            }
    }


    public void handleGameOver(String msg, String title) {
        JOptionPane.showMessageDialog(frame, msg,
            title, JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
    }

  
    /* TESTED */
    public void showStartOptionsPane() {

        JTextField inputUnavBlocks = new JTextField();
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

        
        if (option == JOptionPane.YES_OPTION) {

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

        else if (option == JOptionPane.NO_OPTION) {
            compStartX = logic.random.nextInt(board.N);
            compStartY = logic.random.nextInt(board.M);

            while(playerStartX != compStartX && 
                    playerStartY != compStartY) {

                        playerStartX = logic.random.nextInt(board.N);
                        playerStartY = logic.random.nextInt(board.M);
                    }
           
            startUnavBlocks = logic.random.nextInt((board.M*board.N) / 2);
        }

        else System.exit(0);
    }
    

    
    public static void main(String[] args) {

        board b = new board();
        b.showStartOptionsPane();
        b.buttons[0][0].setText("A.I.");
        b.setVisible(true);
                
        

    }
}
