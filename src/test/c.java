package test;
/**
 * ºº×Ö×ªunicode
 * @author Administrator
 *
 */
public class c {

	public static void main(String[] args) {
		String aString = "ÄãºÃ";
		gbEncoding(aString);
		
		
	}

	
	public static String gbEncoding(String gbString) {
		char[] utfBytes = gbString.toCharArray();
		String unicodeBytes = "";
		for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
			String hexB = Integer.toHexString(utfBytes[byteIndex]);
			if (hexB.length() <= 2) {
				hexB = "00" + hexB;
			}
			unicodeBytes = unicodeBytes + "\\u" + hexB;
		}
		try {
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		System.out.println("unicodeBytes is:" + unicodeBytes);
		return unicodeBytes;
	}
}
