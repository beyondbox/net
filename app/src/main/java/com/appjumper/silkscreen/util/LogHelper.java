package com.appjumper.silkscreen.util;

import android.util.Log;

/**
 * Created by Botx on 2017/3/16.
 */
public class LogHelper {
	private static boolean debug = true;
	public static int DEBUG = android.util.Log.DEBUG;

	public static void i(String tag, String msg) {
		if (debug) {

			if(msg.length() > 4000) {
				for(int i=0; i < msg.length(); i+=4000){
					if(i+4000 < msg.length())
						Log.i(tag+i, msg.substring(i, i+4000));
					else
						Log.i(tag+i, msg.substring(i, msg.length()));
				}
			} else
				Log.i(tag, msg);
		}

			//android.util.Log.i(tag, msg);
	}

	public static void e(String tag, String msg) {
		if (debug)
			android.util.Log.e(tag, msg);
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (debug)
			android.util.Log.e(tag, msg, tr);
	}

	public static void d(String tag, String msg) {
		if (debug)
			android.util.Log.d(tag, msg);
	}

	public static void v(String tag, String msg) {
		if (debug)
			android.util.Log.v(tag, msg);
	}

	public static boolean isLoggable(String arg0, int arg1) {
		if (debug)
			return android.util.Log.isLoggable(arg0, arg1);
		else
			return false;
	}
}
