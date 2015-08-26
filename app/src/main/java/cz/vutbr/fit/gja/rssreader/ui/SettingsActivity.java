package cz.vutbr.fit.gja.rssreader.ui;

import cz.vutbr.fit.gja.rssreader.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Aktivita s nastavenim aplikace
 */
public class SettingsActivity extends Activity {

    /** Uroven detailu zobrazenych v seznamu clanku */
    private int articleDetailLevel;

    /** Pocet dni pro smazani clanku */
    private int removeOlderThanDays;

    /** Smazani starych neprectenych clanku */
    private boolean removeOldUnread;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        // Nacteni nastaveni
        initData();

        // Urovne detailu
        String[] levels = new String[]{getString(R.string.detail_min),
                getString(R.string.detail_mid),
                getString(R.string.detail_max)};
        Spinner spinner = (Spinner) findViewById(R.id.SettingDetailLevelSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
              android.R.layout.simple_spinner_item, levels);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(articleDetailLevel);

        // Mazani starych clanku
        EditText daysEdit = (EditText) findViewById(R.id.remove_after_days);
        daysEdit.setText(String.valueOf(removeOlderThanDays));

        CheckBox removeUnreadCheckBox = (CheckBox) findViewById(R.id.remove_old_unread_checkbox);
        removeUnreadCheckBox.setChecked(removeOldUnread);
    }

    /**
     * Ziskani nastaveni z hlavni aktivity
     */
    private void initData() {
        articleDetailLevel = getIntent().getIntExtra(MainActivity.MSG_DETAIL, MainActivity.SHOW_DETAIL_MIN);
        removeOlderThanDays = getIntent().getIntExtra(MainActivity.MSG_REMOVE_OLDER_THAN, 10);
        removeOldUnread = getIntent().getBooleanExtra(MainActivity.MSG_REMOVE_OLD_UNREAD, false);
    }

    /**
     * Zpracovani ulozeni nastaveni
     */
    public void submit(View v) {
        // Nacteni zadanych hodnot
        int levelid = ((Spinner) findViewById(R.id.SettingDetailLevelSpinner)).getSelectedItemPosition();

        String deleteAfterStr = ((EditText) findViewById(R.id.remove_after_days)).getText().toString();
        int deleteAfter = Integer.valueOf(deleteAfterStr);
        if ((deleteAfter < 0) || (deleteAfter > 100)) {
             Toast.makeText(this, getString(R.string.toast_day_limit), Toast.LENGTH_SHORT).show();
             return;
        }

        boolean rmUnread = ((CheckBox) findViewById(R.id.remove_old_unread_checkbox)).isChecked();

        if (deleteAfter == 0 && rmUnread) {
            Toast.makeText(this, getString(R.string.toast_check_limit), Toast.LENGTH_SHORT).show();
            return;
        }

        // Predani hlavni aktivite
        Intent i = new Intent();
        i.putExtra(MainActivity.MSG_DETAIL, levelid);
        i.putExtra(MainActivity.MSG_REMOVE_OLDER_THAN, deleteAfter);
        i.putExtra(MainActivity.MSG_REMOVE_OLD_UNREAD, rmUnread);
        setResult(RESULT_OK, i);
        finish();
    }

}
