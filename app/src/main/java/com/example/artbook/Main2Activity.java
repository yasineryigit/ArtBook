package com.example.artbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Main2Activity extends AppCompatActivity {

    Bitmap selectedImage;
    ImageView imageView;
    EditText artNameText,painterNameText,yearText;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        imageView=findViewById(R.id.imageView);
        artNameText=findViewById(R.id.artNameText);
        painterNameText=findViewById(R.id.painterNameText);
        yearText=findViewById(R.id.yearText);
        button=findViewById(R.id.button);

    }

    public void selectImage(View v){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            //izin verlmediyse
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else{
            //izin verildiyse
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery,2);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==1){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentToGallery,2);
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //izinlerden sonra işlem yapılacak yer
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //galeriye gidip bir dosya seçtiyse
        if(requestCode==2 && resultCode==RESULT_OK && data !=null){
            Uri imageData= data.getData();

            try {

                if(Build.VERSION.SDK_INT>=28){//yüksek versiyonlar için
                    ImageDecoder.Source source= ImageDecoder.createSource(this.getContentResolver(),imageData);
                    selectedImage = ImageDecoder.decodeBitmap(source);
                    imageView.setImageBitmap(selectedImage);
                }else{//düşük versiyonlar için
                    selectedImage= MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageData);
                    imageView.setImageBitmap(selectedImage);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void save(View v){

        String artName = artNameText.getText().toString();
        String painterName = painterNameText.getText().toString();
        String year = yearText.getText().toString();

        Bitmap smallImage = makeSmallerImage(selectedImage,300);

        //image'ı aldık ve veriye çevirdik
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50,os);
        byte[] byteArray = os.toByteArray();

        try{

            SQLiteDatabase db = this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);
            db.execSQL("CREATE TABLE IF NOT EXISTS eserler (id INTEGER PRIMARY KEY, eseradi VARCHAR, paintername VARCHAR, year VARCHAR, image BLOB)");

            String sqlString = "INSERT INTO eserler (eseradi,paintername,year,image) VALUES (?,?,?,?)";
            SQLiteStatement st = db.compileStatement(sqlString);
            st.bindString(1,artName);
            st.bindString(2,painterName);
            st.bindString(3,year);
            st.bindBlob(4,byteArray);
            st.execute();






        }catch (Exception e ){
            e.printStackTrace();
        }

        finish();

    }

    public Bitmap makeSmallerImage(Bitmap image, int maximumSize){
        int width=image.getWidth();
        int height=image.getHeight();

        float bitmapRatio = (float) width / (float) height;

        //resim yatay mı dikey mi
        if(bitmapRatio>1){
            width = maximumSize;
            height=(int) (width/bitmapRatio);
        }else{
            height=maximumSize;
            width=(int) (height*bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image,width,height,true);
    }
}
