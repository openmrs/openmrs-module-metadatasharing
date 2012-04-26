<%@ include file="../template/localInclude.jsp"%>
<openmrs:require privilege="Share Metadata" otherwise="/login.htm"
	redirect="/module/metadatasharing/subscription/manageSubscriptions.form" />
<%@ include file="../template/localHeader.jsp"%>
<h3>
	<spring:message code="metadatasharing.importPackage" />
</h3>
<p>
	<spring:message code="metadatasharing.addInvalidSubscription.line1" />
	<br/>
	<spring:message code="metadatasharing.addInvalidSubscription.line2" />
 </p>
<p>
	<input type="button" value="<spring:message code="metadatasharing.no"/>" onclick="window.location='../import/upload.form'" />
	<input type="button" value="<spring:message code="metadatasharing.yes" />" onclick="window.location='invalidSubscription.form?add=true'" />
</p>
<%@ include file="/WEB-INF/template/footer.jsp"%>