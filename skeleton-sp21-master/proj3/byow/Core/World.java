package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
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
        int numOfRoom = random.nextInt(8, 12);
        ArrayList<Room> rooms = new ArrayList<Room>();
        int num = 0; // number of room already created
        while (num <= numOfRoom) {
            int x = random.nextInt(1, width - 1);
            int y = random.nextInt(1, height - 1);
            position p = new position(x, y);
            int L = random.nextInt(7, width / 3);
            int H = random.nextInt(5, height / 3);
            Room room = new Room(p, L, H);
            if (validRoom(room)) {
                num += 1;
                rooms.add(room);
                addRectangularRoom(room);
            }
        }
        //TODO 有了room这个class，可以更容易获得每个room的信息了
    }
    public static class Room {
        private static position position;
        private static int length;
        private static int height;

        public Room(position p, int L, int H) {
            this.position = p;
            this.length = L;
            this.height = H;
        }
        public ArrayList<position> getPositions() {
            ArrayList<position> positions = new ArrayList<position>();
            for (int i = position.x; i < position.x + length; i++) {
                for (int j = position.y; j < position.y + height; j++) {
                    position temp = new position(i, j);
                    positions.add(temp);
                }
            }
            return positions;
        }
        public ArrayList<position> getWalls() {
            ArrayList<position> walls = new ArrayList<position>();
            for (position a : this.getPositions()) {
                if (a.x == position.x || a.x == position.x + length - 1
                        || a.y == position.y || a.y == position.y + height - 1) {
                    walls.add(a);
                }
            }
            return walls;
        }
        //TODO 给room加一个开门（与外界联通）的功能：可以开锁着的门和非锁着的门。arguments是point（检查是否是墙）和TETile（确定材质）

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

    public void draw(position p, TETile tile) {
        worldMap[p.x][p.y] = tile;
    }

    /** Add one Rectangle room in world from position p
     * @param room this room
     */
    private void addRectangularRoom(Room room) {
        position p = room.position;
        int L = room.length;
        int H = room.height;
        //check if the Rectangle out of world
        if (p.x + L > width || p.y + H > height) {
            throw new IllegalArgumentException("Room out of map");
        }
        //create room closed: outside walls and inside floors, without door
        for (position position : room.getPositions()) {
            TETile floor = Tileset.FLOOR;
            draw(position, floor);
        }
        for (position position : room.getWalls()) {
            TETile wall = Tileset.WALL;
            draw(position, wall);
        }
    }

    /**To make sure if a room duplicate with others
     * @param room
     * @return Can we add room in this place with givens arguments
     */
    private boolean validRoom(Room room) {
        position p = room.position;
        int L = room.length;
        int H = room.height;

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
        world.randomMap(4555);

        TERenderer ter = new TERenderer();
        ter.initialize(80, 40);
        ter.renderFrame(world.worldMap);
    }


}
