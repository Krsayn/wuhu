package easy;

public class question1176 {
    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 5};
        int k = 1;
        int lower = 3;
        int upper = 3;
        System.out.println(dietPlanPerformance(arr, k, lower, upper));
    }

    public static int dietPlanPerformance(int[] calories, int k, int lower, int upper) {
        int flag = 0;
        int sum = 0;
        for (int i = 0; i < k; i++) {
            sum += calories[i];
        }
        int i=0;
        while(i>=calories.length-k){

            if (sum < lower) {
                flag -= 1;
            }
            if (sum > upper) {
                flag += 1;
            }
            sum -= calories[i];
            sum += calories[i + k-1];
            i++;
        }
        return flag;
    }
}
