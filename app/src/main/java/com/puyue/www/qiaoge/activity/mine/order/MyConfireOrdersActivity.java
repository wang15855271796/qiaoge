package com.puyue.www.qiaoge.activity.mine.order;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.bumptech.glide.Glide;
import com.chinaums.pppay.unify.UnifyPayPlugin;
import com.chinaums.pppay.unify.UnifyPayRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.puyue.www.qiaoge.QiaoGeApplication;
import com.puyue.www.qiaoge.R;
import com.puyue.www.qiaoge.activity.HomeActivity;

import com.puyue.www.qiaoge.activity.cart.PayResultActivity;
import com.puyue.www.qiaoge.activity.mine.account.EditPasswordInputCodeActivity;

import com.puyue.www.qiaoge.activity.mine.account.PayActivity;
import com.puyue.www.qiaoge.api.cart.CheckPayPwdAPI;
import com.puyue.www.qiaoge.api.cart.GetPayResultAPI;
import com.puyue.www.qiaoge.api.cart.OrderPayAPI;
import com.puyue.www.qiaoge.api.mine.AccountCenterAPI;
import com.puyue.www.qiaoge.api.mine.GetWalletAmountAPI;
import com.puyue.www.qiaoge.api.mine.order.GenerateOrderAPI;
import com.puyue.www.qiaoge.api.mine.order.MyOrderNumAPI;
import com.puyue.www.qiaoge.base.BaseSwipeActivity;
import com.puyue.www.qiaoge.constant.AppConstant;
import com.puyue.www.qiaoge.dialog.YueDialog;
import com.puyue.www.qiaoge.event.BackEvent;
import com.puyue.www.qiaoge.event.GoToCartFragmentEvent;
import com.puyue.www.qiaoge.event.GoToMineEvent;
import com.puyue.www.qiaoge.event.WeChatPayEvent;
import com.puyue.www.qiaoge.event.WeChatUnPayEvent;
import com.puyue.www.qiaoge.helper.AppHelper;
import com.puyue.www.qiaoge.helper.FVHelper;
import com.puyue.www.qiaoge.helper.UserInfoHelper;
import com.puyue.www.qiaoge.listener.NoDoubleClickListener;
import com.puyue.www.qiaoge.model.cart.CheckPayPwdModel;
import com.puyue.www.qiaoge.model.cart.GetPayResultModel;
import com.puyue.www.qiaoge.model.cart.OrderPayModel;
import com.puyue.www.qiaoge.model.mine.AccountCenterModel;
import com.puyue.www.qiaoge.model.mine.GetWalletAmountModel;
import com.puyue.www.qiaoge.model.mine.order.GenerateOrderModel;
import com.puyue.www.qiaoge.model.mine.order.MyOrderNumModel;
import com.puyue.www.qiaoge.utils.SharedPreferencesUtil;
import com.puyue.www.qiaoge.utils.ToastUtil;


import com.rrtx.tzpaylib.CashierManager;
import com.rrtx.tzpaylib.PaymentCallback;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.umeng.socialize.utils.ContextUtil.getContext;

/**
 * @author daff
 * @date 2018/9/24.
 * 备注  订单支付页面
 */
public class MyConfireOrdersActivity extends BaseSwipeActivity {
    private ImageView imageViewBack;
    private ImageView okPay;
    private AVLoadingIndicatorView lav_activity_loading;
    private String remark;
    private double payAmount;

    private ImageView balancPay;
    private ImageView aliPay;
    private ImageView wechatPay;


    private String orderId;
    private String outTradeNo;
    private byte payChannel;
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    private TextView textViewAmount;
    private String num;
    private String mUserCell;
    private Handler handler = new Handler();
    boolean flag = false;
    private int orderDeliveryType;
    YueDialog yueDialog;
    ImageView iv_yun;
    @Override
    public boolean handleExtra(Bundle savedInstanceState) {
        return false;
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_my_confrim_order);

    }

    @Override
    public void findViewById() {
        iv_yun = (ImageView) findViewById(R.id.iv_yun);
        lav_activity_loading = (AVLoadingIndicatorView) findViewById(R.id.lav_activity_loading);
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        okPay = (ImageView) findViewById(R.id.okPay);
        balancPay = FVHelper.fv(this, R.id.rb_activity_order_balance);
        aliPay = FVHelper.fv(this, R.id.rb_activity_order_alipay);
        wechatPay = FVHelper.fv(this, R.id.rb_activity_order_wechat);
        textViewAmount = FVHelper.fv(this, R.id.textViewAmount);
        getWalletAmount();
    }

    @Override
    public void setViewData() {
        EventBus.getDefault().register(this);
        orderId = getIntent().getStringExtra("orderId");
        payAmount = getIntent().getDoubleExtra("payAmount", 0.00);
        remark = getIntent().getStringExtra("remark");
        orderDeliveryType = getIntent().getIntExtra("orderDeliveryType", 0);

        UserInfoHelper.saveOrderId(mContext, orderId);
        UserInfoHelper.saveRemark(mContext, remark);
        UserInfoHelper.saveDeliverType(mContext,orderDeliveryType+"");
        UserInfoHelper.saveOrderAmount(mContext, String.valueOf(payAmount));



    }

    @Override
    public void setClickEvent() {
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orderDeliveryType==0){
                    Intent intent = new Intent(mContext, NewOrderDetailActivity.class);
                    intent.putExtra("orderDeliveryType", orderDeliveryType);
                    intent.setClass(mContext, NewOrderDetailActivity.class);
                    intent.putExtra("orderId",orderId);
                    intent.putExtra(AppConstant.ORDERSTATE, "1");
                    startActivity(intent);
                }else if (orderDeliveryType==1){
                    Intent intent = new Intent(mContext, SelfSufficiencyOrderDetailActivity.class);
                    intent.putExtra("orderDeliveryType", orderDeliveryType);
                    intent.setClass(mContext, SelfSufficiencyOrderDetailActivity.class);
                    intent.putExtra("orderId",orderId);
                    intent.putExtra(AppConstant.ORDERSTATE, "1");
                    startActivity(intent);
                }
                finish();
            }
        });

        okPay.setOnClickListener(noDoubleClickListener);
        balancPay.setOnClickListener(noDoubleClickListener);
        aliPay.setOnClickListener(noDoubleClickListener);
        wechatPay.setOnClickListener(noDoubleClickListener);
        iv_yun.setOnClickListener(noDoubleClickListener);
    }

    private NoDoubleClickListener noDoubleClickListener = new NoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View view) {
            switch (view.getId()) {
                case R.id.rb_activity_order_balance: // 余额支付
                    payChannel = 1;
                    balancPay.setImageResource(R.mipmap.ic_pay_ok);
                    aliPay.setImageResource(R.mipmap.ic_pay_no);
                    wechatPay.setImageResource(R.mipmap.ic_pay_no);
                    iv_yun.setImageResource(R.mipmap.ic_pay_no);

                    break;
                case R.id.rb_activity_order_alipay: //支付宝支付
                    payChannel = 2;
                    aliPay.setImageResource(R.mipmap.ic_pay_ok);
                    balancPay.setImageResource(R.mipmap.ic_pay_no);
                    wechatPay.setImageResource(R.mipmap.ic_pay_no);
                    iv_yun.setImageResource(R.mipmap.ic_pay_no);
                    break;
                case R.id.rb_activity_order_wechat: //微信支付
                    payChannel = 3;
                    wechatPay.setImageResource(R.mipmap.ic_pay_ok);
                    aliPay.setImageResource(R.mipmap.ic_pay_no);
                    balancPay.setImageResource(R.mipmap.ic_pay_no);
                    iv_yun.setImageResource(R.mipmap.ic_pay_no);
                    break;
                    case R.id.iv_yun:
                        payChannel = 16;
                        wechatPay.setImageResource(R.mipmap.ic_pay_no);
                        aliPay.setImageResource(R.mipmap.ic_pay_no);
                        balancPay.setImageResource(R.mipmap.ic_pay_no);
                        iv_yun.setImageResource(R.mipmap.ic_pay_ok);
                        break;
                case R.id.okPay:
                    if (payChannel == 0) {
                        AppHelper.showMsg(mContext, "请选择支付方式");
                        return;
                    } else {
                        lav_activity_loading.setVisibility(View.VISIBLE);
                        lav_activity_loading.show();
                        if (payChannel == 1) {
                            String userWalletAccount = UserInfoHelper.getUserWalletAccount(mContext);
                            if(payAmount > Double.parseDouble(userWalletAccount)) {
                                yueDialog = new YueDialog(mActivity,userWalletAccount) {
                                    @Override
                                    public void close() {
                                        lav_activity_loading.hide();
                                        lav_activity_loading.setVisibility(View.GONE);
                                        dismiss();
                                    }
                                };
                                yueDialog.show();
                            }

                            Log.i("account", "onNoDoubleClick: " + payAmount + "____" + Double.parseDouble(userWalletAccount));
                        }
                    }
                    //调支付接口
                    //将这个按钮设置为false,失去焦点，只有在确切的事件之后，我们才会将这个状态重置回来。这样就写好了。
                    okPay.setEnabled(false);
                    orderPay(orderId, payChannel, payAmount, remark);

                    break;


            }
        }
    };

    // 支付
    private void orderPay(final String orderId, final byte payChannel, double payAmount, String remark) {
        OrderPayAPI.requestData(mContext, orderId, payChannel, payAmount, remark)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<OrderPayModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(OrderPayModel orderPayModel) {
                        okPay.setEnabled(true);
                        if (orderPayModel.success) {
                            outTradeNo = orderPayModel.data.outTradeNo;
                            String orderNoList = orderPayModel.data.orderNoList;
                            String businessCstNo = orderPayModel.data.businessCstNo;
                            String merchantNo = orderPayModel.data.merchantNo;
                            UserInfoHelper.saveWalletStatus(mContext, outTradeNo);

                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("orderFlowNo",orderNoList);
                                jsonObject.put("businessCstNo",businessCstNo);
                                jsonObject.put("platMerCstNo",merchantNo);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //台州银行
                            if(orderPayModel.data.payType==16) {
                                CashierManager.getInstance().init(MyConfireOrdersActivity.this);
                                CashierManager.getInstance().launchPayment(jsonObject.toString(), new PaymentCallback() {
                                    @Override
                                    public void paymentResult(String s) {
                                        switch (s){
                                            case "10":
                                                //初始状态
                                                break;

                                            case "70":
                                                //失败
                                                getPayResult(outTradeNo);
                                                break;

                                            case "80":
                                                //关闭
                                                ToastUtil.showSuccessMsg(mContext,"支付通道关闭");
                                                break;

                                            case "90":
                                                //成功
                                                getPayResult(outTradeNo);
                                                break;
                                        }
                                    }
                                });
                            }

                            if (payChannel == 1) {
                                //余额支付
                                //ok
                                SharedPreferencesUtil.saveString(mContext,"payKey","1");
                                accountCenter();
                                okPay.setEnabled(true);

                            } else if (payChannel == 2&&orderPayModel.data.payType==2) {
                                //支付宝支付 已经改好了
                                SharedPreferencesUtil.saveString(mContext,"payKey","2");
                                aliPay(orderPayModel.data.payToken);
                            } else if (payChannel == 3) {
                                //微信支付
                                SharedPreferencesUtil.saveString(mContext,"payKey","3");
                                weChatPay(orderPayModel.data.payToken);

                            }else if(orderPayModel.data.payType==14&&payChannel == 2) {
                                //银联
                                SharedPreferencesUtil.saveString(mContext,"payKey","4");
                                payAliPay(orderPayModel.data.payToken);
                            }

                        } else {
                            //ok
                            okPay.setEnabled(true);
                        }
                    }
                });
    }


    /**
     * 支付宝
     * @param parms
     */

    private void payAliPay(String parms){
        UnifyPayRequest msg = new UnifyPayRequest();
        msg.payChannel = UnifyPayRequest.CHANNEL_ALIPAY;
        msg.payData = parms;
        UnifyPayPlugin.getInstance(this).sendPayRequest(msg);
        lav_activity_loading.setVisibility(View.GONE);
        lav_activity_loading.hide();
    }




    @Override
    protected void onResume() {
        super.onResume();
        getWalletAmount();
        if(outTradeNo!=null&&SharedPreferencesUtil.getString(mContext,"payKey").equals("4")) {
            getPayResult(outTradeNo);
        }
    }

    private AlertDialog mDialog;


    /**
     * 获取用户支付密码的状态
     */
    private void accountCenter() {
        AccountCenterAPI.requestAccountCenter(mContext)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AccountCenterModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(AccountCenterModel accountCenterModel) {
                        logoutAndToHome(mContext, accountCenterModel.code);
                        if (accountCenterModel.success) {
                            mUserCell = accountCenterModel.data.phone;
                            lav_activity_loading.setVisibility(View.GONE);
                            lav_activity_loading.hide();
                            if (accountCenterModel.data.hasSetPayPwd) {
                                showInputPwdDialog();
                            } else {
                                showGoSetDialog();
                            }
                        } else {
                            AppHelper.showMsg(mContext, accountCenterModel.message);

                        }
                    }
                });
    }

    /**
     * 显示设置支付密码弹窗
     */
    private void showGoSetDialog() {
        mDialog = new AlertDialog.Builder(mContext).create();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        mDialog.getWindow().setContentView(R.layout.dialog_not_set_paypwd);
        mDialog.getWindow().findViewById(R.id.tv_dialog_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
//                mLavLoading.setVisibility(View.GONE);
                //   mIvError.setVisibility(View.VISIBLE);
                //  mTvState.setText("取消支付");
            }
        });
        mDialog.getWindow().findViewById(R.id.tv_dialog_goset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfoHelper.saveDeliverType(mContext,orderDeliveryType+"");
                UserInfoHelper.saveForgetPas(mContext, "wwwe");
//                startActivity(EditPasswordInputCodeActivity.getIntent(mContext, EditPasswordInputCodeActivity.class, "0", mUserCell, "pay","forgetPsw",orderDeliveryType,payAmount));
                Intent intent = new Intent(getContext(),PayActivity.class);
                intent.putExtra("phone",mUserCell);
                getContext().startActivity(intent);
                mDialog.dismiss();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //     mIvError.setVisibility(View.VISIBLE);
                        //    mTvState.setText("取消支付");
                    }
                }, 1000);
            }
        });
    }


    /**
     * 显示输入支付密码弹窗
     */
    private void showInputPwdDialog() {
        mDialog = new AlertDialog.Builder(mContext).create();
        mDialog.setView(new EditText(mContext));
        mDialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        mDialog.show();
        mDialog.getWindow().setContentView(R.layout.dialog_input_pwd);
        final EditText mEtPwd = mDialog.getWindow().findViewById(R.id.et_dialog_paypwd);
        mDialog.getWindow().findViewById(R.id.tv_dialog_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mContext, EditPasswordInputCodeActivity.class);


                startActivity(EditPasswordInputCodeActivity.getIntent(mContext, EditPasswordInputCodeActivity.class, "1", mUserCell, "pay","forgetPsw",orderDeliveryType,payAmount));
                UserInfoHelper.saveForgetPas(mContext, "wwwe");
                UserInfoHelper.saveDeliverType(mContext,orderDeliveryType+"");
                mDialog.dismiss();
//                mLavLoading.setVisibility(View.GONE);

            }
        });
        mDialog.getWindow().findViewById(R.id.iv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.getWindow().findViewById(R.id.tv_dialog_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mEtPwd.getText().toString())) {
                    AppHelper.showMsg(mContext, "请输入交易密码");
                } else {
                    mDialog.dismiss();
                    aliPay.setEnabled(false);
                    wechatPay.setEnabled(false);
                    okPay.setEnabled(false);
                    checkPayPwd(outTradeNo, mEtPwd.getText().toString());

                }
            }
        });
    }

    /**
     * 校验支付密码
     */
    private void checkPayPwd(final String outTradeNo, String passWord) {
        CheckPayPwdAPI.requestData(mContext, outTradeNo, passWord)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CheckPayPwdModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(CheckPayPwdModel checkPayPwdModel) {
                        if (checkPayPwdModel.success) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {

                                    getPayResult(outTradeNo);

                                }
                            }, 500);
                        } else {
//                            mLavLoading.setVisibility(View.GONE);
//                            tryRecycleAnimationDrawable(animationDrawable);
                            AppHelper.showMsg(mContext, checkPayPwdModel.message);
                            balancPay.setEnabled(true);
                            wechatPay.setEnabled(true);
                            aliPay.setEnabled(true);
                            okPay.setEnabled(true);


                        }
                    }
                });
    }

    /**
     * 获取支付结果
     */
    private void getPayResult(String outTradeNo) {
        GetPayResultAPI.requestData(mContext, outTradeNo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GetPayResultModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(GetPayResultModel getPayResultModel) {
                        logoutAndToHome(mContext, getPayResultModel.getCode());
                        if (getPayResultModel.isSuccess()) {
                            if(getPayResultModel.getData()!=null) {
                                Intent intent = new Intent(mContext, PayResultActivity.class);
                                intent.putExtra(AppConstant.PAYCHANNAL, payChannel);
                                intent.putExtra(AppConstant.OUTTRADENO, outTradeNo);
                                intent.putExtra(AppConstant.ORDERID, orderId);
                                intent.putExtra(AppConstant.ORDERDELIVERYTYPE, orderDeliveryType+"");
                                startActivity(intent);
                                finish();
                                mDialog.dismiss();


                            }

                        } else {
                            AppHelper.showMsg(mContext, getPayResultModel.getMessage());

                        }
                    }
                });
    }


    /**
     * 获取账户余额
     */
    private void getWalletAmount() {
        GetWalletAmountAPI.requestData(mContext)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GetWalletAmountModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(GetWalletAmountModel getWalletAmountModel) {
                        if (getWalletAmountModel.success) {
                            textViewAmount.setText("(" + getWalletAmountModel.data + ")");
                            UserInfoHelper.saveUserWallet(mContext, getWalletAmountModel.data);


                        } else {
                            AppHelper.showMsg(mContext, getWalletAmountModel.message);
                        }

                    }
                });
    }

    //------------------支付逻辑-------------------------------------------//

    /**
     * 微信支付
     */

    private void weChatPay(String json) {
        SharedPreferencesUtil.saveString(mContext,"pays","0");
        try {
            IWXAPI api = WXAPIFactory.createWXAPI(this, "wxbc18d7b8fee86977");
            JSONObject obj = new JSONObject(json);
            PayReq request = new PayReq();
            request.appId = obj.optString("appId");
            request.partnerId = obj.optString("mchID");
            request.prepayId = obj.optString("prepayId");
            request.packageValue = obj.optString("pkg");
            request.nonceStr = obj.optString("nonceStr");
            request.timeStamp = obj.optString("timeStamp");
            request.sign = obj.optString("paySign");
            api.sendReq(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
        lav_activity_loading.setVisibility(View.GONE);
        lav_activity_loading.hide();
        Log.d("dwdsssss.......","00000");
    }

    /**
     * 微信支付的回调,使用的eventBus.......((‵□′))
     **/
    @Subscribe
    public void onEventMainThread(WeChatPayEvent event) {
        //okpay
        Log.d("dwdsssssss......","00000");
        okPay.setEnabled(true);
        Intent intent = new Intent(mContext, PayResultActivity.class);
        intent.putExtra(AppConstant.PAYCHANNAL, payChannel);
        intent.putExtra(AppConstant.OUTTRADENO, outTradeNo);
        intent.putExtra(AppConstant.ORDERID, orderId);
        intent.putExtra(AppConstant.ORDERDELIVERYTYPE, orderDeliveryType+"");
        startActivity(intent);
        finish();

    }

    /**
     * 微信支付用户已取消支付的回调,使用的eventBus.......((‵□′))
     **/
    @Subscribe
    public void onEventMainThread(WeChatUnPayEvent event) {
        okPay.setEnabled(true);
    }

    /**
     * 支付宝支付
     */
    private void aliPay(final String orderInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(MyConfireOrdersActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
                //设置支付调用
            }
        };
        lav_activity_loading.setVisibility(View.GONE);
        lav_activity_loading.hide();
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 支付宝支付结果
     */
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    Map<String, String> result = (Map<String, String>) msg.obj;
                    Log.e("TGA", result.get("resultStatus") + "");
                    if ("9000".equals(result.get("resultStatus"))) {
                        //okpay
                        okPay.setEnabled(true);
                        //支付成功
                        Intent intent = new Intent(mContext, PayResultActivity.class);
                        intent.putExtra(AppConstant.PAYCHANNAL, payChannel);
                        intent.putExtra(AppConstant.OUTTRADENO, outTradeNo);
                        intent.putExtra(AppConstant.ORDERDELIVERYTYPE, orderDeliveryType + "");

                        intent.putExtra(AppConstant.ORDERID, orderId);
                        startActivity(intent);
                        finish();
                    } else if ("6001".equals(result.get("resultStatus"))) {
                        //用户取消支付
                        AppHelper.showMsg(mContext, "您已取消支付");
                        okPay.setEnabled(true);
                    } else if ("6002".equals(result.get("resultStatus"))) {
                        //网络连接错误
                        AppHelper.showMsg(mContext, "网络连接错误");
                        okPay.setEnabled(true);
                    } else {
                        //okpay
                        okPay.setEnabled(true);
                        //支付失败
                        Intent intent = new Intent(mContext, PayResultActivity.class);
                        intent.putExtra(AppConstant.PAYCHANNAL, payChannel);
                        intent.putExtra(AppConstant.OUTTRADENO, outTradeNo);
                        intent.putExtra(AppConstant.ORDERID, orderId);
                        intent.putExtra(AppConstant.ORDERDELIVERYTYPE, orderDeliveryType + "");


                        startActivity(intent);
                        finish();
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
                    break;
                }
                default:
                    break;
            }
        }


    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Intent intent = new Intent(mContext, NewOrderDetailActivity.class);
//        intent.putExtra(AppConstant.ORDERID, orderId);
//        intent.putExtra(AppConstant.ORDERSTATE, "");
//        intent.putExtra(AppConstant.RETURNPRODUCTMAINID, "");
//        startActivity(intent);
        startActivity(MyOrdersActivity.getIntent(mContext, MyOrdersActivity.class, AppConstant.ALL));

        finish();
        return true;
    }

}
