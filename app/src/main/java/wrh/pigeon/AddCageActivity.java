package wrh.pigeon;

import android.app.ActionBar;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class AddCageActivity extends Activity {

    private static final String[] rooms = {
        "01", "02", "03", "04", "05", "06", "07", "08"
    };

    private static final String[] groups = {
        "01", "02", "03", "04", "05", "06", "07", "08", "09", "10"
    };

    private static final String[] layers = {
        "1","2","3","4","5"
    };

    private static final String[] sns = {
        "01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        setContentView(R.layout.activity_addcage);

        Spinner roomSpinner = (Spinner)findViewById(R.id.roomid);
        roomSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, rooms));

        Spinner groupSpinner = (Spinner)findViewById(R.id.groupid);
        groupSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, groups));

        Spinner layerSpinner = (Spinner)findViewById(R.id.layerid);
        layerSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, layers));

        Spinner firstSpinner = (Spinner)findViewById(R.id.firstsn);
        firstSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sns));

        Spinner lastSpinner = (Spinner)findViewById(R.id.lastsn);
        lastSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sns));
        lastSpinner.setSelection(11);

        Spinner statusSpinner = (Spinner)findViewById(R.id.status);
        statusSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.cage_status)));
        statusSpinner.setSelection(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,1,1,R.string.save).setIcon(android.R.drawable.ic_menu_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                doSave();
                break;
        }
        return true;
    }

    public void doSave(){
        Spinner roomSpinner = (Spinner)findViewById(R.id.roomid);
        String roomid = roomSpinner.getSelectedItem().toString();

        Spinner groupSpinner = (Spinner)findViewById(R.id.groupid);
        String groupid = groupSpinner.getSelectedItem().toString();

        Spinner layerSpinner = (Spinner)findViewById(R.id.layerid);
        String layerid = layerSpinner.getSelectedItem().toString();

        Spinner firstSpinner = (Spinner)findViewById(R.id.firstsn);
        int firtsn = (int)firstSpinner.getSelectedItemId() + 1;

        Spinner lastSpinner = (Spinner)findViewById(R.id.lastsn);
        int lastsn = (int)lastSpinner.getSelectedItemId() + 1;

        Spinner statusSpinner = (Spinner)findViewById(R.id.status);
        int status = (int)statusSpinner.getSelectedItemId();

        if (firtsn > lastsn) {
            Toast.makeText(this, R.string.firstsn_cant_gt_lastsn, Toast.LENGTH_SHORT).show();
            return;
        }

        DbManager dbm = ((MyApplication)getApplication()).getDbManager();
        if (dbm.batchAddCages(roomid, groupid, layerid, firtsn, lastsn, status)) {
            finish();
        }
    }

}
