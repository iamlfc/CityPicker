package com.zaaach.citypicker;

import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;
import com.zaaach.citypicker.model.LocateState;
import com.zaaach.citypicker.model.LocatedCity;

import java.lang.ref.WeakReference;
import java.util.List;

import androidx.annotation.StyleRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * @Author: Bro0cL
 * @Date: 2018/2/6 17:52
 */
public class CityPicker {
    private static final String TAG = "CityPicker";

    private WeakReference<FragmentActivity> mContext;
    private WeakReference<Fragment> mFragment;
    private WeakReference<FragmentManager> mFragmentManager;

    private boolean enableAnim;
    private boolean isOutData;//是否是外部数据
    private boolean isShowTVSearch;//是否 显示搜索按钮
    private boolean isShowLocation;//是否 显示搜索按钮
    private boolean isSearchWithCode;//是否 连带搜索

    private int mAnimStyle;
    private LocatedCity mLocation;
    private List<HotCity> mHotCities;
    private List<City> mCustomCities;
    private OnPickListener mOnPickListener;
    private String strHint = "";
    private String strTitle = "";

    private CityPicker() {
    }

    private CityPicker(Fragment fragment) {
        this(fragment.getActivity(), fragment);
        mFragmentManager = new WeakReference<>(fragment.getChildFragmentManager());
    }

    private CityPicker(FragmentActivity activity) {
        this(activity, null);
        mFragmentManager = new WeakReference<>(activity.getSupportFragmentManager());
    }

    private CityPicker(FragmentActivity activity, Fragment fragment) {
        mContext = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
    }

    public static CityPicker from(Fragment fragment) {
        return new CityPicker(fragment);
    }

    public static CityPicker from(FragmentActivity activity) {
        return new CityPicker(activity);
    }

    /**
     * 设置动画效果
     *
     * @param animStyle
     * @return
     */
    public CityPicker setAnimationStyle(@StyleRes int animStyle) {
        this.mAnimStyle = animStyle;
        return this;
    }

    /**
     * 设置当前已经定位的城市
     *
     * @param location
     * @return
     */
    public CityPicker setLocatedCity(LocatedCity location) {
        this.mLocation = location;
        return this;
    }

    public CityPicker setHotCities(List<HotCity> data) {
        this.mHotCities = data;
        return this;
    }

    public CityPicker setAllCities(List<City> data) {
        this.mCustomCities = data;
        return this;
    }

    public CityPicker setIsOutData(boolean data) {
        this.isOutData = data;
        return this;
    }

    public CityPicker setIsTvSearch(boolean data) {
        this.isShowTVSearch = data;
        return this;
    }

    public CityPicker setShowLocation(boolean data) {
        this.isShowLocation = data;
        return this;
    }

    public CityPicker setHintValue(String data) {
        this.strHint = data;
        return this;
    }

    public CityPicker setTopTitle(String data) {
        this.strTitle = data;
        return this;
    }

    public CityPicker setIsWithSearchCode(boolean data) {
        this.isSearchWithCode = data;
        return this;
    }

    /**
     * 启用动画效果，默认为false
     *
     * @param enable
     * @return
     */
    public CityPicker enableAnimation(boolean enable) {
        this.enableAnim = enable;
        return this;
    }

    /**
     * 设置选择结果的监听器
     *
     * @param listener
     * @return
     */
    public CityPicker setOnPickListener(OnPickListener listener) {
        this.mOnPickListener = listener;
        return this;
    }

    public void show() {
        FragmentTransaction ft = mFragmentManager.get().beginTransaction();
        final Fragment prev = mFragmentManager.get().findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev).commit();
            ft = mFragmentManager.get().beginTransaction();
        }
        ft.addToBackStack(null);
        final CityPickerDialogFragment cityPickerFragment =
                CityPickerDialogFragment.newInstance(enableAnim);
        cityPickerFragment.setLocatedCity(mLocation);
        cityPickerFragment.setHotCities(mHotCities);
        cityPickerFragment.setISCustomeData(isOutData);
        cityPickerFragment.setEtInputHint(strHint);
        cityPickerFragment.setTopTitle(strTitle);
        cityPickerFragment.setIsHideSearch(isShowTVSearch);
        cityPickerFragment.setShowLocation(isShowLocation);
        cityPickerFragment.setIsWithSearch(isSearchWithCode);
        cityPickerFragment.setAllCities(mCustomCities);
        cityPickerFragment.setAnimationStyle(mAnimStyle);
        cityPickerFragment.setOnPickListener(mOnPickListener);
        cityPickerFragment.show(ft, TAG);
    }

    /**
     * 定位完成
     *
     * @param location
     * @param state
     */
    public void locateComplete(LocatedCity location, @LocateState.State int state) {
        CityPickerDialogFragment fragment = (CityPickerDialogFragment) mFragmentManager.get().findFragmentByTag(TAG);
        if (fragment != null) {
            fragment.locationChanged(location, state);
        }
    }
}
