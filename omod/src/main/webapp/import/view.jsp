<%@ taglib prefix="h"
	uri="/WEB-INF/view/module/metadatasharing/taglib/openmrsObjectHandler.tld"%>
<%@ taglib prefix="springform"
	uri="http://www.springframework.org/tags/form"%>
<%@ include file="../template/localInclude.jsp"%>
<openmrs:require privilege="Share Metadata" otherwise="/login.htm"
	redirect="/module/metadatasharing/managePackages.form" />
<%@ include file="../template/jqueryPage.jsp"%>
<openmrs:htmlInclude
	file="${pageContext.request.contextPath}/moduleResources/metadatasharing/css/metadatasharing.css" />

<script type="text/javascript">
	var $j = jQuery.noConflict();

	$j(document)
			.ready(
					function() {
						$j("#assessItemsTable")
								.dataTable(
										{
											"bProcessing" : true,
											"bServerSide" : true,
											"sAjaxSource" : "json/importItems.form",
											"aoColumns" : [ {
												"sName" : "index",
												"bVisible" : false
											}, {
												"sName" : "type"
											}, {
												"sName" : "incoming"
											}, {
												"sName" : "existing"
											}, {
												"sName" : "importType"
											}, {
												"sName" : "assessed",
												"bVisible" : false
											} ],
											"bJQueryUI" : true,
											"sPaginationType" : "full_numbers",
											"bLengthChange" : false,
											"bSort" : false,
											"bFilter" : false,
											"iDisplayLength" : 25,
											"sDom" : '<"fg-toolbar ui-corner-tl ui-corner-tr ui-helper-clearfix"r>t<"fg-toolbar ui-corner-bl ui-corner-br ui-helper-clearfix"ip>',
											"fnRowCallback" : function(nRow,
													aData, iDisplayIndex) {
												var index = aData[0];
												$j(":nth-child(4)", nRow)
														.append(
																" <a href=\"assessItem.form?index="
																		+ index
																		+ "\">assess</a>");
												if (aData[5] == "true") {
													if ($j(nRow)
															.hasClass("odd")) {
														$j(nRow).removeClass(
																"odd");
														$j(nRow).addClass(
																"oddAssessed");
													} else {
														$j(nRow).removeClass(
																"even");
														$j(nRow).addClass(
																"evenAssessed");
													}
												}
												return nRow;
											}
										});
					});
</script>

<h2>
	<spring:message code="metadatasharing.title" />
</h2>
<h3>
	<spring:message code="metadatasharing.importPackage" />
</h3>

<fieldset>
	<legend>
		5.
		<spring:message code="metadatasharing.import.summary" />
	</legend>

	<c:if test="${importer.partsCount > 1}">
		<p>
		<div>
			<span class="normal"> <spring:message
					code="metadatasharing.import.parts" />:
			</span>
			<c:forEach var="part" items="${parts}">
				<c:set var="decoration" value="class=\"error\"" />
				<c:if test="${part.value}">
					<c:set var="decoration" value="class=\"correct\"" />
				</c:if>
				<c:if test="${part.key == itemsStats.part}">
					<c:set var="decoration" value="class=\"normal\"" />
				</c:if>
				<span${decoration}><a href="load.form?part=${part.key}">${part.key}</a></span> &nbsp; &nbsp;
	</c:forEach>
		</div>
		</p>
	</c:if>

	<table width="90%">
		<tr>
			<td class="to_right to_upper"><spring:message
					code="metadatasharing.items" />:</td>
			<td>${itemsStats.totalCount}</td>
		</tr>
		<tr>
			<td></td>
			<td>
				<table width="90%">
					<tr>
						<td width="20%"><b><spring:message
									code="metadatasharing.needAssesment" /></b></td>
						<td width="20%"><b><spring:message
									code="metadatasharing.createNew" /></b></td>
						<td width="20%"><b><spring:message
									code="metadatasharing.skipIfPossible" /></b></td>
						<td width="20%"><b>Merge<br /> (Prefer Mine)
						</b></td>
						<td width="20%"><b>Merge<br /> (Prefer Theirs)
						</b></td>
					</tr>
					<tr>
						<td class="to_upper"><c:forEach var="item"
								items="${itemsStats.notAssessedCounts}">
					${item.key} (${item.value})<br />
							</c:forEach></td>
						<td class="to_upper"><c:forEach var="item"
								items="${itemsStats.createCounts}">
					${item.key} (${item.value})<br />
							</c:forEach></td>
						<td class="to_upper"><c:forEach var="item"
								items="${itemsStats.omitCounts}">
					${item.key} (${item.value})<br />
							</c:forEach></td>
						<td class="to_upper"><c:forEach var="item"
								items="${itemsStats.preferMineCounts}">
					${item.key} (${item.value})<br />
							</c:forEach></td>
						<td class="to_upper"><c:forEach var="item"
								items="${itemsStats.perferTheirsCounts}">
					${item.key} (${item.value})<br />
							</c:forEach></td>
					</tr>
				</table>
			</td>
		</tr>
	</table>

	<div id="assessItemsTableDialog">
		<c:set var="allNotAssessedItemsCount"
			value="${itemsStats.notAssessedTotalCount}" />
		<c:if test="${allNotAssessedItemsCount > 0}">
			<p>
				<spring:message code="metadatasharing.itemsLeft"
					arguments="${allNotAssessedItemsCount}" />
				<input type="button" value="Start assessing"
					onclick="window.location='assessNextItem.form'" />
			</p>
		</c:if>
		<table id="assessItemsTable" style="width: 100%">
			<thead>
				<tr>
					<th></th>
					<th><spring:message code="metadatasharing.type" /></th>
					<th><spring:message code="metadatasharing.incoming" /></th>
					<th><spring:message code="metadatasharing.existing" /></th>
					<th><spring:message code="metadatasharing.action" /></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
			</tbody>
		</table>
	</div>
</fieldset>
<p>
	<input type="button"
		value="<spring:message code="metadatasharing.back" />"
		onclick="window.location='validate.form'" />
	<c:choose>
		<c:when test="${itemsStats.notAssessedTotalCount > 0}">
			<input type="button"
				value="<spring:message code="metadatasharing.import.startAssessing" />"
				onclick="window.location='assessNextItem.form'" />
		</c:when>
		<c:when test="${!empty loadNext}">
			<input type="button"
				value="<spring:message code="metadatasharing.import.loadNextPart" />"
				onclick="window.location='load.form?part=${loadNext}'; $j(this).attr('disabled', true);" />
		</c:when>
		<c:otherwise>
			<input type="button"
				value="<spring:message code="metadatasharing.import" />"
				onclick="window.location='complete.form'; $j(this).attr('disabled', true);" />
		</c:otherwise>
	</c:choose>
	<br />
</p>

<%@ include file="/WEB-INF/template/footer.jsp"%>