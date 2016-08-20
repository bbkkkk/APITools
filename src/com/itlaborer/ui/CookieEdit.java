package com.itlaborer.ui;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.itlaborer.utils.ApiUtils;

public class CookieEdit extends Dialog {

	private static Logger logger = Logger.getLogger(CookieEdit.class.getName());
	protected Object result;
	protected Shell cookieEditShell;
	private Table editTable;

	public CookieEdit(Shell parent, int style) {
		super(parent, style);
		logger.info("进入Cookie参数工具");
	}

	public Object open() {
		createContents();
		cookieEditShell.open();
		cookieEditShell.layout();
		Display display = getParent().getDisplay();
		while (!cookieEditShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	private void createContents() {
		cookieEditShell = new Shell(getParent(), getStyle());
		cookieEditShell.setImage(SWTResourceManager.getImage(CookieEdit.class, "/com/itlaborer/res/icon.ico"));
		cookieEditShell.setSize(665, 400);
		cookieEditShell.setText("Cookie参数工具");
		ApiUtils.SetCenterinParent(getParent(), cookieEditShell);

		editTable = new Table(cookieEditShell, SWT.BORDER | SWT.HIDE_SELECTION);
		editTable.setBounds(0, 0, 659, 346);
		editTable.setHeaderVisible(true);
		editTable.setLinesVisible(true);
		editTable.setItemCount(30);

		TableColumn collumName = new TableColumn(editTable, SWT.NONE);
		collumName.setWidth(300);
		collumName.setText("参数名");
		collumName.setResizable(false);

		TableColumn collumValue = new TableColumn(editTable, SWT.NONE);
		collumValue.setWidth(300);
		collumValue.setText("参数值");
		collumValue.setResizable(false);

		TableColumn collumEdit = new TableColumn(editTable, SWT.NONE);
		collumEdit.setWidth(editTable.getBounds().width - 600 - editTable.getVerticalBar().getSize().x - 4);
		collumEdit.setText("操作");
		collumEdit.setResizable(false);

		Button saveButton = new Button(cookieEditShell, SWT.NONE);
		saveButton.setBounds(0, 345, 330, 27);
		saveButton.setText("保存并关闭");

		Button canceButton = new Button(cookieEditShell, SWT.NONE);
		canceButton.setText("放弃并关闭");
		canceButton.setBounds(330, 345, 330, 27);

		// 添加可编辑的单元格
		// /******************************************************
		TableItem[] items = editTable.getItems();
		for (int i = 0; i < items.length; i++) {

			final TableEditor editor = new TableEditor(editTable);
			final Text text = new Text(editTable, SWT.NONE);
			text.setText(items[i].getText(0));
			editor.grabHorizontal = true;
			editor.setEditor(text, items[i], 0);
			text.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					editor.getItem().setText(1, text.getText());
				}
			});
			final TableEditor editor1 = new TableEditor(editTable);
			final Text text1 = new Text(editTable, SWT.NONE);
			text.setText(items[i].getText(1));
			editor1.grabHorizontal = true;
			editor1.setEditor(text1, items[i], 1);
			text1.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					editor.getItem().setText(1, text1.getText());
				}
			});
			final TableEditor editor2 = new TableEditor(editTable);
			final Label text2 = new Label(editTable, SWT.NONE|SWT.CENTER);
			text2.setText("删除");
			editor2.grabHorizontal = true;
			editor2.setEditor(text2, items[i], 2);
		}
	}
}
