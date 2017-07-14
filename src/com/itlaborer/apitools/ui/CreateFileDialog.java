package com.itlaborer.apitools.ui;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.itlaborer.apitools.res.Resource;
import com.itlaborer.apitools.utils.PubUtils;

public class CreateFileDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private boolean saveFlag = false;
	////////////////////////////////
	private String name;
	private String version;
	private String serverList;
	private Text nameText;
	private Text versionText;
	private Text serverText;
	private Button buttonYes;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public CreateFileDialog(Shell parent, int style) {
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
		return (new Object[] { saveFlag, name, version, serverList });
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setImage(SWTResourceManager.getImage(CreateFileDialog.class, Resource.IMAGE_ICON));
		shell.setText("创建一个新的接口文档");
		shell.setSize(480, 220);
		PubUtils.setCenterinParent(getParent(), shell);

		buttonYes = new Button(shell, SWT.NONE);
		buttonYes.setBounds(293, 155, 80, 27);
		buttonYes.setText("保存");
		buttonYes.setEnabled(false);
		buttonYes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveFlag = true;
				name = nameText.getText();
				version = versionText.getText();
				serverList = serverText.getText();
				shell.dispose();
			}
		});

		Button buttonNo = new Button(shell, SWT.NONE);
		buttonNo.setBounds(384, 155, 80, 27);
		buttonNo.setText("放弃");

		nameText = new Text(shell, SWT.BORDER);
		nameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (StringUtils.isNotEmpty(nameText.getText().trim())) {
					buttonYes.setEnabled(true);
				} else {
					buttonYes.setEnabled(false);
				}
			}
		});
		nameText.setBounds(10, 35, 454, 23);

		Label label = new Label(shell, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		label.setBounds(10, 18, 61, 17);
		label.setText("文档名:");

		versionText = new Text(shell, SWT.BORDER);
		versionText.setBounds(10, 81, 454, 23);

		Label label_1 = new Label(shell, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		label_1.setBounds(10, 64, 61, 17);
		label_1.setText("接口版本:");

		Label label_2 = new Label(shell, SWT.NONE);
		label_2.setText("服务器列表:");
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		label_2.setBounds(10, 109, 61, 17);

		serverText = new Text(shell, SWT.BORDER);
		serverText.setBounds(10, 126, 454, 23);
		buttonNo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveFlag = false;
				shell.dispose();
			}
		});
	}
}
