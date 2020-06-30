package test;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import ext.GWStudent;



public class cv {
public static void main(String[] args) {
	try {
			
		List temp=new ArrayList();
		FileInputStream fileIn=new FileInputStream("");
		HSSFWorkbook wb=new HSSFWorkbook(fileIn);
		HSSFSheet sht0=wb.getSheetAt(0);
		for(Row r:sht0) {
			if(r.getRowNum()<1) {
				continue;
			}
			GWStudent stu=new GWStudent();
			stu.setName(r.getCell(0).getStringCellValue());
			stu.setType(r.getCell(1).getStringCellValue());
			stu.setGWName(r.getCell(2).getStringCellValue());
			stu.setGWSex(r.getCell(3).getStringCellValue());
			stu.setGWClass(r.getCell(4).getStringCellValue());
			temp.add(stu);
		}
	
		fileIn.close();
		
	} catch (Exception e) {
	
	}
	
}

	
	
}
