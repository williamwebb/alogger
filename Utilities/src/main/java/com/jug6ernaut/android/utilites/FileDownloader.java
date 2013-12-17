package com.jug6ernaut.android.utilites;

/*
 * Copyright (c) 2010 Evenflow, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class FileDownloader{

	public File downloadFile(String serverUrl, String fileToFetch, File outputFile) throws IOException
	{
		//File outputFile = null;
		
		String urlToFetch = serverUrl + fileToFetch;
		URL url = new URL(urlToFetch);
		HttpURLConnection c = (HttpURLConnection) url.openConnection();
		c.setRequestMethod("GET");
		c.setDoOutput(true);
		c.connect();
		
		File dir = outputFile.getParentFile();
		dir.mkdirs();
	 
		FileOutputStream fos = new FileOutputStream(outputFile);

		InputStream is = c.getInputStream();

		byte[] buffer = new byte[1024];
		int len1 = 0;
		while ((len1 = is.read(buffer)) != -1) {
			fos.write(buffer, 0, len1);
		}
		fos.close();
		is.close();

		
		//pd.dismiss();
		return outputFile;
	}
	
	/**
	 * still has issues...
	 * 
	 * @param inputUrl
	 * @param outputFile
	 * @return
	 */
	
	@Deprecated
	public File downloadFileNew(File inputUrl, File outputFile){
		OutputStream out = null;
		InputStream fis = null;
		
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 2500;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		int timeoutSocket = 2500;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		
		File dir = outputFile.getParentFile();
		dir.mkdirs();

		try {
						
			HttpGet httpRequest = new HttpGet(stringFromFile(inputUrl));
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
			HttpResponse response = httpClient.execute(httpRequest);
			
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				out = new BufferedOutputStream(new FileOutputStream(outputFile));
				long total = entity.getContentLength();
				fis = entity.getContent();
				
				
				if(total>1||true)
					startTalker(total);
				else{
					throw new IOException("Content null");
				}
				long progress = 0;
				
				byte[] buffer = new byte[1024];
				int length = 0;
				while ((length = fis.read(buffer)) != -1) {
					
					out.write(buffer, 0, length);
					
					progressTalker(length);
					progress+=length;
				}
				
				/*
				for (int b; (b = fis.read()) != -1;) {
					out.write(b);
					progress+=b;
					progressTalker(b);
				}
				*/
				
				finishTalker(progress);
			}
		}catch(Exception e){
			cancelTalker(e);
			e.printStackTrace();
			return null;
		}finally{
			try {
				if(out!=null){
					out.flush();
					out.close();
				}
				if(fis!=null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return outputFile;
	}
	
	public int getStatus(){
		return status;
	}
	
	private int status = 0;
	
	public File downloadFile(File serverFile, File outputFile) throws IOException
	{
		long total = 0;
		long progress = 0;
		status = 0;
		
		String stringURL = serverFile.getPath();
		
		//fix url as File removes //
		if(stringURL.startsWith("http:/") && !stringURL.startsWith("http://"))stringURL = stringURL.replace("http:/", "http://");
		if(stringURL.startsWith("https:/") && !stringURL.startsWith("https://"))stringURL = stringURL.replace("https:/", "https://");
				
		try{
			URL url = new URL(stringURL);
			HttpURLConnection c = (HttpURLConnection) url.openConnection();
			c.setRequestMethod("GET");
			c.setDoOutput(true);
			c.connect();
			
			File dir = outputFile.getParentFile();
			dir.mkdirs();
		 
			FileOutputStream fos = new FileOutputStream(outputFile);
			total = c.getContentLength();
			startTalker(total);
			InputStream is = c.getInputStream();
			
			
			byte[] buffer = new byte[1024];
			int len1 = 0;
			while ((len1 = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len1);
				progressTalker(len1);
				progress+=len1;
			}
			finishTalker(progress);
			fos.close();
			is.close();
		}catch(Exception e){
			cancelTalker(e);
			e.printStackTrace();
			status = STATUS_FAILED;
			return null;
		}

		if(total==progress)status = STATUS_SUCCESS;	
		else status = STATUS_FAILED;	
		
		return outputFile;
	}
	
	public static final int STATUS_SUCCESS = 1;
	public static final int STATUS_FAILED = -1;
	public static final int STATUS_NULL = 0;
		
	private static OnDownloadProgressListener progressListener;
	private static void progressTalker(int progress){
		if(progressListener!=null)progressListener.onProgress(progress);
	}
	private static void startTalker(long total){
		if(progressListener!=null)progressListener.onStart(total);
	}
	private static void finishTalker(long downloaded){
		if(progressListener!=null)progressListener.onFinish(downloaded);
	}
	private static void cancelTalker(Exception e){
		if(progressListener!=null)progressListener.onCancel(e);
	}
	
	public interface OnDownloadProgressListener{
		public void onStart(long total);
		public void onProgress(int progress);		
		public void onFinish(long downloaded);
		public void onCancel(Exception e);
	}
	
	public void setOnDownloadProgressListener(OnDownloadProgressListener listener){
		progressListener = listener;
	}
	public void removeOnDownloadProgressListener(){
		progressListener = null;
	}
		
	public URL urlFromFile(File url) throws MalformedURLException{
		String stringURL = stringFromFile(url);
		return new URL(stringURL);
	}
	
	public static String stringFromFile(File url){
		String stringURL = url.getPath();
		
		if(stringURL.startsWith("http:/") && !stringURL.startsWith("http://"))stringURL = stringURL.replace("http:/", "http://");
		if(stringURL.startsWith("https:/") && !stringURL.startsWith("https://"))stringURL = stringURL.replace("https:/", "https://");
		
		return stringURL;
	}
	
}


