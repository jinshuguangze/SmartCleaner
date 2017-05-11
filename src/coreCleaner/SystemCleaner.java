package coreCleaner;

import java.io.*;
import java.util.*;

/**
 * @author jinshu
 * @version 1.0
 */
public class SystemCleaner {
	String drivePath;
	String programPath;
	String programPathx86;
	Vector<File> fileList=new Vector<>();
	
	public SystemCleaner(){
		drivePath=System.getenv("SYSTEMDRIVE");
		programPath=System.getenv("ProgramFiles");
		programPathx86=System.getenv("ProgramFiles(x86)");
	}
	
	private File[] getFiles(String superPath){
		File superFile=new File(superPath);
		if(superFile.exists()&&superFile.isDirectory()){
			return superFile.listFiles();
		}
		else {
			return null;
		}
	}
	
	private File[] getFiles(String superPath,String... suffix) {	
		File superFile=new File(superPath);
		if(superFile.exists()&&superFile.isDirectory()){
			SuffixFilter filter=new SuffixFilter(suffix);				
			return superFile.listFiles(filter);			
		}
		else {
			return null;
		}					
	}

	private File[] getAllFiles(String superPath) {		
		File[] files=getFiles(superPath);	
		if(files!=null){
			for (File file : files) {
				System.out.println("����·��..."+file.getPath());
				getAllFiles(file.getPath());
				fileList.add(file);			
			}
			return fileList.toArray(new File[fileList.size()]);
		}
		else {
			return null;
		}
	}
	
	private File[] getAllFiles(String superPath,String... suffix) {
		File[] files=getAllFiles(superPath);		
		if(files!=null){
			SuffixFilter filter=new SuffixFilter(suffix);
			Vector<File> acceptList=new Vector<>();
			for (File file : files) {
				if(filter.accept(file.getAbsoluteFile(), file.getName())){
					System.out.println("�˶Ը�ʽ..."+file.getPath());
					acceptList.add(file);
				}
			}
			if(acceptList.size()!=0){
				return acceptList.toArray(new File[acceptList.size()]);
			}
			else {
				return null;
			}
		}
		else {
			return null;
		}
	}
	
	public void tmpClean(){
		System.out.println("������...");
		File[] files=getAllFiles("C:\\Users\\40825\\AppData", ".tmp", ".TMP");	
		System.gc();
		for (int i=0;i<files.length;i++) {
			files[i].setExecutable(true);
			if(files[i].delete()){
				System.out.println("ɾ���ɹ�!����:"+(float)(i+1)/(float)files.length*100.0f+"%,·��:"+files[i].getPath());
			}
			else {
				System.out.println("ɾ��ʧ��!����:"+(float)(i+1)/(float)files.length*100.0f+"%,·��:"+files[i].getPath());
			}
		}
	}
	/*
	File file=new File("C:\\Users\\40825\\AppData\\Local\\Temp\\~DFCAC32B367836CBB7.tmp");
	file.setExecutable(true);
	System.out.println(file.delete());
	}
	*/
}

