package com.project.vinance.view;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.project.vinance.R;
import com.project.vinance.view.fragment.PositionFragment;
import com.project.vinance.view.socket.BinanceSocketClient;
import com.project.vinance.view.sub.MyCustomSeekBar;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
//    private TabLayout topTabs;
    private LinearLayout topTabs;
    private TabLayout innerTabs;
    private ViewPager2 innerPager;
    private BottomNavigationView bottomView;
    private MyCustomSeekBar seekBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        themeChange();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    /** 기기 테마 값에 따라 색상 변경 */
    private void themeChange() {
        // 다크 모드가 지원되는 경우
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
        }
    }

    private void init() {
        initComponent();
        initDesign();
        initFunction();
    }

    /** 레이아웃 컴포넌트와 연결 */
    private void initComponent() {
        topTabs = findViewById(R.id.main_top_tab);
        innerTabs = findViewById(R.id.main_inner_tab);
        innerPager = findViewById(R.id.main_pager);
        bottomView = findViewById(R.id.bottom_navigation);
        seekBar = findViewById(R.id.main_type_market_percentage2);
    }

    /** 기본 디자인 설정 */
    private void initDesign() {
        innerPager.setAdapter(new MainViewAdapter(this));
        /*pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabs.selectTab(tabs.getTabAt(position));
            }
        });*/
        new TabLayoutMediator(innerTabs, innerPager, (tab, position) -> {
            String tab1 = getString(R.string.tab1_prev) + "(0)";
            String tab2 = getString(R.string.tab2_prev) + "(1)";

            List<String> tabCount = Arrays.asList(tab1, tab2);
            tab.setText(tabCount.get(position));
        }).attach();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(this.getClass().getSimpleName(), "onProgressChanged: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /** 기본 기능 설정 */
    private void initFunction() {
        // 툴팁 비활성화
        View.OnLongClickListener disableLongClick = v -> true;

        findViewById(R.id.a).setOnLongClickListener(disableLongClick);
        findViewById(R.id.b).setOnLongClickListener(disableLongClick);
        findViewById(R.id.c).setOnLongClickListener(disableLongClick);
        findViewById(R.id.d).setOnLongClickListener(disableLongClick);
        findViewById(R.id.e).setOnLongClickListener(disableLongClick);

        innerTabs.getTabAt(0).view.setOnLongClickListener(disableLongClick);
        innerTabs.getTabAt(1).view.setOnLongClickListener(disableLongClick);

        /*topTabs.getTabAt(0).view.setOnLongClickListener(disableLongClick);
        topTabs.getTabAt(1).view.setOnLongClickListener(disableLongClick);
        topTabs.getTabAt(2).view.setOnLongClickListener(disableLongClick);
        topTabs.getTabAt(3).view.setOnLongClickListener(disableLongClick);*/

        bottomView.setSelectedItemId(R.id.d);

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
            fragment = Arrays.asList(new Fragment(), new PositionFragment());
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