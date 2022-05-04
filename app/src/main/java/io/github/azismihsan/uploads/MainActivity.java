package io.github.azismihsan.uploads;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button btnbrowse, btnupload;
    EditText txttitle, txtmasjid, txtlokasi, txtdeskripsi, txtustad;
    ImageView imgview;
    Uri FilePathUri;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    int Image_Request_Code = 7;
    ProgressDialog progressDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storageReference = FirebaseStorage.getInstance().getReference("Banner");
        databaseReference = FirebaseDatabase.getInstance().getReference("Data");
        btnbrowse = findViewById(R.id.btn_Browse);
        btnupload = findViewById(R.id.btn_Upload);
        txtdeskripsi = findViewById(R.id.txt_Data);
        txttitle = findViewById(R.id.txt_Materi);
        txtlokasi = findViewById(R.id.txt_Lokasi);
        txtmasjid = findViewById(R.id.txt_Masjid);
        txtustad = findViewById(R.id.txt_Ustad);
        imgview = findViewById(R.id.image_View);
        progressDialog = new ProgressDialog(MainActivity.this);

        btnbrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select Image"), Image_Request_Code);
            }
        });

        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImage();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null){
            FilePathUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                imgview.setImageBitmap(bitmap);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public  String GetFileExtension (Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void UploadImage(){
        if (FilePathUri != null){
            progressDialog.setTitle("Data is uploading...");
            progressDialog.show();
            StorageReference storageReference2 = storageReference.child(System.currentTimeMillis()+"."+GetFileExtension(FilePathUri));
            storageReference2.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String TempImageName = txttitle.getText().toString().trim();
                            String TempLokasi = txtlokasi.getText().toString().trim();
                            String TempUstad = txtustad.getText().toString().trim();
                            String TempDeskripsi = txtdeskripsi.getText().toString().trim();
                            String TempMasjid = txtmasjid.getText().toString().trim();
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Data Uploaded Successfully", Toast.LENGTH_LONG).show();
                            @SuppressWarnings("VisibleForTests")
                            UploadInfo imageUploadInfo = new UploadInfo(TempImageName, TempDeskripsi,
                                    TempLokasi, TempMasjid, TempUstad, taskSnapshot.getUploadSessionUri().toString());
                            String ImageUploadId = databaseReference.push().getKey();
                            databaseReference.child(ImageUploadId).setValue(imageUploadInfo);
                        }
                    });
        }
        else {
            Toast.makeText(MainActivity.this, "Please entry the empty data", Toast.LENGTH_LONG).show();
        }
    }
}
