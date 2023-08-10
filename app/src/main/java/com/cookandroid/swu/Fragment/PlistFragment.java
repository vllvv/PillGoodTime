package com.cookandroid.swu.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cookandroid.swu.AdapterPlistTime;
import com.cookandroid.swu.PlistActivity;
import com.cookandroid.swu.R;

public class PlistFragment extends Fragment{
    ListView lvPlist;
    Button fabAdd;
    //    final int REQUESTCODE_REVIEW_WRITE = 3;
    public static AdapterPlistTime plAdapter = new AdapterPlistTime();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plist, container, false);

        fabAdd = view.findViewById(R.id.fabAdd);
        lvPlist = view.findViewById(R.id.lvPlist);

        lvPlist.setAdapter(plAdapter);

        // 복용약 리스트 추가하기
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PlistActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("복용약 리스트");
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public static void addItem(Integer listCount, Bitmap icon, String name, String memo, String day,
                               String time1, String time2, String time3, String time4, String time5, String time6) {
        plAdapter.addItem(listCount, icon, name, memo, day, time1, time2, time3, time4, time5, time6);
        plAdapter.notifyDataSetChanged();
    }


}