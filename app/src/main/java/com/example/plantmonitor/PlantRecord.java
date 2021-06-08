package com.example.plantmonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PlantRecord extends AppCompatActivity {

    List<recorddatelist> record = new ArrayList<>();
    private RecyclerView plantrecyclerView;

    final OkHttpClient client = new OkHttpClient();
    public ExecutorService service = Executors.newSingleThreadExecutor();
    Gson gson = new Gson();
    public MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private SectionedRecyclerViewAdapter sectionedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_record);

        plantrecyclerView = findViewById(R.id.plantrecyclerView);

        plantrecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        sectionedAdapter = new SectionedRecyclerViewAdapter();

        service.submit(new Runnable() {
            @Override
            public void run() {

                Request request = new Request.Builder().url("http://192.168.10.114:9402/Login/getrecord").get().build();
                try {
                    final Response response = client.newCall(request).execute();
                    final String resStr = response.body().string();
                    Log.e("", resStr);
                    JSONObject jsonObject = new JSONObject(resStr);
                    String array = jsonObject.getString("JsonResult");
                    record.clear();
                    record.addAll(gson.fromJson(array, new TypeToken<List<recorddatelist>>() {
                    }.getType()));
                    Log.e("結果", record.size() + "");

                    runOnUiThread(new Runnable() {
                        @Override


                        public void run() {


                            record.stream().map(x->x.date).distinct().forEach(x->{

                                List<recorddatelist> list = record.stream().filter(y->y.date.equals(x)).collect(Collectors.toList());

                                ExpandableContactsSection section = new ExpandableContactsSection (x,list);

                                sectionedAdapter.addSection(section);


                            });
                            plantrecyclerView.setAdapter(sectionedAdapter);
                        }
                    });


                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    class ExpandableContactsSection extends Section {

        private final String title;
        private final List<recorddatelist> SectionList;


        private boolean expanded = false;

        ExpandableContactsSection(@NonNull String title, @NonNull List<recorddatelist> list
        ) {
            super(SectionParameters.builder()
                    .itemResourceId(R.layout.plant_record_cardview)
                    .headerResourceId(R.layout.plant_record_header)
                    .build());

            this.title = title;
            this.SectionList = list;

        }

        class ItemViewHolder extends RecyclerView.ViewHolder {

            final View rootView;
            final TextView item_time;
            final TextView item_sw;
            final TextView item_at;
            final TextView item_aw;


            ItemViewHolder(@NonNull View view) {
                super(view);

                rootView = view;
                item_time = view.findViewById(R.id.item_time);
                item_sw = view.findViewById(R.id.item_sw);
                item_at = view.findViewById(R.id.item_at);
                item_aw = view.findViewById(R.id.item_aw);
            }
        }

        class HeaderViewHolder extends RecyclerView.ViewHolder {

            final View rootView;
            final TextView tvTitle;
            final ImageView imgArrow;

            HeaderViewHolder(@NonNull View view) {
                super(view);

                rootView = view;
                tvTitle = view.findViewById(R.id.recodeTitle);
                imgArrow = view.findViewById(R.id.imgArrow);
            }
        }


        @Override
        public int getContentItemsTotal() {
            return expanded ? SectionList.size() : 0;
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(final View view) {
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            final ItemViewHolder itemHolder = (ItemViewHolder) holder;

            final recorddatelist fireItem = SectionList.get(position);

            itemHolder.item_time.setText(fireItem.time);
            itemHolder.item_sw.setText(fireItem.sw);
            itemHolder.item_at.setText(fireItem.at);
            itemHolder.item_aw.setText(fireItem.aw);





            itemHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                }
            });


            itemHolder.rootView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View view) {




                    return true;
                }
            });
        }


        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(final View view) {
            return new HeaderViewHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(final RecyclerView.ViewHolder holder) {
            final HeaderViewHolder headerHolder = (HeaderViewHolder) holder;


            int index = sectionedAdapter.getSectionIndex(this) + 1;

            headerHolder.tvTitle.setText(title +" 有"+SectionList.size()+"筆");
            headerHolder.imgArrow.setImageResource(
                    expanded ? R.drawable.ic_keyboard_arrow_up_black_18dp : R.drawable.ic_keyboard_arrow_down_black_18dp
            );




////////////////////////////////////////////////////////////////////////////////////
            headerHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final SectionAdapter sectionAdapter = sectionedAdapter.getAdapterForSection(ExpandableContactsSection.this);


                    // store info of current section state before changing its state
                    final boolean wasExpanded = ExpandableContactsSection.this.isExpanded();
                    final int previousItemsTotal = ExpandableContactsSection.this.getContentItemsTotal();

                    ExpandableContactsSection.this.setExpanded(!wasExpanded);
                    sectionAdapter.notifyHeaderChanged();

                    if (wasExpanded) {
                        sectionAdapter.notifyItemRangeRemoved(0, previousItemsTotal);
                    } else {

                        sectionAdapter.notifyAllItemsInserted();
                    }


                }
            });


        }

        boolean isExpanded() {
            return expanded;
        }

        void setExpanded(final boolean expanded) {
            this.expanded = expanded;
        }

////////////////////////////////////////////////////////////////////////////////////
    }


    public class recorddatelist {

        public String date;
        public String time;
        public String sw;
        public String at;
        public String aw;
    }

}