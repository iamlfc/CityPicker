package com.zaaach.citypicker;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zaaach.citypicker.adapter.CityListAdapter;
import com.zaaach.citypicker.adapter.InnerListener;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.adapter.decoration.DividerItemDecoration;
import com.zaaach.citypicker.adapter.decoration.SectionItemDecoration;
import com.zaaach.citypicker.db.DBManager;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;
import com.zaaach.citypicker.model.LocateState;
import com.zaaach.citypicker.model.LocatedCity;
import com.zaaach.citypicker.util.ScreenUtil;
import com.zaaach.citypicker.view.SideIndexBar;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @Author: Bro0cL
 * @Date: 2018/2/6 20:50
 */
public class CityPickerDialogFragment extends DialogFragment implements TextWatcher,
        View.OnClickListener, SideIndexBar.OnIndexTouchedChangedListener, InnerListener {
    private View mContentView;
    private RecyclerView mRecyclerView;
    private View mEmptyView;
    private TextView mOverlayTextView;
    private SideIndexBar mIndexBar;
    private EditText mSearchBox;
    private LinearLayout lay_location;
    private TextView mTv_search;
    private TextView mTv_title;
    //    private TextView mTvLocation;
    private ImageView mClearAllBtn;
    private RelativeLayout rl_back;

    private LinearLayoutManager mLayoutManager;
    private CityListAdapter mAdapter;

    private List<City> mAllCities = new ArrayList<City>();
    private List<HotCity> mHotCities = new ArrayList<HotCity>();
    private List<City> mResults = new ArrayList<City>();

    private DBManager dbManager;

    private int height;
    private int width;
    /**
     * 是否自定义数据
     */
    private boolean isCustomeData = false;
    private boolean enableAnim = false;
    private int mAnimStyle = R.style.DefaultCityPickerAnimation;
    private LocatedCity mLocatedCity;
    private int locateState;
    private OnPickListener mOnPickListener;
    private String strHint = ""; //搜索框提示语
    private String strTitle = ""; // 顶部 标题
    private boolean isShowTvSearch = true;// 是否显示搜索按钮
    private boolean isShowLocation = true;//是否显示定位城市
    private boolean isSearchWithProvice = false;//是否 连带 搜索

    /**
     * 获取实例
     *
     * @param enable 是否启用动画效果
     * @return
     */
    public static CityPickerDialogFragment newInstance(boolean enable) {
        final CityPickerDialogFragment fragment = new CityPickerDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean("cp_enable_anim", enable);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.CityPickerStyle);
    }

    public void setLocatedCity(LocatedCity location) {
        mLocatedCity = location;
    }

    public void setHotCities(List<HotCity> data) {
        if (data != null && !data.isEmpty()) {
            this.mHotCities = data;
        }
    }

    public void setAllCities(List<City> data) {
        if (data != null && !data.isEmpty()) {
            this.mAllCities = data;
        }
    }

    public void setISCustomeData(boolean isOutData) {
        this.isCustomeData = isOutData;

    }
    public void setTopTitle(String strTitle) {
        this.strTitle = strTitle;
//        if (mSearchBox != null)
//            mSearchBox.setHint(strHint);

    }

    public void setEtInputHint(String strHint) {
        this.strHint = strHint;
//        if (mSearchBox != null)
//            mSearchBox.setHint(strHint);

    }



    public void setIsHideSearch(boolean isShow) {
        isShowTvSearch = isShow;
//        if (mTv_search != null)
//             mTv_search.setVisibility(isShow ? View.VISIBLE : View.GONE);

    }
    public void setIsWithSearch(boolean isShow) {
        isSearchWithProvice = isShow;
//        if (mTv_search != null)
//             mTv_search.setVisibility(isShow ? View.VISIBLE : View.GONE);

    }

    public void setShowLocation (boolean isShowLocation) {
        this.isShowLocation = isShowLocation;
    }

    @SuppressLint("ResourceType")
    public void setAnimationStyle(@StyleRes int resId) {
        this.mAnimStyle = resId <= 0 ? mAnimStyle : resId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.cp_dialog_city_picker, container, false);
        return mContentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initViews();
        locate();
    }

    private void initViews() {

        mRecyclerView = mContentView.findViewById(R.id.cp_city_recyclerview);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SectionItemDecoration(getActivity(), mAllCities), 0);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()), 1);
        mAdapter = new CityListAdapter(getActivity(), mAllCities, mHotCities, locateState);
        mAdapter.autoLocate(true);
        mAdapter.setInnerListener(this);
        mAdapter.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //确保定位城市能正常刷新
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mAdapter.refreshLocationItem();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }
        });

        mEmptyView = mContentView.findViewById(R.id.cp_empty_view);
        mOverlayTextView = mContentView.findViewById(R.id.cp_overlay);
//        mTvLocation = mContentView.findViewById(R.id.tv_location);
        lay_location = mContentView.findViewById(R.id.lay_location);
        lay_location.setOnClickListener(this);

        mTv_title = mContentView.findViewById(R.id.tv_title);
        mTv_title.setText(strTitle);

        mTv_search = mContentView.findViewById(R.id.tv_search);
        mTv_search.setOnClickListener(this);

        rl_back = mContentView.findViewById(R.id.rl_back);
        rl_back.setOnClickListener(this);
        mIndexBar = mContentView.findViewById(R.id.cp_side_index_bar);
        mIndexBar.setSearchWithCode(isSearchWithProvice);
        mIndexBar.setNavigationBarHeight(ScreenUtil.getNavigationBarHeight(getActivity()));
        mIndexBar.setOverlayTextView(mOverlayTextView)
                .setOnIndexChangedListener(this);

        mSearchBox = mContentView.findViewById(R.id.cp_search_box);
        mSearchBox.addTextChangedListener(this);
        mSearchBox.setHint(strHint);
        mSearchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 执行搜索操作
                    mTv_search.performClick();
                    return true; // 表示事件已处理
                }
                return false; // 如果不是搜索按钮，返回false让系统继续处理其他操作
            }
        });

        mTv_search.setVisibility(isShowTvSearch ? View.VISIBLE : View.GONE);
//        mCancelBtn = mContentView.findViewById(R.id.cp_cancel);
        mClearAllBtn = mContentView.findViewById(R.id.cp_clear_all);
//        mCancelBtn.setOnClickListener(this);
        mClearAllBtn.setOnClickListener(this);
    }

    private void initData() {
        Bundle args = getArguments();
        if (args != null) {
            enableAnim = args.getBoolean("cp_enable_anim");
        }
        //初始化热门城市
        if (mHotCities == null || mHotCities.isEmpty()) {
          /*  mHotCities = new ArrayList<>();
            mHotCities.add(new HotCity("北京", "北京", "101010100"));
            mHotCities.add(new HotCity("上海", "上海", "101020100"));
            mHotCities.add(new HotCity("广州", "广东", "101280101"));
            mHotCities.add(new HotCity("深圳", "广东", "101280601"));
            mHotCities.add(new HotCity("天津", "天津", "101030100"));
            mHotCities.add(new HotCity("杭州", "浙江", "101210101"));
            mHotCities.add(new HotCity("南京", "江苏", "101190101"));
            mHotCities.add(new HotCity("成都", "四川", "101270101"));
            mHotCities.add(new HotCity("武汉", "湖北", "101200101"));*/
        }
        //初始化定位城市，默认为空时会自动回调定位
        if (mLocatedCity == null) {
            mLocatedCity = new LocatedCity(getString(R.string.cp_locating), "未知", "0");
            locateState = LocateState.LOCATING;
        } else {
            locateState = LocateState.SUCCESS;
        }
        if (!isCustomeData) {
            mAllCities.clear();
            dbManager = new DBManager(getActivity());
            mAllCities = dbManager.getAllCities();
        }

        if (isShowLocation&&!mAllCities.get(0).getPinyin().equals("定位城市"))
            mAllCities.add(0, mLocatedCity);
        if (mHotCities.size() > 0) {
            if (!mAllCities.get(1).getPinyin().equals("热门城市"))
                mAllCities.add(1, new HotCity("热门城市", "未知", "0"));
        } else {
            if (mAllCities.get(1).getPinyin().equals("热门城市")) {
                mAllCities.remove(1);
            }
        }
        mResults.addAll(mAllCities);
//        mResults = mAllCities;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (mOnPickListener != null) {
                        mOnPickListener.onCancel();
                    }
                }
                return false;
            }
        });

        measure();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(width, height - ScreenUtil.getStatusBarHeight(getActivity()));
            if (enableAnim) {
                window.setWindowAnimations(mAnimStyle);
            }
        }
    }

    //测量宽高
    private void measure() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getRealMetrics(dm);
            height = dm.heightPixels;
            width = dm.widthPixels;
        } else {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            height = dm.heightPixels;
            width = dm.widthPixels;
        }
    }

    /**
     * 搜索框监听
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        String keyword = s.toString();
        if (TextUtils.isEmpty(keyword)) {
            mClearAllBtn.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.GONE);
        } else {
            mClearAllBtn.setVisibility(View.VISIBLE);

        }
/*
        if (TextUtils.isEmpty(keyword)) {
            mClearAllBtn.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.GONE);
            mResults = mAllCities;
            ((SectionItemDecoration) (mRecyclerView.getItemDecorationAt(0))).setData(mResults);
            mAdapter.updateData(mResults);
        } else {
            mClearAllBtn.setVisibility(View.VISIBLE);
            //开始数据库查找
            mResults = dbManager.searchCity(keyword);
            ((SectionItemDecoration) (mRecyclerView.getItemDecorationAt(0))).setData(mResults);
            if (mResults == null || mResults.isEmpty()) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
                mAdapter.updateData(mResults);
            }
        }
        mRecyclerView.scrollToPosition(0);
*/
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_back) {
            dismiss();
            if (mOnPickListener != null) {
                mOnPickListener.onCancel();
            }
        } else if (id == R.id.cp_clear_all) {
            mSearchBox.setText("");
        } else if (id == R.id.tv_search) {
            String keyword = mSearchBox.getText().toString().toLowerCase();
            if (TextUtils.isEmpty(keyword)) {
                mClearAllBtn.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.GONE);
                mResults = mAllCities;
                ((SectionItemDecoration) (mRecyclerView.getItemDecorationAt(0))).setData(mResults);
                mAdapter.updateData(mResults);
            } else {
                mClearAllBtn.setVisibility(View.VISIBLE);
                if (!isCustomeData) {
//开始数据库查找
                    mResults = dbManager.searchCity(keyword);
                } else {
//                    内存数据筛选
                    mResults.clear();
                    for (City mAllCity : mAllCities) {
                        if (isSearchWithProvice){
                            if (mAllCity.getName().contains(keyword) || keyword.contains(mAllCity.getName())||
                                    (mAllCity.getProvince()!=null&&!mAllCity.getProvince().isEmpty()&&(mAllCity.getProvince().contains(keyword) || keyword.contains(mAllCity.getProvince()))))
                                mResults.add(mAllCity);
                        }else {
                            if (mAllCity.getName().contains(keyword) || keyword.contains(mAllCity.getName()))
                                mResults.add(mAllCity);
                        }

                    }
                }

                ((SectionItemDecoration) (mRecyclerView.getItemDecorationAt(0))).setData(mResults);
                if (mResults == null || mResults.isEmpty()) {
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                    mAdapter.updateData(mResults);
                }
            }
            mRecyclerView.scrollToPosition(0);
        } else if (id == R.id.lay_location) {
            if (lCity == null) {
                Toast.makeText(getContext(), "暂无定位信息！", Toast.LENGTH_LONG);
                return;
            }
            dismiss();
            if (mOnPickListener != null) {
                mOnPickListener.onPick(-1, lCity);
            }
        }
    }


    @Override
    public void onIndexChanged(String index, int position) {
        //滚动RecyclerView到索引位置
        mAdapter.scrollToSection(index);
    }

    private LocatedCity lCity;

    public void locationChanged(LocatedCity location, int state) {
        lCity = location;
//        mTvLocation.setText(location.getName());
//        mTvLocation.setTag(location.getCode());
        mAdapter.updateLocateState(location, state);
    }

    @Override
    public void dismiss(int position, City data) {
        dismiss();
        if (mOnPickListener != null) {
            mOnPickListener.onPick(position, data);
        }
    }

    @Override
    public void locate() {
        if (mOnPickListener != null) {
            mOnPickListener.onLocate();
        }
    }

    public void setOnPickListener(OnPickListener listener) {
        this.mOnPickListener = listener;
    }
}
