package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Random;

public class World {
    private static int height = 0;
    private static int width = 0;
    private static TETile[][] worldMap;

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
        int numOfRoom = random.nextInt(5, 9);
        ArrayList<Room> rooms = new ArrayList<Room>();
        int num = 0; // number of room already created
        while (num <= numOfRoom) {
            int x = random.nextInt(1, width - 1);
            int y = random.nextInt(1, height - 1);
            position p = new position(x, y);
            int L = random.nextInt(8, width / 3);
            int H = random.nextInt(8, height / 3 );
            Room room = new Room(p, L, H);
            if (validRoom(room)) {
                addRectangularRoom(room);
                rooms.add(room);
                num += 1;
            }
        }
        // set the door for every room
        TETile tile = Tileset.UNLOCKED_DOOR;
        for (Room r : rooms) {
            r.setRandomUnlockedDoors(n);
        }
    }
    public class Room {
        private final position position;
        private final int length;
        private final int height;
        private ArrayList<position> doors;

        public Room(position p, int L, int H) {
            this.position = p;
            this.length = L;
            this.height = H;
            this.doors = new ArrayList<>();
        }
        public ArrayList<position> getPositions() {
            ArrayList<position> positions = new ArrayList<>();
            for (int i = position.x; i < position.x + length; i++) {
                for (int j = position.y; j < position.y + height; j++) {
                    position temp = new position(i, j);
                    positions.add(temp);
                }
            }
            return positions;
        }
        public ArrayList<position> getWalls() {
            ArrayList<position> walls = new ArrayList<>();
            for (position a : this.getPositions()) {
                if (a.x == position.x || a.x == position.x + length - 1
                        || a.y == position.y || a.y == position.y + height - 1) {
                    walls.add(a);
                }
            }
            return walls;
        }
        public void setRandomUnlockedDoors(long seed) {
            TETile tile = Tileset.UNLOCKED_DOOR;
            Random random = new Random(seed);
            ArrayList<position> walls = getWalls();
            int numOfWall = walls.size();
            int numOfDoor = random.nextInt(1, 2);  //1 door
            int counter = 0;
            while (counter < numOfDoor) {
                int index = random.nextInt(numOfWall);
                position d = walls.get(index);
                if (helperRandomDoor(d,8)) {
                    draw(d, tile);
                    counter += 1;
                    doors.add(d);
                } else {
                    continue;
                }
            }
        }
        /**
         *必须是墙；不能是四个点；不能离周围建筑太近
         * @param p potential door position
         * @param n distance with the other thing from the door
         * @return  If is a good position to set a door
         */
        private boolean helperRandomDoor(position p, int n) {
            if (!worldMap[p.x][p.y].description().equals("wall")) {
                return false;
            }
            if(p.samePosition(position) || p.samePosition(position.verticalMove(height - 1))
                    || p.samePosition(position.horizontalMove(length - 1))
                    || p.samePosition(position.verticalMove(height - 1).horizontalMove(length - 1))){
                return false;
            }


            int direction = 0;
            if (p.horizontalMove(-1).emptyPoint()) {
                direction = 1;
            } //left
            if (p.horizontalMove(1).emptyPoint()) {
                direction = 2;
            } //right
            if (p.verticalMove(1).emptyPoint()) {
                direction = 3;
            } //up
            if (p.verticalMove(-1).emptyPoint()) {
                direction = 4;
            } //down
            switch (direction) {
                case 1:
                    return p.horizontalMove(-n).emptyPoint();
                case 2:
                    return p.horizontalMove(n).emptyPoint();
                case 3:
                    return p.verticalMove(n).emptyPoint();
                case 4:
                    return p.verticalMove(-n).emptyPoint();
            }
            return false;
        }
    }

    /**
     * Helper class to localise one tile in world:
     * .x is weight,
     * .y is height
     */
    private class position {
        int x;
        int y;
        position(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public boolean emptyPoint() {
            return x >= 0 && y >= 0 && x < width && y < height && worldMap[x][y].description().equals("nothing");
        }
        public position verticalMove(int n) {
            position up = new position(x, y + n);
            return up;
        }
        public position horizontalMove(int n) {
            position right = new position(x + n, y);
            return right;
        }

        public boolean samePosition(position p) {
            if (x == p.x && y == p.y) {return true;}
            return false;
        }
    }

    public static void draw(position p, TETile tile) {
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
        World world = new World(80, 60);
        world.randomMap(5452);

        TERenderer ter = new TERenderer();
        ter.initialize(80, 60);
        ter.renderFrame(world.worldMap);
    }
}
