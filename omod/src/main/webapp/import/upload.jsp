<%@ taglib prefix="h"
	uri="/WEB-INF/view/module/metadatasharing/taglib/openmrsObjectHandler.tld"%>
<%@ taglib prefix="springform"
	uri="http://www.springframework.org/tags/form"%>
<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="../template/localHeader.jsp" %>
<openmrs:require privilege="Share Metadata" otherwise="/login.htm"
	redirect="/module/metadatasharing/import/list.form" />
<openmrs:htmlInclude file="/scripts/jquery/jquery-1.3.2.min.js" />
<openmrs:htmlInclude
	file="/scripts/jquery/dataTables/css/dataTables.css" />
<openmrs:htmlInclude
	file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />
<openmrs:htmlInclude
	file="/scripts/jquery-ui/js/jquery-ui-1.7.2.custom.min.js" />
<openmrs:htmlInclude
	file="/scripts/jquery-ui/css/redmond/jquery-ui-1.7.2.custom.css" />
<openmrs:htmlInclude
	file="${pageContext.request.contextPath}/moduleResources/metadatasharing/css/metadatasharing.css" />
<style>
td.upload {
	padding: 0px 5px;
}

table.upload {
	background-color: whitesmoke;
	border: 1px solid lightgrey;
	padding: 5px;
	margin: 10px;
}

#url {
	width: 575px;
}

#file {
	width: 575px;
	background: white;
}

.titleColumn {
	width: 30px;
}
</style>
<h3>
	<spring:message code="metadatasharing.importPackage" />
</h3>

<springform:form commandName="uploadForm" enctype="multipart/form-data"
	id="uploadForm">
	<fieldset>
		<legend>
			1.
			<spring:message code="metadatasharing.chooseFileToImport" />
		</legend>
		<table class="upload">
			<tr>
				<td class="titleColumn"><spring:message
						code="metadatasharing.file" /></td>
				<td><spring:message code="metadatasharing.file.packageZip" /></td>
			</tr>
			<tr>
				<td><img
					src="${pageContext.request.contextPath}/moduleResources/metadatasharing/images/package.png" />
				</td>
				<td><input type="file" name="file" id="file" size="60" /><br />
					<springform:errors path="file" cssClass="error" /></td>
			</tr>
		</table>
		<p>
			&nbsp; -
			<spring:message code="metadatasharing.or" />
			-
		</p>
		<table class="upload">
			<tr>
				<td class="titleColumn"><spring:message
						code="metadatasharing.url" /></td>
				<td><spring:message code="metadatasharing.packageUrl" /></td>
			</tr>
			<tr>
				<td><img
					src="${pageContext.request.contextPath}/moduleResources/metadatasharing/images/subscription.png" /></td>
				<td><input type="text" name="url" id="url"
					value="<spring:message htmlEscape="true" code="${url}"/>" /><br />
					<springform:errors path="url" cssClass="error" /></td>
			</tr>
		</table>
	</fieldset>
	<p>
		<input type="button"
			value="<spring:message code="metadatasharing.cancel" />"
			onclick="window.location='list.form'" /> <input
			type="submit" value="<spring:message code="metadatasharing.next" />"
			onclick="$j('#uploadForm').submit(); $j(this).attr('disabled', true);" />

		<br />
	</p>
</springform:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>