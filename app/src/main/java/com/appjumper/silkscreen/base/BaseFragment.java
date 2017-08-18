package com.appjumper.silkscreen.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.bean.User;
import com.appjumper.silkscreen.ui.my.LoginActivity;
import com.appjumper.silkscreen.util.AppToast;
import com.appjumper.silkscreen.util.MProgressDialog;

import org.apache.http.message.BasicNameValuePair;

import butterknife.ButterKnife;


/**
 * Created by Administrator on 2016/6/29.
 */
public abstract class BaseFragment extends Fragment {


    public final static int NETWORK_SUCCESS_DATA_RIGHT = 0x01;
    public final static int NETWORK_SUCCESS_PAGER_RIGHT = 0x02;
    public final static int NETWORK_OTHER = 0x19;
    public final static int NETWORK_SUCCESS_DATA_ERROR = 0x06;
    public final static int NETWORK_FAIL = 0x05;
    public MProgressDialog progress;

    protected FragmentActivity context;

    protected boolean isViewCreated = false;
    protected boolean isDataInited = false;


    protected abstract void initData();


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isViewCreated && !isDataInited) {
            initData();
            isDataInited = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        isViewCreated = true;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getUserVisibleHint()) {
            initData();
            isDataInited = true;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isViewCreated = false;
        isDataInited = false;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }


    /**
     * 打开Activity
     *
     * @param activity
     * @param cls
     * @param name
     */
    public static void start_Activity(Activity activity, Class<?> cls,
                                      BasicNameValuePair... name) {
        Intent intent = new Intent();
        intent.setClass(activity, cls);
        if (name != null)
            for (int i = 0; i < name.length; i++) {
                intent.putExtra(name[i].getName(), name[i].getValue());
            }
        activity.startActivity(intent);
        //activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

    }




    /**
     * 打开ForResultActivity
     *
     * @param activity
     * @param cls
     * @param name
     */
    protected void startForResult_Activity(Activity activity, Class<?> cls, int requestCode,
                                               BasicNameValuePair... name) {
        Intent intent = new Intent();
        intent.setClass(activity, cls);
        if (name != null)
            for (int i = 0; i < name.length; i++) {
                intent.putExtra(name[i].getName(), name[i].getValue());
            }
        startActivityForResult(intent, requestCode);
        //activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

    }




    /**
     * 不带文字转转框
     */
    public void initProgressDialog() {
        initProgressDialog(true, null);
    }

    /**
     * 带文字转转框
     *
     * @param cancel
     * @param message
     */
    public void initProgressDialog(boolean cancel, String message) {
        progress = new MProgressDialog(getActivity(), cancel, message);
    }

    public void initProgressDialog(Context mContext, boolean cancel,
                                   String message) {
        progress = new MProgressDialog(mContext, cancel, message);
//        progress.setMessage(message);
    }

    /**
     * 关闭软键盘
     */
    public void hideKeyboard(View v) {
        ((InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(v.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 关闭软键盘
     */
    public void hideKeyboard() {
        hideKeyboard(getActivity().getWindow().getDecorView());
    }


    /**
     * 如果登录了，返回true，否则返回false并去登录
     *
     * @return
     */
    public boolean checkLogined() {
        if ("".equals(getUserID()) || getUserID() == null) {
            Intent i = new Intent(getActivity(), LoginActivity.class);
            startActivity(i);
            return false;
        } else {
            return true;
        }
    }



    /**
     * 获取Application
     *
     * @return
     */
    public MyApplication getMyApplication() {
        return (MyApplication) getActivity().getApplication();
    }

    /**
     * 获取个人资料
     *
     * @return
     */
    public User getUser() {
        User instanceUser = getMyApplication()
                .getMyUserManager().getInstance();
        return instanceUser;
    }

    /**
     * 获取userId
     *
     * @return
     */
    public String getUserID() {
        User instanceUser = getUser();
        return instanceUser == null ? "" : instanceUser.getId();
    }

    /**
     * 网络连接失败提示
     */
    public void showErrorToast() {
        showFailTips("无法连接到网络\n请稍后再试");
    }

    /**
     * 自定义提示
     *
     * @param content
     */
    public void showFailTips(String content) {
        AppToast tipsToast = AppToast.makeText(context, content, AppToast.LENGTH_SHORT);
        tipsToast.show();
    }

    /**
     * 提交成功提示
     *
     * @param content
     */
    public void showSuccessTips(String content) {
        AppToast tipsToast = AppToast.makeText(context, content, AppToast.LENGTH_SHORT);
        tipsToast.setIcon(R.mipmap.tips_success);
        tipsToast.show();
    }


    /**
     * 普通toast
     *
     * @param err
     */
    public void showErrorToast(String err) {
        Toast.makeText(context, err, Toast.LENGTH_SHORT).show();
    }

    public void setPullLvHeight(ListView pull) {
        int totalHeight = 0;
        ListAdapter adapter = pull.getAdapter();
        for (int i = 0, len = adapter.getCount(); i < len; i++) { //listAdapter.getCount()返回数据项的数目
            View listItem = adapter.getView(i, null, pull);
            listItem.measure(0, 0); //计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); //统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = pull.getLayoutParams();
        params.height = totalHeight + (pull.getDividerHeight() * (pull.getCount() - 1));
        pull.setLayoutParams(params);
    }

    /**
     * @param context
     * @param pxValue
     * @return
     */
    public int dip(Context context, int pxValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pxValue, context.getResources().getDisplayMetrics());
    }
}
