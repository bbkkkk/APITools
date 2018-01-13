package com.itlaborer.apitools.model;

import java.util.ArrayList;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * API接口类
 * 
 * @author liudewei[793554262@qq.com]
 * @version 1.1
 * @since 1.0
 */

public class ApiItem {

	@JSONField(ordinal = 1)
	private String uuid;
	@JSONField(ordinal = 2)
	private String name;
	@JSONField(ordinal = 3)
	private String description;
	@JSONField(ordinal = 4)
	private String path;
	@JSONField(ordinal = 5)
	private String method;
	@JSONField(ordinal = 6)
	private String contenttype;
	@JSONField(ordinal = 7)
	private ArrayList<ApiPar> parameters;
	@JSONField(ordinal = 8)
	private String rawparameters;

	public ApiItem() {

	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getContenttype() {
		return contenttype;
	}

	public void setContenttype(String contenttype) {
		this.contenttype = contenttype;
	}

	public ArrayList<ApiPar> getParameters() {
		return parameters;
	}

	public void setParameters(ArrayList<ApiPar> parameters) {
		this.parameters = parameters;
	}

	public String getRawparameters() {
		return rawparameters;
	}

	public void setRawparameters(String rawparameters) {
		this.rawparameters = rawparameters;
	}
}
