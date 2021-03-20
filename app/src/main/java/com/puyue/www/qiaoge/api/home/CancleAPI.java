package com.puyue.www.qiaoge.api.home;

import android.content.Context;

import com.puyue.www.qiaoge.api.PostLoadAmountAPI;
import com.puyue.www.qiaoge.base.BaseModel;
import com.puyue.www.qiaoge.constant.AppInterfaceAddress;
import com.puyue.www.qiaoge.helper.RestHelper;
import com.puyue.www.qiaoge.model.CancleModel;
import com.puyue.www.qiaoge.model.CancleReasonModel;
import com.puyue.www.qiaoge.model.ExCouponModel;
import com.puyue.www.qiaoge.model.HotKeyModel;
import com.puyue.www.qiaoge.model.home.ClickCollectionModel;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by ${王涛} on 2020/6/4
 */
public class CancleAPI {
    private interface CancleService {

    @FormUrlEncoded
    @POST(AppInterfaceAddress.Is_Cancle)
    Observable<CancleModel> getData(@Field("cancelReason") String cancelReason);
}

    public static Observable<CancleModel> requestData(Context context,String cancelReason) {
        CancleService service = RestHelper.getBaseRetrofit(context).create(CancleService.class);
        return service.getData(cancelReason);
    }


    /**
     * 注销原因
     */
    private interface CancleReasonService {
        @FormUrlEncoded
        @POST(AppInterfaceAddress.Cancle_Reason)
        Observable<CancleReasonModel> getData(@Field("sysKey") String sysKey);
    }

    public static Observable<CancleReasonModel> getList(Context context, String sysKey) {
        CancleReasonService service = RestHelper.getBaseRetrofit(context).create(CancleReasonService.class);
        return service.getData(sysKey);
    }

    /**
     * 注销原因
     */
    private interface HotService {
        @FormUrlEncoded
        @POST(AppInterfaceAddress.Hot_Key)
        Observable<HotKeyModel> getData(@Field("searchKey") String searchKey);
    }

    public static Observable<HotKeyModel> getHot(Context context, String searchKey) {
        HotService service = RestHelper.getBaseRetrofit(context).create(HotService.class);
        return service.getData(searchKey);
    }

    /**
     * 余额兑换优惠券
     */
    private interface ExService {
        @FormUrlEncoded
        @POST(AppInterfaceAddress.Ex_Coupon)
        Observable<ExCouponModel> getData(@Field("giftAmounts") String giftAmounts, @Field("payPwd") String payPwd);
    }

    public static Observable<ExCouponModel> getCopon(Context context, String giftAmounts,String payPwd) {
        ExService service = RestHelper.getBaseRetrofit(context).create(ExService.class);
        return service.getData(giftAmounts,payPwd);
    }

}
