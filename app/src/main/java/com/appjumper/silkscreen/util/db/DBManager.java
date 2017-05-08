package com.appjumper.silkscreen.util.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.appjumper.silkscreen.bean.MaterProduct;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by yc on 16/9/23.
 */
public class DBManager {
    private DBHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
//        helper.deleteDatabase(context);
        db = helper.getWritableDatabase();

    }

    /**
     * add product
     * @param
     * @param
     */
    public void addProduct(List<MaterProduct> products) {
        db.beginTransaction();  //开始事务
        try {
            for (MaterProduct product : products) {
                db.execSQL("INSERT INTO siwang VALUES(null, ?, ?,?)", new Object[]{product.getId(), product.getName(),product.isAllowDelete()});
            }
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    /**
     * update  name
     * @param
     */
    public void updateName(String id, String name) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        db.update("siwang", cv, "id = ?", new String[]{id});
    }
    /**
     * delete  product
     * @param
     */
    public void deleteProduct() {
//        db.beginTransaction();  //开始事务
//        try {
                db.delete("siwang", null, null);
//            db.setTransactionSuccessful();  //设置事务成功完成
//        } finally {
//            db.endTransaction();    //结束事务
//        }

    }

    /**
     * query all persons, return list
     * @return List<Person>
     */
    public List<MaterProduct> query() {
        ArrayList<MaterProduct> persons = new ArrayList<MaterProduct>();
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            MaterProduct person = new MaterProduct();
            person.setId(c.getString(c.getColumnIndex("id")));
            person.setName(c.getString(c.getColumnIndex("name")));
            persons.add(person);
        }
        c.close();
        return persons;
    }

    /**
     * query all persons, return cursor
     * @return  Cursor
     */
    public Cursor queryTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM siwang", null);
        return c;
    }
    /**
     * query all persons, return cursor
     * @return  Cursor
     */
    public String getName(String id) {
        String name="";
        //读取数据
        Cursor c = db.rawQuery("SELECT* FROM siwang WHERE id = ?", new String[]{id});
        while (c.moveToNext()) {
            name = c.getString(c.getColumnIndex("name"));
        }
        c.close();
        return name;
    }

    /**
     * close_red database
     */
    public void closeDB() {
        db.close();
    }



}
