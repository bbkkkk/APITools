package com.itlaborer.ui;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
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

	private static Logger logger = Logger.getLogger(MainWindow.class.getName());;
	// 参数
	private ApiDoc apiDoc;
	private ApiList history;
	private Properties returnCode;
	private int httpCode, method, parsSum, hsitorysum;
	// BEGIN FOR HUNDSUN API3.0
	private boolean sissonKeyLock;
	private String sessionkey;
	private String merID;
	private String merPassWord;
	// END/////////////////////
	private String httpMethod;
	private String apiVersion;
	private String apiServerAdress;
	private String apiReturnStr;
	private String headerReturnStr;
	private String apiLoadJsonFile;
	private String apiReturnCodeFile;
	private long httpTime;
	protected LinkedHashMap<String, String> header;
	protected LinkedHashMap<String, String> cookies;
	// 界面组件
	protected Shell mainWindowShell;
	private final FormToolkit formToolkit;
	private Button parsCovertButton;
	private Button parsClearButton;
	private Button toBrower;
	private Button sessionKeyHoldButton;
	private Button apiStatusButton;
	private Combo methodSelectCombo;
	private Combo modSelectCombo;
	private Combo interfaceCombo;
	private StyledText resultStyledText;
	private Text statusBar;
	private Text parsText;
	private Text urlText;
	private Button submitButton;
	private Button textClearButton;
	private Button clearSpaceButton;
	private Table formTable;
	private Text[][] form;

	// 主窗口
	public MainWindow() {
		PropertyConfigurator.configure("config/log4j.properties ");
		logger.info("程序启动, 程序版本为:" + Resource.getVersion());
		this.formToolkit = new FormToolkit(Display.getDefault());
		this.apiVersion = "4.0";
		this.parsSum = 128;
		this.hsitorysum = 30;
		this.cookies = new LinkedHashMap<>();
		this.header = new LinkedHashMap<>();
		this.header.put("User-Agent", "APITools-" + Resource.VERSION);
		this.header.put("SocksTimeout", "20000");
		this.header.put("ConnectTimeout", "20000");

	}

	public static void main(String[] args) {
		try {
			MainWindow window = new MainWindow();
			window.open();
		} catch (Exception e) {
			logger.error("异常:", e);
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

	protected void createContents(Display display) {
		mainWindowShell = new Shell(SWT.CLOSE | SWT.MIN | SWT.TITLE);
		ApiUtils.SetCenter(mainWindowShell);
		mainWindowShell.setSize(1148, 650);
		mainWindowShell.setText("APITools" + "-" + Resource.getVersion());
		mainWindowShell.setImage(SWTResourceManager.getImage(MainWindow.class, "/com/itlaborer/res/icon.ico"));

		// 菜单////////////////////////////////////////////////////////
		Menu rootMenu = new Menu(mainWindowShell, SWT.BAR);
		mainWindowShell.setMenuBar(rootMenu);

		/////////////////// 编辑////////////////////////////////////////
		MenuItem menuFile = new MenuItem(rootMenu, SWT.CASCADE);
		menuFile.setText("编辑");

		Menu menu_1 = new Menu(menuFile);
		menuFile.setMenu(menu_1);

		MenuItem menuItemSave = new MenuItem(menu_1, SWT.NONE);
		menuItemSave.setText("保存参数（关闭前有效）");
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
		// MenuItem menuItemApiListEdit = new MenuItem(menu, SWT.NONE);
		// menuItemApiListEdit.setText("接口列表编辑");

		MenuItem menuItemUrl = new MenuItem(menu, SWT.NONE);
		menuItemUrl.setText("URL编码/解码");

		MenuItem menuItemBase64 = new MenuItem(menu, SWT.NONE);
		menuItemBase64.setText("Base64编码/解码");

		MenuItem menuItemUnicode = new MenuItem(menu, SWT.NONE);
		menuItemUnicode.setText("Unicode编码/解码");

		MenuItem menuItemMd5 = new MenuItem(menu, SWT.NONE);
		menuItemMd5.setText("MD5加密");

		// MenuItem menuPar = new MenuItem(rootMenu, SWT.CASCADE);
		// menuPar.setText("特殊参数");
		// Menu menu_2 = new Menu(menuPar);
		// menuPar.setMenu(menu_2);
		//
		// MenuItem menuItemHeader = new MenuItem(menu_2, SWT.NONE);
		// menuItemHeader.setText("Header");
		//
		// MenuItem menuItemCookie = new MenuItem(menu_2, SWT.NONE);
		// menuItemCookie.setText("Cookie");

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
		formToolkit.paintBordersFor(modSelectCombo);
		// 接口选择
		interfaceCombo = new Combo(mainWindowShell, SWT.READ_ONLY);
		interfaceCombo.setBounds(237, 8, 245, 25);
		formToolkit.adapt(interfaceCombo);
		formToolkit.paintBordersFor(interfaceCombo);
		// 表单
		parsText = new Text(mainWindowShell, SWT.BORDER);
		parsText.setBounds(7, 39, 476, 25);
		// URL
		urlText = new Text(mainWindowShell, SWT.BORDER);
		urlText.setBounds(487, 8, 487, 25);
		// HTTP请求的方法是get还是post-下拉选择框
		methodSelectCombo = new Combo(mainWindowShell, SWT.DROP_DOWN | SWT.READ_ONLY);
		methodSelectCombo.setBounds(980, 7, 57, 25);
		formToolkit.adapt(methodSelectCombo);
		formToolkit.paintBordersFor(methodSelectCombo);
		methodSelectCombo.add("GET", 0);
		methodSelectCombo.add("POST", 1);
		// 提交按钮
		submitButton = new Button(mainWindowShell, SWT.NONE);
		submitButton.setBounds(1042, 6, 92, 27);
		submitButton.setText("提      交");
		// 参数转换
		parsCovertButton = new Button(mainWindowShell, SWT.NONE);
		parsCovertButton.setText("导入参数");
		parsCovertButton.setBounds(487, 38, 65, 27);
		formToolkit.adapt(parsCovertButton, true, true);

		// 清空参数
		parsClearButton = new Button(mainWindowShell, SWT.NONE);
		parsClearButton.setText("清空参数");
		parsClearButton.setBounds(558, 38, 65, 27);
		formToolkit.adapt(parsClearButton, true, true);

		// 排除空格
		clearSpaceButton = new Button(mainWindowShell, SWT.NONE);
		clearSpaceButton.setToolTipText("清空参数里可能存在的空格");
		clearSpaceButton.setText("排除空格");
		clearSpaceButton.setBounds(629, 38, 65, 27);
		formToolkit.adapt(clearSpaceButton, true, true);

		// 重排参数
		Button button = new Button(mainWindowShell, SWT.NONE);
		button.setToolTipText("清空参数里可能存在的空格");
		button.setText("参数重排");
		button.setBounds(700, 38, 65, 27);
		formToolkit.adapt(button, true, true);

		// 锁定sessionkey
		sessionKeyHoldButton = new Button(mainWindowShell, SWT.NONE);
		sessionKeyHoldButton.setText("锁定sessionkey");
		sessionKeyHoldButton.setBounds(771, 38, 107, 27);
		formToolkit.adapt(sessionKeyHoldButton, true, true);
		// api状态码
		apiStatusButton = new Button(mainWindowShell, SWT.NONE);
		apiStatusButton.setText("API返回码解读");
		apiStatusButton.setBounds(884, 38, 90, 27);
		formToolkit.adapt(apiStatusButton, true, true);
		// 点击清除结果
		textClearButton = new Button(mainWindowShell, SWT.NONE);
		textClearButton.setText("清空结果");
		textClearButton.setBounds(979, 38, 59, 27);
		formToolkit.adapt(textClearButton, true, true);
		// 去浏览器
		toBrower = new Button(mainWindowShell, SWT.NONE);
		toBrower.setText("浏览器中打开");
		toBrower.setBounds(1042, 38, 92, 27);
		formToolkit.adapt(toBrower, true, true);

		formTable = new Table(mainWindowShell, SWT.BORDER | SWT.HIDE_SELECTION | SWT.V_SCROLL);
		formTable.setBounds(7, 70, 476, 500);
		formTable.setItemCount(parsSum);
		formToolkit.adapt(formTable);
		formToolkit.paintBordersFor(formTable);
		formTable.setHeaderVisible(true);
		formTable.setLinesVisible(true);

		// 表列
		TableColumn tblclmnNewColumn = new TableColumn(formTable, SWT.NONE);
		tblclmnNewColumn.setWidth(38);
		tblclmnNewColumn.setResizable(false);
		tblclmnNewColumn.setText("编号");

		TableColumn nameColumn = new TableColumn(formTable, SWT.NONE);
		nameColumn.setWidth(188);
		nameColumn.setText("参数名");
		nameColumn.setResizable(false);

		TableColumn valueColumn_1 = new TableColumn(formTable, SWT.NONE);
		valueColumn_1.setWidth(227);
		valueColumn_1.setText("参数值");
		valueColumn_1.setResizable(false);

		form = new Text[parsSum][2];
		TableItem[] items = formTable.getItems();
		for (int i = 0; i < parsSum; i++) {

			// 第一列
			TableEditor editor0 = new TableEditor(formTable);
			Label text = new Label(formTable, SWT.BORDER_SOLID | SWT.CENTER);
			text.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
			text.setText(new DecimalFormat("000").format(i + 1));
			editor0.grabHorizontal = true;
			editor0.setEditor(text, items[i], 0);
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
		}
		// 接口返回内容显示区域
		resultStyledText = new StyledText(mainWindowShell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		resultStyledText.setAlwaysShowScrollBars(true);
		resultStyledText.setBounds(487, 70, 647, 500);
		formToolkit.adapt(resultStyledText);
		formToolkit.paintBordersFor(resultStyledText);
		ApiUtils.StyledTextAddContextMenu(resultStyledText);

		// 状态栏
		statusBar = new Text(mainWindowShell, SWT.BORDER);
		statusBar.setBounds(7, 575, 1127, 23);
		formToolkit.adapt(statusBar, true, true);

		////////////////////////////////////////////////////////////////////
		// 拖拽源
		DropTarget dropTarget = new DropTarget(mainWindowShell, DND.DROP_NONE);
		Transfer[] transfer = new Transfer[] { FileTransfer.getInstance() };
		dropTarget.setTransfer(transfer);

		// 拖拽监听
		dropTarget.addDropListener(new DropTargetListener() {
			@Override
			public void dragEnter(DropTargetEvent event) {
				// TODO Auto-generated method stub
			}

			@Override
			public void dragLeave(DropTargetEvent event) {
				// TODO Auto-generated method stub
			}

			@Override
			public void dragOperationChanged(DropTargetEvent event) {
				// TODO Auto-generated method stub

			}

			@Override
			public void dragOver(DropTargetEvent event) {
				// TODO Auto-generated method stub

			}

			// 获取拖放进来的文件，暂无用途
			@Override
			public void drop(DropTargetEvent event) {
				// TODO Auto-generated method stub
				String[] files = (String[]) event.data;
				formTable.getItem(0).setText(0, "text");
				for (int i = 0; i < files.length; i++) {
					@SuppressWarnings("unused")
					File file = new File(files[i]);
				}
			}

			@Override
			public void dropAccept(DropTargetEvent event) {
				// TODO Auto-generated method stub
			}
		});

		// 各个组件的监听事件//////////////////////////////////////////////////////////////////////////////////////////
		// 保存事件
		menuItemSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.debug("调用了保存参数");
				if (modSelectCombo.getSelectionIndex() == -1 | interfaceCombo.getSelectionIndex() == -1) {
					statusBar.setText("保存失败：只允许在现有接口上保存已填写的参数");
					return;
				}
				if (apiVersion == "3.0") {
					statusBar.setText("保存参数功能不支持API3.0");
					return;
				}
				ArrayList<ApiPar> pars = apiDoc.getApilist().get(modSelectCombo.getSelectionIndex()).getApi()
						.get(interfaceCombo.getSelectionIndex()).getParameters();
				// 此处添加参数保存代码
				statusBar.setText("保存成功，程序关闭前有效");
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

		// menuItemHeader.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// HeaderEdit headerEdit = new HeaderEdit(mainWindowShell, SWT.CLOSE |
		// SWT.SYSTEM_MODAL);
		// headerEdit.open();
		// // LinkedHashMap<String, String> HeaderTemp = headerEdit.open();
		// // if (null != HeaderTemp) {
		// // header = HeaderTemp;
		// // }
		// logger.info("读取到Header:" + header);
		// }
		// });
		//
		// menuItemCookie.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// CookieEdit cookieEdit = new CookieEdit(mainWindowShell, SWT.CLOSE |
		// SWT.SYSTEM_MODAL);
		// cookieEdit.open();
		// }
		// });

		// 清空参数事件
		parsClearButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearParameters();
				try {
					urlText.setText(apiServerAdress + apiDoc.getApilist().get(modSelectCombo.getSelectionIndex())
							.getApi().get(interfaceCombo.getSelectionIndex()).getAddress());
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
				urlText.setText(apiServerAdress + apiDoc.getApilist().get(modSelectCombo.getSelectionIndex()).getApi()
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
				logger.debug("切换表单发送方式为:" + (methodSelectCombo.getSelectionIndex() == 0 ? "GET" : "POST"));
			}
		});

		// 在浏览器中打开的按钮事件
		toBrower.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				HashMap<String, String> pars = getParameters();
				if (apiVersion.equals("3.0")) {
					// 待修正
					// text[5].setText(ApiUtils.SignMessage(pars, merPassWord));
					HashMap<String, String> hashMap2 = getParameters();
					parsText.setText(ParamUtils.mapToQuery(hashMap2));
					String url = urlText.getText();
					Program.launch(url + "?" + ParamUtils.mapToQuery(hashMap2));
					logger.info("浏览器中请求:" + url + "?" + ParamUtils.mapToQuery(hashMap2));
				} else {
					HashMap<String, String> pars1 = getParameters();
					parsText.setText(ParamUtils.mapToQuery(pars1));
					String url = urlText.getText();
					Program.launch(url + (pars1.size() == 0 ? ("") : ("?" + ParamUtils.mapToQuery(pars1))));
					logger.info("浏览器中请求:" + url + (pars1.size() == 0 ? ("") : ("?" + ParamUtils.mapToQuery(pars1))));
				}
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
						JSONObject resultJson = null;
						if (apiVersion.equals("3.0")) {
							resultJson = (JSONObject) (jsonObject.get("results"));
						} else {
							resultJson = jsonObject;
						}
						String returnText = returnCode.getProperty(resultJson.get("code") + "");
						if (returnText == null) {
							logger.warn("返回码是:" + resultJson.get("code") + ",未找到该返回码的描述信息");
							statusBar.setText("返回码是:" + resultJson.get("code") + ",未找到该返回码的描述信息");
						} else {
							logger.debug("返回码是:" + resultJson.get("code") + ":" + returnText);
							statusBar.setText("返回码是:" + resultJson.get("code") + ":" + returnText);
						}
					} catch (Exception e2) {
						logger.error("异常" , e2);
						statusBar.setText("异常:解析API返回码错误");
					}

				} else if (!isJson(text) && isXml(text) && !text.isEmpty()) {
					logger.debug("返回的信息为XML格式，开始尝试解析返回码");
					try {
						Document document = DocumentHelper.parseText(text);
						Element root = document.getRootElement();
						String returnText = returnCode.getProperty(root.element("code").getText());
						if (returnText == null) {
							logger.warn("返回码是:" + root.element("code").getText() + ",未找到该返回码的描述信息");
							statusBar.setText("返回码是:" + root.element("code").getText() + ",未找到该返回码的描述信息");
						} else {
							statusBar.setText("返回码是:" + root.element("code").getText() + ":" + returnText);
						}
					} catch (DocumentException e1) {
						statusBar.setText("异常:解析API返回码错误");
						logger.error("异常" , e1);
					} catch (NullPointerException e2) {
						logger.error("异常" , e2);
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
				HashMap<String, String> pars = getParameters();
				if (apiVersion.equals("3.0") && pars.size() > 0) {
					// 待修正
					// text[11].setText(ApiUtils.SignMessage(pars,
					// merPassWord));
					// logger.info("签名结束，签名值为:" + text[11].getText());
					// pars.put("signmsg", text[11].getText());
				}
				parsText.setText(ParamUtils.mapToQuery(pars));
				String url = urlText.getText();
				method = methodSelectCombo.getSelectionIndex();
				// 避免阻塞UI线程,你懂得
				new Thread() {
					public void run() {
						logger.debug("请求信息:" + url + "?" + ParamUtils.mapToQuery(pars));
						// 标记请求开始时间
						long sumbegintime = System.currentTimeMillis();
						long httpend = System.currentTimeMillis();
						if (method == 0) {
							logger.debug("使用GET方式发起请求");
							RawResponse result;
							try {
								result = ApiUtils.HttpGet(url, pars, header, cookies, StandardCharsets.UTF_8);
								httpend = System.currentTimeMillis();
								httpTime = httpend - sumbegintime;
								apiReturnStr = JsonFormatUtils.Format(result.readToText());
								headerReturnStr = result.getHeaders().toString();
								httpCode = result.getStatusCode();
								logger.info("响应头部:" + result.getHeaders().toString());
							} catch (Exception e) {
								apiReturnStr = "";
								logger.error("异常", e);
								httpend = System.currentTimeMillis();
								httpTime = httpend - sumbegintime;
							}
						}
						if (method == 1) {
							logger.debug("使用POST方式发起请求");
							RawResponse result;
							try {
								result = ApiUtils.HttpPost(url, pars, header, cookies, StandardCharsets.UTF_8);
								httpend = System.currentTimeMillis();
								httpTime = httpend - sumbegintime;
								apiReturnStr = JsonFormatUtils.Format(result.readToText());
								headerReturnStr = result.getHeaders().toString();
								httpCode = result.getStatusCode();
								logger.info("响应头部:" + result.getHeaders().toString());
							} catch (Exception e) {
								apiReturnStr = "";
								headerReturnStr = null;
								logger.error("异常", e);
								httpend = System.currentTimeMillis();
								httpTime = httpend - sumbegintime;
							}
						}
						// 标记请求结束时间
						long sumendtime = System.currentTimeMillis();
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
				String queryString = parsText.getText();
				if (queryString.equals("")) {
					logger.info("参数串为空,停止转换");
					statusBar.setText("参数串为空,停止转换");
					return;
				}
				HashMap<String, String> queryMap = new HashMap<String, String>();
				queryMap = ParamUtils.queryToMap(queryString);
				if (apiVersion.equals("3.0")) {
					initParameters3(covertHashMaptoApiPar(queryMap));
					parsText.setText(queryString);
				} else {
					initParameters4(covertHashMaptoApiPar(queryMap));
					parsText.setText(queryString);
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

		// 状态改变的监听方法
		methodSelectCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				httpMethod = methodSelectCombo.getText();
			}
		});

		// sessionkey锁定按钮
		sessionKeyHoldButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if (sissonKeyLock) {
					sessionKeyHoldButton.setText("锁定sessionkey");
					sissonKeyLock = false;
					// 待修正
					// text[12].setEnabled(true);
					// text[13].setEnabled(true);
					sessionkey = "";
					logger.debug("sessionkey已解锁");
				} else {
					sissonKeyLock = true;
					// text[12].setEnabled(false);
					// text[13].setEnabled(false);
					sessionKeyHoldButton.setText("解锁sessionkey");
					// sessionkey = text[13].getText();
					logger.debug("sessionkey已锁定");
				}
			}
		});
	}

	///////////////////////////////////////////////////////////////////////////
	// 初始化程序
	private void InitSystem() {
		// 加载并初始化参数信息
		this.httpMethod = "GET";
		logger.debug("初始化配置信息-默认表单发送方式为:" + this.httpMethod);
		methodSelectCombo.select((httpMethod.equals("GET") ? 0 : 1));
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
			if (properties.getProperty("apiversion") == null) {
				logger.debug("直接以通用接口方式启动");
			} else {
				// 配置历史记录条数
				if ((null != properties.getProperty("hsitorysum"))
						&& Integer.parseInt(properties.getProperty("hsitorysum")) > 0) {
					this.hsitorysum = Integer.parseInt(properties.getProperty("hsitorysum"));
				}
				if (properties.getProperty("apiversion").equals("3.0")) {
					logger.debug("选择启动API3.0模式");
					this.apiVersion = "3.0";
					this.merID = properties.getProperty("merid");
					this.merPassWord = properties.getProperty("merpassword");
					this.apiReturnCodeFile = properties.getProperty("returncodefile");
					this.apiServerAdress = properties.getProperty("apiaddress");
					this.apiLoadJsonFile = properties.getProperty("apilist");
				} else {
					logger.debug("选择启动通用模式");
					this.apiVersion = "4.0";
					this.apiReturnCodeFile = properties.getProperty("returncodefile");
					this.apiServerAdress = properties.getProperty("apiaddress");
					this.apiLoadJsonFile = properties.getProperty("apilist");
					sessionKeyHoldButton.setEnabled(false);
				}
			}
		} catch (Exception e) {
			statusBar.setText("读取配置失败，请检查");
			logger.warn("读取配置失败，请检查", e);
		}
		// 此处开始加载返回码列表文件
		try {
			if (apiReturnCodeFile == null) {
				logger.debug("没有读到返回码配置,跳过加载");
			} else {
				File reader = new File("./config/" + apiReturnCodeFile);
				logger.debug("加载返回码配置文件" + "./config/" + apiReturnCodeFile);
				returnCode = PropertiesUtils.ReadProperties(reader);
			}
		} catch (Exception e) {
			statusBar.setText("加载返回码配置失败，请检查");
			logger.error("加载返回码配置失败，请检查");
		}

		// 配置文件加载完毕后开始加载API列表
		if (apiLoadJsonFile == null) {
			logger.debug("API列表为空，跳过加载");
		} else {
			InitApiList();
		}
	}

	// 初始化API列表信息
	private void InitApiList() {
		File apilistfile = new File("./config/" + apiLoadJsonFile);
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
		if (null == apiItems) {
			logger.debug("此分类下无接口，跳过加载");
			return;
		}
		for (int i = 0; i < apiItems.size(); i++) {
			interfaceCombo.add(apiItems.get(i).getName());
		}
		try {
			interfaceCombo.select(0);
			urlText.setText(apiServerAdress + apiItems.get(0).getAddress());
			initParameters(apiItems.get(0).getParameters());
		} catch (Exception e) {
			logger.error("异常",e);
			urlText.setText(apiServerAdress);
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

	// 初始化参数
	private void initParameters(ArrayList<ApiPar> pars) {
		if (apiVersion.equals("3.0")) {
			initParameters3(pars);
		} else {
			initParameters4(pars);
		}
	}

	// 参数初始化--针对API3.0特殊处理
	private void initParameters3(ArrayList<ApiPar> pars) {
		clearParameters();
		// 此处需要补全
	}

	// 参数初始化--通用方法
	private void initParameters4(ArrayList<ApiPar> pars) {
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

	// 参数重排算法
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
						}
					}
				}
			}
		}
	}

	// 初始化历史记录
	private void initHistory() {
		history = new ApiList();
		history.setName("历史记录");
		history.setApi(new ArrayList<ApiItem>());
		apiDoc.getApilist().add(history);
	}

	// 触发更新历史记录，只有在点击请求的时候才触发
	private void notifyHistory() {
		ArrayList<ApiItem> historyList = history.getApi();
		if (historyList.size() < this.hsitorysum) {
			ApiItem item = new ApiItem();
			historyList.add(null);
		} else {

		}
	}
}
