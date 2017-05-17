package coreCleaner;

import java.io.*;
import java.util.*;

public class Reader {

	// 文件结构集,key是上级文件,value是下级文件列表
	private HashMap<File, File[]> fileMap = new HashMap<>();

	// 过滤文件结构集,key是上级文件,value是下级文件列表
	private HashMap<File, File[]> fileMapFilter = new HashMap<>();

	// 需要进行一系列处理的文件根目录,比如"C:\\"
	private final String rootPath;

	// 过滤器集,一般以文件类型作为区分,比如{".exe"}就是过滤出应用程序的
	private String[] suffix;

	/**
	 * 构造函数
	 * 
	 * @param rootPath
	 *            根文件地址字符串
	 * @param suffix
	 *            过滤字符串数组
	 */
	public Reader(String rootPath, String... suffix) {
		this.rootPath = rootPath;
		this.suffix = suffix;
		// 防止在循环时HashMap发生改变,复制一份
		HashMap<File, File[]> fileMapClone = getAllFiles(rootPath);
		HashMap<File, File[]> fileMapFilter = new HashMap<>();
		// 防止一开始给予的路径就是不存在路径
		if (fileMapClone == null) {
			return;
		}
		fileMapClone.forEach((k, v) -> {
			// 防止子目录有不存在路径
			if (v != null) {
				Vector<File> acceptFile = new Vector<>();
				SuffixFilter filter = new SuffixFilter(suffix);
				for (File file : v) {
					if (filter.accept(file.getParentFile(), file.getName())) {
						acceptFile.add(file);
					}
				}
				// 如果没有文件满足过滤器,那么put进去的是个空数组
				fileMapFilter.put(k, acceptFile.toArray(new File[acceptFile.size()]));
			}
		});
		this.fileMapFilter = fileMapFilter;
	}

	/**
	 * 得到过滤器下的浅度搜索目标根文件下的所有文件
	 * 
	 * @param rootPath
	 *            根文件地址字符串
	 * @param suffix
	 *            过滤字符串数组
	 * @return 正常文件夹返回过滤后的文件列表,空文件夹和文件返回空数组,地址不存在返回空
	 */
	public File[] getFiles(String path) {
		File file = new File(path);
		if (file.exists()) {
			if (file.isFile()) {
				return new File[0];
			}
			else {
				return file.listFiles();
			}
		}
		else {
			return null;
		}
	}

	/**
	 * 得到过滤器下的深度搜索目标根文件下的所有文件
	 * 
	 * @param rootPath
	 *            根文件地址字符串
	 * @param suffix
	 *            过滤字符串数组
	 * @return 一个存放过滤器下的文件集合的HashMap
	 */
	public HashMap<File, File[]> getAllFiles(String path) {
		File[] files = getFiles(path);
		this.fileMap.put(new File(path), files);
		// 如果路径不存在或者是空文件夹或文件,则跳出递归
		if (files != null && files.length != 0) {
			for (File file : files) {
				getAllFiles(file.getPath());
			}
		}
		return this.fileMap;
	}

	public HashMap<File, File[]> getFileMap() {
		return fileMap;
	}

	public HashMap<File, File[]> getFileMapFilter() {
		return fileMapFilter;
	}

	public String getRootPath() {
		return rootPath;
	}

	public String[] getSuffix() {
		return suffix;
	}

	public void setSuffix(String[] suffix) {
		this.suffix = suffix;
		// TODO
	}

}