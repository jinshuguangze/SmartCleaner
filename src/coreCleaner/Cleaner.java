package coreCleaner;

import java.io.*;
import java.util.*;

/**
 * @author jinshu
 * @version 1.7
 */
public class Cleaner {
	// 需要进行一系列处理的文件根目录,比如"C:\\"
	private String rootPath;

	// 过滤器集,一般以文件类型作为区分,比如{".exe"}就是过滤出应用程序的
	private String[] suffix;

	// 是否至少一次检索过目标文件目录
	private boolean hasSearch = false;

	// 文件结构集,key是上级文件,value是下级文件列表
	private HashMap<File, File[]> fileMap = new HashMap<>();

	/**
	 * 构造函数
	 * 
	 * @param rootPath
	 *            根文件地址字符串
	 * @param suffix
	 *            过滤字符串数组
	 */
	public Cleaner(String rootPath, String... suffix) {
		this.rootPath = rootPath;
		this.suffix = suffix;
	}

	/**
	 * 得到浅度搜索目标根文件下的所有文件
	 * 
	 * @param rootPath
	 *            根文件地址字符串
	 * @return 正常文件夹返回文件列表,空文件夹和文件返回空数组,地址不存在返回空
	 */
	private File[] getFiles(String rootPath) {
		File rootFile = new File(rootPath);
		if (rootFile.exists()) {
			if (rootFile.isFile()) {
				return new File[0];
			}
			else {
				return rootFile.listFiles();
			}
		}
		else {
			return null;
		}
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
	private File[] getFiles(String rootPath, String... suffix) {
		File rootFile = new File(rootPath);
		if (rootFile.exists()) {
			if (rootFile.isFile()) {
				return new File[0];
			}
			else {
				SuffixFilter filter = new SuffixFilter(suffix);
				return rootFile.listFiles(filter);
			}
		}
		else {
			return null;
		}
	}

	/**
	 * 得到深度搜索目标根文件下的所有文件
	 * 
	 * @param rootPath
	 *            根文件地址字符串
	 * @return 一个存放文件集合的HashMap
	 */
	private HashMap<File, File[]> getAllFiles(String rootPath) {
		File rootFile = new File(rootPath);
		File[] files = getFiles(rootPath);
		this.fileMap.put(rootFile, files);
		// 如果路径不存在或者是空文件夹或文件,则跳出递归
		if (files != null && files.length != 0) {
			for (File file : files) {
				getAllFiles(file.getPath());
			}
		}
		this.hasSearch = true;
		return this.fileMap;
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
	private HashMap<File, File[]> getAllFiles(String rootPath, String... suffix) {
		// 防止在循环时HashMap发生改变,复制一份
		HashMap<File, File[]> fileMapClone = getAllFiles(rootPath);
		HashMap<File, File[]> fileMapFilter = new HashMap<>();
		// 防止一开始给予的路径就是不存在路径
		if (fileMapClone == null) {
			return null;
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
		return fileMapFilter;
	}

	/**
	 * 移除一个HashMap的父目录以及对应的子目录值
	 * 
	 * @param fileMap
	 *            给予的目标文件结构
	 * @param keyFile
	 *            父目录值
	 * @return 处理后的HashMap
	 */
	private HashMap<File, File[]> removeHashMapKey(HashMap<File, File[]> fileMap, File keyFile) {
		// 防止传进来是个未初始化的HashMap
		if (fileMap != null) {
			fileMap.remove(keyFile);
			// 防止传进来的null参数造成空指针
			if (keyFile != null) {
				removeHashMapValue(fileMap, keyFile.getParentFile(), keyFile);
			}
		}
		return fileMap;
	}

	/**
	 * 移除一个HashMap的父目录数组以及对应的所有子目录值
	 * 
	 * @param fileMap
	 *            给予的目标文件结构
	 * @param keyFiles
	 *            父目录值
	 * @return 处理后的HashMap
	 */
	private HashMap<File, File[]> removeHashMapKey(HashMap<File, File[]> fileMap, File[] keyFiles) {
		// 防止传进来是个未初始化的HashMap
		if (fileMap != null) {
			// 防止空指针
			if (keyFiles != null) {
				for (File file : keyFiles) {
					fileMap.remove(file);
					removeHashMapValue(fileMap, file.getParentFile(), file);
				}
			}
			else {
				fileMap.remove(null);
			}
		}
		return fileMap;
	}

	/**
	 * 移除一个HashMap的子目录值
	 * 
	 * @param fileMap
	 *            给予的目标文件结构
	 * @param keyFile
	 *            父目录值
	 * @param valueFile
	 *            子目录值
	 * @return 处理后的HashMap
	 */
	private HashMap<File, File[]> removeHashMapValue(HashMap<File, File[]> fileMap, File keyFile, File valueFile) {
		// 防止传进来是个未初始化的HashMap
		if (fileMap != null) {
			File[] oldFiles = fileMap.get(keyFile);
			// 防止key在HashMap中不存在
			if (oldFiles != null) {
				Vector<File> newFiles = new Vector<>();
				for (File file : oldFiles) {
					if (!file.equals(valueFile)) {
						newFiles.add(file);
					}
				}
				fileMap.replace(keyFile, newFiles.toArray(new File[newFiles.size()]));
			}
		}
		return fileMap;
	}

	/**
	 * 移除一个HashMap的子目录数组值
	 * 
	 * @param fileMap
	 *            给予的目标文件结构
	 * @param keyFile
	 *            父目录值
	 * @param valueFiles
	 *            子目录值
	 * @return 处理后的HashMap
	 */
	private HashMap<File, File[]> removeHashMapValue(HashMap<File, File[]> fileMap, File keyFile, File[] valueFiles) {
		// 防止传进来是个未初始化的HashMap
		if (fileMap != null) {
			File[] oldFiles = fileMap.get(keyFile);
			// 防止key在HashMap中不存在
			if (oldFiles != null) {
				Vector<File> newFiles = new Vector<>();
				for (File fileO : oldFiles) {
					for (File fileV : valueFiles) {
						if (!fileO.equals(fileV)) {
							newFiles.add(fileO);
						}
					}
				}
				fileMap.replace(keyFile, newFiles.toArray(new File[newFiles.size()]));
			}
		}
		return fileMap;
	}

	/**
	 * 清除目标过滤器下的所有文件
	 */
	public void cleanFilterFile() {
		HashMap<File, File[]> fileMapFilter = getAllFiles(this.rootPath, this.suffix);
		// 垃圾回收器,暂时没有用:(
		System.gc();
		fileMapFilter.forEach((k, v) -> {
			// 防止由于k是不存在路径导致的v为空,此时直接进入下一次循环
			if (v != null) {
				for (File file : v) {
					// 给予虚拟机权限,仍然暂时没有用:(
					file.setExecutable(true);
					if (file.delete()) {
						// 更新文件结构集属性值
						removeHashMapValue(this.fileMap, k, file);
						System.out.println("过滤器" + Arrays.toString(suffix) + "删除成功,路径:" + file.getPath());
					}
					else {
						System.out.println("过滤器" + Arrays.toString(suffix) + "删除失败,路径:" + file.getPath());
					}
				}
			}
		});
	}

	/**
	 * 清除所有空文件夹
	 */
	public void cleanEmptyFolder() {
		// 如果没至少一次检索文件目录就直接调用,那么就检索后递归一次
		if (this.hasSearch) {
			// 防止在循环时HashMap发生改变,复制一份
			HashMap<File, File[]> fileMapClone = this.fileMap;
			// 循环终止条件:一次循环没有成功删除过文件
			while (fileMapClone.size() > 0) {
				// 一个缓存删除成功文件的HashMap
				HashMap<File, File[]> fileMapCache = new HashMap<>();
				fileMapClone.forEach((k, v) -> {
					// k,v防止为空造成空指针,k必须为文件夹,并且v必须为空数组
					if (k != null && v != null && k.isDirectory() && v.length == 0 && k.delete()) {
						File superFile = k.getParentFile();
						// 防止全盘清理出现的空指针
						if (superFile != null) {
							fileMapCache.put(superFile, getFiles(superFile.getPath()));
						}
						System.out.println("空文件夹删除成功,路径:" + k.getPath());
					}
				});
				// 缓存文件内容复制给下一次循环的HashMap
				fileMapClone = fileMapCache;
			}
		}
		else {
			this.fileMap = getAllFiles(this.rootPath);
			cleanEmptyFolder();
		}
	}

	/**
	 * 清理全家桶:>
	 */
	public void cleanAll() {
		cleanFilterFile();
		cleanEmptyFolder();
	}
}