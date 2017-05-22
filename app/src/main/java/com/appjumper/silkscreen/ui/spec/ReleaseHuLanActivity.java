package com.appjumper.silkscreen.ui.spec;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.appjumper.silkscreen.R;
import com.appjumper.silkscreen.ui.my.adapter.SpecChoiceAdapter;
import com.appjumper.silkscreen.view.phonegridview.BasePhotoGridActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * 护栏网发布
 * Created by Botx on 2017/5/22.
 */

public class ReleaseHuLanActivity extends BasePhotoGridActivity {

    @Bind(R.id.llSpecKuangJia)
    LinearLayout llSpecKuangJia;
    @Bind(R.id.llSpecLiZhu)
    LinearLayout llSpecLiZhu;

    @Bind(R.id.chkKuangJia)
    CheckBox chkKuangJia;
    @Bind(R.id.chkLiZhu)
    CheckBox chkLiZhu;

    @Bind(R.id.llYuanZhu)
    LinearLayout llYuanZhu;
    @Bind(R.id.llFangZhu)
    LinearLayout llFangZhu;
    @Bind(R.id.llTaoXingZhu)
    LinearLayout llTaoXingZhu;

    @Bind(R.id.choiceXZGuan)
    GridView choiceXZGuan;
    @Bind(R.id.choiceXZLiZhu)
    GridView choiceXZLiZhu;
    @Bind(R.id.choiceZhiJing)
    GridView choiceZhiJing;
    @Bind(R.id.choiceChiCun)
    GridView choiceChiCun;

    @Bind(R.id.lowSuHou)
    EditText lowSuHou;
    @Bind(R.id.highSuHou)
    EditText highSuHOu;
    @Bind(R.id.lowSuQian)
    EditText lowSuQian;
    @Bind(R.id.highSuQian)
    EditText highSuQian;
    @Bind(R.id.lowKongKuan)
    EditText lowKongKuan;
    @Bind(R.id.highKongKuan)
    EditText highKongKuan;
    @Bind(R.id.lowKongChang)
    EditText lowKongChang;
    @Bind(R.id.highKongChang)
    EditText highKongChang;
    @Bind(R.id.lowPianKuan)
    EditText lowPianKuan;
    @Bind(R.id.highPianKuan)
    EditText highPianKuan;
    @Bind(R.id.lowPianChang)
    EditText lowPianChang;
    @Bind(R.id.highPianChang)
    EditText highPianChang;
    @Bind(R.id.lowWeightWangPian)
    EditText lowWeightWangPian;
    @Bind(R.id.highWeightWangPian)
    EditText highWeightWangPian;
    @Bind(R.id.lowMianKuan)
    EditText lowMianKuan;
    @Bind(R.id.highMianKuan)
    EditText highMianKuan;
    @Bind(R.id.lowMianChang)
    EditText lowMianChang;
    @Bind(R.id.highMianChang)
    EditText highMianChang;
    @Bind(R.id.lowGuanHou)
    EditText lowGuanHou;
    @Bind(R.id.highGuanHou)
    EditText highGuanHou;
    @Bind(R.id.lowLiZhuKuan)
    EditText lowLiZhuKuan;
    @Bind(R.id.highLiZhuKuan)
    EditText highLiZhuKuan;
    @Bind(R.id.lowLiZhuChang)
    EditText lowLiZhuChang;
    @Bind(R.id.highLiZhuChang)
    EditText highLiZhuChang;
    @Bind(R.id.lowBiHou)
    EditText lowBiHou;
    @Bind(R.id.highBiHou)
    EditText highBiHou;
    @Bind(R.id.lowHeight)
    EditText lowHeight;
    @Bind(R.id.highHeight)
    EditText highHeight;
    @Bind(R.id.lowWeightLiZhu)
    EditText lowWeightLiZhu;
    @Bind(R.id.highWeightLizhu)
    EditText highWeightLizhu;



    private String [] xzGuanArr = {"方管", "圆管"};
    private String [] xzLiZhuArr = {"圆柱", "方柱", "桃形柱"};
    private String [] zhiJingArr = {"48", "60", "75", "其它"};
    private String [] chiCunArr = {"5*10", "7*10", "7*15", "其它"};

    private SpecChoiceAdapter xzGuanAdapter;
    private SpecChoiceAdapter xzLiZhuAdapter;
    private SpecChoiceAdapter zhiJingAdapter;
    private SpecChoiceAdapter chiCunAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_hulan);
        ButterKnife.bind(this);

        initView();
        initData();
    }


    private void initData() {
        xzGuanAdapter = new SpecChoiceAdapter(context, Arrays.asList(xzGuanArr));
        choiceXZGuan.setAdapter(xzGuanAdapter);

        zhiJingAdapter = new SpecChoiceAdapter(context, Arrays.asList(zhiJingArr));
        choiceZhiJing.setAdapter(zhiJingAdapter);

        chiCunAdapter = new SpecChoiceAdapter(context, Arrays.asList(chiCunArr));
        choiceChiCun.setAdapter(chiCunAdapter);

        xzLiZhuAdapter = new SpecChoiceAdapter(context, Arrays.asList(xzLiZhuArr));
        xzLiZhuAdapter.setChoiceMode(SpecChoiceAdapter.CHOICE_MODE_SINGLE);
        choiceXZLiZhu.setAdapter(xzLiZhuAdapter);
        choiceXZLiZhu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                xzLiZhuAdapter.changeSelected(position);
                switch (position) {
                    case 0:
                        llYuanZhu.setVisibility(View.VISIBLE);
                        llFangZhu.setVisibility(View.GONE);
                        llTaoXingZhu.setVisibility(View.GONE);
                        break;
                    case 1:
                        llFangZhu.setVisibility(View.VISIBLE);
                        llYuanZhu.setVisibility(View.GONE);
                        llTaoXingZhu.setVisibility(View.GONE);
                        break;
                    case 2:
                        llTaoXingZhu.setVisibility(View.VISIBLE);
                        llYuanZhu.setVisibility(View.GONE);
                        llFangZhu.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setDefault(choiceXZGuan, 0);
                setDefault(choiceXZLiZhu, 0);
                setDefault(choiceZhiJing, 0);
                setDefault(choiceChiCun, 0);
            }
        }, 50);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                llSpecKuangJia.setVisibility(View.GONE);
                llSpecLiZhu.setVisibility(View.GONE);
            }
        }, 100);
    }


    /**
     * 设置默认项
     */
    private void setDefault(GridView gridView, int position) {
        ((CheckBox)((LinearLayout)gridView.getChildAt(position)).getChildAt(0)).setChecked(true);
    }



    /**
     * 发布
     */
    private void submit() {
        if (!isDataOK())
            return;

        JSONArray jsonArr = new JSONArray();
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("suhousijing", lowSuHou.getText().toString().trim() + "-" + highSuHOu.getText().toString().trim());
            jsonObj.put("wanglongkuan", lowKongKuan.getText().toString().trim() + "-" + highKongKuan.getText().toString().trim());
            jsonObj.put("wanglongchang", lowKongChang.getText().toString().trim() + "-" + highKongChang.getText().toString().trim());
            jsonObj.put("wangpiankuan", lowPianKuan.getText().toString().trim() + "-" + highPianKuan.getText().toString().trim());
            jsonObj.put("wangpianchang", lowPianChang.getText().toString().trim() + "-" + highPianChang.getText().toString().trim());

            if (TextUtils.isEmpty(lowSuQian.getText().toString().trim()))
                jsonObj.put("suqiansijing", " ");
            else
                jsonObj.put("suqiansijing", lowSuQian.getText().toString().trim() + "-" + highSuQian.getText().toString().trim());

            if (TextUtils.isEmpty(lowWeightWangPian.getText().toString().trim()))
                jsonObj.put("wangpianzhongliang", " ");
            else
                jsonObj.put("wangpianzhongliang", lowWeightWangPian.getText().toString().trim() + "-" + highWeightWangPian.getText().toString().trim());

            if (chkKuangJia.isChecked()) {
                String gxzStr = "";
                for (int i = 0; i < 2; i++) {
                    LinearLayout linearLayout = (LinearLayout) choiceXZGuan.getChildAt(i);
                    if (((CheckBox)linearLayout.getChildAt(0)).isChecked()) {
                        gxzStr += xzGuanArr[i] + ",";
                    }
                }
                jsonObj.put("guanxingzhuang", gxzStr);

                if (TextUtils.isEmpty(lowMianKuan.getText().toString().trim()))
                    jsonObj.put("guanhengjiemiankuan", " ");
                else
                    jsonObj.put("guanhengjiemiankuan", lowMianKuan.getText().toString().trim() + "-" + highMianKuan.getText().toString().trim());

                if (TextUtils.isEmpty(lowMianChang.getText().toString().trim()))
                    jsonObj.put("guanhengjiemianchang", " ");
                else
                    jsonObj.put("guanhengjiemianchang", lowMianChang.getText().toString().trim() + "-" + highMianChang.getText().toString().trim());

                if (TextUtils.isEmpty(lowGuanHou.getText().toString().trim()))
                    jsonObj.put("guanghoudu", " ");
                else
                    jsonObj.put("guanghoudu", lowGuanHou.getText().toString().trim() + "-" + highGuanHou.getText().toString().trim());
            } else {
                jsonObj.put("guanxingzhuang", " ");
                jsonObj.put("guanhengjiemiankuan", " ");
                jsonObj.put("guanhengjiemianchang", " ");
                jsonObj.put("guanghoudu", " ");
            }



            if (chkLiZhu.isChecked()) {
                if (llYuanZhu.getVisibility() == View.VISIBLE) {

                } else {

                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }











    /**
     * 判断用户输入数据是否合理
     */
    private boolean isDataOK() {
        String lSuhou = lowSuHou.getText().toString().trim();
        String hSuhou = highSuHOu.getText().toString().trim();
        if (TextUtils.isEmpty(lSuhou) || TextUtils.isEmpty(hSuhou)) {
            showErrorToast("请输入完整的塑后丝径");
            return false;
        }
        if (Float.valueOf(lSuhou) < 2.5 || Float.valueOf(hSuhou) > 8) {
            showErrorToast("塑后丝径的范围为2.5-8");
            return false;
        }

        String lSuqian = lowSuQian.getText().toString().trim();
        String hSuqian = highSuQian.getText().toString().trim();
        if (!TextUtils.isEmpty(lSuqian) || !TextUtils.isEmpty(hSuqian)) {
            if (!TextUtils.isEmpty(lSuqian) && !TextUtils.isEmpty(hSuqian)) {
                if (Float.valueOf(lSuqian) < 2 || Float.valueOf(hSuqian) > 6) {
                    showErrorToast("塑前丝径的范围为2-6");
                    return false;
                }
            } else {
                showErrorToast("请输入完整的塑前丝径");
                return false;
            }
        }

        String lKongkuan = lowKongKuan.getText().toString().trim();
        String hKongkuan = highKongKuan.getText().toString().trim();
        if (TextUtils.isEmpty(lKongkuan) || TextUtils.isEmpty(hKongkuan)) {
            showErrorToast("请输入完整的网孔宽度");
            return false;
        }
        if (Float.valueOf(lKongkuan) < 5 || Float.valueOf(hKongkuan) > 20) {
            showErrorToast("网孔宽度的范围为5-20");
            return false;
        }

        String lKongchang = lowKongChang.getText().toString().trim();
        String hKongchang = highKongChang.getText().toString().trim();
        if (TextUtils.isEmpty(lKongchang) || TextUtils.isEmpty(hKongchang)) {
            showErrorToast("请输入完整的网孔长度");
            return false;
        }
        if (Float.valueOf(lKongchang) < 5 || Float.valueOf(hKongchang) > 30) {
            showErrorToast("网孔长度的范围为5-30");
            return false;
        }

        String lPiankuan = lowPianKuan.getText().toString().trim();
        String hPiankuan = highPianKuan.getText().toString().trim();
        if (TextUtils.isEmpty(lPiankuan) || TextUtils.isEmpty(hPiankuan)) {
            showErrorToast("请输入完整的网片宽度");
            return false;
        }
        if (Float.valueOf(lPiankuan) < 0.1 || Float.valueOf(hPiankuan) > 10) {
            showErrorToast("网片宽度的范围为0.1-10");
            return false;
        }

        String lPianchang = lowPianChang.getText().toString().trim();
        String hPianchang = highPianChang.getText().toString().trim();
        if (TextUtils.isEmpty(lPianchang) || TextUtils.isEmpty(hPianchang)) {
            showErrorToast("请输入完整的网片长度");
            return false;
        }
        if (Float.valueOf(lPianchang) < 0.1 || Float.valueOf(hPianchang) > 10) {
            showErrorToast("网片长度的范围为0.1-10");
            return false;
        }

        String lWeightWangpian = lowWeightWangPian.getText().toString().trim();
        String hWeightWangpian = highWeightWangPian.getText().toString().trim();
        if (!TextUtils.isEmpty(lWeightWangpian) || !TextUtils.isEmpty(hWeightWangpian)) {
            if (!TextUtils.isEmpty(lWeightWangpian) && !TextUtils.isEmpty(hWeightWangpian)) {
                if (Float.valueOf(lWeightWangpian) < 1 || Float.valueOf(hWeightWangpian) > 100) {
                    showErrorToast("网片重量的范围为1-100");
                    return false;
                }
            } else {
                showErrorToast("请输入完整的网片重量");
                return false;
            }
        }


        if (chkKuangJia.isChecked()) {
            String lMiankuan = lowMianKuan.getText().toString().trim();
            String hMiankuan = highMianKuan.getText().toString().trim();
            if (!TextUtils.isEmpty(lMiankuan) || !TextUtils.isEmpty(hMiankuan)) {
                if (!TextUtils.isEmpty(lMiankuan) && !TextUtils.isEmpty(hMiankuan)) {
                    if (Float.valueOf(lMiankuan) < 1 || Float.valueOf(hMiankuan) > 10) {
                        showErrorToast("管横截面宽的范围为1-10");
                        return false;
                    }
                } else {
                    showErrorToast("请输入完整的管横截面宽");
                    return false;
                }
            }

            String lMianchang = lowMianChang.getText().toString().trim();
            String hMianchang = highMianChang.getText().toString().trim();
            if (!TextUtils.isEmpty(lMianchang) || !TextUtils.isEmpty(hMianchang)) {
                if (!TextUtils.isEmpty(lMianchang) && !TextUtils.isEmpty(hMianchang)) {
                    if (Float.valueOf(lMianchang) < 1 || Float.valueOf(hMianchang) > 10) {
                        showErrorToast("管横截面长的范围为1-10");
                        return false;
                    }
                } else {
                    showErrorToast("请输入完整的管横截面长");
                    return false;
                }
            }

            String lGuanhou = lowGuanHou.getText().toString().trim();
            String hGuanhou = highGuanHou.getText().toString().trim();
            if (!TextUtils.isEmpty(lGuanhou) || !TextUtils.isEmpty(hGuanhou)) {
                if (!TextUtils.isEmpty(lGuanhou) && !TextUtils.isEmpty(hGuanhou)) {
                    if (Float.valueOf(lGuanhou) < 0.1 || Float.valueOf(hGuanhou) > 10) {
                        showErrorToast("管厚度的范围为0.1-10");
                        return false;
                    }
                } else {
                    showErrorToast("请输入完整的管厚度");
                    return false;
                }
            }
        }



        if (chkLiZhu.isChecked()) {
            if (llFangZhu.getVisibility() == View.VISIBLE) {
                String lLizhukuan = lowLiZhuKuan.getText().toString().trim();
                String hLizhukuan = highLiZhuKuan.getText().toString().trim();
                if (!TextUtils.isEmpty(lLizhukuan) || !TextUtils.isEmpty(hLizhukuan)) {
                    if (!TextUtils.isEmpty(lLizhukuan) && !TextUtils.isEmpty(hLizhukuan)) {
                        if (Float.valueOf(lLizhukuan) < 1 || Float.valueOf(hLizhukuan) > 20) {
                            showErrorToast("立柱宽度的范围为1-20");
                            return false;
                        }
                    } else {
                        showErrorToast("请输入完整的立柱宽度");
                        return false;
                    }
                }


                String lLizhuchang = lowLiZhuChang.getText().toString().trim();
                String hLizhuchang = highLiZhuChang.getText().toString().trim();
                if (!TextUtils.isEmpty(lLizhuchang) || !TextUtils.isEmpty(hLizhuchang)) {
                    if (!TextUtils.isEmpty(lLizhuchang) && !TextUtils.isEmpty(hLizhuchang)) {
                        if (Float.valueOf(lLizhuchang) < 1 || Float.valueOf(hLizhuchang) > 20) {
                            showErrorToast("立柱长度的范围为1-20");
                            return false;
                        }
                    } else {
                        showErrorToast("请输入完整的立柱长度");
                        return false;
                    }
                }
            }



            String lBihou = lowBiHou.getText().toString().trim();
            String hBihou = highBiHou.getText().toString().trim();
            if (!TextUtils.isEmpty(lBihou) || !TextUtils.isEmpty(hBihou)) {
                if (!TextUtils.isEmpty(lBihou) && !TextUtils.isEmpty(hBihou)) {
                    if (Float.valueOf(lBihou) < 0.1 || Float.valueOf(hBihou) > 10) {
                        showErrorToast("立柱壁厚的范围为0.1-10");
                        return false;
                    }
                } else {
                    showErrorToast("请输入完整的立柱壁厚");
                    return false;
                }
            }


            String lHeight = lowHeight.getText().toString().trim();
            String hHeight = highHeight.getText().toString().trim();
            if (!TextUtils.isEmpty(lHeight) || !TextUtils.isEmpty(hHeight)) {
                if (!TextUtils.isEmpty(lHeight) && !TextUtils.isEmpty(hHeight)) {
                    if (Float.valueOf(lHeight) < 0.1 || Float.valueOf(hHeight) > 10) {
                        showErrorToast("立柱高度的范围为0.1-10");
                        return false;
                    }
                } else {
                    showErrorToast("请输入完整的立柱高度");
                    return false;
                }
            }


            String lWeightLizhu = lowWeightLiZhu.getText().toString().trim();
            String hWeightLizhu = highWeightLizhu.getText().toString().trim();
            if (!TextUtils.isEmpty(lWeightLizhu) || !TextUtils.isEmpty(hWeightLizhu)) {
                if (!TextUtils.isEmpty(lWeightLizhu) && !TextUtils.isEmpty(hWeightLizhu)) {
                    if (Float.valueOf(lWeightLizhu) < 0.1 || Float.valueOf(hWeightLizhu) > 100) {
                        showErrorToast("立柱重量的范围为0.1-100");
                        return false;
                    }
                } else {
                    showErrorToast("请输入完整的立柱重量");
                    return false;
                }
            }


        }


        if (selectedPicture.size() == 0) {
            showErrorToast("请上传产品图片");
            return false;
        }


        return true;
    }





    @OnClick(R.id.tv_confirm)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_confirm:
                submit();
                break;
            default:
                break;
        }
    }


    @OnCheckedChanged({R.id.chkKuangJia, R.id.chkLiZhu})
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.chkKuangJia:
                if (isChecked)
                    llSpecKuangJia.setVisibility(View.VISIBLE);
                else
                    llSpecKuangJia.setVisibility(View.GONE);
                break;
            case R.id.chkLiZhu:
                if (isChecked)
                    llSpecLiZhu.setVisibility(View.VISIBLE);
                else
                    llSpecLiZhu.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }



}
