package com.itlaborer.ui;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import com.itlaborer.utils.ApiUtils;

public class MD5Tools extends Dialog {

	private static Logger logger = Logger.getLogger(MD5Tools.class.getName());
	protected Object result;
	protected Shell md5ToolsShell;

	public MD5Tools(Shell parent, int style) {
		super(parent, style);
		logger.info("进入MD5工具");
		setText("SWT Dialog");
	}

	public Object open() {
		createContents();
		md5ToolsShell.open();
		md5ToolsShell.layout();
		Display display = getParent().getDisplay();
		while (!md5ToolsShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	private void createContents() {
		md5ToolsShell = new Shell(getParent(), getStyle());
		md5ToolsShell.setImage(SWTResourceManager.getImage(MD5Tools.class, "/com/itlaborer/res/icon.ico"));
		md5ToolsShell.setSize(600, 287);
		md5ToolsShell.setText("MD5加密工具");
		ApiUtils.SetCenterinParent(getParent(), md5ToolsShell);

		StyledText styledText = new StyledText(md5ToolsShell, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
		styledText.setBounds(10, 10, 574, 202);
		ApiUtils.StyledTextAddContextMenu(styledText);

		// 小写加密
		Button btnNewButton = new Button(md5ToolsShell, SWT.NONE);
		btnNewButton.setBounds(10, 222, 285, 27);
		btnNewButton.setText("32位小写MD5");
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.debug("加密串:" + styledText.getText());
				styledText.setText(
						styledText.getText().length() == 0 ? "" : (ApiUtils.MD5(styledText.getText()).toLowerCase()));
			}
		});

		// 大写加密
		Button btnmd = new Button(md5ToolsShell, SWT.NONE);
		btnmd.setText("32位大写MD5");
		btnmd.setBounds(301, 222, 285, 27);
		btnmd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.debug("加密串:" + styledText.getText());
				styledText.setText(styledText.getText().length() == 0 ? "" : ApiUtils.MD5(styledText.getText()));
			}
		});
	}
}
