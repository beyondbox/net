package com.appjumper.silkscreen.util.manager;

import android.app.Activity;


import com.appjumper.silkscreen.base.BaseActivity;

import java.util.Stack;

/**
 * 一个Activity管理器管理活动的Activity。
 */
public class ActivityTaskManager {
    private static ActivityTaskManager activityTaskManager = null;

    private Stack<Activity> activityStack = new Stack<Activity>();

    private ActivityTaskManager() {
    }


    /**
     * 返回activity管理器的唯一实例对象。
     *
     * @return ActivityTaskManager
     */
    public static synchronized ActivityTaskManager getInstance() {
        if (activityTaskManager == null) {
            activityTaskManager = new ActivityTaskManager();
        }
        return activityTaskManager;
    }

    /**
     * 将一个activity添加到管理器。
     *
     * @param activity
     */
    public void putActivity(Activity activity) {
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity getTopActivity() {
        if (activityStack.size() > 0) {
            return activityStack.lastElement();
        }
        return null;
    }

    /**
     * 得到保存在管理器中的Activity对象。
     *
     * @return Activity
     */
    public Activity getActivity(Class<? extends Activity> class1) {
        for (Activity activity : activityStack) {
            if (activity.getClass().getSimpleName().equals(class1.getSimpleName())) {
                return activity;
            }
        }
        return null;
    }

    /**
     * 关闭所有活动的Activity
     */
    public void closeAllActivity() {
        for (Activity activity : activityStack) {
            finisActivity(activity);
        }
        activityStack.clear();
    }

    /**
     *
     */
    public void closeOtherAllActivity(Class<? extends BaseActivity> class1) {
        Activity temp = null;
        for (int i = 0; i < activityStack.size(); i++) {
            Activity activity = activityStack.get(i);
            if (activity.getClass().getSimpleName().equals(class1.getSimpleName())) {
                temp = activity;
            } else {
                finisActivity(activity);
            }
        }
        activityStack.clear();
        if (temp != null) {
            activityStack.add(temp);
        }

    }

    /**
     * 移除Activity对象,如果它未结束则结束它。
     *
     * @param class1 所要删除的Activity类
     */
    public void removeActivity(Class<? extends BaseActivity> class1) {
        for (int i = activityStack.size() - 1; i >= 0; i--) {
            Activity activity = activityStack.get(i);
            if (activity.getClass().getSimpleName().equals(class1.getSimpleName())) {
                finisActivity(activity);
                activityStack.remove(activity);
                return;
            }
        }
    }

    /**
     * 结束指定的Activity
     *
     * @param activity 指定的Activity。
     */
    private final void finisActivity(Activity activity) {
        if (activity != null && !activity.isFinishing()) {
            activity.finish();
        }
    }

    public int getActivityCount() {
        return activityStack.size();
    }
}