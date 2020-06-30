package recursionTest;




public class testUtil {

	/**
	 * 쳲��������еĵݹ�д��
	 * @param n
	 * @return
	 */
	public static  int Febo(int n) {
		if (n <= 1)
			return n;
		return Febo(n - 1) + Febo(n - 2);
	}
	
	
	/**
	 * �߼��д���
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
	 * �׳˵ĵݹ�д��
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
	 * ��������ĵݹ�д��
	 * @param n
	 */
	public void printDigit(int n) {
		System.out.print(n%10);
		if(n>10) {
			printDigit(n/10);
		}
	}
	
	
	/**
	 * �ۼӵĵݹ�д��
	 * @param n
	 * @return
	 */
	public static int plus(int n) {
		if(n<=0)
			return 0;
		return n+plus(n-1);
	}
	
}
