package com.imuges.order.activity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.imuges.order.R
import com.imuges.order.adapter.MainAdapter
import com.imuges.order.base.BaseFullTitleActivity
import com.imuges.order.base.BasePresenter
import com.imuges.order.base.ContentView
import com.imuges.order.base.IBaseView
import com.imuges.order.presenter.MainPresenter
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author BGQ
 * 主界面 订单列表界面
 */
@SuppressLint("NonConstantResourceId")
@ContentView(R.layout.activity_main)
class MainActivity : BaseFullTitleActivity(), IMainView, View.OnClickListener,
    OnItemChildClickListener {

    private val mMainAdapter by lazy { MainAdapter(defaultPresenter<MainPresenter>().getViewData()) }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }

    override fun getPresenterMap(): Map<String, BasePresenter<out IBaseView>> {
        return mapOf(Pair(DEFAULT, MainPresenter()))
    }

    override fun onCreate() {
        initTitleBar()
        initView()
        initListener()
    }

    override fun initTitleBar() {
        val color = getColor(R.color.mainTheme)
        setStateBarColor(color)
        titleBarCard.mBolHeight = ConvertUtils.dp2px(20f).toFloat()
    }

    private fun initView() {
        recyclerView.adapter = mMainAdapter
    }

    private fun initListener() {
        addOrder.setOnClickListener(this)
        searchOrder.setOnClickListener(this)
        searchCancel.setOnClickListener(this)
        mMainAdapter.setOnItemChildClickListener(this)
        searchEdit.addTextChangedListener(afterTextChanged = {
            defaultPresenter<MainPresenter>().searchOrder(it.toString())
        })
    }

    override fun onClick(v: View) {
        when (v) {
            addOrder -> {
                AddOrderActivity.startActivity(this)
            }
            searchOrder -> {
                startSearchAnim()
            }
            searchCancel -> {
                stopSearchAnim()
                defaultPresenter<MainPresenter>().searchOrder(":cancel")
            }
        }
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        OrderDetailActivity.startActivity(this)
    }

    override fun updateList() {
        mMainAdapter.notifyDataSetChanged()
    }

    /**
     * 打开搜索动画
     */
    private fun startSearchAnim() {
        val endLeftMargin = ConvertUtils.dp2px(16f)
        val startLeftMargin = resources.displayMetrics.widthPixels - endLeftMargin
        val sclp = searchCard.layoutParams as ConstraintLayout.LayoutParams
        sclp.leftMargin = startLeftMargin
        searchCard.layoutParams = sclp

        searchCard.isVisible = true

        val animator = ValueAnimator.ofInt(startLeftMargin, endLeftMargin)
        animator.addUpdateListener {
            val vl = it.animatedValue as Int
            sclp.leftMargin = vl
            searchCard.layoutParams = sclp
        }
        animator.duration = 300
        animator.start()
    }

    /**
     * 关闭搜索动画
     */
    private fun stopSearchAnim() {
        val endLeftMargin = ConvertUtils.dp2px(16f)
        val startLeftMargin = resources.displayMetrics.widthPixels - endLeftMargin
        val sclp = searchCard.layoutParams as ConstraintLayout.LayoutParams

        val animator = ValueAnimator.ofInt(endLeftMargin, startLeftMargin)
        animator.addUpdateListener {
            val vl = it.animatedValue as Int
            sclp.leftMargin = vl
            searchCard.layoutParams = sclp
        }
        animator.addListener(onEnd = {
            searchEdit.setText("")
            searchCard.isVisible = false
            if (KeyboardUtils.isSoftInputVisible(this)) {
                KeyboardUtils.hideSoftInput(this)
            }
        })
        animator.duration = 300
        animator.start()
    }

}