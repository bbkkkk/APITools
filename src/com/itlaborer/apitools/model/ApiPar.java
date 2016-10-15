package com.itlaborer.apitools.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 
 * @author liudewei[793554262@qq.com]
 * @see API参数类
 * @version 1.0
 * @since 1.0
 * 
 */

public class ApiPar {
	@JSONField(ordinal = 1)
	private String name;
	@JSONField(ordinal = 2)
	private String tip;
	@JSONField(ordinal = 3)
	private String value;

	public ApiPar() {

	}

	public ApiPar(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public ApiPar(String name, String tip, String value) {
		this.name = name;
		this.tip = tip;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

}
