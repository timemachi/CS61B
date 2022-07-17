package DynamiqueConnectivity;
import java.util.*

public class test {
    public static void main(String[] args) {
        UFImp UF1 = new UFImp(9);
        UF1.connected(2, 3);
        UF1.connected(1, 2);
        UF1.connected(5, 7);
        UF1.connected(8, 4);
        UF1.connected(7, 2);
        int M = UF1.find(3);
        System.out.println(M);
    }
}
