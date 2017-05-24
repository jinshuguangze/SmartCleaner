package dataHandle;

public class ClearUnit {

	public String rootPath;	
	public String[] subPath;
	
	public ClearUnit(String rootPath,String... subPath) {
		this.rootPath=rootPath;
		this.subPath=subPath;
	}

	public String getRootPath() {
		return this.rootPath;
	}

	public String[] getSubPath() {
		return this.subPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public void setSubPath(String[] subPath) {
		this.subPath = subPath;
	}
	
}
