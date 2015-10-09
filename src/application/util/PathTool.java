package application.util;

public class PathTool {

	private PathTool () {
	}
	
	public static String NormalizePath (String Path) {
		
		if(Path != null) {
			String path = new String(Path);
			
			if(path.lastIndexOf("\\") >= 0)
				path = path.replace('\\', '/');
			
			return path;
		}
		
		return null;
	}
    
	public static String GetSpecificOSPath (String path) {
		
		String osPath = path;
		
		if(System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
			if(osPath.lastIndexOf("/") >= 0)
				osPath = osPath.replace('/', '\\');
		}
		else {
			if(osPath.lastIndexOf("\\") >= 0)
				osPath = osPath.replace('\\', '/');
		}
		
		return osPath;
	}
	
	public static String GetFullPath (String basefolder, String relativePath) {
		
		String nBaseFolder = NormalizePath(basefolder);
		String nRelativeFolder = NormalizePath(relativePath);
		
		
		//while(basefolder.lastIndexOf("/") == basefolder.length()-1)
		//	basefolder.substring(0,basefolder.length()-1);

		if(!nBaseFolder.endsWith("/"))
			nBaseFolder += "/";
		
		while(nRelativeFolder.startsWith("/"))
			nRelativeFolder = nRelativeFolder.substring(1);
		
		
		return GetSpecificOSPath(nBaseFolder + nRelativeFolder);			
	}
	
	public static String GetRelativePath (String fullPath, String baseFolder) {
		if(fullPath.indexOf(baseFolder) == 0)
			return fullPath.substring(baseFolder.length(), fullPath.length());
		
		return fullPath;
	}
	
	public static String getSafeRelativePath (String path) {
		
		String safePath = PathTool.NormalizePath(path);
		
		while (safePath.lastIndexOf("//") >= 0)
			safePath = safePath.replace("//", "/");
		
		while(safePath.startsWith("/"))
			safePath = safePath.substring(1);
		
		return safePath;
	}
}
