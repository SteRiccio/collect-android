package org.openforis.collect.android.misc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileSaver {
	
	private File file;

	public FileSaver(String fileName){
		this.file = new File(fileName);
	}
	
	public boolean save(String fileContent){
    	try {
			BufferedWriter buf = new BufferedWriter(new FileWriter(file, true)); 
			buf.append(fileContent);
			buf.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}