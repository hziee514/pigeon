package wrh.pigeon;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    public void onClickLaydt(View view){
        final EditText laydtEdit = (EditText)view;
        String text = laydtEdit.getText().toString();
        final Calendar c = getCalendar(text);
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                laydtEdit.setText(getDate(year, monthOfYear, dayOfMonth));
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
                break;
        }
        return true;
    }

}
