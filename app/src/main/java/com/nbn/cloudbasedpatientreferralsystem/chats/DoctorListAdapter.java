package com.nbn.cloudbasedpatientreferralsystem.chats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nbn.cloudbasedpatientreferralsystem.R;
import com.nbn.cloudbasedpatientreferralsystem.pojo.DoctorProfile;
import com.nbn.cloudbasedpatientreferralsystem.utils.Constants;

import java.util.ArrayList;

/**
 * Created by dudupoo on 30/1/18.
 */

public class DoctorListAdapter extends RecyclerView.Adapter<DoctorListAdapter.DoctorListVH>
{
    Context context;
    ArrayList<DoctorProfile> doctors;

    public DoctorListAdapter(Context context, ArrayList<DoctorProfile> doctors)
    {
        this.context = context;
        this.doctors = doctors;
    }

    @Override
    public DoctorListVH onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater
                .from(context)
                .inflate(R.layout.card_doctor_list, parent, false);
        return new DoctorListVH((CardView) v);
    }

    @Override
    public void onBindViewHolder(DoctorListVH holder, int position)
    {
        holder.setLayout(doctors.get(position));
    }

    @Override
    public int getItemCount()
    {
        return doctors.size();
    }

    class DoctorListVH extends RecyclerView.ViewHolder{

        TextView tvName;
        CardView cardView;
        public DoctorListVH(CardView itemView)
        {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_doctor_name);
            cardView = itemView;
        }

        void setLayout(final DoctorProfile d) {
            tvName.setText(d.toString());
            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //@TODO Go to chat fragment
                    Intent intent = new Intent(context, ChatScreenActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.KEY_PROFILE, d.getFirebaseId());
                    intent.putExtra(Constants.KEY_BUNDLE, bundle);
                    context.startActivity(intent);
                }
            });
        }
    }
}
