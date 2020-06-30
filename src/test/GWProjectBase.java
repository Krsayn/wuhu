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
    public MapList getGWProjectAttributes(Context context, String args[])
        throws Exception {

        HashMap programMap           = (HashMap)JPO.unpackArgs(args);
       
        MapList mapList            = new MapList();
        
        try { 
        StringList selectStmts=new StringList();
        selectStmts.addElement(DomainConstants.SELECT_ID);
        selectStmts.addElement(DomainConstants.SELECT_NAME);
        selectStmts.addElement(DomainConstants.SELECT_TYPE);
       
        System.out.println();
        
        mapList =DomainObject.findObjects(context, "Project Space", null, null,selectStmts );
       
        }catch(FrameworkException ex) {
	   throw ex;
     }
       
       return mapList; 

         
    }
    
  
}