public class drawTriangle{
    public static void printStars(int n){
        int row = 0;
        int col = 0;
        while (row < n){
            col = 0;
            while (col <= row){
                System.out.print("*");
                col = col + 1;
            }
            row = row + 1;
            System.out.println();
        }
    }

    public static void main(String[] args) {
        int n = 10;
        printStars(n);
    }
}