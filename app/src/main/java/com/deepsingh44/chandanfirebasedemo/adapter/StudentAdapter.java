package com.deepsingh44.chandanfirebasedemo.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.deepsingh44.chandanfirebasedemo.R;
import com.deepsingh44.chandanfirebasedemo.SingleTask;
import com.deepsingh44.chandanfirebasedemo.model.Student;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.MyViewHolder> {
    private List<Student> students;
    private DeepListener deepListener;
    private Context context;

    public StudentAdapter(Context context, List<Student> students) {
        this.students = students;
        this.context = context;
    }

    public void update(List<Student> students) {
        this.students = students;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.error).placeholder(R.drawable.ic_baseline_cloud_upload_24);
        Student student = students.get(position);
        holder.tname.setText(student.getName());
        holder.tmobile.setText(student.getMobile());
        SingleTask singleTask = (SingleTask) (((AppCompatActivity) context).getApplication());
        singleTask.getStorageReference().child("profileimages/" + student.getProfileimage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //load image using glide
                Log.e("error", uri + "");
                Glide.with(context).load(uri).apply(requestOptions).into(holder.imageView);
            }
        });

    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tname, tmobile;
        ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tname = itemView.findViewById(R.id.name);
            tmobile = itemView.findViewById(R.id.mobile);
            imageView = itemView.findViewById(R.id.profile_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            deepListener.onCLick(v, getAdapterPosition());
        }
    }

    public void setDeepListener(DeepListener deepListener) {
        this.deepListener = deepListener;
    }

    public interface DeepListener {
        void onCLick(View view, int position);
    }
}
