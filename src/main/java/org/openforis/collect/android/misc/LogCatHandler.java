package org.openforis.collect.android.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.util.Log;

public class LogCatHandler {	
	
	public StringBuilder getLogcat(){
		Process mLogcatProc = null;
		BufferedReader reader = null;
		StringBuilder log = new StringBuilder();
		try
		{
			Log.e("JKHJK", "NJMJJ");
			mLogcatProc = Runtime.getRuntime().exec(new String[]{"logcat", "-d", " *:D" });
			reader = new BufferedReader(new InputStreamReader(mLogcatProc.getInputStream()));
	        String line;
	        while ((line = reader.readLine()) != null)
	        {
				if (!line.equals(""))
					log.append(line);
				log.append("\n");	
	        }    
		}
		catch (IOException e)
		{
		        
		}
		finally
		{
			if (reader != null)
			    try
			    {
			    	reader.close();			       
			    }
			    catch (IOException e)
			    {
			           
			    }
		}
		return log;
	}
}
