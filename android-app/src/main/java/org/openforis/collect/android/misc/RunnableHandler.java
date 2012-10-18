package org.openforis.collect.android.misc;

import android.util.Log;

public class RunnableHandler implements Runnable 
{
    private int threadType;
    private FileSaver fileHandler;

    public RunnableHandler(int threadType, String file) 
    {
    	this.threadType = threadType;
    	this.fileHandler = new FileSaver(file);
    }
    
    public void run() 
    {
        if (this.threadType==0){//saving log file
        	LogCatHandler lcHandler = new LogCatHandler();
        	String logcat = lcHandler.getLogcat().toString();
			fileHandler.save(logcat);
        }
    }
    
    public static void reportException(Exception e, String tag, String msg, String file){
    	Log.e(tag,msg);
		e.printStackTrace();
		Thread thread = new Thread(new RunnableHandler(0, file/*, null*/));
		thread.start();
    }
}
