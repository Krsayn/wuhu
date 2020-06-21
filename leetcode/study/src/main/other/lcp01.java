package other;

public class lcp01 {
    public static void main(String[] args) {
        int[] a={1,2,3};
        int[] b={2,2,3};
        System.out.println(game1(a,b));
    }
    public int game(int[] guess, int[] answer) {
        int flag = 0;
        for (int i = 0; i < 3; i++) {
            if (guess[i] == answer[i])
                flag++;
        }
        return flag;
    }

    // 通过异或运算
    public static int game1(int[] guess, int[] answer) {
        int flag = 0;
        for (int i = 0; i < 3; i++) {
            flag += (guess[i] ^ answer[i])==0?1:0;
        }
        return flag;
    }
}
