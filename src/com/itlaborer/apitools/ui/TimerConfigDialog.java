package com.itlaborer.apitools.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import com.itlaborer.apitools.res.Resource;
import com.itlaborer.apitools.utils.PubUtils;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

public class TimerConfigDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private boolean saveFlag = false;
	private Button buttonYes;
	private long delay;
	private long intevalPeriod;
	private Text delayText;
	private Text intevalPeriodText;
	private boolean errFlag = false;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public TimerConfigDialog(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object[] open(long delay, long intevalPeriod) {
		this.delay = delay;
		this.intevalPeriod = intevalPeriod;
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return (new Object[] { saveFlag, this.delay, this.intevalPeriod });
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setImage(SWTResourceManager.getImage(TimerConfigDialog.class, Resource.IMAGE_ICON));
		shell.setText("定时器设置");
		shell.setSize(400, 200);
		PubUtils.setCenterinParent(getParent(), shell);

		buttonYes = new Button(shell, SWT.NONE);
		buttonYes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 如果存在错误，不允许保存
				if (errFlag) {
					return;
				}
				delay = new Long(delayText.getText());
				intevalPeriod = new Long(intevalPeriodText.getText());
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

		Label waringText = new Label(shell, SWT.NONE);
		waringText.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		waringText.setBounds(11, 83, 373, 27);

		Label label = new Label(shell, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		label.setBounds(11, 31, 185, 17);
		label.setText("延迟启动时间(毫秒)：");

		Label label_1 = new Label(shell, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		label_1.setText("每次请求间隔时长(毫秒)：");
		label_1.setBounds(202, 31, 182, 17);

		delayText = new Text(shell, SWT.BORDER);
		delayText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				try {
					long time = new Long(delayText.getText());
					if (time < 0) {
						errFlag = true;
						waringText.setText("延迟启动时间只能是大于等于0的数字");
					} else {
						errFlag = false;
						waringText.setText("");
					}
				} catch (Exception e2) {
					errFlag = true;
					waringText.setText("延迟启动时间只能是大于等于0的数字");
				}
			}
		});
		delayText.setBounds(10, 54, 186, 23);
		delayText.setText(delay + "");

		intevalPeriodText = new Text(shell, SWT.BORDER);
		intevalPeriodText.setBounds(202, 54, 182, 23);
		intevalPeriodText.setText(intevalPeriod + "");
		intevalPeriodText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				try {
					long time = new Long(intevalPeriodText.getText());
					if (time < 1) {
						errFlag = true;
						waringText.setText("每次请求间隔时长只能是大于等于1的数字");
					} else if (time < 50) {
						errFlag = false;
						waringText.setText("每次请求间隔时长不建议低于50毫秒,以免造成服务器压力");
					} else {
						errFlag = false;
						waringText.setText("");
					}
				} catch (Exception e2) {
					errFlag = true;
					waringText.setText("每次请求间隔时长只能是大于等于1的数字");
				}
			}
		});

	}
}
