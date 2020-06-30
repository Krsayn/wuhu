<%--
   Copyright (c) 1992-2015 Dassault Systemes.
   All Rights Reserved.
   This program contains proprietary and trade secret information of MatrixOne,
   Inc.  Copyright notice is precautionary only
   and does not evidence any actual or intended publication of such program
   
--%>
<%@page import="matrix.util.MatrixException, matrix.util.StringList,java.util.ListIterator" %>
<%@page import="com.matrixone.apps.domain.DomainConstants,com.matrixone.apps.domain.DomainObject" %>
<%@page import="com.matrixone.apps.program.ProjectSpace" %>
<%@page import="com.matrixone.apps.program.mycalendar.MyCalendarUtil"%>
<%@page import="java.util.*" %>
<%@ include file="emxProgramGlobals2.inc" %>
<%@ include file="../emxUICommonAppInclude.inc"%>
<%
    FrameworkServlet framework = new FrameworkServlet();
    
    
    String strNavigate = emxGetParameter(request,"navigate");
    strNavigate = XSSUtil.encodeURLForServer(context, strNavigate);
    
    String inputString = emxGetParameter(request,"StudentText_msvalue");
    System.out.println("*****************inputString**********************"+inputString);
    inputString = XSSUtil.encodeURLForServer(context, inputString);
    
    System.out.println("&&&&&&&&&&&&&&&&&&&&&&strNavigate&&&&&&&&&&&&&&&&&&&&&&&&&&&"+strNavigate);
   
    Enumeration requestParams = emxGetParameterNames(request);
    StringBuffer url = new StringBuffer();
    if(requestParams != null){
        while(requestParams.hasMoreElements()){
            String param = (String)requestParams.nextElement();  
            String value = (String) emxGetParameter(request,param);
            value = XSSUtil.encodeURLForServer(context, value);
            url.append("&" + XSSUtil.encodeForURL(context, param) + "=" + value);
        }
    } 
    //End :: Modified for Client TimeZone
          String contentURL = "";
          contentURL="../common/emxIndentedTable.jsp?table=GWStudentTable&toolbar=StudentToolBar&selection=multiple&editLink=true&program=emxGWStudentBase:expandMyStudent&PMCStudentInput="+inputString+url.toString()+"";
       
        //contentURL = "../common/emxIndentedTable.jsp?table=PMCMyCalendarViewTable&PMCMyCalendarTaskTypes="+strTaskToShow+"&program=emxMyCalendar:expandMyCalendar&editLink=false&hideHeader=true&customize=false&rowGrouping=false&showPageURLIcon=false&launched=true&portalMode=false&export=false&displayView=details&export=false&displayView=details&toolbar=PMCMyCalendarToolbar&dateT="+strDate+url.toString()+"";
        
        
   
    System.out.println("*******************contentURL********************"+contentURL);
    //Avoiding 0 in a date for calendar object
    
    
%>


<%@page import="com.matrixone.apps.domain.util.XSSUtil"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <script language="javascript">
    var url = "<%= contentURL %>"; <%-- XSSOK --%>
    <%
    
    if("next".equalsIgnoreCase(strNavigate)){
		%>
		parent.window.location.href = url;
		<%
    }else if("prev".equalsIgnoreCase(strNavigate)){
		%>
		parent.window.location.href = url;
		<%
    }else if("goto".equalsIgnoreCase(strNavigate)){
        %>
        parent.window.location.href = url;
        <%
    }else{
		%>
		this.location.href = url;
		<%
    }
    %>
    </script>
</head>
<body>

</body>
</html>
