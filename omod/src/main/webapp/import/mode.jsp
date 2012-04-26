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

<springform:form>
	<fieldset>
		<legend>
			3.
			<spring:message code="metadatasharing.import.mode.choose" />
		</legend>
		<p>
			<input type="radio" name="importMode" value="PREVIOUS" checked />&nbsp;
			<spring:message code="metadatasharing.import.mode.previous" />
		<div style="padding-left: 4em; font-size: 0.8em;">
			<spring:message code="metadatasharing.import.mode.previous.description" />
		</div>
		</p>
		<p>
			<input type="radio" name="importMode" value="PARENT_AND_CHILD" />&nbsp;
			<spring:message code="metadatasharing.import.mode.parentAndChild" />
		<div style="padding-left: 4em; font-size: 0.8em;">
			<spring:message
				code="metadatasharing.import.mode.parentAndChild.description" />
		</div>
		</p>
		<p>
			<input type="radio" name="importMode" value="PEER_TO_PEER" />&nbsp;
			<spring:message code="metadatasharing.import.mode.peerToPeer" />
		<div style="padding-left: 4em; font-size: 0.8em;">
			<spring:message
				code="metadatasharing.import.mode.peerToPeer.description" />
		</div>
		</p>
		<p>
			<input type="radio" name="importMode" value="TEST" />&nbsp;
			<spring:message code="metadatasharing.import.mode.test" />
		<div style="padding-left: 4em; font-size: 0.8em;">
			<spring:message code="metadatasharing.import.mode.test.description" />
		</div>
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