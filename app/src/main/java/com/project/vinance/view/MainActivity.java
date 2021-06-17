package com.project.vinance.view;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.project.vinance.R;
import com.project.vinance.view.fragment.PositionFragment;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabs;
    private ViewPager2 pager;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
        }
    }

    private void init() {
        initComponent();
        initDesign();
    }

    /**
     * 레이아웃 컴포넌트와 연결
     */
    private void initComponent() {
        tabs = findViewById(R.id.main_tab);
        pager = findViewById(R.id.main_pager);
    }

    /**
     * 기본 디자인 설정
     */
    private void initDesign() {
        pager.setAdapter(new MainViewAdapter(this));

        new TabLayoutMediator(tabs, pager, (tab, pos) -> tab.setText(Arrays.asList("대기 주문 (0)", "포지션 (1)").get(pos))).attach();
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