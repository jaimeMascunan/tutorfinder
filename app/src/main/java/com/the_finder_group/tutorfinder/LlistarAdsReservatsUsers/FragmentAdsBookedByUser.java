package com.the_finder_group.tutorfinder.LlistarAdsReservatsUsers;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.the_finder_group.tutorfinder.ConnManager.AdDTO;
import com.the_finder_group.tutorfinder.ConnManager.TFClientImple;
import com.the_finder_group.tutorfinder.Helper.ContactsAdapterProducts;
import com.the_finder_group.tutorfinder.Helper.SQLiteHandler;
import com.the_finder_group.tutorfinder.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FragmentAdsBookedByUser extends Fragment {

    View v;
    private static final int POPUP_MENU_BOOKED_COURSES = 3;
    private RecyclerView recyclerView;
    private List<AdDTO> productList;
    private ContactsAdapterProducts mAdapter;
    private TFClientImple tfClientImple;
    private SQLiteHandler db;
    private Integer popupOption, queryUserId;

    public FragmentAdsBookedByUser(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.list_students_fragment, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = getView().findViewById(R.id.recycler_view_products);
        productList = new ArrayList<>();
        mAdapter = new ContactsAdapterProducts(getActivity(), productList);

        // SqLite database handler
        db = new SQLiteHandler(getActivity().getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();
        //Id de l'usuari que esta realitzant la consulta
        queryUserId = Integer.parseInt(user.get("user_id"));

        if (((popupOption = (db.getOptionPopUP()))==null)||((db.getOptionPopUP()==0))) {
            db.addPopUpOption(POPUP_MENU_BOOKED_COURSES);
            Log.d("popupOption", String.valueOf(popupOption));
        }else{
            db.updateMenuPoputOption(popupOption, POPUP_MENU_BOOKED_COURSES);
            Log.d("popupOption", String.valueOf(popupOption));
        }

        //TFClient implementation
        tfClientImple = new TFClientImple();
        fetchAdsBookedByUser();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    public void fetchAdsBookedByUser(){
        new obtindreLlistatAdsBookedByUser().execute();
    }

    private class obtindreLlistatAdsBookedByUser extends AsyncTask<String, Void, List<AdDTO>> {

        @Override
        protected void onPreExecute() {
        }
        @Override
        //Instaniem un objecte de la clase tfClientImpl per realitzar la conexio
        protected List<AdDTO> doInBackground(String... strings) {
            List<AdDTO> listAds = tfClientImple.listBookAdsUser(queryUserId);

            return listAds;
        }
        @Override

        protected void onPostExecute(List<AdDTO> result) {
            super.onPostExecute(result);
            productList.clear();
            productList.addAll(result);
            // refreshing recycler view
            mAdapter.notifyDataSetChanged();
        }
    }
}
