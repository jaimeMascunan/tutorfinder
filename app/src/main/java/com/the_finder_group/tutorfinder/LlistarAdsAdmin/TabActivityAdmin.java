package com.the_finder_group.tutorfinder.LlistarAdsAdmin;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.the_finder_group.tutorfinder.Helper.Helper;
import com.the_finder_group.tutorfinder.Helper.SQLiteHandler;
import com.the_finder_group.tutorfinder.Helper.ViewPageAdapter;
import com.the_finder_group.tutorfinder.R;

import java.util.HashMap;

public class TabActivityAdmin extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPageAdapter viewPageAdapter;
    private Helper helper;
    private SQLiteHandler db;
    private String db_loggedUserType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        Toolbar toolbar = findViewById(R.id.toolbar_llistar_producte);
        setSupportActionBar(toolbar);
        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        helper = new Helper(getApplicationContext());

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();
        //Id de l'usuari que esta realitzant la publicacio
        db_loggedUserType = user.get(getResources().getString(R.string.user_type));

        tabLayout = (TabLayout)findViewById(R.id.tab_layout_id);
        viewPager = (ViewPager)findViewById(R.id.view_pager);
        viewPageAdapter =  new ViewPageAdapter(getSupportFragmentManager());

        viewPageAdapter.addFragment(new FragmentStudentsProducts(), "Cursos d'estudiants");
        viewPageAdapter.addFragment(new FragmentTutorsProducts(), "Cursos de tutors");

        viewPager.setAdapter(viewPageAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_school_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_school_black_24dp);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home) { helper.redirectUserTypeAct(db_loggedUserType); }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //desabilitamos volver atras de la MainActivity
        moveTaskToBack(true);
    }
}
