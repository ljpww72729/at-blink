package com.ljpww72729.atblink.firebase;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.TextView;

import com.ljpww72729.atblink.R;
import com.ljpww72729.atblink.data.GPIO;

/**
 * Created by LinkedME06 on 2017/9/4.
 */

public class GPIOViewHolder extends RecyclerView.ViewHolder {

    public GPIO gpioEntry;
    public TextView gpio;
    public TextView function;
    public SwitchCompat switch_status;
    public View switch_overlay;

    public GPIOViewHolder(View itemView) {
        super(itemView);
        gpio = itemView.findViewById(R.id.gpio);
        function = itemView.findViewById(R.id.function);
        switch_status = itemView.findViewById(R.id.switch_status);
        switch_overlay = itemView.findViewById(R.id.switch_overlay);
    }

    public void bindToGPIO(final GPIO gpio, View.OnClickListener onClickListener) {
        this.gpioEntry = gpio;
        this.gpio.setText(gpio.getGpio());
        function.setText(gpio.getFunction());
        switch_status.setChecked(gpio.getStatus());
        // 以下两种方式切换动画时有时无
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                if (switch_status.isChecked() == !gpio.getStatus()) {
//                    switch_status.toggle();
//                }
//            }
//        });
//        switch_status.post(new Runnable() {
//            @Override
//            public void run() {
//                if (switch_status.isChecked() == !gpio.getStatus()) {
//                    switch_status.toggle();
//                }
//            }
//        });

        switch_overlay.setOnClickListener(onClickListener);
//        switch_status.setOnCheckedChangeListener(onCheckedChangeListener);
    }

}
