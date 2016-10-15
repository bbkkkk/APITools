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
import org.eclipse.wb.swt.SWTResourceManager;

import com.itlaborer.apitools.res.Resource;
import com.itlaborer.apitools.utils.ApiUtils;

/**
 * 
 * @author liudewei[793554262@qq.com]
 * @see 关于界面
 * @version 1.0
 * @since 1.0
 * 
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
		aboutToolsShell.setImage(SWTResourceManager.getImage(AboutTools.class, "/com/itlaborer/apitools/res/icon.ico"));
		aboutToolsShell.setSize(400, 218);
		aboutToolsShell.setText(getText());
		ApiUtils.SetCenterinParent(getParent(), aboutToolsShell);

		Label copyRightlabel = new Label(aboutToolsShell, SWT.CENTER);
		copyRightlabel.setBounds(10, 132, 375, 17);
		copyRightlabel.setText(Resource.AUTHOR);

		Link link = new Link(aboutToolsShell, SWT.NONE);
		link.setBounds(143, 157, 108, 17);
		link.setText("<a>www.itlaborer.com</a>");
		link.addSelectionListener(new LinkSelection());

		Label versionLabel = new Label(aboutToolsShell, SWT.NONE);
		versionLabel.setBounds(184, 109, 25, 17);
		versionLabel.setText(Resource.VERSION);

		Label readMeTextLabel = new Label(aboutToolsShell, SWT.WRAP);
		readMeTextLabel.setBounds(10, 20, 375, 82);
		readMeTextLabel
				.setText(Resource.EXPLAIN);
	}

	// 打开网站
	private final class LinkSelection extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			logger.info("访问作者的博客");
			Program.launch(Resource.BLOG);
		}
	}
}
