package com.project.vinance.view

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.project.vinance.R
import com.project.vinance.database.TradeHistoryDatabase
import com.project.vinance.view.recycler.RecycleTradeList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FutureTradeListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_future_trade_list)

        init()
    }

    private fun init() {
        // 탭 고정
        val tab: TabLayout = findViewById(R.id.future_trade_list_tab)
        tab.getTabAt(2)?.select()
        
        // 리사이클러 뷰
        val recyclerView: RecyclerView = findViewById(R.id.trade_list_recycler_view)

        // 데이터베이스
        CoroutineScope(Dispatchers.Default).launch {
            TradeHistoryDatabase.getInstance(this@FutureTradeListActivity)?.let { database ->
                val list = database.dao.getAllList()

                recyclerView.adapter = RecycleTradeList(this@FutureTradeListActivity, list)
            }
        }

        // 뒤로가기 버튼
        findViewById<ImageView>(R.id.trade_list_back).setOnClickListener {
            finish()
        }
    }
}