public class fillGrid {

    public static void main(String[] args){
        int[] LL = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 0, 0 };
        int[] UR = { 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };
        int[][] S = {
                { 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0}
        };
        fillGrid(LL, UR, S);

    }
    public static void fillGrid(int[] LL, int[] UR, int[][] S) {
        int N = S.length;
        int kL, kR;
        kL = kR = 0;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (i < j) {
                    S[i][j] = UR[kR];
                    kL += 1;
                } else if (i > j) {
                    S[i][j] = LL[kL];
                    kL += 1;
                }
            }
        }
    }

}
