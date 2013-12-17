package com.jug6ernaut.android.utilites;

/****************************************************************************
 * SuperUser class... should speak for itself. These are the functions I    *
 * personally use as base for most things SuperUser.                        *
 *                                                                          *
 * I know some parts look overkill, but tested on a wide range of devices,  *
 * doing things this way works around a large number of issues.             *
 *                                                                          *
 * Doesn't include an easy way to keep a su shell open in the background    *
 * and dynamically read and write to it. However if you're looking for that,*
 * executeSU(String[] commands) should be a good starting point.            *
 *                                                                          *
 *  - Chainfire                                                             *
 *                                                                          *
 *  Copyright (c) Chainfire, license public domain                          *
 ****************************************************************************/

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SuperUser {
	// change this
	private static final String LOG_TAG = "SuperUser"; 
	
	private static int haveSUresult = 0;
	
	private static void log(String text) {
		// using LOG_TAG twice so its easier to use in @#$&)%^&@( DDMS
		android.util.Log.d(LOG_TAG, "[" + LOG_TAG + "] " + text);
	}
	
	public static String getAppPid(String appName){
		List<String> x = executeSU("ps | grep " + appName + " | awk '{print $2}'"); 
		return (x.size()>0)?x.get(0):null;
	}
	
	public static List<String> getChildPids(int parentPid){
		List<String> children = executeSU("ps | grep " + parentPid + " | awk '{print $2}'");
		Collections.synchronizedList(children);
		for(String c : children){
			if(c.equals(parentPid)){
				children.remove(c);
			}
		}
		return children;
	}
	
	public static boolean haveSU() {
		if (haveSUresult == 0) {			
			List<String> result = executeSU("ls /");
			if (result == null) {
				haveSUresult = 1;
				return false;
			} else {
				haveSUresult = 2;
				return true;
			}
		} else {
			if (haveSUresult == 1) {
				return false;
			} else {
				return true;
			}
		}
	}
	
	
	
	static SuperUser instance = null;
	public static SuperUser getInstance(){
		
		if(instance == null)instance = new SuperUser();
		
		return instance;		
	}
	
	public void restart(){
		if(process!=null){
			try {
				os.write("exit\n".getBytes());
				os.flush();
				process.waitFor();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			process = null;
		}
		
		try {
			// open su, attach stdin, stdout and stderr
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			osRes = new DataInputStream(process.getInputStream());
			osError = new DataInputStream(process.getErrorStream());
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	public Process process;
	DataOutputStream os = null;
	DataInputStream osRes = null;
	DataInputStream osError = null;
	
	private SuperUser(){
		restart();
	}
	
	public void raw(String command){
		try {
			os.write((command + "\n").getBytes());
			os.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<String> execute(String command) {
		return executeSU(new String[] { command });
	}
	
	public List<String> execute(String[] commands) throws InterruptedException {
		List<String> res = new ArrayList<String>();	
		// execute commands and log them
		try{
					for (String single : commands) {
						log("[SU+] " + single);
						os.write((single + "\n").getBytes());
				   		//os.flush();
					}
					// wait until su exits
					process.waitFor();
					if (process.exitValue() == 255) {
						// su (probably) denied
						return null;
					}
					
					// log output and error streams
					while (osError.available() > 0) {
						String read = osError.readLine();
						log("[SU*] " + read);
						if(read.length()>0)
						res.add(read);
					}
			   		while (osRes.available() > 0) {
			   			String read = osRes.readLine();
						log("[SU-] " + read);
			   			res.add(read);
			   		}
				} catch (IOException e) {
					// su (probably) not found
					return null;
				}
				
				return res;
			}
	
	public static List<String> executeSU(String command) {
		return executeSU(new String[] { command });
	}
	
	public static List<String> executeSU(String[] commands) {
		List<String> res = new ArrayList<String>();
		
		Process process;
		try {
			// open su, attach stdin, stdout and stderr
			process = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(process.getOutputStream());
			DataInputStream osRes = new DataInputStream(process.getInputStream());
			DataInputStream osError = new DataInputStream(process.getErrorStream());
			
			// execute commands and log them
			for (String single : commands) {
				log("[SU+] " + single);
				os.writeBytes(single + "\n");
		   		os.flush();
			}
			
			// exit
			os.writeBytes("exit\n");
			os.flush();
			
			// wait until su exits
			process.waitFor();
			if (process.exitValue() == 255) {
				// su (probably) denied
				return null;
			}
			
			// log output and error streams
			while (osError.available() > 0) {
				String read = osError.readLine();
				log("[SU*] " + read);
				if(read.length()>0)
				res.add(read);
			}
	   		while (osRes.available() > 0) {
	   			String read = osRes.readLine();
				log("[SU-] " + read);
	   			res.add(read);
	   		}
		} catch (IOException e) {
			// su (probably) not found
			return null;
		} catch (InterruptedException e) {
			// should probably reraise InterruptedException instead
			return null;
		}
		
		return res;
	}
	
	public static void executeSUreboot() {
		// yes, seriously.
		executeSU(new String[] {
				"reboot -f",
				"reboot",
				"reboot normal",
				"toolbox reboot",
				"busybox reboot -f",
				"busybox reboot",
				"busybox reboot normal"				
		});
	}
}