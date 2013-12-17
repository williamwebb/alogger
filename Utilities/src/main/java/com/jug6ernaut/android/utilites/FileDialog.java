package com.jug6ernaut.android.utilites;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import com.jug6ernaut.android.utilities.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class FileDialog{

	Context mContext = null;
	
	Dialog d = null;
		
	public static final Theme Theme_Dark = new Theme(
			android.R.style.Theme_Holo_Dialog,
			R.drawable.file_dark,
			R.drawable.folder_dark
			);
	public static final Theme Theme_light = new Theme(
			android.R.style.Theme_Holo_Light_Dialog,
			R.drawable.file_light,
			R.drawable.folder_light
			);
	
	private Theme mTheme = null;
	
	private static class Theme{

		public int theme = 0;
		public int file = 0;
		public int folder = 0;
		
		public Theme(int theme, int file, int folder) {
			this.theme = theme;
			this.file = file;
			this.folder = folder;
		}
	}
	
	
	public FileDialog(Context context,String startPath, int fileSelectFlag){
		this(context,startPath,fileSelectFlag,Theme_Dark);
	}
	
	public FileDialog(Context context,String startPath, int fileSelectFlag, Theme theme) {
		
		mContext = context;
		FILE_SELECTION_FLAG = fileSelectFlag;
		this.rootPath = "/";
		this.startPath = startPath;
		mTheme = theme;
		
		d = new Dialog(context,theme.theme){
			@Override
			public boolean onKeyDown(int keyCode, KeyEvent event) {
				if ((keyCode == KeyEvent.KEYCODE_BACK)) {
					unselect();

					if (layoutCreate.getVisibility() == View.VISIBLE) {
						layoutCreate.setVisibility(View.GONE);
						layoutSelect.setVisibility(View.VISIBLE);
					} else {
						if (!currentPath.equals(root)) {
							getDir(parentPath);
						} else {
							return super.onKeyDown(keyCode, event);
						}
					}

					return true;
				} else {
					return super.onKeyDown(keyCode, event);
				}
			}
		};
		d.setTitle("Select File/Folder");
		RelativeLayout ll = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.file_dialog_main,null);
		d.setContentView(ll);
		myTitle = (TextView) ll.findViewById(R.id.tvTitle);
		myTitle.setVisibility(View.GONE);
		myPath = (TextView) ll.findViewById(R.id.path);
		mFileName = (EditText) ll.findViewById(R.id.fdEditTextFile);
		mListView = (ListView) ll.findViewById(R.id.list);
		
		
		mListView.setOnItemLongClickListener(new OnItemLongClickListener(){

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				
				String current = currentPath + (currentPath.equals("/")?"":"/") + (String) mList.get(arg2).get(ITEM_KEY);
				
				File temp = new File(current);
				
				System.out.println("CurrentPath: " + current);
				
					switch(FILE_SELECTION_FLAG){
					
					case 1: // return if a file
						if(!temp.isFile())break;
						sendResult(currentPath + (currentPath.equals("/")?"":"/") + (String) mList.get(arg2).get(ITEM_KEY));
						d.dismiss();
					
					break;
					
					case 2: // return files/folders
						sendResult(currentPath + (currentPath.equals("/")?"":"/") + (String) mList.get(arg2).get(ITEM_KEY));
						d.dismiss();
						
					break;
					
					case 3: // return if folder
						if(!temp.isDirectory())break;
						sendResult(currentPath + (currentPath.equals("/")?"":"/") + (String) mList.get(arg2).get(ITEM_KEY));
						d.dismiss();
						
					break; // return nothing
					
					default:break;
					
					}

				return false;
			}});
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				File file = new File(path.get(position));

				if (file.isDirectory()) {
					//unselect();
					if (true){//file.canRead()) {
						lastPositions.put(currentPath, position);
						getDir(path.get(position));
					} 
				} else {
					v.setSelected(true);
					selectButton.setEnabled(true);
				}
			}
		});
				
		inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

		selectButton = (Button) ll.findViewById(R.id.fdButtonSelect);
		selectButton.setEnabled(false);
		selectButton.setText("Cancel");
		selectButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				d.dismiss();				
			}
		});
		unselect();
		newButton = (Button) ll.findViewById(R.id.fdButtonNew);
		newButton.setText("UP");
		newButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				getDir(parentPath);
			}
		});

		layoutSelect = (LinearLayout) ll.findViewById(R.id.fdLinearLayoutSelect);
		layoutCreate = (LinearLayout) ll.findViewById(R.id.fdLinearLayoutCreate);
		layoutCreate.setVisibility(View.GONE);

		cancelButton = (Button) ll.findViewById(R.id.fdButtonCancel);
		cancelButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				layoutCreate.setVisibility(View.GONE);
				layoutSelect.setVisibility(View.VISIBLE);

				inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
				unselect();
			}

		});
		createButton = (Button) ll.findViewById(R.id.fdButtonCreate);
		createButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

			}
		});
						
	}
	
	public void show(){
		if (rootPath != null) {
			root = rootPath;
		} else {
			root = "/";
		}

		if (startPath != null) {
			getDir(startPath);
		} else {
			getDir(root);
		}
		parentPath = root;
		
		d.show();
	}

	private String rootPath = null;
	private String startPath = null;
	
	private static final String ITEM_KEY = "key";
	private static final String ITEM_IMAGE = "image";

	public static final String START_PATH = "START_PATH";
	public static final String RESULT_PATH = "RESULT_PATH";
	public static final String ROOT_PATH = "ROOT_PATH";
	
	private int FILE_SELECTION_FLAG = 1;
	public static final String FILE_SELECTION = "FILE_SELECTION";
	public static final int SELECT_FILES = 1;
	public static final int SELECT_FILES_FOLDERS = 2;
	public static final int SELECT_FOLDERS = 3;
	public static final int SELECT_NOTHING = 4;

	private List<String> item = null;
	private List<String> path = null;
	private String root = "/";
	private TextView myPath;
	private TextView myTitle;
	private EditText mFileName;
	private ArrayList<HashMap<String, Object>> mList;

	private Button selectButton;
	private Button newButton;
	private Button cancelButton;
	private Button createButton;
	
	private LinearLayout layoutSelect;
	private LinearLayout layoutCreate;
	private ListView mListView;
	private InputMethodManager inputManager;
	private String parentPath;
	private String currentPath = root;
	
	

	private HashMap<String, Integer> lastPositions = new HashMap<String, Integer>();

	/**
	 * Limit files shown to the given file extension.
	 * 
	 * Example: "txt"
	 * 
	 * @param extention
	 */
	private String extensionLimiter = "";
	public void setExtentionLimiter(String extension){
		extensionLimiter = extension;
	}
	
	/**
	 * Set title, null to disable title.
	 * Title disabled by Default.
	 * 
	 * @param title
	 */

	public void setTitle(String title){
		if(d!=null){
			d.setTitle(title);
		}
	}

	private void getDir(String dirPath) {
		
		if(dirPath==null||dirPath.equals("null")){
			d.dismiss();
			return;
		}
		
		if(dirPath.length()<root.length())return;

		boolean useAutoSelection = dirPath.length() < currentPath.length();

		Integer position = lastPositions.get(parentPath);

		getDirImpl(dirPath);

		if (position != null && useAutoSelection) {
			mListView.setSelection(position);
		}

	}

	private void getDirImpl(String dirPath) {

		myPath.setText("Location: " + dirPath);
		currentPath = dirPath;

		item = new ArrayList<String>();
		path = new ArrayList<String>();
		mList = new ArrayList<HashMap<String, Object>>();

		File f = new File(dirPath);
		File[] files = null;
		if(f.canRead())
		{
			files = f.listFiles();
		}
		else{
			List<String> SUfiles = SuperUser.executeSU("ls " + dirPath);
			
			if(SUfiles!=null && SUfiles.size()>0){
				
				files = new File[SUfiles.size()];//create new array size of # of files

				for(String file : SUfiles){
					files[SUfiles.indexOf(file)]=new File(currentPath + "/" + file);
				}
			}
		}
		if (!dirPath.equals(root)) {

			parentPath = f.getParent();

		}
		
		if(files==null)files = new File[0];

		TreeMap<String, String> dirsMap = new TreeMap<String, String>();
		TreeMap<String, String> dirsPathMap = new TreeMap<String, String>();
		TreeMap<String, String> filesMap = new TreeMap<String, String>();
		TreeMap<String, String> filesPathMap = new TreeMap<String, String>();
		for (File file : files) {
			if (file.isDirectory()) {
				String dirName = file.getName();
				dirsMap.put(dirName, dirName);
				dirsPathMap.put(dirName, file.getPath());
			} else {
				filesMap.put(file.getName(), file.getName());
				filesPathMap.put(file.getName(), file.getPath());
			}
		}
		item.addAll(dirsMap.tailMap("").values());
		item.addAll(filesMap.tailMap("").values());
		path.addAll(dirsPathMap.tailMap("").values());
		path.addAll(filesPathMap.tailMap("").values());
		
		ArrayList<Data> m_Data = new ArrayList<Data>();
		Data temp = null;
		
		for (File file : files) {
			if (file.isDirectory()) {
				temp = new Data(file.getPath(),"","");
			} else {
				temp = new Data(file.getParent(),file.getName(),String.valueOf(file.lastModified()));
			}
			m_Data.add(temp);
		}

		SimpleAdapter fileList = new SimpleAdapter(mContext, mList,
				R.layout.file_dialog_row,
				new String[] { ITEM_KEY, ITEM_IMAGE }, new int[] {
						R.id.fdrowtext, R.id.fdrowimage });

		for (String dir : dirsMap.tailMap("").values()) {
			addItem(dir, mTheme.folder);
		}

		for (String file : filesMap.tailMap("").values()) {

			if(!extensionLimiter.equals("")){
				
				if(file.endsWith("." + extensionLimiter))
					addItem(file, mTheme.file);
				
			}else addItem(file, mTheme.file);
			
		}

		fileList.notifyDataSetChanged();
		
		mListView.setAdapter(fileList);

	}

	private void addItem(String fileName, int imageId) {
		HashMap<String, Object> item = new HashMap<String, Object>();
		item.put(ITEM_KEY, fileName);
		item.put(ITEM_IMAGE, imageId);
		mList.add(item);
	}

	private void unselect() {
		selectButton.setEnabled(true);
	}
	
	static OnResultListener mListener = null;
	
	// Interface for resize notifications
    public interface OnResultListener {
        public void onResult(String result);
    }
    
    private static void sendResult(String result){
    	if(mListener!=null)mListener.onResult(result);
    }
	
	public void setOnResultListener(OnResultListener listener){
		mListener = listener;
	}
	
	public void removeOnResultListener(){
		mListener = null;
	}

    public class Data {

        String filePath;
        String fileName;
        String fileDate;

        public Data(String filePath,String fileName,String fileDate){
            this.filePath=filePath;
            this.fileName=fileName;
            this.fileDate=fileDate;
        }

        public String getPath(){
            return filePath;
        }
        public String getName(){
            return fileName;
        }
        public String getDate(){
            return fileDate;
        }
    }

}