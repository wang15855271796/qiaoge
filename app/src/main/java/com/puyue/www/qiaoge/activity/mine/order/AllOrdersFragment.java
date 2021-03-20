package com.puyue.www.qiaoge.activity.mine.order;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.puyue.www.qiaoge.R;
import com.puyue.www.qiaoge.adapter.mine.MyOrdersItemAdapter;
import com.puyue.www.qiaoge.adapter.mine.MySubOrdersItemAdapter;
import com.puyue.www.qiaoge.api.cart.CancelOrderAPI;
import com.puyue.www.qiaoge.api.cart.DeleteOrderAPI;
import com.puyue.www.qiaoge.api.mine.order.ConfirmGetGoodsAPI;
import com.puyue.www.qiaoge.api.mine.order.ConfirmOrderSelfAPI;
import com.puyue.www.qiaoge.api.mine.order.CopyToCartAPI;
import com.puyue.www.qiaoge.api.mine.order.MyOrderListAPI;
import com.puyue.www.qiaoge.base.BaseFragment;
import com.puyue.www.qiaoge.base.BaseModel;
import com.puyue.www.qiaoge.fragment.mine.coupons.PaymentFragment;
import com.puyue.www.qiaoge.fragment.mine.coupons.PaymentFragments;
import com.puyue.www.qiaoge.helper.AppHelper;
import com.puyue.www.qiaoge.helper.StringHelper;
import com.puyue.www.qiaoge.helper.UserInfoHelper;
import com.puyue.www.qiaoge.model.OrdersModel;
import com.puyue.www.qiaoge.model.cart.CancelOrderModel;
import com.puyue.www.qiaoge.model.mine.order.CommonModel;
import com.puyue.www.qiaoge.model.mine.order.ConfirmGetGoodsModel;
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

import static com.umeng.commonsdk.stateless.UMSLEnvelopeBuild.mContext;

/**
 * Created by ${王涛} on 2020/4/9
 */
public class AllOrdersFragment extends BaseFragment {

    private PtrClassicFrameLayout mPtr;
    private RecyclerView mRv;
    private MySubOrdersItemAdapter mAdapterMyOrders;
    private String mType;
    private ImageView mIvNoData;
    private OrdersModel mModelMyOrders;
    private int pageNum = 1;
    private List<OrdersModel.DataBean.ListBean> mListResult = new ArrayList<>();
    private CopyToCartModel mModelCopyToCart;
    private List<OrderEvaluateListModel> mListEvaluate = new ArrayList<>();
    private boolean isFirstLoading = true;
    String subId;
    private int orderDeliveryType;

    public static AllOrdersFragment getInstance(String subId) {
        AllOrdersFragment fragment = new AllOrdersFragment();
        Bundle bundle = new Bundle();
        bundle.putString("subId", subId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int setLayoutId() {
        return R.layout.fragment_my_orders;
    }

    @Override
    public void initViews(View view) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle getargs = getArguments();
        if (getargs != null) {
            subId = getargs.getString("subId");

        }

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
        if (UserInfoHelper.getDeliverType(mActivity) != null && StringHelper.notEmptyAndNull(UserInfoHelper.getDeliverType(mActivity))) {
            orderDeliveryType = Integer.parseInt(UserInfoHelper.getDeliverType(mActivity));
        }


        mPtr.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                pageNum = 1;
//                requestOrdersList(0);
            }
        });
        subId = getArguments().getString("subId");
        if (orderDeliveryType == 0) {
            mAdapterMyOrders = new MySubOrdersItemAdapter(R.layout.item_my_order,subId, mListResult, 0, orderDeliveryType, new MySubOrdersItemAdapter.OnClick() {
                @Override
                public void evaluateNowOnclick(int position,String orderId) { // 立即评价
                    getComment(orderId);
                }

                @Override
                public void againBayOnclick(int position) { //再次购买

                    OrdersModel.DataBean.ListBean listBean = mListResult.get(position);
                    requestCopyToCart(listBean.orderId);
                }

                @Override
                public void cancelOnclick(String orderId) {

                    final AlertDialog mDialog = new AlertDialog.Builder(getContext()).create();
                    mDialog.show();
                    mDialog.getWindow().setContentView(R.layout.dailog_cancel);
                    TextView mBtnCancel = (TextView) mDialog.getWindow().findViewById(R.id.btnCancel);
                    TextView mBtnOK = (TextView) mDialog.getWindow().findViewById(R.id.btnOK);

                    mBtnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                        }
                    });


                    mBtnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                            //取消订单的接口
                            cancelOrder(orderId);
                            //   mPtr.refreshComplete();
                            //   mAdapterMyOrders.notifyDataSetChanged();
                        }
                    });

                }

                @Override
                public void deleteOnclick(String orderId) {
                    final AlertDialog mDialog = new AlertDialog.Builder(getContext()).create();
                    mDialog.show();
                    mDialog.getWindow().setContentView(R.layout.dialog_delete_order);
                    TextView mBtnCancel = (TextView) mDialog.getWindow().findViewById(R.id.btnCancel);
                    TextView mBtnOK = (TextView) mDialog.getWindow().findViewById(R.id.btnOK);

                    mBtnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                        }
                    });


                    mBtnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                            //取消订单的接口
                            deleteOrder(orderId);
                            //   mPtr.refreshComplete();
                            //   mAdapterMyOrders.notifyDataSetChanged();
                        }
                    });


                }

                @Override
                public void imageGo(String orderId, String payAmount) {
//                    Intent intent = new Intent(getActivity(), MyConfireOrdersActivity.class);
//                    intent.putExtra("orderId", orderId);
//                    intent.putExtra("remark", "");
//                    intent.putExtra("payAmount", Double.parseDouble(payAmount));
//                    intent.putExtra("flag", true);
//                    intent.putExtra("orderDeliveryType", orderDeliveryType);
//                    startActivity(intent);

                    PaymentFragments paymentFragment = new PaymentFragments();
                    Bundle bundle = new Bundle();
                    bundle.putString("total", payAmount);
                    bundle.putString("remark","");
                    bundle.putString("payAmount",payAmount);
                    bundle.putString("orderId",orderId);
                    bundle.putString("orderDeliveryType",orderDeliveryType+"");
                    paymentFragment.setArguments(bundle);
                    paymentFragment.setCancelable(false);
                    paymentFragment.show(getFragmentManager(),"paymentFragment");
                }

                @Override
                public void requestConfirmGetGoods(String orderId) {
                    ConfirmGetGoodsAPI.reuqestConfirmGetGoods(getContext(), orderId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<ConfirmGetGoodsModel>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(ConfirmGetGoodsModel confirmGetGoodsModel) {

                                    if (confirmGetGoodsModel.success) {
                                        //确认收货成功
                                        AppHelper.showMsg(mContext, "确认收货成功");
                                        requestOrdersList(0);
                                        mPtr.autoRefresh();
                                        //刷新订单状态
                                        //  getOrderDetail(orderId, orderState, returnProductMainId);
                                    } else {
                                        AppHelper.showMsg(mContext, confirmGetGoodsModel.message);
                                    }
                                }
                            });
                }

                @Override
                public void confirmSelfOrder(String orderId) {

                }

                @Override
                public void confirmSelfReturnOrder(String orderId, int pos) {

                }


            });
        } else if (orderDeliveryType == 1) {
            mAdapterMyOrders = new MySubOrdersItemAdapter(R.layout.item_my_order_self,subId,mListResult, 0, orderDeliveryType, new MySubOrdersItemAdapter.OnClick() {


                @Override
                public void evaluateNowOnclick(int position,String orderId) { // 立即评价
                    getComment(orderId);
                }

                @Override
                public void againBayOnclick(int position) { //再次购买

                    OrdersModel.DataBean.ListBean listBean = mListResult.get(position);
                    requestCopyToCart(listBean.orderId);
                }

                @Override
                public void cancelOnclick(String orderId) {

                    final AlertDialog mDialog = new AlertDialog.Builder(getContext()).create();
                    mDialog.show();
                    mDialog.getWindow().setContentView(R.layout.dailog_cancel);
                    TextView mBtnCancel = (TextView) mDialog.getWindow().findViewById(R.id.btnCancel);
                    TextView mBtnOK = (TextView) mDialog.getWindow().findViewById(R.id.btnOK);

                    mBtnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                        }
                    });


                    mBtnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                            //取消订单的接口
                            cancelOrder(orderId);
                            //   mPtr.refreshComplete();
                            //   mAdapterMyOrders.notifyDataSetChanged();
                        }
                    });

                }

                @Override
                public void deleteOnclick(String orderId) {
                    final AlertDialog mDialog = new AlertDialog.Builder(getContext()).create();
                    mDialog.show();
                    mDialog.getWindow().setContentView(R.layout.dialog_delete_order);
                    TextView mBtnCancel = (TextView) mDialog.getWindow().findViewById(R.id.btnCancel);
                    TextView mBtnOK = (TextView) mDialog.getWindow().findViewById(R.id.btnOK);

                    mBtnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                        }
                    });


                    mBtnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                            //取消订单的接口
                            deleteOrder(orderId);
                            //   mPtr.refreshComplete();
                            //   mAdapterMyOrders.notifyDataSetChanged();
                        }
                    });


                }

                @Override
                public void imageGo(String orderId, String payAmount) {
//                    Intent intent = new Intent(getActivity(), MyConfireOrdersActivity.class);
//                    intent.putExtra("orderId", orderId);
//                    intent.putExtra("remark", "");
//                    intent.putExtra("payAmount", Double.parseDouble(payAmount));
//                    intent.putExtra("flag", true);
//                    intent.putExtra("orderDeliveryType", orderDeliveryType);
//                    startActivity(intent);

                    PaymentFragments paymentFragment = new PaymentFragments();
                    Bundle bundle = new Bundle();
                    bundle.putString("total", payAmount);
                    bundle.putString("remark", "");
                    bundle.putString("payAmount",payAmount);
                    bundle.putString("orderId",orderId);
                    bundle.putString("orderDeliveryType",orderDeliveryType+"");
                    paymentFragment.setArguments(bundle);
                    paymentFragment.setCancelable(false);
                    paymentFragment.show(getFragmentManager(),"paymentFragment");
                }

                @Override
                public void requestConfirmGetGoods(String orderId) {
                    ConfirmGetGoodsAPI.reuqestConfirmGetGoods(getContext(), orderId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<ConfirmGetGoodsModel>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(ConfirmGetGoodsModel confirmGetGoodsModel) {

                                    if (confirmGetGoodsModel.success) {
                                        //确认收货成功
                                        AppHelper.showMsg(mContext, "确认收货成功");

                                        mPtr.autoRefresh();
                                        requestOrdersList(0);
                                        //刷新订单状态
                                        //  getOrderDetail(orderId, orderState, returnProductMainId);
                                    } else {
                                        AppHelper.showMsg(mContext, confirmGetGoodsModel.message);
                                    }
                                }
                            });
                }

                @Override
                public void confirmSelfOrder(String orderId) {
                    showConfirmOrderDialog(orderId);

                }

                @Override
                public void confirmSelfReturnOrder(String orderId, int position) {

                    String orderStatus1 = String.valueOf(mModelMyOrders.data.list.get(position).orderStatus);
                    Intent intent = new Intent(mActivity, ReturnGoodActivity.class);
                    intent.putExtra("orderId", orderId);
                    intent.putExtra("orderStatus", orderStatus1);
                    intent.putExtra("orderDeliveryType",orderDeliveryType);
                    startActivity(intent);


                }


            });
        }

      /*  mAdapterMyOrders = new MyOrdersItemAdapter(R.layout.item_my_order, mListResult, 0, new MyOrdersItemAdapter.OnClick() {


            @Override
            public void evaluateNowOnclick(int position) { // 立即评价
                MyOrdersModel.DataBean.ListBean listBean = mListResult.get(position);
                requestEvaluate(listBean);
            }

            @Override
            public void againBayOnclick(int position) { //再次购买

                MyOrdersModel.DataBean.ListBean listBean = mListResult.get(position);
                requestCopyToCart(listBean.orderId);
            }

            @Override
            public void cancelOnclick(String orderId) {

                final AlertDialog mDialog = new AlertDialog.Builder(getContext()).create();
                mDialog.show();
                mDialog.getWindow().setContentView(R.layout.dailog_cancel);
                TextView mBtnCancel = (TextView) mDialog.getWindow().findViewById(R.id.btnCancel);
                TextView mBtnOK = (TextView) mDialog.getWindow().findViewById(R.id.btnOK);

                mBtnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });


                mBtnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        //取消订单的接口
                        cancelOrder(orderId);
                        //   mPtr.refreshComplete();
                        //   mAdapterMyOrders.notifyDataSetChanged();
                    }
                });

            }

            @Override
            public void deleteOnclick(String orderId) {
                final AlertDialog mDialog = new AlertDialog.Builder(getContext()).create();
                mDialog.show();
                mDialog.getWindow().setContentView(R.layout.dialog_delete_order);
                TextView mBtnCancel = (TextView) mDialog.getWindow().findViewById(R.id.btnCancel);
                TextView mBtnOK = (TextView) mDialog.getWindow().findViewById(R.id.btnOK);

                mBtnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });


                mBtnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        //取消订单的接口
                        deleteOrder(orderId);
                        //   mPtr.refreshComplete();
                        //   mAdapterMyOrders.notifyDataSetChanged();
                    }
                });


            }

            @Override
            public void imageGo(String orderId, String payAmount) {
                Intent intent = new Intent(getActivity(), MyConfireOrdersActivity.class);
                intent.putExtra("orderId", orderId);
                intent.putExtra("remark", "");
                intent.putExtra("payAmount", Double.parseDouble(payAmount));
                intent.putExtra("flag", true);
                intent.putExtra("orderDeliveryType",orderDeliveryType);
                startActivity(intent);
            }

            @Override
            public void requestConfirmGetGoods(String orderId) {
                ConfirmGetGoodsAPI.reuqestConfirmGetGoods(getContext(), orderId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<ConfirmGetGoodsModel>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(ConfirmGetGoodsModel confirmGetGoodsModel) {

                                if (confirmGetGoodsModel.success) {
                                    //确认收货成功
                                    AppHelper.showMsg(mContext, "确认收货成功");
                                    requestOrdersList(0);
                                    mPtr.autoRefresh();
                                    //刷新订单状态
                                    //  getOrderDetail(orderId, orderState, returnProductMainId);
                                } else {
                                    AppHelper.showMsg(mContext, confirmGetGoodsModel.message);
                                }
                            }
                        });
            }


        });*/


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
                requestOrdersList(0);
            }
        });
//        requestOrdersList(0);

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

    /**
     * 确认收货弹窗
     */

    private void showConfirmOrderDialog(String orderId) {
        AlertDialog dialog = new AlertDialog.Builder(mActivity, R.style.DialogStyle).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Window window = dialog.getWindow();
        window.setContentView(R.layout.confirm_sufficiency_order_dialog);
        TextView tv_ok = window.findViewById(R.id.tv_ok);
        TextView tv_cancel = window.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestConfirmGetGoods(orderId);
            }
        });
    }

    private void  requestConfirmGetGoods(String orderId){
        ConfirmOrderSelfAPI.requestDriverMe(mActivity, orderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BaseModel baseModel) {
                        if (baseModel.success) {
                            //确认收货成功
                            AppHelper.showMsg(mContext, "确认提货成功");
                            //刷新订单状态

                            mListResult.clear();
                            mPtr.autoRefresh();
                            pageNum = 1;
                            requestOrdersList(0);

                        } else {
                            AppHelper.showMsg(mContext, baseModel.message);
                        }
                    }
                });
    }


    /**
     * 子账户订单列表
     * @param orderStatus
     */
    private void requestOrdersList(int orderStatus) {
        MyOrderListAPI.getList(getContext(), orderStatus, pageNum, 20,orderDeliveryType,subId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<OrdersModel>() {
                    @Override
                    public void onCompleted() {
                        mPtr.refreshComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mPtr.refreshComplete();
                    }

                    @Override
                    public void onNext(OrdersModel myOrdersModel) {
                        mPtr.refreshComplete();
                        logoutAndToHome(getContext(), myOrdersModel.code);
                        mModelMyOrders = myOrdersModel;
                        if (mModelMyOrders.success) {
                            updateOrderList();
                        } else {
                            AppHelper.showMsg(getActivity(), mModelMyOrders.message);
                        }
                    }
                });
    }

    private void updateOrderList() {
        if (pageNum == 1) {
            //第一次加载
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
            //加载更多数据
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

    @Override
    public void setClickEvent() {

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

    //取消订单
    private void cancelOrder(String orderId) {
        CancelOrderAPI.requestData(getContext(), orderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CancelOrderModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CancelOrderModel cancelOrderModel) {
                        if (cancelOrderModel.success) {
                            //取消成功
                            AppHelper.showMsg(mContext, "取消订单成功");
                            //mPtr.autoRefresh();
                            mPtr.autoRefresh();
                            requestOrdersList(1);

                            // getOrderDetail(orderId, orderState, returnProductMainId);
                        } else {
                            AppHelper.showMsg(mContext, cancelOrderModel.message);
                        }
                    }

                });
    }

    //删除订单
    private void deleteOrder(String orderId) {
        DeleteOrderAPI.requestData(getContext(), orderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CancelOrderModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CancelOrderModel cancelOrderModel) {
                        if (cancelOrderModel.success) {
                            //删除成功
                            AppHelper.showMsg(mContext, "删除订单成功");

                            // getOrderDetail(orderId, orderState, returnProductMainId);
                            //mPtr.autoRefresh();
                            pageNum = 1;
                            mPtr.autoRefresh();
                            requestOrdersList(0);

                        } else {
                            AppHelper.showMsg(mContext, cancelOrderModel.message);
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            mPtr.autoRefresh();
            pageNum = 1;
            requestOrdersList(0);


        }


    }

    //重新进来的时候自动刷新fragment
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if ((isVisibleToUser && isResumed())) {
            onResume();
        } else if (!isVisibleToUser) {
            onPause();

        }
    }

}
