package zhenyuyang.cs190i.cs.ucsb.edu.a9977158imagetagexplorer;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Zhenyu on 2017-05-14.
 */

public class SelelctImageGrid extends BaseAdapter {
    private Context mContext;
    private final String[] web;
    private  int[] Imageid;
    private  Uri[] ImageUris;





    public SelelctImageGrid(Context c,String[] web,int[] Imageid ) {
        mContext = c;
        this.Imageid = Imageid;
        this.web = web;
    }

    public SelelctImageGrid(Context c,String[] web,Uri[] ImageUris ) {
        mContext = c;
        this.ImageUris = ImageUris;
        this.web = web;
    }

    public SelelctImageGrid(Context c,Uri[] ImageUris ) {
        mContext = c;
        this.ImageUris = ImageUris;
        this.web = null;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return ImageUris.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*
        // TODO Auto-generated method stub
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
//            if(position!=ImageUris.length) {
                //grid = new View(mContext);
                grid = inflater.inflate(R.layout.grid_single, null);
                //TextView textView = (TextView) grid.findViewById(R.id.grid_text);
                ImageView imageView = (ImageView) grid.findViewById(R.id.grid_image);
                //textView.setText(web[position]);
                //imageView.setImageResource(Imageid[position]);
                //imageView.setImageURI(ImageUris[position]);
                Picasso.with(parent.getContext()).load(ImageUris[position]).fit().into(imageView);
//            }
//            else{
//                grid = new View(mContext);
//                grid = inflater.inflate(R.layout.grid_single, null);
//                //TextView textView = (TextView) grid.findViewById(R.id.grid_text);
//                ImageView imageView = (ImageView) grid.findViewById(R.id.grid_image);
//                //textView.setText(web[position]);
//                imageView.setBackgroundColor(Color.GRAY);
//                imageView.setImageResource(R.drawable.kong);
//                //imageView.setImageURI(ImageUris[position]);
//            }
        } else {
            grid = (View) convertView;
        }

        return grid;
        */

        // TODO Auto-generated method stub
        View grid;
        if(convertView==null)
        {
            LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            grid = li.inflate(R.layout.grid_single, null);
        }else{
            grid = convertView;
        }
        ImageView imageView = (ImageView) grid.findViewById(R.id.grid_image);
        Picasso.with(parent.getContext()).load(ImageUris[position]).fit().into(imageView);
        //iv.setImageResource(R.drawable.icon);

        return grid;
    }
}
