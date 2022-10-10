package com.vnptt.ota.settings;

import java.util.HashMap;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class OtaSettingsHelper {
	
	public static final String OTA_SETTINGS_PROVIDER_URI = "content://com.vnptt.ota.settings.otasettingsprovider/otasettings";

	private static final String COLUMN_NAME = "name";
	private static final String COLUMN_VALUE = "value";
	private static final String COLUMN_ID = "_id";

	public static final String OTA_SETTINGS = "ota_settings";

	public static boolean putString(ContentResolver resolver, String name, String value) {
		return putValueToDb(resolver, name, value);
	}

	public static boolean putBoolean(ContentResolver resolver, String name, boolean value) {
		String tempValue = String.valueOf(value);
		return putValueToDb(resolver, name, tempValue);
	}

	public static boolean putInt(ContentResolver resolver, String name, int value){
		String tempValue = String.valueOf(value);
		return putValueToDb(resolver, name, tempValue);
	}

	public static String getString(ContentResolver resolver, String name, String defValue) {
		return getValueToDb(resolver, name, defValue);
	}
	
	public static int getInt(ContentResolver resolver, String name, int defValue) {
		String tempValue = String.valueOf(defValue);
		try { 
			return Integer.valueOf(getValueToDb(resolver, name, tempValue));
		} catch (NumberFormatException e){
			return defValue;
		}
		
	}
	
	public static boolean getBoolean(ContentResolver resolver, String name, boolean defValue) {
		String tempValue = String.valueOf(defValue);
		try { 
			return Boolean.valueOf(getValueToDb(resolver, name, tempValue));
		} catch (NumberFormatException e){
			return defValue;
		}
	}

	public static HashMap<String, String> getAllValue(ContentResolver resolver) {
		HashMap<String, String> returnValue = new HashMap<String, String>();
		Uri simStateUri = Uri.parse(OTA_SETTINGS_PROVIDER_URI);
		Cursor cursor = null;
		try {
			cursor = resolver.query(simStateUri, null, null, null, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						returnValue.put(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)), 
								cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VALUE)));
					}while (cursor.moveToNext());
				}
			} 
		} catch (Exception e) {
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return returnValue;
	}

	private static String getValueToDb(ContentResolver resolver, String name, String defValue) {
		Uri simStateUri = Uri.parse(OTA_SETTINGS_PROVIDER_URI);
		String selection = COLUMN_NAME + "=?";
		Cursor cursor = null;
		try {
			cursor = resolver.query(simStateUri, null, selection, new String[]{name}, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					return cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VALUE));
				}
			} 
		} catch (Exception e) {
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return defValue;
	}

	private static boolean putValueToDb(ContentResolver resolver, String name, String value) {
		Uri simStateUri = Uri.parse(OTA_SETTINGS_PROVIDER_URI);
		String selection = COLUMN_NAME + "=?";
		Cursor cursor = null;
		try {
			cursor = resolver.query(simStateUri, new String[]{COLUMN_ID, COLUMN_VALUE}, selection, new String[]{name}, null);
			if (cursor == null || !cursor.moveToFirst()) {
				ContentValues values = new ContentValues();
				values.put(COLUMN_NAME, name);
				values.put(COLUMN_VALUE, value);
				if (!resolver.insert(simStateUri, values).equals(null)) {
					return true;
				}
			} else {
				int comlumId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
				String oldValue = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VALUE));
				if (!oldValue.equalsIgnoreCase(value)) {
					Uri rowUri = ContentUris.withAppendedId(simStateUri, comlumId);
					ContentValues values = new ContentValues(1);
					values.put(COLUMN_VALUE, value);
					if (resolver.update(rowUri, values, null, null) > 0) {
						return true;
					}
				}	
			}
		} catch (Exception e) {
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return false;
	}
}
