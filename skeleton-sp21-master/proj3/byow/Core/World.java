package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class World {
    private final int height;
    private final int width;
    private TETile[][] worldMap;

    public World(int width, int height) {
        this.height = height;
        this.width = width;
        this.worldMap = new TETile[width][height];

    }
    public TETile[][] getWorldMap() {
        return worldMap;
    }

    /**
     *
     * @param n seed to create random world
     */
    public void randomMap(long n) {
        Random random =new Random(n);
        //fill the world with nothing
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                worldMap[x][y] = Tileset.NOTHING;
            }
        }
        //create random rooms in the map
        //first step:  create random(4-7] room in the map
        int numOfRoom = random.nextInt(8, 12);
        int num = 0; // number of room already created
        while (num <= numOfRoom) {
            int x = random.nextInt(1, width - 1);
            int y = random.nextInt(1, height - 1);
            position p = new position(x, y);
            int L = random.nextInt(7, width / 3);
            int H = random.nextInt(5, height / 3);
            if (validRoom(p, L, H)) {
                num += 1;
                addRectangularRoom(p, L, H);
            }
        }
    }


    /**
     * Helper class to localise one tile in world:
     * x is weight,
     * y is height
     */
    private static class position {
        int x;
        int y;
        position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    /** Add one Rectangle room in world from position p
     * @param p left-bottom point of the room
     * @param L Length of the room
     * @param H Height of the room
     */
    private void addRectangularRoom(position p, int L, int H) {
        //check if the Rectangle out of world
        if (p.x + L > width || p.y + H > height) {
            throw new IllegalArgumentException("Room out of map");
        }
        //create room closed: outside walls and inside floors, without door
        for (int i = p.x; i < p.x + L; i++) {
            worldMap[i][p.y] = Tileset.WALL;
            worldMap[i][p.y + H - 1] = Tileset.WALL;
        }
        for (int i = p.y; i < p.y + H; i++) {
            worldMap[p.x][i] = Tileset.WALL;
            worldMap[p.x + L - 1][i] = Tileset.WALL;
        }
        for (int i = p.x + 1; i < p.x + L - 1; i++) {
            for (int j = p.y + 1; j < p.y + H - 1; j++) {
                worldMap[i][j] = Tileset.FLOWER;
            }
        }
    }

    /**To make sure if a room duplicate with others
     * @param p given position
     * @param L width of room
     * @param H height of room
     * @return Can we add room in this place with givens arguments
     */
    private boolean validRoom(position p, int L, int H) {

        if (p.x + L > width - 1|| p.y + H > height - 1) {
            return false;
        }
        for (int i = p.x - 1; i <= p.x + L; i++ ) {
            for (int j = p.y - 1; j <= p.y + H; j++) {
                if (!worldMap[i][j].description().equals("nothing")) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        World world = new World(80, 40);
        world.randomMap(462);

        TERenderer ter = new TERenderer();
        ter.initialize(80, 40);
        ter.renderFrame(world.worldMap);
    }


}
