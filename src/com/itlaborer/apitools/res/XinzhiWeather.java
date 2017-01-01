package com.itlaborer.apitools.res;

import java.util.ArrayList;

import com.itlaborer.apitools.model.ApiDoc;
import com.itlaborer.apitools.model.ApiItem;
import com.itlaborer.apitools.model.ApiList;
import com.itlaborer.apitools.model.ApiPar;
import com.itlaborer.apitools.utils.ApiUtils;

/**
 * 心知天气API，作为示例接口文档
 * 
 * @author liudewei
 * @version 1.1
 * @since 1.6
 */

public class XinzhiWeather {

	private ApiDoc apidoc;

	public XinzhiWeather() {
		// 模板里的接口列表是取自心知天气的接口，感谢感谢
		this.apidoc = new ApiDoc();
		// 第一个接口
		ApiItem nowWeather = new ApiItem();
		nowWeather.setUuid(ApiUtils.getUUID());
		nowWeather.setAddress("now.json");
		nowWeather.setName("天气实况");
		nowWeather.setExplain("可以通过此接口获取到全国的城市的当日天气信息");
		nowWeather.setMethod("GET");
		ArrayList<ApiPar> nowWeatherPars = new ArrayList<ApiPar>();
		nowWeatherPars.add(new ApiPar("key", "API密钥", "lo5ujaa0pv5jtrkv"));
		nowWeatherPars.add(new ApiPar("location", "城市中文名 例如：location=北京", "北京"));
		nowWeather.setParameters(nowWeatherPars);
		// 第二个接口
		ApiItem daily = new ApiItem();
		daily.setUuid(ApiUtils.getUUID());
		daily.setAddress("daily.json");
		daily.setName("逐日预报和历史");
		daily.setExplain("可以通过此接口获取到全国的城市的近七日天气信息");
		daily.setMethod("GET");
		ArrayList<ApiPar> hourly_historyPars = new ArrayList<ApiPar>();
		hourly_historyPars.add(new ApiPar("key", "API密钥", "lo5ujaa0pv5jtrkv"));
		hourly_historyPars.add(new ApiPar("location", "城市中文名 例如：location=北京", "上海"));
		daily.setParameters(hourly_historyPars);

		// 加入接口列表
		ArrayList<ApiItem> apiItems = new ArrayList<ApiItem>();
		apiItems.add(nowWeather);
		apiItems.add(daily);

		// 心知分组
		ApiList xinzhi = new ApiList();
		xinzhi.setName("心知天气");
		xinzhi.setApi(apiItems);

		// 加入分组列表
		ArrayList<ApiList> apiList = new ArrayList<ApiList>();
		apiList.add(xinzhi);

		// 加入接口文档
		apidoc.setApilist(apiList);
		apidoc.setDoc_name("心知天气");
		apidoc.setApi_version("V3");
		apidoc.setDecode_version(1.1);
	}

	public ApiDoc getApidoc() {
		return apidoc;
	}
}
