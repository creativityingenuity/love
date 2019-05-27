package com.yc.love.ui.frament.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yc.love.R;
import com.yc.love.adaper.rv.LoveHealDetailsAdapter;
import com.yc.love.adaper.rv.base.RecyclerViewItemListener;
import com.yc.love.adaper.rv.holder.BaseViewHolder;
import com.yc.love.adaper.rv.holder.EmptyViewHolder;
import com.yc.love.adaper.rv.holder.LoveHealDetItemHolder;
import com.yc.love.adaper.rv.holder.LoveHealDetVipHolder;
import com.yc.love.adaper.rv.holder.ProgressBarViewHolder;
import com.yc.love.model.base.MySubscriber;
import com.yc.love.model.bean.AResultInfo;
import com.yc.love.model.bean.LoveHealDetBean;
import com.yc.love.model.bean.LoveHealDetDetailsBean;
import com.yc.love.model.engin.LoveEngin;
import com.yc.love.model.single.YcSingle;
import com.yc.love.ui.activity.BecomeVipActivity;
import com.yc.love.ui.activity.CollectActivity;
import com.yc.love.ui.activity.ShareActivity;
import com.yc.love.ui.view.LoadDialog;

import java.util.List;

/**
 * Created by mayn on 2019/5/5.
 */

public class BaseShareFragment extends BaseLazyFragment {

    public ShareActivity mShareActivity;
    private RecyclerView mRecyclerView;
    private LoveEngin mLoveEngin;
    private int PAGE_SIZE = 10;
    private int PAGE_NUM = 1;
    private LoadDialog mLoadingDialog;
    private boolean loadMoreEnd;
    private boolean loadDataEnd;
    private boolean showProgressBar = false;
    private LoveHealDetailsAdapter mAdapter;
    private List<LoveHealDetBean> mLoveHealDetBeans;
    private String keyword;

    @Override
    protected View onFragmentCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mShareActivity = (ShareActivity) getActivity();
        return super.onFragmentCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_base_share;
    }

    @Override
    protected void initViews() {
        mLoveEngin = new LoveEngin(mShareActivity);
        mLoadingDialog = mShareActivity.mLoadingDialog;
        initRecyclerView();
    }

    @Override
    protected void lazyLoad() {

    }

    private void initRecyclerView() {
        mRecyclerView = rootView.findViewById(R.id.base_share_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mShareActivity);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    /**
     * @param searchType 1模糊  2精准
     * @param keyword
     */
    public void netData(String searchType, String keyword) {
        this.keyword = keyword;
        int id = YcSingle.getInstance().id;
        if (id <= 0) {
            mShareActivity.showToLoginDialog();
            return;
        }
        mLoadingDialog.showLoadingDialog();
        mLoveEngin.searchDialogue(String.valueOf(id), searchType, keyword, String.valueOf(PAGE_NUM), String.valueOf(PAGE_SIZE), "Dialogue/search").subscribe(new MySubscriber<AResultInfo<List<LoveHealDetBean>>>(mLoadingDialog) {


            @Override
            protected void onNetNext(AResultInfo<List<LoveHealDetBean>> listAResultInfo) {
                mLoveHealDetBeans = listAResultInfo.data;
                initRecyclerData();
            }

            @Override
            protected void onNetError(Throwable e) {

            }

            @Override
            protected void onNetCompleted() {

            }
        });
    }

    private void initRecyclerData() {
        mLoveHealDetBeans=addTitle(mLoveHealDetBeans);

        mAdapter = new LoveHealDetailsAdapter(mLoveHealDetBeans, mRecyclerView) {
            @Override
            public BaseViewHolder getHolder(ViewGroup parent) {
                LoveHealDetItemHolder loveHealDetItemHolder = new LoveHealDetItemHolder(mShareActivity, null, parent);
                loveHealDetItemHolder.setOnClickCopyListent(new LoveHealDetItemHolder.OnClickCopyListent() {
                    @Override
                    public void onClickCopy(LoveHealDetDetailsBean detailsBean) {
                        Log.d("mylog", "onClickCopy: detailsBean " + detailsBean.toString());
                        mShareActivity.showToastShort(detailsBean.content);
                    }
                });
                return loveHealDetItemHolder;
            }

            @Override
            protected RecyclerView.ViewHolder getPayVipHolder(ViewGroup parent) {
                return new LoveHealDetVipHolder(mShareActivity, recyclerViewItemListener, parent);
            }

            @Override
            protected RecyclerView.ViewHolder getEmptyHolder(ViewGroup parent) {
                return new EmptyViewHolder(mShareActivity, parent, "此关键字无数据，请更换");
            }

            @Override
            protected RecyclerView.ViewHolder getBarViewHolder(ViewGroup parent) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_test_item_footer, parent, false);
                ProgressBarViewHolder progressBarViewHolder = new ProgressBarViewHolder(view);
                return progressBarViewHolder;
            }
        };
        mRecyclerView.setAdapter(mAdapter);
        if (mLoveHealDetBeans.size() < PAGE_SIZE) {
            Log.d("ssss", "loadMoreData: data---end");
        } else {
            mAdapter.setOnMoreDataLoadListener(new LoveHealDetailsAdapter.OnLoadMoreDataListener() {
                @Override
                public void loadMoreData() {
                    if (loadDataEnd == false) {
                        return;
                    }
                    if (showProgressBar == false) {
                        //加入null值此时adapter会判断item的type
                        mLoveHealDetBeans.add(null);
                        mAdapter.notifyDataSetChanged();
                        showProgressBar = true;
                    }
                    if (!loadMoreEnd) {
                        netLoadMore();
                    } else {
                        Log.d("mylog", "loadMoreData: loadMoreEnd end 已加载全部数据 ");
                        mLoveHealDetBeans.remove(mLoveHealDetBeans.size() - 1);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
        loadDataEnd = true;
    }

    private List<LoveHealDetBean> addTitle(List<LoveHealDetBean> loveHealDetBeans) {
        for (LoveHealDetBean loveHealDetBean : loveHealDetBeans
                ) {
            List<LoveHealDetDetailsBean> details = loveHealDetBean.details;
            if (details == null || details.size() == 0) {
                details = loveHealDetBean.detail;
            }
            details.add(0, new LoveHealDetDetailsBean(0, loveHealDetBean.id, loveHealDetBean.chat_name, loveHealDetBean.ans_sex));
        }
        return loveHealDetBeans;
    }

    private void netLoadMore() {
        int id = YcSingle.getInstance().id;
        if (id <= 0) {
            mShareActivity.showToLoginDialog();
            return;
        }
        mLoveEngin.searchDialogue(String.valueOf(id), "1", keyword, String.valueOf(++PAGE_NUM), String.valueOf(PAGE_SIZE), "Dialogue/search").subscribe(new MySubscriber<AResultInfo<List<LoveHealDetBean>>>(mShareActivity) {

            @Override
            protected void onNetNext(AResultInfo<List<LoveHealDetBean>> listAResultInfo) {
                List<LoveHealDetBean> netLoadMoreData = listAResultInfo.data;
                showProgressBar = false;
                mLoveHealDetBeans.remove(mLoveHealDetBeans.size() - 1);
                mAdapter.notifyDataSetChanged();
                if (netLoadMoreData.size() < PAGE_SIZE) {
                    loadMoreEnd = true;
                }
//                netLoadMoreData=addTitle(netLoadMoreData);
                mLoveHealDetBeans.addAll(addTitle(netLoadMoreData));
                mAdapter.notifyDataSetChanged();
                mAdapter.setLoaded();
            }

            @Override
            protected void onNetError(Throwable e) {

            }

            @Override
            protected void onNetCompleted() {

            }
        });
    }

    RecyclerViewItemListener recyclerViewItemListener = new RecyclerViewItemListener() {
        @Override
        public void onItemClick(int position) {
            //TODO 购买VIP刷新数据
            startActivity(new Intent(mShareActivity, BecomeVipActivity.class));
        }

        @Override
        public void onItemLongClick(int position) {

        }
    };

    public void recoverData() {
        if (PAGE_NUM != 1) {
            PAGE_NUM = 1;
        }
    }

}
