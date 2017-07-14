package com.itlaborer.apitools.ui;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
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
import com.itlaborer.apitools.utils.PubUtils;

/**
 * 快捷键说明界面
 * 
 * @author liudewei[793554262@qq.com]
 * @version 1.0
 * @since 1.0
 */

public class ShortcutKeyExplain extends Dialog {

	private static Logger logger = Logger.getLogger(ShortcutKeyExplain.class.getName());
	protected Object result;
	protected Shell aboutToolsShell;

	public ShortcutKeyExplain(Shell parent, int style) {
		super(parent, style);
		setText("快捷键");
		logger.info("进入快捷键说明");
	}

	public Object open(String str) {
		createContents(str);
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

	private void createContents(String str) {
		aboutToolsShell = new Shell(getParent(), getStyle());
		aboutToolsShell.setImage(SWTResourceManager.getImage(ShortcutKeyExplain.class, Resource.IMAGE_ICON));
		aboutToolsShell.setSize(400, 391);
		aboutToolsShell.setText(getText());
		PubUtils.setCenterinParent(getParent(), aboutToolsShell);

		Link link = new Link(aboutToolsShell, SWT.NONE);
		link.setBounds(143, 336, 108, 17);
		link.setText("<a>www.itlaborer.com</a>");
		link.addSelectionListener(new LinkSelection());

		StyledText readMeTextLabel = new StyledText(aboutToolsShell,
				SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		readMeTextLabel.setBounds(3, 33, 389, 297);
		readMeTextLabel.setText(str);

		Label label_2 = new Label(aboutToolsShell, SWT.NONE);
		label_2.setFont(org.eclipse.wb.swt.SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		label_2.setText("快捷键说明:");
		label_2.setBounds(3, 12, 136, 17);
	}

	// 打开网站
	private final class LinkSelection extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			logger.info("访问作者的博客");
			Program.launch(Resource.BLOG);
		}
	}
}
