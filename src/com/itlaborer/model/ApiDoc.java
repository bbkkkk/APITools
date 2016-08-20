package com.itlaborer.model;

import java.util.ArrayList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.itlaborer.utils.JsonFormatUtils;

/**
 * 
 * API文档类
 * 
 * @author liu
 *
 */
public class ApiDoc {

	@JSONField(ordinal = 1)
	private String decode_version;
	@JSONField(ordinal = 2)
	private String api_version;
	@JSONField(ordinal = 3)
	private ArrayList<ApiList> apilist;

	public ApiDoc() {

	}

	public String getDecode_version() {
		return decode_version;
	}

	public void setDecode_version(String decode_version) {
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
