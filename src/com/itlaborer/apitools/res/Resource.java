package com.itlaborer.apitools.res;

import com.itlaborer.apitools.utils.PubUtils;

/**
 * 程序中使用到的一些静态字符串资源，放在此处方便统一管理
 * 
 * @author liudewei
 * @version 1.0
 * @since 1.0
 */

public class Resource {

	public static String APIVERSION = "V1.9.0";
	public static String AUTHOR = "作者:刘德位(793554262@qq.com)";
	public static String BLOG = "http://www.itlaborer.com";
	public static String CONFIG = PubUtils.base64DecodeString(
			"I+S4u+mFjee9ruaWh+S7tgoj6K+35oyJ54Wn5Y+C5pWw5ZCN6L+b6KGM55u45YWz6YWN572uCiPoh6rliqjoo4Xovb3nmoRBUEnliJfooajmlofku7Ys5aaC5p6c6ZyA6KaB5Yqg6L296L+H5Liq77yM6K+35LulfOWIhuW8gCzkvovlpoJhYWEuanNvbnxiYmIuanNvbgphcGlsaXN0PWFwaS14aW56aGl3ZWF0aGVyLmpzb24=");
	public static String APIEXPLAIN = "这是一个适合HTTP服务的请求工具，可以快速的填充参数，拉取接口返回的结果,如果你"
			+ "不想每次测试接口的时候都手动输入大量的参数,可以预先编辑好接口和参数列表,这样工具启动的时候" + "可以自动加载预先定义好的接口信息,这一切都是如此的简单,你只需要掌握"
			+ "简单的JSON格式书写规则即可";
	public static String LOG4J = "log4j.rootLogger=INFO,CONSOLE,FILE\r\n\r\nlog4j.logger.CONSOLE=INFO\r\nlog4j.logger.FILE=INFO\r\n\r\n#打印到Console的日志配置\r\nlog4j.appender.CO"
			+ "NSOLE=org.apache.log4j.ConsoleAppender\r\nlog4j.appender.CONSOLE.layout.ConversionPattern=%d{[yyyy-MM-dd HH:mm:ss]}[%c][%-5p][%m]%n\r\nlog4j.appender.CONSOL"
			+ "E.Target=System.out\r\nlog4j.appender.CONSOLE.Encoding=UTF-8\r\nlog4j.appender.CONSOLE.layout=org.apache.log4j.PatternLayout\r\n\r\n#打印到日志文件的日志配置\r\nlog4"
			+ "j.appender.FILE=org.apache.log4j.DailyRollingFileAppender \r\nlog4j.appender.FILE.file=log/APITools.log\r\nlog4j.appender.FILE.DatePattern='.'yyyy-MM-dd\r\nlog"
			+ "4j.appender.FILE.layout=org.apache.log4j.PatternLayout\r\nlog4j.appender.FILE.layout.ConversionPattern=%d{[yyyy-MM-dd HH:mm:ss]}[%c][%-5p][%m]%n";
	public static String MANUAL = "http://www.itlaborer.com/apitools_manual";
	public static String FEEDBACK = "http://www.itlaborer.com/2016/08/07/apitools_feedback.html";
	// 图片资源串
	public static String IMAGE_ICON = "/com/itlaborer/apitools/res/icon.ico";
	public static String IMAGE_CHECKED = "/com/itlaborer/apitools/res/checked.png";

}