package gitlet;
import java.io.IOException;

import static gitlet.Utils.*;



/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        // TODO: what if args is empty?
        if (args.length == 0) {
            throw new GitletException("Please enter a command.");
        }

        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                validateNumArgs(args, 1);
                Repository.init();
            case "add":
                // TODO: handle the `add [filename]` command

                break;

            case "commit":
                validateNumArgs(args, 2);
                Repository.commit(args[1]);
                break;

            case "rm":
                break;

            case "log":
                break;

            case "global-log":
                break;

            case "find":
                break;

            case "status":
                break;

            case "checkout":
                break;

            case "branch":
                break;

            case "rm-branch":
                break;

            case "reset":
                break;

            case "merge":
                break;

        }

    }
    /**
     * Checks the number of arguments versus the expected number.
     *
     * @param args Argument array from command line
     * @param n    Number of expected arguments
     */
    private static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            message("Incorrect operands.", args);
            System.exit(0);
        }

}
