package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {

    private static int SizeOfHexagon = 3;


    private static final Random RANDOM = new Random();


    public static void fillWithHexagon(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        // upper middle hexagon beginning point: tiles[(int) Math.ceil(width / 2)][height - 1]
        int middle = (width - 1) / 2;  //middle left point
        int a = middle - (SizeOfHexagon - 1) / 2;
        int b = height - 1;
        int largest = SizeOfHexagon + 2 * (SizeOfHexagon - 1);
        //middle 5 hexagons:
        addHexagon(SizeOfHexagon, tiles, a, b);
        addHexagon(SizeOfHexagon, tiles, a, b - SizeOfHexagon * 2);
        addHexagon(SizeOfHexagon, tiles, a, b - SizeOfHexagon * 4);
        addHexagon(SizeOfHexagon, tiles, a, b - SizeOfHexagon * 6);
        addHexagon(SizeOfHexagon, tiles, a, b - SizeOfHexagon * 8);

        //leftest and rightest hexagon
        //location of left higher hexagon: tiles[a - Size - (Size + 2 * (Size - 1))][b - Size * 2]
        addHexagon(SizeOfHexagon, tiles, a - SizeOfHexagon - largest, b - SizeOfHexagon * 2);
        addHexagon(SizeOfHexagon, tiles, a - SizeOfHexagon - largest, b - SizeOfHexagon * 4);
        addHexagon(SizeOfHexagon, tiles, a - SizeOfHexagon - largest, b - SizeOfHexagon * 6);
        addHexagon(SizeOfHexagon, tiles, a + SizeOfHexagon + largest, b - SizeOfHexagon * 2);
        addHexagon(SizeOfHexagon, tiles, a + SizeOfHexagon + largest, b - SizeOfHexagon * 4);
        addHexagon(SizeOfHexagon, tiles, a + SizeOfHexagon + largest, b - SizeOfHexagon * 6);

        //left middle and right middle hexagon
        //location of left middle higer hexagon: tiles[a - Size - (Size - 1)][b - Size]
        addHexagon(SizeOfHexagon, tiles, a - SizeOfHexagon * 2 + 1, b - SizeOfHexagon);
        addHexagon(SizeOfHexagon, tiles, a - SizeOfHexagon * 2 + 1, b - SizeOfHexagon * 3);
        addHexagon(SizeOfHexagon, tiles, a - SizeOfHexagon * 2 + 1, b - SizeOfHexagon * 5);
        addHexagon(SizeOfHexagon, tiles, a - SizeOfHexagon * 2 + 1, b - SizeOfHexagon * 7);
        addHexagon(SizeOfHexagon, tiles, a + SizeOfHexagon * 2 - 1, b - SizeOfHexagon);
        addHexagon(SizeOfHexagon, tiles, a + SizeOfHexagon * 2 - 1, b - SizeOfHexagon * 3);
        addHexagon(SizeOfHexagon, tiles, a + SizeOfHexagon * 2 - 1, b - SizeOfHexagon * 5);
        addHexagon(SizeOfHexagon, tiles, a + SizeOfHexagon * 2 - 1, b - SizeOfHexagon * 7);

    }
    /**
     * @param N size-N hexagons
     * @return  width and height of the world composed by the size-N hexagons as int[]: index 0 is width; index 1 is height
     */
    private static int[] sizeCalculator(int N) {

        //check the argument
        if (N < 2) {
            throw new IllegalArgumentException("N have to bigger than 1");
        }
        int [] size = new int[2];
        //longest line of the size-N hexagons
        int longest = N + (N - 1) * 2;
        //height and width of the map
        int height = N * 2 * 5;
        int width = longest * 3 + N * 2 ;
        size[0] = width;
        size[1] = height;
        return size;
    }

    /**
     * Add S-size hexagon from one Tile in world
     * @param s side length of hexagon
     * @param tiles world
     * @param a width of beginning tile
     * @param b height of beginning tile
     */
    private static void addHexagon(int s, TETile[][] tiles, int a, int b) {
        //check a, b
        int height = tiles[0].length;
        int width = tiles.length;
        if (a < 0 || b < 0) {
            throw new IllegalArgumentException("location of beginning tile have a positive number");
        }
        if (a + 2 * s - 2 > width || a - s + 1 < 0 || b - 2 * s + 1 < 0) {
            throw new IllegalArgumentException("Hexagon out of map");
        }

        TETile random = randomTile();
        //add upper part of Hexagon
        for (int h = 0; h < s  ; h++) {  //int h is layer index: from 0 to s - 1
            for (int w = 0; w < s + 2 * h; w++) { // int w is 0 to length of this layer: index in this layer
                tiles[a - h + w][b - h] = random;
            }
        }
        //add lower part of Hexagon
        //tiles[a-s+1][b-s] is beginning of lower part
        for (int h = 0; h < s; h++) {  //there are s layers in lower part
            for (int w = 0; w < s + 2 * (s - 1) - h * 2; w++) { //number of every layer in lower part
                tiles[a - s + 1 + h + w][b - s - h] = random;
            }
        }
    }

    /** Picks a RANDOM tile
     */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(5);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.GRASS;
            case 3: return Tileset.WATER;
            case 4: return Tileset.AVATAR;
            case 5: return Tileset.SAND;
            default: return Tileset.NOTHING;
        }
    }

    public static void main(String[] args) {
        // initialize the tile rendering engine with a windows of size WIDTH x HEIGHT
        int[] volume = sizeCalculator(SizeOfHexagon);
        int WIDTH = volume[0];
        int HEIGHT = volume[1];

        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        //fill the world with nothing
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        fillWithHexagon(world);

        ter.renderFrame(world);
    }
}
