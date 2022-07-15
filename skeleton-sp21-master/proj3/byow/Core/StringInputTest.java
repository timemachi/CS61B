package byow.Core;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringInputTest {

    @Test
    public void testgetSeed() {
        String N = "N4573468";
        StringInput input = new StringInput(N);
        int seed = input.getSeed();
        assertEquals(-2, seed);
    }
}