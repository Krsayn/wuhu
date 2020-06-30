package ext;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;

public class util {
	public static String formatCell(HSSFCell hasfCell) {
		if (hasfCell == null) {
			return "";
		}else {
			if(hasfCell.getCellType()==Cell.CELL_TYPE_BOOLEAN) {
				return String.valueOf(hasfCell.getBooleanCellValue());
			}else if(hasfCell.getCellType()==Cell.CELL_TYPE_NUMERIC){
				return String.valueOf(hasfCell.getNumericCellValue());
			}else {
				return String.valueOf(hasfCell.getStringCellValue());
			}
		}

	}
}
