package com.puyue.www.qiaoge.fragment.mine.order;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.puyue.www.qiaoge.R;

import com.puyue.www.qiaoge.activity.mine.order.OrderEvaluateActivity;
import com.puyue.www.qiaoge.adapter.mine.MyOrdersItemAdapter;
import com.puyue.www.qiaoge.api.mine.order.CopyToCartAPI;
import com.puyue.www.qiaoge.api.mine.order.MyOrderListAPI;
import com.puyue.www.qiaoge.base.BaseFragment;
import com.puyue.www.qiaoge.constant.AppConstant;
import com.puyue.www.qiaoge.helper.AppHelper;
import com.puyue.www.qiaoge.helper.StringHelper;
import com.puyue.www.qiaoge.helper.UserInfoHelper;
import com.puyue.www.qiaoge.model.OrdersModel;
import com.puyue.www.qiaoge.model.mine.order.CommonModel;
import com.puyue.www.qiaoge.model.mine.order.CopyToCartModel;
import com.puyue.www.qiaoge.model.mine.order.MyOrdersModel;
import com.puyue.www.qiaoge.model.mine.order.OrderEvaluateListModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/4/21.
 */
//待评价订单
public class EvaluatedOrderFragment extends BaseFragment {
    private PtrClassicFrameLayout mPtr;
    private RecyclerView mRv;
    private MyOrdersItemAdapter mAdapterMyOrders;
    private String mType;
    private int pageNum = 1;
    private ImageView mIvNoData;
    private OrdersModel mModelMyOrders;
    private List<OrdersModel.DataBean.ListBean> mListResult = new ArrayList<>();
    private CopyToCartModel mModelCopyToCart;
    private List<OrderEvaluateListModel> mListEvaluate = new ArrayList<>();

    private int orderDeliveryType;
    @Override
    public int setLayoutId() {
        return R.layout.fragment_my_orders;
    }

    @Override
    public void initViews(View view) {

    }

    @Override
    public void findViewById(View view) {
        mPtr = ((PtrClassicFrameLayout) view.findViewById(R.id.ptr_my_orders));
        mRv = ((RecyclerView) view.findViewById(R.id.rv_my_orders));
        mIvNoData = ((ImageView) view.findViewById(R.id.iv_my_orders_no_data));
    }

    @Override
    public void setViewData() {
        mListResult.clear();
        mListEvaluate.clear();
//        requestOrdersList(5);
        if (UserInfoHelper.getDeliverType(mActivity)!=null&&StringHelper.notEmptyAndNull(UserInfoHelper.getDeliverType(mActivity))){
            orderDeliveryType=Integer.parseInt(UserInfoHelper.getDeliverType(mActivity));
        }
        mPtr.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                pageNum = 1;
                requestOrdersList(5);
            }
        });
        if (orderDeliveryType==0){
            mAdapterMyOrders = new MyOrdersItemAdapter(R.layout.item_my_order, mListResult, 5,orderDeliveryType, new MyOrdersItemAdapter.OnClick() {


                @Override
                public void evaluateNowOnclick(int position,String orderId) { // 立即评价
                    getComment(orderId);
                }

                @Override
                public void againBayOnclick(int position) { // 在次购买
                    OrdersModel.DataBean.ListBean listBean = mListResult.get(position);
                    requestCopyToCart(listBean.orderId);
                }

                @Override
                public void cancelOnclick(String orderId) {

                }

                @Override
                public void deleteOnclick(String orderId) {

                }

                @Override
                public void imageGo(String orderId, String payAmount) {

                }

                @Override
                public void requestConfirmGetGoods(String orderId) {

                }

                @Override
                public void confirmSelfOrder(String orderId) {

                }

                @Override
                public void confirmSelfReturnOrder(String orderId, int pos) {

                }


            });
        }else if (orderDeliveryType==1){
            mAdapterMyOrders = new MyOrdersItemAdapter(R.layout.item_my_order_self, mListResult, 5,orderDeliveryType, new MyOrdersItemAdapter.OnClick() {


                @Override
                public void evaluateNowOnclick(int position,String orderId) { // 立即评价
                    getComment(orderId);
                }

                @Override
                public void againBayOnclick(int position) { // 在次购买
                    OrdersModel.DataBean.ListBean listBean = mListResult.get(position);
                    requestCopyToCart(listBean.orderId);
                }

                @Override
                public void cancelOnclick(String orderId) {

                }

                @Override
                public void deleteOnclick(String orderId) {

                }

                @Override
                public void imageGo(String orderId, String payAmount) {

                }

                @Override
                public void requestConfirmGetGoods(String orderId) {

                }

                @Override
                public void confirmSelfOrder(String orderId) {

                }

                @Override
                public void confirmSelfReturnOrder(String orderId, int pos) {

                }


            });
        }



        mAdapterMyOrders.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });
        mRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.canScrollVertically(-1)) {
                    mPtr.setEnabled(false);
                } else {
                    mPtr.setEnabled(true);
                }
            }
        });
        mRv.setAdapter(mAdapterMyOrders);
        mAdapterMyOrders.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                pageNum++;
                requestOrdersList(5);
            }
        });
//        requestOrdersList(5);
    }

    /**
     * 点击去评价
     * @param orderId
     */
    private void getComment(String orderId) {
        MyOrderListAPI.getComment(getContext(),orderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommonModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(CommonModel commonModel) {
                        if(commonModel.isSuccess()) {
                            if(commonModel.getData()!=null) {
                                requestEvaluate(commonModel.getData(),orderId);
                            }
                        }
                    }
                });
    }

    private void requestOrdersList(int orderStatus) {
        MyOrderListAPI.requestOrderList(getContext(), orderStatus, pageNum, 10,orderDeliveryType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<OrdersModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(OrdersModel myOrdersModel) {
                        logoutAndToHome(getContext(), myOrdersModel.code);
                        mPtr.refreshComplete();
                        mModelMyOrders = myOrdersModel;
                        if (mModelMyOrders.success) {
                            updateOrderList();
                        } else {
                            AppHelper.showMsg(getContext(), mModelMyOrders.message);
                        }
                    }
                });
    }

    private void updateOrderList() {
        if (pageNum == 1) {
            if (mModelMyOrders.data != null && mModelMyOrders.data.list.size() > 0) {
                mIvNoData.setVisibility(View.GONE);
                mRv.setVisibility(View.VISIBLE);
                mListResult.clear();
                mListResult.addAll(mModelMyOrders.data.list);
                mAdapterMyOrders.notifyDataSetChanged();
            } else {
                mIvNoData.setVisibility(View.VISIBLE);
                mRv.setVisibility(View.GONE);
            }
        } else {
            mListResult.addAll(mModelMyOrders.data.list);
            mAdapterMyOrders.notifyDataSetChanged();
        }
        if (mModelMyOrders.data.hasNextPage) {
            //有下一页数据
            mAdapterMyOrders.loadMoreComplete();
        } else {
            //没有下一页数据了
            mAdapterMyOrders.loadMoreEnd();
        }
    }

    // 添加到购物车
    private void requestCopyToCart(String orderId) {
        CopyToCartAPI.requestCopyToCart(mActivity, orderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CopyToCartModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CopyToCartModel copyToCartModel) {
                        mModelCopyToCart = copyToCartModel;
                        if (mModelCopyToCart.success) {
                            //将订单内的商品加入购物车
                            AppHelper.showMsg(mActivity, mModelCopyToCart.message);
                        } else {
                            AppHelper.showMsg(mActivity, mModelCopyToCart.message);
                        }
                    }
                });
    }

    @Override
    public void setClickEvent() {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()){
            requestOrdersList(5);
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if ((isVisibleToUser && isResumed())) {
            onResume();
        } else if (!isVisibleToUser) {
            onPause();
        }
    }

    //立即评价
    private void requestEvaluate(List<CommonModel.DataBean> data,String orderId) {
        //去评价需要将订单里面的商品列表中的商品的商品名,商品ID组成list,传到评价的界面
        mListEvaluate.clear();
        if (data != null && data.size() > 0) {
            for (int i = 0; i < data.size(); i++) {
                mListEvaluate.add(new OrderEvaluateListModel(data.get(i).getProductId(),
                        data.get(i).getBusinessType(),
                        data.get(i).getName(),data.get(i).getPicUrl(),5+"",""));

            }
        } else {
            AppHelper.showMsg(getActivity(), "订单商品数据错误!");
        }
        Intent intentPut = new Intent(mActivity, OrderEvaluateActivity.class);
        intentPut.putExtra("evaluateList", (Serializable) mListEvaluate);
        intentPut.putExtra("orderId",orderId);
        intentPut.putExtra("orderDeliveryType",orderDeliveryType);
        startActivityForResult(intentPut, 12);
    }


}
