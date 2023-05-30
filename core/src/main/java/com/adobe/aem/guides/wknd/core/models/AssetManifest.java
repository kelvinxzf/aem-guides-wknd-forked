package com.adobe.aem.guides.wknd.core.models;

import com.google.gson.annotations.Expose;

public class AssetManifest {

	@Expose
	private String uuid;
	@Expose
	private String assetName;
	@Expose
	private String assetPath;
	@Expose
	private Long assetSize;
	@Expose
	private String exportDestination;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getAssetName() {
		return assetName;
	}

	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}

	public String getAssetPath() {
		return assetPath;
	}

	public void setAssetPath(String assetPath) {
		this.assetPath = assetPath;
	}

	public long getAssetSize() {
		return assetSize;
	}

	public void setAssetSize(Long assetSize) {
		this.assetSize = assetSize;
	}

	public String getExportDestination() {
		return exportDestination;
	}

	public void setExportDestination(String exportDestination) {
		this.exportDestination = exportDestination;
	}

}
