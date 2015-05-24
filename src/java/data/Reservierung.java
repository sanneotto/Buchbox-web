/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
@Table(name = "reservierung", catalog = "buchbox", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Reservierung.findAll", query = "SELECT r FROM Reservierung r"),
    @NamedQuery(name = "Reservierung.findByReservierungId", query = "SELECT r FROM Reservierung r WHERE r.reservierungId = :reservierungId"),
    @NamedQuery(name = "Reservierung.findByReservierungDatum", query = "SELECT r FROM Reservierung r WHERE r.reservierungDatum = :reservierungDatum"),
    @NamedQuery(name = "Reservierung.findByReservierungAktiv", query = "SELECT r FROM Reservierung r WHERE r.reservierungAktiv = :reservierungAktiv")})
public class Reservierung implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "reservierung_id", nullable = false)
    private Integer reservierungId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "reservierung_datum", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date reservierungDatum;
    @Column(name = "reservierung_aktiv")
    private Boolean reservierungAktiv;
    @JoinColumn(name = "exemplar_id", referencedColumnName = "exemplar_id", nullable = false)
    @ManyToOne(optional = false)
    private Exemplar exemplarId;
    @JoinColumn(name = "kunde_id", referencedColumnName = "kunde_id", nullable = false)
    @ManyToOne(optional = false)
    private Kunde kundeId;

    // Standardkonstruktor

    /**
     *
     */
        public Reservierung() {
    }

    // Konstruktor für Reservierungs-ID

    /**
     *
     * @param reservierungId
     */
        public Reservierung(Integer reservierungId) {
        this.reservierungId = reservierungId;
    }

    // Konstruktor für Reservierungs-ID und -Datum

    /**
     *
     * @param reservierungId
     * @param reservierungDatum
     */
        public Reservierung(Integer reservierungId, Date reservierungDatum) {
        this.reservierungId = reservierungId;
        this.reservierungDatum = reservierungDatum;
    }

    // Getter und Setter

    /**
     *
     * @return
     */
        public Integer getReservierungId() {
        return reservierungId;
    }

    /**
     *
     * @param reservierungId
     */
    public void setReservierungId(Integer reservierungId) {
        this.reservierungId = reservierungId;
    }

    /**
     *
     * @return
     */
    public Date getReservierungDatum() {
        return reservierungDatum;
    }

    /**
     *
     * @param reservierungDatum
     */
    public void setReservierungDatum(Date reservierungDatum) {
        this.reservierungDatum = reservierungDatum;
    }

    /**
     *
     * @return
     */
    public Boolean getReservierungAktiv() {
        return reservierungAktiv;
    }

    /**
     *
     * @param reservierungAktiv
     */
    public void setReservierungAktiv(Boolean reservierungAktiv) {
        this.reservierungAktiv = reservierungAktiv;
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
        hash += (reservierungId != null ? reservierungId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Reservierung)) {
            return false;
        }
        Reservierung other = (Reservierung) object;
        return !((this.reservierungId == null && other.reservierungId != null) || (this.reservierungId != null && !this.reservierungId.equals(other.reservierungId)));
    }

    @Override
    public String toString() {
        return reservierungId + " ";
    }
    
}
