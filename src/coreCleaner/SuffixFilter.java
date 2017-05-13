package coreCleaner;

import java.io.*;

public class SuffixFilter implements FilenameFilter{
	private String[] suffix;
	
	public SuffixFilter(String[] suffix) {
		this.suffix=suffix;
	}
	
	@Override
	public boolean accept(File dir, String name) {
		boolean pass = false;
		for (int i = 0; i < suffix.length; i++) {
			pass = pass || name.endsWith(suffix[i]);
		}
		return pass;
	}	
}