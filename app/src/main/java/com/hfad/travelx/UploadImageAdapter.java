package com.hfad.travelx;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UploadImageAdapter extends RecyclerView.Adapter<UploadImageAdapter.ViewHolder> {
    public List<String> filenamelist;
    public List<String> filedonelist;

    public UploadImageAdapter(List<String> filenamelist, List<String> filedonelist) {
        this.filenamelist = filenamelist;
        this.filedonelist = filedonelist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.imageselected,parent,false);
       return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
         String filename = filenamelist.get(position);
         holder.filenameview.setText(filename);
         String filedone = filedonelist.get(position);
         if(filedone.equals("Uploading")){
             holder.fileDoneview.setImageResource(R.drawable.unchecked_icon);
         }else{
             holder.fileDoneview.setImageResource(R.drawable.check_image);
         }
    }

    @Override
    public int getItemCount() {
        return filedonelist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View maview;
        public TextView filenameview;
        public ImageView fileDoneview;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.maview = itemView;
            filenameview = itemView.findViewById(R.id.image_name);
            fileDoneview = itemView.findViewById(R.id.check_icon);

        }
    }
}
