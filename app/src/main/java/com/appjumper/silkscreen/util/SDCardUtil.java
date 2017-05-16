package com.appjumper.silkscreen.util;

import android.os.StatFs;

public class SDCardUtil {

	public static String getExternalStoragePath() {
		// 获取SdCard状态
		String state = android.os.Environment.getExternalStorageState();
		// 判断SdCard是否存在并且是可用的
		if (android.os.Environment.MEDIA_MOUNTED.equals(state)) {
			/*if (android.os.Environment.getExternalStorageDirectory().canWrite()) {
				return android.os.Environment.getExternalStorageDirectory().getPath();
			}*/
			return android.os.Environment.getExternalStorageDirectory().getPath();
		}
		return null;
	}

	/**
	 * 获取SDCard空间大小
	 * 
	 * @return -1 没有SDCard
	 */
	public static long getSDcardSize() {

		String sdcard = SDCardUtil.getExternalStoragePath();
		long size = -1;
		if (sdcard != null) {
			size = SDCardUtil.getAvailableStore(sdcard);
		}
		return size;
	}

	public static long getAvailableStore(String filePath) {
		// 取得sdcard文件路径
		StatFs statFs = new StatFs(filePath);
		// 获取block的SIZE
		long blocSize = statFs.getBlockSize();
		// 获取BLOCK数量
		// long totalBlocks = statFs.getBlockCount();
		// 可使用的Block的数量
		long availaBlock = statFs.getAvailableBlocks();
		// long total = totalBlocks * blocSize;
		long availableSpare = availaBlock * blocSize;
		return availableSpare;
	}
	
	/**判断是否手机插入Sd卡*/
	public static boolean hasSdcard() {

		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

}
