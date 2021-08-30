package minimax;

import java.util.Scanner;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Semaphore;

import javax.swing.JOptionPane;
import javax.swing.JTextField;


import javax.swing.JPasswordField;


class minimax {

    public minimax[] potentialMoves = new minimax[16];
    public minimax nextMove;
    public int[][] grid = new int[board.N][board.M];

    int compX, compY;
    int playerX, playerY;


    /* TESTED */
    public void fillGrid(int VALUE) {

        for (int[] row : this.grid)
            Arrays.fill(row, VALUE);
    }
    
}


public class logic {

    
    private static minimax state;
    private static board bo;
    public static Random random = new Random();

    static final int MAX = 1;
    static final int MIN = -1;
    public static final int FREE_BLOCK = 0;
    public static final int  UNAVAILABLE_BLOCK = 1;
    public static final int AI = 2;
    public static final int PLAYER = 3;
    static int turn = MAX;

    private static final int SIMULATION_LIMIT = 4000000;
    private static int simulatedNodes;


    
    /*private static minimax getCurrentState() {
        return state;
    }


    private static board getBoard() {
        return bo;
    }*/


    /* TESTED */
    private static boolean isGameOver(int[][] grid, int x, int y) {

        if (isPotentialMove(grid, x + 1, y) || isPotentialMove(grid, x - 1, y) ||
            isPotentialMove(grid, x, y + 1) || isPotentialMove(grid, x, y - 1) ||
            isPotentialMove(grid, x + 1, y + 1) || isPotentialMove(grid, x - 1, y - 1) ||
            isPotentialMove(grid, x - 1, y + 1) || isPotentialMove(grid, x + 1, y - 1)) {
                //System.out.println("got here");
                return false;
            }
        
        return true;
    }


    /* TESTED */
    public static boolean isPotentialMove(int[][] grid, int x, int y) {

        return (x < board.N && x >= 0 &&
                    y  < board.M && y >= 0 &&
                    grid[x][y] == logic.FREE_BLOCK);
    }


    /* TESTED */
    private static void getStartPositions(board bo, minimax state) {
        
        int compStartX = bo.getCompStartX();
        int compStartY = bo.getCompStartY();
        int playerStartX = bo.getPlayerStartX();
        int playerStartY = bo.getPlayerStartY();
        int startUnavBlocks = bo.getStartUnavBlocks();
        int randomX, randomY;


        if (isPotentialMove(state.grid, compStartX, compStartY)) {
            state.compX = compStartX;
            state.compY = compStartY;
        }
        else {
            state.compX = bo.defaultCompX;
            state.compY = bo.defaultCompY;
        }
    

        if (isPotentialMove(state.grid, playerStartX, playerStartY)) {
            state.playerX = playerStartX;
            state.playerY = playerStartY;
        }
        else {
            state.playerX = bo.defaultPlayerX;
            state.playerY = bo.defaultPlayerY;
        }


        state.grid[state.compX][state.compY] = AI;
        state.grid[state.playerX][state.playerY] = PLAYER;


        if (startUnavBlocks > 0 && startUnavBlocks < (board.M*board.N) / 2) {


            for (int u=0; u < startUnavBlocks; u++) {

                randomX = random.nextInt(board.N);
                randomY = random.nextInt(board.M);

                if (isPotentialMove(state.grid, randomX, randomY)) {
                    state.grid[randomX][randomY] = UNAVAILABLE_BLOCK;
                }


            }
        }
    }


    private static void play() {


        //bo.updateButtonsGrid(currentState.grid);
        //System.out.println(state.compX + "," + state.compY);
        //System.out.println(state.compY);

        /*if (turn == MAX && isGameOver(state.grid, state.compX, state.compY)) {
            bo.handleGameOver("Player wins!", "GAME OVER");
        }

        if (turn == MIN && isGameOver(state.grid, state.playerX, state.playerY)) {
            bo.handleGameOver("A.I. wins!", "GAME OVER");
        }*/

    

        if (turn == MAX) {

            if (isGameOver(state.grid, state.compX, state.compY)) {
                bo.handleGameOver("Player wins!", "GAME OVER");
            }
            else moveAI();
        }
        if (turn == MIN) {

            if (isGameOver(state.grid, state.playerX, state.playerY)) {
                bo.handleGameOver("A.I. wins!", "GAME OVER");
            }
        }


        try {
            Thread.sleep(20);
        }
        catch (Exception e) {

        }

    }


    private static void moveAI() {

        simulatedNodes = 0;
        simulateMinimaxTree(state, MAX);
        state = state.nextMove;

        printGrid(state.grid, "AI moved");

        bo.updateButtonsGrid(state.grid, state.playerX, state.playerY);
        
        turn = MIN;
    }


    /* TESTED */
    public static void movePlayer(int destinationX, int destinationY) {

        int crossedBlockX = state.playerX;
        int crossedBlockY = state.playerY;
        int moveX = destinationX - state.playerX;
        int moveY = destinationY - state.playerY;

        boolean isDoubleMove = (moveX == 2 || moveX == -2 ||
                                                        moveY == 2 || moveY == -2);


        bo.disableAllButtons();

        state.grid[crossedBlockX][crossedBlockY] = UNAVAILABLE_BLOCK;
        state.grid[destinationX][destinationY] = PLAYER;
      
        if (moveX == 2) crossedBlockX = destinationX - 1;
        else if (moveX == -2) crossedBlockX = destinationX + 1;

        if (moveY == 2) crossedBlockY = destinationY -1;
        else if (moveY == -2) crossedBlockY = destinationY +1;

        if (isDoubleMove) 
            state.grid[crossedBlockX][crossedBlockY] = UNAVAILABLE_BLOCK;


        state.playerX = destinationX;
        state.playerY = destinationY;

        
        printGrid(state.grid, "Player moved");
        
        //bo.updateButtonsGrid(state.grid, state.playerX, state.playerY);
        turn = MAX;
    }




    private static int simulateMinimaxTree(minimax tree, int turn) {

        
        int position;
        int bestValue;
        int[] treeValues = new int[16];

        minimax bestMove;
        minimax[] potentialMoves = new minimax[16];


        if (turn == MAX && isGameOver(tree.grid, tree.compX, tree.compY)) {
            return MIN;
        }

        if (turn == MIN && isGameOver(tree.grid, tree.playerX, tree.playerY)) {
            return MAX;
        }

        if (simulatedNodes >= SIMULATION_LIMIT) 
            return turn == MAX ? MIN : MAX;


        position = createNodeChildren(tree, turn, treeValues, potentialMoves);


        bestValue = treeValues[0];
        bestMove = potentialMoves[0];

        if (turn == MAX) {         

            for (int i = 1; i < position; i++) {

                if (treeValues[i] > bestValue) {
                    bestValue = treeValues[i];
                    bestMove = potentialMoves[i];
                }
            }
        }
        else if (turn == MIN) {

            for (int i = 1; i < position; i++) {

                if (treeValues[i] < bestValue) {
                    bestValue = treeValues[i];
                    bestMove = potentialMoves[i];
                }
            } 
        }

        tree.nextMove = bestMove;
        return bestValue;
    }


    private static int createNodeChildren(minimax tree, int turn, 
            int[] treeValues, minimax[] potentialMoves) {

        int currentX, currentY;
        int pos = 0;
        

        if (turn == MAX) {
            currentX = tree.compX;
            currentY = tree.compY;
        }
        else {
            currentX = tree.playerX;
            currentY = tree.playerY;
        }

        if (isPotentialMove(tree.grid, currentX + 1, currentY)) {
            pos += createNodeChild(tree, potentialMoves, turn, treeValues, pos, 1, 0);

            if (isPotentialMove(tree.grid, currentX + 2, currentY)) {
                pos += createNodeChild(tree, potentialMoves, turn, treeValues, pos, 2, 0);
            }
        }

        if (isPotentialMove(tree.grid, currentX - 1, currentY)) {
            pos += createNodeChild(tree, potentialMoves, turn, treeValues, pos, -1, 0);

            if (isPotentialMove(tree.grid, currentX - 2, currentY)) {
                pos += createNodeChild(tree, potentialMoves, turn, treeValues, pos, -2, 0);
            }
        }
        
        if (isPotentialMove(tree.grid, currentX, currentY + 1)) {
            pos += createNodeChild(tree, potentialMoves, turn, treeValues, pos, 0, 1);

            if (isPotentialMove(tree.grid, currentX, currentY + 2)) {
                pos += createNodeChild(tree, potentialMoves, turn, treeValues, pos, 0, 2);
            }
        }

        if (isPotentialMove(tree.grid, currentX, currentY - 1)) {
            pos += createNodeChild(tree, potentialMoves, turn, treeValues, pos, 0, -1);

            if (isPotentialMove(tree.grid, currentX, currentY - 2)) {
                pos += createNodeChild(tree, potentialMoves, turn, treeValues, pos, 0, -2);
            }
        }

        if (isPotentialMove(tree.grid, currentX + 1, currentY + 1)) {
            pos += createNodeChild(tree, potentialMoves, turn, treeValues, pos, 1, 1);

            if (isPotentialMove(tree.grid, currentX + 2, currentY + 2)) {
                pos += createNodeChild(tree, potentialMoves, turn, treeValues, pos, 2, 2);
            }
        }

        if (isPotentialMove(tree.grid, currentX + 1, currentY - 1)) {
            pos += createNodeChild(tree, potentialMoves, turn, treeValues, pos, 1, -1);

            if (isPotentialMove(tree.grid, currentX + 2, currentY - 2)) {
                pos += createNodeChild(tree, potentialMoves, turn, treeValues, pos, 2, -2);
            }
        }

        if (isPotentialMove(tree.grid, currentX - 1, currentY + 1)) {
            pos += createNodeChild(tree, potentialMoves, turn, treeValues, pos, -1, 1);

            if (isPotentialMove(tree.grid, currentX - 2, currentY + 2)) {
                pos += createNodeChild(tree, potentialMoves, turn, treeValues, pos, -2, 2);
            }
        }

        if (isPotentialMove(tree.grid, currentX - 1, currentY - 1)) {
            pos += createNodeChild(tree, potentialMoves, turn, treeValues, pos, -1, -1);

            if (isPotentialMove(tree.grid, currentX - 2, currentY - 2)) {
                pos += createNodeChild(tree, potentialMoves, turn, treeValues, pos, -2, -2);
            }
        }

        return pos;
    }


    private static int createNodeChild(minimax tree, minimax[] potentialMoves, int turn,
            int[] treeValues, int pos, int moveX, int moveY) {

        int crossedBlockX, crossedBlockY;
        int destinationX, destinationY;
        int compMoveX, compMoveY;
        int playerMoveX, playerMoveY;
        int OCCUPANT;
        int playsNext;
        
        boolean isDoubleMove = (moveX == 2 || moveX == -2 ||
                                                        moveY == 2 || moveY == -2);

        
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
        
        tree.potentialMoves[pos] = new minimax();


        copyArray(tree.grid, tree.potentialMoves[pos].grid);
        //tree.potentialMoves[pos].grid = tree.grid;


        tree.potentialMoves[pos].grid[destinationX][destinationY] = OCCUPANT;
        tree.potentialMoves[pos].grid[crossedBlockX][crossedBlockY] = UNAVAILABLE_BLOCK;

        if (moveX == 2) crossedBlockX = destinationX - 1;
        else if (moveX == -2) crossedBlockX = destinationX + 1;

        if (moveY == 2) crossedBlockY = destinationY -1;
        else if (moveY == -2) crossedBlockY = destinationY +1;

        if (isDoubleMove) 
            tree.potentialMoves[pos].grid[crossedBlockX][crossedBlockY] = UNAVAILABLE_BLOCK;

        tree.potentialMoves[pos].compX = tree.compX + compMoveX;
        tree.potentialMoves[pos].compY = tree.compY + compMoveY;
        tree.potentialMoves[pos].playerX = tree.playerX + playerMoveX;
        tree.potentialMoves[pos].playerY = tree.playerY + playerMoveY;
        
        treeValues[pos] = simulateMinimaxTree(tree.potentialMoves[pos], playsNext);
        potentialMoves[pos] = tree.potentialMoves[pos];

        /* TESTING */
        //printGrid(potentialMoves[pos].grid, "child grid");
        /*System.out.println("child playerX:" + tree.potentialMoves[pos].playerX);
        System.out.println("child playerY:" + tree.potentialMoves[pos].playerY);

        System.out.println("sim child playerX:" + potentialMoves[pos].playerX);
        System.out.println("sim child playerY:" + potentialMoves[pos].playerY);

        tree.playerX = tree.potentialMoves[pos].playerX;
        tree.playerY = tree.potentialMoves[pos].playerY;
        tree.compX = tree.potentialMoves[pos].compX;
        tree.compY = tree.potentialMoves[pos].compY;*/

        //pos++;
        simulatedNodes++;

        return 1;
    }

    
    private static void copyArray(int[][] from, int[][] to) {

        for (int i = 0; i < board.N; i++) {
            for (int j = 0; j < board.M; j++) {

                to[i][j] = from[i][j];
            }
        }
    }



    public static void printGrid(int[][] A, String str) // Emfanizei stin othoni ton pinaka paihnidiou
	{
		int i;
		int j;

        System.out.printf(str+":\n");

	   for (i = 0;i < board.N;i++)
	   {
		   for (j = 0;j < board.M;j++)
		   {
                //System.out.print(A[i][j] + " ");


                if (A[i][j] == FREE_BLOCK) {
                    System.out.printf("_ ");
                }
                else if (A[i][j] == UNAVAILABLE_BLOCK) {
                    System.out.printf("X ");
                }
                else if (A[i][j] == AI) {
                    System.out.printf("A ");
                }
                else if (A[i][j] == PLAYER) {
                    System.out.printf("B ");
                }
		   }
		  System.out.printf("\n");
	   }
	   System.out.printf("\n\n\n");
	}



    public static void main(String[] args) {


        /*logic lo = new logic();
        logic.currentState = new minimax();
        logic.bo = new board();

        logic.bo.showStartOptionsPane();
        

        for (int[] row : logic.currentState.grid)
            Arrays.fill(row, logic.FREE_BLOCK);

        logic.currentState.compX = logic.bo.getCompStartX();
        logic.currentState.compY = logic.bo.getCompStartX();
        logic.currentState.grid[logic.currentState.compX][logic.currentState.compY] = AI;

        logic.currentState.playerX = logic.bo.getPlayerStartX();
        logic.currentState.playerY = logic.bo.getPlayerStartY();
        logic.currentState.grid[logic.currentState.playerX][logic.currentState.playerY] = PLAYER;*/

        /* TODO optionally set predetermined unavailable blocks */

        /*
        logic.bo.setVisible(true);
        
        logic.bo.updateButtonsGrid(logic.currentState.grid);

        //lo.play(bo, currentState);

        while (true) lo.play(logic.bo, logic.currentState);
        */

        state = new minimax();
        bo = new board();
        
        /*(int[][] testGrid = { 
            { AI, UNAVAILABLE_BLOCK, FREE_BLOCK, FREE_BLOCK },
            { FREE_BLOCK, PLAYER, FREE_BLOCK, FREE_BLOCK },
            { FREE_BLOCK, UNAVAILABLE_BLOCK, FREE_BLOCK, FREE_BLOCK },
            { FREE_BLOCK, FREE_BLOCK, UNAVAILABLE_BLOCK, FREE_BLOCK }
        };

        int[] treeValues = new int[16];*/

        bo.showStartOptionsPane();
        
        /*System.out.println("\ngetCompStartX:" + bo.getCompStartX());
        System.out.println("getCompStartY:" + bo.getCompStartY());
        System.out.println("getPlayerStartX:" + bo.getPlayerStartX());
        System.out.println("getPlayerStartY:" + bo.getPlayerStartY());*/

        state.fillGrid(FREE_BLOCK);
        getStartPositions(bo, state);



        //printGrid(state.grid, "\ntest");

        bo.setVisible(true);
        bo.updateButtonsGrid(state.grid, state.playerX, state.playerY);
        bo.disableAllButtons();

       //createNodeChild(testState, testState.potentialMoves, MIN, treeValues, 0, 0, -2);
       //simulateMinimaxTree(state, turn);

       //state = state.nextMove;

        /*printGrid(state.grid, "\ntest2");
        System.out.println("playerX:" + state.playerX);
        System.out.println("playerY:" + state.playerY);*/
        

        while(true) play();
        //bo.updateButtonsGrid(state.grid, state.playerX, state.playerY);
        
        
        //createNodeChild(testState, testState.potentialMoves, MAX, treeValues, 0, 0, 2);
        //testBoard.updateButtonsGrid(testState.grid, testState.playerX, testState.playerY);
    }


}


