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
@Table(name = "kunde", catalog = "buchbox", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Kunde.findAll", query = "SELECT k FROM Kunde k order by k.kundeName"),
    @NamedQuery(name = "Kunde.findByKundeId", query = "SELECT k FROM Kunde k WHERE k.kundeId = :kundeId order by k.kundeName"),
    @NamedQuery(name = "Kunde.findByKundeVorname", query = "SELECT k FROM Kunde k WHERE k.kundeVorname = :kundeVorname order by k.kundeName"),
    @NamedQuery(name = "Kunde.findByKundeName", query = "SELECT k FROM Kunde k WHERE k.kundeName = :kundeName order by k.kundeName"),
    @NamedQuery(name = "Kunde.findByKundePlz", query = "SELECT k FROM Kunde k WHERE k.kundePlz = :kundePlz order by k.kundeName"),
    @NamedQuery(name = "Kunde.findByKundeOrt", query = "SELECT k FROM Kunde k WHERE k.kundeOrt = :kundeOrt order by k.kundeName"),
    @NamedQuery(name = "Kunde.findByKundeStr", query = "SELECT k FROM Kunde k WHERE k.kundeStr = :kundeStr order by k.kundeName")})
public class Kunde implements Serializable {
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "kundeId")
    private List<Reservierung> reservierungList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "kunde_id", nullable = false)
    private Integer kundeId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "kunde_vorname", nullable = false, length = 50)
    private String kundeVorname;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "kunde_name", nullable = false, length = 50)
    private String kundeName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "kunde_plz", nullable = false, length = 5)
    private String kundePlz;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "kunde_ort", nullable = false, length = 50)
    private String kundeOrt;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "kunde_str", nullable = false, length = 50)
    private String kundeStr;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "kundeId")
    private List<Ausleihe> ausleiheList;

    // Standardkonstruktor

    /**
     *
     */
        public Kunde() {
    }

    // Konstruktor für Kunden-ID

    /**
     *
     * @param kundeId
     */
        public Kunde(Integer kundeId) {
        this.kundeId = kundeId;
    }

    // Konstruktor für Kunden-Datensatz

    /**
     *
     * @param kundeId
     * @param kundeVorname
     * @param kundeName
     * @param kundePlz
     * @param kundeOrt
     * @param kundeStr
     */
        public Kunde(Integer kundeId, String kundeVorname, String kundeName, String kundePlz, String kundeOrt, String kundeStr) {
        this.kundeId = kundeId;
        this.kundeVorname = kundeVorname;
        this.kundeName = kundeName;
        this.kundePlz = kundePlz;
        this.kundeOrt = kundeOrt;
        this.kundeStr = kundeStr;
    }

    
    // Getter und Setter

    /**
     *
     * @return
     */
        public Integer getKundeId() {
        return kundeId;
    }

    /**
     *
     * @param kundeId
     */
    public void setKundeId(Integer kundeId) {
        this.kundeId = kundeId;
    }

    /**
     *
     * @return
     */
    public String getKundeVorname() {
        return kundeVorname;
    }

    /**
     *
     * @param kundeVorname
     */
    public void setKundeVorname(String kundeVorname) {
        this.kundeVorname = kundeVorname;
    }

    /**
     *
     * @return
     */
    public String getKundeName() {
        return kundeName;
    }

    /**
     *
     * @param kundeName
     */
    public void setKundeName(String kundeName) {
        this.kundeName = kundeName;
    }

    /**
     *
     * @return
     */
    public String getKundePlz() {
        return kundePlz;
    }

    /**
     *
     * @param kundePlz
     */
    public void setKundePlz(String kundePlz) {
        this.kundePlz = kundePlz;
    }

    /**
     *
     * @return
     */
    public String getKundeOrt() {
        return kundeOrt;
    }

    /**
     *
     * @param kundeOrt
     */
    public void setKundeOrt(String kundeOrt) {
        this.kundeOrt = kundeOrt;
    }

    /**
     *
     * @return
     */
    public String getKundeStr() {
        return kundeStr;
    }

    /**
     *
     * @param kundeStr
     */
    public void setKundeStr(String kundeStr) {
        this.kundeStr = kundeStr;
    }

    /**
     *
     * @return
     */
    @XmlTransient
    public List<Ausleihe> getAusleiheList() {
        return ausleiheList;
    }

    /**
     *
     * @param ausleiheList
     */
    public void setAusleiheList(List<Ausleihe> ausleiheList) {
        this.ausleiheList = ausleiheList;
    }

    /**
     *
     * @return
     */
    @XmlTransient
    public List<Reservierung> getReservierungList() {
        return reservierungList;
    }

    /**
     *
     * @param reservierungList
     */
    public void setReservierungList(List<Reservierung> reservierungList) {
        this.reservierungList = reservierungList;
    }
    
    // überschriebene Standardmethoden
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (kundeId != null ? kundeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Kunde)) {
            return false;
        }
        Kunde other = (Kunde) object;
        return !((this.kundeId == null && other.kundeId != null) || (this.kundeId != null && !this.kundeId.equals(other.kundeId)));
    }

    @Override
    public String toString() {
        return kundeName + ", " + kundeVorname ;
    }


    
}
