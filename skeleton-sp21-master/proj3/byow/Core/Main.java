package byow.Core;

import byow.TileEngine.TETile;

import java.io.IOException;

/** This is the main entry point for the program. This class simply parses
 *  the command line inputs, and lets the byow.Core.Engine class take over
 *  in either keyboard or input string mode.
 */
public class Main {
    //命令行长度大于2：报错；
    //命令行长度等于2， 第一个string是-s：运行Engine，用interactWithInputString运行第二个string
    //否则运行interactWithKeyboard()
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Can only have one argument - the input string");
            System.exit(0);
        } else if (args.length == 1) {
            Engine engine = new Engine();
            TETile[][] randomWorld = engine.interactWithInputString(args[0]);
            System.out.println(TETile.toString(randomWorld));
        } else {
            Engine engine = new Engine();
            engine.interactWithKeyboard();
        }
    }
}
