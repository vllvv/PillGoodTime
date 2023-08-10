package com.cookandroid.swu.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.cookandroid.swu.R;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;


public class SetFragment extends Fragment {
    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("설정");
    }

    LabeledSwitch toggle1,toggle2,toggle3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set, container, false);

        toggle1=view.findViewById(R.id.toggle1);
        toggle2=view.findViewById(R.id.toggle2);
        toggle3=view.findViewById(R.id.toggle3);

        toggle1.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn){
                    toggle1.setOn(true);
                    toggle2.setOn(true);
                    toggle3.setOn(true);
                }else{
                    toggle1.setOn(false);
                    toggle2.setOn(false);
                    toggle3.setOn(false);
                }
            }
        });
        toggle2.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn){
                    toggle2.setOn(true);


                }else{
                    toggle2.setOn(false);
                }
            }
        });
        toggle3.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn){
                    toggle3.setOn(true);
                }else{
                    toggle3.setOn(false);
                }
            }
        });

        return view;
    }

}