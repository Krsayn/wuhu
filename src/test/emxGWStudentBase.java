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
import com.matrixone.apps.domain.util.i18nNow;
import com.matrixone.apps.domain.util.mxBus;


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
    public MapList getGWStudentAttributes(Context context, String args[])
        throws Exception {

        HashMap programMap           = (HashMap)JPO.unpackArgs(args);
       
        MapList mapList            = new MapList();
        
        try { 
        StringList selectStmts=new StringList();
        selectStmts.addElement(DomainConstants.SELECT_ID);
        selectStmts.addElement(DomainConstants.SELECT_NAME);
        selectStmts.addElement(DomainConstants.SELECT_TYPE);
        selectStmts.addElement("attribute[GWName]");
        selectStmts.addElement("attribute[GWAge]");
        selectStmts.addElement("attribute[GWSex]");
        selectStmts.addElement("to[GWSchoolPeopleRelation].from.name");
        System.out.println();
        
      
        
        mapList =DomainObject.findObjects(context, "GWStudent", null, null,selectStmts );
  
        }catch(FrameworkException ex) {
	   throw ex;
     }
       
       return mapList; 

         
    }
    
    public Vector getChineseScore(Context context, String args[])
            throws Exception {
    	
        HashMap programMap  = (HashMap)JPO.unpackArgs(args);
        
        MapList objectList = (MapList)programMap.get("objectList");
        Vector resultsVector=new Vector(objectList.size());
        System.out.println("objectList===="+objectList.size());
        for (int i=0;i<objectList.size();i++) {
        	Map map=(Map)objectList.get(i);
        	String studentID=(String)map.get(DomainObject.SELECT_ID);
        	
        	@SuppressWarnings("deprecation")
        	String chineseID=MqlUtil.mqlCommand(context, "print bus GWSubject Chinese 1 select id dump");
        	
        	String attributeString=null;
        	
        	@SuppressWarnings("deprecation")
        	String expandString =MqlUtil.mqlCommand(context,"expand bus "+studentID+" to rel GWSchoolScoreRelation"
        			+ " select rel attribute[GWScore] where from.id=="+chineseID+" dump &");
        	
        	if(expandString.split("&").length>6) {
        		attributeString =expandString.split("&")[6];
        		resultsVector.add(attributeString);
        	}else {
        		resultsVector.add("");
        	}
        	
        }
    	return resultsVector;
    	
    }
    
    public Vector getEnglishScore(Context context, String args[])
            throws Exception {
    	
       HashMap programMap  = (HashMap)JPO.unpackArgs(args);
        
        MapList objectList = (MapList)programMap.get("objectList");
        Vector resultsVector=new Vector(objectList.size());
       
        StringList slBusSelect=new StringList();   //  object attribute package
    	slBusSelect.addElement(DomainConstants.SELECT_ID);
    	slBusSelect.addElement(DomainConstants.SELECT_NAME);
    	slBusSelect.addElement(DomainConstants.SELECT_TYPE);
    	StringList relSelect=new StringList();   // rel attribute package   
    	relSelect.addElement(DomainConstants.SELECT_RELATIONSHIP_ID);
    	relSelect.addElement("attribute[GWScore]");
    	
    	MapList englishList=DomainObject.findObjects(context,"GWSubject",null,"name==English",slBusSelect);
    	
    	Map e_map=(Map)englishList.get(0);
    	String englishID=(String) e_map.get(DomainConstants.SELECT_ID);
    	
        //String englishID=MqlUtil.mqlCommand(context, "print bus GWSubject English 1 select id dump");
    	//System.out.println("====chineseID==1=====>"+englishID);
        
        for (int i=0;i<objectList.size();i++) {
        	Map map=(Map)objectList.get(i);
        	String studentID=(String)map.get(DomainObject.SELECT_ID);
        	
        	@SuppressWarnings("deprecation")
        	
        	String attributeString=null;
        	
        	//@SuppressWarnings("deprecation")
        	String expandString =MqlUtil.mqlCommand(context,"expand bus "+studentID+" to rel GWSchoolScoreRelation"
        			+ " select rel attribute[GWScore] where from.id=="+englishID+" dump &");
        	System.out.println("======expandString=====>"+expandString);
        	if(expandString.split("&").length>6) {
        		attributeString =expandString.split("&")[6];
        		resultsVector.add(attributeString);
        	}else {
        		resultsVector.add("");
        	}
        	
        	
        	
        	DomainObject domObject=DomainObject.newInstance(context,studentID);//
        	
        	
        	//String englishID=(java.lang.String) ((Map)englishList.get(0)).get(DomainConstants.SELECT_ID);
        	
        	MapList studentList=domObject.getRelatedObjects
        			(context, 
        			"GWSchoolScoreRelation", 
        			"GWSubject",
        			slBusSelect,
        			relSelect, true, false, (short)1,"id == "+englishID, null,0);// == 
        	
        	
        	if(studentList.isEmpty()) {
        		resultsVector.add("");
        	}else {
        		String colorScore="";
        		Map temp_Map =(Map)studentList.get(0);
        		
        		Integer Score=Integer.valueOf((String)temp_Map.get("attribute[GWScore]"));
        		
        		if(Score<60) {
        			colorScore="<p><font color=\"red\">"+Score+"</font></p>";
        		}else {
        			colorScore="<p><font color=\"green\">"+Score+"</font></p>";
        			

        		}
        		resultsVector.add(colorScore);
        	}
        	
        	
        }
       
    	return resultsVector;
    	
    }
    
    @com.matrixone.apps.framework.ui.CreateProcessCallable
    public Map createStudentObject(Context context, String args[]) throws FrameworkException {
        try {
            HashMap requestMap = (HashMap)JPO.unpackArgs(args);
            HashMap programMap  = (HashMap)JPO.unpackArgs(args);
            Locale locale = (Locale)requestMap.get("localeObj"); 
           // String strOrganizationId = (String) requestMap.get("objectId");
            String type = "GWStudent";
            String autonameCheck = (String) programMap.get("autoNameCheck");
			
            String name = (String) requestMap.get("Name");
            String description=(String) requestMap.get("Description");
            String policy="GWSchoolPolicy";
            String vault="eService Production";
            String GWName=(String)requestMap.get("GWName");
            String ChineseScore=(String)requestMap.get("GWChineseScore");
            String sex=(String)requestMap.get("GWSex");
            String classId=(String)requestMap.get("GWClass");
            
            System.out.println("**********classId***************"+classId);
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
            domainObj.setAttributeValue(context, "GWName", GWName);
            domainObj.setAttributeValue(context, "GWSex", sex);
            
            String ChineseID=MqlUtil.mqlCommand(context, "print bus GWSubject Chinese 1 select id dump");
           
            
            DomainObject chineseObj=new DomainObject(ChineseID);
            
            DomainRelationship relationship=domainObj.connect(context, "GWSchoolScoreRelation", chineseObj, true);
            
            
            
            relationship.setAttributeValue(context, "GWScore", ChineseScore);
            
            DomainObject classObject=new DomainObject(classId);
            DomainRelationship relationship1=domainObj.connect(context, "GWSchoolPeopleRelation", classObject, true);
            
            
            
            // Need the BusinessUnit id in order to add a Region.
           /* if (strOrganizationId == null) {
              throw new FrameworkException(ComponentsUtil.i18nStringNow("emxComponents.Common.InvalidRequestParameters", locale.getLanguage()));
            }
            
            DomainObject organization   = new DomainObject(strOrganizationId);
            policy = policy == null || policy.equals("") || policy.equals("null") ? organization.getDefaultPolicy(context, type) : policy;*/
            
            Jedis jedis;
            
            jedis = new Jedis("localhost", 6379);

            Map<String, String> map1 = new HashMap<String, String>();
            map1.put("id",domainObj.getObjectId());
            map1.put("ChineseScore",ChineseScore);
            jedis.hmset(domainObj.getObjectId(),map1);
            
            
            HashMap map = new HashMap(1);
            map.put(DomainObject.SELECT_ID, domainObj.getObjectId());
            return map;
        } catch (Exception e) {
            throw new FrameworkException(e);
        }
    }
    
    public Map getOrgRangeValues(Context context, String[] args) throws Exception {
        HashMap tempMap = new HashMap();
        StringList fieldRangeValues = new StringList();
        StringList fieldDisplayRangeValues = new StringList();
        
        StringList slBusSelect=new StringList();   //  object attribute package
    	slBusSelect.addElement(DomainConstants.SELECT_ID);
    	slBusSelect.addElement(DomainConstants.SELECT_NAME);
    	slBusSelect.addElement(DomainConstants.SELECT_TYPE);
    	slBusSelect.addElement("attribute[GWName]");
    	MapList classListList=DomainObject.findObjects(context,"GWClass",null,null,slBusSelect);
    	
    	 for(int j = 0; j < classListList.size(); j++) {
    		 Map temp_mapMap = (Map) classListList.get(j);
    		 
     		 String temp_id=(String)temp_mapMap.get(DomainConstants.SELECT_ID);
     		String temp_class=(String)temp_mapMap.get(DomainConstants.SELECT_NAME);
     		 
     		
    				 
             fieldRangeValues.add(temp_id);
             fieldDisplayRangeValues.add(temp_class);
         }

        

        tempMap.put("field_choices", fieldRangeValues);
        tempMap.put("field_display_choices", fieldDisplayRangeValues);
        return tempMap;
    }
    
    public void delete(Context context, String[] args) throws Exception {
    	 HashMap programMap  = (HashMap)JPO.unpackArgs(args);
        
    	 String[] strObjectIDArr = (String[])programMap.get("emxTableRowIds");
    	 System.out.println(" nnnnnnnnnn"+strObjectIDArr);
    	 DomainObject.deleteObjects(context,strObjectIDArr);
    }
    
    public void updateGWName(Context context, String[] args) throws Exception {
    	Map inputMap = JPO.unpackArgs(args);
		Map paramMap = (Map) inputMap.get("paramMap");
		
		String objectId = (String) paramMap.get("objectId");
		String newAttrValue = (String) paramMap.get("New Value");
		
		DomainObject domObj=new DomainObject(objectId);
		domObj.setAttributeValue(context, "GWName", newAttrValue);
    }
    
        /*public void updateChineseScore(Context context, String[] args) throws Exception {
    	Map inputMap = JPO.unpackArgs(args);
		Map paramMap = (Map) inputMap.get("paramMap");
		
		String objectId = (String) paramMap.get("objectId");
		String newAttrValue1 = (String) paramMap.get("New Value");
		
		DomainObject domObj=new DomainObject(objectId);
		String ChineseID=MqlUtil.mqlCommand(context, "print bus GWSubject Chinese 1 select id dump");
		
        
        
        DomainObject chineseObj=new DomainObject(ChineseID);
        
        String relationShipId = MqlUtil.mqlCommand(context, "expand bus "+objectId+" to rel GWSchoolScoreRelation select rel id where to.id=="+chineseId+" dump #");
        relationShipId = relationShipId.split("@");
      
         DomainRelationship relationship=domObj.connect(context, "GWSchoolScoreRelation", chineseObj, true);
              
              Integer score=Integer.valueOf(newAttrValue1);
              System.out.println("^^^^^^^^^^^^^^^^^^^^^^^"+score);
              if(score>100 || score<0)
              {
              	
                  String sErrMsg = "chinese score is out of range";
                  throw new MatrixException(sErrMsg);
              }else {
              
              relationship.setAttributeValue(context, "GWScore", newAttrValue1);
              }
        	
       
        
        
        
    }*/

    public void updateChineseScore(Context context, String args[]) throws FrameworkException {
        try {
        	System.out.println("&&&&&&&&&&&&&&&&&&&&&begin");
            HashMap requestMap = (HashMap)JPO.unpackArgs(args);
            Map paramMap = (Map) requestMap.get("paramMap");
            String objectId = (String)paramMap .get("objectId");
            String chineseId = MqlUtil.mqlCommand(context, "print bus GWSubject Chinese 1 select id dump");
            System.out.println("!!!!!!!!!chineseID!!!!!!!!!!!!!!!!"+chineseId);
            String relationShipId = MqlUtil.mqlCommand(context, "expand bus "+objectId+" to rel GWSchoolScoreRelation select rel id where from.id=="+chineseId+" dump &");
            System.out.println("!!!!!!!!!!!relationShipId!!!!!!!!!!!!!!"+relationShipId);
            if("".equals(relationShipId)) {
            	String sErrMsg="Chinese is null";
            	throw new FrameworkException(sErrMsg);
            	
            }else {
            relationShipId=relationShipId.split("&")[6];
            System.out.println("!!!!!!!!!!!relationShipId!!!!!!!!!!!!!!"+relationShipId);
            String newAttrValue = (String) paramMap.get("New Value");
            System.out.println("newAttrValue==========>"+newAttrValue);
       
        
             
            	Integer score=Integer.valueOf(newAttrValue);
                if(0>score||score>100) {
                	String sErrMsg="Chinese is out of range";
                	throw new FrameworkException(sErrMsg);
                }else {
                	DomainRelationship domainRelationship =new DomainRelationship(relationShipId);
                	domainRelationship.setAttributeValue(context, "GWScore", newAttrValue);
                   
                }
         
            }
            	
        } catch (Exception e) {
            throw new FrameworkException(e);
        }
    }
     
    public void updateClass(Context context, String args[])    {
    	try {
    	HashMap requestMap = (HashMap)JPO.unpackArgs(args);
        Map paramMap = (Map) requestMap.get("paramMap");
        String objectId = (String)paramMap .get("objectId");
		DomainObject domObj=new DomainObject(objectId);
		String newAttrValue = (String) paramMap.get("New Value");
		System.out.println("???????????newAttrValue???????????"+newAttrValue);
		
		String oldClass=domObj.getInfo(context, "to[GWSchoolPeopleRelation].from.id");
		System.out.println("^^^^^^^^^^oldClass^^^^^^^^^^^^^^^^"+oldClass);
		/*if(newAttrValue.equals(oldClass)) {
			System.out.println("**********I am coming*************");
			return;
		}*/
			
			String relationShipId=domObj.getInfo(context, "to[GWSchoolPeopleRelation].id");
			System.out.println("!!!!!!!!!!!relationShipId!!!!!!!!!!!!!!"+relationShipId);
			
			DomainRelationship.disconnect(context, relationShipId);
			
			DomainObject classObject=new DomainObject(newAttrValue);
            DomainRelationship relationship1=domObj.connect(context, "GWSchoolPeopleRelation", classObject, true);
    	}catch (FrameworkException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}catch (Exception e1) {
			e1.printStackTrace();
			System.out.println(e1.getMessage());
		}
	
		
		
    }
    
    public void updateSex(Context context, String[] args) {
    	try {
    	Map inputMap = JPO.unpackArgs(args);
		Map paramMap = (Map) inputMap.get("paramMap");
		
		String objectId = (String) paramMap.get("objectId");
		String newAttrValue = (String) paramMap.get("New Value");
		
		DomainObject domObj=new DomainObject(objectId);
		domObj.setAttributeValue(context, "GWSex", newAttrValue);
    	}catch (FrameworkException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}catch (Exception e1) {
			e1.printStackTrace();
			System.out.println(e1.getMessage());
		}
    }
    
    @com.matrixone.apps.framework.ui.ProgramCallable
    public MapList expandMyStudent(Context context, String[] args)  
    {
    	MapList mapList            = new MapList();
    	try {
    		    HashMap programMap           = (HashMap)JPO.unpackArgs(args);
    	        String inputString = (String)programMap.get("PMCStudentInput");
    	        System.out.println("<<<<<<<<<<<<<<<inputString>>>>>>>>>>>>>>>>"+inputString);
    	        
    	        
    	        StringList selectStmts=new StringList();
    	        selectStmts.addElement(DomainConstants.SELECT_ID);
    	        selectStmts.addElement(DomainConstants.SELECT_NAME);
    	        selectStmts.addElement(DomainConstants.SELECT_TYPE);
    	        selectStmts.addElement("attribute[GWName]");
    	        selectStmts.addElement("attribute[GWAge]");
    	        selectStmts.addElement("attribute[GWSex]");
    	        selectStmts.addElement("to[GWSchoolPeopleRelation].from.name");
    	        
    	        mapList =DomainObject.findObjects(context, "GWStudent", null, "to[GWSchoolPeopleRelation].from.name=="+inputString,selectStmts);
    	        //mapList =DomainObject.findObjects(context, "GWStudent", null, "name ~~ *"+inputString+"*",selectStmts);
    	       //mapList =DomainObject.findObjects(context, "GWStudent", null, "name =="+inputString,selectStmts);
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		System.out.println(e.getMessage());
    	}
    	 return mapList;
    	
        
    }
    
    @com.matrixone.apps.framework.ui.ProgramCallable
    public MapList getClass(Context context, String args[])
        throws Exception {

        HashMap programMap           = (HashMap)JPO.unpackArgs(args);
        String objectId = (String)programMap .get("objectId");
		DomainObject domObj=new DomainObject(objectId);
        
        MapList mapList            = new MapList();
        try { 
        StringList selectStmts=new StringList();
        selectStmts.addElement(DomainConstants.SELECT_ID);
        selectStmts.addElement(DomainConstants.SELECT_NAME);
        selectStmts.addElement(DomainConstants.SELECT_TYPE);
        
        selectStmts.addElement("to[GWSchoolPeopleRelation].from.name");
        
        
       mapList =DomainObject.findObjects(context, "GWStudent", null,"id == "+objectId,selectStmts);
        
        }catch(FrameworkException ex) {
	     throw ex;
     }
       
       return mapList; 

         
    }
    

    /*public MapList getGWClass(Context context, String args[])
            throws Exception {
    	HashMap programMap           = (HashMap)JPO.unpackArgs(args);
        String objectId = (String)programMap .get("objectId");
		DomainObject domObj=new DomainObject(objectId);
        
        MapList mapList            = new MapList();
        try { 
        StringList selectStmts=new StringList();
        selectStmts.addElement(DomainConstants.SELECT_ID);
        selectStmts.addElement(DomainConstants.SELECT_NAME);
        selectStmts.addElement(DomainConstants.SELECT_TYPE);
        
        selectStmts.addElement("to[GWSchoolPeopleRelation].from.name");
        
        
       mapList =DomainObject.findObjects(context, "GWStudent", null,"id == "+objectId,selectStmts);
        
        }catch(FrameworkException ex) {
	     throw ex;
     }
       
       return mapList; 

    }*/
    
    public String getChinese(Context context, String args[])
            throws Exception {
    	 //System.out.println("************** i am coming*********************");
    	 HashMap programMap           = (HashMap)JPO.unpackArgs(args);
         Map studentMap = (Map) programMap.get("paramMap");
         String objectId = (String)studentMap .get("objectId");
 		
  
 		DomainObject domObj=new DomainObject(objectId);
 		
 		
         
         MapList mapList            = new MapList();
        
        	
        	
        	@SuppressWarnings("deprecation")
        	String chineseID=MqlUtil.mqlCommand(context, "print bus GWSubject Chinese 1 select id dump");
        	
        	String attributeString=null;
        	
        	@SuppressWarnings("deprecation")
        	String expandString =MqlUtil.mqlCommand(context,"expand bus "+objectId+" to rel GWSchoolScoreRelation"
        			+ " select rel attribute[GWScore] where from.id=="+chineseID+" dump &");
        	
        	if(expandString.split("&").length>6) {
        		attributeString =expandString.split("&")[6];
        	
        	}else {
        		attributeString="";
        	}
    
        
    	return attributeString;
    	
    }
}