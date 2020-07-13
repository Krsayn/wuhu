package array;

import java.util.Arrays;

public class Question0888 {
    public static void main(String[] args) {
        int[] a = {2};
        int[] b = {1, 3};
        int[] ints = fairCandySwap(a, b);
        System.out.println(Arrays.toString(ints));
    }

    public static int[] fairCandySwap(int[] A, int[] B) {
        int sum1 = 0;
        int sum2 = 0;
        for (int n : A)
            sum1 += n;
        for (int n : B)
            sum2 += n;
        int temp = (sum1 + sum2) / 2;
        for (int n : A)
            for (int m : B)
                if ((sum1 - n + m) == temp) {
                    return new int[]{n, m};
                }
        throw null;
    }
}
