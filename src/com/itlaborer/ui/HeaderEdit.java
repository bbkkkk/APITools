package com.itlaborer.ui;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import com.itlaborer.utils.ApiUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.SWT;

public class HeaderEdit extends Dialog {

	private static Logger logger = Logger.getLogger(HeaderEdit.class.getName());
	protected Object result;
	protected Shell headerEditShell;
	private Table table;

	public HeaderEdit(Shell parent, int style) {
		super(parent, style);
		logger.info("进入Cookie参数工具");
	}

	public Object open() {
		createContents();
		headerEditShell.open();
		headerEditShell.layout();
		Display display = getParent().getDisplay();
		while (!headerEditShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	private void createContents() {
		headerEditShell = new Shell(getParent(), getStyle());
		headerEditShell.setImage(SWTResourceManager.getImage(HeaderEdit.class, "/com/itlaborer/res/icon.ico"));
		headerEditShell.setSize(665, 400);
		headerEditShell.setText("Cookie参数工具");
		ApiUtils.SetCenterinParent(getParent(), headerEditShell);
		
		table = new Table(headerEditShell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(10, 10, 639, 311);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setItemCount(100);
	}
}
