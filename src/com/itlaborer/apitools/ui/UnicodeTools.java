package com.itlaborer.apitools.ui;

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
 * 
 * @author liudewei[793554262@qq.com]
 * @see Unicode工具界面
 * @version 1.0
 * @since 1.0
 * 
 */

public class UnicodeTools extends Dialog {

	private static Logger logger = Logger.getLogger(UnicodeTools.class.getName());
	protected Object result;
	protected Shell unicodeToolsShell;

	public UnicodeTools(Shell parent, int style) {
		super(parent, style);
		logger.info("进入Unicode编码/解码工具");
	}

	public Object open() {
		createContents();
		unicodeToolsShell.open();
		unicodeToolsShell.layout();
		Display display = getParent().getDisplay();
		while (!unicodeToolsShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	private void createContents() {
		unicodeToolsShell = new Shell(getParent(), getStyle());
		unicodeToolsShell.setImage(SWTResourceManager.getImage(UnicodeTools.class, "/com/itlaborer/apitools/res/icon.ico"));
		unicodeToolsShell.setSize(680, 420);
		unicodeToolsShell.setText("Unicode编码/解码工具");
		ApiUtils.SetCenterinParent(getParent(), unicodeToolsShell);

		final StyledText styledText = new StyledText(unicodeToolsShell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL );
		styledText.setBounds(6, 8, 662, 346);
		ApiUtils.StyledTextAddContextMenu(styledText);

		//全Unicode编码
		Button allUnicodeButton = new Button(unicodeToolsShell, SWT.NONE);
		allUnicodeButton.setBounds(5,360, 215, 27);
		allUnicodeButton.setText("全Unicode编码");
		allUnicodeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 获取unicode串
				logger.debug("开始Unicode编码");
				String unicodeString = ApiUtils.string2Unicode(styledText.getText());
				if (unicodeString == null) {
					//
				} else {
					styledText.setText(styledText.getText().length() == 0 ? "" : unicodeString);
				}
			}
		});
		
		//中文Unicode编码
		Button zhUnicodeButton = new Button(unicodeToolsShell, SWT.NONE);
		zhUnicodeButton.setText("中文Unicode编码");
		zhUnicodeButton.setBounds(226, 360,221, 27);
		zhUnicodeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 获取unicode串
				logger.debug("开始Unicode编码");
				String unicodeString = ApiUtils.stringzh2Unicode(styledText.getText());
				if (unicodeString == null) {
					//
				} else {
					styledText.setText(styledText.getText().length() == 0 ? "" : unicodeString);
				}
			}
		});

		//Unicode解码
		Button unicodeDecodeButton = new Button(unicodeToolsShell, SWT.NONE);
		unicodeDecodeButton.setText("Unicode解码");
		unicodeDecodeButton.setBounds(453,360, 216, 27);
		unicodeDecodeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.debug("开始Unicode解码");
				;
				styledText.setText(ApiUtils.unicode2String(styledText.getText()));
			}
		});
	}
}
