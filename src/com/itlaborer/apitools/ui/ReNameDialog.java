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

public class ReNameDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private String title;
	private String text;
	private Text nameText;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public ReNameDialog(Shell parent, int style, String title, String text) {
		super(parent, style);
		this.title = title;
		this.text = text;

	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public String open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return text;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setImage(SWTResourceManager.getImage(ReNameDialog.class, Resource.IMAGE_ICON));
		shell.setText(this.title);
		shell.setSize(400, 180);
		ApiUtils.SetCenterinParent(getParent(), shell);

		Button buttonYes = new Button(shell, SWT.NONE);
		buttonYes.setBounds(213, 115, 80, 27);
		buttonYes.setText("保存");
		buttonYes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				text = nameText.getText();
				shell.dispose();
			}
		});

		Button buttonNo = new Button(shell, SWT.NONE);
		buttonNo.setBounds(304, 115, 80, 27);
		buttonNo.setText("放弃");

		nameText = new Text(shell, SWT.BORDER);
		nameText.setBounds(10, 52, 374, 23);
		nameText.setText(text);
		buttonNo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
	}
}
