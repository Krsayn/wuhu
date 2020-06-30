package recursionTest;




public class testUtil {

	/**
	 * 斐波那契数列的递归写法
	 * @param n
	 * @return
	 */
	public static  int Febo(int n) {
		if (n <= 1)
			return n;
		return Febo(n - 1) + Febo(n - 2);
	}
	
	
	/**
	 * 逻辑有错误
	 * @param n
	 * @return
	 */
	public static long FeboPlus(int n) {
		if(n<=1) {
			return n;
		}
		long fn=0;
		long fn_1=1;
		long fn_2=1;
		for(int i=2;i<=n;i++) {
			fn=fn_1+fn_2;
			fn_2=fn_1;
			fn_1=fn;
		}
		return fn;
	}
	
	/**
	 * 阶乘的递归写法
	 * @param n
	 * @return
	 */
	public static int factorial(int n) {
		if(n<=1) {
			return 1;
		}
		return n*factorial(n-1);
	}
	
	
	/**
	 * 倒序输出的递归写法
	 * @param n
	 */
	public void printDigit(int n) {
		System.out.print(n%10);
		if(n>10) {
			printDigit(n/10);
		}
	}
	
	
	/**
	 * 累加的递归写法
	 * @param n
	 * @return
	 */
	public static int plus(int n) {
		if(n<=0)
			return 0;
		return n+plus(n-1);
	}
	
}
