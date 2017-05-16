package zhenyuyang.cs190i.cs.ucsb.edu.a9977158imagetagexplorer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Zhenyu on 2017-05-15.
 */

public class TagRVAdapter extends RecyclerView.Adapter<TagRVAdapter.TagViewHolder>{
    private List<String> tags;

    public class TagViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public TagViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tag_display);
        }
    }

    TagRVAdapter(List<String> tags){
        this.tags = tags;
    }


    @Override
    public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_list,parent,false);
        TagViewHolder holder = new TagViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(TagViewHolder holder, int position) {
        holder.textView.setText(tags.get(position));
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }


}



