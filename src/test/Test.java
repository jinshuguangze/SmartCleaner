package test;

import coreCleaner.*;

public class Test {
	public static void main(String[] args) {
		Cleaner aCleaner = new Cleaner(new Reader("C:\\", ".tmp", ".TMP", "._mp", "._MP"));
		aCleaner.cleanAll();

		// 这是驱动器盘名的获得方式,保存备用
		System.getenv("SYSTEMDRIVE");
	}
}
