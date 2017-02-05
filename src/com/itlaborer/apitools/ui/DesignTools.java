package com.itlaborer.apitools.ui;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import com.itlaborer.apitools.res.Resource;
import com.itlaborer.apitools.swt.SWTResourceManager;
import com.itlaborer.apitools.utils.ApiUtils;

public class DesignTools {

	protected Shell shell;
	private static Logger logger = Logger.getLogger(MainWindow.class.getName());

	public DesignTools() {
		logger.info("进入APIDesignTools");
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		shell = new Shell(SWT.MIN);
		shell.setSize(1145, 680);
		shell.setImage(SWTResourceManager.getImage(MainWindow.class, Resource.IMAGE_ICON));
		shell.setText("APIDesignTools-" + Resource.VERSION);
		ApiUtils.SetCenter(shell);

		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

		MenuItem fileMenuItem = new MenuItem(menu, SWT.CASCADE);
		fileMenuItem.setText("文件");

		Menu fileMenu = new Menu(fileMenuItem);
		fileMenuItem.setMenu(fileMenu);

		MenuItem menuItemNewFile = new MenuItem(fileMenu, SWT.NONE);
		menuItemNewFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CreateFileDialog createFileDialog = new CreateFileDialog(shell, SWT.CLOSE | SWT.SYSTEM_MODAL);
				createFileDialog.open();
			}
		});
		menuItemNewFile.setText("新建");

		MenuItem menuItemOpenFile = new MenuItem(fileMenu, SWT.NONE);
		menuItemOpenFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.setFilterExtensions(new String[] { "*.json", "*.*" });
				fd.setFilterNames(new String[] { "JSON(*.json)", "All Files(*.*)" });
				fd.setFilterPath("./config/");
				String filepath = fd.open();
				if (StringUtils.isNotEmpty(filepath)) {
					logger.info(filepath);
				}
			}
		});
		menuItemOpenFile.setText("打开");

		MenuItem menuItemSaveFile = new MenuItem(fileMenu, SWT.NONE);
		menuItemSaveFile.setText("保存");

		MenuItem menuItemExport = new MenuItem(fileMenu, SWT.NONE);
		menuItemExport.setText("导出");
	}
}
