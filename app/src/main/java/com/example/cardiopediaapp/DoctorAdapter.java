package com.example.cardiopediaapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {
    private List<Doctor> doctorList;
    private Context context;

    public DoctorAdapter(Context context, List<Doctor> doctorList) {
        this.context = context;
        this.doctorList = doctorList;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);
        holder.tvName.setText(doctor.getName());
        holder.tvQualification.setText(doctor.getQualification());
        holder.tvSpecialization.setText(doctor.getSpecialization());
        holder.tvHospital.setText(doctor.getHospital());
        holder.tvPhoneNumber.setText(doctor.getPhoneNumber());
        holder.tvAddress.setText(doctor.getAddress());

        holder.contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatsAppChat(doctor.getPhoneNumber());
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public void updateList(List<Doctor> newList) {
        doctorList.clear();
        doctorList.addAll(newList);
        notifyDataSetChanged();
    }

    private void openWhatsAppChat(String phoneNumber) {
        String url = "https://api.whatsapp.com/send?phone=" + phoneNumber;
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "WhatsApp not installed.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    static class DoctorViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQualification, tvSpecialization, tvHospital, tvPhoneNumber, tvAddress;
        Button contactBtn;

        DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvQualification = itemView.findViewById(R.id.tvQualification);
            tvSpecialization = itemView.findViewById(R.id.tvSpecialization);
            tvHospital = itemView.findViewById(R.id.tvHospital);
            tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            contactBtn = itemView.findViewById(R.id.contactBtn);
        }
    }
}
