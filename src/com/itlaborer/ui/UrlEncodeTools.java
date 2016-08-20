package com.itlaborer.ui;

import java.net.URLDecoder;
import java.net.URLEncoder;

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

public class UrlEncodeTools extends Dialog {

	private static Logger logger = Logger.getLogger(UrlEncodeTools.class.getName());
	protected Object result;
	protected Shell urlEncodeToolsShell;

	public UrlEncodeTools(Shell parent, int style) {
		super(parent, style);
		logger.info("进入Url编码/解码工具");
	}

	public Object open() {
		createContents();
		urlEncodeToolsShell.open();
		urlEncodeToolsShell.layout();
		Display display = getParent().getDisplay();
		while (!urlEncodeToolsShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	private void createContents() {
		urlEncodeToolsShell = new Shell(getParent(), getStyle());
		urlEncodeToolsShell.setImage(SWTResourceManager.getImage(UrlEncodeTools.class, "/com/itlaborer/res/icon.ico"));
		urlEncodeToolsShell.setSize(600, 287);
		urlEncodeToolsShell.setText("URL编码/解码工具");
		ApiUtils.SetCenterinParent(getParent(), urlEncodeToolsShell);

		StyledText styledText = new StyledText(urlEncodeToolsShell, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
		styledText.setBounds(10, 10, 574, 202);
		ApiUtils.StyledTextAddContextMenu(styledText);

		// URL编码
		Button btnNewButton = new Button(urlEncodeToolsShell, SWT.NONE);
		btnNewButton.setBounds(10, 222, 285, 27);
		btnNewButton.setText("URL编码");
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				@SuppressWarnings("deprecation")
				String encode = URLEncoder.encode(styledText.getText());
				if (encode == null) {
					//
				} else {
					logger.debug("编码串:" + styledText.getText());
					styledText.setText(styledText.getText().length() == 0 ? "" : encode);
				}
			}
		});

		// URL解码
		Button btnmd = new Button(urlEncodeToolsShell, SWT.NONE);
		btnmd.setText("URL解码");
		btnmd.setBounds(301, 222, 285, 27);
		btnmd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				@SuppressWarnings("deprecation")
				String encode = URLDecoder.decode(styledText.getText());
				if (encode == null) {
					//
				} else {
					logger.debug("解码串:" + styledText.getText());
					styledText.setText(styledText.getText().length() == 0 ? "" : encode);
				}
			}
		});
	}
}
