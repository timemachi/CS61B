package byow.Core;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.lab12.Position;

import java.util.*;
public class RandomWorldGenerator {
    TERenderer ter = new TERenderer();
    TETile[][] randomWorldFrame;  //这一列都是实例变量（instance variable），每一个object都有其特殊的实例变量。与之相对，类变量（Class variable）是独立于方法之外的变量，用 static 修饰。It is a special type of class attribute
    /* Feel free to change the width and height. */
    private int WIDTH;
    private int HEIGHT;
    private Random RANDOM;

    public RandomWorldGenerator(int width, int height, long seed) {  //生成器 只有三个参数：随机数、长、宽。
        RANDOM = new Random(seed);
        WIDTH = width;
        HEIGHT = height;
    }

    public TETile[][] getRandomWorldFrame() {
        ter.initialize(WIDTH, HEIGHT); //生成一个指定长宽的ter
        randomWorldFrame = new TETile[WIDTH][HEIGHT];  //生成一个新的TETile[][]
        initialize(randomWorldFrame, Tileset.NOTHING); //使用initialize(TETile[][] world, TETile t)：输入TETile[][]和TETile，用后者填满前者
        for(int i = 0; i < Math.max(WIDTH, HEIGHT); i++) {  //对0-长宽里的较大值重复N次：生成随机position、放一个随机房子
            Position roomStart = new Position(RandomUtils.uniform(RANDOM, WIDTH), RandomUtils.uniform(RANDOM, HEIGHT)); //position是表示坐标的class。x是位于0-width的随机数，y是0-height的随机数。
            placeRandomRoom(randomWorldFrame, roomStart, RANDOM); //随机放置房子。三个arguments分别是：1.地图；2.刚刚生成的随机position； 3.RANDOM
        }                                                           //但是有可能放不了房子。因此要尝试这么多次。这么做后果是会放好多房子在里面。但是感觉算法很奇怪。可是随机性很好，纯粹靠尝试。
        //下面这个神奇的算法更是重量级，纯粹用随机性随机放置hallway。所以要尝试更多次。让我们来看看效果
        for(int i = 0; i < Math.max(WIDTH, HEIGHT) * 2; i++) { //尝试次数：高宽中较大值的2倍
            Position hallwayStart = new Position(RandomUtils.uniform(RANDOM, WIDTH), RandomUtils.uniform(RANDOM, HEIGHT)); //一样的方法：生成随机hallwayStart
            placeARandomHallway(randomWorldFrame, hallwayStart, RANDOM);                                                   //放置随机hallway
        }
        removeWall(randomWorldFrame);
        addRandomExit(randomWorldFrame, RANDOM);
        addRandomAvatar(randomWorldFrame, RANDOM);
        ter.renderFrame(randomWorldFrame);
        return randomWorldFrame;
    }

    /**
     *
     * @param world TETile[][] 地图
     * @param t 初始化地图的材质
     */
    private void initialize(TETile[][] world, TETile t) {
        for(int i = 0; i < WIDTH; i++) {
            for(int j = 0; j < HEIGHT; j++) {
                world[i][j] = t;
            }
        }
    }

    /**
     *
     * @param world 世界的代表
     * @param start 房屋的起始点
     * @param RANDOM 随机数生成器
     */
    private void placeRandomRoom(TETile[][] world, Position start, Random RANDOM) {
        int roomWidth = RandomUtils.uniform(RANDOM, 5, 10);  //房屋的宽和高：(5. 10]随机数，每次引用都会不一样
        int roomHeight = RandomUtils.uniform(RANDOM, 5, 10);
        if(canPlaceARoom(world, start, roomWidth, roomHeight)) { //如果房子可以放进去

            //Draw a room surrounded by wall 画墙
            for(int i = start.x; i < start.x + roomWidth; i++) {
                world[i][start.y] = Tileset.WALL;
                world[i][start.y + roomHeight - 1] = Tileset.WALL;
            }
            for(int j = start.y; j < start.y + roomHeight; j++) {
                world[start.x][j] = Tileset.WALL;
                world[start.x + roomWidth - 1][j] = Tileset.WALL;
            }

            //Draw floors inside the room 画地板
            for(int i = start.x + 1; i < start.x + roomWidth - 1; i++) {
                for(int j = start.y + 1; j < start.y + roomHeight - 1; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
        }
    }
    /**
     *
     * @param world 世界
     * @param p 某一个点
     * @param roomWidth 房屋的宽度
     * @param roomHeight 房屋的高度
     * @return 能不能放房子进去
     */
    private boolean canPlaceARoom(TETile[][] world, Position p, int roomWidth, int roomHeight) {
        if(p.x + roomWidth >= WIDTH - 3|| p.y + roomHeight >= HEIGHT - 3 || p.x < 3 || p.y < 3) { //先看房子有没有离地图边缘3格
            return false;
        }
        for(int i = p.x; i < p.x + roomWidth; i++) {    //检查房子的每个点：如果不是nothing就返回false
            for(int j = p.y; j < p.y + roomHeight; j++) {
                if(!world[i][j].equals(Tileset.NOTHING)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     *
     * @param world 世界
     * @param start 起始点
     * @param RANDOM RNG
     */
    private void placeARandomHallway(TETile[][] world, Position start, Random RANDOM) {
        while(!(world[start.x][start.y].equals(Tileset.WALL)) || start.x < 3 || start.y < 3 ||    //当 起始点不是墙 或者 起始点在离地图边缘3格以内（含3格）
                start.x > WIDTH - 3 || start.y > HEIGHT - 3) {
            start = new Position(RandomUtils.uniform(RANDOM, WIDTH), RandomUtils.uniform(RANDOM, HEIGHT)); //生成另一个随机点，start变成这个点

            if(start.x < 3 || start.y < 3 || start.x > WIDTH - 3 || start.y > HEIGHT - 3) { //如果 这个点依然不是墙并且离地图边缘3格以内
                continue;                                                                   //继续next loop （再随机生成一个）
            }

            if(!canPlaceAHallway(world, start)) {                                           //如果这个点符合第一个条件了，再看能不能放置Hallway。如果不行：继续loop
                continue;
            }
        }
        //最后终于生成一个符合条件的position：这个position在墙上、离地图边缘3格之外、可以放置一个hallway（上下左右总有一个地方有floor）
        HallwayDirection direction = new HallwayDirection(randomWorldFrame, start);
        //现在假设它写的是正确的
        switch (direction.direction()) {
            case "UP": {                                                                              //如果direction是up：假设意思是走廊应该往上走，即某点的下方是floor；其它同理
                while(start.y < HEIGHT - 2 && !direction.isConnected("UP")) {    //当 这个点小于高度 - 2（离上界限至少还有2格） 以及 还没连上
                    world[start.x - 1][start.y] = Tileset.WALL;                                     //起始点左边加一个墙
                    world[start.x + 1][start.y] = Tileset.WALL;                                     //起始点左边加一个墙
                    world[start.x][start.y] = Tileset.FLOOR;                                        //起始点加一个地板
                    start.y += 1;                                                                   //起始点上移一格
                }                                                                                   //也就是说，上面代码的功能是：如果hallway的方向是向上，那么就向上一直造hallway。如果已经connected了：
                world[start.x - 1][start.y] = Tileset.WALL;                                         //点的左边加一个墙
                world[start.x + 1][start.y] = Tileset.WALL;                                         //点的右边加一个墙
                world[start.x][start.y] = Tileset.WALL;                                             //这个点本身也变成墙
                break;
            }

            case "DOWN": {
                while(start.y > 1 && !direction.isConnected("DOWN")) {
                    world[start.x - 1][start.y] = Tileset.WALL;
                    world[start.x + 1][start.y] = Tileset.WALL;
                    world[start.x][start.y] = Tileset.FLOOR;
                    start.y -= 1;
                }
                world[start.x - 1][start.y] = Tileset.WALL;
                world[start.x + 1][start.y] = Tileset.WALL;
                world[start.x][start.y] = Tileset.WALL;
                break;
            }

            case "LEFT": {
                while(start.x > 1 && !direction.isConnected("LEFT")) {
                    world[start.x][start.y - 1] = Tileset.WALL;
                    world[start.x][start.y + 1] = Tileset.WALL;
                    world[start.x][start.y] = Tileset.FLOOR;
                    start.x -= 1;
                }
                world[start.x][start.y + 1] = Tileset.WALL;
                world[start.x][start.y - 1] = Tileset.WALL;
                world[start.x][start.y] = Tileset.WALL;
                break;
            }

            case "RIGHT": {
                while(start.x < WIDTH - 2 && !direction.isConnected("RIGHT")) {
                    world[start.x][start.y - 1] = Tileset.WALL;
                    world[start.x][start.y + 1] = Tileset.WALL;
                    world[start.x][start.y] = Tileset.FLOOR;
                    start.x += 1;
                }
                world[start.x][start.y + 1] = Tileset.WALL;
                world[start.x][start.y - 1] = Tileset.WALL;
                world[start.x][start.y] = Tileset.WALL;
                break;
            }
        }

    }

    /**
     * Check whether you can draw a random hallway at a given position.
     */
    private boolean canPlaceAHallway(TETile[][] world, Position p) {
        if(world[p.x + 1][p.y].equals(Tileset.FLOOR) || world[p.x - 1][p.y].equals(Tileset.FLOOR) ||           //如果这个点的左边、右边、上边、下边有一个格子是地板，就可以。房间顶点就不可以。
                world[p.x][p.y + 1].equals(Tileset.FLOOR) || world[p.x][p.y - 1].equals(Tileset.FLOOR)) {
            return true;
        }
        return false;
    }
    private void removeWall(TETile[][] world) {

        for(int i = 2; i < WIDTH - 2; i++) {                               //从world[2][2]开始：检查地图离边界2格以上的所有点
            for(int j = 2; j < HEIGHT - 2; j++) {                          //
                Position currPos = new Position(i, j);                     //
                if(needRemoval(world, currPos)) {                          //如果需要remove（或者说如果这个点在走廊里面，是走廊的地板）
                    world[currPos.x][currPos.y] = Tileset.FLOOR;           //就把这个点变成地板
                }
            }
        }                                                                  // 从world[2][2]开始：检查地图离边界2格以上的所有点
        for(int i = 2; i < WIDTH - 2; i++) {                               //
            for(int j = 2; j < HEIGHT - 2; j++) {                          //
                Position currPos = new Position(i, j);                     //
                if(wallWithOpenHallways(world, currPos)) {                 //如果这个点在房子内部：
                    world[currPos.x][currPos.y] = Tileset.FLOOR;           //装上地板。
                }
            }
        }
    }

    private boolean needRemoval(TETile[][] world, Position p) {                                                        //给定一个地图和一个点
        if((world[p.x + 1][p.y].equals(Tileset.FLOOR) && world[p.x - 1][p.y].equals(Tileset.FLOOR) &&                  //如果 这个点右边的一个点是地板 而且 这个点左边的一个点也是地板
                world[p.x][p.y - 1].equals(Tileset.WALL) && world[p.x][p.y + 1].equals(Tileset.WALL)) ||               //而且 这个点下面的一个点是墙 而且 这个点上面的一个点是墙         或者
                (world[p.x + 1][p.y].equals(Tileset.WALL) && world[p.x - 1][p.y].equals(Tileset.WALL) &&               //这个点右边的一个点是墙 而且 这个点左边的一个点是墙
                        world[p.x][p.y - 1].equals(Tileset.FLOOR) && world[p.x][p.y + 1].equals(Tileset.FLOOR))) {     //而且 这个点下面的一个点是地板 而且 这个点上面的一个点是地板
            return true;                                                                                               //这个点就应该被移除？这不是说明这玩意在走廊里面吗  说明这个走廊点应该移除！
        } else {
            return false;
        }
    }

    private boolean wallWithOpenHallways(TETile[][] world, Position p) {   //敞开的走廊和墙
        if(world[p.x][p.y].equals(Tileset.FLOOR)) return false;            //如果这个点是地板：否
        int numOfFloors = 0;                                               //地板数量
        if(world[p.x + 1][p.y].equals(Tileset.FLOOR)) {                    //如果 这个点右边的点是地板：地板数+1
            numOfFloors += 1;                                              //
        }                                                                  //
        if(world[p.x - 1][p.y].equals(Tileset.FLOOR)) {                    //如果 这个点左边的点是地板：地板数+1
            numOfFloors += 1;                                              //
        }                                                                  //
        if(world[p.x][p.y + 1].equals(Tileset.FLOOR)) {                    //如果 这个点上面的点是地板：地板数+1
            numOfFloors += 1;                                              //
        }                                                                  //
        if(world[p.x][p.y - 1].equals(Tileset.FLOOR)) {                    //如果 这个点下面的点是地板：地板数+1
            numOfFloors += 1;                                              //
        }                                                                  //如果地板数>3，也就是有3个或4个地板在周围，说明这个点必定在房里内部。而且还不在角落里。
        if(numOfFloors >= 3) return true;
        return false;

    }

    private void addRandomExit(TETile[][] world, Random RANDOM) {
        Position p = new Position(RandomUtils.uniform(RANDOM, 3, WIDTH - 3), RandomUtils.uniform(RANDOM, 3, HEIGHT - 3)); //在所有离边界距离大于3的点中随机产生一个点
        while (!world[p.x][p.y].equals(Tileset.WALL)) {                                                                                 //如果这个点不是墙
            p = new Position(RandomUtils.uniform(RANDOM, 3, WIDTH - 3), RandomUtils.uniform(RANDOM, 3, HEIGHT - 3));      //重新再选一个点
        }
        world[p.x][p.y] = Tileset.UNLOCKED_DOOR;                                                                                      //在这个点装上没锁的门
    }

    public Position addRandomAvatar(TETile[][] world, Random RANDOM) {
        Position p = new Position(RandomUtils.uniform(RANDOM, 3, WIDTH - 3), RandomUtils.uniform(RANDOM, 3, HEIGHT - 3));
        while (!world[p.x][p.y].equals(Tileset.FLOOR)) {
            p = new Position(RandomUtils.uniform(RANDOM, 3, WIDTH - 3), RandomUtils.uniform(RANDOM, 3, HEIGHT - 3));
        }
        world[p.x][p.y] = Tileset.AVATAR;
        return p;
    }
}
