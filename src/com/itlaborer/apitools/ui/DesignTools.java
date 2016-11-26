package com.itlaborer.apitools.ui;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
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
	 */
	protected void createContents() {
		shell = new Shell(SWT.MIN);
		shell.setSize(1148, 650);
		shell.setImage(SWTResourceManager.getImage(MainWindow.class, Resource.IMAGE_ICON));
		shell.setText("APIDesignTools-"+Resource.VERSION);
		ApiUtils.SetCenter(shell);
	}

}
