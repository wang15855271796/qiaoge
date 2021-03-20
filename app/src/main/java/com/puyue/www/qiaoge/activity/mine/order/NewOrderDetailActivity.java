package com.puyue.www.qiaoge.activity.mine.order;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.tu.loadingdialog.LoadingDailog;

import com.bumptech.glide.Glide;
import com.puyue.www.qiaoge.R;
import com.puyue.www.qiaoge.activity.BeizhuActivity;
import com.puyue.www.qiaoge.activity.CartActivity;
import com.puyue.www.qiaoge.activity.HomeActivity;
import com.puyue.www.qiaoge.activity.mine.account.AddressListActivity;
import com.puyue.www.qiaoge.activity.mine.account.AddressListsActivity;
import com.puyue.www.qiaoge.adapter.NewOrderUnOperateAdapter;
import com.puyue.www.qiaoge.adapter.OrderFullAdapter;

import com.puyue.www.qiaoge.adapter.mine.NewOrderDetailAdapter;
import com.puyue.www.qiaoge.api.cart.CancelOrderAPI;
import com.puyue.www.qiaoge.api.cart.DeleteOrderAPI;
import com.puyue.www.qiaoge.api.home.GetOrderDetailAPI;
import com.puyue.www.qiaoge.api.mine.order.ConfirmGetGoodsAPI;
import com.puyue.www.qiaoge.api.mine.order.CopyToCartAPI;
import com.puyue.www.qiaoge.api.mine.order.GetDeliverTimeOrderAPI;
import com.puyue.www.qiaoge.api.mine.order.MyOrderListAPI;
import com.puyue.www.qiaoge.api.mine.order.NotifyDeliverTimeOrderAPI;
import com.puyue.www.qiaoge.base.BaseModel;
import com.puyue.www.qiaoge.base.BaseSwipeActivity;
import com.puyue.www.qiaoge.constant.AppConstant;
import com.puyue.www.qiaoge.event.AddressEvent;
import com.puyue.www.qiaoge.event.BackEvent;
import com.puyue.www.qiaoge.event.BeizhuEvent;
import com.puyue.www.qiaoge.event.GoToCartFragmentEvent;
import com.puyue.www.qiaoge.fragment.mine.coupons.PaymentFragment;
import com.puyue.www.qiaoge.fragment.mine.coupons.PaymentFragments;
import com.puyue.www.qiaoge.helper.AppHelper;
import com.puyue.www.qiaoge.helper.StringHelper;
import com.puyue.www.qiaoge.listener.NoDoubleClickListener;
import com.puyue.www.qiaoge.model.cart.CancelOrderModel;
import com.puyue.www.qiaoge.model.cart.GetOrderDetailModel;
import com.puyue.www.qiaoge.model.home.GetDeliverTimeModel;
import com.puyue.www.qiaoge.model.mine.order.CommonModel;
import com.puyue.www.qiaoge.model.mine.order.ConfirmGetGoodsModel;
import com.puyue.www.qiaoge.model.mine.order.CopyToCartModel;
import com.puyue.www.qiaoge.model.mine.order.OrderEvaluateListModel;
import com.puyue.www.qiaoge.view.GradientColorTextView;
import com.puyue.www.qiaoge.view.SnapUpCountDownTimerView;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ${daff}
 * on 2018/10/20
 * 备注  改版后的 订单详情
 */
public class NewOrderDetailActivity extends BaseSwipeActivity {

    private ImageView imageViewBreak;
    private RecyclerView recyclerView;
    RelativeLayout rl_unOperate;
    RelativeLayout rl_operate;
    RecyclerView rv_full;
    TextView tv_unOperate_num;
    TextView tv_operate_num;
    // 非退货订单layout 头部
    private LinearLayout orderLinearLayout;
    //支付方式
    LinearLayout ll_payWay;
    //底部支付信息
    LinearLayout ll_order_info;
    //备注
//    RelativeLayout ll_beizhu;
    //关联订单
    LinearLayout linearLayoutPay;
    TextView tv_beizhu;
    private TextView tvOrderTitle; //订单类型
    private SnapUpCountDownTimerView orderTimerView; //倒计时
    private TextView tvOrderContent; //订单内容
    private LinearLayout threeButtonLayout; //三个按钮（退货按钮 评价按钮 在次购买按钮）
    private TextView buttonReturnGoods; // 退货按钮
    private TextView buttonEvaluate; //评价按钮
    private TextView buttonAgainBuy; // 在次购买按钮

    private LinearLayout twoButtonLayout; //二个按钮（取消订单  去支付）
    private TextView buttonCancelOrder; // 取消订单
    private TextView buttonGOPay; // 去支付

    //退货订单 layout 头部
    private LinearLayout ReturnGoodsLinearLayout;
    private TextView ReturnGoodsTitle; //退货类型
    private TextView ReturnGoodsContent; //退货内容
    TextView tv_full_price;
    TextView tv_full_desc;
    LinearLayout ll_full_activies;
    private TextView tvNewOrderCommodityAmount; //商品金额
    private TextView tvNewOrderDistributionFeePrice; // 配送费
    private TextView tvNewOrderVipSubtractionPrice; //会员满减
    private TextView tvNewOrderCoupons;//优惠卷

    private TextView returnGoodsCommodityNum;//退货数量
    private TextView returnGoodsCommodityAmount; // 退货金额
    private TextView returnGoodsReason; //退款原因


    //以下是每个订单详情都有的订单信息
    private TextView tvNewOrderAddresseeName;
    private TextView tvNewOrderAddress;
    //关联id
    TextView tv_connect_id;
    private TextView tvNewOrderTime;//下单时间
    private ImageView tvNewOrderPay; //下单方式
    private TextView tvNewOrderRemarks; //备注
    //付款时间
    private LinearLayout tvNewOrderPayTimeLinearLayout;
    private TextView tvNewOrderPayTime;
    //申请退货时间
    private LinearLayout newOrderReturnTimeLinearLayout;
    private TextView tvNewOrderReturnTime;
    //审核时间
    private TextView tvExamineTime;
    private LinearLayout examineTimeLinearLayout;

    //支付方式
    TextView tv_payWay;
    //支付金额
    TextView tv_total_amount;
    private GradientColorTextView tvInfo;

    //删除订单
    private LinearLayout deleteButtonLayout;
    private TextView tv_delete;
    private TextView tv_buttonAgainBuy;

    ImageView iv_operate_pic;
    //自营
    private NewOrderDetailAdapter adapter;
    //自营
    private List<GetOrderDetailModel.DataBean.OrderProdsBean> list = new ArrayList<>();

    private List<GetOrderDetailModel.DataBean.SendGiftInfo> list_full = new ArrayList<>();
    private String returnProductMainId = "";
    private String orderId;
    private String orderState = "";
    private int orderStatusRequest;
    private Dialog mDialog;

    private List<OrderEvaluateListModel> mListEvaluate = new ArrayList<>();
    private GetOrderDetailModel.DataBean getOrderDetailModel;
    private ConfirmGetGoodsModel mModelConfirmGetGoods;
    private RelativeLayout relativeLayoutCommodityReturn;

    private TextView mTvDeliverTime;
    private LinearLayout mLinearLayoutDeliver;
    private LinearLayout linearLayoutAddressArrow;
    private TextView sendAmountStr;
    private TextView tvDeductDsc;
    private LinearLayout ll_pay;

    private LinearLayout mLinearLayoutShipped;

    private LinearLayout mLinearLayoutEvalute;
    private LinearLayout mLinearLayoutReciverOrder;
    //复制订单
    private TextView tvCopyOrderOne;
    private TextView tvCopyOrderTwo;
    private TextView tvCopyOrderThree;
    private AVLoadingIndicatorView lav_activity_loading;
    private TextView mTvConfirmOrder;
    //物流信息
    private RelativeLayout mRelativeDriver;
    private TextView mTvDriverDeliverTime;
    private ImageView mFreshDriverStatus;
    private TextView mTvOrderStatus;
    private LinearLayout ll_beizhu_desc;
    private TextView mTvSeeDriverStatus;
    private TextView mTvDriverName;
    private TextView mTvOrderReturn;
    private TextView tv_return_reason;
    private TextView tv_driver_phone;
    private TextView tv_normal_vip_desc;
    private LoadingDailog dialog;
    private TextView buttonReturnGood_two;
    private TextView tv_notify_time;//修改配送时间
    private String deliverTimeStart = "";
    private String deliverTimeEnd = "";
    private String deliverTimeName = "";
    List<String> mlist = new ArrayList<>();
    private String goAccount;
    private TextView tv_evaluate;
    private int returnCode;
    private boolean isShowed = false;
    private String account;
    private String subId;
    OrderFullAdapter orderFullAdapter;
    TextView tv_amount;
    @Override
    public boolean handleExtra(Bundle savedInstanceState) {
        return false;
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_new_order_detail);
    }

    @Override
    public void findViewById() {
        ll_full_activies = (LinearLayout) findViewById(R.id.ll_full_activies);
        tv_full_price =  (TextView) findViewById(R.id.tv_full_price);
        tv_full_desc =  (TextView) findViewById(R.id.tv_full_desc);
        ll_beizhu_desc = (LinearLayout) findViewById(R.id.ll_beizhu_desc);
        linearLayoutPay = (LinearLayout) findViewById(R.id.linearLayoutPay);
        ll_pay = (LinearLayout) findViewById(R.id.ll_pay);
        tv_beizhu = (TextView) findViewById(R.id.tv_beizhu);
        ll_order_info = (LinearLayout) findViewById(R.id.ll_order_info);
//        ll_beizhu = (RelativeLayout) findViewById(R.id.ll_beizhu);
        ll_payWay = (LinearLayout) findViewById(R.id.ll_payWay);
        tv_operate_num = (TextView) findViewById(R.id.tv_operate_num);
        rl_unOperate = (RelativeLayout) findViewById(R.id.rl_unOperate);
        rl_operate = (RelativeLayout) findViewById(R.id.rl_operate);
        iv_operate_pic = (ImageView) findViewById(R.id.iv_operate_pic);
        tv_connect_id = (TextView) findViewById(R.id.tv_connect_id);
        tv_payWay = (TextView) findViewById(R.id.tv_payWay);
        tv_total_amount = (TextView) findViewById(R.id.tv_total_amount);
        tv_amount = (TextView) findViewById(R.id.tv_amount);
        rv_full = (RecyclerView) findViewById(R.id.rv_full);
        imageViewBreak = (ImageView) findViewById(R.id.imageViewBreak);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        orderLinearLayout = (LinearLayout) findViewById(R.id.orderLinearLayout);
        tvOrderTitle = (TextView) findViewById(R.id.tvOrderTitle);
        orderTimerView = (SnapUpCountDownTimerView) findViewById(R.id.orderTimerView);
        tvOrderContent = (TextView) findViewById(R.id.tvOrderContent);
        threeButtonLayout = (LinearLayout) findViewById(R.id.threeButtonLayout);
        buttonReturnGoods = (TextView) findViewById(R.id.buttonReturnGoods);
        buttonEvaluate = (TextView) findViewById(R.id.buttonEvaluate);
        buttonAgainBuy = (TextView) findViewById(R.id.buttonAgainBuy);
        twoButtonLayout = (LinearLayout) findViewById(R.id.twoButtonLayout);
        buttonCancelOrder = (TextView) findViewById(R.id.buttonCancelOrder);
        buttonGOPay = (TextView) findViewById(R.id.buttonGOPay);
        ReturnGoodsLinearLayout = (LinearLayout) findViewById(R.id.ReturnGoodsLinearLayout);
        ReturnGoodsTitle = (TextView) findViewById(R.id.ReturnGoodsTitle);
        ReturnGoodsContent = (TextView) findViewById(R.id.ReturnGoodsContent);
        tvNewOrderCommodityAmount = (TextView) findViewById(R.id.tvNewOrderCommodityAmount);
        tvNewOrderDistributionFeePrice = (TextView) findViewById(R.id.tvNewOrderDistributionFeePrice);
        tvNewOrderVipSubtractionPrice = (TextView) findViewById(R.id.tvNewOrderVipSubtractionPrice);
        tvNewOrderCoupons = (TextView) findViewById(R.id.tvNewOrderCoupons);
//        tvnormalReduct = (TextView) findViewById(R.id.tvnormalReduct);
        mTvOrderReturn = findViewById(R.id.tv_order_return);
        returnGoodsCommodityNum = (TextView) findViewById(R.id.returnGoodsCommodityNum);
        returnGoodsCommodityAmount = (TextView) findViewById(R.id.returnGoodsCommodityAmount);

        returnGoodsReason = (TextView) findViewById(R.id.returnGoodsReason);

        tvNewOrderTime = (TextView) findViewById(R.id.tvNewOrderTime);
//        tvNewOrderPay = (ImageView) findViewById(R.id.tvNewOrderPay);
        tvNewOrderRemarks = (TextView) findViewById(R.id.tvNewOrderRemarks);

        tvNewOrderAddresseeName = (TextView) findViewById(R.id.tvNewOrderAddresseeName);
        tvNewOrderAddress = (TextView) findViewById(R.id.tvNewOrderAddress);
        tvNewOrderPayTimeLinearLayout = (LinearLayout) findViewById(R.id.tvNewOrderPayTimeLinearLayout);
        tvNewOrderPayTime = (TextView) findViewById(R.id.tvNewOrderPayTime);
        newOrderReturnTimeLinearLayout = (LinearLayout) findViewById(R.id.newOrderReturnTimeLinearLayout);
        tvNewOrderReturnTime = (TextView) findViewById(R.id.tvNewOrderReturnTime);
        tvExamineTime = (TextView) findViewById(R.id.tvExamineTime);
        examineTimeLinearLayout = (LinearLayout) findViewById(R.id.examineTimeLinearLayout);
        tvInfo = (GradientColorTextView) findViewById(R.id.tvInfo);
        relativeLayoutCommodityReturn = (RelativeLayout) findViewById(R.id.relativeLayoutCommodityReturn);
        //删除订单
        deleteButtonLayout = (LinearLayout) findViewById(R.id.deleteButtonLayout);
        tv_delete = (TextView) findViewById(R.id.tv_delete);
        tv_buttonAgainBuy = (TextView) findViewById(R.id.tv_buttonAgainBuy);
        mTvDeliverTime = findViewById(R.id.tv_deliver_time);
        mLinearLayoutDeliver = findViewById(R.id.deliver_linearLayout);
        linearLayoutAddressArrow = findViewById(R.id.linearLayout_address_arrow);
        sendAmountStr = findViewById(R.id.tv_send_amount_str);
        tvDeductDsc = findViewById(R.id.tv_deduct_desc);
//        tvNomalReductDesc = findViewById(R.id.tv_normal_reduct_desc);
        lav_activity_loading = findViewById(R.id.lav_activity_loading);
        mLinearLayoutShipped = findViewById(R.id.linearLayout_shipped);
        mLinearLayoutEvalute = findViewById(R.id.linearLayout_evalute);
        mLinearLayoutReciverOrder = findViewById(R.id.linearLayout_get_order);
        tvCopyOrderOne = findViewById(R.id.copy_order);
        tvCopyOrderTwo = findViewById(R.id.tv_get_order_copy);
        tvCopyOrderThree = findViewById(R.id.tv_copy_order);
        mTvConfirmOrder = findViewById(R.id.tv_confirm_order);
        mRelativeDriver = findViewById(R.id.relativeLayout_driver);
        mTvDriverDeliverTime = findViewById(R.id.tv_driver_content);
        mFreshDriverStatus = findViewById(R.id.iv_fresh_status);
        mTvOrderStatus = findViewById(R.id.tv_order_status);
        mTvSeeDriverStatus = findViewById(R.id.tv_order_driver_message);
        mTvDriverName = findViewById(R.id.tv_driver_name);
        tv_return_reason = findViewById(R.id.tv_return_reason);
        tv_driver_phone = findViewById(R.id.tv_driver_phone);
        tv_normal_vip_desc = findViewById(R.id.tv_normal_vip_desc);
        buttonReturnGood_two = findViewById(R.id.buttonReturnGood_two);
        tv_notify_time = findViewById(R.id.tv_notify_time);
        tv_evaluate = findViewById(R.id.tv_evaluate);

    }

    @Override
    public void setViewData() {

//        EventBus.getDefault().register(this);
        orderId = getIntent().getStringExtra(AppConstant.ORDERID);
        subId = getIntent().getStringExtra("subId");
        //账号标识 1子账号点击进来的   0“我的界面”点击进来的 2订单确认界面进来的
        account = getIntent().getStringExtra("account");
        orderState = getIntent().getStringExtra(AppConstant.ORDERSTATE);
        returnProductMainId = getIntent().getStringExtra(AppConstant.RETURNPRODUCTMAINID);
        goAccount = getIntent().getStringExtra("goAccount");
        //未支付，15分钟后跳转到取消订单
        orderTimerView.setTimeout(new SnapUpCountDownTimerView.Timeout() {
            @Override
            public void getStop() {
                if (orderStatusRequest == 1) {
                    cancelOrder(orderId);
                }
            }
        });

        adapter = new NewOrderDetailAdapter(mActivity,R.layout.new_order_detail, list,orderId);
        //自营
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        rv_full.setLayoutManager(new LinearLayoutManager(mContext));
        orderFullAdapter = new OrderFullAdapter(R.layout.item_full_order,list_full);
        rv_full.setAdapter(orderFullAdapter);

        // 文字渐变色
        LinearGradient mLinearGradient = new LinearGradient(0, 0, 0, tvInfo.getPaint().getTextSize(), Color.parseColor("#CEA6FF")
                , Color.parseColor("#6F81FF"), Shader.TileMode.CLAMP);
        tvInfo.getPaint().setShader(mLinearGradient);
    }

    @Override
    public void setClickEvent() {
//        ll_beizhu.setOnClickListener(noDoubleClickListener);
        tv_connect_id.setOnClickListener(noDoubleClickListener);
        imageViewBreak.setOnClickListener(noDoubleClickListener);
        buttonCancelOrder.setOnClickListener(noDoubleClickListener);
        buttonGOPay.setOnClickListener(noDoubleClickListener);
        buttonReturnGoods.setOnClickListener(noDoubleClickListener);
        buttonEvaluate.setOnClickListener(noDoubleClickListener);
        buttonAgainBuy.setOnClickListener(noDoubleClickListener);
        tv_delete.setOnClickListener(noDoubleClickListener);
        tv_buttonAgainBuy.setOnClickListener(noDoubleClickListener);
        linearLayoutAddressArrow.setOnClickListener(noDoubleClickListener);

        tvCopyOrderOne.setOnClickListener(noDoubleClickListener);
        tvCopyOrderTwo.setOnClickListener(noDoubleClickListener);
        tvCopyOrderThree.setOnClickListener(noDoubleClickListener);
        mTvConfirmOrder.setOnClickListener(noDoubleClickListener);
        mFreshDriverStatus.setOnClickListener(noDoubleClickListener);
        mTvOrderReturn.setOnClickListener(noDoubleClickListener);
        mTvSeeDriverStatus.setOnClickListener(noDoubleClickListener);
        buttonReturnGood_two.setOnClickListener(noDoubleClickListener);
        tv_notify_time.setOnClickListener(noDoubleClickListener);
        tv_evaluate.setOnClickListener(noDoubleClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        list.clear();
        getOrderDetail(orderId);

    }

//    BeizhuEvent beizhuEvent;
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void getBeizhu(BeizhuEvent beizhuEvent) {
//        this.beizhuEvent = beizhuEvent;
//        tv_beizhu.setText(beizhuEvent.getBeizhu());
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backEvent();
            //暂且注释
            EventBus.getDefault().post(new BackEvent());
//            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }


    private void getOrderDetailDialog(String orderId) {
        GetOrderDetailAPI.requestData(mContext, orderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GetOrderDetailModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(GetOrderDetailModel orderDetailModel) {

                        if (orderDetailModel.success) {
                            dialog.dismiss();
                            if (orderDetailModel != null) {
                                setText(orderDetailModel);
                                list.clear();

                                if (orderDetailModel.data.orderProds.size()>0) {
                                    list.addAll(orderDetailModel.data.orderProds);
                                }
                            }
                            adapter.notifyDataSetChanged();

                        } else {
                            AppHelper.showMsg(mContext, orderDetailModel.message);
                        }
                    }
                });
    }

    //获取订单详情
    private void getOrderDetail(String orderId) {
        GetOrderDetailAPI.requestData(mContext, orderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GetOrderDetailModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(GetOrderDetailModel orderDetailModel) {

                        if (orderDetailModel.success) {
                            if (orderDetailModel != null) {
                                setText(orderDetailModel);
                                list.clear();
                                list_full.clear();
                                if (orderDetailModel.data.orderProds.size() > 0) {

                                    list.addAll(orderDetailModel.data.orderProds);
                                    adapter.notifyDataSetChanged();
                                    if(orderDetailModel.data.sendGiftInfo!=null) {
                                        list_full.addAll(orderDetailModel.data.sendGiftInfo);
                                        orderFullAdapter.notifyDataSetChanged();
                                        rv_full.setVisibility(View.VISIBLE);
                                    }else {
                                        rv_full.setVisibility(View.GONE);
                                    }

                                }
                            }


                            if (returnCode == 39 && !isShowed) {
                                isShowed = true;
                                getDeliverChangeTime();
                            }
                            //  getDeliverChangeTime();
                        } else {
                            AppHelper.showMsg(mContext, orderDetailModel.message);
                        }
                    }
                });
    }

    //设置文字
    private void setText(GetOrderDetailModel info) {

        getOrderDetailModel = info.data;
        ReturnGoodsTitle.setText(getOrderDetailModel.orderStatusName);
        tvOrderTitle.setText(getOrderDetailModel.orderStatusName);
        orderStatusRequest = getOrderDetailModel.orderStatus;
        setViewShow();
        //关联id
        if(info.data.connectOrderId!=null) {
            tv_connect_id.setText(info.data.connectOrderId);
            linearLayoutPay.setVisibility(View.VISIBLE);
        }else {
            linearLayoutPay.setVisibility(View.GONE);
        }

        tv_payWay.setText(info.data.payChannel);
        tv_total_amount.setText("￥"+info.data.totalAmount);
        tv_amount.setText(info.data.prodNum+"");
        tvNewOrderCommodityAmount.setText("￥" + getOrderDetailModel.prodAmount);

        tvNewOrderVipSubtractionPrice.setText("￥" + getOrderDetailModel.vipReductStr);
        tvNewOrderDistributionFeePrice.setText("￥" + getOrderDetailModel.deliveryFee);


//        tvNewOrderCoupons.setText("￥" + getOrderDetailModel.offerAmount + "");
//        returnGoodsCommodityNum.setText(getOrderDetailModel.applyReturnDate);
//        returnGoodsReason.setText(getOrderDetailModel.returnReson);

//        returnGoodsCommodityAmount.setText(getOrderDetailModel.date);
//        tv_return_reason.setText(getOrderDetailModel.returnReson);
        //配送费
        sendAmountStr.setText(getOrderDetailModel.sendAmountStr);
        tvNewOrderTime.setText(getOrderDetailModel.gmtCreate);
        tv_normal_vip_desc.setText(getOrderDetailModel.vipReductStr);
        //优惠券金额
        tvNewOrderCoupons.setText("￥"+getOrderDetailModel.giftAmount);
        //优惠券描述
        tvDeductDsc.setText(getOrderDetailModel.giftName);
        tv_full_desc.setText(getOrderDetailModel.normalReductStr);
        tv_full_price.setText(getOrderDetailModel.normalReduct);
        //已取消
//        if(getOrderDetailModel.orderStatus==7||getOrderDetailModel.orderStatus==1) {
//            ll_pay.setVisibility(View.GONE);
//            ll_beizhu.setVisibility(View.GONE);
//        }else {
//            ll_pay.setVisibility(View.VISIBLE);
//            ll_beizhu.setVisibility(View.GONE);
//        }
        if (getOrderDetailModel.deliverTimeStart == null) {
            mLinearLayoutDeliver.setVisibility(View.GONE);
        } else {
            mLinearLayoutDeliver.setVisibility(View.VISIBLE);
            mTvDeliverTime.setText(getOrderDetailModel.deliverTimeName + "  " + getOrderDetailModel.deliverTimeStart + " - " + getOrderDetailModel.deliverTimeEnd);
        }

        mTvDriverName.setText(getOrderDetailModel.driverName);
        tv_driver_phone.setText(getOrderDetailModel.driverPhone);
        tvNewOrderAddresseeName.setText(getOrderDetailModel.addressVO.userName + "    "
                + getOrderDetailModel.addressVO.contactPhone);
        tvNewOrderAddress.setText(getOrderDetailModel.addressVO.provinceName + getOrderDetailModel.addressVO.cityName
                + getOrderDetailModel.addressVO.areaName + getOrderDetailModel.addressVO.detailAddress);
        tvNewOrderVipSubtractionPrice.setText(" ￥ " + getOrderDetailModel.vipReduct);
        tvNewOrderRemarks.setText(getOrderDetailModel.remark);
        examineTimeLinearLayout.setVisibility(orderStatusRequest == 11 ? View.VISIBLE : View.GONE);

        if (orderStatusRequest == 2) {
            mTvDriverDeliverTime.setText(getOrderDetailModel.payDate);
        } else if (orderStatusRequest == 14) {
            mTvDriverDeliverTime.setText(getOrderDetailModel.waitSendReceiveTime);
        } else if (orderStatusRequest == 3) {
            mTvDriverDeliverTime.setText(getOrderDetailModel.confirmDate);
        }

        if (orderStatusRequest == 2) {
            mTvOrderStatus.setText("等待接收订单");
        } else if (orderStatusRequest == 14) {
            mTvOrderStatus.setText("订单已接收");
        } else if (orderStatusRequest == 3) {
            mTvOrderStatus.setText("装车完成-已发货");
        }

        //订单状态
        if(info.data.orderStatus==1||info.data.orderStatus==7) {
            ll_payWay.setVisibility(View.GONE);
        }else {
            ll_payWay.setVisibility(View.VISIBLE);
        }

        //倒计时设置
        orderTimerView.SnapUpCountDownTimerViewType(mContext, 1);
        orderTimerView.setBackTheme(true);
        orderTimerView.setTime(true, getOrderDetailModel.sysCurrentTime, getOrderDetailModel.orderOverTime, 0);
        orderTimerView.changeTypeColor(Color.WHITE);
        orderTimerView.start();
        if (!TextUtils.isEmpty(getOrderDetailModel.payDate)) {// 付款时间
            tvNewOrderPayTime.setText(getOrderDetailModel.payDate);
            tvNewOrderPayTimeLinearLayout.setVisibility(View.VISIBLE);
        } else {
            tvNewOrderPayTimeLinearLayout.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(getOrderDetailModel.confirmDate)) {
            tvNewOrderReturnTime.setText(getOrderDetailModel.confirmDate);
            newOrderReturnTimeLinearLayout.setVisibility(View.VISIBLE);
        } else {
            newOrderReturnTimeLinearLayout.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(getOrderDetailModel.finishDate)) {
            tvExamineTime.setText(getOrderDetailModel.finishDate);
            examineTimeLinearLayout.setVisibility(View.VISIBLE);
        } else {
            examineTimeLinearLayout.setVisibility(View.GONE);

        }
        if (info.data.orderStatus == 1 || info.data.orderStatus == 2) {
            if (info.data.deliverTimeStart == null || info.data.deliverTimeEnd == null || !StringHelper.notEmptyAndNull(info.data.deliverTimeStart) || !StringHelper.notEmptyAndNull(info.data.deliverTimeEnd)) {
                tv_notify_time.setVisibility(View.GONE);
            } else {
                tv_notify_time.setVisibility(View.GONE);
            }
        }

    }

    //返回上个页面
    private void backEvent() {
        if(account.equals("0")) {
            finish();
        }else if(account.equals("2")){
            Intent intent = new Intent(mContext, MyOrdersActivity.class);
            intent.putExtra("type", AppConstant.PAYMENT);
            intent.putExtra("orderDeliveryType", 0);
            startActivity(intent);
            finish();
        }else if(account.equals("1")) {
            finish();
        }
    }

    // 不同的状态显示不同的layout

    /**
     * 订单类型：("待付款", 1), ("待发货-待接收", 2), ("待发货-已接收", 14), ("待收货", 3), ("已完成", 4), ("待评价", 5), ("已评价", 6), ("已取消", 7), ("退货", 11),
     */
    private void setViewShow() {
        //修改配送时间段
        //   tv_notify_time.setVisibility(orderStatusRequest == 1 || orderStatusRequest == 2 ? View.VISIBLE : View.GONE);
//评价
        mLinearLayoutEvalute.setVisibility(orderStatusRequest == 5 || orderStatusRequest == 6 ? View.VISIBLE : View.GONE);
        //待评价
        buttonEvaluate.setVisibility(orderStatusRequest == 5 ? View.VISIBLE : View.GONE);
        //查看评价
        tv_evaluate.setVisibility(orderStatusRequest == 6 ? View.VISIBLE : View.GONE);

        // 判是否出现退货的  orderStatusRequest=11头部 layout
        //textViewTitle.setText(orderStatusRequest == 11 ? "退货订单" : " 订单详情");

        ReturnGoodsLinearLayout.setVisibility(orderStatusRequest == 11 ? View.VISIBLE : View.GONE);
        ReturnGoodsTitle.setVisibility(orderStatusRequest == 11 ? View.VISIBLE : View.GONE);
        orderLinearLayout.setVisibility(orderStatusRequest == 11 ? View.GONE : View.VISIBLE);
        // 判是否出现退货的 底部 layout
        // returnGoodsInfoLayout.setVisibility(orderStatusRequest == 11 ? View.VISIBLE : View.GONE);
        // newOrderInfoLayout.setVisibility(orderStatusRequest == 11 ? View.GONE : View.VISIBLE);

        //待发货待接受
        mLinearLayoutShipped.setVisibility(orderStatusRequest == 2 ? View.VISIBLE : View.GONE);
        //待发货 已接收
        mLinearLayoutReciverOrder.setVisibility(orderStatusRequest == 14 ? View.VISIBLE : View.GONE);
        //待收货
        threeButtonLayout.setVisibility(orderStatusRequest == 3 ? View.VISIBLE : View.GONE);

//司机物流信息
        mRelativeDriver.setVisibility(orderStatusRequest == 2 || orderStatusRequest == 14 || orderStatusRequest == 3 ? View.VISIBLE : View.GONE);
        mTvSeeDriverStatus.setVisibility(orderStatusRequest == 3 ? View.VISIBLE : View.GONE);
        mTvDriverName.setVisibility(orderStatusRequest == 3 ? View.VISIBLE : View.GONE);
        tv_driver_phone.setVisibility(orderStatusRequest == 3 ? View.VISIBLE : View.GONE);

        //状态是待付款 或者是待支付 显示二个个按钮(取消订单 去支付) 其他状态需要不显示
        twoButtonLayout.setVisibility(orderStatusRequest == 1 ? View.VISIBLE : View.GONE);

        tvInfo.setVisibility(orderStatusRequest == 11 ? View.VISIBLE : View.GONE);
        //  状态是待付款 或者是待支付 显示倒计时 其他状态不需要显示
        linearLayoutAddressArrow.setVisibility(orderStatusRequest == 1 || orderStatusRequest == 2 ? View.VISIBLE : View.GONE);
        orderTimerView.setVisibility(orderStatusRequest == 1 ? View.VISIBLE : View.GONE);


        //  orderTimerView.setVisibility(orderStatusRequest == 1 ? View.VISIBLE : View.GONE);
        //状态是待付款 或者是待支付 不显示状态文案 其他状态需要显示
        tvOrderContent.setVisibility(orderStatusRequest == 1 ? View.GONE : View.VISIBLE);
        //状态是待付款 或者是待支付 orderStatusRequest=1 不显示三个按钮(退货 评价 再次购买) 其他状态需要显示


        //状态是待发货 已评价 不显示评价 其他状态
        // buttonEvaluate.setVisibility(orderStatusRequest == 2 || orderStatusRequest == 7 || orderStatusRequest == 6 ? View.GONE : View.VISIBLE);

        // buttonReturnGoods.setVisibility(orderStatusRequest == 7 ? View.GONE : View.VISIBLE);
        // buttonEvaluate.setText(orderStatusRequest == 5 ? "确认收货" : "评价");
        //状态是已取消  删除订单，再次购买显示，其它不显示
        //
        //  mRlReturn.setVisibility(orderStatusRequest == 11 ? View.VISIBLE : View.GONE);


        setOrderContent(getOrderDetailModel.orderStatus);

    }

    private void setOrderContent(int type) {
        switch (orderStatusRequest) {
            case 2:
                tvOrderContent.setText("我们将尽快发货，感谢您对翘歌的信任");
                break;
            case 3:
                tvOrderContent.setText("有问题请及时联系客服，客服电话" + getOrderDetailModel.customerPhone);
                break;
            case 4:
                tvOrderContent.setText("收到商品后，可以来评价试试");
                break;
            case 5:
                tvOrderContent.setText("收到商品后，可以来评价试试");
                break;  // 0审核 1 成功 2 失败
            case 7:
                tvOrderContent.setText("您的订单已取消");

                deleteButtonLayout.setVisibility(View.VISIBLE);
                threeButtonLayout.setVisibility(View.GONE);
                buttonAgainBuy.setVisibility(View.GONE);
                break;
            case 11:
                if (type == 0) {
                    ReturnGoodsContent.setText("不要着急，工作人员正在审核中");
                    relativeLayoutCommodityReturn.setVisibility(View.GONE);
                } else if (type == 1) {
                    ReturnGoodsContent.setText("欢迎再次使用翘歌烧烤");
                    relativeLayoutCommodityReturn.setVisibility(View.VISIBLE);
                } else {
//                    ReturnGoodsContent.setText(getOrderDetailModel.feedbackReson);
                    relativeLayoutCommodityReturn.setVisibility(View.GONE);
                }
                break;
            case 6:
                tvOrderContent.setText("感谢您的评价！欢迎您再次使用翘歌！");
                break;
            case 14:
                tvOrderContent.setText("我们将尽快发货，感谢您对翘歌的信任");
                break;
        }
    }

    //显示取消订单提示
    private void showCancleOrder() {
        mDialog = new AlertDialog.Builder(mContext).create();
        mDialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        mDialog.show();
        mDialog.getWindow().setContentView(R.layout.dialog_are_you_sure);
        TextView mTvTip = mDialog.getWindow().findViewById(R.id.tv_dialog_tip);
        mTvTip.setText("确认取消订单?");
        mDialog.getWindow().findViewById(R.id.tv_dialog_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.getWindow().findViewById(R.id.tv_dialog_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                lav_activity_loading.show();
                cancelOrder(orderId);
            }
        });
    }

    //取消订单
    private void cancelOrder(final String orderId) {
        CancelOrderAPI.requestData(mContext, orderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CancelOrderModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        lav_activity_loading.hide();
                    }

                    @Override
                    public void onNext(CancelOrderModel cancelOrderModel) {
                        if (cancelOrderModel.success) {
                            //取消成功
                            lav_activity_loading.hide();
                            AppHelper.showMsg(NewOrderDetailActivity.this, "取消订单成功");

                            getOrderDetail(orderId);
                        } else {
                            lav_activity_loading.hide();
                            AppHelper.showMsg(NewOrderDetailActivity.this, cancelOrderModel.message);
                        }
                    }
                });
    }

    //不能退货弹窗
    private void showCannotReturnDialog() {
        final android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(mContext, R.style.CommonDialogStyle).create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.dialog_can_not_return_reason);
        TextView mTvReason = (TextView) window.findViewById(R.id.tv_dialog_cannot_return_reason);
        Button mBtnConfirm = (Button) window.findViewById(R.id.btn_dialog_cannot_return_confirm);
        mTvReason.setText(getOrderDetailModel.cannotReturnGoodsMsg);
        mBtnConfirm.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    //立即评价
    private void requestEvaluate(List<CommonModel.DataBean> data, String orderId) {
        //去评价需要将订单里面的商品列表中的商品的商品名,商品ID组成list,传到评价的界面
        mListEvaluate.clear();
        if (data != null && data.size() > 0) {
            for (int i = 0; i < data.size(); i++) {
                mListEvaluate.add(new OrderEvaluateListModel(data.get(i).getProductId(),
                        data.get(i).getBusinessType(),
                        data.get(i).getName(), data.get(i).getPicUrl(), 5 + "", ""));
            }
        } else {
            AppHelper.showMsg(mContext, "订单商品数据错误!");
        }
        Intent intentPut = new Intent(mContext, OrderEvaluateActivity.class);
        intentPut.putExtra("evaluateList", (Serializable) mListEvaluate);
        intentPut.putExtra("orderId", orderId);
        intentPut.putExtra("orderDeliveryType", 0);
        startActivityForResult(intentPut, 12);

    }

    /**
     * 点击去评价
     * @param orderId
     */
    private void getComment(String orderId) {
        MyOrderListAPI.getComment(mActivity,orderId)
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

    // 再次购买
    private void requestCopyToCart() {
        CopyToCartAPI.requestCopyToCart(mContext, orderId)
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

                        if (copyToCartModel.success) {
                            //将订单内的商品加入购物车
                            AppHelper.showMsg(mContext, copyToCartModel.message);
                            startActivity(new Intent(mContext, HomeActivity.class));
                            EventBus.getDefault().post(new GoToCartFragmentEvent());

                        } else {
                            AppHelper.showMsg(mContext, copyToCartModel.message);
                        }
                    }
                });
    }

    // 确认收货
    private void requestConfirmGetGoods() {
        ConfirmGetGoodsAPI.reuqestConfirmGetGoods(mContext, orderId)
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
                        mModelConfirmGetGoods = confirmGetGoodsModel;
                        if (mModelConfirmGetGoods.success) {
                            //确认收货成功
                            AppHelper.showMsg(mContext, "确认收货成功");
                            //刷新订单状态
                            getOrderDetail(orderId);
                        } else {
                            AppHelper.showMsg(mContext, mModelConfirmGetGoods.message);
                        }
                    }
                });
    }

    private NoDoubleClickListener noDoubleClickListener = new NoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View view) {
            switch (view.getId()) {
                case R.id.imageViewBreak: //返回按钮
                    backEvent();
//                    finish();
                    break;


                case R.id.ll_beizhu:
                    Intent intents = new Intent(mActivity,BeizhuActivity.class);
                    intents.putExtra("beizhu",tv_beizhu.getText().toString()+"");
                    startActivity(intents);
                    break;
                    //关联订单
                case R.id.tv_connect_id:
                    getOrderDetail(tv_connect_id.getText().toString());
                    break;

                case R.id.buttonCancelOrder:// 取消订单
                    showCancleOrder();
                    break;
                case R.id.buttonGOPay://去支付
//                    Intent intent = new Intent(mContext, MyConfireOrdersActivity.class);
//                    intent.putExtra("remark", getOrderDetailModel.remark);
//                    // Log.i("wwb", "onNoDoubleClick: "+getOrderDetailModel.remark);
//                    intent.putExtra("payAmount", Double.parseDouble(getOrderDetailModel.totalAmount));
//                    intent.putExtra("orderId", orderId);
//                    intent.putExtra("flag", true);
//                    finish();
//                    startActivity(intent);
                    PaymentFragments paymentFragment = new PaymentFragments();
                    Bundle bundle = new Bundle();
                    bundle.putString("total", getOrderDetailModel.totalAmount);
                    bundle.putString("remark","");
                    bundle.putString("payAmount",getOrderDetailModel.totalAmount);
                    bundle.putString("orderId",orderId);
                    bundle.putString("orderDeliveryType","0");
                    paymentFragment.setArguments(bundle);
                    paymentFragment.setCancelable(false);
                    paymentFragment.show(getSupportFragmentManager(),"paymentFragment");
                    break;

                case R.id.buttonReturnGood_two:
                    if (getOrderDetailModel.canReturnGoods) {
                        Intent intent1 = new Intent(mContext, ReturnGoodActivity.class);
                        intent1.putExtra("orderStatus", orderStatusRequest + "");
                        intent1.putExtra("orderId", orderId);
                        intent1.putExtra("orderDeliveryType", 0);
                        startActivity(intent1);

                    } else {
                        //不能退货,弹框提示消息
                        showCannotReturnDialog();
                    }

                    break;
                case R.id.buttonReturnGoods: //退货

                    if (getOrderDetailModel.canReturnGoods) {
                        Intent intent1 = new Intent(mContext, ReturnGoodActivity.class);
                        intent1.putExtra("orderStatus", orderStatusRequest + "");
                        intent1.putExtra("orderId", orderId);
                        intent1.putExtra("orderDeliveryType", 0);
                        startActivity(intent1);

                    } else {
                        //不能退货,弹框提示消息
                        showCannotReturnDialog();
                    }

                    break;

                case R.id.tv_order_return:
                    if (getOrderDetailModel.canReturnGoods) {
                        Intent intent1 = new Intent(mContext, ReturnGoodActivity.class);
                        intent1.putExtra("orderStatus", orderStatusRequest + "");
                        intent1.putExtra("orderId", orderId);
                        intent1.putExtra("orderDeliveryType", 0);
                        startActivity(intent1);

                    } else {
                        //不能退货,弹框提示消息
                        showCannotReturnDialog();
                    }

                    break;

                case R.id.tv_confirm_order:

                    requestConfirmGetGoods();
                    break;
                case R.id.buttonEvaluate:
                    /*if (orderStatusRequest == 3) { // 确实收货
                        requestConfirmGetGoods();
                    } else {   // 立即评价
                        requestEvaluate();
                    }*/

                    if (orderStatusRequest == 5) {
                        getComment(orderId);
                    }
                    break;
                case R.id.buttonAgainBuy: // 再次购买
                    requestCopyToCart();
                    break;

                case R.id.tv_buttonAgainBuy:
                    requestCopyToCart();
                    break;
                case R.id.tv_delete:
                    deleteOrder();
                    break;
                case R.id.linearLayout_address_arrow:
                    Intent intent_ = new Intent(mContext, AddressListsActivity.class);

                    intent_.putExtra("orderId", orderId);
                    intent_.putExtra("type", 1);
                    intent_.putExtra("mineAddress", "address");
                    intent_.putExtra("UseAddress", getOrderDetailModel.addressVO.provinceName + getOrderDetailModel.addressVO.cityName
                            + getOrderDetailModel.addressVO.areaName + getOrderDetailModel.addressVO.detailAddress);
                    // startActivity(intent_);
                    startActivityForResult(intent_, 42);
                    break;

                case R.id.copy_order:
                    requestCopyToCart();
                    break;
                case R.id.tv_get_order_copy:
                    requestCopyToCart();

                    break;
                case R.id.tv_copy_order:
                    requestCopyToCart();
                    break;
                case R.id.iv_fresh_status:
                    LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(mContext)
                            .setMessage("获取数据中")
                            .setCancelable(false)

                            .setCancelOutside(true);
                    dialog = loadBuilder.create();
                    dialog.show();
                    getOrderDetailDialog(orderId);
                    break;

                //查看物流信息
                case R.id.tv_order_driver_message:

                    Intent intent1 = new Intent(mContext, MapOrderMessageActivity.class);
                    intent1.putExtra("orderId", orderId);
                    startActivity(intent1);
                    break;
                case R.id.tv_notify_time:
                    getDeliverTime();

                    break;
                case R.id.tv_evaluate:
                    Intent intent2 = new Intent(mContext, UserEvaluateActivity.class);
                    intent2.putExtra("orderId", orderId);
                    intent2.putExtra("orderDeliveryType", 0);
                    startActivity(intent2);


                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 39) {
            returnCode = resultCode;
            isShowed = false;
            orderId = data.getStringExtra("orderId");
            //   list.clear();
            //    getOrderDetail(orderId, orderState, returnProductMainId);

        }

    }

    /**
     * 获取时间段及修改配送时间段
     */

    private void getDeliverChangeTime() {
        GetDeliverTimeOrderAPI.requsetOrderTime(mActivity, orderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GetDeliverTimeModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(GetDeliverTimeModel getDeliverTimeModel) {

                        if (getDeliverTimeModel.success) {
                            if (getDeliverTimeModel.data != null) {

                                AlertDialog alertDialog = new AlertDialog.Builder(mContext, R.style.DialogStyle).create();
                                alertDialog.show();
                                alertDialog.setCancelable(false);
                                alertDialog.setContentView(R.layout.change_deliver_time);

                                TextView tv_ok = alertDialog.getWindow().findViewById(R.id.tv_ok);
                                tv_ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                        getDeliverTime();
                                    }
                                });


                            } else {
                                AlertDialog alertDialog1 = new AlertDialog.Builder(mContext, R.style.DialogStyle).create();
                                alertDialog1.show();
                                alertDialog1.setCancelable(false);
                                alertDialog1.setContentView(R.layout.change_deliver_time_cancel);

                                TextView tv_ok = alertDialog1.getWindow().findViewById(R.id.tv_ok);


                                tv_ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog1.dismiss();
                                        getOrderDetail(orderId);
                                    }
                                });

                            }
                        } else {
                            AppHelper.showMsg(mActivity, getDeliverTimeModel.message);
                        }


                    }
                });


    }

    /**
     * 获取时间段及修改配送时间段
     */

    private void getDeliverTime() {
        GetDeliverTimeOrderAPI.requsetOrderTime(mActivity, orderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GetDeliverTimeModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(GetDeliverTimeModel getDeliverTimeModel) {

                        if (getDeliverTimeModel.success) {
                            if (getDeliverTimeModel.data != null) {

                                mlist.clear();
                                try {
                                    JSONArray jsonArray = new JSONArray(getDeliverTimeModel.data);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        mlist.add(jsonObject.getString("name") + " " + jsonObject.getString("start") + "-" + jsonObject.getString("end"));

                                    }

//                                    PickCityUtil.showSinglePickViewTwo(mActivity, mlist, "请选择配送时间段", new PickCityUtil.ChoosePositionListener() {
//                                        @Override
//                                        public void choosePosition(int position, String s) {
//                                            try {
//                                                JSONObject jsonObjects = jsonArray.getJSONObject(position);
//                                                deliverTimeStart = jsonObjects.getString("start");
//                                                deliverTimeName = jsonObjects.getString("name");
//                                                deliverTimeEnd = jsonObjects.getString("end");
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//                                            notifyDeliverTime();
//                                        }
//                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                //   requestOrderNum();
                            }
                        } else {
                            AppHelper.showMsg(mActivity, getDeliverTimeModel.message);
                        }


                    }
                });


    }


    private void notifyDeliverTime() {
        NotifyDeliverTimeOrderAPI.notifyDeliverTime(mActivity, orderId, deliverTimeName, deliverTimeStart, deliverTimeEnd)
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
                            getOrderDetail(orderId);
                        } else {
                            AppHelper.showMsg(mContext, baseModel.message);
                        }
                    }
                });
    }


    public void deleteOrder() {
        DeleteOrderAPI.requestData(mContext, orderId)
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
                            AppHelper.showMsg(mContext, "删除订单成功");
                            startActivity(new Intent(mContext, MyOrdersActivity.class));
                        } else {
                            AppHelper.showMsg(mContext, cancelOrderModel.message);
                        }
                    }
                });
    }

/*

    protected void setTranslucentStatus() {
        // 5.0以上系统状态栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
*/

    @Override
    protected void onDestroy() {

        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }


}
