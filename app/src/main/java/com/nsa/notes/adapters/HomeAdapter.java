package com.nsa.notes.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.nsa.notes.R;
import com.nsa.notes.models.NoteModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private Context context;
    private List<NoteModel> list;
    private List<NoteModel> selectedList=new ArrayList<>();
    private OnNoteClickListener listener;
    private boolean selectAll=false;

    private boolean isSelectionEnabled=false;

    public HomeAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.ViewHolder holder, int position) {
        holder.setData(list.get(position));
    }

    @Override
    public int getItemCount() {
        if(list==null){
            return 0;
        }else{
        return list.size();
    }
    }

    public void setListener(OnNoteClickListener listener) {
        this.listener = listener;
    }

    public void setList(List<NoteModel> list) {
        this.list=list;
    }

    public void selectAll(boolean selectAll) {
        this.selectAll=selectAll;
        selectedList.clear();
        if(selectAll){
            selectedList.addAll(list);
        }
        listener.selectedCounter(selectedList.size());
    }

    public void disableSelection() {
        isSelectionEnabled=false;
    }

    public List<NoteModel> getSelectedList() {
        return selectedList;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTV, descrpTV, timeTV, dateTV;
        private MaterialCardView bottomCard;
        private ConstraintLayout constraintLayout;
        private AppCompatCheckBox checkBox;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTV = itemView.findViewById(R.id.titleTV);
            descrpTV = itemView.findViewById(R.id.descrpTV);
            timeTV = itemView.findViewById(R.id.timeTV);
            dateTV = itemView.findViewById(R.id.dateTV);
            bottomCard = itemView.findViewById(R.id.bottom_card);
            checkBox = itemView.findViewById(R.id.checkbox);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);


            bottomCard.setRotation(-8f);


            bottomCard.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (!isSelectionEnabled) {
                        isSelectionEnabled = true;
                        selectedList.clear();
                        listener.onLongClick();
                        bottomCard.callOnClick();
                    }
                    return true;
                }
            });

            bottomCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isSelectionEnabled) {
                        if (checkBox.isChecked()) {
                            checkBox.setChecked(false);
                            constraintLayout.setBackground(context.getDrawable(R.drawable.note_item_bg));
                            selectedList.remove(list.get(getAdapterPosition()));
                        } else {
                            checkBox.setChecked(true);
                            constraintLayout.setBackground(context.getDrawable(R.drawable.note_item_bg_select));
                            selectedList.add(list.get(getAdapterPosition()));
                        }
                        listener.selectedCounter(selectedList.size());
                    } else {
                        listener.onClick(list.get(getAdapterPosition()));
                    }
                }
            });
        }


        public void setData(NoteModel noteModel) {


            checkBox.setChecked(selectAll);
            if (selectAll) {
                constraintLayout.setBackground(context.getDrawable(R.drawable.note_item_bg_select));
            } else {
                constraintLayout.setBackground(context.getDrawable(R.drawable.note_item_bg));
            }


            Random randomGenerator = new Random();
            int red = randomGenerator.nextInt(256);
            int green = randomGenerator.nextInt(256);
            int blue = randomGenerator.nextInt(256);

            bottomCard.setCardBackgroundColor(Color.rgb(red, green, blue));
            titleTV.setText(noteModel.getTitle());
            descrpTV.setText(noteModel.getDescription());

            String time = new SimpleDateFormat("hh:mm a").format(new Date(Long.parseLong(noteModel.getTimeStamp())));
            String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date(Long.parseLong(noteModel.getTimeStamp())));

            timeTV.setText(time);
            dateTV.setText(date);
        }
    }

}
