package com.samdroid.file;

import java.io.File;

import android.content.Context;

import com.samdroid.common.MyLog;

public class FileCache {

	private static final String TAG = "FileCache";

	// the cache directory
	protected File cacheDir;

	public FileCache (String folderName, Context context) {
		// make the directory to save data to
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), File.separator + folderName + File.separator + ".Cache");
		} else {
			cacheDir = context.getCacheDir();
		}

		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
	}

	/**
	 * Get a file from the cache
	 * 
	 * @param filename
	 * @return
	 */
	public File getFile (String filename) {
		if (cacheDir == null) {
			MyLog.e(TAG, "getFile -> cache directory is null");
			return null;
		}

		File f = new File(cacheDir, filename);
		return f;
	}

	public File getCacheFile (String url) {
		//I identify images by hashcode. Not a perfect solution, good for the demo.
		String filename = String.valueOf(url.hashCode());
		//Another possible solution (thanks to grantland)
		//String filename = URLEncoder.encode(url);
		File f = new File(cacheDir, filename);
		return f;        
	}

	/**
	 * Clear the cache directory
	 */
	public void clear () {
		if (cacheDir == null) return;

		final File[] files = cacheDir.listFiles();

		if (files == null) return;

		new Thread(new Runnable() {

			@Override
			public void run() {
				for (File f : files)
					if (f != null)
						f.delete();
			}

		}).run();
	}

}
