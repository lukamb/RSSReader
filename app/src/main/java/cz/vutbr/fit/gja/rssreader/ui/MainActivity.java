package cz.vutbr.fit.gja.rssreader.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import cz.vutbr.fit.gja.rssreader.R;
import cz.vutbr.fit.gja.rssreader.database.dao.MainDAO;
import cz.vutbr.fit.gja.rssreader.database.model.Article;
import cz.vutbr.fit.gja.rssreader.database.model.Category;
import cz.vutbr.fit.gja.rssreader.database.model.Source;
import cz.vutbr.fit.gja.rssreader.rssparser.GetSourceAsync;

/**
 * Hlavni aktivita obsahujici seznam clanku
 */
public class MainActivity extends ActionBarActivity implements OnNavigationListener {

	/** Identifikatory zprav */
	public static final String MSG_CATEGORY = "cz.vutbr.fit.gja.rssreader.CATEGORY";
	public static final String MSG_ARTICLE = "cz.vutbr.fit.gja.rssreader.ARTICLE";
	public static final String MSG_DETAIL = "cz.vutbr.fit.gja.rssreader.ARTICLE_DETAIL_LEVEL";
	public static final String MSG_REMOVE_OLDER_THAN = "cz.vutbr.fit.gja.rssreader.REMOVE_OLDER_THAN";
	public static final String MSG_REMOVE_OLD_UNREAD = "cz.vutbr.fit.gja.rssreader.REMOVE_OLD_UNREAD";
	
	/** ID pro kategorii "Vse" */
	public static final int CATEGORY_ALL = -2;
	/** ID pro kategorii "Ulozene" */
	public static final int CATEGORY_SAVED = -1;
	/** ID pro kategorii "Nezarazene" */
	public static final int CATEGORY_UNCATEGORIZED = 0;
	
	/** Urovne detailu pro zobrazeni clanku */
	public static final int SHOW_DETAIL_MIN = 0;
	public static final int SHOW_DETAIL_MID = 1;
	public static final int SHOW_DETAIL_MAX = 2;

	private static final String TAG = "MainActivity";

	private MainDAO db;
	private GetSourceAsync rssparser;
    private ArticleSimpleAdapter adapter;
    private ArticlesListFragment ArticlesList;
    private int articleDetailLevel;
    private int removeOlderThanDays;
    private boolean removeOldUnread;
    public ProgressBar progressBar;
    public int progressValue;

	/** Seznam kategorii pro filtrovani */
	private SpinnerAdapter gCategories;
	/** ID aktualne zvolene kategorie */
	private int selectedCategory = CATEGORY_ALL;
    /** Seznam implicitnich kategorii */
    private List<Category> implicitCategories;

	/**
	 * Indikuje, jestli maji byt zobrazeny prectene clanky.
	 * Hodnota se uklada a nacita z preferences.
	 */
    private boolean showReadedArticles = false;

    private boolean invalidateDataOnResume = false;

    private boolean reloadCategoriesOnResume = false;

    /**
     * Nacteni clanku z RSS zdroju
     */
    private void updateRSSData() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            if (rssparser.getStatus() != AsyncTask.Status.RUNNING) {
                if (rssparser.getStatus() == AsyncTask.Status.FINISHED)
                    this.rssparser = new GetSourceAsync(db, this);

                Log.d(TAG, "UpdateRSSData: Starting to receive RSS data");
                Toast.makeText(this, getString(R.string.updating_articles), Toast.LENGTH_SHORT).show();
                rssparser.execute(this.db.getAllSources().toArray(new Source[0]));
            } else {
                Log.d(TAG, "UpdateRSSData: another process in execution");
                Toast.makeText(this, getString(R.string.was_executed), Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "Not connected to network");
            Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
	}

	/**
	 * Aktualizace seznamu clanku
	 */
	public void invalidateData() {
		adapter.clear();
		
		switch (selectedCategory) {
            case CATEGORY_ALL:
                adapter.addAll(db.getAllArticles(showReadedArticles));
                break;
            case CATEGORY_SAVED:
                adapter.addAll(db.getSavedArticles(showReadedArticles));
                break;
            case CATEGORY_UNCATEGORIZED:
                adapter.addAll(db.getUncategorizedArticles(showReadedArticles));
                break;
            default:
                adapter.addAll(db.getArticlesByCategory((long) selectedCategory, showReadedArticles));
                break;
		}
	}
	
	/**
	 * Vraci pole vsech kategorii
	 * @return Pole kategorii
	 */
	private Category[] getCategories() {
		List<Category> res = new ArrayList<>();

		res.addAll(implicitCategories);
		
		// Uzivatelske kategorie
        res.addAll(db.getAllCategories());

        return res.toArray(new Category[0]);
    }
	
	/**
	 * Vrati pozici zadane kategorie v menu pro vyber kategorie
	 * @param categoryId ID kategorie
	 * @return Pozice kategorie v menu
	 */
	private int getCategoryPosition(int categoryId) {
		Category c;
		int i;
		for (i = 0; i < gCategories.getCount(); i++) {
			c = (Category) gCategories.getItem(i);
			if (c.getId() == categoryId)
				break;
		}
		
		return i;
	}
	
    /**
     * Zmena kategorie pro filtrovani clanku
     */
    @Override
    public boolean onNavigationItemSelected(int position, long itemId) {
        Category c = (Category) gCategories.getItem(position);
        selectedCategory = (int) c.getId();
        invalidateData();
        return true;
    }

    /**
     * Vytvoreni objektu aktivity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.db = new MainDAO(this);
        this.db.open();

        createImplicitCategories();
        loadPreferences();
        setUpArticleView();
        this.rssparser = new GetSourceAsync(db, this);
        setUpActionBar();

        this.progressBar = (ProgressBar) findViewById(R.id.main_progress_bar);
        updateRSSData();
    }

    /**
     * Vytvoreni seznamu implicitnich kategorii
     */
    private void createImplicitCategories() {
        implicitCategories = new ArrayList<>();

        // Systemove kategorie
        Resources r = getResources();

        Category c = new Category((long) CATEGORY_ALL, r.getString(R.string.all));
        implicitCategories.add(c);

        c = new Category((long) CATEGORY_SAVED, r.getString(R.string.saved));
        implicitCategories.add(c);

        c = new Category((long) CATEGORY_UNCATEGORIZED, r.getString(R.string.uncategorized));
        implicitCategories.add(c);
    }

    /**
     * Zobrazeni pozadovaneho seznamu clanku
     */
    private void setUpArticleView() {
    	List<Article> tmp;

    	switch (selectedCategory) {
            case CATEGORY_ALL:
                tmp = db.getAllArticles(showReadedArticles);
                break;
            case CATEGORY_SAVED:
                tmp = db.getSavedArticles(showReadedArticles);
                break;
            case CATEGORY_UNCATEGORIZED:
                tmp = db.getUncategorizedArticles(showReadedArticles);
                break;
            default:
                tmp = db.getArticlesByCategory((long) selectedCategory, showReadedArticles);
                break;
		}
    	
        adapter = new ArticleSimpleAdapter(this, android.R.layout.simple_list_item_1, tmp);
        adapter.setDB(db);
        adapter.setDetailLevel(articleDetailLevel);
        setContentView(R.layout.activity_main);

        ArticlesList = (ArticlesListFragment) getSupportFragmentManager().findFragmentById(R.id.articles_list_fragment);
        ArticlesList.setListAdapter(adapter);
    }

    /**
     * Nastaveni action baru - nacteni kategorii pro vyber apod.
     */
    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        gCategories = new ArrayAdapter<Category>(actionBar.getThemedContext(),
                android.R.layout.simple_spinner_dropdown_item,
                getCategories());
        actionBar.setListNavigationCallbacks(gCategories, this);
        actionBar.setSelectedNavigationItem(getCategoryPosition(selectedCategory));
    }

    /**
     * Nacteni uzivatelskych nastaveni
     */
    private void loadPreferences() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        showReadedArticles = sharedPref.getBoolean(getString(R.string.appPrefShowReadedArticles), false);
        articleDetailLevel = sharedPref.getInt(MSG_DETAIL, SHOW_DETAIL_MIN);
        removeOlderThanDays = sharedPref.getInt(MSG_REMOVE_OLDER_THAN, 10);
        removeOldUnread = sharedPref.getBoolean(MSG_REMOVE_OLD_UNREAD, false);
    }


    @Override
    protected void onStop() {
        super.onStop();
        storePreferences();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        this.db.close();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Znovunacteni clanku
        if (invalidateDataOnResume) {
            updateRSSData();
            invalidateData();
            invalidateDataOnResume = false;
        }

        // Znovunacteni kategorii v action baru
        if (reloadCategoriesOnResume) {
            setUpActionBar();  
            reloadCategoriesOnResume = false;
        }
    }

    /**
     * Ulozeni uzivatelskych nastaveni
     */
    private void storePreferences() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.appPrefShowReadedArticles), showReadedArticles);
        editor.putInt(MSG_DETAIL, articleDetailLevel);
        editor.putInt(MSG_REMOVE_OLDER_THAN, removeOlderThanDays);
        editor.putBoolean(MSG_REMOVE_OLD_UNREAD, removeOldUnread);
        editor.commit();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d("onPrepareOptionsMenu", "onPrepareOptionsMenu");

        // Nastaveni polozky v menu pro (ne)zobrazeni prectenych clanku
        if (showReadedArticles)
            menu.getItem(1).setTitle(R.string.menu_hide_readed);
        else
            menu.getItem(1).setTitle(R.string.menu_show_readed);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Zpracovani vybrane polozky v menu
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                Log.d("onOptionsItemSelected", "Selected refresh");
                updateRSSData();
                return true;
                
            case R.id.menu_show_readed:
                this.showReadedArticles = !this.showReadedArticles;
                invalidateData();
                Log.d("onOptionsItemSelected", "Selected show readed");
                return true;

            case R.id.menu_add_source:
                Log.d("onOptionsItemSelected", "Selected add source");
                invalidateDataOnResume = true;
                startActivity(new Intent(this, AddEditSourceActivity.class));
                return true;

            case R.id.menu_manage_sources:
                Log.d("onOptionsItemSelected", "Selected manage sources");
                invalidateDataOnResume = true;
                startActivity(new Intent(this, ManageSourcesActivity.class));
                return true;

            case R.id.menu_add_category:
                Log.d("onOptionsItemSelected", "Selected add category");
                invalidateDataOnResume = true;
                reloadCategoriesOnResume = true;
                startActivity(new Intent(this, AddEditCategoryActivity.class));
                return true;

            case R.id.menu_manage_categories:
                Log.d("onOptionsItemSelected", "Selected manage categories");
                invalidateDataOnResume = true;
                reloadCategoriesOnResume = true;
                startActivity(new Intent(this, ManageCategoriesActivity.class));
                return true;

            case R.id.menu_settings:
                Log.d("onOptionsItemSelected", "Selected settings");
                Intent i = new Intent(this, SettingsActivity.class);
                i.putExtra(MSG_DETAIL, articleDetailLevel);
                i.putExtra(MSG_REMOVE_OLDER_THAN, removeOlderThanDays);
                i.putExtra(MSG_REMOVE_OLD_UNREAD, removeOldUnread);
                startActivityForResult(i, 1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                articleDetailLevel = data.getIntExtra(MainActivity.MSG_DETAIL, articleDetailLevel);
                removeOlderThanDays = data.getIntExtra(MainActivity.MSG_REMOVE_OLDER_THAN, removeOlderThanDays);
                removeOldUnread = data.getBooleanExtra(MainActivity.MSG_REMOVE_OLD_UNREAD, removeOldUnread);

                storePreferences();
                adapter.setDetailLevel(articleDetailLevel);
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Zpracovani stisku titulku clanku (rozbaleni popisu)
     */
    public void onClickArticleTitle(View v) {
        // Nastaveni clanku jako precteneho
        Log.d(TAG, "Call onClickArticleTitle");
        int position = ArticlesList.getListView().getPositionForView(v);
        Article a = adapter.getItem(position);
        ArticleSimpleView asw = (ArticleSimpleView) v.getParent().getParent();
        asw.showDetails();
        asw.markAsRead();
        a.setUnread(false);
        db.updateArticle(a);
        Log.d(TAG, "Article '" + a.getTitle() + "' on position " + String.valueOf(position) + " was marked as read");
    }

    /**
     * Zpracovani stisku tlacitka smazat u clanku
     */
    public void onClickArticleDeleteButton(View v) {
        // Nastaveni clanku jako smazaneho (nutne aby se nenacetl znova pri dalsim updatu)
        Log.d(TAG, "Call onClickArticleDeleteButton");
        int position = ArticlesList.getListView().getPositionForView(v);
        Article a = adapter.getItem(position);
        ArticleSimpleView asw = (ArticleSimpleView) v.getParent().getParent();
        
        // Smazani souboru, jedna-li se o ulozeny clanek
        if (a.isSaved()) {
        	a.setSaved(false);
        	String filename = getFilesDir().getAbsolutePath() + "/article" + a.getId();
        	if (Build.VERSION.SDK_INT >= 19)
        		filename += ".mht";
    		File file = new File(filename);
    		if (file.exists())
    			file.delete();
    		asw.markAsUnsaved();
    		Log.d(TAG, "Saved article '" + a.getTitle() + "' on position " + String.valueOf(position) + " was unsaved");
        } else {
        	adapter.remove(a);
            a.setDeleted(true);
            Log.d(TAG, "Article '" + a.getTitle() + "' on position " + String.valueOf(position) + " was removed");
        }

        db.updateArticle(a);
    }

    /**
     * Zpracovani stisku tlacitka pro zobrazeni clanku
     */
    public void onClickArticleGoButton(View v) {
        Log.d(TAG, "Call onClickArticleGoButton");
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        int position = ArticlesList.getListView().getPositionForView(v);
        Article a = adapter.getItem(position);
        if ((networkInfo != null && networkInfo.isConnected()) 
        		|| a.isSaved()) {
            Intent i = new Intent(this, ArticleActivity.class);
            i.putExtra(MSG_CATEGORY, selectedCategory);
            i.putExtra(MSG_ARTICLE, a.getId());
            startActivity(i);
        } else {
            Log.e(TAG, "Not connected to network");
            Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Zpracovani nastaveni clanku jako neprecteneho
     */
    public void onClickArticleUnreadButton(View v) {
        Log.d(TAG, "Call onClickArticleUnreadButton");
        int position = ArticlesList.getListView().getPositionForView(v);
        Article a = adapter.getItem(position);
        ArticleSimpleView asw = (ArticleSimpleView) v.getParent().getParent();
        boolean newstate = ! a.isUnread();
        a.setUnread(newstate);
        if (a.isUnread()){
            asw.markAsUnread();
            Log.d(TAG, "Article '" + a.getTitle() + "' on position " + String.valueOf(position) + " marked as unread");
        } else {
            asw.markAsRead();
            Log.d(TAG, "Article '" + a.getTitle() + "' on position " + String.valueOf(position) + " marked as read");
        }
        db.updateArticle(a);
    }

    /**
     * Zpracovani ulozeni clanku
     */
    public void onClickArticleSaveButton(View v) {
        Log.d(TAG, "Call onClickArticleSaveButton");
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            int position = ArticlesList.getListView().getPositionForView(v);
            Article a = adapter.getItem(position);
            ArticleSimpleView asw = (ArticleSimpleView) v.getParent().getParent();
            WebView w = new WebView(this);
            w.setWebViewClient(new BackgroundWebViewClient(a, asw));
            w.loadUrl(a.getLink());
        } else {
            Log.e(TAG, "Not connected to network");
            Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    public int getRemoveOlderThanDays() {
        return removeOlderThanDays;
    }

    public boolean getRemoveOldUnread() {
        return removeOldUnread;
    }
    
    /**
     * Upraveny WebViewClient pro ulozeni clanku na pozadi
     */
    private class BackgroundWebViewClient extends WebViewClient {
    	
    	/** Ukladany clanek */
    	private Article article;
    	/** Polozka clanku v UI seznamu clanku */
    	private ArticleSimpleView aView;
    	
    	public BackgroundWebViewClient(Article a, ArticleSimpleView v) {
    		article = a;
    		aView = v;
    	}
    	
    	@Override
    	public void onPageFinished(WebView view, String url) {
    		String filename = getFilesDir().getAbsolutePath() + "/article" + article.getId();
    		if (Build.VERSION.SDK_INT >= 19)
    			filename += ".mht";
    		view.saveWebArchive(filename, false, new BckgndSaveArticleCallback(article, aView));
    	}
    	
    }
    
    /**
     * Obsahuje callback pro zpracovani vysledku ulozeni clanku
     */
    private class BckgndSaveArticleCallback implements ValueCallback<String> {
    	
    	/** Ukladany clanek */
    	private Article article;
    	/** Polozka clanku v UI seznamu clanku */
    	private ArticleSimpleView view;
    	
    	public BckgndSaveArticleCallback(Article a, ArticleSimpleView v) {
    		article = a;
    		view = v;
    	}
    	
		@Override
		public void onReceiveValue(String filename) {
			if (filename == null) {
				Toast.makeText(MainActivity.this, getResources().getString(R.string.save_failed), Toast.LENGTH_SHORT).show();
			} else {
				article.setSaved(true);
				db.updateArticle(article);
				view.markAsSaved();
				Toast.makeText(MainActivity.this, getResources().getString(R.string.save_success), Toast.LENGTH_SHORT).show();
			}
		}
		
	}
    
}
