package zhenyuyang.cs190i.cs.ucsb.edu.a9977158imagetagexplorer;

import android.app.DialogFragment;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Zhenyu on 2017-05-16.
 */

public class EditNameDialogFragment extends DialogFragment {
     ArrayList<String> tags= new  ArrayList<String>();
     RecyclerView tagRecyclerView;
    String[] clickedTagList;
    private EditText mEditText;


    public EditNameDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static EditNameDialogFragment newInstance(String title,String ImageUri,String[] clickedTagList) {
        EditNameDialogFragment frag = new EditNameDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("imageUri", ImageUri);
        args.putStringArray("clickedTagList",clickedTagList);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_name, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        //mEditText = (EditText) view.findViewById(R.id.txt_your_name);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        String imageUri = getArguments().getString("imageUri", "file:///data/data/zhenyuyang.cs190i.cs.ucsb.edu.a9977158imagetagexplorer/files/Test5.jpg");

        String [] tagsList2 = {"qwer","weewer","qwgrqeg3","gh","t42","my","342gr","3rvf","uizxcvo","qewdvs","qwefcavd"};
        clickedTagList = getArguments().getStringArray("clickedTagList");
        getDialog().setTitle(title);
        ImageView imageView_frag = (ImageView) view.findViewById(R.id.imageView_frag);
        Log.i("my", "Uri.parse(imageUri) = " + (Uri.parse(imageUri)));



        Picasso.with(view.getContext()).load(Uri.parse(imageUri)).fit().into(imageView_frag);


        tags.add("rwet3w");
        tags.add("134r2");
        //String [] tags

        tagRecyclerView = (RecyclerView) view.findViewById(R.id.tag_list_frag);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(),LinearLayoutManager.HORIZONTAL,false);
        tagRecyclerView.setLayoutManager(linearLayoutManager);
        //update tags RV

        TagRVAdapter tagAdapter = new TagRVAdapter(new ArrayList<String>(Arrays.asList(clickedTagList)));
        tagRecyclerView.setAdapter(tagAdapter);
        Log.i("my", "tags.size()-1 = " + (tags.size() - 1));

        tagRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                tagRecyclerView.smoothScrollToPosition(tags.size() - 1);
            }
        });




        AutoCompleteTextView autocomplete = (AutoCompleteTextView)
                view.findViewById(R.id.autoCompleteTextView_frag);

        ArrayAdapter<String> auto_complete_adapter = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item, StringsForAutoComplete);

        autocomplete.setThreshold(1);
        autocomplete.setAdapter(auto_complete_adapter);
        autocomplete.setOnEditorActionListener(
                new AutoCompleteTextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            Log.i("my", "TextView = "+v.getText().toString());
                            if(!v.getText().toString().equals("")) {
                                //tags.add(v.getText().toString());   //not adding
                                if(tags.size()==0){
                                    tags.add(v.getText().toString());
                                }
                                else{
                                    tags.set(0,v.getText().toString());
                                }

                                //update tags RV
                                TagRVAdapter tagAdapter = new TagRVAdapter(tags);
                                tagRecyclerView.setAdapter(tagAdapter);
                                Log.i("my", "tags.size()-1 = " + (tags.size() - 1));

                                tagRecyclerView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        tagRecyclerView.smoothScrollToPosition(tags.size() - 1);
                                    }
                                });
                                Uri[] LinkedUris = getLinkedDataFromDBByTag(v.getText().toString(),dbHelper.getReadableDatabase());  //test linked table
                                SelelctImageGrid adapter = new SelelctImageGrid(getApplicationContext(), LinkedUris);
                                grid = (GridView) findViewById(R.id.grid);
                                grid.setAdapter(adapter);

                                v.setText("");
                            }


                            //StringsForAutoComplete
//                            InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            return true;
                        }
                        return false;
                    }
                });
        // end of autocompletetextview





        // Show soft keyboard automatically and request focus to field
//        mEditText.requestFocus();
//        getDialog().getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}