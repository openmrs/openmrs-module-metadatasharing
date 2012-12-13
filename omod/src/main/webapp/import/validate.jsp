<%@ taglib prefix="springform"
	uri="http://www.springframework.org/tags/form"%>
<%@ include file="../template/localInclude.jsp"%>
<%@ include file="../template/localHeader.jsp"%>
<openmrs:require privilege="Share Metadata" otherwise="/login.htm"
	redirect="/module/metadatasharing/managePackages.form" />
<%@ include file="../template/jqueryPage.jsp"%>
<openmrs:htmlInclude
	file="${pageContext.request.contextPath}/moduleResources/metadatasharing/css/metadatasharing.css" />

<h3>
	<spring:message code="metadatasharing.importPackage" />
</h3>
<style type="text/css">
#reviewPackageTable {
	width: 100%;
}
</style>

<springform:form commandName="importer" method="post">
	<fieldset>
		<legend>
			2.
			<spring:message code="metadatasharing.import.reviewPackage" />
		</legend>
		<table id="reviewPackageTable">
			<thead>
				<tr>
					<th class="to_right"><spring:message
							code="metadatasharing.field" /></th>
					<th><spring:message code="metadatasharing.description" /></th>
				</tr>
			</thead>
			<tr>
				<td class="to_right"><spring:message
						code="metadatasharing.name" />:</td>
				<td>${importer.importedPackage.name} <springform:errors
						path="package.name" cssClass="error" /></td>
			</tr>
			<tr>
				<td class="to_right"><spring:message
						code="metadatasharing.description" />:</td>
				<td>${importer.importedPackage.description} <springform:errors
						path="package.description" cssClass="error" /></td>
			</tr>
			<tr>
				<td class="to_right"><spring:message
						code="metadatasharing.openmrsVersion" />:</td>
				<td>${importer.importedPackage.openmrsVersion} <springform:errors
						path="package.openmrsVersion" cssClass="warning" /></td>
			</tr>
			<tr>
				<td class="to_right"><spring:message
						code="metadatasharing.dateExported" />:</td>
				<td><openmrs:formatDate date="${importer.importedPackage.dateCreated}"
						format="yyyy-MM-dd HH:mm:ss" /></td>
			</tr>
			<tr>
				<td class="to_right"><spring:message
						code="metadatasharing.version" />:</td>
				<td>${importer.importedPackage.version} <springform:errors
						path="package.version" cssClass="warning" /></td>
			</tr>
			<tr>
				<td></td>
				<td>
					<p>
						<springform:errors path="package.requiredModules" cssClass="error" />
					</p>
					<p>
						<springform:errors path="package.modules" cssClass="warning" />
					</p>
				</td>
			</tr>
		</table>
	</fieldset>
	<p>
		<input type="button" id="cancel"
			value="<spring:message code="metadatasharing.cancel" />"
			onclick="window.location = 'upload.form';" />
		<c:if test="${!hasErrors}">
			<input type="submit" id="next"
				value="<spring:message code="metadatasharing.next" />" />
		</c:if>
		<br />
	</p>
</springform:form>


<%@ include file="/WEB-INF/template/footer.jsp"%>