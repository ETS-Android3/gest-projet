package com.example.android.employeesmanagementsoftware;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.android.employeesmanagementsoftware.DepartmentDB.DepFragment;
import com.example.android.employeesmanagementsoftware.DepartmentDB.DepartmentActivity;
import com.example.android.employeesmanagementsoftware.data.DBHelpers.EmployeesManagementDbHelper;
import com.example.android.employeesmanagementsoftware.data.Contracts.DepartmentContract.DepartmentEntry;
import com.example.android.employeesmanagementsoftware.DepartmentDB.DepartementRowData.DepartmentItem;
import com.google.android.material.snackbar.Snackbar;

public class DepartmentCreation extends AppCompatActivity {
    private EditText description;
    private EditText nameOfDepartment;
    private EmployeesManagementDbHelper emdb ;
    private DepFragment depFragment = DepFragment.newInstance(0);
    private Button save;
    private Intent intent;
    private long departmentId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depaetment_creation);
        intent = getIntent();
        boolean IsEditable = intent.getExtras().getBoolean("IsEdit");
        emdb =  new EmployeesManagementDbHelper(this);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        description= findViewById(R.id.department_description);
        nameOfDepartment= findViewById(R.id.department_name);
        save= findViewById(R.id.save);
        if (IsEditable) {
            updateAction();
        } else {
            AddNewDepartemnt();
        }
    }
    private void AddNewDepartemnt(){
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!((nameOfDepartment.getText().toString()).matches("^\\w+( \\w+)*$")) ||!((description.getText().toString()).matches("^[\\s\\S]{2,200}$")))
                    Snackbar.make(v, "Certaines ou toutes les valeurs entrées semblent incorrectes.", Snackbar.LENGTH_LONG).setAction("", null).show();
                else{
                    boolean flag =   emdb.addDepartment( nameOfDepartment.getText().toString(),description.getText().toString());
                    actionSave(flag, v, false);
                }
            }
        });

    }

    private void updateAction() {
        departmentId = intent.getExtras().getLong("depatmentID");
        Cursor cursorDep = emdb.getDepartment(departmentId);
        Log.v("Dep cre cur" , ""+departmentId);
        if (cursorDep.moveToFirst()) {
            description.setText(cursorDep.getString(cursorDep.getColumnIndex(DepartmentEntry.COLUMN_DEPARTMENT_DESCRIPTION)));
            nameOfDepartment.setText(cursorDep.getString(cursorDep.getColumnIndex(DepartmentEntry.COLUMN_DEPARTMENT_NAME)));
        }
        cursorDep.close();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!((nameOfDepartment.getText().toString()).matches("^\\w+( \\w+)*$")) || !((description.getText().toString()).matches("^[\\s\\S]{2,200}$"))) {
                    Snackbar.make(v, "SOME OR ALL INPUTS ARE INVALID. PLEASE ENTER VALID VALUES.", Snackbar.LENGTH_LONG).setAction("", null).show();
                } else {
                    boolean correct = emdb.updateDepartment(new DepartmentItem(departmentId,nameOfDepartment.getText().toString(),description.getText().toString()));
                    actionSave(correct, v, true);

                }

            }
        });

    }
    private  void actionSave(boolean flag,View v, boolean isEdit) {
        if(flag){
            if(!isEdit)
            Snackbar.make(v, "Enregistrement reussi", Snackbar.LENGTH_LONG).setAction("", null).show();
            else
                Snackbar.make(v, "modification reussie", Snackbar.LENGTH_LONG).setAction("", null).show();
            description.setText("",TextView.BufferType.EDITABLE);
            nameOfDepartment.setText("",TextView.BufferType.EDITABLE);
            depFragment.updateDepartmentList(emdb);
            Intent intent2 = new Intent(getBaseContext(), DepartmentActivity.class);
            intent2.putExtra("departmentId", departmentId);
            this.finish();
            startActivity(intent2);
        }
        else
            Snackbar.make(v, "Echec lors de l'insertion du departement. reesayez plutard.", Snackbar.LENGTH_LONG).setAction("", null).show();
    }
}



