package com.appjumper.silkscreen.util.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.appjumper.silkscreen.bean.MaterProduct;

/**
 * Created by yc on 16/9/23.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "siwang.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        //CursorFactory设置为null,使用默认值
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //数据库第一次被创建时onCreate会被调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS siwang" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, id VARCHAR, name TEXT,allowDelete TEXT)");
        MaterProduct product = new MaterProduct();
        product.setName("盘条");
        product.setId("1");
        product.setAllowDelete("0");
        addProduct(product,db);
    }

    public void addProduct(MaterProduct product, SQLiteDatabase db) {
        db.execSQL("INSERT INTO siwang VALUES(null, ?, ?,?)", new Object[]{product.getId(), product.getName(),product.isAllowDelete()});
    }
    //如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("ALTER TABLE person ADD COLUMN other STRING");
    }

    /**
     * 删除数据库
     * @param context
     * @return
     */
    public boolean deleteDatabase(Context context) {
        return context.deleteDatabase(DATABASE_NAME);
    }
}
