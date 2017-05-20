package coreCleaner;

import java.io.*;
import java.util.*;

/**
 * @author jinshuguangze
 * @version 1.8
 */
public class Cleaner {

	private Reader aReader;

	/**
	 * 构造函数
	 * 
	 * @param aReader
	 *            Reader对象
	 */
	public Cleaner(Reader aReader) {
		this.aReader = aReader;
	}

	/**
	 * 清除目标过滤器下的所有文件
	 */
	public void cleanFilterFile() {
		aReader.getFileMapFilter().forEach((k, v) -> {
			// 防止由于k是不存在路径导致的v为空,此时直接进入下一次循环
			if (v != null) {
				for (File file : v) {
					// 给予虚拟机权限,仍然暂时没有用:(
					file.setExecutable(true);
					if (file.delete()) {
						// 更新文件结构集属性值
						Toolkit.removeHashMapValue(aReader.getFileMap(), k, file);
						System.out.println("过滤器" + Arrays.toString(aReader.getSuffix()) + "删除成功,路径:" + file.getPath());
					}
					else {
						System.out.println("过滤器" + Arrays.toString(aReader.getSuffix()) + "删除失败,路径:" + file.getPath());
					}
				}
			}
		});
	}

	/**
	 * 清除所有空文件夹
	 */
	public void cleanEmptyFolder() {
		// 防止在循环时HashMap发生改变,复制一份
		HashMap<File, File[]> fileMapClone = aReader.getFileMap();
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
						fileMapCache.put(superFile, aReader.getFiles(superFile.getPath()));
					}
					System.out.println("空文件夹删除成功,路径:" + k.getPath());
				}
			});
			// 缓存文件内容复制给下一次循环的HashMap
			fileMapClone = fileMapCache;
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