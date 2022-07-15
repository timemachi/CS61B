package byow.Core;


import static java.lang.Character.*;
import static java.lang.Integer.parseInt;

public class StringInput {

    private static String input;

    public StringInput(String N) {
        this.input = N;

    }
    public static int getSeed() {
        //Make sure the first and last input is N or S
        char first = input.charAt(0);
        if (isLowerCase(first)) {
            toUpperCase(first);
        }
        if (first != 'N') {
            return -1;
        }
        char last = input.charAt(input.length() - 1);
        if (isLowerCase(last)) {
            toUpperCase(last);
        }
        if (last != 'S') {
            return -2;
        }

        //get Seed of random world
        int seed = 0;
        String temp = input.substring(1, input.length() - 1);
        try{
            seed = parseInt(temp);
        }
        catch (NumberFormatException ex) {
            System.out.println("seed has to be a number.");
        }
        return seed;
    }
}
