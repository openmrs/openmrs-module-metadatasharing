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
	<fieldset class="choose-import-mode">
		<legend>
			3.
			<spring:message code="metadatasharing.import.mode.choose" />
		</legend>
		<p>
			<input id="mode-PREVIOUS" type="radio" name="importMode" value="PREVIOUS" checked />&nbsp;
			<label for="mode-PREVIOUS">
				<spring:message code="metadatasharing.import.mode.previous" />
				<br/>
				<span class="import-mode-description">
					<spring:message code="metadatasharing.import.mode.previous.description" />
				</span>
			</label>
		</p>
		<p>
			<input id="mode-PARENT_AND_CHILD" type="radio" name="importMode" value="PARENT_AND_CHILD" />&nbsp;
			<label for="mode-PARENT_AND_CHILD">
				<spring:message code="metadatasharing.import.mode.parentAndChild" />
				<br/>
				<span class="import-mode-description">
					<spring:message
						code="metadatasharing.import.mode.parentAndChild.description" />
				</span>
			</label>
		</p>
		<p>
			<input id="mode-PEER_TO_PEER" type="radio" name="importMode" value="PEER_TO_PEER" />&nbsp;
			<label for="mode-PEER_TO_PEER">
				<spring:message code="metadatasharing.import.mode.peerToPeer" />
				<br/>
				<span class="import-mode-description">
					<spring:message
						code="metadatasharing.import.mode.peerToPeer.description" />
				</span>
			</label>
		</p>
		<p>
			<input id="mode-TEST" type="radio" name="importMode" value="TEST" />&nbsp;
			<label for="mode-TEST">
				<spring:message code="metadatasharing.import.mode.test" />
				<br/>
				<span class="import-mode-description">
					<spring:message code="metadatasharing.import.mode.test.description" />
				</span>
			</label>
		</p>
		<p>
			<input id="mode-MIRROR" type="radio" name="importMode" value="MIRROR" />&nbsp;
			<label for="mode-MIRROR">
				<spring:message code="metadatasharing.import.mode.mirror" />
				<br/>
				<span class="import-mode-description">
					<spring:message code="metadatasharing.import.mode.mirror.description" />
				</span>
			</label>
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