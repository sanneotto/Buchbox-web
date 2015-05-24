package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Wrapperklasse für die Verwaltung von Buch-Exemplaren
 * 
 * @author Susanne Otto
 */
public class WrapperExemplar implements Serializable {

    
    private Integer exemplarId;
    private String exemplarnummer;
    private String buchtitel;
    private String author;
    private String verlag;
    private String isbn;


    /**
     * Standardkonstruktor
     */
        public WrapperExemplar() {
    }

  

    /** createExemplarTable(exemplar)
     * erstellt Tabellenansicht der übergebenen Exemplarliste mit Informationen aus Stammdatentabellen
     * @param exemplar
     * @return
     */
        public List<WrapperExemplar> createExemplarTable(List<Exemplar> exemplar) {
        List<WrapperExemplar> exemplarTableList = new ArrayList();

        for (Iterator<Exemplar> iterator = exemplar.iterator(); iterator.hasNext();) {
            WrapperExemplar exemplartable = new WrapperExemplar();

            Exemplar next = iterator.next();
            exemplartable.setExemplarId(next.getExemplarId());
            exemplartable.setExemplarnummer(next.getExemplarNr());
            exemplartable.setBuchtitel(next.getVerlagbuchId().getBuchId().getBuchTitel());
            String h = "";
            for (int i = 0; i < next.getVerlagbuchId().getBuchId().getBuchauthorList().size(); i++) {
                Buchauthor ba = (Buchauthor) (next.getVerlagbuchId().getBuchId().getBuchauthorList().toArray()[i]);
                if (i > 0) {
                    h += " / ";
                }
                h += ba.getAuthorId().getAuthorVorname() + " " + ba.getAuthorId().getAuthorName();
            }
            exemplartable.setAuthor(h);
            exemplartable.setVerlag(next.getVerlagbuchId().getVerlagId().getVerlagName());
            exemplartable.setIsbn(next.getVerlagbuchId().getVerlagbuchIsbn());
            exemplarTableList.add(exemplartable);
        }
        return exemplarTableList;
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
    public String getExemplarnummer() {
        return exemplarnummer;
    }

    /**
     *
     * @param exemplarnummer
     */
    public void setExemplarnummer(String exemplarnummer) {
        this.exemplarnummer = exemplarnummer;
    }

    /**
     *
     * @return
     */
    public String getBuchtitel() {
        return buchtitel;
    }

    /**
     *
     * @param buchtitel
     */
    public void setBuchtitel(String buchtitel) {
        this.buchtitel = buchtitel;
    }

    /**
     *
     * @return
     */
    public String getAuthor() {
        return author;
    }

    /**
     *
     * @param author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     *
     * @return
     */
    public String getVerlag() {
        return verlag;
    }

    /**
     *
     * @param verlag
     */
    public void setVerlag(String verlag) {
        this.verlag = verlag;
    }

    /**
     *
     * @return
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     *
     * @param isbn
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    
    @Override
    public String toString() {
        return exemplarnummer + ": " + author + ", " + buchtitel + " - " + verlag ;
    }
    
    
    
}
