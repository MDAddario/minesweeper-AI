import java.util.Random;

public class Board {

    // Fields
    private int height;
    private int width;
    private int totalBombs;
    private int concealedBombs;
    private Tile[][] tileArray;

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
            throw new RuntimeException("There must be more tiles than there are bombs");
        this.height = height;
        this.width = width;
        this.totalBombs = totalBombs;
        this.constructTiles();
    }

    // Construct the array of tiles
    private void constructTiles() {

        // Create proxy board to keep track of bomb locations
        boolean[][] isBomb = new boolean[this.height][this.width];

        // Prepare the random number generator
        Random rand = new Random();

        // Populate the board with bombs
        this.concealedBombs = 0;
        while (this.concealedBombs < this.totalBombs) {

            int i = rand.nextInt(this.height);
            int j = rand.nextInt(this.width);

            if (!isBomb[i][j]){
                isBomb[i][j] = true;
                this.concealedBombs++;
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

    public void printBoard(boolean revealAll) {

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
                    else
                        System.out.print(" . ");
                }
            }
            System.out.println("|");
        }
    }

    private class Tile {

        // Fields
        private int i;
        private int j;
        private boolean isBomb;
        private int numNeighbors;
        private boolean isRevealed;

        // Constructor
        private Tile(boolean isBomb, int i, int j) {
            this.i = i;
            this.j = j;
            this.isBomb = isBomb;
            this.numNeighbors = 0;
            this.isRevealed = false;
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
