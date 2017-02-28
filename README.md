# APITools

### 程序说明
* 一个HTTP请求工具，类似于Postman
* 可以记录接口信息，方便选择接口 支持常见的http请求方法
* 拥有中文提示,方便填写接口参数
* 规范的Json格式文档定义，方便导出接口信息到其他格式
* 可拓展其他功能，使用Java编写，编写简单

### 构建说明
* 1:执行git clone https://git.oschina.net/zzunet/APITools.git
* 2:根据系统类别修改pom.xml(程序使用SWT开发，需要根据系统和jvm版本修改pom.xml)
* 3:执行build.bat构建程序包，构建完成后程序包将生成在target目录，譬如APITools-1.8-jar-with-dependencies.jar
* 4:切换到target目录，执行java -Dfile.encoding=UTF-8 -jar APITools-1.8-jar-with-dependencies.jar启动程序

### 常见问题说明
* 1：控制台里打印日志乱码，需要修改config/log4j.properties,将log4j.appender.CONSOLE.Encoding值改成GB2312
* 2：启动程序失败，此问题可能是因为使用的SWT版本和jdk版本不一致导致的，比如32位jdk必须使用32位的SWTjar包，请再pom.xml里修改

### 联系作者
* QQ:793554262
* Email:793554262@qq.com
* Blog:www.itlaborer.com
* 微信:liudeweichina