package data;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Susanne Otto
 */
@Entity
@Table(name = "buch", catalog = "buchbox", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Buch.findAll", query = "SELECT b FROM Buch b order by b.buchTitel"),
    @NamedQuery(name = "Buch.findByBuchId", query = "SELECT b FROM Buch b WHERE b.buchId = :buchId order by b.buchTitel"),
    @NamedQuery(name = "Buch.findByBuchTitel", query = "SELECT b FROM Buch b WHERE b.buchTitel = :buchTitel order by b.buchTitel"),
    @NamedQuery(name = "Buch.findByBuchThema", query = "SELECT b FROM Buch b WHERE b.buchThema = :buchThema order by b.buchTitel")})
public class Buch implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "buch_id", nullable = false)
    private Integer buchId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "buch_titel", nullable = false, length = 100)
    private String buchTitel;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "buch_thema", nullable = false, length = 50)
    private String buchThema;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "buchId")
    private List<Verlagbuch> verlagbuchList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "buchId")
    private List<Buchauthor> buchauthorList;

    // Standardkonstruktor

    /**
     *
     */
        public Buch() {
    }

    // Konstruktor für Buch-Nr

    /**
     *
     * @param buchId
     */
        public Buch(Integer buchId) {
        this.buchId = buchId;
    }

    // Konstruktor für komplettes Buch

    /**
     *
     * @param buchId
     * @param buchTitel
     * @param buchThema
     */
        public Buch(Integer buchId, String buchTitel, String buchThema) {
        this.buchId = buchId;
        this.buchTitel = buchTitel;
        this.buchThema = buchThema;
    }

    // Getter und Setter

    /**
     *
     * @return
     */
        public Integer getBuchId() {
        return buchId;
    }

    /**
     *
     * @param buchId
     */
    public void setBuchId(Integer buchId) {
        this.buchId = buchId;
    }

    /**
     *
     * @return
     */
    public String getBuchTitel() {
        return buchTitel;
    }

    /**
     *
     * @param buchTitel
     */
    public void setBuchTitel(String buchTitel) {
        this.buchTitel = buchTitel;
    }

    /**
     *
     * @return
     */
    public String getBuchThema() {
        return buchThema;
    }

    /**
     *
     * @param buchThema
     */
    public void setBuchThema(String buchThema) {
        this.buchThema = buchThema;
    }

    /**
     *
     * @return
     */
    @XmlTransient
    public List<Verlagbuch> getVerlagbuchList() {
        return verlagbuchList;
    }

    /**
     *
     * @param verlagbuchList
     */
    public void setVerlagbuchList(List<Verlagbuch> verlagbuchList) {
        this.verlagbuchList = verlagbuchList;
    }

    /**
     *
     * @return
     */
    @XmlTransient
    public List<Buchauthor> getBuchauthorList() {
        return buchauthorList;
    }

    /**
     *
     * @param buchauthorList
     */
    public void setBuchauthorList(List<Buchauthor> buchauthorList) {
        this.buchauthorList = buchauthorList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (buchId != null ? buchId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Buch)) {
            return false;
        }
        Buch other = (Buch) object;
        return !((this.buchId == null && other.buchId != null) || (this.buchId != null && !this.buchId.equals(other.buchId)));
    }

    @Override
    public String toString() {
        return buchTitel;
    }
    
}
