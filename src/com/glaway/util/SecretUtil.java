package com.glaway.util;

import matrix.db.AttributeList;

public class SecretUtil
{
  public static String[] getDocSecret()
  {
   // GetProperties pro = new GetProperties();
    String[] arrSecret = { "�ڲ�", "����", "����" };
		/*
		 * try { String secret = pro.pro("docSecret", "properties/path.properties"); if
		 * ((secret != null) && (!"".equals(secret))) { arrSecret = secret.split(","); }
		 * } catch (IOException e) { e.printStackTrace(); }
		 */
    return arrSecret;
  }

  public static String[] getPersonSecret()
  {
	  
    //GetProperties pro = new GetProperties();
    String[] arrSecret = { "�ڲ�", "һ��", "��Ҫ" };
		/*
		 * try { String secret = pro.pro("personSecret", "properties/path.properties");
		 * if ((secret != null) && (!"".equals(secret))) { arrSecret =
		 * secret.split(","); } } catch (IOException e) { e.printStackTrace(); }
		 */
    return arrSecret;
  }
}