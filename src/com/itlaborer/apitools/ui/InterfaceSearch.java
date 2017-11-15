package com.itlaborer.apitools.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.itlaborer.apitools.res.KeyCode;
import com.itlaborer.apitools.utils.PubUtils;

public class InterfaceSearch extends Dialog {

	public InterfaceSearch(Shell parent, int style) {
		super(parent, style);
	}

	protected Shell shell;
	private Text text;
	private Table table;
	private ArrayList<HashMap<String, String>> queryList;

	// 返回结果
	boolean flag = false;
	private String UUID;

	/**
	 * Open the window.
	 */
	public Object[] open(ArrayList<HashMap<String, String>> queryList) {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		this.queryList = queryList;
		disPlayTable("");
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return new Object[] { flag, UUID };
	}

	protected void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setImage(SWTResourceManager.getImage(InterfaceSearch.class, "/com/itlaborer/apitools/res/icon.ico"));
		shell.setSize(538, 400);
		shell.setText("查找接口");
		PubUtils.setCenterinParent(getParent(), shell);

		text = new Text(shell, SWT.BORDER);
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == KeyCode.ENTER) {
					disPlayTable(text.getText());
				}
			}
		});
		text.setBounds(0, 3, 449, 23);

		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(0, 29, 533, 342);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		addDoubleClick(table);

		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setResizable(false);
		tableColumn.setWidth(203);
		tableColumn.setText("接口名字");

		TableColumn tblclmnid = new TableColumn(table, SWT.NONE);
		tblclmnid.setWidth(305);
		tblclmnid.setText("唯一ID");

		Button button = new Button(shell, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				disPlayTable(text.getText());
			}
		});
		button.setBounds(450, 2, 83, 25);
		button.setText("查找");
	}

	private void addDoubleClick(Table table) {
		table.addMouseListener(new MouseAdapter() {
			// 双击鼠标打开此行记录
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if (table.getSelectionIndex() >= 0) {
					open(table.getItem(table.getSelectionIndex()).getText(1));
				}
			}

			void open(String uuid) {
				if (StringUtils.isNoneEmpty(uuid)) {
					UUID = uuid;
					flag = true;
				} else {
					flag = false;
				}
				shell.dispose();
			}
		});
	}

	private void disPlayTable(String search) {
		// 显示前清理原有数据
		int oldRows = table.getItemCount();
		if (oldRows > 0) {
			for (int i = oldRows - 1; i >= 0; i--) {
				table.getItem(i).dispose();
			}
		}
		int sizeAll = 0;
		if (null == queryList) {
			return;
		}
		// 可用服务总行数
		sizeAll = queryList.size();
		table.setItemCount(sizeAll);
		int i = 0;
		if (StringUtils.isEmpty(search)) {
			for (int index = 0; index < sizeAll; index++) {
				table.getItem(index)
						.setText(new String[] { queryList.get(index).get("name"), queryList.get(index).get("uuid") });
			}
		} else {
			for (int index = 0; index < sizeAll; index++) {
				if (StringUtils.containsIgnoreCase(queryList.get(index).get("name"), search)) {
					table.getItem(i).setText(
							new String[] { queryList.get(index).get("name"), queryList.get(index).get("uuid") });
					i++;
				}
			}
		}
	}
}
