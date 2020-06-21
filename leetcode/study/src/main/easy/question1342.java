package easy;

public class question1342 {
    public static void main(String[] args) {
        int num=8;
        System.out.println(numberOfSteps(14));
    }
    public static int numberOfSteps(int num) {
        int step = 0;
        while (num > 0) {
            if (num % 2 == 0) {
                num /= 2;
                step++;
            } else {
                num -= 1;
                step++;
            }
        }
        return step;
    }
}
