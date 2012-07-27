package org.openmrs.module.metadatasharing.web.view;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.openmrs.module.metadatasharing.ExportedPackage;
import org.openmrs.module.metadatasharing.MetadataSharingConsts;
import org.openmrs.module.metadatasharing.web.controller.PublishController;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;

/**
 * The view for /ws/rest/.../.../download page
 * 
 * @see PublishController#getPackageContent(org.springframework.ui.Model, HttpServletRequest,
 *      HttpServletResponse)
 */
@Component(MetadataSharingConsts.MODULE_ID + ".DownloadZippedPackageView")
public class DownloadPackageView extends AbstractView {
	
	/**
	 * Writes the zipped package content to the client.
	 * 
	 * @see org.springframework.web.servlet.view.AbstractView#renderMergedOutputModel(java.util.Map,
	 *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void renderMergedOutputModel(@SuppressWarnings("rawtypes")
	Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		ExportedPackage pack = (ExportedPackage) model.get("package");
		response.setContentType("application/zip");
		String filename = pack.getName().replace(" ", "_") + "-" + pack.getVersion();
		response.setHeader("Content-Disposition", "attachment; filename=" + filename + ".zip");
		response.setHeader("Pragma", "No-cache");
		response.setDateHeader("Expires", 0);
		response.setHeader("Cache-Control", "no-cache");
		
		InputStream in = null;
		OutputStream out = null;
		try {
			in = pack.getSerializedPackageStream();
			out = response.getOutputStream();
			IOUtils.copy(in, out);
			in.close();
			out.close();
		}
		finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}
}
