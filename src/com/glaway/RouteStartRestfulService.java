package com.glaway;


import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.common.Route;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.*;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.servlet.Framework;
import matrix.db.*;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.SelectList;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

/**
 * @Auther : Dumpling
 * @Description
 **/
@Path("/StartTemplateRoute")
public class RouteStartRestfulService extends RestService {

    /**
     * @param request
     * @return javax.ws.rs.core.Response
     * @Description https://r2019x.glaway.com/3dspace/resources/dsic/StartTemplateRoute/startRoute?TemplateId=58813.57799.10340.39453&ObjectId=58813.57799.29859.7938,58813.57799.40386.25863
     * @Author Dumpling
     **/
    @GET
    @Path("/startRoute")
    public Response startRoute(@javax.ws.rs.core.Context HttpServletRequest request) {
        try {
            Context context = Framework.getContext(request.getSession());

            HashMap resultMap = new HashMap();

            Route route = (Route) DomainObject.newInstance(context, DomainConstants.TYPE_ROUTE);
            String version = "";

            String typeAlias = "type_Route";
            String policyAlias = "policy_Route";
            String AutoNameSeries = null;
            String routeId = "";
            routeId = FrameworkUtil.autoName(context, typeAlias, AutoNameSeries, policyAlias);


            route.setId(routeId);
            DomainObject dmoRequest = new DomainObject(routeId);

            String restrictMembers = "Organization";
            String selscopeId = restrictMembers;

            String objectIds = request.getParameter("ObjectId");
            String[] objectIdArr = objectIds.split(",");

            String routeCompletionAction = "Notify Route Owner";
            String routeDescription = "交付物校审";
            String portalMode = null;
            String routeBasePurpose = "Approval";

            String supplierOrgId = null;
            String suiteKey = "Components";

            String sTemplateId = request.getParameter("TemplateId");
            //String sTemplateName = "RT-000101";
            String visblToParent = null;
            String strAutoStopOnRejection = "Immediate";

            Hashtable routeDetails = new Hashtable();
            if (routeBasePurpose != null) {
                routeDetails.put("routeBasePurpose", routeBasePurpose);
            }

            if (visblToParent == null || visblToParent.equals("null")) {
                visblToParent = "";
            }

            boolean rtSelected = (sTemplateId != null && !"null".equals(sTemplateId) && !sTemplateId.equals(""));


            if (rtSelected)
                new com.matrixone.apps.common.RouteTemplate(sTemplateId).checksToUseRouteTemplateInRoute(context);


            String sAttrRestrictMembers = PropertyUtil.getSchemaProperty(context, "attribute_RestrictMembers");
            String sAttrRouteBasePurpose = PropertyUtil.getSchemaProperty(context, "attribute_RouteBasePurpose");
            String sAttrRouteCompletionAction = PropertyUtil.getSchemaProperty(context, "attribute_RouteCompletionAction");
            String attrOriginator = PropertyUtil.getSchemaProperty(context, "attribute_Originator");
            final String ATTRIBUTE_AUTO_STOP_ON_REJECTION = PropertyUtil.getSchemaProperty(context, "attribute_AutoStopOnRejection");
            String routeAutoNameId = null;
            String strProjectVault = "";
            String revisionSequence = "";


            boolean WSNotSelected = true;
            boolean isCompletedTask = false;
            String strLanguage = "zh,en-US;q=0.7,en;q=0.3";
            String errorMessage = EnoviaResourceBundle.getProperty(context, "emxComponentsStringResource", new Locale(strLanguage), "emxComponents.CreateRoute.OnCompleteTaskError");


            if (WSNotSelected) {
                String objectId = objectIdArr[0];
                if ((objectId != null && !"".equals(objectId) && !"null".equals(objectId))) {

                    DomainObject boObject = new DomainObject(objectId);
                    // 审批对象 推状态
                    boObject.promote(context);

                    String objState = boObject.getInfo(context, DomainConstants.SELECT_CURRENT);

                    routeDetails.put(objectId, objState);
                    //这边是审批对象的 第一个
                    Route.routeWithScope(context, objectId, routeId, routeDetails);

                    AttributeList routeAttrList = new AttributeList();
                    routeAttrList.addElement(new Attribute(new AttributeType(attrOriginator), context.getUser()));
                    routeAttrList.addElement(new Attribute(new AttributeType(sAttrRouteCompletionAction), routeCompletionAction));
                    routeAttrList.addElement(new Attribute(new AttributeType(sAttrRouteBasePurpose), routeBasePurpose));
                    routeAttrList.addElement(new Attribute(new AttributeType(ATTRIBUTE_AUTO_STOP_ON_REJECTION), strAutoStopOnRejection));// getting Auto Stop Attribute


                    if ((selscopeId != null) && (!selscopeId.equals(""))) {
                        if (FrameworkUtil.isObjectId(context, selscopeId)) {

                            DomainObject boscope = new DomainObject(selscopeId);
                            selscopeId = boscope.getInfo(context, "physicalid");

                        }
                        routeAttrList.addElement(new Attribute(new AttributeType(sAttrRestrictMembers), selscopeId));
                    }
                    route.setId(routeId);
                    route.setAttributes(context, routeAttrList);
                    route.setDescription(routeDescription);
                    route.update(context);


                    BusinessObject routeTemplateObj = null;
                    BusinessObject personObj = null;

                    SelectList selectPersonStmts = null;
                    SelectList selectPersonRelStmts = null;
                    ExpansionWithSelect personSelect = null;
                    RelationshipWithSelectItr relPersonItr = null;
                    Relationship relationShipRouteNode = null;

                    String routeActionValueStr = null;
                    String routeSequenceValueStr = null;
                    String routeInstructionsValueStr = null;
                    String sRouteTitle = null;
                    String routeTaskScheduleDate = null;
                    String routeTaskNameValueStr = null;
                    String routeTaskUser = null;
                    String routeAssigneeDueDateOptStr = null;
                    String dueDateOffset = null;
                    String dueDateOffsetFrom = null;
                    String parallelNodeProcessionRule = null;
                    String reviewTask = "";
                    String allowDelegation = "";

                    Attribute routeTitle = null;
                    Attribute routeActionAttribute = null;
                    Attribute routeOrderAttribute = null;
                    Attribute routeInstructionsAttribute = null;
                    Attribute templateTaskAttribute = null;
                    AttributeList attrList = null;
                    Attribute routeAssigneeDueDateOptAttribute = null;
                    Attribute routeDueDateOffsetAttribute = null;
                    Attribute routeDateOffsetFromAttribute = null;
                    Attribute routeTaskUserAttribute = null;
                    Attribute parallelNodeProcessionRuleAttrib = null;
                    Attribute reviewTaskAttribute = null;
                    Attribute allowDelegationAttribute = null;
                    Attribute routeTaskScheduleDateAttribute = null;

                    String templateTaskStr = PropertyUtil.getSchemaProperty(context, "attribute_TemplateTask");
                    Hashtable routeNodeAttributesTable = new Hashtable();


                    if (rtSelected) {

                        selectPersonStmts = new SelectList();
                        AccessUtil accessUtil = new AccessUtil();

                        selectPersonRelStmts = new SelectList();
                        selectPersonRelStmts.addAttribute(DomainObject.ATTRIBUTE_ROUTE_SEQUENCE);
                        selectPersonRelStmts.addAttribute(DomainObject.ATTRIBUTE_ROUTE_ACTION);
                        selectPersonRelStmts.addAttribute(DomainObject.ATTRIBUTE_ROUTE_INSTRUCTIONS);
                        selectPersonRelStmts.addAttribute(DomainObject.ATTRIBUTE_TITLE);
                        selectPersonRelStmts.addAttribute(DomainObject.ATTRIBUTE_ASSIGNEE_SET_DUEDATE);
                        selectPersonRelStmts.addAttribute(DomainObject.ATTRIBUTE_DUEDATE_OFFSET);
                        selectPersonRelStmts.addAttribute(DomainObject.ATTRIBUTE_DATE_OFFSET_FROM);
                        selectPersonRelStmts.addAttribute(DomainObject.ATTRIBUTE_ROUTE_TASK_USER);
                        selectPersonRelStmts.addAttribute(DomainObject.ATTRIBUTE_SCHEDULED_COMPLETION_DATE);
                        String strParallelNodeProscessionRule = PropertyUtil.getSchemaProperty(context, "attribute_ParallelNodeProcessionRule");
                        String sAttReviewTask = PropertyUtil.getSchemaProperty(context, "attribute_ReviewTask");

                        selectPersonRelStmts.addAttribute(strParallelNodeProscessionRule);
                        selectPersonRelStmts.addAttribute(sAttReviewTask);
                        selectPersonRelStmts.addAttribute(DomainObject.ATTRIBUTE_ALLOW_DELEGATION);


                        routeTemplateObj = new BusinessObject(sTemplateId);
                        routeTemplateObj.open(context);
                        try {
                            route.connectTemplate(context, sTemplateId);
                        } catch (Exception e) {
                            resultMap.put("Message", e.getMessage());
                        }

                        Pattern typePattern = new Pattern(DomainObject.TYPE_PERSON);
                        typePattern.addPattern(DomainObject.TYPE_ROUTE_TASK_USER);
                        personSelect = routeTemplateObj.expandSelect(context, DomainObject.RELATIONSHIP_ROUTE_NODE, typePattern.getPattern(),
                                selectPersonStmts, selectPersonRelStmts, false, true, (short) 1);


                        routeTemplateObj.close(context);
                        relPersonItr = new RelationshipWithSelectItr(personSelect.getRelationships());

                        while ((relPersonItr != null) && relPersonItr.next()) {
                            if (relPersonItr.obj().getTypeName().equals(DomainObject.RELATIONSHIP_ROUTE_NODE)) {
                                personObj = relPersonItr.obj().getTo();
                                if (personObj != null) {
                                    personObj.open(context);


                                    if ((DomainObject.TYPE_ROUTE_TASK_USER).equals(personObj.getTypeName()) || ((DomainObject.TYPE_PERSON).equals(personObj.getTypeName()))) {

                                        try {
                                            relationShipRouteNode = route.connect(context, new RelationshipType(DomainObject.RELATIONSHIP_ROUTE_NODE), true, personObj);
                                        } catch (Exception ex) {
                                            resultMap.put("Message", ex.getMessage());
                                        }

                                        routeNodeAttributesTable = relPersonItr.obj().getRelationshipData();
                                        routeSequenceValueStr = (String) routeNodeAttributesTable.get("attribute[" + DomainObject.ATTRIBUTE_ROUTE_SEQUENCE + "]");


                                        sRouteTitle = (String) routeNodeAttributesTable.get("attribute[" + DomainObject.ATTRIBUTE_TITLE + "]");


                                        routeActionValueStr = (String) routeNodeAttributesTable.get("attribute[" + DomainObject.ATTRIBUTE_ROUTE_ACTION + "]");


                                        routeInstructionsValueStr = (String) routeNodeAttributesTable.get("attribute[" + DomainObject.ATTRIBUTE_ROUTE_INSTRUCTIONS + "]");


                                        routeTaskNameValueStr = (String) routeNodeAttributesTable.get("attribute[" + DomainObject.ATTRIBUTE_TITLE + "]");
                                        System.out.println("routeTaskNameValueStr===>" + routeTaskNameValueStr);

                                        routeAssigneeDueDateOptStr = (String) routeNodeAttributesTable.get("attribute[" + DomainObject.ATTRIBUTE_ASSIGNEE_SET_DUEDATE + "]");


                                        dueDateOffset = (String) routeNodeAttributesTable.get("attribute[" + DomainObject.ATTRIBUTE_DUEDATE_OFFSET + "]");


                                        dueDateOffsetFrom = (String) routeNodeAttributesTable.get("attribute[" + DomainObject.ATTRIBUTE_DATE_OFFSET_FROM + "]");


                                        routeTaskUser = (String) routeNodeAttributesTable.get("attribute[" + DomainObject.ATTRIBUTE_ROUTE_TASK_USER + "]");


                                        routeTaskScheduleDate = (String) routeNodeAttributesTable.get("attribute[" + DomainObject.ATTRIBUTE_SCHEDULED_COMPLETION_DATE + "]");


                                        // Added by Infosys for Bug # 303103 Date 05/11/2005
                                        parallelNodeProcessionRule = (String) routeNodeAttributesTable.get("attribute[" + strParallelNodeProscessionRule + "]");


                                        // Added for the bug 301391
                                        reviewTask = (String) routeNodeAttributesTable.get("attribute[" + sAttReviewTask + "]");


                                        allowDelegation = (String) routeNodeAttributesTable.get("attribute[" + DomainObject.ATTRIBUTE_ALLOW_DELEGATION + "]");


                                        attrList = new AttributeList();
                                        relationShipRouteNode.open(context);

                                        // Added by Infosys for Bug # 303103 Date 05/11/2005
                                        // set parallelNodeProcessionRule
                                        parallelNodeProcessionRuleAttrib = new Attribute(new AttributeType(strParallelNodeProscessionRule), parallelNodeProcessionRule);
                                        attrList.addElement(parallelNodeProcessionRuleAttrib);

                                        // set title
                                        routeTitle = new Attribute(new AttributeType(DomainObject.ATTRIBUTE_TITLE), sRouteTitle);
                                        attrList.addElement(routeTitle);

                                        // set route action
                                        if (routeActionValueStr != null) {
                                            routeActionAttribute = new Attribute(new AttributeType(DomainObject.ATTRIBUTE_ROUTE_ACTION), routeActionValueStr);
                                            attrList.addElement(routeActionAttribute);
                                        }

                                        // set route order
                                        routeOrderAttribute = new Attribute(new AttributeType(DomainObject.ATTRIBUTE_ROUTE_SEQUENCE), routeSequenceValueStr);
                                        attrList.addElement(routeOrderAttribute);

                                        // set route instructions
                                        if (routeInstructionsValueStr != null) {
                                            routeInstructionsAttribute = new Attribute(new AttributeType(DomainObject.ATTRIBUTE_ROUTE_INSTRUCTIONS), routeInstructionsValueStr);
                                            attrList.addElement(routeInstructionsAttribute);
                                        }

                                        templateTaskAttribute = new Attribute(new AttributeType(templateTaskStr), "Yes");
                                        attrList.addElement(templateTaskAttribute);

                                        // set route assignee due date option
                                        if (routeAssigneeDueDateOptStr != null) {
                                            routeAssigneeDueDateOptAttribute = new Attribute(new AttributeType(DomainObject.ATTRIBUTE_ASSIGNEE_SET_DUEDATE), routeAssigneeDueDateOptStr);
                                            attrList.addElement(routeAssigneeDueDateOptAttribute);
                                        }

                                        // set route due date offset
                                        if (dueDateOffset != null) {
                                            routeDueDateOffsetAttribute = new Attribute(new AttributeType(DomainObject.ATTRIBUTE_DUEDATE_OFFSET), dueDateOffset);
                                            attrList.addElement(routeDueDateOffsetAttribute);
                                        }


                                        // set route due date offset from
                                        if (dueDateOffsetFrom != null) {
                                            routeDateOffsetFromAttribute = new Attribute(new AttributeType(DomainObject.ATTRIBUTE_DATE_OFFSET_FROM), dueDateOffsetFrom);
                                            attrList.addElement(routeDateOffsetFromAttribute);
                                        }

                                        // set route task user attribute
                                        if (routeTaskUser != null) {
                                            routeTaskUserAttribute = new Attribute(new AttributeType(DomainObject.ATTRIBUTE_ROUTE_TASK_USER), routeTaskUser);
                                            attrList.addElement(routeTaskUserAttribute);
                                        }
                                        // Added for the bug 301391
                                        // set Review Task attribute
                                        if (reviewTask != null) {
                                            reviewTaskAttribute = new Attribute(new AttributeType(sAttReviewTask), reviewTask);
                                            attrList.addElement(reviewTaskAttribute);
                                        }
                                        // set Allow Delegation attribute
                                        if (allowDelegation != null) {
                                            allowDelegationAttribute = new Attribute(new AttributeType(DomainObject.ATTRIBUTE_ALLOW_DELEGATION), allowDelegation);
                                            attrList.addElement(allowDelegationAttribute);
                                        }

                                        // set Schedule Date attribute
                                        if (UIUtil.isNotNullAndNotEmpty(routeTaskScheduleDate)) {
                                            routeTaskScheduleDateAttribute = new Attribute(new AttributeType(DomainObject.ATTRIBUTE_SCHEDULED_COMPLETION_DATE), routeTaskScheduleDate);
                                            attrList.addElement(routeTaskScheduleDateAttribute);
                                        }


                                        relationShipRouteNode.setAttributes(context, attrList);
                                        relationShipRouteNode.close(context);


                                        // Added for bug 376886
                                        if (((DomainObject.TYPE_ROUTE_TASK_USER).equals(personObj.getTypeName()))) {
                                            String personName = PropertyUtil.getSchemaProperty(context, routeTaskUser);

                                            try {
                                                if (!UIUtil.isNullOrEmpty(personName)) {
                                                    accessUtil.setAccess(personName, AccessUtil.ROUTE_ACCESS_GRANTOR, accessUtil.getReadAccess());

                                                }
                                            } catch (MatrixException e) {
                                                throw new FrameworkException(e.toString());
                                            }
                                        }
                                        // Ended

                                    }
                                    personObj.close(context);
                                }

                            }
                        }//End while

                        if (accessUtil.getAccessList().size() > 0) {
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

            }

            Map<String, String> map = new HashMap<>();
            map.put("Route Base Policy", "policy_Document");
            map.put("Route Base Purpose", "Approval");
            map.put("Route Base State", "state_IN_WORK");

            if (objectIdArr.length > 1) {
                for (int i = 1; i < objectIdArr.length; i++) {
                    // 将 其余的 审批对象 和Route 关联
                    DomainObject elseObj = new DomainObject(objectIdArr[i]);

                    // todo 其余交付物类型的判断
                    //for 关联 route 和审批对象  加存属性 加 推状态
                    elseObj.promote(context);
                    DomainRelationship relationship = route.connect(context, "Object Route", elseObj, true);
                    relationship.setAttributeValues(context, map);
                }
            }


            route.open(context);
           // route.promote(context);
            route.close(context);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.ok("ERROR CREATE ROUTE").build();
        }
        String result = "Success create route";
        return Response.ok(result).build();
    }
}
