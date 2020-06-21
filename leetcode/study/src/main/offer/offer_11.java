package offer;

public class offer_11 {
    public int minArray(int[] numbers) {
        int flag = numbers[0];
        for (int i = 0; i < numbers.length - 1; i++) {
            if (numbers[i] > numbers[i + 1]) {
                flag = numbers[i + 1];
            }
        }
        return flag;
    }
}
