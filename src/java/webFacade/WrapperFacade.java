package webFacade;

import data.Author;
import data.Buch;
import data.Buchauthor;
import data.Exemplar;
import data.Kunde;
import data.Ausleihe;
import data.Genre;
import data.Reservierung;
import data.Verlag;
import data.Verlagbuch;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import webController.util.JsfUtil;

/**
 *
 * @author Susanne Otto
 */
@Stateless
public class WrapperFacade {

    @PersistenceContext(unitName = "Buchbox-webPU")
    private EntityManager em;

    /**
     * persist(object)
     * save Object
     * @param object
     */
    public void persist(Object object) {
        em.persist(object);
    }

    /**
     * alleGenres()
     * @return Liste aller Genre
     */
    public List<Genre> alleGenres() {
        return em.createNamedQuery("Genre.findAll").getResultList();
    }

    /**
     * alleAutoren()
     * @return Liste aller Autoren
     */
    public List<Author> alleAutoren() {
        return em.createNamedQuery("Author.findAll").getResultList();
    }

    /**
     * alleBuecher()
     * @return Liste aller Bücher aus Tabelle BuchAuthor
     */
    public List<Buchauthor> alleBuecher() {
        return em.createNamedQuery("Buchauthor.findAll").getResultList();
    }

    /**
     * alleBuchTitel()
     * @return Liste aller Bücher aus Tabelle Buch
     */
    public List<Buch> alleBuchTitel() {
        return em.createNamedQuery("Buch.findAll").getResultList();
    }

    /**
     * alleKunden()
     * @return Liste aller Kunden
     */
    public List<Kunde> alleKunden() {
        return em.createNamedQuery("Kunde.findAll").getResultList();
    }

    /**
     * alleVerlage
     * @return Liste aller Verlage
     */
    public List<Verlag> alleVerlage() {
        return em.createNamedQuery("Verlag.findAll").getResultList();
    }

    /**
     * getKundeById(kundeId)
     * @param kundeId
     * @return Kundenobjekt zu übergebener Kunden-Nr.
     */
    public Kunde getKundeById(Integer kundeId) {
        Query q = em.createNamedQuery("Kunde.findByKundeId");
        q.setParameter("kundeId", kundeId);
        return (Kunde) q.getSingleResult();
    }

    /**
     * getAlleExemplare()
     * @return Liste aller Exemplare
     */
    public List<Exemplar> getAlleExemplare() {
        return em.createNamedQuery("Exemplar.findAll").getResultList();
    }

    /**
     * getExemplarById(exemplarId)
     * @param exemplarId
     * @return Exemplarobjekt zu übergebener ExemplarID
     */
    public Exemplar getExemplarById(Integer exemplarId) {
        Query q = em.createNamedQuery("Exemplar.findByExemplarId");
        q.setParameter("exemplarId", exemplarId);
        return (Exemplar) q.getSingleResult();
    }

    /**
     * getExemplarByVerlag(verlagId)
     * @param verlagId
     * @return Liste aller Exemplare mit übergebener VerlagID
     */
    public List<Exemplar> getExemplarByVerlag(Integer verlagId) {
        Query q = em.createNamedQuery("Exemplar.findByVerlagId").setParameter("verlagId", verlagId);
        return q.getResultList();
    }

    /**
     * getExemplarByAutor(autorId)
     * @param authorId
     * @return Liste aller Exemplare für Autor mit übergebener Autoren-Nr
     */
    public List<Exemplar> getExemplarByAutor(Integer authorId) {
        // Liste aller Buchautoren mit übergebener Autor-ID
        Query qBa = em.createNamedQuery("Buchauthor.findByAuthorId");
        List<Buchauthor> baList = qBa.setParameter("authorId", authorId).getResultList();

        // Liste aller Buchverlage für jedes Buch aus Buchautor-Liste
        List<Verlagbuch> vbList = new ArrayList<>();
        for (Buchauthor ba : baList) {
            Query qVb = em.createNamedQuery("Verlagbuch.findByBuch");
            vbList = qVb.setParameter("buchId", ba.getBuchId()).getResultList();
        }
        // Liste aller Exemplare für jeden Buchverlag aus Exemplar-Liste
        List<Exemplar> exList = new ArrayList<>();
        for (Verlagbuch vb : vbList) {
            System.out.println("Verlagbuch -> verlagID: " + vb.getVerlagId().getVerlagId() + " verlagbuchID: " + vb.getVerlagbuchId());
            Query q = em.createNamedQuery("Exemplar.findByVerlagBuchId").setParameter("verlagbuchId", vb.getVerlagbuchId());
            exList = q.getResultList();
            for (Exemplar ex : exList) {
                System.out.println("exemplar -> exemplarID:" + ex.getExemplarNr() + " verlagbuchID: " + ex.getVerlagbuchId().getVerlagbuchId() + " buchID:" + ex.getVerlagbuchId().getBuchId().getBuchId());
            }
        }
        return exList;
    }

    /**
     * getExemplarByBuch(buchId)
     * @param buchId
     * @return Liste aller Exemplare für Bücher mit übergebener BuchId
     */
    public List<Exemplar> getExemplarByBuch(Integer buchId) {
        Query q = em.createNamedQuery("Exemplar.findByBuchId").setParameter("buchId", buchId);
        return q.getResultList();
    }

    /**
     * getExemplarByBuchTitel(buchTitel)
     * @param buchTitel
     * @return Liste aller Exemplare für Bücher, deren Titel den übergebenen String enthält
     */
    public List<Exemplar> getExemplarByBuchTitel(String buchTitel) {
        Query q = em.createNamedQuery("Exemplar.findByBuchtitel").setParameter("buchId", "%" + buchTitel + "%");
        return q.getResultList();
    }

    /**
     * getAlleAusleihen()
     * @return Liste aller Exemplare aus Ausleih-Tabelle, die kein Rückgabedatum haben
     */
    public List<Ausleihe> getAlleAusleihen() {
        return em.createNamedQuery("Ausleihe.findRueckgabeDatumIsNull").getResultList();
    }

    /**
     * getAlleAusleihenByKunde()
     * @return Liste aller Ausleihen eines Kunden
     */
    public List<Ausleihe> getAlleAusleihenByKunde() {
        return em.createNamedQuery("Ausleihe.findAllByKundeId").getResultList();
    }

    /**
     * getAlleReservierungen()
     * @return Liste aller reservierten Exemplare
     */
    public List<Reservierung> getAlleReservierungen() {
        return em.createNamedQuery("Reservierung.findAll").getResultList();
    }

    /**
     * getAusleiheById(leihId)
     * @param leihId
     * @return Ausleihobjekt mit übergebener LeihId
     */
    public Ausleihe getAusleiheById(Integer leihId) {
        Query q = em.createNamedQuery("Ausleihe.findByLeihId");
        q.setParameter("leihId", leihId);
        return (Ausleihe) q.getSingleResult();
    }

    /**
     * getAusleiheByKundeId(kundeId)
     * @param kundeId
     * @return Liste aller Ausleihen eines Kunden
     */
    public List<Ausleihe> getAusleiheByKundeId(Integer kundeId) {
        Query q = em.createNamedQuery("Ausleihe.findAllByKundeId");
        q.setParameter("kundeId", kundeId);
        return q.getResultList();
    }

    /**
     * getAusgeliehenByKunde(kundeId)
     * @param kundeId
     * @return Liste aller aktuell ausgeliehenen Exemplare eines Kunden
     */
    public List<Ausleihe> getAusgeliehenByKundeId(Integer kundeId) {
        Query q = em.createNamedQuery("Ausleihe.findAusgeliehenByKundeId");
        q.setParameter("kundeId", kundeId);
        return q.getResultList();
    }

    /**
     * getAusgeliehenByExemplarId(exemplarId)
     * @param exemplarId
     * @return Liste aller ausgeliehenen Exemplare mit übergebener ExemplarId
     */
    public List<Ausleihe> getAusgeliehenByExemplarId(Integer exemplarId) {
        Query q = em.createNamedQuery("Ausleihe.findAusgeliehenByExemplarId");
        q.setParameter("exemplarId", exemplarId);
        return q.getResultList();
    }

    /**
     * getAuthorById(authorId)
     * @param autorId
     * @return Autorenobjekt mit übergebener AutorId
     */
    public Author getAuthorById(Integer autorId) {
        Query q = em.createNamedQuery("Author.findByAuthorId");
        q.setParameter("authorId", autorId);
        return (Author) q.getSingleResult();
    }

    /**
     * getBuchautorById(buchId)
     * @param buchId
     * @return Liste aller Einträge aus Buchautor-Tabelle mit übergebener BuchId 
     * (wird benötigt, da ein Buch mehrere Autoren haben kann)
     */
    public List<Buchauthor> getBuchauthorById(Integer buchId) {
        Query q = em.createNamedQuery("Buchauthor.findByBuchId");
        q.setParameter("buchId", buchId);
        return q.getResultList();
    }

    /**
     * getBuchById(buchId)
     * @param buchId
     * @return Buchobjekt für übergebene BuchId
     */
    public Buch getBuchById(Integer buchId) {
        Query q = em.createNamedQuery("Buch.findByBuchId");
        q.setParameter("buchId", buchId);
        return (Buch) q.getSingleResult();
    }

    /**
     * getVerlagById(verlagId)
     * @param verlagId
     * @return Verlagsobjekt für übergebene VerlagID
     */
    public Verlag getVerlagById(Integer verlagId) {
        Query q = em.createNamedQuery("Verlag.findByVerlagId");
        q.setParameter("verlagId", verlagId);
        return (Verlag) q.getSingleResult();
    }

    /**
     * getGenreByName
     * @param genreName
     * @return Genreobjekt für übergebenen Genrenamen
     */
    public Genre getGenreByName(String genreName) {
        Query q = em.createNamedQuery(genreName);
        q.setParameter("genreName", genreName);
        return (Genre) q.getSingleResult();
    }

    /**
     * setAusleihe(kundeId, exemplarId)
     * @param kundeId
     * @param exemplarId
     * erstellt neuen Ausleihe-Datensatz für übergebenen Kunden und übergebenes Exemplar
     * @return erstelltes Ausleiheobjekt 
     */
    public Ausleihe setAusleihe(Integer kundeId, Integer exemplarId) {

        Ausleihe newAusleihe = new Ausleihe();

        Kunde k = getKundeById(kundeId);
        Exemplar ex = getExemplarById(exemplarId);

        newAusleihe.setKundeId(k);
        newAusleihe.setExemplarId(ex);

        Date ausleihDate = new Date();
        newAusleihe.setAusleihDatum(ausleihDate);

        try {
            em.persist(newAusleihe);
            return newAusleihe;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * setRueckgabe(exemplarId, kundeId)
     * @param exemplarId
     * @param kundeId
     * sucht nach Ausleiheobjekt für übergebenen Kunden und übergebenes Exemplar
     * und markiert das Objekt als "zurückgegeben" (= setzt Rückgabedatum)
     * 
     * @return Ausleiheobjekt
     */
    public Ausleihe setRueckgabe(Integer exemplarId, Integer kundeId) {
        Date rueckgabeDate = new Date();
        System.out.println("Rückgabe am :" + rueckgabeDate);

        Query q = em.createNamedQuery("Ausleihe.findAusgeliehenByKundeExemplar");
        q.setParameter("exemplarId", exemplarId);
        q.setParameter("kundeId", kundeId);
          System.out.println("Anzahl gefundener Datensätze: " + q.getResultList().size());
        Ausleihe rueckgabe = (Ausleihe) q.getSingleResult();
        rueckgabe.setLeihRueckgabedatum(rueckgabeDate);
        try {
            em.persist(rueckgabe);
            return rueckgabe;
        } catch (Exception e) {
            System.out.println("Fehler bei der Buch-Rückgabe: " + e);
            return null;
        }
    }

    /**
     * setRueckgabe(leihId)
     * @param leihId
     * sucht nach Ausleiheobjekt mit übergebener LeihId
     * und markiert das Objekt als "zurückgegeben" (= setzt Rückgabedatum)
     * 
     * @return Ausleiheobjekt
     */
    public Ausleihe setRueckgabe(Integer leihId) {
        Date rueckgabeDate = new Date();
        System.out.println("Rückgabe am :" + rueckgabeDate);

        Query q = em.createNamedQuery("Ausleihe.findByLeihId").setParameter("leihId", leihId);
        Ausleihe rueckgabe = (Ausleihe) q.getSingleResult();
        rueckgabe.setLeihRueckgabedatum(rueckgabeDate);
        try {
            em.persist(rueckgabe);
            return rueckgabe;
        } catch (Exception e) {
            System.out.println("Fehler bei der Buch-Rückgabe: " + e);
            return null;
        }
    }

    /**
     * getLeihscheinById()
     * 
     * (Methode ist sinnlos, da der Übergabeparameter für Abfrage fehlt!!!)
     * @return Liste aller Ausleihen für LeihId
     */
    public List<Ausleihe> getLeihscheinById() {
        return em.createNamedQuery("Ausleihe.findByLeihId").getResultList();
    }

    /**
     * getVerfuegbareExemplareByVerlag(verlagId)
     * @param verlagId
     * ruft Methode getListVerfuegbar mit Filter Verlag auf
     * @return Liste aller verfügbaren Exemplare eines Verlages
     */
    public List<Exemplar> getVerfuegbareExemplareByVerlag(Integer verlagId) {

        return getListVerfuegbar("verlag", String.valueOf(verlagId));
    }

    /**
     * getVerfuegbareExemplareByBuch(buchId)
     * @param buchId
     * ruft Methode getListVerfuegbar mit Filter Buch auf
     * @return Liste aller verfügbaren Exemplare einer BuchID
     */
    public List<Exemplar> getVerfuegbareExemplareByBuch(Integer buchId) {

        return getListVerfuegbar("buch", String.valueOf(buchId));
    }

    /**
     * getVerfuegbareExemplareByBuchtitel
     * @param buchtitel
     * ruft Methode getListVerfuegbar mit Filter Buchtitel auf
     * @return Liste aller verfügbaren Exemplare eines Buchs mit übergebenem Titel
     */
    public List<Exemplar> getVerfuegbareExemplareByBuchtitel(String buchtitel) {

        return getListVerfuegbar("buch", buchtitel);
    }

    /**
     * getVerfuegbareExemplareByAutor
     * @param autorId
     * ruft Methode getListVerfuegbar mit Filter Autor auf
     * @return Liste aller verfügbaren Exemplare eines Autors
     */
    public List<Exemplar> getVerfuegbareExemplareByAutor(Integer autorId) {

        return getListVerfuegbar("autor", String.valueOf(autorId));
    }

    /**
     * getVerfuegbareExemplare()
     * ruft Methode getListVerfuegbar ohne Filter auf
     * @return Liste aller verfügbaren Exemplare
     */
    public List<Exemplar> getVerfuegbareExemplare() {

        return getListVerfuegbar("alle", "");
    }

    /**
     * getListVerfuegbar(filter, value)
     * @param filter Feld, nach dem gefiltert werden soll
     * @param value Wert für Filterkriterium
     * @return Liste aller verfügbaren Exemplare nach übergebem Filter
     */
    private List<Exemplar> getListVerfuegbar(String filter, String value) {

        Date nichtAbgegebenDate = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String leer = "0000-00-00";
        try {
            nichtAbgegebenDate = df.parse(leer);
        } catch (ParseException ex) {
            Logger.getLogger(WrapperFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

        Date rueckgabeDate = new Date();
        List<Exemplar> alleExemplare = getListAll(filter, value);
        List<Ausleihe> alleAusleihe = getListAusgeliehen();
        List<Reservierung> alleReservierung = getListReserviert();
        List<Exemplar> nichtverfuegbarExemplare = new ArrayList();

        for (Exemplar next : alleExemplare) {
            for (Ausleihe aus : alleAusleihe) {
                if (next.getExemplarNr().equals(aus.getExemplarId().getExemplarNr())) {
                    nichtverfuegbarExemplare.add(next);
                }
            }
            // für Release 2: reservierte Bücher sind nicht verfügbar
            for (Reservierung res : alleReservierung) {
                if (next.getExemplarNr().equals(res.getExemplarId().getExemplarNr())) {
                    nichtverfuegbarExemplare.add(next);
                }
            }
        }
        alleExemplare.removeAll(nichtverfuegbarExemplare);
        return alleExemplare;
    }

    /**
     * getListAusgeliehen()
     * @return Liste aller Ausleihen, die kein Rückgabedatum haben
     */
    public List<Ausleihe> getListAusgeliehen() {
        return em.createNamedQuery("Ausleihe.findRueckgabeDatumIsNull").getResultList();
    }

    /**
     * getListReserviert
     * @return Liste aller Reservierungen, die als aktiv markiert sind
     */
    public List<Reservierung> getListReserviert() {
        Query q = em.createNamedQuery("Reservierung.findByReservierungAktiv").setParameter("reservierungAktiv", true);
        return q.getResultList();
    }

    /**
     * getListAll(filter, value)
     * @param filter Feld, nach dem gefiltert werden sol
     * @param value Wert des Filterkriteriums
     * @return Liste aller Exemplare nach übergebenem Filter
     */
    public List<Exemplar> getListAll(String filter, String value) {
        List<Exemplar> liste = new ArrayList<>();

        switch (filter) {
            case "verlag":
                liste = this.getExemplarByVerlag(Integer.parseInt(value));
                break;
            case "buch":
                liste = this.getExemplarByBuch(Integer.parseInt(value));
                break;
            case "buchtitel":
                liste = this.getExemplarByBuchTitel(value);
                break;
            case "autor":
                liste = this.getExemplarByAutor(Integer.parseInt(value));
                break;
            default:
                liste = this.getAlleExemplare();
        }
        return liste;
    }

    /**
     * getBuchByTitel(buchtitel)
     * @param buchTitel
     * @return Liste aller Bücher (aus Buchautor) mit übergebenem Titel
     */
    public List<Buchauthor> getBuchbyTitel(String buchTitel) {
        Query q = em.createNamedQuery("Buchauthor.findBuchByBuchTitel");
        q.setParameter("buchTitel", "%" + buchTitel + "%");
        return q.getResultList();
    }

    /**
     * setGenre(genre)
     * @param genre
     * prüft, ob ein Genreobjektr mit übergebenem Namen existiert bzw. erstellt es 
     * @return Genreobjekt
     */
        public Genre setNewGenre(String genre) {
        // neues Objekt anlegen
        Genre g = new Genre();
        // Attribute setzen
        g.setGenreName(genre);
        // prüfen, ob vorhanden
        Query q = em.createNamedQuery("Genre.findByGenreName");
        q.setParameter("genreName", genre);
        if (q.getResultList().size() > 0) {
            return (Genre) q.getResultList().get(0);
        } else {
            try {
                em.persist(g);
                return g;                                   // Rückgabe des neu erstellten Objektes
            } catch (Exception e) {
                return null;
            }
        }
    }

    // Author prüfen

    /**
     * setNewAuthor
     * @param vName
     * @param name
     * @param genre
     * prüft, ob ein Autorobjekt mit übergebenem Namen existiert bzw. erstellt es
     * @return Autorobjekt
     */
        public Author setNewAuthor(String vName, String name, Genre genre) {
        // neues Objekt anlegen
        Author a = new Author();
        // Attribute setzen
        a.setAuthorVorname(vName);
        a.setAuthorName(name);
        a.setAuthorGenre(genre);

        // prüfen, ob bereits vorhanden
        Query q = em.createNamedQuery("Author.findByAuthorName");
        q.setParameter("authorName", name);
        q.setParameter("authorVorname", vName);
        if (q.getResultList().size() > 0) {
            return (Author) q.getResultList().get(0);       // Rückgabe des vorhandenen Objektes
        } else {
            try {
                em.persist(a);
                return a;                                   // Rückgabe des neu erstellten Objektes
            } catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * setNewVerlag
     * @param name
     * prüft, ob ein Vertagsobjekt mit übergebenem Namen existiert bzw. erstellt es 
     * @return Vertragsgsobjekt
     */
        public Verlag setNewVerlag(String name) {
        // neues Objekt anlegen
        Verlag v = new Verlag();
        // Attribute setzen
        v.setVerlagName(name);

        // prüfen, ob bereits vorhanden
        Query q = em.createNamedQuery("Verlag.findByVerlagNameLike").setParameter("verlagName", "%" + name + "%");
        if (q.getResultList().size() > 0) {
            return (Verlag) q.getResultList().get(0);       // Rückgabe des vorhandenen Objektes
        } else {
            try {
                em.persist(v);
                return v;                                   // Rückgabe des neu erstellten Objektes
            } catch (Exception e) {
                return null;
            }
        }
    }

    // Buch prüfen

    /**
     * setNewBuch
     * @param titel
     * @param thema
     * prüft, ob ein Buchobjekt mit übergebenem Titel existiert bzw. erstellt es
     * @return
     */
        public Buch setNewBuch(String titel, String thema) {
        Buch b = new Buch();
        b.setBuchTitel(titel);
        b.setBuchThema(thema);

        Query q = em.createNamedQuery("Buch.findByBuchTitel");
        q.setParameter("buchTitel", titel);
        if (q.getResultList().size() > 0) {
            return (Buch) q.getResultList().get(0);
        } else {
            try {
                em.persist(b);
                return b;
            } catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * setNewBuchAutor
     * @param a
     * @param b
     * prüft, ob es einen Buchautor für übergebenes Buch und Autor gibt bzw. erstellt es
     * @return Buchautorobjekt
     */
        public Buchauthor setNewBuchAuthor(Author a, Buch b) {
        // leeren Author anlegen
        Buchauthor ba = new Buchauthor();
        ba.setAuthorId(a);
        ba.setBuchId(b);

        Query q = em.createNamedQuery("Buchauthor.findByBuchAuthor");
        q.setParameter("buchId", b);
        q.setParameter("authorId", a);
        if (q.getResultList().size() > 0) {
            return (Buchauthor) q.getResultList().get(0);
        } else {
            try {
                em.persist(ba);
                return ba;
            } catch (Exception e) {
                return null;
            }
        }
    }

    // Zwischentabelle VerlagBuch prüfen

    /**
     * setNewVerlagBuch
     * @param v
     * @param b
     * @param isbn
     * prüft, ob ein VerlagBuchobjekt mit übergebenem Buch aus übergebenem Verlag bzw. erstellen es
     * @return Verlagbuchobjekt
     */
        public Verlagbuch setNewVerlagBuch(Verlag v, Buch b, String isbn) {
        Verlagbuch vb = new Verlagbuch();
        vb.setVerlagId(v);
        vb.setBuchId(b);
        vb.setVerlagbuchIsbn(isbn);

        Query q = em.createNamedQuery("Verlagbuch.findByVerlagBuch");
        q.setParameter("verlagId", v);
        q.setParameter("buchId", b);
        if (q.getResultList().size() > 0) {
            return (Verlagbuch) q.getResultList().get(0);
        } else {
            try {
                em.persist(vb);
                return vb;
            } catch (Exception e) {
                return null;
            }
        }
    }

    // Zwischentabelle Exemplar prüfen

    /**
     * setNewExemplar
     * @param vb
     * @param exemplarNr
     * prüft, ob ein Exemplarobjekt mit übergebener ExemplarNr existiert bzw. erstellen es
     * @return Exemplarobjekt
     */
        public Exemplar setNewExemplar(Verlagbuch vb, String exemplarNr) {
        Exemplar ex = new Exemplar();
        ex.setVerlagbuchId(vb);
        ex.setExemplarNr(exemplarNr);
        Query q = em.createNamedQuery("Exemplar.findByExemplarNr").setParameter("exemplarNr", exemplarNr);
        if (q.getResultList().size() > 0) {
            System.out.println("Exemplar bereits vorhanden");
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("config/Bundle").getString("DoubleExemplar"));
            return null;
        } else {
            try {
                em.persist(ex);
                System.out.println("neues Exemplar angelegt: " + ex.getExemplarNr());
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("config/Bundle").getString("ExemplarCreated"));
                return ex;
            } catch (Exception e) {
                System.out.println("Exemplarerstellung gescheitert");
                JsfUtil.addErrorMessage(ResourceBundle.getBundle("config/Bundle").getString("MissingData4Exemplar"));
                return null;
            }
        }

    }

    /**
     *setLeihe
     * @param kundeId
     * @param exemplarId
     * erzeugt neues AUsleiheobjekt für übergebenes Exmeplar und übergebenen Kunden
     * @return Ausleiheobjekt
     */
    public Ausleihe setLeihe(Integer kundeId, Integer exemplarId) {

        Ausleihe newLeih = new Ausleihe();

        Kunde k = getKundeById(kundeId);
        Exemplar ex = getExemplarById(exemplarId);

        newLeih.setKundeId(k);
        newLeih.setExemplarId(ex);

        Date ausleihDate = new Date();
        newLeih.setAusleihDatum(ausleihDate);

//        Calendar calendar = new GregorianCalendar();
//        calendar.setTime(ausleihDate);
//        calendar.add(Calendar.MONTH, 1);   //addiert einen Monat zum Leihdatum
//        newLeih.setLeihRueckgabedatum(calendar.getTime());
        try {
            em.persist(newLeih);
            return newLeih;
        } catch (Exception e) {
            return null;
        }
    }
}
