package com.nsa.notes.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.nsa.notes.R;

import org.jetbrains.annotations.NotNull;




public class YesNoDialog extends DialogFragment {

    private TextView titleTV,messageTV1,yesTV,noTV;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.yes_no_dialog, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        yesTV=view.findViewById(R.id.yes_txt);
        titleTV=view.findViewById(R.id.titleTv);
        messageTV1=view.findViewById(R.id.messageTV1);
        noTV=view.findViewById(R.id.no_txt);

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(title.isEmpty()){
            titleTV.setVisibility(View.GONE);
        }
        titleTV.setText(title);
        messageTV1.setText(message);


        if(!yesText.isEmpty()){
        yesTV.setText(yesText);
        yesTV.setVisibility(View.VISIBLE);
        }
        if(!noText.isEmpty()){
            noTV.setText(noText);
            noTV.setVisibility(View.VISIBLE);
        }
        yesTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onYesClickListener!=null){
                    getDialog().dismiss();
                    onYesClickListener.OnClick();
                }
            }
        });
        noTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onNoClickListener!=null){
                    getDialog().dismiss();
                    onNoClickListener.OnClick();
                }
            }
        });
    }


    private String title="",message="",yesText="",noText="";

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private OnClickListener onYesClickListener;
    private OnClickListener onNoClickListener;

    public void setYesText(String yesText,OnClickListener onClickListener) {
        this.onYesClickListener=onClickListener;
       this.yesText=yesText;
    }

    public void setNoText(String noText,OnClickListener onClickListener) {
        this.onNoClickListener=onClickListener;
       this.noText=noText;
    }
    public interface OnClickListener{
        void OnClick();
    }

}
