package com.itlaborer.apitools.ui;

import java.io.UnsupportedEncodingException;
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

import com.itlaborer.apitools.utils.ApiUtils;

/**
 * Url工具界面
 * @author liudewei[793554262@qq.com]
 * @version 1.0
 * @since 1.0
 */

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
		urlEncodeToolsShell.setImage(SWTResourceManager.getImage(UrlEncodeTools.class, "/com/itlaborer/apitools/res/icon.ico"));
		urlEncodeToolsShell.setSize(680, 420);
		urlEncodeToolsShell.setText("URL编码/解码工具");
		ApiUtils.SetCenterinParent(getParent(), urlEncodeToolsShell);

		final StyledText styledText = new StyledText(urlEncodeToolsShell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		styledText.setWordWrap(true);
		styledText.setBounds(6, 8, 662, 346);
		ApiUtils.StyledTextAddContextMenu(styledText);

		// URL编码
		Button btnNewButton = new Button(urlEncodeToolsShell, SWT.NONE);
		btnNewButton.setBounds(5, 360, 342, 27);
		btnNewButton.setText("URL编码");
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String encode = null;
				try {
					encode = URLEncoder.encode(styledText.getText(), "UTF-8");
				} catch (UnsupportedEncodingException e1) {
					logger.error("异常", e1);
				}
				if (null == encode) {
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
		btnmd.setBounds(353, 360, 317, 27);
		btnmd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String encode = null;
				try {
					encode = URLDecoder.decode(styledText.getText(), "UTF-8");
				} catch (UnsupportedEncodingException e1) {
					logger.error("异常", e1);
				}
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
