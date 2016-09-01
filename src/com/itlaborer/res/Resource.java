package com.itlaborer.res;

/**
 * 
 * @author liudewei
 *
 */

public class Resource {

	public static String VERSION = "V1.7";
	public static String AUTHOR = "作者:恒生电子/刘德位(793554262@qq.com)";
	public static String BLOG = "http://www.itlaborer.com";
	public static String CONFIG = "#主配置文件\n#请按照参数名进行相关配置\n#服务器地址"
			+ "\napiaddress=https://api.thinkpage.cn/v3/weather/\n#自动装载的API列表文件\napilist=api-xinzhiweather.json\n#自动装载的错误码列表文件-仅支持恒生FUNDAPI\nreturncodefile"
			+ "=xxx.properties\n#历史记录条数\nhsitorysum=30";
	public static String EXPLAIN = "这是一个适合HTTP接口的测试工具，可以快速的填充参数，拉取接口返回的结果,如果你"
			+ "不想每次测试接口的时候都手动输入大量的参数,可以预先编辑好接口和参数列表,这样工具启动的时候" + "可以自动加载预先定义好的接口信息,这一切都是如此的简单,你只需要掌握"
			+ "简单的JSON格式书写规则即可";
	public static String LOG4J = "log4j.rootLogger=INFO,CONSOLE,FILE\n\nlog4j.logger.CONSOLE=INFO\nlog4j.logger.FILE=INFO\n\n#打印到Console的日志配置\nlog4j.appender.CO"
			+ "NSOLE=org.apache.log4j.ConsoleAppender\nlog4j.appender.CONSOLE.layout.ConversionPattern=%d{[yyyy-MM-dd HH:mm:ss]}[%c][%-5p][%m]%n\nlog4j.appender.CONSOL"
			+ "E.Target=System.out\nlog4j.appender.CONSOLE.Encoding=UTF-8\nlog4j.appender.CONSOLE.layout=org.apache.log4j.PatternLayout\n\n#打印到日志文件的日志配置\nlog4"
			+ "j.appender.FILE=org.apache.log4j.DailyRollingFileAppender \nlog4j.appender.FILE.file=log/APITools.log\nlog4j.appender.FILE.DatePattern='.'yyyy-MM-dd\nlog"
			+ "4j.appender.FILE.layout=org.apache.log4j.PatternLayout\nlog4j.appender.FILE.layout.ConversionPattern=%d{[yyyy-MM-dd HH:mm:ss]}[%c][%-5p][%m]%n";

	public static String getVersion() {
		return (VERSION);
	}

	public static String getAUTHOR() {
		return AUTHOR;
	}

	public static String getEXPLAIN() {
		return EXPLAIN;
	}

	public static String getBLOG() {
		return BLOG;
	}

	public static String getCONFIG() {
		return CONFIG;
	}

	public static String getLOG4J() {
		return LOG4J;
	}
}
