package interview;

public class question58_2 {
    public static void main(String[] args) {
        String str = "abcd";
        System.out.println(str.substring(0, 1));
        System.out.println(str.substring(1, 4));
    }

    public String reverseLeftWords(String s, int n) {
        String str1 = s.substring(0, n);
        String str2 = s.substring(n, s.length());
        return str2 + str1;
    }

    // 求余 简化运算
    public String reverseLeftWords2(String s, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = n; i < s.length() + n; i++) {
            sb.append(s.charAt(i % s.length()));
        }
        return sb.toString();
    }
}
