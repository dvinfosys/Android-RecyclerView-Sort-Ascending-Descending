package com.dvinfosys.listviewfilters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<CategoryGetSet> list;
    String CategoryItem, Nature;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Intent i;
    int q1 = 0;
    private Context context;
    private LayoutInflater inflater;
    private String CategoryName, Name, ContactNo, EmailID, Request, Status;
    private ArrayList<CategoryGetSet> arrayList;

    public CategoryAdapter(Context context, List<CategoryGetSet> list) {

        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.arrayList = new ArrayList<>();
        this.arrayList.addAll(list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.category_item, parent, false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final MyHolder myHolder = (MyHolder) holder;
        final CategoryGetSet getSet = list.get(position);

        myHolder.offertitle.setText(getSet.getIndustryName());

        if (q1 == 0) {
            myHolder.card.setCardBackgroundColor(Color.parseColor("#126586"));
            myHolder.offertitle.setTextColor(Color.WHITE);
            q1++;
        } else {
            myHolder.card.setCardBackgroundColor(Color.parseColor("#B87C1F"));
            myHolder.offertitle.setTextColor(Color.WHITE);
            q1--;
        }

        myHolder.offertitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.showToast(context, "Click : " + getSet.getIndustryName());
            }
        });
    }


    public void sortNameByAsc() {
        Comparator<CategoryGetSet> comparator = new Comparator<CategoryGetSet>() {

            @Override
            public int compare(CategoryGetSet object1, CategoryGetSet object2) {
                return object1.getIndustryName().compareToIgnoreCase(object2.getIndustryName());
            }
        };
        Collections.sort(list, comparator);
        notifyDataSetChanged();

    }

    public void sortNameByDesc() {
        Comparator<CategoryGetSet> comparator = new Comparator<CategoryGetSet>() {

            @Override
            public int compare(CategoryGetSet object1, CategoryGetSet object2) {
                return object2.getIndustryName().compareToIgnoreCase(object1.getIndustryName());
            }
        };
        Collections.sort(list, comparator);
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filter(String newText) {

        newText = newText.toLowerCase(Locale.getDefault());
        if (list.isEmpty()) {
            Toast.makeText(context, "No item found", Toast.LENGTH_SHORT).show();
        } else {
            list.clear();
            if (newText.length() == 0) {
                list.addAll(arrayList);
            } else {
                list.clear();
                for (CategoryGetSet wp : arrayList) {
                    if (wp.getIndustryName().toLowerCase(Locale.getDefault()).contains(newText)) {
                        list.add(wp);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    private class MyHolder extends RecyclerView.ViewHolder {
        TextView offertitle;
        CardView card;
        ImageView image1;

        public MyHolder(View view) {
            super(view);

            offertitle = (TextView) view.findViewById(R.id.category_item);
            card = (CardView) view.findViewById(R.id.item_card);
        }
    }
}
