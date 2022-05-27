public class Exercise4 {
    public static int[] windowPosSum(int[] a, int n){
        for (int i = 0; i < a.length; i++){
            if (a[i] < 0){
                continue;
            }
            for(int m = i + 1; m <= i + n; m++){
                if (m >= a.length){
                    break;
                }
                a[i] = a[i] + a[m];
            }
        }
        return a;
    }

    public static void main(String[] args){
        int[] a = {1, 2, -3, 4, 5, 4};
        int n = 3;
        windowPosSum(a, n);
        System.out.println(java.util.Arrays.toString(a));
    }
}
