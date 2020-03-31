import java.util.Random;
import java.util.Scanner;

public class Board {

    public static void main(String[] args) {

        // Bring in a scanner
        Scanner scan = new Scanner(System.in);

        // Create the board
        Board myBoard = new Board(5, 5, 2);

        do {
            // Print board
            myBoard.printBoard(true);
            myBoard.printBoard(false);

            // Receive input and make move
            System.out.println("r=0/f=1, i, j");
            int type = scan.nextInt();
            int i = scan.nextInt();
            int j = scan.nextInt();

            if (type == 0)
                myBoard.revealTile(i, j);
            else if (type == 1)
                myBoard.flagTile(i, j);
            else
                System.out.println("Invalid input.");

        } while (myBoard.isActive);
    }

    // Fields
    private int height;
    private int width;
    private int totalBombs;
    private int revealedTiles;
    private Tile[][] tileArray;
    private boolean firstMove;
    private boolean isActive;

    // Simplest constructor with default values
    private Board() {
        this.height = 4;
        this.width = 10;
        this.totalBombs = 10;
        this.constructTiles();
    }

    // Complicated constructor without default values
    public Board(int height, int width, int totalBombs) {

        // Check for nonsense
        if (totalBombs >= height * width)
            throw new RuntimeException("There must be more tiles than there are bombs.");
        this.height = height;
        this.width = width;
        this.totalBombs = totalBombs;
        this.constructTiles();
    }

    // Construct the array of tiles
    private void constructTiles() {

        // Fresh board
        this.firstMove = true;
        this.isActive = true;
        this.revealedTiles = 0;

        // Create proxy board to keep track of bomb locations
        boolean[][] isBomb = new boolean[this.height][this.width];

        // Prepare the random number generator
        Random rand = new Random();

        // Populate the board with bombs
        int placedBombs = 0;
        while (placedBombs < this.totalBombs) {

            int i = rand.nextInt(this.height);
            int j = rand.nextInt(this.width);

            if (!isBomb[i][j]){
                isBomb[i][j] = true;
                placedBombs++;
            }
        }

        // Create the actual representation of tiles
        this.tileArray = new Tile[this.height][this.width];
        for (int i = 0; i < this.height; i++)
            for (int j = 0; j < this.width; j++)
                this.tileArray[i][j] = new Tile(isBomb[i][j], i, j);

        // Compute the number of bomb neighbors
        for (int i = 0; i < this.height; i++)
            for (int j = 0; j < this.width; j++)
                this.tileArray[i][j].countNeighbors();
    }

    public void revealTile(int i, int j) {

        // Make sure game is active
        if (!this.isActive) {
            System.out.println("Game must be active to play.");
            return;
        }

        // Make sure tile in bounds
        if (i < 0 || i >= this.height || j < 0 || j >= this.width) {
            System.out.println("Tile location value not in bounds.");
            return;
        }

        // Make sure tile is not already revealed
        if (this.tileArray[i][j].isRevealed)
            return;

        // Make sure tile is not flagged
        if (this.tileArray[i][j].isFlagged)
            return;

        // Flip the tile!
        this.tileArray[i][j].isRevealed = true;
        this.revealedTiles++;

        // Valley propagate
        if (!this.tileArray[i][j].isBomb && this.tileArray[i][j].numNeighbors == 0){

            // Flip all tiles within a 2 manhattan distance
            for (int di = -1; di <= 1; di++)
                for (int dj = -1; dj <= 1; dj++){

                    // Ensure indices in bounds
                    int iNew = i + di;
                    int jNew = j + dj;

                    if (iNew < 0 || iNew >= this.height || jNew < 0 || jNew >= this.width)
                        continue;

                    // Flip the tile
                    this.revealTile(iNew, jNew);
                }
        }

        // Check for bomb!
        if (this.tileArray[i][j].isBomb){

            // If first move, just reset
            if (this.firstMove) {
                this.constructTiles();
                this.revealTile(i, j);

            } else {
                this.loseGame();
            }
        // No bomb
        } else {
            this.firstMove = false;
            this.checkVictory();
        }
    }

    public void flagTile(int i, int j) {

        // Make sure game is active
        if (!this.isActive)
            throw new RuntimeException("Game must be active to play.");

        // Make sure tile in bounds
        if (i < 0 || i >= this.height || j < 0 || j >= this.width)
            throw new RuntimeException("Tile location value not in bounds.");

        // Make sure tile is not revealed
        if (this.tileArray[i][j].isRevealed)
            throw new RuntimeException("Tile is revealed.");

        // Toggle flag status
        this.tileArray[i][j].isFlagged = !this.tileArray[i][j].isFlagged;
    }

    private void checkVictory() {

        // Make sure enough tiles have been revealed
        if (this.revealedTiles == this.height * this.width - this.totalBombs){
            this.isActive = false;
            System.out.println("You have won Minesweeper!");
        }
    }

    private void loseGame() {

        // Make the player feel bad
        this.isActive = false;
        System.out.println("You have lost Minesweeper!");
    }

    public void printBoard(boolean revealAll) {

        System.out.print("/");
        for (int j = 0; j < this.width; j++) System.out.print("---");
        System.out.println("\\");

        for (int i = 0; i < this.height; i++) {

            System.out.print("|");

            for (int j = 0; j < this.width; j++) {

                if (revealAll) {

                    if (this.tileArray[i][j].isBomb)
                        System.out.print(" B ");
                    else
                        System.out.print(" " + this.tileArray[i][j].numNeighbors + " ");

                } else {

                    if (this.tileArray[i][j].isRevealed)
                        System.out.print(" " + this.tileArray[i][j].numNeighbors + " ");
                    else if (this.tileArray[i][j].isFlagged)
                        System.out.print(" F ");
                    else
                        System.out.print(" . ");
                }
            }
            System.out.println("|");
        }
        System.out.print("\\");
        for (int j = 0; j < this.width; j++) System.out.print("---");
        System.out.println("/");
    }

    private class Tile {

        // Fields
        private int i;
        private int j;
        private boolean isBomb;
        private int numNeighbors;
        private boolean isRevealed;
        private boolean isFlagged;

        // Constructor
        private Tile(boolean isBomb, int i, int j) {
            this.i = i;
            this.j = j;
            this.isBomb = isBomb;
            this.numNeighbors = 0;
            this.isRevealed = false;
            this.isFlagged = false;
        }

        // Determine the number of bomb neighbors
        private void countNeighbors() {

            // Don't bother counting if the tile is a bomb
            if (this.isBomb)
                return;

            // Count all tiles within a 2 manhattan distance
            for (int di = -1; di <= 1; di++)
                for (int dj = -1; dj <= 1; dj++){

                    // Ensure indices in bounds
                    int iNew = this.i + di;
                    int jNew = this.j + dj;

                    if (iNew < 0 || iNew >= Board.this.height || jNew < 0 || jNew >= Board.this.width)
                        continue;

                    if (Board.this.tileArray[iNew][jNew].isBomb)
                        this.numNeighbors++;
                }
        }
    }
}
