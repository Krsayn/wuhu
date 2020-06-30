package ac;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MapList;

import matrix.db.Context;
import matrix.util.StringList;

public class datagridServlet {
  public static void main(String[] args) {
	  try {
		  Context context = new Context("http://192.168.23.131:8080/enovia/");
			context.setUser("creator");
			context.setPassword("");
			context.connect();
			StringList selectStmts = new StringList();
		    selectStmts.addElement(DomainConstants.SELECT_ID);
		    selectStmts.addElement(DomainConstants.SELECT_TYPE);
		    selectStmts.addElement(DomainConstants.SELECT_NAME);
			MapList mapList  = DomainObject.findObjects(context, "Person", null, null,  selectStmts);
			System.out.println("mapList===>"+mapList.size());
			context.destroy();
	} catch (Exception e) {
		// TODO: handle exception
	}
	 
}
}

