package com.project.vinance.view.fragment.future

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.project.vinance.R
import com.project.vinance.network.socket.SimpleSocketClient
import com.project.vinance.view.recycler.RecycleFuturePosition

class FuturePositionFragment : Fragment() {
    private var pager: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_future_position, container, false)
        pager = view.findViewById(R.id.fragment_position_view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val positionAdapter = RecycleFuturePosition(requireContext(), pager)
        SimpleSocketClient.futureRecycle = pager?.layoutManager
        pager?.adapter = positionAdapter
    }
}