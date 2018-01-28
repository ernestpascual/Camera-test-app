package com.example.ernesteugenepascual.mycameraapp;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {

    // Add a reference to the firebase Storage singleton instance
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    static final int REQUEST_VIDEO_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    // Take video using android's MediaStore library
    public void dispatchTakeVideoIntent(View v) {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        // add 60 seconds limit to recorded videos
        takeVideoIntent.putExtra("android.intent.extra.durationLimit", 60);
        // change video quality
        takeVideoIntent.putExtra("EXTRA_VIDEO_QUALITY", 0);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();

            // set storage reference to the url of your storage application
            StorageReference storageRef = storage.getReferenceFromUrl("gs://spotx-b922c.appspot.com");

            // store video to location in firebase storage
            StorageReference videoRef = storageRef.child("myVideos/" + videoUri.getLastPathSegment());

            // upload video through video uri from your intent
            UploadTask uploadTask = videoRef.putFile(videoUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads

                    Toast.makeText(MainActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                    Toast.makeText(MainActivity.this, "Upload Success", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }


}
