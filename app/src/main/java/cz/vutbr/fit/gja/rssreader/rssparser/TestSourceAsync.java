package cz.vutbr.fit.gja.rssreader.rssparser;

import java.util.List;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import cz.vutbr.fit.gja.rssreader.database.model.Article;
import cz.vutbr.fit.gja.rssreader.database.model.Source;

/**
 * Otestovani dostupnosti zdroje
 */
public class TestSourceAsync extends AsyncTask<Source, Integer, Boolean> {

    private static final String TAG = "TestSourceAsync";
    private ProgressDialog progress;
    private Handler handler = new Handler();

    public TestSourceAsync(ProgressDialog progress) {
        super();
        this.progress = progress;
    }

    @Override
    protected void onPreExecute() {
        handler.post(
                new Runnable() {
                    public void run() {
                        progress.show();
                    }
                });

    }

    /**
     * Test zdroje
     * @return Vysledek testu
     */
    protected Boolean doInBackground(Source... sources) {
        RSSParser parser = new RSSParser(null, 0, false);

        Log.d(TAG, "TestSourceAsync started");
        Source s = sources[0];
        List<Article> articles = parser.getArticlesFromSource(s, false);
        if (articles.isEmpty())
            return Boolean.FALSE;
        return Boolean.TRUE;
    }

    @Override
    protected void onPostExecute(Boolean res) {
        Log.d(TAG, "TestSourceAsync PostExecute");
        if (res.booleanValue()) {
            Log.d(TAG, "TestSourceAsync OK");
        } else {
            Log.d(TAG, "TestSourceAsync False");
        }
        handler.post(
                new Runnable() {
                    public void run() {
                        if (progress.isShowing()) {
                            progress.dismiss();
                        }
                    }
                });
    }

    @Override
    protected void onCancelled(Boolean res) {
        Log.d(TAG, "Downloading articles cancelled");
        handler.post(
                new Runnable() {
                    public void run() {
                        if (progress.isShowing()) {
                            progress.dismiss();
                        }
                    }
                });
    }

}
