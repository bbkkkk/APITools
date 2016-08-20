package com.itlaborer.model;

import java.util.ArrayList;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 
 * API接口类
 * 
 * @author liu
 *
 */
public class ApiItem {

	@JSONField(ordinal = 1)
	private String name;
	@JSONField(ordinal = 2)
	private String address;
	@JSONField(ordinal = 3)
	private ArrayList<ApiPar> parameters;

	public ApiItem() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public ArrayList<ApiPar> getParameters() {
		return parameters;
	}

	public void setParameters(ArrayList<ApiPar> parameters) {
		this.parameters = parameters;
	}

}
