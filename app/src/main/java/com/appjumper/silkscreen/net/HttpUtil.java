package com.appjumper.silkscreen.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.appjumper.silkscreen.util.LogHelper;
import com.appjumper.silkscreen.util.MD5Tool;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class HttpUtil {


    private static final int DAY = 24 * 60 * 60;// 天
    private static final int HOUR = 60 * 60;// 小时
    private static final int MINUTE = 60;// 分钟

    /**
     * 拼接http请求参数，并加密访问服务器，若不想加密，请查看getDataWithOutSig方法
     * <p>
     * 加密规则：比如要传递的参数为  sex：1  name：张三 age：25
     * 第一步，先按key的首字母排序 并拼接成 String buffermd5 = “age=25&name=张三&sex=1”
     * 第二步，buffermd5再拼接上sig = SIG_KEY。结果为 buffermd5 = “age=25&name=张三&sex=1&sig=iTopic2015”
     * 第三步，将buffermd5做md5加密，得到一个32位小写string
     * 第四步，将上一步得到的string当做一个参数传给服务器，key是“sig”
     */
    public static String getData(Map<String, String> map)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
//        map.put("XDEBUG_SESSION_START", "1");
        List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(
                map.entrySet());

        // 排序
        Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> o1,
                               Map.Entry<String, String> o2) {
                return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });

        StringBuffer buffer = new StringBuffer();
        StringBuffer buffermd5 = new StringBuffer();
        for (int i = 0; i < infoIds.size(); i++) {
            String key = infoIds.get(i).getKey();
            buffer.append(key);
            buffermd5.append(key);
            buffer.append("=");
            buffermd5.append("=");

            String value = infoIds.get(i).getValue();
            value = value == null ? "" : value;
            buffer.append(URLEncoder.encode(value, "utf-8"));
            buffermd5.append(value);
            buffer.append("&");
            buffermd5.append("&");
        }

        buffer.append("sig");
        buffermd5.append("sig");
        buffer.append("=");
        buffermd5.append("=");
        buffer.append(getMD5(buffermd5.toString() + Url.SIG_KEY));

        return buffer.toString();
    }

    /**
     * 处理图片
     *
     * @param bm 所要转换的bitmap
     * @return 指定宽高的bitmap
     * @param"newWidth新的宽
     * @param:newHeight新的高
     */
    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        //	    matrix.reset();
        return newbm;
    }

    public static String getMD5(String val) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String sb = MD5Tool.getMD5(val);
        return sb.toLowerCase();
    }

    public static String getMD5WithCatch(String val) {
        String sb = val;
        try {
            sb = MD5Tool.getMD5(val);
            return sb.toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sb;
    }

    /**
     * 采用不加密的请求方式去拼接参数
     */
    public static String getDataWithOutSig(Map<String, String> map)
            throws UnsupportedEncodingException {
        StringBuffer buffer = new StringBuffer();
        Iterator<String> ite = map.keySet().iterator();
        while (ite.hasNext()) {
            String key = ite.next();
            String value = map.get(key);
            buffer.append(key);
            buffer.append("=");
            buffer.append(URLEncoder.encode(value, "utf-8"));
            if (ite.hasNext()) {
                buffer.append("&");
            }
        }

        return buffer.toString();
    }

    public static String getDataWithoutEncoder(Map<String, String> map)
            throws UnsupportedEncodingException {
        StringBuffer buffer = new StringBuffer();
        Iterator<String> ite = map.keySet().iterator();
        while (ite.hasNext()) {
            String key = ite.next();
            String value = map.get(key);
            buffer.append(key);
            buffer.append("=");
            buffer.append(value);
            if (ite.hasNext()) {
                buffer.append("&");
            }
        }
        return buffer.toString();
    }

    /**
     * 发出post请求
     *
     * @param data      是通过getData方法返回的string
     * @param serverUrl 请求的url
     * @return
     * @throws IOException
     */
    public static String postMsg(String data, String serverUrl)
            throws IOException {
        LogHelper.e("log", "请求参数------------------" + data + "url------------" + serverUrl);
        String callback = "";
        URL url = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        HttpURLConnection httpUrlConnection = null;
        url = new URL(serverUrl);
        httpUrlConnection = (HttpURLConnection) url.openConnection();

        httpUrlConnection.setRequestMethod("POST");
        httpUrlConnection.setDoInput(true);
        httpUrlConnection.setDoOutput(true);
        httpUrlConnection.setUseCaches(false);
        httpUrlConnection.setRequestProperty("Charset", "UTF-8");
        httpUrlConnection.setConnectTimeout(15 * 1000);
        httpUrlConnection.setReadTimeout(50 * 1000);
        if (data != null) {
            outputStream = httpUrlConnection.getOutputStream();
            outputStream.write(data.getBytes());
        }

        inputStream = httpUrlConnection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream, "utf-8"));
        StringBuilder buffer = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        callback = buffer.toString();
        LogHelper.e("log", "返回数据------------------" + callback);
        if (outputStream != null) {
            outputStream.close();
            outputStream = null;
        }

        if (inputStream != null) {
            inputStream.close();
            inputStream = null;
        }
        if (httpUrlConnection != null) {
            httpUrlConnection.disconnect();
            httpUrlConnection = null;
        }
        return callback;
    }

    /**
     * 发出get请求
     */
    public static String getMsg(String serverUrl) throws IOException {
        LogHelper.e("log", "url------------" + serverUrl);
        String callback = "";
        URL url = null;
        InputStream inputStream = null;
        HttpURLConnection httpUrlConnection = null;
        url = new URL(serverUrl);
        httpUrlConnection = (HttpURLConnection) url.openConnection();

        httpUrlConnection.setRequestMethod("GET");
        httpUrlConnection.setDoInput(true);
        httpUrlConnection.setUseCaches(false);
        httpUrlConnection.setConnectTimeout(15 * 1000);
        httpUrlConnection.setReadTimeout(15 * 1000);

        int responseCode = httpUrlConnection.getResponseCode();
        if (responseCode != 200) {
            LogHelper.e("log", "返回码------------------" + responseCode);
            return callback;
        }
        inputStream = httpUrlConnection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream, "utf-8"));
        StringBuilder buffer = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        callback = buffer.toString();
        LogHelper.e("log", "返回数据------------------" + callback);
        if (inputStream != null) {
            inputStream.close();
            inputStream = null;
        }
        return callback;
    }


    /**
     * 上传多文件 + 自定义参数
     *
     * @param paramsMap 参数
     * @param urlstring 接口url
     * @return
     * @throws Exception
     */
    public static String uploadImageScreen(Map<String, String> paramsMap, String imageName, String urlstring)
            throws Exception {

        // 定义数据分隔线
        String BOUNDARY = "------------------------7dc2fd5c0894";
        // 定义最后数据分隔线
        byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();

        URL url = new URL(urlstring);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("connection", "Keep-Alive");

        conn.setRequestProperty("Charsert", "UTF-8");
        conn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + BOUNDARY);

        OutputStream out = new DataOutputStream(conn.getOutputStream());

        Map<String, String> sigMap = new HashMap<String, String>();

        paramsMap.put("sig", HttpUtil.getSig(paramsMap));
        Iterator<String> ite = paramsMap.keySet().iterator();
        while (ite.hasNext()) {
            String key = ite.next();
            String value = paramsMap.get(key);
            // 附带参数
            StringBuffer params = new StringBuffer();
            params.append("--" + BOUNDARY + "\r\n");
            params.append("Content-Disposition: form-data; name=\"" + key + "\"\r\n\r\n");
            params.append(value);
            params.append("\r\n");

            out.write(params.toString().getBytes());
        }


        /**
         * 图片
         */
        File file = new File(imageName);
        StringBuilder sb = new StringBuilder();
        sb.append("--");
        sb.append(BOUNDARY);
        sb.append("\r\n");
        sb.append("Content-Disposition: form-data;name=\"picture\";filename=\""
                + file.getName() + "\"\r\n");
        // 这里不能漏掉，根据文件类型来来做处理，由于上传的是图片，所以这里可以写成image/pjpeg
        sb.append("Content-Type:image/pjpeg\r\n\r\n");
        out.write(sb.toString().getBytes());

        DataInputStream in = new DataInputStream(new FileInputStream(file));
        int bytes = 0;
        byte[] bufferOut = new byte[1024];
        while ((bytes = in.read(bufferOut)) != -1) {
            out.write(bufferOut, 0, bytes);
        }
        out.write("\r\n".getBytes());
        in.close();


        out.write(end_data);
        out.flush();
        out.close();

		/* 取得Response内容 */
        InputStream inputStream = conn.getInputStream();
        BufferedInputStream bs = new BufferedInputStream(inputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(bs));
        int temp = -1;
        char[] b = new char[1];
        StringBuilder resultbuffer = new StringBuilder();
        while ((temp = reader.read(b, 0, b.length)) != -1) {
            resultbuffer.append(String.valueOf(b));
        }
        reader.close();
        bs.close();
        String callback = resultbuffer.toString();
        LogHelper.e("log", "返回数据--" + callback);
        return callback;
    }

    /* 上传 单个文件至服务器的方法 */
    public static String uploadFile(String actionUrl, File tempPhotoFile)
            throws Exception {
        LogHelper.e("log", actionUrl + "-----" + tempPhotoFile);
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = UUID.randomUUID().toString();
        URL url = new URL(actionUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        /* 允许Input、Output，不使用Cache */
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
		/* setRequestProperty */
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Charset", "UTF-8");

        con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
		/* 设置传送的method=POST */
        con.setRequestMethod("POST");
        con.setConnectTimeout(20 * 1000);
        con.setReadTimeout(20 * 1000);
		/* 设置DataOutputStream */
        DataOutputStream ds = new DataOutputStream(con.getOutputStream());

		/* 开始拼接秘钥sig参数，若不需要加密，这一段可以删除 */
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("sig", HttpUtil.getSig(paramsMap));
        Iterator<String> ite = paramsMap.keySet().iterator();
        while (ite.hasNext()) {
            String key = ite.next();
            String value = paramsMap.get(key);
            StringBuffer params = new StringBuffer();
            params.append("--" + boundary + "\r\n");
            params.append("Content-Disposition: form-data; name=\"" + key + "\"\r\n\r\n");
            params.append(value);
            params.append("\r\n");
            ds.write(params.toString().getBytes());
        }
		/* 秘钥sig参数添加完毕 */
        ds.writeBytes(twoHyphens + boundary + end);
        ds.writeBytes("Content-Disposition: form-data; "
                + "name=\"file\";filename=\"" + " Content-Type:image/jpeg "
                + tempPhotoFile.getName() + "\"" + end);
        ds.writeBytes(end);
		/* 取得文件的FileInputStream */
        FileInputStream fStream = new FileInputStream(tempPhotoFile);
		/* 设置每次写入1024bytes */
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int length = -1;
		/* 从文件读取数据至缓冲区 */
        while ((length = fStream.read(buffer)) != -1) {
			/* 将资料写入DataOutputStream中 */
            ds.write(buffer, 0, length);
        }
        ds.writeBytes(end);
        ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
		/* close_red streams */
        fStream.close();
        ds.flush();
		/* 取得Response内容 */
        InputStream inputStream = con.getInputStream();

        BufferedInputStream bs = new BufferedInputStream(inputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(bs));
        int temp = -1;
        char[] b = new char[1];
        StringBuilder resultbuffer = new StringBuilder();
        while ((temp = reader.read(b, 0, b.length)) != -1) {
            resultbuffer.append(String.valueOf(b));
        }
        reader.close();
        bs.close();
        String callback = resultbuffer.toString();
        LogHelper.e("log", callback + "返回-----");
        return callback;
    }

    /**
     * 上传多文件 + 自定义参数
     * @param paramsMap 参数
     * @param urlstring 接口url
     * @return
     * @throws Exception
     */
    /**
     * 上传多文件 + 自定义参数
     *
     * @param list      本地文件路径
     * @param urlstring 接口url
     * @return
     * @throws Exception
     */
    public static String upload(List<String> list, String urlstring)
            throws Exception {

        // 定义数据分隔线
        String BOUNDARY = "------------------------7dc2fd5c0894";
        // 定义最后数据分隔线
        byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();

        URL url = new URL(urlstring);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("connection", "Keep-Alive");

        conn.setRequestProperty("Charsert", "UTF-8");
        conn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + BOUNDARY);

        OutputStream out = new DataOutputStream(conn.getOutputStream());

        Map<String, String> sigMap = new HashMap<String, String>();


        int leng = list.size();
        for (int i = 0; i < leng; i++) {
            String fname = list.get(i);
            File file = new File(fname);

            StringBuilder sb = new StringBuilder();
            sb.append("--");
            sb.append(BOUNDARY);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;name=\"file[]\";filename=\""
                    + file.getName() + "\"\r\n");
            // 这里不能漏掉，根据文件类型来来做处理，由于上传的是图片，所以这里可以写成image/pjpeg
            sb.append("Content-Type:image/pjpeg\r\n\r\n");
            out.write(sb.toString().getBytes());

            DataInputStream in = new DataInputStream(new FileInputStream(file));
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            out.write("\r\n".getBytes());
            in.close();
        }

        out.write(end_data);
        out.flush();
        out.close();

		/* 取得Response内容 */
        InputStream inputStream = conn.getInputStream();
        BufferedInputStream bs = new BufferedInputStream(inputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(bs));
        int temp = -1;
        char[] b = new char[1];
        StringBuilder resultbuffer = new StringBuilder();
        while ((temp = reader.read(b, 0, b.length)) != -1) {
            resultbuffer.append(String.valueOf(b));
        }
        reader.close();
        bs.close();
        String callback = resultbuffer.toString();
        LogHelper.e("Log", "返回数据-----" + callback);
        return callback;
    }


    /**
     * 得到sig值
     * 这个方法只给上传图片（upload）接口用
     *
     * @param map
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     */
    private static String getSig(Map<String, String> map)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(
                map.entrySet());

        // 排序
        Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> o1,
                               Map.Entry<String, String> o2) {
                // return (o2.getValue() - o1.getValue());
                return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });

        StringBuffer buffermd5 = new StringBuffer();
        for (int i = 0; i < infoIds.size(); i++) {
            String key = infoIds.get(i).getKey();
            buffermd5.append(key);
            buffermd5.append("=");
            String value = infoIds.get(i).getValue();
            value = value == null ? "" : value;
            buffermd5.append(value);
            buffermd5.append("&");
        }
        buffermd5.append("sig");
        buffermd5.append("=");
        return getMD5(buffermd5.toString() + Url.SIG_KEY);
    }


    public static long getTimeLong(String sTime)  {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(sTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date.getTime();
    }


    /**
     * 根据时间戳获取描述性时间，如3分钟前，1天前
     *
     * @param timestamp 时间戳 单位为毫秒
     * @return 时间字符串
     */
    public static String getTimeStringFromNow(long timestamp) {
        long currentTime = System.currentTimeMillis();
        long timeGap = (currentTime - timestamp) / 1000;// 与现在时间相差秒数
        String timeStr = null;
        if (timeGap > DAY) {// 1天以上
            timeStr = getSimpleDate(new Date(timestamp));
        } else if (timeGap > HOUR) {// 1小时-24小时
            timeStr = timeGap / HOUR + "小时前";
        } else if (timeGap > MINUTE) {// 1分钟-59分钟
            timeStr = timeGap / MINUTE + "分钟前";
        } else {// 1秒钟-59秒钟
            timeStr = "刚刚";
        }
        return timeStr;
    }

    public static String getTimeStringFromEndTime(long endtime) {
        long currentTime = System.currentTimeMillis();
        long timeGap = (endtime - currentTime) / 1000;// 与现在时间相差秒数
        String timeStr = null;
        if (timeGap > DAY) {// 1天以上
            timeStr = "" + timeGap / DAY;
        } else {// 1秒钟-59秒钟
            timeStr = "1";
        }
        return timeStr;
    }

    public static String getSimpleDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String time = sdf.format(date);
        return time;
    }

    /**
     * 保留两位小数
     */
    public static String getfloat(String str) {
        float f = Float.parseFloat(str);
        java.text.DecimalFormat myformat = new java.text.DecimalFormat("0.00");
        String string = myformat.format(Double.parseDouble(f + ""));
        return string;
    }

    /**
     * 得到格式化后的系统时间 精确到秒
     *
     * @return
     * @param:date
     */
    public static String getSimpleTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(date);
        return time;
    }

    /**
     * 得到格式化后的系统时间 精确到秒
     *
     * @return
     * @throws java.text.ParseException
     * @param:date
     */
    public static Date getSimpleData(String datestr)
            throws java.text.ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time = sdf.parse(datestr);
        return time;
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
