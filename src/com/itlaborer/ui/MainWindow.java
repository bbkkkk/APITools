package com.itlaborer.ui;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wb.swt.SWTResourceManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.itlaborer.model.ApiDoc;
import com.itlaborer.model.ApiItem;
import com.itlaborer.model.ApiList;
import com.itlaborer.model.ApiPar;
import com.itlaborer.res.Resource;
import com.itlaborer.res.XinzhiWeather;
import com.itlaborer.utils.ApiUtils;
import com.itlaborer.utils.JsonFormatUtils;
import com.itlaborer.utils.ParamUtils;
import com.itlaborer.utils.PropertiesUtils;

import net.dongliu.requests.RawResponse;

/**
 * 
 * 主界面
 * 
 * @author liu
 *
 */
public class MainWindow {

	private static Logger logger = Logger.getLogger(MainWindow.class.getName());
	// 加载的配置文件项
	private String[] loadAddressArray;
	private String loadApiJson;
	private String loadCodeFile;
	private int loadHistorySum;
	// 使用的成员变量
	private ApiDoc apiDoc;
	private String serverAdress;
	private Properties apiReturnCode;
	private String apiReturnStr;
	private ApiList history;
	private int httpCode, parsSum;
	private String headerReturnStr;
	private long httpTime;
	protected LinkedHashMap<String, String> header;
	protected LinkedHashMap<String, String> cookies;
	// 界面组件
	protected Shell mainWindowShell;
	private final FormToolkit formToolkit;
	private Button parsCovertButton;
	private Button parsClearButton;
	private Button toBrower;
	private Button apiStatusButton;
	private Combo methodSelectCombo;
	private Combo modSelectCombo;
	private Combo interfaceCombo;
	private StyledText resultStyledText;
	private Text statusBar;
	private Text parsText;
	private Text urlText;
	private Button submitButton;
	private Button button;
	private Button textClearButton;
	private Button clearSpaceButton;
	private Table formTable;
	private Text[][] form;
	private Label[] label;
	private MenuItem serverSelect;
	private Menu servers;

	// 主窗口
	public MainWindow() {
		PropertyConfigurator.configure("config/log4j.properties ");
		logger.info("程序启动, 程序版本为:" + Resource.getVersion());
		this.formToolkit = new FormToolkit(Display.getDefault());
		this.parsSum = 128;
		this.loadHistorySum = 30;
		this.cookies = new LinkedHashMap<String, String>();
		this.header = new LinkedHashMap<String, String>();
		this.header.put("User-Agent", "APITools-" + Resource.VERSION);
		this.header.put("SocksTimeout", "30000");
		this.header.put("ConnectTimeout", "30000");
	}

	// 从这里开始，不是么？小桥流水人家~
	public static void main(String[] args) {
		try {
			MainWindow window = new MainWindow();
			window.open();
		} catch (Exception e) {
			logger.error("异常", e);
		}
	}

	public void open() {
		Display display = Display.getDefault();
		createContents(display);
		mainWindowShell.open();
		InitSystem();
		while (!mainWindowShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		logger.info("再见~~~~~");
		// 执行下面这一句，否则存在一定的几率程序退出后，
		// 进程还在,这种情况发生在将程序通过exe4j打包为exe的时候
		System.exit(0);
	}

	protected void createContents(final Display display) {
		mainWindowShell = new Shell(SWT.CLOSE | SWT.MIN | SWT.TITLE);
		ApiUtils.SetCenter(mainWindowShell);
		mainWindowShell.setSize(1148, 650);
		mainWindowShell.setText("APITools" + "-" + Resource.getVersion());
		mainWindowShell.setImage(SWTResourceManager.getImage(MainWindow.class, "/com/itlaborer/res/icon.ico"));
		ApiUtils.DropTargetSupport(mainWindowShell);
		// 菜单////////////////////////////////////////////////////////
		Menu rootMenu = new Menu(mainWindowShell, SWT.BAR);
		mainWindowShell.setMenuBar(rootMenu);

		// 工具菜单///////////////////////////////////////////////////
		/////////////////// 编辑////////////////////////////////////////
		MenuItem menuEdit = new MenuItem(rootMenu, SWT.CASCADE);
		menuEdit.setText("编辑");

		Menu menu_1 = new Menu(menuEdit);
		menuEdit.setMenu(menu_1);

		MenuItem menuItemSave = new MenuItem(menu_1, SWT.NONE);
		menuItemSave.setText("保存当前接口参数（关闭前有效）");

		MenuItem menuItemSaveToFile = new MenuItem(menu_1, SWT.NONE);
		menuItemSaveToFile.setText("保存当前接口参数（保存到接口列表文件）");

		// 工具菜单///////////////////////////////////////////////////
		MenuItem menuToolKit = new MenuItem(rootMenu, SWT.CASCADE);
		menuToolKit.setText("工具箱");
		// 工具菜单子菜单
		Menu menu = new Menu(menuToolKit);
		menuToolKit.setMenu(menu);
		// 工具-文件转换
		MenuItem menuItemConvertDoc = new MenuItem(menu, SWT.NONE);
		menuItemConvertDoc.setText("恒生API文档转换");
		// 工具-接口列表编辑
		MenuItem menuItemApiListEdit = new MenuItem(menu, SWT.NONE);
		menuItemApiListEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				statusBar.setText("此功能暂未实现");
			}
		});
		menuItemApiListEdit.setText("接口列表编辑");

		MenuItem menuItemUrl = new MenuItem(menu, SWT.NONE);
		menuItemUrl.setText("URL编码/解码");

		MenuItem menuItemBase64 = new MenuItem(menu, SWT.NONE);
		menuItemBase64.setText("Base64编码/解码");

		MenuItem menuItemUnicode = new MenuItem(menu, SWT.NONE);
		menuItemUnicode.setText("Unicode编码/解码");

		MenuItem menuItemMd5 = new MenuItem(menu, SWT.NONE);
		menuItemMd5.setText("MD5加密");

		MenuItem menuPar = new MenuItem(rootMenu, SWT.CASCADE);
		menuPar.setText("Header参数");
		Menu menu_2 = new Menu(menuPar);
		menuPar.setMenu(menu_2);

		MenuItem menuItemHeader = new MenuItem(menu_2, SWT.NONE);
		menuItemHeader.setText("Header");

		MenuItem menuItemCookie = new MenuItem(menu_2, SWT.NONE);
		menuItemCookie.setText("Cookie");

		// 服务器列表
		serverSelect = new MenuItem(rootMenu, SWT.CASCADE);
		serverSelect.setText("服务器列表");

		servers = new Menu(serverSelect);
		serverSelect.setMenu(servers);

		/////////////////// 帮助////////////////////////////////////////
		MenuItem menuHelp = new MenuItem(rootMenu, SWT.CASCADE);
		menuHelp.setText("帮助");

		Menu menu_3 = new Menu(menuHelp);
		menuHelp.setMenu(menu_3);

		MenuItem menuItemManual = new MenuItem(menu_3, SWT.NONE);
		menuItemManual.setText("查看手册");

		MenuItem menuItemFeedBack = new MenuItem(menu_3, SWT.NONE);
		menuItemFeedBack.setText("报告问题");
		// 菜单项-关于
		MenuItem menuItemAbout = new MenuItem(menu_3, SWT.NONE);
		menuItemAbout.setText("关于");
		// 模块选择
		modSelectCombo = new Combo(mainWindowShell, SWT.READ_ONLY);
		modSelectCombo.setBounds(8, 8, 224, 25);
		formToolkit.adapt(modSelectCombo);
		// 接口选择
		interfaceCombo = new Combo(mainWindowShell, SWT.READ_ONLY);
		interfaceCombo.setBounds(237, 8, 245, 25);
		formToolkit.adapt(interfaceCombo);
		// 表单
		parsText = new Text(mainWindowShell, SWT.BORDER);
		parsText.setBounds(7, 39, 476, 25);
		// URL
		urlText = new Text(mainWindowShell, SWT.BORDER);
		urlText.setBounds(487, 8, 478, 25);
		// HTTP请求的方法下拉选择框
		methodSelectCombo = new Combo(mainWindowShell, SWT.DROP_DOWN | SWT.READ_ONLY);
		methodSelectCombo.setBounds(971, 7, 67, 25);
		formToolkit.adapt(methodSelectCombo);
		methodSelectCombo.add("GET", 0);
		methodSelectCombo.add("POST", 1);
		methodSelectCombo.add("HEAD", 2);
		methodSelectCombo.add("PUT", 3);
		methodSelectCombo.add("PATCH", 4);
		methodSelectCombo.add("DELETE", 5);

		// 提交按钮
		submitButton = new Button(mainWindowShell, SWT.NONE);
		submitButton.setBounds(1044, 6, 90, 27);
		submitButton.setText("提      交");

		// 参数转换
		parsCovertButton = new Button(mainWindowShell, SWT.NONE);
		parsCovertButton.setToolTipText("导入形如a=1&&b=2的参数串到表单");
		parsCovertButton.setText("导入参数");
		parsCovertButton.setBounds(487, 38, 70, 27);
		formToolkit.adapt(parsCovertButton, true, true);

		// 重置参数
		parsClearButton = new Button(mainWindowShell, SWT.NONE);
		parsClearButton.setToolTipText("重置参数为接口文档中定义的参数");
		parsClearButton.setText("重置参数");
		parsClearButton.setBounds(563, 38, 70, 27);
		formToolkit.adapt(parsClearButton, true, true);

		// 排除空格
		clearSpaceButton = new Button(mainWindowShell, SWT.NONE);
		clearSpaceButton.setToolTipText("清除参数两头可能存在的空格");
		clearSpaceButton.setText("TRIM参数");
		clearSpaceButton.setBounds(639, 38, 70, 27);
		formToolkit.adapt(clearSpaceButton, true, true);

		// 重排参数
		button = new Button(mainWindowShell, SWT.NONE);
		button.setToolTipText("将参数重新从第一个表格重排列");
		button.setText("重排参数");
		button.setBounds(714, 38, 70, 27);
		formToolkit.adapt(button, true, true);

		// auth
		Button btnAuthorization = new Button(mainWindowShell, SWT.NONE);
		btnAuthorization.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				statusBar.setText("此功能暂未实现");
			}
		});
		btnAuthorization.setToolTipText("授权管理");
		btnAuthorization.setText("Authorization");
		btnAuthorization.setBounds(790, 38, 86, 27);
		formToolkit.adapt(btnAuthorization, true, true);

		// api状态码
		apiStatusButton = new Button(mainWindowShell, SWT.NONE);
		apiStatusButton.setToolTipText("解析Response里的code值");
		apiStatusButton.setText("返回码解读");
		apiStatusButton.setBounds(882, 38, 83, 27);
		formToolkit.adapt(apiStatusButton, true, true);
		// 点击清除结果
		textClearButton = new Button(mainWindowShell, SWT.NONE);
		textClearButton.setToolTipText("清空结果内容");
		textClearButton.setText("清空结果");
		textClearButton.setBounds(970, 38, 69, 27);
		formToolkit.adapt(textClearButton, true, true);
		// 去浏览器
		toBrower = new Button(mainWindowShell, SWT.NONE);
		toBrower.setToolTipText("用HTTP GET方式在浏览器中请求接口");
		toBrower.setText("浏览器中打开");
		toBrower.setBounds(1044, 38, 90, 27);
		formToolkit.adapt(toBrower, true, true);

		// 参数table
		formTable = new Table(mainWindowShell, SWT.BORDER | SWT.HIDE_SELECTION);
		formTable.setBounds(7, 70, 476, 500);
		formTable.setItemCount(parsSum);
		formTable.setHeaderVisible(true);
		formTable.setLinesVisible(true);

		// 表列
		TableColumn tblclmnNewColumn = new TableColumn(formTable, SWT.BORDER);
		tblclmnNewColumn.setWidth(38);
		tblclmnNewColumn.setResizable(false);
		tblclmnNewColumn.setText("编号");

		TableColumn nameColumn = new TableColumn(formTable, SWT.BORDER);
		nameColumn.setWidth((int) ((formTable.getBounds().width - tblclmnNewColumn.getWidth()
				- formTable.getVerticalBar().getSize().x - 4) * 0.45));
		nameColumn.setText("参数名");
		nameColumn.setResizable(false);

		TableColumn valueColumn_1 = new TableColumn(formTable, SWT.BORDER);
		valueColumn_1.setWidth((int) ((formTable.getBounds().width - tblclmnNewColumn.getWidth()
				- formTable.getVerticalBar().getSize().x - 4) * 0.55));
		valueColumn_1.setText("参数值");
		valueColumn_1.setResizable(false);

		// 将Label和Text绑定到table
		label = new Label[parsSum];
		form = new Text[parsSum][2];
		TableItem[] items = formTable.getItems();
		for (int i = 0; i < parsSum; i++) {
			// 第一列
			TableEditor editor0 = new TableEditor(formTable);
			label[i] = new Label(formTable, SWT.NONE | SWT.CENTER);
			label[i].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
			label[i].setText(new DecimalFormat("000").format(i + 1));
			editor0.grabHorizontal = true;
			editor0.setEditor(label[i], items[i], 0);

			// 第二列
			TableEditor editor1 = new TableEditor(formTable);
			form[i][0] = new Text(formTable, SWT.NONE);
			form[i][0].setText(items[i].getText(1));
			editor1.grabHorizontal = true;
			editor1.setEditor(form[i][0], items[i], 1);
			// 第三列
			TableEditor editor2 = new TableEditor(formTable);
			form[i][1] = new Text(formTable, SWT.NONE);
			form[i][1].setText(items[i].getText(2));
			editor2.grabHorizontal = true;
			editor2.setEditor(form[i][1], items[i], 2);
			// 设置焦点变色
			final int b = i;
			form[i][0].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(FocusEvent e) {
					label[b].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
					form[b][0].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
					form[b][1].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
				}

				@Override
				public void focusGained(FocusEvent e) {
					label[b].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
					form[b][0].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
					form[b][1].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
				}
			});
			form[i][1].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(FocusEvent e) {
					label[b].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
					form[b][0].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
					form[b][1].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
				}

				@Override
				public void focusGained(FocusEvent e) {
					label[b].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
					form[b][0].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
					form[b][1].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
				}
			});
		}
		// 接口返回内容显示区域
		resultStyledText = new StyledText(mainWindowShell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		resultStyledText.setAlwaysShowScrollBars(true);
		resultStyledText.setBounds(487, 70, 647, 500);
		formToolkit.adapt(resultStyledText);
		ApiUtils.StyledTextAddContextMenu(resultStyledText);

		// 状态栏
		statusBar = new Text(mainWindowShell, SWT.BORDER);
		statusBar.setBounds(7, 575, 1127, 23);
		formToolkit.adapt(statusBar, true, true);

		// 各个组件的监听事件//////////////////////////////////////////////////////////////////////////////////////////
		// 保存事件
		menuItemSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				savePars();
				statusBar.setText("保存成功，程序关闭前有效");
			}
		});
		// 保存事件
		menuItemSaveToFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				savePars();
				// 保存到文件--潜在的风险，保存时间过长程序界面卡死
				if (ApiUtils.SaveToFile(new File("./config/" + loadApiJson),
						JsonFormatUtils.Format(JSON.toJSONString(apiDoc)))) {
					statusBar.setText("保存成功");
				} else {
					statusBar.setText("保存失败，请重试");
				}
			}
		});
		// 参数重排
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				compressParameters();
			}
		});

		// 菜单选项事件
		menuItemUrl.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				UrlEncodeTools urlEncodeTools = new UrlEncodeTools(mainWindowShell, SWT.CLOSE | SWT.SYSTEM_MODAL);
				urlEncodeTools.open();
			}
		});

		// 关于-点击事件
		menuItemAbout.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AboutTools about = new AboutTools(mainWindowShell, SWT.CLOSE | SWT.SYSTEM_MODAL);
				about.open();
			}
		});

		// Base64工具
		menuItemBase64.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Base64Tools base64Tools = new Base64Tools(mainWindowShell, SWT.CLOSE | SWT.SYSTEM_MODAL);
				base64Tools.open();
			}
		});

		// Unicode工具
		menuItemUnicode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				UnicodeTools unicodeTools = new UnicodeTools(mainWindowShell, SWT.CLOSE | SWT.SYSTEM_MODAL);
				unicodeTools.open();
			}
		});

		// MD5工具
		menuItemMd5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MD5Tools md5Tools = new MD5Tools(mainWindowShell, SWT.CLOSE | SWT.SYSTEM_MODAL);
				md5Tools.open();
			}
		});

		// 手册
		menuItemManual.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Program.launch("http://www.itlaborer.com/apitools_manual");
			}
		});

		// 问题反馈
		menuItemFeedBack.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Program.launch("http://www.itlaborer.com/2016/08/07/apitools_feedback.html");
			}
		});
		// Header编辑器
		menuItemHeader.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				HeaderEdit headerEdit = new HeaderEdit(mainWindowShell, "Header常规", SWT.CLOSE | SWT.SYSTEM_MODAL);
				header = headerEdit.open(header);
				logger.info("读取到Header:" + header);
			}
		});
		// Cookie编辑器
		menuItemCookie.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				HeaderEdit headerEdit = new HeaderEdit(mainWindowShell, "Cookie", SWT.CLOSE | SWT.SYSTEM_MODAL);
				cookies = headerEdit.open(cookies);
				logger.info("读取到Cookie:" + cookies);
			}
		});

		// 重置参数事件
		parsClearButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearParameters();
				try {
					urlText.setText(serverAdress + apiDoc.getApilist().get(modSelectCombo.getSelectionIndex()).getApi()
							.get(interfaceCombo.getSelectionIndex()).getAddress());
					initParameters(apiDoc.getApilist().get(modSelectCombo.getSelectionIndex()).getApi()
							.get(interfaceCombo.getSelectionIndex()).getParameters());
				} catch (Exception e2) {
					logger.error("当前选择的接口并不包含参数信息，无法完成重新初始化，默认留空");
				}
			}
		});

		// 清空空格点击事件
		clearSpaceButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 遍历参数框，找到参数里多余的空格
				for (int i = 0; i < parsSum; i++) {
					form[i][0].setText(form[i][0].getText().trim());
					form[i][1].setText(form[i][1].getText().trim());
				}
				logger.debug("寻找参数里的多余的空格完毕");
			}
		});

		// 分组选择事件
		modSelectCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearParameters();
				initInterfaceCombo(apiDoc.getApilist().get(modSelectCombo.getSelectionIndex()).getApi());
				logger.debug("切换到分组:" + apiDoc.getApilist().get(modSelectCombo.getSelectionIndex()).getName());
			}
		});

		// 接口选择事件
		interfaceCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				urlText.setText(serverAdress + apiDoc.getApilist().get(modSelectCombo.getSelectionIndex()).getApi()
						.get(interfaceCombo.getSelectionIndex()).getAddress());
				initParameters(apiDoc.getApilist().get(modSelectCombo.getSelectionIndex()).getApi()
						.get(interfaceCombo.getSelectionIndex()).getParameters());
				logger.debug("切换到接口:" + apiDoc.getApilist().get(modSelectCombo.getSelectionIndex()).getApi()
						.get(interfaceCombo.getSelectionIndex()).getName());
			}
		});

		// 切换表单发送方式的点击事件
		methodSelectCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.debug("切换表单发送方式为:" + methodSelectCombo.getText());
			}
		});

		// 在浏览器中打开的按钮事件
		toBrower.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (StringUtils.isEmpty(urlText.getText())) {
					statusBar.setText("空地址无法发起请求");
					return;
				}
				HashMap<String, String> pars1 = getParameters();
				parsText.setText(ParamUtils.mapToQuery(pars1));
				String url = urlText.getText();
				Program.launch(url + (pars1.size() == 0 ? ("") : ("?" + ParamUtils.mapToQuery(pars1))));
				logger.info("浏览器中打开:" + url + (pars1.size() == 0 ? ("") : ("?" + ParamUtils.mapToQuery(pars1))));
				statusBar.setText("已在浏览器中发起请求");
			}
		});

		// 菜单，转换工具的点击事件
		menuItemConvertDoc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CovertTools tools = new CovertTools(mainWindowShell, SWT.CLOSE | SWT.SYSTEM_MODAL);
				tools.open();
			}
		});

		// 状态码按钮的点击事件
		apiStatusButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String text = resultStyledText.getText();
				if (isJson(text) && !isXml(text) && !text.isEmpty()) {
					logger.debug("返回的信息为JSON格式，开始尝试解析返回码");
					try {
						JSONObject jsonObject = (JSONObject) JSONObject.parse(text);
						JSONObject resultJson = jsonObject;
						String returnText = apiReturnCode.getProperty(resultJson.get("code") + "");
						if (returnText == null) {
							logger.warn("返回码是:" + resultJson.get("code") + ",未找到该返回码的描述信息");
							statusBar.setText("返回码是:" + resultJson.get("code") + ",未找到该返回码的描述信息");
						} else {
							logger.debug("返回码是:" + resultJson.get("code") + ":" + returnText);
							statusBar.setText("返回码是:" + resultJson.get("code") + ":" + returnText);
						}
					} catch (Exception e2) {
						logger.error("异常", e2);
						statusBar.setText("异常:解析API返回码错误");
					}

				} else if (!isJson(text) && isXml(text) && !text.isEmpty()) {
					logger.debug("返回的信息为XML格式，开始尝试解析返回码");
					try {
						Document document = DocumentHelper.parseText(text);
						Element root = document.getRootElement();
						String returnText = apiReturnCode.getProperty(root.element("code").getText());
						if (returnText == null) {
							logger.warn("返回码是:" + root.element("code").getText() + ",未找到该返回码的描述信息");
							statusBar.setText("返回码是:" + root.element("code").getText() + ",未找到该返回码的描述信息");
						} else {
							statusBar.setText("返回码是:" + root.element("code").getText() + ":" + returnText);
						}
					} catch (DocumentException e1) {
						statusBar.setText("异常:解析API返回码错误");
						logger.error("异常", e1);
					} catch (NullPointerException e2) {
						logger.error("异常", e2);
						statusBar.setText("异常:解析API返回码错误");
					}
				} else if (!(isJson(text) && isXml(text))) {
					statusBar.setText("解析API返回码错误--貌似结果里不包含状态码");
					logger.debug("解析API返回码错误--貌似结果里不包含状态码");
				}
			}

			// 判断是否是json
			boolean isJson(String text) {
				try {
					JSONObject.parse(text);
					return true;
				} catch (Exception e1) {
					return false;
				}
			}

			// 判断是否是xml
			boolean isXml(String text) {
				try {
					DocumentHelper.parseText(text);
					return true;
				} catch (Exception e1) {
					return false;
				}
			}
		});

		// 此为为提交按钮添加点击事件
		submitButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				statusBar.setText("请求中······");
				final HashMap<String, String> pars = getParameters();
				parsText.setText(ParamUtils.mapToQuery(pars));
				final String url = urlText.getText();
				final String method = methodSelectCombo.getText();
				// 通知更新历史
				notifyHistory();
				new Thread() {
					public void run() {
						logger.debug("请求信息:" + url + "?" + ParamUtils.mapToQuery(pars));
						final long sumbegintime = System.currentTimeMillis();
						long httpend = System.currentTimeMillis();
						RawResponse result = null;
						switch (method) {
						case "GET":
							logger.debug("使用GET方式发起请求");
							try {
								result = ApiUtils.HttpGet(url, pars, header, cookies, StandardCharsets.UTF_8);
								httpend = System.currentTimeMillis();
								httpTime = httpend - sumbegintime;
								apiReturnStr = "";
								headerReturnStr = "";
								apiReturnStr = JsonFormatUtils.Format(result.readToText());
								headerReturnStr = result.getHeaders().toString();
								httpCode = result.getStatusCode();
								logger.info("响应头部:" + result.getHeaders().toString());
							} catch (Exception e) {
								apiReturnStr = "";
								headerReturnStr = "";
								logger.error("异常", e);
								httpend = System.currentTimeMillis();
								httpTime = httpend - sumbegintime;
							}
							break;
						case "POST":
							logger.debug("使用POST方式发起请求");
							try {
								result = ApiUtils.HttpPost(url, pars, header, cookies, StandardCharsets.UTF_8);
								httpend = System.currentTimeMillis();
								httpTime = httpend - sumbegintime;
								apiReturnStr = "";
								headerReturnStr = "";
								apiReturnStr = JsonFormatUtils.Format(result.readToText());
								headerReturnStr = result.getHeaders().toString();
								httpCode = result.getStatusCode();
								logger.info("响应头部:" + result.getHeaders().toString());
							} catch (Exception e) {
								apiReturnStr = "";
								headerReturnStr = "";
								headerReturnStr = null;
								logger.error("异常", e);
								httpend = System.currentTimeMillis();
								httpTime = httpend - sumbegintime;
							}
							break;
						case "HEAD":
							logger.debug("使用HEAD方式发起请求");
							try {
								result = ApiUtils.HttpHead(url, pars, header, cookies, StandardCharsets.UTF_8);
								httpend = System.currentTimeMillis();
								httpTime = httpend - sumbegintime;
								apiReturnStr = "";
								headerReturnStr = "";
								headerReturnStr = result.getHeaders().toString();
								List<Entry<String, String>> header = result.getHeaders();
								for (int i = 0; i < header.size(); i++) {
									apiReturnStr += header.get(i).toString() + "\n";
								}
								httpCode = result.getStatusCode();
								logger.info("响应头部:" + result.getHeaders().toString());
							} catch (Exception e) {
								apiReturnStr = "";
								headerReturnStr = "";
								logger.error("异常", e);
								httpend = System.currentTimeMillis();
								httpTime = httpend - sumbegintime;
							}
							break;
						case "PUT":
							logger.debug("使用PUT方式发起请求");
							try {
								result = ApiUtils.HttpPost(url, pars, header, cookies, StandardCharsets.UTF_8);
								httpend = System.currentTimeMillis();
								httpTime = httpend - sumbegintime;
								apiReturnStr = "";
								headerReturnStr = "";
								apiReturnStr = JsonFormatUtils.Format(result.readToText());
								headerReturnStr = result.getHeaders().toString();
								httpCode = result.getStatusCode();
								logger.info("响应头部:" + result.getHeaders().toString());
							} catch (Exception e) {
								apiReturnStr = "";
								headerReturnStr = "";
								logger.error("异常", e);
								httpend = System.currentTimeMillis();
								httpTime = httpend - sumbegintime;
							}
							break;
						case "PATCH":
							logger.debug("使用PATCH方式发起请求");
							try {
								result = ApiUtils.HttpPatch(url, pars, header, cookies, StandardCharsets.UTF_8);
								httpend = System.currentTimeMillis();
								httpTime = httpend - sumbegintime;
								apiReturnStr = "";
								headerReturnStr = "";
								apiReturnStr = JsonFormatUtils.Format(result.readToText());
								headerReturnStr = result.getHeaders().toString();
								httpCode = result.getStatusCode();
								logger.info("响应头部:" + result.getHeaders().toString());
							} catch (Exception e) {
								apiReturnStr = "";
								headerReturnStr = "";
								logger.error("异常", e);
								httpend = System.currentTimeMillis();
								httpTime = httpend - sumbegintime;
							}
							break;
						case "DELETE":
							logger.debug("使用DELETE方式发起请求");
							try {
								result = ApiUtils.HttpDelete(url, pars, header, cookies, StandardCharsets.UTF_8);
								httpend = System.currentTimeMillis();
								httpTime = httpend - sumbegintime;
								apiReturnStr = "";
								headerReturnStr = "";
								apiReturnStr = JsonFormatUtils.Format(result.readToText());
								headerReturnStr = result.getHeaders().toString();
								httpCode = result.getStatusCode();
								logger.info("响应头部:" + result.getHeaders().toString());
							} catch (Exception e) {
								apiReturnStr = "";
								headerReturnStr = "";
								logger.error("异常", e);
								httpend = System.currentTimeMillis();
								httpTime = httpend - sumbegintime;
							}
							break;
						default:
							logger.debug("HTTP请求时未找到可用的方法");
							apiReturnStr = "";
							headerReturnStr = "你选择的方法本工具暂未实现";
							break;
						}
						// 标记请求结束时间
						final long sumendtime = System.currentTimeMillis();
						display.syncExec(new Thread() {
							public void run() {
								resultStyledText.setText(apiReturnStr);
								statusBar.setText("请求结束/HTTP状态码:" + httpCode + "/HTTP请求耗时:" + httpTime + "ms" + "/总耗时:"
										+ (sumendtime - sumbegintime) + "ms" + "/Header:" + headerReturnStr);
							}
						});
					}
				}.start();
			}
		});

		// 此为导入参数按钮添加点击事件
		parsCovertButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 需要尝试url解码
				String queryString;
				try {
					queryString = URLDecoder.decode(parsText.getText(), "UTF-8");
					if (queryString.equals("")) {
						logger.info("参数串为空,停止转换");
						statusBar.setText("参数串为空,停止转换");
						return;
					}
					HashMap<String, String> queryMap = new HashMap<String, String>();
					queryMap = ParamUtils.queryToMap(queryString);
					initParameters(covertHashMaptoApiPar(queryMap));
					parsText.setText(queryString);
				} catch (UnsupportedEncodingException e1) {
					logger.error("异常", e1);
				}
			}
		});

		// 清除内容
		textClearButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				resultStyledText.setText("");
				statusBar.setText("");
				logger.debug("清理结束");
			}
		});
	}

	// 保存参数
	private void savePars() {
		logger.debug("调用了保存参数");
		if (modSelectCombo.getSelectionIndex() == -1 | interfaceCombo.getSelectionIndex() == -1) {
			statusBar.setText("保存失败：只允许在现有接口上保存已填写的参数");
			return;
		}
		// 获取当前文档节点
		ApiItem item = apiDoc.getApilist().get(modSelectCombo.getSelectionIndex()).getApi()
				.get(interfaceCombo.getSelectionIndex());
		ArrayList<ApiPar> pars = item.getParameters();
		// 移除现有的参数
		pars.removeAll(pars);
		// 重新从form框初始化
		for (int i = 0; i < form.length; i++) {
			if (StringUtils.isNotEmpty(form[i][0].getText()) || StringUtils.isNotEmpty(form[i][1].getText())) {
				pars.add(new ApiPar(form[i][0].getText(), form[i][0].getToolTipText(), form[i][1].getText()));
			}
		}
	}

	///////////////////////////////////////////////////////////////////////////
	// 初始化程序
	private void InitSystem() {
		// 加载并初始化参数信息
		methodSelectCombo.select(0);
		logger.debug("初始化配置信息-默认表单发送方式为:" + this.methodSelectCombo.getText());
		File file = new File("./config");
		if (!file.exists()) {
			file.mkdir();
			logger.warn("警告:初始化配置信息出错,配置目录不存在,已创建");
		} else if (!file.isDirectory()) {
			logger.warn("警告:初始化配置信息出错,配置目录不存在,已创建");
			file.delete();
			file.mkdir();
		}
		File file1 = new File("./log");
		if (!file1.exists()) {
			file1.mkdir();
			logger.warn("警告:日志目录不存在,已创建");
		} else if (!file1.isDirectory()) {
			logger.warn("警告:日志目录不存在,已创建");
			file1.delete();
			file1.mkdir();
		}
		// 读取并加载配置文件
		File configFile = new File("./config/config.properties");
		File log4jFile = new File("./config/log4j.properties");
		if (!configFile.exists()) {
			try {
				ApiUtils.SaveToFile(configFile, Resource.getCONFIG());
				logger.warn("警告:参数配置文件丢失，已创建默认配置");
				// 当创建默认配置文档的时候也生成个默认的接口列表--心知天气
				/////////////////////////// 示例接口//////////////////////////////////////
				ApiUtils.SaveToFile(new File("./config/api-xinzhiweather.json"),
						JsonFormatUtils.Format(JSON.toJSONString(new XinzhiWeather().getApidoc())));
			} catch (Exception e) {
				logger.warn("异常", e);
			}
		}
		if (!log4jFile.exists()) {
			try {
				ApiUtils.SaveToFile(log4jFile, Resource.getLOG4J());
				logger.warn("警告:日志配置文件丢失，已创建默认配置");
			} catch (Exception e) {
				logger.warn("异常", e);
			}
		}
		// 此处开始加载配置文件内容
		try {
			// 加载配置
			Properties properties = PropertiesUtils.ReadProperties(configFile);

			// 配置历史记录条数
			if ((null != properties.getProperty("hsitorysum"))
					&& Integer.parseInt(properties.getProperty("hsitorysum")) > 0) {
				this.loadHistorySum = Integer.parseInt(properties.getProperty("hsitorysum"));
			}
			// 加载地址列表
			loadAddressArray = properties.getProperty("apiaddress").split(",");
			if (null != loadAddressArray && loadAddressArray.length > 0) {
				if (StringUtils.isNotEmpty(loadAddressArray[0])) {
					this.serverAdress = loadAddressArray[0];
					// 初始化服务器下拉选择框
					for (int i = 0; i < loadAddressArray.length; i++) {
						final MenuItem mntmNewItem = new MenuItem(servers, SWT.NONE);
						mntmNewItem.setText(loadAddressArray[i]);
						mntmNewItem.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								urlText.setText(urlText.getText().replace(serverAdress, mntmNewItem.getText()));
								serverAdress = mntmNewItem.getText();
							}
						});
					}
				} else {
					this.serverAdress = "http://127.0.0.1/";
				}
			}
			this.loadCodeFile = properties.getProperty("returncodefile");
			this.loadApiJson = properties.getProperty("apilist");

		} catch (Exception e) {
			statusBar.setText("读取配置失败，请检查");
			logger.warn("读取配置失败，请检查", e);
		}
		// 此处开始加载返回码列表文件
		File reader = new File("./config/" + loadCodeFile);
		if (reader.exists()) {
			apiReturnCode = PropertiesUtils.ReadProperties(reader);
			logger.debug("加载返回码配置文件" + "./config/" + loadCodeFile);
		} else {
			logger.debug("没有读到返回码配置,跳过加载");
		}
		// 配置文件加载完毕后开始加载API列表
		if (null == loadApiJson) {
			logger.debug("API列表为空，跳过加载");
		} else {
			InitApiList();
		}
	}

	// 初始化API列表信息
	private void InitApiList() {
		File apilistfile = new File("./config/" + loadApiJson);
		if (!apilistfile.exists()) {
			logger.warn("警告:您加载的API文档不存在，程序将跳过加载API列表，请检查配置");
			return;
		}
		try {
			apiDoc = JSON.parseObject(ApiUtils.ReadFromFile(apilistfile, "UTF-8"), ApiDoc.class);
			// 初始化历史记录
			initHistory();
			if (apiDoc.getDecode_version().equals("1.0")) {
				logger.debug("加载的api版本为" + apiDoc.getApi_version());
				initModCombo();
			} else {
				logger.warn("警告:您加载的API列表可能是老版本的，请重新生成列表配置");
			}
		} catch (Exception e) {
			logger.error("异常:" + e);
		}
	}

	// 初始化接口分类
	private void initModCombo() {
		modSelectCombo.removeAll();
		if (null == apiDoc.getApilist()) {
			return;
		}
		for (int i = 0; i < apiDoc.getApilist().size(); i++) {
			modSelectCombo.add(apiDoc.getApilist().get(i).getName());
			logger.debug("API分类:" + apiDoc.getApilist().get(i).getName() + "加载完毕");
		}
		modSelectCombo.select(0);
		initInterfaceCombo(apiDoc.getApilist().get(0).getApi());
	}

	// 初始化接口列表
	private void initInterfaceCombo(ArrayList<ApiItem> apiItems) {
		clearParameters();
		interfaceCombo.removeAll();
		if (null == apiItems || apiItems.size() == 0) {
			logger.debug("当前分类下无接口信息，跳过加载");
			return;
		}
		for (int i = 0; i < apiItems.size(); i++) {
			interfaceCombo.add(apiItems.get(i).getName());
		}
		try {
			interfaceCombo.select(0);
			urlText.setText(serverAdress + apiItems.get(0).getAddress());
			initParameters(apiItems.get(0).getParameters());
		} catch (Exception e) {
			logger.error("异常", e);
			urlText.setText(serverAdress);
		}
	}

	// 转换HashMap到ArrayList<ApiPar>
	private ArrayList<ApiPar> covertHashMaptoApiPar(HashMap<String, String> queryMap) {
		if (null == queryMap) {
			return null;
		}
		ArrayList<ApiPar> apiPars = new ArrayList<ApiPar>();
		for (Entry<String, String> entry : queryMap.entrySet()) {
			apiPars.add(new ApiPar(entry.getKey(), entry.getValue()));
		}
		return apiPars;
	}

	// 参数初始化
	private void initParameters(ArrayList<ApiPar> pars) {
		clearParameters();
		if (null != pars) {
			for (int i = 0; i < pars.size(); i++) {
				if (i > 127) {
					logger.info("使用的参数竟然超过了" + parsSum + "个");
					statusBar.setText("暂不支持" + parsSum + "个以上参数");
					break;
				}
				// 将参数初始化一下
				form[i][0].setText(pars.get(i).getName());
				form[i][0].setToolTipText(pars.get(i).getTip());
				form[i][1].setText(pars.get(i).getValue());
			}
		}

	}

	// 获取输入框中的参数-供发起请求的时候使用
	private HashMap<String, String> getParameters() {
		HashMap<String, String> par = new HashMap<String, String>();
		for (int i = 0; i < parsSum; i++) {
			if (!(form[i][0].getText().isEmpty() || form[i][1].getText().isEmpty())) {
				par.put(form[i][0].getText(), form[i][1].getText());
			}
		}
		return par;
	}

	// 清空参数信息-清空表单和参数输入框
	private void clearParameters() {
		statusBar.setText("");
		parsText.setText("");
		for (int i = 0; i < parsSum; i++) {
			form[i][0].setText("");
			form[i][0].setToolTipText("");
			form[i][1].setText("");
			form[i][1].setToolTipText("");
		}
	}

	// 参数重排
	private void compressParameters() {
		for (int i = 0; i < parsSum; i++) {
			if (form[i][0].getText().trim().isEmpty() && form[i][1].getText().trim().isEmpty()) {
				if (i == parsSum - 1) {
					break;
				} else {
					for (int j = i + 1; j < parsSum; j++) {
						if (!form[j][0].getText().trim().isEmpty() || !form[j][1].getText().trim().isEmpty()) {
							form[i][0].setText(form[j][0].getText());
							form[i][0].setToolTipText((form[j][0].getToolTipText()));
							form[i][1].setText(form[j][1].getText());
							form[j][0].setText("");
							form[j][0].setToolTipText("");
							form[j][1].setText("");
							break;
						} else {
							// 落花流水忽西东
							continue;
						}
					}
				}
			}
		}
	}

	// 初始化历史记录
	// 后期会支持保存历史记录
	private void initHistory() {
		history = new ApiList();
		history.setName("历史记录");
		history.setApi(new ArrayList<ApiItem>());
	}

	// 触发更新历史记录，只有在点击请求的时候才触发
	private void notifyHistory() {
		ArrayList<ApiItem> historyList = history.getApi();
		if (historyList.size() < this.loadHistorySum) {
			@SuppressWarnings("unused")
			ApiItem item = new ApiItem();
			historyList.add(null);
		} else {
		}
	}
}
