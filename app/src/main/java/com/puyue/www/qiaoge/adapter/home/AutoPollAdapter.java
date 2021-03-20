package com.puyue.www.qiaoge.adapter.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.puyue.www.qiaoge.R;
import com.puyue.www.qiaoge.activity.home.SpecialGoodDetailActivity;
import com.puyue.www.qiaoge.constant.AppConstant;
import com.puyue.www.qiaoge.helper.StringHelper;
import com.puyue.www.qiaoge.helper.UserInfoHelper;
import com.puyue.www.qiaoge.model.home.CouponModel;
import com.puyue.www.qiaoge.utils.SharedPreferencesUtil;

import java.util.List;

/**
 * Created by ${王涛} on 2020/8/18
 */
public class AutoPollAdapter extends BaseQuickAdapter<CouponModel.DataBean.ActivesBean,BaseViewHolder> {
    private ImageView iv_pic;
    private ImageView iv_add;
    private RelativeLayout rl_group;
    String flag;
    ImageView iv_flag;
    private TextView tv_old_price;
    private TextView tv_coupon;
    RelativeLayout rl_coupon;
    String style;
    public OnClick onClick;
    ImageView iv_sale_done;
    TextView tv_price;
    TextView tv_desc;
    List<CouponModel.DataBean.ActivesBean> data;
    public AutoPollAdapter(String style,int layoutResId, @Nullable List<CouponModel.DataBean.ActivesBean> data) {
        super(layoutResId, data);
        this.style = style;
        this.data = data;
    }


    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    public void setOnclick(OnClick onClick) {
        this.onClick = onClick;
    }

    @Override
    protected void convert(final BaseViewHolder helper, CouponModel.DataBean.ActivesBean item) {
//        CouponModel.DataBean.ActivesBean activesBean = data.get(helper.getAdapterPosition()%data.size());
//        iv_pic = helper.getView(R.id.iv_pic);
//        tv_price = helper.getView(R.id.tv_price);
//        tv_desc = helper.getView(R.id.tv_desc);
//        iv_sale_done = helper.getView(R.id.iv_sale_done);
//        iv_flag = helper.getView(R.id.iv_flag);
//        iv_add = helper.getView(R.id.iv_add);
//        tv_coupon = helper.getView(R.id.tv_coupon);
//        rl_coupon = helper.getView(R.id.rl_coupon);
//        rl_group = helper.getView(R.id.rl_group);
//        Glide.with(mContext).load(activesBean.getDefaultPic()).into(iv_pic);
//        helper.setText(R.id.tv_name,activesBean.getActiveName());
//        helper.setText(R.id.tv_price,activesBean.getPrice());
//        tv_old_price = helper.getView(R.id.tv_old_price);
//        tv_old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
//        helper.setText(R.id.tv_old_price,item.getOldPrice());
//        tv_old_price.getPaint().setAntiAlias(true);//抗锯齿
//        if(item.getDiscount()!=null) {
//            tv_coupon.setText(item.getDiscount());
//            rl_coupon.setVisibility(View.VISIBLE);
//        }else {
//            rl_coupon.setVisibility(View.GONE);
//        }
//
//        if(item.getFlag()==1) {
//            iv_sale_done.setVisibility(View.VISIBLE);
//            Glide.with(mContext).load(item.getSoldOutPic()).into(iv_sale_done);
//        }else {
//            iv_sale_done.setVisibility(View.GONE);
//        }
//
//        if(StringHelper.notEmptyAndNull(UserInfoHelper.getUserId(mContext))) {
//            if(SharedPreferencesUtil.getString(mContext,"priceType").equals("1")) {
//                tv_desc.setVisibility(View.GONE);
//                tv_old_price.setVisibility(View.VISIBLE);
//                tv_price.setVisibility(View.VISIBLE);
//            }else {
//                tv_desc.setVisibility(View.VISIBLE);
//                tv_old_price.setVisibility(View.GONE);
//                tv_price.setVisibility(View.GONE);
//            }
//        }else {
//            tv_desc.setVisibility(View.GONE);
//            tv_old_price.setVisibility(View.VISIBLE);
//            tv_price.setVisibility(View.VISIBLE);
//        }
//
//        rl_group.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(style.equals("2")) {
//                    Intent intent = new Intent(mContext,SeckillGoodActivity.class);
//                    intent.putExtra(AppConstant.ACTIVEID,item.getActiveId());
//                    intent.putExtra("priceType",SharedPreferencesUtil.getString(mContext,"priceType"));
//                    intent.putExtra("num","-1");
//                    mContext.startActivity(intent);
//                }else {
//                    Intent intent = new Intent(mContext,SpecialGoodDetailActivity.class);
//                    intent.putExtra(AppConstant.ACTIVEID,item.getActiveId());
//                    intent.putExtra("priceType",SharedPreferencesUtil.getString(mContext,"priceType"));
//                    mContext.startActivity(intent);
//                }
//            }
//        });
//
//        iv_add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(onClick!=null) {
////                    if(SharedPreferencesUtil.getString(mContext,"priceType").equals("1")) {
////                        onClick.shoppingCartOnClick(helper.getAdapterPosition());
////                    }else {
////                        onClick.tipClick();
////                    }
////                    onClick.shoppingCartOnClick(helper.getAdapterPosition());
////                    onClick.tipClick();
//                    if(StringHelper.notEmptyAndNull(UserInfoHelper.getUserId(mContext))) {
//                        if(SharedPreferencesUtil.getString(mContext,"priceType").equals("1")) {
//                            onClick.shoppingCartOnClick(helper.getAdapterPosition());
//                        }else {
//                            onClick.tipClick();
//                        }
//                    }else {
//                        onClick.addDialog();
//                    }
//
//
//                }
//            }
//        });
    }

    public interface OnClick {
        void shoppingCartOnClick(int position);
        void tipClick();
        void addDialog();
    }

}
