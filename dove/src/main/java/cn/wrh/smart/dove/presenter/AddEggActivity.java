package cn.wrh.smart.dove.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import java.util.Calendar;

import cn.wrh.smart.dove.R;
import cn.wrh.smart.dove.util.DateUtils;
import cn.wrh.smart.dove.view.AddEggDelegate;

public class AddEggActivity extends BaseActivity<AddEggDelegate> {

    @Override
    protected void onLoaded(@Nullable Bundle state) {
        getViewDelegate().setOnClickListener(this::onClickDateEditor, R.id.laying);
    }

    private void onClickDateEditor(View view) {
        final EditText editor = (EditText)view;
        final Calendar c = DateUtils.getCalendarForEditor(editor.getText().toString());
        getViewDelegate().showDatePicker(c, datePicker -> editor.setText(DateUtils.getDateForEditor(datePicker)));
    }

}
