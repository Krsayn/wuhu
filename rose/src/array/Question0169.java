package array;

import java.util.HashMap;

public class Question0169 {
    public int majorityElement(int[] nums) {

        if(nums.length==1){
            return nums[0];
        }
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (map.containsKey(nums[i])) {
                int value = map.get(nums[i])+1;
                if (value > nums.length / 2) {
                    return nums[i];
                }
                map.put(nums[i],value);
            } else {
                map.put(nums[i], 1);
            }
        }
       return -1;
    }
}
