package coreCleaner;

import java.io.*;
import java.util.*;

import javax.security.auth.kerberos.KerberosKey;

/**
 * @author jinshu
 * @version 1.0
 */
public class Cleaner {
	private String rootPath;
	private String[] suffix;	
	private HashMap<File, File[]> fileMap = new HashMap<>();
	private boolean hasNext=false;

	public Cleaner(String rootPath, String... suffix) {
		this.rootPath = rootPath;
		this.suffix = suffix;
	}

	private File[] getFiles(String rootPath) {
		File superFile = new File(rootPath);
		if (superFile.exists()) {
			if(superFile.isFile()){
				return new File[0];
			}
			else {
				return superFile.listFiles();//空文件夹和顶层文件返回空数组,非空文件夹正常返回
			}			
		} else {
			return null;//不存在文件返回null
		}
	}

	private File[] getFiles(String rootPath, String... suffix) {
		File superFile = new File(rootPath);
		if (superFile.exists()) {
			if(superFile.isFile()){
				return new File[0];
			}
			else {
				SuffixFilter filter = new SuffixFilter(suffix);
				return superFile.listFiles(filter);
			}		
		} else {
			return null;
		}
	}

	private HashMap<File, File[]> getAllFiles(String rootPath) {
		File rootFile=new File(rootPath);
		File[] files = getFiles(rootPath);
		fileMap.put(rootFile, files);
		if (files != null && files.length!=0) {
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
				Vector<File> acceptFile=new Vector<>();
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

	public void cleanFilterFile() {
		HashMap<File, File[]> fileMapFilter=getAllFiles(rootPath, suffix);
		System.gc();
		if(fileMapFilter.size()!=0){
			fileMapFilter.forEach((k,v)->{
				if(v!=null){
					for (File file : v) {
						file.setExecutable(true);
						if (file.delete()) {
							System.out.println("删除成功,路径:" + file.getPath());
							//删除后更改对应的全局fileMap
							//TODO
						}
						else{
							System.out.println("删除失败,路径:" + file.getPath());
						}
					}
				}			
			});
		}
	}

	public void cleanEmptyFolder() {
		if(fileMap.size()!=0){
			{				
				hasNext=false;
				fileMap.forEach((k,v)->{
					if(k.isDirectory()&&v.length==0&&k.delete()){
						hasNext=true;
					}
				});				
			}while (hasNext);
		}
	}

	public void cleanAll() {
		cleanFilterFile();
		cleanEmptyFolder();
	}
}
