package easy;

public class question27 {
    public static int removeElement(int[] nums, int val) {
        int i = 0;
        for (int j = 0; j < nums.length; j++) {
            if (nums[j] != val) {
                nums[i] = nums[j];
                i++;
            }
        }
        return i;
    }

    public static int removeElement2(int[] nums, int val) {
        int k = nums.length;
        for (int i = 0; i < k; ) {
            if (nums[i] == val) {
                nums[i] = nums[k - 1];
                k--;
            } else {
                i++;
            }
        }
        return k;
    }
}
