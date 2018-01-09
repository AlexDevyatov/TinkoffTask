package alexdevyatov.com.tinkofftask.model;

/**
 * Created by Алексей on 09.01.2018.
 */

public class PublicationDate {

    private Long milliseconds;

    public Long getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(Long milliseconds) {
        this.milliseconds = milliseconds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PublicationDate that = (PublicationDate) o;

        return milliseconds != null ? milliseconds.equals(that.milliseconds) : that.milliseconds == null;

    }

    @Override
    public int hashCode() {
        return milliseconds != null ? milliseconds.hashCode() : 0;
    }
}
