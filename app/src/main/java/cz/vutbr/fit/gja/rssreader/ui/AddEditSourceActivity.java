package cz.vutbr.fit.gja.rssreader.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import cz.vutbr.fit.gja.rssreader.R;
import cz.vutbr.fit.gja.rssreader.database.dao.MainDAO;
import cz.vutbr.fit.gja.rssreader.database.model.Category;
import cz.vutbr.fit.gja.rssreader.database.model.Source;
import cz.vutbr.fit.gja.rssreader.rssparser.TestSourceAsync;

/**
 * Aktivita pro vytvoreni nebo editaci zdroje
 */
public class AddEditSourceActivity extends Activity {

    /** Reference na DB */
    private MainDAO db;

    /** ID kategorie pro nezarazene clanky */
    private final static long NO_CATEGORY_ID = 0L;

    /** Vychozi ID zdroje pro test, zda jde o vytvareni/editaci */
    private final static long NO_SOURCE_ID = -1L;

    /** ID vybraneho zdroje */
    private long sourceId;

    /** Reference na vybrany zdroj */
    private Source currentSource;

    /** Tester dostupnosti zadaneho zdroje */
    private TestSourceAsync testSource;

    /** Ukazatel stavu testovani dostupnosti zdroje */
    private ProgressDialog progress; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_source);

        // Pripojeni k DB
        db = new MainDAO(this);
        db.open();

        progress = new ProgressDialog(this);
        setProgressBar();

        // Ziskani ID vybraneho zdroje pro editaci
        loadParam();

        // Nacteni hodnot do formulare
        if (isEdit())
            setValuesOfFields();
        else
            initSpinnerWithCategories();

    }

    /**
     * Nacteni hodnot pri editaci zdroje
     */
    private void setValuesOfFields() {
        EditText nameField = (EditText) findViewById(R.id.addSourceNameField);
        nameField.setText(currentSource.getName());
        EditText linkField = (EditText) findViewById(R.id.addSourceLinkField);
        linkField.setText(currentSource.getLink());
        
        // Nacteni kategorii do spinneru
        Spinner spinner = (Spinner) findViewById(R.id.addSourceCategoriesSpinner);
        List<Category> categories = getCategoriesList();

        Category currentSourceCategory = db.getCategory(currentSource.getCategoryId());
        int position = categories.indexOf(currentSourceCategory);
        
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(position);
    }

    /**
     * Ziskani ID vybraneho zdroje pro editaci
     */
    private void loadParam() {
        sourceId = getIntent().getLongExtra("sourceId", NO_SOURCE_ID);
        
        if (isEdit())
            currentSource = db.getSource(sourceId);
    }

    /**
     * Test zda jde o vytvoreni/editaci
     * @return Vysledek testu
     */
    private boolean isEdit() {
        if (sourceId == NO_SOURCE_ID)
            return false;
        else
            return true;
    }

    /**
     * Nacteni kategorii pri vytvareni zdroje
     */
    private void initSpinnerWithCategories() {
        Spinner spinner = (Spinner) findViewById(R.id.addSourceCategoriesSpinner);

        ArrayAdapter<Category> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getCategoriesList());

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    /**
     * Ulozeni zadanych dat. Ziska data, overi a prida zdroj a zacne stahovat RSS
     */
    public void submit(View view) {
        boolean correct = true;
        Resources res = getResources();

        Category category = (Category) ((Spinner) findViewById(R.id.addSourceCategoriesSpinner))
                .getSelectedItem();

        EditText nameField = (EditText) findViewById(R.id.addSourceNameField);
        String name = nameField.getText().toString();
        if (name.isEmpty()) {
            nameField.setError(res.getString(R.string.addSourceMsgNoName));
            correct = false;
        }

        EditText linkField = (EditText) findViewById(R.id.addSourceLinkField);
        String link = linkField.getText().toString();
        if (link.isEmpty()) {
            linkField.setError(res.getString(R.string.addSourceMsgNoLink));
            correct = false;
        }

        Source sourceToTest;
        Source source = null;
        if (correct) {
            if (!link.startsWith("http://"))
                link = "http://" + link;

            if (isEdit()) {
                currentSource.setName(name);
                currentSource.setLink(link);
                if (category.equals(res.getString(R.string.uncategorized)))
                    currentSource.setCategoryId(0);
                else
                    currentSource.setCategoryId(category.getId());
                sourceToTest = currentSource;
            } else {
                if (category.equals(res.getString(R.string.uncategorized)))
                    source = new Source(link, name, 0);
                else
                    source = new Source(link, name, category.getId());
                sourceToTest = source;
            }

            // Test platnosti URL
            if (correct) {
                 try {
                     new URL(sourceToTest.getLink());
                 } catch (MalformedURLException e) {
                     correct = false;
                     Log.e("AddEditSource Test", e.getMessage());
                     Toast.makeText(this, getString(R.string.invalid_url), Toast.LENGTH_SHORT).show();
                 }
            }

            // Test dostupnosti zdroje
            if (correct) {
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    testSource = new TestSourceAsync(progress);
                    testSource.execute(sourceToTest);
                    try {
                        correct = testSource.get(10000, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException | ExecutionException
                        | TimeoutException e) {
                        correct = false;
                    }

                    if (!correct) {
                        Toast.makeText(this, getString(R.string.unable_to_get_data), Toast.LENGTH_SHORT).show();
                        Log.e("AddEditSource Test", "Unable to get data");
                    }
                } else {
                    Log.e("AddEditSource Test", "Not connected to network");
                    Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                }
            }

            // Ulozeni do DB
            if (correct) {
                if (isEdit()) {
                    db.updateSource(currentSource);
                } else {
                    db.createSource(source);
                }
            }

            if (correct) {
                finish();
            } else {
                Toast.makeText(this, getString(R.string.invalid_source), Toast.LENGTH_SHORT).show();
                Log.e("AddEditSource Test", "unvalid_source");
            }
            return;
        }
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
     * Vytvoreni seznamu kategorii
     * @return Seznam kategorii
     */
    private List<Category> getCategoriesList() {
        List<Category> allCat = new ArrayList<>();

        // Systemove kategorie
        Resources r = getResources();
        Category noCat = new Category(NO_CATEGORY_ID, r.getString(R.string.uncategorized));
        allCat.add(noCat);

        // Uzivatelske kategorie
        List<Category> catDB = db.getAllCategories();

        allCat.addAll(catDB);
        return allCat;
    }

    /**
     * Nastaveni progress baru
     */
    private void setProgressBar() {
		progress.setMessage(getString(R.string.checking_source_msg));
		progress.setTitle(R.string.checking_source_title);
		progress.setCancelable(false);
		progress.isIndeterminate();
    }

}
