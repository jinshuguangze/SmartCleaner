package test;

import coreCleaner.*;

public class Test {
	public static void main(String[] args){
		Cleaner aCleaner=new Cleaner("Z:\\新建文件夹", ".tmp");
		aCleaner.clean(true);
		System.getenv("SYSTEMDRIVE");
	}
}
