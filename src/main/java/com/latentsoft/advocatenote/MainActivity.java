package com.latentsoft.advocatenote;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.latentsoft.advocatenote.model.AdvocateModel;
import com.latentsoft.advocatenote.model.CaseModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView tvAdvocateName, tvAdvocateCourt;
    ListView lvCaseContainer;
    TextView tvCaseContainerDialog;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    ArrayList<CaseModel> allCase = new ArrayList<>();
    BaseAdapter adapter;
    DatabaseReference advocateInfo;
    AdvocateModel model;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        lvCaseContainer = findViewById(R.id.lv_case_container);
        tvCaseContainerDialog = findViewById(R.id.tv_case_container_dialog);
        firebaseDatabase = FirebaseDatabase.getInstance();
        sp = getSharedPreferences("advocate_memory", MODE_PRIVATE);
        reference = firebaseDatabase.getReference("user/"+FirebaseAuth.getInstance().getUid()+"/Case");
        advocateInfo = firebaseDatabase.getReference("user/"+FirebaseAuth.getInstance().getUid()+"/advocateInfo");


        setSupportActionBar(toolbar);

        getValue();

        setAdapter();

        lvCaseContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CaseModel caseModel = allCase.get(position);
                startActivity(new Intent(MainActivity.this, CaseDetailsActivity.class).putExtra("caseModel", caseModel));
            }
        });



        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddCaseActivity.class));
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        tvAdvocateName = headerView.findViewById(R.id.tv_advocate_info_name);
        tvAdvocateCourt = headerView.findViewById(R.id.tv_advocate_info_court);

        setNavigationInfo();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setNavigationInfo() {

        advocateInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AdvocateModel model = dataSnapshot.getValue(AdvocateModel.class);
                tvAdvocateName.setText(model.getName());
                tvAdvocateCourt.setText(model.getCourt());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getValue(){

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allCase.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    CaseModel caseModel = data.child("info").getValue(CaseModel.class);
                    allCase.add(caseModel);

                    if(allCase.isEmpty() || allCase.size() == 0){
                        tvCaseContainerDialog.setVisibility(View.VISIBLE);
                        lvCaseContainer.setVisibility(View.GONE);
                    } else{
                        tvCaseContainerDialog.setVisibility(View.GONE);
                        lvCaseContainer.setVisibility(View.VISIBLE);
                    }
                }
                sortAllCases();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sortAllCases() {
        Collections.sort(allCase, new Comparator<CaseModel>() {
            @Override
            public int compare(CaseModel o1, CaseModel o2) {
                return (int)(o1.getDate() - o2.getDate());
            }
        });
    }


    private void setAdapter() {
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return allCase.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                convertView = getLayoutInflater().inflate(R.layout.case_container, parent, false);

                TextView tvCaseTitle = convertView.findViewById(R.id.tv_case_title);
                TextView tvDefendantName = convertView.findViewById(R.id.tv_defendant_name);
                TextView tvComplainantName = convertView.findViewById(R.id.tv_complainant_name);
                TextView tvHearingDate = convertView.findViewById(R.id.tv_hearing_date);


                tvCaseTitle.setText(allCase.get(position).getTitle());
                tvDefendantName.setText(allCase.get(position).getDefendant());
                tvComplainantName.setText(allCase.get(position).getComplainant());

                String formet = "MMM dd, yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(formet);

                tvHearingDate.setText(sdf.format(allCase.get(position).getDate()));


                return convertView;
            }
        };
        lvCaseContainer.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.log_out) {

            new AlertDialog.Builder(this)
                    .setTitle("Log Out")
                    .setMessage("Are you sure to log out?")
                    .setCancelable(false)
                    .setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseAuth.getInstance().signOut();
                            sp.edit().putBoolean("isLogged", false).apply();
                            startActivity(new Intent(MainActivity.this, LogInActivity.class));
                            finish();
                        }
                    })
                    .show();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
