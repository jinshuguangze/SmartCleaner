package dataHandle;

import java.io.*;

import com.google.gson.*;

public class ClearUnitReader {

	public ClearUnitReader(String dataPath) {
		File dataFile=new File(dataPath);
		if(dataFile.exists()) {
			File[] files=dataFile.listFiles(new FilenameFilter() {			
				@Override
				public boolean accept(File dir, String name) {
					if(name.endsWith(".json")) {
						return true;
					}
					else {
						return false;
					}
				}
			});
			if (files.length>0) {
				for (File file : files) {
					Gson aGson=new Gson();
					try {
						ClearUnit aUnit=aGson.fromJson(new FileReader(file), ClearUnit.class);
						//how to use it?
						aUnit.getRootPath();
						aUnit.getSubPath();
					}
					catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
						e.printStackTrace();
					}				
				}
			}
		}
	}
}
