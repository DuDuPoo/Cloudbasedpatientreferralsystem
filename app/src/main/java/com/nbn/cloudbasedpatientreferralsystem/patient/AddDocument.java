package com.nbn.cloudbasedpatientreferralsystem.patient;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nbn.cloudbasedpatientreferralsystem.BaseActivity;
import com.nbn.cloudbasedpatientreferralsystem.R;
import com.nbn.cloudbasedpatientreferralsystem.pojo.DocumentInfo;
import com.nbn.cloudbasedpatientreferralsystem.utils.Constants;

import java.io.ByteArrayOutputStream;

public class AddDocument extends BaseActivity implements View.OnClickListener
{
    String TAG = getClass().getSimpleName();

    private AlertDialog alertDialog;
    private TextView galleryText, cameraText, deleteText;
    private ImageView imageView;
    private EditText etDocName;
    private ImageButton btnUpload;
    private ProgressBar progressBar;

    final static int GALLERY_REQUEST_CODE = 100;
    final static int CAMERA_REQUEST_CODE = 101;
    final static int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 200;
    final static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_document);
        databaseReference = rootDatabaseReference
                .child(Constants.ROOT_PATIENTS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(Constants.PATIENT_DOCS)
                .getRef();
        initLayout();
    }

    private void initLayout()
    {
        imageView = (ImageView) findViewById(R.id.doc_image);
        etDocName = (EditText) findViewById(R.id.doc_name);
        btnUpload = (ImageButton) findViewById(R.id.btn_upload);
        btnUpload.setOnClickListener(this);
        imageView.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void createDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = View.inflate(this, R.layout.dialog_custom_image_picker, null);
        galleryText = (TextView) dialogView.findViewById(R.id.gallery);
        cameraText = (TextView) dialogView.findViewById(R.id.camera);
        deleteText = (TextView) dialogView.findViewById(R.id.deletePhoto);
        galleryText.setOnClickListener(this);
        cameraText.setOnClickListener(this);
        deleteText.setOnClickListener(this);
        builder.setView(dialogView);

        alertDialog = builder.create();
        if (alertDialog.getWindow() != null)
        {
            alertDialog.getWindow().setGravity(Gravity.BOTTOM);
            alertDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        alertDialog.show();
    }//end of createDialog()

    private void uploadImage()
    {
        if(imageView.getDrawable() == null)
        {
            Toast.makeText(this, "Please select the image", Toast.LENGTH_SHORT).show();
        }else
        {
            if (TextUtils.isEmpty(etDocName.getText().toString()))
            {
                Toast.makeText(this, "Please enter name of the document", Toast.LENGTH_SHORT).show();
            } else
            {
                StorageReference patientImagesRef = rootStorageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                StorageReference docRef = patientImagesRef.child(etDocName.getText().toString() + ".jpg");
                imageView.setDrawingCacheEnabled(true);
                imageView.buildDrawingCache();
                Bitmap bitmap = imageView.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = docRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception exception)
                    {
                        //@TODO Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Log.d(TAG, "onSuccess: " + downloadUrl.toString());
                        DocumentInfo docInfo = new DocumentInfo();
                        Log.d(TAG, "onSuccess: RootPatient :: "+rootPatientProfile);
                        docInfo.setDocName(etDocName.getText().toString());
                        docInfo.setPatientProfile(rootPatientProfile);
                        docInfo.setImageUrl(downloadUrl.toString());
                        databaseReference
                                .child(docInfo.getDocName())
                                .setValue(docInfo)
                                .addOnCompleteListener(new OnCompleteListener<Void>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG, "onComplete: DocInfo node updated");
                                        finish();     
                                    }
                                });
                    }
                });
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.doc_image:
                Toast.makeText(this, "ImageView pressed", Toast.LENGTH_SHORT).show();
                if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED)
                {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) &&
                            ActivityCompat.shouldShowRequestPermissionRationale(this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    {

                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    } else
                    {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                    return;
                } else
                {
                    createDialog();
                }
                break;
            case R.id.gallery:
            {
                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                alertDialog.dismiss();
                startActivityForResult(pickIntent, GALLERY_REQUEST_CODE);
                break;
            }
            case R.id.camera:
            {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                alertDialog.dismiss();
                break;
            }
            case R.id.deletePhoto:
            {
                imageView.setImageDrawable(null);
                break;
            }
            case R.id.btn_upload:
            {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.animate();
                progressBar.setHovered(true);
                uploadImage();
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case GALLERY_REQUEST_CODE:
            {
                if (resultCode == RESULT_OK && data != null)
                {
                    Uri uri = data.getData();
                    try
                    {
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        int nh = (int) (imageBitmap.getHeight() * (512.0 / imageBitmap.getWidth()));
                        Bitmap scaled = Bitmap.createScaledBitmap(imageBitmap, 512, nh, true);
                        imageView.setImageBitmap(scaled);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            case CAMERA_REQUEST_CODE:
            {
                if (resultCode == RESULT_OK && data != null)
                {
                    try
                    {
                        Bundle bundle = data.getExtras();
                        Bitmap imageBitmap = (Bitmap) bundle.get("data");
                        imageView.setImageBitmap(imageBitmap);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    }//end of onActivityResult()
}
