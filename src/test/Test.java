package test;

import coreCleaner.*;

public class Test {
	public static void main(String[] args) {
		Reader aReader=new Reader("C:\\", ".tmp");
		Cleaner aCleaner = new Cleaner(aReader);
		aCleaner.cleanFilterFile();
		aReader.setSuffix(".TMP","._MP","._mp");
		aCleaner.cleanFilterFile();
		
		// 这是驱动器盘名的获得方式,保存备用
		System.getenv("SYSTEMDRIVE");
	}
}
