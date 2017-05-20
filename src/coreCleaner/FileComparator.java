package coreCleaner;

import java.io.*;
import java.util.*;

/**
 * @author jinshuguangze
 * @version 1.0
 */
public class FileComparator implements Comparator<File> {

	@Override
	public int compare(File file, File otherFile) {
		if (file == null) {
			return 1;
		}
		else if (otherFile == null) {
			return -1;
		}
		else {
			int diff = file.getPath().split("\\\\").length - otherFile.getPath().split("\\\\").length;
			return diff != 0 ? diff : file.getPath().compareTo(otherFile.getPath());
		}
	}
}
