package com.appjumper.silkscreen.util.manager;

import android.app.Activity;
import android.content.SharedPreferences;

import com.appjumper.silkscreen.base.MyApplication;
import com.appjumper.silkscreen.bean.HomeDataResponse;
import com.appjumper.silkscreen.bean.MaterProduct;
import com.appjumper.silkscreen.bean.User;
import com.appjumper.silkscreen.net.JsonUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MyUserManager {
    private User instanceUser;

    private static MyApplication mContext;
    private List<MaterProduct> product;
    private HomeDataResponse instanceHome=null;

    public MyUserManager(MyApplication mContext) {
        this.mContext = mContext;
    }

    /**
     * 获取User
     *
     * @return
     */
    public User getInstance() {
        return instanceUser;
    }

    /**
     * 获取UserId
     *
     * @return
     */
    public String getUserId() {
        return instanceUser == null ? "" : instanceUser.getId();
    }

    /**
     * 只运行初始化app调用
     */
    public void checkUserInfo() {
        SharedPreferences pref = mContext.getSharedPreferences("USER",
                Activity.MODE_PRIVATE);
        instanceUser = JsonUtil.getObject(pref.getString("USERJSON", ""),
                User.class);
    }
    /**
     * 只运行初始化app调用首页
     */
    public void checkHomeInfo() {
        SharedPreferences pref = mContext.getSharedPreferences("HOME",
                Activity.MODE_PRIVATE);
        instanceHome = JsonUtil.getObject(pref.getString("HOMEJSON", ""),
                HomeDataResponse.class);
    }

    /**
     * 保存首页信息
     */
    public void storeHomeInfo(HomeDataResponse ub) {
        instanceHome = ub;
        SharedPreferences pref = mContext.getSharedPreferences("HOME",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("HOMEJSON", JsonUtil.getJson(ub));
        editor.commit();
    }

    /**
     * 获取首页信息
     *
     * @return
     */
    public HomeDataResponse getHome() {
        return instanceHome;
    }

    /**
     * 登录成功保存个人资料
     */
    public void storeUserInfo(User ub) {
        instanceUser = ub;
        SharedPreferences pref = mContext.getSharedPreferences("USER",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("USERJSON", JsonUtil.getJson(ub));
        editor.commit();
    }

    /**
     * 退出清空帐号信息
     */
    public void clean() {
        instanceUser = null;
        SharedPreferences pref = mContext.getSharedPreferences("USER",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 获取历史记录
     *
     * @return
     */
    public Set getHistory() {
        SharedPreferences pref = mContext.getSharedPreferences("HISTORY",
                Activity.MODE_PRIVATE);
        Set<String> keySet = pref.getStringSet("HISTORY", new HashSet<String>());
        return keySet;
    }

    /**
     * 保存历史记录
     */
    public void saveHistory(String history) {
        SharedPreferences pref = mContext.getSharedPreferences("HISTORY",
                Activity.MODE_PRIVATE);
        Set<String> keySet = pref.getStringSet("HISTORY", new HashSet<String>());
        SharedPreferences.Editor editor = pref.edit();
        HashSet<String> hashSet = new HashSet<>(keySet);
        hashSet.add(history);
        editor.putStringSet("HISTORY", hashSet);
        editor.commit();
    }

    /**
     * 清空历史记录
     */
    public void cleanHistory() {
        SharedPreferences pref = mContext.getSharedPreferences("HISTORY",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
}
