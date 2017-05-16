package zhenyuyang.cs190i.cs.ucsb.edu.a9977158imagetagexplorer;

import android.app.DialogFragment;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Zhenyu on 2017-05-16.
 */

public class EditNameDialogFragment extends DialogFragment {
     ArrayList<String> tags= new  ArrayList<String>();
     RecyclerView tagRecyclerView;
    private EditText mEditText;


    public EditNameDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static EditNameDialogFragment newInstance(String title,String ImageUri) {
        EditNameDialogFragment frag = new EditNameDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("imageUri", ImageUri);

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
        String [] tags2 = {"qwer","weewer","qwgrqeg3","gh","t42","my","342gr","3rvf","uizxcvo","qewdvs","qwefcavd"};
        TagRVAdapter tagAdapter = new TagRVAdapter(new ArrayList<String>(Arrays.asList(tags2)));
        tagRecyclerView.setAdapter(tagAdapter);
        Log.i("my", "tags.size()-1 = " + (tags.size() - 1));

        tagRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                tagRecyclerView.smoothScrollToPosition(tags.size() - 1);
            }
        });









        // Show soft keyboard automatically and request focus to field
//        mEditText.requestFocus();
//        getDialog().getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}