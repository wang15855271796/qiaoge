package com.puyue.www.qiaoge.model.mine.order;

import com.puyue.www.qiaoge.base.BaseModel;

/**
 * Created by ${王文博} on 2019/5/29
 */
public class ReturnOrderSucModel extends BaseModel {

    public int data;
    String allReturn;

    public String getAllReturn() {
        return allReturn;
    }

    public void setAllReturn(String allReturn) {
        this.allReturn = allReturn;
    }
}
