package webController;

import data.Author;
import data.Buch;
import data.Buchauthor;
import data.Exemplar;
import data.Genre;
import data.Verlag;
import data.Verlagbuch;
import data.WrapperExemplar;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import webController.util.JsfUtil;
import webFacade.WrapperFacade;

/**
 *
 * @author Susanne Otto
 */
@SessionScoped
@Named(value = "exemplarBean")
public class WrapperExemplarController implements Serializable {
    @EJB
    private WrapperFacade ms;

    private static final long serialVersionUID = 1L;

    private String authorEintrag;
    List<String> authorenEintragsList = new ArrayList<>();
    List<WrapperExemplar> exemplarListe = new ArrayList<>();
    WrapperExemplar exemplarTable = new WrapperExemplar();

    private String buchTitel;
    private String buchThema;
    private String authorVorname;
    private String authorName;
    private String authorGenre;
    private String verlagName;
    private String verlagbuchIsbn;
    private String exemplarNr;
    // für Ausgabe
    private String retAuthor = "";
    private String retBuch = "";
    private String retVerlag = "";
    private String retISBN = "";
    private String retExemplar = "";
    // für Suche
    private LinkedHashMap<String, Integer> alleVerlageMap = new LinkedHashMap();
    private LinkedHashMap<String, Integer> alleAutorenMap = new LinkedHashMap();
    private LinkedHashMap<String, Integer> alleBuecherMap = new LinkedHashMap();
    private final LinkedHashMap<String, Integer> verfuegbareBuecher = new LinkedHashMap();
    private Integer searchBuch;
    private Integer searchAutor;
    private Integer searchVerlag;
    private String searchTitel;

    /**
     * Creates a new instance of ExemplarBean
     */
    public WrapperExemplarController() {
    }

    /**
     *
     * @return
     */
    public String sucheBuchtitel() {
        return "search4AllBuchtitel";
    }

    /**
     *
     * @return Zielseite für Anzeige
     */
    public String sucheBuchtitelVerfuegbar() {
        // hier muss nach dem Titel gesucht, das Ergebnis in die Property searchBuch eingetragen und die Seite search4BuchVerfuegbar geöffnet werden
        return "search4BuchtitelVerfuegbar";
    }

    /** 
     * addAuthorId()
     * ermittelt passendes Objekt aus Property searchAutor und fügt Informationen zu Autorenliste hinzu
     * @return Zielseite für Anzeige der Informationen
     */
    public String addAuthorId() {
        Author myAutor = ms.getAuthorById(searchAutor);
        authorenEintragsList.add(myAutor.getAuthorVorname() + " " + myAutor.getAuthorName() + " " + myAutor.getAuthorGenre());
        searchAutor = null;
        return "CreateNewExemplar";
    }

    /** 
     * addAuthor()
     * fügt Autorendaten zu Autorenliste hinzu
     * @return Zielseite für Anzeige der Informationen
     */
    public String addAuthor() {
        System.out.println("Autor '" + searchAutor + "' erfassen...");
        authorenEintragsList.add(authorVorname + " " + authorName + " " + authorGenre);
        authorEintrag = null;
        return "CreateNewBuch";
    }

    /**
     * reset()
     * setzt Autorenliste zurück -> auf Button "Auswahl löschen"
     */
    public void reset() {
        authorenEintragsList.clear();
    }

    /** saveExemplar()
     *  speichert neues Exemplar eines vorhandenen Buches mit neuer Exemplar-Nr
     * 
     */
    public void saveExemplar() {
        // da Autor, Buch und Verlag ausgewählt wurden, müssen sie vorhanden sein
        // also werden nur VerlagBuch und Exemplar geprüft bzw. neu angelegt
        retBuch = "";
        retAuthor = "";
        retVerlag = "";
        retISBN = "";
        retExemplar = "";

        System.out.println("BuchId: " + searchBuch.toString() + ", VerlagsID: " + searchVerlag.toString());
        Buch tmpBuch = ms.getBuchById(searchBuch);
        retBuch = tmpBuch.getBuchTitel();
        Verlag tmpVerlag = ms.getVerlagById(searchVerlag);
        retVerlag = tmpVerlag.getVerlagName();

        // Autorenliste prüfen
        System.out.println("Autorenliste enthält " + authorenEintragsList.size() + " Einträge.");
        if (authorenEintragsList.isEmpty()) {
            System.out.println("keine Autoren ausgewählt, Speicherung wird abgebrochen");
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("config/Bundle").getString("MissingAutoren"));
        } else {
            for (String eintrag : authorenEintragsList) {
                String[] autor = eintrag.split(" ");
                Genre tmpGenre = ms.setNewGenre(autor[2]);
                Author tmpAuthor = ms.setNewAuthor(autor[0], autor[1], tmpGenre);
                if (tmpAuthor == null) {
                    System.out.println("kein Autor gefunden");
                    JsfUtil.addErrorMessage(ResourceBundle.getBundle("config/Bundle").getString("MissingData4Autor"));
                } else {
                    System.out.println("autor vorname= " + tmpAuthor.getAuthorVorname());
                    System.out.println("autor nachname  = " + tmpAuthor.getAuthorName());
                    System.out.println("autor genre  = " + tmpAuthor.getAuthorGenre());
                    System.out.println("\n");

                    retAuthor += tmpAuthor.getAuthorVorname() + " " + tmpAuthor.getAuthorName() + " (" + tmpAuthor.getAuthorGenre().getGenreName() + "), ";
                    // Buchautor prüfen
                    Buchauthor tmpBuchauthor = ms.setNewBuchAuthor(tmpAuthor, tmpBuch);
                    if (tmpBuchauthor != null) {
                        System.out.println("Buchautor: " + tmpBuchauthor.getBuchauthorId());
                    } else {
                        JsfUtil.addErrorMessage(ResourceBundle.getBundle("config/Bundle").getString("MissingData4Buchautor"));
                    }
                }
            }
            // Buchverlag prüfen
            Verlagbuch tmpBuchverlag = ms.setNewVerlagBuch(tmpVerlag, tmpBuch, verlagbuchIsbn);
            if (tmpBuchverlag == null) {
                JsfUtil.addErrorMessage(ResourceBundle.getBundle("config/Bundle").getString("MissingData4Buchverlag"));
            } else {
                System.out.println("isbn = " + tmpBuchverlag.getVerlagbuchIsbn());
                this.retISBN = tmpBuchverlag.getVerlagbuchIsbn();
                if (retISBN == null) {
                    JsfUtil.addErrorMessage(ResourceBundle.getBundle("config/Bundle").getString("MissingData4Isbn"));
                }

                // neues Exemplar erstellen
                Exemplar tmpExemplar = ms.setNewExemplar(tmpBuchverlag, exemplarNr);
                if (tmpExemplar == null) {
                    JsfUtil.addErrorMessage(ResourceBundle.getBundle("config/Bundle").getString("MissingData4Genre"));
                } else {
                    System.out.println("exemplar = " + tmpExemplar.getExemplarId());
                    retExemplar = tmpExemplar.getExemplarNr();
                }
            }
        }
    }

    /** 
     * save()
     * speichert ein neues Buch und legt Autor, Buchtitel, Verlag und Exemplar an, falls nicht vorhanden
     * (ToDo: sicherstellen, dass keine neuen Datensätze angelegt werden, weil es bei der Eingabe Tippfehler gab)
     */
    public void save() {

        String vorname;
        String name;
        String genre;

        Buch tmpBuch = null;

        retBuch = "";
        retAuthor = "";
        retVerlag = "";
        retISBN = "";
        retExemplar = "";

        System.out.println("Titel: " + buchTitel + " -> Thema: " + buchThema);
        System.out.println("Verlag: " + verlagName);
        System.out.println("ISBN: " + verlagbuchIsbn);
        System.out.println("ExemplarNr: " + exemplarNr);

        // Autorenliste prüfen    
        System.out.println("Autorenliste enthält " + authorenEintragsList.size() + " Einträge.");
        if (authorenEintragsList.isEmpty()) {
            System.out.println("keine Autoren ausgewählt, Speicherung wird abgebrochen");
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("config/Bundle").getString("MissingAutoren"));
        }
        for (String eintrag : authorenEintragsList) {

            String[] autor = eintrag.split(" ");
            vorname = autor[0];
            name = autor[1];
            genre = autor[2];

            System.out.println("Autor: " + vorname + " " + name + " (" + genre + ") ");

            // Genre überprüfen
            Genre tmpGenre = ms.setNewGenre(genre);
            if (tmpGenre == null) {
                System.out.println("ungültiger Genre-Eintrag, Speicherung wird abgebrochen");
                JsfUtil.addErrorMessage(ResourceBundle.getBundle("config/Bundle").getString("MissingData4Genre"));
            } else {
                // Autor überprüfen bzw. anlegen   
                Author tmpAuthor = ms.setNewAuthor(vorname, name, tmpGenre);
                if (tmpAuthor == null) {
                    System.out.println("kein Autor gefunden");
                    JsfUtil.addErrorMessage(ResourceBundle.getBundle("config/Bundle").getString("MissingData4Autor"));
                } else {
//                    authorenList.add(tmpAuthor);

                    System.out.println("autor vorname= " + tmpAuthor.getAuthorVorname());
                    System.out.println("autor nachname  = " + tmpAuthor.getAuthorName());
                    System.out.println("autor genre  = " + tmpAuthor.getAuthorGenre());
                    System.out.println("\n");

                    retAuthor += tmpAuthor.getAuthorVorname() + " " + tmpAuthor.getAuthorName() + " (" + tmpAuthor.getAuthorGenre().getGenreName() + "), ";

                    // Buchtitel prüfen
                    tmpBuch = ms.setNewBuch(buchTitel, buchThema);
                    if (tmpBuch == null) {
                        System.out.println("kein Buch gefunden");
                        JsfUtil.addErrorMessage(ResourceBundle.getBundle("config/Bundle").getString("MissingData4Buch"));
                    } else {
                        System.out.println("buchtitel = " + tmpBuch.getBuchTitel());
                        this.retBuch = tmpBuch.getBuchTitel();

                        // BuchAuthor überprüfen
                        Buchauthor tmpBuchauthor = ms.setNewBuchAuthor(tmpAuthor, tmpBuch);
                        if (tmpBuchauthor != null) {
                            System.out.println("Buchautor: " + tmpBuchauthor.getBuchauthorId());
                        } else {
                            JsfUtil.addErrorMessage(ResourceBundle.getBundle("config/Bundle").getString("MissingData4Buchautor"));
                        }
                    }
                }
            }
        }
        // Verlag prüfen
        Verlag tmpVerlag = ms.setNewVerlag(verlagName);
        System.out.println("verlag = " + tmpVerlag.getVerlagName());
        this.retVerlag = tmpVerlag.getVerlagName();

        // Buchverlag prüfen
        if (tmpBuch != null) {

            Verlagbuch tmpBuchverlag = ms.setNewVerlagBuch(tmpVerlag, tmpBuch, verlagbuchIsbn);
            if (tmpBuchverlag == null) {
                JsfUtil.addErrorMessage(ResourceBundle.getBundle("config/Bundle").getString("MissingData4Buchverlag"));
            } else {
                System.out.println("isbn = " + tmpBuchverlag.getVerlagbuchIsbn());
                this.retISBN = tmpBuchverlag.getVerlagbuchIsbn();
                if (retISBN == null) {
                    JsfUtil.addErrorMessage(ResourceBundle.getBundle("config/Bundle").getString("MissingData4Isbn"));
                }
                // neues Exemplar erstellen
                Exemplar tmpExemplar = ms.setNewExemplar(tmpBuchverlag, exemplarNr);
                if (tmpExemplar == null) {
                    JsfUtil.addErrorMessage(ResourceBundle.getBundle("config/Bundle").getString("MissingData4Genre"));
                } else {
                    System.out.println("exemplar = " + tmpExemplar.getExemplarId());
                    retExemplar = tmpExemplar.getExemplarNr();
                }
            }
        }
    }

    /** 
     * getErgebnis()
     *  setzt einen String aus den übergebenen Infromationen zusammen
     */
        public void getErgebnis() {

        String retStr;

        if (retExemplar == null) {
            retStr = "Es konnte keine neue Exemplar-Nr. vergeben werden.";
        } else {
            retStr = "Das Buch '" + retBuch + "'"
                    + " von " + retAuthor
                    + " aus dem Verlag '" + retVerlag + "'"
                    + " in Auflage [" + retISBN + "]"
                    + " wurde als Exemplar <" + retExemplar + ">"
                    + " archiviert.";
        }
        System.out.println(retStr);
    }

    /** 
     * getAlleExemplarTable()
     * Suche über alle Exemplare
     * @return Liste aller Exemplare
     */
        public List<WrapperExemplar> getAlleExemplarTable() {
        return exemplarTable.createExemplarTable(ms.getAlleExemplare());
    }



    /**
     * getExemplarTableByBuch()
     * Suche über Buch-ID
     * @return Exemplarliste für bestimmtes Buch
     */
        public List<WrapperExemplar> getExemplarTableByBuch() {
        return exemplarTable.createExemplarTable(ms.getExemplarByBuch(searchBuch));
    }

    /**
     * getExemplarTableByAuthor()
     * Suche über Autor-ID
     * @return Exemplarliste für bestimmten Autor
     */
        public List<WrapperExemplar> getExemplarTableByAutor() {
        return exemplarTable.createExemplarTable(ms.getExemplarByAutor(searchAutor));
    }

    /**
     * getExemplarTableByVerlag()
     * Suche über Verlags-ID
     * @return Exemplarliste für bestimmten Verlag
     */
        public List<WrapperExemplar> getExemplarTableByVerlag() {
        //    System.out.println("selektierte Verlags-ID: '" + searchVerlag + "' ");
        return exemplarTable.createExemplarTable(ms.getExemplarByVerlag(searchVerlag));
    }

    /**
     * getExemplarTableByTitel()
     * Suche über Buchtitel (mit Wildcards)
     * @return Exemplarliste für Buchtitel 
     */
        public List<WrapperExemplar> getExemplarTableByTitel() {
        return exemplarTable.createExemplarTable(ms.getExemplarByBuchTitel(searchTitel));
    }

    // Suche über alle verfügbaren Exemplare

    /**
     * getTableVerfuegbar()
     * Suche über alle verfügbaren Exemplare
     * @return Liste aller Exemplare, die nicht als ausgeliehen oder reserviert gekennzeichnet sind
     */
        public List<WrapperExemplar> getTableVerfuegbar() {
        exemplarListe = exemplarTable.createExemplarTable(ms.getVerfuegbareExemplare());
        this.setVerfuegbareBuecher(exemplarListe);
        return exemplarListe;
    }
    
    /**
     * getTableVerfuegbarByVerlag()
     * Suche über alle verfügbaren Exemplare eines Verlages
     * @return Liste aller Exemplare, die nicht als ausgeliehen oder reserviert gekennzeichnet sind
     */
        public List<WrapperExemplar> getTableVerfuegbarByVerlag() {
        if (searchVerlag == null) {
            verfuegbareBuecher.clear();
            return null;
        }
        exemplarListe = exemplarTable.createExemplarTable(ms.getVerfuegbareExemplareByVerlag(searchVerlag));
        this.setVerfuegbareBuecher(exemplarListe);
        return exemplarListe;
    }

    /**
     * getTableVerfuegbarByBuch()
     * Suche über alle verfügbaren Exemplare eines Buches
     * @return Liste aller Exemplare, die nicht als ausgeliehen oder reserviert gekennzeichnet sind
     */
        public List<WrapperExemplar> getTableVerfuegbarByBuch() {
        if (searchBuch == null) {
            verfuegbareBuecher.clear();
            return null;
        }
        exemplarListe = exemplarTable.createExemplarTable(ms.getVerfuegbareExemplareByBuch(searchBuch));
        this.setVerfuegbareBuecher(exemplarListe);
        return exemplarListe;
    }

    // Suche verfügbarer Exemplare über Buchtitel

    /**
     * getTableVerfuegbarByBuchtitel()
     * Suche über alle verfügbaren Exemplare eines Buchtitels
     * @return Liste aller Exemplare, die nicht als ausgeliehen oder reserviert gekennzeichnet sind
     */
        public List<WrapperExemplar> getTableVerfuegbarByBuchtitel() {
        if (searchTitel == null) {
            verfuegbareBuecher.clear();
            return null;
        } else {
            exemplarListe = exemplarTable.createExemplarTable(ms.getVerfuegbareExemplareByBuchtitel(searchTitel));
            this.setVerfuegbareBuecher(exemplarListe);
            return exemplarListe;
        }
    }

    /**
     * getTableVerfuegbarByAutor()
     * Suche über alle verfügbaren Exemplare eines Autors
     * @return Liste aller Exemplare, die nicht als ausgeliehen oder reserviert gekennzeichnet sind
     */
        public List<WrapperExemplar> getTableVerfuegbarByAutor() {
        System.out.println("searchAutor = " + searchAutor);

        if (searchAutor == null) {
            verfuegbareBuecher.clear();
            return null;
        } else {
            exemplarListe = exemplarTable.createExemplarTable(ms.getVerfuegbareExemplareByAutor(searchAutor));
            this.setVerfuegbareBuecher(exemplarListe);
            return exemplarListe;
        }
    }

    // Gertter und Setter

    /**
     *
     * @return
     */
        public String getBuchTitel() {
        return buchTitel;
    }

    /**
     *
     * @param buchTitel
     */
    public void setBuchTitel(String buchTitel) {
        this.buchTitel = buchTitel;
    }

    /**
     *
     * @return
     */
    public String getBuchThema() {
        return buchThema;
    }

    /**
     *
     * @param buchThema
     */
    public void setBuchThema(String buchThema) {
        this.buchThema = buchThema;
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
     * @param authorFname
     */
    public void setAuthorVorname(String authorFname) {
        this.authorVorname = authorFname;
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
     * @param authorLname
     */
    public void setAuthorName(String authorLname) {
        this.authorName = authorLname;
    }

    /**
     *
     * @return
     */
    public String getAuthorGenre() {
        return authorGenre;
    }

    /**
     *
     * @param genre
     */
    public void setAuthorGenre(String genre) {
        this.authorGenre = genre;
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
    public String getAuthorEintrag() {
        return authorEintrag;
    }

    /**
     *
     * @param authorEintrag
     */
    public void setAuthorEintrag(String authorEintrag) {
        this.authorEintrag = authorEintrag;
    }

    /**
     *
     * @return
     */
    public List<String> getAuthorenEintragsList() {
        return authorenEintragsList;
    }

    /**
     *
     * @param authorenEintragsList
     */
    public void setAuthorenEintragsList(List<String> authorenEintragsList) {
        this.authorenEintragsList = authorenEintragsList;
    }

    /**
     *
     * @return
     */
    public Integer getSearchBuch() {
        return searchBuch;
    }

    /**
     *
     * @param searchBuch
     */
    public void setSearchBuch(Integer searchBuch) {
        this.searchBuch = searchBuch;
    }

    /**
     *
     * @return
     */
    public Integer getSearchAutor() {
        return searchAutor;
    }

    /**
     *
     * @param searchAutor
     */
    public void setSearchAutor(Integer searchAutor) {
        this.searchAutor = searchAutor;
    }

    /**
     *
     * @return
     */
    public Integer getSearchVerlag() {
        return searchVerlag;
    }

    /**
     *
     * @param searchVerlag
     */
    public void setSearchVerlag(Integer searchVerlag) {
        this.searchVerlag = searchVerlag;
    }

    /**
     *
     * @return
     */
    public String getSearchTitel() {
        return searchTitel;
    }

    /**
     *
     * @param searchTitel
     */
    public void setSearchTitel(String searchTitel) {
        this.searchTitel = searchTitel;
    }

    /**
     *
     * @return
     */
    public LinkedHashMap<String, Integer> getAlleBuecherMap() {

        List<Buch> buchListe = ms.alleBuchTitel();
        alleBuecherMap.put("---", null);
        for (Buch b : buchListe) {
            alleBuecherMap.put(b.getBuchTitel(), b.getBuchId());
        }
        return alleBuecherMap;
    }

    /**
     *
     * @param alleBuecherMap
     */
    public void setAlleBuecherMap(LinkedHashMap<String, Integer> alleBuecherMap) {
        this.alleBuecherMap = alleBuecherMap;
    }

    /**
     *
     * @return
     */
    public LinkedHashMap<String, Integer> getAlleVerlageMap() {

        List<Verlag> verlagsListe = ms.alleVerlage();
        alleVerlageMap.put("---", null);
        for (Verlag v : verlagsListe) {
            alleVerlageMap.put(v.getVerlagName(), v.getVerlagId());
        }
        return alleVerlageMap;
    }

    /**
     *
     * @param alleVerlageMap
     */
    public void setAlleVerlageMap(LinkedHashMap<String, Integer> alleVerlageMap) {
        this.alleVerlageMap = alleVerlageMap;
    }

    /**
     *
     * @return
     */
    public LinkedHashMap<String, Integer> getAlleAutorenMap() {

        List<Author> autorenListe = ms.alleAutoren();
        alleAutorenMap.put("---", null);
        for (Author a : autorenListe) {
            alleAutorenMap.put(a.getAuthorVorname() + " " + a.getAuthorName(), a.getAuthorId());
        }
        return alleAutorenMap;
    }

    /**
     *
     * @param alleAutorenMap
     */
    public void setAlleAutorenMap(LinkedHashMap<String, Integer> alleAutorenMap) {
        this.alleAutorenMap = alleAutorenMap;
    }

    /**
     *
     * @return
     */
    public LinkedHashMap<String, Integer> getVerfuegbareBuecher() {
        return verfuegbareBuecher;
    }

    /**
     *
     * @param exemplarListe
     */
    public void setVerfuegbareBuecher(List<WrapperExemplar> exemplarListe) {

        verfuegbareBuecher.clear();
        for (WrapperExemplar ex : exemplarListe) {
            verfuegbareBuecher.put(ex.toString(), ex.getExemplarId());
        }
    }

}
