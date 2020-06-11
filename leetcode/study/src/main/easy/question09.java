package easy;

public class question09 {
    public static boolean isPalindrome(int x) {
        if (x < 0) {
            return false;
        } else if (x < 10) {
            return true;
        } else {
            String num = String.valueOf(x);
            int[] arr = new int[num.length()];
            for (int i = 0; i < num.length(); i++) {
                arr[i] = Integer.parseInt(String.valueOf(num.charAt(i)));
            }
            int j=arr.length-1;
            for(int i=0;i<=j;){
                if(arr[i]!=arr[j]){
                    return false;
                }
            }
            return true;
        }
    }

    public static void main(String[] args) {
        int x=121;
        System.out.println(isPalindrome(x));
    }
}
