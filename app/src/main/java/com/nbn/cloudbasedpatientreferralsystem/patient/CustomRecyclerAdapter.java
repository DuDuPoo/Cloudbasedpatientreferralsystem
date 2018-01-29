package com.nbn.cloudbasedpatientreferralsystem.patient;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.nbn.cloudbasedpatientreferralsystem.R;
import com.nbn.cloudbasedpatientreferralsystem.pojo.DocumentInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by dudupoo on 29/1/18.
 */

public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.CustomViewHolder>
{
    ArrayList<DocumentInfo> docs;
    Context context;

    public CustomRecyclerAdapter(Context context, ArrayList<DocumentInfo> docs) {
        this.docs = docs;
        this.context = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater
                .from(context)
                .inflate(R.layout.card_patient_docs, parent, false);
        return new CustomViewHolder((CardView) v);

    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position)
    {
        holder.setLayout(docs.get(position));
    }

    @Override
    public int getItemCount()
    {
        return docs.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;
        public CustomViewHolder(CardView itemView)
        {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_docname);
            imageView = (ImageView) itemView.findViewById(R.id.iv_doc);
        }

        void setLayout(DocumentInfo documentInfo) {
            textView.setText(documentInfo.getDocName());
            Picasso.with(context).load(documentInfo.getImageUrl()).into(imageView);
        }
    }
}
