package easy;

public class question35 {
    public static int searchInsert(int[] nums, int target) {
        int i = nums.length - 1;
        while (nums[i] <=target) {
            i = i / 2 + 1;
        }
        return i-1;
    }

    public static void main(String[] args) {
        int[] arr=new int[]{1,3,5,6};
        System.out.println(searchInsert(arr,2));
    }
}
