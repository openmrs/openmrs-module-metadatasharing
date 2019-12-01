<%@ include file="../template/localInclude.jsp" %>
<%@ taglib prefix="utils" uri="../taglib/javaScriptPrinter.tld"%>
<openmrs:require privilege="Share Metadata" otherwise="/login.htm" redirect="/module/metadatasharing/export/details.form" />
<%@ include file="../template/jqueryPage.jsp" %> 
<%@ include file="../template/localHeader.jsp" %>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/metadatasharing/css/metadatasharing.css"/> 
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/metadatasharing/scripts/packageSummary.js"/>

<script type="text/javascript">
	//Messages definitions
	var $messages = { 
		packageVersions: '<spring:message code="metadatasharing.packageSummary.packageVersions" />',
		latestVersionNotPublished: '<spring:message code="metadatasharing.packageSummary.latestVersionNotPublished" />',
		configure: '<spring:message code="metadatasharing.configure.urlPrefix" />', 
		pleasePublish: '<spring:message code="metadatasharing.packageSummary.pleasePublish" />', 
		confirmDelete: '<spring:message code="metadatasharing.packageSummary.confirmDelete" />', 
		unpublish:'<spring:message code="metadatasharing.packageSummary.unpublish" />',
		publish:'<spring:message code="metadatasharing.packageSummary.publish" />',
		newVersionThisOne: '<spring:message code="metadatasharing.packageSummary.newVersionThisOne" />', 
		downloadZipped: '<spring:message code="metadatasharing.packageSummary.downloadZipped" />', 
		deletePackage: '<spring:message code="metadatasharing.delete" />', 
	}
	
	var $packages = new Array();
	var p = $packages;
	//[ID, VERSION, NAME, DESCRIPTION, DATE_CREATED, PUBLISHED, GROUP]
	<c:forEach var="pack" items="${packages}">
		p.push([<utils:printJavaScript text="${pack.exportedPackageId}"/>,<utils:printJavaScript text="${pack.version}"/>,
		        <utils:printJavaScript text="${pack.name}" asString="true"/>, 
		<utils:printJavaScript text="${pack.description}" asString="true"/>,
		<utils:printJavaScript text="${pack.dateCreated}" asString="true"/>,
		<utils:printJavaScript text="${pack.published}"/>, <utils:printJavaScript text="${pack.groupUuid}" asString="true"/>]);
	</c:forEach>
	var $latestVersionPublished = <utils:printJavaScript text="${packages[0].published}" />;
	var $group = <utils:printJavaScript text="${packages[0].groupUuid}" asString="true"/>; 
	var $publishUrl = <utils:printJavaScript text="${publishUrl}" asString="true"/>; 
	var $publishConfigured = <utils:printJavaScript text="${publishConfigured}"/>; 
	
</script>
<div id="packageSummary">
	<h3><spring:message code="metadatasharing.packageSummary" /></h3>
	<input type="button" value="<spring:message code="metadatasharing.back" />" id="back"/>
	<p id="shareUrl">
		<spring:message code="metadatasharing.packageSummary.shareUrl" />
		<br />
		<input type="text" />
	</p>
	<div class="warning" id="notPublishedWarning" style="display:none;"></div>
    <input type="button" value="<spring:message code="metadatasharing.packageSummary.downloadLatest"  />" id="downloadLatest" class="toolbarButton ui-icon-download"/>
    <input type="button" value="<spring:message code="metadatasharing.packageSummary.newVersionLatest" />" id="latest" class="toolbarButton ui-icon-version"/>
	<input type="button" value="<spring:message code="metadatasharing.packageSummary.newVersionScratch" />" id="scratch" class="toolbarButton ui-icon-scratch"/>
	<table id="packages">
		<thead>
			<tr>
				<th>ID</th>
				<th class="versionColumn"><spring:message code="metadatasharing.version" /></th>
				<th class="nameColumn"><spring:message code="metadatasharing.name" /></th>
				<th class="descriptionColumn"><spring:message code="metadatasharing.description" /></th>
				<th class="dateColumn"><spring:message code="metadatasharing.dateCreated" /></th>
				<th class="publishedColumn"><spring:message code="metadatasharing.published" /></th>
				<th class="actionColumn"><spring:message code="metadatasharing.action" /></th>
			</tr>
		</thead>
	</table>
</div>
<div style="clear:both;"></div>
<%@ include file="/WEB-INF/template/footer.jsp"%>
