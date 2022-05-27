public class sum {
    /** Using a basic for loop to sum A */
    public static int summer(int[] a){
        int sum = 0;
        for (int i = 0; i < a.length; i++){
            sum = sum + a[i];
        }
        return sum;
    }

    public static void main(String[] args){
        int[] a = {4, 3, 2,1};
        System.out.println(summer(a));
    }
}
