package array;

import java.util.Arrays;

public class Question1085 {
    public static void main(String[] args) {
        System.out.println(9 / 10);
        System.out.println(108 % 10);
        System.out.println(108 / 10);
    }

    public int sumOfDigits(int[] A) {
        Arrays.sort(A);
        int flag = 0;
        while (A[0] > 0) {
            flag += A[0] % 10;
            A[0] = A[0] / 10;
        }

        return flag % 2 == 0 ? 1 : 0;
    }
}
