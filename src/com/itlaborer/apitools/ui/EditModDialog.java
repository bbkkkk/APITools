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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.itlaborer.apitools.res.Resource;
import com.itlaborer.apitools.utils.PubUtils;
import org.eclipse.swt.widgets.Label;

public class EditModDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private String name;
	private String description;
	private Text nameText;
	private boolean saveFlag = false;
	private Text descriptionText;
	private Button buttonYes;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public EditModDialog(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object[] open(String modname, String des) {
		this.name = modname;
		this.description = des;
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
		shell.setImage(SWTResourceManager.getImage(EditModDialog.class, Resource.IMAGE_ICON));
		shell.setText("编辑模块");
		shell.setSize(400, 200);
		PubUtils.SetCenterinParent(getParent(), shell);

		buttonYes = new Button(shell, SWT.NONE);
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
		nameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (StringUtils.isEmpty(nameText.getText())) {
					buttonYes.setEnabled(false);
				} else {
					buttonYes.setEnabled(true);
				}
			}
		});
		if (StringUtils.isNotEmpty(name)) {
			nameText.setText(name);
		} else {
			buttonYes.setEnabled(false);
		}

		Label label = new Label(shell, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		label.setBounds(10, 20, 61, 17);
		label.setText("模块名:");

		descriptionText = new Text(shell, SWT.BORDER);
		descriptionText.setBounds(10, 87, 374, 23);
		if (StringUtils.isNotEmpty(description)) {
			descriptionText.setText(description);
		}
		Label label_1 = new Label(shell, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		label_1.setBounds(10, 70, 61, 17);
		label_1.setText("备注:");
		buttonNo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveFlag = false;
				shell.dispose();
			}
		});
	}
}
