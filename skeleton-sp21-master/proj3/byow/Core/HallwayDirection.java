package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.lab12.Position;

public class HallwayDirection {

    public TETile[][] world;
    public Position start;
    public HallwayDirection(TETile[][] world, Position start) {
        this.world = world;
        this.start = start;
    }

    public String direction() {
        if (world[start.x][start.y - 1].equals(Tileset.FLOOR)) {
            return "UP";
        }
        if (world[start.x][start.y + 1].equals(Tileset.FLOOR)) {
            return "DOWN";
        }
        if (world[start.x + 1][start.y].equals(Tileset.FLOOR)) {
            return "LEFT";
        }
        if (world[start.x - 1][start.y].equals(Tileset.FLOOR)) {
            return "RIGHT";
        }
        return "NULL";
    }

    public boolean isConnected(String direction) {
        boolean connected = false;
        switch (direction) {                                                                                    //如果Hallway的方向应该往上（其它同理）
            case "UP": {                                                                                          //
                if(world[start.x - 1][start.y].equals(Tileset.FLOOR) || world[start.x + 1][start.y].equals(Tileset.FLOOR) ||    //如果 position左边的一个格子是地板 或者 position右边的一个格子是地板
                        world[start.x][start.y + 1].equals(Tileset.FLOOR)) {                                            //或者 position 上面的一个格子是地板
                    connected = true;                                                                           //就连上了
                }                                                                                               //
                break;                                                                                          //
            }
            case "DOWN": {
                if(world[start.x - 1][start.y].equals(Tileset.FLOOR) || world[start.x + 1][start.y].equals(Tileset.FLOOR) ||
                        world[start.x][start.y - 1].equals(Tileset.FLOOR)) {
                    connected = true;
                }
                break;
            }
            case "LEFT": {
                if(world[start.x][start.y - 1].equals(Tileset.FLOOR) || world[start.x][start.y + 1].equals(Tileset.FLOOR) ||
                        world[start.x - 1][start.y].equals(Tileset.FLOOR)) {
                    connected = true;
                }
                break;
            }
            case "RIGHT": {
                if(world[start.x][start.y - 1].equals(Tileset.FLOOR) || world[start.x][start.y + 1].equals(Tileset.FLOOR) ||
                        world[start.x + 1][start.y].equals(Tileset.FLOOR)) {
                    connected = true;
                }
                break;
            }
        }
        return connected;
    }
}
