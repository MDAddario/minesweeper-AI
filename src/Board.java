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

    // Complicated constructor
    private Board(int height, int width, int totalBombs) {
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

    private class Tile {

        // Fields
        private int i;
        private int j;
        private boolean isBomb;
        private int numNeighbors;
        private boolean isRevealed;

        // Constructor
        public Tile(boolean isBomb, int i, int j) {
            this.i = i;
            this.j = j;
            this.isBomb = isBomb;
            this.numNeighbors = -1;
            this.isRevealed = false;
        }
    }
}
