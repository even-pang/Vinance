package com.project.vinance.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.project.vinance.R
import com.project.vinance.database.TradeHistoryDatabase
import com.project.vinance.database.TradeHistoryTable
import com.project.vinance.view.recycler.RecycleTradeHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InputActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        init()
    }

    private fun init() {
        // 리사이클러 뷰
        val recyclerView: RecyclerView = findViewById(R.id.input_recycler_view)
        val historyAdapter: RecycleTradeHistory =
            RecycleTradeHistory(this, emptyList())

        with(recyclerView) {
            adapter = historyAdapter
            addItemDecoration(DividerItemDecoration(this@InputActivity, DividerItemDecoration.VERTICAL))
        }

        // 초기 데이터베이스 불러오기
        CoroutineScope(Dispatchers.Default).launch {
            TradeHistoryDatabase.getInstance(this@InputActivity)?.let {
                val list = it.dao.getAllList()

                withContext(Dispatchers.Main) { historyAdapter.update(list) }
            }
        }

        // 저장하고 넘기기
        findViewById<Button>(R.id.input_next_with_save).setOnClickListener {
            CoroutineScope(Dispatchers.Default).launch {
                TradeHistoryDatabase.getInstance(this@InputActivity)?.let { database ->
                    if (historyAdapter.canNext()) {
                        // 저장할 리스트
                        val updateList = historyAdapter.historyList
                        updateList.forEach { if (it.id == null) database.dao.insertHistory(it) }

                        // 저장된 리스트로 변경
                        val updatedList = database.dao.getAllList()
                        withContext(Dispatchers.Main) { historyAdapter.update(updatedList) }

                        startActivity(Intent(this@InputActivity, FutureTradeListActivity::class.java))
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@InputActivity, "값 확인이 필요함", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        // 저장하지 않고 넘기기
        findViewById<Button>(R.id.input_next_without_save).setOnClickListener {
            startActivity(Intent(this, FutureTradeListActivity::class.java))
        }

        // 추가 버튼
        findViewById<Button>(R.id.input_add_button).setOnClickListener {
            historyAdapter.update(historyAdapter.historyList + TradeHistoryTable.EMPTY)
        }
    }
}