<%@ taglib prefix="springform"
	uri="http://www.springframework.org/tags/form"%>
<%@ include file="../template/localInclude.jsp"%>
<openmrs:require privilege="Share Metadata" otherwise="/login.htm"
	redirect="/module/metadatasharing/export/createPackage.form" />
<%@ include file="../template/jqueryPage.jsp"%>
<%@ include file="../template/localHeader.jsp"%>
<style type="text/css">
.packageDescription {
	width: 400px;
	height: 150px;
}

.error {
	vertical-align: top !important;
}

.grey {
	color: #999;
}
</style>
<h3>
	<spring:message code="metadatasharing.createPackage" />
</h3>

<c:if test="${hasErrors}">
	<div class="warning">
		<span><spring:message code="metadatasharing.error.editPackage" /><br/><br/></span>
	
		<c:if test="${!empty missingClasses}">
			<b><spring:message code="metadatasharing.error.editPackage.missingClasses" /></b>
		</c:if>
		<c:forEach var="missingClass" items="${missingClasses}">
			${missingClass}<br />
		</c:forEach>
		<c:if test="${!empty missingClasses}">
			<br/><br/>
		</c:if>	
			
		<c:if test="${!empty missingItems}">
			<b><spring:message code="metadatasharing.error.editPackage.missingItems" /></b>
		</c:if>
		<c:forEach var="missingType" items="${missingItems}">
			<table width="100%"> 
				<thead> 
					<th><spring:message code="metadatasharing.type" /></th>
					<th><spring:message code="metadatasharing.uuid" /></th>
					<th><spring:message code="metadatasharing.name" /></th>
				</thead>
				<tbody>
					<c:forEach var="missingItem" items="${missingType.value}">
						<tr>
							<td>${missingType.key}</td>
							<td>${missingItem.uuid}</td>
							<td>${missingItem.name}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:forEach>
	</div>
</c:if>

<springform:form modelAttribute="exporter" method="post">
	<fieldset>
		<legend>
			1.
			<spring:message code="metadatasharing.specifyPackage" />
		</legend>
		<p>
		<table>
			<tr>
				<td class="to_upper to_right"><spring:message
						code="metadatasharing.name" />:</td>
				<td><springform:input path="package.name" maxlength="64" /> <springform:errors
						path="package.name" cssClass="error" /></td>
			</tr>
			<tr>

				<td class="to_upper to_right"><spring:message
						code="metadatasharing.description" />:</td>
				<td><springform:textarea path="package.description"
						cssClass="packageDescription" /> <springform:errors
						path="package.description" cssClass="error" /> <br /></td>
			</tr>
		</table>
		</p>
	</fieldset>
	<fieldset>
		<legend>
			2.
			<spring:message code="metadatasharing.packageUpdates" />
		</legend>
		<c:if test="${!publishConfigured}">
			<p>
				<spring:message code="metadatasharing.configure.urlPrefix" />
			</p>
		</c:if>
		<p <c:if test="${!publishConfigured}">class="grey"</c:if>>
			<springform:checkbox path="exportedPackage.published"
				disabled="${!publishConfigured}" />
			<spring:message code="metadatasharing.packageUpdates.description" />
		</p>
	</fieldset>
	<p>
		<input type="submit"
			value="<spring:message code="metadatasharing.next" />" /> <br />
	</p>
</springform:form>
<%@ include file="/WEB-INF/template/footer.jsp"%>
