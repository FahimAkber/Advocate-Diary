package com.latentsoft.advocatenote;

import android.app.DatePickerDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.latentsoft.advocatenote.model.CaseModel;
import com.latentsoft.advocatenote.model.HistoryModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddHearingActivity extends AppCompatActivity {
    EditText etComment, etNextStep;
    TextView tvNextHearingDate;
    Button btnAddToDairy;
    String id;
    CaseModel caseModel;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    Calendar calendar;
    long timeMilis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hearing);

        init();

        DatabaseReference infoRef = firebaseDatabase.getReference("user/" + FirebaseAuth.getInstance().getUid() + "/Case/" + id + "/info");

        infoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                caseModel = dataSnapshot.getValue(CaseModel.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        tvNextHearingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener ods = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        timeMilis = calendar.getTimeInMillis();

                        String formet = "MMM dd, yyyy";
                        SimpleDateFormat sdf = new SimpleDateFormat(formet);
                        tvNextHearingDate.setText(sdf.format(calendar.getTime()));

                    }
                };

                DatePickerDialog dpd = new DatePickerDialog(AddHearingActivity.this, ods, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });

        btnAddToDairy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = etComment.getText().toString().trim();
                String nextStep = etNextStep.getText().toString().trim();
                String date = tvNextHearingDate.getText().toString();

                if (date.isEmpty()) {
                    Toast.makeText(AddHearingActivity.this, "Please set hearing date!!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                CaseModel model = new CaseModel(caseModel.getId(), caseModel.getTitle(), caseModel.getDescription(), caseModel.getComplainant(), caseModel.getDefendant(), timeMilis);
                reference.child("info").setValue(model);
                DatabaseReference history = reference.child("history").push();
                HistoryModel hisModel = new HistoryModel(history.getKey(), comment, nextStep, caseModel.getDate());
                history.setValue(hisModel);
                setResult(RESULT_OK);
                finish();
            }
        });

    }

    private void init() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        etComment = findViewById(R.id.et_comment);
        tvNextHearingDate = findViewById(R.id.tv_next_hearing);
        calendar = Calendar.getInstance();
        etNextStep = findViewById(R.id.et_step);
        btnAddToDairy = findViewById(R.id.btn_add_to_dairy);
        id =getIntent().getStringExtra("infoKey");
        reference = firebaseDatabase.getReference("user/" + FirebaseAuth.getInstance().getUid() + "/Case/" + id);
    }
}
