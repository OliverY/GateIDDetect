package com.yuantu.gateiddtect.widget.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.yuantu.gateiddtect.R;
import com.yxj.dialog.BaseDialog;

/**
 * Author:  Yxj
 * Time:    2018/6/21 上午11:14
 * -----------------------------------------
 * Description:
 */
public class RegistPortraitDialog extends BaseDialog {

    private static final String TAG = RegistPortraitDialog.class.getSimpleName();

    public RegistPortraitDialog(@NonNull Context context) {
        super(context);
    }

    public static class Builder extends BaseDialog.Builder {

        EditText etName;
        ImageView img;
        Button btnOk;

        Bitmap bitmap;
        OnDialogClick click;

        public Builder(Context context) {
            super(context);
        }

        @Override
        public int setContentView() {
            return R.layout.dialog_register;
        }

        @Override
        public void initView(View layout) {
            etName = layout.findViewById(R.id.editview);
            img = layout.findViewById(R.id.img);
            btnOk = layout.findViewById(R.id.btn_ok);

            btnOk.setOnClickListener(v->{
                if (click != null) {
                    if(!TextUtils.isEmpty(etName.getText().toString())){
                        click.ok(etName.getText().toString());
                        dialog.dismiss();
                    }
                }
            });
            img.setImageBitmap(bitmap);
        }

        public Builder setData(Bitmap bitmap){
            this.bitmap = bitmap;
            return this;
        }

        public Builder setClick(OnDialogClick click){
            this.click = click;
            return this;
        }

    }

    public interface OnDialogClick {
        void ok(String name);
    }

}
