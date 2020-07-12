package array;

public class question1470 {
    public int[] shuffle(int[] nums, int n) {
        //nums.length=2n;
        int[] arr = new int[2 * n];
        int j = 0;
        int k = n;
        for (int i = 0; i < 2 * n; ) {
            arr[i] = nums[j++];

            arr[i + 1] = nums[k++];
            //k++;
            i += 2;
        }
        return arr;
    }
}
