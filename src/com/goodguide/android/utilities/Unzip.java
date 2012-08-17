package com.goodguide.android.utilities;

import android.util.Log; 

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File; 
import java.io.FileInputStream; 
import java.io.FileNotFoundException;
import java.io.FileOutputStream; 
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry; 
import java.util.zip.ZipInputStream; 

import com.goodguide.android.shared.Constants;
 
/** 
 * 
 * @author jon 
 */ 
public class Unzip { 
  private String _zipFile; 
  private String _location; 
 
  public Unzip(String zipFile, String location) { 
    _zipFile = zipFile; 
    _location = location; 
 
    _dirChecker(""); 
  } 
 
  public void unzip() { 
    try  { 
      FileInputStream fin = new FileInputStream(_zipFile); 
      ZipInputStream zin = new ZipInputStream(fin); 
      ZipEntry ze = null; 
      while ((ze = zin.getNextEntry()) != null) { 
      if(Constants.showLog) Log.v("Decompress", "Unzipping " + ze.getName() + " Size:" + ze.getSize()); 
 
        if(ze.isDirectory()) { 
          _dirChecker(ze.getName()); 
        } else { 
          FileOutputStream fout = new FileOutputStream(_location + ze.getName()); 
          byte[] buffer = new byte[10240];

          for (int c = zin.read(buffer); c != -1; c = zin.read()) { 
            fout.write(buffer, 0, c); 
          } 
 
          zin.closeEntry(); 
          fout.close(); 
        } 
         
      } 
      zin.close(); 
    } catch(Exception e) { 
    if(Constants.showLog) Log.e("Decompress", "unzip", e); 
    } 
 
  } 
 
  public void unzipFile() {
      FileInputStream in;
	try {
		if(Constants.showLog) Log.v("in UnzipFile",_zipFile);
		  in = new FileInputStream(_zipFile);
	      GZIPInputStream inZip = new GZIPInputStream(in);
	      BufferedInputStream in2 = new BufferedInputStream(inZip);
	      FileOutputStream out = new FileOutputStream(_zipFile.replace(".mp3", ""));
	      BufferedOutputStream out2 = new BufferedOutputStream(out);
	      int chunk;
          byte[] buffer = new byte[1024];

	      while ((chunk = in2.read(buffer)) != -1) {
	              out2.write(buffer, 0, chunk);
	      }
	      out2.close();
	      out.close();
	    if(Constants.showLog) Log.v("","Wrote: " + _zipFile.replace(".mp3", ""));
	} catch (Exception e) {
		// TODO Auto-generated catch block
		Log.v("Exception",e.getMessage());
	}
}
  
  private void _dirChecker(String dir) { 
    File f = new File(_location + dir); 
 
    if(!f.isDirectory()) { 
      f.mkdirs(); 
    } 
  } 
} 
