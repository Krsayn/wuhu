<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%


response.setContentType( "text/html; charset=UTF-8" );
String json = "{\"rows\":[{\"productid\":\"FI-SW-01\",\"productname\":\"Koi\",\"unitcost\":\"10.00\",\"status\":\"P\",\"listprice\":\"36.50\",\"attr1\":\"Large\",\"itemid\":\"EST-1\"}]}"
response.getWriter().print(json);
%>
