package com.itlaborer.apitools.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.itlaborer.apitools.res.Resource;
import com.itlaborer.apitools.utils.ApiUtils;
import org.eclipse.swt.widgets.Label;

public class CreateModDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private String name;
	private String description;
	private Text nameText;
	private boolean saveFlag = false;
	private Text descriptionText;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public CreateModDialog(Shell parent, int style) {
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
		return (new Object[] { saveFlag, name, description });
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setImage(SWTResourceManager.getImage(CreateModDialog.class, Resource.IMAGE_ICON));
		shell.setText("创建一个新的模块");
		shell.setSize(400, 200);
		ApiUtils.SetCenterinParent(getParent(), shell);

		Button buttonYes = new Button(shell, SWT.NONE);
		buttonYes.setBounds(213, 135, 80, 27);
		buttonYes.setText("保存");
		buttonYes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveFlag = true;
				name = nameText.getText();
				description = descriptionText.getText();
				shell.dispose();
			}
		});

		Button buttonNo = new Button(shell, SWT.NONE);
		buttonNo.setBounds(304, 135, 80, 27);
		buttonNo.setText("放弃");

		nameText = new Text(shell, SWT.BORDER);
		nameText.setBounds(10, 37, 374, 23);

		Label label = new Label(shell, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		label.setBounds(10, 20, 61, 17);
		label.setText("模块名");

		descriptionText = new Text(shell, SWT.BORDER);
		descriptionText.setBounds(10, 87, 374, 23);

		Label label_1 = new Label(shell, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		label_1.setBounds(10, 70, 61, 17);
		label_1.setText("备注");
		buttonNo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveFlag = false;
				shell.dispose();
			}
		});
	}
}
