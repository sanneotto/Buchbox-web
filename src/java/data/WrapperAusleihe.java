package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Wrapperklasse für Ausleihe und Rückgabe
 *
 * @author Susanne Otto
 */
public class WrapperAusleihe implements Serializable {

    private Integer leihId;
    private String kundeVorname;
    private String kundeName;
    private String buchtitel;
    private String verlag;
    private String isbn;
    private String exemplarnummer;
    private String autor;
    private Date vonDate;
    private Date bisDate;

    // Standardkonstruktor

    /**
     *
     */
        public WrapperAusleihe() {

    }

    // Konstruktor für einzelen Ausleihe

    /**
     *
     * @param leihId
     */
        public WrapperAusleihe(Integer leihId) {
        this.leihId = leihId;
    }

    /** createLeihtabelle(leih)
     * erstelt Listenansicht mit Informationen aus Stammdatentabellen für übergebene Ausleihliste
     * @param leih
     * @return
     */
        public List<WrapperAusleihe> createLeihtable(List<Ausleihe> leih) {
        List<WrapperAusleihe> leihTableList = new ArrayList();

        for (Iterator<Ausleihe> iterator = leih.iterator(); iterator.hasNext();) {
            WrapperAusleihe leihtable = new WrapperAusleihe();

            Ausleihe next = iterator.next();
            leihtable.setLeihId(next.getLeihId());
            leihtable.setKundeVorname(next.getKundeId().getKundeVorname());
            leihtable.setKundeName(next.getKundeId().getKundeName());
            leihtable.setExemplarnummer(next.getExemplarId().getExemplarNr());
            leihtable.setVonDate(next.getAusleihDatum());
            leihtable.setBisDate(next.getLeihRueckgabedatum());
            leihtable.setVerlag(next.getExemplarId().getVerlagbuchId().getVerlagId().getVerlagName());
            leihtable.setIsbn(next.getExemplarId().getVerlagbuchId().getVerlagbuchIsbn());
            leihtable.setBuchtitel(next.getExemplarId().getVerlagbuchId().getBuchId().getBuchTitel());

            String h = "";
            for (int i = 0; i < next.getExemplarId().getVerlagbuchId().getBuchId().getBuchauthorList().size(); i++) {
                Buchauthor ba = (Buchauthor) (next.getExemplarId().getVerlagbuchId().getBuchId().getBuchauthorList().toArray()[i]);
                if (i > 0) {
                    h += " / ";
                }
                h += ba.getAuthorId().getAuthorVorname() + " " + ba.getAuthorId().getAuthorName();
            }
            leihtable.setAutor(h);

            leihTableList.add(leihtable);
        }
        return leihTableList;
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
    public String getAutor() {
        return autor;
    }

    /**
     *
     * @param autor
     */
    public void setAutor(String autor) {
        this.autor = autor;
    }

    /**
     *
     * @return
     */
    public Date getVonDate() {
        return vonDate;
    }

    /**
     *
     * @param vonDate
     */
    public void setVonDate(Date vonDate) {
        this.vonDate = vonDate;
    }

    /**
     *
     * @return
     */
    public Date getBisDate() {
        return bisDate;
    }

    /**
     *
     * @param bisDate
     */
    public void setBisDate(Date bisDate) {
        this.bisDate = bisDate;
    }

}
