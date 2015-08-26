package cz.vutbr.fit.gja.rssreader.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import cz.vutbr.fit.gja.rssreader.R;
import cz.vutbr.fit.gja.rssreader.database.dao.MainDAO;
import cz.vutbr.fit.gja.rssreader.database.model.Category;

/**
 * Aktivita pro spravu kategorii
 */
public class ManageCategoriesActivity extends ListActivity {

    /** Reference na DB */
    private MainDAO db;

    /** Adapter pro seznam kategorii */
    ArrayAdapter<Category> adapter;

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
                R.layout.src_cat_simple_view, R.id.manageSrcCatLabel, db.getAllCategories()); 
        
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

        // Nacteni kategorii
        if (adapter != null)
            reloadData();
    }

    @Override
    protected void onStop() {
        db.close();
        super.onStop();
    }

    /**
     * Zpracovani vyberu kategorie - editace
     */
    public void onClickTitle(View view) {
        int position = getListView().getPositionForView(view);
        Category category = adapter.getItem(position);
        
        Intent intent = new Intent(this, AddEditCategoryActivity.class);
        intent.putExtra("categoryId", category.getId());
        startActivity(intent);
    }

    /**
     * Nacteni kategorii do seznamu
     */
    private void reloadData() {
        adapter.clear();
        adapter.addAll(db.getAllCategories());
    }

    /**
     * Zpracovani smazani kategorie
     */
    public void onClickDeleteButton(View view) {
        int position = getListView().getPositionForView(view);
        Category category = adapter.getItem(position);
        
        db.deleteCategory(category.getId());
        reloadData();
    }

}
