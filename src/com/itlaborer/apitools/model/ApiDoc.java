package com.itlaborer.apitools.model;

import java.util.ArrayList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.itlaborer.apitools.utils.JsonFormatUtils;

/**
 * API文档类
 * 
 * @author liudewei[793554262@qq.com]
 * @version 1.1
 * @since 1.0
 */

public class ApiDoc {

	@JSONField(ordinal = 1)
	private String doc_name;
	@JSONField(ordinal = 2)
	private Double decode_version;
	@JSONField(ordinal = 3)
	private String api_version;
	@JSONField(ordinal = 4)
	private ArrayList<ApiList> apilist;

	public ApiDoc() {

	}

	public String getDoc_name() {
		return doc_name;
	}

	public void setDoc_name(String doc_name) {
		this.doc_name = doc_name;
	}

	public Double getDecode_version() {
		return decode_version;
	}

	public void setDecode_version(Double decode_version) {
		this.decode_version = decode_version;
	}

	public String getApi_version() {
		return api_version;
	}

	public void setApi_version(String api_version) {
		this.api_version = api_version;
	}

	public ArrayList<ApiList> getApilist() {
		return apilist;
	}

	public void setApilist(ArrayList<ApiList> apilist) {
		this.apilist = apilist;
	}

	public String toString() {
		return JsonFormatUtils.Format(JSON.toJSONString(this));
	}
}
