package com.ist.rylibrary.base.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ist.rylibrary.R;

/**
 * Created by mxy
 * on 2017/4/19.
 * 加载框
 */
public class ProgressDialog extends AlertDialog{
    private View layout;
    private TextView textMessage;
    private Context context;
    private boolean rejectClosed;

    public ProgressDialog(Context context) {
        super(context, R.style.progress_dialog);
        this.context = context;
        show();
        layout = LayoutInflater.from(context).inflate(R.layout.progress,
                null);

        textMessage = (TextView) layout.findViewById(R.id.text_message);
        getWindow().setContentView(layout);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setOnDismissListener(new OnDismissListener() {

            public void onDismiss(DialogInterface dialog) {
                textMessage = null;
                layout = null;
            }
        });

    }

    /**
     * 是否拒绝被关闭
     *
     * @param context
     * @param rejectClosed
     */
    public ProgressDialog(Context context, boolean rejectClosed) {
        this(context);
        this.rejectClosed = rejectClosed;
    }

    /*
     * 8 拒绝关闭
     */
    public void rejectClosed() {
        rejectClosed = true;
    }

    public void setMessage(CharSequence message) {
        super.setMessage(message);
        textMessage.setText(message);

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (rejectClosed) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {

                return true;
            } else if (keyCode == KeyEvent.KEYCODE_HOME) {

                return true;
            }
        } else {
            if (keyCode == KeyEvent.KEYCODE_BACK) {

                return true;
            } else if (keyCode == KeyEvent.KEYCODE_HOME) {

                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
