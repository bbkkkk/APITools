package com.itlaborer.utils;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import net.dongliu.requests.RawResponse;
import net.dongliu.requests.Requests;

/**
 * 
 * 工具类
 * 
 * @author liu
 *
 */
public class ApiUtils {

	private static Logger logger = Logger.getLogger(ApiUtils.class.getName());

	public ApiUtils() {
	}

	// MD5字符串算法
	public final static String MD5(String s) {

		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			logger.error("异常", e);
			return null;
		}
	}

	// HTTP GET 忽略证书安全
	public static RawResponse HttpGet(String url, HashMap<String, String> parameter,
			LinkedHashMap<String, String> header, LinkedHashMap<String, String> cookies, Charset requestCharset)
			throws Exception {
		RawResponse resp = Requests.get(url).verify(false).headers(header).cookies(cookies).params(parameter)
				.requestCharset(requestCharset).send();
		return resp;
	}

	// HTTP POST 忽略证书安全
	public static RawResponse HttpPost(String url, HashMap<String, String> parameter,
			LinkedHashMap<String, String> header, LinkedHashMap<String, String> cookies, Charset requestCharset)
			throws Exception {
		RawResponse resp = Requests.post(url).verify(false).headers(header).cookies(cookies).forms(parameter)
				.requestCharset(requestCharset).send();
		return resp;
	}

	// HTTP HEAD 忽略证书安全
	public static RawResponse HttpHead(String url, HashMap<String, String> parameter,
			LinkedHashMap<String, String> header, LinkedHashMap<String, String> cookies, Charset requestCharset)
			throws Exception {
		RawResponse resp = Requests.head(url).verify(false).headers(header).cookies(cookies).params(parameter)
				.requestCharset(requestCharset).send();
		return resp;
	}

	// HTTP PUT 忽略证书安全
	public static RawResponse HttpPut(String url, HashMap<String, String> parameter,
			LinkedHashMap<String, String> header, LinkedHashMap<String, String> cookies, Charset requestCharset)
			throws Exception {
		RawResponse resp = Requests.put(url).verify(false).headers(header).cookies(cookies).forms(parameter)
				.requestCharset(requestCharset).send();
		return resp;
	}

	// HTTP PATCH 忽略证书安全
	public static RawResponse HttpPatch(String url, HashMap<String, String> parameter,
			LinkedHashMap<String, String> header, LinkedHashMap<String, String> cookies, Charset requestCharset)
			throws Exception {
		RawResponse resp = Requests.patch(url).verify(false).headers(header).cookies(cookies).forms(parameter)
				.requestCharset(requestCharset).send();
		return resp;
	}

	// HTTP DELETE 忽略证书安全
	public static RawResponse HttpDelete(String url, HashMap<String, String> parameter,
			LinkedHashMap<String, String> header, LinkedHashMap<String, String> cookies, Charset requestCharset)
			throws Exception {
		RawResponse resp = Requests.delete(url).verify(false).headers(header).cookies(cookies).forms(parameter)
				.requestCharset(requestCharset).send();
		return resp;
	}

	// 设置程序窗口居中
	public static void SetCenter(Shell shell) {
		int screenH = Toolkit.getDefaultToolkit().getScreenSize().height;
		int screenW = Toolkit.getDefaultToolkit().getScreenSize().width;
		int shellH = shell.getBounds().height;
		int shellW = shell.getBounds().width;
		if (shellH > screenH) {
			shellH = screenH;
		}
		if (shellW > screenW) {
			shellW = screenW;
		}
		shell.setLocation(((screenW - shellW) / 2), ((screenH - shellH) / 2));
	}

	// 设置程序窗口居中于父窗口
	public static void SetCenterinParent(Shell parentshell, Shell shell) {
		int screenH = Toolkit.getDefaultToolkit().getScreenSize().height;
		int screenW = Toolkit.getDefaultToolkit().getScreenSize().width;
		int shellH = shell.getBounds().height;
		int shellW = shell.getBounds().width;
		// 如果窗口大小超过屏幕大小，调整为屏幕大小
		if (shellH > screenH) {
			shellH = screenH;
		}
		if (shellW > screenW) {
			shellW = screenW;
		}
		int targetx = parentshell.getLocation().x + parentshell.getBounds().width / 2 - shell.getBounds().width / 2;
		int targety = parentshell.getLocation().y + parentshell.getBounds().height / 2 - shell.getBounds().height / 2;
		if (targetx + shellW > screenW) {
			targetx = screenW - shellW;
		}
		if (targety + shellH > screenH) {
			targety = screenH - shellH;
		}
		shell.setLocation(targetx, targety);
	}

	// 读取文件到String字符串
	public static String ReadFromFile(File file, String encoding) {
		InputStreamReader reader = null;
		StringWriter writer = new StringWriter();
		try {
			if (encoding == null || "".equals(encoding.trim())) {
				reader = new InputStreamReader(new FileInputStream(file), encoding);
			} else {
				reader = new InputStreamReader(new FileInputStream(file));
			}
			char[] buffer = new char[4096];
			int n = 0;
			while (-1 != (n = reader.read(buffer))) {
				writer.write(buffer, 0, n);
			}
		} catch (Exception e) {
			logger.error("异常", e);
			return null;
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					logger.error("异常", e);
				}
		}
		return writer.toString();
	}

	// String保存到文件方法
	public static boolean SaveToFile(File file, String content) {
		FileWriter fwriter = null;
		try {
			fwriter = new FileWriter(file);
			fwriter.write(content);
			fwriter.flush();
			fwriter.close();
			return true;
		} catch (Exception ex) {
			logger.error("捕获异常", ex);
			return false;
		}
	}

	// 文件复制方法
	public static long CopyFile(File f1, File f2) throws Exception {
		long time = new Date().getTime();
		int length = 2097152;
		FileInputStream in = new FileInputStream(f1);
		FileOutputStream out = new FileOutputStream(f2);
		byte[] buffer = new byte[length];
		while (true) {
			int ins = in.read(buffer);
			if (ins == -1) {
				in.close();
				out.flush();
				out.close();
				return new Date().getTime() - time;
			} else
				out.write(buffer, 0, ins);
		}
	}

	// String编码转换方法
	public static String GetUTF8StringFromGBKString(String gbkStr) {
		try {
			return new String(GetUTF8BytesFromGBKString(gbkStr), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new InternalError();
		}
	}

	// GBK字符串转换到UTF-8
	public static byte[] GetUTF8BytesFromGBKString(String gbkStr) {
		int n = gbkStr.length();
		byte[] utfBytes = new byte[3 * n];
		int k = 0;
		for (int i = 0; i < n; i++) {
			int m = gbkStr.charAt(i);
			if (m < 128 && m >= 0) {
				utfBytes[k++] = (byte) m;
				continue;
			}
			utfBytes[k++] = (byte) (0xe0 | (m >> 12));
			utfBytes[k++] = (byte) (0x80 | ((m >> 6) & 0x3f));
			utfBytes[k++] = (byte) (0x80 | (m & 0x3f));
		}
		if (k < utfBytes.length) {
			byte[] tmp = new byte[k];
			System.arraycopy(utfBytes, 0, tmp, 0, k);
			return tmp;
		}
		return utfBytes;
	}

	// 删除文件夹的方法
	public static boolean DeleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = DeleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	// 字符串转unicode
	public static String string2Unicode(String string) {

		StringBuffer unicode = new StringBuffer();
		for (int i = 0; i < string.length(); i++) {
			// 取出每一个字符
			char c = string.charAt(i);
			String unicodestring = Integer.toHexString(c);
			// 英文的话，要补全四位
			if (unicodestring.length() == 2) {
				unicodestring = "00" + unicodestring;
			}
			// 转换为unicode
			unicode.append("\\u" + unicodestring);
		}
		return unicode.toString();
	}

	// 字符串转unicode-只转换中日韩
	public static String stringzh2Unicode(String string) {

		StringBuffer unicode = new StringBuffer();
		for (int i = 0; i < string.length(); i++) {
			// 取出每一个字符
			char c = string.charAt(i);
			// 转换为unicode
			if (isChinese(c)) {
				unicode.append("\\u" + Integer.toHexString(c));
			} else {
				unicode.append(c);
			}
		}
		return unicode.toString();
	}

	// unicode串解码
	public static String unicode2String(String str) {

		Charset set = Charset.forName("UTF-16");
		Pattern p = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
		Matcher m = p.matcher(str);
		int start = 0;
		int start2 = 0;
		StringBuffer sb = new StringBuffer();
		while (m.find(start)) {
			start2 = m.start();
			if (start2 > start) {
				String seg = str.substring(start, start2);
				sb.append(seg);
			}
			String code = m.group(1);
			int i = Integer.valueOf(code, 16);
			byte[] bb = new byte[4];
			bb[0] = (byte) ((i >> 8) & 0xFF);
			bb[1] = (byte) (i & 0xFF);
			ByteBuffer b = ByteBuffer.wrap(bb);
			sb.append(String.valueOf(set.decode(b)).trim());
			start = m.end();
		}
		start2 = str.length();
		if (start2 > start) {
			String seg = str.substring(start, start2);
			sb.append(seg);
		}
		return sb.toString();
	}

	// 判断字符是否是中日韩字符
	private static final boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	// 给不支持右键菜单的StyledText添加右键菜单
	public static void StyledTextAddContextMenu(StyledText styledText) {
		Menu popupMenu = new Menu(styledText);
		MenuItem cut = new MenuItem(popupMenu, SWT.NONE);
		cut.setText("剪切");
		MenuItem copy = new MenuItem(popupMenu, SWT.NONE);
		copy.setText("复制");
		MenuItem paste = new MenuItem(popupMenu, SWT.NONE);
		paste.setText("粘贴");
		MenuItem allSelect = new MenuItem(popupMenu, SWT.NONE);
		allSelect.setText("全选");
		MenuItem clear = new MenuItem(popupMenu, SWT.NONE);
		clear.setText("清空");
		MenuItem warp = new MenuItem(popupMenu, SWT.NONE);
		warp.setText("自动换行");
		styledText.setMenu(popupMenu);

		// 判断初始自动换行状态
		if (styledText.getWordWrap()) {
			warp.setText("关闭自动换行");
		} else {
			warp.setText("打开自动换行");
		}
		styledText.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.stateMask == SWT.CTRL && e.keyCode == 'a') {
					styledText.selectAll();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
			}
		});
		// 剪切菜单的点击事件
		cut.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (styledText.getSelectionCount() == 0) {
					return;
				}
				Clipboard clipboard = new Clipboard(styledText.getDisplay());
				String plainText = styledText.getSelectionText();
				TextTransfer textTransfer = TextTransfer.getInstance();
				clipboard.setContents(new String[] { plainText }, new Transfer[] { textTransfer });
				clipboard.dispose();
				// 将已经剪切走的部分删除,并将插入符移动到剪切位置
				int caretOffset = styledText.getSelection().x;
				styledText.setText(new StringBuffer(styledText.getText())
						.replace(styledText.getSelection().x, styledText.getSelection().y, "").toString());
				styledText.setCaretOffset(caretOffset);
			}
		});

		// 粘贴菜单的点击事件
		paste.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Clipboard clipboard = new Clipboard(styledText.getDisplay());
				TextTransfer textTransfer = TextTransfer.getInstance();
				// 获取剪切板上的文本
				String cliptext = (clipboard.getContents(textTransfer) != null
						? clipboard.getContents(textTransfer).toString() : "");
				clipboard.dispose();
				int caretOffset = styledText.getSelection().x;
				styledText.setText(new StringBuffer(styledText.getText())
						.replace(styledText.getSelection().x, styledText.getSelection().y, cliptext).toString());
				styledText.setCaretOffset(caretOffset + cliptext.length());
			}
		});

		// 复制上下文菜单的点击事件
		copy.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (styledText.getSelectionCount() == 0) {
					return;
				}
				Clipboard clipboard = new Clipboard(styledText.getDisplay());
				String plainText = styledText.getSelectionText();
				TextTransfer textTransfer = TextTransfer.getInstance();
				clipboard.setContents(new String[] { plainText }, new Transfer[] { textTransfer });
				clipboard.dispose();
			}
		});

		// 全选上下文菜单的点击事件
		allSelect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				styledText.selectAll();
			}
		});

		// 清空上下文菜单的点击事件
		clear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				styledText.setText("");
			}
		});

		// 更改是否自动换行
		warp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (styledText.getWordWrap()) {
					styledText.setWordWrap(false);
					warp.setText("打开自动换行");
				} else {
					styledText.setWordWrap(true);
					warp.setText("关闭自动换行");
				}
			}
		});
	}

	public static void DropTargetSupport(Shell shell) {

		DropTarget dropTarget = new DropTarget(shell, DND.DROP_NONE);
		Transfer[] transfer = new Transfer[] { FileTransfer.getInstance() };
		dropTarget.setTransfer(transfer);
		// 拖拽监听
		dropTarget.addDropListener(new DropTargetListener() {
			@Override
			public void dragEnter(DropTargetEvent event) {
			}

			@Override
			public void dragLeave(DropTargetEvent event) {
			}

			@Override
			public void dragOperationChanged(DropTargetEvent event) {
			}

			@Override
			public void dragOver(DropTargetEvent event) {
			}

			// 获取拖放进来的文件，暂无用途
			@Override
			public void drop(DropTargetEvent event) {
				String[] files = (String[]) event.data;
				for (int i = 0; i < files.length; i++) {
					@SuppressWarnings("unused")
					File file = new File(files[i]);
				}
			}
			@Override
			public void dropAccept(DropTargetEvent event) {
			}
		});
	}
}
