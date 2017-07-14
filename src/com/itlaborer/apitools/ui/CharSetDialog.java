package com.itlaborer.apitools.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import com.itlaborer.apitools.res.Resource;
import com.itlaborer.apitools.utils.PubUtils;

public class CharSetDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private boolean saveFlag = false;
	private Button buttonYes;
	private String requestCharSet;
	private String responseCharSet;
	private Combo comboRes;
	private Combo comboReq;

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
	public Object[] open(String requestCharset, String responseCharSet) {
		this.requestCharSet = requestCharset;
		this.responseCharSet = responseCharSet;
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return (new Object[] { saveFlag, this.requestCharSet, this.responseCharSet });
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setImage(SWTResourceManager.getImage(CharSetDialog.class, Resource.IMAGE_ICON));
		shell.setText("字符集设置");
		shell.setSize(400, 200);
		PubUtils.setCenterinParent(getParent(), shell);

		buttonYes = new Button(shell, SWT.NONE);
		buttonYes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 编码设置
				switch (comboReq.getSelectionIndex()) {
				case 0:
					requestCharSet = "UTF-8";
					break;
				case 1:
					requestCharSet = "GBK";
					break;
				case 2:
					requestCharSet = "GB2312";
					break;
				case 3:
					requestCharSet = "GB18030";
					break;
				case 4:
					requestCharSet = "Big5";
					break;
				case 5:
					requestCharSet = "Big5-HKSCS";
					break;
				case 6:
					requestCharSet = "ISO-8859-1";
					break;
				default:
					break;
				}
				// 编码设置
				switch (comboRes.getSelectionIndex()) {
				case 0:
					responseCharSet = "auto";
					break;
				case 1:
					responseCharSet = "UTF-8";
					break;
				case 2:
					responseCharSet = "GBK";
					break;
				case 3:
					responseCharSet = "GB2312";
					break;
				case 4:
					responseCharSet = "GB18030";
					break;
				case 5:
					responseCharSet = "Big5";
					break;
				case 6:
					responseCharSet = "Big5-HKSCS";
					break;
				case 7:
					responseCharSet = "ISO-8859-1";
					break;
				default:
					break;
				}
				saveFlag = true;
				shell.dispose();
			}
		});
		buttonYes.setBounds(213, 135, 80, 27);
		buttonYes.setText("保存");

		Button buttonNo = new Button(shell, SWT.NONE);
		buttonNo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveFlag = false;
				shell.dispose();
			}
		});
		buttonNo.setBounds(304, 135, 80, 27);
		buttonNo.setText("放弃");

		comboReq = new Combo(shell, SWT.NONE | SWT.READ_ONLY);
		comboReq.setBounds(10, 59, 170, 25);
		// 编码设置
		comboReq.add("Unicode (UTF-8)");
		comboReq.add("简体中文 (GBK)");
		comboReq.add("简体中文 (GB2312)");
		comboReq.add("简体中文 (GB18030)");
		comboReq.add("繁体中文 (Big5)");
		comboReq.add("繁体中文 (Big5-HKSCS)");
		comboReq.add("西方 (ISO-8859-1)");
		// 初始化
		switch (requestCharSet.toUpperCase()) {
		case "UTF-8":
			comboReq.select(0);
			break;
		case "GBK":
			comboReq.select(1);
			break;
		case "GB2312":
			comboReq.select(2);
			break;
		case "GB18030":
			comboReq.select(3);
			break;
		case "BIG5":
			comboReq.select(4);
			break;
		case "BIG5-HKSCS":
			comboReq.select(5);
			break;
		case "ISO-8859-1":
			comboReq.select(6);
			break;
		default:
			comboReq.select(0);
			break;
		}

		comboRes = new Combo(shell, SWT.NONE | SWT.READ_ONLY);
		comboRes.setBounds(201, 59, 170, 25);
		// 编码设置
		comboRes.add("自动检测");
		comboRes.add("Unicode (UTF-8)");
		comboRes.add("简体中文 (GBK)");
		comboRes.add("简体中文 (GB2312)");
		comboRes.add("简体中文 (GB18030)");
		comboRes.add("繁体中文 (Big5)");
		comboRes.add("繁体中文 (Big5-HKSCS)");
		comboRes.add("西方 (ISO-8859-1)");
		// 初始化
		switch (responseCharSet.toUpperCase()) {
		case "AUTO":
			comboRes.select(0);
			break;
		case "UTF-8":
			comboRes.select(1);
			break;
		case "GBK":
			comboRes.select(2);
			break;
		case "GB2312":
			comboRes.select(3);
			break;
		case "GB18030":
			comboRes.select(4);
			break;
		case "BIG5":
			comboRes.select(5);
			break;
		case "BIG5-HKSCS)":
			comboRes.select(6);
			break;
		case "ISO-8859-1":
			comboRes.select(7);
			break;
		default:
			comboRes.select(0);
			break;
		}

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
