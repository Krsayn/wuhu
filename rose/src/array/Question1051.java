package array;

import java.util.Arrays;

public class Question1051 {
    public static void main(String[] args) {
        int[] arr = {5, 1, 2, 3, 4};
        System.out.println(heightChecker(arr));


    }

    public static int heightChecker(int[] heights) {
        int[] newArr = new int[heights.length];
        System.arraycopy(heights, 0, newArr, 0, heights.length);

        // for (int i = 0; i < heights.length; i++) {
        // newArr[i] = heights[i];
        // }
        Arrays.sort(heights);
        int flag = 0;
        for (int i = 0; i < heights.length; i++) {
            if (heights[i] != newArr[i]) {
                flag++;
            }
        }
        return flag;
    }
}
