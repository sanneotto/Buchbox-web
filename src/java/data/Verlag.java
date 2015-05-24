/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
@Table(name = "verlag", catalog = "buchbox", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Verlag.findAll", query = "SELECT v FROM Verlag v order by v.verlagName"),
    @NamedQuery(name = "Verlag.findByVerlagId", query = "SELECT v FROM Verlag v WHERE v.verlagId = :verlagId order by v.verlagName"),
    @NamedQuery(name = "Verlag.findByVerlagName", query = "SELECT v FROM Verlag v WHERE v.verlagName = :verlagName order by v.verlagName"),
    @NamedQuery(name = "Verlag.findByVerlagNameLike", query = "SELECT v FROM Verlag v WHERE v.verlagName like :verlagName order by v.verlagName")} )
public class Verlag implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "verlag_id", nullable = false)
    private Integer verlagId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "verlag_name", nullable = false, length = 50)
    private String verlagName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "verlagId")
    private List<Verlagbuch> verlagbuchList;

    // Standardkonstruktor

    /**
     *
     */
        public Verlag() {
    }

    // Konstruktor für Verlags-ID

    /**
     *
     * @param verlagId
     */
        public Verlag(Integer verlagId) {
        this.verlagId = verlagId;
    }

    // Konstruktor für Verlags-ID und Verlagsname

    /**
     *
     * @param verlagId
     * @param verlagName
     */
        public Verlag(Integer verlagId, String verlagName) {
        this.verlagId = verlagId;
        this.verlagName = verlagName;
    }
    
    // Getter und Setter

    /**
     *
     * @return
     */
        public Integer getVerlagId() {
        return verlagId;
    }

    /**
     *
     * @param verlagId
     */
    public void setVerlagId(Integer verlagId) {
        this.verlagId = verlagId;
    }

    /**
     *
     * @return
     */
    public String getVerlagName() {
        return verlagName;
    }

    /**
     *
     * @param verlagName
     */
    public void setVerlagName(String verlagName) {
        this.verlagName = verlagName;
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

    // überschriebene Standardmethoden
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (verlagId != null ? verlagId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Verlag)) {
            return false;
        }
        Verlag other = (Verlag) object;
        if ((this.verlagId == null && other.verlagId != null) || (this.verlagId != null && !this.verlagId.equals(other.verlagId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return verlagName;
    }
    
}
