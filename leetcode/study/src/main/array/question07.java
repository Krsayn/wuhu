package array;

public class question07 {
    public static void main(String[] args) {
        int x = 123;
        System.out.println(reverse(x));
    }

    public static int reverse(int x) {
        long result = 0;
        while (x != 0) {
            long flag = x % 10;
            result = result * 10 + flag;
            x /= 10;
        }
        return (int) result == result ? (int) result : 0;
    }
}
