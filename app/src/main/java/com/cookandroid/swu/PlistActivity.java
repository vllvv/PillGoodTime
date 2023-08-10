
package com.cookandroid.swu;

import android.Manifest;
import android.app.Activity;
import android.app.TimePickerDialog;
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
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.cookandroid.swu.Fragment.PlistFragment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class PlistActivity extends AppCompatActivity {
    Integer plListCount = 1;
    String plName="", plMemo="", plDay="";
    String[] plRealDay = {"월", "화", "수", "목", "금", "토", "일"};
    String imagePath="";
    ImageButton plistBtnPicture;
    EditText plistEdtName, plistEdtRealMemo;
    Button btnDay[] = new Button[7];
    Integer btnDayIDs[] = {R.id.plistBtnMon, R.id.plistBtnTue, R.id.plistBtnWed,
            R.id.plistBtnThu, R.id.plistBtnFri, R.id.plistBtnSat, R.id.plistBtnSun};
    Button plistBtnTime, plistBtnSave;
    Button btnTime[] = new Button[6];
    Integer btnTimeIds[] = {R.id.btnTime1, R.id.btnTime2, R.id.btnTime3, R.id.btnTime4,
            R.id.btnTime5, R.id.btnTime6};
    // TimePickerDialog를 띄웠을 때 시간 설정
    int alarmHour = 0, alarmMinute = 0;
    String plTime1, plTime2, plTime3, plTime4, plTime5, plTime6;
    // 카메라
    final int CAMERA = 100; // 카메라 선택 시 인텐트로 보내는 값
    final int GALLERY = 101; // 갤러리 선택 시 인텐트로 보내는 값
    Intent intentC, intentG;
    Bitmap bitmap = null;



    /* 카메라 갤러리 */
    // 파일 생성 메소드
    File createImageFile() throws IOException {
        // 이미지 파일 생성
        SimpleDateFormat imageDate = new SimpleDateFormat("yyyyMMdd_HHmmss");
        // 파일명 중복을 피하기 위한 "yyyyMMdd_HHmmss" 꼴의 timeStamp
        String timeStamp = imageDate.format(new Date());
        String fileName = "IMAGE_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // 이미지 파일 생성
        File file = File.createTempFile(fileName, ".jpg", storageDir);
        // 파일 절대경로 저장
        imagePath = file.getAbsolutePath();

        return file;
    }
    // 카메라 이미지 세팅
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            // 결과가 있을 경우
            switch (requestCode) {
                case GALLERY:
                    // 갤러리에서 이미지로 선택한 경우
                    Cursor cursor = getContentResolver().query(data.getData(),
                            null, null, null, null);
                    if(cursor != null) {
                        cursor.moveToFirst();
                        int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                        imagePath = cursor.getString(index);
                        bitmap = BitmapFactory.decodeFile(imagePath);
                        cursor.close();
                    }
                    // InputStream으로 이미지 세팅하기
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(data.getData());
                        ExifInterface exif = new ExifInterface(inputStream);
                        inputStream.close();

                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        Matrix matrix = new Matrix();
                        if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
                            matrix.postRotate(90);
                        else if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
                            matrix.postRotate(180);
                        else if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
                            matrix.postRotate(270);

                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                // 카메라로 이미지 가져온 경우
                case CAMERA:
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    // 이미지 축소 정도. 원 크기에서 1/inSampleSize로 축소
                    options.inSampleSize = 5;
                    bitmap = BitmapFactory.decodeFile(imagePath, options);
                    // 사진이 왼쪽으로 90도 회전되어 나와서 오른쪽으로 90도 회전
                    Matrix rotateMatrix = new Matrix();
                    rotateMatrix.postRotate(90);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                            bitmap.getWidth(), bitmap.getHeight(), rotateMatrix, false);

                    break;
            }
            plistBtnPicture.setImageBitmap(bitmap);
        }
    }
    // 카메라 함수
    public void doTakePhotoAction(){
        intentC = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intentC.resolveActivity(getPackageManager()) != null) {
            File imageFile = null;
            try {
                imageFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (imageFile != null) {
                Uri imageUri = FileProvider.getUriForFile(getApplicationContext(),
                        "com.cookandroid.swu.fileprovider", imageFile);
                intentC.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intentC, CAMERA);
            }
        }
    }
    // 갤러리 함수
    public void doTakeAlbumAction(){
        intentG = new Intent(Intent.ACTION_PICK);
        intentG.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intentG.setType("image/*");
        startActivityForResult(intentG, GALLERY);
    }


    /* 컨텍스트 메뉴 */
    // 시간 개수 선택 컨텍스트 메뉴
    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        if (v == plistBtnTime) {
            menuInflater.inflate(R.menu.plist_time, menu);
        }
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        // 선택되면 개수만큼 button을 보이게 함
        switch (item.getItemId()){
            case R.id.itemPlist1:
                plistBtnTime.setText("1");
                plListCount = 1;
                for(int i=0;i<6;i++){
                    if(i<plListCount)btnTime[i].setVisibility(View.VISIBLE);
                    else btnTime[i].setVisibility(View.GONE);
                }
                return true;
            case R.id.itemPlist2:
                plistBtnTime.setText("2");
                plListCount = 2;
                for(int i=0;i<6;i++){
                    if(i<plListCount)btnTime[i].setVisibility(View.VISIBLE);
                    else btnTime[i].setVisibility(View.GONE);
                }
                return true;
            case R.id.itemPlist3:
                plistBtnTime.setText("3");
                plListCount = 3;
                for(int i=0;i<6;i++){
                    if(i<plListCount)btnTime[i].setVisibility(View.VISIBLE);
                    else btnTime[i].setVisibility(View.GONE);
                }
                return true;
            case R.id.itemPlist4:
                plistBtnTime.setText("4");
                plListCount = 4;
                for(int i=0;i<6;i++){
                    if(i<plListCount)btnTime[i].setVisibility(View.VISIBLE);
                    else btnTime[i].setVisibility(View.GONE);
                }
                return true;
            case R.id.itemPlist5:
                plistBtnTime.setText("5");
                plListCount = 5;
                for(int i=0;i<6;i++){
                    if(i<plListCount)btnTime[i].setVisibility(View.VISIBLE);
                    else btnTime[i].setVisibility(View.GONE);
                }
                return true;
            case R.id.itemPlist6:
                plistBtnTime.setText("6");
                plListCount = 6;
                for(int i=0;i<6;i++){
                    if(i<plListCount)btnTime[i].setVisibility(View.VISIBLE);
                    else btnTime[i].setVisibility(View.GONE);
                }
                return true;
        }
        return false;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plist);
        setTitle("복용 약 리스트 추가");

        // findViewById
        fvbi();

        // 카메라 권한체크
        // 안드로이드 버전이 마시멜로우 버전 이상이어야 가능
        boolean hasCamPerm = checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean hasWritePerm = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        // 권한 없을 시 권한 설정 요청
        if(!hasCamPerm || !hasWritePerm)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        // 카메라
        plistBtnPicture.setOnClickListener(new View.OnClickListener() {
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
                new AlertDialog.Builder(PlistActivity.this)
                        .setTitle("이미지를 선택하세요")
                        .setPositiveButton("사진촬영", cameraListener)
                        .setNeutralButton("앨범선택", albumListener)
                        .setNegativeButton("취소", cancelListener)
                        .show();
            }
        });

        // 요일 저장
        for(int i=0;i<btnDay.length;i++){
            final int index = i;
            int[] count = new int[btnDay.length];
            btnDay[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // count 개수를 세서 한 번 클릭하면 pressed 상태, 두 번 클릭하면 not pressed 상태로 변경
                    if(count[index] > 0) count[index] = 0;
                    count[index] += 1;
                    if(count[index] % 2 == 1) {
                        btnDay[index].setSelected(true);
                        plDay += btnDay[index].getText().toString();
                    } // pressed
                    else if(count[index] % 2 == 0) {
                        btnDay[index].setSelected(false);
                        count[index] = 0;
                        plDay = plDay.replace(btnDay[index].getText().toString(), "");
                    } // not pressed
                }
            });
        }

        // 시간 개수 선택 - context menu 클릭만으로 열기
        plistBtnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerForContextMenu(view);
                openContextMenu(view);
            }
        });
        // 시간 저장
        btnTime[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog =
                        new TimePickerDialog(PlistActivity.this, android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                                String minTen = "", hourTen = "";

                                // min이 10이하면 0을 붙여서 저장
                                if (min < 10) minTen = "0" + Integer.toString(min);
                                else minTen = Integer.toString(min);
                                if (hour < 10) hourTen = "0" + Integer.toString(hour);
                                else hourTen = Integer.toString(hour);


                                plTime1 = hourTen+" : "+minTen+" ";
                                btnTime[0].setText("복용 시간 1         "+plTime1);
                            }
                        }, alarmHour, alarmMinute, true);
                timePickerDialog.show();
            }
        });
        btnTime[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog =
                        new TimePickerDialog(PlistActivity.this, android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                                String minTen = "", hourTen = "";

                                // min이 10이하면 0을 붙여서 저장
                                if (min < 10) minTen = "0" + Integer.toString(min);
                                else minTen = Integer.toString(min);
                                if (hour < 10) hourTen = "0" + Integer.toString(hour);
                                else hourTen = Integer.toString(hour);

                                plTime2 = hourTen+" : "+minTen+" ";
                                btnTime[1].setText("복용 시간 2         "+plTime2);
                            }
                        }, alarmHour, alarmMinute, true);
                timePickerDialog.show();
            }
        });
        btnTime[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog =
                        new TimePickerDialog(PlistActivity.this, android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                                String minTen = "", hourTen = "";

                                // min이 10이하면 0을 붙여서 저장
                                if (min < 10) minTen = "0" + Integer.toString(min);
                                else minTen = Integer.toString(min);
                                if (hour < 10) hourTen = "0" + Integer.toString(hour);
                                else hourTen = Integer.toString(hour);

                                plTime3 = hourTen+" : "+minTen+" ";
                                btnTime[2].setText("복용 시간 3         "+plTime3);
                            }
                        }, alarmHour, alarmMinute, true);
                timePickerDialog.show();
            }
        });
        btnTime[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog =
                        new TimePickerDialog(PlistActivity.this, android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                                String minTen = "", hourTen = "";

                                // min이 10이하면 0을 붙여서 저장
                                if (min < 10) minTen = "0" + Integer.toString(min);
                                else minTen = Integer.toString(min);
                                if (hour < 10) hourTen = "0" + Integer.toString(hour);
                                else hourTen = Integer.toString(hour);

                                plTime4 = hourTen+" : "+minTen+" ";
                                btnTime[3].setText("복용 시간 4         "+plTime4);
                            }
                        }, alarmHour, alarmMinute, true);
                timePickerDialog.show();
            }
        });
        btnTime[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog =
                        new TimePickerDialog(PlistActivity.this, android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                                String minTen = "", hourTen = "";

                                // min이 10이하면 0을 붙여서 저장
                                if (min < 10) minTen = "0" + Integer.toString(min);
                                else minTen = Integer.toString(min);
                                if (hour < 10) hourTen = "0" + Integer.toString(hour);
                                else hourTen = Integer.toString(hour);

                                plTime5 = hourTen+" : "+minTen+" ";
                                btnTime[4].setText("복용 시간 5         "+plTime5);
                            }
                        }, alarmHour, alarmMinute, true);
                timePickerDialog.show();
            }
        });
        btnTime[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog =
                        new TimePickerDialog(PlistActivity.this, android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                                String minTen = "", hourTen = "";

                                // min이 10이하면 0을 붙여서 저장
                                if (min < 10) minTen = "0" + Integer.toString(min);
                                else minTen = Integer.toString(min);
                                if (hour < 10) hourTen = "0" + Integer.toString(hour);
                                else hourTen = Integer.toString(hour);

                                plTime6 = hourTen+" : "+minTen+" ";
                                btnTime[5].setText("복용 시간 6         "+plTime6);
                            }
                        }, alarmHour, alarmMinute, true);
                timePickerDialog.show();
            }
        });


        // 저장 버튼 - 데이터 주기
        plistBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 약 이름, 메모 저장
                plName = plistEdtName.getText().toString();
                plMemo = plistEdtRealMemo.getText().toString();


                Bitmap plBitmap = null;
                if(bitmap == null) plBitmap = null; // 사진을 찍거나 고르지 않았으면 null로 저장
                else plBitmap = bitmap;

                HashMap<Integer, String> tmp = new HashMap<Integer, String>();
                for(int i = 0 ;i< plDay.length();i++){
                    for(int j = 0 ; j < 7; j++){
                        if (plRealDay[j].equals(plDay.substring(i, i+1))){
                            tmp.put(j, plRealDay[j]);
                        }
                    }
                }
                plDay = "";
                for(int i = 0 ;i < 7; i++){
                    if(tmp.containsKey(i))
                        plDay += tmp.get(i);
                }

                if(!plName.equals("")){
                    PlistFragment plistFragment = (PlistFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    plistFragment.addItem(plListCount, plBitmap, plName, plMemo, plDay, plTime1, plTime2, plTime3, plTime4, plTime5, plTime6);
                    plDay = "";
                    finish();
                }
                else if(plName.equals("")) {
                    Toast.makeText(PlistActivity.this, "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    // findViewById
    void fvbi() {
        plistBtnPicture = findViewById(R.id.plistBtnPicture);
        plistEdtName = findViewById(R.id.plistEdtName);
        plistEdtRealMemo = findViewById(R.id.plistEdtRealMemo);
        plistBtnTime = findViewById(R.id.plistBtnTime);
        for (int i = 0; i < btnDay.length; i++) {
            btnDay[i] = findViewById(btnDayIDs[i]);
        }
        for (int i = 0; i < btnTime.length; i++) {
            btnTime[i] = findViewById(btnTimeIds[i]);
        }
        plistBtnSave = findViewById(R.id.plistBtnSave);
    }
}