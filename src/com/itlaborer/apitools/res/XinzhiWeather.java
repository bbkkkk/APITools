package com.itlaborer.apitools.res;

import java.util.ArrayList;

import com.itlaborer.apitools.model.ApiDoc;
import com.itlaborer.apitools.model.ApiItem;
import com.itlaborer.apitools.model.ApiMod;
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

		// 第一个接口
		ApiItem nowWeather = new ApiItem();
		nowWeather.setUuid(ApiUtils.getUUID());
		nowWeather.setPath("now.json");
		nowWeather.setName("天气实况");
		nowWeather.setDescription("可以通过此接口获取到全国的城市的当日天气信息");
		nowWeather.setMethod("GET");
		ArrayList<ApiPar> nowWeatherPars = new ArrayList<ApiPar>();
		nowWeatherPars.add(new ApiPar("key", "API密钥", "lo5ujaa0pv5jtrkv"));
		nowWeatherPars.add(new ApiPar("location", "城市中文名 例如：location=北京", "北京"));
		nowWeather.setParameters(nowWeatherPars);
		// 第二个接口
		ApiItem daily = new ApiItem();
		daily.setUuid(ApiUtils.getUUID());
		daily.setPath("daily.json");
		daily.setName("逐日预报和历史");
		daily.setDescription("可以通过此接口获取到全国的城市的近七日天气信息");
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
		ApiMod xinzhi = new ApiMod();
		xinzhi.setName("心知天气");
		xinzhi.setDescription("心知天气V3-一个天气接口");
		xinzhi.setItem(apiItems);

		// 加入分组列表
		ArrayList<ApiMod> apiList = new ArrayList<ApiMod>();
		apiList.add(xinzhi);

		// 模板里的接口列表是取自心知天气的接口，感谢感谢
		this.apidoc = new ApiDoc();
		// 加入接口文档
		apidoc.setItem(apiList);
		apidoc.setName("心知天气");
		apidoc.setVersion("V3");
		apidoc.setDecodeversion(1.1);
		apidoc.setServerlist("https://api.thinkpage.cn/v3/weather/|https://api.thinkpage.cn/v3/weather/");
	}

	public ApiDoc getApidoc() {
		return apidoc;
	}
}
