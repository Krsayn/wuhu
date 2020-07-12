package array;

public class Question0674 {
    public int findLengthOfLCIS(int[] nums) {
        int flag = 1;
        int max = 1;
        for (int i = 0; i < nums.length - 1; i++) {
            if (nums[i + 1] - nums[i] > 0) {
                flag++;
            } else {
                flag = 1;
            }
        }
        return max;
    }
}
