package com.cookandroid.swu;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.cookandroid.swu.Fragment.EboxFragment;
import com.google.android.gms.common.util.IOUtils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EboxAdd extends AppCompatActivity {

    private Button savebtn;
    private ImageView view1;
    private Button dateview;
    File photoFile;
    Bitmap photo;
    static String ebox_date, ebox_name, ebox_sympton, ebox_memo;
    private EditText eboxname, eboxsympton,eboxmemo;


    //날짜 선택
    private Calendar c = Calendar.getInstance();
    int mYear = c.get(Calendar.YEAR);
    int mMonth= c.get(Calendar.MONTH);
    int mDay = c.get(Calendar.DAY_OF_MONTH);

    final int CAMERA = 100; // 카메라 선택시 인텐트로 보내는 값
    final int GALLERY = 300; // 갤러리 선택 시 인텐트로 보내는 값

    @SuppressLint("SimpleDateFormat")

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ebox_add);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기 버튼
        ActionBar ac=getSupportActionBar();
        ac.setTitle("우리집 구급함 약 추가하기"); //actionbar추가
        EboxFragment tf = (EboxFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);



        //접근 확인
        boolean hasCamPerm = checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean hasWritePerm = checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if (!hasCamPerm || !hasWritePerm)  // 권한 없을 시  권한설정 요청
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        view1 = (ImageView) findViewById(R.id.ebox_view);
        dateview = (Button) findViewById(R.id.datebtn);
        savebtn =(Button)findViewById(R.id.ebox_save);
        eboxname=(EditText)findViewById(R.id.ebox_name);
        eboxmemo=(EditText)findViewById(R.id.ebox_memo);
        eboxsympton=(EditText)findViewById(R.id.ebox_sympton);
        //camera
        view1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        doTakePhotoAction();
                    }
                };
                DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        doTakeAlbumAction();
                    }
                };
                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                };
                new AlertDialog.Builder(EboxAdd.this)
                        .setTitle("이미지를 선택하세요")
                        .setPositiveButton("사진촬영", cameraListener)
                        .setNeutralButton("앨범선택", albumListener)
                        .setNegativeButton("취소", cancelListener)
                        .show();
            }

        });

        //Date
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                dateview.setText(year + " - " + (month + 1) + " - " + dayOfMonth);
                ebox_date = String.format("유통기한 : " + year + " - " + (month + 1) + " - " + dayOfMonth);
            }
        }, mYear, mMonth, mDay);

        dateview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dateview.isClickable()) {
                    datePickerDialog.getDatePicker().setCalendarViewShown(false);
                    datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    datePickerDialog.show();
                }
            }
        }); //setonclicklistener

        //파이어베이스-Ebox
        FirebaseDatabase Eboxdb;
        DatabaseReference refEbox;
        Eboxdb = FirebaseDatabase.getInstance();
        refEbox = Eboxdb.getReference("ebox");


        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ebox_name = eboxname.getText().toString();
                ebox_memo = eboxmemo.getText().toString();
                ebox_sympton = "증상 : " + eboxsympton.getText().toString();
                tf.addItem(photo,ebox_name,ebox_sympton,ebox_date,ebox_memo);
                String Ename = eboxname.getText().toString();
                String Edate = ebox_date;
                Ebox ebox = new Ebox(Ename,Edate);
                refEbox.child(Edate).setValue(ebox);
                finish();
            }
        });
    }//oncreate

    private void doTakePhotoAction()
    {
        int permissionCheck = ContextCompat.checkSelfPermission(
                EboxAdd.this, android.Manifest.permission.CAMERA);

        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(EboxAdd.this,
                    new String[]{android.Manifest.permission.CAMERA} ,
                    1000);
            Toast.makeText(EboxAdd.this, "카메라 권한 필요합니다.",
                    Toast.LENGTH_SHORT).show();
            return;
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(intent.resolveActivity(getPackageManager())  != null  ){

                // 사진의 파일명을 만들기
                String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                photoFile = getPhotoFile(fileName);

                Uri fileProvider = FileProvider.getUriForFile(EboxAdd.this,
                        "com.cookandroid.swu.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                startActivityForResult(intent, CAMERA);

            } else{
                Toast.makeText(EboxAdd.this, "이폰에는 카메라 앱이 없습니다.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File getPhotoFile(String fileName) {
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try{
            return File.createTempFile(fileName, ".jpg", storageDirectory);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
    private void doTakeAlbumAction()
    {
        if(checkPermission()){
            displayFileChoose();
        }else{
            requestPermission();
        }
    }
    private void requestPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(EboxAdd.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(EboxAdd.this, "권한 수락이 필요합니다.",
                    Toast.LENGTH_SHORT).show();
        }else{
            ActivityCompat.requestPermissions(EboxAdd.this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 500);
        }
    }
    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(EboxAdd.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result == PackageManager.PERMISSION_DENIED){
            return false;
        }else{
            return true;
        }
    }
    private void displayFileChoose() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, GALLERY);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(EboxAdd.this, "권한 허가 되었음",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EboxAdd.this, "아직 승인하지 않았음",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 500: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(EboxAdd.this, "권한 허가 되었음",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EboxAdd.this, "아직 승인하지 않았음",
                            Toast.LENGTH_SHORT).show();
                }

            }

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 100 && resultCode == RESULT_OK){

            photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

            ExifInterface exif = null;
            try {
                exif = new ExifInterface(photoFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            photo = rotateBitmap(photo, orientation);

            // 압축시킨다. 해상도 낮춰서
            OutputStream os;
            try {
                os = new FileOutputStream(photoFile);
                photo.compress(Bitmap.CompressFormat.JPEG, 50, os);
                os.flush();
                os.close();
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
            }

            photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

            view1.setImageBitmap(photo);
            view1.setScaleType(ImageView.ScaleType.FIT_XY);

            // 네트워크로 데이터 보낸다.



        }else if(requestCode == 300 && resultCode == RESULT_OK && data != null &&
                data.getData() != null){

            Uri albumUri = data.getData( );
            String fileName = getFileName( albumUri );
            try {

                ParcelFileDescriptor parcelFileDescriptor = getContentResolver( ).openFileDescriptor( albumUri, "r" );
                if ( parcelFileDescriptor == null ) return;
                FileInputStream inputStream = new FileInputStream( parcelFileDescriptor.getFileDescriptor( ) );
                photoFile = new File( this.getCacheDir( ), fileName );
                FileOutputStream outputStream = new FileOutputStream( photoFile );
                IOUtils.copyStream( inputStream, outputStream );

//                //임시파일 생성
//                File file = createImgCacheFile( );
//                String cacheFilePath = file.getAbsolutePath( );


                // 압축시킨다. 해상도 낮춰서
                photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                OutputStream os;
                try {
                    os = new FileOutputStream(photoFile);
                    photo.compress(Bitmap.CompressFormat.JPEG, 60, os);
                    os.flush();
                    os.close();
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
                }
                // InputStream inputStream1 = getContentResolver().openInputStream(albumUri);
                //photo = BitmapFactory.decodeStream(inputStream1);
                //inputStream.close();
                view1.setImageBitmap(photo);
                view1.setScaleType(ImageView.ScaleType.FIT_XY);

//                imageView.setImageBitmap( getBitmapAlbum( imageView, albumUri ) );

            } catch ( Exception e ) {
                e.printStackTrace( );
            }

            // 네트워크로 보낸다.
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }
    public String getFileName( Uri uri ) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        try {
            if (cursor == null) return null;
            cursor.moveToFirst();
            @SuppressLint("Range") String fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            cursor.close();
            return fileName;

        } catch (Exception e) {
            e.printStackTrace();
            cursor.close();
            return null;
        }
    }

    //이미지뷰에 뿌려질 앨범 비트맵 반환
    public Bitmap getBitmapAlbum( View targetView, Uri uri ) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
            if (parcelFileDescriptor == null) return null;
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            if (fileDescriptor == null) return null;

            int targetW = targetView.getWidth();
            int targetH = targetView.getHeight();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

            int photoW = options.outWidth;
            int photoH = options.outHeight;

            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
            if (scaleFactor >= 8) {
                options.inSampleSize = 8;
            } else if (scaleFactor >= 4) {
                options.inSampleSize = 4;
            } else {
                options.inSampleSize = 2;
            }
            options.inJustDecodeBounds = false;

            Bitmap reSizeBit = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

            ExifInterface exifInterface = null;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    exifInterface = new ExifInterface(fileDescriptor);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            int exifOrientation;
            int exifDegree = 0;

            //사진 회전값 구하기
            if (exifInterface != null) {
                exifOrientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
                    exifDegree = 90;
                } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
                    exifDegree = 180;
                } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
                    exifDegree = 270;
                }
            }

            parcelFileDescriptor.close();
            Matrix matrix = new Matrix();
            matrix.postRotate(exifDegree);

            Bitmap reSizeExifBitmap = Bitmap.createBitmap(reSizeBit, 0, 0, reSizeBit.getWidth(), reSizeBit.getHeight(), matrix, true);
            return reSizeExifBitmap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}//class