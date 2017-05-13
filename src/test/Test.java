package test;

import coreCleaner.*;

public class Test {
	public static void main(String[] args){
		Cleaner aCleaner=new Cleaner("C:\\", ".tmp", ".TMP");
		aCleaner.cleanAll();
		//System.getenv("SYSTEMDRIVE");
	}
}
