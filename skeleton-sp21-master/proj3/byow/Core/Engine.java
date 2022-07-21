package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.*;
import java.nio.Buffer;
import java.util.Scanner;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 60;
    public static final int HEIGHT = 60;
    private long SEED;

    private TETile[][] world;


    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() throws IOException {
        mainMenu();
        String input = "";
        //开始界面的三个功能：新建游戏；快速载入‘
        while (true) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char key = StdDraw.nextKeyTyped();
            input += key;
            if (input.toUpperCase().contains("L")) {
                File savedFile = new File("byow\\Core\\savefile.txt");
                if (savedFile.exists()) {
                    loadFile(savedFile);
                    break;
                }
            }
            if (input.toUpperCase().contains("Q") && !input.toUpperCase().contains("N")) {
                System.exit(0);
            }
            if (input.toUpperCase().contains("N")) { //已经开始输入数字了
                String seed = "";
                int seedStart = input.toUpperCase().indexOf("N") + 1;
                for (char i : input.substring(seedStart).toCharArray()) {
                    if (Character.isDigit(i)) {
                        seed += i;
                    }
                }
                drawSeed(seed);
                if (input.toUpperCase().contains("S")) {
                    SEED = Long.parseLong(seed);
                    RandomWorldGenerator randomWorldGenerator = new RandomWorldGenerator(WIDTH, HEIGHT, SEED);
                    world = randomWorldGenerator.getRandomWorldFrame();
                    break;
                }
            }
            continue;
        }
        //输入:q的时候需要强制储存并退出；如果只输入q：询问是否储存，如果是，就是储存。重置input，并确定这里面没有q
        input = "";
        while (true) {
            //Mouse interaction
            int x = (int)Math.floor(StdDraw.mouseX());
            int y = (int)Math.floor(StdDraw.mouseY());
            //如果鼠标位置在Std的画布里面
            if (x >= 0 && x < WIDTH && y >= 0 && y < WIDTH) {
                //显示HUD
                StdDraw.pause(100);
                drawDescription(world[x][y].description());
            }

            //Keyboard interaction
            // save function
            if (input.contains("Q")) {
                int indexOfQ = input.indexOf("Q");
                //检查有没有:在前面
                if (input.contains(":Q")) {
                    saveWorld("Q");

                }
                String S = input.substring(indexOfQ);
                if (S.contains("Y") || S.contains("N")) {
                    saveWorld(S);
                    input = "";
                } else{
                    saveWorld(S);
                }
            }
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char key = StdDraw.nextKeyTyped();
            CharacterMovement m = new CharacterMovement(world);
            world = m.Move(key);
            input += key;
            input = input.toUpperCase();
        }
    }

    /**
     * @source https://stackoverflow.com/questions/4716503/reading-a-plain-text-file-in-java
     * @param loadedFile
     * @return
     */
    private TETile[][] loadFile(File loadedFile) throws IOException {
        TETile[][] loadedWorld = new TETile[WIDTH][HEIGHT];
        //这个文件确实是存在的
        Scanner sc = new Scanner(loadedFile);
        int height = 0;   //最上面一行，也就是TETILE[][] world的从下往上最后一行，编号应该是HEIGHT - 1
        while (sc.hasNextLine()) {
            height -= 1;
            String line = sc.nextLine();  //HEIGHT + height 行 的line
            int weight = 0;
            for (char c : line.toCharArray()) {
                TETile tile = Tileset.NOTHING;
                if (c == ' ') {tile = Tileset.NOTHING;}
                if (c == '#') {tile = Tileset.WALL;}
                if (c == '@') {tile = Tileset.AVATAR;}
                if (c == '·') {tile = Tileset.FLOOR;}
                if (c == '▢') {tile = Tileset.UNLOCKED_DOOR;}
                loadedWorld[weight][HEIGHT + height] = tile;
                weight += 1;
            }
        }
        sc.close();
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(loadedWorld);
        this.world = loadedWorld;
        return loadedWorld;
    }

    private void saveWorld(String S) throws IOException {
        StdDraw.setPenColor(Color.BLUE);
        StdDraw.filledRectangle(WIDTH / 2 , HEIGHT / 2, WIDTH / 2 * 0.8, HEIGHT / 2 * 0.4);
        StdDraw.setPenColor(Color.white);
        Font warningFont = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(warningFont);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Press Y to save the game, Press N to continue");
        StdDraw.show();

        if (S.contains("Y")) {
            File savefile = new File("byow\\Core\\savefile.txt");
            FileWriter myWriter = new FileWriter(savefile);
            myWriter.write(TETile.toString(world));
            myWriter.close();
            ter.initialize(WIDTH, HEIGHT);
            ter.renderFrame(world);
        }
        if (S.contains("N")) {
            ter.initialize(WIDTH, HEIGHT);
            ter.renderFrame(world);
        }
    }

    public void mainMenu() {
        //Initialize canvas
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.white);

        StdDraw.enableDoubleBuffering();

        Font bigFont = new Font("Monaco", Font.BOLD, 60);
        StdDraw.setFont(bigFont);
        StdDraw.text(WIDTH / 2, HEIGHT * 0.75, "CS61B: THE GAME");

        Font smallFont = new Font("Monaco", Font.BOLD, 40);
        StdDraw.setFont(smallFont);
        StdDraw.text(WIDTH / 2, HEIGHT * 0.75 / 2 + 5, "New Game (N)");
        StdDraw.text(WIDTH / 2, HEIGHT * 0.75 / 2, "Load Game (L)");
        StdDraw.text(WIDTH / 2, HEIGHT * 0.75 / 2 - 5, "Quit (Q)");

        StdDraw.show();
    }
    public void drawSeed(String s) {
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.white);
        Font smallFont = new Font("Monaco", Font.BOLD, 40);
        StdDraw.setFont(smallFont);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 10, "Please enter seed to create random world");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 5, "Press S to finish your seed ");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 5, s);
        StdDraw.show();
    }
    public void drawDescription(String s) {
        //最上面画上黑色长方形
        StdDraw.setPenColor(Color.black);
        StdDraw.filledRectangle(WIDTH / 2, HEIGHT - 1, WIDTH / 2, 1);
        //输入这个地形的描述
        StdDraw.setPenColor(Color.white);
        Font miniFont = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(miniFont);
        StdDraw.textLeft(0, HEIGHT - 1, s);
        StdDraw.show();
    }

    private void movement(char key) {
    }


    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        if(input.toUpperCase().contains("N") && input.toUpperCase().contains("S")) {
            int seedStart = input.toUpperCase().indexOf("N") + 1;
            int seedStop = input.toUpperCase().indexOf("S");
            if(input.substring(seedStart, seedStop).length() > 0) {
                SEED = Long.valueOf(input.substring(seedStart, seedStop));
            } else {
                throw new IllegalArgumentException("You must input a number between N and S");
            }
        } else {
            throw new IllegalArgumentException("Please input a string starting with N and ending with S.");
        }
        RandomWorldGenerator randomWorldGenerator = new RandomWorldGenerator(WIDTH, HEIGHT, SEED);
        return randomWorldGenerator.getRandomWorldFrame();
    }
}
