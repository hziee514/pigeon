package wrh.pigeon;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;

public class EggInfoActivity extends Activity {

    public void onClickDateEdit(View view){
        final EditText editText = (EditText)view;
        String text = editText.getText().toString();
        final Calendar c = AddEggActivity.getCalendar(text);
        final DatePickerDialog dlg = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dlg.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dlg.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatePicker picker = dlg.getDatePicker();
                editText.setText(AddEggActivity.getDate(picker.getYear(), picker.getMonth(), picker.getDayOfMonth()));
            }
        });
        dlg.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        setContentView(R.layout.activity_egginfo);

        final Intent intent = getIntent();
        final DbManager dbm = ((MyApplication)getApplication()).getDbManager();
        final Map<String, String> egg = dbm.getEgg(intent.getStringExtra("id"));

        EditText cageEdit = (EditText)findViewById(R.id.cagesn);
        cageEdit.setText(intent.getStringExtra("cage_sn"));

        EditText laydtEdit = (EditText)findViewById(R.id.laydt);
        laydtEdit.setText(egg.get("lay_dt"));

        Spinner numSpinner = (Spinner)findViewById(R.id.num);
        numSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, AddEggActivity.times));
        numSpinner.setSelection(Integer.parseInt(egg.get("num")) - 1);

        EditText reviewdtEdit = (EditText)findViewById(R.id.reviewdt);
        reviewdtEdit.setText(egg.get("review_dt"));

        EditText hatchdtEdit = (EditText)findViewById(R.id.hatchdt);
        hatchdtEdit.setText(egg.get("hatch_dt"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,1,1,R.string.delete).setIcon(android.R.drawable.ic_menu_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0,2,2,R.string.save).setIcon(android.R.drawable.ic_menu_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                doDelete();
                break;
            case 2:
                doSave();
                break;
        }
        return true;
    }

    private void doDelete(){
        final Intent intent = getIntent();
        final EggInfoActivity that = this;
        final DbManager dbm = ((MyApplication)getApplication()).getDbManager();

        new AlertDialog.Builder(EggInfoActivity.this)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(R.string.msg_del_egg)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbm.deleteEgg(intent.getStringExtra("id"));
                        that.finish();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

    private void doSave(){
        EditText laydtEdit = (EditText)findViewById(R.id.laydt);
        String laydt = laydtEdit.getText().toString();

        Spinner numSpinner = (Spinner)findViewById(R.id.num);
        int num = (int)numSpinner.getSelectedItemId() + 1;

        EditText reviewdtEdit = (EditText)findViewById(R.id.reviewdt);
        String reviewdt = reviewdtEdit.getText().toString();

        EditText hatchdtEdit = (EditText)findViewById(R.id.hatchdt);
        String hatchdt = hatchdtEdit.getText().toString();

        final Intent intent = getIntent();
        DbManager dbm = ((MyApplication)getApplication()).getDbManager();
        dbm.updateEgg(intent.getStringExtra("id"), laydt, num, reviewdt, hatchdt);
        finish();
    }
}
