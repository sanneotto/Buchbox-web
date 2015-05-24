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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "verlagbuch", catalog = "buchbox", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Verlagbuch.findAll", query = "SELECT v FROM Verlagbuch v"),
    @NamedQuery(name = "Verlagbuch.findByVerlagbuchId", query = "SELECT v FROM Verlagbuch v WHERE v.verlagbuchId = :verlagbuchId"),
    @NamedQuery(name = "Verlagbuch.findByVerlagIdBuchId", query = "SELECT v FROM Verlagbuch v WHERE v.verlagId.verlagId = :verlagId and v.buchId.buchId = :buchId"),
    @NamedQuery(name = "Verlagbuch.findByVerlagBuch", query = "SELECT v FROM Verlagbuch v WHERE v.verlagId = :verlagId and v.buchId = :buchId"),
    @NamedQuery(name = "Verlagbuch.findByBuch", query = "SELECT v from Verlagbuch v WHERE v.buchId = :buchId"),
    @NamedQuery(name = "Verlagbuch.findByVerlag", query = "SELECT v from Verlagbuch v WHERE v.verlagId = :verlagId"),
    @NamedQuery(name = "Verlagbuch.findByBuchId", query = "SELECT v from Verlagbuch v WHERE v.buchId.buchId = :buchId"),
    @NamedQuery(name = "Verlagbuch.findByVerlagId", query = "SELECT v from Verlagbuch v WHERE v.verlagId.verlagId = :verlagId"),
    @NamedQuery(name = "Verlagbuch.findByVerlagbuchIsbn", query = "SELECT v FROM Verlagbuch v WHERE v.verlagbuchIsbn = :verlagbuchIsbn")})
public class Verlagbuch implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "verlagbuch_id", nullable = false)
    private Integer verlagbuchId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "verlagbuch_isbn", nullable = false, length = 25)
    private String verlagbuchIsbn;
    @JoinColumn(name = "buch_id", referencedColumnName = "buch_id", nullable = false)
    @ManyToOne(optional = false)
    private Buch buchId;
    @JoinColumn(name = "verlag_id", referencedColumnName = "verlag_id", nullable = false)
    @ManyToOne(optional = false)
    private Verlag verlagId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "verlagbuchId")
    private List<Exemplar> exemplarList;

    // Standardkonstruktor

    /**
     *
     */
        public Verlagbuch() {
    }

    // Konstruktor für einzelenen Buchverlag

    /**
     *
     * @param verlagbuchId
     */
        public Verlagbuch(Integer verlagbuchId) {
        this.verlagbuchId = verlagbuchId;
    }

    // Konstruktor für Buchverlag-ID und ISBN

    /**
     *
     * @param verlagbuchId
     * @param verlagbuchIsbn
     */
        public Verlagbuch(Integer verlagbuchId, String verlagbuchIsbn) {
        this.verlagbuchId = verlagbuchId;
        this.verlagbuchIsbn = verlagbuchIsbn;
    }

    // Getter und Setter

    /**
     *
     * @return
     */
        public Integer getVerlagbuchId() {
        return verlagbuchId;
    }

    /**
     *
     * @param verlagbuchId
     */
    public void setVerlagbuchId(Integer verlagbuchId) {
        this.verlagbuchId = verlagbuchId;
    }

    /**
     *
     * @return
     */
    public String getVerlagbuchIsbn() {
        return verlagbuchIsbn;
    }

    /**
     *
     * @param verlagbuchIsbn
     */
    public void setVerlagbuchIsbn(String verlagbuchIsbn) {
        this.verlagbuchIsbn = verlagbuchIsbn;
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

    /**
     *
     * @return
     */
    public Verlag getVerlagId() {
        return verlagId;
    }

    /**
     *
     * @param verlagId
     */
    public void setVerlagId(Verlag verlagId) {
        this.verlagId = verlagId;
    }

    /**
     *
     * @return
     */
    @XmlTransient
    public List<Exemplar> getExemplarList() {
        return exemplarList;
    }

    /**
     *
     * @param exemplarList
     */
    public void setExemplarList(List<Exemplar> exemplarList) {
        this.exemplarList = exemplarList;
    }

    // überschriebene Standardmethoden 
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (verlagbuchId != null ? verlagbuchId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Verlagbuch)) {
            return false;
        }
        Verlagbuch other = (Verlagbuch) object;
        return !((this.verlagbuchId == null && other.verlagbuchId != null) || (this.verlagbuchId != null && !this.verlagbuchId.equals(other.verlagbuchId)));
    }

    @Override
    public String toString() {
        return buchId + ", " + verlagId + " (" + verlagbuchIsbn + ")";
    }
    
}
