package coreCleaner;

import java.io.*;

/**
 * @author jinshuguangze
 * @version 1.1
 */
public class SuffixFilter implements FilenameFilter {
	// 过滤器集,一般以文件类型作为区分,比如{".exe"}就是过滤出应用程序的
	private String[] suffix;

	/**
	 * 构造函数
	 * 
	 * @param suffix
	 *            过滤字符串数组
	 */
	public SuffixFilter(String[] suffix) {
		this.suffix = suffix;
	}

	/**
	 * 重写的过滤方法,使用或运算符号得出是否通过
	 * 
	 * @param dir
	 *            要过滤的文件
	 * @param name
	 *            文件名
	 * @return 如果通过过滤就是true,没有通过就是false
	 */
	@Override
	public boolean accept(File dir, String name) {
		boolean pass = false;
		for (int i = 0; i < suffix.length; i++) {
			pass = pass || name.endsWith(suffix[i]);
		}
		return pass;
	}
}