package com.example.diabcalc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Επεξεργασία scroll listview, για φιλτράρισμα λιστών
 */
public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<String> expandableListTitle;
    private final HashMap<String, List<String>> expandableListDetail;
    private final ArrayList<String> checkedCategories;
    private final ArrayList<String> checkedBrands;
    private final ArrayList<Scroll> temp;
    View view;


    public CustomExpandableListAdapter(Context context, List<String> expandableListTitle, HashMap<String, List<String>> expandableListDetail) {
        checkedCategories = new ArrayList<>();
        checkedBrands = new ArrayList<>();
        temp = new ArrayList<>();
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
        for(String id : expandableListDetail.keySet())
            for(String name: Objects.requireNonNull(expandableListDetail.get(id)))
                temp.add(new Scroll(name));

    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return Objects.requireNonNull(this.expandableListDetail.get(this.expandableListTitle.get(listPosition))).get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    public Scroll getBox(String name) {
        for(Scroll scroll: temp) {
            if (name.equals(scroll.getName()))
                return scroll;
        }
        return null;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String expandedListText = (String) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.checkbox, null);
        }
        view = convertView;
        Scroll scroll= getBox(expandedListText);
        CheckBox checkBox = convertView.findViewById(R.id.expandedListItem);
        checkBox.setText(scroll.getName());
        checkBox.setChecked(scroll.getCheck());
        checkBox.setOnClickListener(view -> {
            temp.remove(scroll);
            scroll.setCheck(!scroll.getCheck());
            temp.add(scroll);
            notifyDataSetChanged();
            if(listPosition == 0 ) {
                if (checkBox.isChecked() && !checkedBrands.contains(expandedListText))
                    checkedBrands.add(expandedListText);
                else if (!checkBox.isChecked())
                    checkedBrands.remove(expandedListText);
            }
            else if(listPosition == 1 ) {
                if (checkBox.isChecked() && !checkedCategories.contains(expandedListText))
                    checkedCategories.add(expandedListText);
                else if (!checkBox.isChecked())
                    checkedCategories.remove(expandedListText);
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return Objects.requireNonNull(this.expandableListDetail.get(this.expandableListTitle.get(listPosition))).size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.group_name, null);
        }
        TextView listTitleTextView =  convertView
                .findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return false;
    }

    public HashMap<String,ArrayList<String>> getChecked() {
        HashMap<String,ArrayList<String>> hashMap = new HashMap<>();
        hashMap.put(context.getResources().getString(R.string.category), checkedCategories);
        hashMap.put(context.getResources().getString(R.string.brand),checkedBrands);
        return hashMap;
    }
}