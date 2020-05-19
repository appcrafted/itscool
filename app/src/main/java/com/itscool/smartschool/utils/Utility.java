package com.itscool.smartschool.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import com.itscool.smartschool.R;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static android.content.Context.DOWNLOAD_SERVICE;

/* if add this class get error so add in build.gradle second(Module app)
  to below of this line of libray
  buildToolsVersion "23.0.3"
  useLibrary 'org.apache.http.legacy'
*/

public class Utility {

	public static Context appContext;
	private static String PREFERENCE="SmartSchool";

	public static void setSharedPreference(Context context, String name,
                                           String value) {
		appContext = context;
		SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
		SharedPreferences.Editor editor = settings.edit();
		// editor.clear();
		editor.putString(name, value);
		editor.commit();
	}
	
	public static void setIntegerSharedPreference(Context context, String name,
                                                  int value) {
		appContext = context;
		SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
		SharedPreferences.Editor editor = settings.edit();
		// editor.clear();
		editor.putInt(name, value);
		editor.commit();
	}


	public static String getSharedPreferences(Context context, String name) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFERENCE, 0);
		return settings.getString(name, "");
	}

	
	public static int getIntegerSharedPreferences(Context context, String name) {
		SharedPreferences settings = context
				.getSharedPreferences(PREFERENCE, 0);
		return settings.getInt(name, 1);
	}

	public static void setSharedPreferenceBoolean(Context context, String name,
                                                  boolean value) {
		appContext = context;
		SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(name, value);
		editor.commit();
	}

	public static boolean getSharedPreferencesBoolean(Context context, String name) {
		SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
		return settings.getBoolean(name, false);
	}

    public static void clearPreference(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
        SharedPreferences.Editor editor = settings.edit();
        // editor.clear();
        editor.clear();
        editor.commit();
    }

    public static boolean isConnectingToInternet(Context context){
        ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

	public static String parseDate(String orignalFormat, String newFormat, String date) {
        String formattedDate;
        DateFormat targetFormat;
        DateFormat originalFormat = new SimpleDateFormat(orignalFormat, Locale.ENGLISH);
        try {
            targetFormat = new SimpleDateFormat(newFormat, Locale.ENGLISH);
        } catch (IllegalArgumentException IAE) {
            newFormat = newFormat.replace("Y", "y");
            targetFormat = new SimpleDateFormat(newFormat, Locale.ENGLISH);
        }

        try{
            Date newDate = originalFormat.parse(date);
            formattedDate = targetFormat.format(newDate);  // 20120821
        } catch (ParseException E) {
            formattedDate = "";
        }

        return  formattedDate;
    }


	public static long beginDownload(Context context, String filePath, String urlStr){

		String[] fileNameArray = filePath.split("/");
		String fileName = fileNameArray[fileNameArray.length-1];

		Log.e("fileName", fileName);

		String path = Environment.getExternalStorageDirectory()+"/"+Constants.downloadDirectory;

		File file=new File(path);

		if(!file.exists()) {
			file.mkdir();
		}

		File downloadFile = new File(path+"/"+fileName);

        Uri Download_Uri = Uri.parse(urlStr);

        DownloadManager.Request request=new DownloadManager.Request(Download_Uri)
				.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI)
				.setTitle(context.getApplicationContext().getString(R.string.app_name))
				.setDescription("Downloading " + fileName)
				.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
				.setDestinationUri(Uri.fromFile(downloadFile))
				.setAllowedOverMetered(true)
				.setVisibleInDownloadsUi(true)
				.setAllowedOverRoaming(true);
		DownloadManager downloadManager= (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);

        return downloadManager.enqueue(request);

    }

	public static SecretKey generateKey()
			throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		return new SecretKeySpec(Constants.appKey.getBytes(), "AES");
	}

	public static byte[] encryptMsg(SecretKey secret)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException,
			IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException
	{
		/* Encrypt the message. */
		Cipher cipher = null;
		cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secret);
		String url = "https://sstrace.qdocs.in/postlic/verifyappjson";
		//String url = "https://qdocs.in/lictrack/postlic/verifyappjson";
		byte[] cipherText = cipher.doFinal(url.getBytes("UTF-8"));
		return cipherText;
	}

	public static String decryptMsg(byte[] cipherText, SecretKey secret)
			throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException
	{
		/* Decrypt the message, given derived encContentValues and initialization vector. */
		Cipher cipher = null;
		cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secret);
		String decryptString = new String(cipher.doFinal(cipherText), "UTF-8");
		return decryptString;
	}

	public static void setLocale(Context context, String localeName) {

		if(localeName.isEmpty() || localeName.equals("null")) {
			localeName = "en";
			Log.e("localName status", "empty");
		}

		Locale myLocale = new Locale(localeName);
		Locale.setDefault(myLocale);
		Resources res = context.getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);
		Log.e("Utility Status", "Locale updated!");
	}

	 
}// final class ends here

