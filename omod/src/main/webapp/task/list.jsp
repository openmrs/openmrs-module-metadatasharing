<%@ include file="../template/localInclude.jsp"%>
<openmrs:require privilege="Share Metadata" otherwise="/login.htm"
	redirect="/module/metadatasharing/task/manageTasks.form" />
<%@ include file="../template/localHeader.jsp"%>

<h3>
	<spring:message code="metadatasharing.task.manageTasks" />
</h3>

<ol>
	<c:forEach var="task" items="${tasks}">
		<li><spring:message code="${task.type.code}" /> - <strong><spring:message
					text="${task['package']['name']}" />:</strong> <em><spring:message
					text="${task['package']['description']}" /></em> - <c:choose>
				<c:when test="${!task.completed}">
					<img src="${pageContext.request.contextPath}/moduleResources/metadatasharing/images/loading.gif" />
					<spring:message code="metadatasharing.task.running" />
				</c:when>
				<c:when test="${!empty task.errors}">
					<spring:message code="metadatasharing.task.failed" />
				</c:when>
				<c:otherwise>
					<spring:message code="metadatasharing.task.completed" />
				</c:otherwise>
			</c:choose> <a href="details.form?uuid=${task.uuid}"><spring:message
					code="general.view" /></a> <c:if test="${!task.active}">
				<a href="remove.form?uuid=${task.uuid}"><spring:message
						code="general.remove" /></a>
			</c:if></li>
	</c:forEach>
</ol>

<c:if test="${empty tasks}"><p> <spring:message code="metadatasharing.noTasks" /> </p></c:if>

<%@ include file="/WEB-INF/template/footer.jsp"%>
