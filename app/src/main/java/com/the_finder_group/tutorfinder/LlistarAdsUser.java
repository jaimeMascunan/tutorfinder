package com.the_finder_group.tutorfinder;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.the_finder_group.tutorfinder.ConnManager.AdDTO;
import com.the_finder_group.tutorfinder.ConnManager.TFClientImple;
import com.the_finder_group.tutorfinder.Helper.ContactsAdapterProducts;
import com.the_finder_group.tutorfinder.Helper.Helper;
import com.the_finder_group.tutorfinder.Helper.SQLiteHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LlistarAdsUser extends AppCompatActivity {

    private static final String TAG = LlistarAdsUser.class.getSimpleName();
    private static final int POPUP_MENU_OWN_COURSES = 1;
    private static final int POPUP_MENU_OTHER_COURSES = 2;

    private RecyclerView recyclerView;
    private List<AdDTO> productList;
    private ContactsAdapterProducts mAdapter;
    private SearchView searchView;
    private String db_loggedUserType, db_user_id;
    private Integer userRoleIdList, userId, popupOption;

    private SQLiteHandler db;
    private TFClientImple tfClientImple;
    private Helper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llistar_productes);
        Toolbar toolbar = findViewById(R.id.toolbar_search_producte);
        setSupportActionBar(toolbar);

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_view_products);
        productList = new ArrayList<>();
        mAdapter = new ContactsAdapterProducts(this, productList);

        //TFClient implementation
        tfClientImple = new TFClientImple();

        helper = new Helper(getApplicationContext());

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();
        //Id de l'usuari que esta realitzant la publicacio
        db_loggedUserType = user.get(getResources().getString(R.string.user_type));
        //Es user_id en compte de id com esta definit a la creacio de la taula perque ho tenim definit aixi a getUserDetails de la DB
        db_user_id = user.get("user_id");

        Intent intentGetListType = getIntent();

        if(intentGetListType.hasExtra("user_role_id")){
            userRoleIdList = Integer.parseInt(intentGetListType.getStringExtra("user_role_id"));
            fetchCoursesRole();
            if (((popupOption = (db.getOptionPopUP()))==null)||((db.getOptionPopUP()==0))) {
                db.addPopUpOption(POPUP_MENU_OTHER_COURSES);
            }else{
                db.updateMenuPoputOption(popupOption, POPUP_MENU_OTHER_COURSES);
            }
        }

        if(intentGetListType.hasExtra("user_id_list_published_courses")){
           userId = Integer.parseInt(intentGetListType.getStringExtra("user_id_list_published_courses"));
           if(userId == Integer.parseInt(db_user_id)){
               fetchCoursesUser();
               if (((popupOption = (db.getOptionPopUP()))==null)||((db.getOptionPopUP()==0))) {
                   db.addPopUpOption(POPUP_MENU_OWN_COURSES);
               }else{
                   db.updateMenuPoputOption(popupOption, POPUP_MENU_OWN_COURSES);
               }
           }
        }
        // white background notification bar
        whiteNotificationBar(recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }
    /**
     * Obtenim la llista d'usuaris
     */
    public void fetchCoursesRole() {
        new obtindreLlistatRole().execute();
    }

    public void fetchCoursesUser() {new obtindreLlistatUser().execute(); }

    private class obtindreLlistatRole extends AsyncTask<String, Void, List<AdDTO>> {

        @Override
        protected void onPreExecute() {
        }
        @Override
        //Instaniem un objecte de la clase tfClientImpl per realitzar la conexio
        protected List<AdDTO> doInBackground(String... strings) {
            List<AdDTO> llistatProductesRole = tfClientImple.listProductsRole(userRoleIdList);

            return llistatProductesRole;
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

    private class obtindreLlistatUser extends AsyncTask<String, Void, List<AdDTO>> {

        @Override
        protected void onPreExecute() {
        }
        @Override
        //Instaniem un objecte de la clase tfClientImpl per realitzar la conexio
        protected List<AdDTO> doInBackground(String... strings) {
            List<AdDTO> llistatProductesUser = tfClientImple.listProductsUser(userId);

            return llistatProductesUser;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) { return true; }

        if(id == android.R.id.home) {
            helper.redirectUserTypeAct(db_loggedUserType);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //desabilitamos volver atras de la MainActivity
        moveTaskToBack(true);
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }
}
