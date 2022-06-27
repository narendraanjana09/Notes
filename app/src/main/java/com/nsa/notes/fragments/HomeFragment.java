package com.nsa.notes.fragments;

import static android.app.Activity.RESULT_OK;
import static com.nsa.notes.extra.GoogleSignIn.RC_SIGN_IN;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nsa.notes.BuildConfig;
import com.nsa.notes.MainActivity;
import com.nsa.notes.R;
import com.nsa.notes.adapters.HomeAdapter;
import com.nsa.notes.adapters.OnNoteClickListener;
import com.nsa.notes.databinding.FragmentHomeBinding;
import com.nsa.notes.db.Constants;
import com.nsa.notes.dialogs.YesNoDialog;
import com.nsa.notes.extra.FireBase;
import com.nsa.notes.extra.Keyboard;
import com.nsa.notes.extra.SavedText;
import com.nsa.notes.extra.SignInListener;
import com.nsa.notes.models.NoteModel;
import com.nsa.notes.models.UserModel;
import com.nsa.notes.repo.SyncToServer;
import com.nsa.notes.viewmodels.MainViewModel;
import com.rm.rmswitch.RMSwitch;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentHomeBinding binding;
    private MainViewModel viewModel;
    private NavController navController;

    private FirebaseAuth mfirebaseAuth;
    public static FirebaseUser firebaseUser;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;




    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        }
        viewModel= ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())
                .create(MainViewModel.class);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentHomeBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
    private String TAG="home_frag";
    private HomeAdapter adapter;
    private List<NoteModel> notesList=new ArrayList<>();
    private boolean clicked=true;
    private boolean isSelectPos=false;

    @Override
    public void onResume() {
        super.onResume();
        setStatusBarBlack();
        Keyboard.hide(getView());
        MainActivity.listener=new OnBackPressedListener() {
            @Override
            public void onBackPressed() {
                if(MainActivity.isInAdd){
                 binding.back1.callOnClick();
                    setStatusBarBlack();
                    MainActivity.isInAdd=false;
                    Log.e(TAG, "onBackPressed: isInAdd" );
                }
                if(MainActivity.isInSelect){
                    binding.cancelSelectBtn.callOnClick();
                   cancelSelect();
                    Log.e(TAG, "onBackPressed: isInSelect" );
                }

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController=Navigation.findNavController(view);
        getParentFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        adapter=new HomeAdapter(getContext());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
                .requestEmail()
                .build();
        mGoogleSignInClient = com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(getActivity(), gso);

        mfirebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=mfirebaseAuth.getCurrentUser();
        account= com.google.android.gms.auth.api.signin.GoogleSignIn.getLastSignedInAccount(getActivity());
        if(account!=null){
            Glide.with(getContext()).load(account.getPhotoUrl()).into(binding.profileImgBtn);
            String[] splited = account.getDisplayName().split(" ");
            if(splited.length!=0 && !splited[0].trim().isEmpty()){
                String name=splited[0];
                name=(name.charAt(0)+"").toUpperCase()+name.substring(1,name.length());
            binding.text1.setText("Hello "+name+",");
            }else{
                binding.text1.setText("Hello There,");
            }
        }



        binding.homeRecycleview.setLayoutManager(new GridLayoutManager(getContext(),2));
        binding.homeRecycleview.setAdapter(adapter);



        adapter.setListener(new OnNoteClickListener() {
            @Override
            public void onClick(NoteModel note) {
                Keyboard.hide(view);
                Bundle bundle=new Bundle();
              bundle.putSerializable("note",note);
              navController.navigate(R.id.action_homeFragment_to_detailFragment,bundle);
            }

            @Override
            public void onLongClick() {
                MainActivity.isInSelect=true;
                if(isSelectPos){

                    binding.selectFromPosBTN.callOnClick();
                }else{

                setSatusBarWhite();
                Keyboard.hide(view);
               binding.selectedBTN.callOnClick();
            }
            }

            @Override
            public void selectedCounter(int size) {


                binding.selectCounterTV.setText(size+" selected");
                binding.deleteSelectedBtn.setCardBackgroundColor(getContext().getResources().getColor(R.color.red));
                binding.deleteSelectedBtn.setClickable(true);
                if(size==0){
                    binding.selectCounterTV.setText("Selected Notes");
                    binding.deleteSelectedBtn.setCardBackgroundColor(getContext().getResources().getColor(R.color.red1));
                    binding.deleteSelectedBtn.setClickable(false);
                }
                clicked=false;
                if(size==notesList.size()){
                    binding.selectAllCB.setChecked(true);
                }else{
                    binding.selectAllCB.setChecked(false);
                }
                clicked=true;

            }
        });

        binding.selectAllCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(clicked){
                if(b){
                adapter.selectAll(true);
                }else{
                    adapter.selectAll(false);
                }
                adapter.notifyDataSetChanged();
                }

                clicked=true;
            }
        });


        viewModel.getAllNotes().observe(getViewLifecycleOwner(),list->{
            notesList.clear();

            if(list==null || list.size()==0){

                binding.upDownBtn.setAlpha(0f);
                binding.upDownBtn.setClickable(false);
                binding.searchEdt.setEnabled(false);
            }else{

                binding.searchEdt.setEnabled(true);
                binding.upDownBtn.setClickable(true);
                binding.upDownBtn.setAlpha(1f);
                notesList.addAll(list);
                if(new SavedText(getContext()).getText("sync").equals("on")){
                    syncNotesToServer(list);
                }else{
                }
            }
            adapter.setList(notesList);
            adapter.notifyDataSetChanged();
        });
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if(notesList.size()==0){
                    binding.gifImage.setVisibility(View.VISIBLE);
                }else{
                    binding.gifImage.setVisibility(View.GONE);
                }
            }
        });
        binding.deleteSelectedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(NoteModel noteModel:adapter.getSelectedList()){
                    viewModel.delete(noteModel);
                    if(firebaseUser!=null) {
                        Log.e(TAG, "delete " );
                        new FireBase().userDataReference.child(firebaseUser.getUid()).child(noteModel.getTimeStamp()).removeValue();
                    }
                }
                MainActivity.isInSelect=false;
                binding.selectedBTN.callOnClick();
                setStatusBarBlack();
                adapter.selectAll(false);
                adapter.disableSelection();
                adapter.notifyDataSetChanged();

                isSelectPos=false;
                binding.aboveImg.setVisibility(View.VISIBLE);
                binding.belowImg.setVisibility(View.GONE);
            }
        });

        binding.profileImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog=new Dialog(getActivity());
                dialog.setContentView(R.layout.profile_layout);
                dialog.setCancelable(false);
                DisplayMetrics displayMetrics = new DisplayMetrics();
                ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;
                dialog.getWindow().setLayout((int)(width),(int)(height*0.6));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                MaterialCardView closeBTN,signInBTN;
                CircleImageView imageView=dialog.findViewById(R.id.profile_img);
                TextView nameTV=dialog.findViewById(R.id.name_tv);
                TextView signOutBtn=dialog.findViewById(R.id.signOut_tv);
                ProgressBar progressBar=dialog.findViewById(R.id.progressBar);
                RMSwitch syncSwitch=dialog.findViewById(R.id.sync_switch);


                if(new SavedText(getContext()).getText("sync").equals("on")){
                    syncSwitch.setChecked(true);
                }

                ConstraintLayout dataLayout=dialog.findViewById(R.id.data_layout);
                LinearLayout signInLayout=dialog.findViewById(R.id.sign_in_layout);
                TextView joinedDateTV=dialog.findViewById(R.id.joinedDateTV);

                joinedDateTV.setText(new SavedText(getContext()).getText("joinDate"));

                signInBTN=dialog.findViewById(R.id.sign_in_btn);
                closeBTN=dialog.findViewById(R.id.close_profile_card_btn);

                closeBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                signOutBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        YesNoDialog yesNoDialog=new YesNoDialog();
                        yesNoDialog.setTitle("Sign-Out Request!");
                        yesNoDialog.setMessage("Do You Really Want To Sign-Out?");
                        yesNoDialog.setYesText("sign-out", new YesNoDialog.OnClickListener() {
                            @Override
                            public void OnClick() {
                                dataLayout.setVisibility(View.GONE);
                                signInLayout.setVisibility(View.GONE);
                                progressBar.setVisibility(View.VISIBLE);
                                mfirebaseAuth.signOut();
                                mGoogleSignInClient.signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        binding.profileImgBtn.setImageDrawable(getContext().getDrawable(R.drawable.ic_baseline_person_24));
                                        binding.text1.setText("Hello There,");
                                        joinedDateTV.setText("");
                                        dataLayout.setVisibility(View.GONE);
                                        progressBar.setVisibility(View.GONE);
                                        signInLayout.setVisibility(View.VISIBLE);
                                        updateUserObject();
                                        new SavedText(getContext()).setText("joinDate","");
                                        new SavedText(getContext()).setText("sync","off");
                                    }
                                });
                            }
                        });
                        yesNoDialog.setNoText("cancel", new YesNoDialog.OnClickListener() {
                            @Override
                            public void OnClick() {

                            }
                        });
                        yesNoDialog.setDeleteText("delete account & data", new YesNoDialog.OnClickListener() {
                            @Override
                            public void OnClick() {
                                dataLayout.setVisibility(View.GONE);
                                signInLayout.setVisibility(View.GONE);
                                progressBar.setVisibility(View.VISIBLE);
                                viewModel.deleteAll();
                                new FireBase().userDataReference.child(firebaseUser.getUid()).removeValue();
                                new FireBase().userReference.child(firebaseUser.getUid()).removeValue();
                                mfirebaseAuth.signOut();
                                mGoogleSignInClient.signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        binding.profileImgBtn.setImageDrawable(getContext().getDrawable(R.drawable.ic_baseline_person_24));
                                        binding.text1.setText("Hello There,");
                                        dataLayout.setVisibility(View.GONE);
                                        joinedDateTV.setText("");
                                        progressBar.setVisibility(View.GONE);
                                        signInLayout.setVisibility(View.VISIBLE);
                                        updateUserObject();
                                        new SavedText(getContext()).setText("sync","off");
                                        new SavedText(getContext()).setText("joinDate","");
                                        showToast("Your Data Is Deleted");
                                    }
                                });
                            }
                        });
                        yesNoDialog.show(getParentFragmentManager(),"yesNo");

                    }

                });
                syncSwitch.addSwitchObserver(new RMSwitch.RMSwitchObserver() {
                    @Override
                    public void onCheckStateChange(RMSwitch switchView, boolean isChecked) {
                        if(isChecked){
                            new SavedText(getContext()).setText("sync","on");
                            new FireBase().userReference.child(firebaseUser.getUid()).child("syncEnabled").setValue(true);
                            syncNotesToServer(notesList);
                        }else{
                            new SavedText(getContext()).setText("sync","off");
                            new FireBase().userReference.child(firebaseUser.getUid()).child("syncEnabled").setValue(false);
                        }
                    }
                });

                signInBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SignInListener listener=new SignInListener() {
                            @Override
                            public void info(int code) {
                                if(code== Constants.SUCCESS){
                                    progressBar.setVisibility(View.GONE);
                                    dataLayout.setVisibility(View.VISIBLE);
                                    closeBTN.setEnabled(true);
                                    updateUserObject();
                                    syncOnlineToOffLine();
                                    if(account!=null){
                                        Glide.with(getContext()).load(account.getPhotoUrl()).into(imageView);
                                        Glide.with(getContext()).load(account.getPhotoUrl()).into(binding.profileImgBtn);
                                        String[] splited = account.getDisplayName().split(" ");
                                        if(splited.length!=0 && !splited[0].trim().isEmpty()){
                                            String name=splited[0];
                                            name=(name.charAt(0)+"").toUpperCase()+name.substring(1,name.length());
                                            binding.text1.setText("Hello "+name+",");
                                        }else{
                                            binding.text1.setText("Hello There,");
                                        }
                                        nameTV.setText(account.getDisplayName());
                                        joinedDateTV.setText(new SavedText(getContext()).getText("joinDate"));
                                    }
                                }else if(code==Constants.SHOW_PROGRESS){
                                    signInLayout.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.VISIBLE);
                                    closeBTN.setEnabled(false);
                                }else{
                                    signInLayout.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                    closeBTN.setEnabled(true);
                                }
                            }

                            @Override
                            public void data(UserModel model) {
                                updateUserObject();
                                new SavedText(getContext()).setText("joinDate",model.getJoinedDate());
                                joinedDateTV.setText(model.getJoinedDate());


                                    syncSwitch.setChecked(model.isSyncEnabled());
                                    if(model.isSyncEnabled()){
                                        new SavedText(getContext()).setText("sync","on");
                                        syncNotesToServer(notesList);
                                    }else{
                                        new SavedText(getContext()).setText("sync","off");
                                    }

                            }
                        };
                        beginsignIn(listener);
                    }
                });

                if(firebaseUser==null){
                    dataLayout.setVisibility(View.GONE);
                    signInLayout.setVisibility(View.VISIBLE);
                }else{
                    dataLayout.setVisibility(View.VISIBLE);
                    signInLayout.setVisibility(View.GONE);
                    Glide.with(getContext()).load(account.getPhotoUrl()).into(imageView);
                    nameTV.setText(account.getDisplayName());
                }


                Keyboard.hide(view);
                dialog.show();
            }
        });

        binding.addNotesBtn.setOnTouchListener((view1, motionEvent) -> {

            if(motionEvent.getAction() == MotionEvent.ACTION_UP){

                closeKeyboard();
                binding.titleEd.requestFocus();
                MainActivity.isInAdd=true;
                new CountDownTimer(600, 600) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        showKeyboard();
                    }
                }.start();
                setSatusBarWhite();
            }
            return false;
        });

        binding.cancelSelectBtn.setOnTouchListener((view1, motionEvent) -> {

            if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                cancelSelect();
            }
            return false;
        });

        binding.upDownBtn.setOnTouchListener((view1, motionEvent) -> {

            if(motionEvent.getAction() == MotionEvent.ACTION_UP){

                closeKeyboard();
               if(binding.aboveImg.getVisibility()==View.VISIBLE){
                   binding.aboveImg.setVisibility(View.GONE);
                   binding.belowImg.setVisibility(View.VISIBLE);
                   isSelectPos=true;
                   setSatusBarWhite();
               }else{
                   isSelectPos=false;
                   binding.aboveImg.setVisibility(View.VISIBLE);
                   binding.belowImg.setVisibility(View.GONE);
                   setStatusBarBlack();
               }


            }
            return false;
        });
        binding.back1.setOnTouchListener((view1, motionEvent) -> {

            if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                closeKeyboard();
                MainActivity.isInAdd=false;
                setStatusBarBlack();

            }
            return false;
        });
        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title=binding.titleEd.getText().toString().trim();
                String descrp=binding.descriptionEd.getText().toString().trim();

                if(checkNote(title,descrp)){



                    viewModel.saveNote(new NoteModel(0,title,descrp,System.currentTimeMillis()+""));

                    binding.back1.callOnClick();
                    setStatusBarBlack();
                    MainActivity.isInAdd=false;
                    binding.titleEd.setText("");
                    binding.descriptionEd.setText("");
                    closeKeyboard();



                }
            }
        });

        binding.searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s=editable.toString();
                if(s.isEmpty()){
                    setSearch();
                    adapter.setList(notesList);
                    adapter.notifyDataSetChanged();
                }else{
                    search(s);
                    setEmpty();
                }

            }
        });
        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(notesList.size()>0){
                binding.searchEdt.requestFocus();
                showKeyboard();
                binding.searchEdt.setText("");
            }else{
                    if(!isSearch){
                    setSearch();
                    binding.searchEdt.setText("");
                    }
                }
            }
        });

    }
    private boolean isSearch=true;

    private void cancelSelect() {
        setStatusBarBlack();
        adapter.selectAll(false);
        adapter.disableSelection();
        adapter.notifyDataSetChanged();
        MainActivity.isInSelect=false;
        isSelectPos=false;
        binding.aboveImg.setVisibility(View.VISIBLE);
        binding.belowImg.setVisibility(View.GONE);
    }


    private void updateUserObject() {
        account= GoogleSignIn.getLastSignedInAccount(getActivity());
        mfirebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=mfirebaseAuth.getCurrentUser();
    }

    private void syncOnlineToOffLine() {
        new FireBase().userDataReference.child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                NoteModel noteModel=dataSnapshot.getValue(NoteModel.class);
                                viewModel.saveNote(noteModel);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void syncNotesToServer(List<NoteModel> list) {
        if(list==null || list.size()==0){
            return;
        }
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            return;
        }
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=new FireBase().userDataReference.child(firebaseUser.getUid());
        new SyncToServer(reference).execute(list);
    }


    private com.nsa.notes.extra.GoogleSignIn googleSignIn;
    private void beginsignIn(SignInListener listener) {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);


        googleSignIn=new com.nsa.notes.extra.GoogleSignIn(getActivity());
        googleSignIn.setListener(listener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){

            switch (requestCode){

                case RC_SIGN_IN:

                    //show progress
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    googleSignIn.handleSignInResult(task);


                    break;

            }

        }

    }



    private void showKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void setSatusBarWhite() {
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(getContext().getResources().getColor(R.color.background));
    }

    private void search(String s) {
        List<NoteModel> list=new ArrayList<>();
        for(NoteModel note:notesList){
            if(note.getTitle().toLowerCase().contains(s.toLowerCase())
            || note.getDescription().toLowerCase().contains(s.toLowerCase())){
                list.add(note);
            }
        }
        adapter.setList(list);
        adapter.notifyDataSetChanged();
    }

    private void setStatusBarBlack() {
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.setStatusBarColor(getContext().getResources().getColor(R.color.status_bar));
    }
    public void closeKeyboard(){
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    public void showToast(String message){
        Toast.makeText(getContext(), ""+message, Toast.LENGTH_SHORT).show();
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

    private void setSearch() {
        isSearch=true;
        binding.searchImg.setVisibility(View.VISIBLE);
        binding.clearImg.setVisibility(View.GONE);
    }
    private void setEmpty() {
        isSearch=false;
        binding.searchImg.setVisibility(View.GONE);
        binding.clearImg.setVisibility(View.VISIBLE);

    }



}