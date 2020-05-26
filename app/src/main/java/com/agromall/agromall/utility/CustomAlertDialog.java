package com.agromall.agromall.utility;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.agromall.agromall.R;
import com.agromall.agromall.interfaces.OnCustomDialogClickListener;

/**
 * Created by Wisdom Emenike.
 * Date: 4/15/2019
 * Time: 4:34 PM
 */

public class CustomAlertDialog extends Dialog {

    private final TextView dialogTitle;
    private final TextView dialogMessage;
    private final Button dialogBtnPositive;
    private final Button dialogBtnNegative;
    private final Button dialogBtnNeutral;
    private final LinearLayout dialogView;

    public CustomAlertDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_alert_dialog);
        dialogView = findViewById(R.id.custom_dialog_view);
        dialogTitle = findViewById(R.id.dialog_title);
        dialogMessage = findViewById(R.id.dialog_message);
        dialogBtnPositive = findViewById(R.id.btn_positive);
        dialogBtnNegative = findViewById(R.id.btn_negative);
        dialogBtnNeutral = findViewById(R.id.btn_neutral);
        Window window = getWindow();
        assert window != null;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        window.getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    public void setTitle(@Nullable CharSequence title) {
        dialogTitle.setVisibility(View.VISIBLE);
        dialogTitle.setText(title);
    }

    public void setMessage(@Nullable CharSequence message) {
        dialogMessage.setVisibility(View.VISIBLE);
        dialogMessage.setText(message);
    }

    public void setCancelable(boolean cancelable) {
        setCanceledOnTouchOutside(cancelable);
    }

    public void setPositiveButton(CharSequence text, final OnCustomDialogClickListener listener) {
        dialogBtnPositive.setVisibility(View.VISIBLE);
        dialogBtnPositive.setText(text);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onClick(CustomAlertDialog.this);
                else detach();
            }
        };

        dialogBtnPositive.setOnClickListener(onClickListener);
    }

    public void setNegativeButton(CharSequence text, final OnCustomDialogClickListener listener) {
        dialogBtnNegative.setVisibility(View.VISIBLE);
        dialogBtnNegative.setText(text);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onClick(CustomAlertDialog.this);
                else detach();
            }
        };

        dialogBtnNegative.setOnClickListener(onClickListener);
    }

    public void setNeutralButton(CharSequence text, final OnCustomDialogClickListener listener) {
        dialogBtnNeutral.setVisibility(View.VISIBLE);
        dialogBtnNeutral.setText(text);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onClick(CustomAlertDialog.this);
                else detach();
            }
        };

        dialogBtnNeutral.setOnClickListener(onClickListener);
    }

    public void setView(View view) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        dialogView.addView(view);
    }

    public void display() {
        show();
    }

    private void detach() {
        dismiss();
    }
}
