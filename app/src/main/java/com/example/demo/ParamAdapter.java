package com.example.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import java.util.ArrayList;

public class ParamAdapter extends BaseAdapter {
    public ArrayList<MainActivity.Param> list;
    public boolean changeStatus;
    private boolean last =false;
    private Context mContext;
    ViewHolder holder;
    public  ParamAdapter(Context context, String mData, boolean changeStatus){
        list = getList(mData);
        this.changeStatus = changeStatus;
        this.mContext = context;
    }
    public Object getItem(int position){
        return list.get(position);
    }
    public long getItemId(int arg0) {
        return 0;
    }
    public int getCount() {
        return list.size();
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder=new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false);
            holder.editButton = convertView.findViewById(R.id.editButton);
            holder.key = convertView.findViewById(R.id.key);
            holder.keyEdit = convertView.findViewById(R.id.keyEdit);
            holder.value = convertView.findViewById(R.id.value);
            holder.valueEdit = convertView.findViewById(R.id.valueEdit);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.keyEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if((!hasFocus)) {
                    EditText view = (EditText)v;
                    changeKey(view.getText().toString(), position);
                    //notifyDataSetChanged();
                }
            }
        });
        holder.valueEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    EditText view = (EditText)v;
                    changeValue(view.getText().toString(), position);
                    //notifyDataSetChanged();
                }
            }
        });
        holder.keyEdit.setText(list.get(position).key);
        holder.valueEdit.setText(list.get(position).value);
        if(changeStatus){
            holder.editButton.setVisibility(View.VISIBLE);
        }else {
            holder.editButton.setVisibility(View.INVISIBLE);
        }
        if(position == list.size()-1){
            holder.editButton.setText("Add");
        }else {
            holder.editButton.setText("Delete");
        }
        holder.editButton.setTag(position);
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button view = (Button) v;
                int position = (int) view.getTag();
                if(position == list.size()-1){
                    list.add(new MainActivity.Param("",""));
                }else {
                    list.remove(position);
                }
                notifyDataSetChanged();
            }
        });
        return convertView;
    }
    public final class ViewHolder {
        public Button editButton;
        public TextView key;
        public EditText keyEdit;
        public TextView value;
        public EditText valueEdit;
    }
    public void changeKey(String key,int position){
        list.get(position).key = key;
    }
    public void changeValue(String value,int position){
        list.get(position).value = value;
    }
    public ArrayList<MainActivity.Param> getList(String text){
        ArrayList<MainActivity.Param> list = new ArrayList<>();
        String subString = "";
        if(text.endsWith("}")&&text.startsWith("{")){
            subString = text.substring(text.indexOf("{")+1,text.lastIndexOf("}"));
            String [] array = subString.split(",");
            for (int i= 0;i<array.length;i++){
                if(array[i].split("=").length==0){
                    list.add(new MainActivity.Param("",""));
                }
                else if(array[i].startsWith("=")) {
                    list.add(new MainActivity.Param("", array[i].split("=")[1]));
                }else if(array[i].endsWith("=")){
                    list.add(new MainActivity.Param(array[i].split("=")[0], ""));
                }else{
                    list.add(new MainActivity.Param(array[i].split("=")[0], array[i].split("=")[1]));
                }
            }
            return list;
        }
        return  list;
    }
}
