package cz.vutbr.fit.gja.rssreader.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import cz.vutbr.fit.gja.rssreader.R;
import cz.vutbr.fit.gja.rssreader.database.dao.MainDAO;
import cz.vutbr.fit.gja.rssreader.database.model.Article;
import cz.vutbr.fit.gja.rssreader.database.model.Category;
import cz.vutbr.fit.gja.rssreader.webarchive.WebArchiveReader;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Aktivita pro zobrazeni detailu clanku
 */
public class ArticleActivity extends ActionBarActivity {

    /** Reference na databazovou vrstvu */
    private MainDAO db;

    /** Kategorie zobrazeneho clanku */
    private Category category;

    /** Zobrazovany clanek */
    private Article article = null;

    /** WebView pro zobrazeni clanku */
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pripojeni k DB
        db = new MainDAO(this);
        db.open();

        // Ziskani informaci o clanku
        Intent i = getIntent();
        int categoryId = i.getIntExtra(MainActivity.MSG_CATEGORY, MainActivity.CATEGORY_ALL);
        long articleId = i.getLongExtra(MainActivity.MSG_ARTICLE, 0L);
        if (categoryId == MainActivity.CATEGORY_ALL)
            category = new Category(categoryId, getResources().getString(R.string.all));
        else if (categoryId == MainActivity.CATEGORY_SAVED)
            category = new Category(categoryId, getResources().getString(R.string.saved));
        else if (categoryId == MainActivity.CATEGORY_UNCATEGORIZED)
            category = new Category(categoryId, getResources().getString(R.string.uncategorized));
        else
            category = db.getCategory(categoryId);
        article = db.getArticle((int) articleId);

        setContentView(R.layout.activity_article);

        // Nastaveni action baru
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(category.getName());

        // Nastaveni web view pro zobrazeni clanku
        webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());

        // Nacteni clanku z webu/uloziste
        if (article.isSaved())
            loadArticle();
        else
            webView.loadUrl(article.getLink());
    }

    @Override
    protected void onStop() {
        super.onStop();
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.article, m);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Nacte ulozeny clanek ze souboru
     */
    private void loadArticle() {
        String filename = getFilesDir().getAbsolutePath() + "/article" + article.getId();
        if (Build.VERSION.SDK_INT >= 19)
            filename += ".mht";
        File file = new File(filename);
        if (file.exists()) {
            Log.i("loadArticle", "Article loaded successfully");
            // Pro novejsi verze systemu
            if (Build.VERSION.SDK_INT >= 19) {
                webView.loadUrl("file://" + filename);
                return;
            }
            try {
                InputStream is = new FileInputStream(file);
                WebArchiveReader wr = new WebArchiveReader() {
                    public void onFinished(WebView v) {
                        continueWhenLoaded(v);
                    }
                };
                if (wr.readWebArchive(is)) {
                    wr.loadToWebView(webView);
                }
            } catch (IOException e) {
                article.setSaved(false);
                db.updateArticle(article);
                webView.loadUrl(article.getLink());
                Toast.makeText(ArticleActivity.this, getResources().getString(R.string.save_not_found), Toast.LENGTH_SHORT).show();
            }
        } else {
            // Ulozeny clanek nenalezen, bude nacten ze site
            article.setSaved(false);
            db.updateArticle(article);
            webView.loadUrl(article.getLink());
            Toast.makeText(ArticleActivity.this, getResources().getString(R.string.save_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Nastaveni vlastniho klienta pro zobrazeni stranky
     */
    void continueWhenLoaded(WebView webView) {
        webView.setWebViewClient(new WebViewClient());
    }

}
