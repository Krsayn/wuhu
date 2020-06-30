package ext;

import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;

import matrix.db.Context;
import matrix.util.StringList;

public class TreeRecourse {
  private static StringBuffer  sb = new StringBuffer();
	
	
	
	public static StringBuffer digui(Context context,String tempId){
		
		
		
		StringList selectStmt = new StringList(2);
		selectStmt.addElement(DomainConstants.SELECT_ID);
		selectStmt.addElement(DomainConstants.SELECT_NAME);
		selectStmt.addElement("to[Subtask].from.id");
		try {
		DomainObject taskObj = new DomainObject(tempId);
		System.out.println("taskName==father==>"+taskObj.getName(context));
			MapList mapList = taskObj.getRelatedObjects(context,
					"Subtask",
					"*",
					selectStmt,
					null,       // relationshipSelects
					false,      // getTo
					true,       // getFrom
					(short) 1,  // recurseToLevel
					null,// objectWhere
					null); // relationshipWhere
			System.out.println("&"+mapList.size());
			System.out.println("mapList-------->"+mapList);
			if(mapList.size()==0){
				sb.append(",leaf:true},");
			}else{
				//sb.append(",expanded:true,");
				sb.append(",children:[");
				for (int i = 0; i < mapList.size(); i++) {
					Map map = (Map)mapList.get(i);
					String tempId_ = (String)map.get("id");
					System.out.println("333333333"+tempId_);
					String tempName = (String)map.get("name");
					System.out.println("*******"+tempName);
					sb.append("{id:");
					sb.append(map.get("id"));
					sb.append(",text:'");
					sb.append(map.get("name"));
					sb.append("'");
					digui(context,tempId_);
					//context.digui(context,tempId_);
				}
				sb.append("*],");
			}
			
		} catch (FrameworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb;
		
	}
	
	public  void a() {
		if(!sb.equals("")) {
			sb=new StringBuffer();
		}
	}
	
}
