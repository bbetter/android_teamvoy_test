package com.example.andriypuhach.android_teamvoy_test.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.andriypuhach.android_teamvoy_test.R;
import com.example.andriypuhach.android_teamvoy_test.models.Account;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONArray;

import java.util.Arrays;

/**
 * Created by andriypuhach on 1/21/15.
 */
public class EditAccountDialog extends Dialog {

    private Activity activity;
    public Account account;
    public EditAccountDialog(Activity act,Account account){
        super(act);
        this.activity=act;
        this.account=account;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_account_dialog);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        final EditText editNameText = (EditText) findViewById(R.id.accountEditName);
        final EditText editSurnameText = (EditText) findViewById(R.id.accountEditSurname);
        final Spinner statusEdit= (Spinner) findViewById(R.id.accountEditStatus);
        Resources res = activity.getResources();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, Arrays.asList(res.getStringArray(R.array.sp_status)));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusEdit.setAdapter(adapter);
        final EditText editAboutText=(EditText)findViewById(R.id.accountEditAboutMe);
        final DatePicker editBirthday=(DatePicker)findViewById(R.id.accountBirthday);
        Button button =(Button)findViewById(R.id.submitEditAccount);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account.setAbout(editAboutText.getText().toString());
                account.setName(editNameText.getText().toString());
                account.setSurname(editSurnameText.getText().toString());
                account.setRelationships(statusEdit.getSelectedItem().toString());
                account.setBirthday(new DateTime(editBirthday.getYear(),editBirthday.getMonth()+1,editBirthday.getDayOfMonth(), 0,0));
                dismiss();
            }
        });
        Button cancelButton=(Button)findViewById(R.id.cancelEditAccount);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        editNameText.setText(account.getName());
        editSurnameText.setText(account.getSurname());
        editAboutText.setText(account.getAbout());
        DateTime dateTime=account.getBirthday();
        editBirthday.updateDate(dateTime.getYear(),dateTime.getMonthOfYear()-1,dateTime.getDayOfMonth());
    }
}
