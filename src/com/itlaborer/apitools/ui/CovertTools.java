package com.itlaborer.apitools.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;
import com.itlaborer.apitools.model.ApiDoc;
import com.itlaborer.apitools.model.ApiItem;
import com.itlaborer.apitools.model.ApiMod;
import com.itlaborer.apitools.model.ApiPar;
import com.itlaborer.apitools.res.Resource;
import com.itlaborer.apitools.swt.SWTResourceManager;
import com.itlaborer.apitools.utils.ApiUtils;
import com.itlaborer.apitools.utils.JsonFormatUtils;

/**
 * API文档转换工具界面
 * 
 * @author liudewei[793554262@qq.com]
 * @version 1.0
 * @since 1.0
 */

public class CovertTools extends Dialog {

	private static Logger logger = Logger.getLogger(CovertTools.class.getName());
	protected Object result;
	protected Shell covertToolsShell;
	private Text filePath;
	private File path;
	private Text setNameText;
	private Button saveButton;
	private Text apiDocVersion;
	private String apiDocJson;
	private Label apiDocVersionl;
	private Combo versionSelect;
	private int versionSelectint;
	private Text statusText;

	public CovertTools(Shell parent, int style) {
		super(parent, style);
		this.setText("API文档转换工具");
		this.path = null;
		this.apiDocJson = "";
		versionSelectint = 4;
	}

	public Object open() {
		createContents();
		covertToolsShell.open();
		covertToolsShell.layout();
		Display display = getParent().getDisplay();

		while (!covertToolsShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	private void createContents() {
		covertToolsShell = new Shell(getParent(), getStyle());
		covertToolsShell.setImage(SWTResourceManager.getImage(CovertTools.class, Resource.IMAGE_ICON));
		covertToolsShell.setSize(400, 161);
		covertToolsShell.setText(getText());
		ApiUtils.SetCenterinParent(getParent(), covertToolsShell);

		filePath = new Text(covertToolsShell, SWT.BORDER);
		filePath.setBounds(3, 10, 237, 25);

		Button fileSelect = new Button(covertToolsShell, SWT.NONE);
		fileSelect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(covertToolsShell, SWT.OPEN);
				fd.setFilterExtensions(new String[] { "*.html;*.chm", "*.*" });
				fd.setFilterNames(new String[] { "HTML(*.html)/CHM(*.chm)", "All Files(*.*)" });
				String file = fd.open();
				if (StringUtils.isNotEmpty(file)) {
					path = new File(file);
					filePath.setText(path.getPath());
				}
			}
		});
		fileSelect.setBounds(319, 9, 73, 27);
		fileSelect.setText("选择文件");

		final Button beginCovert = new Button(covertToolsShell, SWT.NONE);
		beginCovert.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (null == path) {
					logger.info("请选择API文档");
					statusText.setText("请选择API文档");
					return;
				} else if (StringUtils.isEmpty(apiDocVersion.getText())) {
					logger.info("请填写API文档版本");
					statusText.setText("请填写API文档版本");
					return;
				}

				logger.info("开始解析文件" + path.getPath());
				// 转换前准备
				beginCovert.setText("转换中");
				beginCovert.setEnabled(false);
				saveButton.setEnabled(false);
				statusText.setText("开始解析文件" + path.getPath());
				setNameText.setText("fundapi-" + apiDocVersion.getText() + ".json");
				final String version = apiDocVersion.getText();
				// 新线程中读取
				new Thread() {
					public void run() {
						// 此处为另外一个单独线程，非UI线程,防止UI线程卡死
						apiDocJson = CovertApiDoc(version);
						// 非UI线程访问UI,更新UI内容
						getParent().getDisplay().syncExec(new Thread() {
							public void run() {
								// 这段代码放在UI线程中执行
								if (apiDocJson == null) {
									beginCovert.setEnabled(true);
									beginCovert.setText("开始转换");
									saveButton.setEnabled(true);
									logger.info("转换失败了,请确认你加载的是" + versionSelect.getText() + "的文档");
									statusText.setText("转换失败了,请确认你加载的是" + versionSelect.getText() + "的文档");
								} else {
									beginCovert.setEnabled(true);
									beginCovert.setText("开始转换");
									saveButton.setEnabled(true);
									logger.info("转换结束");
									statusText.setText("转换结束,请保存");
								}
							}
						});
					}
				}.start();
			}
		});
		beginCovert.setText("开始转换");
		beginCovert.setBounds(249, 40, 143, 27);

		setNameText = new Text(covertToolsShell, SWT.BORDER);
		setNameText.setBounds(91, 73, 149, 25);

		Label setName = new Label(covertToolsShell, SWT.NONE);
		setName.setFont(SWTResourceManager.getFont("微软雅黑", 13, SWT.NORMAL));
		setName.setBounds(3, 72, 90, 25);
		setName.setText("保存文件名");
		saveButton = new Button(covertToolsShell, SWT.NONE);
		saveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (setNameText.getText() == "") {
					statusText.setText("请填写要保存的文件名");
					return;
				}
				if (apiDocJson.equals("")) {
					logger.info("没有什么可以保存，请先进行转换");
					statusText.setText("没有什么可以保存，请先进行转换");
					return;
				}
				if (ApiUtils.SaveToFile(new File("./config/" + setNameText.getText()), apiDocJson)) {
					statusText.setText("保存完毕，请关闭工具后在config.properties配置读取新的接口文档");
				} else {
					statusText.setText("保存失败，请尝试重新保存");
				}
			}
		});
		saveButton.setBounds(249, 73, 143, 27);
		saveButton.setText("保存文件");

		apiDocVersion = new Text(covertToolsShell, SWT.BORDER);
		apiDocVersion.setBounds(88, 41, 152, 25);

		apiDocVersionl = new Label(covertToolsShell, SWT.NONE);
		apiDocVersionl.setText("手 册 版 本");
		apiDocVersionl.setFont(SWTResourceManager.getFont("微软雅黑", 13, SWT.NORMAL));
		apiDocVersionl.setBounds(3, 41, 90, 25);

		versionSelect = new Combo(covertToolsShell, SWT.NONE | SWT.READ_ONLY);
		versionSelect.setBounds(248, 10, 65, 25);
		versionSelect.add("API4.0");
		versionSelect.select(0);

		statusText = new Text(covertToolsShell, SWT.BORDER);
		statusText.setBounds(3, 105, 389, 23);
	}

	/**
	 * 转换方法
	 */
	public String CovertApiDoc(String apiDocVersion) {
		// 新建一个ApiDoc对象
		ApiDoc apiDoc = new ApiDoc();
		apiDoc.setApiversion(apiDocVersion);
		apiDoc.setDecodeversion(1.1);
		apiDoc.setServerlist("服务器地址列表请维护在接口文档的serverlist参数里");
		// 判断读取的是什么文件
		// html版本
		if (path.getPath().substring(path.getPath().lastIndexOf(".") + 1).equals("html")
				|| path.getPath().substring(path.getPath().lastIndexOf(".") + 1).equals("HTML")
				|| path.getPath().substring(path.getPath().lastIndexOf(".") + 1).equals("htm")
				|| path.getPath().substring(path.getPath().lastIndexOf(".") + 1).equals("HTM")) {
			logger.info("读取到html格式的手册文件");
			// 用户选择的可能是主页的index.html也有可能是html下的index.html,做个简单的判断
			if (new File(path.getParent() + "/html/index.html").exists()) {
				apiDoc.setItem(CovertApilist(path.getParent() + "/html/index.html"));
			} else {
				apiDoc.setItem(CovertApilist(path.getPath()));
			}
			// chm,注意，chm需要调用hh可执行程序解析，这个东东只有windows下存在
		} else if (path.getPath().substring(path.getPath().lastIndexOf(".") + 1).equals("chm")
				|| path.getPath().substring(path.getPath().lastIndexOf(".") + 1).equals("CHM")) {
			logger.info("读取到chm格式的手册文件,开始尝试调用Windows系统工具解析");
			// chm文件需要解压处理
			// 创建tmp目录
			File tmp = new File("./tmp");
			if (tmp.exists()) {
				ApiUtils.DeleteDir(tmp);
			} else {
				tmp.mkdir();
			}
			// 调用hh.exe
			Runtime rn = Runtime.getRuntime();
			try {
				String command = "hh.exe -decompile " + "./tmp " + path.getPath();
				Process process = rn.exec(command);
				// 这里需要判断是否解压完毕，解压完毕后再接着去解析
				process.waitFor();
				apiDoc.setItem(CovertApilist(tmp.getPath() + "/index.html"));
				ApiUtils.DeleteDir(tmp);
			} catch (Exception e) {
				logger.info("异常", e);
			}
		}
		// 其余格式
		else {
			logger.info("无法接受的格式");
		}
		return (JsonFormatUtils.Format(JSON.toJSONString(apiDoc)));
	}

	/**
	 * API文档分类转换方法--传入参数为接口列表文档的地址
	 * 
	 */
	public ArrayList<ApiMod> CovertApilist(String indexPath) {
		// 接口定义
		logger.info("开始解析文件" + indexPath);
		ArrayList<ApiMod> apiList = new ArrayList<ApiMod>();
		try {
			// 获取文档
			Document document = Jsoup.parse(new File(indexPath), "UTF-8");
			// 找到所有接口分类的定义
			Elements div_ul = document.body().children();
			for (int i = 0; i < div_ul.size(); i++) {
				// 获取分类
				if (div_ul.get(i).tagName().equals("div")) {
					ApiMod thisTypeApiList = new ApiMod();
					ArrayList<String> apiHTMLPath = new ArrayList<String>();
					thisTypeApiList.setName(div_ul.get(i).text());
					// 获取此分类下的接口列表
					for (int r = 0; r < div_ul.get(i + 1).children().size(); r++) {
						String path = new File(indexPath).getParent() + "/"
								+ div_ul.get(i + 1).child(r).child(0).attr("href");
						logger.info(path);
						apiHTMLPath.add(path);
					}
					if (versionSelectint == 4) {
						logger.info("API4.0方案解析");
						thisTypeApiList.setItem(CovertApi4(apiHTMLPath));
					}
					if (thisTypeApiList.getItem().size() > 0) {
						apiList.add(thisTypeApiList);
					}
				}
			}
		} catch (IOException e) {
			logger.error("异常", e);
		}
		return apiList;
	}

	/**
	 * API文档接口列表转换方法 针对API4.0的文档转换
	 */
	public ArrayList<ApiItem> CovertApi4(ArrayList<String> apiHTMLPath) {
		// 创建这个分类下的接口列表信息
		ArrayList<ApiItem> apiItems = new ArrayList<ApiItem>();
		// 循环读取每一个接口的定义文件
		for (int i = 0; i < apiHTMLPath.size(); i++) {
			logger.info("解析到" + apiHTMLPath.get(i));
			// 开始解析这个接口
			ApiItem apiItem = new ApiItem();
			try {
				// 具体的此接口的HTML文档地址生成
				Document document = Jsoup.parse(new File(apiHTMLPath.get(i)), "UTF-8");
				Elements tables = document.getElementsByTag("table");
				// 接口元素列表有6个table
				// UUID
				apiItem.setUuid(ApiUtils.getUUID());
				// 名字
				apiItem.setName(tables.get(1).child(0).child(0).child(1).text());
				// 地址
				try {
					// 需要去除空格和空格里的内容
					apiItem.setPath(tables.get(1).child(0).child(1).child(1).text().substring(23)
							.replaceAll("\\(.*?\\)|\\{.*?}|\\[.*?]|（.*?）", "").trim());
				} catch (StringIndexOutOfBoundsException e) {
					apiItem.setPath("接口地址未知，请手动订正");
					logger.error("异常", e);
				}
				// 开始处理参数，获取所有的信息接口参数在第二个table
				ArrayList<ApiPar> pars = new ArrayList<ApiPar>();
				Elements parsrow = tables.get(2).getElementsByTag("tr");
				if (parsrow != null) {
					// 私有参数的个数,根据rowspan计算得到私有参数个数
					// 判断方法，首先找到标记为私有参数的位置，并从从此处获取rowspan，然后开始遍历rowspan行
					int privateParSartIndex = 0;
					for (int i1 = 0; i1 < parsrow.size(); i1++) {
						if (parsrow.get(i1).child(1).text().equals("私有参数")) {
							privateParSartIndex = i1;
							logger.info("识别到了开始位置：" + privateParSartIndex);
							break;
						}
					}
					// 获取私有参数个数，根据rowspan
					int rowspan = Integer.parseInt(parsrow.get(privateParSartIndex).child(1).attr("rowspan"));
					// 获取默认值在第几列
					int colDefault = 9;
					for (int i1 = 0; i1 < parsrow.get(0).childNodes().size(); i1++) {
						if (parsrow.get(0).child(i1).text().equals("默认值")) {
							colDefault = i1;
							logger.info("识别到了默认值所在的列数：" + colDefault);
							break;
						}
					}
					// 循环获取参数个数
					for (int i1 = privateParSartIndex; i1 < privateParSartIndex + rowspan; i1++) {
						ApiPar par = new ApiPar();
						if (i1 == privateParSartIndex) {
							par.setName(parsrow.get(i1).child(2).text());
							par.setTip(parsrow.get(i1).child(3).text());
							par.setValue(parsrow.get(i1).child(colDefault).text() + "");
						} else {
							par.setName(parsrow.get(i1).child(1).text());
							par.setTip(parsrow.get(i1).child(2).text());
							par.setValue(parsrow.get(i1).child(colDefault - 1).text() + "");
						}
						pars.add(par);
					}
				}
				apiItem.setParameters(pars);
				apiItem.setMethod("POST");
			}
			// 暴力执法，如果捕获异常就跳过此页面
			catch (Exception e) {
				logger.info("解析接口信息出错");
				logger.error("异常", e);
				continue;
			}
			// 加入列表
			apiItems.add(apiItem);
		}
		return apiItems;
	}
}
