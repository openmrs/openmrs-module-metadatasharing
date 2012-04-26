<spring:htmlEscape defaultHtmlEscape="true" /> 
<%@ page import="org.openmrs.module.metadatasharing.MetadataSharing" %>
<%
	pageContext.setAttribute("configured", MetadataSharing.getInstance().isConfigured());
%>

<ul id="menu">
        <li class="first">
                <a href="${pageContext.request.contextPath}/admin"><spring:message code="admin.title.short"/></a>
        </li>
        
       	<li <c:if test='<%= request.getRequestURI().contains("/export/list") %>'>class="active"</c:if>>
                <a href="${pageContext.request.contextPath}/module/metadatasharing/export/list.form"><spring:message code="metadatasharing.exportMetadata"/></a>
        </li>
        
        <li <c:if test='<%= request.getRequestURI().contains("/import/list") %>'>class="active"</c:if>>
                <a href="${pageContext.request.contextPath}/module/metadatasharing/import/list.form"><spring:message code="metadatasharing.importMetadata"/></a>
        </li>
        
        <li <c:if test='<%= request.getRequestURI().contains("/task/list") %>'>class="active"</c:if>>
                <a href="${pageContext.request.contextPath}/module/metadatasharing/task/list.form"><spring:message code="metadatasharing.task.manageTasks"/></a>
        </li>
        
        <li <c:if test='<%= request.getRequestURI().contains("/configure") %>'>class="active"</c:if>>
                <a href="${pageContext.request.contextPath}/module/metadatasharing/configure.form">
                	<c:choose>
                		<c:when test="${configured}">
                			<spring:message code="metadatasharing.configure"/>
                		</c:when>
                		<c:otherwise>
                			<spring:message code="metadatasharing.configure.required" htmlEscape="false"/>
                		</c:otherwise>
                	</c:choose>
                </a>
        </li>
</ul> 
<h2><spring:message code="metadatasharing.title" /></h2> 