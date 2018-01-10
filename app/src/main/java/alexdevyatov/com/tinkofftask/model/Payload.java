package alexdevyatov.com.tinkofftask.model;

/**
 * Created by Алексей on 09.01.2018.
 */

public class Payload implements Comparable<Payload> {

    private Long id;
    private String name;
    private String text;
    private PublicationDate publicationDate;
    private Long bankInfoTypeId;

    public Payload(Long id, String name, String text, PublicationDate publicationDate, Long bankInfoTypeId) {
        this.id = id;
        this.name = name;
        this.text = text;
        this.publicationDate = publicationDate;
        this.bankInfoTypeId = bankInfoTypeId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public PublicationDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(PublicationDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Long getBankInfoTypeId() {
        return bankInfoTypeId;
    }

    public void setBankInfoTypeId(Long bankInfoTypeId) {
        this.bankInfoTypeId = bankInfoTypeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Payload payload = (Payload) o;

        if (id != null ? !id.equals(payload.id) : payload.id != null) return false;
        if (name != null ? !name.equals(payload.name) : payload.name != null) return false;
        if (text != null ? !text.equals(payload.text) : payload.text != null) return false;
        if (publicationDate != null ? !publicationDate.equals(payload.publicationDate) : payload.publicationDate != null)
            return false;
        return bankInfoTypeId != null ? bankInfoTypeId.equals(payload.bankInfoTypeId) : payload.bankInfoTypeId == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (publicationDate != null ? publicationDate.hashCode() : 0);
        result = 31 * result + (bankInfoTypeId != null ? bankInfoTypeId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public int compareTo(Payload o) {
        return this.publicationDate.getMilliseconds().compareTo(o.publicationDate.getMilliseconds());
    }
}
