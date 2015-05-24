package data;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Susanne Otto
 */
@Entity
@Table(name = "buchauthor", catalog = "buchbox", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Buchauthor.findAll", query = "SELECT b FROM Buchauthor b"),
    @NamedQuery(name = "Buchauthor.findByBuchauthorId", query = "SELECT b FROM Buchauthor b WHERE b.buchauthorId = :buchauthorId"),
    @NamedQuery(name = "Buchauthor.findByBuchAuthor", query = "SELECT b FROM Buchauthor b WHERE b.buchId = :buchId and b.authorId = :authorId"),
    @NamedQuery(name = "Buchauthor.findByBuch", query = "SELECT b FROM Buchauthor b WHERE b.buchId = :buchId"),
    @NamedQuery(name = "Buchauthor.findByBuchId", query = "SELECT b FROM Buchauthor b WHERE b.buchId.buchId = :buchId"),
    @NamedQuery(name = "Buchauthor.findByAuthor", query = "SELECT b FROM Buchauthor b WHERE b.authorId = :authorId"),
    @NamedQuery(name = "Buchauthor.findByAuthorId", query = "SELECT b FROM Buchauthor b WHERE b.authorId.authorId = :authorId")})
public class Buchauthor implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "buchauthor_id", nullable = false)
    private Integer buchauthorId;
    @JoinColumn(name = "author_id", referencedColumnName = "author_id", nullable = false)
    @ManyToOne(optional = false)
    private Author authorId;
    @JoinColumn(name = "buch_id", referencedColumnName = "buch_id", nullable = false)
    @ManyToOne(optional = false)
    private Buch buchId;

    // standardkonstruktor

    /**
     *
     */
        public Buchauthor() {
    }

    // Konstruktor für Buchautor-ID

    /**
     *
     * @param buchauthorId
     */
        public Buchauthor(Integer buchauthorId) {
        this.buchauthorId = buchauthorId;
    }

    // Getter und Setter

    /**
     *
     * @return
     */
        public Integer getBuchauthorId() {
        return buchauthorId;
    }

    /**
     *
     * @param buchauthorId
     */
    public void setBuchauthorId(Integer buchauthorId) {
        this.buchauthorId = buchauthorId;
    }

    /**
     *
     * @return
     */
    public Author getAuthorId() {
        return authorId;
    }

    /**
     *
     * @param authorId
     */
    public void setAuthorId(Author authorId) {
        this.authorId = authorId;
    }

    /**
     *
     * @return
     */
    public Buch getBuchId() {
        return buchId;
    }

    /**
     *
     * @param buchId
     */
    public void setBuchId(Buch buchId) {
        this.buchId = buchId;
    }

    // überschriebene Standardmethoden
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (buchauthorId != null ? buchauthorId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Buchauthor)) {
            return false;
        }
        Buchauthor other = (Buchauthor) object;
        return !((this.buchauthorId == null && other.buchauthorId != null) || (this.buchauthorId != null && !this.buchauthorId.equals(other.buchauthorId)));
    }

    @Override
    public String toString() {
        return buchauthorId + " " + buchId;
    }
    
}
