package array;

public class Question1486 {
    public int xorOperation(int n, int start) {
        // nums[i] = start + 2*i
        int[] nums = new int[n];
        int result = start;
        for (int i = 1; i < n; i++) {
            nums[i] = start + 2 * i;
            result ^= nums[i];
        }
        return result;

    }
}
