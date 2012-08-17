package com.goodguide.android.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.goodguide.android.shared.Constants;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.os.StatFs;

public class DataBaseHelper extends SQLiteOpenHelper{
	 
    private static final String TAG = null;

	//The Android's default system path of your application database. These will change if the DB is to be unzipped to an SD Card.
    private static String DB_PATH = "/data/data/com.goodguide.android.app/databases/";
    private static String DB_NAME = "goodguide_android";
    private static String DB_ZIP_NAME = "goodguide_android.mp3";
    private SQLiteDatabase myDataBase; 
 
    private final Context myContext;
    
    
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    
    public DataBaseHelper(Context context) {
    	super(context, DB_NAME, null, 1);
        this.myContext = context;
    }	
 
    /**
     * test to see if the db is open before querying
     */
    public boolean isDBOpen() {
    	return myDataBase.isOpen();
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{
 
    		//By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
	    	boolean dbExist = checkDataBase();
	
	    	if(dbExist){

	    	//do nothing - database already exist
	    	}else{
	
		    	//By calling this method and empty database will be created into the default system path
		    	//of your application so we are gonna be able to overwrite that database with our database.
		    	SQLiteDatabase db_Read = null;
		    	db_Read = this.getReadableDatabase();
		    	db_Read.close();
	 
	        	try { 
	        		copyDataBase();
	 
	    		} catch (IOException e) {
	 
	    			Log.v("",e.getMessage());
	        		throw new Error("Error copying database");
	 
	        	}
	    		
	        	try { 
	        		UnzipDataBase(); 			
	 
	    		} catch (IOException e) {
	    			Log.v("",e.getMessage());
	        		throw new Error("Error unzipping database");
	        	}
	    	}
    }
 
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application. Checks SD Card then Internal.
     * @return true if it exists, false if it doesn't
     */
    public boolean checkDataBase(){
    	
    	boolean result = false;
    	
		String sdPath = Environment.getExternalStorageDirectory() + "/goodguide/" + DB_NAME;
		String internalPath = DB_PATH + DB_NAME;

		boolean sdDBFileExists = new File(sdPath).exists();
		boolean internalDBFileExists = new File(internalPath).exists();
    	 
		Log.v(TAG, "DB Check " + sdDBFileExists + " " + internalDBFileExists);
    	if  (sdDBFileExists || internalDBFileExists)
    		result = true;
    	
    	return result;
    }
 
    private void UnzipDataBase() throws IOException{
    	Log.w("unzip", "Starting unzip of DB");
    	try {
    		
	    	String zipFile;
	    	String unzipLocation; 
	    	
		    zipFile = Environment.getExternalStorageDirectory() + "/goodguide/" + DB_ZIP_NAME;
		    unzipLocation = Environment.getExternalStorageDirectory() + "/goodguide/"; 
	    	
		    boolean useSdCard = new File(zipFile).exists();
		    
		    if (!useSdCard) {
		    	zipFile = DB_PATH + DB_ZIP_NAME;
		    	unzipLocation = DB_PATH; 
	    	}

	    	 
	    	Unzip d = new Unzip(zipFile, unzipLocation); 
	    	d.unzipFile(); 
	    	
	    	//delete file after unzipped
	    	File file = new File(zipFile);
	    	boolean deleted = file.delete();
	    	if(Constants.showLog) Log.v(TAG, "zip:"+zipFile+" deleted?"+deleted);
    	}
    	catch (Exception e) {
    		Log.w("unzip", "Exception thrown on unzip");
    	}
    	Log.w("unzip", "Finished unzip of DB");
    }
    
    
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{
 
    	//Open your local db as the input stream
    	InputStream myInput = myContext.getAssets().open(DB_ZIP_NAME);
    	if(Constants.showLog) Log.v(TAG, "inputstream:"+myInput);
    	
    	File sdCardPath= Environment.getExternalStorageDirectory();
    	File internalPath= Environment.getDataDirectory();
    	
    	StatFs internalStat = new StatFs(internalPath.getPath());
    	StatFs sdStat = new StatFs(sdCardPath.getPath());
    	
    	Log.v(TAG, "INTERNAL: " + internalPath.getPath() + "     SD: " + sdCardPath.getPath());

    	long internalBlockSize = internalStat.getBlockSize();
    	long internalAvailableBlocks = internalStat.getAvailableBlocks();
    	long sdBlockSize = sdStat.getBlockSize();
    	long sdAvailableBlocks = sdStat.getAvailableBlocks();
    	
    	long internalAvailableBytes = (internalAvailableBlocks * internalBlockSize);
    	long sdAvailableBytes = sdAvailableBlocks * sdBlockSize;
    	
    	Log.v(TAG, "Internal free space: " + internalAvailableBytes);    	
    	Log.v(TAG, "SD card free space: " + sdAvailableBytes);

    	String pathToDb;
    	
    	/* For the time being we'll only install on internal memory; It is going to be possible to install on SD Card
    	 * but I need more time to find out what the SQLiteDatabase class is doing with cursors. 
    	 */
    	
    	if (sdAvailableBytes > 120000000) {
    		File ggdir = new File(Environment.getExternalStorageDirectory() + "/goodguide/");
    		if (ggdir.mkdir()) {
    			Log.v(TAG,"Created dir on SD");
    		}
    		else
    			Log.v(TAG,"Couldn't create dir on SD");
    		
    		pathToDb = Environment.getExternalStorageDirectory() + "/goodguide/";
    	}
    	else
    		pathToDb = DB_PATH;
    	
    	Log.v(TAG, "Copying to " + pathToDb);

    	// Path to the just created empty db
    	String outFileName = pathToDb + DB_ZIP_NAME;
 
    	if(Constants.showLog) Log.v(TAG, "new db:"+outFileName);
    	
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
    	if(Constants.showLog) Log.v(TAG, "outputstream:"+myOutput);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[10240];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	if(Constants.showLog) Log.v(TAG, "done with copy");
    	
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
  
    }
    
    public void OpenDB() throws SQLException{
 
    	boolean use_sd = new File(Environment.getExternalStorageDirectory() + "/goodguide/" + DB_NAME).exists();
    	//Open the database
    	
    	String myPath;
    	
    	if (use_sd) {
    		Log.v(TAG, "Opening SD DB");
    		myPath = Environment.getExternalStorageDirectory() + "/goodguide/" + DB_NAME;
    	}
    	else {
    		Log.v(TAG, "Opening Internal DB");
    		myPath = DB_PATH + DB_NAME;
    	}
    	
		Log.v(TAG, "Opening " + myPath);

		try {
			File myDb = new File(myPath);
	    	// myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
			
			myDataBase= SQLiteDatabase.openOrCreateDatabase(myPath, null);
			
			//DataBase = SQLiteDatabase.openOrCreateDatabase(myPath, null);
			Log.v(TAG, "Opened " + myPath);
		}
		catch (SQLiteException e) {
			Log.v(TAG, e.getMessage());
		}

    }
    public SQLiteDatabase getReadableDatabase() {
        if (myDataBase==null)
            OpenDB();
        return myDataBase;
     }
    public SQLiteDatabase getWritableDatabase() {
        if (myDataBase==null)
            OpenDB();
        return myDataBase;
     }

    @Override
	public synchronized void close() {
 
    	    if(myDataBase != null)
    		    myDataBase.close();
 
    	    super.close();
 
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
 
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 
	}
 
        // Add your public helper methods to access and get content from the database.
       // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
       // to you to create adapters for your views.
 
}
