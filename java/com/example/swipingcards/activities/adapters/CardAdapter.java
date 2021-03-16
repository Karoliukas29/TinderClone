package com.example.swipingcards.activities.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.swipingcards.R;
import com.example.swipingcards.activities.User;
import com.huxq17.swipecardsview.BaseCardAdapter;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class CardAdapter extends BaseCardAdapter {
    public List<User> userList;
    public Context context;

    public CardAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public int getCardLayoutId() {
        return R.layout.item;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindData(int position, View cardview) {
    if (userList == null || userList.size() == 0){
        return;
    }
        ImageView imageView = (ImageView)cardview.findViewById(R.id.usersImageView);
        TextView textView = (TextView)cardview.findViewById(R.id.textNameAgeView);
        User user = userList.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        Date firstDate = null;
        try {
            firstDate = sdf.parse(user.dateOfBirth);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date secondDate = new Date();
        try {
            secondDate = sdf.parse(sdf.format(secondDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        String age = (String.valueOf((int) diff / 365));
        textView.setText(user.userName + " " +  age);

        String imageUrl;
        imageUrl = user.userImages.get(0).imageUrl;

        Picasso.get().load(imageUrl).into(imageView);


    }
}


