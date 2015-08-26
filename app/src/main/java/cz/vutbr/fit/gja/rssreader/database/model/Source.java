package cz.vutbr.fit.gja.rssreader.database.model;

/**
 * Trida reprezentujici zdroj
 */
public class Source {

    /** PK */
    private long id;

    private String link;

    private String name;

    private long categoryId;

    public Source() {
        this.categoryId = 0L;
    }
    
    public Source(String link, String name, long categoryId) {
        this.link = link;
        this.name = name;
        this.categoryId = categoryId;
    }
    
    public Source(long id, String link, String name, long categoryId) {
        this.id = id;
        this.link = link;
        this.name = name;
        this.categoryId = categoryId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return name;
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
        Source other = (Source) obj;
        if (id != other.id)
            return false;
        return true;
    }

}
