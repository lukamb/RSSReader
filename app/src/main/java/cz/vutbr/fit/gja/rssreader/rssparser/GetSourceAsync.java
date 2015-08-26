package cz.vutbr.fit.gja.rssreader.rssparser;

import java.util.List;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import cz.vutbr.fit.gja.rssreader.database.dao.MainDAO;
import cz.vutbr.fit.gja.rssreader.database.model.Article;
import cz.vutbr.fit.gja.rssreader.database.model.Source;
import cz.vutbr.fit.gja.rssreader.ui.MainActivity;

/**
 * Stazeni RSS zdroju na pozadi
 */
public class GetSourceAsync extends AsyncTask<Source, Integer, Void> {

    private MainDAO db;
    private MainActivity ma;
    private static final String TAG = "GetSourceAsync";
    private Handler handler = new Handler();

    public GetSourceAsync(MainDAO db, MainActivity ma) {
        this.db = db;
        this.ma = ma;
    }

    protected void onPreExecute() {
        handler.post(
                new Runnable() {
                    public void run() {
                        ma.progressValue = 0;
                        ma.progressBar.setProgress(0);
                        ma.progressBar.setVisibility(View.VISIBLE);
                    }
                });
    }

    protected Void doInBackground(Source... sources) {
        RSSParser parser = new RSSParser(this.db, ma.getRemoveOlderThanDays(), ma.getRemoveOldUnread());

        int count = sources.length;
        int i = 0;
        for (Source s : sources) {
            List<Article> articles = parser.getArticlesFromSource(s, true);
            Log.d(TAG, String.valueOf(articles.size()) + " new article(s) in " + s.getName());
            for (Article a : articles) {
                db.createArticle(a);
            }
            i++;
            publishProgress((int) ((i / (float) count) * 100));
            if (isCancelled()) break;
        }
        return null;
    }

    protected void onProgressUpdate(Integer... progress) {
        ma.progressValue = progress[0];
        handler.post(
                new Runnable() {
                    public void run() {
                        ma.progressBar.setProgress(ma.progressValue);
                    }
                });
    }

    @Override
    protected void onPostExecute(Void res) {
        Log.d(TAG, "Downloading articles finished");
        handler.postDelayed(
                new Runnable() {
                    public void run() {
                        ma.progressBar.setVisibility(View.GONE);
                        ma.invalidateData();
                    }
                }, 5000);
    }

    @Override
    protected void onCancelled(Void res) {
        Log.d(TAG, "Downloading articles cancelled");
        handler.post(
                new Runnable() {
                    public void run() {
                        ma.progressBar.setVisibility(View.GONE);
                    }
                });
    }

}
