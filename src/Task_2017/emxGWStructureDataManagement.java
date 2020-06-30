// emxGWStructureDataManageBase.java
//
// Copyright (c) 2002-2015 Dassault Systemes.
// All Rights Reserved
//

//
//Create By ZH 2018-03-16
//
import matrix.db.Context;
import com.matrixone.apps.domain.util.MapList;
import java.util.Map;
import java.text.SimpleDateFormat;
import matrix.db.JPO;
import matrix.util.MatrixException;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;

import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkLicenseUtil;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.XSSUtil;
import com.matrixone.apps.domain.util.i18nNow;
import com.matrixone.apps.program.ProgramCentralUtil;
import com.matrixone.apps.program.ProgramCentralConstants;
import matrix.db.*;
import java.util.Hashtable;
import java.util.Iterator;
import matrix.util.StringList;
import matrix.util.Pattern;

import com.matrixone.apps.common.CommonDocument;
import com.matrixone.apps.common.Part;
import com.matrixone.apps.common.VCDocument;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.*;
import com.matrixone.apps.engineering.EngineeringConstants;
import com.matrixone.apps.framework.ui.UIUtil;
import matrix.util.SelectList;

public class ${CLASSNAME} extends com.matrixone.apps.program.ProjectSpace
        {

        java.text.SimpleDateFormat sdf =
        new java.text.SimpleDateFormat(eMatrixDateFormat.getEMatrixDateFormat(),
        Locale.US);

private Context context;

public ${CLASSNAME} (Context context, String[] args)
        throws Exception
        {
        // Call the super constructor
        super();
        this.context = context;
        if ((args != null) && (args.length > 0))
        {
        setId(args[0]);
        }
        }

public ${CLASSNAME} (String id) throws Exception
        {
        // Call the super constructor
        super(id);
        }

public Map createNewProduct(Context context,String[]args)throws Exception
        {

        Map programMap 					= (HashMap) JPO.unpackArgs(args);
        Map<String,String> returnMap = new HashMap<String, String>();
        String proName 				= (String)programMap.get("Name");
        String policy 				= (String)programMap.get("Policy");
        String proAutoName 			= (String)programMap.get("autoNameCheck");
        String shipowner 			= (String)programMap.get("GWShipowner");
        String type 			= (String)programMap.get("TypeActual");
        String title 			= (String)programMap.get("Title");  //??
        String shipType 			= (String)programMap.get("GWShipType");
        String responsibl 			= (String)programMap.get("GWResponsible");
        String manufactureUnit 			= (String)programMap.get("GWManufactureUnit");
        String designUni 			= (String)programMap.get("GWDesignUnit");
        String description 			= (String)programMap.get("GWDescription");

        if(ProgramCentralUtil.isNullString(proName) && proAutoName.equalsIgnoreCase("true")){
        String symbolicTypeName = PropertyUtil.getAliasForAdmin(context, "Type", type, true);
        String symbolicPolicyName = PropertyUtil.getAliasForAdmin(context, "Policy", policy, true);
        System.out.println("symbolicTypeName==="+ symbolicTypeName);
        System.out.println("symbolicPolicyName==="+ symbolicPolicyName);
        proName = FrameworkUtil.autoName(context,
        symbolicTypeName,
        null,
        symbolicPolicyName,
        null,
        null,
        true,
        true);
        }
        BusinessObject product = new BusinessObject(type, proName, "A", context
        .getVault().getName());
        product.create(context, "Standard Part");
        product.open(context);
        product.setAttributeValue(context,"GWShipType",shipType);
        product.setAttributeValue(context,"GWShipowner",shipowner);
        product.setAttributeValue(context,"Title",title);
        product.setAttributeValue(context,"GWShipType",shipType);
        product.setAttributeValue(context,"GWManufactureUnit",manufactureUnit);
        product.setAttributeValue(context,"GWResponsible",responsibl);
        product.setAttributeValue(context,"GWDesignUnit",designUni);
        product.setAttributeValue(context,"GWDescription",description);
        product.close(context);
        returnMap.put("id",product.getObjectId(context));
        return returnMap;
        }

public MapList getProductTree(Context contex,String[]args)throws Exception
        {
	    System.out.println("in===========>getProductTree");
        Map arguMap 		= (HashMap)JPO.unpackArgs(args);
        String strObjectId 	= (String) arguMap.get("objectId");
        String strExpandLevel = (String) arguMap.get("expandLevel");
        short nExpandLevel =  ProgramCentralUtil.getExpandLevel(strExpandLevel);
        if ("".equals(strObjectId))
        return null;
        MapList mapList  = getTreeInfo(contex,strObjectId,"GWEBOM",nExpandLevel);
        System.out.println("out===========>getProductTree");
        return mapList;
        }

protected MapList getTreeInfo(Context context, String objectId, String relPattern,short nExpandLevel) throws Exception {
        MapList mapList = new MapList();
        MapList resultList = new MapList();
        String rowEditable = "show";
       // System.out.println("getTreeInfo=======nExpandLevel" + nExpandLevel);
        try {
        Pattern typePattern = new Pattern("GWProduct");
        typePattern.addPattern("GWZone");
        typePattern.addPattern("GWPEBlock");
        typePattern.addPattern("GWBlock");
        typePattern.addPattern("GWUnit");
        typePattern.addPattern("GWTray");
        DomainObject rootNodeObj = DomainObject.newInstance(context, objectId);

        StringList objectSelects = new StringList();
        StringList relationshipSelects = new StringList();

        objectSelects.addElement(DomainConstants.SELECT_ID);
        objectSelects.addElement(DomainConstants.SELECT_NAME);
        objectSelects.addElement(DomainConstants.SELECT_TYPE);
        objectSelects.addElement(DomainConstants.SELECT_CURRENT);
        objectSelects.addElement("attribute[Title]");
        objectSelects.addElement("attribute[GWDescription]");
        if (nExpandLevel != 0)
        {
        //selectable to determine if task is summary to display plus sign in SB.
        objectSelects.addElement("from[" + relPattern + "]");
        }
        relationshipSelects.addElement(DomainConstants.SELECT_RELATIONSHIP_ID);
        relationshipSelects.addElement(DomainConstants.SELECT_RELATIONSHIP_TYPE);
        relationshipSelects.addElement(DomainConstants.SELECT_RELATIONSHIP_NAME);
        relationshipSelects.addElement(DomainConstants.SELECT_LEVEL);
        relationshipSelects.addElement("from.id");//Added for "What if"
        //String where = "type != GWTray";
        String strDirection = "from";
        MapList result = rootNodeObj.getRelatedObjects(context,
        relPattern,
        typePattern.getPattern(),
        false,
        true,
        nExpandLevel,
        objectSelects,
        relationshipSelects,
        null,
        "",
        null,
        null,
        null) ;
       // System.out.println("getTreeInfo=======mapList" + mapList);
	    SelectList sPartSelStmts = new SelectList(11);
		sPartSelStmts.add("last.id");
		Map objMap = null;
		String lastRevObjId = "";	
	    for(Iterator itr = result.iterator();itr.hasNext();){
			Map map = (Map) itr.next();
			String currentId = (String)map.get(DomainObject.SELECT_ID);
			DomainObject childObj = new DomainObject(currentId);			
			objMap = childObj.getInfo(context, (StringList) sPartSelStmts);
			lastRevObjId = (String) objMap.get("last.id");
			if(currentId.equals(lastRevObjId) && !"".equals(currentId)){
				mapList.add(map);
			}			
		}
	   
	   
        Iterator itrProTree = mapList.iterator();
        while(itrProTree.hasNext())
        {
        Hashtable hTableWbsTasks = (Hashtable) itrProTree.next();
        //String strTaskType = (String)hTableWbsTasks.get(DomainConstants.SELECT_TYPE);

        if (nExpandLevel != 0)
        {
        //Determine if task is summary inform SB to display a plus sign.
        String strHasSubTask = (String) hTableWbsTasks.get("from[" + relPattern + "]");
        if (strHasSubTask != null)
        hTableWbsTasks.put("hasChildren", strHasSubTask);
        else
        hTableWbsTasks.put("hasChildren", "True");
        }

        hTableWbsTasks.put("RowEditable",rowEditable);
        if(rowEditable.equalsIgnoreCase("readonly"))
        {
        hTableWbsTasks.put("selection","none");
        }
        hTableWbsTasks.put("direction",strDirection);
        }
        }
        catch (Exception e)
        {
        e.printStackTrace();
        throw e;
        }
        finally
        {
        System.out.println("treeInfo============="+ mapList);
        return mapList;
        }
        }
public Map createNewPart(Context context,String[]args)throws Exception
        {
        Part part = (Part)DomainObject.newInstance(context,DomainConstants.TYPE_PART);
        Map returnMap = new HashMap();
        Map programMap = JPO.unpackArgs(args);
        String selectedPartId           = (String) programMap.get("objectId");
        String parentId			        = (String) programMap.get("parentId");
        String partName 				= (String)programMap.get("Name");
        String policy 				= (String)programMap.get("Policy");
        String proAutoName 			= (String)programMap.get("autoNameCheck");
        String type 			= (String)programMap.get("TypeActual");
        String title 			= (String)programMap.get("Title");  //??
        String responsible 			= (String)programMap.get("GWResponsible");
        String description 			= (String)programMap.get("GWDescription");
        String shipowner 			= (String)programMap.get("GWShipowner");
        String shipType 			= (String)programMap.get("GWShipType");
        String manufactureUnit 			= (String)programMap.get("GWManufactureUnit");
        String designUni 			= (String)programMap.get("GWDesignUnit");
        String zoneType 			= (String)programMap.get("GWZoneType");
        String zoneRange 			= (String)programMap.get("GWZoneRange");
        String pEBlockType 			= (String)programMap.get("GWPEBlockType");
        String pEBlockRange 			= (String)programMap.get("GWPEBlockRange");
        String position 			= (String)programMap.get("GWPosition");
        String blockType 			= (String)programMap.get("GWBlockType");

        if(ProgramCentralUtil.isNullString(partName) && proAutoName.equalsIgnoreCase("true")){
        String symbolicTypeName = PropertyUtil.getAliasForAdmin(context, "Type", type, true);
        String symbolicPolicyName = PropertyUtil.getAliasForAdmin(context, "Policy", policy, true);
        partName = FrameworkUtil.autoName(context,
        symbolicTypeName,
        null,
        symbolicPolicyName,
        null,
        null,
        true,
        true);
        }
        if(ProgramCentralUtil.isNullString(policy)){
        policy = part.getDefaultPolicy(context,DomainConstants.TYPE_PART);
        }
        if(ProgramCentralUtil.isNullString(selectedPartId)){
        selectedPartId = parentId;
        }
        BusinessObject newpart = new BusinessObject(type, partName, "A", context
        .getVault().getName());
        newpart.create(context, policy);
        newpart.open(context);
        newpart.setAttributeValue(context,"Title",title);
        newpart.setAttributeValue(context,"GWResponsible",responsible);
        newpart.setAttributeValue(context,"GWDescription",description);
        if("GWProduct".equals(type))
        {
        newpart.setAttributeValue(context,"GWShipType",shipType);
        newpart.setAttributeValue(context,"GWShipowner",shipowner);
        newpart.setAttributeValue(context,"GWManufactureUnit",manufactureUnit);
        newpart.setAttributeValue(context,"GWDesignUnit",designUni);
        }
        if("GWZone".equals(type)) {
        newpart.setAttributeValue(context,"GWZoneType",zoneType);
        newpart.setAttributeValue(context,"GWZoneRange",zoneRange);
        }
        if("GWPEBlock".equals(type)) {
        newpart.setAttributeValue(context,"GWPEBlockType",pEBlockType);
        newpart.setAttributeValue(context,"GWPEBlockRange",pEBlockRange);
        }
        if("GWBlock".equals(type)) {
        newpart.setAttributeValue(context,"GWPosition",position);
        newpart.setAttributeValue(context,"GWBlockType",blockType);
        }
       
        connObject(context,selectedPartId,newpart.getObjectId(context),"GWEBOM","to",null,null);
        newpart.close(context);
        returnMap.put("id", newpart.getObjectId(context));
        return returnMap;
        }
/**
 *
 * @return
 */
public  boolean connObject(Context context, String fromOrToId1,
        String fromOrToId2, String relationName, String fromOrTo,
        String attributeName, String attributeValue) {
        try {
        boolean flag = hasConnect(context, fromOrToId1, fromOrToId2,
        relationName, fromOrTo);
        System.out.println("has connect >>>>" + flag);
        if (flag) {
        if (attributeName == null) {
        MqlUtil.mqlCommand(context,
        "connect bus $1 relationship $2 $3 $4",
        new String[]{fromOrToId1, relationName, fromOrTo,
        fromOrToId2});
        } else {
        MqlUtil.mqlCommand(
        context,
        "connect bus $1 relationship $2 $3 $4 $5 $6",
        new String[] { fromOrToId1, relationName, fromOrTo,
        fromOrToId2, attributeName, attributeValue });
        }
        }
        } catch (FrameworkException e) {
        System.out.println("connObject error!!!!");
        return false;
        }
        return true;
        }
/**
 * @return
 * @throws FrameworkException
 */
public boolean hasConnect(Context context, String fromOrToId1,
        String fromOrToId2, String relationName, String fromOrTo)
        throws FrameworkException {
        boolean flag = true;
        String hasRelStr = MqlUtil.mqlCommand(context, "expand bus '"
        + fromOrToId2 + "' " + fromOrTo + " relationship '"
        + relationName + "' select bus id;");
        if (hasRelStr.indexOf(fromOrToId1) > -1) {
        flag = false;
        }
        return flag;
        }

public boolean isGWProduct(Context contex,String[]args)throws Exception
        {

        boolean isre = false;
        Map programMap 					= (HashMap) JPO.unpackArgs(args);

        String partType = (String)programMap.get("type");
        String[] types = partType.split(",");
        if (types.length == 0)
        return false;
        if(types[0].contains("GWProduct"))
        {
        isre = true;
        }

        return isre;
        }

public boolean isGWZone(Context contex,String[]args)throws Exception
        {

        boolean isre = false;
        Map programMap 					= (HashMap) JPO.unpackArgs(args);

        String partType = (String)programMap.get("type");
        String[] types = partType.split(",");
        if (types.length == 0)
        return false;
        if(types[0].contains("GWZone"))
        {
        isre = true;
        }

        return isre;
        }

public boolean isGWPEBlock(Context contex,String[]args)throws Exception
        {

        boolean isre = false;
        Map programMap 					= (HashMap) JPO.unpackArgs(args);

        String partType = (String)programMap.get("type");
        String[] types = partType.split(",");
        if (types.length == 0)
        return false;
        if(types[0].contains("GWPEBlock"))
        {
        isre = true;
        }
        return isre;
        }

public boolean isGWBlock(Context contex,String[]args)throws Exception
        {

        boolean isre = false;
        Map programMap 					= (HashMap) JPO.unpackArgs(args);

        String partType = (String)programMap.get("type");
        String[] types = partType.split(",");
        if (types.length == 0)
        return false;
        if(types[0].contains("GWBlock"))
        {
        isre = true;
        }
        return isre;
        }
public boolean isGWSpecial(Context contex,String[]args)throws Exception
        {

        boolean isre = false;
        Map programMap 					= (HashMap) JPO.unpackArgs(args);

        String partType = (String)programMap.get("type");
        String[] types = partType.split(",");
        if (types.length == 0)
        return false;
        if(types[0].contains("GWSpecial"))
        {
        isre = true;
        }

        return isre;
        }
public HashMap getPartTypes(Context context, String[] args) throws MatrixException {



        HashMap mapTaskTypeNames = new HashMap();
        StringList slPartSubTypes = new StringList();
        //slPartSubTypes.add("GWProduct");
        slPartSubTypes.add("GWZone");
        slPartSubTypes.add("GWPEBlock");
        slPartSubTypes.add("GWBlock");
        slPartSubTypes.add("GWUnit"); 
		slPartSubTypes.add("GWTray");
       // slPartSubTypes.add("GWSpecial");
        StringList slPartSubTypesIntNames = new StringList();
        int count = 0;
        for (Iterator iterator = slPartSubTypes.iterator(); iterator.hasNext();) {
        String str = (String) iterator.next();
        String i18nTaskTypeName = i18nNow.getTypeI18NString(slPartSubTypes.get(count).toString(),context.getSession().getLanguage());
        slPartSubTypesIntNames.add(i18nTaskTypeName);
        count++;
        }

        mapTaskTypeNames.put("field_choices", slPartSubTypes);
        mapTaskTypeNames.put("field_display_choices", slPartSubTypesIntNames);
        return mapTaskTypeNames;
        }
public String judgeType(String oType)
        {
        String[] types = oType.split(",");
        if (types.length == 0)
        return "";
        if(types[0].contains("GWProduct"))
        {
        return "GWProduct";
        }
        else if (types[0].contains("GWZone"))
        {
        return "GWZone";
        }
        else if (types[0].contains("GWPEBlock"))
        {
        return "GWPEBlock";
        }
        else if (types[0].contains("GWBlock"))
        {
        return "GWBlock";
        }
        else if (types[0].contains("GWUnit"))
        {
        return "GWUnit";
        }
        else if(types[0].contains("GWTray"))
        {
        return "GWTray";
        }
        return "";
        }
public static MapList getStructureCmd(Context context, String[] args) throws Exception
        {
        HashMap programMap = (HashMap) JPO.unpackArgs(args);
        HashMap paramMap   = (HashMap)programMap.get("paramMap");
        HashMap requestMap = (HashMap)programMap.get("requestMap");
        String objectId    = (String)paramMap.get("objectId");
        DomainObject domObj = DomainObject.newInstance(context,objectId);
        Pattern typePattern = new Pattern("GWProduct");
        typePattern.addPattern("GWZone");
        typePattern.addPattern("GWPEBlock");
        typePattern.addPattern("GWBlock");
        typePattern.addPattern("GWUnit");
        typePattern.addPattern("GWTray");
        StringList strSel  = new StringList();
        strSel.add(DomainConstants.SELECT_CURRENT);
        strSel.add(DomainConstants.SELECT_TYPE);
        MapList result = new MapList();
		MapList finalResult = new MapList();
        StringList sSelects = new StringList();
        StringList selectRelStmts  = new StringList();
        StringList selectTypeStmts = new StringList();
        selectTypeStmts.add(domObj.SELECT_NAME);
        selectTypeStmts.add(domObj.SELECT_ID);
		//selectTypeStmts.add("last.id");
        try
        {
        result= (MapList)domObj.getRelatedObjects(context,
        "GWEBOM",
        typePattern.getPattern(),
        selectTypeStmts,
        selectRelStmts,
        false,
        true,
        (short)1,
        null,
        null);
		
		SelectList sPartSelStmts = new SelectList(11);
		sPartSelStmts.add("last.id");
		Map objMap = null;
		String lastRevObjId = "";
		
		for(Iterator itr = result.iterator();itr.hasNext();){
			Map map = (Map) itr.next();
			String currentId = (String)map.get(DomainObject.SELECT_ID);
			DomainObject childObj = new DomainObject(currentId);			
			objMap = childObj.getInfo(context, (StringList) sPartSelStmts);
			lastRevObjId = (String) objMap.get("last.id");
			if(currentId.equals(lastRevObjId) && !"".equals(currentId)){
				finalResult.add(map);
			}			
		}
        } catch ( FrameworkException e){
        throw new FrameworkException(e);
        }
		
        return finalResult;
        }





		
		
		
/**
 * yt showVPMDocument
 * @param context
 * @param args
 * @return
 * @throws Exception
 */
public static MapList showVPMDocument(Context context, String[] args) throws Exception
{
	HashMap programMap = (HashMap) JPO.unpackArgs(args);
	String objectId    = (String)programMap.get("objectOId");
	if("".equals(objectId) || objectId == null){
		objectId = (String)programMap.get("objectId");
	}
	DomainObject domObj = DomainObject.newInstance(context,objectId);
	HashMap argMap = new HashMap();
	MapList firstChildVPMIDList = new MapList();
	StringList select = new StringList();
	select.add(DomainObject.SELECT_ID);
	MapList resultList = new MapList();
	MapList documentList = new MapList();
	Map tempMap = new HashMap();
	tempMap.put("id", objectId);
	firstChildVPMIDList.add(tempMap);
	if(domObj.getType(context).equals("VPMReference")){
		MapList tempList = domObj.getRelatedObjects(context,
		        "GWPartToVPMRes",
		        "Part",
		        select,
		        null,
		        true,
		        false,
		        (short)1,
		        null,
		        null);
		firstChildVPMIDList.addAll(tempList);
	}else{
		MapList childBOMList = domObj.getRelatedObjects(context,
		        "GWEBOM",
		        "Part",
		        select,
		        null,
		        false,
		        true,
		        (short)1,
		        null,
		        null);
		firstChildVPMIDList.addAll(childBOMList);
	}
	System.out.println(firstChildVPMIDList+"------firstChildVPMIDList");
	for(Iterator itr = firstChildVPMIDList.iterator();itr.hasNext();){
		Map map = (Map) itr.next();
		/*argMap.put("objectId", (String)map.get(DomainObject.SELECT_ID));
		MapList documentList = (MapList)JPO.invoke(context,"VPLMDocument",null,"getDocuments",JPO.packArgs(argMap),MapList.class);*/
		DomainObject childObj = new DomainObject((String)map.get(DomainObject.SELECT_ID));
		MapList tempDocList = childObj.getRelatedObjects(context,
		        "GWPartToDocumentRel",
		        "Document",
		        select,
		        null,
		        false,
		        true,
		        (short)1,
		        null,
		        null);
		documentList.addAll(tempDocList);
	}
	
	for(Iterator itr = documentList.iterator();itr.hasNext();){
		Map map = (Map) itr.next();
		argMap.put("objectId", (String)map.get(DomainObject.SELECT_ID));
		MapList tempDocFileList = (MapList)JPO.invoke(context,"emxCommonFileUI",null,"getFiles",JPO.packArgs(argMap),MapList.class);
		resultList.addAll(tempDocFileList);
	}
	System.out.println(resultList+"-----resultList");
	return resultList;
	}


	 public static Vector getFileName(Context context, String[] args)
     throws Exception
 {
	Vector vActions = new Vector();
    HashMap programMap = (HashMap) JPO.unpackArgs(args);
	MapList objectList = (MapList)programMap.get("objectList");
	if(objectList.size() <= 0){
		return vActions;
	}else{
		for( Iterator itr = objectList.iterator(); itr.hasNext();){
			Map mapData = (Map) itr.next();
			String fileName = (String) mapData.get("format.file.name");
			vActions.add(fileName);
		}
		return vActions;
	}
	
 
 }
 
 
  	 public static Vector getTitle(Context context, String[] args)
     throws Exception
 {
	Vector vActions = new Vector();
    HashMap programMap = (HashMap) JPO.unpackArgs(args);
	MapList objectList = (MapList)programMap.get("objectList");
	
	if(objectList.size() <= 0){
		return vActions;
	}else{
		String strLink = "<a href=\"JavaScript:showModalDialog('../common/emxTree.jsp?emxSuiteDirectory=components&amp;relId=null&amp;";
		for( Iterator itr = objectList.iterator(); itr.hasNext();){
			Map mapData = (Map) itr.next();
			String fileId = (String) mapData.get("fileId");
			String type = (String) mapData.get("type");
			String title = new DomainObject(fileId).getAttributeValue(context,"Title");
			
			String strTypeSymName = FrameworkUtil.getAliasForAdmin(context, "type", type, true);
			String typeIcon = EnoviaResourceBundle.getProperty(context,"emxFramework.smallIcon." + strTypeSymName);
			String defaultTypeIcon = "<img src=\"../common/images/"+typeIcon+"\" border=\"0\"></img>";
			String sLink = strLink + "&amp;objectId="+fileId+"&amp;AppendParameters=true&amp;', '700', '600', 'false', 'content', '');\">";
			String strURL = sLink + defaultTypeIcon + "</a> ";
			strURL += sLink + XSSUtil.encodeForXML(context,title) + "</a>&#160;";
			vActions.add("<nobr>"+strURL+"</nobr>");
			//vActions.add(title);
		}
		return vActions;
	}
	
 
 }
 
 
 	 public static Vector getBelongPart(Context context, String[] args)
     throws Exception
 {
	Vector vActions = new Vector();
    HashMap programMap = (HashMap) JPO.unpackArgs(args);
	MapList objectList = (MapList)programMap.get("objectList");
	if(objectList.size() <= 0){
		return vActions;
	}else{
		for( Iterator itr = objectList.iterator(); itr.hasNext();){
			Map mapData = (Map) itr.next();
			String fileId = (String) mapData.get("fileId");
			String belongPartName = new DomainObject(fileId).getInfo(context,"to[GWPartToDocumentRel].from.name");
			vActions.add(belongPartName);
		}
		return vActions;
	}
	
 
 }
 
 


	/**
	 * yt showvpm
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public Vector getVPMPhysicalIdPath(Context context, String[] args) throws Exception {
		Vector vNameVector = new Vector();
		HashMap programMap = (HashMap)JPO.unpackArgs(args);
		MapList objectList = (MapList)programMap.get("objectList");
		Iterator mapListItr = objectList.iterator();
		Map partMap = null; 
		String path = "";

		while(mapListItr.hasNext())
		{

			partMap 		  = (Map)mapListItr.next();
			String sRootNode  = (String)partMap.get("Root Node");
			String objectId = (String)partMap.get(EngineeringConstants.SELECT_ID);
			String parentId = (String)partMap.get("id[parent]");
			DomainObject dmRootObj = DomainObject.newInstance(context, objectId);
			String prdphysicalID = dmRootObj.getInfo(context, "physicalid");
			//for parent physicalID	 Pattern
			if("true".equalsIgnoreCase(sRootNode))
			{
				if(UIUtil.isNotNullAndNotEmpty(prdphysicalID))
					path =prdphysicalID+":3dPlayKeyIdPath";
					vNameVector.add(path);
			}else{
				String relIdStr = MqlUtil.mqlCommand(context, "expand bus "+parentId+"from rel VPMInstance select rel physicalid dump , where 'to.id == "+objectId+"'");
				String relId = "";
				if(relIdStr.length()>0){
					
					String[] relIdArr = relIdStr.split("\n");
					for(String arr : relIdArr){
						if(relId == ""){
							relId = arr.split(",")[6];
						}else{
							relId += "," + arr.split(",")[6];
						}
						
					}
				}
				String childprdphysicalID = "";
				String[] relArr = relId.split(",");
				for(String a : relArr){
					String temp = a + "/" + prdphysicalID;
					if("".equals(childprdphysicalID)){
						childprdphysicalID = temp;
					}else{
						childprdphysicalID += "," + temp;
					}
				}
				
				for(String key :childprdphysicalID.split(",")){
					path = key + ":3dPlayKeyIdPath";
					if(!vNameVector.contains(path)){
						vNameVector.add(path);
					}
				}

			}
			
				
		}
		return vNameVector;
	}

	//EDIT BY ZY
 public Object getVersion (Context context, String[] args) throws Exception {
        // unpack and get parameter
        HashMap programMap  = (HashMap)JPO.unpackArgs(args);
        Map paramList = (Map)programMap.get("paramList");
        MapList objectList = (MapList)programMap.get("objectList");
        String objectId = (String) paramList.get("objectId");
        String fromPage = (String) paramList.get("fromPage");
        if (fromPage != null) {
            fromPage = fromPage.trim();
        }
        boolean isprinterFriendly = false;
        if (paramList.get("reportFormat") != null) {
            isprinterFriendly = true;
        }

        Vector versionVector = new Vector(objectList.size());
        Map objectMap = null;
        StringBuffer sBuff = new StringBuffer(256);
        StringBuffer sbNextURL = new StringBuffer(128);

        // loop through objects that are in the UI table.  populate Vector
        // with the appropriate revision value.
        for (int i = 0; i < objectList.size(); i++) {
            sBuff= new StringBuffer(256);
            sbNextURL = new StringBuffer(256);
            objectMap = (Map) objectList.get(i);
            String vcname = (String)objectMap.get("vcName");
            String versionid = (String)objectMap.get("versionId");
            String folderPath = (String)objectMap.get("folderPath");
            // set a revision level for the object.
            sbNextURL.append("../common/emxTree.jsp?objectId=");
            sbNextURL.append(XSSUtil.encodeForJavaScript(context, objectId));
            sbNextURL.append("&mode=insert&jsTreeID=");
            sbNextURL.append(XSSUtil.encodeForJavaScript(context, (String) paramList.get("jsTreeID")));
            sbNextURL.append("&treeMenu=type_VCBranches&treeLabel=");
            sbNextURL.append(XSSUtil.encodeForJavaScript(context, versionid));
            sbNextURL.append("&AppendParameters=true");
            sbNextURL.append("&versionid=");
            sbNextURL.append(XSSUtil.encodeForJavaScript(context, versionid));
            sbNextURL.append("&fromPage=");
            sbNextURL.append(XSSUtil.encodeForJavaScript(context, fromPage));
            sbNextURL.append("&vcName=");
            sbNextURL.append(XSSUtil.encodeForJavaScript(context, vcname));
            sbNextURL.append("&folderPath=");
            sbNextURL.append(folderPath);

            if (!isprinterFriendly) {
                sBuff.append("<a href ='");
                sBuff.append(sbNextURL.toString());
                sBuff.append(" ' class='object' target=\"content\">");
            }
            sBuff.append(XSSUtil.encodeForHTML(context, vcname)+""+XSSUtil.encodeForHTML(context, versionid));
            if (!isprinterFriendly) {
               sBuff.append("</a>");
            }
            versionVector.add(sBuff.toString());
        }

        return versionVector;
    }	

	public Object getOwner (Context context, String[] args) throws Exception {
        return  buildVector(context, args, "author");
    }
	
	
	 public Object buildVector(Context context, String[] args, String selectable) throws Exception {
        // unpack and get parameter
        HashMap programMap  = (HashMap) JPO.unpackArgs(args);
        MapList objectList = (MapList) programMap.get("objectList");
        Vector<String> returnVector = new Vector<String>(objectList.size());

        // loop through objects that are in the UI table.
        // extract the value and add it to the result vector.
        for (int i = 0; i < objectList.size(); i++) {
            Map objectMap = (Map) objectList.get(i);
            Object obj = objectMap.get(selectable);

            if (obj == null) {
                returnVector.add("");
            } else if (obj.getClass().equals(StringList.class)) {
                returnVector.add(FrameworkUtil.join((StringList)obj, ", "));
            } else {
                returnVector.add((String) obj);
            }
        }

        return returnVector;
    }
	
	 @com.matrixone.apps.framework.ui.ProgramCallable
    public static MapList getRevisions(Context context, String[] args)
        throws Exception
    {

        HashMap map = (HashMap) JPO.unpackArgs(args);

        String       objectId = (String) map.get("objectId");
		System.out.println("objectId........."+objectId);
        DomainObject busObj   = DomainObject.newInstance(context, objectId);
		MapList revisionsList = new MapList();
		StringList busSelects = new StringList(1);
        busSelects.add(DomainObject.SELECT_ID);
		if(busObj.getType(context).equals("VPMReference")){
			StringList select = new StringList();
			select.add(DomainObject.SELECT_ID);
			MapList tempList = busObj.getRelatedObjects(context,
		        "GWPartToVPMRes",
		        "Part",
		        select,
		        null,
		        true,
		        false,
		        (short)1,
		        null,
		        null);	
			if(tempList != null && tempList.size()>0){
				Map tempmap = (Map)tempList.get(0);
				String trayId = (String)tempmap.get(DomainObject.SELECT_ID);
				DomainObject trayObj   = DomainObject.newInstance(context, trayId);
				revisionsList = trayObj.getRevisionsInfo(context,busSelects,
                                                        new StringList(0));
			}
		}else{
			revisionsList = busObj.getRevisionsInfo(context,busSelects,
                                                        new StringList(0));	
		}
        return revisionsList;
    }
}