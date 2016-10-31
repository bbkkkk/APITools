package com.itlaborer.apitools.ui;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import com.itlaborer.apitools.res.Resource;
import com.itlaborer.apitools.swt.SWTResourceManager;
import com.itlaborer.apitools.utils.ApiUtils;

/**
 * 关于界面
 * 
 * @author liudewei[793554262@qq.com]
 * @version 1.0
 * @since 1.0
 */

public class AboutTools extends Dialog {

	private static Logger logger = Logger.getLogger(AboutTools.class.getName());
	protected Object result;
	protected Shell aboutToolsShell;

	public AboutTools(Shell parent, int style) {
		super(parent, style);
		setText("关于此工具");
		logger.info("进入关于");
	}

	public Object open() {
		createContents();
		aboutToolsShell.open();
		aboutToolsShell.layout();
		Display display = getParent().getDisplay();

		while (!aboutToolsShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	private void createContents() {
		aboutToolsShell = new Shell(getParent(), getStyle());
		aboutToolsShell.setImage(SWTResourceManager.getImage(AboutTools.class, Resource.IMAGE_ICON));
		aboutToolsShell.setSize(400, 391);
		aboutToolsShell.setText(getText());
		ApiUtils.SetCenterinParent(getParent(), aboutToolsShell);

		Label copyRightlabel = new Label(aboutToolsShell, SWT.CENTER);
		copyRightlabel.setBounds(10, 311, 375, 17);
		copyRightlabel.setText(Resource.AUTHOR);

		Link link = new Link(aboutToolsShell, SWT.NONE);
		link.setBounds(143, 336, 108, 17);
		link.setText("<a>www.itlaborer.com</a>");
		link.addSelectionListener(new LinkSelection());

		Label versionLabel = new Label(aboutToolsShell, SWT.NONE);
		versionLabel.setBounds(184, 288, 25, 17);
		versionLabel.setText(Resource.VERSION);

		Label readMeTextLabel = new Label(aboutToolsShell, SWT.BORDER | SWT.WRAP | SWT.SHADOW_NONE);
		readMeTextLabel.setBounds(10, 33, 375, 114);
		readMeTextLabel.setText(Resource.EXPLAIN);

		Label lblgplV = new Label(aboutToolsShell, SWT.BORDER | SWT.WRAP | SWT.SHADOW_NONE);
		lblgplV.setText(
				"本程序遵从XXX开源协议开源");
		lblgplV.setBounds(10, 176, 375, 106);

		Label label_1 = new Label(aboutToolsShell, SWT.NONE);
		label_1.setFont(org.eclipse.wb.swt.SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		label_1.setText("版权声明：");
		label_1.setBounds(10, 153, 57, 17);

		Label label_2 = new Label(aboutToolsShell, SWT.NONE);
		label_2.setFont(org.eclipse.wb.swt.SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		label_2.setText("软件说明：");
		label_2.setBounds(10, 10, 57, 17);
	}

	// 打开网站
	private final class LinkSelection extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			logger.info("访问作者的博客");
			Program.launch(Resource.BLOG);
		}
	}
}
