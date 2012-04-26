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
			1. Exact Match: <br />
			&nbsp; &nbsp; <springform:radiobutton path="importConfig.exactMatch"
				value="PREFER_MINE" />
			Merge (prefer mine) <br />
			&nbsp; &nbsp; <springform:radiobutton path="importConfig.exactMatch"
				value="PREFER_THEIRS" />
			Merge (prefer theirs) <br />
			&nbsp; &nbsp; <springform:radiobutton path="importConfig.exactMatch" value="OMIT" />
			Omit <br /> <br />

			&nbsp; &nbsp; <springform:checkbox path="importConfig.confirmExactMatch" />
			Ask if dates differ
		</p>
		<p>
			2. Possible Match: <br />
			&nbsp; &nbsp; <springform:radiobutton path="importConfig.possibleMatch"
				value="CREATE" />
			Create new <br />
			&nbsp; &nbsp; <springform:radiobutton path="importConfig.possibleMatch"
				value="PREFER_MINE" />
			Merge (prefer mine) <br />
			&nbsp; &nbsp; <springform:radiobutton path="importConfig.possibleMatch"
				value="PREFER_THEIRS" />
			Merge (prefer theirs) <br />
			&nbsp; &nbsp; <springform:radiobutton path="importConfig.possibleMatch"
				value="OMIT" />
			Omit <br /> <br />

			&nbsp; &nbsp; <springform:checkbox path="importConfig.confirmPossibleMatch" />
			Ask if dates differ
		</p>
		<p>
			3. No Match: <br />
			&nbsp; &nbsp; <springform:radiobutton path="importConfig.noMatch" value="CREATE" />
			Create new <br />
			&nbsp; &nbsp; <springform:radiobutton path="importConfig.noMatch"
				value="PREFER_MINE" />
			Merge (prefer mine) <br />
			&nbsp; &nbsp; <springform:radiobutton path="importConfig.noMatch"
				value="PREFER_THEIRS" />
			Merge (prefer theirs) <br />
			&nbsp; &nbsp; <springform:radiobutton path="importConfig.noMatch" value="OMIT" />
			Omit <br /> <br />

			&nbsp; &nbsp; <springform:checkbox path="importConfig.confirmNoMatch" />
			Ask every time (lets you search for replacements)
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