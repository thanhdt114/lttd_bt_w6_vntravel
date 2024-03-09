package com.example.vntravel.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.vntravel.About;
import com.example.vntravel.Detail;
import com.example.vntravel.MainActivity;
import com.example.vntravel.R;
import com.example.vntravel.fragment.placeholder.PlaceholderContent;
import com.example.vntravel.reqCallBack.PlaceReqCallBack;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.concurrent.Executors;

/**
 * A fragment representing a list of Items.
 */
public class ItemFragment extends Fragment{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ItemFragment newInstance(int columnCount) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyItemRecyclerViewAdapter(PlaceholderContent.ITEMS));
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        CronetEngine.Builder builder = new CronetEngine.Builder(this.requireContext());
        CronetEngine engine = builder.build();

        UrlRequest urlReq = engine.newUrlRequestBuilder("http://192.168.31.220:8080/movies",
                        new PlaceReqCallBack(
                                (jsonArray -> {
                                    for (int i=0; i< jsonArray.length(); i++){
                                        try {
                                            JSONObject movie = (JSONObject) jsonArray.get(i);
                                            String name = movie.getString("name");
                                            String desc = movie.getString("desc");
                                            String poster = movie.getString("poster");
                                            PlaceholderContent.addItem(PlaceholderContent.createPlaceholderItem(i, name));
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                })
                        ), Executors.newSingleThreadExecutor())
                .setHttpMethod("GET").build();
        urlReq.start();
    }
}