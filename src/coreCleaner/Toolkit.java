package coreCleaner;

import java.io.*;
import java.util.*;

/**
 * @author jinshuguangze
 * @version 1.3
 */
public class Toolkit {

	/**
	 * 移除一个TreeMap的父目录以及对应的子目录值
	 * 
	 * @param fileMap
	 *            给予的目标文件结构
	 * @param keyFile
	 *            父目录值
	 * @return 处理后的TreeMap
	 */
	public static TreeMap<File, File[]> removeTreeMapKey(TreeMap<File, File[]> fileMap, File keyFile) {
		// 防止传进来是个未初始化的TreeMap
		if (fileMap != null) {
			fileMap.remove(keyFile);
			// 防止传进来的null参数造成空指针
			if (keyFile != null) {
				removeTreeMapValue(fileMap, keyFile.getParentFile(), keyFile);
			}
		}
		return fileMap;
	}

	/**
	 * 移除一个TreeMap的父目录数组以及对应的所有子目录值
	 * 
	 * @param fileMap
	 *            给予的目标文件结构
	 * @param keyFiles
	 *            父目录值
	 * @return 处理后的TreeMap
	 */
	public static TreeMap<File, File[]> removeTreeMapKey(TreeMap<File, File[]> fileMap, File[] keyFiles) {
		// 防止传进来是个未初始化的TreeMap
		if (fileMap != null) {
			// 防止空指针
			if (keyFiles != null) {
				for (File file : keyFiles) {
					fileMap.remove(file);
					removeTreeMapValue(fileMap, file.getParentFile(), file);
				}
			}
			else {
				fileMap.remove(null);
			}
		}
		return fileMap;
	}

	/**
	 * 移除一个TreeMap的子目录值
	 * 
	 * @param fileMap
	 *            给予的目标文件结构
	 * @param keyFile
	 *            父目录值
	 * @param valueFile
	 *            子目录值
	 * @return 处理后的TreeMap
	 */
	public static TreeMap<File, File[]> removeTreeMapValue(TreeMap<File, File[]> fileMap, File keyFile,
			File valueFile) {
		// 防止传进来是个未初始化的TreeMap
		if (fileMap != null) {
			File[] oldFiles = fileMap.get(keyFile);
			// 防止key在TreeMap中不存在
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
	 * 移除一个TreeMap的子目录数组值
	 * 
	 * @param fileMap
	 *            给予的目标文件结构
	 * @param keyFile
	 *            父目录值
	 * @param valueFiles
	 *            子目录值
	 * @return 处理后的TreeMap
	 */
	public static TreeMap<File, File[]> removeTreeMapValue(TreeMap<File, File[]> fileMap, File keyFile,
			File[] valueFiles) {
		// 防止传进来是个未初始化的TreeMap
		if (fileMap != null) {
			File[] oldFiles = fileMap.get(keyFile);
			// 防止key在TreeMap中不存在
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
}
