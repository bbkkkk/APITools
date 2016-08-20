package com.itlaborer.ui;

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
import com.itlaborer.utils.Base64Utils;

import org.apache.log4j.Logger;

public class Base64Tools extends Dialog {

	private static Logger logger = Logger.getLogger(Base64Tools.class.getName());
	protected Object result;
	protected Shell base64ToolsShell;

	public Base64Tools(Shell parent, int style) {
		super(parent, style);
		logger.info("进入Base64编码/解码工具");
	}

	public Object open() {
		createContents();
		base64ToolsShell.open();
		base64ToolsShell.layout();
		Display display = getParent().getDisplay();
		while (!base64ToolsShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	private void createContents() {
		base64ToolsShell = new Shell(getParent(), getStyle());
		base64ToolsShell.setImage(SWTResourceManager.getImage(Base64Tools.class, "/com/itlaborer/res/icon.ico"));
		base64ToolsShell.setSize(665, 287);
		base64ToolsShell.setText("Base64编码/解码工具");
		ApiUtils.SetCenterinParent(getParent(), base64ToolsShell);

		StyledText styledText = new StyledText(base64ToolsShell, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
		styledText.setFont(SWTResourceManager.getFont("Courier New", 10, SWT.NORMAL));
		styledText.setBounds(10, 10, 636, 202);
		ApiUtils.StyledTextAddContextMenu(styledText);
		// 编码
		Button encodeButton = new Button(base64ToolsShell, SWT.NONE);
		encodeButton.setBounds(10, 222, 315, 27);
		encodeButton.setText("Base64编码");
		encodeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.debug("编码串:" + styledText.getText());
				styledText.setText(
						styledText.getText().length() == 0 ? "" : Base64Utils.encode(styledText.getText().getBytes()));
			}
		});

		// 解码
		Button decodeButton = new Button(base64ToolsShell, SWT.NONE);
		decodeButton.setText("Base64解码");
		decodeButton.setBounds(331, 222, 315, 27);
		decodeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				byte[] bytes = Base64Utils.decode(styledText.getText());
				if (bytes == null) {
				} else {
					logger.debug("解码串:" + styledText.getText());
					styledText.setText(styledText.getText().length() == 0 ? "" : new String(bytes));
				}
			}
		});
	}
}
