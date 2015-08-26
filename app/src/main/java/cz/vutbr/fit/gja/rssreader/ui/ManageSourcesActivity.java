package cz.vutbr.fit.gja.rssreader.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import cz.vutbr.fit.gja.rssreader.R;
import cz.vutbr.fit.gja.rssreader.database.dao.MainDAO;
import cz.vutbr.fit.gja.rssreader.database.model.Source;

/**
 * Aktivita pro spravu zdroju
 */
public class ManageSourcesActivity extends ListActivity {

    /** Reference na DB */
    private MainDAO db;

    /** Adapter pro seznam zdroju */
    ArrayAdapter<Source> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_src_cat);

        db = new MainDAO(this);
        db.open();
        
        initListView();
    }

    /**
     * Inicializace seznamu aktivity ListActivity
     */
    private void initListView() {
        adapter = new ArrayAdapter<>(this,
                R.layout.src_cat_simple_view, R.id.manageSrcCatLabel, db.getAllSources()); 
        
        setListAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        db.open();
    }
    
    @Override
    protected void onResume() {
        super.onResume();

        // Nacteni zdroju
        if (adapter != null)
            reloadData();
    }

    @Override
    protected void onStop() {
        db.close();
        super.onStop();
    }

    /**
     * Zpracovani vyberu zdroje - editace
     */
    public void onClickTitle(View view) {
        Log.d("ManageSourcesActivity", "Edit source action");
        int position = getListView().getPositionForView(view);
        Source source = adapter.getItem(position);
        
        Intent intent = new Intent(this, AddEditSourceActivity.class);
        intent.putExtra("sourceId", source.getId());
        startActivity(intent);
    }

    /**
     * Nacteni zdroju do seznamu
     */
    private void reloadData() {
        adapter.clear();
        adapter.addAll(db.getAllSources());
    }

    /**
     * Zpracovani smazani zdroje
     */
    public void onClickDeleteButton(View view) {
        int position = getListView().getPositionForView(view);
        Source source = adapter.getItem(position);
        
        db.deleteSource(source.getId());
        reloadData();
    }
    
}
