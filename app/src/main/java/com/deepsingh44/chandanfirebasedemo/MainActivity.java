package com.deepsingh44.chandanfirebasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.deepsingh44.chandanfirebasedemo.adapter.StudentAdapter;
import com.deepsingh44.chandanfirebasedemo.model.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference myRef;
    private EditText tname, tmobile;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private StudentAdapter studentAdapter;
    private ImageView imageView;
    private SingleTask singleTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singleTask = (SingleTask) getApplication();
        setContentView(R.layout.activity_main);
        myRef = singleTask.getDatabase().getReference("students");
        tname = findViewById(R.id.name);
        tmobile = findViewById(R.id.mobile);
        progressBar = findViewById(R.id.myprogress);
        imageView = findViewById(R.id.image);
        recyclerView = findViewById(R.id.myrecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        studentAdapter = new StudentAdapter(this,students);
        recyclerView.setAdapter(studentAdapter);

        studentAdapter.setDeepListener(new StudentAdapter.DeepListener() {
            @Override
            public void onCLick(View view, int position) {
                Student student = students.get(position);
                //Toast.makeText(MainActivity.this, student.getName(), Toast.LENGTH_SHORT).show();
                deleteStudent(student);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(openCamera, 1001);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1001) {
                cameraHandledCode(data);
            }
        }
    }

    byte[] imagearray;

    private void cameraHandledCode(Intent data) {
        Bitmap photo = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, b);
        imagearray = b.toByteArray();
        imageView.setImageBitmap(photo);
    }

    private List<Student> students = new ArrayList<>();

    public void addStudent(View view) {
        progressBar.setVisibility(View.VISIBLE);
        final Student student = new Student();
        student.setMobile(tmobile.getText().toString());
        student.setName(tname.getText().toString());

        final DatabaseReference databaseReference = myRef.push();
        student.setId(databaseReference.getKey());

        //upload image to FCM Storage get Url
        singleTask.getStorageReference().child("profileimages/"+databaseReference.getKey()+".jpg").putBytes(imagearray).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getTask().isComplete()) {
                    //Task<Uri> uri=taskSnapshot.getStorage().getDownloadUrl();
                    Log.e("error", taskSnapshot.getMetadata().getPath() + "");
                    Log.e("error", taskSnapshot.getMetadata().getName() + "");
                    student.setProfileimage(taskSnapshot.getMetadata().getName());

                    databaseReference.setValue(student).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(MainActivity.this, "Successfully Added", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Log.e("error", e.toString());
                        }
                    });

                }

            }
        });

    }

    public void fetchAllStudents(View view) {
        progressBar.setVisibility(View.VISIBLE);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                students = new ArrayList<>();
                Iterator<DataSnapshot> ds = snapshot.getChildren().iterator();
                while (ds.hasNext()) {
                    DataSnapshot dd = ds.next();
                    Student student = dd.getValue(Student.class);
                    students.add(student);
                }
                studentAdapter.update(students);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void deleteStudent(Student student) {
        myRef.child(student.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Successfully Deleted", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}