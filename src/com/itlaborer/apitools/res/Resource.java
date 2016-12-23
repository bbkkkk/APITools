package com.itlaborer.apitools.res;

/**
 * 程序中使用到的一些静态字符串资源，放在此处方便统一管理
 * @author liudewei
 * @version 1.0
 * @since 1.0
 */

public class Resource {

	public static String VERSION = "V1.8.2";
	public static String AUTHOR = "作者:恒生电子/刘德位(793554262@qq.com)";
	public static String BLOG = "http://www.itlaborer.com";
	public static String CONFIG = "#主配置文件\r\n#请按照参数名进行相关配置\r\n#服务器地址,如果有多台服务器，请用,（英文逗号）分开"
			+ "\r\napiaddress=https://api.thinkpage.cn/v3/weather/\r\n#自动装载的API列表文件\r\napilist=api-xinzhiweather.json\r\n#自动装载的错误码列表文件-仅支持恒生FUNDAPI\r\nreturncodefile"
			+ "=xxx.properties\r\n#历史记录条数\r\nhsitorysum=30";
	public static String EXPLAIN = "这是一个适合HTTP接口的测试工具，可以快速的填充参数，拉取接口返回的结果,如果你"
			+ "不想每次测试接口的时候都手动输入大量的参数,可以预先编辑好接口和参数列表,这样工具启动的时候" + "可以自动加载预先定义好的接口信息,这一切都是如此的简单,你只需要掌握"
			+ "简单的JSON格式书写规则即可";
	public static String LOG4J = "log4j.rootLogger=INFO,CONSOLE,FILE\r\n\r\nlog4j.logger.CONSOLE=INFO\r\nlog4j.logger.FILE=INFO\r\n\r\n#打印到Console的日志配置\r\nlog4j.appender.CO"
			+ "NSOLE=org.apache.log4j.ConsoleAppender\r\nlog4j.appender.CONSOLE.layout.ConversionPattern=%d{[yyyy-MM-dd HH:mm:ss]}[%c][%-5p][%m]%n\r\nlog4j.appender.CONSOL"
			+ "E.Target=System.out\r\nlog4j.appender.CONSOLE.Encoding=UTF-8\r\nlog4j.appender.CONSOLE.layout=org.apache.log4j.PatternLayout\r\n\r\n#打印到日志文件的日志配置\r\nlog4"
			+ "j.appender.FILE=org.apache.log4j.DailyRollingFileAppender \r\nlog4j.appender.FILE.file=log/APITools.log\r\nlog4j.appender.FILE.DatePattern='.'yyyy-MM-dd\r\nlog"
			+ "4j.appender.FILE.layout=org.apache.log4j.PatternLayout\r\nlog4j.appender.FILE.layout.ConversionPattern=%d{[yyyy-MM-dd HH:mm:ss]}[%c][%-5p][%m]%n";
	public static String MANUAL = "http://www.itlaborer.com/apitools_manual";
	public static String FEEDBACK = "http://www.itlaborer.com/2016/08/07/apitools_feedback.html";	
	//图片资源串
	public static String IMAGE_ICON="/com/itlaborer/apitools/res/icon.ico";
	public static String IMAGE_CHECKED="/com/itlaborer/apitools/res/checked.png";

}