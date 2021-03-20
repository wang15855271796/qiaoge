package com.puyue.www.qiaoge.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.puyue.www.qiaoge.R;
import com.puyue.www.qiaoge.model.home.AddressBean;
import com.puyue.www.qiaoge.model.home.CityChangeModel;
import com.puyue.www.qiaoge.view.MenuItemAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${王涛} on 2021/1/6
 */
public class MenuItemsAdapter extends BaseAdapter {
    //上下文
    private Context mContext;
    //菜单列表
    private List<CityChangeModel.DataBean> mListData;
    private int selectedPos = -1;
    private String selectedText = "";
    private int normalDrawbleId;
    private Drawable selectedDrawble;
    private float textSize;
    private View.OnClickListener onClickListener;
    private MenuItemAdapter.OnItemClickListener mOnItemClickListener;

    public MenuItemsAdapter(Context context, List<CityChangeModel.DataBean> listData, int sId, int nId) {
        mContext = context;
        mListData = listData;
        selectedDrawble = mContext.getResources().getDrawable(sId);
        normalDrawbleId = nId;
        init();
    }

    private void init() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPos = (Integer) view.getTag();
                setSelectedPosition(selectedPos);
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, selectedPos);
                }
            }
        };
    }
    /**
     * 设置选中的position,并通知刷新其它列表
     */
    public void setSelectedPosition(int pos) {
        if (mListData != null && pos < mListData.size()) {
            selectedPos = pos;
            selectedText = mListData.get(pos).getProvinceName();
            notifyDataSetChanged();

        }
    }

    /**
     * 设置选中的position,但不通知刷新
     */
    public void setSelectedPositionNoNotify(int pos,List<CityChangeModel.DataBean> list) {
        selectedPos = pos;
        mListData = list;
        if (mListData != null && pos < mListData.size()) {
            selectedText = mListData.get(pos).getProvinceName();
        }
    }

    /**
     * 获取选中的position
     */
    public int getSelectedPosition() {

        if (mListData != null && selectedPos < mListData.size()) {
            return selectedPos;
        }

        return -1;
    }

    /**
     * 设置列表字体大小
     */
    public void setTextSize(float tSize) {
        textSize = tSize;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView view;

        if (convertView == null) {
            view = (TextView) LayoutInflater.from(mContext).inflate(R.layout.choose_item, parent, false);

        } else {
            view = (TextView) convertView;
        }
        view.setTag(position);
        String mString = "";
        if (mListData != null) {
            if (position < mListData.size()) {
                mString = mListData.get(position).getProvinceName();
            }
        }
        if (mString.contains("不限"))
            view.setText("不限");
        else
            view.setText(mString);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);

        if (selectedText != null && selectedText.equals(mString)) {
//            view.setBackgroundDrawable(selectedDrawble);//设置选中的背景图�?/
            view.setBackgroundColor(mContext.getResources().getColor(R.color.color_f6));
        } else {
//            view.setBackgroundDrawable(mContext.getResources().getDrawable(normalDrawbleId));//设置未�?中状态背景图�?
            view.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }
        view.setPadding(20, 0, 0, 0);
        view.setOnClickListener(onClickListener);
        return view;
    }

    public void setOnItemClickListener(MenuItemAdapter.OnItemClickListener l) {
        mOnItemClickListener = l;
    }

//    public void setSelectsedPositionNoNotify(int pos, List<CityChangeModel.DataBean.CityNamesBean> list) {
//        selectedPos = pos;
//        mListData = list;
//        if (mListData != null && pos < mListData.size()) {
//            selectedText = mListData.get(pos).getProvinceName();
//        }
//    }

    /**
     * 重新定义菜单选项单击接口
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public Object getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
