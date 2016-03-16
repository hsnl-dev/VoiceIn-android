package tw.kits.voicein.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import tw.kits.voicein.model.CountryCode;

/**
 * Created by Henry on 2016/3/6.
 */
public class CountryAdapter extends BaseAdapter {
    List<CountryCode> codeList;
    private Context context;
    public CountryAdapter(List<CountryCode> codes, Context iContext){
        this.codeList = codes;
        this.context = iContext;

    }
    @Override
    public int getCount() {
        return codeList.size();
    }

    @Override
    public Object getItem(int position) {
        return codeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v =layoutInflater.inflate(android.R.layout.simple_spinner_item, null);
        //TextView countryText = (TextView)v.findViewById(android.R.id.text2);
        TextView dcodeText = (TextView)v.findViewById(android.R.id.text1);
//        countryText.setText(codeList.get(position).getName());
        dcodeText.setText(codeList.get(position).getDialCode()+"-"+codeList.get(position).getName());
        return  v;

    }
    public int findByCode(String query){
        int i;
        for(i = 0 ;i < codeList.size() ; i++){
            if(codeList.get(i).getCode().equalsIgnoreCase(query)) {

                return i;
            }

        }
        return -1;
    }
}
