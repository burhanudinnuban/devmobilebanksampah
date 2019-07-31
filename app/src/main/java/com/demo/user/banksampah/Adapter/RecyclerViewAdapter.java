package com.demo.user.banksampah.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.demo.user.banksampah.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private ArrayList<String> arr_TipeSampah;
    private ArrayList<String> arr_Deskripsi;
    private ArrayList<String> arr_Image;
    private ArrayList<String> arr_ImageParent;
    private ArrayList<String> arr_JenisSampah;

    protected RestProcess rest_class;
    protected HashMap<String, String> apiData;

    public RecyclerViewAdapter(Context context, ArrayList<String>tipe_sampah, ArrayList<String>deskripsi,
                               ArrayList<String> ImageUrl, ArrayList<String>ImageUrl_Parent, ArrayList<String>jenis_sampah){
        arr_TipeSampah = tipe_sampah;
        arr_Deskripsi = deskripsi;
        arr_Image = ImageUrl;
        arr_ImageParent = ImageUrl_Parent;
        arr_JenisSampah = jenis_sampah;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_detail_info_sampah, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){

        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();

        String url_image = apiData.get("str_url_main") ;

        Picasso.get()
                .load(url_image + arr_Image.get(position))
                .error(R.drawable.ic_no_data)
                .into(holder.image);

        Picasso.get()
                .load(url_image + arr_ImageParent.get(position))
                .error(R.drawable.ic_no_data)
                .into(holder.imageParent);

        String penjelasan = arr_Deskripsi.get(position);
        String change = penjelasan.replace("\n", "<br>");

        holder.jenis_sampah.setText(arr_JenisSampah.get(position));
        holder.tipe_sampah.setText(arr_TipeSampah.get(position));
        holder.wb_Text.loadData
                (
                        "<p style=\'font-size:14px;text-align:justify;color:gray\'>"
                                + change
                                + "</p>", "text/html", "UTF-8"
                );
    }

    @Override
    public int getItemCount(){
        return arr_Image.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image, imageParent;
        TextView tipe_sampah, deskripsi, jenis_sampah;
        WebView wb_Text;

        public ViewHolder(View itemView){
            super(itemView);
            image = itemView.findViewById(R.id.imgTipeSampah);
            //deskripsi = itemView.findViewById(R.id.tvDeskripsi);
            tipe_sampah = itemView.findViewById(R.id.tvTipeSampah);
            imageParent = itemView.findViewById(R.id.imgJenisSampah);
            jenis_sampah = itemView.findViewById(R.id.tvJenisSampah);

            wb_Text = itemView.findViewById(R.id.tvDeskripsi);
            wb_Text.setVerticalScrollBarEnabled(false);
            wb_Text.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
            wb_Text.setLongClickable(false);
            wb_Text.setHapticFeedbackEnabled(false);
        }
    }
}
