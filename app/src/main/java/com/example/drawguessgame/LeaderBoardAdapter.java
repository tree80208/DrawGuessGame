package com.example.drawguessgame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class LeaderBoardAdapter extends ArrayAdapter<UserProfile> {
    private Context context;
    private List<UserProfile> profiles;
    int textViewResourceId;

    public LeaderBoardAdapter(Context context, int textViewResourceId, List<UserProfile> profiles){
        super(context,textViewResourceId,profiles);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.profiles = profiles;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View row_view = convertView;
        if(row_view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row_view = inflater.inflate(this.textViewResourceId,null);
        }
        TextView name_view = (TextView) row_view.findViewById(R.id.leader_board_list_row_name);
        TextView score_view = (TextView) row_view.findViewById(R.id.leader_board_list_row_score);

        if( name_view!=null && score_view != null){
            UserProfile curr = profiles.get(position);
            name_view.setText(curr.getName());
            score_view.setText(curr.getScore());
        }

        return row_view;
    }



}