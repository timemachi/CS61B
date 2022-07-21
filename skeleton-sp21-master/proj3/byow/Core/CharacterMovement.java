package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.lab12.Position;
import edu.princeton.cs.introcs.StdDraw;

import java.util.ArrayList;


public class CharacterMovement {
    public TETile[][] map;
    private final int WEIGHT;
    private final int HEIGHT;
    public Position avatar;
    private ArrayList<String> CANMOVE;



    public CharacterMovement(TETile[][] world) {
        this.map = world;
        this.HEIGHT = world[0].length;
        this.WEIGHT = world.length;
        CANMOVE = new ArrayList<>();
        CANMOVE.add("nothing"); CANMOVE.add("floor"); CANMOVE.add("grass"); CANMOVE.add("unlocked door");

        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WEIGHT; j++) {
                if (world[j][i].description() == "you") {
                    this.avatar = new Position(j, i);
                    break;
                }
            }
        }
    }
    public Position getAvatar() {
        return avatar;
    }
    public TETile[][] Move(char C) {
        String order = Character.toString(C);
        order = order.toUpperCase();

        if (!canMove(order)) {return map;}
        TETile me = Tileset.AVATAR;
        TETile actualTile = whatTileOfAvatar();
        MoveHelper(actualTile, avatar);

        switch (order) {
            case "W":
                Position upper = new Position(avatar.x, avatar.y + 1);
                MoveHelper(me, upper);
                break;
            case "A":
                Position lefter = new Position(avatar.x - 1, avatar.y);
                MoveHelper(me, lefter);
                break;
            case "S":
                Position lower = new Position(avatar.x, avatar.y - 1);
                MoveHelper(me, lower);
                break;
            case "D":
                Position righter = new Position(avatar.x + 1, avatar.y);
                MoveHelper(me, righter);
                break;
            default:
                break;
        }
        return map;
    }
    private boolean canMove(String direction) {
        switch (direction) {
            case "W":
                if (avatar.y + 1 >= HEIGHT) {return false;}
                TETile moveW = map[avatar.x][avatar.y + 1];
                return CANMOVE.contains(moveW.description());
            case "A":
                if (avatar.x - 1 < 0) {return false;}
                TETile moveA = map[avatar.x - 1][avatar.y];
                return CANMOVE.contains(moveA.description());
            case "S":
                if (avatar.y - 1 < 0) {return false;}
                TETile moveS = map[avatar.x][avatar.y - 1];
                return CANMOVE.contains(moveS.description());
            case "D":
                if (avatar.x + 1 >= WEIGHT) {return false;}
                TETile moveD = map[avatar.x + 1][avatar.y];
                return CANMOVE.contains(moveD.description());
        }
        return false;
    }
    private void MoveHelper(TETile tile, Position p) {
        //position p是指定位置.TETile tile是准备放在这个位置的tile种类。
        //先要把这地方涂黑
        Tileset.NOTHING.draw(p.x, p.y); //先把这地方涂黑
        tile.draw(p.x, p.y); //再把这地方用指定tile填充
        map[p.x][p.y] = tile;
    }
    private TETile whatTileOfAvatar(){
        int x = avatar.x;
        int y = avatar.y;
        if (map[x][y + 1].description() == "nothing" || map[x][y - 1].description() == "nothing"
            || map[x + 1][y].description() == "nothing" || map[x - 1][y].description() == "nothing") {
            return Tileset.NOTHING;
        }
        return Tileset.FLOOR;
    }


}
