package data;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Susanne Otto
 */
@Entity
@Table(name = "ausleihe", catalog = "buchbox", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ausleihe.findAll", query = "SELECT a FROM Ausleihe a"),
    @NamedQuery(name = "Ausleihe.findByLeihId", query = "SELECT a FROM Ausleihe a WHERE a.leihId = :leihId"),
    @NamedQuery(name = "Ausleihe.findAllByKundeId", query = "SELECT a FROM Ausleihe a WHERE a.kundeId.kundeId = :kundeId"),    
    @NamedQuery(name = "Ausleihe.findByAusleihDatum", query = "SELECT a FROM Ausleihe a WHERE a.ausleihDatum = :ausleihDatum"),
    @NamedQuery(name = "Ausleihe.findByLeihRueckgabedatum", query = "SELECT a FROM Ausleihe a WHERE a.leihRueckgabedatum = :leihRueckgabedatum"),
    @NamedQuery(name = "Ausleihe.findRueckgabeDatumIsNull", query = "SELECT a FROM Ausleihe a WHERE a.leihRueckgabedatum is NULL"),
    @NamedQuery(name = "Ausleihe.findAusgeliehenByKundeId", query = "SELECT a FROM Ausleihe a WHERE a.leihRueckgabedatum is NULL and a.kundeId.kundeId = :kundeId"),
    @NamedQuery(name = "Ausleihe.findAusgeliehenByExemplarId", query = "SELECT a FROM Ausleihe a WHERE a.leihRueckgabedatum is NULL and a.exemplarId.exemplarId = :exemplarId"),
    @NamedQuery(name = "Ausleihe.findAusgeliehenByKundeExemplar", query = "SELECT a FROm Ausleihe a WHERE a.leihRueckgabedatum is NULL and a.exemplarId.exemplarId = :exemplarId and a.kundeId.kundeId = :kundeId"),
    @NamedQuery(name = "Ausleihe.findByVerlaengerbar", query = "SELECT a FROM Ausleihe a WHERE a.verlaengerbar = :verlaengerbar")})
    public class Ausleihe implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "leih_id", nullable = false)
    private Integer leihId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ausleih_datum", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date ausleihDatum;
    @Column(name = "leih_rueckgabedatum")
    @Temporal(TemporalType.DATE)
    private Date leihRueckgabedatum;
    @Column(name = "verlaengerbar")
    private Boolean verlaengerbar;
    @JoinColumn(name = "exemplar_id", referencedColumnName = "exemplar_id", nullable = false)
    @ManyToOne(optional = false)
    private Exemplar exemplarId;
    @JoinColumn(name = "kunde_id", referencedColumnName = "kunde_id", nullable = false)
    @ManyToOne(optional = false)
    private Kunde kundeId;

    /** Standardkonstruktor
     * 
     */
        public Ausleihe() {
    }

    /** Konstruktor für Ausleih-Nr
     *
     * @param leihId
     */
        public Ausleihe(Integer leihId) {
        this.leihId = leihId;
    }

    /** Konstruktor für Ausleih-Nr und -Datum
     *
     * @param leihId
     * @param ausleihDatum
     */
        public Ausleihe(Integer leihId, Date ausleihDatum) {
        this.leihId = leihId;
        this.ausleihDatum = ausleihDatum;
    }

    /**  Konstruktor für Kunde und Exemplar
     *
     * @param kundeId
     * @param exemplarId
     */
        public Ausleihe(Kunde kundeId, Exemplar exemplarId) {
        this.kundeId = kundeId;
        this.exemplarId = exemplarId;
    }

    // Getter und Setter

    /**
     *
     * @return
     */
        public Integer getLeihId() {
        return leihId;
    }

    /**
     *
     * @param leihId
     */
    public void setLeihId(Integer leihId) {
        this.leihId = leihId;
    }

    /**
     *
     * @return
     */
    public Date getAusleihDatum() {
        return ausleihDatum;
    }

    /**
     *
     * @param ausleihDatum
     */
    public void setAusleihDatum(Date ausleihDatum) {
        this.ausleihDatum = ausleihDatum;
    }

    /**
     *
     * @return
     */
    public Date getLeihRueckgabedatum() {
        return leihRueckgabedatum;
    }

    /**
     *
     * @param leihRueckgabedatum
     */
    public void setLeihRueckgabedatum(Date leihRueckgabedatum) {
        this.leihRueckgabedatum = leihRueckgabedatum;
    }

    /**
     *
     * @return
     */
    public Boolean getVerlaengerbar() {
        return verlaengerbar;
    }

    /**
     *
     * @param verlaengerbar
     */
    public void setVerlaengerbar(Boolean verlaengerbar) {
        this.verlaengerbar = verlaengerbar;
    }

    /**
     *
     * @return
     */
    public Exemplar getExemplarId() {
        return exemplarId;
    }

    /**
     *
     * @param exemplarId
     */
    public void setExemplarId(Exemplar exemplarId) {
        this.exemplarId = exemplarId;
    }

    /**
     *
     * @return
     */
    public Kunde getKundeId() {
        return kundeId;
    }

    /**
     *
     * @param kundeId
     */
    public void setKundeId(Kunde kundeId) {
        this.kundeId = kundeId;
    }

    // überschriebene Standardmethoden
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (leihId != null ? leihId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Ausleihe)) {
            return false;
        }
        Ausleihe other = (Ausleihe) object;
        return !((this.leihId == null && other.leihId != null) || (this.leihId != null && !this.leihId.equals(other.leihId)));
    }

    @Override
    public String toString() {
        return "" + leihId + ": " + this.exemplarId + " -> entliehen von " + this.kundeId  ;
    }
    
}
