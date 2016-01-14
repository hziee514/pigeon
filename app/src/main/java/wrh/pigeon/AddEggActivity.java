package wrh.pigeon;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

public class AddEggActivity extends Activity {

    private Map<String, String> busy_cages_;

    public static final String[] times = {
        "1","2"
    };

    public static String getDate(int y, int m, int d){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.set(y, m, d);
        return sdf.format(c.getTime());
    }

    public static Calendar getCalendar(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date));
        }catch (ParseException e){
        }
        return c;
    }

    public void onClickDateEdit(View view){
        final EditText editText = (EditText)view;
        String text = editText.getText().toString();
        final Calendar c = getCalendar(text);
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
                editText.setText(getDate(picker.getYear(), picker.getMonth(), picker.getDayOfMonth()));
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
        setContentView(R.layout.activity_addegg);

        DbManager dbm = ((MyApplication)getApplication()).getDbManager();
        busy_cages_ = dbm.getBusyCages();

        final AddEggActivity that = this;

        ArrayList<String> busy_cages = new ArrayList<String>(busy_cages_.keySet());
        Collections.sort(busy_cages);
        Spinner cageSpinner = (Spinner)findViewById(R.id.cagesn);
        cageSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, busy_cages));

        final Calendar c = Calendar.getInstance();
        final EditText laydtEdit = (EditText) findViewById(R.id.laydt);
        laydtEdit.setText(getDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)));

        Spinner numSpinner = (Spinner)findViewById(R.id.num);
        numSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, times));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,1,1,R.string.save).setIcon(android.R.drawable.ic_menu_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Calendar c = Calendar.getInstance();
        switch (item.getItemId()) {
            case 1:
                doSave();
                break;
        }
        return true;
    }

    public void doSave(){
        Spinner cageSpinner = (Spinner)findViewById(R.id.cagesn);
        String cagesn = cageSpinner.getSelectedItem().toString();
        String cageid = busy_cages_.get(cagesn);

        EditText laydtEdit = (EditText)findViewById(R.id.laydt);
        String laydt = laydtEdit.getText().toString();

        Spinner numSpinner = (Spinner)findViewById(R.id.num);
        int num = (int)numSpinner.getSelectedItemId() + 1;

        EditText reviewdtEdit = (EditText)findViewById(R.id.reviewdt);
        String reviewdt = reviewdtEdit.getText().toString();

        EditText hatchdtEdit = (EditText)findViewById(R.id.hatchdt);
        String hatchdt = hatchdtEdit.getText().toString();

        DbManager dbm = ((MyApplication)getApplication()).getDbManager();
        dbm.addEgg(cageid, laydt, num, reviewdt, hatchdt);
        finish();
    }

}
