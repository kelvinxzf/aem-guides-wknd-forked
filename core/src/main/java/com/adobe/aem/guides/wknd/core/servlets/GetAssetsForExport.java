package com.adobe.aem.guides.wknd.core.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.guides.wknd.core.models.AssetManifest;
import com.adobe.aem.guides.wknd.core.services.AssetExportManifestService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component(service = { Servlet.class })
@SlingServletResourceTypes(resourceTypes = "sling:Folder", methods = HttpConstants.METHOD_GET, selectors = "exportassets", extensions = "json")
@ServiceDescription("Get assets for export servlet")
public class GetAssetsForExport extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(GetAssetsForExport.class);

	@Reference
	private transient AssetExportManifestService assetExportManifestService;

	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {
		LOG.info("inside GetAssetsForExport servlet -> doGet");
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		final List<AssetManifest> assetManifests = assetExportManifestService.getAssetExportManifest(request);
		if (assetManifests != null && !assetManifests.isEmpty()) {
			final GsonBuilder builder = new GsonBuilder();
			builder.excludeFieldsWithoutExposeAnnotation();
			final Gson gson = builder.create();
			final String jsonStr = gson.toJson(assetManifests);
			response.getWriter().write(jsonStr);
		} else {
			response.setStatus(SlingHttpServletResponse.SC_NO_CONTENT);
		}
	}
}
