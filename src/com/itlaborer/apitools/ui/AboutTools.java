package com.itlaborer.apitools.ui;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
 * 此界面是软件的关于界面，可以在open方法中传入解释和版本信息
 * 
 * @author liudewei[793554262@qq.com]
 * @version 1.1
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

	/**
	 * oepn方法
	 * 
	 * @param exlplain
	 * @param version
	 * @return
	 */
	public Object open(String exlplain, String version) {
		createContents(exlplain, version);
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

	private void createContents(String exlplain, String version) {
		aboutToolsShell = new Shell(getParent(), getStyle());
		aboutToolsShell.setImage(SWTResourceManager.getImage(AboutTools.class, Resource.IMAGE_ICON));
		aboutToolsShell.setSize(400, 391);
		aboutToolsShell.setText(getText());
		PubUtils.setCenterinParent(getParent(), aboutToolsShell);

		Label copyRightlabel = new Label(aboutToolsShell, SWT.CENTER);
		copyRightlabel.setBounds(10, 311, 375, 17);
		copyRightlabel.setText(Resource.AUTHOR);

		Link link = new Link(aboutToolsShell, SWT.NONE);
		link.setBounds(143, 336, 108, 17);
		link.setText("<a>www.itlaborer.com</a>");
		link.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.info("访问作者的博客");
				Program.launch(Resource.BLOG);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Label versionLabel = new Label(aboutToolsShell, SWT.NONE);
		versionLabel.setAlignment(SWT.CENTER);
		versionLabel.setBounds(156, 288, 82, 17);
		versionLabel.setText(version);

		StyledText readMeTextLabel = new StyledText(aboutToolsShell,
				SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		readMeTextLabel.setBounds(3, 33, 389, 114);
		readMeTextLabel.setText(exlplain);

		StyledText lblgplV = new StyledText(aboutToolsShell, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		lblgplV.setText(
				"Copyright itlaborer.com\n\nLicensed under the Apache License, Version 2.0 (the \"License\");\nyou may not use this file except in compliance with the License.\nYou may obtain a copy of the License at\n\n    http://www.apache.org/licenses/LICENSE-2.0\n\nUnless required by applicable law or agreed to in writing, software\ndistributed under the License is distributed on an \"AS IS\" BASIS,\nWITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\nSee the License for the specific language governing permissions and\nlimitations under the License.");
		lblgplV.setBounds(3, 176, 389, 106);

		Label label_1 = new Label(aboutToolsShell, SWT.NONE);
		label_1.setFont(org.eclipse.wb.swt.SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		label_1.setText("Apache License:");
		label_1.setBounds(3, 155, 136, 17);

		Label label_2 = new Label(aboutToolsShell, SWT.NONE);
		label_2.setFont(org.eclipse.wb.swt.SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		label_2.setText("软件说明:");
		label_2.setBounds(3, 12, 136, 17);
	}
}
