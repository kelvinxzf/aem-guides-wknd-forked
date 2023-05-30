package com.adobe.aem.guides.wknd.core.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Session;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.guides.wknd.core.models.AssetManifest;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;

@Component(service = AssetExportManifestService.class, immediate = true)
public class AssetExportManifestService {
	private static final Logger LOG = LoggerFactory.getLogger(AssetExportManifestService.class);

	@Reference
	private QueryBuilder queryBuilder;

	@Activate
	protected void activate() {
		LOG.info("Inside AssetExportManifestService activate method...");
	}

	public List<AssetManifest> getAssetExportManifest(final SlingHttpServletRequest request) {
		final Resource currentResource = request.getResource();
		final ResourceResolver resourceResolver = request.getResourceResolver();

		String folderPath = currentResource != null ? currentResource.getPath() : "/content/dam";
		String limit = request.getParameter("limit") != null ? request.getParameter("limit") : "-1";
		LOG.info("got folderPath={}", folderPath);

		final Map<String, String> map = new HashMap<>();
		map.put("type", "dam:Asset");
		map.put("path", folderPath);
		// only getting processed assets. this also excludes content fragment
		map.put("1_property", "jcr:content/dam:assetState");
		map.put("1_property.value", "processed");
		if (request.getParameter("destination") != null) {
			map.put("2_property", "jcr:content/metadata/exportDestination");
			map.put("2_property.value", request.getParameter("destination"));
		}
		map.put("p.limit", limit);

		if (queryBuilder == null) {
			LOG.error("QueryBuilder object is null. No way to do the search");
			return Collections.emptyList();
		}
		final Session session = resourceResolver.adaptTo(Session.class);
		final Query query = queryBuilder.createQuery(PredicateGroup.create(map), session);
		SearchResult result = query.getResult();

		if (query.getResult() != null) {
			final List<AssetManifest> assetManifests = new ArrayList<>();
			final Iterator<Resource> resources = result.getResources();
			while (resources != null && resources.hasNext()) {
				final Resource resource = resources.next();
				LOG.debug("resource path in result: {}", resource.getPath());

				ValueMap properties = resource.getValueMap();
				AssetManifest am = new AssetManifest();
				am.setUuid(properties.get("jcr:uuid", String.class));
				am.setAssetName(properties.get("jcr:content/cq:name", String.class));
				am.setAssetPath(resource.getPath());
				am.setAssetSize(properties.get("jcr:content/metadata/dam:size", Long.class));
				am.setExportDestination(properties.get("jcr:content/metadata/exportDestination", String.class));
				assetManifests.add(am);
			}
			return assetManifests;
		}
		return Collections.emptyList();
	}

}
