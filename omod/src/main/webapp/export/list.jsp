<%@ include file="../template/localInclude.jsp"%>
<%@ taglib prefix="utils" uri="../taglib/javaScriptPrinter.tld"%>
<%@ taglib prefix="colletions" uri="../taglib/javaScriptCollectionPrinter.tld"%>
<openmrs:require privilege="Share Metadata" otherwise="/login.htm" redirect="/module/metadatasharing/export/list.form" />
<%@ include file="../template/jqueryPage.jsp"%>
<%@ include file="../template/localHeader.jsp"%>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/metadatasharing/css/metadatasharing.css" />
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/metadatasharing/scripts/exportList.js"/>

<script type="text/javascript">
	//Messages definitions
	var $messages = { 
		importPackage: '<spring:message code="metadatasharing.importPackage" />',
		createPackage: '<spring:message code="metadatasharing.createPackage" />',
		notPublished: '<spring:message code="metadatasharing.notPublished" />',
		latestVersion: '<spring:message code="metadatasharing.latestVersion" />',
		latestPublishedVersion: '<spring:message code="metadatasharing.latestPublishedVersion" />',
		warning: '<spring:message code="metadatasharing.warning" />',
		downloadPackage: '<spring:message code="metadatasharing.download" />',
		deletePackage: '<spring:message code="metadatasharing.delete" />',
		latestVersionNotPublished: '<spring:message code="metadatasharing.warning.latestVersionNotPublished" />',
		oldPackage: '<spring:message code="metadatasharing.package.old" />'
	};

	var $publishConfigured = <utils:printJavaScript text="${publishConfigured}"/>;
	var $packages = new Array();
	var p = $packages;
	//[ORDER, {name:PACKAGE_NAME, publishedVersions:[1,2,..], latestVersion:LATEST_VERSION, groupUuid:GROUP, id:ID}, DESCRIPTION, DATE_CREATED]
	<c:forEach var="pack" items="${packages}">
		p.push([${order[pack.groupUuid]},{name:<utils:printJavaScript text="${pack.name}" asString="true"/>, 
			publishedVersions:<colletions:printCollectionJavaScript var="${publishedVersions[pack.groupUuid]}" />,
			latestVersion:<utils:printJavaScript text="${pack.version}"/>, groupUuid:<utils:printJavaScript text="${pack.groupUuid}" asString="true"/>,
			id: <utils:printJavaScript text="${pack.exportedPackageId}"/>},
			<utils:printJavaScript text="${pack.description}" asString="true"/>,
			<utils:printJavaScript text="${pack.dateCreated}" asString="true"/>]);
	</c:forEach>  
</script>

<h3><spring:message code="metadatasharing.shareMetadata" /></h3>
<div id="manageMetadataSharing">
	<table id="metadataPackages">
		<thead>
			<tr>
				<th></th>
				<th class="nameColumn"><spring:message code="metadatasharing.name" /></th>
				<th class="descriptionColumn"><spring:message code="metadatasharing.description" /></th>
				<th class="dateColumn"><spring:message code="metadatasharing.modificationDate" /></th>
			</tr>
		</thead>
	</table>
</div>
<%@ include file="/WEB-INF/template/footer.jsp"%>
