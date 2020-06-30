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
    String strMode = emxGetParameter(request,"portalCmdName");
    
    strMode = XSSUtil.encodeURLForServer(context, strMode);
    System.out.println("*****************strMode**********************"+strMode);
    
    String strNavigate = emxGetParameter(request,"navigate");
    strNavigate = XSSUtil.encodeURLForServer(context, strNavigate);
    
    System.out.println("&&&&&&&&&&&&&&&&&&&&&&strNavigate&&&&&&&&&&&&&&&&&&&&&&&&&&&"+strNavigate);
    String strDate = emxGetParameter(request,"PMCMyCalendarDateBox_msvalue");
    System.out.println("*****************strDate**********************"+strDate);
    strDate = XSSUtil.encodeURLForServer(context, strDate);
    
    System.out.println("*****************strDate**********************"+strDate);
    String strTaskToShow = emxGetParameter(request,"PMCMyCalendarTaskTypes");
    strTaskToShow = XSSUtil.encodeURLForServer(context, strTaskToShow);
    System.out.println("&&&&&&&&&&&&&&&&&&&&&&strTaskToShow&&&&&&&&&&&&&&&&&&&&&&&&&&&"+strTaskToShow);
   
    //Start :: Modified for Client TimeZone
    String clientOffset = (String)session.getAttribute("timeZone");
    
    
    framework.setGlobalCustomData(session,context,"timeZone",clientOffset);
    //End :: Modified for Client TimeZone
    
    System.out.println("*******************clientOffset********************"+clientOffset);
    if(strTaskToShow == null || strTaskToShow.equals("")){
    	strTaskToShow = MyCalendarUtil.getValueFromContext(context,"PMCMyCalendarTaskTypes");
    	if(null == strTaskToShow){
    		strTaskToShow = "Due";
    	}
    }
    
    Enumeration requestParams = emxGetParameterNames(request);
    StringBuffer url = new StringBuffer();
    if(requestParams != null){
        while(requestParams.hasMoreElements()){
            String param = (String)requestParams.nextElement();  
            String value = (String) emxGetParameter(request,param);
            value = XSSUtil.encodeURLForServer(context, value);
            url.append("&" + XSSUtil.encodeForURL(context, param) + "=" + value);
            
            System.out.println("*******************url********************"+url);
        }
    } 
    boolean isValueInContext = false;
    String strKeyValue = "";
    Calendar cal = Calendar.getInstance();
    
    if(strDate == null || strDate.equals("")){
    	strDate = MyCalendarUtil.getValueFromContext(context,"CurrentDate");
    	cal = MyCalendarUtil.getCalendarObject(strDate);
    	isValueInContext = true;
    }else{
    	//Take date from toolbar filter
    	strDate = strDate.trim();
		long lngMS = Long.parseLong(strDate);
		Date filterDate = new Date(lngMS);
		cal.setTime(filterDate);
    }
    
    if(strMode == null || strMode.equals("")){
    	strMode = MyCalendarUtil.getValueFromContext(context,"View");
    }
    
    if(!isValueInContext){
        strDate = null;
    }
   
    String contentURL = "";
    if("PMCMyCalendarDay".equalsIgnoreCase(strMode)){
        
        if(null != strDate){
            
            if("next".equalsIgnoreCase(strNavigate)){
                cal.add(Calendar.DATE, 1);
            }else if("prev".equalsIgnoreCase(strNavigate)){
                cal.add(Calendar.DATE, -1);
            }
            
            strDate = cal.get(Calendar.DATE)+"-"+cal.get(Calendar.MONTH)+"-"+cal.get(Calendar.YEAR);
            
        }else{
            strDate = cal.get(Calendar.DATE)+"-"+cal.get(Calendar.MONTH)+"-"+cal.get(Calendar.YEAR);    
        }
        
        framework.setGlobalCustomData(session,context,"View",strMode);
        framework.setGlobalCustomData(session,context,"CurrentDate",strDate);
        framework.setGlobalCustomData(session,context,"PMCMyCalendarTaskTypes",strTaskToShow);
        
        contentURL = "../common/emxIndentedTable.jsp?table=PMCMyCalendarViewTable&PMCMyCalendarTaskTypes="+strTaskToShow+"&program=emxMyCalendar:expandMyCalendar&editLink=false&hideHeader=true&toolbar=PMCMyCalendarToolbar&customize=false&rowGrouping=false&showPageURLIcon=false&launched=true&portalMode=false&export=false&displayView=details&PMCMyCalendarTempDate="+strDate+url.toString()+"";
        
    }else if("PMCMyCalendarMonth".equalsIgnoreCase(strMode)){
    	
    	if(null != strDate){
    		//cal.set(Calendar.DATE, 1);
            if("next".equalsIgnoreCase(strNavigate)){
                cal.add(Calendar.MONTH, 1);
            }else if("prev".equalsIgnoreCase(strNavigate)){
                cal.add(Calendar.MONTH, -1);
            }
            
            strDate = cal.get(Calendar.DATE)+"-"+cal.get(Calendar.MONTH)+"-"+cal.get(Calendar.YEAR);
        }else{
            strDate = cal.get(Calendar.DATE)+"-"+cal.get(Calendar.MONTH)+"-"+cal.get(Calendar.YEAR);    
        }
        
    	framework.setGlobalCustomData(session,context,"View",strMode);
        framework.setGlobalCustomData(session,context,"CurrentDate",strDate);
        
        contentURL = "../common/emxIndentedTable.jsp?table=PMCMyCalendarViewTable&PMCMyCalendarTaskTypes="+strTaskToShow+"&program=emxMyCalendar:expandMyCalendar&editLink=false&hideHeader=true&toolbar=PMCMyCalendarToolbar&customize=false&rowGrouping=false&showPageURLIcon=false&launched=true&portalMode=false&export=false&displayView=details&sortColumnName=ID&export=false&dateT="+strDate+url.toString()+"";
    }else{
    	
    	if(null != strDate){
    		
            if("next".equalsIgnoreCase(strNavigate)){
                cal.add(Calendar.WEEK_OF_YEAR, 1);
            }else if("prev".equalsIgnoreCase(strNavigate)){
                cal.add(Calendar.WEEK_OF_YEAR, -1);
            }
            strDate = cal.get(Calendar.DATE)+"-"+cal.get(Calendar.MONTH)+"-"+cal.get(Calendar.YEAR);
        }else{
            strDate = cal.get(Calendar.DATE)+"-"+cal.get(Calendar.MONTH)+"-"+cal.get(Calendar.YEAR);    
        }
        
    	framework.setGlobalCustomData(session,context,"View",strMode);
        framework.setGlobalCustomData(session,context,"CurrentDate",strDate);
        framework.setGlobalCustomData(session,context,"PMCMyCalendarTaskTypes",strTaskToShow);
        
        contentURL = "../common/emxIndentedTable.jsp?table=PMCMyCalendarViewTable&PMCMyCalendarTaskTypes="+strTaskToShow+"&program=emxMyCalendar:expandMyCalendar&editLink=false&hideHeader=true&customize=false&rowGrouping=false&showPageURLIcon=false&launched=true&portalMode=false&export=false&displayView=details&export=false&displayView=details&toolbar=PMCMyCalendarToolbar&dateT="+strDate+url.toString()+"";
        
        
    }
    System.out.println("*******************contentURL********************"+contentURL);
    //Avoiding 0 in a date for calendar object
    if(strDate.contains("-0-")){
    	strDate = strDate.replace("-0-","-1-");
    }
    
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
