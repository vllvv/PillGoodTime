package com.cookandroid.swu;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>() ;
    //필터링된 결과 데이터를 저장하기 위한 ArrayList 최초에는 전체 리스트 보유

    private TextView name,date,memo;
    private Button ok, yes, no;


    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.eboxlistview_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView1) ;
        TextView titleTextView = (TextView) convertView.findViewById(R.id.textView1) ;
        TextView descTextView = (TextView) convertView.findViewById(R.id.textView2) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ListViewItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        iconImageView.setImageBitmap(listViewItem.getIcon());
        titleTextView.setText(listViewItem.getTitle());
        descTextView.setText(listViewItem.getDesc());

        //파이어베이스-Ebox listviewadapter
        FirebaseDatabase Itemdb;
        DatabaseReference refItem;
        Itemdb = FirebaseDatabase.getInstance();
        refItem = Itemdb.getReference("itemdb");

        //커스텀다이얼로그 각 위젯 정의
        LinearLayout cmdArea = (LinearLayout) convertView.findViewById(R.id.cmdarea);
        cmdArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog eboxdialog;
                eboxdialog = new Dialog(view.getContext());
                eboxdialog.setContentView(R.layout.ebox_dia);
                eboxdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                name = (TextView)eboxdialog.findViewById(R.id.dia_name);
                date = (TextView)eboxdialog.findViewById(R.id.dia_date);
                memo = (TextView)eboxdialog.findViewById(R.id.dia_memo);
                ok = (Button)eboxdialog.findViewById(R.id.dia_save);

                name.setText(listViewItemList.get(pos).getTitle());
                date.setText(listViewItemList.get(pos).getDate());
                memo.setText(listViewItemList.get(pos).getMemo());
                eboxdialog.show();

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        eboxdialog.dismiss();
                    }
                });
            }
        });

        cmdArea.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view) {
                Dialog deletedia;
                deletedia = new Dialog(view.getContext());
                deletedia.setContentView(R.layout.dialogdel);
                deletedia.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                deletedia.show();

                yes = (Button)deletedia.findViewById(R.id.yes);
                no = (Button)deletedia.findViewById(R.id.no);

                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listViewItemList.remove(pos);
                        notifyDataSetChanged();
                        deletedia.dismiss();
                    }
                });

                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deletedia.dismiss();
                    }
                });

                return false;
            }
        });

        return convertView;
    }




    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(Bitmap icon, String title, String desc, String date, String memo) {
        ListViewItem item = new ListViewItem();

        item.setIcon(icon);
        item.setTitle(title);
        item.setDesc(desc);
        item.setDate(date);
        item.setMemo(memo);

        listViewItemList.add(item);


    }

}