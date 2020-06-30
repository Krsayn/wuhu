<%-- Common Includes --%>
<%@page import="com.matrixone.apps.program.Currency"%>
<%@page import="com.matrixone.apps.common.WorkCalendar"%>
<%@page import="com.matrixone.apps.program.fiscal.Helper"%>
<%@page import="com.matrixone.apps.common.Search"%>
<%@page import="com.matrixone.json.JSONObject"%>
<%@page import="java.util.Set"%>
<%@include file="emxProgramGlobals2.inc"%>
<%@include file="../common/emxNavigatorTopErrorInclude.inc"%>
<%@include file="../emxUICommonAppInclude.inc"%>
<%@ include file = "../emxUICommonHeaderBeginInclude.inc" %>
<%@include file = "../emxUICommonHeaderEndInclude.inc" %>
<%@include file = "../common/emxUIConstantsInclude.inc"%>

<%@page import="com.matrixone.apps.domain.util.EnoviaResourceBundle"%>
<%@page import="com.matrixone.apps.domain.DomainObject"%>
<%@page import="matrix.db.MQLCommand"%>
<%@page import="com.matrixone.apps.common.Company,matrix.util.StringList" %>
<%@page import="com.matrixone.apps.program.ProgramCentralUtil"%>
<%@page import="com.matrixone.apps.domain.util.MapList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>

<%@page import="java.util.Enumeration"%>
<%@page import="com.matrixone.apps.program.ProgramCentralConstants"%>
<%@page import="com.matrixone.apps.domain.util.XSSUtil"%>
<%@page import="com.matrixone.apps.domain.util.FrameworkUtil"%>
<%@page import="com.matrixone.apps.domain.util.EnoviaResourceBundle"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.matrixone.apps.program.FTE"%>
<%@page import="com.matrixone.apps.program.ResourceRequest"%>
<%@page import="com.matrixone.apps.program.Question"%>
<%@page import="com.matrixone.apps.domain.DomainConstants" %>

<jsp:useBean id="indentedTableBean" class="com.matrixone.apps.framework.ui.UITableIndented" scope="session"/>
<jsp:useBean id="formBean" scope="session" class="com.matrixone.apps.common.util.FormBean"/>
<SCRIPT language="javascript" src="../common/scripts/emxUICore.js"></SCRIPT>
<script language="javascript" src="../common/scripts/emxUIConstants.js"></script>
<script src="../common/scripts/emxUIModal.js" type="text/javascript"></script>
<script src="../programcentral/emxProgramCentralUIFormValidation.js" type="text/javascript"></script>
<%
System.out.println("====");
                  try{
                  String[] selectedIds = emxGetParameterValues(request,"emxTableRowId");  
				  String[] strObjectIDArr    = new String[selectedIds.length];
				  String sObjId = "";
					  for(int i=0; i<selectedIds.length; i++)
						  {
							 String sTempObj = selectedIds[i];
							 Map mParsedObject = ProgramCentralUtil.parseTableRowId(context,sTempObj);
							 sObjId = (String)mParsedObject.get("objectId");
							 strObjectIDArr[i] = sObjId;
							 System.out.println(" nnnnnnnnnn"+sObjId);
						  } 
						  if ( strObjectIDArr != null )
						  {
						      try
						      {
						    	  //DomainObject.deleteObjects(context,strObjectIDArr);
						    		Map paramMapForJPO = new HashMap();		
						    					
						    		paramMapForJPO.put("emxTableRowIds" ,strObjectIDArr);		
						    		String[] args = JPO.packArgs(paramMapForJPO);
						    		JPO.invoke(context, "emxGWStudentBase", null, "delete", args);
						      }  
						      catch(Exception e)
						      {
						        session.setAttribute("error.message", e.getMessage());
						      }
						  }	
                   }  
				      catch(Exception e)
				      {
				    		e.printStackTrace();
				      }				  
				  %>
		<script language="javascript" type="text/javaScript">

		parent.location.href = parent.location.href;
	    </script>
	            
<%@include file = "../emxUICommonEndOfPageInclude.inc" %>
<%@include file = "../components/emxComponentsDesignBottomInclude.inc"%>