package com.itlaborer.apitools.model;

import java.util.ArrayList;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * API模块类
 * @author liudewei[793554262@qq.com]
 * @version 1.0
 * @since 1.0
 */

public class ApiMod {

	/*
	 * 模块名字和api列表
	 * 
	 */
	@JSONField(ordinal = 1)
	private String name;
	@JSONField(ordinal = 2)
	private String description;
	@JSONField(ordinal = 3)
	private ArrayList<ApiItem> item;

	public ApiMod() {

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

	public ArrayList<ApiItem> getItem() {
		return item;
	}

	public void setItem(ArrayList<ApiItem> item) {
		this.item = item;
	}

}
