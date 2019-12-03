<%@ taglib prefix="springform"
	uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="h" uri="/WEB-INF/view/module/metadatasharing/taglib/openmrsObjectHandler.tld" %>
<%@ include file="../template/localInclude.jsp" %>
<openmrs:require privilege="Share Metadata" otherwise="/login.htm" redirect="/module/metadatasharing/export/editPackage.form" />
<%@ include file="../template/jqueryPage.jsp" %> 
<%@ include file="../template/localHeader.jsp"%>

<script type="text/javascript">
var $j = jQuery.noConflict();
var selectItems;
$j(document).ready(function() {
	$j("#reviewPackageTable").dataTable({
		"bJQueryUI": true,
		"bSort": false,
		"bPaginate": false,
		"sDom": "<t>"
	});
	selectItems = $j('#selectItems').dialog(
			{ autoOpen: false, modal: true, width: '80%', height: $j(window).height()-200,
				resizable: false, draggable: false,
				title: '<spring:message code="metadatasharing.selectItemsToExport" />',
				close: function(event, ui) { $j(this).dialog("option", "buttons", null); } });
	$j('#addMetadata').click(function() {
		selectItems.load('selectItems.form', function() {
			selectItems.dialog("open");
		});
	});
} );
function chooseIndividually(type) {
	if (selectItems != null) {
		selectItems.load('selectItems.form?type=' + encodeURIComponent(type), function() {
			selectItems.dialog("open");
		});
	}
}
function hideShow(elementId) {
	$j("#"+elementId).toggle();
}
</script>

<style>
	.item-details-table {padding: 10px;}
</style>

<div id="selectItems"></div>
<h3><spring:message code="metadatasharing.createPackage" />: ${exporter.getPackage().getName()}</h3>

<springform:form modelAttribute="exporter" action="complete.form" method="post">
	<springform:errors path="package" cssStyle="error" />
	<fieldset>
		<legend>3. <spring:message code="metadatasharing.selectItemsToExport" /></legend>
		<p><input type="button" id="addMetadata" value="<spring:message code="metadatasharing.addMetadataToPackage" />" /></p>
	</fieldset>
	<c:if test="${!empty packageItems.items}">
		<fieldset>
			<legend>4. <spring:message code="metadatasharing.reviewChoice" /></legend>
			<table style="width:100%;">
				<thead>
					<tr><th></th><th><th></th></tr>
				</thead>
				<tbody>
					<c:forEach var="packageItem" items="${packageItems.items}" varStatus="status">
						<tr>
							<td style="vertical-align: top; padding-right: 10px; width:100%;">
								<a href="javascript:hideShow('item-details-${packageItem.key}')">
									${packageItem.key}
									<c:choose>
										<c:when test="${empty packageItems.completeTypesMap[packageItem.key]}">
											(${fn:length(packageItem.value)} selected)
										</c:when>
										<c:otherwise>
											(All ${fn:length(packageItem.value)} selected)
										</c:otherwise>
									</c:choose>
								</a>
								<table class="item-details-table" id="item-details-${packageItem.key}" style="display: none;">
									<tr>
										<td style="padding-top:10px; font-weight: bold;"><spring:message code="metadatasharing.name"/></td>
										<td style="padding-top:10px; font-weight: bold;"><spring:message code="metadatasharing.UUID"/></td>
									</tr>
									<c:forEach var="item" items="${packageItem.value}" varStatus="itemStatus">
										<tr>
											<td style="padding-right: 10px;">
												<c:set var="itemUrl" value="${h:metadataUrl(item.classname, item.uuid)}" />
												<c:choose>
													<c:when test="${empty itemUrl}">
														${item.name}
													</c:when>
													<c:otherwise>
														<a href="${pageContext.request.contextPath}/${itemUrl}" target="__new">${item.name}</a>
													</c:otherwise>
												</c:choose>
											</td>
											<td>${item.uuid}</td>
										</tr>
									</c:forEach>
								</table>
							</td>
							<td style="vertical-align: top; white-space: nowrap;">
								<c:if test="${empty packageItems.completeTypesMap[packageItem.key]}">
								<a href="addAllItems.form?includeRetired=true&type=${packageItem.key}">
								</c:if>
								<spring:message code="metadatasharing.addAll" /><c:if test="${empty packageItems.completeTypesMap[packageItem.key]}"></a></c:if>
								<a href="javascript:chooseIndividually('${packageItem.key}');"><spring:message code="metadatasharing.chooseIndividually" /></a>
								<c:url value="removeAllItems.form?type=${packageItem.key}" var="removeAllUrl" />
								<a href="${removeAllUrl}"><spring:message code="metadatasharing.removeAll" /></a>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</fieldset>
	</c:if>
	<p>
	<c:if test="${!empty packageItems.items}">
		<input type="submit" id="export" value="<spring:message code="metadatasharing.export" />" />
	</c:if>
	<br />
	</p>
</springform:form>
<%@ include file="/WEB-INF/template/footer.jsp"%>
