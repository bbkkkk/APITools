package com.itlaborer.apitools.ui;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.itlaborer.apitools.res.KeyCode;
import com.itlaborer.apitools.res.Resource;
import com.itlaborer.apitools.utils.PubUtils;

/**
 * styletext搜索器,对指定的styletext进行内容搜索
 * 
 * @author liu
 *
 */
public class TextSearch extends Dialog {

	// 日志组件
	private static Logger logger = Logger.getLogger(TextSearch.class.getName());
	protected Shell shell;
	private Text text_1;
	private StyledText styleText;
	private Label lblNewLabel;
	private int index = 0;
	private Button button_1;

	private boolean lastIsZero = false;

	public TextSearch(Shell parent, int style) {
		super(parent, style);
	}

	public void open(StyledText styleText) {
		this.styleText = styleText;
		index = styleText.getSelection().x;
		logger.debug("定位搜索索引位置:" + index);
		createContents();
		shell.open();
		shell.layout();
		// 窗口活动标志
		shell.addListener(SWT.Activate, new Listener() {
			@Override
			public void handleEvent(Event event) {
				shell.setAlpha(255);
				index = styleText.getSelection().x;
				logger.debug("定位搜索索引位置:" + index);
				logger.debug(shell.hashCode() + "窗口获得焦点");
			}
		});
		shell.addListener(SWT.Deactivate, new Listener() {
			@Override
			public void handleEvent(Event event) {
				shell.setAlpha(156);
				logger.debug(shell.hashCode() + "窗口失去焦点");
			}
		});
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public void updateTextAndActiveWindow(StyledText newStyleText) {
		if (newStyleText == styleText) {
			// 继续搜索原来的表格
			shell.setActive();
			// 窗口激活后校准索引位置
			index = styleText.getSelection().x;
		}
		// 新的表格
		else {
			styleText = newStyleText;
			shell.setActive();
		}
	}

	/**
	 * 获取窗口shell
	 * 
	 * @return
	 */
	public Shell getshell() {
		return shell;
	}

	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setImage(SWTResourceManager.getImage(MySelectionDialog.class, Resource.IMAGE_ICON));
		shell.setSize(400, 199);
		PubUtils.setCenterinParent(getParent(), shell);
		shell.setText("文本搜索");

		text_1 = new Text(shell, SWT.BORDER);
		text_1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == KeyCode.ENTER && (e.stateMask == SWT.SHIFT)) {
					searchPre();
					return;
				}
				if (e.keyCode == KeyCode.ENTER) {
					searchNext();
					return;
				}
			}
		});
		text_1.setBounds(10, 24, 374, 23);

		button_1 = new Button(shell, SWT.CHECK);
		button_1.setBounds(10, 65, 98, 17);
		button_1.setText("区分大小写");

		Button buttonYes = new Button(shell, SWT.NONE);
		buttonYes.setToolTipText("输入搜索内容后直接按Enter键往下搜索");
		buttonYes.setBounds(134, 124, 78, 27);
		buttonYes.setText("往下搜索");
		buttonYes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				searchNext();
			}
		});

		Button button = new Button(shell, SWT.NONE);
		button.setToolTipText("输入搜索内容后直接按Shift+Enter键往上搜索");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				searchPre();
			}
		});
		button.setText("往上搜索");
		button.setBounds(218, 124, 80, 27);

		Button buttonNo = new Button(shell, SWT.NONE);
		buttonNo.setBounds(304, 124, 80, 27);
		buttonNo.setText("关闭");
		buttonNo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});

		lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 10, SWT.NORMAL));
		lblNewLabel.setBounds(10, 88, 374, 23);

		text_1.setFocus();
	}

	/**
	 * 往下搜索
	 */
	private void searchNext() {
		lblNewLabel.setText("");
		if (StringUtils.isNotEmpty(text_1.getText()) && StringUtils.isNotEmpty(styleText.getText())) {
			boolean ignoreCase = !button_1.getSelection();
			int myIndex = index;
			// 往下搜索是从下一个位置开始搜索,除非光标在0位置时，要搜索第一个字符
			if (myIndex == 0 && !lastIsZero) {
				myIndex = 0;
			} else {
				myIndex++;
			}
			logger.debug("从第[" + myIndex + "]个字符开始向下查找[" + text_1.getText() + "]");
			// 查找
			if (ignoreCase) {
				myIndex = StringUtils.indexOfIgnoreCase(styleText.getText(), text_1.getText(), myIndex);
			} else {
				myIndex = StringUtils.indexOf(styleText.getText(), text_1.getText(), myIndex);
			}
			// 标记是不是在第一个位置找到了需要的内容
			if (myIndex == 0) {
				lastIsZero = true;
			}
			if (myIndex >= 0) {
				index = myIndex;
				logger.debug("搜索索引被标记为了:" + index);
				// 跳转
				styleText.setSelection(index);
				// 标记
				styleText.setSelection(index, index + text_1.getText().length());
			} else {
				lblNewLabel.setText("再往下没有你要查找的内容了");
				logger.debug("再往下没有你要查找的内容了");
			}
		}
	}

	/**
	 * 往上搜索
	 */
	private void searchPre() {
		if (StringUtils.isNotEmpty(text_1.getText()) && StringUtils.isNotEmpty(styleText.getText())) {
			boolean ignoreCase = !button_1.getSelection();
			int myIndex = index;
			lblNewLabel.setText("");
			logger.debug("从第[" + myIndex + "]个字符开始向上查找[" + text_1.getText() + "]");
			// 查找
			if (ignoreCase) {
				myIndex = StringUtils.lastIndexOfIgnoreCase(styleText.getText().substring(0, index), text_1.getText());
			} else {
				myIndex = StringUtils.lastIndexOf(styleText.getText().substring(0, index), text_1.getText());
			}
			// 标记是不是在第一个位置找到了需要的内容
			if (myIndex == 0) {
				lastIsZero = true;
			}
			if (myIndex < 0) {
				logger.debug("再往上没有你要查找的内容了");
				lblNewLabel.setText("再往上没有你要查找的内容了");
			} else {
				index = myIndex;
				logger.debug("搜索索引被标记为了:" + index);
				// 跳转
				styleText.setSelection(index);
				// 标记
				styleText.setSelection(index, index + text_1.getText().length());
			}
		}
	}
}
