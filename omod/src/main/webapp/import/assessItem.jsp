<%@ taglib prefix="h"
	uri="/WEB-INF/view/module/metadatasharing/taglib/openmrsObjectHandler.tld"%>
<%@ taglib prefix="springform"
	uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="utils" uri="../taglib/javaScriptPrinter.tld"%>
<%@ include file="../template/localInclude.jsp"%>
<%@ include file="../template/localHeader.jsp"%>
<openmrs:require privilege="Share Metadata" otherwise="/login.htm"
	redirect="/module/metadatasharing/managePackages.form" />
<%@ include file="../template/jqueryPage.jsp"%>
<openmrs:htmlInclude
	file="${pageContext.request.contextPath}/moduleResources/metadatasharing/css/metadatasharing.css" />

<script type="text/javascript">
	var $j = jQuery.noConflict();
	var chooseExistingTable;
	var chooseExistingDialog;

	$j(document).ready(function() {
		chooseExistingTable = $j("#chooseExistingTable")
				.dataTable(
						{
							"bProcessing" : true,
							"bServerSide" : true,
							"aoColumns" : [ {
								"sName" : "name"
							}, {
								"sName" : "description"
							}, {
								"sName" : "uuid"
							}, {
								"sName" : "id"
							}, {
								"sName" : "retired"
							} ],
							"bJQueryUI" : true,
							"sPaginationType" : "full_numbers",
							"bLengthChange" : false,
							"bSort" : false,
							"iDisplayLength" : 25,
							"sDom" : '<"fg-toolbar ui-corner-tl ui-corner-tr ui-helper-clearfix"lfr>t<"fg-toolbar ui-corner-bl ui-corner-br ui-helper-clearfix"ip>',
							"fnRowCallback" : function(nRow,
									aData, iDisplayIndex) {
								$j(":last-child", nRow)
										.replaceWith(
												"<input type=\"button\" value=\"choose\" onclick=\"window.location='assessItem.form?index=${assessItemForm.index}&uuid=" + aData[2] + "'\" />");

								if (aData[4] == "true") {
									$j('td', nRow).addClass("retired");
								}

								return nRow;
							}
						});
		chooseExistingDialog = $j('#chooseExistingDialog')
				.dialog(
						{
							autoOpen : false,
							modal : true,
							width : '80%',
							height : $j(window).height() - 200,
							resizable : false,
							draggable : false,
							title : 'Choose replacement'
						});

		$j('#omitButton').click(function() {
			$j('#existingItem').hide(200);
		});
		$j('#createButton').click(function() {
			$j('#existingItem').hide(200);
		});
		$j('#preferMineButton').click(function() {
			$j('#existingItem').show(200);
		});
		$j('#preferTheirsButton').click(function() {
			$j('#existingItem').show(200);
		});

		<c:if test="${empty item.existing || item.importType.create || item.importType.omit}">
		$j('#existingItem').hide(200);
		</c:if>

		highlightDifferences();

		$j('#chooseExistingButton').click(showChooseExistingDialog);

		$j('#nextButton').focus();
	});

	function highlightDifferences() {
		highlightDifferent("#incomingName", "#existingName");
		highlightDifferent("#incomingDescription", "#existingDescription");
		highlightDifferent("#incomingUUID", "#existingUUID");
		highlightDifferent("#incomingDateChanged", "#existingDateChanged");
		<c:if test="${type == 'Concept'}">
		highlightDifferent("#incomingDatatype", "#existingDatatype");
		highlightDifferent("#incomingClass", "#existingClass");
		</c:if>
	}

	function highlightDifferent(incoming, existing) {
		var incoming = $j(incoming);
		var existing = $j(existing);
		if ($j.trim(incoming.text()) != $j.trim(existing.text())) {
			incoming.addClass("warning");
			existing.addClass("warning");
		}
	}

	function showChooseExistingDialog() {
		var settings = chooseExistingTable.fnSettings();
		settings.sAjaxSource = 'json/items.form?type=<h:handle object="${item.incoming}" get="type" />';
		chooseExistingTable.fnDraw();
		chooseExistingDialog.dialog("open");
		$j('input', '#chooseExistingTable_filter').focus();
	}
</script>

<h3>
	<spring:message code="metadatasharing.importPackage" />
</h3>
<p>
	<input type="button" value="Back to Summary"
		onclick="window.location='load.form'" />
</p>

<springform:form commandName="assessItemForm">
		<spring:message code="metadatasharing.assessingItem" />
		${assessItemForm.index + 1} <input
		name="assessItemForm.index" type="hidden"
		value="${assessItemForm.index}" />
	<input id="uuid" name="uuid" type="hidden"
		value="<h:handle object="${item.existing}" get="uuid" />" />
	<p>
		<spring:message code="metadatasharing.type" /> <b><h:handle object="${item.incoming}" get="type" /> </b>
	</p>

	<table width="100%">
		<tr>
			<td width="50%" style="vertical-align: top">
				<fieldset>
					<legend> <spring:message code="metadatasharing.incomingItem" /> </legend>
					<table>
						<c:if test="${not empty item.existing}">
							<c:if test="${item.existingClassSimpleName != item.incomingClassSimpleName}">
								<tr>
									<td class="to_right"> <spring:message code="metadatasharing.type" /> </td>
									<td><span id="incomingType">${item.incomingClassSimpleName}</span></td>
								</tr>
							</c:if>
						</c:if>
						<tr>
							<td class="to_right"> <spring:message code="metadatasharing.name" /> </td>
							<td><b><span id="incomingName"><h:handle
											object="${item.incoming}" get="name" /></span></b></td>
						</tr>
						<tr>
							<td class="to_right"> <spring:message code="metadatasharing.description" /> </td>
							<td><span id="incomingDescription"><h:handle
										object="${item.incoming}" get="description" /></span></td>
						</tr>
						<tr>
							<td class="to_right"> <spring:message code="metadatasharing.UUID" /> </td>
							<td><span id="incomingUUID"><h:handle
										object="${item.incoming}" get="uuid" /></span></td>
						</tr>
						<tr>
							<td class="to_right"> <spring:message code="metadatasharing.dateModified" /> </td>
							<td><span id="incomingDateChanged"><h:handle
										object="${item.incoming}" get="dateChanged" /></span></td>
						</tr>
						<c:if test="${type == 'Concept'}">
							<tr>
								<td class="to_right"> <spring:message code="metadatasharing.datatype" /></td>
								<td><span id="incomingDatatype">${item.incoming.datatype.name}</span></td>
							</tr>
							<tr>
								<td class="to_right"> <spring:message code="metadatasharing.class" /></td>
								<td><span id="incomingClass">${item.incoming.conceptClass.name}</span></td>
							</tr>
						</c:if>
					</table>
				</fieldset>
			</td>
			<td width="50%" style="vertical-align: top">
				<fieldset>
					<legend> <spring:message code="metadatasharing.existingItem" /> </legend>
					<div id="existingItem">
						<table>
							<c:if test="${not empty item.existing}">
								<c:if test="${item.existingClassSimpleName != item.incomingClassSimpleName}">
									<tr>
										<td class="to_right"> <spring:message code="metadatasharing.type" /> </td>
										<td><span id="existingType">${item.existingClassSimpleName}</span></td>
									</tr>
								</c:if>
							</c:if>
							<tr>
								<td class="to_right"> <spring:message code="metadatasharing.name" /> </td>
								<td><b><span id="existingName"><h:handle
												object="${item.existing}" get="name" /></span></b></td>
							</tr>
							<tr>
								<td class="to_right"> <spring:message code="metadatasharing.description" /> </td>
								<td><span id="existingDescription"><h:handle
											object="${item.existing}" get="description" /></span></td>
							</tr>
							<tr>
								<td class="to_right"> <spring:message code="metadatasharing.UUID" /> </td>
								<td><span id="existingUUID"><h:handle
											object="${item.existing}" get="uuid" /></span></td>
							</tr>
							<tr>
								<td class="to_right"> <spring:message code="metadatasharing.dateModified" /> </td>
								<td><span id="existingDateChanged"><h:handle
											object="${item.existing}" get="dateChanged" /></span></td>
							</tr>
							<c:if test="${type == 'Concept'}">
								<tr>
									<td class="to_right"> <spring:message code="metadatasharing.datatype" /> </td>
									<td><span id="existingDatatype">${item.existing.datatype.name}</span></td>
								</tr>
								<tr>
									<td class="to_right"> <spring:message code="metadatasharing.class" /> </td>
									<td><span id="existingClass">${item.existing.conceptClass.name}</span></td>
								</tr>
							</c:if>
						</table>
					</div>
					<c:if test="${item.existingReplaceable}">
						<p style="text-align: center">
							<input type="button" id="chooseExistingButton"
								value="Choose Existing" />
						</p>
					</c:if>
				</fieldset>
			</td>
		</tr>
		<tr>
			<td><springform:radiobutton path="importType" value="CREATE"
					id="createButton" disabled="${!empty incomingInvalid}" />
					<spring:message code="metadatasharing.createNew" />
				<div style="padding-left: 4em; font-size: 0.8em;">
					<c:choose>
						<c:when test="${empty incomingInvalid}">
						<spring:message code="metadatasharing.createNewItem" />
				</c:when>
						<c:otherwise>
							<spring:message code="metadatasharing.cannotCreateItem" />
							<span class="error">${incomingInvalid}</span>
						</c:otherwise>
					</c:choose>
				</div> <springform:radiobutton path="importType" value="OMIT"
					id="omitButton" />
				<spring:message code="metadatasharing.skipIfPossible" />
				<div style="padding-left: 4em; font-size: 0.8em;">
					<spring:message code="metadatasharing.skipImportingIfWorks" /> </div></td>
			<td><springform:radiobutton path="importType"
					value="PREFER_MINE" id="preferMineButton" /> <spring:message code="metadatasharing.mergePreferMine" />
				<div style="padding-left: 4em; font-size: 0.8em;"> <spring:message code="metadatasharing.mergeNoOverwrite" /> </div> <springform:radiobutton
					path="importType" value="PREFER_THEIRS" id="preferTheirsButton" />
				<spring:message code="metadatasharing.mergePreferTheirs" />
				<div style="padding-left: 4em; font-size: 0.8em;"> <spring:message code="mergeWithOverwrite" /> </div></td>
		</tr>
	</table>




	<p>
		<input id="nextButton" type="submit"
			value="<spring:message code="metadatasharing.next" />" /> <br />
	</p>
</springform:form>

<div id="chooseExistingDialog">
	<table id="chooseExistingTable" style="width: 100%">
		<thead>
			<tr>
				<th><spring:message code="metadatasharing.name" /></th>
				<th><spring:message code="metadatasharing.description" /></th>
				<th><spring:message code="metadatasharing.uuid" /></th>
				<th><spring:message code="metadatasharing.id" /></th>
				<th><spring:message code="metadatasharing.choose" /></th>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>