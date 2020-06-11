package easy;

/**
 * 给定一个排序数组，你需要在 原地 删除重复出现的元素，使得每个元素只出现一次，返回移除后数组的新长度。
 * <p>
 * 不要使用额外的数组空间，你必须在 原地 修改输入数组 并在使用 O(1) 额外空间的条件下完成。
 */
public class question26 {
    /**
     * 快慢指针法
     *
     * @param nums
     * @return
     */
    public static int removeDuplicates(int[] nums) {
        if (nums.length == 0) {
            return 0;
        }
        int i = 0;
        for (int j = 1; j < nums.length; j++) {
            if (nums[i] != nums[j]) {
                i++;
                nums[i] = nums[j];
            }
        }
        return i + 1;
    }

    public static int removeDuplicates1(int[] nums) {
        int size = nums.length;
        //统计当前元素需要前移的位数，注意还是计数排序思想！！！只不过不需要整个数组
        int cnt = 0;
        for (int i = 1; i < size; ++i) {
            if (nums[i] == nums[i - 1]) {
                cnt++;
            }
            //前移cnt个位置
            nums[i - cnt] = nums[i];
        }
        return size - cnt;
    }

    public static void main(String[] args) {
        int[] nums = new int[]{1, 1, 2};
        System.out.println(removeDuplicates1(nums));
    }
}
