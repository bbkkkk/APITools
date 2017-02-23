package com.itlaborer.apitools.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import com.itlaborer.apitools.res.Resource;
import com.itlaborer.apitools.utils.ApiUtils;

public class CharSetDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private boolean saveFlag = false;
	private Button buttonYes;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public CharSetDialog(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object[] open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return (new Object[] { saveFlag });
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setImage(SWTResourceManager.getImage(CharSetDialog.class, Resource.IMAGE_ICON));
		shell.setText("字符集设置");
		shell.setSize(400, 200);
		ApiUtils.SetCenterinParent(getParent(), shell);

		buttonYes = new Button(shell, SWT.NONE);
		buttonYes.setBounds(213, 135, 80, 27);
		buttonYes.setText("保存");

		Button buttonNo = new Button(shell, SWT.NONE);
		buttonNo.setBounds(304, 135, 80, 27);
		buttonNo.setText("放弃");

		Combo combo = new Combo(shell, SWT.NONE | SWT.READ_ONLY);
		combo.setBounds(10, 59, 170, 25);
		combo.add("Unicode (UTF-8)");
		combo.add("简体中文 (GBK)");
		combo.add("简体中文 (GB2312)");
		combo.add("简体中文 (GB18030)");
		combo.add("繁体中文 (Big5)");
		combo.add("繁体中文 (Big5-HKSCS)");
		combo.select(0);

		Combo combo_1 = new Combo(shell, SWT.NONE | SWT.READ_ONLY);
		combo_1.setBounds(201, 59, 170, 25);
		combo_1.add("Unicode (UTF-8)");
		combo_1.add("简体中文 (GBK)");
		combo_1.add("简体中文 (GB2312)");
		combo_1.add("简体中文 (GB18030)");
		combo_1.add("繁体中文 (Big5)");
		combo_1.add("繁体中文 (Big5-HKSCS)");
		combo_1.select(0);

		Label label = new Label(shell, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		label.setBounds(11, 31, 61, 17);
		label.setText("请求编码：");

		Label label_1 = new Label(shell, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		label_1.setText("响应编码：");
		label_1.setBounds(202, 31, 61, 17);
	}
}
