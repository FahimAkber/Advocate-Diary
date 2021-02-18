package com.latentsoft.advocatenote;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.latentsoft.advocatenote.model.CaseModel;
import com.latentsoft.advocatenote.model.HistoryModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CaseDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvTitle, tvDescription, tvHearingDate, tvDefendant, tvComplinant;
    ListView lvHistory;
    FloatingActionButton fab;
    Intent intent;
    String id;
    CaseModel caseModel;
    BaseAdapter adapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    ArrayList<HistoryModel> allHistory;
    private final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_details);

        init();

        setValue();

        fab.setOnClickListener(this);

        setAdapter();

    }

    private void setAdapter() {

        reference = firebaseDatabase.getReference("user/" + FirebaseAuth.getInstance().getUid() + "/Case/" + id + "/history");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allHistory.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    HistoryModel model = data.getValue(HistoryModel.class);
                    allHistory.add(model);
                }
                sortHistory();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return allHistory.size();
            }

            @Override
            public HistoryModel getItem(int position) {
                return allHistory.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                convertView = getLayoutInflater().inflate(R.layout.history_container, parent, false);

                TextView date = convertView.findViewById(R.id.tv_history_container_date);
                TextView previousStep = convertView.findViewById(R.id.tv_history_container_previous_Step);
                TextView comment = convertView.findViewById(R.id.tv_history_container_comment);

                String formet = "MMM dd, yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(formet);

                date.setText(sdf.format(getItem(position).getDate()));
                previousStep.setText(getItem(position).getNextStep());
                comment.setText(getItem(position).getComment());

                return convertView;
            }

        };
        lvHistory.setAdapter(adapter);
    }

    private void sortHistory() {
        Collections.sort(allHistory, new Comparator<HistoryModel>() {
            @Override
            public int compare(HistoryModel o1, HistoryModel o2) {
                return (int)(o2.getDate() - o1.getDate());
            }
        });
    }

    private void setValue() {

        DatabaseReference infoRef = firebaseDatabase.getReference("user/" + FirebaseAuth.getInstance().getUid() + "/Case/" + id + "/info");

        infoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CaseModel value = dataSnapshot.getValue(CaseModel.class);

                tvTitle.setText(value.getTitle());
                tvDescription.setText(value.getDescription());

                String formet = "MMM dd, yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(formet);
                tvHearingDate.setText(sdf.format(value.getDate()));
                tvDefendant.setText(value.getDefendant());
                tvComplinant.setText(value.getComplainant());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void init() {
        intent = getIntent();
        caseModel = (CaseModel) intent.getSerializableExtra("caseModel");
        tvTitle = findViewById(R.id.tv_case_details_title);
        tvDescription = findViewById(R.id.tv_case_details_description);
        tvHearingDate = findViewById(R.id.tv_case_details_date);
        tvDefendant = findViewById(R.id.tv_case_details_defendant);
        tvComplinant = findViewById(R.id.tv_case_details_complainant);
        lvHistory = findViewById(R.id.lv_case_details_history);
        fab = findViewById(R.id.fab_add_new_date);
        firebaseDatabase = FirebaseDatabase.getInstance();
        id = caseModel.getId();
        allHistory = new ArrayList<>();
    }


    @Override
    public void onClick(View v) {
        if (v == fab) {
            startActivityForResult(new Intent(CaseDetailsActivity.this, AddHearingActivity.class).putExtra("infoKey", id), REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            setValue();
            adapter.notifyDataSetChanged();
        }

    }
}
