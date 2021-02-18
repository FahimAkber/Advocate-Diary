package com.latentsoft.advocatenote;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.latentsoft.advocatenote.model.CaseModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddCaseActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etTitle, etDescription, etComplainant, etDefendant;
    TextView tvHearingDate;
    Button btnSave;
    Calendar calendar;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    long timeMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_case);

        init();

        btnSave.setOnClickListener(this);
        tvHearingDate.setOnClickListener(this);


    }

    private void init() {
        calendar = Calendar.getInstance();
        etTitle = findViewById(R.id.et_add_case_title);
        etDescription = findViewById(R.id.et_add_case_description);
        etComplainant = findViewById(R.id.et_add_case_complainant);
        etDefendant = findViewById(R.id.et_add_case_defendant);
        tvHearingDate = findViewById(R.id.tv_hearing_date);
        btnSave = findViewById(R.id.btn_add_case_save);
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("user/"+FirebaseAuth.getInstance().getUid()+"/Case");
    }

    @Override
    public void onClick(View v) {
        if(v == btnSave){
            DatabaseReference pushRef = reference.push();

            String id = pushRef.getKey();
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String complainant = etComplainant.getText().toString().trim();
            String defendant = etDefendant.getText().toString().trim();
            String date = tvHearingDate.getText().toString().trim();

            if(title.isEmpty() || description.isEmpty() || complainant.isEmpty() || defendant.isEmpty()){
                Toast.makeText(this, "Please fill the gap.", Toast.LENGTH_SHORT).show();
                return;
            }
            if(date.isEmpty()){
                Toast.makeText(this, "Please Select the date.", Toast.LENGTH_SHORT).show();
                return;
            }

            CaseModel model = new CaseModel(id, title, description, complainant, defendant, timeMillis);
            pushRef.child("info").setValue(model);
            finish();
        }

        if(v == tvHearingDate){
            DatePickerDialog.OnDateSetListener ods = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    timeMillis = calendar.getTimeInMillis();

                    String formet = "MMM dd, yyyy";
                    SimpleDateFormat sdf = new SimpleDateFormat(formet);
                    tvHearingDate.setText(sdf.format(calendar.getTime()));

                }
            };

            DatePickerDialog dpd = new DatePickerDialog(this, ods, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        }
    }
}
