package com.stxnext.stxinsider.fragment

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.stxnext.stxinsider.R
import com.stxnext.stxinsider.adapter.SliderAdapter
import com.stxnext.stxinsider.constant.Teams
import com.stxnext.stxinsider.model.SliderItem
import com.stxnext.stxinsider.model.TeamCategoryHeader
import com.stxnext.stxinsider.util.Util
import com.stxnext.stxinsider.util.convertDpToPixel
import com.stxnext.stxinsider.util.setTransitionAnimationsForLayout
import com.stxnext.stxinsider.view.TopFirstSpaceMarginDecorator
import com.stxnext.stxinsider.view.MarginDecoration
import com.stxnext.stxinsider.view.elementItemView.BaseItemView
import com.stxnext.stxinsider.view.elementItemView.ShortItemView
import com.stxnext.stxinsider.view.elementItemView.TallItemView

import java.io.IOException
import java.io.InputStream

/**
 * Created by bkosarzycki on 22.01.16.
 */
class TeamCategoryFragment(var teamCategoryHeader: TeamCategoryHeader) : Fragment() {

    internal val TAG = TeamCategoryFragment::class.java.name
    lateinit var teamListRecyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_team_outer_layout, container, false)

        teamListRecyclerView = view.findViewById(R.id.fragment_team_header_team_list) as RecyclerView

        val adapter = SliderAdapter(context, { viewType: Int -> ShortItemView(context, null, viewType) })
        adapter.addItem(SliderItem().header(getString(R.string.stx_next_developer_teams)).imageResource(R.drawable.teams).description(teamCategoryHeader.additionalDescr?:""))
        for (team in Teams.teams)
        //            if (team.category != null && teamCategoryHeader.category != null)
        //                if (team.category == teamCategoryHeader.category)
            adapter.addItem(team)

        if (adapter.itemCount > 1)
            initializeRecyclerView(LinearLayoutManager(context), adapter)

        return view
    }

    fun <T : BaseItemView> initializeRecyclerView(linearLayoutManager: LinearLayoutManager, adapter: SliderAdapter<T>) {
        teamListRecyclerView.addItemDecoration(TopFirstSpaceMarginDecorator(Util().convertDpToPixel(8.0f, activity).toInt(),
                Util().convertDpToPixel(5.0f, activity).toInt()))
        teamListRecyclerView.setHasFixedSize(true)
        teamListRecyclerView.layoutManager = linearLayoutManager
        teamListRecyclerView.adapter = adapter
    }
}
