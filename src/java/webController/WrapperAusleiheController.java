package webController;

import data.Kunde;
import data.Ausleihe;
import data.WrapperAusleihe;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
@Named(value = "leihBean")
public class WrapperAusleiheController implements Serializable {
    @EJB
    private WrapperFacade ms;

    private static final long serialVersionUID = 1L;
    private final LinkedHashMap<String, Integer> alleKundenMap = new LinkedHashMap();
    private final LinkedHashMap<String, Integer> alleAusleihenMap = new LinkedHashMap();

    List<Object[]> anzeigeList = new ArrayList<>();
    private Integer kundeId;
    private Integer exemplarId;
    private Integer leihId;
    private Integer searchLeihId;
    private Integer searchExemplarId;
    private Integer searchKundeId;
    
    WrapperAusleihe leihTable = new WrapperAusleihe();

    /**
     * Standardkonstruktor
     */
    public WrapperAusleiheController() {
    }

    /**
     *
     * @return Kunden-Nr.
     */
    public Integer getKundeId() {
        return kundeId;
    }

    /**
     *
     * @param kundeId
     */
    public void setKundeId(Integer kundeId) {
        this.kundeId = kundeId;
    }

    /**
     *
     * @return Exemplar-ID
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
     * @return Liste für Anzeige
     */
    public List<Object[]> getAnzeigeList() {
        return anzeigeList;
    }

    /**
     *
     * @param anzeigeList
     */
    public void setAnzeigeList(List<Object[]> anzeigeList) {
        this.anzeigeList = anzeigeList;
    }

    /**
     *
     * @return Ausleih-Nr
     */
    public Integer getLeihId() {
        System.out.println("LeihId = " + leihId);
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
     * @return Ausleih-ID für den Ausleih-Datensatz, der in 
     * der Maske ausgewählt wurde
     */
    public Integer getSearchLeihId() {
        return searchLeihId;
    }

    /**
     *
     * @param searchLeihId
     */
    public void setSearchLeihId(Integer searchLeihId) {
        this.searchLeihId = searchLeihId;
    }

    /**
     *
     * @return Exemplar-ID für den Datensatz, der in der Maske ausgewählt wurde
     */
    public Integer getSearchExemplarId() {
        return searchExemplarId;
    }

    /**
     *
     * @param searchExemplarId
     */
    public void setSearchExemplarId(Integer searchExemplarId) {
        this.searchExemplarId = searchExemplarId;
    }

    /**
     *
     * @return Kunbden-Nr, die in der Maske ausgewählt wurde
     */
    public Integer getSearchKundeId() {
        return searchKundeId;
    }

    /**
     *
     * @param searchKundeId
     */
    public void setSearchKundeId(Integer searchKundeId) {
        this.searchKundeId = searchKundeId;
    }
 
    /**
     *
     * @return Liste aller Kunden für die Anzeige als Auswahl
     */
    public Map<String, Integer> getAlleKundenMap() {
        List<Kunde> tempList = ms.alleKunden();
        this.alleKundenMap.put(" --- ", null);
        for (Kunde kd : tempList) {
            this.alleKundenMap.put(kd.getKundeName() + ", " + kd.getKundeVorname() + ", " + kd.getKundeOrt() + ", " + kd.getKundePlz() + ", " + kd.getKundeStr(), kd.getKundeId());
        }
        return alleKundenMap;
    }

    /**
     *
     * @return Liste alle Ausleihdatensätze für die Anzeige als Auswahl
     */
    public Map<String, Integer> getAlleAusleihen() {
        List<Ausleihe> tempList = ms.getAlleAusleihen();
        this.alleAusleihenMap.put(" --- ", null);
        for (Ausleihe a : tempList) {
            this.alleAusleihenMap.put(a.getExemplarId().getExemplarNr() + ": " + a.getExemplarId().getVerlagbuchId().getBuchId().getBuchTitel(), a.getLeihId());
        }
        return alleAusleihenMap; 
    }

    /**
     *
     * @return Liste aller Ausleihdatensätze eines Exemplars für die Anzeige als Auswahl
     */
    public Map<String, Integer> getAusgeliehenByExemplarId() {
        List<Ausleihe> tempList = ms.getAusgeliehenByExemplarId(searchExemplarId);
        this.alleAusleihenMap.put(" --- ", null);
        for (Ausleihe a : tempList) {
            this.alleAusleihenMap.put(a.getExemplarId().getExemplarNr() + ": " + a.getExemplarId().getVerlagbuchId().getBuchId().getBuchTitel(), a.getLeihId());
        }
        return alleAusleihenMap;        
    }

    /**
     *
     * @return Liste aller Ausleihdatensätze eines Kunden für die Anzeige als Auswahl
     */
    public Map<String, Integer> getAusgeliehenByKundeId() {
        List<Ausleihe> tempList = ms.getAusleiheByKundeId(searchKundeId);
        this.alleAusleihenMap.put(" --- ", null);
        for (Ausleihe a : tempList) {
            this.alleAusleihenMap.put(a.getExemplarId().getExemplarNr() + ": " + a.getExemplarId().getVerlagbuchId().getBuchId().getBuchTitel(), a.getExemplarId().getExemplarId());
        }
        return alleAusleihenMap;
    }

    /**
     * confirmLeihe()
     * prüft, ob Kunde und Exemplar ausgewählt wurden und speichert die Daten für die Ausleihe
     * @return Zielmaske für die Anzeige (kehrt zu aufrufender Seite zurück)
     */
    public String confirmLeihe() {
        if ((searchExemplarId == null) || (searchKundeId == null) || (searchExemplarId == 0) || (searchKundeId == 0)) {
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("config/Bundle").getString("MissingData4Ausleihe"));
        } 
        else {
            try {
                ms.setAusleihe(searchKundeId, searchExemplarId);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("config/Bundle").getString("AusleiheCreated"));
                setExemplarId(0);
                return "";
            } catch (Exception e) {
                JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("config/Bundle").getString("PersistenceErrorOccured"));
                return null;
            }
        }
        return "";
    }

    /**
     * confirmRueckgabeByKunde()
     * prüft, ob Kunde und Exemplar ausgewählt wurden und markiert das Exemplar als "zurückgegeben" 
     * = setzt in der Ausleih-Tabelle das Rückgabedatum
     * @return Zielmaske für die Anzeige (kehrt zu aufrufender Seite zurück)
     */
    public String confirmRueckgabeByKunde() {
  
        if ((searchKundeId != 0 && searchKundeId != null) && (searchExemplarId != 0 && searchExemplarId != null))    
        {           
            try {
                System.out.println("ExemplarId: " + searchExemplarId);
                System.out.println("KundeId: " + searchKundeId);
                
                ms.setRueckgabe(searchExemplarId, searchKundeId);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("config/Bundle").getString("RueckgabeCreated"));
                setLeihId(0);
                return "";
            } catch (Exception e) {
                JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("config/Bundle").getString("PersistenceErrorOccured"));
                return null;
            }
        }
        else {
            return "";
        }
    }
    
    /**
     * confirmRueckgabe()
     * prüft, ob eine LeihID ausgewählt wurde und markiert diesen Datensatz als "zurückgegeben" 
     * = setzt in der Ausleih-Tabelle den Datensatz mit der passenden ID das Rückgabedatum
     * @return Zielmaske für die Anzeige (kehrt zu aufrufender Seite zurück)
     */
    public String confirmRueckgabe() {
        System.out.println("Test: in ConfirmRueckgabe angekommen");
        if ((searchLeihId == null) || (searchLeihId == 0) ) {
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("config/Bundle").getString("MissingData4Rueckgabe"));
            return "";
        } 
        else {
            try {
      //          ms.setRueckgabe(searchExemplarId, searchKundeId);
                ms.setRueckgabe(searchLeihId);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("config/Bundle").getString("RueckgabeCreated"));
                setLeihId(0);
                return "";
            } catch (Exception e) {
                JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("config/Bundle").getString("PersistenceErrorOccured"));
                return null;
            }
        }
    }

    /**
     * zurLeihscheinPage()
     * springt zur Anzeige der Leihscheine
     * @return Adresse der Maske "Leihschein"
     */
    public String zurLeihscheinPage() {
        return "./leihschein.xhtml";
    }

    /**
     * getLeihTable()
     * @return Liste aller Ausleihen
     */
    public List<WrapperAusleihe> getLeihTable() {
        return leihTable.createLeihtable(ms.getAlleAusleihen());
    }

    /**
     * getLeihTableByKunde()
     * @return Liste aller aktuellen Ausleihen eines Kunden
     */
    public List<WrapperAusleihe> getLeihTableByKunde() {
        return leihTable.createLeihtable(ms.getAusgeliehenByKundeId(searchKundeId));
    }

    /**
     * getLeihTableByExemplar()
     * @return Liste aller aktuellen Ausleihen für ein Exemplar
     */
    public List<WrapperAusleihe> getLeihTableByExemplar() {
        return leihTable.createLeihtable(ms.getAusgeliehenByExemplarId(exemplarId));
    }

    /**
     * getLeihschein()
     * @return Liste aller Ausleihen für einen Kunden
     */
    public List<WrapperAusleihe> getLeihschein() {

        List<Ausleihe> leih = new ArrayList();
        // Anweisung ist sinnlos, da KundenID übergeben wird und die NamedQuery stattdessen die LeihID erwartet
        leih.add(ms.getAusleiheById(kundeId));
        return leihTable.createLeihtable(leih);
    }

    /**
     * getAusleiheByKunde()
     * @return Liste aller Ausleihen für einen Kunden
     */
    public List<WrapperAusleihe> getAusleiheByKunde() {
        return leihTable.createLeihtable(ms.getAusleiheByKundeId(kundeId));
    }

}
