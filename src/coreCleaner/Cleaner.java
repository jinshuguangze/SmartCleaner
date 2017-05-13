package coreCleaner;

import java.io.*;
import java.util.*;

/**
 * @author jinshu
 * @version 1.5
 */
public class Cleaner {
	private String rootPath;
	private String[] suffix;
	private HashMap<File, File[]> fileMap = new HashMap<>();
	private boolean hasNext = true;

	public Cleaner(String rootPath, String... suffix) {
		this.rootPath = rootPath;
		this.suffix = suffix;
	}

	private File[] getFiles(String rootPath) {
		File superFile = new File(rootPath);
		if (superFile.exists()) {
			if (superFile.isFile()) {
				return new File[0];
			} else {
				return superFile.listFiles();
			}
		} else {
			return null;
		}
	}

	@SuppressWarnings("unused")
	private File[] getFiles(String rootPath, String... suffix) {
		File superFile = new File(rootPath);
		if (superFile.exists()) {
			if (superFile.isFile()) {
				return new File[0];
			} else {
				SuffixFilter filter = new SuffixFilter(suffix);
				return superFile.listFiles(filter);
			}
		} else {
			return null;
		}
	}

	private HashMap<File, File[]> getAllFiles(String rootPath) {
		File rootFile = new File(rootPath);
		File[] files = getFiles(rootPath);
		fileMap.put(rootFile, files);
		if (files != null && files.length != 0) {
			for (File file : files) {
				getAllFiles(file.getPath());
			}
		}
		return fileMap;
	}

	private HashMap<File, File[]> getAllFiles(String rootPath, String... suffix) {
		HashMap<File, File[]> fileMapClone = getAllFiles(rootPath);
		HashMap<File, File[]> fileMapFilter = new HashMap<>();
		SuffixFilter filter = new SuffixFilter(suffix);
		fileMapClone.forEach((k, v) -> {
			if (v != null) {
				Vector<File> acceptFile = new Vector<>();
				for (File file : v) {
					if (filter.accept(file.getParentFile(), file.getName())) {
						acceptFile.add(file);
					}
				}
				fileMapFilter.put(k, acceptFile.toArray(new File[acceptFile.size()]));
			}
		});
		return fileMapFilter;
	}

	public void removeHashMapKey(File keyFile) {
		if (keyFile != null) {
			fileMap.remove(keyFile);
			removeHashMapValue(keyFile.getParentFile(), keyFile);
		}
	}

	public void removeHashMapKey(File[] keyFiles) {
		if (keyFiles != null && keyFiles.length != 0) {
			for (File file : keyFiles) {
				fileMap.remove(file);
				removeHashMapValue(file.getParentFile(), file);
			}
		}
	}

	public void removeHashMapValue(File keyFile, File valueFile) {
		if (keyFile != null && valueFile != null) {
			File[] oldFiles = fileMap.get(keyFile);
			Vector<File> newFiles = new Vector<>();
			if (oldFiles != null && oldFiles.length > 0) {
				for (File file : oldFiles) {
					if (!file.equals(valueFile)) {
						newFiles.add(file);
					}
				}
				fileMap.replace(keyFile, newFiles.toArray(new File[newFiles.size()]));
			}
		}
	}

	public void removeHashMapValue(File keyFile, File[] valueFiles) {
		if (keyFile != null && valueFiles != null && valueFiles.length != 0) {
			File[] oldFiles = fileMap.get(keyFile);
			Vector<File> newFiles = new Vector<>();
			if (oldFiles != null && oldFiles.length > 0) {
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
	}

	public void cleanFilterFile() {
		HashMap<File, File[]> fileMapFilter = getAllFiles(rootPath, suffix);
		System.gc();
		if (fileMapFilter.size() != 0) {
			fileMapFilter.forEach((k, v) -> {
				if (v != null) {
					for (File file : v) {
						file.setExecutable(true);
						if (file.delete()) {
							removeHashMapValue(k, file);
							System.out.println("过滤器" + Arrays.toString(suffix) + "删除成功,路径:" + file.getPath());
						} else {
							System.out.println("过滤器" + Arrays.toString(suffix) + "删除失败,路径:" + file.getPath());
						}
					}
				}
			});
		}
	}

	public void cleanEmptyFolder() {
		if (fileMap.size() != 0) {
			Vector<File> deleteKey = new Vector<>();
			while (hasNext) {
				hasNext = false;
				fileMap.forEach((k, v) -> {
					if (k != null && v != null && k.isDirectory() && v.length == 0 && k.delete()) {
						hasNext = true;
						deleteKey.add(k);
						System.out.println("空文件夹删除成功,路径:" + k.getPath());
					}
				});
				removeHashMapKey(deleteKey.toArray(new File[deleteKey.size()]));
			} // 更好的迭代算法:只检测上次迭代删除空文件夹的父文件夹TODO
		}
	}

	public void cleanAll() {
		cleanFilterFile();
		cleanEmptyFolder();
	}
}
