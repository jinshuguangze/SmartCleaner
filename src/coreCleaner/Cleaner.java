package coreCleaner;

import java.io.*;
import java.util.*;

/**
 * @author jinshu
 * @version 1.0
 */
public class Cleaner {
	private String rootPath;
	private String[] suffix;
	private HashMap<File, File[]> fileList = new HashMap<>();
	private Vector<File> redlist=new Vector<>();
	private boolean hasNext=false;

	public Cleaner(String rootPath, String... suffix) {
		this.rootPath = rootPath;
		this.suffix = suffix;
	}

	private File[] getFiles(String rootPath) {
		File superFile = new File(rootPath);
		if (superFile.exists() && superFile.isDirectory()&&superFile.listFiles().length!=0) {
			return superFile.listFiles();
		} else {
			return null;
		}
	}

	private File[] getFiles(String rootPath, String... suffix) {
		File superFile = new File(rootPath);
		if (superFile.exists() && superFile.isDirectory()&&superFile.listFiles().length!=0) {
			SuffixFilter filter = new SuffixFilter(suffix);
			return superFile.listFiles(filter);
		} else {
			return null;
		}
	}

	private File[] getAllFiles(String rootPath) {
		File[] files = getFiles(rootPath);
		fileList.put(new File(rootPath), files);
		if (files != null) {
			for (File file : files) {
				System.out.println("搜寻"+file.getPath());
				getAllFiles(file.getPath());
				redlist.add(file);
			}
		}
		return redlist.toArray(new File[redlist.size()]);
	}

	private File[] getAllFiles(String rootPath, String... suffix) {
		File[] files = getAllFiles(rootPath);
		if (files != null) {
			SuffixFilter filter = new SuffixFilter(suffix);
			Vector<File> acceptList = new Vector<>();
			fileList.forEach((k, v) -> {
				if(v!=null){
					Vector<File> unacceptList = new Vector<>();
					for (int i = 0; i < v.length; i++) {
						if (filter.accept(v[i].getAbsoluteFile(), v[i].getName())) {
							acceptList.add(v[i]);
						} else {
							unacceptList.add(v[i]);
						}
					}
					fileList.replace(k, unacceptList.toArray(new File[unacceptList.size()]));
				}
			});
			if (acceptList.size() != 0) {
				return acceptList.toArray(new File[acceptList.size()]);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public void clean(boolean cleanEmptyFolder) {
		File[] files = getAllFiles(rootPath, suffix);
		System.gc();
		for (int i = 0; i < files.length; i++) {
			files[i].setExecutable(true);
			if (files[i].delete()) {
				System.out.println(
						"删除成功!进度:" + (float) (i + 1) / (float) files.length * 100.0f 
						+"%,路径:" + files[i].getPath());
			} else {
				System.out.println(
						"删除失败!进度:" + (float) (i + 1) / (float) files.length * 100.0f
						+"%,路径:" + files[i].getPath());
			}
		}
		
		if(cleanEmptyFolder){			
			{
				fileList.forEach((k, v) -> {
					if(v==null&&k.delete()){
						hasNext=true;
					}
				});
			}while(hasNext);
		}
	}
}
