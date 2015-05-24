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
@Table(name = "author", catalog = "buchbox", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Author.findAll", query = "SELECT a FROM Author a order by a.authorName"),
    @NamedQuery(name = "Author.findByAuthorId", query = "SELECT a FROM Author a WHERE a.authorId = :authorId order by a.authorName"),
 //   @NamedQuery(name = "Author.findByAuthorVorname", query = "SELECT a FROM Author a WHERE a.authorVorname = :authorVorname"),
    @NamedQuery(name = "Author.findByAuthorName", query = "SELECT a FROM Author a WHERE a.authorName = :authorName and a.authorVorname = :authorVorname order by a.authorName")})
public class Author implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "author_id", nullable = false)
    private Integer authorId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "author_vorname", nullable = false, length = 50)
    private String authorVorname;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "author_name", nullable = false, length = 50)
    private String authorName;
    @JoinColumn(name = "author_genre", referencedColumnName = "genre_id", nullable = false)
    @ManyToOne(optional = false)
    private Genre authorGenre;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "authorId")
    private List<Buchauthor> buchauthorList;

    // Standardkonstruktor

    /**
     *
     */
        public Author() {
    }

    // Konstruktor für Autoren-Nr

    /**
     *
     * @param authorId
     */
        public Author(Integer authorId) {
        this.authorId = authorId;
    }

    // Konstruktor für Autor

    /**
     *
     * @param authorId
     * @param authorVorname
     * @param authorName
     */
        public Author(Integer authorId, String authorVorname, String authorName) {
        this.authorId = authorId;
        this.authorVorname = authorVorname;
        this.authorName = authorName;
    }

    // Getter und Setter

    /**
     *
     * @return
     */
        public Integer getAuthorId() {
        return authorId;
    }

    /**
     *
     * @param authorId
     */
    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    /**
     *
     * @return
     */
    public String getAuthorVorname() {
        return authorVorname;
    }

    /**
     *
     * @param authorVorname
     */
    public void setAuthorVorname(String authorVorname) {
        this.authorVorname = authorVorname;
    }

    /**
     *
     * @return
     */
    public String getAuthorName() {
        return authorName;
    }

    /**
     *
     * @param authorName
     */
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    /**
     *
     * @return
     */
    public Genre getAuthorGenre() {
        return authorGenre;
    }

    /**
     *
     * @param authorGenre
     */
    public void setAuthorGenre(Genre authorGenre) {
        this.authorGenre = authorGenre;
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

    // überschriebene Standardmethoden
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (authorId != null ? authorId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Author)) {
            return false;
        }
        Author other = (Author) object;
        return !((this.authorId == null && other.authorId != null) || (this.authorId != null && !this.authorId.equals(other.authorId)));
    }

    @Override
    public String toString() {
        return authorName + ", " + authorVorname ;
    }
    
}
