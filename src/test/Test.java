package test;

import coreCleaner.*;

public class Test {
	public static void main(String[] args) {
		long before=System.nanoTime();
		Cleaner aCleaner = new Cleaner(new Reader("C:\\", ".tmp", ".TMP", "._mp", "._MP"));
		aCleaner.cleanAll();
		long after=System.nanoTime();
		
		//使用HashMap算法清理C盘,过滤器[".tmp", ".TMP", "._mp", "._MP"],连续清理两次,第二次共花费38820898957纳秒.
		//在同样情况下使用TreeMap尝试,用时是41650096899纳秒.
		//由此看来,使用不用排序操作的HashMap快了6.8%.
		System.out.println("清理完毕,总共花费"+(after-before)+"纳秒");
		
		// 这是驱动器盘名的获得方式,保存备用
		System.getenv("SYSTEMDRIVE");
	}
}
