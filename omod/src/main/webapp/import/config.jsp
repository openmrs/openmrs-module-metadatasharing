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
<h3>
	<spring:message code="metadatasharing.importPackage" />
</h3>

<springform:form commandName="importer">
	<fieldset>
		<legend>
			4.
			<spring:message code="metadatasharing.import.mode.adjust" />
		</legend>
		<p>
			<spring:message code="metadatasharing.exactMatch" /> <br />
			&nbsp; &nbsp; <springform:radiobutton path="importConfig.exactMatch"
				value="PREFER_MINE" />
			<spring:message code="metadatasharing.mergePreferMine" /> <br />
			&nbsp; &nbsp; <springform:radiobutton path="importConfig.exactMatch"
				value="PREFER_THEIRS" />
			<spring:message code="metadatasharing.mergePreferTheirs" /> <br />
			&nbsp; &nbsp; <springform:radiobutton path="importConfig.exactMatch" value="OMIT" />
			<spring:message code="metadatasharing.omit" /> <br />
			&nbsp; &nbsp; <springform:radiobutton path="importConfig.exactMatch" value="OVERWRITE_MINE" />
			<spring:message code="metadatasharing.overwritemine" /> <br /> <br />

			&nbsp; &nbsp; <springform:checkbox path="importConfig.confirmExactMatch" />
			<spring:message code="metadatasharing.askIfDatesDiffer" />
		</p>
		<p>
			<spring:message code="metadatasharing.possibleMatch" /> <br />
			&nbsp; &nbsp; <springform:radiobutton path="importConfig.possibleMatch"
				value="CREATE" />
			<spring:message code="metadatasharing.createNew" /> <br />
			&nbsp; &nbsp; <springform:radiobutton path="importConfig.possibleMatch"
				value="PREFER_MINE" />
			<spring:message code="metadatasharing.mergePreferMine" /> <br />
			&nbsp; &nbsp; <springform:radiobutton path="importConfig.possibleMatch"
				value="PREFER_THEIRS" />
			<spring:message code="metadatasharing.mergePreferTheirs" /> <br />
			&nbsp; &nbsp; <springform:radiobutton path="importConfig.possibleMatch"
				value="OMIT" />
			<spring:message code="metadatasharing.omit" /> <br />
			&nbsp; &nbsp; <springform:radiobutton path="importConfig.possibleMatch" value="OVERWRITE_MINE" />
			<spring:message code="metadatasharing.overwritemine" /> <br /> <br />

			&nbsp; &nbsp; <springform:checkbox path="importConfig.confirmPossibleMatch" />
			<spring:message code="metadatasharing.askIfDatesDiffer" />
		</p>
		<p>
			<spring:message code="metadatasharing.noMatch" /> <br />
			&nbsp; &nbsp; <springform:radiobutton path="importConfig.noMatch" value="CREATE" />
			<spring:message code="metadatasharing.createNew" /> <br />
			&nbsp; &nbsp; <springform:radiobutton path="importConfig.noMatch"
				value="PREFER_MINE" />
			<spring:message code="metadatasharing.mergePreferMine" /> <br />
			&nbsp; &nbsp; <springform:radiobutton path="importConfig.noMatch"
				value="PREFER_THEIRS" />
			<spring:message code="metadatasharing.mergePreferTheirs" /> <br />
			&nbsp; &nbsp; <springform:radiobutton path="importConfig.noMatch" value="OMIT" />
			<spring:message code="metadatasharing.omit" /> <br /> <br />

			&nbsp; &nbsp; <springform:checkbox path="importConfig.confirmNoMatch" />
			<spring:message code="metadatasharing.askEveryTime" /> <br /> <br />

			&nbsp; &nbsp; <springform:checkbox path="importConfig.skipAssessing" />
			<spring:message code="metadatasharing.import.skipAssessing" />
		</p>
	</fieldset>
	<p>
		<input type="button"
			value="<spring:message code="metadatasharing.cancel" />"
			onclick="window.location='upload.form'" /> <input
			type="submit" value="<spring:message code="metadatasharing.next" />" />

		<br />
	</p>
</springform:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>