package com.project.vinance.view;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.project.vinance.R;
import com.project.vinance.view.fragment.WaitOrderFragment;
import com.project.vinance.view.fragment.PositionFragment;
import com.project.vinance.view.socket.BinanceSocketClient;
import com.project.vinance.view.sub.StickScrollView;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LinearLayout topTabs;
    private TabLayout innerTabs;
    private ViewPager2 innerPager;
    private StickScrollView scrollView;
    private ConstraintLayout status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        themeChange();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    /**
     * 기기 테마 값에 따라 색상 변경
     */
    private void themeChange() {
        // 다크 모드가 지원되는 경우
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
        }*/
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    private void init() {
        initComponent();
        initDesign();
        initFunction();
    }

    /**
     * 레이아웃 컴포넌트와 연결
     */
    private void initComponent() {
        topTabs = findViewById(R.id.main_top_tab);
        innerTabs = findViewById(R.id.main_inner_tab);
        innerPager = findViewById(R.id.main_pager);
        scrollView = findViewById(R.id.main_scroll_view);
        status = findViewById(R.id.main_status);
    }

    /**
     * 기본 디자인 설정
     */
    private void initDesign() {
        innerPager.setAdapter(new MainViewAdapter(this));
        ((RecyclerView) innerPager.getChildAt(0)).setOverScrollMode(View.OVER_SCROLL_NEVER);
        new TabLayoutMediator(innerTabs, innerPager, (tab, position) -> {
            String tab1 = getString(R.string.tab1_prev) + "(0)";
            String tab2 = getString(R.string.tab2_prev) + "(1)";

            List<String> tabCount = Arrays.asList(tab1, tab2);
            tab.setText(tabCount.get(position));
        }).attach();
    }

    /**
     * 뷰 페이저의 최대 높이 설정
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);


        ViewGroup.LayoutParams params = innerPager.getLayoutParams();
        params.height = scrollView.getHeight() - status.getHeight() - innerTabs.getHeight();
        innerPager.setLayoutParams(params);
    }

    /**
     * 기본 기능 설정
     */
    private void initFunction() {
        // 툴팁 비활성화
        View.OnLongClickListener disableLongClick = v -> true;

        innerTabs.getTabAt(0).view.setOnLongClickListener(disableLongClick);
        innerTabs.getTabAt(1).view.setOnLongClickListener(disableLongClick);

        // 헤더 설정
        scrollView.setHeader(status);
        // 헤더가 떨어졌을 때
        scrollView.setFreeListener(view -> {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.parseColor("#171E26"));
            view.setBackgroundColor(Color.parseColor("#171E26"));
            return null;
        });
        // 헤더가 붙었을 때
        scrollView.setStickListener(view -> {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.parseColor("#1F2630"));
            view.setBackgroundColor(Color.parseColor("#1F2630"));
            return null;
        });

        // 웹소켓 연결
        try {
            BinanceSocketClient client = new BinanceSocketClient(new URI("wss://stream.binance.com:9443"));
            client.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static class MainViewAdapter extends FragmentStateAdapter {
        private final List<Fragment> fragment;

        public MainViewAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
            fragment = Arrays.asList(new WaitOrderFragment(), new PositionFragment());
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragment.get(position);
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}