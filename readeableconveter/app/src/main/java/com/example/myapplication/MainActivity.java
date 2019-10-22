package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class MainActivity extends AppCompatActivity {

    EditText mResultEt;
    ImageView mPreviewIv;

    private static final int CAMERA_REQUEST_CODE=200;
    private static final int STORAGE_REQUEST_CODE=400;
    private static final int IMAGE_PICK_GALLERY_CODE=1000;
    private static final int IMAGE_PICK_CAMERA_CODE=1001;

    String cameraPermission[];
    String storagePermssion[];

    Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setSubtitle("Click + button to insert Image");

        mResultEt=findViewById(R.id.resultEt);
        mPreviewIv=findViewById(R.id.imageIv);

        //cameraPErmsion
        cameraPermission=new String[]{Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //storage PErmssion
        storagePermssion=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

    }


    //actionbar menu


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //infalte menu
        getMenuInflater().inflate(R.menu.menu_main,menu);


        return true;
    }



    //handle actionbar item click
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id =item.getItemId();
        if(id==R.id.addImage){
            showImageImportDialog();


        }

        if(id==R.id.settings){
            Toast.makeText(this,"Settings",Toast.LENGTH_SHORT).show();

        }

        return super.onOptionsItemSelected(item);
    }

    private void showImageImportDialog() {
        //Items to display in dialog
        String[] items={" Camera","Gallery"};
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);

        //Set title
        dialog.setTitle("Select Image");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0)
                {
                    //camera option clicked
                    /*for os marshammlow and above ask runtime permission for camer and storage*/
                    if(!checkCameraPermission()){

                        //camera permsiion not allowed request it
                        requestCameraPermission();
                    }
                    else{
                        //permsion alowed,take pictire
                        pickCamera();
                    }


                }
                if(which == 1){
                    //gallery option click

                    if(!checkStoragePermission()){
                        //Storage permsiion not allowed request it
                        requestStoragePermission();
                    }
                    else{
                        //permsion alowed,take pictire
                        pickGallery();
                    }

                }
            }
        });
        dialog.create().show();//show dialog



    }

    private void pickGallery() {
        //Inetnet to pick image form gallery
        Intent intent=new Intent(Intent.ACTION_PICK);
        //set integer type to image
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);

    }



    private void pickCamera() {

        //Intent to take Image from Camera,it Will also be save to storeage to get high qulaituy image

        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Pictures"); //title of the Pic
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image to Text");//description
        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);
    }



    private void requestStoragePermission() {

        ActivityCompat.requestPermissions(this,storagePermssion,STORAGE_REQUEST_CODE);
    }



    private boolean checkStoragePermission() {

        boolean result= ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }



    private void requestCameraPermission() {

        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }



    private boolean checkCameraPermission() {

        //check camera permission and retrun the result
        //in order to get high quality we have to save image to external storage first
        //before inserting to image view thats why storage permssion weill also be required

        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);

        boolean result1= ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);


        return result && result1;
    }



    //handle PErmssion result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length>0){
                    boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccepted){
                        pickCamera();
                    }
                    else {
                        Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();

                    }
                }
                break;

            case STORAGE_REQUEST_CODE:
                if(grantResults.length>0){
                    boolean writeStorageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted){
                        pickGallery();
                    }
                    else {
                        Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();

                    }
                }
                break;

        }

    }

    //handle Image REsult

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //got Image form camera
        if(resultCode==RESULT_OK){
            if(requestCode==IMAGE_PICK_GALLERY_CODE){
                //got image from gallert now crop it
                CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);  //enable Image guidline

            }
            if(requestCode==IMAGE_PICK_CAMERA_CODE){
                //image from camer now crop it
                CropImage.activity(image_uri).setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);  //enable Image guidline

            }
        }

        //get Cropped Image
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                Uri resultUri=result.getUri();//get image uri
                //set Image to image View
                mPreviewIv.setImageURI(resultUri);

                //get drawbale bitmap for Text recoginatiom
                BitmapDrawable bitmapDrawable = (BitmapDrawable)mPreviewIv.getDrawable();
                Bitmap bitmap=bitmapDrawable.getBitmap();
                TextRecognizer recognizer=new TextRecognizer.Builder(getApplicationContext()).build();
                if(!recognizer.isOperational()) {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                }
                else {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items=recognizer.detect(frame);
                    StringBuilder sb=new StringBuilder();
                    //get text from sb until there is no text
                    for(int i=0;i<items.size();i++)
                        {
                            TextBlock myItem=items.valueAt(i);
                            sb.append(myItem.getValue());
                            sb.append("\n");

                        }
                    //set text to edit text
                    mResultEt.setText(sb.toString());
                }

            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                //if there is any error show it
                Exception error=result.getError();
                Toast.makeText(this,""+error,Toast.LENGTH_SHORT).show();

            }

        }

    }

}
