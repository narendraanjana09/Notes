package com.nsa.notes.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.nsa.notes.MainActivity;
import com.nsa.notes.R;
import com.nsa.notes.databinding.FragmentDetailBinding;
import com.nsa.notes.extra.FireBase;
import com.nsa.notes.extra.Keyboard;
import com.nsa.notes.models.NoteModel;
import com.nsa.notes.viewmodels.MainViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentDetailBinding binding;
    private NavController navController;
    private NoteModel noteModel;
    private MainViewModel viewModel;
    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance(String param1, String param2) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            noteModel=(NoteModel) getArguments().getSerializable("note");
        }
        viewModel= ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())
                .create(MainViewModel.class);


    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.listener=new OnBackPressedListener() {
            @Override
            public void onBackPressed() {
                showToast("back pressed in detail");
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity.listener=null;
    }

    @Override
    public void onPause() {
        super.onPause();
        MainActivity.listener=null;
    }

    @Override
    public void onStop() {
        super.onStop();
        MainActivity.listener=null;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController= Navigation.findNavController(view);
        setSatusBarWhite();
        setData();
        binding.back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.popBackStack();
            }
        });
        binding.back2.setOnTouchListener((view1, motionEvent) -> {

            if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                Keyboard.hide(view);

            }
            return false;
        });
        binding.editBTN.setOnTouchListener((view1, motionEvent) -> {

            if(motionEvent.getAction() == MotionEvent.ACTION_UP){
              setData();

            }
            return false;
        });
        binding.deleteBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                    new FireBase().userDataReference.child(FirebaseAuth.getInstance().getUid()).child(noteModel.getTimeStamp()).removeValue();
                } viewModel.delete(noteModel);
                navController.popBackStack();
            }
        });
        binding.shareBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String time=new SimpleDateFormat("hh:mm a").format(new Date(Long.parseLong(noteModel.getTimeStamp())));
                String date=new SimpleDateFormat("dd/MM/yyyy").format(new Date(Long.parseLong(noteModel.getTimeStamp())));
                String text="title:- "+binding.titleTV.getText().toString()+"\ndescription:-"+binding.descrpTV.getText().toString()
                        +"\ndate:- "+date+" "+time+"\n\n\n";
                String link="https://play.google.com/store/apps/details?id=com.nsa.notes";

                Intent intent2 = new Intent(); intent2.setAction(Intent.ACTION_SEND);
                intent2.setType("text/plain");
                intent2.putExtra(Intent.EXTRA_TEXT, text+"\ncheckout app:-"+link );
                startActivity(Intent.createChooser(intent2, "Share via"));
            }
        });
        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title=binding.titleEd.getText().toString().trim();
                String descrp=binding.descriptionEd.getText().toString().trim();
                if(checkNote(title,descrp)){


                    noteModel.setTitle(title);
                    noteModel.setDescription(descrp);
                    String timeStamp=noteModel.getTimeStamp();
                    noteModel.setTimeStamp(System.currentTimeMillis()+"");

                    viewModel.update(noteModel);
                    if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                        new FireBase().userDataReference.child(FirebaseAuth.getInstance().getUid()).child(timeStamp).removeValue();
                        new FireBase().userDataReference.child(FirebaseAuth.getInstance().getUid()).child(noteModel.getTimeStamp()).setValue(noteModel);
                    }

                    binding.back2.callOnClick();
                    binding.titleEd.setText("");
                    binding.descriptionEd.setText("");
                    Keyboard.hide(view);
                    setData();

            }
            }
        });
    }

    private boolean checkNote(String title, String descrp) {
        if(title.isEmpty()){
            showToast("please enter title!");
            return false;
        }
        if(descrp.isEmpty()){
            showToast("please enter description!");
            return false;
        }
        if(descrp.length()<5){
            showToast("description must have atleast 5 characters!");
            return false;
        }

        return true;
    }


    private void setData() {
        binding.titleTV.setText(noteModel.getTitle());
        binding.titleEd.setText(noteModel.getTitle());
        binding.descrpTV.setText(noteModel.getDescription());
        binding.descriptionEd.setText(noteModel.getDescription());
    }

    private void setSatusBarWhite() {
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(getContext().getResources().getColor(R.color.background));
    }

    private void showToast(String title) {
        Toast.makeText(getContext(), ""+title, Toast.LENGTH_SHORT).show();
    }

}