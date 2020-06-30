package ext;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hslf.model.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.servlet.Framework;

import matrix.db.Context;
import matrix.util.StringList;

public class ContextTest {
	public static void main(String[] args) {
		try {
			
			List<String> parentIdList=new ArrayList<String>();
			Context context = new Context("");
			context.setUser("creator");
			context.setPassword("");
			context.connect();
			StringList selectStmts = new StringList();
			selectStmts.addElement(DomainConstants.SELECT_ID);
			selectStmts.addElement(DomainConstants.SELECT_TYPE);
			selectStmts.addElement(DomainConstants.SELECT_NAME);
			MapList mapList = DomainObject.findObjects(context, "Person", null, null, selectStmts);

			org.apache.poi.ss.usermodel.Sheet
		
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
