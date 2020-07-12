package array;

public class question724 {
    public int pivotIndex(int[] nums) {
        int sum = 0;
        int leftSum = 0;
        for (int num : nums) {
            sum += num;
        }
        for (int i = 0; i < nums.length ; i++) {
            if(2*leftSum+nums[i]==sum){
                return i;
            }
            leftSum +=nums[i];
        }
        return -1;
    }
}
