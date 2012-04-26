<%@ include file="../template/localInclude.jsp" %>
<%@ taglib prefix="utils" uri="../taglib/javaScriptPrinter.tld"%>
<openmrs:require privilege="Share Metadata" otherwise="/login.htm" redirect="/module/metadatasharing/import/list.form" />
<%@ include file="../template/jqueryPage.jsp" %>
<%@ include file="../template/localHeader.jsp" %>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/metadatasharing/scripts/downloadPackage.js"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/metadatasharing/css/metadatasharing.css"/>

<script type="text/javascript">
	var $id = <utils:printJavaScript text="${subscription.id}" />;
	var $messages = {
		timeout: '<spring:message code="metadatasharing.subscription.status.timeout" />',
		invalidSubscription: '<spring:message code="metadatasharing.subscription.status.invalidSubscription" />',
		serviceUnavailable: '<spring:message code="metadatasharing.subscription.status.serviceUnavailable" />',
		fatalError: '<spring:message code="metadatasharing.error.subscription.fatalError" />',
		showStackTrace: '<spring:message code="metadatasharing.showStackTrace" />',
		hideStackTrace: '<spring:message code="metadatasharing.hideStackTrace" />',
		connecting: '<spring:message code="metadatasharing.connecting" />',
		downloading: '<spring:message code="metadatasharing.downloading" />'
	}
</script>

<h3><spring:message code="metadatasharing.subscription.downloadPackage" /></h3>
<div id="downloadPackage">
	<div id="downloadStatusBar"></div>
	<div id="downloadErrorBar" class="error"></div>
	<input type="button" value="<spring:message code="metadatasharing.back" />" id="backButton"/>
	<br />
	<small>
		<a id="toggleStackTrace" href="javascript:void(0);"><spring:message code="metadatasharing.showStackTrace" /></a>
	</small>
	<div id="errorStackTrace"></div>
</div>
<div style="clear:both;"></div>
<%@ include file="/WEB-INF/template/footer.jsp"%>
