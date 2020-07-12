package array;

import java.util.Arrays;
import java.util.HashSet;

public class question217 {
    public boolean containsDuplicate(int[] nums) {
        HashSet<Integer> set = new HashSet<Integer>(nums.length);
        for (int num : nums) {
            if (!set.add(num))
                return true;
        }
        return false;
    }

    public boolean containsDuplicate1(int[] nums) {
        Arrays.sort(nums);
        for (int i = 0; i < nums.length - 1; i++) {
            if (nums[i] == nums[i + 1])
                return true;
        }
        return false;
    }
}
