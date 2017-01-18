package com.itlaborer.apitools.ui;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.alibaba.fastjson.JSON;
import com.itlaborer.apitools.model.ApiDoc;
import com.itlaborer.apitools.model.ApiItem;
import com.itlaborer.apitools.model.ApiMod;
import com.itlaborer.apitools.model.ApiPar;
import com.itlaborer.apitools.res.KeyCode;
import com.itlaborer.apitools.res.Resource;
import com.itlaborer.apitools.res.XinzhiWeather;
import com.itlaborer.apitools.swt.SWTResourceManager;
import com.itlaborer.apitools.utils.ApiUtils;
import com.itlaborer.apitools.utils.ParamUtils;

import net.dongliu.requests.RawResponse;

/**
 * 程序主界面
 * 
 * @author liudewei[793554262@qq.com]
 * @version 1.8
 * @since 1.0
 */

public class MainWindow {

	private static Logger logger = Logger.getLogger(MainWindow.class.getName());
	// 加载的配置文件项
	private int loadHistorySum;
	private String[] loadServerAdressArray;
	private String[] loadApiJsonFileArray;

	// 其他成员变量-
	private int httpCode, parsSum;
	private long httpTime;
	private Properties properties;
	private String applicationName;
	private String serverAdress;
	private String apiJsonFile;
	private String interfaceContextPath;
	private String bodyReturnStr;
	private String headerReturnStr;
	private boolean keyDownFlag = false;
	private boolean windowFocusFlag = false;
	private boolean openByShortcutFlag = false;
	private ApiDoc apiDoc;
	private ApiMod history;
	protected LinkedHashMap<String, String> header;
	protected LinkedHashMap<String, String> cookies;
	private HashMap<String, ApiItem> tempSavePars;

	// 界面组件
	private final FormToolkit formToolkit;
	protected Shell mainWindowShell;
	private CTabFolder cTabFolder;
	private Button parsCovertButton;
	private Button parsClearButton;
	private Button toBrower;
	private Button responseCheck;
	private Combo methodSelectCombo;
	private Combo modSelectCombo;
	private Combo interfaceCombo;
	private StyledText resultBodyStyledText;
	private StyledText resultHeaderStyledText;
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
	private MenuItem[] menuItem1SubFrozen;
	private MenuItem serverSelect;
	private Menu servers;
	private MenuItem apiSelect;
	private Menu apis;
	private Button btnAuthorization;
	private Listener shortcutListener;
	private Listener shortcutListenerRecover;
	private Display display;

	// 主窗口
	public MainWindow() {
		PropertyConfigurator.configure("config/log4j.properties ");
		logger.info("程序启动, 程序版本为:" + Resource.VERSION);
		this.formToolkit = new FormToolkit(Display.getDefault());
		this.parsSum = 128;
		this.loadHistorySum = 50;
		this.serverAdress = "";
		this.cookies = new LinkedHashMap<String, String>();
		this.header = new LinkedHashMap<String, String>();
		this.tempSavePars = new HashMap<String, ApiItem>();
		this.header.put("User-Agent", "APITools-" + Resource.VERSION);
		this.header.put("SocksTimeout", "30000");
		this.header.put("ConnectTimeout", "30000");
	}

	// 从这里开始，不是么？小桥流水人家~
	public static void main(String[] args) {
		try {
			MainWindow window = new MainWindow();
			window.open(true, false);
		} catch (Exception e) {
			logger.error("异常", e);
		}
	}

	public void open(boolean mainWindowFlag, boolean openByShortcutFlag) {
		this.openByShortcutFlag = openByShortcutFlag;
		display = Display.getDefault();
		createContents(display);
		mainWindowShell.open();
		InitSystem();
		while (!mainWindowShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		logger.info("再见~~~~~");
		display.removeFilter(SWT.KeyDown, shortcutListener);
		display.removeFilter(SWT.KeyUp, shortcutListenerRecover);
		if (mainWindowFlag) {
			System.exit(0);
		}
	}

	protected void createContents(final Display display) {
		applicationName = "APITools" + "-" + Resource.VERSION;
		mainWindowShell = new Shell(display, SWT.MIN);
		mainWindowShell.setSize(1145, 670);
		mainWindowShell.setText(applicationName);
		mainWindowShell.setImage(SWTResourceManager.getImage(MainWindow.class, Resource.IMAGE_ICON));
		ApiUtils.SetCenter(mainWindowShell);
		ApiUtils.DropTargetSupport(mainWindowShell);
		// 菜单////////////////////////////////////////////////////////
		Menu rootMenu = new Menu(mainWindowShell, SWT.BAR);
		mainWindowShell.setMenuBar(rootMenu);

		// 工具菜单///////////////////////////////////////////////////
		/////////////////// 编辑////////////////////////////////////////
		MenuItem menuEdit = new MenuItem(rootMenu, SWT.CASCADE);
		menuEdit.setText("文件");

		Menu menuSave = new Menu(menuEdit);
		menuEdit.setMenu(menuSave);

		MenuItem menuItemSave = new MenuItem(menuSave, SWT.NONE);
		menuItemSave.setText("保存当前接口参数（程序关闭前有效）");

		MenuItem menuItemSaveToFile = new MenuItem(menuSave, SWT.NONE);
		menuItemSaveToFile.setText("保存当前接口参数（保存到接口文档）");

		// 工具菜单///////////////////////////////////////////////////
		MenuItem menuToolKit = new MenuItem(rootMenu, SWT.CASCADE);
		menuToolKit.setText("工具箱");
		// 工具菜单子菜单
		Menu menu = new Menu(menuToolKit);
		menuToolKit.setMenu(menu);
		// 工具-接口列表编辑
		MenuItem menuItemApiListEdit = new MenuItem(menu, SWT.NONE);
		menuItemApiListEdit.setText("接口设计器");
		menuItemApiListEdit.setEnabled(false);
		menuItemApiListEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DesignTools designTools = new DesignTools();
				designTools.open();
			}
		});

		MenuItem menuItemMd5 = new MenuItem(menu, SWT.NONE);
		menuItemMd5.setText("MD5加密");

		// MD5工具
		menuItemMd5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MD5Tools md5Tools = new MD5Tools(mainWindowShell, SWT.CLOSE | SWT.SYSTEM_MODAL);
				md5Tools.open();
			}
		});

		MenuItem menuItemUrl = new MenuItem(menu, SWT.NONE);
		menuItemUrl.setText("URL编码/解码");

		MenuItem menuItemBase64 = new MenuItem(menu, SWT.NONE);
		menuItemBase64.setText("Base64编码/解码");

		// Base64工具
		menuItemBase64.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Base64Tools base64Tools = new Base64Tools(mainWindowShell, SWT.CLOSE | SWT.SYSTEM_MODAL);
				base64Tools.open();
			}
		});

		MenuItem menuItemUnicode = new MenuItem(menu, SWT.NONE);
		menuItemUnicode.setText("Unicode编码/解码");
		// 工具-文件转换
		MenuItem menuItemConvertDoc = new MenuItem(menu, SWT.NONE);
		menuItemConvertDoc.setText("恒生FUNDAPI转换工具");

		// 菜单，转换工具的点击事件
		menuItemConvertDoc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CovertTools tools = new CovertTools(mainWindowShell, SWT.CLOSE | SWT.SYSTEM_MODAL);
				tools.open();
			}
		});

		MenuItem menuPar = new MenuItem(rootMenu, SWT.CASCADE);
		menuPar.setText("Header参数");
		Menu menu_2 = new Menu(menuPar);
		menuPar.setMenu(menu_2);

		MenuItem menuItemHeader = new MenuItem(menu_2, SWT.NONE);
		menuItemHeader.setText("Header");

		MenuItem menuItemCookie = new MenuItem(menu_2, SWT.NONE);
		menuItemCookie.setText("Cookie");

		// API列表
		apiSelect = new MenuItem(rootMenu, SWT.CASCADE);
		apiSelect.setText("接口列表");
		apis = new Menu(apiSelect);
		apiSelect.setMenu(apis);

		// 服务器列表
		serverSelect = new MenuItem(rootMenu, SWT.CASCADE);
		serverSelect.setText("服务器列表");
		servers = new Menu(serverSelect);
		serverSelect.setMenu(servers);

		MenuItem menuItem = new MenuItem(rootMenu, SWT.NONE);
		menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				initNewWindow(false, false);
			}
		});
		menuItem.setText("应用分身");

		/////////////////// 帮助////////////////////////////////////////
		MenuItem menuHelp = new MenuItem(rootMenu, SWT.CASCADE);
		menuHelp.setText("帮助");

		Menu menu_3 = new Menu(menuHelp);
		menuHelp.setMenu(menu_3);

		MenuItem menuItemManual = new MenuItem(menu_3, SWT.NONE);
		menuItemManual.setText("查看手册");

		MenuItem menuItemFeedBack = new MenuItem(menu_3, SWT.NONE);
		menuItemFeedBack.setText("报告问题");
		// 快捷键说明
		MenuItem menuItemShortcutKey = new MenuItem(menu_3, SWT.NONE);
		menuItemShortcutKey.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ShortcutKeyExplain shortcutKeyExplain = new ShortcutKeyExplain(mainWindowShell,
						SWT.CLOSE | SWT.SYSTEM_MODAL);
				shortcutKeyExplain.open();
			}
		});
		menuItemShortcutKey.setText("快捷键");
		// 菜单项-关于
		MenuItem menuItemAbout = new MenuItem(menu_3, SWT.NONE);
		menuItemAbout.setText("关于");
		// 模块选择
		modSelectCombo = new Combo(mainWindowShell, SWT.READ_ONLY);
		modSelectCombo.setBounds(3, 3, 230, 25);
		formToolkit.adapt(modSelectCombo);

		Menu menu_6 = new Menu(modSelectCombo);
		modSelectCombo.setMenu(menu_6);

		MenuItem menuItem_8 = new MenuItem(menu_6, SWT.NONE);
		menuItem_8.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Clipboard clipboard = new Clipboard(modSelectCombo.getDisplay());
				TextTransfer textTransfer = TextTransfer.getInstance();
				if (StringUtils.isNotEmpty(modSelectCombo.getText())) {
					clipboard.setContents(new String[] { modSelectCombo.getText() }, new Transfer[] { textTransfer });
				}
				clipboard.dispose();
				statusBar.setText("已复制到剪切板:" + modSelectCombo.getText());
			}
		});
		menuItem_8.setText("复制模块名");

		MenuItem menuItem_4 = new MenuItem(menu_6, SWT.NONE);
		menuItem_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (StringUtils.isEmpty(modSelectCombo.getText())) {
					statusBar.setText("不能重命名不存在的模块");
					return;
				}
				ReNameModDialog reNameModDialog = new ReNameModDialog(mainWindowShell, SWT.CLOSE | SWT.SYSTEM_MODAL,
						"重命名模块", modSelectCombo.getText());
				String nameFromDialog = reNameModDialog.open();
				if (StringUtils.equals(nameFromDialog, modSelectCombo.getText())) {
					logger.debug("模块名没有发生变化");
				} else if (StringUtils.isEmpty(nameFromDialog)) {
					logger.debug("新模块名为空");
					statusBar.setText("模块名不准为空");
				} else {
					// 重名判断
					boolean flag = false;
					for (int i = 0; i < apiDoc.getItem().size(); i++) {
						if (StringUtils.equals(apiDoc.getItem().get(i).getName(), nameFromDialog)
								&& (i != modSelectCombo.getSelectionIndex())) {
							flag = true;
							break;
						}
					}
					if (flag) {
						logger.debug("重命名模块时发生重名");
						statusBar.setText("重命名时发现重名模块，放弃重命名");
					} else {
						modSelectCombo.setItem(modSelectCombo.getSelectionIndex(), nameFromDialog);
						apiDoc.getItem().get(modSelectCombo.getSelectionIndex()).setName(nameFromDialog);
						ApiUtils.SaveToFile(new File("./config/" + apiJsonFile),
								ApiUtils.jsonFormat(JSON.toJSONString(apiDoc)));
						statusBar.setText("模块名被修改为:" + nameFromDialog);
					}
				}
			}
		});
		menuItem_4.setText("重命名此模块");

		MenuItem menuItem_5 = new MenuItem(menu_6, SWT.NONE);
		menuItem_5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (StringUtils.isEmpty(modSelectCombo.getText())) {
					statusBar.setText("不能删除不存在的模块");
					return;
				}
				MySelectionDialog mySelectionDialog = new MySelectionDialog(mainWindowShell,
						SWT.CLOSE | SWT.SYSTEM_MODAL, "确定要删除此模块吗？删除后此模块下的接口也将删除，并且将无法恢复");
				boolean flag = mySelectionDialog.open();
				if (flag && (modSelectCombo.getSelectionIndex() != -1)) {
					try {
						int modindex = modSelectCombo.getSelectionIndex();
						logger.debug("开始删除模块:" + modSelectCombo.getText());
						// 移除
						apiDoc.getItem().remove(modindex);
						// 保存
						ApiUtils.SaveToFile(new File("./config/" + apiJsonFile),
								ApiUtils.jsonFormat(JSON.toJSONString(apiDoc)));
						// 重新初始化界面
						modSelectCombo.remove(modindex);
						if (modSelectCombo.getItemCount() == 0) {
							logger.debug("模块被删光了");
							mainWindowShell.setText(applicationName);
							clearParameters();
							urlText.setText("");
							interfaceCombo.removeAll();
						}
						// 删除的是最后一个，则初始化倒数第二个
						else if (modindex == modSelectCombo.getItemCount()) {
							modSelectCombo.select(modindex - 1);
							initSelectMod(modindex - 1);
						} else {
							modSelectCombo.select(modindex);
							initSelectMod(modindex);
						}
						logger.debug("删除完成");
						statusBar.setText("删除完成");
					} catch (Exception e2) {
						logger.debug("删除时发生异常", e2);
						statusBar.setText("删除失败");
					}

				} else if (interfaceCombo.getSelectionIndex() == -1) {
					statusBar.setText("没有模块可供删除");
				} else {
					logger.debug("放弃删除模块:" + modSelectCombo.getText());
				}
			}
		});
		menuItem_5.setText("删除此模块");

		MenuItem menuItem_7 = new MenuItem(menu_6, SWT.NONE);
		menuItem_7.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String modname;
				CreateModDialog createModDialog = new CreateModDialog(mainWindowShell, SWT.CLOSE | SWT.SYSTEM_MODAL);
				Object[] res = createModDialog.open();
				if ((boolean) res[0]) {
					modname = (String) res[1];
					if (StringUtils.isEmpty(modname)) {
						logger.debug("模块名为空，放弃添加");
						statusBar.setText("不能创建名字为空的模块");
					} else {
						// 重名判断
						boolean flag = false;
						for (int i = 0; i < apiDoc.getItem().size(); i++) {
							if (StringUtils.equals(apiDoc.getItem().get(i).getName(), modname)
									&& (i != modSelectCombo.getSelectionIndex())) {
								flag = true;
								break;
							}
						}
						if (flag) {
							logger.debug("不能添加重名的模块");
							statusBar.setText("不能添加重名的模块");
						} else {
							modSelectCombo.add(modname);
							ApiMod apiList = new ApiMod();
							apiList.setName(modname);
							apiDoc.getItem().add(apiList);
							try {
								ApiUtils.SaveToFile(new File("./config/" + apiJsonFile),
										ApiUtils.jsonFormat(JSON.toJSONString(apiDoc)));
								modSelectCombo.select(modSelectCombo.getItemCount() - 1);
								initSelectMod(modSelectCombo.getItemCount() - 1);
							} catch (Exception e2) {
								logger.debug("创建新模块写入报错", e2);
							}
						}
					}
				} else {
					logger.debug("放弃新增模块");
				}
			}
		});
		menuItem_7.setText("新增一个模块");
		menuItem_7.setEnabled(false);

		// 接口选择
		interfaceCombo = new Combo(mainWindowShell, SWT.READ_ONLY);
		interfaceCombo.setBounds(237, 3, 245, 25);
		formToolkit.adapt(interfaceCombo);

		Menu menu_4 = new Menu(interfaceCombo);
		interfaceCombo.setMenu(menu_4);

		MenuItem menuItem_9 = new MenuItem(menu_4, SWT.NONE);
		menuItem_9.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Clipboard clipboard = new Clipboard(interfaceCombo.getDisplay());
				TextTransfer textTransfer = TextTransfer.getInstance();
				if (StringUtils.isNotEmpty(interfaceCombo.getText())) {
					clipboard.setContents(new String[] { interfaceCombo.getText() }, new Transfer[] { textTransfer });
				}
				clipboard.dispose();
				statusBar.setText("已复制到剪切板:" + interfaceCombo.getText());
			}
		});
		menuItem_9.setText("复制接口名");

		MenuItem menuItem_1 = new MenuItem(menu_4, SWT.NONE);
		menuItem_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (StringUtils.isEmpty(modSelectCombo.getText())) {
					statusBar.setText("不能重命名不存在的接口");
					return;
				}
				ReNameModDialog reNameModDialog = new ReNameModDialog(mainWindowShell, SWT.CLOSE | SWT.SYSTEM_MODAL,
						"重命名接口", interfaceCombo.getText());
				String nameFromDialog = reNameModDialog.open();
				if (StringUtils.equals(nameFromDialog, interfaceCombo.getText())) {
					logger.debug("接口名没有发生变化");
				} else if (StringUtils.isEmpty(nameFromDialog)) {
					logger.debug("新接口名为空");
					statusBar.setText("接口名不准为空");
				} else {
					// 重名判断
					boolean flag = false;
					ArrayList<ApiItem> items = apiDoc.getItem().get(modSelectCombo.getSelectionIndex()).getItem();
					for (int i = 0; i < items.size(); i++) {
						if (StringUtils.equals(items.get(i).getName(), nameFromDialog)
								&& (i != interfaceCombo.getSelectionIndex())) {
							flag = true;
							break;
						}
					}
					if (flag) {
						logger.debug("重命名接口时发生重名");
						statusBar.setText("重命名时发现重名接口，放弃重命名");
					} else {
						interfaceCombo.setItem(interfaceCombo.getSelectionIndex(), nameFromDialog);
						apiDoc.getItem().get(modSelectCombo.getSelectionIndex()).getItem()
								.get(interfaceCombo.getSelectionIndex()).setName(nameFromDialog);
						ApiUtils.SaveToFile(new File("./config/" + apiJsonFile),
								ApiUtils.jsonFormat(JSON.toJSONString(apiDoc)));
						statusBar.setText("接口名被修改为:" + nameFromDialog);
					}
				}
			}
		});
		menuItem_1.setText("重命名此接口");

		MenuItem menuItem_2 = new MenuItem(menu_4, SWT.NONE);
		menuItem_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (StringUtils.isEmpty(modSelectCombo.getText())) {
					statusBar.setText("不能删除不存在的接口");
					return;
				}
				MySelectionDialog mySelectionDialog = new MySelectionDialog(mainWindowShell,
						SWT.CLOSE | SWT.SYSTEM_MODAL, "确定要删除此接口吗？删除后将无法恢复");
				boolean flag = mySelectionDialog.open();
				if (flag && (interfaceCombo.getSelectionIndex() != -1)) {
					try {
						int modindex = modSelectCombo.getSelectionIndex();
						int interfaceindex = interfaceCombo.getSelectionIndex();
						logger.debug("开始删除接口:" + modSelectCombo.getText() + "模块下的" + interfaceCombo.getText());
						// 移除
						apiDoc.getItem().get(modindex).getItem().remove(interfaceindex);
						// 保存--请注意，保存时会把之前保存到内存中的参数也更新到文档---
						ApiUtils.SaveToFile(new File("./config/" + apiJsonFile),
								ApiUtils.jsonFormat(JSON.toJSONString(apiDoc)));
						// 重新初始化界面
						interfaceCombo.remove(interfaceindex);
						if (interfaceCombo.getItemCount() == 0) {
							logger.debug("此模块下的接口被删光了");
							mainWindowShell.setText(applicationName);
							clearParameters();
							urlText.setText("");
						}
						// 删除的是最后一个，则初始化倒数第二个
						else if (interfaceindex == interfaceCombo.getItemCount()) {
							interfaceCombo.select(interfaceindex - 1);
							initSelectInterface(modindex, interfaceindex - 1);
						} else {
							interfaceCombo.select(interfaceindex);
							initSelectInterface(modindex, interfaceindex);
						}
						logger.debug("删除完成");
						statusBar.setText("删除完成");
					} catch (Exception e2) {
						logger.debug("删除时发生异常", e2);
						statusBar.setText("删除失败");
					}

				} else if (interfaceCombo.getSelectionIndex() == -1) {
					statusBar.setText("没有接口可供删除");
				} else {
					logger.debug("放弃删除接口:" + modSelectCombo.getText() + "模块下的" + interfaceCombo.getText());
				}
			}
		});
		menuItem_2.setText("删除此接口");

		MenuItem menuItem_6 = new MenuItem(menu_4, SWT.NONE);
		menuItem_6.setEnabled(false);
		menuItem_6.setText("新增一个接口");
		// 表单
		parsText = new Text(mainWindowShell, SWT.BORDER);
		parsText.setBounds(3, 32, 480, 25);
		// URL
		urlText = new Text(mainWindowShell, SWT.BORDER);
		urlText.setBounds(487, 3, 478, 25);
		// HTTP请求的方法下拉选择框
		methodSelectCombo = new Combo(mainWindowShell, SWT.DROP_DOWN | SWT.READ_ONLY);
		methodSelectCombo.setBounds(970, 3, 66, 25);
		formToolkit.adapt(methodSelectCombo);
		methodSelectCombo.add("GET", 0);
		methodSelectCombo.add("POST", 1);
		methodSelectCombo.add("HEAD", 2);
		methodSelectCombo.add("PUT", 3);
		methodSelectCombo.add("PATCH", 4);
		methodSelectCombo.add("DELETE", 5);

		// 提交按钮
		submitButton = new Button(mainWindowShell, SWT.NONE);
		submitButton.setBounds(1040, 2, 97, 27);
		submitButton.setText("提      交");

		// 参数转换
		parsCovertButton = new Button(mainWindowShell, SWT.NONE);
		parsCovertButton.setToolTipText("导入形如a=1&&b=2的参数串到表单");
		parsCovertButton.setText("导入参数");
		parsCovertButton.setBounds(487, 31, 72, 27);
		formToolkit.adapt(parsCovertButton, true, true);

		Menu menu_5 = new Menu(parsCovertButton);
		parsCovertButton.setMenu(menu_5);

		MenuItem menuItem_3 = new MenuItem(menu_5, SWT.NONE);
		menuItem_3.setText("切换分隔符");

		// 重置参数
		parsClearButton = new Button(mainWindowShell, SWT.NONE);
		parsClearButton.setToolTipText("重置参数为接口文档中定义的参数");
		parsClearButton.setText("重置参数");
		parsClearButton.setBounds(562, 31, 72, 27);
		formToolkit.adapt(parsClearButton, true, true);

		// 排除空格
		clearSpaceButton = new Button(mainWindowShell, SWT.NONE);
		clearSpaceButton.setToolTipText("清除参数两头可能存在的空格");
		clearSpaceButton.setText("TRIM参数");
		clearSpaceButton.setBounds(637, 31, 72, 27);
		formToolkit.adapt(clearSpaceButton, true, true);

		// 重排参数
		button = new Button(mainWindowShell, SWT.NONE);
		button.setToolTipText("将参数重新从第一个表格重排列");
		button.setText("重排参数");
		button.setBounds(712, 31, 72, 27);
		formToolkit.adapt(button, true, true);

		// auth
		btnAuthorization = new Button(mainWindowShell, SWT.NONE);
		btnAuthorization.setToolTipText("授权管理");
		btnAuthorization.setText("Authorization");
		btnAuthorization.setBounds(787, 31, 92, 27);
		formToolkit.adapt(btnAuthorization, true, true);
		btnAuthorization.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				statusBar.setText("此功能暂未实现");
			}
		});

		// 结果校验器
		responseCheck = new Button(mainWindowShell, SWT.NONE);
		responseCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				statusBar.setText("此功能暂未实现");
			}
		});
		responseCheck.setToolTipText("校验Response");
		responseCheck.setText("结果校验器");
		responseCheck.setBounds(882, 31, 83, 27);
		formToolkit.adapt(responseCheck, true, true);
		// 点击清除结果
		textClearButton = new Button(mainWindowShell, SWT.NONE);
		textClearButton.setToolTipText("清空结果内容");
		textClearButton.setText("清空结果");
		textClearButton.setBounds(968, 31, 69, 27);
		formToolkit.adapt(textClearButton, true, true);
		// 去浏览器
		toBrower = new Button(mainWindowShell, SWT.NONE);
		toBrower.setToolTipText("用HTTP GET方式在浏览器中请求接口");
		toBrower.setText("浏览器中打开");
		toBrower.setBounds(1040, 31, 97, 27);
		formToolkit.adapt(toBrower, true, true);

		// 参数table
		formTable = new Table(mainWindowShell, SWT.BORDER | SWT.HIDE_SELECTION);
		formTable.setBounds(3, 62, 480, 530);
		formTable.setItemCount(parsSum);
		formTable.setHeaderVisible(true);
		formTable.setLinesVisible(true);

		// 表列
		TableColumn tblclmnNewColumn = new TableColumn(formTable, SWT.BORDER);
		tblclmnNewColumn.setWidth(38);
		tblclmnNewColumn.setResizable(false);
		tblclmnNewColumn.setText("编号");

		TableColumn tblclmnNewColumn2 = new TableColumn(formTable, SWT.BORDER);
		tblclmnNewColumn2.setWidth((int) ((formTable.getBounds().width - tblclmnNewColumn.getWidth()
				- formTable.getVerticalBar().getSize().x - 4) * 0.24));
		tblclmnNewColumn2.setResizable(false);
		tblclmnNewColumn2.setText("备注");

		TableColumn nameColumn = new TableColumn(formTable, SWT.BORDER);
		nameColumn.setWidth((int) ((formTable.getBounds().width - tblclmnNewColumn.getWidth()
				- formTable.getVerticalBar().getSize().x - 4) * 0.38));
		nameColumn.setText("参数名");
		nameColumn.setResizable(false);

		TableColumn valueColumn_1 = new TableColumn(formTable, SWT.BORDER);
		valueColumn_1.setWidth((int) ((formTable.getBounds().width - tblclmnNewColumn.getWidth()
				- formTable.getVerticalBar().getSize().x - 4) * 0.38));
		valueColumn_1.setText("参数值");
		valueColumn_1.setResizable(false);

		// 将Label和Text绑定到table
		label = new Label[parsSum];
		form = new Text[parsSum][3];
		menuItem1SubFrozen = new MenuItem[parsSum];
		TableItem[] items = formTable.getItems();
		for (int i = 0; i < parsSum; i++) {
			final int b = i;
			//////////////////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////////////////
			// 第一列--编号和操作列
			TableEditor editor0 = new TableEditor(formTable);
			label[i] = new Label(formTable, SWT.NONE | SWT.CENTER);
			label[i].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
			label[i].setText(new DecimalFormat("000").format(i + 1));
			editor0.grabHorizontal = true;
			editor0.setEditor(label[i], items[i], 0);

			Menu frozenPar = new Menu(label[i]);
			label[i].setMenu(frozenPar);
			menuItem1SubFrozen[i] = (MenuItem) new MenuItem(frozenPar, SWT.NONE);
			menuItem1SubFrozen[i].setText("冻结此参数");

			////////////////////////////////////////////////////////////////////////////////////////
			MenuItem mntmKey = new MenuItem(frozenPar, SWT.CASCADE);
			mntmKey.setText("参数名操作");

			Menu menuKey = new Menu(mntmKey);
			mntmKey.setMenu(menuKey);

			MenuItem menuItemKeyUrlEncode = new MenuItem(menuKey, SWT.NONE);
			menuItemKeyUrlEncode.setText("UrlEncode");
			menuItemKeyUrlEncode.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (StringUtils.isNotEmpty(form[b][1].getText())) {
						try {
							form[b][1].setText(URLEncoder.encode(form[b][1].getText(), "UTF-8"));
						} catch (UnsupportedEncodingException e1) {
							logger.debug("转码异常", e1);
						}
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

			MenuItem menuItemKeyUrlDecode = new MenuItem(menuKey, SWT.NONE);
			menuItemKeyUrlDecode.setText("UrlDecode");
			menuItemKeyUrlDecode.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (StringUtils.isNotEmpty(form[b][1].getText())) {
						try {
							form[b][1].setText(URLDecoder.decode(form[b][1].getText(), "UTF-8"));
						} catch (UnsupportedEncodingException e1) {
							logger.debug("解码异常", e1);
						}
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

			MenuItem menuItemKeyBase64 = new MenuItem(menuKey, SWT.NONE);
			menuItemKeyBase64.setText("Base64转码");
			menuItemKeyBase64.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (StringUtils.isNotEmpty(form[b][1].getText())) {
						form[b][1].setText(ApiUtils.base64EncodeString(form[b][1].getText()));
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

			MenuItem menuItemKeyBase64D = new MenuItem(menuKey, SWT.NONE);
			menuItemKeyBase64D.setText("Base64解码");
			menuItemKeyBase64D.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (StringUtils.isNotEmpty(form[b][1].getText())) {
						form[b][1].setText(ApiUtils.base64DecodeString(form[b][1].getText()));
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

			MenuItem menuItemKeyMD5Cap = new MenuItem(menuKey, SWT.NONE);
			menuItemKeyMD5Cap.setText("MD5大写");
			menuItemKeyMD5Cap.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (StringUtils.isNotEmpty(form[b][1].getText())) {
						form[b][1].setText(ApiUtils.MD5(form[b][1].getText()));
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

			MenuItem menuItemKeyMD5Low = new MenuItem(menuKey, SWT.NONE);
			menuItemKeyMD5Low.setText("MD5小写");
			menuItemKeyMD5Low.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (StringUtils.isNotEmpty(form[b][1].getText())) {
						form[b][1].setText(ApiUtils.MD5(form[b][1].getText()).toLowerCase());
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

			MenuItem menuItemKeyTrim = new MenuItem(menuKey, SWT.NONE);
			menuItemKeyTrim.setText("TRIM");
			menuItemKeyTrim.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (StringUtils.isNotEmpty(form[b][1].getText())) {
						form[b][1].setText(form[b][1].getText().trim());
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
			///////////////////////////////////////////////////////////////////////////////////
			MenuItem mntmValue = new MenuItem(frozenPar, SWT.CASCADE);
			mntmValue.setText("参数值操作");

			Menu menuValue = new Menu(mntmValue);
			mntmValue.setMenu(menuValue);

			MenuItem menuItemValueUrlEncode = new MenuItem(menuValue, SWT.NONE);
			menuItemValueUrlEncode.setText("UrlEncode");
			menuItemValueUrlEncode.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (StringUtils.isNotEmpty(form[b][2].getText())) {
						try {
							form[b][2].setText(URLEncoder.encode(form[b][2].getText(), "UTF-8"));
						} catch (UnsupportedEncodingException e1) {
							logger.debug("转码异常", e1);
						}
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

			MenuItem menuItemValueUrlDecode = new MenuItem(menuValue, SWT.NONE);
			menuItemValueUrlDecode.setText("UrlDecode");
			menuItemValueUrlDecode.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (StringUtils.isNotEmpty(form[b][2].getText())) {
						try {
							form[b][2].setText(URLDecoder.decode(form[b][2].getText(), "UTF-8"));
						} catch (UnsupportedEncodingException e1) {
							logger.debug("解码异常", e1);
						}
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

			MenuItem menuItemValueBase64 = new MenuItem(menuValue, SWT.NONE);
			menuItemValueBase64.setText("Base64转码");
			menuItemValueBase64.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (StringUtils.isNotEmpty(form[b][2].getText())) {
						form[b][2].setText(ApiUtils.base64EncodeString(form[b][2].getText()));
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

			MenuItem menuItemValueBase64D = new MenuItem(menuValue, SWT.NONE);
			menuItemValueBase64D.setText("Base64解码");
			menuItemValueBase64D.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (StringUtils.isNotEmpty(form[b][2].getText())) {
						form[b][2].setText(ApiUtils.base64DecodeString(form[b][2].getText()));
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

			MenuItem menuItemValueMD5Cap = new MenuItem(menuValue, SWT.NONE);
			menuItemValueMD5Cap.setText("MD5大写");
			menuItemValueMD5Cap.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (StringUtils.isNotEmpty(form[b][2].getText())) {
						form[b][2].setText(ApiUtils.MD5(form[b][2].getText()));
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

			MenuItem menuItemValueMD5Low = new MenuItem(menuValue, SWT.NONE);
			menuItemValueMD5Low.setText("MD5小写");
			menuItemValueMD5Low.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (StringUtils.isNotEmpty(form[b][2].getText())) {
						form[b][2].setText(ApiUtils.MD5(form[b][2].getText()).toLowerCase());
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

			MenuItem menuItemValueTrim = new MenuItem(menuValue, SWT.NONE);
			menuItemValueTrim.setText("TRIM");
			menuItemValueTrim.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (StringUtils.isNotEmpty(form[b][2].getText())) {
						form[b][2].setText(form[b][2].getText().trim());
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
			//////////////////////////////////////////////////////////////////////////
			// 参数冻结解冻事件
			menuItem1SubFrozen[i].addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (StringUtils.isEmpty(form[b][1].getText())) {
						statusBar.setText("空的输入框，放弃冻结");
					} else {
						if (StringUtils.equals(menuItem1SubFrozen[b].getText(), "冻结此参数")) {
							// 冻结标志
							label[b].setToolTipText("此参数已冻结,冻结后不再发送此参数");
							// label[b].setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
							form[b][0].setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
							form[b][1].setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
							form[b][2].setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
							menuItem1SubFrozen[b].setText("解冻此参数");
							statusBar.setText("冻结参数完毕");
						} else if (StringUtils.equals(menuItem1SubFrozen[b].getText(), "解冻此参数")) {
							// 解冻标志
							label[b].setToolTipText("");
							// label[b].setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
							form[b][0].setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
							form[b][1].setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
							form[b][2].setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
							menuItem1SubFrozen[b].setText("冻结此参数");
							statusBar.setText("解冻参数完毕");
						}
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {

				}
			});

			// 鼠标事件
			label[i].addMouseTrackListener(new MouseTrackListener() {
				@Override
				public void mouseHover(MouseEvent e) {
				}

				@Override
				public void mouseExit(MouseEvent e) {
					label[b].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
					form[b][0].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
					form[b][1].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
					form[b][2].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
				}

				@Override
				public void mouseEnter(MouseEvent e) {
					label[b].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
					form[b][0].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
					form[b][1].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
					form[b][2].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
				}
			});
			//////////////////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////////////////
			// 第二列
			TableEditor editor1 = new TableEditor(formTable);
			form[i][0] = new Text(formTable, SWT.NONE);
			editor1.grabHorizontal = true;
			editor1.setEditor(form[i][0], items[i], 1);
			// 第三列
			TableEditor editor2 = new TableEditor(formTable);
			form[i][1] = new Text(formTable, SWT.NONE);
			editor2.grabHorizontal = true;
			editor2.setEditor(form[i][1], items[i], 2);

			// 第四列
			TableEditor editor3 = new TableEditor(formTable);
			form[i][2] = new Text(formTable, SWT.NONE);
			editor3.grabHorizontal = true;
			editor3.setEditor(form[i][2], items[i], 3);
			// 设置焦点变色--鼠标和焦点
			form[i][0].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(FocusEvent e) {
					label[b].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
					form[b][0].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
					form[b][1].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
					form[b][2].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
				}

				@Override
				public void focusGained(FocusEvent e) {
					label[b].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
					form[b][0].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
					form[b][1].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
					form[b][2].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
				}
			});
			form[i][1].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(FocusEvent e) {
					label[b].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
					form[b][0].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
					form[b][1].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
					form[b][2].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
				}

				@Override
				public void focusGained(FocusEvent e) {
					label[b].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
					form[b][0].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
					form[b][1].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
					form[b][2].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
				}
			});
			form[i][2].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(FocusEvent e) {
					label[b].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
					form[b][0].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
					form[b][1].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
					form[b][2].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
				}

				@Override
				public void focusGained(FocusEvent e) {
					label[b].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
					form[b][0].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
					form[b][1].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
					form[b][2].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
				}
			});
			// 鼠标监听
			form[i][0].addMouseTrackListener(new MouseTrackListener() {

				@Override
				public void mouseHover(MouseEvent e) {
				}

				@Override
				public void mouseExit(MouseEvent e) {
					label[b].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
					form[b][0].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
					form[b][1].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
					form[b][2].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
				}

				@Override
				public void mouseEnter(MouseEvent e) {
					label[b].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
					form[b][0].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
					form[b][1].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
					form[b][2].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
				}
			});

			form[i][1].addMouseTrackListener(new MouseTrackListener() {

				@Override
				public void mouseHover(MouseEvent e) {
				}

				@Override
				public void mouseExit(MouseEvent e) {
					label[b].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
					form[b][0].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
					form[b][1].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
					form[b][2].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
				}

				@Override
				public void mouseEnter(MouseEvent e) {
					label[b].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
					form[b][0].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
					form[b][1].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
					form[b][2].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
				}
			});
			form[i][2].addMouseTrackListener(new MouseTrackListener() {

				@Override
				public void mouseHover(MouseEvent e) {
				}

				@Override
				public void mouseExit(MouseEvent e) {
					label[b].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
					form[b][0].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
					form[b][1].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
					form[b][2].setBackground(new Color(Display.getCurrent(), 255, 255, 255));
				}

				@Override
				public void mouseEnter(MouseEvent e) {
					label[b].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
					form[b][0].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
					form[b][1].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
					form[b][2].setBackground(new Color(Display.getCurrent(), 227, 247, 255));
				}
			});
		}

		// 接口返回内容显示区域
		cTabFolder = new CTabFolder(mainWindowShell, SWT.BORDER);
		cTabFolder.setBounds(487, 62, 649, 530);
		cTabFolder.setSelectionBackground(
				Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		cTabFolder.forceFocus();
		CTabItem tabItem = new CTabItem(cTabFolder, SWT.BORDER);
		tabItem.setText(" 响应内容  ");
		CTabItem tabItem2 = new CTabItem(cTabFolder, SWT.BORDER);
		tabItem2.setText(" 响应头部 ");

		resultBodyStyledText = new StyledText(cTabFolder, SWT.NONE | SWT.V_SCROLL | SWT.H_SCROLL);
		tabItem.setControl(resultBodyStyledText);
		resultBodyStyledText.setAlwaysShowScrollBars(true);
		formToolkit.adapt(resultBodyStyledText);
		StyledTextAddContextMenu(resultBodyStyledText);

		resultHeaderStyledText = new StyledText(cTabFolder, SWT.NONE | SWT.V_SCROLL | SWT.H_SCROLL);
		tabItem2.setControl(resultHeaderStyledText);
		resultHeaderStyledText.setAlwaysShowScrollBars(true);
		formToolkit.adapt(resultHeaderStyledText);
		StyledTextAddContextMenu(resultHeaderStyledText);

		// 状态栏
		statusBar = new Text(mainWindowShell, SWT.BORDER);
		statusBar.setBounds(3, 595, 1134, 25);
		formToolkit.adapt(statusBar, true, true);

		// 各个组件的监听事件//////////////////////////////////////////////////////////////////////////////////////////
		// 全局快捷键--要注意阻止快捷键重复执行
		// 按键按下时执行快捷键操作
		shortcutListener = new Listener() {
			public void handleEvent(Event e) {
				// 只有窗口是激活状态，并且按键是第一次按下时才执行快捷键操作，避免重复不停的执行
				if ((openByShortcutFlag == false) && (windowFocusFlag == true) && (keyDownFlag == false)) {
					// Ctrl+n开启新的窗口
					if ((e.stateMask == SWT.CTRL) && (e.keyCode == KeyCode.KEY_N)) {
						keyDownFlag = true;
						initNewWindow(false, true);
					}
					// Ctrl+Q执行提交
					if ((e.stateMask == SWT.CTRL) && (e.keyCode == KeyCode.KEY_Q)) {
						keyDownFlag = true;
						sentRequest();
					}
					// Ctrl+Enter执行提交
					if ((e.stateMask == SWT.CTRL)
							&& ((e.keyCode == KeyCode.ENTER) || (e.keyCode == KeyCode.SMALL_KEY_BOARD_ENTER))) {
						keyDownFlag = true;
						sentRequest();
					}
					// Ctrl+l清空结果
					if ((e.stateMask == SWT.CTRL) && (e.keyCode == KeyCode.KEY_L)) {
						keyDownFlag = true;
						clearResult();
					}
					// Ctrl+s临时保存
					if ((e.stateMask == SWT.CTRL) && (e.keyCode == KeyCode.KEY_S)) {
						keyDownFlag = true;
						SavePars2Memory();
					}
				}
			}
		};
		// 按键释放时标记
		shortcutListenerRecover = new Listener() {
			@Override
			public void handleEvent(Event event) {
				openByShortcutFlag = false;
				keyDownFlag = false;
			}
		};
		// 窗口活动标志
		mainWindowShell.addListener(SWT.Activate, new Listener() {
			@Override
			public void handleEvent(Event event) {
				windowFocusFlag = true;
				logger.debug(mainWindowShell.hashCode() + "窗口获得焦点");
			}
		});
		mainWindowShell.addListener(SWT.Deactivate, new Listener() {
			@Override
			public void handleEvent(Event event) {
				windowFocusFlag = false;
				logger.debug(mainWindowShell.hashCode() + "窗口失去焦点");
			}
		});
		display.addFilter(SWT.KeyDown, shortcutListener);
		display.addFilter(SWT.KeyUp, shortcutListenerRecover);

		// 保存事件
		menuItemSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SavePars2Memory();
			}
		});
		// 保存事件
		menuItemSaveToFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SavePars2Memory();
				SavePars2File();
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

		// Unicode工具
		menuItemUnicode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				UnicodeTools unicodeTools = new UnicodeTools(mainWindowShell, SWT.CLOSE | SWT.SYSTEM_MODAL);
				unicodeTools.open();
			}
		});

		// 手册
		menuItemManual.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Program.launch(Resource.MANUAL);
			}
		});

		// 问题反馈
		menuItemFeedBack.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Program.launch(Resource.FEEDBACK);
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
				// 在别的ToolTipText更新时，鼠标点击所在的Button的ToolTipText会不停地闪烁，需要纠正
				parsClearButton.setToolTipText(null);
				try {
					urlText.setText(serverAdress + apiDoc.getItem().get(modSelectCombo.getSelectionIndex()).getItem()
							.get(interfaceCombo.getSelectionIndex()).getPath());
					initParameters(apiDoc.getItem().get(modSelectCombo.getSelectionIndex()).getItem()
							.get(interfaceCombo.getSelectionIndex()).getParameters());
					// 重置参数时，删除内存中临时保存的数据
					tempSavePars.remove(apiDoc.getItem().get(modSelectCombo.getSelectionIndex()).getItem()
							.get(interfaceCombo.getSelectionIndex()).getUuid());
				} catch (Exception e2) {
					logger.error("当前选择的接口并不包含参数信息，无法完成重新初始化，默认留空");
				}
			}
		});
		parsClearButton.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseExit(MouseEvent e) {
				parsClearButton.setToolTipText("重置参数为接口文档中定义的参数");
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
					form[i][2].setText(form[i][2].getText().trim());
				}
				logger.debug("寻找参数里的多余的空格完毕");
			}
		});

		// 分组选择事件
		modSelectCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearParameters();
				initSelectMod(modSelectCombo.getSelectionIndex());
				logger.debug("切换到分组:" + apiDoc.getItem().get(modSelectCombo.getSelectionIndex()).getName());
			}
		});

		// 接口选择事件
		interfaceCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				initSelectInterface(modSelectCombo.getSelectionIndex(), interfaceCombo.getSelectionIndex());
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

		// 此为为提交按钮添加点击事件
		submitButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sentRequest();
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
				clearResult();
			}
		});
	}

	// 提交请求
	private void sentRequest() {
		if (StringUtils.isEmpty(urlText.getText())) {
			statusBar.setText("地址为空");
			return;
		}
		resultBodyStyledText.setText("");
		resultHeaderStyledText.setText("");
		statusBar.setText("请求中······");
		final HashMap<String, String> pars = getParameters();
		parsText.setText(ParamUtils.mapToQuery(pars));
		final String url = urlText.getText();
		final String method = methodSelectCombo.getText();
		// 通知更新历史
		notifyHistory();
		Thread httpThread = new Thread() {
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
					} catch (Exception e) {
						logger.error("异常", e);
					}
					break;
				case "POST":
					logger.debug("使用POST方式发起请求");
					try {
						result = ApiUtils.HttpPost(url, pars, header, cookies, StandardCharsets.UTF_8);
					} catch (Exception e) {
						logger.error("异常", e);
					}
					break;
				case "HEAD":
					logger.debug("使用HEAD方式发起请求");
					try {
						result = ApiUtils.HttpHead(url, pars, header, cookies, StandardCharsets.UTF_8);
					} catch (Exception e) {
						logger.error("异常", e);
					}
					break;
				case "PUT":
					logger.debug("使用PUT方式发起请求");
					try {
						result = ApiUtils.HttpPost(url, pars, header, cookies, StandardCharsets.UTF_8);
					} catch (Exception e) {
						logger.error("异常", e);
					}
					break;
				case "PATCH":
					logger.debug("使用PATCH方式发起请求");
					try {
						result = ApiUtils.HttpPatch(url, pars, header, cookies, StandardCharsets.UTF_8);
					} catch (Exception e) {
						logger.error("异常", e);
					}
					break;
				case "DELETE":
					logger.debug("使用DELETE方式发起请求");
					try {
						result = ApiUtils.HttpDelete(url, pars, header, cookies, StandardCharsets.UTF_8);
					} catch (Exception e) {
						logger.error("异常", e);
					}
					break;
				default:
					logger.debug("HTTP请求时未找到可用的方法");
					break;
				}
				httpend = System.currentTimeMillis();
				httpTime = httpend - sumbegintime;
				bodyReturnStr = "";
				bodyReturnStr = ApiUtils.jsonFormat(result.readToText() + "");
				headerReturnStr = "";
				List<Entry<String, String>> header = result.getHeaders();
				for (int i = 0; i < header.size(); i++) {
					headerReturnStr += header.get(i).getKey() + ":" + header.get(i).getValue() + "\n";
				}
				httpCode = result.getStatusCode();
				logger.info("响应头部:" + result.getHeaders().toString());
				// 标记请求结束时间
				final long sumendtime = System.currentTimeMillis();
				display.syncExec(new Thread() {
					public void run() {
						resultBodyStyledText.setText(bodyReturnStr);
						resultHeaderStyledText.setText(headerReturnStr);
						RenderingColor(resultBodyStyledText);
						statusBar.setText("请求结束/HTTP状态码:" + httpCode + "/HTTP请求耗时:" + httpTime + "ms" + "/总耗时:"
								+ (sumendtime - sumbegintime) + "ms");
					}
				});
			}
		};
		httpThread.setName("httpRequest");
		httpThread.start();
	}

	// 保存参数到内存
	private void SavePars2Memory() {
		logger.debug("调用了临时保存参数");
		if (modSelectCombo.getSelectionIndex() == -1 | interfaceCombo.getSelectionIndex() == -1) {
			statusBar.setText("保存失败：只允许在现有接口上保存已填写的参数");
			return;
		}
		// 获取当前文档节点
		ApiItem item = tempSavePars.get(apiDoc.getItem().get(modSelectCombo.getSelectionIndex()).getItem()
				.get(interfaceCombo.getSelectionIndex()).getUuid());
		if (null == item) {
			item = new ApiItem();
		}
		item.setUuid(apiDoc.getItem().get(modSelectCombo.getSelectionIndex()).getItem()
				.get(interfaceCombo.getSelectionIndex()).getUuid());
		item.setName(interfaceCombo.getText());
		item.setDescription(interfaceCombo.getToolTipText());
		item.setPath(urlText.getText().replace(serverAdress, ""));
		item.setMethod(methodSelectCombo.getText());
		item.setParameters(new ArrayList<ApiPar>());
		// 从form框初始化
		for (int i = 0; i < form.length; i++) {
			// 判断参数名和参数值，不判断备注
			if (StringUtils.isNotEmpty(form[i][1].getText()) || StringUtils.isNotEmpty(form[i][2].getText())) {
				item.getParameters().add(
						new ApiPar(form[i][1].getText() + "", form[i][0].getText() + "", form[i][2].getText() + ""));
			}
		}
		// 做个标识临时存起来
		tempSavePars.put(apiDoc.getItem().get(modSelectCombo.getSelectionIndex()).getItem()
				.get(interfaceCombo.getSelectionIndex()).getUuid(), item);
		statusBar.setText("保存成功，程序关闭前有效");
	}

	// 保存参数到文件
	// 新读取一份文档保存，因为内存中的那份可能在其他接口处做了临时保存
	private void SavePars2File() {
		logger.debug("调用了永久保存参数");
		if (modSelectCombo.getSelectionIndex() == -1 | interfaceCombo.getSelectionIndex() == -1) {
			return;
		}
		try {
			// 将临时区的内容拷贝到加载的文档里
			apiDoc.getItem().get(modSelectCombo.getSelectionIndex()).getItem().set(interfaceCombo.getSelectionIndex(),
					tempSavePars.get(apiDoc.getItem().get(modSelectCombo.getSelectionIndex()).getItem()
							.get(interfaceCombo.getSelectionIndex()).getUuid()));
			// 保存到文件--潜在的风险，保存时间过长程序界面卡死
			if (ApiUtils.SaveToFile(new File("./config/" + apiJsonFile),
					ApiUtils.jsonFormat(JSON.toJSONString(apiDoc)))) {
				statusBar.setText("保存成功，已写入接口配置文件");
			} else {
				statusBar.setText("保存失败，请重试");
			}
		} catch (Exception e) {
			logger.error("异常:" + e);
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
				ApiUtils.SaveToFile(configFile, Resource.CONFIG);
				logger.warn("警告:参数配置文件丢失，已创建默认配置");
				// 当创建默认配置文档的时候也生成个默认的接口列表--心知天气
				/////////////////////////// 示例接口//////////////////////////////////////
				ApiUtils.SaveToFile(new File("./config/api-xinzhiweather.json"),
						ApiUtils.jsonFormat(JSON.toJSONString(new XinzhiWeather().getApidoc())));
			} catch (Exception e) {
				logger.warn("异常", e);
			}
		}
		if (!log4jFile.exists()) {
			try {
				ApiUtils.SaveToFile(log4jFile, Resource.LOG4J);
				logger.warn("警告:日志配置文件丢失，已创建默认配置");
			} catch (Exception e) {
				logger.warn("异常", e);
			}
		}
		// 此处开始加载配置文件内容
		try {
			// 加载配置
			properties = ApiUtils.ReadProperties(configFile);
			// 加载API列表
			loadApiJsonFileArray = properties.getProperty("apilist").split("\\|");
			if (null != loadApiJsonFileArray && loadApiJsonFileArray.length > 0) {
				if (StringUtils.isNotEmpty(loadApiJsonFileArray[0])) {
					this.apiJsonFile = loadApiJsonFileArray[0];
					// 初始化API下拉选择框
					for (int i = 0; i < loadApiJsonFileArray.length; i++) {
						final MenuItem apiItem = new MenuItem(apis, SWT.NONE);
						apiItem.setText(loadApiJsonFileArray[i]);
						if (i == 0) {
							apiItem.setImage(SWTResourceManager.getImage(MainWindow.class, Resource.IMAGE_CHECKED));
						}
						apiItem.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								// 设置焦点
								for (int i = 0; i < apis.getItemCount(); i++) {
									apis.getItem(i).setImage(null);
								}
								apiItem.setImage(SWTResourceManager.getImage(MainWindow.class, Resource.IMAGE_CHECKED));
								apiJsonFile = apiItem.getText();
								InitApiList();
							}
						});
					}
				} else {
					this.apiJsonFile = null;
				}
			}
		} catch (Exception e) {
			statusBar.setText("读取配置失败，请检查");
			logger.warn("读取配置失败，请检查", e);
		}
		// 配置文件加载完毕后开始加载API列表
		if (null == apiJsonFile) {
			logger.debug("API列表为空，跳过加载");
		} else {
			InitApiList();
		}
	}

	// 初始化API列表信息
	private void InitApiList() {
		File apilistfile = new File("./config/" + apiJsonFile);
		if (!apilistfile.exists()) {
			logger.warn("警告:您加载的API文档不存在，程序将跳过加载API列表，请检查配置");
			return;
		}
		try {
			apiDoc = JSON.parseObject(ApiUtils.ReadFromFile(apilistfile, "UTF-8"), ApiDoc.class);
			// 检查接口文档是老版本的文档没有，需要补正
			// 加载前判断版本
			if (apiDoc.getDecodeversion().equals(1.1)) {
				logger.debug("加载的api版本为" + apiDoc.getVersion());
				// 初始化历史记录
				initServerList(apiDoc.getServerlist());
				initHistory();
				initMod();
			} else {
				logger.warn("警告:您加载的API列表可能是老版本的，请重新生成列表配置");
				statusBar.setText("警告:您加载的API列表可能是老版本的，请重新生成列表配置");
			}
		} catch (Exception e) {
			logger.error("异常:", e);
			statusBar.setText("您加载的接口文件有误，请核对");
		}
	}

	// 地址列表加载方法
	private void initServerList(String serverlist) {
		// 移除之前的数据
		for (MenuItem menuItem : servers.getItems()) {
			menuItem.dispose();
		}
		// 加载地址列表
		if (null == serverlist) {
			logger.info("没有读取到服务器信息，调过加载");
			return;
		}
		logger.info("加载到的服务器列表:" + serverlist);
		loadServerAdressArray = serverlist.split("\\|");
		if (null != loadServerAdressArray && loadServerAdressArray.length > 0) {
			if (StringUtils.isNotEmpty(loadServerAdressArray[0])) {
				this.serverAdress = loadServerAdressArray[0];
				// 初始化服务器下拉选择框
				for (int i = 0; i < loadServerAdressArray.length; i++) {
					final MenuItem serverItem = new MenuItem(servers, SWT.NONE);
					serverItem.setText(loadServerAdressArray[i]);
					if (i == 0) {
						serverItem.setImage(SWTResourceManager.getImage(MainWindow.class, Resource.IMAGE_CHECKED));
					}
					serverItem.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(SelectionEvent e) {
							// 设置焦点
							for (int i = 0; i < servers.getItemCount(); i++) {
								servers.getItem(i).setImage(null);
							}
							serverItem.setImage(SWTResourceManager.getImage(MainWindow.class, Resource.IMAGE_CHECKED));
							serverAdress = serverItem.getText();
							urlText.setText(serverAdress + interfaceContextPath);
						}
					});
				}
			} else {
				this.serverAdress = "";
			}
		}
	}

	// 初始化接口模块
	private void initMod() {
		modSelectCombo.removeAll();
		if (null == apiDoc.getItem()) {
			return;
		}
		for (int i = 0; i < apiDoc.getItem().size(); i++) {
			modSelectCombo.add(apiDoc.getItem().get(i).getName());
			logger.debug("API分类:" + apiDoc.getItem().get(i).getName() + "加载完毕");
		}
		if (modSelectCombo.getItemCount() > 0) {
			modSelectCombo.select(0);
			initSelectMod(0);
		}
	}

	// 初始化选择的模块
	private void initSelectMod(int modindex) {
		clearParameters();
		interfaceCombo.removeAll();
		if (null == apiDoc.getItem().get(modindex).getItem() || apiDoc.getItem().get(modindex).getItem().size() == 0) {
			logger.debug("当前分类下无接口信息，跳过加载");
			return;
		}
		for (int i = 0; i < apiDoc.getItem().get(modindex).getItem().size(); i++) {
			interfaceCombo.add(apiDoc.getItem().get(modindex).getItem().get(i).getName());
		}
		try {
			// 默认初始化这个分类下的第一个接口
			interfaceCombo.select(0);
			initSelectInterface(modindex, interfaceCombo.getSelectionIndex());

		} catch (Exception e) {
			logger.error("异常", e);
			urlText.setText("");
		}
	}

	// 初始化选择的接口
	private void initSelectInterface(int modindex, int interfaceindex) {
		// 初始化前要判断是否之前有保存过，如果有保存过，则初始化保存的那份数据
		ApiItem apiItem = tempSavePars.get(apiDoc.getItem().get(modindex).getItem().get(interfaceindex).getUuid());
		if (null != apiItem) {
			logger.debug("此接口有之前保存的数据，读取保存的数据");
			interfaceContextPath = apiItem.getPath();
			urlText.setText(serverAdress + apiItem.getPath());
			interfaceCombo.setToolTipText(apiItem.getDescription());
			mainWindowShell.setText(applicationName + "-" + interfaceCombo.getText());
			methodChoice(apiItem.getMethod());
			initParameters(apiItem.getParameters());
			logger.debug("切换到接口:" + apiDoc.getItem().get(modindex).getItem().get(interfaceindex).getName());
		} else {
			interfaceContextPath = apiDoc.getItem().get(modindex).getItem().get(interfaceindex).getPath();
			urlText.setText(serverAdress + apiDoc.getItem().get(modindex).getItem().get(interfaceindex).getPath());
			interfaceCombo
					.setToolTipText(apiDoc.getItem().get(modindex).getItem().get(interfaceindex).getDescription());
			mainWindowShell.setText(applicationName + "-" + interfaceCombo.getText());
			methodChoice(apiDoc.getItem().get(modindex).getItem().get(interfaceindex).getMethod());
			initParameters(apiDoc.getItem().get(modindex).getItem().get(interfaceindex).getParameters());
			logger.debug("切换到接口:" + apiDoc.getItem().get(modindex).getItem().get(interfaceindex).getName());
		}
	}

	// 参数初始化
	private void initParameters(ArrayList<ApiPar> pars) {
		clearParameters();
		if (null != pars) {
			for (int i = 0; i < pars.size(); i++) {
				if (i > (this.parsSum - 1)) {
					logger.info("使用的参数超过了" + parsSum + "个");
					statusBar.setText("暂不支持" + parsSum + "个以上参数");
					break;
				}
				// 将参数初始化一下
				form[i][0].setToolTipText(pars.get(i).getTip() + "");
				form[i][0].setText(pars.get(i).getTip() + "");
				form[i][1].setText(pars.get(i).getName());
				form[i][2].setText(pars.get(i).getValue());
			}
		}
	}

	// 请求方法选择器
	public void methodChoice(String method) {
		if (null != method) {
			switch (method.toUpperCase()) {
			case "GET":
				methodSelectCombo.select(0);
				break;
			case "POST":
				methodSelectCombo.select(1);
				break;
			case "HEAD":
				methodSelectCombo.select(2);
				break;
			case "PUT":
				methodSelectCombo.select(3);
				break;
			case "PATCH":
				methodSelectCombo.select(4);
				break;
			case "DELETE":
				methodSelectCombo.select(5);
				break;
			default:
				logger.info("未找到合适的请求方法,忽略");
				break;
			}
		}
	}

	// 转换HashMap到ArrayList<ApiPar>
	private ArrayList<ApiPar> covertHashMaptoApiPar(HashMap<String, String> queryMap) {
		if (null == queryMap) {
			return null;
		}
		ArrayList<ApiPar> apiPars = new ArrayList<ApiPar>();
		for (Entry<String, String> entry : queryMap.entrySet()) {
			apiPars.add(new ApiPar(entry.getKey(), "", entry.getValue()));
		}
		return apiPars;
	}

	// 获取输入框中的参数-供发起请求的时候使用
	private HashMap<String, String> getParameters() {
		HashMap<String, String> par = new HashMap<String, String>();
		for (int i = 0; i < parsSum; i++) {
			if (!(form[i][1].getText().isEmpty() || form[i][2].getText().isEmpty())
					&& !(form[i][1].getForeground().getRGB().equals(new RGB(128, 128, 128)))) {
				par.put(form[i][1].getText(), form[i][2].getText());
			}
		}
		return par;
	}

	// 清空结果
	private void clearResult() {
		resultBodyStyledText.setText("");
		resultHeaderStyledText.setText("");
		statusBar.setText("");
		logger.debug("清理结束");
	}

	// 清空参数信息-清空表单和参数输入框
	private void clearParameters() {
		statusBar.setText("");
		parsText.setText("");
		for (int i = 0; i < parsSum; i++) {
			menuItem1SubFrozen[i].setText("冻结此参数");
			form[i][0].setText("");
			form[i][0].setToolTipText("");
			form[i][1].setText("");
			form[i][2].setText("");
			// label[i].setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
			form[i][0].setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
			form[i][1].setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
			form[i][2].setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		}
	}

	// 参数重排
	private void compressParameters() {
		for (int i = 0; i < parsSum; i++) {
			if (form[i][0].getText().trim().isEmpty() && form[i][1].getText().trim().isEmpty()
					&& form[i][2].getText().trim().isEmpty()) {
				if (i == parsSum - 1) {
					break;
				} else {
					for (int j = i + 1; j < parsSum; j++) {
						if (!form[j][0].getText().trim().isEmpty() || !form[j][1].getText().trim().isEmpty()
								|| !form[j][2].getText().trim().isEmpty()) {
							form[i][0].setText(form[j][0].getText());
							form[i][0].setToolTipText((form[j][0].getToolTipText()));
							form[i][1].setText(form[j][1].getText());
							form[i][2].setText(form[j][2].getText());
							form[j][0].setText("");
							form[j][0].setToolTipText("");
							form[j][1].setText("");
							form[j][2].setText("");
							// 判断是否做过冻结，如果是，则需要移动锁定标志位
							if ((form[j][0].getForeground().getRGB().equals(new RGB(128, 128, 128)))) {
								label[i].setToolTipText("此参数已冻结,冻结后不再发送此参数");
								// label[i].setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
								form[i][0].setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
								form[i][1].setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
								form[i][2].setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
								menuItem1SubFrozen[i].setText("解冻此参数");
								label[j].setToolTipText("");
								// label[j].setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
								form[j][0].setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
								form[j][1].setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
								form[j][2].setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
								menuItem1SubFrozen[j].setText("冻结此参数");
							}
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

	// 给主页面的返回区域添加右键菜单
	public void StyledTextAddContextMenu(final StyledText styledText) {
		Menu popupMenu = new Menu(styledText);
		MenuItem cut = new MenuItem(popupMenu, SWT.NONE);
		cut.setText("剪切");
		MenuItem copy = new MenuItem(popupMenu, SWT.NONE);
		copy.setText("复制");
		MenuItem paste = new MenuItem(popupMenu, SWT.NONE);
		paste.setText("粘贴");
		MenuItem allSelect = new MenuItem(popupMenu, SWT.NONE);
		allSelect.setText("全选");
		MenuItem clear = new MenuItem(popupMenu, SWT.NONE);
		clear.setText("清空");
		MenuItem compressJson = new MenuItem(popupMenu, SWT.NONE);
		compressJson.setText("压缩JSON");
		MenuItem formatJson = new MenuItem(popupMenu, SWT.NONE);
		formatJson.setText("格式化JSON");
		final MenuItem warp = new MenuItem(popupMenu, SWT.NONE);
		warp.setText("自动换行");
		styledText.setMenu(popupMenu);

		// 判断初始自动换行状态
		if (styledText.getWordWrap()) {
			warp.setText("关闭自动换行");
		} else {
			warp.setText("打开自动换行");
		}
		styledText.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.stateMask == SWT.CTRL && e.keyCode == 'a') {
					styledText.selectAll();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
			}
		});
		// 剪切菜单的点击事件
		cut.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (styledText.getSelectionCount() == 0) {
					return;
				}
				Clipboard clipboard = new Clipboard(styledText.getDisplay());
				String plainText = styledText.getSelectionText();
				TextTransfer textTransfer = TextTransfer.getInstance();
				clipboard.setContents(new String[] { plainText }, new Transfer[] { textTransfer });
				clipboard.dispose();
				// 将已经剪切走的部分删除,并将插入符移动到剪切位置
				int caretOffset = styledText.getSelection().x;
				styledText.setText(new StringBuffer(styledText.getText())
						.replace(styledText.getSelection().x, styledText.getSelection().y, "").toString());
				styledText.setCaretOffset(caretOffset);
			}
		});

		// 粘贴菜单的点击事件
		paste.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Clipboard clipboard = new Clipboard(styledText.getDisplay());
				TextTransfer textTransfer = TextTransfer.getInstance();
				// 获取剪切板上的文本
				String cliptext = (clipboard.getContents(textTransfer) != null
						? clipboard.getContents(textTransfer).toString() : "");
				clipboard.dispose();
				int caretOffset = styledText.getSelection().x;
				styledText.setText(new StringBuffer(styledText.getText())
						.replace(styledText.getSelection().x, styledText.getSelection().y, cliptext).toString());
				styledText.setCaretOffset(caretOffset + cliptext.length());
			}
		});

		// 复制上下文菜单的点击事件
		copy.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (styledText.getSelectionCount() == 0) {
					return;
				}
				Clipboard clipboard = new Clipboard(styledText.getDisplay());
				String plainText = styledText.getSelectionText();
				TextTransfer textTransfer = TextTransfer.getInstance();
				clipboard.setContents(new String[] { plainText }, new Transfer[] { textTransfer });
				clipboard.dispose();
			}
		});

		// 全选上下文菜单的点击事件
		allSelect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				styledText.selectAll();
			}
		});

		// 清空上下文菜单的点击事件
		clear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				styledText.setText("");
			}
		});

		// 压缩JSON点击事件
		compressJson.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Pattern p = Pattern.compile("\\s*|\t|\r|\n");
				Matcher m = p.matcher(styledText.getText());
				styledText.setText(m.replaceAll(""));
			}
		});

		// 格式化JSON点击事件
		formatJson.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Pattern p = Pattern.compile("\\s*|\t|\r|\n");
				Matcher m = p.matcher(styledText.getText());
				styledText.setText(ApiUtils.jsonFormat(m.replaceAll("")));
			}
		});

		// 更改是否自动换行
		warp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (styledText.getWordWrap()) {
					styledText.setWordWrap(false);
					warp.setText("打开自动换行");
				} else {
					styledText.setWordWrap(true);
					warp.setText("关闭自动换行");
				}
			}
		});
	}

	// 开启一个新的窗口
	private void initNewWindow(boolean mainWindowFlag, boolean openByShortcutFlag) {
		MainWindow mainWindow = new MainWindow();
		mainWindow.open(mainWindowFlag, openByShortcutFlag);
	}

	// 初始化历史记录
	// 后期会支持保存历史记录
	private void initHistory() {
		history = new ApiMod();
		history.setName("历史记录");
		history.setItem(new ArrayList<ApiItem>());
	}

	// TODO 待实现的JSON着色器
	private void RenderingColor(StyledText styledText) {
		StyleRange styleRange = new StyleRange();
		styledText.setStyleRange(styleRange);
		int startIndex = 0;
		int endIndex = styledText.getText().length();
		if (endIndex == -1) {
			return;
		} else {

		}
	}

	// TODO 历史记录器
	// 触发更新历史记录，只有在点击请求的时候才触发
	private void notifyHistory() {
		ArrayList<ApiItem> historyList = history.getItem();
		if (historyList.size() < this.loadHistorySum) {
			@SuppressWarnings("unused")
			ApiItem item = new ApiItem();
			historyList.add(null);
		} else {
		}
	}
}
