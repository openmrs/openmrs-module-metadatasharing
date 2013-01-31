<%@ include file="template/localInclude.jsp"%>
<%@ include file="template/localHeader.jsp"%>
<%@ include file="template/jqueryPage.jsp"%>
<%@ taglib prefix="springform" uri="http://www.springframework.org/tags/form"%>
<openmrs:require allPrivileges="Share Metadata, Manage Global Properties" otherwise="/login.htm" redirect="/module/configure.form" />
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/metadatasharing/css/metadatasharing.css"/>
<script type="text/javascript">
	var $j = jQuery.noConflict();
	$j(document).ready(function() { 		
		if((!$j("#urlPrefix").val()) && ($j(".urlPrefixErrors").length == 0)) {
			$j("#urlPrefix").parents("p").prepend("<span class='warning'><spring:message code="metadatasharing.configure.urlPrefix.enter" /></span><br/><br/>");
		}
		
		$j("#checkAutomatically").click(toggleNotifications);
		toggleNotifications();
	});
	
	function toggleNotifications() {
		if($j("#checkAutomatically").is(":checked")) {
			$j("#daysTextBox").removeAttr("disabled").removeClass("disabled");
		} else{
			$j("#daysTextBox").attr("disabled", "disabled").addClass("diabled");
		}
	}
	
	function toggleSourceInfo(ele) {
		if($j(ele).is(":checked")) {
			$j("#sourceInfo").show();
		} else{
			$j("#sourceInfo").hide();
		}
	}
</script>
<style>
	.watermark {
		color:#999;
	}	
	#urlPrefix {
		width:500px;
	}
	.urlExample {
		font-style:italic; 
	}
	#daysTextBox {
		width: 30px;
	}
	fieldset {
		margin-bottom: 1em;
	}
	.indented {
		margin-left: 3em;
	}
	.even-configuration-item {
		padding: 0.5em; 0.3em;
	}
	.odd-configuration-item {
		padding: 0.5em; 0.3em;
		background-color: #e0e0e0;
	}
</style>

<h3>
	<spring:message code="metadatasharing.configure" />
</h3>

<springform:form commandName="configureForm">
	<fieldset>
		<legend><spring:message code="metadatasharing.configure.importing"/></legend>

		<div class="even-configuration-item">
			<spring:message code="metadatasharing.preferredConceptSources.description" />
			<div class="indented">
				<b><spring:message code="metadatasharing.preferredConceptSources" /></b>
				<c:forEach var="source" items="${conceptSources}" varStatus="varStatus">
					<br /><input type="checkbox" name="preferredSourceIds" value="${source.conceptSourceId}" <c:if test="${sourceIdPreferredMap[source.conceptSourceId] == true}">checked="checked"</c:if> /> ${source.name}
				</c:forEach>
			</div>
		</div>
	</fieldset>

	<fieldset>
		<legend><spring:message code="metadatasharing.configure.subscribing"/></legend>
		
		<div class="even-configuration-item">
			<springform:checkbox path="notifyAutomatically" id="checkAutomatically" />
			<label for="checkAutomatically"><b><spring:message code="metadatasharing.notifyMe" /></b></label>
			<br />
			<div class="indented">
				<spring:message code="metadatasharing.checkUpdatesEvery" />
				<springform:input path="intervalDays" id="daysTextBox" /> <spring:message code="metadatasharing.days" />
				<springform:errors path="intervalDays" cssClass="error"/>
			</div>
		</div>  
	</fieldset>
	
	<fieldset>
		<legend><spring:message code="metadatasharing.configure.publishing"/></legend>
		
		<div class="even-configuration-item">
			<label for="urlPrefix">
				<b><spring:message code="metadatasharing.urlPrefix.description" /></b>
				<spring:message code="metadatasharing.urlPrefix.description2" htmlEscape="false" />
			</label>
			<br/>
			<springform:input path="urlPrefix" id="urlPrefix" />
			<springform:errors path="urlPrefix" cssClass="error urlPrefixErrors"/>
		</div>
		
		<div class="odd-configuration-item">
			<springform:checkbox path="enableOnTheFlyPackages" id="enableOnTheFlyPackages" />
			<label for="enableOnTheFlyPackages">
				<b><spring:message code="metadatasharing.enableOnTheFlyPackages" /></b>
			</label> 
			
			<springform:errors path="enableOnTheFlyPackages" cssClass="error"/>
			<br />
			<div class="indented">
				<spring:message code="metadatasharing.enableOnTheFlyPackages.description" />
				<br/>
				<b><spring:message code="metadatasharing.webservicesKey" /></b> 
				<springform:input path="webservicesKey" id="webservicesKey" />
				<springform:errors path="webservicesKey" cssClass="error"/>
				<br />
				<i><spring:message code="metadatasharing.webservicesKey.description" /></i>
			</div>
		</div>		 
	</fieldset>
	
	<p>
		<input type="submit" value="<spring:message code="general.save"/>" />
	</p>
</springform:form>
<%@ include file="/WEB-INF/template/footer.jsp"%>
