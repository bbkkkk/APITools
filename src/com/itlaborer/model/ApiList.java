package com.itlaborer.model;

import java.util.ArrayList;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 
 * @author liudewei[793554262@qq.com]
 * @see API列表类
 * @version 1.0
 * @since 1.0
 * 
 */

public class ApiList {

	/*
	 * 分类名字和api列表
	 * 
	 */
	@JSONField(ordinal = 1)
	private String name;
	@JSONField(ordinal = 2)
	private ArrayList<ApiItem> api;

	public ApiList() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<ApiItem> getApi() {
		return api;
	}

	public void setApi(ArrayList<ApiItem> api) {
		this.api = api;
	}

}
