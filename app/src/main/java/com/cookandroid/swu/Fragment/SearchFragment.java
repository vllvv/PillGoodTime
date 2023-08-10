package com.cookandroid.swu.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.cookandroid.swu.DsSearch;
import com.cookandroid.swu.PillSearch;
import com.cookandroid.swu.R;

public class SearchFragment extends Fragment {
    TextView pill,drugstore;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        pill = (TextView)v.findViewById(R.id.search_pill);
        pill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(getActivity(), PillSearch.class);
                startActivity(intent);
            }
        });
        drugstore = (TextView) v.findViewById(R.id.search_drugstore);
        drugstore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DsSearch.class);
                startActivity(intent);
            }
        });


        return v;
    }
}