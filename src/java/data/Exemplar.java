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
@Table(name = "exemplar", catalog = "buchbox", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Exemplar.findAll", query = "SELECT e FROM Exemplar e"),
    @NamedQuery(name = "Exemplar.findByExemplarId", query = "SELECT e FROM Exemplar e WHERE e.exemplarId = :exemplarId"),
    @NamedQuery(name = "Exemplar.findByExemplarNr", query = "SELECT e FROM Exemplar e WHERE e.exemplarNr = :exemplarNr"),
    @NamedQuery(name = "Exemplar.findByVerlag", query = "SELECT e FROM Exemplar e WHERE e.verlagbuchId.verlagId = :verlagId"),
    @NamedQuery(name = "Exemplar.findByVerlagBuchId", query = "SELECT e FROM Exemplar e WHERE e.verlagbuchId.verlagbuchId = :verlagbuchId"),
    @NamedQuery(name = "Exemplar.findByBuch", query = "SELECT e FROM Exemplar e WHERE e.verlagbuchId.buchId = :buchId"),
    @NamedQuery(name = "Exemplar.findByVerlagId", query = "SELECT e FROM Exemplar e WHERE e.verlagbuchId.verlagId.verlagId = :verlagId"),
    @NamedQuery(name = "Exemplar.findByBuchId", query = "SELECT e FROM Exemplar e WHERE e.verlagbuchId.buchId.buchId = :buchId"),
    @NamedQuery(name = "Exemplar.findByBuchtitel", query = "SELECT e FROM Exemplar e WHERE e.verlagbuchId.buchId.buchTitel like :buchTitel"),})
public class Exemplar implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "exemplarId")
    private List<Reservierung> reservierungList;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "exemplar_id", nullable = false)
    private Integer exemplarId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "exemplar_nr", nullable = false, length = 25)
    private String exemplarNr;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "exemplarId")
    private List<Ausleihe> ausleiheList;
    @JoinColumn(name = "verlagbuch_id", referencedColumnName = "verlagbuch_id", nullable = false)
    @ManyToOne(optional = false)
    private Verlagbuch verlagbuchId;

    // Standardkonstruktor

    /**
     *
     */
        public Exemplar() {
    }

    // Konstruktor für Exemplar-ID

    /**
     *
     * @param exemplarId
     */
        public Exemplar(Integer exemplarId) {
        this.exemplarId = exemplarId;
    }

    // Konstruktor für Exemplar-Nr

    /**
     *
     * @param exemplarId
     * @param exemplarNr
     */
        public Exemplar(Integer exemplarId, String exemplarNr) {
        this.exemplarId = exemplarId;
        this.exemplarNr = exemplarNr;
    }

    // Getter und Setter

    /**
     *
     * @return
     */
        public Integer getExemplarId() {
        return exemplarId;
    }

    /**
     *
     * @param exemplarId
     */
    public void setExemplarId(Integer exemplarId) {
        this.exemplarId = exemplarId;
    }

    /**
     *
     * @return
     */
    public String getExemplarNr() {
        return exemplarNr;
    }

    /**
     *
     * @param exemplarNr
     */
    public void setExemplarNr(String exemplarNr) {
        this.exemplarNr = exemplarNr;
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
    public Verlagbuch getVerlagbuchId() {
        return verlagbuchId;
    }

    /**
     *
     * @param verlagbuchId
     */
    public void setVerlagbuchId(Verlagbuch verlagbuchId) {
        this.verlagbuchId = verlagbuchId;
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
        hash += (exemplarId != null ? exemplarId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Exemplar)) {
            return false;
        }
        Exemplar other = (Exemplar) object;
        return !((this.exemplarId == null && other.exemplarId != null) || (this.exemplarId != null && !this.exemplarId.equals(other.exemplarId)));
    }

    @Override
    public String toString() {
        List<Buchauthor> autorList = verlagbuchId.getBuchId().getBuchauthorList();
        String tmpAutor = "";
        for (Buchauthor ba : autorList) {
            tmpAutor += ba.getAuthorId().getAuthorVorname() + " " + ba.getAuthorId().getAuthorName() + ", ";
        }
        String autoren = tmpAutor.substring(0, tmpAutor.length() - 2);
        String titel = verlagbuchId.getBuchId().getBuchTitel();
        String verlag = verlagbuchId.getVerlagId().getVerlagName();
        return exemplarNr + ": " + autoren + ", " + titel + " - " + verlag;
    }

}
