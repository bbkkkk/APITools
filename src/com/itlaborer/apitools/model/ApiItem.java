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
	private String explain;
	@JSONField(ordinal = 4)
	private String address;
	@JSONField(ordinal = 5)
	private String method;
	@JSONField(ordinal = 6)
	private ArrayList<ApiPar> parameters;

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

	public String getExplain() {
		return explain;
	}

	public void setExplain(String explain) {
		this.explain = explain;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public ArrayList<ApiPar> getParameters() {
		return parameters;
	}

	public void setParameters(ArrayList<ApiPar> parameters) {
		this.parameters = parameters;
	}

}
