/*
 *  emxRouteBase.java
 *
 * Copyright (c) 1992-2018 Dassault Systemes.
 *
 * All Rights Reserved.
 * This program contains proprietary and trade secret information of
 * MatrixOne, Inc.  Copyright notice is precautionary only and does
 * not evidence any actual or intended publication of such program.
 *
 */
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import com.matrixone.apps.domain.DomainAccess;
import java.util.Set;
import java.util.LinkedHashSet;
import com.matrixone.apps.framework.ui.UIExpression;
import java.util.Vector;

import matrix.db.Access;
import matrix.db.AccessItr;
import matrix.db.AccessList;
import matrix.db.Attribute;
import matrix.db.AttributeList;
import matrix.db.AttributeType;
import matrix.db.BusinessObject;
import matrix.db.BusinessObjectList;
import matrix.db.Context;
import matrix.db.ExpansionWithSelect;
import matrix.db.JPO;
import matrix.db.Policy;
import matrix.db.Relationship;
import matrix.db.RelationshipType;
import matrix.db.RelationshipWithSelectItr;
import matrix.db.Role;
import matrix.db.RoleList;
import matrix.db.State;
import matrix.db.StateList;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.SelectList;
import matrix.util.StringItr;
import matrix.util.StringList;

import com.matrixone.apps.common.CommonDocument;
import com.matrixone.apps.common.Document;
import com.matrixone.apps.common.Person;
import com.matrixone.apps.common.Route;
import com.matrixone.apps.common.UserTask;
import com.matrixone.apps.common.Workspace;
import com.matrixone.apps.common.WorkspaceVault;
import com.matrixone.apps.common.util.ComponentsUIUtil;
import com.matrixone.apps.common.util.ComponentsUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.DomainSymbolicConstants;
import com.matrixone.apps.domain.util.AccessUtil;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkProperties;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MessageUtil;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.XSSUtil;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.domain.util.i18nNow;
import com.matrixone.apps.domain.util.mxType;
import com.matrixone.apps.framework.ui.UIComponent;
import com.matrixone.apps.framework.ui.UIMenu;
import com.matrixone.apps.framework.ui.UINavigatorUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.jsystem.util.StringUtils;

/**
 * @version AEF Rossini - Copyright (c) 2002, MatrixOne, Inc.
 */
public class ${CLASSNAME} extends ${CLASS:emxDomainObject}
{
      // Updating StringBuffers
    protected static final String sbRouteStatus = "attribute["+DomainObject.ATTRIBUTE_ROUTE_STATUS+          "]";
    protected static final String selParentRouteOwner = "to["+DomainConstants.RELATIONSHIP_TASK_SUBROUTE+"].from.from["+DomainConstants.RELATIONSHIP_ROUTE_TASK+"].to.owner";
    protected static final String attRouteVisibility = "attribute["+DomainObject.ATTRIBUTE_SUBROUTE_VISIBILITY+          "]";
    protected static final String attRestrictMembers = "attribute["+DomainObject.ATTRIBUTE_RESTRICT_MEMBERS+          "]";

     protected static String activeFilter = "";
     protected static String inActiveFilter = "";
     protected static final String strMatchList = " matchlist ";
    static final String AEF_WORKSPACE_ACCESS_GRANTOR_USERNAME = "Workspace Access Grantor";
    static final String AEF_WORKSPACE_LEAD_GRANTOR_USERNAME = "Workspace Lead Grantor";
    static final String AEF_WORKSPACE_MEMBER_GRANTOR_USERNAME = "Workspace Member Grantor";
    static final String AEF_ROUTE_DELEGATION_GRANTOR_USERNAME = "Route Delegation Grantor";
    static final String AEF_ROUTE_ACCESS_GRANTOR_USERNAME = "Route Access Grantor";
    static final String AEF_COMMON_ACCESS_GRANTOR_USERNAME = "Common Access Grantor";
    static final String RED="Red";
    static final String YELLOW="Yellow";
    static final String GREEN="Green";
    private static final String OBJECT_ID = "objectId";
    private static final String YES = "Yes";

    /**
     *
     * @param context the eMatrix <code>Context</code> object
     * @param args holds no arguments
     * @throws Exception if the operation fails
     * @since AEF Rossini
     */
    public ${CLASSNAME} (Context context, String[] args)
        throws Exception
    {
      super(context, args);

      synchronized(${CLASSNAME}.class) {
          try
              {
                  activeFilter = EnoviaResourceBundle.getProperty(context,"emxComponentsRoutes.Filter.Active");
                  inActiveFilter = EnoviaResourceBundle.getProperty(context,"emxComponentsRoutes.Filter.InActive");
              }
          catch(Exception e)
              {
                  throw e;
              }
      }
    }

    /**
     * This method is executed if a specific method is not specified.
     *
     * @param context the eMatrix <code>Context</code> object
     * @param args holds no arguments
     * @returns nothing
     * @throws Exception if the operation fails
     * @since AEF Rossini
     */
    public int mxMain(Context context, String[] args)
        throws Exception
    {
        if (!context.isConnected())
            throw new Exception(ComponentsUtil.i18nStringNow("emxComponents.Generic.NotSupportedOnDesktopClient", context.getLocale().getLanguage()));
        return 0;
    }

    /**
     * getMyRoutes - gets the list of Routes the user has access to, used as input for the Route Summary table
     * @param context the eMatrix <code>Context</code> object
     * @param args holds the following input arguments:
     *        0 - objectList MapList
     * @returns Object
     * @throws Exception if the operation fails
     * @since AEF Rossini
     */
    @com.matrixone.apps.framework.ui.ProgramCallable
    public Object getMyRoutes(Context context, String[] args)
        throws Exception
    {
        try
        {
            HashMap programMap        = (HashMap) JPO.unpackArgs(args);
            String parentId           = (String) programMap.get(OBJECT_ID);
            String selOwner           = DomainConstants.SELECT_OWNER;

            StringBuffer selRouteNode = new StringBuffer();
            selRouteNode.append("to[");
            selRouteNode.append(DomainConstants.RELATIONSHIP_ROUTE_NODE);
            selRouteNode.append("].from.id");


            com.matrixone.apps.common.Person loggedInPerson = com.matrixone.apps.common.Person.getPerson(context);
            String loggedInUser       = loggedInPerson.getName(context);

            // Where clause to display routes only if users has min Read access to the Route

//             commented on 10th March for displaying Sub Routes to Parent Route Owner
//            String sTypeWhere  = "(current.access[read] == true && current.access[checkout] == true && current.access[show] == true)";



            DomainObject connectedObject = new DomainObject(parentId);
            StringList typeSelects = new StringList();
            typeSelects.add(DomainConstants.SELECT_ID);
            typeSelects.add(selOwner);
            typeSelects.add(attRestrictMembers.toString());
            String objectType = connectedObject.getType(context);
            MapList routeList = new MapList();
            typeSelects.add(selParentRouteOwner.toString());
            typeSelects.add(attRouteVisibility.toString());

      String type = connectedObject.getType(context);
      String sTypeWhere  = "";
      Pattern relPattern         = null;
            Pattern typePattern        = new Pattern(DomainConstants.TYPE_ROUTE);

      if(!type.equals(DomainConstants.TYPE_INBOX_TASK))
      {
    	  sTypeWhere  = "(current.access[read] == true && current.access[show] == true)";
         relPattern  = new Pattern(DomainConstants.RELATIONSHIP_ROUTE_SCOPE);
               relPattern.addPattern(DomainConstants.RELATIONSHIP_OBJECT_ROUTE);
               relPattern.addPattern(DomainConstants.RELATIONSHIP_ROUTE_TASK);
            }
      else
      {
           relPattern  = new Pattern(DomainConstants.RELATIONSHIP_TASK_SUBROUTE);
      }

      routeList   = connectedObject.getRelatedObjects(context,
                                                                 relPattern.getPattern(),
                                                                 typePattern.getPattern(),
                                                                 typeSelects,
                                                                 null,
                                                                 false,
                                                                 true,
                                                                 (short)1,
                                                                 sTypeWhere,
                                                                 null,
                                                                 null,
                                                                 null,
                                                                 null);
                Iterator routeListItr = routeList.iterator();
                MapList tempRouteList = new MapList();
                StringList memberRouteList =   loggedInPerson.getInfoList(context , selRouteNode.toString());
                while(routeListItr.hasNext()) {
                    Map routeMap = (Map)routeListItr.next();
                    String routeOwner = (String)routeMap.get(selOwner);
                    String routeId    = (String)routeMap.get(DomainConstants.SELECT_ID);
                    String parentRouteOwner = (String)routeMap.get(selParentRouteOwner.toString());
    			if(parentRouteOwner != null && parentRouteOwner.equals(loggedInUser)) {
						String viewSubRoute = (String)routeMap.get(attRouteVisibility.toString());
    				if(viewSubRoute != null && viewSubRoute.equals(YES))
								tempRouteList.add(routeMap);
						} else {
							tempRouteList.add(routeMap);
						}
 //till here
                }
                routeList     = tempRouteList;
                tempRouteList = null;
            return routeList;
        }
        catch (Exception ex)
        {
            throw ex;
        }
  }

    /**
     * showCheckbox - determines if the checkbox needs to be enabled in the column of the Route Summary table
     *
     * @param context the eMatrix <code>Context</code> object
     * @param args holds the following input arguments:
     *        0 - objectList MapList
     * @returns Object of type Vector
     * @throws Exception if the operation fails
     * @since AEF Rossini
     */
    public Vector showCheckbox(Context context, String[] args)
        throws Exception
    {
        try
        {
            HashMap programMap = (HashMap) JPO.unpackArgs(args);
            MapList objectList = (MapList)programMap.get("objectList");

            Vector enableCheckbox = new Vector();
            String user = context.getUser();

            Iterator objectListItr = objectList.iterator();
            while(objectListItr.hasNext())
            {
                Map objectMap = (Map) objectListItr.next();
                String routeId = (String) objectMap.get(DomainConstants.SELECT_ID);

                DomainObject routeObject = new DomainObject(routeId);
                String owner = routeObject.getInfo(context, DomainConstants.SELECT_OWNER);
                AccessUtil accessUtil     = new AccessUtil();  // added for bug 278128
                Access access =  routeObject.getAccessMask(context);  // added for bug 278128
                if(user.equals(owner) && AccessUtil.hasRemoveAccess(access))   // modified for bug 278128
                {
                    enableCheckbox.add("true");
                }
                else
                {
                    enableCheckbox.add("false");
                }
            }
            return enableCheckbox;
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }

    /**
     * showStatusGif - gets the status gif to be shown in the column of the Route Summary table
     *
     * @param context the eMatrix <code>Context</code> object
     * @param args holds the following input arguments:
     *        0 - objectList MapList
     * @returns Object of type Vector
     * @throws Exception if the operation fails
     * @since AEF Rossini
     */
    public Vector showStatusGif(Context context, String[] args)
        throws Exception
    {
        try {
            HashMap programMap = (HashMap) JPO.unpackArgs(args);
            MapList objectList = (MapList)programMap.get("objectList");
            String sStatus = "";
            String statusColor = "";
            Vector vecResult = new Vector();

            if ( objectList == null || objectList.size() == 0) {
                return vecResult;
            }

            final String SELECT_ATTRIBUTE_ACTUAL_COMPLETION_DATE = "attribute[" + DomainConstants.ATTRIBUTE_ACTUAL_COMPLETION_DATE + "]";
            final String SELECT_ATTRIBUTE_SCHEDULED_COMPLETION_DATE = "attribute[" + DomainConstants.ATTRIBUTE_SCHEDULED_COMPLETION_DATE + "]";
            Map mapObjectInfo = null;
            DomainObject dmoRoute = null;
            String strRouteId = null;
            StringList slBusSelect = new StringList();
            StringList slRelSelect = new StringList();
            slRelSelect.add(SELECT_ATTRIBUTE_ACTUAL_COMPLETION_DATE);
            slRelSelect.add(SELECT_ATTRIBUTE_SCHEDULED_COMPLETION_DATE);
            String strObjWhr = null;
            String strRelWhr = null;
            short nRecurseLevel = (short)1;
            MapList mlRouteNodes = null;
            MapList mlTemp = null;
            Map mapRouteNodeInfo = null;
            String strTaskDueDate = null;
            String strTaskCompletionDate = null;
            String strRotueDueDate = null;
            Date dtDueDate = null;
            Date dtCompletionDate = null;
            Date dtCurrentDate = new Date();
            MapList mlLastTasks = null;
            MapList mlNonLastTasks = null;

            // Do for each route
            for (Iterator itrObjectList = objectList.iterator(); itrObjectList.hasNext();) {
                mapObjectInfo = (Map)itrObjectList.next();
                strRouteId = (String)mapObjectInfo.get(DomainConstants.SELECT_ID);
                statusColor = null;

                dmoRoute = DomainObject.newInstance(context, strRouteId);
                sStatus =  dmoRoute.getInfo(context,Route.SELECT_ROUTE_STATUS);

                if (sStatus != null &&  !"".equals(sStatus)) {
                    sStatus= i18nNow.getRangeI18NString(DomainConstants.ATTRIBUTE_ROUTE_STATUS, sStatus, context.getSession().getLanguage());
                }
                else {
                    sStatus= "*";
                }
                mlRouteNodes = dmoRoute.getRelatedObjects(context,
                        DomainConstants.RELATIONSHIP_ROUTE_NODE,
                        DomainConstants.TYPE_PERSON + "," + DomainConstants.TYPE_ROUTE_TASK_USER,
                        slBusSelect,
                        slRelSelect,
                        false,
                        true,
                        nRecurseLevel,
                        strObjWhr,
                        strRelWhr);

                if (mlRouteNodes == null || mlRouteNodes.size() == 0) {
                    statusColor = "";
                }
                else {
                    // Remove the entries with empty due dates
                    mlTemp = new MapList();
                    for (Iterator itrRouteNodes = mlRouteNodes.iterator(); itrRouteNodes.hasNext();) {
                        mapRouteNodeInfo = (Map) itrRouteNodes.next();
                        strTaskDueDate = (String)mapRouteNodeInfo.get(SELECT_ATTRIBUTE_SCHEDULED_COMPLETION_DATE);
                        if (strTaskDueDate != null && !"".equals(strTaskDueDate)) {
                            mlTemp.add(mapRouteNodeInfo);
                        }
                    }
                    mlRouteNodes = mlTemp;
                    mlTemp = null;

                    if (mlRouteNodes.size() == 0) {
                        statusColor = GREEN;
                    }
                    else {
                        // Sort maplist descending w.r.t. due dates
                        mlRouteNodes.sort(SELECT_ATTRIBUTE_SCHEDULED_COMPLETION_DATE, "descending", "date");

                        // Get route's due date, first due date in sorted list
                        mapRouteNodeInfo = (Map)mlRouteNodes.get(0);
                        strRotueDueDate = (String)mapRouteNodeInfo.get(SELECT_ATTRIBUTE_SCHEDULED_COMPLETION_DATE);

                        // Separate the last tasks of the route from othe tasks
                        mlLastTasks = new MapList();
                        mlNonLastTasks = new MapList();
                        for (Iterator itrRouteNodes = mlRouteNodes.iterator(); itrRouteNodes.hasNext();) {
                            mapRouteNodeInfo = (Map) itrRouteNodes.next();
                            strTaskDueDate = (String)mapRouteNodeInfo.get(SELECT_ATTRIBUTE_SCHEDULED_COMPLETION_DATE);
                            if (strRotueDueDate != null && strRotueDueDate.equals(strTaskDueDate)) {
                                mlLastTasks.add(mapRouteNodeInfo);
                            }
                            else {
                                mlNonLastTasks.add(mapRouteNodeInfo);
                            }
                        }

                        // Process last tasks to decide if RED status is to be shown
                        dtDueDate = eMatrixDateFormat.getJavaDate(strRotueDueDate);
                        for (Iterator itrLastTasks = mlLastTasks.iterator(); itrLastTasks.hasNext();) {
                            mapRouteNodeInfo = (Map)itrLastTasks.next();
                            strTaskCompletionDate = (String)mapRouteNodeInfo.get(SELECT_ATTRIBUTE_ACTUAL_COMPLETION_DATE);
                            // If this task is not completed
                            if (strTaskCompletionDate == null || "".equals(strTaskCompletionDate)) {
                                dtCompletionDate = dtCurrentDate;
                            }
                            else {
                                dtCompletionDate = eMatrixDateFormat.getJavaDate(strTaskCompletionDate);
                            }


                            if (dtCompletionDate != null && dtCompletionDate.after(dtDueDate)) {
                                statusColor = RED;
                                break;
                            }
                        }

                        // Process non-last tasks to decide if YELLOW status is to be shown
                        if (statusColor == null) {
                            for (Iterator itrNonLastTasks = mlNonLastTasks.iterator(); itrNonLastTasks.hasNext();) {
                                mapRouteNodeInfo = (Map)itrNonLastTasks.next();
                                strTaskDueDate = (String)mapRouteNodeInfo.get(SELECT_ATTRIBUTE_SCHEDULED_COMPLETION_DATE);
                                strTaskCompletionDate = (String)mapRouteNodeInfo.get(SELECT_ATTRIBUTE_ACTUAL_COMPLETION_DATE);

                                dtDueDate = eMatrixDateFormat.getJavaDate(strTaskDueDate);
                                // If this task is not completed
                                if (strTaskCompletionDate == null || "".equals(strTaskCompletionDate)) {
                                    dtCompletionDate = dtCurrentDate;
                                }
                                else {
                                    dtCompletionDate = eMatrixDateFormat.getJavaDate(strTaskCompletionDate);
                                }


                                if (dtCompletionDate != null && dtCompletionDate.after(dtDueDate)) {
                                    statusColor = YELLOW;
                                    break;
                                }
                            }
                        }

                        // If no yellow and red then it is green
                        if (statusColor == null) {
                            statusColor = GREEN;
                        }
                    }
                }

                String statusImageString = "";
				String titleString="";
                if(RED.equals(statusColor)) {
//XSSOK
					titleString=EnoviaResourceBundle.getProperty(context,"emxFrameworkStringResource",context.getLocale(), "emxFramework.Route.Red");
                    statusImageString = "<img border=\"0\" src=\"../common/images/iconStatusRed.gif\" name=\"red\" id=\"red\" alt=\""+sStatus+"\" title=\""+titleString+"\"></img>";
                }
                else if(YELLOW.equals(statusColor)) {
//XSSOK
					titleString=EnoviaResourceBundle.getProperty(context,"emxFrameworkStringResource",context.getLocale(), "emxFramework.Route.Yellow");
                    statusImageString = "<img border=\"0\" src=\"../common/images/iconStatusYellow.gif\" name=\"yellow\" id=\"yellow\" alt=\""+sStatus+"\" title=\""+titleString+"\"></img>";
                }
                else if(GREEN.equals(statusColor)) {
//XSSOK
					titleString=EnoviaResourceBundle.getProperty(context,"emxFrameworkStringResource",context.getLocale(), "emxFramework.Route.Green");
                    statusImageString = "<img border=\"0\" src=\"../common/images/iconStatusGreen.gif\" name=\"green\" id=\"green\" alt=\""+sStatus+"\" title=\""+titleString+"\"></img>";
                }

                vecResult.add(statusImageString);
            }// for each route
            //XSSOK
            return vecResult;
        }
        catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * getScheduledCompletionDate - get the route scheduled completion date that needs to be displayed in the column of the Route Summary table
     *
     * @param context the eMatrix <code>Context</code> object
     * @param args holds the following input arguments:
     *        0 - objectList MapList
     * @returns Object of type Vector
     * @throws Exception if the operation fails
     * @since AEF Rossini
     */
    public Vector getScheduledCompletionDate(Context context, String[] args)
        throws Exception
    {
        try
        {
            HashMap programMap = (HashMap) JPO.unpackArgs(args);
            MapList objectList = (MapList)programMap.get("objectList");

            Vector enableCheckbox = new Vector();
            StringBuffer selectRouteNodeScheduledDate = new StringBuffer();
    		selectRouteNodeScheduledDate.append("from[");
    		selectRouteNodeScheduledDate.append(DomainConstants.RELATIONSHIP_ROUTE_NODE);
    		selectRouteNodeScheduledDate.append("].attribute[");
    		selectRouteNodeScheduledDate.append(DomainConstants.ATTRIBUTE_SCHEDULED_COMPLETION_DATE);
    		selectRouteNodeScheduledDate.append("]");

    		StringBuffer selectRouteTaskScheduledDate = new StringBuffer();
    		selectRouteTaskScheduledDate.append("to[");
    		selectRouteTaskScheduledDate.append(DomainConstants.RELATIONSHIP_ROUTE_TASK);
    		selectRouteTaskScheduledDate.append("].from.attribute[");
    		selectRouteTaskScheduledDate.append(DomainConstants.ATTRIBUTE_SCHEDULED_COMPLETION_DATE);
    		selectRouteTaskScheduledDate.append("]");

    		StringList objectSelects = new StringList();
    		objectSelects.addElement(selectRouteNodeScheduledDate.toString());
    		objectSelects.addElement(selectRouteTaskScheduledDate.toString());

            Iterator objectListItr = objectList.iterator();
            while(objectListItr.hasNext())
            {
                Map objectMap = (Map) objectListItr.next();
                String routeId = (String) objectMap.get(DomainConstants.SELECT_ID);

                DomainObject routeObject = new DomainObject(routeId);
                
                Map map = routeObject.getInfo(context, objectSelects);

    			StringList dateList  = new StringList();
    			String taskDueDate = (String) map.get(selectRouteTaskScheduledDate.toString());
    			taskDueDate = UIUtil.isNullOrEmpty(taskDueDate)? (String) map.get(selectRouteNodeScheduledDate.toString()): taskDueDate;
				taskDueDate = UIUtil.isNullOrEmpty(taskDueDate) ? "": taskDueDate;

    			dateList.add(taskDueDate);

                MapList dateMapList = new MapList();

                Iterator dateListItr = dateList.iterator();
                while(dateListItr.hasNext())
                {
                    String schDate = (String) dateListItr.next();
                    HashMap dateMap = new HashMap();
                    dateMap.put("date", schDate);
                    dateMapList.add(dateMap);
                }
                dateMapList.sort("date", "descending", "date");

                String displayDate = "";
                Iterator dateMapListItr = dateMapList.iterator();
                while(dateMapListItr.hasNext())
                {
                    Map tempMap = (Map) dateMapListItr.next();
                    displayDate = (String) tempMap.get("date");
                    break;
                }
                enableCheckbox.add(displayDate);
            }
            return enableCheckbox;
        }
        catch (Exception ex)
        {

            throw ex;
        }
    }

    /**
     * getActualCompletionDate - get the route actual completion date that needs to be displayed in the column of the Route Summary table
     *
     * @param context the eMatrix <code>Context</code> object
     * @param args holds the following input arguments:
     *        0 - objectList MapList
     * @returns Object of type Vector
     * @throws Exception if the operation fails
     * @since AEF Rossini
     */
    public Vector getActualCompletionDate(Context context, String[] args)
        throws Exception
    {
        try
        {
            HashMap programMap = (HashMap) JPO.unpackArgs(args);
            MapList objectList = (MapList)programMap.get("objectList");
            Vector enableCheckbox = new Vector();

            MapList busObjwsl = null;

            StringList strList = new StringList(2);
            strList.addElement(DomainConstants.SELECT_CURRENT);
            strList.addElement(Route.SELECT_ROUTE_COMPLETED_DATE);

            if ( objectList != null)
            {
               String compDate = "";
               String routeState = "";

                String objIdArray[] = new String[objectList.size()];
               //Get the array of Object Ids to be paased into the methods
                for (int i = 0; i < objectList.size(); i++)
                {
                    Map objMap = (Map)objectList.get(i);
                    objIdArray[i]  = (String)objMap.get(DomainConstants.SELECT_ID);
                }
                busObjwsl=DomainObject.getInfo(context,objIdArray,strList);
                for (int i = 0; i < objectList.size(); i++)
                {

                    Map objectMap = (Map) objectList.get(i);

                    routeState = (String)((Map)(busObjwsl.get(i))).get(DomainConstants.SELECT_CURRENT);
                    if(routeState.equals(Route.STATE_ROUTE_COMPLETE))
                    {
                        compDate=(String)((Map)(busObjwsl.get(i))).get(Route.SELECT_ROUTE_COMPLETED_DATE);
                    }

                    enableCheckbox.add(compDate);
                }
            }
            return enableCheckbox;
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }
     /**
   * Gets the list of all Routes.
   *
   * @param context the eMatrix <code>Context</code> object
   * @param args holds the following input arguments:
   *        0 - objectList MapList
   * @returns Object
   * @throws Exception if the operation fails
   * @since Common 10.0.1.1
   */
  @com.matrixone.apps.framework.ui.ProgramCallable
  public Object getAllMyDeskRoutes(Context context, String[] args)
      throws Exception, MatrixException
  {
      try
      {
          return getMyDeskRoutes(context,args,"");
      }
      catch (Exception ex)
      {
          throw ex;
      }
  }

  /**
   * Gets the list of Active Routes
   *
   * @param context the eMatrix <code>Context</code> object
   * @param args holds the following input arguments:
   *        0 - objectList MapList
   * @returns Object
   * @throws Exception if the operation fails
   * @since AEF 10.0.1.1
   */
  @com.matrixone.apps.framework.ui.ProgramCallable
  public Object getMyDeskActiveRoutes(Context context, String[] args)
      throws Exception, MatrixException
  {
      try
      {
          StringBuffer sbWhere = new StringBuffer(64);
          sbWhere.append(sbRouteStatus.toString());
          sbWhere.append(strMatchList);

          if ((activeFilter != null) &&
              (!"".equals(activeFilter.trim())))
          {
              StringTokenizer tokenizer=new StringTokenizer(activeFilter,",");
              boolean first = true;
              while(tokenizer.hasMoreTokens())
              {
                  String nextFilter = tokenizer.nextToken();
                  if (first)
                  {
                    sbWhere.append("'");
                    first = false;
                  }
                  else
                  {
                    sbWhere.append(",");
                  }

                  sbWhere.append(nextFilter);
              }
              sbWhere.append("'','");
          }

          return getMyDeskRoutes(context,args,sbWhere.toString());
      }
      catch (Exception ex)
      {
          throw ex;
      }
  }

  /**
   * Gets the list of Inactive Routes
   *
   * @param context the eMatrix <code>Context</code> object
   * @param args holds the following input arguments:
   *        0 - objectList MapList
   * @returns Object
   * @throws Exception if the operation fails
   * @since AEF 10.0.1.1
   */
  @com.matrixone.apps.framework.ui.ProgramCallable
  public Object getMyDeskInActiveRoutes(Context context, String[] args)
      throws Exception, MatrixException
  {
      try
      {
          StringBuffer sbWhere = new StringBuffer(64);
          sbWhere.append(sbRouteStatus.toString());
          sbWhere.append(strMatchList);

          if ((inActiveFilter != null) &&
              (!"".equals(inActiveFilter.trim())))
          {
              StringTokenizer tokenizer=new StringTokenizer(inActiveFilter,",");
              boolean first = true;
              while(tokenizer.hasMoreTokens())
              {
                  String nextFilter = tokenizer.nextToken();
                  if (first)
                  {
                    sbWhere.append("'");
                    first = false;
                  }
                  else
                  {
                    sbWhere.append(",");
                  }

                  sbWhere.append(nextFilter);
              }
              sbWhere.append("'','");
          }

          return getMyDeskRoutes(context,args,sbWhere.toString());
      }
      catch (Exception ex)
      {
          throw ex;
      }
  }

    /**
     * getMyDeskRoutes - gets the list of Routes to be shown under MyDesk
     *
     * @param context the eMatrix <code>Context</code> object
     * @param args holds the following input arguments:
     *        0 - objectList MapList
     * @returns Object
     * @throws Exception if the operation fails
     * @since AEF Rossini
     */
    public Object getMyDeskRoutes(Context context, String[] args, String WhereExpression)
        throws Exception
    {
        try
        {
            String loggedInUser = com.matrixone.apps.common.Person.getPerson(context).getName(context);

            MapList totalResultList = new MapList();

            StringList typeSelects = new StringList(4);
            typeSelects.add(DomainConstants.SELECT_ID);
            typeSelects.add(selParentRouteOwner.toString());
            typeSelects.add(attRouteVisibility.toString());
            typeSelects.add(attRestrictMembers.toString());

            boolean isRouteVisibility      = true;
            String isRouteVisibilityEnabled = EnoviaResourceBundle.getProperty(context,"emxFramework.Routes.RouteVisibility");
            if ((isRouteVisibilityEnabled == null) ||
                ("false".equals(isRouteVisibilityEnabled)))
              isRouteVisibility = false;

            Pattern relPattern = new Pattern(DomainConstants.RELATIONSHIP_PROJECT_ROUTE);
            if (isRouteVisibility)
              relPattern.addPattern(DomainConstants.RELATIONSHIP_ROUTE_NODE);

            com.matrixone.apps.common.Person person = com.matrixone.apps.common.Person.getPerson(context);
            MapList routeList = person.getRelatedObjects(context,
                                       relPattern.getPattern(),
                                                    DomainConstants.TYPE_ROUTE,
                                                    typeSelects,
                                                    null,
                                                    true,
                                                    false,
                                                    (short)1,
                                                    WhereExpression,
                                                    "",
                                                    null,
                                                    null,
                                                    null);

            StringList sRouteList = new StringList();
            Iterator mapItr = routeList.iterator();
            String sRouteId = null;
            while(mapItr.hasNext())
            {
                Map route = (Map)mapItr.next();
                sRouteId = (String)route.get(DomainConstants.SELECT_ID);
                if (!sRouteList.contains(sRouteId))
                {
                  sRouteList.add(sRouteId);
                  String viewSubRoute = (String)route.get(attRouteVisibility.toString());
                  if ((viewSubRoute != null) &&
                      (viewSubRoute.equals("No")))
                  {
                     String parentRouteOwner = (String)route.get(selParentRouteOwner.toString());
                     if ((parentRouteOwner == null) ||
                         ("".equals(parentRouteOwner)))
                     {
                       totalResultList.add(route);
                     }
                     else if ((parentRouteOwner != null) &&
                              (!(loggedInUser.equals(parentRouteOwner))))
                     {
                       totalResultList.add(route);
                     }
                  }
                  else
                  {
                    totalResultList.add(route);
                  }
                }
            }
            return totalResultList;
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }

    /**
     * showOwner - displays the owner with lastname,firstname format
     *
     * @param context the eMatrix <code>Context</code> object
     * @param args holds the following input arguments:
     *        0 - objectList MapList
     * @returns Object
     * @throws Exception if the operation fails
     * @since AEF Rossini
     */
    public Vector showOwner(Context context, String[] args)
        throws Exception
    {
        try
        {
            HashMap programMap = (HashMap) JPO.unpackArgs(args);
            MapList objectList = (MapList)programMap.get("objectList");
            Vector vecOwner = new Vector();
            MapList busObjwsl = null;

            StringList strList = new StringList(1);
            strList.addElement(DomainConstants.SELECT_OWNER);
            if ( objectList != null)
            {
                String objIdArray[] = new String[objectList.size()];
               //Get the array of Object Ids to be paased into the methods
                for (int i = 0; i < objectList.size(); i++)
                {
                    Map objMap = (Map)objectList.get(i);
                    objIdArray[i]  = (String)objMap.get(DomainConstants.SELECT_ID);
                }
                busObjwsl=DomainObject.getInfo(context,objIdArray,strList);
                for (int i = 0; i < objectList.size(); i++)
                {
                    String strOwner = com.matrixone.apps.common.Person.getDisplayName(context,(String)((Map)(busObjwsl.get(i))).get(DomainConstants.SELECT_OWNER));
                    vecOwner.add(strOwner);
                }
            }
            return vecOwner;
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }


  /**
  * Grants the Access to Route to Route Member when Connected thru Route Node rel.
  *
  * @param context the eMatrix Context object
  * @param holds the grantee name
  * @return void
  * @throws Exception if the operation fails
  * @since AEF Rossini
  */
  public void grantAccess(matrix.db.Context context, String[] args) throws Exception
  {
      // Don't grant access for Route Templeates.
      /*if(getType(context).equals(TYPE_ROUTE_TEMPLATE))
      {
          return;
      }*/

      String sToType = args[1];
      // Don't grant access if the "To" side type is "Route Task User"
      // "Route Task User" exists on the "To" side if a route is
      // assigned to a Group or Role
      if(sToType.equals(TYPE_ROUTE_TASK_USER))
      {
          return;
      }

      String sGrantee = args[0];
      Access access = null;
      //AccessList accessList = null;
      if(getType(context).equals(TYPE_ROUTE_TEMPLATE) &&  getAccessForGrantee(context,sGrantee) != null && (getAccessForGrantee(context,sGrantee)).size() > 0)
          return;
      // Construct grantee list
      StringList sGranteeList = new StringList();
      sGranteeList.add(sGrantee);

      BusinessObjectList busRouteList = new BusinessObjectList();
      busRouteList.addElement(this);

      //<Fix 372839>
      pushContextForGrantor(context, AEF_ROUTE_ACCESS_GRANTOR_USERNAME);
      //</Fix 372839>
      try
      {
          // Grant the access for the business Object List to grantee (Project Lead)
          AccessUtil accessUtil = new AccessUtil();
          //accessUtil.setReadWrite(sGrantee);
          accessUtil.setRead(sGrantee);
          access = (Access)((accessUtil.getAccessList()).elementAt(0));
          matrix.db.BusinessObject.grantAccessRights(context, busRouteList, access);
      }
      catch(Exception exp)
      {
          ContextUtil.popContext(context);
          throw exp;
      }
      ContextUtil.popContext(context);
  }


  /**
  * Revoke Access from Route Member when Route Node disconnected
  *
  * @param context the eMatrix Context object
  * @param holds the grantee name
  * @return void
  * @throws Exception if the operation fails
  * @since AEF Rossini
  */
  public void revokeAccess(matrix.db.Context context, String[] args) throws Exception
  {
     /* if(getType(context).equals(TYPE_ROUTE_TEMPLATE))
      {
          return;
      }*/

      ContextUtil.restoreContext(context);
      String sToType = args[1];
      // Don't revoke access if the "To" side type is "Route Task User"
      // "Route Task User" exists on the "To" side if a route is
      // assigned to a Group or Role
      if(sToType.equals(TYPE_ROUTE_TASK_USER))
      {
          return;
      }

      // Person Disconnected from Route Node
      String sGrantee = args[0];

      StringBuffer selPerson = new StringBuffer();
      selPerson.append("from[");
      selPerson.append(RELATIONSHIP_ROUTE_NODE );
      selPerson.append("].to.name");

      // check if
      // Do not revoke access for User on Route if User has more than 1 tasks.
      StringList sPersonList = getInfoList(context, selPerson.toString());
      if(sPersonList.indexOf(sGrantee) != sPersonList.lastIndexOf(sGrantee))
      {
          return;
      }

      // Revoke access from Grantee granted by Access Grantor.
      //<Fix 372839>
      Route route = new Route(getId());
      route.revokeAccessOnContent(context, new String[]{args[0]});
      //</Fix 372839>
  }
    /**
  * Inherit Access only for Workspace Leads from the Route Scope Object
  *
  * @param context the eMatrix Context object
  * @param holds Route Objects
  * @return void
  * @throws Exception if the operation fails
  * @since AEF Rossini
  */
  public void inheritAccesstoContent(matrix.db.Context context, String[] args) throws Exception
  {
		if(true) {
		  return;//this method will grant access to the content, already taken care through trigger programs		  
	  }
      Access access = null;
      AccessUtil accessUtil = null;
      // Route object'b BO list
      BusinessObjectList listRoute = new BusinessObjectList();
      for (int i =0 ;i< args.length ;i++ )
      {
         listRoute.addElement(new BusinessObject(args[i]));
      }
      //<Fix 372839>
      AccessList accessGrantorList = getAccessForGrantor(context, AEF_ROUTE_ACCESS_GRANTOR_USERNAME);
      //</Fix 372839>
        if(accessGrantorList.size() > 0)
        {
          //<Fix 372839>
          pushContextForGrantor(context, AEF_ROUTE_ACCESS_GRANTOR_USERNAME);
          //<Fix 372839>
          try
          {
              grantAccessRights(context, listRoute, accessGrantorList);
          }
          catch(Exception exp)
          {
              ContextUtil.popContext(context);
              throw exp;
          }
          ContextUtil.popContext(context);
        }
  }
  /**
  * Inherit Access only for Workspace Leads from the Route Scope Object
  *
  * @param context the eMatrix Context object
  * @param holds Route Objects
  * @return void
  * @throws Exception if the operation fails
  * @since AEF Rossini
  */
  public void inheritAccess(matrix.db.Context context, String[] args) throws Exception
  {
      Access access = null;
      AccessUtil accessUtil = null;

      // Route object'b BO list
      BusinessObjectList listRoute = new BusinessObjectList();
      listRoute.addElement(new BusinessObject(args[0]));

      // Get the Access List of the Route Scope Object granted by Lead Grantor.
      AccessItr itrAccess = new AccessItr(getAccessForGrantor(context, AEF_WORKSPACE_LEAD_GRANTOR_USERNAME));

      AccessList listGrantAccess = new AccessList();
      while (itrAccess.next())
      {
          // Set READ access for Workspace Leads to the Route.
          accessUtil = new AccessUtil();
          accessUtil.setRead(itrAccess.obj().getUser());
          access = (Access)((accessUtil.getAccessList()).elementAt(0));
          listGrantAccess.addElement(access);
      }

      if (listGrantAccess.size() > 0 )
      {
          pushContextForGrantor(context, AEF_WORKSPACE_LEAD_GRANTOR_USERNAME);
          try
          {
              grantAccessRights(context, listRoute, listGrantAccess);
          }
          catch(Exception exp)
          {
              ContextUtil.popContext(context);
              throw exp;
          }
          ContextUtil.popContext(context);
      }
  }

  /**
  * Update Route Node ID attribute on Route Node relationship
  * when Connected thru Route Node rel.
  *
  * @param context the eMatrix Context object
  * @param holds the grantee name
  * @return void
  * @throws Exception if the operation fails
  * @since AEF Rossini
  */
  public void populateRouteNodeId(matrix.db.Context context, String[] args) throws Exception
  {
      // Get Route Node ID
      String sRouteNodeId = args[0];
      sRouteNodeId = MqlUtil.mqlCommand(context, "print connection $1 select $2 dump", sRouteNodeId, "physicalid");
      try
      {
          // update attribute Route Node ID on passed in relationship to it's id
          AttributeList attList = new AttributeList();
          Attribute attr = new Attribute( new AttributeType (ATTRIBUTE_ROUTE_NODE_ID), sRouteNodeId);
          attList.addElement(attr);
          Relationship rel = new Relationship(sRouteNodeId);
          rel.open(context);
          rel.setAttributes(context, attList);
          rel.update(context);
          rel.close(context);
      } catch(Exception exp)
      {
          throw exp;
      }
  }

  public void deleteRouteTaskUserObject(matrix.db.Context context, String[] args) throws Exception
  {
      //Continue only if the TO side of the route node relation is a "Route Task User" object
      if(getType(context).equals(TYPE_PERSON))
      {
          return;
      }

      //if the Route Task User object does not have Route Node relationship then delete it
      //which means that this was the last group or role assignment.
      //Either the group or role assignment is removed by the route owner OR
      //the user who belongs to the assigned group or role has accepted the task
      if(hasObjects(context, RELATIONSHIP_ROUTE_NODE, false))
      {
          //other group or role assignments exists. Do nothing
          return;
      }
      else
      {
          try
          {
              //No more group or role assignments. Delete the Route Task User object
              deleteObject(context);
          }
          catch(Exception exp)
          {
              throw exp;
          }
      }
  }

  /**
  * Push the context for the respective grantor.
  *
  * @param context the eMatrix Context object
  * @param grantor name to push context to.
  * @return void
  * @throws Exception if the operation fails
  * @since VCRossini
  */
  protected void pushContextForGrantor(matrix.db.Context context, String sGrantor) throws Exception
  {
      // Check for grantor.
      if(sGrantor.equals(AEF_WORKSPACE_ACCESS_GRANTOR_USERNAME))
      {
          pushContextAccessGrantor(context);
      }
      else if (sGrantor.equals(AEF_WORKSPACE_MEMBER_GRANTOR_USERNAME))
      {
          pushContextMemberGrantor(context);
      }
      else if (sGrantor.equals(AEF_WORKSPACE_LEAD_GRANTOR_USERNAME))
      {
          pushContextLeadGrantor(context);
      }
      else if (sGrantor.equals(AEF_ROUTE_DELEGATION_GRANTOR_USERNAME))
      {
          pushContextDelegationGrantor(context);
      }
      //<Fix 372839>
      else if(sGrantor.equals(AEF_ROUTE_ACCESS_GRANTOR_USERNAME))
      {
          pushContextRouteAccessGrantor(context);
      }
      //</Fix 372839>
  }

  /**
  * Change context to Workspace Access Grantor
  *
  * @param context the eMatrix Context object
  * @return void
  * @throws Exception if the operation fails
  * @since VCRossini
  */
  protected void pushContextAccessGrantor(Context context) throws Exception
  {
      ContextUtil.pushContext(context, AEF_WORKSPACE_ACCESS_GRANTOR_USERNAME, null, null);
  }

  /**
  * Change context to Route Delegation Grantor
  *
  * @param context the eMatrix Context object
  * @return void
  * @throws Exception if the operation fails
  * @since VCRossini
  */
  protected void pushContextDelegationGrantor(Context context) throws Exception
  {
      ContextUtil.pushContext(context, AEF_ROUTE_DELEGATION_GRANTOR_USERNAME, null, null);
  }

  /**
  * Change context to Workspace Member Grantor
  *
  * @param context the eMatrix Context object
  * @return void
  * @throws Exception if the operation fails
  * @since VCRossini
  */
  protected void pushContextMemberGrantor(Context context) throws Exception
  {
      // Puch context to super user to turn off triggers
      ContextUtil.pushContext(context);
      try
      {
        // Turn off all triggers
        MqlUtil.mqlCommand(context, "trigger off;", true);
      }
      catch (Exception exp)
      {
        ContextUtil.popContext(context);
        throw exp;
      }

      ContextUtil.pushContext(context, AEF_WORKSPACE_MEMBER_GRANTOR_USERNAME, null, null );
  }

  /**
  * Change context to Workspace Lead Grantor
  *
  * @param context the eMatrix Context object
  * @return void
  * @throws Exception if the operation fails
  * @since VCRossini
  */
  protected void pushContextLeadGrantor(Context context) throws Exception
  {
      ContextUtil.pushContext(context, AEF_WORKSPACE_LEAD_GRANTOR_USERNAME, null, null);
  }
  //<Fix 372839>
  protected void pushContextRouteAccessGrantor(Context context) throws Exception
  {
      ContextUtil.pushContext(context, AEF_ROUTE_ACCESS_GRANTOR_USERNAME, null, null);
  }
  //</Fix 372839>

  /**
  * Revoke access on Object b/w particular Grantor and Grantee
  *
  * @param context the eMatrix Context object
  * @param grantor name to push context to.
  * @return void
  * @throws Exception if the operation fails
  * @since VCRossini
  */
  protected void revokeAccessGrantorGrantee(matrix.db.Context context, String sGrantor, String sGrantee) throws Exception
  {
      StringBuffer sCommand = new StringBuffer();
      sCommand.append("mod bus ");
      sCommand.append(getId());
      sCommand.append(" revoke grantor '");
      sCommand.append(sGrantor);
      sCommand.append("' grantee '");
      sCommand.append(sGrantee);
      sCommand.append("'");
      MqlUtil.mqlCommand(context,sCommand.toString());
  }

  /**
  * displayLinkAccessCheck - determines if the Create New, Create Route Wizard ,Set Task Escalation, Remove Selected, Start/ResumeRoute links needs to be show in the Route Summary table
  *
  *
  * @param context the eMatrix <code>Context</code> object
  * @param args holds the objectId
  * @returns boolean type
  * @throws Exception if the operation fails
  * @Modified:  AEF V6R2014 
  * After Team Central Security Context highlight, now we are checking if the member is having 'Add Remove' access then he can perform these operations
  */

  public static boolean displayLinkAccessCheck(Context context, String args[]) throws Exception
  {
      HashMap programMap         = (HashMap) JPO.unpackArgs(args);
      String objectId            = (String) programMap.get(OBJECT_ID);

      boolean result             = true;

      if (objectId == null || "".equals(objectId)) {
         // happens when this JPO invoked for MyRouteSummary
         return true;
      }

      DomainObject objectGeneral = DomainObject.newInstance(context);

      try {
    	  // set the domain object with the passed id , id can be of Workspcae/WorkspcaVault/Document.
          objectGeneral.setId(objectId);

          String objState = objectGeneral.getInfo(context,DomainConstants.SELECT_CURRENT);
          Access access = objectGeneral.getAccessMask(context);

          if(AccessUtil.hasAddRemoveAccess(access) && objState.equals("Active")) {
              result=true;
          } else {
              result=false;
          }
      } catch(Exception e )  {
        throw e;
      }
      
      return result;
  }

  /**
  * displaySetTaskLinkAccessCheck - determines if the Set Task Escalation links needs to be show in the Route Summary table
  *
  * @param context the eMatrix <code>Context</code> object
  * @param args holds the objectId
  * @returns boolean type
  * @throws Exception if the operation fails
  * @since AEF Rossini
  */

  public static boolean displaySetTaskLinkAccessCheck(Context context, String args[]) throws Exception
  {
         HashMap programMap         = (HashMap) JPO.unpackArgs(args);
         DomainObject objectGeneral = DomainObject.newInstance(context);
         String objectId            = (String) programMap.get(OBJECT_ID);

         String sProjectId          = "";
         String sPassedType         = "";
         String sProjectVaultId     = "";
         boolean result             = true;

        StringBuffer selWorkspaceVaults = new StringBuffer();
        selWorkspaceVaults.append("to[");
        selWorkspaceVaults.append(DomainConstants.RELATIONSHIP_WORKSPACE_VAULTS);
        selWorkspaceVaults.append("].from.id");

        StringBuffer selVaultedDoc = new StringBuffer();
        selVaultedDoc.append("to[");
        selVaultedDoc.append(DomainConstants.RELATIONSHIP_VAULTED_DOCUMENTS);
        selVaultedDoc.append("].from.id");


         if (objectId == null || "".equals(objectId))
         {
            // happens when this JPO invoked for MyRouteSummary
            return true;
         }
         // set the domain object with the passed id , id can be of Workspcae/WorkspcaVault/Document.
         objectGeneral.setId(objectId);
         // get the object type name
         sPassedType= objectGeneral.getType(context);
         if (sPassedType.equals(DomainConstants.TYPE_PROJECT))
         {
             sProjectId = objectId;
         }
         else if(sPassedType.equals(DomainConstants.TYPE_PROJECT_VAULT))
         {
             WorkspaceVault workspaceVault = new WorkspaceVault(objectId);

             StringList objsel = new StringList();
             objsel.add(DomainConstants.SELECT_ID);

             Map topVaultMap   = workspaceVault.getTopLevelVault(context , objsel);
             String topVaultId = (String)topVaultMap.get(DomainConstants.SELECT_ID);

             workspaceVault.setId(topVaultId);

             sProjectId = workspaceVault.getInfo(context , selWorkspaceVaults.toString());

         }
         else if(sPassedType.equals(DomainConstants.TYPE_DOCUMENT) || sPassedType.equals(DomainConstants.TYPE_PACKAGE) || sPassedType.equals(DomainConstants.TYPE_RTS_QUOTATION) || sPassedType.equals(DomainConstants.TYPE_REQUEST_TO_SUPPLIER))
         {
             // get the projectvault id of the passed document.
             sProjectVaultId               = objectGeneral.getInfo(context, selVaultedDoc.toString());

             if(sProjectVaultId != null && !"".equals(sProjectVaultId))
             {
                 WorkspaceVault workspaceVault = new WorkspaceVault(sProjectVaultId);

                 StringList objsel   = new StringList();
                 objsel.add(DomainConstants.SELECT_ID);

                 Map topVaultMap   = workspaceVault.getTopLevelVault(context , objsel);
                 String topVaultId = (String)topVaultMap.get(DomainConstants.SELECT_ID);
                 workspaceVault.setId(topVaultId);

                 sProjectId = workspaceVault.getInfo(context ,selWorkspaceVaults.toString());
             }
             else if(sPassedType.equals(DomainConstants.TYPE_DOCUMENT))
             {
                 WorkspaceVault wsVault = (WorkspaceVault)DomainObject.newInstance(context,DomainConstants.TYPE_WORKSPACE_VAULT);
                 Document doc = (Document)DomainObject.newInstance(context,objectId);
                 sProjectId = doc.getWorkspaceId(context);
                 DomainObject domProj = DomainObject.newInstance(context,sProjectId);
                 if((DomainConstants.TYPE_WORKSPACE_VAULT).equals(domProj.getType(context)))
                 {
                     wsVault.setId(sProjectId);
                     Map wksVaultMap = wsVault.getTopLevelVault(context, new StringList(DomainConstants.SELECT_ID));
                     sProjectId = (String)wksVaultMap.get(DomainConstants.SELECT_ID);
                     wsVault.setId(sProjectId);
                     sProjectId = wsVault.getInfo(context,selWorkspaceVaults.toString());
                 }
             }

         }
         else if(sPassedType.equals(DomainConstants.TYPE_INBOX_TASK))
         {
            StringBuffer selectWorkspaceID = new StringBuffer();
            selectWorkspaceID.append("from[");
            selectWorkspaceID.append(DomainConstants.RELATIONSHIP_ROUTE_TASK);
            selectWorkspaceID.append("].to.to[");
            selectWorkspaceID.append(DomainConstants.RELATIONSHIP_ROUTE_SCOPE);
            selectWorkspaceID.append("].from.id");

             String prjId              =objectGeneral.getInfo(context,selectWorkspaceID.toString());

             DomainObject wkspaceObject= DomainObject.newInstance(context, prjId);

             if(wkspaceObject.getType(context).equals(DomainConstants.TYPE_PROJECT))
             {
                 sProjectId=prjId;
             }
             else
             {
                 WorkspaceVault workspaceVault     =(WorkspaceVault) DomainObject.newInstance(context, prjId);
                 StringList objsel     = new StringList();
                 objsel.add(DomainConstants.SELECT_ID);

                 Map topVaultMap       = workspaceVault.getTopLevelVault(context , objsel);
                 String topVaultId     = (String)topVaultMap.get(DomainConstants.SELECT_ID);

                 workspaceVault.setId(topVaultId);
                 sProjectId            = workspaceVault.getInfo(context , selWorkspaceVaults.toString());
             }
         }

         try
         {
             String workspaceId     = sProjectId;
             Map map                = null;
             Iterator memberItr     = null;
             String sCreateRoute    = null;
             String sWorkspaceAccess= null;

             DomainObject BaseObject = DomainObject.newInstance(context);
             BaseObject.setId(workspaceId);

             String objState = BaseObject.getInfo(context,DomainConstants.SELECT_CURRENT);

             com.matrixone.apps.common.Person person = null;
             person = Person.getPerson(context);
             StringList objectSelects    = new StringList();

             StringBuffer sAttSelCreateRoute   = new StringBuffer();
             sAttSelCreateRoute.append("attribute[");
             sAttSelCreateRoute.append(DomainConstants.ATTRIBUTE_CREATE_ROUTE);
             sAttSelCreateRoute.append("].value");

            StringBuffer sAttSelProjectAccess   = new StringBuffer();
             sAttSelProjectAccess.append("attribute[");
             sAttSelProjectAccess.append(DomainConstants.ATTRIBUTE_PROJECT_ACCESS);
             sAttSelProjectAccess.append("].value");

            StringBuffer sSelWorkspaceId   = new StringBuffer();
             sSelWorkspaceId.append( "to[");
             sSelWorkspaceId.append(DomainConstants.RELATIONSHIP_PROJECT_MEMBERS);
             sSelWorkspaceId.append("].from.id");

             objectSelects.addElement(sAttSelCreateRoute.toString());
             objectSelects.addElement(sAttSelProjectAccess.toString());
             objectSelects.addElement(sSelWorkspaceId.toString());
             objectSelects.addElement(DomainConstants.SELECT_CURRENT);
             String objectWhere = "("+sSelWorkspaceId.toString()+" == '"+workspaceId+"')";

             Pattern typePatternWorkspace = new Pattern(DomainConstants.TYPE_WORKSPACE);
             typePatternWorkspace.addPattern(DomainConstants.TYPE_PROJECT_MEMBER);

             Pattern relPatternProject = new Pattern(DomainConstants.RELATIONSHIP_PROJECT_MEMBERSHIP);
             relPatternProject.addPattern(DomainConstants.RELATIONSHIP_PROJECT_MEMBERS);

             MapList mapList = person.getRelatedObjects(context,
                                                         relPatternProject.getPattern(),
                                                         typePatternWorkspace.getPattern(),
                                                         objectSelects,
                                                         null,
                                                         true,
                                                         true,
                                                         (short)1,
                                                         objectWhere,
                                                         "",
                                                         null,
                                                         null,
                                                         null);

             memberItr = mapList.iterator();
             while(memberItr.hasNext())
             {
                 map              = (Map) memberItr.next();
                 sCreateRoute     = (String)map.get(sAttSelCreateRoute.toString());
                 sWorkspaceAccess = (String)map.get(sAttSelProjectAccess.toString());
             }
             if(sCreateRoute != null && sCreateRoute.equals(YES) && objState.equals("Active") && sWorkspaceAccess != null && sWorkspaceAccess.equalsIgnoreCase("Project Lead"))
             {
                 result=true;
             }
             else
             {
                 result=false;
             }
         }
         catch(Exception e )  {
           throw e;
         }
         return result;
  }



  /**
   * Get the list of Routes.
   *
   * @param context the eMatrix <code>Context</code> object
   * @param args holds the following input arguments:
   *        0 - objectList MapList
   * @param sWhere where condition
   * @returns Object
   * @throws Exception if the operation fails
   * @since Common 10.0.1.1
   */
  public Object getRoutes(Context context, String[] args, String sWhere)
      throws Exception, MatrixException
  {
      try
      {
        HashMap programMap = (HashMap) JPO.unpackArgs(args);
        String parentId = (String) programMap.get(OBJECT_ID);

        StringBuffer routeScopeName= new StringBuffer();
        routeScopeName.append("to[");
        routeScopeName.append(DomainConstants.RELATIONSHIP_ROUTE_SCOPE);
        routeScopeName.append("].from.name");
        com.matrixone.apps.common.Person loggedInPerson = com.matrixone.apps.common.Person.getPerson(context);
        String loggedInUser       = loggedInPerson.getName(context);

        MapList totalResultList = new MapList();
        StringList typeSelects = new StringList();
        typeSelects.add(DomainConstants.SELECT_ID);
        typeSelects.add(DomainConstants.SELECT_DESCRIPTION);
        typeSelects.add(DomainConstants.SELECT_OWNER);
        typeSelects.add(routeScopeName.toString());
        typeSelects.add(sbRouteStatus.toString());
        typeSelects.add(attRestrictMembers.toString());
        typeSelects.add(selParentRouteOwner.toString());
        typeSelects.add(attRouteVisibility.toString());

        boolean isRouteVisibility = true;

        String isRouteVisibilityEnabled = EnoviaResourceBundle.getProperty(context,"emxFramework.Routes.RouteVisibility");
        if( isRouteVisibilityEnabled == null || "false".equals(isRouteVisibilityEnabled))
        {
          isRouteVisibility = false;
        }

        if(parentId != null && !"".equals(parentId) && !"null".equals(parentId))
        {
          //Connected to an object
          totalResultList = Route.getRoutes(context,parentId, typeSelects, null, sWhere,false );
        }
        else
        {

         //MyDesk Query
         totalResultList = Route.getMyRoutes(context, typeSelects, null, isRouteVisibility,sWhere,false);

        }
        // Remove Duplicate Routes
        Hashtable routeHash = new Hashtable();
        MapList routeList = new MapList();
        Iterator mapItr = totalResultList.iterator();
        String sRouteId = null;
        while(mapItr.hasNext())
        {
          Map routeMap = (Map)mapItr.next();
          sRouteId = (String)routeMap.get(DomainConstants.SELECT_ID);
          String routeOwner = (String)routeMap.get(DomainConstants.SELECT_OWNER);
          String parentRouteOwner = (String)routeMap.get(selParentRouteOwner.toString());
          if (!routeHash.containsKey(sRouteId))
          {
           if((routeOwner != null && routeOwner.equals(loggedInUser)) || (parentRouteOwner != null && parentRouteOwner.equals(loggedInUser)))
            {
                String viewSubRoute = (String)routeMap.get(attRouteVisibility.toString());
                if(viewSubRoute != null && viewSubRoute.equals("No"))
                {
                    if(parentRouteOwner == null || "".equals(parentRouteOwner))
                    {
                       routeList.add(routeMap);
                    }
                    else if(parentRouteOwner != null && loggedInUser.equals(parentRouteOwner))
                    {
                       routeList.add(routeMap);
                    }
                }
                else
                {
                   routeList.add(routeMap);
                }
            routeHash.put(sRouteId, "");
            }
//added for 308049
            else
            {
                routeList.add(routeMap);
                routeHash.put(sRouteId, "");
            }
 //till here
           // routeList.add(route);
          }
        }
        totalResultList = routeList;

        return totalResultList;
      }
      catch (Exception ex)
      {
        throw ex;
      }
  }


  /**
   * Gets the list of all Routes.
   *
   * @param context the eMatrix <code>Context</code> object
   * @param args holds the following input arguments:
   *        0 - objectList MapList
   * @returns Object
   * @throws Exception if the operation fails
   * @since Common 10.0.1.1
   */
  @com.matrixone.apps.framework.ui.ProgramCallable
  public Object getAllRoutes(Context context, String[] args)
      throws Exception, MatrixException
  {
      try
      {
          return getRoutes(context,args,"");
      }
      catch (Exception ex)
      {
          throw ex;
      }
  }

  /**
   * Gets the list of Inactive Routes
   *
   * @param context the eMatrix <code>Context</code> object
   * @param args holds the following input arguments:
   *        0 - objectList MapList
   * @returns Object
   * @throws Exception if the operation fails
   * @since AEF 10.0.1.1
   */
  @com.matrixone.apps.framework.ui.ProgramCallable
  public Object getActiveRoutes(Context context, String[] args)
      throws Exception, MatrixException
  {
      try
      {
          String sWhere="";
          if(activeFilter!=null && !"".equals(activeFilter.trim()) )
          {
              StringTokenizer tokenizer=new StringTokenizer(activeFilter,",");
              while(tokenizer.hasMoreTokens())
              {
                  String nextFilter=tokenizer.nextToken();
                  if(!"".equals(sWhere))
                  {
                      sWhere += "||";
                  }
                  sWhere += sbRouteStatus.toString() + " == \"" + nextFilter + "\"";
              }
          }

          return getRoutes(context,args,sWhere);
      }
      catch (Exception ex)
      {
          throw ex;
      }
  }

 /**
  * Gets the list of Active Routes.
  *
  * @param context the eMatrix <code>Context</code> object
  * @param args holds the following input arguments:
  *        0 - objectList MapList
  * @returns Object
  * @throws Exception if the operation fails
  * @since Common 10.0.1.1
  */
  @com.matrixone.apps.framework.ui.ProgramCallable
  public Object getInActiveRoutes(Context context, String[] args)
      throws Exception, MatrixException
  {
       try
       {
            String sWhere="";
            if(inActiveFilter != null && !"".equals(inActiveFilter.trim()))
            {
                StringTokenizer tokenizer=new StringTokenizer(inActiveFilter,",");
                while(tokenizer.hasMoreTokens())
                {
                    String nextFilter=tokenizer.nextToken();
                    if(!"".equals(sWhere))
                    {
                        sWhere += "||";
                    }
                    sWhere += sbRouteStatus.toString() + " == \"" + nextFilter + "\"";
                }
            }
            return getRoutes(context,args,sWhere);
       }
       catch (Exception ex)
       {
           throw ex;
       }
    }
	public static boolean checkCreateSimpleRouteAccess(Context context, String args[]) throws Exception {
		return emxCheckCreateRoutesAccess(context, args) && !(new ${CLASSNAME}(context, args)).showRouteVisibleToParentField(context, args);
	}

    public static boolean emxCheckCreateRoutesAccess(Context context, String args[]) throws Exception
    {

        HashMap programMap         = (HashMap) JPO.unpackArgs(args);
        DomainObject objectGeneral = DomainObject.newInstance(context);
        String objectId            = (String) programMap.get("objectId");

        String loggedInRole = PersonUtil.getActiveSecurityContext(context);
        if (PersonUtil.isVPLMOwner(context, loggedInRole) || PersonUtil.isVPLMAdmin(context, loggedInRole) || PersonUtil.isVPLMReader(context, loggedInRole))  {
               return false;         
        }
        
        String loggedInUser       = context.getUser();
        Access access = null;
        if (objectId == null || "".equals(objectId) || "null".equals(objectId))
        {
           return true;
        }

        
        objectGeneral.setId(objectId);
        StringList grantorList = Route.getGranteeGrantor(context, objectId);
        if(grantorList.contains(AEF_COMMON_ACCESS_GRANTOR_USERNAME)) {
            access = objectGeneral.getAccessForGranteeGrantor(context, loggedInUser, AEF_COMMON_ACCESS_GRANTOR_USERNAME);
        } else if(grantorList.contains(AEF_ROUTE_ACCESS_GRANTOR_USERNAME)){
            access = objectGeneral.getAccessForGranteeGrantor(context, loggedInUser, AEF_ROUTE_ACCESS_GRANTOR_USERNAME);
        } else {
            access = objectGeneral.getAccessMask(context);
           }
        
        if (objectGeneral.isKindOf(context,PropertyUtil.getSchemaProperty(context, DomainSymbolicConstants.SYMBOLIC_type_DOCUMENTS))){
        	// With Team central security context HL, anyone having 'Add Remove' access can create Route
        	return AccessUtil.hasReadAccess(access);
        } else {
        	return access.hasReadAccess();
        }
        
    }

    public Vector getScope(Context context,String[] args) throws Exception
    {
        Vector AvailabilityList = new Vector();

        try
        {
            HashMap programMap = (HashMap) JPO.unpackArgs(args);


            Map paramList      = (Map)programMap.get("paramList");
            String languageStr = (String)paramList.get("languageStr");

            MapList objectList = (MapList)programMap.get("objectList");
            StringBuffer sbAvailabilityURL = new StringBuffer();
            StringBuffer strBuf = new StringBuffer();

            boolean isPrinterFriendly = false;
            String PrinterFriendly = (String)paramList.get("reportFormat");
            if (PrinterFriendly != null ) {
               isPrinterFriendly = true;
            }

            Iterator objectListItr = objectList.iterator();
            while(objectListItr.hasNext())
            {
                Map objectMap = (Map) objectListItr.next();
                String restrictScope =(String)objectMap.get(attRestrictMembers.toString());
                if ((restrictScope != null )&& (!restrictScope.equals("All")) && (!restrictScope.equals("Organization")))
                {
                    //<Fix 371409>
                    //If the login user doesn't have access to the scope object
                    //get the details by pushing the context and display name without hyperlink
                    Route route = new Route((String) objectMap.get(DomainConstants.SELECT_ID));
                    boolean hasAccessToScopeObject = route.hasReadAccessToScopeObject(context, restrictScope);
                    Map scopeObjInfo = route.getScopeObjectTypeNameRevision(context, restrictScope, !hasAccessToScopeObject);

                    if(!isPrinterFriendly && hasAccessToScopeObject) {

                        sbAvailabilityURL.append("../common/emxTree.jsp?objectId=");
                        sbAvailabilityURL.append(XSSUtil.encodeForJavaScript(context, restrictScope));

                        strBuf.append("<a href='javascript:showModalDialog(\""+sbAvailabilityURL.toString()+"\",575,575)'>");
                        strBuf.append(XSSUtil.encodeForXML(context, scopeObjInfo.get(DomainConstants.SELECT_NAME).toString())).append("</a>");
                    } else
                    {
                        strBuf.append(XSSUtil.encodeForXML(context, scopeObjInfo.get(DomainConstants.SELECT_NAME).toString()));
                    }

                }
                else
                {
                    strBuf.append(i18nNow.getRangeI18NString(DomainConstants.ATTRIBUTE_RESTRICT_MEMBERS,restrictScope,languageStr));
                }
                AvailabilityList.add(strBuf.toString());
                strBuf.delete(0,strBuf.length());
                sbAvailabilityURL.delete(0,sbAvailabilityURL.length());
            }

        }
        catch (Exception ex)
        {
            throw ex;
        }
        return AvailabilityList;
    }

    /**
     * gets the list of connected connect for the Route
     * Used for APPDocumentSummary table
     *
     * @param context the eMatrix <code>Context</code> object
     * @param args holds the following input arguments:
     *        0 - objectId - parent object OID
     * @returns Object
     * @throws Exception if the operation fails
     * @since Common 10.5
     */
    @com.matrixone.apps.framework.ui.ProgramCallable
    public Object getContent(Context context, String[] args)
        throws Exception
    {
        try
        {
            HashMap programMap        = (HashMap) JPO.unpackArgs(args);
            String  parentId          = (String) programMap.get(OBJECT_ID);
            String  relPattern        = (String) programMap.get("parentRelName");

            if(relPattern == null || "null".equals(relPattern) || "".equals(relPattern))
            {
                relPattern = PropertyUtil.getSchemaProperty(context, DomainSymbolicConstants.SYMBOLIC_relationship_ReferenceDocument);
            }
            else
            {
                relPattern = PropertyUtil.getSchemaProperty(context, relPattern);
            }

            String objectWhere = "";//CommonDocument.SELECT_IS_VERSION_OBJECT + "==\"False\"";

            DomainObject masterObject = DomainObject.newInstance(context, parentId);

            StringList typeSelects = new StringList();
            typeSelects.add(DomainConstants.SELECT_ID);
            typeSelects.add(DomainConstants.SELECT_TYPE);
            typeSelects.add(DomainConstants.SELECT_FILE_NAME);
            typeSelects.add(CommonDocument.SELECT_ACTIVE_FILE_LOCKED);
            typeSelects.add(CommonDocument.SELECT_TITLE);
            typeSelects.add(DomainConstants.SELECT_REVISION);
            typeSelects.add(DomainConstants.SELECT_NAME);
            typeSelects.add(CommonDocument.SELECT_ACTIVE_FILE_VERSION);
            typeSelects.add(CommonDocument.SELECT_HAS_ROUTE);
            typeSelects.add(CommonDocument.SELECT_FILE_NAMES_OF_ACTIVE_VERSION);
            typeSelects.add(CommonDocument.SELECT_SUSPEND_VERSIONING);
            typeSelects.add(CommonDocument.SELECT_HAS_CHECKOUT_ACCESS);
            typeSelects.add(CommonDocument.SELECT_HAS_CHECKIN_ACCESS);
            typeSelects.add(CommonDocument.SELECT_MOVE_FILES_TO_VERSION);
            typeSelects.add(DomainConstants.SELECT_POLICY);

            StringList relSelects = new StringList();
            relSelects.add(DomainConstants.SELECT_RELATIONSHIP_ID);
            relSelects.add(Route.SELECT_ROUTE_BASESTATE);
            MapList documentList = masterObject.getRelatedObjects(context,
                                                          relPattern,
                                                          "*",
                                                          typeSelects,
                                                          relSelects,
                                                          true,
                                                          false,
                                                          (short)0,
                                                          objectWhere,
                                                          null,
                                                          null,
                                                          null,
                                                          null);
            return documentList;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            throw ex;
        }
    }

  /**
   * canUpload - This method is used to determine if
   *             the context user can Upload.
   * @param context the eMatrix <code>Context</code> object
   * @param args empty
   * @return boolean
   * @throws Exception if the operation fails
   * @since Common 10-5
   */
  public boolean canUpload(Context context, String[] args)
    throws Exception
  {
      HashMap programMap = (HashMap) JPO.unpackArgs(args);
      String objectId    = (String) programMap.get(OBJECT_ID);

      boolean bTeam = FrameworkUtil.isSuiteRegistered(context,"featureVersionTeamCentral",false,null,null);
      String loggedInRole = PersonUtil.getActiveSecurityContext(context);
      String roleOwner =   PropertyUtil.getSchemaProperty(context,"role_VPLMProjectAdministrator");
      String roleAdmin =   PropertyUtil.getSchemaProperty(context,"role_VPLMAdmin");
      //commented for bug 318155

      /*boolean bProgram = FrameworkUtil.isSuiteRegistered(context,"appVersionProgramCentral",false,null,null);
      If Team or Program is not installed then cannot upload.
      if ((!bTeam) && (!bProgram))*/

      //If Team is not installed then cannot upload.
      if ((!bTeam)) {
        return false;
      }

      Route route = (Route)DomainObject.newInstance(context,DomainConstants.TYPE_ROUTE);
      route.setId(objectId);
      route.open(context);
      Access contextAccess = route.getAccessMask(context);
      StringList selects = new StringList(2);
      selects.addElement(DomainConstants.SELECT_OWNER);
      selects.addElement(DomainConstants.SELECT_CURRENT);
      Map routeMap = route.getInfo(context, selects);

      String sOwner = (String)routeMap.get(DomainConstants.SELECT_OWNER);
      String sState = (String)routeMap.get(DomainConstants.SELECT_CURRENT);
      route.close(context);

      boolean isRouteEditable = true;
      // Do not show links if the Route State is Complete or Archive
      if(sState.equals(Route.STATE_ROUTE_COMPLETE) || sState.equals("Archive"))
      {
         return false;
      }

      boolean showLink = false;
      // show link only to the route owner
     if (((sOwner.equals(context.getUser()) && (isRouteEditable)) || (contextAccess.hasToConnectAccess() && contextAccess.hasFromConnectAccess())) && !loggedInRole.contains(roleOwner) && !loggedInRole.contains(roleAdmin) )
        showLink = true;

     return showLink;
  }

  /**
   * canAddContent - This method is used to determine if
   *             the context user can Add Content to a Route.
   * @param context the eMatrix <code>Context</code> object
   * @param args empty
   * @return boolean
   * @throws Exception if the operation fails
   * @since Common 10-5
   */
  public boolean canAddContent(Context context, String[] args)
    throws Exception
  {
      HashMap programMap = (HashMap) JPO.unpackArgs(args);
      String objectId    = (String) programMap.get(OBJECT_ID);

      Route route = (Route)DomainObject.newInstance(context,DomainConstants.TYPE_ROUTE);
      route.setId(objectId);
      route.open(context);
      Access contextAccess = route.getAccessMask(context);
      StringList selects = new StringList(2);
      selects.addElement(DomainConstants.SELECT_OWNER);
      selects.addElement(DomainConstants.SELECT_CURRENT);
      Map routeMap = route.getInfo(context, selects);

      String sOwner = (String)routeMap.get(DomainConstants.SELECT_OWNER);
      String sState = (String)routeMap.get(DomainConstants.SELECT_CURRENT);
      route.close(context);

      boolean isRouteEditable = true;
      // Do not show links if the Route State is Complete or Archive
      if(sState.equals(Route.STATE_ROUTE_COMPLETE) || sState.equals("Archive"))
      {
         return false;
      }

      boolean showLink = false;
      // show link only to the route owner
     if ((sOwner.equals(context.getUser()) && (isRouteEditable)) || (contextAccess.hasFromConnectAccess() && contextAccess.hasToConnectAccess()))
        showLink = true;

     return showLink;
  }

  /**
   * canEdit - This method is used to determine if
   *           the context user can Edit the LifeCycle Blocks of a Route
   * @param context the eMatrix <code>Context</code> object
   * @param args empty
   * @return boolean
   * @throws Exception if the operation fails
   * @since Common 10-5
   */
  public boolean canEdit(Context context, String[] args)
    throws Exception
  {
      HashMap programMap = (HashMap) JPO.unpackArgs(args);
      String objectId    = (String) programMap.get(OBJECT_ID);

      Route route = (Route)DomainObject.newInstance(context,DomainConstants.TYPE_ROUTE);
      route.setId(objectId);
      route.open(context);
      StringList selects = new StringList(2);
      selects.addElement(DomainConstants.SELECT_OWNER);
      selects.addElement(DomainConstants.SELECT_CURRENT);
      Map routeMap = route.getInfo(context, selects);

      String sOwner = (String)routeMap.get(DomainConstants.SELECT_OWNER);
      String sState = (String)routeMap.get(DomainConstants.SELECT_CURRENT);
      route.close(context);

      boolean isRouteEditable = true;
      // Do not show links if the Route State is Complete or Archive
      if(sState.equals(Route.STATE_ROUTE_COMPLETE) || sState.equals("Archive"))
      {
         isRouteEditable = false;
      }

      boolean showLink = false;
      // show link only to the route owner
     if (sOwner.equals(context.getUser()) && (isRouteEditable))
        showLink = true;

     return showLink;
  }
  public boolean canEditLifecycleBlocks(Context context, String[] args)
		    throws Exception
		  {
		      HashMap programMap = (HashMap) JPO.unpackArgs(args);
		      String objectId    = (String) programMap.get(OBJECT_ID);

		      Route route = (Route)DomainObject.newInstance(context,DomainConstants.TYPE_ROUTE);
		      route.setId(objectId);
		      route.open(context);
		      StringList selects = new StringList(2);
		      selects.addElement(DomainConstants.SELECT_OWNER);
		      selects.addElement(DomainConstants.SELECT_CURRENT);
		      selects.addElement(Route.SELECT_ROUTE_STATUS);
		      Map routeMap = route.getInfo(context, selects);

		      String sOwner = (String)routeMap.get(DomainConstants.SELECT_OWNER);
		      String sState = (String)routeMap.get(DomainConstants.SELECT_CURRENT);
		      String sStatus = (String)routeMap.get(Route.SELECT_ROUTE_STATUS);
		      System.out.println("sStatus "+sStatus);
		      route.close(context);

		      boolean isRouteEditable = true;
		      // Do not show links if the Route State is Complete or Archive
		      if(sState.equals(Route.STATE_ROUTE_COMPLETE) || sState.equals("Archive"))
		      {
		         isRouteEditable = false;
		      }

		      boolean showLink = false;
		      // show link only to the route owner

		      StringList members = new StringList(route.getRouteMembers(context));
		      if((members.contains(context.getUser()) || sOwner.equals(context.getUser())) && (isRouteEditable)){
		    	  showLink = true;
		      }
		     return showLink;
		  }
   public Vector showStateBlock(Context context, String[] args)
        throws Exception
    {
        try
        {
            HashMap programMap = (HashMap) JPO.unpackArgs(args);
            MapList objectList = (MapList)programMap.get("objectList");
            Map paramList      = (Map)programMap.get("paramList");
            String languageStr = (String)paramList.get("languageStr");
            Vector stateBlockList = new Vector();
            Iterator objectListItr = objectList.iterator();
            String sNoneValue = EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource", new Locale(languageStr),"emxComponents.Common.None");
            String stateValue="";
            String stateBlockVal="";
            String sPolicy="";
            while(objectListItr.hasNext())
            {
                Map objectMap = (Map) objectListItr.next();
                stateBlockVal = (String) objectMap.get(Route.SELECT_ROUTE_BASESTATE);
                sPolicy=(String) objectMap.get(DomainConstants.SELECT_POLICY);

                if( ( stateBlockVal != null) && (!stateBlockVal.equals("")) )
                {
                 stateBlockVal = FrameworkUtil.lookupStateName(context,sPolicy,stateBlockVal);
           if (stateBlockVal != null && !"Ad Hoc".equals(stateBlockVal)){
                        stateValue = i18nNow.getStateI18NString(sPolicy,stateBlockVal,languageStr); //FrameworkUtil.lookupStateName(context,sPolicy,stateBlockVal);
                    } else {
                        stateValue = sNoneValue;
                   }
                }else{
                    stateValue = sNoneValue;
                }
                stateBlockList.add(stateValue);
            }
            return stateBlockList;
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }

    public boolean canShowStateBlockColumn (Context context,String[] args) throws Exception
    {
        HashMap programMap = (HashMap) JPO.unpackArgs(args);
        String objectId = "";
        objectId=(String)programMap.get(OBJECT_ID);
        DomainObject route = new DomainObject();
        route.setId(objectId);
        String type = route.getInfo(context,DomainConstants.SELECT_TYPE);
        boolean retVal=false;
        if( (type != null) && (!type.equals("")) && (type.equals(DomainConstants.TYPE_ROUTE)) )
            retVal=true;
        else
            retVal=false;
        return retVal;
    }

  /**
   * canDeleteContent - This method is used to determine if
   *             the context user can Delete Content to a Route.
   * @param context the eMatrix <code>Context</code> object
   * @param args empty
   * @return boolean
   * @throws Exception if the operation fails
   * @since Common 10-5
   */
  public boolean canDeleteContent(Context context, String[] args)
    throws Exception
  {
      HashMap programMap = (HashMap) JPO.unpackArgs(args);
      String objectId    = (String) programMap.get(OBJECT_ID);

      Route route = (Route)DomainObject.newInstance(context,DomainConstants.TYPE_ROUTE);
      route.setId(objectId);
      route.open(context);
      Access contextAccess = route.getAccessMask(context);
      StringList selects = new StringList(2);
      selects.addElement(DomainConstants.SELECT_OWNER);
      selects.addElement(DomainConstants.SELECT_CURRENT);
      Map routeMap = route.getInfo(context, selects);

      String sOwner = (String)routeMap.get(DomainConstants.SELECT_OWNER);
      String sState = (String)routeMap.get(DomainConstants.SELECT_CURRENT);
      route.close(context);

      boolean isRouteEditable = true;
      // Do not show links if the Route State is Complete or Archive
      if(sState.equals(Route.STATE_ROUTE_COMPLETE) || sState.equals("Archive"))
      {
         return false;
      }

      boolean showLink = false;
      // show link only to the route owner
     if ((sOwner.equals(context.getUser()) && (isRouteEditable)) || (contextAccess.hasFromDisconnectAccess() && contextAccess.hasToDisconnectAccess()) )
        showLink = true;

     return showLink;
  }

/**
   * canRemoveContent - This method is used to determine if
   *             the context user can remove Content to a Route.
   * @param context the eMatrix <code>Context</code> object
   * @param args empty
   * @return boolean
   * @throws Exception if the operation fails
   * @since Common 11-0
   */
//Added for the Bug 314495 Begin
 public boolean canRemoveContent(Context context, String[] args)
    throws Exception
  {
      HashMap programMap = (HashMap) JPO.unpackArgs(args);
      String objectId    = (String) programMap.get(OBJECT_ID);
       boolean showLink = true;

      if(objectId == null || objectId.equals(""))
       {
        showLink = false;
       }

      return showLink;
  }
//Added for the Bug 314495 End

    public com.matrixone.jdom.Document getRouteMailXML(Context context, Map info) throws Exception
    {
        // get base url
        String baseURL = (String)info.get("baseURL");
        // get notification name
        String notificationName = (String)info.get("notificationName");
        HashMap eventCmdMap = UIMenu.getCommand(context, notificationName);
        String eventName = UIComponent.getSetting(eventCmdMap, "Event Type");
        String eventKey = "emxComponents.Route.Event." + eventName.replace(' ', '_');
        String bundleName = (String)info.get("bundleName");
        String locale = ((Locale)info.get("locale")).toString();
        String i18NEvent = EnoviaResourceBundle.getProperty(context, bundleName, context.getLocale(),eventKey);
        // get Message Type
        String messageType = (String)info.get("messageType");

        // get route id
        String routeId = (String)info.get("id");
        // get document object info
        DomainObject route = DomainObject.newInstance(context, routeId);
        StringList selectList = new StringList(3);
        selectList.addElement(SELECT_TYPE);
        selectList.addElement(SELECT_NAME);
        selectList.addElement(SELECT_REVISION);
        Map routeInfo = route.getInfo(context, selectList);
        String routeType = (String)routeInfo.get(SELECT_TYPE);
        String i18NRouteType = UINavigatorUtil.getAdminI18NString("type", routeType, locale);
        String routeName = (String)routeInfo.get(SELECT_NAME);
        String routeRev = (String)routeInfo.get(SELECT_REVISION);

        // header data
        HashMap headerInfo = new HashMap();
        headerInfo.put("header", i18NEvent + " : " + i18NRouteType + " " + routeName + " " + routeRev);

        // body data
        HashMap bodyInfo = null;
        MapList objList = route.getRelatedObjects(context, DomainConstants.RELATIONSHIP_OBJECT_ROUTE, "*", selectList, null, true, false, (short)1, null, null);
        if (objList != null && objList.size() > 0)
        {
            bodyInfo = new HashMap();
            HashMap fieldInfo = new HashMap();
            bodyInfo.put(EnoviaResourceBundle.getProperty(context,bundleName, context.getLocale(), "emxComponents.Route.Event.Mail.Connected_Objects"),fieldInfo); 
            for(int i = 0; i < objList.size(); i++)
            {
                Map objInfo = (Map) objList.get(i);
                String objType = (String)objInfo.get(SELECT_TYPE);
                String i18NObjectType = UINavigatorUtil.getAdminI18NString("type", objType, locale);
                String objName = (String)objInfo.get(SELECT_NAME);
                String objRev = (String)objInfo.get(SELECT_REVISION);

                fieldInfo.put(EnoviaResourceBundle.getProperty(context,bundleName, context.getLocale(), "emxComponents.Route.Event.Mail.TNR"), i18NObjectType + " " + objName + " " + objRev);
            }
        }

        // footer data
        HashMap footerInfo = new HashMap();
        ArrayList dataLineInfo = new ArrayList();
        if (messageType.equalsIgnoreCase("html"))
        {
            String[] messageValues = new String[4];
            messageValues[0] = baseURL + "?objectId=" + routeId;
            messageValues[1] = i18NRouteType;
            messageValues[2] = routeName;
            messageValues[3] = routeRev;
            String viewLink = MessageUtil.getMessage(context,null,
                                                     "emxComponents.Object.Event.Html.Mail.ViewLink",
                                                     messageValues,null,
                                                     context.getLocale(),bundleName);

            dataLineInfo.add(viewLink);
        } else {
            String[] messageValues = new String[3];
            messageValues[0] = i18NRouteType;
            messageValues[1] = routeName;
            messageValues[2] = routeRev;
            String viewLink = MessageUtil.getMessage(context,null,
                                                     "emxComponents.Object.Event.Text.Mail.ViewLink",
                                                     messageValues,null,
                                                     context.getLocale(),bundleName);

            dataLineInfo.add(viewLink);
            dataLineInfo.add(baseURL + "?objectId=" + routeId);
        }
        footerInfo.put("dataLines", dataLineInfo);

        return (${CLASS:emxSubscriptionUtil}.prepareMailXML(context, headerInfo, bodyInfo, footerInfo));
    }

    public String getRouteMessageHTML(Context context, String[] args) throws Exception
    {
        Map info = (Map)JPO.unpackArgs(args);
        info.put("messageType", "html");
        com.matrixone.jdom.Document doc = getRouteMailXML(context, info);

        return (${CLASS:emxSubscriptionUtil}.getMessageBody(context, doc, "html"));

    }

    public String getRouteMessageText(Context context, String[] args) throws Exception
    {
        Map info = (Map)JPO.unpackArgs(args);
        info.put("messageType", "text");
        com.matrixone.jdom.Document doc = getRouteMailXML(context, info);

        return (${CLASS:emxSubscriptionUtil}.getMessageBody(context, doc, "text"));

    }

    /**
     * Implements resume process for the route object. Following things will happen on broad level
     * o    All the tasks of this route will be found, and each tasks if assigned to any task assignee, it will be unassigned.
     * (The Inbox Task will be disconnected from the Person object with relationship 'Project Task').
     * So all these tasks will be removed from the 'My Tasks' list of the respective task assignees.
     * o    Demote the route to Define state. (The first state of the route).
     * o    Set 'Current Route Node' on route object to 1. The Route Status of the route will be made as 'Started' and route is promoted to 'In Process' state.
     * This will start the route.
     *
     * @param context The Matrix Context object
     * @param args The arguments array.
     * @return 0 indicating successful operation or 1 indicating unsuccessful operation
     * @throws Exception of operation fails
     * @since Common V6R2009-1
     * @grade 0
     */
    public int resume(Context context, String[] args) throws Exception {
        // Arguments check
        if (context == null) {
            throw new Exception("Invalid context");
        }
        try {
            // Get parameter
            String strRouteId = this.getId();
            ContextUtil.startTransaction(context, true);

            // Some constants
            final String POLICY_ROUTE_STATE_DEFINE = PropertyUtil.getSchemaProperty(context, "Policy", POLICY_ROUTE, "state_Define");
            final String POLICY_INBOX_TASK_STATE_ASSIGNED = PropertyUtil.getSchemaProperty(context, "Policy", POLICY_INBOX_TASK, "state_Assigned");
            final String POLICY_INBOX_TASK_STATE_COMPLETE = PropertyUtil.getSchemaProperty(context, "Policy", POLICY_INBOX_TASK, "state_Complete");
            final String SELECT_RELATIONSHIP_PROJECT_TASK_ID = "from[" + RELATIONSHIP_PROJECT_TASK + "].id";
            final String SELECT_ROUTE_NODE_ID = "attribute[" + DomainConstants.ATTRIBUTE_ROUTE_NODE_ID + "]";
            final String SELECT_ROUTE_TASK_USER = "attribute[" + DomainConstants.ATTRIBUTE_ROUTE_TASK_USER + "]";
            final String ATTRIBUTE_CURRENT_ROUTE_NODE = PropertyUtil.getSchemaProperty(context, "attribute_CurrentRouteNode");
            final boolean USE_CACHE = true;

            // Variables
            Map mapInfo = null;
            String strRelProjectTaskId = "";
            String strTaskId = "";
            String strTaskState = "";
            String strRouteTaskUser = "";
            String strRouteTaskUserId = "";
            String strRouteNodeId = "";
            String strRoleOrGroup = "";
            DomainObject dmoTask = null;
            DomainObject dmoRTU = new DomainObject();

            // Create route object
            Route objRoute = (Route)newInstance(context, TYPE_ROUTE);
            objRoute.setId(strRouteId);

            // Expand the route to find the connected task objects
            StringList slBusSelect = new StringList();
            StringList slRelSelect = new StringList();
            String strWhereClause = "";

            slBusSelect.add(SELECT_RELATIONSHIP_PROJECT_TASK_ID);
            slBusSelect.add(DomainConstants.SELECT_ID);
            slBusSelect.add(DomainConstants.SELECT_CURRENT);
            slBusSelect.add(SELECT_ROUTE_NODE_ID);
            slBusSelect.add(SELECT_ROUTE_TASK_USER);

            MapList mlRouteTasks = objRoute.getRouteTasks(context, slBusSelect, slRelSelect, strWhereClause, !USE_CACHE);

            // Iterate on each task
            for (Iterator itrRouteTasks = mlRouteTasks.iterator(); itrRouteTasks.hasNext(); ) {
                mapInfo = (Map)itrRouteTasks.next();

                strTaskId = (String)mapInfo.get(DomainConstants.SELECT_ID);
                strTaskState = (String)mapInfo.get(DomainConstants.SELECT_CURRENT);
                strRelProjectTaskId = (String)mapInfo.get(SELECT_RELATIONSHIP_PROJECT_TASK_ID);
                strRouteNodeId = (String)mapInfo.get(SELECT_ROUTE_NODE_ID);
                strRouteTaskUser = (String)mapInfo.get(SELECT_ROUTE_TASK_USER);

                // Disconnect the relationship project task, so that the task will be unassigned
                if (strRelProjectTaskId != null) {
                		ContextUtil.pushContext(context);
                    DomainRelationship.disconnect(context, strRelProjectTaskId);
						ContextUtil.popContext(context);
                }

                dmoTask = new DomainObject(strTaskId);

                // If the route task user is given then the task was created for role or group.
                // Therefore, we will disconnect the current person from route and then connect an newly created Route Task User
                // object, so that next time when route will be started, the task will be assigned to the role/group.
                //
                if (strRouteTaskUser != null && !"".equals(strRouteTaskUser) && (strRouteTaskUser.startsWith("role_") || strRouteTaskUser.startsWith("group_"))) {
                    // Create a new RTU object
                    dmoRTU.createObject(context, DomainConstants.TYPE_ROUTE_TASK_USER, null, null, DomainConstants.POLICY_ROUTE_TASK_USER, null);

                    // Connect to the 'to' side of the corresponding route node relationship
                    DomainRelationship.setToObject(context, strRouteNodeId, dmoRTU);

                    // Set the value of RTU on route node relationship (it was cleared when task was completed)
                    DomainRelationship.setAttributeValue(context, strRouteNodeId, DomainConstants.ATTRIBUTE_ROUTE_TASK_USER, strRouteTaskUser);

                    // Check the state of this task, if it is not Complete then it means that it will be reused.
                    // So set its owner as Route Task User (role or group).
                    if (!POLICY_INBOX_TASK_STATE_COMPLETE.equals(strTaskState)) {
                        strRoleOrGroup = PropertyUtil.getSchemaProperty(context, strRouteTaskUser);
                        dmoTask.setOwner(context, strRoleOrGroup);
                    }
                }

                // Check the state of this task, if it is not Complete then set it to Assigned (first state).
                if (!POLICY_INBOX_TASK_STATE_COMPLETE.equals(strTaskState)) {
                    dmoTask.setState(context, POLICY_INBOX_TASK_STATE_ASSIGNED);
                }
            }//for

            // Set route object from the begining
            objRoute.setState(context, POLICY_ROUTE_STATE_DEFINE);

            // Reset the attribute values
            Map mapAttributes = new HashMap();
            mapAttributes.put(ATTRIBUTE_CURRENT_ROUTE_NODE, "1");
            mapAttributes.put(ATTRIBUTE_ROUTE_STATUS, "Not Started");
            objRoute.setAttributeValues(context, mapAttributes);

            //Promote the route, so that InitiateRoute() method in emxCommonInitiateRouteBase JPO will be triggered
            objRoute.promote(context);
            objRoute.setDueDateFromOffsetForGivenLevelTasks(context, 1);

            ContextUtil.commitTransaction(context);
            return 0;
        }
        catch (Exception exp) {
            ContextUtil.abortTransaction(context);
            throw exp;
        }
    }

    /**
     * Implements promote trigger process for the object. Following things will happen on broad level
     * For the object for which the routes are attached to when promoted to next state the Routes
     * attached to the state is resumed.
     * @param context The Matrix Context object
     * @param args The arguments array.
     *  args[0] : The object id
     * @return 0 indicating successful operation or 1 indicating unsuccessful operation
     * @throws Exception of operation fails
     * @since Common V6R2009-1
     * @grade 0
     */
    public int triggerAutoResumeRoute(Context context, String[] args) throws Exception
    {
        if (context == null) {
            throw new Exception("Invalid context");
        }

        String strObjectId = args[0];   // getting the id of the object
        DomainObject domainObject = new DomainObject(strObjectId);

        StringList busSelects = new StringList();
        busSelects.add(DomainConstants.SELECT_CURRENT);
        busSelects.add(DomainConstants.SELECT_POLICY);

        //Finding the current state and policy of the object
        Map mapInfo = domainObject.getInfo(context, busSelects);
        String strStateName = (String)mapInfo.get(DomainConstants.SELECT_CURRENT);
        String strPolicyName = (String)mapInfo.get(DomainConstants.SELECT_POLICY);

        //Getting the symbolic names of the object
        String strSymbolicState = FrameworkUtil.reverseLookupStateName(context, strPolicyName, strStateName);
        String strSymbolicPolicy = FrameworkUtil.getAliasForAdmin(context, "Policy", strPolicyName, false);

        //Obtaining the state based routes of the current state
        String strTypePattern = DomainConstants.TYPE_ROUTE;
        String strRelPattern = DomainConstants.RELATIONSHIP_OBJECT_ROUTE;
        StringList slBusSelect = new StringList();
        slBusSelect.add(DomainConstants.SELECT_ID);
        StringList slRelSelect = new StringList();
        short nRecurseLevel = (short)1;
        String strBusWhere = "";
        String strRelWhere = "attribute[" + DomainConstants.ATTRIBUTE_ROUTE_BASE_STATE + "]==\"" + strSymbolicState + "\" && attribute[" + DomainConstants.ATTRIBUTE_ROUTE_BASE_POLICY + "]==\"" +strSymbolicPolicy+ "\"";
        final boolean GET_TO = true;
        final boolean GET_FROM = true;

        MapList mlRoutes = domainObject.getRelatedObjects(context, strRelPattern, strTypePattern, slBusSelect, slRelSelect, !GET_TO, GET_FROM, nRecurseLevel, strBusWhere, strRelWhere);

        Map mapRoute = null;
        String strRouteId = "";

        for (Iterator itrRoutes = mlRoutes.iterator(); itrRoutes.hasNext();) {
            mapRoute = (Map)itrRoutes.next();
            strRouteId = (String)mapRoute.get(DomainConstants.SELECT_ID);

            this.setId(strRouteId);
            this.resume(context, new String[0]); //calling the resume process for the state based Route
        }

        return 0;
    }

    /**
     * Implements demote trigger process for the object. Following things will happen on broad level
     * For the object for which the routes are attached to when Demoted the Routes attached to the state
     * it is demoted will start from the begining and the routes attached to the state from which it is
     * demoted will stop.
     * @param context The Matrix Context object
     * @param args The arguments array.
     *  args[0] : The object id
     * @return 0 indicating successful operation or 1 indicating unsuccessful operation
     * @throws Exception of operation fails
     * @since Common V6R2009-1
     * @grade 0
     */
    public int triggerAutoStopRoute(Context context, String[] args) throws Exception
    {
        if (context == null) {
            throw new Exception("Invalid context");
        }

        String strObjectId = args[0];   // getting the id of the object
        DomainObject domainObject = new DomainObject(strObjectId);

        StringList busSelects = new StringList();
        busSelects.add(DomainConstants.SELECT_CURRENT);
        busSelects.add(DomainConstants.SELECT_POLICY);

        //Finding the current state and policy of the object
        Map mapInfo = domainObject.getInfo(context, busSelects);
        String strStateName = (String)mapInfo.get(DomainConstants.SELECT_CURRENT);
        String strPolicyName = (String)mapInfo.get(DomainConstants.SELECT_POLICY);

         //Iterating to get the next state from whr the object was demoted
         StateList stateList = domainObject.getStates(context);
         ArrayList listStates = new ArrayList();
         State state = null;
         for (Iterator itrStates = stateList.iterator(); itrStates.hasNext();) {
             state = (State)itrStates.next();
             listStates.add(state.getName());
         }
         int nIndexOfState = listStates.indexOf(strStateName);
         if (nIndexOfState == -1) {
             String[] formatArgs = {strStateName};
             String message =  ComponentsUIUtil.getI18NString(context, "emxComponents.RouteBase.CannotFindState",formatArgs);
             throw new Exception(message);
         }
         String strNextStateName = (String)listStates.get(nIndexOfState + 1);

        //Getting the symbolic names of the object
        String strSymbolicState = FrameworkUtil.reverseLookupStateName(context, strPolicyName, strStateName);
        String strSymbolicNextState = FrameworkUtil.reverseLookupStateName(context, strPolicyName, strNextStateName);
        String strSymbolicPolicy = FrameworkUtil.getAliasForAdmin(context, "Policy", strPolicyName, false);

        //Obtaining the state based routes of the current state
        String strTypePattern = DomainConstants.TYPE_ROUTE;
        String strRelPattern = DomainConstants.RELATIONSHIP_OBJECT_ROUTE;
        StringList slBusSelect = new StringList();
        slBusSelect.add(DomainConstants.SELECT_ID);
        StringList slRelSelect = new StringList();
        short nRecurseLevel = (short)1;
        String strBusWhere = "";
        String strRelWhere = "attribute[" + DomainConstants.ATTRIBUTE_ROUTE_BASE_STATE + "]== \"" + strSymbolicState + "\" && attribute[" + DomainConstants.ATTRIBUTE_ROUTE_BASE_POLICY + "]==\"" +strSymbolicPolicy+ "\"";
        final boolean GET_TO = true;
        final boolean GET_FROM = true;

        MapList mlRoutes = domainObject.getRelatedObjects(context, strRelPattern, strTypePattern, slBusSelect, slRelSelect, !GET_TO, GET_FROM, nRecurseLevel, strBusWhere, strRelWhere);

        Map mapRoute = null;
        String strRouteId = "";
        for (Iterator itrRoutes = mlRoutes.iterator(); itrRoutes.hasNext();) {
            mapRoute = (Map)itrRoutes.next();
            strRouteId = (String)mapRoute.get(DomainConstants.SELECT_ID);

            this.setId(strRouteId);
            this.resume(context, new String[0]); //calling the resume process for the state based Route
        }

        //Obtaining the state based Route for the state from which the object was demoted
        strRelWhere = "attribute[" + DomainConstants.ATTRIBUTE_ROUTE_BASE_STATE + "]== \"" + strSymbolicNextState + "\" && attribute[" + DomainConstants.ATTRIBUTE_ROUTE_BASE_POLICY + "]==\"" +strSymbolicPolicy+ "\"";

        mlRoutes = domainObject.getRelatedObjects(context, strRelPattern, strTypePattern, slBusSelect, slRelSelect, !GET_TO, GET_FROM, nRecurseLevel, strBusWhere, strRelWhere);

        DomainObject dmoObject = null;
        String strRouteStatus = null;
        for (Iterator itrRoutes = mlRoutes.iterator(); itrRoutes.hasNext();) {
            mapRoute = (Map)itrRoutes.next();
            strRouteId = (String)mapRoute.get(DomainConstants.SELECT_ID);

            dmoObject = new DomainObject(strRouteId);
            strRouteStatus = dmoObject.getInfo(context, "attribute[" + DomainConstants.ATTRIBUTE_ROUTE_STATUS + "]");

            // Only stop the route when it is Started
            if ("Started".equals(strRouteStatus)) {
                dmoObject.setAttributeValue(context, DomainConstants.ATTRIBUTE_ROUTE_STATUS, "Stopped") ; // Stopping the route of the state from which the object was demoted
            }
        }

        return 0;
    }

    /**
     * Starts the task on current level
     *
     * @param context the eMatrix Context object
     * @param args The arguments
     * @returns -
     * @throws Exception if the operation fails
     * @since Common V6R2009-1
     * @grade 0
     */
    public int startTasksOnCurrentLevel(Context context, String[] args) throws Exception {
        // Arguments check
        if (context == null) {
            throw new Exception("Invalid context");
        }

        String strRouteId = this.getId();
        if (strRouteId == null || "".equals(strRouteId)) {
            String[] formatArgs = {strRouteId};
            String message =  ComponentsUIUtil.getI18NString(context, "emxComponents.RouteBase.InvalidRouteId",formatArgs);
            throw new Exception(message);
        }

        final String ATTRIBUTE_CURRENT_ROUTE_NODE = PropertyUtil.getSchemaProperty(context,"attribute_CurrentRouteNode");
        final String SELECT_ATTRIBUTE_CURRENT_ROUTE_NODE = "attribute[" + ATTRIBUTE_CURRENT_ROUTE_NODE + "]";
        final String SELECT_ATTRIBUTE_ROUTE_STATUS = "attribute[" + DomainConstants.ATTRIBUTE_ROUTE_STATUS + "]";

        StringList slBusSelect = new StringList(SELECT_ATTRIBUTE_ROUTE_STATUS);
        slBusSelect.add(SELECT_ATTRIBUTE_CURRENT_ROUTE_NODE);
        slBusSelect.add(DomainConstants.SELECT_TYPE);
        slBusSelect.add(DomainConstants.SELECT_NAME);
        slBusSelect.add(DomainConstants.SELECT_REVISION);

        DomainObject dmoRoute = new DomainObject(strRouteId);
        Map mapRouteInfo = dmoRoute.getInfo(context, slBusSelect);

        String strRouteStatus = (String)mapRouteInfo.get(SELECT_ATTRIBUTE_ROUTE_STATUS);
        String strCurrentRouteNode = (String)mapRouteInfo.get(SELECT_ATTRIBUTE_CURRENT_ROUTE_NODE);
        String strType = (String)mapRouteInfo.get(DomainConstants.SELECT_TYPE);
        String strName = (String)mapRouteInfo.get(DomainConstants.SELECT_NAME);
        String strRevision = (String)mapRouteInfo.get(DomainConstants.SELECT_REVISION);

        if (!"Started".equals(strRouteStatus)) {
            return 1;
        }

        //
        // Do the due date adjustments if required. If the new task is added on the current level and for which
        // due date offset is provided, the actual due dates are to be populated on the route node relationships
        // before actual tasks are activated.
        //
        setDueDateFromOffsetForGivenLevelTasks(context, new String[]{strCurrentRouteNode});

        //
        // Call Initiate route so that this method will start any of the not started tasks on given level
        //
        String[] strMethodArguments = new String[] {
            strType, strName, strRevision, strCurrentRouteNode, "0"
        };
        int nReturnCode = ${CLASS:emxCommonInitiateRoute}.InitiateRoute(context, strMethodArguments);
        return nReturnCode;
    }

    /**
     * Method sets the due dates from due date offset for given level Route Node relationships and Inbox Tasks
     * (if they are activate).
     * (This method assumes that the tasks will be activated (if not already) immediately and hence the current
     * system time is assumed to be the task creation time.)
     *
     * @param context The Matrix Context object
     * @param args The arguments. args[0]: Level/Order for the tasks
     * @return 0 for success and 1 for error
     * @throws Exception if operation fails
     */
    public int setDueDateFromOffsetForGivenLevelTasks(Context context, String[] args) throws Exception {
        // Arguments check
        if (context == null) {
            throw new Exception("Invalid context");
        }

        String strRouteId = this.getId();
        if (strRouteId == null || "".equals(strRouteId)) {
            String[] formatArgs = {strRouteId};
            String message =  ComponentsUIUtil.getI18NString(context, "emxComponents.RouteBase.InvalidRouteId",formatArgs);
            throw new Exception(message);
        }

        String strGivenTaskLevel = args[0];
        if (strGivenTaskLevel == null || "".equals(strGivenTaskLevel)) {
            String[] formatArgs = {strGivenTaskLevel};
            String message =  ComponentsUIUtil.getI18NString(context, "emxComponents.RouteBase.InvalidTaskLevel",formatArgs);
            throw new Exception(message);
        }

        final String SELECT_ATTRIBUTE_ROUTE_STATUS = "attribute[" + DomainConstants.ATTRIBUTE_ROUTE_STATUS + "]";
        final String SELECT_REL_ATTRIBUTE_DUEDATE_OFFSET = DomainRelationship.getAttributeSelect(DomainConstants.ATTRIBUTE_DUEDATE_OFFSET);
        final String SELECT_REL_ATTRIBUTE_DATE_OFFSET_FROM = DomainRelationship.getAttributeSelect(DomainConstants.ATTRIBUTE_DATE_OFFSET_FROM);
        final String SELECT_TASK_ASSIGNEE = "from[" + DomainConstants.RELATIONSHIP_PROJECT_TASK + "].to.id";
        final String SELECT_ATTRIBUTE_ROUTE_NODE_ID = "attribute[" + DomainConstants.ATTRIBUTE_ROUTE_NODE_ID + "]";
        final String POLICY_INBOX_TASK_STATE_COMPLETE = PropertyUtil.getSchemaProperty(context, "Policy", DomainConstants.POLICY_INBOX_TASK, "state_Complete");
        final String POLICY_ROUTE_STATE_IN_PROCESS = PropertyUtil.getSchemaProperty(context, "Policy", DomainConstants.POLICY_ROUTE, "state_InProcess");
        final String POLICY_ROUTE_STATE_ASSIGNED = PropertyUtil.getSchemaProperty(context, "Policy", DomainConstants.POLICY_INBOX_TASK, "state_Assigned");
        final String SELECT_ROUTE_START_DATE = "state[" + POLICY_ROUTE_STATE_IN_PROCESS + "].actual";
        final String SELECT_TASK_START_DATE = "state[" + POLICY_ROUTE_STATE_ASSIGNED + "].actual";

        StringList slBusSelect = new StringList(SELECT_ATTRIBUTE_ROUTE_STATUS);
        slBusSelect.add(SELECT_ROUTE_START_DATE);
        DomainObject dmoRoute = new DomainObject(strRouteId);
        Map mapRouteInfo = dmoRoute.getInfo(context, slBusSelect);

        String strRouteStatus = (String)mapRouteInfo.get(SELECT_ATTRIBUTE_ROUTE_STATUS);
        String strRouteStartDate = (String)mapRouteInfo.get(SELECT_ROUTE_START_DATE);

        // Only Started routes are valid
        if (!"Started".equals(strRouteStatus)) {
            return 1;
        }

        // Find all the Route Nodes on current level
        String strRelationshipPattern = DomainConstants.RELATIONSHIP_ROUTE_NODE;
        String strTypePattern = DomainConstants.TYPE_PERSON + "," + DomainConstants.TYPE_ROUTE_TASK_USER;
        slBusSelect = new StringList();
        StringList slRelSelect = new StringList();
        final boolean GET_TO = true;
        final boolean GET_FROM = true;
        String strObjectWhere = "";
        String strRelWhere = "";

        slRelSelect.add(DomainRelationship.SELECT_ID);
        slRelSelect.add("physicalid[connection]");       
        slRelSelect.add(SELECT_REL_ATTRIBUTE_DUEDATE_OFFSET);
        slRelSelect.add(SELECT_REL_ATTRIBUTE_DATE_OFFSET_FROM);
        strRelWhere = "attribute[" + DomainConstants.ATTRIBUTE_ROUTE_SEQUENCE + "]==" + strGivenTaskLevel;
        MapList mlRouteNodes = dmoRoute.getRelatedObjects(context,
                                                        strRelationshipPattern,
                                                        strTypePattern,
                                                        slBusSelect,
                                                        slRelSelect,
                                                        !GET_TO,
                                                        GET_FROM,
                                                        (short)1,
                                                        strObjectWhere,
                                                        strRelWhere);
        // Filter all the Route Nodes for which Due Date Offset is not set
        MapList mlFilteredData = new MapList();
        Map mapRouteNode = null;
        String strDueDateOffset = null;
        StringList slRouteNodeIds = new StringList();
        for (Iterator itrRouteNodes = mlRouteNodes.iterator(); itrRouteNodes.hasNext();) {
            mapRouteNode = (Map) itrRouteNodes.next();
            strDueDateOffset = (String)mapRouteNode.get(SELECT_REL_ATTRIBUTE_DUEDATE_OFFSET);
            if (!(strDueDateOffset == null || "".equals(strDueDateOffset))) {
                mlFilteredData.add(mapRouteNode);

                // Add the ids to be used in matchlist preparation
                slRouteNodeIds.add((String)mapRouteNode.get("physicalid[connection]"));
            }
        }
        mlRouteNodes = mlFilteredData;

        // If there are no such relationships then we are done here itself
        if (mlRouteNodes.size() == 0) {
            return 0;
        }

        // Get the tasks for all these Route Nodes relationships
        strRelationshipPattern = DomainConstants.RELATIONSHIP_ROUTE_TASK;
        strTypePattern = DomainConstants.TYPE_INBOX_TASK;
        slBusSelect = new StringList();
        slRelSelect = new StringList();
        strObjectWhere = "attribute[" + DomainConstants.ATTRIBUTE_ROUTE_NODE_ID + "] matchlist \"" + StringUtil.join(slRouteNodeIds, ",") + "\" \",\" && current != const \"" + POLICY_INBOX_TASK_STATE_COMPLETE + "\"";

        strRelWhere = "";
        slBusSelect.add(DomainConstants.SELECT_ID);
        slBusSelect.add(SELECT_TASK_ASSIGNEE);
        slBusSelect.add(SELECT_ATTRIBUTE_ROUTE_NODE_ID);

        MapList mlInboxTasks = dmoRoute.getRelatedObjects(context,
                                                        strRelationshipPattern,
                                                        strTypePattern,
                                                        slBusSelect,
                                                        slRelSelect,
                                                        GET_TO,
                                                        !GET_FROM,
                                                        (short)1,
                                                        strObjectWhere,
                                                        strRelWhere);
        // Filter tasks which do not have assignees. This is due to partial tasks created by Resume Process of route
        mlFilteredData = new MapList();
        Map mapInboxTasks = null;
        String strTaskAssigneeId = null;
        for (Iterator itrInboxTasks = mlInboxTasks.iterator(); itrInboxTasks.hasNext();) {
            mapInboxTasks = (Map) itrInboxTasks.next();
            strTaskAssigneeId = (String)mapInboxTasks.get(SELECT_TASK_ASSIGNEE);
            if (!(strTaskAssigneeId == null || "".equals(strTaskAssigneeId))) {
                mlFilteredData.add(mapInboxTasks);
            }
        }
        mlInboxTasks = mlFilteredData;

        // Check if we have any Route Node relationships for which the tasks are created,
        // if so then note the task id against those Route Node relationships.
        for (Iterator itrRouteNodes = mlRouteNodes.iterator(); itrRouteNodes.hasNext();) {
            mapRouteNode = (Map) itrRouteNodes.next();
            String strRouteNodeRelId = (String)mapRouteNode.get("physicalid[connection]");

            for (Iterator itrInboxTask = mlInboxTasks.iterator(); itrInboxTask.hasNext();) {
                Map mapInboxTask = (Map) itrInboxTask.next();
                String strRouteNodeId = (String)mapInboxTask.get(SELECT_ATTRIBUTE_ROUTE_NODE_ID);

                if (strRouteNodeRelId.equals(strRouteNodeId)) {
                    mapRouteNode.put("CorrespondingTaskId", mapInboxTask.get(DomainConstants.SELECT_ID));
                    break;
                }
            }
        }

        //
        // Set due dates on the route node relationships using the due date offset.
        // If for the Route Node relationship, a task is created, then also update the due date on the task object
        //
        SimpleDateFormat dateFormat = new SimpleDateFormat (eMatrixDateFormat.getInputDateFormat(), Locale.US);
        GregorianCalendar calTaskCreationDate = new GregorianCalendar(); // Current date is task Creation date default
        GregorianCalendar calRouteStartDate = new GregorianCalendar();   // The date when route moved into In Process state
        calRouteStartDate.setTime(dateFormat.parse(strRouteStartDate));

        GregorianCalendar calOffset = new GregorianCalendar();
        String strRouteNodeRelId = null;
        String strDueDateOffsetFrom = null;
        String strCorrespondingTaskId = null;
        String strCalculatedDueDate = null;
        String strTaskStartDate = null;
        DomainObject dmoInboxTask = DomainObject.newInstance(context);

        for (Iterator itrRouteNodes = mlRouteNodes.iterator(); itrRouteNodes.hasNext();) {
            mapRouteNode = (Map) itrRouteNodes.next();

            strRouteNodeRelId = (String)mapRouteNode.get(DomainRelationship.SELECT_ID);
            strDueDateOffset = (String)mapRouteNode.get(SELECT_REL_ATTRIBUTE_DUEDATE_OFFSET);
            strDueDateOffsetFrom = (String)mapRouteNode.get(SELECT_REL_ATTRIBUTE_DATE_OFFSET_FROM);
            strCorrespondingTaskId = (String)mapRouteNode.get("CorrespondingTaskId");

            if ("Route Start Date".equals(strDueDateOffsetFrom)) {
                calOffset = (GregorianCalendar) calRouteStartDate.clone();
            }
            else if ("Task Create Date".equals(strDueDateOffsetFrom)) {
                // If the task is not yet created then take current date as Task Start Date,
                // else find the date when task has gone into Assigned state
                if (strCorrespondingTaskId != null && !"".equals(strCorrespondingTaskId) && !"null".equals(strCorrespondingTaskId)) {
                    dmoInboxTask.setId(strCorrespondingTaskId);
                    strTaskStartDate = dmoInboxTask.getInfo(context, SELECT_TASK_START_DATE);
                    calTaskCreationDate.setTime(dateFormat.parse(strTaskStartDate));
                }

                calOffset = (GregorianCalendar) calTaskCreationDate.clone();
            }
            else {
                continue;
            }

            // Calculate new date
            calOffset.add(Calendar.DATE, Integer.parseInt(strDueDateOffset));
            strCalculatedDueDate = dateFormat.format(calOffset.getTime());

            // Update the relationship attribute.
            DomainRelationship.setAttributeValue(context, strRouteNodeRelId, DomainConstants.ATTRIBUTE_SCHEDULED_COMPLETION_DATE, strCalculatedDueDate);

            // Check if we need to set this value on the task object also
            if (strCorrespondingTaskId != null && !"".equals(strCorrespondingTaskId) && !"null".equals(strCorrespondingTaskId)) {
                dmoInboxTask.setId(strCorrespondingTaskId);
                dmoInboxTask.setAttributeValue(context, DomainConstants.ATTRIBUTE_SCHEDULED_COMPLETION_DATE, strCalculatedDueDate);
            }
        }

        return 0;
    }


    /**
     * canManageRouteApprovals - This method is used to determine if
     *             the context user can Manage Route Approvals.
     * @param context the eMatrix <code>Context</code> object
     * @param args empty
     * @return boolean
     * @throws Exception if the operation fails
     * @since Common V6R2009-1
     * @grade 0
     */
    public boolean canManageRouteApprovals(Context context, String[] args) throws Exception {
        HashMap programMap = (HashMap) JPO.unpackArgs(args);
        String objectId    = (String) programMap.get(OBJECT_ID);

        if(objectId == null || objectId.equals("")) {
            return false;
        }
        if (isRoutedObjectInLastState(context, args)) {
            return false;
        }
        return true;
    }

    /** added for the bug 341480
     * routeDeleteCheck - gets the list of Task Escalation Message objects connected to the context Route
     * @param context the eMatrix <code>Context</code> object
     * @param args holds the following input arguments:
     *        0 - Route Object Id
     * @returns int
     * @throws Exception if the operation fails
    * @since Common 10.7.HF4
     */
//added for the bug 341480 begin
public int routeDeleteCheck(Context context, String[] args)
        throws Exception
    {
        try
        {
           StringList objectSelects=new StringList(1);
           objectSelects.addElement(SELECT_ID);
           String typePattern=PropertyUtil.getSchemaProperty(context,DomainSymbolicConstants.SYMBOLIC_relationship_TaskEscalationMessage);
           String relPattern=PropertyUtil.getSchemaProperty(context,DomainSymbolicConstants.SYMBOLIC_type_TaskEscalationMessage);
           DomainObject routeObj=new DomainObject(args[0]);
           MapList list=routeObj.getRelatedObjects(context,
                                 typePattern,
                                 relPattern,
                                 objectSelects,
                                 null,
                                 false,
                                 true,
                                 (short)1,
                                 null,
                                 null);
           String objectIds[]=new String[list.size()];

           for(int i=0;i<list.size();i++)
           {
              objectIds[i]=(String)((Map)list.get(i)).get(SELECT_ID);
           }
           DomainObject.deleteObjects(context,objectIds);
        }
        catch (Exception ex)
        {
            throw ex;
        }
        return 0;
  }
//added for the bug 341480 End

    /**
     * The trigger will check if this is the state based route,
     * then the routed object must be in correct state for route to be started
     *
     * @param context The Matrix Context object
     * @param args The arguments, args[0]: Route object id
     * @return 0 for success 1 for failure
     * @throws Exception if operation fails
     */
    public int validateStateBasedRouteStarting (Context context, String[] args) throws Exception {
        try {
            if (context == null) {
                throw new Exception("Invalid context");
            }
            if (args == null) {
                throw new Exception("Invalid arguments");
            }

            String strObjectId = args[0];
            if (strObjectId == null || "".equals(strObjectId.trim()) || "null".equals(strObjectId.trim())) {
                String[] formatArgs = {strObjectId};
                String message =  ComponentsUIUtil.getI18NString(context, "emxComponents.RouteBase.InvalidObjectId",formatArgs);
                throw new Exception(message);
            }

            final String SELECT_REL_ATTRIBUTE_ROUTE_BASE_STATE = DomainRelationship.getAttributeSelect(DomainConstants.ATTRIBUTE_ROUTE_BASE_STATE);
            final String SELECT_REL_ATTRIBUTE_ROUTE_BASE_POLICY = DomainRelationship.getAttributeSelect(DomainConstants.ATTRIBUTE_ROUTE_BASE_POLICY);
            i18nNow loc = new i18nNow();
            final String STRING_ROUTE_CANNOT_BE_STARTED = loc.GetString("emxComponentsStringResource", context.getSession().getLanguage(), "emxComponents.Route.AlertCannotStartStateBasedRoute");

            String relationshipPattern = DomainConstants.RELATIONSHIP_OBJECT_ROUTE;
            String typePattern = "*";
            StringList objectSelects = new StringList();
            StringList relationshipSelects = new StringList();
            final boolean GET_TO = true;
            final boolean GET_FROM = false;
            short recurseToLevel = (short)1;
            String objectWhere = "";
            String relationshipWhere = "";

            objectSelects.addElement(DomainConstants.SELECT_CURRENT);
            objectSelects.addElement(DomainConstants.SELECT_POLICY);
            objectSelects.addElement(DomainConstants.SELECT_TYPE);

            relationshipSelects.addElement(SELECT_REL_ATTRIBUTE_ROUTE_BASE_POLICY);
            relationshipSelects.addElement(SELECT_REL_ATTRIBUTE_ROUTE_BASE_STATE);

            // Find the routed objects
            DomainObject dmoRoute = DomainObject.newInstance(context, strObjectId);
            MapList mlRoutedObjects = dmoRoute.getRelatedObjects(context,
                                                                    relationshipPattern,
                                                                    typePattern,
                                                                    objectSelects,
                                                                    relationshipSelects,
                                                                    GET_TO,
                                                                    !GET_FROM,
                                                                    recurseToLevel,
                                                                    objectWhere,
                                                                    relationshipWhere);

            Map mapRoutedObject = null;
            String strCurrentState = "";
            String strCurrentPolicy = "";
            String strRouteBaseState = "";
            String strRouteBasePolicy = "";
            String strType = "";
            for (Iterator itrRoutedObjects = mlRoutedObjects.iterator(); itrRoutedObjects.hasNext();) {
                mapRoutedObject = (Map) itrRoutedObjects.next();

                strCurrentState = (String)mapRoutedObject.get(DomainConstants.SELECT_CURRENT);
                strCurrentPolicy = (String)mapRoutedObject.get(DomainConstants.SELECT_POLICY);
                strRouteBaseState = (String)mapRoutedObject.get(SELECT_REL_ATTRIBUTE_ROUTE_BASE_STATE);
                strRouteBasePolicy = (String)mapRoutedObject.get(SELECT_REL_ATTRIBUTE_ROUTE_BASE_POLICY);

                // Is it state based route?
                if (strRouteBasePolicy != null && !"".equals(strRouteBasePolicy) && !"null".equals(strRouteBasePolicy)
                        && strRouteBaseState != null && !"".equals(strRouteBaseState) && !"null".equals(strRouteBaseState) && !"Ad Hoc".equals(strRouteBaseState)) {

                    // Symbolic names -> real names
                    strRouteBasePolicy = PropertyUtil.getSchemaProperty(context, strRouteBasePolicy);
                    strRouteBaseState = PropertyUtil.getSchemaProperty(context, "Policy", strRouteBasePolicy, strRouteBaseState);

                    // Is the current state of the object is correct?
                    if (!(strRouteBasePolicy.equals(strCurrentPolicy) && strRouteBaseState.equals(strCurrentState))) {
                        strType = (String)mapRoutedObject.get(DomainConstants.SELECT_TYPE);
                        strType = i18nNow.getAdminI18NString("Type", strType, context.getSession().getLanguage());
                        ${CLASS:emxContextUtil}.mqlWarning(context, FrameworkUtil.findAndReplace(STRING_ROUTE_CANNOT_BE_STARTED, "<type name>", strType));
                        return 1;
                    }
                }
            }

            return 0;//Validation success
        }
        catch(Exception exception) {
            ${CLASS:emxContextUtil}.mqlError(context, exception.getMessage());
            return 1;//Validation failure
        }
    }

    /**
     * Method returns the value for State Condition column for APPObjectRouteSummary table
     *
     * @param context The Matrix Context object
     * @param args The packed arguments
     * @return Vector containing column values
     * @throws Exception if operation fails
     */
    public Vector getStateCondition(Context context,String[] args) throws Exception
    {
        try
        {
            HashMap programMap = (HashMap) JPO.unpackArgs(args);

            Map paramList      = (Map)programMap.get("paramList");
            String strObjectId = (String)paramList.get(OBJECT_ID);
			if(UIUtil.isNotNullAndNotEmpty(strObjectId))
    		{
            	strObjectId = FrameworkUtil.getOIDfromPID(context, strObjectId);
    		}
            MapList mlRoutes = (MapList)programMap.get("objectList");

            final String SELECT_REL_ATTRIBUTE_ROUTE_BASE_POLICY = DomainRelationship.getAttributeSelect(ATTRIBUTE_ROUTE_BASE_POLICY);
            final String SELECT_REL_ATTRIBUTE_ROUTE_BASE_STATE = DomainRelationship.getAttributeSelect(ATTRIBUTE_ROUTE_BASE_STATE);

            String languageStr = (String)paramList.get("languageStr");
            final String STRING_NONE = EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource", new Locale(languageStr),"emxComponents.Common.None");

            StringList slRelSelect = new StringList();
            slRelSelect.addElement(SELECT_REL_ATTRIBUTE_ROUTE_BASE_POLICY);
            slRelSelect.addElement(SELECT_REL_ATTRIBUTE_ROUTE_BASE_STATE);

            Vector vecStateConditions = new Vector();
            String strRouteBaseState = null;
            String strRouteBasePolicy = null;
            String strRoutedObjectId = null;
            String strRouteId = null;
            String strObjectType = null;
            String strBusWhere = "id=='" + strObjectId + "'";
            DomainObject dmoRoute = DomainObject.newInstance(context);

            DomainObject dmoObject = DomainObject.newInstance(context, strObjectId);
            strObjectType = dmoObject.getInfo(context, DomainConstants.SELECT_TYPE);

            Map mapRouteInfo = null;
            MapList mlStateBlockInfo = null;
            Map mapStateBlockInfo = null;
            String strStateCondition = null;
            for(Iterator itrRoutes = mlRoutes.iterator();itrRoutes.hasNext();)
            {
                mapRouteInfo = (Map) itrRoutes.next();

                // Preapare route object
                strRouteId = (String)mapRouteInfo.get(DomainConstants.SELECT_ID);
                dmoRoute.setId(strRouteId);

                // Find information from route
                mlStateBlockInfo = dmoRoute.getRelatedObjects(context, DomainConstants.RELATIONSHIP_OBJECT_ROUTE, strObjectType, null, slRelSelect, true, false, (short)1, strBusWhere, null);
                mapStateBlockInfo = (Map)mlStateBlockInfo.get(0);

                strRouteBasePolicy = (String)mapStateBlockInfo.get(SELECT_REL_ATTRIBUTE_ROUTE_BASE_POLICY);
                strRouteBaseState = (String)mapStateBlockInfo.get(SELECT_REL_ATTRIBUTE_ROUTE_BASE_STATE);
                strStateCondition = null;

                if (strRouteBasePolicy != null && !"".equals(strRouteBasePolicy) && !"null".equals(strRouteBasePolicy)) {
                    // Symbolic -> Real
                    strRouteBasePolicy = PropertyUtil.getSchemaProperty(context, strRouteBasePolicy);

                    if (strRouteBasePolicy != null && !"".equals(strRouteBasePolicy) && !"null".equals(strRouteBasePolicy)) {
                        if (strRouteBaseState != null && !"".equals(strRouteBaseState) && !"null".equals(strRouteBaseState)) {
                            // Symbolic to Real
                            strRouteBaseState = PropertyUtil.getSchemaProperty(context, "Policy", strRouteBasePolicy, strRouteBaseState);
                            if (strRouteBaseState != null && !"".equals(strRouteBaseState) && !"null".equals(strRouteBaseState)) {
                                strStateCondition = i18nNow.getStateI18NString(strRouteBasePolicy, strRouteBaseState, languageStr);
                            }
                        }
                    }
                }

                if (strStateCondition == null) {
                    vecStateConditions.add(STRING_NONE);
                }
                else {
                    vecStateConditions.add(strStateCondition);
                }
            }//for

            return vecStateConditions;
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }

    /**
     * Method checks if the routed object is in last state or not. This method
     * is supposed to be used to determine the access for commands for route creation.
     *
     * @param context The Matrix context object
     * @param args The packed program map
     * @return true if route object is in last state, false if the route object is not there or route object
     *         is there but not in last state.
     * @throws Exception if operation fails.
     */
    public static boolean isRoutedObjectInLastState(Context context, String args[]) throws Exception
    {
        HashMap programMap         = (HashMap) JPO.unpackArgs(args);
        DomainObject objectGeneral = DomainObject.newInstance(context);
        String strObjectId            = (String) programMap.get(OBJECT_ID);

        if (strObjectId == null || "".equals(strObjectId) || "null".equals(strObjectId))
        {
           return false;
        }

        DomainObject dmoRoutedObject = DomainObject.newInstance(context, strObjectId);
        StringList slBusSelect = new StringList(DomainConstants.SELECT_CURRENT);
        //slBusSelect.add(DomainObject.SELECT_POLICY);
        Map mapRoutedObjectInfo = dmoRoutedObject.getInfo(context, slBusSelect);

        String strCurrentState = (String)mapRoutedObjectInfo.get(DomainConstants.SELECT_CURRENT);
        //String strPolicy = (String)mapRoutedObjectInfo.get(DomainObject.SELECT_POLICY);

        StateList stateList = dmoRoutedObject.getStates(context);
        int lastStateIndex = stateList.size() - 1;
        if(lastStateIndex == 0)
        {
            return false;
        }
        State lastState = (State)stateList.get(lastStateIndex);
        if (lastState.getName().equals(strCurrentState)) {
            return true;
        }

        return false;
    }

    /**
     * Method checks if user can set task escalation. This method
     * is supposed to be used to determine the access for command.
     *
     * @param context The Matrix context object
     * @param args The packed program map
     * @return false if route object is in last state, else true
     * @throws Exception if operation fails.
     */
    public static boolean canSetTaskEscalation(Context context, String args[]) throws Exception
    {
        HashMap programMap         = (HashMap) JPO.unpackArgs(args);
        DomainObject objectGeneral = DomainObject.newInstance(context);
        String strObjectId            = (String) programMap.get(OBJECT_ID);

        if (strObjectId == null || "".equals(strObjectId) || "null".equals(strObjectId))
        {
           return true;
        }

        if (isRoutedObjectInLastState(context, args)) {
            return false;
        }
        return true;
    }

    /**
     * Method checks if user can start/resume route. This method
     * is supposed to be used to determine the access for command.
     *
     * @param context The Matrix context object
     * @param args The packed program map
     * @return false if route object is in last state, else true
     * @throws Exception if operation fails.
     */
    public static boolean canStartOrResume(Context context, String args[]) throws Exception
    {
        HashMap programMap         = (HashMap) JPO.unpackArgs(args);
        DomainObject objectGeneral = DomainObject.newInstance(context);
        String strObjectId            = (String) programMap.get(OBJECT_ID);

        if (strObjectId == null || "".equals(strObjectId) || "null".equals(strObjectId))
        {
           return true;
        }
        if (isRoutedObjectInLastState(context, args)) {
            return false;
        }
        return true;
    }


    /** added for the Bug 360573
     * routeDeleteCheck - gets the list of Discussion objects connected to the context Route
     * @param context the eMatrix <code>Context</code> object
     * @param args holds the following input arguments:
     *        0 - Route Object Id
     * @returns int
     * @throws Exception if the operation fails
     * @since Common V6R2010x
     */

       public int routeDiscussionDeleteCheck(Context context, String[] args)
       throws Exception
       {
          try
            {
                StringList objectSelects=new StringList(1);
                objectSelects.addElement(SELECT_ID);
                String typePattern=PropertyUtil.getSchemaProperty(context,DomainSymbolicConstants.SYMBOLIC_relationship_Thread);
                String relPattern=PropertyUtil.getSchemaProperty(context,DomainSymbolicConstants.SYMBOLIC_type_Thread);
                DomainObject routeObj=new DomainObject(args[0]);
                MapList list=routeObj.getRelatedObjects(context,
                                 typePattern,
                                 relPattern,
                                 objectSelects,
                                 null,
                                 false,
                                 true,
                                 (short)1,
                                 null,
                                 null);
                String objectIds[]=new String[list.size()];

              for(int i=0;i<list.size();i++)
                {
                   objectIds[i]=(String)((Map)list.get(i)).get(SELECT_ID);
                }
                 DomainObject.deleteObjects(context,objectIds);
           }
            catch (Exception ex)
            {
              throw ex;
            }
          return 0;
        }
       /**
        *  When the route route is completed system has to revoke the access granted by Route Access Grantor on Route content (for all grantees)
        *  Access granted by other than Route Access Grantor will remain same.
        */

       public void revokeAccessGrantedByRouteAccessGrantorOnRouteContent(Context context, String[] args) throws Exception {
           Route route = new Route(args[0]);
           /*
            * If current state is not equlas to complete do not revoke the access.
            */
           
           StringList objectSelects = new StringList(); 
           objectSelects.add(SELECT_CURRENT);
           objectSelects.add(SELECT_NAME);
           objectSelects.add(SELECT_ID);
           objectSelects.add(SELECT_TYPE);
           objectSelects.add("from["+RELATIONSHIP_ROUTE_NODE+"].to.id");
           objectSelects.add("from["+RELATIONSHIP_ROUTE_NODE+"].to.name");
           objectSelects.add("from["+RELATIONSHIP_ROUTE_NODE+"].to.type");
           objectSelects.add("from["+RELATIONSHIP_ROUTE_NODE+"].to.physicalid");	
           
           DomainConstants.MULTI_VALUE_LIST.add("from["+RELATIONSHIP_ROUTE_NODE+"].to.id");
           DomainConstants.MULTI_VALUE_LIST.add("from["+RELATIONSHIP_ROUTE_NODE+"].to.name");
           DomainConstants.MULTI_VALUE_LIST.add("from["+RELATIONSHIP_ROUTE_NODE+"].to.type");
           DomainConstants.MULTI_VALUE_LIST.add("from["+RELATIONSHIP_ROUTE_NODE+"].to.physicalid");	
           
           Map objMap = route.getInfo(context, objectSelects);
           String currentState = (String) objMap.get(SELECT_CURRENT);
           if(STATE_ROUTE_DEFINE.equals(currentState) || STATE_ROUTE_IN_PROCESS.equals(currentState))
               return;
           
           StringList routeNodeList = (StringList) objMap.get("from["+RELATIONSHIP_ROUTE_NODE+"].to.id");
           StringList routeNodeName = (StringList) objMap.get("from["+RELATIONSHIP_ROUTE_NODE+"].to.name");
           StringList routeNodeType = (StringList) objMap.get("from["+RELATIONSHIP_ROUTE_NODE+"].to.type");
           
           String fromName = (String) objMap.get(SELECT_NAME);
           String fromType = (String) objMap.get(SELECT_TYPE);
           String fromId = (String) objMap.get(SELECT_ID);	
           
           for(int i = 0;i<routeNodeList.size();i++){
        	   String toId = (String) routeNodeList.get(i);
        	   String toType = (String) routeNodeType.get(i);
        	   String toName = (String) routeNodeName.get(i);
        	   Route.revokeAccessOnRouteForMembers(context,  toName,  toType, toId , fromId, fromType, fromName);
        	   
           }   
       }

       public String routeScopeSelect(Context context, String[] args) throws Exception
       {
           HashMap programMap         = (HashMap) JPO.unpackArgs(args);
           Map requestMap             = (Map) programMap.get("requestMap");
           Map paramMap               = (Map) programMap.get("paramMap");
           String strLanguage         = (String)requestMap.get("languageStr");
           String supplierOrgId       = (String) requestMap.get("supplierOrgId");
           String relatedObjectId     = (String) requestMap.get(OBJECT_ID);
           String mode                = (String) requestMap.get("mode");
           StringBuffer sb            = new StringBuffer();
           i18nNow i18nnow            = new i18nNow();
           String scopeChecking       = EnoviaResourceBundle.getProperty(context,"emxComponentsRoutes.RouteUse");
           String strSelectScope      = i18nNow.getI18nString("emxComponents.CreateRoute.SelectScope","emxComponentsStringResource",strLanguage);
           boolean isSupplierReview   = (!"null".equals(supplierOrgId) && supplierOrgId != null && supplierOrgId.trim().length() > 0)?true:false;
           String sTypeName           = "";
           boolean bTeam              = FrameworkUtil.isSuiteRegistered(context,"featureVersionTeamCentral",false,null,null);
           boolean bProgram           = FrameworkUtil.isSuiteRegistered(context,"appVersionProgramCentral",false,null,null);

           if(mode.equalsIgnoreCase("create"))
           {
           if(isSupplierReview) {

               }else {
                 boolean boolHostCompanyEmployee=false;
                 if(scopeChecking.equals("Enterprise")){
                   sb.append("<input type=\"radio\" name=\"selscope\" value=\"All\" checked = \"checked\" ></input>");
                String strAll = i18nNow.getI18nString("emxComponents.Common.All","emxComponentsStringResource",strLanguage);
                	 sb.append(strAll);
                sb.append("<br></br>");
                 }

                 sb.append("<input type=\"radio\" name=\"selscope\" value=\"Organization\"");
                 if(scopeChecking.equals("Exchange")|| !boolHostCompanyEmployee){
                     sb.append(" checked = \"checked\" ></input>");
                 }
                 String strOrganization = i18nNow.getI18nString("emxComponents.Common.Organization","emxComponentsStringResource",strLanguage);
                 sb.append(strOrganization);
                 sb.append("<br></br>");
                 if( relatedObjectId != null ) {
                     DomainObject boProject = new DomainObject(relatedObjectId);
                     sTypeName = boProject.getInfo(context,"type");
                     String sName = XSSUtil.encodeForHTML(context, boProject.getInfo(context,"name"));
                     if(sTypeName.equals(DomainConstants.TYPE_WORKSPACE) ||
                             com.matrixone.apps.domain.util.mxType.isOfParentType(context,sTypeName,DomainConstants.TYPE_WORKSPACE_VAULT) || //Modified:16-Mar-09:wqy:R207:PRG Bug 370839
                             sTypeName.equals(DomainConstants.TYPE_PROJECT_SPACE) || mxType.isOfParentType(context,sTypeName,DomainConstants.TYPE_PROJECT_SPACE)) //Modified to handle Sub Type
                  {

                         sb.append("<input type=\"radio\" name=\"selscope\" value=\"ScopeName\" checked = \"checked\" ></input>");
                         sb.append(XSSUtil.encodeForHTML(context,sName));
                         sb.append("&#160;&#160;&#160;");
                         sb.append("Type:");
                         sb.append(EnoviaResourceBundle.getTypeI18NString(context, sTypeName, context.getLocale().toString()));
                         sb.append("<input type=\"hidden\" name=\"folderId\" value=\"");
                         sb.append(XSSUtil.encodeForHTMLAttribute(context,relatedObjectId));
                         sb.append("\"></input>");
                         sb.append("<input type=\"hidden\" name=\"txtWSFolder\" value=\"");
                         sb.append(XSSUtil.encodeForHTMLAttribute(context,sName));
                         sb.append("\"></input>");

                     }else if(sTypeName.equalsIgnoreCase(DomainConstants.TYPE_INBOX_TASK)) {
                       String selectWorkspaceID  ="from["+DomainConstants.RELATIONSHIP_ROUTE_TASK+"].to.to["+DomainConstants.RELATIONSHIP_ROUTE_SCOPE+"].from.id";
                       String prjId              =boProject.getInfo(context,selectWorkspaceID);
                       if(prjId != null && !prjId.equals("")) {
                         DomainObject wkspaceObject= DomainObject.newInstance(context, prjId);
                         String Type = wkspaceObject.getType(context);
                           sName=XSSUtil.encodeForHTML(context, wkspaceObject.getName(context));
                         
                         sb.append("<input type=\"radio\" name=\"selscope\" value=\"ScopeName\" ></input>");
                         sb.append(XSSUtil.encodeForHTML(context,sName));
                         sb.append("&#160;&#160;&#160;");
                         sb.append("Type:");
                         sb.append(Type);
                         sb.append("<input type=\"hidden\" name=\"folderId\" value=\"");
                         sb.append(XSSUtil.encodeForHTMLAttribute(context,prjId));
                         sb.append("\"></input>");
                         sb.append("<input type=\"hidden\" name=\"txtWSFolder\" value=\"");
                         sb.append(XSSUtil.encodeForHTMLAttribute(context,sName));
                         sb.append("\"></input>");
                       }else {
                         relatedObjectId = null;
                       }
                     }else if(sTypeName.equalsIgnoreCase(DomainConstants.TYPE_TASK)) {
                       StringList busSelects = new StringList();
                       busSelects.addElement(DomainConstants.SELECT_ID);
                       busSelects.addElement(DomainConstants.SELECT_NAME);
                       busSelects.addElement(DomainConstants.SELECT_TYPE);

                       com.matrixone.apps.common.Task task = new com.matrixone.apps.common.Task();
                       task.setId(relatedObjectId);

                       Map taskMap = task.getProject(context,busSelects);
                       String prjId =(String)taskMap.get(DomainConstants.SELECT_ID);
                       String prjName =(String)taskMap.get(DomainConstants.SELECT_NAME);
                       String prjType =(String)taskMap.get(DomainConstants.SELECT_TYPE);
                       String strPrjType = i18nNow.getI18nString(prjType,"emxComponentsStringResource",strLanguage);
                       String strType = i18nNow.getI18nString("emxComponents.Common.Type","emxComponentsStringResource",strLanguage);

                       if(prjType.equals(DomainConstants.TYPE_PROJECT_SPACE) || mxType.isOfParentType(context,prjType,DomainConstants.TYPE_PROJECT_SPACE)) {//Modified to handle Sub Type
                       sb.append("<input type=\"radio\" name=\"selscope\" value=\"ScopeName\" checked = \"checked\"></input> ");
                       sb.append(XSSUtil.encodeForHTML(context,prjName));
                       sb.append(XSSUtil.encodeForHTML(context,strType));
                       sb.append(":");
                       sb.append(XSSUtil.encodeForHTML(context,strPrjType));
                       sb.append("<input type=\"hidden\" name=\"folderId\" value=\"");
                       sb.append(XSSUtil.encodeForHTMLAttribute(context,prjId));
                       sb.append("\"></input>");
                       sb.append("<input type=\"hidden\" name=\"txtWSFolder\" value=\"");
                       sb.append(XSSUtil.encodeForHTMLAttribute(context,prjName));
                       sb.append("\"></input>");

                       }else {

                       sb.append("<input type=\"radio\" name=\"selscope\" value=\"ScopeName\" ></input>");
                       sb.append("<input type=\"text\" readonly=\"readonly\" name=\"txtWSFolder\" value=\"\" placeholder=\"");
                       sb.append(XSSUtil.encodeForXML(context, strSelectScope));
                       sb.append("\" ></input>");
                       sb.append("<input type=\"button\" name=\"btnScope\" value=\"...\" onclick= \"showRouteCreateWSChooser()\" ></input>");
                       sb.append("<input type=\"hidden\" name=\"folderId\" value=\"\" ></input>");
                       }
                     } else if(sTypeName.equals(DomainConstants.TYPE_DOCUMENT) ||
                               sTypeName.equals(DomainConstants.TYPE_PACKAGE) ||
                                 sTypeName.equals(DomainConstants.TYPE_RTS_QUOTATION) ||
                                   sTypeName.equals(DomainConstants.TYPE_REQUEST_TO_SUPPLIER) ||
                                       sTypeName.equals(DomainConstants.TYPE_PART))
                       {
                       String sId = "";
                       String sName1 = "";

                       DomainObject doObj=new DomainObject(relatedObjectId);
                           doObj.open(context);
                           BusinessObject boWorkspace = ComponentsUtil.getConnectedObject(context,doObj,"","Workspace Vault",true,false);
                       // If project Id is not null then the page is from workspace
                       if(boWorkspace!=null){
                         sId   = boWorkspace.getObjectId();
                         boWorkspace.open(context);
                         sName1 = XSSUtil.encodeForHTML(context, boWorkspace.getName());
                         boWorkspace.close(context);
                       }
                       StringList objSelects = new StringList();
                       objSelects.addElement(DomainConstants.SELECT_ID);
                       objSelects.addElement(DomainConstants.SELECT_NAME);

                       StringList relSelects = new StringList();
                       short level = 1;

                           //MapList scopeList = doObj.getRelatedObjects(context, DomainConstants.RELATIONSHIP_VAULTED_OBJECTS,TYPE_PROJECT_VAULT, objSelects, relSelects, true, false, level, "", "");
                           MapList scopeList = doObj.getRelatedObjects(context, "*",TYPE_PROJECT_VAULT, objSelects, relSelects, true, false, level, "", "");

                       if (scopeList.size()!=0) {
                       sb.append("<input type=\"radio\" name=\"selscope\" value=\"ScopeName\" checked = \"checked\" ></input>");
                       sb.append("<select name=\"txtWSFolder\" onChange=\"javascript:setScopeId()\">");

                         Map workspaceMap = null;
                         String scopeIds  = "";
                         String scopeNames = "";
                         Iterator scopeListItr = scopeList.iterator();

                         // get a list of workspace folder id's for the member
                         StringList scopeIdList = new StringList();
                         while(scopeListItr.hasNext()) {
                           workspaceMap = (Map)scopeListItr.next();
                           scopeIds = (String)workspaceMap.get(DomainConstants.SELECT_ID);
                           scopeNames = (String)workspaceMap.get(DomainConstants.SELECT_NAME);
               				sb.append("<option value=\"");
               				sb.append(XSSUtil.encodeForHTMLAttribute(context,scopeIds));
               				sb.append("\" selected=\"");
               				sb.append(scopeNames.equals(sName1)? "selected":"");
               				sb.append("\">");
               				sb.append(XSSUtil.encodeForHTML(context,scopeNames));
               				sb.append("</option>");
                         }
						 sb.append("</select>");
                         sb.append("<input type=\"hidden\" name=\"folderId\" value=\"");
                         sb.append(XSSUtil.encodeForHTMLAttribute(context,sId));
                         sb.append("\"></input>");

                       }

                       sb.append("<input type=\"hidden\" name=\"contentId\" value=\"");
                       sb.append(XSSUtil.encodeForHTMLAttribute(context,relatedObjectId));
                       sb.append("\"></input>");

                     }else{
                       relatedObjectId = null;
                     }
                   }
                 if((bTeam || bProgram) && (relatedObjectId == null || "null".equals(relatedObjectId) || "".equals(relatedObjectId) ) ) {

               sb.append("<input type=\"radio\" name=\"selscope\" value=\"ScopeName\" ></input>");
               sb.append("<input type=\"text\" readonly=\"readonly\" name=\"txtWSFolder\" value=\"\" placeholder=\"");
			   sb.append(XSSUtil.encodeForXML(context, strSelectScope));
               sb.append("\"></input>");
               sb.append("<input type=\"button\" name=\"btnScope\" value=\"...\" onclick= \"showRouteCreateWSChooser()\"></input>");
               sb.append("<input type=\"hidden\" name=\"folderId\" value=\"\" ></input>");

                 }
                 }
           }
           if(mode.equalsIgnoreCase("view") || mode.equalsIgnoreCase("edit"))
           {
               Route boRoute = (Route)DomainObject.newInstance(context, DomainConstants.TYPE_ROUTE);
               boRoute.setId(relatedObjectId);
               SelectList selectStmts = new SelectList();
               String sAttrRestrictMembers = PropertyUtil.getSchemaProperty(context, "attribute_RestrictMembers" );
               String SELECT_RESTRICT_MEMBERS = DomainObject.getAttributeSelect(sAttrRestrictMembers);
               selectStmts.addElement(SELECT_RESTRICT_MEMBERS);
               Map resultMap = boRoute.getInfo(context, selectStmts);
               String restrictMembers = (String) resultMap.get(SELECT_RESTRICT_MEMBERS);
               String scopeName = "";
               sTypeName = boRoute.getInfo(context,"type");
               if(sTypeName.equals(DomainConstants.TYPE_ROUTE_TEMPLATE) && mode.equalsIgnoreCase("edit"))
               {
                   sb.append("<input type=\"radio\" name=\"scope\" value=\"All\" " );
                   sb.append(restrictMembers.equals("All")?"checked":"");
                   sb.append(">");
                   sb.append(i18nNow.getI18nString("emxComponents.Common.All","emxComponentsStringResource",strLanguage));
                   sb.append("<br><input type=\"radio\" name=\"scope\" value=\"Organization\" ");
                   sb.append(restrictMembers.equals("Organization")?"checked":"");
                   sb.append(" >");
                   sb.append(i18nNow.getI18nString("emxComponents.Common.Organization","emxComponentsStringResource",strLanguage));
               }
               else{
                   if(restrictMembers.equalsIgnoreCase("All") || restrictMembers.equalsIgnoreCase("Organization") )
                   {
                       scopeName = restrictMembers;
                       if("All".equalsIgnoreCase(scopeName))
                           scopeName= EnoviaResourceBundle.getProperty(context, "emxComponentsStringResource",new Locale(strLanguage),"emxComponents.Common.All");
                       else if("Organization".equalsIgnoreCase(scopeName))
                        	   scopeName= EnoviaResourceBundle.getProperty(context,
                               		"emxComponentsStringResource", new Locale(strLanguage),"emxComponents.Common.Organization");
                   }
                   else
                   {
                       Map scopeObjInfo = boRoute.getScopeObjectTypeNameRevision(context, restrictMembers);
                       scopeName = (String) scopeObjInfo.get(DomainConstants.SELECT_NAME);
                   }
                   sb.append(scopeName);
               }
           }
           return sb.toString();
       }

       @com.matrixone.apps.framework.ui.CreateProcessCallable
       @com.matrixone.apps.framework.ui.PostProcessCallable
       public HashMap createRouteProcess(Context context, String[] args) throws Exception
       {
           HashMap requestMap         = (HashMap) JPO.unpackArgs(args);
           String name = (String) requestMap.get("Name");
           System.out.println("name=======>"+name);
           
           
           
           String autoNameCheck = (String) requestMap.get("autoNameCheck");
           System.out.println("autoNameCheck=======>"+autoNameCheck);
           
           
           String AutoNameSeries = (String) requestMap.get("AutoNameSeries");
           System.out.println("AutoNameSeries=======>"+AutoNameSeries);
           
           String vault = (String) requestMap.get("Vault");
           System.out.println("Vault=======>"+vault);
           
           String revision = (String) requestMap.get("Revision");
           System.out.println("revision=======>"+revision);
           
           String strLanguage         = (String)requestMap.get("languageStr");
           System.out.println("strLanguage=======>"+strLanguage);
           
           String objectId            = (String)requestMap.get(OBJECT_ID);
           System.out.println("objectId=======>"+objectId);
           
           String routeId             = "";
           
           String  restrictMembers    = (String)requestMap.get("selscope");
           System.out.println("restrictMembers=======>"+restrictMembers);
           
           String  selscopeId         = "";
           String scopeName           = "";
           String strTypeName         = "";
           boolean WSNotSelected       = true;
           boolean isCompletedTask       = false;
           Hashtable routeDetails     =  new Hashtable();
           HashMap resultMap          =  new HashMap();
           i18nNow i18nnow            =  new i18nNow();
           String errorMessage        = EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource", new Locale(strLanguage),"emxComponents.CreateRoute.OnCompleteTaskError");
           com.matrixone.apps.common.Person person = (com.matrixone.apps.common.Person)DomainObject.newInstance(context,DomainConstants.TYPE_PERSON);
           Route route = (Route)DomainObject.newInstance(context,DomainConstants.TYPE_ROUTE);
           if(UIUtil.isNullOrEmpty(revision)){
                   revision = new Policy(DomainConstants.POLICY_ROUTE).getSequence(context);
                   System.out.println("revision=======>"+revision);
            }


           BusinessObject routeObject = new BusinessObject(DomainConstants.TYPE_ROUTE,name,revision,vault);
           boolean isExists = routeObject.exists(context);
           if(isExists){
               resultMap.put("ErrorMessage", i18nNow.getTypeI18NString(DomainConstants.TYPE_ROUTE, strLanguage) + " " + name + " " + EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource",new Locale(strLanguage),"emxComponents.Common.AlreadyExists"));
           }else{
             if ("true".equalsIgnoreCase(autoNameCheck)|| UIUtil.isNullOrEmpty(name)){
                 String typeAlias = FrameworkUtil.getAliasForAdmin(context, "type", DomainConstants.TYPE_ROUTE, true);
                 
                 System.out.println("typeAlias=======>"+typeAlias);
                 
                 
                 String policyAlias = FrameworkUtil.getAliasForAdmin(context, "policy", DomainConstants.POLICY_ROUTE, true);
                 
                 System.out.println("policyAlias=======>"+policyAlias);
                 
                 routeId = FrameworkUtil.autoName(context, typeAlias, AutoNameSeries, policyAlias);
                 
                 System.out.println("routeId  above=======>"+routeId);
             }else{
                     route.createObject(context, DomainConstants.TYPE_ROUTE, name, revision, DomainConstants.POLICY_ROUTE, vault);
                     routeId = route.getObjectId(context);
                     
                     System.out.println("routeId  below=======>"+routeId);
                     
             }            
           route.setId(routeId);

           BusinessObject personObject = Person.getPerson(context);
           DomainObject dmoRequest = new DomainObject(routeId);

           if(restrictMembers.equals("ScopeName")){
                 selscopeId = (String)requestMap.get("folderId");
                 scopeName   = (String)requestMap.get("txtWSFolder");
                 if(UIUtil.isNullOrEmpty(scopeName) && UIUtil.isNullOrEmpty(selscopeId)){
                     resultMap.put("ErrorMessage", i18nNow.getI18nString("emxComponents.CreateRoute.EnterRouteScope","emxComponentsStringResource",strLanguage));
                     WSNotSelected = false;
                     
                     System.out.println("WSNotSelected =======>"+WSNotSelected);
                 }
            }
            else if(restrictMembers.equals("Organization"))
            {
                selscopeId = restrictMembers;
                System.out.println("selscopeId =======>"+selscopeId);
            }
           if(objectId != null && !"".equals(objectId) && !"null".equals(objectId))
           {
           DomainObject boProject = new DomainObject(objectId);
           
           strTypeName = boProject.getInfo(context,"type");
           System.out.println("strTypeName =======>"+strTypeName);
           
           }
           if((strTypeName!=null || !"".equals(strTypeName) || !"null".equals(strTypeName)) && DomainConstants.TYPE_DOCUMENT.equals(strTypeName)&& com.matrixone.apps.domain.util.mxType.isOfParentType(context,strTypeName,DomainConstants.TYPE_WORKSPACE_VAULT))
           {
               selscopeId = objectId;
               System.out.println("selscopeId next =======>"+selscopeId);
           }
           
           String routeCompletionAction  = (String)requestMap.get("RouteCompletionAction");
           System.out.println("routeCompletionAction =======>"+routeCompletionAction);
           
           String routeDescription       = (String)requestMap.get("Description");
           System.out.println("routeDescription =======>"+routeDescription);
           
           String portalMode             = (String)requestMap.get("portalMode");
           System.out.println("portalMode =======>"+portalMode);
           
           String routeBasePurpose       = (String)requestMap.get("RouteBasePurpose");
           System.out.println("routeBasePurpose =======>"+routeBasePurpose);
           
           String supplierOrgId          = (String)requestMap.get("supplierOrgId");
           System.out.println("supplierOrgId =======>"+supplierOrgId);
           
           String suiteKey               = (String)requestMap.get("suiteKey");
           System.out.println("suiteKey =======>"+suiteKey);
           
           String sTemplateId            = (String)requestMap.get("TemplateOID");
           System.out.println("sTemplateId =======>"+sTemplateId);
           
           String sTemplateName          = (String)requestMap.get("TemplateDisplay");
           System.out.println("sTemplateName =======>"+sTemplateName);
           
           String visblToParent          = (String)requestMap.get("VisbleToParent");
           System.out.println("visblToParent =======>"+visblToParent);
           
           String strAutoStopOnRejection = (String)requestMap.get("AutoStopOnRejection");
           System.out.println("strAutoStopOnRejection =======>"+strAutoStopOnRejection);
           

           if(routeBasePurpose != null)
               routeDetails.put("routeBasePurpose", routeBasePurpose);

           if(visblToParent == null || visblToParent.equals("null")){
                   visblToParent = "";
            }
           boolean rtSelected = (sTemplateId != null && !"null".equals(sTemplateId) && !sTemplateId.equals(""));
           System.out.println("rtSelected =======>"+rtSelected);
           if(rtSelected)
              new com.matrixone.apps.common.RouteTemplate(sTemplateId).checksToUseRouteTemplateInRoute(context);

           String sAttrRestrictMembers       = PropertyUtil.getSchemaProperty(context, "attribute_RestrictMembers" );
           String sAttrRouteBasePurpose      = PropertyUtil.getSchemaProperty(context, "attribute_RouteBasePurpose" );
           String sAttrRouteCompletionAction = PropertyUtil.getSchemaProperty(context, "attribute_RouteCompletionAction" );
           String attrOriginator             = PropertyUtil.getSchemaProperty(context, "attribute_Originator");
           final String ATTRIBUTE_AUTO_STOP_ON_REJECTION   = PropertyUtil.getSchemaProperty(context, "attribute_AutoStopOnRejection" );
           String routeAutoNameId            = null;
           String strProjectVault  = "";
           String revisionSequence = "";
           
           System.out.println("sAttrRestrictMembers =======>"+sAttrRestrictMembers);
           System.out.println("sAttrRouteBasePurpose =======>"+sAttrRouteBasePurpose);
           System.out.println("sAttrRouteCompletionAction =======>"+sAttrRouteCompletionAction);
           System.out.println("attrOriginator =======>"+attrOriginator);
           System.out.println("ATTRIBUTE_AUTO_STOP_ON_REJECTION =======>"+ATTRIBUTE_AUTO_STOP_ON_REJECTION);
           
           
           
           
           if(WSNotSelected)
           {
           if((objectId != null && !"".equals(objectId) && !"null".equals(objectId)) )
           {
             DomainObject boObject = new DomainObject(objectId);
             
             String sType  = boObject.getType(context);
             System.out.println("sType =======>"+sType);
             
             String objState=boObject.getInfo(context,DomainConstants.SELECT_CURRENT);
             System.out.println("objState =======>"+objState);
             
             if(DomainConstants.TYPE_INBOX_TASK.equalsIgnoreCase(sType) && DomainConstants.STATE_INBOX_TASK_COMPLETE.equalsIgnoreCase(objState)){
            	 isCompletedTask = true;
            	 System.out.println("isCompletedTask =======>"+isCompletedTask);
             }
             routeDetails.put(objectId,objState);
             boolean isProjId = false;
             try
             {
            	 Route.routeWithScope(context , objectId , routeId , routeDetails );
            	 System.out.println(" **** i am coming **** 111 ");
             }catch(FrameworkException ranc){
            	 if (isCompletedTask && ranc.getMessage().indexOf("fromconnect") >0) {
            		 throw new FrameworkException(errorMessage);
            	 }else {
                 throw new FrameworkException(ranc.getMessage());
             }
           }
           }
           else
           {
               if(restrictMembers.equals("All") || restrictMembers.equals("Organization"))
               {
                   route.connect(context,new RelationshipType(DomainConstants.RELATIONSHIP_PROJECT_ROUTE),true, personObject);
                   System.out.println(" **** i am coming **** 222");
               }

               else
             {


           try
           {
               Route.routeWithScope(context , selscopeId , routeId , routeDetails );

               System.out.println(" **** i am coming **** 333");
           }catch(Exception ranc){
               throw new FrameworkException(ranc.getMessage());
           }
             }
           }

           AttributeList routeAttrList = new AttributeList();
           routeAttrList.addElement(new Attribute(new AttributeType(attrOriginator),context.getUser()));
           routeAttrList.addElement(new Attribute(new AttributeType(sAttrRouteCompletionAction),routeCompletionAction));
           routeAttrList.addElement(new Attribute(new AttributeType(sAttrRouteBasePurpose),routeBasePurpose));
           routeAttrList.addElement(new Attribute(new AttributeType(ATTRIBUTE_AUTO_STOP_ON_REJECTION), strAutoStopOnRejection));// getting Auto Stop Attribute
           
           System.out.println("routeAttrist======>"+routeAttrList);
           
           
           if( (selscopeId != null) && (!selscopeId.equals("")) ){
        	   if(FrameworkUtil.isObjectId(context, selscopeId)){
        		   System.out.println("selscopeId====== 111111111>"+selscopeId);
        	   	DomainObject boscope = new DomainObject(selscopeId);
        	   	selscopeId  =  boscope.getInfo(context, "physicalid");
        	   	System.out.println("selscopeId====== 2222222>"+selscopeId);
        	   }
             routeAttrList.addElement(new Attribute(new AttributeType(sAttrRestrictMembers),selscopeId));
           }
           route.setId(routeId);
           route.setAttributes(context,routeAttrList);
           route.setDescription(routeDescription);
           route.update(context);
           
           System.out.println("selscopeId====== 3333333"+selscopeId);
           
           if (visblToParent != null && !"null".equals(visblToParent) && !"".equals(visblToParent) && YES.equalsIgnoreCase(visblToParent))
          {
               routeAttrList.addElement(new Attribute(new AttributeType(DomainConstants.ATTRIBUTE_SUBROUTE_VISIBILITY),YES));
               DomainObject taskObj=new DomainObject(objectId);
               String originator=taskObj.getInfo(context,DomainConstants.SELECT_ORIGINATOR);
               StringList accessNames = DomainAccess.getLogicalNames(context, routeId);	
               
               System.out.println("originator====== 3333333"+originator);
               System.out.println("accessNames====== 3333333"+accessNames);
               
	           try{
	          		 ContextUtil.pushContext(context);
	          		 DomainAccess.createObjectOwnership(context, routeId, null, originator +"_PRJ", (String)accessNames.get(0), DomainAccess.COMMENT_MULTIPLE_OWNERSHIP, false);
	           } catch(Exception ex) {
	          		 throw new FrameworkException(ex);
	           } finally {
	          		 ContextUtil.popContext(context);
	           }
          }

           BusinessObject routeTemplateObj             = null;
           BusinessObject personObj                    = null;

           SelectList selectPersonStmts                = null;
           SelectList selectPersonRelStmts             = null;
           ExpansionWithSelect personSelect            = null;
           RelationshipWithSelectItr relPersonItr      = null;
           Relationship relationShipRouteNode          = null;

           String routeActionValueStr                  = null;
           String routeSequenceValueStr                = null;
           String routeInstructionsValueStr            = null;
           String sRouteTitle                          = null;
           String routeTaskScheduleDate                = null;
           String routeTaskNameValueStr                = null;
           String routeTaskUser                        = null;
           String routeAssigneeDueDateOptStr           = null;
           String dueDateOffset                        = null;
           String dueDateOffsetFrom                    = null;
           String parallelNodeProcessionRule           = null;
           String reviewTask = "";
           String allowDelegation ="";

           Attribute routeTitle                        = null;
           Attribute routeActionAttribute              = null;
           Attribute routeOrderAttribute               = null;
           Attribute routeInstructionsAttribute        = null;
           Attribute templateTaskAttribute             = null;
           AttributeList attrList                      = null;
           Attribute routeAssigneeDueDateOptAttribute  = null;
           Attribute routeDueDateOffsetAttribute       = null;
           Attribute routeDateOffsetFromAttribute      = null;
           Attribute routeTaskUserAttribute            = null;
           Attribute parallelNodeProcessionRuleAttrib  = null;
           Attribute reviewTaskAttribute               = null;
           Attribute allowDelegationAttribute          = null;
           Attribute routeTaskScheduleDateAttribute    = null;

           String templateTaskStr                      = PropertyUtil.getSchemaProperty(context, "attribute_TemplateTask");
           Hashtable routeNodeAttributesTable          = new Hashtable();

           System.out.println("templateTaskStr=======>"+templateTaskStr);
           
           if(rtSelected) {

             selectPersonStmts = new SelectList();
             AccessUtil accessUtil = new AccessUtil();

             selectPersonRelStmts = new SelectList();
             selectPersonRelStmts.addAttribute(DomainConstants.ATTRIBUTE_ROUTE_SEQUENCE);
             selectPersonRelStmts.addAttribute(DomainConstants.ATTRIBUTE_ROUTE_ACTION);
             selectPersonRelStmts.addAttribute(DomainConstants.ATTRIBUTE_ROUTE_INSTRUCTIONS);
             selectPersonRelStmts.addAttribute(DomainConstants.ATTRIBUTE_TITLE);
             selectPersonRelStmts.addAttribute(DomainConstants.ATTRIBUTE_ASSIGNEE_SET_DUEDATE);
             selectPersonRelStmts.addAttribute(DomainConstants.ATTRIBUTE_DUEDATE_OFFSET);
             selectPersonRelStmts.addAttribute(DomainConstants.ATTRIBUTE_DATE_OFFSET_FROM);
             selectPersonRelStmts.addAttribute(DomainConstants.ATTRIBUTE_ROUTE_TASK_USER);
             selectPersonRelStmts.addAttribute(DomainConstants.ATTRIBUTE_SCHEDULED_COMPLETION_DATE);
             String strParallelNodeProscessionRule = PropertyUtil.getSchemaProperty(context, "attribute_ParallelNodeProcessionRule");
             String  sAttReviewTask               =  PropertyUtil.getSchemaProperty(context,"attribute_ReviewTask");

             selectPersonRelStmts.addAttribute(strParallelNodeProscessionRule);
             selectPersonRelStmts.addAttribute(sAttReviewTask);
             selectPersonRelStmts.addAttribute(DomainConstants.ATTRIBUTE_ALLOW_DELEGATION);

             System.out.println("selectPersonRelStmts========>"+selectPersonRelStmts);

             routeTemplateObj = new BusinessObject(sTemplateId);
             routeTemplateObj.open(context);
             try{
             route.connectTemplate(context,sTemplateId);
             } catch(Exception e){
                 resultMap.put("Message",e.getMessage());
             }
             Pattern typePattern = new Pattern(DomainConstants.TYPE_PERSON);
             typePattern.addPattern(DomainConstants.TYPE_ROUTE_TASK_USER);
             personSelect = routeTemplateObj.expandSelect(context,DomainConstants.RELATIONSHIP_ROUTE_NODE,typePattern.getPattern(),
                                              selectPersonStmts,selectPersonRelStmts,false, true, (short)1);

            // System.out.println("personSelect========>"+personSelect);
             routeTemplateObj.close(context);
             relPersonItr = new RelationshipWithSelectItr(personSelect.getRelationships());
             // loop thru the rels and get the route object
             while ((relPersonItr != null ) && relPersonItr.next()) {
               if ( relPersonItr.obj().getTypeName().equals(DomainConstants.RELATIONSHIP_ROUTE_NODE)) {
                 personObj = relPersonItr.obj().getTo();
                 if (personObj != null)   {
                   personObj.open(context);
                   
                   System.out.println("personObj  id  =======>"+personObj.getObjectId());
                   
                   if((DomainConstants.TYPE_ROUTE_TASK_USER).equals(personObj.getTypeName()) || ((DomainConstants.TYPE_PERSON).equals(personObj.getTypeName()))  ) {

                     try{
                       relationShipRouteNode = route.connect(context, new RelationshipType(DomainConstants.RELATIONSHIP_ROUTE_NODE),true,personObj);
                     } catch(Exception ex){
                         resultMap.put("Message",ex.getMessage());
                     }

                     routeNodeAttributesTable    =  relPersonItr.obj().getRelationshipData();
                     routeSequenceValueStr       = (String) routeNodeAttributesTable.get("attribute[" + DomainConstants.ATTRIBUTE_ROUTE_SEQUENCE + "]" );
                     sRouteTitle                 = (String) routeNodeAttributesTable.get("attribute[" + DomainConstants.ATTRIBUTE_TITLE + "]" );
                     routeActionValueStr         = (String) routeNodeAttributesTable.get("attribute[" + DomainConstants.ATTRIBUTE_ROUTE_ACTION + "]" );
                     routeInstructionsValueStr   = (String) routeNodeAttributesTable.get("attribute[" + DomainConstants.ATTRIBUTE_ROUTE_INSTRUCTIONS + "]" );
                     routeTaskNameValueStr       = (String) routeNodeAttributesTable.get("attribute[" + DomainConstants.ATTRIBUTE_TITLE + "]" );
                     routeAssigneeDueDateOptStr  = (String) routeNodeAttributesTable.get("attribute[" + DomainConstants.ATTRIBUTE_ASSIGNEE_SET_DUEDATE + "]" );
                     dueDateOffset               = (String) routeNodeAttributesTable.get("attribute[" + DomainConstants.ATTRIBUTE_DUEDATE_OFFSET + "]" );
                     dueDateOffsetFrom           = (String) routeNodeAttributesTable.get("attribute[" + DomainConstants.ATTRIBUTE_DATE_OFFSET_FROM + "]" );
                     routeTaskUser               = (String) routeNodeAttributesTable.get("attribute[" + DomainConstants.ATTRIBUTE_ROUTE_TASK_USER + "]" );
                     routeTaskScheduleDate       = (String) routeNodeAttributesTable.get("attribute[" + DomainConstants.ATTRIBUTE_SCHEDULED_COMPLETION_DATE + "]" );
                     // Added by Infosys for Bug # 303103 Date 05/11/2005
                     parallelNodeProcessionRule  = (String) routeNodeAttributesTable.get("attribute[" + strParallelNodeProscessionRule + "]" );
                    // Added for the bug 301391
                     reviewTask = (String)routeNodeAttributesTable.get("attribute["+sAttReviewTask+"]");
                    allowDelegation = (String)routeNodeAttributesTable.get("attribute[" +DomainConstants.ATTRIBUTE_ALLOW_DELEGATION+ "]" );

                     attrList = new AttributeList();
                     relationShipRouteNode.open(context);

                     // Added by Infosys for Bug # 303103 Date 05/11/2005
                     // set parallelNodeProcessionRule
                     parallelNodeProcessionRuleAttrib  = new Attribute(new AttributeType(strParallelNodeProscessionRule),parallelNodeProcessionRule);
                     attrList.addElement(parallelNodeProcessionRuleAttrib);

                     // set title
                     routeTitle  = new Attribute(new AttributeType(DomainConstants.ATTRIBUTE_TITLE),sRouteTitle);
                     attrList.addElement(routeTitle);

                     // set route action
                     if ( routeActionValueStr != null ) {
                       routeActionAttribute = new Attribute(new AttributeType(DomainConstants.ATTRIBUTE_ROUTE_ACTION),routeActionValueStr);
                       attrList.addElement(routeActionAttribute);
                     }

                     // set route order
                     routeOrderAttribute = new Attribute(new AttributeType(DomainConstants.ATTRIBUTE_ROUTE_SEQUENCE),routeSequenceValueStr);
                     attrList.addElement(routeOrderAttribute);

                     // set route instructions
                     if ( routeInstructionsValueStr != null ) {
                       routeInstructionsAttribute = new Attribute(new AttributeType(DomainConstants.ATTRIBUTE_ROUTE_INSTRUCTIONS),routeInstructionsValueStr);
                       attrList.addElement(routeInstructionsAttribute);
                     }

                     templateTaskAttribute = new Attribute(new AttributeType(templateTaskStr),"Yes");
                     attrList.addElement(templateTaskAttribute);

                     // set route assignee due date option
                     if ( routeAssigneeDueDateOptStr != null ) {
                       routeAssigneeDueDateOptAttribute = new Attribute(new AttributeType(DomainConstants.ATTRIBUTE_ASSIGNEE_SET_DUEDATE),routeAssigneeDueDateOptStr);
                       attrList.addElement(routeAssigneeDueDateOptAttribute);
                     }

                     // set route due date offset
                     if ( dueDateOffset != null ) {
                       routeDueDateOffsetAttribute = new Attribute(new AttributeType(DomainConstants.ATTRIBUTE_DUEDATE_OFFSET),dueDateOffset);
                       attrList.addElement(routeDueDateOffsetAttribute);
                     }


                     // set route due date offset from
                     if( dueDateOffsetFrom != null ) {
                       routeDateOffsetFromAttribute = new Attribute(new AttributeType(DomainConstants.ATTRIBUTE_DATE_OFFSET_FROM),dueDateOffsetFrom);
                       attrList.addElement(routeDateOffsetFromAttribute);
                     }

                     // set route task user attribute
                     if( routeTaskUser != null ) {
                       routeTaskUserAttribute = new Attribute(new AttributeType(DomainConstants.ATTRIBUTE_ROUTE_TASK_USER),routeTaskUser);
                       attrList.addElement(routeTaskUserAttribute);
                     }
                     // Added for the bug 301391
                     // set Review Task attribute
                     if( reviewTask != null){
                        reviewTaskAttribute = new Attribute(new AttributeType(sAttReviewTask),reviewTask);
                        attrList.addElement(reviewTaskAttribute);
                      }
                    // set Allow Delegation attribute
                      if( allowDelegation != null){
                         allowDelegationAttribute = new Attribute(new AttributeType(DomainConstants.ATTRIBUTE_ALLOW_DELEGATION),allowDelegation);
                         attrList.addElement(allowDelegationAttribute);
                     }

                      // set Schedule Date attribute
                      if(UIUtil.isNotNullAndNotEmpty(routeTaskScheduleDate)){
                    	  routeTaskScheduleDateAttribute = new Attribute(new AttributeType(DomainConstants.ATTRIBUTE_SCHEDULED_COMPLETION_DATE),routeTaskScheduleDate);
                          attrList.addElement(routeTaskScheduleDateAttribute);
                     }


                     relationShipRouteNode.setAttributes(context,attrList);
                     relationShipRouteNode.close(context);

                    // Added for bug 376886
                    if(((DomainConstants.TYPE_ROUTE_TASK_USER).equals(personObj.getTypeName())))
                    {
                         String personName = PropertyUtil.getSchemaProperty(context,routeTaskUser);                         
                        try{
                            if(!UIUtil.isNullOrEmpty(personName)) {
                            accessUtil.setAccess(personName,AccessUtil.ROUTE_ACCESS_GRANTOR,accessUtil.getReadAccess());
                        }
                        }
                        catch(MatrixException e) {
                            throw new FrameworkException(e.toString());
                        }
                    }
                    // Ended

                   }
                   personObj.close(context);
                 }

               }
           }//End while

             if(accessUtil.getAccessList().size() > 0){
                 String[] strArgs = new String[]{route.getObjectId()};
                 JPO.invoke(context, "emxWorkspaceConstants", strArgs, "grantAccess", JPO.packArgs(accessUtil.getAccessList()));
         }

             final String SELECT_ATTRIBUTE_AUTO_STOP_ON_REJECTION = "attribute[" + ATTRIBUTE_AUTO_STOP_ON_REJECTION + "]";
             DomainObject dmoRouteTemplate = new DomainObject(routeTemplateObj);
             strAutoStopOnRejection = dmoRouteTemplate.getInfo(context, SELECT_ATTRIBUTE_AUTO_STOP_ON_REJECTION);
             if (strAutoStopOnRejection != null && !"".equals(strAutoStopOnRejection) && !"null".equalsIgnoreCase(strAutoStopOnRejection)) {
                 route.setAttributeValue(context, ATTRIBUTE_AUTO_STOP_ON_REJECTION, strAutoStopOnRejection);
             }

             routeTemplateObj.close(context);
           }
           }
           resultMap.put(SELECT_ID, routeId);
           }
        return resultMap;
    }

    public String routeTemplateLinkOfRoute(Context context, String[] args) throws Exception
    {
        HashMap programMap         = (HashMap) JPO.unpackArgs(args);
        Map requestMap             = (Map) programMap.get("requestMap");
        String routeId     = (String) requestMap.get(OBJECT_ID);
        String mode                = (String) requestMap.get("mode");

        Route boRoute = (Route)DomainObject.newInstance(context,DomainConstants.TYPE_ROUTE);
        boRoute.setId(routeId);
        StringBuffer sb            = new StringBuffer();
        SelectList selectStmts = new SelectList();
        selectStmts.addElement(Route.SELECT_ROUTE_TEMPLATE_NAME);
        selectStmts.addElement(Route.SELECT_ROUTE_TEMPLATE_ID);
        Map resultMap = boRoute.getInfo(context, selectStmts);
        String strTemplate    = (String) resultMap.get(Route.SELECT_ROUTE_TEMPLATE_NAME);
        String strTemplateId  = (String) resultMap.get(Route.SELECT_ROUTE_TEMPLATE_ID);

        if(mode.equalsIgnoreCase("view"))
        {
            if ( strTemplate == null ) {

                sb.append("&#160;");
            } else {
                sb.append("<a href=\"javascript:showModalDialog('../common/emxTree.jsp?emxSuiteDirectory=components&objectId=" + strTemplateId + "', '800', '575')\"><img src=\"../common/images/iconSmallRoute.png\" alt=\"\" name=\"route\" id=\"route\" border=\"0\"/></a>&#160;");
                sb.append("<a href=\"javascript:showModalDialog('../common/emxTree.jsp?emxSuiteDirectory=components&objectId=" + strTemplateId + "', '800', '575')\">"+strTemplate+"</a>");

            }
        }else
        {
            sb.append(strTemplate);
        }
        return sb.toString();
    }


    public boolean showRouteVisibleToParentField(Context context, String[] args) throws Exception
    {
        HashMap programMap         = (HashMap) JPO.unpackArgs(args);
        String objectId             = (String) programMap.get(OBJECT_ID);
        Route boRoute = (Route)DomainObject.newInstance(context,DomainConstants.TYPE_ROUTE);
        String relTaskSubRoute = PropertyUtil.getSchemaProperty(context, "relationship_TaskSubRoute");
        String connectedIdSelect="to["+relTaskSubRoute+"].from.id";
        boolean visibility = false;
        if(UIUtil.isNotNullAndNotEmpty(objectId)) {
            boRoute.setId(objectId);
            String objectType = boRoute.getType(context);
            String connectedId=boRoute.getInfo(context,"to["+relTaskSubRoute+"].from.id");
            visibility = (objectType.equals(DomainConstants.TYPE_INBOX_TASK) ||connectedId != null) ? true : false;
        }
        return visibility;
    }

    /**
     * The main method which all filter access method calls.
     * Returns Route Access List Based on the parameters.
     * @param context the eMatrix Context object
     * @param args holds the grantee and grantor name
     * @return MapList
     * @throws Exception if the operation fails
     * @since R211
     */

   @com.matrixone.apps.framework.ui.ProgramCallable
   public MapList getRouteAccessSummaryMemberList(Context context,String[] args) throws Exception {
       HashMap programMap = (HashMap) JPO.unpackArgs(args);

       String language = (String) programMap.get("languageStr");
       language = language == null ? context.getLocale().getLanguage() : language;

       com.matrixone.apps.common.Person PersonObj = (com.matrixone.apps.common.Person) DomainObject.newInstance(context, DomainConstants.TYPE_PERSON);
       String objectId    = (String)programMap.get(OBJECT_ID);
       String strRouteId  = (String)programMap.get("parentOID");

       AccessUtil accessUtil   = new AccessUtil();

       String sTaskEditSetting="from["+RELATIONSHIP_INITIATING_ROUTE_TEMPLATE+"].to.attribute["+ATTRIBUTE_TASKEDIT_SETTING+"].value";
       
        MapList tempMapList = new MapList();
	   MapList domainAccessParentSummaryList = DomainAccess.getAccessSummaryList(context, objectId);
	   
	   MapList tempMapList1 = getSortedDomainAccessSummaryList(context, domainAccessParentSummaryList, true);
       for(int k = 0;k < tempMapList1.size();k++){
    	   tempMapList.add(tempMapList1.get(k));
       }
       
       tempMapList1 = getSortedDomainAccessSummaryList(context, domainAccessParentSummaryList, false);
       for(int k = 0;k < tempMapList1.size();k++){
    	   tempMapList.add(tempMapList1.get(k));
       }
	   
       Route routeBO = (Route)DomainObject.newInstance(context,DomainConstants.TYPE_ROUTE);
       routeBO.setId(objectId);

       SelectList selectables = new SelectList(10);
       selectables.add(SELECT_OWNER);
       selectables.add(getAttributeSelect(ATTRIBUTE_ROUTE_STATUS));
       selectables.add("from["+RELATIONSHIP_INITIATING_ROUTE_TEMPLATE+"].to.id");
       selectables.add(sTaskEditSetting);       

       Map routeInfo = routeBO.getInfo(context, selectables);

       selectables.clear();
       selectables.addElement(DomainConstants.SELECT_ID);
	   //initate route - start
       selectables.addElement("physicalid");
	   //initate route - end
       selectables.addElement(DomainConstants.SELECT_TYPE);
       selectables.addElement(DomainConstants.SELECT_NAME);
       selectables.addElement(DomainConstants.SELECT_CURRENT);
       selectables.addElement(com.matrixone.apps.common.Person.SELECT_COMPANY_NAME);
       selectables.addElement(com.matrixone.apps.common.Person.SELECT_COMPANY_ID);

       // build select params for Relationship
       SelectList selectPersonRelStmts = new SelectList(2);
       selectPersonRelStmts.addElement(DomainConstants.SELECT_RELATIONSHIP_ID);
       selectPersonRelStmts.addElement(Route.SELECT_ROUTE_TASK_USER);
       //initate route - start
       String selRouteNodeId         = getAttributeSelect(DomainConstants.ATTRIBUTE_ROUTE_NODE_ID);
       selectPersonRelStmts.addElement(selRouteNodeId);
       //initate route - end

       Pattern typePattern = new Pattern(TYPE_PERSON);
       typePattern.addPattern(TYPE_ROUTE_TASK_USER);
       
       MapList memberList = routeBO.getRelatedObjects(context, 
               RELATIONSHIP_ROUTE_NODE, typePattern.getPattern(),
               selectables, selectPersonRelStmts,
               false, true, (short)1, 
               EMPTY_STRING, EMPTY_STRING,
               0);
       MapList domainAccessSummaryList = Route.getOwnershipAccessOnRoute(context, objectId);
       
       boolean isRouteObj = DomainConstants.TYPE_ROUTE.equals(routeInfo.get(DomainConstants.SELECT_TYPE));
       boolean isRouteStarted = !"Not Started".equals((String) routeInfo.get(getAttributeSelect(ATTRIBUTE_ROUTE_STATUS)));
       String strOwner = (String) routeInfo.get(SELECT_OWNER);
       
       String templateId =(String)routeInfo.get("from["+RELATIONSHIP_INITIATING_ROUTE_TEMPLATE+"].to.id");
       String taskSetting= (String) routeInfo.get(sTaskEditSetting);    
       
       StringList members  = new StringList(memberList.size());

       Iterator itr = memberList.iterator();
       
       while(itr.hasNext()) {
    	    
           Map  membermap =  (Map) itr.next();
           String memberName         = (String)membermap.get(SELECT_NAME);
           String routeTaskUser     = (String)membermap.get(Route.SELECT_ROUTE_TASK_USER);
           String nodeType           = (String)membermap.get(SELECT_TYPE);

           boolean isRouteOwner = memberName.equals(strOwner);
           String sAccess  = isRouteOwner ? "Add Remove" : "Read";

           String dispMemberName = nodeType.equals(TYPE_PERSON) ? memberName :
                                   UIUtil.isNullOrEmpty(routeTaskUser) ? "" : PropertyUtil.getSchemaProperty(context, routeTaskUser);

           if(!UIUtil.isNullOrEmpty(dispMemberName) && !members.contains(dispMemberName)) {
               String org = (String) membermap.get(com.matrixone.apps.common.Person.SELECT_COMPANY_NAME);
                              
               HashMap tempHash = new HashMap(10);
               tempHash.put("Organization", org == null ? EMPTY_STRING : org);
               tempHash.put("OrganizationId", membermap.get(com.matrixone.apps.common.Person.SELECT_COMPANY_ID));
               tempHash.put("ProjectMemberId", "");
               tempHash.put("PersonId", membermap.get(SELECT_ID));
               tempHash.put("RouteNodeId", membermap.get(SELECT_RELATIONSHIP_ID));
               tempHash.put(SELECT_RELATIONSHIP_ID, membermap.get(SELECT_RELATIONSHIP_ID));
			   //initate route - start
			   tempHash.put("physicalid",membermap.get("physicalid"));
			   tempHash.put("attribute[Route Node ID]", membermap.get("attribute[Route Node ID]"));
			   //initate route - end

               String toNodeType      = (String)membermap.get(SELECT_TYPE);
               String type = "";
               if(toNodeType.equals(TYPE_ROUTE_TASK_USER) && !UIUtil.isNullOrEmpty(routeTaskUser)) {
                   type = routeTaskUser.substring(0, routeTaskUser.indexOf("_") );
                   String sGrantee = PropertyUtil.getSchemaProperty(context, routeTaskUser);
                   
                   tempHash.put(SELECT_ID, routeTaskUser);
                   tempHash.put("LastFirstName", sGrantee);
                   tempHash.put("Type", type);
               } else {
                   String sGrantee = (String)membermap.get(SELECT_NAME) ;
                   for(int i = 0 ; i < domainAccessSummaryList.size(); i++){
                	   HashMap m = (HashMap)domainAccessSummaryList.get(i);
                	   if((sGrantee).equals((m.get("project"))) && !isRouteOwner){
                		   sAccess = (String)m.get("accessMask");
                		   break;
                       }
                   }
                   
                   
                   tempHash.put("userName", sGrantee); 
                   //initate route - start
			       tempHash.put("name", membermap.get("name"));
                   //initate route - end
                   tempHash.put("Type", membermap.get(SELECT_TYPE));
                   tempHash.put(SELECT_ID, membermap.get(SELECT_ID));
                   tempHash.put("LastFirstName", PersonUtil.getFullName(context, sGrantee));
                   
               }
			   if("Full".equalsIgnoreCase(sAccess)){
            	   sAccess = "Add Remove";
			   }
               tempHash.put("Access", sAccess);
               
               String disableSelection = isRouteObj && (isRouteOwner || isRouteStarted) ? "true" : "false"; 
               String isAccessColumnEditable = "role".equals(type) || "group".equals(type) || isRouteOwner ? "false" : "true"; 
               
               if(UIUtil.isNotNullAndNotEmpty(templateId)&& ("Maintain Exact Task List".equals(taskSetting)||"Extend Task List".equals(taskSetting) || "Modify Task List".equals(taskSetting))){
            	   DomainObject routeTempObj = DomainObject.newInstance(context ,templateId);
                   sTaskEditSetting = routeTempObj.getAttributeValue(context, ATTRIBUTE_TASKEDIT_SETTING);
                   SelectList selectList = new SelectList();
                   selectList.add(DomainConstants.SELECT_NAME) ; 
               
                   Pattern newTypePattern = new Pattern(TYPE_PERSON);
                   
                   MapList PersonList = routeTempObj.getRelatedObjects(context, 
               		   							RELATIONSHIP_ROUTE_NODE, 
               		   							newTypePattern.getPattern(),
               		   							selectList, 
               		   							null,
               		   							false,
               		   							true, 
               		   							(short)0, 
               		   							EMPTY_STRING, 
               		   							EMPTY_STRING,
               		   							0);
                 
				   Iterator iterator = PersonList.iterator();
				   boolean hasPerson=false;
				   while(iterator.hasNext()) {
                      Map  personMap =  (Map) iterator.next();
                      String personName  = (String)personMap.get(SELECT_NAME);
                      if(personName.equals(memberName)){
                    	  hasPerson=true;
                    	  break;
                      }
				   }
       	   		   disableSelection= (hasPerson || isRouteStarted)?"true":"false";
                   isAccessColumnEditable=hasPerson?"false":"true";
               }               
               
               tempHash.put("disableSelection", disableSelection);
               tempHash.put("isAccessColumnEditable", isAccessColumnEditable);
               
               members.add(dispMemberName);
               tempMapList.add(tempHash);
           }
       }
       return tempMapList;
   }

   /**
    * Gets the vector output in HTML format, for the Name column in the access summary table.
    *
    * @param context the eMatrix <code>Context</code> object.
    * @param args contains a packed HashMap with the following entries:
    * objectList - a MapList containing the actual maps "dataMap" containing the data.
    * paramList - a HashMap containing the following parameters.
    * editMode - a String either true or false for deciding the mode.
    * reportFormat - a String to identify the Printer Friendly and Export view.
    * @return Vector of the user display names in the HTML format.
    * @throws Exception if the operation fails.
    * @since CommonComponents R211
    */

   public Vector getRouteAccessSummaryName (Context context, String[] args)
       throws Exception
   {
       HashMap programMap = (HashMap)JPO.unpackArgs(args);
       HashMap paramList = (HashMap) programMap.get("paramList");
       String languageStr = (String)paramList.get("languageStr");
       String strObjectId = (String)paramList.get(OBJECT_ID);
	   String strPrinterFriendly = (String)paramList.get("reportFormat");
       boolean isprinterFriendly = strPrinterFriendly != null;

       MapList objList = (MapList)programMap.get("objectList");

       Vector columnVals = new Vector(objList.size());
       DomainObject doObj = DomainObject.newInstance(context,strObjectId);
       String strObjType = doObj.getInfo(context,DomainConstants.SELECT_TYPE);

       for (int k=0; k < objList.size(); k++){
           Map map = (Map) objList.get(k);
           String strType = (String)map.get("Type");
		   StringBuffer strBuffName = new StringBuffer();
           if("Collab Space".equals(strType)){
        	   String strDisplayName = (String)map.get("project");
        	   strBuffName.append(XSSUtil.encodeForXML(context,strDisplayName));
           }else{
           String strId = (String)map.get("PersonId");
           String strDisplayName = (String)map.get("LastFirstName");

           if("Role".equalsIgnoreCase(strType)){
               strDisplayName= i18nNow.getAdminI18NString("Role", strDisplayName, languageStr);
					if(!isprinterFriendly && (strObjType.equalsIgnoreCase(DomainConstants.TYPE_ROUTE) || strObjType.equalsIgnoreCase(DomainConstants.TYPE_ROUTE_TEMPLATE))) {
               strBuffName.append("<img src=\"../common/images/iconSmallRole.gif\" alt=\"\" name=\"person\" id=\"PersonId\" border=\"0\"/>").append(XSSUtil.encodeForXML(context,strDisplayName));
				   } else if("HTML".equals(strPrinterFriendly)) {
					   strBuffName.append(XSSUtil.encodeForHTML(context,strDisplayName));
				   } else {
					   strBuffName.append(strDisplayName);
               }
           } else if("Group".equalsIgnoreCase(strType)){
               strDisplayName= i18nNow.getAdminI18NString("Group", strDisplayName, languageStr);
					if(!isprinterFriendly && (strObjType.equalsIgnoreCase(DomainConstants.TYPE_ROUTE) || strObjType.equalsIgnoreCase(DomainConstants.TYPE_ROUTE_TEMPLATE))) {
					   strBuffName.append("<img src=\"../common/images/iconSmallGroup.gif\" alt=\"\" name=\"person\" id=\"PersonId\" border=\"0\"/>").append(XSSUtil.encodeForXML(context,strDisplayName));
				   } else if("HTML".equals(strPrinterFriendly)) {
					   strBuffName.append(XSSUtil.encodeForHTML(context,strDisplayName));
				   } else {
					   strBuffName.append(strDisplayName);
               }
           } else {
					if(!isprinterFriendly && (strObjType.equalsIgnoreCase(DomainConstants.TYPE_ROUTE) || strObjType.equalsIgnoreCase(DomainConstants.TYPE_ROUTE_TEMPLATE))) {
                   strBuffName.append("<a href=\"javascript:showModalDialog('../common/emxTree.jsp?emxSuiteDirectory=components&amp;objectId=" + XSSUtil.encodeForJavaScript(context, strId) + "', '800', '575')\"><img src=\"../common/images/iconSmallPerson.png\" alt=\"\" name=\"person\" id=\"PersonId\" border=\"0\" /></a>&#160;");
                   strBuffName.append("<a href=\"javascript:showModalDialog('../common/emxTree.jsp?emxSuiteDirectory=components&amp;objectId=" + XSSUtil.encodeForJavaScript(context, strId) + "', '800', '575')\">"+XSSUtil.encodeForXML(context,strDisplayName)+"</a>");
				   } else if("HTML".equals(strPrinterFriendly)) {
					   strBuffName.append(XSSUtil.encodeForHTML(context,strDisplayName));
               } else {
					   strBuffName.append(strDisplayName);
               }
           }
		   }
           columnVals.add(strBuffName.toString());
       }
       return columnVals;
   }

   /**
    * Gets the vector output, for the user Type column in the access summary table.
    *
    * @param context the eMatrix <code>Context</code> object.
    * @param args contains a packed HashMap with the following entries:
    * objectList - a MapList containing the actual maps "dataMap" containing the data.
    * paramList - a HashMap containing the following parameters.
    * languageStr - a String containing the language information.
    * @return Vector of the user Types in internationalized format.
    * @throws Exception if the operation fails.
    * @since CommonComponents R211
    */

   public Vector getRouteAccessSummaryType (Context context, String[] args)
       throws Exception
   {
       HashMap programMap = (HashMap)JPO.unpackArgs(args);
       MapList objList = (MapList)programMap.get("objectList");
       HashMap paramList = (HashMap) programMap.get("paramList");
       String languageStr = (String)paramList.get("languageStr");

       int objListSize = objList.size();
       Vector columnVals   = new Vector(objListSize);
       for (int k=0; k < objListSize; k++) {
           Map map = (Map) objList.get(k);
           String type = (String)map.get("Type");
		   if("Collab Space".equals(type)){        	   
        	   columnVals.add(EnoviaResourceBundle.getProperty(context,"emxFrameworkStringResource", new Locale(languageStr),"emxFramework.Policy.Security_Context"));
           }else{
           // Internationalize the values of Role, Group or Person.
           type = "Person".equalsIgnoreCase(type) ? i18nNow.getTypeI18NString(type,languageStr) :
                  "role".equalsIgnoreCase(type) ? EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource", new Locale(languageStr),"emxComponents.Common.Role") :
                	  EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource", new Locale(languageStr),"emxComponents.Common.Group");
           columnVals.add(type);
       }
	   }
       return columnVals;
   }

   /**
    * Gets the vector output, for the user Organization column in the access summary table.
    *
    * @param context the eMatrix <code>Context</code> object.
    * @param args contains a packed HashMap with the following entries:
    * objectList - a MapList containing the actual maps "dataMap" containing the data.
    * @return Vector of the user Organizations.
    * @throws Exception if the operation fails.
    * @since CommonComponents R211
    */

   public Vector getRouteAccessSummaryOrganization (Context context, String[] args)
       throws Exception
   {
       HashMap programMap = (HashMap)JPO.unpackArgs(args);
       MapList objList = (MapList)programMap.get("objectList");
       int objListSize = objList.size();
       Vector columnVals   = new Vector(objListSize);
       for (int k=0; k < objListSize; k++) {
           Map map = (Map) objList.get(k);
           String org = (((String)map.get("Type")).equalsIgnoreCase("Person")|| ((String)map.get("Type")).equalsIgnoreCase("Collab Space"))? (String)map.get("Organization") : EMPTY_STRING;
           columnVals.addElement(org);
       }
       return columnVals;
   }


   /**
    * Gets the vector output in HTML format, for the Access column in the access summary table.
    *
    * @param context the eMatrix <code>Context</code> object.
    * @param args contains a packed HashMap with the following entries:
    * charSet - a MapList containing the actual maps "dataMap" containing the data.
    * objectList - a MapList containing the actual maps "dataMap" containing the data.
    * paramList - a HashMap containing the following parameters.
    * editMode - a String either true or false for deciding the mode.
    * reportFormat - a String to identify the Printer Friendly and Export view.
    * languageStr - a String containing the language information.
    * accessChoice - a String containing the comma seperated values of the access rights available.
    * @return Vector containing the user access or access choices in HTML format.
    * @throws Exception if the operation fails.
    * @since CommonComponents R211
    */

   public Vector getRouteAccessSummaryAccess (Context context, String[] args)
       throws Exception
   {
       HashMap programMap = (HashMap)JPO.unpackArgs(args);
       MapList objList = (MapList)programMap.get("objectList");
       HashMap paramList = (HashMap) programMap.get("paramList");
       String languageStr = (String)paramList.get("languageStr");

       HashMap i18nMap     = new HashMap();
       i18nMap.put("Read", "emxComponents.ObjectAccess.Read");
       i18nMap.put("Read Write", "emxComponents.ObjectAccess.ReadWrite");
       i18nMap.put("Add", "emxComponents.ObjectAccess.Add");
       i18nMap.put("Remove", "emxComponents.ObjectAccess.Remove");
       i18nMap.put("Add Remove", "emxComponents.ObjectAccess.AddRemove");

       int objListSize = objList.size();
       Vector columnVals = new Vector(objListSize);
       for (int k=0; k < objListSize; k++) {
           Map map = (Map)objList.get(k);
    	   String type = (String)map.get("Type");
           String access = (String)map.get("Access");
           if("Collab Space".equals(type)){        	   
        	   columnVals.addElement(EnoviaResourceBundle.getProperty(context,"emxFrameworkStringResource", new Locale(languageStr),"emxFramework.Access."+access.replace(" ", "")));
           }else{        	   
        	   	columnVals.addElement(EnoviaResourceBundle.getProperty(context,"emxFrameworkStringResource", new Locale(languageStr),"emxFramework.Access."+access.replace(" ", "")));
       }
       }
       return columnVals;
   }

 
  
  
 
 
  
   
	   

   /**
    * Gets the vector output in HTML format, for the New Window column in the access summary table.
    *
    * @param context the eMatrix <code>Context</code> object.
    * @param args contains a packed HashMap with the following entries:
    * objectList - a MapList containing the actual maps "dataMap" containing the data.
    * paramList - a HashMap containing the following parameters.
    * editMode - a String either true or false for deciding the mode.
    * reportFormat - a String to identify the Printer Friendly and Export view.
    * @return Vector containing the new window icon with the hyperlink in HTML format.
    * @throws Exception if the operation fails.
    * @since CommonComponents R211
    */

   public Vector getRouteAccessSummaryNewWindow (Context context, String[] args)
       throws Exception {
       HashMap programMap = (HashMap)JPO.unpackArgs(args);
       MapList objList = (MapList)programMap.get("objectList");
       int objListSize = objList.size();
       HashMap paramList = (HashMap) programMap.get("paramList");
       boolean isprinterFriendly = paramList.get("reportFormat") != null;

       Vector columnVals = new Vector(objListSize);
       for (int i=0; i < objListSize; i++) {
           Map map = (Map) objList.get(i);
    	   if("Collab Space".equals(map.get("Type"))){
    		   columnVals.addElement("");
    	   }else{
           StringBuffer strBuff = new StringBuffer();
           if (!isprinterFriendly && ((String)map.get("Type")).equalsIgnoreCase("Person")) {
               String name = (String)map.get("LastFirstName");
               // Show the new window icon for the users of type Person with the Hyperlink
               String personId = (String)map.get("PersonId");
               strBuff.append("<a href=\"javascript:emxTableColumnLinkClick('../common/emxTree.jsp?emxSuiteDirectory=components&amp;objectId=");
               strBuff.append(XSSUtil.encodeForJavaScript(context, personId));
               strBuff.append("', '");
               strBuff.append("700");
               strBuff.append("', '");
               strBuff.append("600");
               strBuff.append("', 'false', '");
               strBuff.append("popup");
               strBuff.append("')");
               strBuff.append("\">");
               strBuff.append("<img src=\"../common/images/iconNewWindow.gif\" alt=\"\" name=\"newWindow\" id=\"newWindowId\" border=\"0\" align=\"center\" /></a>");
           }
           columnVals.addElement(strBuff.toString());
       }
	   }
       return columnVals;
   }

   /**
    * Gets the vector output for the checkbox column in the access summary table.
    *
    * @param context the eMatrix <code>Context</code> object.
    * @param args contains a packed HashMap with the following entries:
    * objectList - a MapList containing the actual maps "dataMap" containing the data.
    * paramList - a HashMap containing the following parameters.
    * editMode - a String either true or false for deciding the mode.
    * @return Vector containing the true or false values.
    * @throws Exception if the operation fails.
    * @since CommonComponents R211
    * */

   public Vector getRouteAccessSummaryCheckboxStatus (Context context,String[] args) throws Exception {
       HashMap programMap  = (HashMap)JPO.unpackArgs(args);
       HashMap paramList = (HashMap) programMap.get("paramList");
       MapList objectList = (MapList)programMap.get("objectList");

       Vector enableCheckbox = new Vector();
       Iterator objectListItr = objectList.iterator();
       while(objectListItr.hasNext()) {
           Map objectMap = (Map) objectListItr.next();
           enableCheckbox.add("true".equals(objectMap.get("disableSelection")) ? "false" : "true");
       }
       return enableCheckbox;
   }

   public MapList getRouteAccessRolesSearchResults(Context context, String[] args) throws FrameworkException {
       try {
           Map programMap = (Map) JPO.unpackArgs(args);
           String sNamePattern         = (String)programMap.get("Name");
           String sRouteId             = (String)programMap.get(OBJECT_ID);
           String sSubChecked          = (String)programMap.get("chkSubLevel");
           String sTopChecked          = (String)programMap.get("chkTopLevel");
           String queryLimit           = (String)programMap.get("queryLimit");
           int roleLimit               = Integer.parseInt(queryLimit);

           ${CLASS:emxRoleUtil} roleUtilJPO = new ${CLASS:emxRoleUtil}(context, args);
           java.util.List allRolesList = roleUtilJPO.getAllRolesList(context, sRouteId, sNamePattern, sSubChecked, sTopChecked, roleLimit);
           return roleUtilJPO.getRoleListForSummaryTable(allRolesList, getExistingRoleOrGroupList(context, sRouteId));
       } catch (Exception e) {
           throw new FrameworkException(e);
       }
   }

   protected MapList getExistingRoleOrGroupList(Context context, String sRouteId) throws FrameworkException {
       try {
           MapList existingList = new MapList();
           if(sRouteId!=null && !sRouteId.equals("")) {
               Route routeObj = new Route();
               routeObj.setId(sRouteId);

               SelectList selectPersonRelStmts = new SelectList();
               selectPersonRelStmts.addElement(Route.SELECT_ROUTE_TASK_USER);

               MapList existingRoleorGroup = routeObj.getAssignedRoles(context, null, selectPersonRelStmts, false);
               if( existingRoleorGroup != null && existingRoleorGroup.size() > 0) {
                   Iterator roleItr = existingRoleorGroup.iterator();
                   while(roleItr.hasNext()) {
                       Map roleOrGroupMap  = (Map)roleItr.next();
                       String roleOrGroupName = (String) roleOrGroupMap.get(Route.SELECT_ROUTE_TASK_USER);
                       roleOrGroupName  = PropertyUtil.getSchemaProperty(context, roleOrGroupName);
                       existingList.add(roleOrGroupName);
                   }
               }
           }
           return existingList;
       } catch (Exception e) {
           throw new FrameworkException(e);
       }
   }

   public MapList getRouteAccessGroupSearchResults(Context context, String[] args) throws FrameworkException {
       try {
           Map programMap = (Map) JPO.unpackArgs(args);
           String sNamePattern         = (String)programMap.get("Name");
           String sRouteId             = (String)programMap.get(OBJECT_ID);
           String sSubChecked          = (String)programMap.get("chkSubLevel");
           String sTopChecked          = (String)programMap.get("chkTopLevel");
           String queryLimit           = (String)programMap.get("queryLimit");
           int roleLimit               = Integer.parseInt(queryLimit);

           ${CLASS:emxGroupUtil} groupUtil = new ${CLASS:emxGroupUtil}(context, args);
           java.util.List allRolesList = groupUtil.getAllGroupList(context, sNamePattern, sSubChecked, sTopChecked, roleLimit);
           return groupUtil.getGroupListForSummaryTable(allRolesList, getExistingRoleOrGroupList(context, sRouteId));
       } catch (Exception e) {
           throw new FrameworkException(e);
       }
   }

   @com.matrixone.apps.framework.ui.ProgramCallable
   public MapList getPersonsInWorkspace(Context context,String[] args) throws FrameworkException {
       try {
           HashMap programMap  = (HashMap)JPO.unpackArgs(args);
           String objectId = (String) programMap.get(OBJECT_ID);
           String scopeId = (String) programMap.get("scopeId");

           boolean hasScopeId = UIUtil.isNotNullAndNotEmpty(scopeId);
           DomainObject workspace = hasScopeId ? (DomainObject)DomainObject.newInstance(context, UserTask.getProjectId(context, scopeId)) : getScopeIdFromRouteId(context, objectId);
           if(!hasScopeId){
               DomainObject route = DomainObject.newInstance(context, objectId);
               scopeId = route.getAttributeValue(context, Route.ATTRIBUTE_RESTRICT_MEMBERS);
           }


           MapList routeMemberList = new MapList();

           SelectList typeSelects = new SelectList(1);
           typeSelects.addElement(DomainConstants.SELECT_ID);
           String routeScopeType  = "";

           String sSelRouteScope       = "to[" + DomainConstants.RELATIONSHIP_ROUTE_SCOPE + "].from.type";

           if(null != objectId && !"".equals(objectId)) {
               Route doObj = (Route)DomainObject.newInstance(context,DomainConstants.TYPE_ROUTE);
               doObj.setId(objectId);
               routeMemberList = doObj.getRouteMembers(context, typeSelects, new SelectList(), false);
           }
           StringList connectedMembers = new StringList(routeMemberList.size());
           Iterator itr = routeMemberList.iterator();
           while(itr.hasNext())
           {
               Map map = (Map)itr.next();
               connectedMembers.add((String)map.get(DomainConstants.SELECT_ID));
           }

           DomainObject doObj;
           if(UIUtil.isNotNullAndNotEmpty(scopeId)) {
               doObj = DomainObject.newInstance(context, scopeId);
               routeScopeType = doObj.getInfo(context, DomainConstants.SELECT_TYPE);
           } else {
               doObj = DomainObject.newInstance(context, objectId);
               routeScopeType = doObj.getInfo(context, sSelRouteScope);
           }

           String strType = workspace.getInfo(context,DomainConstants.SELECT_TYPE);
           if(workspace.isKindOf(context, DomainConstants.TYPE_PROJECT_SPACE) && (UIUtil.isNotNullAndNotEmpty(routeScopeType) && DomainConstants.TYPE_PROJECT_SPACE.equalsIgnoreCase(routeScopeType))) {
        	   return getPersonsUnderProjectSpace(context, workspace, connectedMembers);
           } else {
	           //Changed this API as WBI feature would only be applicable to newSecurity context enabled
	           return getPersonsUnderWorkspace_New(context,doObj,connectedMembers);
           }
       } catch (Exception e) {
           throw new FrameworkException(e);
       }
   }
   
   
      @com.matrixone.apps.framework.ui.IncludeOIDProgramCallable
   public StringList getPersonsIdListInWorkspace(Context context,String[] args) throws FrameworkException {
	   
	   StringList personList  = new StringList();
	   MapList personMap = getPersonsInWorkspace(context, args);
	   
	   Map person;
	   for(Object personMapElem:personMap){
		   person = (Map)personMapElem;
		   if(person.containsKey("id")){
			   personList.add((String)person.get("id"));
		   }
	   }

	   return personList;
	   
   }
   
   
   /*
    * To get the Users from the selected Workspace/WorkspaceVault object 
    * */
   private MapList getPersonsUnderWorkspace_New(Context context,DomainObject workspaceVault,StringList connectedMembers) throws FrameworkException {
	   try{
		   String workspaceVaultId = workspaceVault.getId(context);
		   StringList personList = new StringList();
		   MapList personMapList = new MapList();
	
	   //    personList = ${CLASS:emxDomainAccessBase}.getInclusionList(context, workspaceVaultId, "Person");
		  
		   String[] sArgs = new String[3];
		   sArgs[0] = workspaceVaultId;
		   sArgs[1] = "Person";
		   personList = JPO.invoke(context, "emxDomainAccessBase", null, "getInclusionList",sArgs, StringList.class);
		   for(int i=0;i<personList.size();i++){
			   Map tempMap = new HashMap();
			   tempMap.put(DomainConstants.SELECT_ID,personList.get(i));
			   //Not been handled for inactive users
	           if(connectedMembers.contains(personList.get(i))){
	               tempMap.put("disableSelection", "true");
	           }
			   personMapList.add(tempMap);
		   }
		   return personMapList;
	   } catch (Exception e) {
           throw new FrameworkException(e);
       }
   }

   private MapList getPersonsUnderWorkspace(Context context, DomainObject workspace, String routeScopeType, StringList connectedMembers) throws FrameworkException {
       try {
           String sState   =  DomainConstants.SELECT_CURRENT;

           MapList wsMembersList = new MapList();
           if(routeScopeType.equals(DomainConstants.TYPE_PROJECT) || routeScopeType.equals(DomainConstants.TYPE_PROJECT_VAULT) ) {
           StringList objectSelects = new StringList(4);
	           objectSelects.addElement(SELECT_ID);
           objectSelects.addElement(sState);

           MapList mapList =  workspace.getRelatedObjects(context,
	        		   							PropertyUtil.getSchemaProperty(context, "relationship_WorkspaceMember"),
	        		   							DomainConstants.TYPE_PERSON, objectSelects, null, false, true,
	        		   							(short)1, "", "", 0, null, null,  null);
           Iterator mapItr = mapList.iterator();
           while(mapItr.hasNext()) {
               Map tempMap = new HashMap();
               Map map = (Map)mapItr.next();
	               
                   String strState =(String) map.get(sState);
                   if(strState.equals("Active")) {
                       tempMap.put(DomainConstants.SELECT_ID, map.get(SELECT_ID));
                       if(connectedMembers.contains(map.get(SELECT_ID)))
                           tempMap.put("disableSelection", "true");
                       wsMembersList.add(tempMap);
                   }
               }
           }
           return wsMembersList;
       } catch (Exception e) {
           throw new FrameworkException(e);
       }
   }
   private MapList getPersonsUnderProjectSpace(Context context, DomainObject workspace, StringList connectedMembers) throws FrameworkException {
       try {
           StringList projectMemberSelects = new StringList(2);
           projectMemberSelects.add(DomainConstants.SELECT_CURRENT);
           projectMemberSelects.add(DomainConstants.SELECT_ID);

           MapList projectMemberList =  workspace.getRelatedObjects(context,
                   DomainConstants.RELATIONSHIP_MEMBER,
                   "*",
                   projectMemberSelects,
                   null,
                   false,
                   true,
                   (short)1,
                   "",
                   "",
                   0,
                   null,
                   null,
                   null);

           MapList psMembersList = new MapList();
           Iterator projectMemberItr = projectMemberList.iterator();
           while (projectMemberItr.hasNext()) {
               Map tempMap = new HashMap();
               Map projectMemberMap = (Map) projectMemberItr.next();
               String strState =(String) projectMemberMap.get(DomainConstants.SELECT_CURRENT);
               if(strState.equals("Active")) {
                   tempMap.put(DomainConstants.SELECT_ID, projectMemberMap.get(DomainConstants.SELECT_ID));
                   if(connectedMembers.contains(projectMemberMap.get(DomainConstants.SELECT_ID)))
                       tempMap.put("disableSelection", "true");
                   psMembersList.add(tempMap);
               }
           }
           return psMembersList;
        } catch (Exception e) {
            throw new FrameworkException(e);
        }
    }

   protected Workspace getScopeIdFromRouteId(Context context, String objectId) throws FrameworkException {
       DomainObject route = DomainObject.newInstance(context, objectId);
       Workspace workspace = getWorkspace(context, route.getAttributeValue(context, Route.ATTRIBUTE_RESTRICT_MEMBERS));
       return workspace;
   }
   

   @com.matrixone.apps.framework.ui.ProgramCallable
   public MapList getRolesInWorkspace(Context context,String[] args) throws FrameworkException {
/*       try {
           HashMap programMap  = (HashMap)JPO.unpackArgs(args);
           String objectId = (String) programMap.get("objectId");
           String scopeId = (String) programMap.get("scopeId");
           List roleMapList = new ArrayList();
           boolean hasScopeId = (scopeId == null || scopeId.equals("") || scopeId.equals("null")) ? false : true;
           DomainObject workspace = hasScopeId ? (DomainObject)DomainObject.newInstance(context, UserTask.getProjectId(context, scopeId)) : getScopeIdFromRouteId(context, objectId);
           String type = workspace.getInfo(context, SELECT_TYPE);
           if(type.equals(DomainConstants.TYPE_PROJECT_SPACE) || mxType.isOfParentType(context,type,DomainConstants.TYPE_PROJECT_SPACE))
           {
                roleMapList.addAll(getProjectSpaceGroupsOrRoles(context, workspace.getId(), true));
           }else{
               DomainObject scopeObject = DomainObject.newInstance(context, scopeId);
               String scopeObjectType = scopeObject.getInfo(context, DomainConstants.SELECT_TYPE);
               String sSelPersonName = "to[" + scopeObject.RELATIONSHIP_PROJECT_MEMBERSHIP + "].from.name";
        	   StringList objectSelects = new StringList(1);
        	   objectSelects.add(sSelPersonName);
        	   MapList personsMapList = workspace.getRelatedObjects(context,
        			                                           scopeObject.RELATIONSHIP_PROJECT_MEMBERS,
        			                                           scopeObject.TYPE_PROJECT_MEMBER,
										                       objectSelects,
										                       null,
										                       false,
										                       true,
										                       (short)1,
										                       "",
										                       "",
										                       0,
										                       null,
										                       null,
										                       null);
        	   StringList personNamesList = new StringList();
        	   Iterator personsMapListItr = personsMapList.iterator();
        	   while(personsMapListItr.hasNext()){
        		   Map personMap = (Map) personsMapListItr.next();
        		   personNamesList.add((String) personMap.get(sSelPersonName));
        	   }
               boolean isWorkspaceVault = DomainConstants.TYPE_WORKSPACE_VAULT.equals(scopeObjectType);
               if(isWorkspaceVault){
            	   AccessList accessList = new AccessList();
	        	   String sParentId = scopeObject.getInfo(context,"to[" + scopeObject.RELATIONSHIP_SUBVAULTS + "].from.id");
	               if(sParentId == null || sParentId.equals(""))
	               {
	                 sParentId = scopeObject.getInfo(context,"to[" + scopeObject.RELATIONSHIP_WORKSPACE_VAULTS+ "].from.id");
	               }
	               workspace.setId(sParentId);
	        	   accessList = workspace.getAccessForGrantor(context, AccessUtil.WORKSPACE_ACCESS_GRANTOR);
	        	   if(accessList != null && accessList.size() > 0){
	        		   Iterator accessListItr = accessList.iterator();
	        		   while(accessListItr.hasNext()){
	        			   Access access = (Access) accessListItr.next();
	        			   String userName = access.getUser();
	        		       if(!personNamesList.contains(userName) && access.hasReadAccess()){
	        			       roleMapList.add(userName);
	        		       }
	        		   }
	        	   }
	           }else{
	        	   StringList granteeList = workspace.getGrantees(context);
	               if((granteeList != null && granteeList.size() > 0)) {
	                   Iterator granteeListItr  = granteeList.iterator();
	                   while(granteeListItr.hasNext()) {
	                       String memberName = (String)granteeListItr.next();
	                       if(!personNamesList.contains(memberName) && !roleMapList.contains(memberName)) {
	                           roleMapList.add(memberName);
	                       }
	                   }
	               }
	           }
           }
           return roleMapList.size() > 0 ?
                  new ${CLASS:emxRoleUtil}(context, args).getRoleListForSummaryTable(roleMapList, getExistingRoleOrGroupList(context, objectId)) :
                  new MapList();

       } catch (Exception e) {
           throw new FrameworkException(e);
       }*/
	   return new MapList();
   }

   @com.matrixone.apps.framework.ui.ProgramCallable
   public MapList getGroupsInWorkspace(Context context,String[] args) throws FrameworkException {
/*        try
        {
            HashMap programMap  = (HashMap)JPO.unpackArgs(args);
            String objectId = (String) programMap.get("objectId");
            String scopeId = (String) programMap.get("scopeId");
            List groupMapList = new ArrayList();
            boolean hasScopeId = (scopeId == null || scopeId.equals("") || scopeId.equals("null")) ? false : true;
            DomainObject workspace = hasScopeId ? (DomainObject)DomainObject.newInstance(context, UserTask.getProjectId(context, scopeId)) :      getScopeIdFromRouteId(context, objectId);

           String type = workspace.getInfo(context, SELECT_TYPE);
           if(type.equals(DomainConstants.TYPE_PROJECT_SPACE) || mxType.isOfParentType(context,type,DomainConstants.TYPE_PROJECT_SPACE))
           {
                groupMapList.addAll(getProjectSpaceGroupsOrRoles(context, workspace.getId(), false));
           }
           return groupMapList.size() > 0 ? new ${CLASS:emxRoleUtil} (context, args).getRoleListForSummaryTable(groupMapList, getExistingRoleOrGroupList(context, objectId)) : new MapList();
       } catch (Exception e) {
           throw new FrameworkException(e);
       }*/
	   return new MapList();
   }

    protected List getProjectSpaceGroupsOrRoles(Context context, String projectSpaceId, boolean roles) throws FrameworkException
    {
        try {
            List retList = new ArrayList();

            //get the grants from the projectspace
            String cmd = "print bus " + projectSpaceId + " select " +
            "to[" + DomainConstants.RELATIONSHIP_PROJECT_ACCESS_LIST + "].from.grantee " +
            "to[" + DomainConstants.RELATIONSHIP_PROJECT_ACCESS_LIST + "].from.grantee.isarole " +
            "to[" + DomainConstants.RELATIONSHIP_PROJECT_ACCESS_LIST + "].from.grantee.isagroup dump " + DOMAIN_DELIMITER;

            String result = MqlUtil.mqlCommand(context, cmd);


            StringTokenizer st = new StringTokenizer(result, DOMAIN_DELIMITER);
            int nTokens = st.countTokens();
            //number of selectables in the mql command is 3
            int numGrantees = nTokens / 3;
            String[] grantees = new String[nTokens];

            int i = 0;
            while (st.hasMoreTokens())
            {
                grantees[i] = st.nextToken();
                i++;
            }

            //iterate through the result of the mql command to obtain the
            //group or role name
            //sample output of the mqlcommand
            //Employee|Test Everything|TRUE|FALSE
            for(int n = 0; n < numGrantees; n++)
            {
                if ((roles == true && grantees[n+numGrantees].equalsIgnoreCase("true")) ||
                    (roles == false && grantees[n+numGrantees*2].equalsIgnoreCase("true")))
                {
                    retList.add(grantees[n]);
                }
            }
            return retList;
       } catch (Exception e) {
           throw new FrameworkException(e);
       }
   }

   @com.matrixone.apps.framework.ui.ProgramCallable
   public MapList getMemberLists(Context context,String[] args) throws FrameworkException {
       return new MapList();
   }

   protected Workspace getWorkspace(Context context, String objectId) throws FrameworkException {
       try {
           DomainObject domObject = DomainObject.newInstance(context, objectId);
           Workspace workspaceObject = (Workspace)DomainObject.newInstance(context, TYPE_WORKSPACE);
           String type = domObject.getInfo(context, SELECT_TYPE);
           if(TYPE_PROJECT.equals(type)) {
               workspaceObject.setId(objectId);
           } else if(TYPE_PROJECT_VAULT.equals(type) ) {
               String sid = UserTask.getProjectId(context,objectId);
               workspaceObject.setId(sid);
           } else if(TYPE_DOCUMENT.equals(type) )  {
               String sid  = domObject.getInfo(context, "to[" + RELATIONSHIP_VAULTED_DOCUMENTS + "].from.to[" + RELATIONSHIP_PROJECT_VAULTS + "].from.id");
               workspaceObject.setId(sid);
           } else if(type.equals(DomainConstants.TYPE_PROJECT_SPACE) || mxType.isOfParentType(context,type,DomainConstants.TYPE_PROJECT_SPACE)) {
               workspaceObject.setId(objectId);
           }
           return workspaceObject;
       } catch (Exception e) {
           throw new FrameworkException(e);
       }
   }

   public StringList getPersonIDsAddRouteMemberPersonSearch(Context context,String[] args) throws FrameworkException {
       try {
           HashMap programMap  = (HashMap)JPO.unpackArgs(args);
           StringBuffer buffer = new StringBuffer(100);
           buffer.append("print bus ").append(PersonUtil.getUserCompanyId(context)).
           append(" select from[").append(RELATIONSHIP_EMPLOYEE).append("].to.id dump |");

           return StringUtil.split(MqlUtil.mqlCommand(context, buffer.toString()), "|");
       } catch (Exception e) {
           throw new FrameworkException(e);
       }
   }
   /**
    * Access Function for APPRouteEditActionLink command.
    *
    * @param context the eMatrix <code>Context</code> object.
    * @param args contains a packed HashMap with the following entries:
    * objectId - Object Id of the Route object.
    * @return boolean true or false values.
    * @throws Exception if the operation fails.
    * @since CommonComponents R211
    * */
   public boolean checksToEditRoute(Context context,String[] args) throws Exception {
       
       HashMap programMap         = (HashMap) JPO.unpackArgs(args);
       String objectId            = (String) programMap.get(OBJECT_ID);

       StringList selectables = new StringList();
       selectables.add(SELECT_CURRENT);
       selectables.add(SELECT_OWNER);
       selectables.add("current.access[modify]");

       DomainObject boObj = DomainObject.newInstance(context, objectId);
       Map objInfo = boObj.getInfo(context, selectables);
       
       String sOwner = (String)objInfo.get(SELECT_OWNER);
       
       return isRouteEditable(context,objInfo) && 
               ("true".equalsIgnoreCase(boObj.getInfo(context, "current.access[modify]"))); 
       
   }
   /**
    * Access Function for APPRouteSetTaskEscalationLink command.
    *
    * @param context the eMatrix <code>Context</code> object.
    * @param args contains a packed HashMap with the following entries:
    * objectId - Object Id of the Route object.
    * @return boolean true or false values.
    * @throws Exception if the operation fails.
    * @since CommonComponents R211
    * */
   public boolean checksToSetTaskEscalation(Context context,String[] args) throws Exception {
       
       HashMap programMap         = (HashMap) JPO.unpackArgs(args);
       String objectId            = (String) programMap.get(OBJECT_ID);

       StringList selectables = new StringList();
       selectables.add(SELECT_CURRENT);
       selectables.add(SELECT_OWNER);
       
       DomainObject boObj = DomainObject.newInstance(context, objectId);
       Map objInfo = boObj.getInfo(context, selectables);
       String sOwner = (String)objInfo.get(SELECT_OWNER);       
       
       return isRouteEditable(context,objInfo) && (sOwner.equals(context.getUser())); 
       
   }
   /**
    * Returns boolean true/false based on the current state of the Route.
    *
    * @param context the eMatrix <code>Context</code> object.
    * @param Map - objInfo containing teh current state of the Route.
    * @return Boolean true or false.
    * @throws Exception if the operation fails.
    * @since CommonComponents R211
    * */
   protected boolean isRouteEditable(Context context, Map objInfo) throws Exception {
       boolean isRouteEditable = true;
       String sState = (String)objInfo.get(SELECT_CURRENT);       
       
       // Do not show links if the Route State is Complete or Archive
       if(sState.equals("Complete") || sState.equals("Archive")){
          isRouteEditable = false;
       }
       return isRouteEditable;
   }
   /**
    * Returns the displayLink based on the Route Status.
    *
    * @param context the eMatrix <code>Context</code> object.
    * @param objectId - Object Id of the Route object.
    * @return String displayLink.
    * @throws Exception if the operation fails.
    * @since CommonComponents R211
    * */
   protected String getDisplayLink(Context context, String objectId) throws Exception {
       DomainObject boObj = DomainObject.newInstance(context, objectId);
       StringList routeSelects = new StringList();
       routeSelects.add(Route.SELECT_ROUTE_STATUS);
       routeSelects.add(SELECT_OWNER);
       Map routeMap = boObj.getInfo(context, routeSelects);
       String routeStatus= (String) routeMap.get(Route.SELECT_ROUTE_STATUS) ;
       if(!context.getUser().equals((String) routeMap.get(SELECT_OWNER))){
    	   return "";
       }else{
       return "Not Started".equals(routeStatus) ? "StartRoute" :
			  "Stopped".equals(routeStatus) 	? "ResumeAndRestartRoute" :
			  "Started".equals(routeStatus) 	? "StopRoute" : "";
       }
   }
   /**
    * Access Function for APPRouteStopActionLink command.
    *
    * @param context the eMatrix <code>Context</code> object.
    * @param args contains a packed HashMap with the following entries:
    * objectId - Object Id of the Route object.
    * @return boolean true or false values.
    * @throws Exception if the operation fails.
    * @since CommonComponents R211
    * */
   public boolean checksToRouteStopActionLink(Context context, String args[]) throws Exception {
       HashMap programMap         = (HashMap) JPO.unpackArgs(args);
       String objectId            = (String) programMap.get(OBJECT_ID);
       
       return "StopRoute".equals(getDisplayLink(context, objectId));
   }
   /**
    * Access Function for APPRouteRestartActionLink command.
    *
    * @param context the eMatrix <code>Context</code> object.
    * @param args contains a packed HashMap with the following entries:
    * objectId - Object Id of the Route object.
    * @return boolean true or false values.
    * @throws Exception if the operation fails.
    * @since CommonComponents R211
    * */
   public boolean checksToRouteRestartActionLink(Context context, String args[]) throws Exception {
       HashMap programMap         = (HashMap) JPO.unpackArgs(args);
       String objectId            = (String) programMap.get(OBJECT_ID);
       
       return "ResumeAndRestartRoute".equals(getDisplayLink(context, objectId));
   }
   /**
    * Access Function for APPRouteStartActionLink command.
    *
    * @param context the eMatrix <code>Context</code> object.
    * @param args contains a packed HashMap with the following entries:
    * objectId - Object Id of the Route object.
    * @return boolean true or false values.
    * @throws Exception if the operation fails.
    * @since CommonComponents R211
    * */
   public boolean checksToRouteStartActionLink(Context context, String args[]) throws Exception {
       HashMap programMap         = (HashMap) JPO.unpackArgs(args);
       String objectId            = (String) programMap.get(OBJECT_ID);
             
       return "StartRoute".equals(getDisplayLink(context, objectId));
   }
   /**
    * Access Function for APPRouteResumeActionLink command.
    *
    * @param context the eMatrix <code>Context</code> object.
    * @param args contains a packed HashMap with the following entries:
    * objectId - Object Id of the Route object.
    * @return boolean true or false values.
    * @throws Exception if the operation fails.
    * @since CommonComponents R211
    * */
   public boolean checksToRouteResumeActionLink(Context context, String args[]) throws Exception {
       HashMap programMap         = (HashMap) JPO.unpackArgs(args);
       String objectId            = (String) programMap.get(OBJECT_ID);
	   
	   if(!"ResumeAndRestartRoute".equals(getDisplayLink(context, objectId)))
			return false;
	   //   Resume command will not be shown if the route is stopped due to task rejection
       return !isRouteStoppedDueToRejection(context, objectId);
   }
   
   /**
    * Returns true if the route is stopped due to rejection of any task
    * @param context The Matrix Context object
    * @param strRouteId The object id of route object
    * @return true if the route is stopped due to rejection of route task
    *             false if route is stopped but not due to task rejection
    * @throws Exception if operation fails or the route is not stopped
    */
   protected boolean isRouteStoppedDueToRejection(Context context, String strRouteId) throws Exception {
       boolean isRouteStoppedDueToRejection = true;

       // Initialize route object
       Route objRoute = (Route)DomainObject.newInstance(context, DomainConstants.TYPE_ROUTE);
       objRoute.setId(strRouteId);

       StringList slBusSelect = new StringList(DomainConstants.SELECT_ID);
       StringList slRelSelect = new StringList();
       String strRelPattern = DomainConstants.RELATIONSHIP_ROUTE_TASK;
       String strTypePattern = DomainConstants.TYPE_INBOX_TASK;
       String strBusWhere = "attribute[" + DomainConstants.ATTRIBUTE_APPROVAL_STATUS + "]==\"Reject\"";
       String strRelWhere = "";
       short nRecurseLevel = (short)1;
       
	   MapList mlTasks = objRoute.getRelatedObjects( context, 
                                                     strRelPattern, 
                                                     strTypePattern, 
                                                     slBusSelect, 
                                                     slRelSelect, 
                                                     true, 
                                                     false, 
                                                     nRecurseLevel, 
                                                     strBusWhere, 
                                                     strRelWhere, 
                                                     0);

 	   // If tasks found then return true else false
       if (mlTasks == null || mlTasks.size() == 0) {
           isRouteStoppedDueToRejection = false;
       }

       return isRouteStoppedDueToRejection;
   }
   
   public boolean checksToShowEditAllTaskCommand(Context context, String[] args) throws FrameworkException {
       HashMap programMap = getTaskSummaryCommandsAccessChecks(context, args);
       return ((Boolean)programMap.get("APPRouteTaskEditAll")).booleanValue();
   }
   
   public boolean checksToRemoveTask(Context context, String[] args) throws FrameworkException {
       HashMap programMap = getTaskSummaryCommandsAccessChecks(context, args);
       return ((Boolean)programMap.get("APPRouteTaskRemove")).booleanValue();
   }

   protected HashMap getTaskSummaryCommandsAccessChecks(Context context, String[] args) throws FrameworkException {
       try {
           HashMap programMap = (HashMap)JPO.unpackArgs(args);
           HashMap requestMap = (HashMap)programMap.get("requestMap");
           String objectId = (String)programMap.get(OBJECT_ID);
           objectId = objectId != null ? objectId : (String)requestMap.get(OBJECT_ID);
           
           String loggedInUser = context.getUser();

           StringList routeSelects = new StringList();
           routeSelects.add("current.access[modify]");
           routeSelects.add(SELECT_OWNER);
           routeSelects.add(SELECT_TYPE);
           routeSelects.add(SELECT_CURRENT);
           
           DomainObject contextObj = DomainObject.newInstance(context, objectId);
           Map objInfo = contextObj.getInfo(context,routeSelects);
           
           String sOwner       = (String)objInfo.get(SELECT_OWNER);
           String sTypeName    = (String)objInfo.get(SELECT_TYPE);
           String sState       = (String)objInfo.get(SELECT_CURRENT);
           String hasModifyAccess = (String)objInfo.get("current.access[modify]");

           boolean isRouteTemplate = sTypeName.equals(TYPE_ROUTE_TEMPLATE);
           boolean canEditRoute = !isRouteTemplate && !(sState.equals("Complete") || sState.equals("Archive"));
           boolean isOwner = sOwner.equals(context.getUser());
           boolean canEditTaskList = true;
           if(!isRouteTemplate && canEditRoute && isOwner) {
               String templateId = contextObj.getInfo(context , "from[" + RELATIONSHIP_INITIATING_ROUTE_TEMPLATE + "].to.id");
               if(!UIUtil.isNullOrEmpty(templateId)) {
                   DomainObject routeTempObj = DomainObject.newInstance(context ,templateId);
                   String sTaskEditSetting = routeTempObj.getAttributeValue(context, ATTRIBUTE_TASKEDIT_SETTING);
                   canEditTaskList = !"Maintain Exact Task List".equals(sTaskEditSetting);
               }
           } 
           //Can't remove tasks from Route Template
           boolean bShowTaskRemove =  !isRouteTemplate  && canEditTaskList && "true".equalsIgnoreCase(hasModifyAccess);
           boolean bShowTaskEditAll = isRouteTemplate ? "true".equalsIgnoreCase(hasModifyAccess) :
                                                        isOwner && canEditRoute;
           
           HashMap detailsMap = new HashMap();
           detailsMap.put("APPRouteTaskEditAll", Boolean.valueOf(bShowTaskEditAll));
           detailsMap.put("APPRouteTaskRemove", Boolean.valueOf(bShowTaskRemove));
           return detailsMap;
       } catch (Exception e) {
           throw new FrameworkException(e);
       }
   }
   
   public Object getRouteEditAccessComboValues(Context context, String[] args) throws Exception {
       try {
           HashMap programMap = (HashMap) JPO.unpackArgs(args);
           Map paramMap = (Map)programMap.get("paramMap");
           String languageStr = (String)paramMap.get("languageStr");
           
           String i18ReadString = EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource", new Locale(languageStr),"emxComponents.Access.Read");
           String i18ReadWriteString = EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource", new Locale(languageStr),"emxComponents.Access.ReadWrite");
           String i18AddString = EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource", new Locale(languageStr),"emxComponents.Access.Add");
           String i18RemoveString = EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource", new Locale(languageStr),"emxComponents.Access.Remove");
           String i18AddRemoveString = EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource", new Locale(languageStr),"emxComponents.Access.AddRemove");
       
           HashMap tempMap = new HashMap();
           StringList fieldRangeValues = new StringList();
           StringList fieldDisplayRangeValues = new StringList();
           
           // initialize the Stringlists fieldRangeValues
           fieldRangeValues.addElement("Read");
           fieldRangeValues.addElement("Read Write");
           fieldRangeValues.addElement("Add");
           fieldRangeValues.addElement("Remove");
           fieldRangeValues.addElement("Add Remove");
           // initialize the Stringlist fieldDisplayRangeValues with Internalized value
           fieldDisplayRangeValues.addElement(i18ReadString);
           fieldDisplayRangeValues.addElement(i18ReadWriteString);
           fieldDisplayRangeValues.addElement(i18AddString);
           fieldDisplayRangeValues.addElement(i18RemoveString);
           fieldDisplayRangeValues.addElement(i18AddRemoveString);
       
           tempMap.put("field_choices", fieldRangeValues);
           tempMap.put("field_display_choices", fieldDisplayRangeValues);
           return tempMap;
       } catch(Exception e) {
           throw new FrameworkException(e.toString());
       }
   }
   /**
    * Program to get the cell level access for Route Access Table
    *
    * @param context the eMatrix Context object
    * @param String array contains AssigneeIds for Access edit
    * @throws Matrix Exception if the operation fails
    */

   public static StringList getCellAccessforRouteEditAccess(Context context, String args[]) throws Exception {
     HashMap inputMap = (HashMap) JPO.unpackArgs(args);
     MapList objectMap = (MapList) inputMap.get("objectList");
     boolean isCellEditable = false;
     StringList returnStringList = new StringList(objectMap.size());
     StringList selects = new StringList();
     selects.add(DomainConstants.SELECT_OWNER);
     selects.add(DomainConstants.SELECT_HAS_CHANGEOWNER_ACCESS);
     selects.add(DomainConstants.SELECT_HAS_CHANGESOV_ACCESS);
     for (Iterator objectItr = objectMap.iterator(); objectItr.hasNext();) {
         Map curObjectMap = (Map) objectItr.next(); 
         String routeID = (String)curObjectMap.get("id[parent]");
         DomainObject dom = DomainObject.newInstance(context,routeID);
         Map parentObjectMap = dom.getInfo(context, selects);
         String owner=(String)parentObjectMap.get(DomainConstants.SELECT_OWNER);
         String hasChangeOwnerAccess=(String)parentObjectMap.get(DomainConstants.SELECT_HAS_CHANGEOWNER_ACCESS);
         String hasChangeSOVAccess=(String)parentObjectMap.get(DomainConstants.SELECT_HAS_CHANGESOV_ACCESS);
         if(owner.equals(context.getUser()) || "TRUE".equalsIgnoreCase(hasChangeOwnerAccess) || "TRUE".equalsIgnoreCase(hasChangeSOVAccess)){
        	 isCellEditable = "true".equals(curObjectMap.get("isAccessColumnEditable")); 
         }
       returnStringList.addElement(new Boolean(isCellEditable).toString());
     }       
     return returnStringList;
   }
   
   // Update function for Route Edit Access
   public void updateRouteAccessValue(Context context, String[] args)throws FrameworkException {
       try{
           HashMap programMap = (HashMap)JPO.unpackArgs(args);
           HashMap paramMap = (HashMap)programMap.get("paramMap");
           HashMap requestMap = (HashMap)programMap.get("requestMap");
           
           String strNewAccessValue = (String)paramMap.get("New Value");
           
           String strRouteId = (String)requestMap.get(OBJECT_ID);
           String personId = (String)paramMap.get(OBJECT_ID);
           Route routeObj = new Route();
           routeObj.setId(strRouteId);
           try {
               ContextUtil.pushContext(context); 
               String project = (DomainObject.newInstance(context, personId)).getInfo(context, DomainConstants.SELECT_NAME) + "_PRJ";
               DomainAccess.createObjectOwnership(context, strRouteId, null, project, strNewAccessValue, DomainAccess.COMMENT_MULTIPLE_OWNERSHIP, true);
               DomainObject doObj = new DomainObject();
               doObj.setId(strRouteId);
              
               String[] coObjMap = routeObj.getConnectedObjectIds(context);
               String routeName = doObj.getName(context);	           
		       for (int i = 0; i < coObjMap.length; i++) {		           
		    	    String defaultAccessMasks = DomainAccess.getPhysicalAccessMasks(context, strRouteId, strNewAccessValue);
		            DomainAccess.createObjectOwnership(context, (String) coObjMap[i], null, project, defaultAccessMasks, DomainAccess.COMMENT_MULTIPLE_OWNERSHIP_GRANTED +" " +routeName, true);
		        }
           } catch(Exception ex) {
               throw new FrameworkException(ex);
           } finally {
               ContextUtil.popContext(context);
           }
       } catch (Exception e) {
           throw new FrameworkException(e);
       }
       
   }
   
   /**
    * Returns Route Node Task Assignee value.
    * @param context
    * @param args
    * @return
    * @throws FrameworkException
    */
   public String getRouteNodeTaskAssigneeValue(Context context, String[] args) throws FrameworkException {
       try {
           Map programMap = (Map)JPO.unpackArgs(args);
           Map paramMap   = (Map)programMap.get("paramMap");
           HashMap requestMap = (HashMap)programMap.get("requestMap");
           
           String sLanguage = (String) paramMap.get("languageStr");
           String routeNodeId = (String)paramMap.get("relId");
           
           StringList selectables = new StringList(2);
           selectables.add(DomainRelationship.SELECT_TO_ID);
           selectables.add(Route.SELECT_ROUTE_TASK_USER);
           
           Map relValues = (Map) DomainRelationship.getInfo(context, new String[]{routeNodeId}, selectables).get(0);
           DomainObject dmoAssignee = new DomainObject ((String) relValues.get(DomainRelationship.SELECT_TO_ID));
           selectables.clear();
           selectables.add(SELECT_TYPE);
           selectables.add(SELECT_NAME);
           
           Map mapAssigneeInfo = dmoAssignee.getInfo (context, selectables);
           String routeNodeType = (String) mapAssigneeInfo.get(SELECT_TYPE);
           
           String rtu = (String) relValues.get(Route.SELECT_ROUTE_TASK_USER);
           String assignee  = routeNodeType.equals(TYPE_PERSON) ? PersonUtil.getFullName(context, (String) mapAssigneeInfo.get(SELECT_NAME)) :
                                     !UIUtil.isNullOrEmpty(rtu) ? getRoleGroupValue(context, sLanguage, rtu) :
                                    	 EnoviaResourceBundle.getProperty(context, "emxComponentsStringResource", new Locale(sLanguage),
                                    				"emxComponents.RouteTemplateTaskAssignees.None");
           
           return assignee;
       } catch (Exception e) {
           throw new FrameworkException(e);
    }
   }
   
   /**
    * Range values to display in Route Node Task edit form.
    * @param context
    * @param args
    * @return
    * @throws FrameworkException
    */
   public Map getRouteNodeTaskAssigneeRangeValues(Context context, String[] args) throws FrameworkException {
       try {
           Map programMap = (Map)JPO.unpackArgs(args);
           Map paramMap   = (Map)programMap.get("paramMap");
           String sLanguage = (String) paramMap.get("languageStr");
           String objectId = (String)paramMap.get(OBJECT_ID);
		   String routeNodeId = (String)paramMap.get("relId");
		   Map fieldMap = (Map)programMap.get("fieldMap");
		   StringList fieldValue = (StringList)fieldMap.get("field_value");
           
           Pattern typePattern = new Pattern(TYPE_PERSON);
           typePattern.addPattern(TYPE_ROUTE_TASK_USER);
           
           DomainObject domObj = new DomainObject(objectId);
		   StringList selectables = new SelectList(3);
		   String sTaskEditSetting="from["+RELATIONSHIP_INITIATING_ROUTE_TEMPLATE+"].to.attribute["+ATTRIBUTE_TASKEDIT_SETTING+"].value";
		   selectables.add("from["+RELATIONSHIP_INITIATING_ROUTE_TEMPLATE+"].to.id");
		   selectables.add(sTaskEditSetting);
		   selectables.add(SELECT_CURRENT);
		   Map routeInfo = domObj.getInfo(context, selectables);
		   String templateId =(String)routeInfo.get("from["+RELATIONSHIP_INITIATING_ROUTE_TEMPLATE+"].to.id");

		   String taskSetting= (String) routeInfo.get(sTaskEditSetting);
		   String currentState = (String) routeInfo.get(SELECT_CURRENT);
		   selectables = new StringList(3);
		   selectables.add(DomainRelationship.SELECT_TO_ID);
		   selectables.add(Route.SELECT_ROUTE_TASK_USER);
		   selectables.add("attribute["+DomainConstants.ATTRIBUTE_TEMPLATE_TASK+"]");		   
		   
		   Map relValues = (Map) DomainRelationship.getInfo(context, new String[]{routeNodeId}, selectables).get(0);
		   String tempTask = (String) relValues.get("attribute["+DomainConstants.ATTRIBUTE_TEMPLATE_TASK+"]");
		   boolean isTemplateTask = tempTask != null && tempTask.equals(YES);
		   Map map = new HashMap(2);
		   if(UIUtil.isNotNullAndNotEmpty(templateId)&& ("Maintain Exact Task List".equals(taskSetting)||("Extend Task List".equals(taskSetting) && isTemplateTask))){
			  DomainObject dmoAssignee = new DomainObject ((String) relValues.get(DomainRelationship.SELECT_TO_ID));
			  String assigneeId = dmoAssignee.getId(context);
			  selectables = new StringList(SELECT_NAME);
			  Map assigneeInfo = dmoAssignee.getInfo(context, selectables);
			  String fullName = PersonUtil.getFullName(context, (String)assigneeInfo.get(SELECT_NAME));
			  String id = "person#" + assigneeId + "#" + assigneeId;
			  map.put("field_choices", new StringList(id));
			  map.put("field_display_choices", new StringList(fullName));
		  
		   }
		   else{	
			   selectables = new StringList(1);
           selectables.add(SELECT_TYPE);
	           StringList relSelectables = new StringList(3);
           relSelectables.add(Route.SELECT_ROUTE_TASK_USER);
			   relSelectables.add(SELECT_TO_NAME);
			   relSelectables.add(SELECT_TO_ID);
           
           MapList routeNodeTasks = domObj.getRelatedObjects(context, 
                   RELATIONSHIP_ROUTE_NODE, typePattern.getPattern(),
                   selectables, relSelectables,
                   false, true, (short)1, 
                   EMPTY_STRING, EMPTY_STRING,
                   0);
           
           StringList rangeValues = new StringList(routeNodeTasks.size());
           StringList rangeDispay = new StringList(routeNodeTasks.size());
		   if(STATE_ROUTE_DEFINE.equals(currentState) && !("none".equals(fieldValue.get(0)))){
				rangeValues.add(0, "none#none#");
				rangeDispay.add(0, EnoviaResourceBundle.getProperty(context, "emxComponentsStringResource",new Locale(sLanguage),
						"emxComponents.RouteTemplateTaskAssignees.None"));
		   }
           StringList users = new StringList(routeNodeTasks.size());
           String rtuId = null;
           for (int i = 0; i < routeNodeTasks.size(); i++) {
               Map routeNode = (Map) routeNodeTasks.get(i);
               String routeNodeType = (String) routeNode.get(SELECT_TYPE);
 				   String id;	  
               String assigee = null;
               String assigneeDisplay = null;
               
               if(routeNodeType.equals(TYPE_PERSON)) {

					   String assigneeName = (String) routeNode.get(SELECT_TO_NAME);
					   id = (String) routeNode.get(SELECT_TO_ID);
                   assigee = "person#" + id + "#" + id;
					   assigneeDisplay = PersonUtil.getFullName(context, assigneeName);
               } else {
					   id = (String) routeNode.get(SELECT_ID);
                   String rtu = (String)routeNode.get(Route.SELECT_ROUTE_TASK_USER);
                   rtuId = UIUtil.isNullOrEmpty(rtuId) ? id : rtuId;
                   
                   boolean isRoleGroup = !UIUtil.isNullOrEmpty(rtu);
					   String rtuType = "" ;
					   if(!isRoleGroup){
						   continue;
					   }else{
						   rtuType= rtu.startsWith("role_") ? "role#" : "group#";
						   assigneeDisplay=getRoleGroupValue(context, sLanguage, rtu);
					   }
                   assigee =  rtuType + rtuId + "#" + rtu;
		
               }
               if(!users.contains(assigee)) {
                   rangeValues.add(assigee);
                   rangeDispay.add(assigneeDisplay);
                   users.add(assigee);
               }
           }
           map.put("field_choices", rangeValues);
           map.put("field_display_choices", rangeDispay);
		   }
           return map; 
       } catch (Exception e) {
           throw new FrameworkException(e);
       }
   }

   protected String getRoleGroupValue(Context context, String sLanguage, String roleGroup) throws FrameworkException {
       try {
           String taskAssignee = PropertyUtil.getSchemaProperty(context, roleGroup);
           boolean isRole = roleGroup.substring(0, roleGroup.indexOf("_")).equals("role");
           taskAssignee =  isRole ? i18nNow.getRoleI18NString(taskAssignee, sLanguage) :
                                    i18nNow.getAdminI18NString("Group", taskAssignee, sLanguage);
           
           String key = isRole ? "emxFramework.Common.Role" : "emxFramework.Common.Group";
           StringBuffer buffer = new StringBuffer(40);
           buffer.append(taskAssignee);
           buffer.append(" (").append(EnoviaResourceBundle.getProperty(context,"emxFrameworkStringResource",new Locale(sLanguage),key)).append(')');
           return buffer.toString();
       } catch (Exception e) {
           throw new FrameworkException(e);
       }
   }
   
   /**
    * Range values for Route Action in Route Node Task Edit form.
    * @param context
    * @param args
    * @return
    * @throws FrameworkException
    */
   
   public Map getRouteNodeTaskRouteActionValues(Context context, String[] args) throws FrameworkException {
       try {
           Map programMap = (Map)JPO.unpackArgs(args);
           Map paramMap   = (Map)programMap.get("paramMap");
           String sLanguage = (String) paramMap.get("languageStr");
           
           DomainObject domObj = new DomainObject((String) paramMap.get(OBJECT_ID));
           String routeBasePurpose = domObj.getAttributeValue(context, ATTRIBUTE_ROUTE_BASE_PURPOSE);
           
           StringList validRangeValues = new StringList(3);
           if("Standard".equals(routeBasePurpose)) {
               validRangeValues.add("Approve");
               validRangeValues.add("Comment");
               validRangeValues.add("Notify Only");
           } else if("Approval".equals(routeBasePurpose)) {
               validRangeValues.add("Approve");
           } else if("Review".equals(routeBasePurpose)) {
               validRangeValues.add("Comment");
           }  

           StringList displayValue = new StringList(validRangeValues.size());
           for (int i = 0; i < validRangeValues.size(); i++) {
               displayValue.add(i18nNow.getRangeI18NString(ATTRIBUTE_ROUTE_ACTION, (String)validRangeValues.get(i), sLanguage));               
           }
           
           HashMap resultMap = new HashMap();
           resultMap.put("field_choices", validRangeValues);
           resultMap.put("field_display_choices", displayValue);
           return  resultMap;
           
       } catch (Exception e) {
           throw new FrameworkException(e);
       }
   }
   
   /**
    * Update Program for Route Node Task details.
    * @param context
    * @param args
    * @return
    * @throws FrameworkException
    */
   @com.matrixone.apps.framework.ui.PostProcessCallable
   public HashMap updateRouteNodeTask(Context context, String[] args) throws FrameworkException {
       try {
           Map programMap = (Map)JPO.unpackArgs(args);
           Map paramMap   = (Map)programMap.get("paramMap");
           
           String routeId = (String) paramMap.get(OBJECT_ID);
           DomainObject domObj = DomainObject.newInstance(context, routeId);
           StringList objectSelects = new StringList();
           objectSelects.add(DomainConstants.ATTRIBUTE_ORIGINATOR);
           objectSelects.add(SELECT_TYPE);
           objectSelects.add(SELECT_NAME);
           
           Map objInfoMap = domObj.getInfo(context, objectSelects);
           
           String routeOwner = (String) objInfoMap.get(DomainConstants.ATTRIBUTE_ORIGINATOR);
           String ObjType =  (String) objInfoMap.get(SELECT_TYPE);
           if(TYPE_ROUTE_TEMPLATE.equals(ObjType)) {
               return JPO.invoke(context, "emxRouteTemplate", null, "updateRouteNodeTaskForRouteTemplate", args, HashMap.class) ;
           }
           
           String relId = (String) paramMap.get("relId");
           DomainRelationship domRel = DomainRelationship.newInstance(context, relId);
           
           HashMap requestMap = (HashMap)programMap.get("requestMap");
           String sLanguage = (String) requestMap.get("languageStr");
           Locale locale = UIUtil.isNullOrEmpty(sLanguage) ? context.getLocale() : new Locale(sLanguage);

           HashMap resultsMap = new HashMap();
           Map newValues = new HashMap(3);
           
           String selAttrAssigneeDueDate = getAttributeSelect(ATTRIBUTE_ASSIGNEE_SET_DUEDATE);
           String selAttrDueDateOffcet = getAttributeSelect(ATTRIBUTE_DUEDATE_OFFSET);
           
           StringList selectables = new StringList(5);
           selectables.add(DomainRelationship.SELECT_TO_ID);
           selectables.add(Route.SELECT_ROUTE_TASK_USER);
           selectables.add(Route.SELECT_SCHEDULED_COMPLETION_DATE);
           selectables.add(selAttrAssigneeDueDate);
           selectables.add(selAttrDueDateOffcet);
           
           Map relValues = (Map) DomainRelationship.getInfo(context, new String[]{relId}, selectables).get(0);

           String dueDate    = (String)requestMap.get("DueDate");
           String dueTime    = (String)requestMap.get("routeTime");
           
           boolean dueDateEmpty = UIUtil.isNullOrEmpty(dueDate);
           // Owner and assignee can change (if assignee set due date)
           boolean checkDueDate = (YES.equals(relValues.get(selAttrAssigneeDueDate)) && !dueDateEmpty &&
                                  UIUtil.isNullOrEmpty((String) relValues.get(selAttrDueDateOffcet)) || context.getUser().equalsIgnoreCase(routeOwner));
           
           if(dueDateEmpty && checkDueDate) {
               //resultsMap.put("Message", ComponentsUtil.i18nStringNow("emxComponents.AssignTaskDialog.NotCalendarOption", sLanguage));
               resultsMap.put("Message", ComponentsUtil.i18nStringNow("emxComponents.InboxTask.AlertDueDate", sLanguage));
               return resultsMap;
           } 
           if(!locale.toString().startsWith("en") && UIUtil.isNotNullAndNotEmpty(dueDate) ){
        	   dueDate = eMatrixDateFormat.getFormattedInputDate(dueDate, Double.valueOf((String)requestMap.get("timeZone")), i18nNow.getLocale(sLanguage));
           }
           if(checkDueDate && ComponentsUIUtil.isPastDate(eMatrixDateFormat.getJavaDate(dueDate), dueTime)) {
               resultsMap.put("Message", ComponentsUtil.i18nStringNow("emxComponents.EditTaskDetails.DateMessage", sLanguage)); 
               return resultsMap;
           }
           
           
           if(checkDueDate) {
               String taskScheduledDate = (String) relValues.get(Route.SELECT_SCHEDULED_COMPLETION_DATE);
               String timeZone = (String)requestMap.get("timeZone");
               double clientTZOffset   = (new Double(timeZone)).doubleValue();
               
               dueDate = eMatrixDateFormat.getFormattedDisplayDate(dueDate, clientTZOffset);
               
               String taskScheduledDateNew   =  eMatrixDateFormat.
                                                   getFormattedInputDateTime(context,dueDate, dueTime, clientTZOffset, locale);
               if(!taskScheduledDate.equals(taskScheduledDateNew)) {
                   newValues.put(ATTRIBUTE_SCHEDULED_COMPLETION_DATE, taskScheduledDateNew);
               }
           }               
                  
           DomainObject dmoAssignee = new DomainObject ((String) relValues.get(DomainRelationship.SELECT_TO_ID));
           selectables.clear();
           selectables.add(SELECT_TYPE);
           selectables.add(SELECT_NAME);
           selectables.add(SELECT_ID);
           
           Map mapAssigneeInfo = dmoAssignee.getInfo (context, selectables);
           String routeNodeType = (String) mapAssigneeInfo.get(SELECT_TYPE);

           boolean isConnectedToRTU = routeNodeType.equals(TYPE_ROUTE_TASK_USER);
           String currentAssignee = (String) (isConnectedToRTU ? relValues.get(Route.SELECT_ROUTE_TASK_USER) : mapAssigneeInfo.get(SELECT_ID));
           
           /**
            * newTaskAssignee will be in the following format
            * Person -> person#PERSON_OBJ_ID#PERSON_OBJ_ID 
            * Role   -> role#RTU_OBJ_ID#Role_SYMB_NAME
            * Group  -> group#RTU_OBJ_ID#GROUP_SYMB_NAME
            * None   -> none#RTU_OBJ_ID#
            * 
            * 0th Element -> Assignee Type
            * 1st Element -> Obj to be connected -> RTU/Person Id
            * 2nd Element -> Role/Group symbolic name
            *                Empty for None
            *                Person Id for Person
            */
           String newTaskAssignee    = (String)requestMap.get("Assignee");
           StringList newTaskAssigneeInfo = StringUtil.split(newTaskAssignee, "#");
           String newAssigneeValue = (String) newTaskAssigneeInfo.get(2);
           if(!currentAssignee.equals(newAssigneeValue)) {
			   DomainObject rtaskUser;
               String newAssigneeType = (String) newTaskAssigneeInfo.get(0);
               String newAssigneeID = (String) newTaskAssigneeInfo.get(1);
			   if ("none".equals(newAssigneeType)){
        		   // Route.getRouteTaskUserObject() creates RTU object if no RTU is already connected and returns if the boolean parameter is passed as true
        		   // If RTU is already connected then it returns the RTU object
                   rtaskUser = Route.getRouteTaskUserObject(context, domObj, true);
                   newAssigneeID = rtaskUser.getId(context);
               }              
               boolean connectToRTU = !"person".equals(newAssigneeType);
               
               DomainRelationship.setToObject(context, relId, new DomainObject(newAssigneeID));
               newValues.put(ATTRIBUTE_ROUTE_TASK_USER, connectToRTU ? newAssigneeValue : EMPTY_STRING);
           }
           
           newValues.put(ATTRIBUTE_ROUTE_ACTION, (String) requestMap.get("Action"));
           newValues.put(ATTRIBUTE_ALLOW_DELEGATION, (String) requestMap.get("AllowDelegation"));
           newValues.put(ATTRIBUTE_ROUTE_INSTRUCTIONS, (String) requestMap.get("Instructions"));

           DomainRelationship.setAttributeValues(context, relId, newValues);
		   if(DomainConstants.TYPE_PERSON.equals(mapAssigneeInfo.get("type"))){
  			  Route.revokeAccessForOldAssigneeOnReassigningToGroup(context, routeId, ObjType, (String) objInfoMap.get(SELECT_NAME), (String)mapAssigneeInfo.get("name"), (String)mapAssigneeInfo.get("type"), (String)mapAssigneeInfo.get("id") );	 
  		   }
           
           return resultsMap;
       } catch (Exception e) {
           throw new FrameworkException(e);
       }
   }
   
   /**
    * This is dummy update program for Route Node Task edit form.
    * We will be modifying the attributes in the post process JPO.
    * @param context
    * @param args
    * @throws FrameworkException
    */
   public void updateRouteNodeTaskDummy(Context context, String[] args) throws FrameworkException {
       return;
   }
   
   /**
    * Program HTML field for Route Node Task, Due Date field.
    * @param context
    * @param args
    * @return
    * @throws Exception
    */
   public String getRouteNodeTaskDueDateField(Context context, String[] args)throws Exception
   {
       HashMap programMap = (HashMap)JPO.unpackArgs(args);
       Map paramMap   = (Map)programMap.get("paramMap");
       HashMap requestMap = (HashMap)programMap.get("requestMap");
       
       String selTaskDueDate         = getAttributeSelect(DomainConstants.ATTRIBUTE_SCHEDULED_COMPLETION_DATE);
       String selDueDateOffset       = getAttributeSelect(DomainConstants.ATTRIBUTE_DUEDATE_OFFSET);
       String selDueDateOffsetFrom   = getAttributeSelect(DomainConstants.ATTRIBUTE_DATE_OFFSET_FROM);
       String selAssigneeSetDueDate   = getAttributeSelect(DomainConstants.ATTRIBUTE_ASSIGNEE_SET_DUEDATE);
       
       StringList relSelectables = new StringList();
       relSelectables.addElement(selTaskDueDate);
       relSelectables.addElement(selDueDateOffset);
       relSelectables.addElement(selDueDateOffsetFrom);
       relSelectables.addElement(selAssigneeSetDueDate);
       String objectId = (String)paramMap.get(OBJECT_ID);
       String relId = (String) paramMap.get("relId");
       String languageStr = (String)paramMap.get("languageStr");
       
       MapList mlRouteNodeInfo = DomainRelationship.getInfo(context, new String[]{relId}, relSelectables);
       String taskDueDate = (String)((Map)mlRouteNodeInfo.get(0)).get(selTaskDueDate);
	   String dueDateOffset = (String)((Map)mlRouteNodeInfo.get(0)).get(selDueDateOffset);
	   String dueDateOffsetFrom = (String)((Map)mlRouteNodeInfo.get(0)).get(selDueDateOffsetFrom);
	   String assigneeSetDueDate = (String)((Map)mlRouteNodeInfo.get(0)).get(selAssigneeSetDueDate);
	   boolean bDueDateEmpty  = UIUtil.isNullOrEmpty(taskDueDate) ? true : false;
       boolean bDeltaDueDate = (!UIUtil.isNullOrEmpty(dueDateOffset) && bDueDateEmpty) ? true : false;
       StringBuffer sb = new StringBuffer();
       if(UIUtil.isNotNullAndNotEmpty(assigneeSetDueDate) && YES.equalsIgnoreCase(assigneeSetDueDate)){
    	   sb.append(EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource", new Locale(languageStr),"emxComponents.AssignTasksDialog.AssigneeDueDate"));
       }else if(!bDeltaDueDate){
         	String timeZone = (String) requestMap.get("timeZone");
    		int iDateFrm = new Integer(PersonUtil.getPreferenceDateFormatString(context).trim()).intValue();
            double clientTZOffset   = (new Double(timeZone)).doubleValue();
			taskDueDate=   eMatrixDateFormat.getFormattedDisplayDateTime(context, taskDueDate, true, iDateFrm, clientTZOffset, context.getLocale());
           	sb.append(taskDueDate);
	   }else{			
           sb.append(dueDateOffset).append(" ").append(EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource", new Locale(languageStr),"emxComponents.common.DaysFrom")).
           append(" ").append(i18nNow.getRangeI18NString( DomainConstants.ATTRIBUTE_DATE_OFFSET_FROM, dueDateOffsetFrom,languageStr));
       }
       
       return sb.toString();
   }
   
   /**
    * Post process JPO for Delete Routes
    * @param context
    * @param args
    * @return
    * @throws FrameworkException
    */       
       @com.matrixone.apps.framework.ui.PostProcessCallable
       public Map deleteSelectedRoutes(Context context, String args[]) throws FrameworkException {
           try {
               Map programMap = (Map)JPO.unpackArgs(args);
               Map requestValuesMap = (Map) programMap.get("requestValuesMap");
               
               Map returnMap = new HashMap();
               
               String languageStr	= (String) requestValuesMap.get("languageStr");
               String rowIds         =  requestValuesMap.get("rowIds") != null ? 
                                        ((String[])requestValuesMap.get("rowIds"))[0] : null;
               String sMessage       =  requestValuesMap.get("NotificationMessage") != null ? 
                                         ((String[])requestValuesMap.get("NotificationMessage"))[0] : "";

                                        
               if(UIUtil.isNullOrEmpty(rowIds)) {
                   return returnMap;
               }
               
               String notificationSub = ComponentsUtil.i18nStringNow("emxComponents.DeleteRoute.DeleteNotification", languageStr);
               
               SelectList selectables = new SelectList(4);
               selectables.addId();
               selectables.addType();
               selectables.addName();
               selectables.addOwner();
               Route route = (Route)DomainObject.newInstance(context,DomainConstants.TYPE_ROUTE);
               
               String[] routeIds = StringUtils.split(rowIds, ",");
               MapList routeInfo = DomainObject.getInfo(context, routeIds, selectables);
               for (int i = 0; i < routeIds.length; i++) {
                   route.setId(routeIds[i]);
                   Map routeDetails = (Map) routeInfo.get(i);
                   String name = (String) routeDetails.get(SELECT_NAME);
                   String owner = (String)routeDetails.get("owner");
                   String type = i18nNow.getAdminI18NString("Type", (String) routeDetails.get(SELECT_TYPE), languageStr);
                   StringBuffer buffer = new StringBuffer(50);
                   buffer.append(type).append(" ").append(name).append(" ").append(notificationSub);
                   Access  access = route.getAccessMask(context);
       	           boolean deleteAccessFlag=access.hasDeleteAccess();
                   try {
                	   //When we attached newly created route to any object , we are pushing the context.and user is able to create the route on that object.
                	   //But while deleting user we were not pushing the context.So , first checking whether user having delete access or not and then pushing the context.IR-490696-3DEXPERIENCER2015x 
                	   if (deleteAccessFlag) {
                       route.deleteRoute(context, sMessage, buffer.toString());
                	   } else {
                		   String message = EnoviaResourceBundle.getProperty(context,
                                   "emxFrameworkStringResource",
                                   context.getLocale(),
                                   "emxFramework.Alert.CannotDeleteRoute");
                		   message=UIExpression.substituteValues(context, message, routeIds[i]);
                		    returnMap.put("Message", message);
                		    return returnMap;
                	   }
                   } catch (Exception e) {
                       returnMap.put("Message", e.getMessage());
                   }
               }
               return returnMap;
           } catch (Exception e) {
               throw new FrameworkException(e);
           }
       }
       
	@com.matrixone.apps.framework.ui.ProgramCallable
	public MapList getRouteAssigneesToSelect(Context context, String[] args) throws Exception{
       HashMap programMap = (HashMap)JPO.unpackArgs(args);
       HashMap requestMap = (HashMap)programMap.get("RequestValuesMap");
       String[] personId  = (String[]) requestMap.get("personList");
       String[] rolesList = (String[]) requestMap.get("roleList");
       String[] groupsList = (String[]) requestMap.get("groupList");
       String[] noneOption =  (String[]) requestMap.get("showNone");
       
       StringList persons = StringUtil.split(personId[0], "|");
       StringList uniquePersonList=new StringList();
       for(int i1=0;i1<persons.size();i1++){
    	   if(!uniquePersonList.contains(persons.get(i1))){
    		   uniquePersonList.add(persons.get(i1));
    	   }
       }     
       StringList roles = StringUtil.split(rolesList[0], "|");
       StringList groups = StringUtil.split(groupsList[0], "|");
       boolean showNoneOption = noneOption == null || !"false".equalsIgnoreCase(noneOption[0]);
       
       
       MapList detailsMap = new MapList(uniquePersonList.size() + roles.size() + groups.size());
       for (int i = 0; i < uniquePersonList.size(); i++) {
          Map map = new HashMap();
          DomainObject person = new DomainObject((String)uniquePersonList.get(i));
          map.put(SELECT_ID, uniquePersonList.get(i)+"~"+person.getName(context));
          map.put("UserType", "person");
          detailsMap.add(map);
       }
       for (int i = 0; i < roles.size(); i++) {
          Map map = new HashMap();
          map.put(SELECT_ID, "Role~"+(String)roles.get(i));
          map.put("UserType", "role");
          detailsMap.add(map);
       }
       for (int i = 0; i < groups.size(); i++) {
          Map map = new HashMap();
          map.put(SELECT_ID, "Group~"+groups.get(i));
          map.put("UserType", "group");
          detailsMap.add(map);
       }
       if(showNoneOption) {
           Map map = new HashMap();
           map.put(SELECT_ID, "none~none");
           map.put("UserType", "none");
           detailsMap.add(map);
       }
       return detailsMap;
}
   
	public StringList getUserSummaryTableName(Context context, String[] args) throws FrameworkException {
	    try {
	        Map programMap = (Map) JPO.unpackArgs(args);
	        MapList objs =  (MapList) programMap.get("objectList");
	        HashMap paramList = (HashMap) programMap.get("paramList");
	        String languageStr = (String) paramList.get("languageStr");
	        StringList colData = new StringList(objs.size());
	        
	        for (int i = 0; i < objs.size(); i++) {
	            Map map = (Map) objs.get(i);
	            String id = (String) map.get(SELECT_ID);
	            String userType = (String) map.get("UserType");
	            if("person".equalsIgnoreCase(userType)) {
	                StringTokenizer st = new StringTokenizer(id,"~");
	                while(st.hasMoreTokens()){
	                    DomainObject person = new DomainObject(st.nextToken());
	                    colData.add(PersonUtil.getFullName(context,st.nextToken()));
	                }
	            } else if("role".equalsIgnoreCase(userType)) {
	                StringTokenizer st = new StringTokenizer(id,"~");
	                while(st.hasMoreTokens()){
	                    String id1 = st.nextToken();
	                    colData.add(i18nNow.getRoleI18NString(st.nextToken(), languageStr));
	                }
	            } else if("group".equalsIgnoreCase(userType)) {
	                StringTokenizer st = new StringTokenizer(id,"~");
	                while(st.hasMoreTokens()){
	                    String id1 = st.nextToken();
	                    colData.add(i18nNow.getAdminI18NString("Group",st.nextToken(),languageStr));
	                }
	            } else {
                    colData.add(ComponentsUtil.i18nStringNow("emxComponents.Common.None", languageStr));
                }
	        }
	        
	        return colData;
	    } catch (Exception e) {
	        throw new FrameworkException(e);
	    } 
	}
   
	public StringList getUserSummaryTableType(Context context, String[] args) throws FrameworkException {
	    try {
	        Map programMap = (Map) JPO.unpackArgs(args);
	        MapList objs =  (MapList) programMap.get("objectList");
	        HashMap paramList = (HashMap) programMap.get("paramList");
	        String languageStr = (String) paramList.get("languageStr");
	        StringList colData = new StringList(objs.size());
	        
	        for (int i = 0; i < objs.size(); i++) {
	            Map map = (Map) objs.get(i);
	            String id = (String) map.get(SELECT_ID);
	            String userType = (String) map.get("UserType");
	            if("person".equalsIgnoreCase(userType)) {
	                colData.add(EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource",new Locale(languageStr),"emxComponents.Common.Person"));   
	            } else if("role".equalsIgnoreCase(userType)) {
	                colData.add(EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource",new Locale(languageStr),"emxComponents.Common.Role"));
	            } else if("group".equalsIgnoreCase(userType)) {
	                colData.add(EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource",new Locale(languageStr),"emxComponents.Common.Group"));
	            } else {
                    colData.add("");
                }
	        } 
	        
	        return colData;
	    } catch (Exception e) {
	        throw new FrameworkException(e);
	    } 
	}
   
	public StringList getUserSummaryTableOrganization(Context context, String[] args) throws FrameworkException {
	    try {
	        Map programMap = (Map) JPO.unpackArgs(args);
	        MapList objs =  (MapList) programMap.get("objectList");
	        StringList colData = new StringList(objs.size());
	        
	        for (int i = 0; i < objs.size(); i++) {
	            Map map = (Map) objs.get(i);
	            String id = (String) map.get(SELECT_ID);
	            String userType = (String) map.get("UserType");
	            if("person".equalsIgnoreCase(userType)) {
	                Person per = new Person(id);
	                colData.add(per.getCompany(context).getInfo(context, SELECT_NAME));
	            } else {
	                colData.add("");
	            } 
	        }
	        return colData;
	    } catch (Exception e) {
	        throw new FrameworkException(e);
	    } 
	}
	public static  Object getRouteCompletionActionsForRouteCreate(Context context, String[] args
            ) throws Exception, FrameworkException {

		HashMap programMap = (HashMap) JPO.unpackArgs(args);
	    HashMap requestMap = (HashMap) programMap.get("requestMap");
	    HashMap paramMap = (HashMap) programMap.get("paramMap");
	    String sAttrRouteCompletionAction = PropertyUtil.getSchemaProperty(context, "attribute_RouteCompletionAction" );
	    // Get the required parameter values from  "programMap" - as required

	    String  languageStr = (String) paramMap.get("languageStr"); 
		String  objectId = (String) requestMap.get(OBJECT_ID);
		boolean bShowRouteAction = true;
		if(objectId != null){
			DomainObject relatedObject = new DomainObject(objectId);
			bShowRouteAction = FrameworkUtil.hasAccess(context, relatedObject, "promote");
		}
		// initialize the return variable HashMap tempMap = new HashMap();
		HashMap tempMap = new HashMap();
		// initialize the Stringlists fieldRangeValues, fieldDisplayRangeValues
		StringList fieldRangeValues = new StringList();
		StringList fieldDisplayRangeValues = new StringList();
		StringItr strItr = new StringItr(FrameworkUtil.getRanges(context,sAttrRouteCompletionAction));
        String sAttrRange = "";
	    if(bShowRouteAction)
	    {
            while(strItr.next())
            {
            	sAttrRange = strItr.obj();
				 fieldRangeValues.add(sAttrRange);
				 fieldDisplayRangeValues.add(EnoviaResourceBundle.getRangeI18NString(context, sAttrRouteCompletionAction, sAttrRange,languageStr));
            }
	    }
        else
        {
            while(strItr.next())
            {
            	sAttrRange = strItr.obj();
	            if(!sAttrRange.equals("Promote Connected Object"))
	            {
					 fieldRangeValues.add(sAttrRange);
					 fieldDisplayRangeValues.add(EnoviaResourceBundle.getRangeI18NString(context, sAttrRouteCompletionAction, sAttrRange,languageStr));
	            }
            }
        }
	    // Process information to obtain the range values and add them to fieldRangeValues
	    // Get the internationlized value of the range values and add them to fieldDisplayRangeValues
	    tempMap.put("field_choices", fieldRangeValues);
	    tempMap.put("field_display_choices", fieldDisplayRangeValues);
	    return tempMap;
    }

	public static  Object getRouteCompletionActionsForRouteProperties(Context context, String[] args
            ) throws Exception, FrameworkException {
    	
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
	    HashMap requestMap = (HashMap) programMap.get("requestMap");
	    HashMap paramMap = (HashMap) programMap.get("paramMap");
	    String sAttrRouteCompletionAction = PropertyUtil.getSchemaProperty(context, "attribute_RouteCompletionAction" );
	    // Get the required parameter values from  "programMap" - as required

	    String  languageStr = (String) paramMap.get("languageStr"); 
		String  objectId = (String) requestMap.get(OBJECT_ID);
		boolean bShowRouteAction = true;
		if(objectId != null){
			DomainObject routeObject = new DomainObject(objectId);
	        StringList objectSelects = new StringList();
	        //objectSelects.add(DomainConstants.SELECT_ID);
	        objectSelects.add("current.access[promote]");
			StringList relSelects = new StringList();
	        String objectWhere = EMPTY_STRING;
	        String relPattern = RELATIONSHIP_OBJECT_ROUTE;
	        String typePattern = QUERY_WILDCARD;
	        int limit = 100;
	        MapList contentList = routeObject.getRelatedObjects(context, 
	        											relPattern, 
	        											typePattern, 
	        											objectSelects, 
	        											relSelects, 
	        											true, 
	        											false, 
	        											(short)0, 
	        											objectWhere, 
	        											null, 
	        											limit);
	        
	        Iterator contentItr = contentList.iterator();
	        
	        while(contentItr.hasNext()){
	        	Map contentMap = (Map) contentItr.next();
	        	boolean hasPromoteAccess = "TRUE".equalsIgnoreCase((String) contentMap.get("current.access[promote]"));
	        	if(!hasPromoteAccess){
					bShowRouteAction = false;
					break;
	        	}
	        }
		}
		// initialize the return variable HashMap tempMap = new HashMap();
		HashMap tempMap = new HashMap();
		// initialize the Stringlists fieldRangeValues, fieldDisplayRangeValues
		StringList fieldRangeValues = new StringList();
		StringList fieldDisplayRangeValues = new StringList();
		StringItr strItr = new StringItr(FrameworkUtil.getRanges(context,sAttrRouteCompletionAction));

        String sAttrRange = "";
	    if(bShowRouteAction)
	    {
            while(strItr.next())
            {
            	sAttrRange = strItr.obj();
				 fieldRangeValues.add(sAttrRange);
				 fieldDisplayRangeValues.add(EnoviaResourceBundle.getRangeI18NString(context, sAttrRouteCompletionAction, sAttrRange,languageStr));
            }
	    }
        else
        {
            while(strItr.next())
            {
            	sAttrRange = strItr.obj();
	            if(!sAttrRange.equals("Promote Connected Object"))
	            {
					 fieldRangeValues.add(sAttrRange);
					 fieldDisplayRangeValues.add(EnoviaResourceBundle.getRangeI18NString(context, sAttrRouteCompletionAction, sAttrRange,languageStr));
	            }
            }
        }
	    // Process information to obtain the range values and add them to fieldRangeValues
	    // Get the internationlized value of the range values and add them to fieldDisplayRangeValues

	    tempMap.put("field_choices", fieldRangeValues);
	    tempMap.put("field_display_choices", fieldDisplayRangeValues);
	    return tempMap;
    }
	
	private MapList getSortedDomainAccessSummaryList(Context context, MapList domainAccessSummaryList, boolean appendPrimaryContext) throws Exception {
		MapList tempMapList = new MapList(); 
		for(int k = 0;k < domainAccessSummaryList.size();k++){
			Map  membermap =  (Map) domainAccessSummaryList.get(k);
			String comment           = (String)membermap.get("comment");
	    	   
			String memberName         = (String)membermap.get(SELECT_NAME);
			String routeTaskUser     = (String)membermap.get(Route.SELECT_ROUTE_TASK_USER);
			String nodeType           = (String)membermap.get(SELECT_TYPE);
		           
			String project           = (String)membermap.get("project");
			String org           = (String)membermap.get("org");
			String access           = (String)membermap.get("access");           


			if(!UIUtil.isNullOrEmpty(project) && !UIUtil.isNullOrEmpty(org)) {              
		                              
				HashMap tempHash = new HashMap(10);
				tempHash.put("Organization", org);
				tempHash.put("project", project);
				tempHash.put("name", project);
				tempHash.put("Access", access);
				tempHash.put("Type", "Collab Space");
				tempHash.put("disableSelection", "true");
				tempHash.put("isAccessColumnEditable", "false");
				tempHash.put("comment", comment);
		               
				if(appendPrimaryContext && "Primary".equalsIgnoreCase(comment)){
					tempMapList.add(tempHash);
				}else if(!appendPrimaryContext && !"Primary".equalsIgnoreCase(comment)){
					tempMapList.add(tempHash);
				}
			}           
		}
		return tempMapList;
	}
	
	  /**
	   * Gets the vector output, for the Comments column in the access summary table.
	   *
	   * @param context the eMatrix <code>Context</code> object.
	   * @param args contains a packed HashMap with the following entries:
	   * objectList - a MapList containing the actual maps "dataMap" containing the data.
	   * @return Vector of the user Organizations.
	   * @throws Exception if the operation fails.
	   * @since CommonComponents R417HF12
	   */

	   public Vector getRouteAccessSummaryComments (Context context, String[] args)
	       throws Exception
	   {
	       HashMap programMap = (HashMap)JPO.unpackArgs(args);
	       MapList objList = (MapList)programMap.get("objectList");
	       HashMap paramList = (HashMap) programMap.get("paramList");
	       String languageStr = (String)paramList.get("languageStr");
	       int objListSize = objList.size();
	       Vector columnVals   = new Vector(objListSize);
	       for (int k=0; k < objListSize; k++) {
	           Map map = (Map) objList.get(k);
	           String type = (String)map.get("Type");
	           if("Collab Space".equals(type)){
	        	   String Comments = (String)map.get("comment");
	        	   if("primary".equalsIgnoreCase(Comments)){
	        		   Comments = EnoviaResourceBundle.getProperty(context,"emxFrameworkStringResource", new Locale(languageStr),"emxFramework.MultipleOwnership.Primary");
	        	   }else if(UIUtil.isNotNullAndNotEmpty(Comments) && Comments.startsWith("Multiple Ownership") ){
	        		   Comments = EnoviaResourceBundle.getProperty(context,"emxFrameworkStringResource", new Locale(languageStr),"emxFramework.MultipleOwnership.Comments");
	        	   }else if(UIUtil.isNotNullAndNotEmpty(Comments) && Comments.startsWith(DomainAccess.COMMENT_MULTIPLE_OWNERSHIP_GRANTED)){
	        		   Comments = Comments.replaceFirst("Access Granted Through", EnoviaResourceBundle.getProperty(context,"emxFrameworkStringResource",new Locale(languageStr), "emxFramework.MultipleOwnership.Granted"));
	        	   }	        	   
	    		   columnVals.addElement(Comments);
	    	   } else {
	    		   columnVals.addElement("");
	    	   }
	       }
	       return columnVals;
	   }

	
	/**
	* Inherit Access to the newly added content of the route.
	*
	* @param context the eMatrix Context object
	* @param holds Route Objects
	* @return void
	* @throws Exception if the operation fails
	* @since AEF Rossini
	*/
	public void inheritAccesstoNewContent(matrix.db.Context context, String[] args) throws Exception
	{
		for(int i = 0; i< args.length;i++){
			DomainObject contentObject = new DomainObject();
			contentObject.setId(args[i]);
			
			StringList objectSelects = new StringList();
			objectSelects.add("from["+RELATIONSHIP_OBJECT_ROUTE+"].to.id");
			objectSelects.add("from["+RELATIONSHIP_OBJECT_ROUTE+"].to.name");
			objectSelects.add("from["+RELATIONSHIP_OBJECT_ROUTE+"].to.type");
			objectSelects.add("from["+RELATIONSHIP_OBJECT_ROUTE+"].to.physicalid");		
			Map RouteInfo = contentObject.getInfo(context, objectSelects);		
			
			MapList routeAccesList = Route.getOwnershipAccessOnRoute(context,(String)RouteInfo.get("from["+RELATIONSHIP_OBJECT_ROUTE+"].to.id"));
		    for(int k = 0;k<routeAccesList.size();k++){
		  	     Map AccessMap = (Map)routeAccesList.get(k);       	
		  		DomainAccess.createObjectOwnership(context, (String)args[i], null, (String)AccessMap.get(DomainAccess.KEY_ACCESS_PROJECT)+"_PRJ", (String)AccessMap.get(DomainAccess.KEY_ACCESS_GRANTED), DomainAccess.COMMENT_MULTIPLE_OWNERSHIP_GRANTED +" "+(String)RouteInfo.get("from["+RELATIONSHIP_OBJECT_ROUTE+"].to.name"), false);       	
		    }
		}
	}
	
	/**
	 * To Grant ownership on the Route/Route Template
	 *
	 * @param context the eMatrix <code>Context</code> object.
	 * @param args contains a packed HashMap with the following entries:
	 * @return boolean true.member
	 * @throws Exception if the operation fails.
	 * @since CommonComponents R417HF12
	 */
	public boolean grantAccessOnRouteForMembers(Context context, String[] args) throws Exception  {
		String toName = args[0];
		String toType = args[1];
		String toId = args[2];
		String fromId = args[3];
		String fromType = args[4];
		String fromName = args[5];
		      
		StringList accessNames = DomainAccess.getLogicalNames(context, fromId);	
		// accessNames = [Read, Read Write, Add, Remove, Add Remove]
		String defaultAccess = (String)accessNames.get(0);
		String defaultAccessMasks = DomainAccess.getPhysicalAccessMasks(context, fromId, defaultAccess);
		if(!"Route Task User".equals(toType)){
			if("Route Template".equals(fromType)){
				String routeId = getRouteIdFromRouteTemplate(context, fromId);
				if(UIUtil.isNotNullAndNotEmpty(routeId)){
					MapList routeAccesList = Route.getOwnershipAccessOnRoute(context, routeId);
					for(int k = 0;k<routeAccesList.size();k++){
						Map AccessMap = (Map)routeAccesList.get(k);
						try {
							ContextUtil.pushContext(context); 
							DomainAccess.createObjectOwnership(context, fromId, null, (String)AccessMap.get(DomainAccess.KEY_ACCESS_PROJECT)+"_PRJ", (String)AccessMap.get(DomainAccess.KEY_ACCESS_GRANTED), (String)AccessMap.get(DomainAccess.KEY_ACCESS_COMMENT), false);
						} catch(Exception ex) {
							throw new FrameworkException(ex);
						} finally {
							ContextUtil.popContext(context);
						}
					}
				} else{
					DomainObject busObject = new DomainObject();
					busObject.setId(fromId);
					try {
						ContextUtil.pushContext(context); 
						DomainAccess.createObjectOwnership(context, fromId, toId, defaultAccess, DomainAccess.COMMENT_MULTIPLE_OWNERSHIP);
						// THERE WONT BE ANY CONTENT TO THE ROUTE TEMPLATE									        
					} catch(Exception ex) {
						throw new FrameworkException(ex);
					} finally {
						ContextUtil.popContext(context);	
					}
				}		        	
				return true;
		        	
			}else{				
					DomainObject busObject = new DomainObject();
					busObject.setId(fromId);
					
					StringList busSel =  new StringList();  
					busSel.addElement("owner");
					Map busMap = busObject.getInfo(context, busSel);
					if(toName.equals(busMap.get("owner"))){
						defaultAccess = (String)accessNames.get(4);
						defaultAccessMasks = DomainAccess.getPhysicalAccessMasks(context, fromId, defaultAccess);
					}
				    Route routeObj = new Route();
				    routeObj.setId(fromId);
					String templateId = getRouteTemplateId(context, fromId );
					if(UIUtil.isNotNullAndNotEmpty(templateId)){
						MapList  domainAccessSummaryList = DomainAccess.getAccessSummaryList(context, templateId);
						// to grant access to the template members
						String sAccess = "Read";
						boolean isAccessUpdated = false;
						for(int i = 0 ; i < domainAccessSummaryList.size(); i++){
							HashMap m = (HashMap)domainAccessSummaryList.get(i);
							if((toName+"_PRJ").equals((m.get("name")))){
								sAccess = (String)m.get("access");
								try {
									ContextUtil.pushContext(context); 
									String project = toName +"_PRJ";
									DomainAccess.createObjectOwnership(context, fromId, toId, sAccess, DomainAccess.COMMENT_MULTIPLE_OWNERSHIP);
		   							   
									String []coObjMap = routeObj.getConnectedObjectIds(context);
									String[] connectedObjects = new String[coObjMap.length];
									for (int j = 0; j < coObjMap.length; j++) {
										connectedObjects[j] = coObjMap[j];
										String defaultTemplateAccessMasks = DomainAccess.getPhysicalAccessMasks(context, fromId, sAccess);
										DomainAccess.createObjectOwnership(context, (String)connectedObjects[j], toId, defaultTemplateAccessMasks, DomainAccess.COMMENT_MULTIPLE_OWNERSHIP_GRANTED +" " +fromName );
									}
		   							   
								} catch(Exception ex) {
									throw new FrameworkException(ex);
								} finally {
									ContextUtil.popContext(context);
								}
								isAccessUpdated = true;
								break;
							}
						}
						
						if(!isAccessUpdated) {
							try {
								ContextUtil.pushContext(context); 
								String project = toName +"_PRJ";
								String sDefaultTemplateAccess = "Read";
								DomainAccess.createObjectOwnership(context, fromId, toId, sDefaultTemplateAccess, DomainAccess.COMMENT_MULTIPLE_OWNERSHIP);
	   							   
								String []coObjMap = routeObj.getConnectedObjectIds(context);								
								for (int j = 0; j < coObjMap.length; j++) {									
										String defaultTemplateAccessMasks = DomainAccess.getPhysicalAccessMasks(context, fromId, sDefaultTemplateAccess);
										DomainAccess.createObjectOwnership(context, (String)coObjMap[j], toId, defaultTemplateAccessMasks, DomainAccess.COMMENT_MULTIPLE_OWNERSHIP_GRANTED +" " +fromName );						
								}	   							   
							} catch(Exception ex) {
								throw new FrameworkException(ex);
							} finally {
								ContextUtil.popContext(context);
							}
						}
				        	
					}else{		        
						try {
							ContextUtil.pushContext(context); 
							DomainAccess.createObjectOwnership(context, fromId, toId, defaultAccess, DomainAccess.COMMENT_MULTIPLE_OWNERSHIP);
							String []coObjMap = routeObj.getConnectedObjectIds(context);
							String[] connectedObjects = new String[coObjMap.length];
							for (int i = 0; i < coObjMap.length; i++) {
								connectedObjects[i] = coObjMap[i];
								DomainAccess.createObjectOwnership(context, (String)connectedObjects[i], toId, defaultAccessMasks, DomainAccess.COMMENT_MULTIPLE_OWNERSHIP_GRANTED +" " +fromName);
							}
						        
						} catch(Exception ex) {
							throw new FrameworkException(ex);
						} finally {
							ContextUtil.popContext(context);
						}
					}
					return true; 
				
			}
		}else{
			return true;
		}
	}
	
	
	/**
	 * To Grant ownership on the Route/Route Template
	 *
	 * @param context the eMatrix <code>Context</code> object.
	 * @param args contains a packed HashMap with the following entries:
	 * @return boolean true.
	 * @throws Exception if the operation fails.
	 * @since CommonComponents R417HF12
	 */
	public boolean grantAccessOnRouteContentForMembers(Context context, String[] args) throws Exception  {
		String toName = args[0];
		String toType = args[1];
		String toId = args[2];
		String fromId = args[3];
		String fromType = args[4];
		if(!(DomainConstants.TYPE_WORKSPACE.equals(fromType) || DomainConstants.TYPE_WORKSPACE_VAULT.equals(fromType) || DomainConstants.TYPE_PROJECT_SPACE .equals(fromType) || DomainConstants.TYPE_PROJECT_CONCEPT.equals(fromType))) {
		return grantAccessOnRouteContentForMembers(context, toName, toType, toId , fromId );
		}else {
			return true;
		}
		      
		      
	}
	
	private boolean grantAccessOnRouteContentForMembers(Context context, String toName, String toType, String toId , String fromId ) throws Exception  {
		      
		StringList accessNames = DomainAccess.getLogicalNames(context, toId);	
		// accessNames = [Read, Read Write, Add, Remove, Add Remove]
		String defaultAccess = (String)accessNames.get(0);
		if(DomainConstants.TYPE_ROUTE.equals(toType)){
			MapList accessMap = Route.getOwnershipAccessOnRoute(context, toId);
			for(int k = 0; k < accessMap.size(); k++) {
				Map m = (Map) accessMap.get(k);
				String assignee = (String) m.get("project");
				String access = (String) m.get("access");
				DomainAccess.createObjectOwnership(context, fromId, null, assignee+"_PRJ", access, DomainAccess.COMMENT_MULTIPLE_OWNERSHIP_GRANTED +" " +toName , false);				
			}
			return true;
		}else{
			return true;
		}
	}
	
	
	
	
	
	/**
	 * To revoke ownership granted on the Route/Route Template
	 *
	 * @param context the eMatrix <code>Context</code> object.
	 * @param args contains a packed HashMap with the following entries:
	 * @return boolean true.
	 * @throws Exception if the operation fails.
	 * @since CommonComponents R417HF12
	 */
	 public boolean revokeAccessOnRouteForMembers(Context context, String[] args) throws Exception  {
		String toName = args[0];//Person name
		 String toType = args[1];
		String toId = args[2];// Person Id
		String fromId = args[3];//Route Id
		 String fromType = args[4];
		 String fromName = args[5];
		try {
			ContextUtil.pushContext(context);
			DomainAccess.deleteObjectOwnership(context, fromId, "", toName+"_PRJ", DomainAccess.COMMENT_MULTIPLE_OWNERSHIP, false);
		} catch(Exception ex) {
			throw new FrameworkException(ex);
		} finally {
			ContextUtil.popContext(context);
		}
		 return Route.revokeAccessOnRouteForMembers(context,  args[0],  args[1], args[2] , args[3], args[4], args[5]);	        
	 }
	 
	 /**
		 * To revoke ownership granted on the Route/Route Template
		 *
		 * @param context the eMatrix <code>Context</code> object.
		 * @param args contains a packed HashMap with the following entries:
		 * @return boolean true.
		 * @throws Exception if the operation fails.
		 * @since CommonComponents R417HF12
		 */
		 public boolean revokeAccessOnRouteContentForMembers(Context context, String[] args) throws Exception  {
			 String toName = args[0];
			 String toType = args[1];
			 String toId = args[2];	
			 String fromId = args[3];
			 String fromType = args[4];
			 String fromName = args[5];
		 try{
			 ContextUtil.pushContext(context);
			 if("Route".equals(toType)){
				 MapList accessMap = Route.getOwnershipAccessOnRoute(context, toId);
					for(int k = 0; k < accessMap.size(); k++) {
						Map m = (Map) accessMap.get(k);
						String assignee = (String) m.get("project");						
						DomainAccess.deleteObjectOwnership(context, fromId, "",assignee+"_PRJ",DomainAccess.COMMENT_MULTIPLE_OWNERSHIP_GRANTED +" " +toName);						
					}
			 }
		 }finally {
			 ContextUtil.popContext(context);
		 }
			 return true;			 	        
		 }
	
	
	@SuppressWarnings("rawtypes")
	private static  String getRouteIdFromRouteTemplate(Context context, String strObjectId
            ) throws Exception, FrameworkException {
    	
        DomainObject domRoute = DomainObject.newInstance(context, strObjectId);
        SelectList selectables = new SelectList(10);
        selectables.add(DomainConstants.SELECT_ID);
        selectables.add(DomainConstants.SELECT_NAME);
          
        MapList lstRouteTemplateList = domRoute.getRelatedObjects(context, DomainConstants.RELATIONSHIP_INITIATING_ROUTE_TEMPLATE,"*",
        																selectables, null,
        																true, false, (short)1, 
        																EMPTY_STRING, EMPTY_STRING,
        																0);
        
        if (lstRouteTemplateList != null && lstRouteTemplateList.size() > 0) {
            return (String) ((Map) lstRouteTemplateList.get(0)).get(DomainConstants.SELECT_ID);
        } else {
            return DomainConstants.EMPTY_STRING;
        }
    }
	

     
     private static  String getRouteTemplateId(Context context, String strObjectId ) throws Exception {
	    	
	        DomainObject domRoute = DomainObject.newInstance(context, strObjectId);
	        SelectList selectables = new SelectList(10);
	        selectables.add(DomainConstants.SELECT_ID);
	        selectables.add(DomainConstants.SELECT_NAME);
	          
	        MapList lstRouteTemplateList = domRoute.getRelatedObjects(context, DomainConstants.RELATIONSHIP_INITIATING_ROUTE_TEMPLATE,"*",
	        																selectables, null,
	        																false, true, (short)1, 
	        																EMPTY_STRING, EMPTY_STRING,
	        																0);
	        
	        if (lstRouteTemplateList != null && lstRouteTemplateList.size() > 0) {
	            return (String) ((Map) lstRouteTemplateList.get(0)).get(DomainConstants.SELECT_ID);
	        } else {
	            return DomainConstants.EMPTY_STRING;
	        }
	    }
     
	 /**
		 * To revoke ownership granted on the Route/Route Template
		 *
		 * @param context the eMatrix <code>Context</code> object.
		 * @param args contains a packed HashMap with the following entries:
		 * @return boolean true.
		 * @throws Exception if the operation fails.
		 * @since CommonComponents R417HF12
		 */
		 public boolean updateRouteNameOnContentSOV(Context context, String[] args) throws Exception  {
			 String event = args[0];
			 String oldName = args[1];
			 String newRouteName = args[2];
			 String routeId = args[3];	
			 
			 Route route = (Route)DomainObject.newInstance(context,DomainConstants.TYPE_ROUTE);
			 route.setId(routeId);
			 
			 try {
				 String[] grantees = route.getRouteMembers(context);
				 String[] coObjMap = route.getConnectedObjectIds(context);
				 ContextUtil.pushContext(context);      
				 	 
				 for (int j = 0; j < coObjMap.length; j++) {
					 for (int i = 0; i < grantees.length; i++) {
						 DomainAccess.deleteObjectOwnership(context, (String) coObjMap[j], "",grantees[i]+"_PRJ",DomainAccess.COMMENT_MULTIPLE_OWNERSHIP_GRANTED+" " +oldName);
						 
						 
					 }
					 //grant Access with new Route
					 grantAccessOnRouteContentForMembers(context, newRouteName, "Route", routeId , coObjMap[j] );
				 }
		    
		             
			 } finally {
				 ContextUtil.popContext(context);
			 }
			 
			 return true;			 	        
		 }
	
	@com.matrixone.apps.framework.ui.PostProcessCallable
    public HashMap postProcessRefresh (Context context, String[] args) throws Exception
    {
		HashMap returnMap = new HashMap(1);
		returnMap.put("Action","refresh");
		return returnMap;
    }
/**
		     * Check Trigger to block the remove of finished routes if it's not an admin user
		     * This trigger will check if user is admin, then only user can remove the route.
		     * @param context the Enovia <code>Context</code> object
		     * @param args
		     *            0 - String containing object id.
		     */
		public int checkRemoveRouteInFinishedState(Context context, String[] args) throws Exception {
			 String loggedInRole = PersonUtil.getActiveSecurityContext(context);
			 boolean isFinishedState = false;
			 String roleProjectAdmin =   PropertyUtil.getSchemaProperty(context,"role_VPLMProjectAdministrator");
			 String roleAdmin =   PropertyUtil.getSchemaProperty(context,"role_VPLMAdmin");
			 String routeStatus =DomainObject.newInstance(context,args[0]).getInfo(context,"attribute["+ATTRIBUTE_ROUTE_STATUS+"]");
			 isFinishedState = routeStatus.equalsIgnoreCase("Finished");
			 if (!(loggedInRole.contains(roleProjectAdmin) || loggedInRole.contains(roleAdmin)) && isFinishedState)  {
				 ${CLASS:emxContextUtil}.mqlNotice(context,EnoviaResourceBundle.getProperty(context, "emxComponentsStringResource",context.getLocale(), "emxComponents.Route.RemoveFinishedRoute"));
				 return 1;   
			 }else{
				 return 0;
			 } 	
		}
		
		public int checkAssigneeForTask(Context context,String[] args)throws Exception
		{
			if(args!=null && args.length>1){
				String strRouteId=args[0];
			
			  Route boRoute = (Route)DomainObject.newInstance(context,DomainConstants.TYPE_ROUTE);
			  
			  Pattern relPattern        = new Pattern(DomainConstants.RELATIONSHIP_ROUTE_NODE);
			  Pattern typePattern       = new Pattern(DomainConstants.TYPE_PERSON);
			  typePattern.addPattern(DomainConstants.TYPE_ROUTE_TASK_USER);
			
			  
			  StringList selectStmts = new StringList();
			  selectStmts.addElement(DomainConstants.SELECT_TYPE);
			
			  StringList relStmts= new StringList();
			  relStmts.addElement("attribute["+DomainConstants.ATTRIBUTE_ROUTE_TASK_USER+"]");
			  
			  boRoute.setId(strRouteId);
			  boRoute.open(context);		
			   
			  MapList routeTaskList=boRoute.getRelatedObjects(context, relPattern.getPattern(),typePattern.getPattern(), selectStmts, relStmts, 
					  false, true, (short) 1, "", "", 0); 
			  
			  String routeTaskUser	="";
			  String strType="";
			  Iterator relItr=routeTaskList.iterator();
			  Map map=null;
				while(relItr.hasNext()) {
					map=(Map)relItr.next();
					routeTaskUser = (String)map.get("attribute["+DomainConstants.ATTRIBUTE_ROUTE_TASK_USER+"]");
					strType = (String)map.get(DomainConstants.SELECT_TYPE);
					if(UIUtil.isNotNullAndNotEmpty(strType) && strType.equals(DomainConstants.TYPE_ROUTE_TASK_USER) && (routeTaskUser == null || "".equals(routeTaskUser))){
						String strMessage = EnoviaResourceBundle.getProperty(context, "emxComponentsStringResource", context.getLocale(), "emxComponents.RouteDetails.UnAssignedTasksMessage");
						${CLASS:emxContextUtil}.mqlNotice(context,strMessage);				
						return 1;
					}
			  }	   
			}
			   return 0;
		}
		 /**
		    * This method returns field criteria for assignees inside organization.
		    *
		    * @param context - the eMatrix <code>Context</code> object
		    * @param args holds object id of Inbox task
		    * @return string with the refinement
		    * @throws Exception if the operation fails
		    * @since AEF 421
		    */
		  @com.matrixone.apps.framework.ui.ProgramCallable
		  public String getChangeAssigneeRefinements(Context context, String[] args) throws Exception {
		      HashMap inputMap = (HashMap)JPO.unpackArgs(args);	
			  String taskId = (String)inputMap.get("contextObjectId");
			  DomainObject domObj= new DomainObject(taskId);  
			  StringList selectList = new StringList();
			  selectList.add( "from[" + RELATIONSHIP_ROUTE_TASK + "].to.organization");
			  selectList.add( "from[" + RELATIONSHIP_ROUTE_TASK + "].to.attribute["+ ATTRIBUTE_RESTRICT_MEMBERS+"]");
			  selectList.add( "from[" + RELATIONSHIP_ROUTE_TASK + "].to[" + RELATIONSHIP_OWNING_ORGANIZATION + "].from.name");
			  Map objectInfo =  domObj.getInfo(context, selectList);
			  String organization = (String)objectInfo.get("from[" + RELATIONSHIP_ROUTE_TASK + "].to.organization");
			  String scopeId=(String)objectInfo.get("from[" + RELATIONSHIP_ROUTE_TASK + "].to.attribute[Restrict Members]");
			  String owningOrganization = (String)objectInfo.get("from[" + RELATIONSHIP_ROUTE_TASK + "].to[" + RELATIONSHIP_OWNING_ORGANIZATION + "].from.name");	  
			  String searchString = "";
			  if("Organization".equals(scopeId)){	
				  organization = ((UIUtil.isNullOrEmpty(owningOrganization)) || organization.equalsIgnoreCase(owningOrganization))?(organization):(owningOrganization);
				  searchString = "TYPES=type_Person:CURRENT=policy_Person.state_Active:MEMBER=" + organization;
			  }else{
				  searchString = "TYPES=type_Person:CURRENT=policy_Person.state_Active";
			  }
			  return searchString; 
		  }
		  
		public Vector getContentName(Context context, String[] args) throws Exception
		{
		HashMap programMap = (HashMap)JPO.unpackArgs(args);
		MapList objList = (MapList)programMap.get("objectList");
		int objListSize = objList.size();
		HashMap paramList = (HashMap) programMap.get("paramList");
		boolean isprinterFriendly = paramList.get("reportFormat") != null;
		Vector columnVals = new Vector(objListSize);
		for (int i=0; i < objListSize; i++) {
			StringBuffer strBuff = new StringBuffer();
		 	Map map = (Map) objList.get(i);
		 	String name = (String)map.get("name");
		 	String objectId = (String)map.get("id");
		 	String typeIcon = UINavigatorUtil.getTypeIconProperty(context, (String)map.get("type"));
		 	if(!isprinterFriendly) {
		 		strBuff.append("<a href=\"javascript:emxTableColumnLinkClick('../common/emxTree.jsp?DefaultCategory=APPDocumentFiles&amp;emxSuiteDirectory=components&amp;objectId=");
		 	    strBuff.append(XSSUtil.encodeForJavaScript(context, objectId));
		  		strBuff.append("&amp;parentOID="+XSSUtil.encodeForJavaScript(context, (String)paramList.get("parentOID")));
		 	    strBuff.append("', '");
		 	    strBuff.append("700");
		 	    strBuff.append("', '");
		 	    strBuff.append("600");
		 	    strBuff.append("', 'false', '");
		 	    strBuff.append("content");
		 	    strBuff.append("')");
		 	    strBuff.append("\">");
		 	    strBuff.append("<img border='0' src='images/"+typeIcon+"'>"+name+"</a>");
		 	}else {
		 		strBuff.append(XSSUtil.encodeForXML(context, name));
		 	}
		 	columnVals.addElement(strBuff.toString());
		 }
		 return columnVals;
	 }
}

