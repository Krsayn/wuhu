/**
**   Copyright (c) 1992-2015 Dassault Systemes.
**   All Rights Reserved.
**   This program contains proprietary and trade secret information of
**   MatrixOne, Inc.  Copyright notice is precautionary only and does
**   not evidence any actual or intended publication of such program
**
*/

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

import com.dassault_systemes.i3dx.appsmodel.matrix.Relationship;
import com.dassault_systemes.i3dx.engineadapter.base.I3DXRequestManager.booleanRef;
import com.matrixone.apps.common.Person;
import com.matrixone.apps.common.Plant;
import com.matrixone.apps.common.util.ComponentsUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.i18nNow;
import com.matrixone.apps.domain.util.mxBus;
import com.matrixone.apps.program.ProgramCentralConstants;


public class ${CLASSNAME}  extends ${CLASS:emxDomainObject} {

    // The operator symbols
    /** A string constant with the value ==. */
    protected static final String SYMB_EQUAL = " == ";
    /** A string constant with the value '. */
    protected static final String SYMB_QUOTE = "'";
    /** A string constant with the value *. */
    protected static final String SYMB_WILD = "*";
    /** A string constant with the value attribute. */
    protected static final String SYMB_ATTRIBUTE = "attribute";
    /** A string constant with the value [. */
    protected static final String SYMB_OPEN_BRACKET = "[";
    /** A string constant with the value ]. */
    protected static final String SYMB_CLOSE_BRACKET = "]";
    /** A string constant with the value ",". */
    protected static final String SYMB_COMMA = ",";
    /** A string constant with the value "string". */
    protected static final String SYMB_STRING = "string";
    /** A string constant with the value "". */
    protected static final String SYMB_EMPTY_STRING = "";
    /** A string constant with the value "descending". */
    protected static final String SYMB_DESCENDING = "descending";
    /** A string constant with the value "0". */
    protected static final String SYMB_ZERO = "0";
    /** A string constant with the value "0000000001". */
    protected static final String FIRST_PLANT_ID = "0000000001";
    /** A string constant with the value "objectId". */
    protected static final String SELECT_OBJECT_ID = "objectId";
    /** A string constant with the value "current". */
    protected static final String SELECT_STATE = "current";
    /** A string constant that defines string resource file name for common components */
    protected static String RESOURCE_BUNDLE_COMPONENTS_STR = "emxComponentsStringResource";

    /** A string constant with the value "~". */
    protected static final String SYMB_TILT = "~";
    /** A string constant with the value "true". */
    protected static final String BOOL_TRUE = "true";
    /** A string constant with the value "|". */
    protected static final String BOOL_FALSE = "false";
    /** A string constant with the value "|". */
    protected static final String SYMB_PIPE = "|";

    /** A String constant to represent "from["*/
    private static String SELECT_FROM_LEFTBRACE = "from[";
    /** A String constant to represent "]"*/
    private static String SELECT_RIGHTBRACE = "]";
    /** A String constant to represent "."*/
    private static String DOT = ".";
    /** A String constant to represent "attribute["*/
    private static String SELECT_ATTRIBUTE_LEFTBRACE = "attribute[";
    /** A String constant to represent attribute "Plant ID" */
    private static String ATTRIBUTE_PLANT_ID = PropertyUtil.getSchemaProperty("attribute_PlantID");
    /** A String constant to represent "attribute[Plant ID]"*/
    private static String SELECT_PLANT_ID= SELECT_ATTRIBUTE_LEFTBRACE+ATTRIBUTE_PLANT_ID+SELECT_RIGHTBRACE;
    /** A String constant to represent type "Plant"*/
    private static String TYPE_PLANT    = PropertyUtil.getSchemaProperty("type_Plant");


    /**
     * Constructor.
     *
     * @param context the eMatrix <code>Context</code> object
     * @param args holds no arguments
     * @throws Exception if the operation fails
     */
    public ${CLASSNAME} (Context context, String[] args)
      throws Exception {
        super(context, args);
    }

     /**
     * Main entry point.
     * @param context the eMatrix <code>Context</code> object
     * @param args holds no arguments
     * @return an integer status code (0 = success)
     * @throws Exception if the operation fails
     */
    public int mxMain(Context context, String[] args)
      throws Exception {
    	
      if (true) {
          throw new Exception(ComponentsUtil.i18nStringNow("emxComponents.Common.ERROR", context.getLocale().getLanguage()));
      }
      return 0;
    }

     /**
     * This method is used to get the list of all Plants connected to Business Unit
     * by Organization Plant relationship.
     * @author Sudeep Kumar Dwivedi/Kaustav Banerjee
     * @param context the eMatrix <code>Context</code> object
     * @param args holds the following input arguments:
     *        0 - HashMap containing one String entry for key "objectId"
     * @return Object of type MapList
     */
    @com.matrixone.apps.framework.ui.ProgramCallable
    public MapList getGWShipAttributes(Context context, String args[])
        throws Exception {

        HashMap programMap           = (HashMap)JPO.unpackArgs(args);
       
        MapList mapList            = new MapList();
        
        try { 
        StringList selectStmts=new StringList();
        selectStmts.addElement(DomainConstants.SELECT_ID);
        selectStmts.addElement(DomainConstants.SELECT_NAME);
        selectStmts.addElement(DomainConstants.SELECT_TYPE);
        selectStmts.addElement("attribute[GWDescription]");
        selectStmts.addElement("attribute[GWDesignOrg]");
        selectStmts.addElement("attribute[GWMakeOrg]");
        selectStmts.addElement("attribute[GWShipCharger]");
        selectStmts.addElement("attribute[GWShipName]");
        selectStmts.addElement("attribute[GWNumber]");
        selectStmts.addElement("attribute[GWShipOwner]");
        selectStmts.addElement("attribute[GWShipType]");
        
        
        System.out.println();
        
        mapList =DomainObject.findObjects(context, "GWShip", null, null,selectStmts );
      
        System.out.println("*************size+***************"+mapList.size());
        }catch(FrameworkException ex) {
	   throw ex;
     }
       
       return mapList; 

         
    }
    
    /**
                
     * @param context
     * @param args
     * @return
     * @throws FrameworkException
     */
    @com.matrixone.apps.framework.ui.CreateProcessCallable
    public Map createGWShipObject(Context context, String args[]) throws FrameworkException {
        try {
            HashMap requestMap = (HashMap)JPO.unpackArgs(args);
            HashMap programMap  = (HashMap)JPO.unpackArgs(args);
            Locale locale = (Locale)requestMap.get("localeObj"); 
           
            String type = "GWShip";
            String autonameCheck = (String) programMap.get("autoNameCheck");
			
            String name = (String) requestMap.get("Name");
            String description=(String) requestMap.get("Description");
            String policy=(String) requestMap.get("Policy");
            
            System.out.println("****type++***************"+type);
            System.out.println("****Policy++***************"+policy);
            
            String vault="eService Production";
            
            String GWShipName=(String)requestMap.get("GWShipName");
            String GWShipType=(String)requestMap.get("GWShipType");
            String GWDesignOrg=(String)requestMap.get("GWDesignOrg");
            String GWMakeOrg=(String)requestMap.get("GWMakeOrg");
            String GWShipOwner=(String)requestMap.get("GWShipOwner");
            String GWShipCharger=(String)requestMap.get("GWShipCharger");
            
            
            System.out.println("****GWDesignOrg******"+GWDesignOrg);
            System.out.println("****GWShipOwner******"+GWShipOwner);
            System.out.println("****GWShipCharger******"+GWShipCharger);
            System.out.println("****description******"+description);
            
            
            if("true".equalsIgnoreCase(autonameCheck)){
				String symbolicTypeName 	= PropertyUtil.getAliasForAdmin(context, "Type", type, true);
				String symbolicPolicyName 	= PropertyUtil.getAliasForAdmin(context, "Policy", policy, true);
				 

				name =  FrameworkUtil.autoName(context,
						symbolicTypeName,
						null,
						symbolicPolicyName,
						null,
						null,
						true,
						true);
				System.out.println("***********name***********"+name);
				//name=name.replace("auto", "GWStudent");
				System.out.println("<<<<<<<<<<<<<<<<name>>>>>>>>>"+name);
			}else {
				name=name;
			}
            //String policy = (String) requestMap.get("Policy");
            //policy = policy != null ? FrameworkUtil.getAliasForAdmin(context, "policy", policy, true) : policy;
            DomainObject domainObj=new DomainObject();
            domainObj.createObject(context, type, name, System.currentTimeMillis()+"", policy, vault);
            domainObj.setDescription(context, description);
            
            domainObj.setAttributeValue(context, "GWShipName", GWShipName);
            domainObj.setAttributeValue(context, "GWShipType", GWShipType);
            domainObj.setAttributeValue(context, "GWDesignOrg", GWDesignOrg);
            domainObj.setAttributeValue(context, "GWMakeOrg", GWMakeOrg);
            domainObj.setAttributeValue(context, "GWShipOwner", GWShipOwner);
            domainObj.setAttributeValue(context, "GWShipCharger", GWShipCharger);
            
            
           
            
            
            
            // Need the BusinessUnit id in order to add a Region.
           /* if (strOrganizationId == null) {
              throw new FrameworkException(ComponentsUtil.i18nStringNow("emxComponents.Common.InvalidRequestParameters", locale.getLanguage()));
            }
            
            DomainObject organization   = new DomainObject(strOrganizationId);
            policy = policy == null || policy.equals("") || policy.equals("null") ? organization.getDefaultPolicy(context, type) : policy;*/
            
            HashMap map = new HashMap(1);
            map.put(DomainConstants.SELECT_ID, domainObj.getObjectId());
            return map;
        } catch (Exception e) {
            throw new FrameworkException(e);
        }
    }
    

    public void updateGWShipName(Context context, String[] args) throws Exception {
    	Map inputMap = JPO.unpackArgs(args);
		Map paramMap = (Map) inputMap.get("paramMap");
		
		String objectId = (String) paramMap.get("objectId");
		String newAttrValue = (String) paramMap.get("New Value");
		
		DomainObject domObj=new DomainObject(objectId);
		domObj.setAttributeValue(context, "GWShipName", newAttrValue);
    }
    
    public void updateGWShipType(Context context, String[] args) throws Exception {
    	Map inputMap = JPO.unpackArgs(args);
		Map paramMap = (Map) inputMap.get("paramMap");
		
		String objectId = (String) paramMap.get("objectId");
		String newAttrValue = (String) paramMap.get("New Value");
		
		DomainObject domObj=new DomainObject(objectId);
		domObj.setAttributeValue(context, "GWShipType", newAttrValue);
    }
    
    public void updateGWDesignOrg(Context context, String[] args) throws Exception {
    	Map inputMap = JPO.unpackArgs(args);
		Map paramMap = (Map) inputMap.get("paramMap");
		
		String objectId = (String) paramMap.get("objectId");
		String newAttrValue = (String) paramMap.get("New Value");
		
		DomainObject domObj=new DomainObject(objectId);
		domObj.setAttributeValue(context, "GWDesignOrg", newAttrValue);
    }
    
    public void updateGWMakeOrg(Context context, String[] args) throws Exception {
    	Map inputMap = JPO.unpackArgs(args);
		Map paramMap = (Map) inputMap.get("paramMap");
		
		String objectId = (String) paramMap.get("objectId");
		String newAttrValue = (String) paramMap.get("New Value");
		
		DomainObject domObj=new DomainObject(objectId);
		domObj.setAttributeValue(context, "GWMakeOrg", newAttrValue);
    }
    
    public void updateGWShipOwner(Context context, String[] args) throws Exception {
    	Map inputMap = JPO.unpackArgs(args);
		Map paramMap = (Map) inputMap.get("paramMap");
		
		String objectId = (String) paramMap.get("objectId");
		String newAttrValue = (String) paramMap.get("New Value");
		
		DomainObject domObj=new DomainObject(objectId);
		domObj.setAttributeValue(context, "GWShipOwner", newAttrValue);
    }
    
    public void updateGWShipCharger(Context context, String[] args) throws Exception {
    	Map inputMap = JPO.unpackArgs(args);
		Map paramMap = (Map) inputMap.get("paramMap");
		
		String objectId = (String) paramMap.get("objectId");
		String newAttrValue = (String) paramMap.get("New Value");
		
		DomainObject domObj=new DomainObject(objectId);
		domObj.setAttributeValue(context, "GWShipCharger", newAttrValue);
    }
    
    public void updateDescription(Context context, String[] args) throws Exception {
    	Map inputMap = JPO.unpackArgs(args);
		Map paramMap = (Map) inputMap.get("paramMap");
		
		String objectId = (String) paramMap.get("objectId");
		String newAttrValue = (String) paramMap.get("New Value");
		
		DomainObject domObj=new DomainObject(objectId);
		domObj.setDescription(context, newAttrValue);
		
    }
    
    public HashMap getPartTypes(Context context, String[] args) throws MatrixException {

    	
		HashMap mapPartTypeNames = new HashMap();
		StringList partTypes = new StringList();
        StringList partTypesIntNames=new StringList();
		partTypes.add("GWZone");
		partTypes.add("GWBlock");
        partTypes.add("GWPEBlock");
        partTypes.add("GWUnit");
        partTypes.add("GWTray");
        
        
		int count = 0;
        String language = context.getSession().getLanguage();
		for (Iterator iterator = partTypes.iterator(); iterator.hasNext();) {
			String str = (String) iterator.next();
			
			
            String i18nTaskTypeName = i18nNow.getTypeI18NString(partTypes.get(count).toString(),language);
            partTypesIntNames.add(i18nTaskTypeName);
			count++;
		}

		mapPartTypeNames.put("field_choices", partTypes);
		mapPartTypeNames.put("field_display_choices", partTypesIntNames);
		return mapPartTypeNames;
	}
    
    /*public boolean isTest(Context context,String args[]) throws Exception{
    try {
    	String taskType; 
    	boolean access=false;
    	
    	HashMap programMap = (HashMap) JPO.unpackArgs(args);
		String sType = (String) programMap.get("type");
		
		if (sType != null)
		{
			StringList taskTypeList = FrameworkUtil.split(sType, ProgramCentralConstants.COMMA);

			if (((String)taskTypeList.get(0)).contains("_selectedType:"))
			{
				taskType = (String)(FrameworkUtil.split((String)taskTypeList.get(0), ":")).get(1);
				System.out.println("********taskType***********"+taskType);
				if("GWTray".equals(taskType)) {
		    		access=true;
		    	}
			}

			
		}
	
		System.out.println("********sType***********"+sType);
  return access;
    }catch(Exception e){
		throw new MatrixException(e);
	}
		
	}*/
    
    public boolean isGWZone(Context context,String args[]) throws Exception{
        try {
        	String taskType; 
        	boolean access=false;
        	
        	HashMap programMap = (HashMap) JPO.unpackArgs(args);
    		String sType = (String) programMap.get("type");
    		
    		if (sType != null)
    		{
    			StringList taskTypeList = StringUtil.split(sType, ProgramCentralConstants.COMMA);

    			if (((String)taskTypeList.get(0)).contains("_selectedType:"))
    			{
    				taskType = (String)(FrameworkUtil.split((String)taskTypeList.get(0), ":")).get(1);
    				System.out.println("********taskType***********"+taskType);
    				if("GWZone".equals(taskType)) {
    		    		access=true;
    		    	}
    			}

    			
    		}
    	
    		System.out.println("********sType***********"+sType);
            return access;
        }catch(Exception e){
    		throw new MatrixException(e);
    	}
    		
    	}
    
    public boolean isGWBlock(Context context,String args[]) throws Exception{
        try {
        	String taskType; 
        	boolean access=false;
        	
        	HashMap programMap = (HashMap) JPO.unpackArgs(args);
    		String sType = (String) programMap.get("type");
    		
    		if (sType != null)
    		{
    			StringList taskTypeList = StringUtil.split(sType, ProgramCentralConstants.COMMA);

    			if (((String)taskTypeList.get(0)).contains("_selectedType:"))
    			{
    				taskType = (String)(FrameworkUtil.split((String)taskTypeList.get(0), ":")).get(1);
    				System.out.println("********taskType***********"+taskType);
    				if("GWBlock".equals(taskType)) {
    		    		access=true;
    		    	}
    			}

    			
    		}
    	
    		System.out.println("********sType***********"+sType);
            return access;
        }catch(Exception e){
    		throw new MatrixException(e);
    	}
    		
    	}
  
    public boolean isGWPEBlock(Context context,String args[]) throws Exception{
        try {
        	String taskType; 
        	boolean access=false;
        	
        	HashMap programMap = (HashMap) JPO.unpackArgs(args);
    		String sType = (String) programMap.get("type");
    		
    		if (sType != null)
    		{
    			StringList taskTypeList = StringUtil.split(sType, ProgramCentralConstants.COMMA);

    			if (((String)taskTypeList.get(0)).contains("_selectedType:"))
    			{
    				taskType = (String)(FrameworkUtil.split((String)taskTypeList.get(0), ":")).get(1);
    				System.out.println("********taskType***********"+taskType);
    				if("GWPEBlock".equals(taskType)) {
    		    		access=true;
    		    	}
    			}

    			
    		}
    	
    		System.out.println("********sType***********"+sType);
            return access;
        }catch(Exception e){
    		throw new MatrixException(e);
    	}
    		
    	}
    
}