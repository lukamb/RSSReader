package cz.vutbr.fit.gja.rssreader.database.model;

/**
 * Trida reprezentujici clanek
 */
public class Article {

    /** PK */
    private long id;

    private String title;

    /** Odkaz na clanek - unikatni v kombinaci s ID zdroje */
    private String link;

    private String description;

    /** UNIX time format v sekundach */
    private long pubDate;

    /** UNIX time format v sekundach */
    private long insertDate;

    private boolean deleted;

    private boolean unread;

    private boolean saved;

    /** ID zdroje - unikatni v kombinaci s odkazem na clanek */
    private long sourceId;

    private long categoryId;

    public Article() {
        this.deleted = false;
        this.unread = true;
        this.saved = false;
        this.categoryId = 0L;
        this.sourceId = 0L;
        this.insertDate = 0L;
        this.pubDate = 0L;
    }

    public Article(String title, String link, String description,
            long pubDate, long insertDate, boolean deleted, boolean unread, boolean saved,
            long sourceId, long categoryId) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.pubDate = pubDate;
        this.insertDate = insertDate;
        this.deleted = deleted;
        this.unread = unread;
        this.saved = saved;
        this.sourceId = sourceId;
        this.categoryId = categoryId;
    }

    public Article(long id, String title, String link, String description,
            long pubDate, long insertDate, boolean deleted, boolean unread, boolean saved,
            long sourceId, long categoryId) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.description = description;
        this.pubDate = pubDate;
        this.insertDate = insertDate;
        this.deleted = deleted;
        this.unread = unread;
        this.saved = saved;
        this.sourceId = sourceId;
        this.categoryId = categoryId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPubDate() {
        return pubDate;
    }

    public void setPubDate(long pubDate) {
        this.pubDate = pubDate;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public long getSourceId() {
        return sourceId;
    }

    public void setSourceId(long sourceId) {
        this.sourceId = sourceId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(long insertDate) {
        this.insertDate = insertDate;
    }

    @Override
    public String toString() {
        return "Article [id=" + id + ", title=" + title + ", link=" + link + ", description="
                + description + ", pubDate=" + pubDate + ", deleted=" + deleted + ", unread="
                + unread + ", saved=" + saved + ", sourceId=" + sourceId + ", categoryId="
                + categoryId + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    /**
     * Objekty jsou si rovny, rovnaji-li se jejich ID
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Article other = (Article) obj;
        if (id != other.id)
            return false;
        return true;
    }

}
