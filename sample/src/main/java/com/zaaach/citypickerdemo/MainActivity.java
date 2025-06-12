package com.zaaach.citypickerdemo;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.promeg.pinyinhelper.Pinyin;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private TextView currentTV;
    private CheckBox hotCB;
    private CheckBox animCB;
    private CheckBox enableCB;
    private Button themeBtn;

    private static final String KEY = "current_theme";

    private List<HotCity> hotCities=new ArrayList<HotCity>();
    private List<City> list_Cities=new ArrayList<City>();
    private int anim;
    private int theme;
    private boolean enable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            theme = savedInstanceState.getInt(KEY);
            setTheme(theme > 0 ? theme : R.style.DefaultCityPickerTheme);
        }

        setContentView(R.layout.activity_main);

        currentTV = findViewById(R.id.tv_current);
        hotCB = findViewById(R.id.cb_hot);
        animCB = findViewById(R.id.cb_anim);
        enableCB = findViewById(R.id.cb_enable_anim);
        themeBtn = findViewById(R.id.btn_style);

        if (theme == R.style.DefaultCityPickerTheme) {
            themeBtn.setText("默认主题");
        } else if (theme == R.style.CustomTheme) {
            themeBtn.setText("自定义主题");
        }

        hotCB.setOnCheckedChangeListener(this);
        animCB.setOnCheckedChangeListener(this);
        enableCB.setOnCheckedChangeListener(this);

        themeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (themeBtn.getText().toString().startsWith("自定义")) {
                    themeBtn.setText("默认主题");
                    theme = R.style.DefaultCityPickerTheme;
                } else if (themeBtn.getText().toString().startsWith("默认")) {
                    themeBtn.setText("自定义主题");
                    theme = R.style.CustomTheme;
                }
                recreate();
            }
        });
        initData();
        findViewById(R.id.btn_pick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CityPicker.from(MainActivity.this)
                        .enableAnimation(enable)
                        .setAnimationStyle(anim)
                        .setIsOutData(true)
                        .setHintValue("请输入国家/地区名称进行搜索")
                        .setIsTvSearch(false)
                        .setShowLocation(false)
                        .setAllCities(list_Cities)
                        .setLocatedCity(null)
//                        .setHotCities(hotCities)
                        .setOnPickListener(new OnPickListener() {
                            @Override
                            public void onPick(int position, City data) {
                                currentTV.setText(String.format("当前城市：%s，%s", data.getName(), data.getCityCode()));
                                Toast.makeText(
                                        getApplicationContext(),
                                        String.format("点击的数据：%s，%s", data.getName(), data.getCityCode()),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }

                            @Override
                            public void onCancel() {
                                Toast.makeText(getApplicationContext(), "取消选择", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onLocate() {
                                //开始定位，这里模拟一下定位
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
//                                        CityPicker.from(MainActivity.this).locateComplete(new LocatedCity("郑州", "河南", "101180101"), LocateState.SUCCESS);
                                    }
                                }, 3000);
                            }
                        })
                        .show();
            }
        });
    }

    private void initData() {
        String[] arrays = getResources().getStringArray(R.array.arrays_sort);
        list_Cities.clear();
        for (int index = 0; index < arrays.length; index++) {
            City city = new City();
            city.setName(arrays[index]);
            city.setPinyin(Pinyin.toPinyin(city.getName(), ""));
            list_Cities.add(city);
        }
     /*   var city = City()
        city.name = it.name
        city.adCode = it.adcode
        city.cityCode = it.citycode
        var listSplit = it.center.split(",")
        if (listSplit.size == 2) {
            city.lat = listSplit[1]
            city.lng = listSplit[0]
        } else {
            city.lat = ""
            city.lng = ""
        }
        city.pinyin = Pinyin.toPinyin(it.name ?:"", "")*/
//        list_Cities.add();

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_hot:
                if (isChecked) {
                    hotCities = new ArrayList<>();
                    hotCities.add(new HotCity("北京", "北京", "101010100"));
                    hotCities.add(new HotCity("上海", "上海", "101020100"));
                    hotCities.add(new HotCity("广州", "广东", "101280101"));
                    hotCities.add(new HotCity("深圳", "广东", "101280601"));
                    hotCities.add(new HotCity("杭州", "浙江", "101210101"));
                } else {
                    hotCities = null;
                }
                break;
            case R.id.cb_anim:
                anim = isChecked ? R.style.CustomAnim : R.style.DefaultCityPickerAnimation;
                break;
            case R.id.cb_enable_anim:
                enable = isChecked;
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY, theme);
    }
}
