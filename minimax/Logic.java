package minimax;

import java.util.Arrays;
import java.util.Random;


/* At the root of the package, compile with:
    javac minimax/Logic.java

    and run with:
    java minimax/Logic

    jar executable can be created with the following command:
    jar cvfe play.jar minimax.Logic minimax/*.class 
*/


/**
 *      MinimaxTree Class describing a state in a minimax tree.
 */
class MinimaxTree {

    /* Each state in the tree can have a maximum of 16 children 
        since  that is the number of the maximum potential moves */
    public MinimaxTree[] potentialMoves = new MinimaxTree[16];

    /* Extra field used to save the move that was determined as best */
    public MinimaxTree nextMove;

    /* 
        Array used to describe the current state of the game.
        Each position in the array has a value that corresponds to one of
        the following: 
            Player's position
            AI position
            Free block
            Unavailable block
        These values are defined as constants in the Logic Class.
    */
    public int[][] grid = new int[Board.N][Board.M];

    /* player and AI coordinates in the grid */
    int compX, compY;
    int playerX, playerY;


    /**
     * Fills the grid with the given value.
     * 
     * @param VALUE: Value that describes the state of a block in the grid.
     */
    public void fillGrid(int VALUE) {

        for (int[] row : this.grid)
            Arrays.fill(row, VALUE);
    }
    
}


/**
 *      Logic Class implements the recursive Minimax algorithm which is 
 *      used by the AI  to determine it's next best move as well as any other
 *      back-end logicc required for the game to function.
 */
public class Logic {


    /* The current state of the game is stored here */
    private static MinimaxTree state;

    /* GUI */
    private static Board board;

    /* For any random values required */
    public static Random random = new Random();

    /* constants for the minimax algorithm */
    static final int MAX = 1;
    static final int MIN = -1;

    /* Constants that describe the state/occupant of a 
    position in the grid that describes the game */
    public static final int FREE_BLOCK = 0;
    public static final int  UNAVAILABLE_BLOCK = 1;
    public static final int AI = 2;
    public static final int PLAYER = 3;

    /* Who plays first? */
    static int turn = MAX;

    /* 
        !!! CAUTION !!!
        SIMULATION_LIMIT is used to limit the depth of the simulation to the given number.
        Reducing the value saves RAM and CPU usage but makes the algorithm less 'smart'.
        Increasing the value makes the algorithm perform better but can be very resource-heavy 
        and potentially lead to a crash, especially if the board (grid) size is increased too.
        The current value (4000000) with a 5x5 grid seem to be working okay with an AMD Ryzen 5
        and 15GB of RAM. Generally, a 3x3 grid (or maybe 3x4?) seems to be safe as the algorithm 
        doesn't have that many potential scenarios to simulate. 
    */
    private static final int SIMULATION_LIMIT = 4000000;
    private static int simulatedNodes;



    /**
     *      Method responsible for checking whether there are any available moves 
     *      for the given x and y coordinates. 
     * 
     *      @param grid: Array used to describe the current state of the game.
     *      @param x:  X coordinate.
     *      @param y:  Y coordinate.
     *      @return true for game over else false.
     */
    private static boolean isGameOver(int[][] grid, int x, int y) {

        if (isPotentialMove(grid, x + 1, y) || isPotentialMove(grid, x - 1, y) ||
            isPotentialMove(grid, x, y + 1) || isPotentialMove(grid, x, y - 1) ||
            isPotentialMove(grid, x + 1, y + 1) || isPotentialMove(grid, x - 1, y - 1) ||
            isPotentialMove(grid, x - 1, y + 1) || isPotentialMove(grid, x + 1, y - 1)) {
                return false;
            }

        return true;
    }


    /**
     *      Checks whether the given x and y coordinates are a valid potential move.
     *      The coordinates must be within the grid's limit and the block in the grid 
     *      they point to must be a free block.
     * 
     *      @param grid: Array used to describe the current state of the game.
     *      @param x:  X coordinate.
     *      @param y:  Y coordinate.
     *      @return true for a valid potential move else false.
     */
    public static boolean isPotentialMove(int[][] grid, int x, int y) {

        return (x < Board.N && x >= 0 &&
                    y  < Board.M && y >= 0 &&
                    grid[x][y] == Logic.FREE_BLOCK);
    }


    /**
     *      Passes the initial options for the game to the back-end logic.
     * 
     *      @param board: the GUI.
     *      @param state: the current state of the game.
     */
    private static void getStartPositions(Board board, MinimaxTree state) {
        
        int compStartX = board.getCompStartX();
        int compStartY = board.getCompStartY();
        int playerStartX = board.getPlayerStartX();
        int playerStartY = board.getPlayerStartY();
        int startUnavBlocks = board.getStartUnavBlocks();
        int randomX, randomY;


        /* Ensure that the coordinates correspond to a free valid block 
            before updating the current state. Alternatively, the default values are used.*/
        if (isPotentialMove(state.grid, compStartX, compStartY)) {
            state.compX = compStartX;
            state.compY = compStartY;
        }
        else {
            state.compX = board.defaultCompX;
            state.compY = board.defaultCompY;
        }
    

        if (isPotentialMove(state.grid, playerStartX, playerStartY)) {
            state.playerX = playerStartX;
            state.playerY = playerStartY;
        }
        else {
            state.playerX = board.defaultPlayerX;
            state.playerY = board.defaultPlayerY;
        }


        state.grid[state.compX][state.compY] = AI;
        state.grid[state.playerX][state.playerY] = PLAYER;


        /* Add some unavailable blocks */
        if (startUnavBlocks > 0 && startUnavBlocks < (Board.M*Board.N) / 2) {

            for (int u=0; u < startUnavBlocks; u++) {

                randomX = random.nextInt(Board.N);
                randomY = random.nextInt(Board.M);

                if (isPotentialMove(state.grid, randomX, randomY)) {
                    state.grid[randomX][randomY] = UNAVAILABLE_BLOCK;
                }


            }
        }
    }



    /**
     *  Main method that handles the flow of the game.
     */
    private static void play() {

    
        /* Max (AI) is playing */
        if (turn == MAX) {

            /* First check for game over */
            if (isGameOver(state.grid, state.compX, state.compY)) {
                board.handleGameOver("Player wins!", "GAME OVER");
            }
            /* and make a move */
            else moveAI();
        }
        /* MIN (player) is playing */
        if (turn == MIN) {

            /* Check for game over */
            if (isGameOver(state.grid, state.playerX, state.playerY)) {
                board.handleGameOver("A.I. wins!", "GAME OVER");
            }
            /* 
                Note that the movePlayer method which moves the player is not invoked here.
                It is invoked in the ActionListener of the button pressed. Synchronization is
                achieved by generally having buttons disabled. When it is the player's turn only 
                the buttons that correspond to valid moves are enabled, this clickable. Therefore,
                the player can only select valid moves and cannot select more than one move 
                which would create bugs.
            */
        }

        /* Add some delay */
        try {
            Thread.sleep(20);
        }
        catch (Exception e) {

        }

    }


    /**
     * Decides which is the best move for the AI and moves it accordingly
     */
    private static void moveAI() {

        /* Reset the total of simulated nodes and start the simulation */
        simulatedNodes = 0;
        simulateMinimaxTree(state, MAX);

        /* 
            Once the minimax simulation is finished the best move will be
            in the nextMove field of the current state so we pass it as the new
            current state.
        */
        state = state.nextMove;

        /* Print the grid on the terminal for debugging purposes */
        printGrid(state.grid, "AI moved");

        /* Update the GUI buttons */
        board.updateButtonsGrid(state.grid, state.playerX, state.playerY);
        
        /* It is now MIN's turn (player) to play */
        turn = MIN;
    }


    /**
     *      Moves the player to the selected destination and also makes any passed 
     *      blocks unavailable.
     * 
     *      @param destinationX: the destination X coordinate.
     *      @param destinationY: the destination Y coordinate.
     */
    public static void movePlayer(int destinationX, int destinationY) {

        /* The origin position will become unavailable for sure */
        int crossedBlockX = state.playerX;
        int crossedBlockY = state.playerY;

        /* Find the difference between origin and destination*/
        int moveX = destinationX - state.playerX;
        int moveY = destinationY - state.playerY;

        /* Based on the above difference determine whether it is a double move */
        boolean isDoubleMove = (moveX == 2 || moveX == -2 ||
                                                        moveY == 2 || moveY == -2);

        /* 
            If the code gets to this points it is safe to assume that the player has chosen (clicked)
            his move, therefore, we are disabling all the buttons to prevent the player from
            making another move until its the player's turn again.
        */
        board.disableAllButtons();


        /* Update the grid of the current state */
        state.grid[crossedBlockX][crossedBlockY] = UNAVAILABLE_BLOCK;
        state.grid[destinationX][destinationY] = PLAYER;
     
        
        /* In case of a double move figure out the other crossed block */
        if (moveX == 2) crossedBlockX = destinationX - 1;
        else if (moveX == -2) crossedBlockX = destinationX + 1;

        if (moveY == 2) crossedBlockY = destinationY -1;
        else if (moveY == -2) crossedBlockY = destinationY +1;

        /* Update the grid once again*/
        if (isDoubleMove) 
            state.grid[crossedBlockX][crossedBlockY] = UNAVAILABLE_BLOCK;


        /* Save the new player coordinates too */
        state.playerX = destinationX;
        state.playerY = destinationY;


        /* Print the grid on the terminal for debugging purposes */
        printGrid(state.grid, "Player moved");
        
         /*
            Update the GUI buttons and disable the buttons again to ensure they
            remain unclickable while the AI is playing.
        */
        board.updateButtonsGrid(state.grid, state.playerX, state.playerY);
        board.disableAllButtons();

        /* It is now MAX's (AI) turn to play */
        turn = MAX;
    }



    /**
     *      Main method responsible for simulating the minimax tree. The children of the 
     *      given tree are created using recursion. In ideal conditions, the recursion stops 
     *      when a leaf child is created. However, as that might take too much time and
     *      computer resources the simulation is limited by the constant SIMULATION_LIMIT.
     *      The constant's value can be changed at the top of the Logic Class.
     * 
     *      @param tree: Minimax tree describing a state.
     *      @param turn: Who is playing? (MIN or MAX)
     *      @return the value on the tree determined as best
     */
    private static int simulateMinimaxTree(MinimaxTree tree, int turn) {

        
        int childrenCount;
        int bestValue;
        int[] treeValues = new int[16];


        /* Each state in the tree can have a maximum of 16 children 
        since  that is the number of the maximum potential moves */
        MinimaxTree[] potentialMoves = new MinimaxTree[16];

         /* Extra field used to save the move that was determined as best */
        MinimaxTree bestMove;


        /* First check for a leaf child */
        if (turn == MAX && isGameOver(tree.grid, tree.compX, tree.compY)) {
            return MIN;
        }

        if (turn == MIN && isGameOver(tree.grid, tree.playerX, tree.playerY)) {
            return MAX;
        }

        /* Then check if the simulation limit has been reached */
        if (simulatedNodes >= SIMULATION_LIMIT) 
            return turn == MAX ? MIN : MAX;


        /* If the code gets here we can proceed with the simulation by creating more children */
        childrenCount = createNodeChildren(tree, turn, treeValues, potentialMoves);


        /* 
            At this point the children of the node have been created so all we have to do is find the
            best value among the values of the tree. 
        */
        bestValue = treeValues[0];
        bestMove = potentialMoves[0];

        if (turn == MAX) {         

            for (int i = 1; i < childrenCount; i++) {

                /* Greater than for MAX */
                if (treeValues[i] > bestValue) {
                    bestValue = treeValues[i];
                    bestMove = potentialMoves[i];
                }
            }
        }
        else if (turn == MIN) {

            for (int i = 1; i < childrenCount; i++) {

                /* Lesser than for MIN */
                if (treeValues[i] < bestValue) {
                    bestValue = treeValues[i];
                    bestMove = potentialMoves[i];
                }
            } 
        }

        /* Save the best move and return the best value */
        tree.nextMove = bestMove;
        return bestValue;
    }



    /**
     *      Creates the children of the given tree node. Checks the potential moves 
     *      and if a move is valid creates the respective child.
     * 
     *      For a move to be valid the destination block and any crossed block must 
     *      be free. Potential moves are 1 or 2 blocks in the following directions:
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
     *      @param tree: Minimax tree describing a state.
     *      @param turn: Who is playing? (MIN or MAX)
     *      @param treeValues: Array with the values of the children created.
     *      @param potentialMoves: Array of Minimax trees describing the children.
     *      @return the total count of children created
     */
    private static int createNodeChildren(MinimaxTree tree, int turn, 
            int[] treeValues, MinimaxTree[] potentialMoves) {

        int currentX, currentY;
        int childrenCount = 0;
        
        /* First check who is playing */
        if (turn == MAX) {
            currentX = tree.compX;
            currentY = tree.compY;
        }
        else {
            currentX = tree.playerX;
            currentY = tree.playerY;
        }

        /* The check potential moves and create children*/

        /* 1 block DOWN */
        if (isPotentialMove(tree.grid, currentX + 1, currentY)) {
            childrenCount += createNodeChild(tree, potentialMoves, turn, treeValues, childrenCount, 1, 0);

            /* 2 blocks DOWN */
            if (isPotentialMove(tree.grid, currentX + 2, currentY)) {
                childrenCount += createNodeChild(tree, potentialMoves, turn, treeValues, childrenCount, 2, 0);
            }
        }

        /* 1 block UP */
        if (isPotentialMove(tree.grid, currentX - 1, currentY)) {
            childrenCount += createNodeChild(tree, potentialMoves, turn, treeValues, childrenCount, -1, 0);

            /* 2 blocks UP */
            if (isPotentialMove(tree.grid, currentX - 2, currentY)) {
                childrenCount += createNodeChild(tree, potentialMoves, turn, treeValues, childrenCount, -2, 0);
            }
        }
        
        /* 1 block RIGHT */
        if (isPotentialMove(tree.grid, currentX, currentY + 1)) {
            childrenCount += createNodeChild(tree, potentialMoves, turn, treeValues, childrenCount, 0, 1);

            /* 2 blocks RIGHT */
            if (isPotentialMove(tree.grid, currentX, currentY + 2)) {
                childrenCount += createNodeChild(tree, potentialMoves, turn, treeValues, childrenCount, 0, 2);
            }
        }

        /* 1 block LEFT */
        if (isPotentialMove(tree.grid, currentX, currentY - 1)) {
            childrenCount += createNodeChild(tree, potentialMoves, turn, treeValues, childrenCount, 0, -1);

            /* 2 blocks LEFT */
            if (isPotentialMove(tree.grid, currentX, currentY - 2)) {
                childrenCount += createNodeChild(tree, potentialMoves, turn, treeValues, childrenCount, 0, -2);
            }
        }

        /* 1 block DOWN RIGHT */
        if (isPotentialMove(tree.grid, currentX + 1, currentY + 1)) {
            childrenCount += createNodeChild(tree, potentialMoves, turn, treeValues, childrenCount, 1, 1);

            /* 2 blocks DOWN RIGHT */
            if (isPotentialMove(tree.grid, currentX + 2, currentY + 2)) {
                childrenCount += createNodeChild(tree, potentialMoves, turn, treeValues, childrenCount, 2, 2);
            }
        }

        /* 1 block DOWN LEFT */
        if (isPotentialMove(tree.grid, currentX + 1, currentY - 1)) {
            childrenCount += createNodeChild(tree, potentialMoves, turn, treeValues, childrenCount, 1, -1);

            /* 2 blocks DOWN LEFT */
            if (isPotentialMove(tree.grid, currentX + 2, currentY - 2)) {
                childrenCount += createNodeChild(tree, potentialMoves, turn, treeValues, childrenCount, 2, -2);
            }
        }

        /* 1 block UP RIGHT */
        if (isPotentialMove(tree.grid, currentX - 1, currentY + 1)) {
            childrenCount += createNodeChild(tree, potentialMoves, turn, treeValues, childrenCount, -1, 1);

            /* 2 blocks UP RIGHT */
            if (isPotentialMove(tree.grid, currentX - 2, currentY + 2)) {
                childrenCount += createNodeChild(tree, potentialMoves, turn, treeValues, childrenCount, -2, 2);
            }
        }

        /* 1 block UP LEFT */
        if (isPotentialMove(tree.grid, currentX - 1, currentY - 1)) {
            childrenCount += createNodeChild(tree, potentialMoves, turn, treeValues, childrenCount, -1, -1);

            /* 2 blocks UP LEFT */
            if (isPotentialMove(tree.grid, currentX - 2, currentY - 2)) {
                childrenCount += createNodeChild(tree, potentialMoves, turn, treeValues, childrenCount, -2, -2);
            }
        }

        return childrenCount;
    }



    /**
     *      Creates a child of the given tree node based on the turn and the move coordinates.
     * 
     *      @param tree: Minimax tree describing a state.
     *      @param potentialMoves: Array of Minimax trees describing the children.
     *      @param turn: Who is playing? (MIN or MAX)
     *      @param treeValues: Array with the values of the children created.
     *      @param child: children counter
     *      @param moveX: move X coordinate.
     *      @param moveY: move Y coordinate.
     * 
     *      Note that the move coordinates are the move itself (1,-1, 2, -2) not the destination.
     * 
     *      @return 1 which signifies one more child created.
     */
    private static int createNodeChild(MinimaxTree tree, MinimaxTree[] potentialMoves, int turn,
            int[] treeValues, int child, int moveX, int moveY) {

        int crossedBlockX, crossedBlockY;
        int destinationX, destinationY;
        int compMoveX, compMoveY;
        int playerMoveX, playerMoveY;
        int OCCUPANT;
        int playsNext;
        
        /* Determine whether it's a double move */
        boolean isDoubleMove = (moveX == 2 || moveX == -2 ||
                                                        moveY == 2 || moveY == -2);

        
        /* First change values according to who is playing */
        if (turn == MAX) {
            playsNext = MIN;
            compMoveX = moveX;
            compMoveY = moveY;
            playerMoveX = 0;
            playerMoveY = 0;
            destinationX = tree.compX + moveX;
            destinationY = tree.compY + moveY;
            crossedBlockX = tree.compX;
            crossedBlockY = tree.compY;
            OCCUPANT = AI;
        }
        else {
            playsNext = MAX;
            compMoveX = 0;
            compMoveY = 0;
            playerMoveX = moveX;
            playerMoveY = moveY;
            destinationX = tree.playerX + moveX;
            destinationY = tree.playerY + moveY;
            crossedBlockX = tree.playerX;
            crossedBlockY = tree.playerY;
            OCCUPANT = PLAYER;
        }
        
        /* Create the child and store it as one of the potential moves of the current state */
        tree.potentialMoves[child] = new MinimaxTree();

        /* Copy the grid of the current state to the child's grid */
        copyArray(tree.grid, tree.potentialMoves[child].grid);


        /* Update the child's grid with the new move */
        tree.potentialMoves[child].grid[destinationX][destinationY] = OCCUPANT;
        tree.potentialMoves[child].grid[crossedBlockX][crossedBlockY] = UNAVAILABLE_BLOCK;


        /* In case of a double move figure out the other crossed block */
        if (moveX == 2) crossedBlockX = destinationX - 1;
        else if (moveX == -2) crossedBlockX = destinationX + 1;

        if (moveY == 2) crossedBlockY = destinationY -1;
        else if (moveY == -2) crossedBlockY = destinationY +1;

        /* Update the grid once again*/
        if (isDoubleMove) 
            tree.potentialMoves[child].grid[crossedBlockX][crossedBlockY] = UNAVAILABLE_BLOCK;


        /* Save the new coordinates to the child */
        tree.potentialMoves[child].compX = tree.compX + compMoveX;
        tree.potentialMoves[child].compY = tree.compY + compMoveY;
        tree.potentialMoves[child].playerX = tree.playerX + playerMoveX;
        tree.potentialMoves[child].playerY = tree.playerY + playerMoveY;
        
        /* Get the child's tree value by running another simulation.
            Creating this child's children until a leaf child is reached. */
        treeValues[child] = simulateMinimaxTree(tree.potentialMoves[child], playsNext);

        /* Update the current state's (parent node) potential moves */
        potentialMoves[child] = tree.potentialMoves[child];

        /* Update the total count of simulated nodes for the simulation limit */
        simulatedNodes++;

        return 1;
    }

    
    /**
     *      Copy array A to B
     * 
     * @param A: 2d array of ints
     * @param B; 2d array of ints
     */
    private static void copyArray(int[][] A, int[][] B) {

        for (int i = 0; i < Board.N; i++) {
            for (int j = 0; j < Board.M; j++) {

                B[i][j] = A[i][j];
            }
        }
    }


    /**
     *      Testing method. Prints the grid to the terminal.
     * 
     * @param grid: Array used to describe the current state of the game.
     * @param str: Message to be displayed along with the grid.
     */
    public static void printGrid(int[][] grid, String str)
	{

        String buf = str + ":\n";

	   for (int i = 0; i < Board.N; i++) {
		   for (int j = 0; j < Board.M; j++) {

                if (grid[i][j] == FREE_BLOCK) {
                    buf += "_ ";
                }
                else if (grid[i][j] == UNAVAILABLE_BLOCK) {
                    buf += "X ";
                }
                else if (grid[i][j] == AI) {
                    buf += "A ";
                }
                else if (grid[i][j] == PLAYER) {
                    buf += "B ";
                }
		   }
		  buf += "\n";
	   }
	   System.out.print(buf + "\n\n\n");
	}


    /**
     *      Handles the inital user input and starts the game.
     */
    private static void initialize() {

        state = new MinimaxTree();
        board = new Board();
        
        board.showStartOptionsPane();


        state.fillGrid(FREE_BLOCK);
        getStartPositions(board, state);


        board.setVisible(true);
        board.updateButtonsGrid(state.grid, state.playerX, state.playerY);
        board.disableAllButtons();
    
    }


    public static void main(String[] args) {

        initialize();

        while(true) play();

    }


}


