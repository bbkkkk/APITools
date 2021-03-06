package com.itlaborer.apitools.ui;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.itlaborer.apitools.res.Resource;
import com.itlaborer.apitools.utils.PubUtils;

public class EditCollectionDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private boolean saveFlag = false;
	////////////////////////////////
	private String name;
	private String description;
	private String serverAddress;
	private String path;
	private String method;
	///////////////////////////////
	private Text nameText;
	private Text descriptionText;
	private Text serverText;
	private Text pathText;
	private Button buttonYes;
	private Combo combo;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public EditCollectionDialog(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object[] open(String name, String des, String serverAddress, String path, String method) {
		this.serverAddress = serverAddress;
		this.name = name;
		this.description = des;
		this.path = path;
		this.method = method;
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return (new Object[] { this.saveFlag, this.name, this.description, this.path, this.method });
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setImage(SWTResourceManager.getImage(EditCollectionDialog.class, Resource.IMAGE_ICON));
		shell.setText("编辑接口");
		shell.setSize(480, 220);
		PubUtils.setCenterinParent(getParent(), shell);

		buttonYes = new Button(shell, SWT.NONE);
		buttonYes.setBounds(293, 155, 80, 27);
		buttonYes.setText("保存");
		buttonYes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveFlag = true;
				name = nameText.getText();
				description = descriptionText.getText();
				path = pathText.getText();
				method = combo.getText();
				shell.dispose();
			}
		});

		Button buttonNo = new Button(shell, SWT.NONE);
		buttonNo.setBounds(384, 155, 80, 27);
		buttonNo.setText("放弃");
		buttonNo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveFlag = false;
				shell.dispose();
			}
		});
		nameText = new Text(shell, SWT.BORDER);
		nameText.setBounds(10, 35, 363, 23);
		if (StringUtils.isEmpty(name)) {
			buttonYes.setEnabled(false);
		} else {
			nameText.setText(name);
		}
		nameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (StringUtils.isEmpty(nameText.getText())) {
					buttonYes.setEnabled(false);
				} else {
					buttonYes.setEnabled(true);
				}
			}
		});

		Label label = new Label(shell, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		label.setBounds(10, 18, 61, 17);
		label.setText("接口名:");

		descriptionText = new Text(shell, SWT.BORDER);
		descriptionText.setBounds(10, 81, 454, 23);
		if (StringUtils.isNotEmpty(description)) {
			descriptionText.setText(description);
		}
		Label label_1 = new Label(shell, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		label_1.setBounds(10, 64, 61, 17);
		label_1.setText("备注:");

		Label label_2 = new Label(shell, SWT.NONE);
		label_2.setText("路径:");
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		label_2.setBounds(10, 109, 61, 17);

		serverText = new Text(shell, SWT.BORDER | SWT.RIGHT);
		serverText.setBounds(10, 126, 281, 23);
		serverText.setText(serverAddress + "");

		pathText = new Text(shell, SWT.BORDER);
		pathText.setBounds(293, 126, 171, 23);
		pathText.setText(path + "");

		combo = new Combo(shell, SWT.READ_ONLY);
		combo.setBounds(383, 35, 80, 25);
		combo.add("GET", 0);
		combo.add("POST", 1);
		combo.add("HEAD", 2);
		combo.add("PUT", 3);
		combo.add("PATCH", 4);
		combo.add("DELETE", 5);
		methodChoice(method);
	}

	// 请求方法选择器
	public void methodChoice(String method) {
		if (null != method) {
			switch (method.toUpperCase()) {
			case "GET":
				combo.select(0);
				break;
			case "POST":
				combo.select(1);
				break;
			case "HEAD":
				combo.select(2);
				break;
			case "PUT":
				combo.select(3);
				break;
			case "PATCH":
				combo.select(4);
				break;
			case "DELETE":
				combo.select(5);
				break;
			default:
				combo.select(0);
				break;
			}
		}
	}
}
