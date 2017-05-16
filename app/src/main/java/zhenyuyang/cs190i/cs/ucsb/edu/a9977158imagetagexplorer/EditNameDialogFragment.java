package zhenyuyang.cs190i.cs.ucsb.edu.a9977158imagetagexplorer;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.ArrayList;

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

    public static EditNameDialogFragment newInstance(String title) {
        EditNameDialogFragment frag = new EditNameDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
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
        getDialog().setTitle(title);

        tags.add("rwet3w");
        tags.add("134r2");

        tagRecyclerView = (RecyclerView) view.findViewById(R.id.tag_list_frag);


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




        // Show soft keyboard automatically and request focus to field
//        mEditText.requestFocus();
//        getDialog().getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}