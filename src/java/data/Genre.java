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
@Table(name = "genre", catalog = "buchbox", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Genre.findAll", query = "SELECT g FROM Genre g order by g.genreName"),
    @NamedQuery(name = "Genre.findByGenreId", query = "SELECT g FROM Genre g WHERE g.genreId = :genreId order by g.genreName"),
    @NamedQuery(name = "Genre.findByGenreName", query = "SELECT g FROM Genre g WHERE g.genreName = :genreName order by g.genreName")})
public class Genre implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "genre_id", nullable = false)
    private Integer genreId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 25)
    @Column(name = "genre_name", nullable = false, length = 25)
    private String genreName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "authorGenre")
    private List<Author> authorList;

    // Standardkonstruktor

    /**
     *
     */
        public Genre() {
    }

    // Konstruktor für Genre-ID

    /**
     *
     * @param genreId
     */
        public Genre(Integer genreId) {
        this.genreId = genreId;
    }

    // Konstruktor für Genre-ID und -Name

    /**
     *
     * @param genreId
     * @param genreName
     */
        public Genre(Integer genreId, String genreName) {
        this.genreId = genreId;
        this.genreName = genreName;
    }

    // Getter und Setter

    /**
     *
     * @return
     */
        public Integer getGenreId() {
        return genreId;
    }

    /**
     *
     * @param genreId
     */
    public void setGenreId(Integer genreId) {
        this.genreId = genreId;
    }

    /**
     *
     * @return
     */
    public String getGenreName() {
        return genreName;
    }

    /**
     *
     * @param genreName
     */
    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    /**
     *
     * @return
     */
    @XmlTransient
    public List<Author> getAuthorList() {
        return authorList;
    }

    /**
     *
     * @param authorList
     */
    public void setAuthorList(List<Author> authorList) {
        this.authorList = authorList;
    }

    // überschriebene Standardmethoden
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (genreId != null ? genreId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Genre)) {
            return false;
        }
        Genre other = (Genre) object;
        return !((this.genreId == null && other.genreId != null) || (this.genreId != null && !this.genreId.equals(other.genreId)));
    }

    @Override
    public String toString() {
        return genreName;
    }
    
}
