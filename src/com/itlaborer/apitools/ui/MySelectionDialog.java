package com.itlaborer.apitools.ui;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.itlaborer.apitools.res.Resource;
import com.itlaborer.apitools.utils.ApiUtils;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.wb.swt.SWTResourceManager;

public class MySelectionDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private String text;
	private boolean flag = false;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public MySelectionDialog(Shell parent, int style, String text) {
		super(parent, style);
		setText("请确认");
		this.text = text;
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public boolean open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return flag;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setImage(SWTResourceManager.getImage(MySelectionDialog.class, Resource.IMAGE_ICON));
		shell.setSize(400, 180);
		ApiUtils.SetCenterinParent(getParent(), shell);
		shell.setText(getText());

		Label lblNewLabel = new Label(shell, SWT.WRAP);
		lblNewLabel.setBounds(10, 10, 374, 103);
		lblNewLabel.setText(text);

		Button button = new Button(shell, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				flag = true;
				shell.dispose();
			}
		});
		button.setBounds(218, 119, 80, 27);
		button.setText("是");

		Button button_1 = new Button(shell, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				flag = false;
				shell.dispose();
			}
		});
		button_1.setBounds(304, 119, 80, 27);
		button_1.setText("否");

	}
}
