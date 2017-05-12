package com.ist.rylibrary.listener;

import android.view.View;

import com.ist.rylibrary.base.util.TimeUtil;

/**
 * Created by minyuchun on 2017/3/21.
 */

public abstract class OnRyClickListener implements View.OnClickListener{
    private OnClickListener mListener;
    @Override
    public void onClick(View view) {
        if(!TimeUtil.getInstance().isMultipleClick()){
            mListener.onClick(view);
        }
    }

    public OnRyClickListener(OnClickListener listener){
        this.mListener = listener;
    }

    public interface OnClickListener {
        void onClick(View view);
    }
}
