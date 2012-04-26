<%@ include file="../template/localInclude.jsp" %>
<%@ taglib prefix="utils" uri="../taglib/javaScriptPrinter.tld"%>
<openmrs:require privilege="Share Metadata" otherwise="/login.htm" redirect="/module/metadatasharing/import/list.form" />
<%@ include file="../template/jqueryPage.jsp" %> 
<%@ include file="../template/localHeader.jsp" %>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/metadatasharing/css/metadatasharing.css"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/metadatasharing/scripts/importList.js"/>

<script type="text/javascript">
	//Messages definitions
	var $messages = { 
		checkForUpdates:'<spring:message code="metadatasharing.checkForUpdates" />',
		unsubscribe:'<spring:message code="metadatasharing.unsubscribe" />',
		subscribe: 'Subscribe',
		checkNow:'<spring:message code="metadatasharing.checkNow" />',
		neverChecked: '<spring:message code="metadatasharing.subscription.status.neverChecked" />',
		upToDate: '<spring:message code="metadatasharing.subscription.status.upToDate" />',
		updateToVersion: '<spring:message code="metadatasharing.subscription.status.updateToVersion" />',
		available: '<spring:message code="metadatasharing.subscription.status.available" />',
		importText: '<spring:message code="metadatasharing.subscription.status.importText" />',
		timeout: '<spring:message code="metadatasharing.subscription.status.timeout" />',
		invalidSubscription: '<spring:message code="metadatasharing.subscription.status.invalidSubscription" />',
		serviceUnavailable: '<spring:message code="metadatasharing.subscription.status.serviceUnavailable" />',
		duplicateSubscription: '<spring:message code="metadatasharing.subscription.status.duplicateSubscription" />',
		latestImportedVersion: '<spring:message code="metadatasharing.subscription.latestImportedVersion" />',
		neverImported: '<spring:message code="metadatasharing.subscription.neverImported" />',
		packageName: '<spring:message code="metadatasharing.subscription.packageName" />',
		areYouSure: '<spring:message code="metadatasharing.subscription.areYouSure" />',
		fatalError: '<spring:message code="metadatasharing.error.subscription.fatalError" />',
		importPackage: '<spring:message code="metadatasharing.importPackage" />',
		showEntire: '<spring:message code="metadatasharing.subscription.url.showEntire" />',
		subscriptions: '<spring:message code="metadatasharing.subscription.table" />',
		unsubscribed: 'Unsubscribed'
	}
		
	var $subscriptions = new Array();
	var s = $subscriptions ;
	//[ id:"ID", status:"Status", {name:"Name", url:"URL", localPackageVersion:"LocalPackageVersion" }, { status:"Status", remotePackageVersion:"RemotePackageVersion" } ]
	<c:forEach var="subscription" items="${subscriptions}">
		s.push([{id:${subscription.id}, status:${subscription.subscriptionStatus.value}}, {name:<utils:printJavaScript text="${subscription.name}" asString="true"/>, url:<utils:printJavaScript text="${subscription.subscriptionUrl}" asString="true"/>,version:<utils:printJavaScript text="${subscription.version}"/>,imported:${subscription.imported}}, {status:${subscription.subscriptionStatus.value},remoteVersion:<utils:printJavaScript text="${subscription.remoteVersion}"/>}]);
	</c:forEach>  
</script>
<h3><spring:message code="metadatasharing.manageSubscriptions" /></h3>

<div class="manageSubscriptions" id="manageSubscriptions">
	<table id="subscriptions">
		<thead>
			<tr>
				<th><spring:message code="metadatasharing.id" /></th>
				<th class="subscriptionColumn"><spring:message code="metadatasharing.subscription.name" /></th>
				<th class="statusColumn"><spring:message code="metadatasharing.status" /></th>
				<th class="unsubscribeColumn"><spring:message code="metadatasharing.subscribe" /></th>
			</tr>
		</thead>
	</table>
</div>
<div style="clear:both;"></div>
<%@ include file="/WEB-INF/template/footer.jsp"%>
