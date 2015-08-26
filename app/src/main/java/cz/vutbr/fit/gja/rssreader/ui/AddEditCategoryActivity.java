package cz.vutbr.fit.gja.rssreader.ui;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import cz.vutbr.fit.gja.rssreader.R;
import cz.vutbr.fit.gja.rssreader.database.dao.MainDAO;
import cz.vutbr.fit.gja.rssreader.database.model.Category;

/**
 * Aktivita pro vytvoreni nebo editaci kategorie
 */
public class AddEditCategoryActivity extends Activity {

    /** Reference na DB */
    private MainDAO db;

    /** Vychozi ID kategorie pro test, zda jde o vytvareni/editaci */
    private final static long NO_CATEGORY_ID = -1L;

    /** ID vybrane kategorie */
    private long categoryId;

    /** Reference na vybranou kategorii */
    private Category currentCategory;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_category);

        // Pripojeni k DB
        db = new MainDAO(this);
        db.open();

        // Ziskani ID vybrane kategorie pro editaci
        loadParam();

        // Vyplneni formulare v pripade editace
        if (isEdit())
            setValueOfField();
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        db.open();
    }

    @Override
    protected void onStop() {
        db.close();
        super.onStop();
    }

    /**
     * Vyplni formular v pripade editace kategorie
     */
    private void setValueOfField() {
        EditText nameField = (EditText) findViewById(R.id.addEditCategoryNameField);
        nameField.setText(currentCategory.getName());
    }

    /**
     * Ziskani ID vybrane kategorie pro editaci
     */
    private void loadParam() {
        categoryId = getIntent().getLongExtra("categoryId", NO_CATEGORY_ID);
        
        if (isEdit())
            currentCategory = db.getCategory(categoryId);
    }

    /**
     * Test zda jde o vytvoreni/editaci
     * @return Vysledek testu
     */
    private boolean isEdit() {
        if (categoryId == NO_CATEGORY_ID)
            return false;
        else
            return true;
    }

    /**
     * Zpracovani ulozeni zadanych dat
     */
    public void onSubmit(View view) {
        boolean correct = true;
        Resources res = getResources();
        
        EditText nameField = (EditText) findViewById(R.id.addEditCategoryNameField);
        String name = nameField.getText().toString();
        if (name.isEmpty()) {
            nameField.setError(res.getString(R.string.addCatMsgNoName));
            correct = false;
        }
        
        if (correct) {
            if(isEdit()) {
                currentCategory.setName(name);
                db.updateCategory(currentCategory);
            } else {
                db.createCategory(name);
            }
            finish();
        }
    }

}
