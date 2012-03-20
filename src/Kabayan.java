/* SimpleDict
 * Copyright (C) 2011 A. Sofyan Wahyudin
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

import java.util.Vector;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class Kabayan extends MIDlet implements CommandListener {
    private Display display;
    
    private Command cmdExit        = new Command("Exit", Command.EXIT, 2); 
    private Command cmdSearch      = new Command("Search", Command.OK, 1);
    private Command cmdToMenu      = new Command("Back", Command.BACK, 2);
    private Command cmdToEntryWord = new Command("Back", Command.BACK, 2); 
    private Command cmdToListWord  = new Command("Back", Command.BACK, 2); 
    
    private String[] dict, menu, ret, hasil;
    
    private TextBox tbCari = new TextBox("Search Word", "", 25, 0);
    private List lsUtama, lsKata;
    
    private Paluruh paluruh = new Paluruh(); 
    private Config cfg = new Config();

    private String NAME, VERSION, DESCRIP;
    
    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
    }

    protected void pauseApp() {
    }

    protected void startApp() throws MIDletStateChangeException {
        initKabayan();
    }
    
    public void commandAction(Command c, Displayable d) {
        
        if (c.getCommandType() == Command.SCREEN){
            String title = display.getCurrent().getTitle();
            
            if (title.equals(lsUtama.getTitle())) 
                mainMenuSelect(lsUtama.getSelectedIndex());
            else if (title.equals(lsKata.getTitle())) 
                showArti(hasil[lsKata.getSelectedIndex()]);
        }
        
        
        if (c == cmdSearch) showKata(tbCari.getString());
        else if (c == cmdToMenu) display.setCurrent(lsUtama);
        else if (c == cmdToListWord) display.setCurrent(lsKata);
        else if (c == cmdToEntryWord) display.setCurrent(tbCari);
    }
    
    private void initKabayan(){
        
        // get manifest
        try {
            NAME    = cfg.getMetaManifest("MIDlet-Name");
            VERSION = cfg.getMetaManifest("MIDlet-Version");
            DESCRIP = cfg.getMetaManifest("MIDlet-Description");
        } catch (Exception e) {
        }
        
        
        // load dictionaries
        dict = cfg.getDictionaries();
        
        menu = new String[dict.length+3];
        
        for (int i=0; i<dict.length; i++)
            menu[i] = dict[i];
        
        menu[dict.length]   = "Help";
        menu[dict.length+1] = "About";
        menu[dict.length+2] = "Exit";
        
        lsUtama   = new List(NAME, List.IMPLICIT, menu, null);
        
        //
        display = Display.getDisplay(this);
        
        lsUtama.addCommand(cmdExit);
        lsUtama.setCommandListener(this);
        
        tbCari.addCommand(cmdSearch);
        tbCari.addCommand(cmdToMenu);
        tbCari.setCommandListener(this);
        
        display.setCurrent(lsUtama);
        
    }
    
    private void mainMenuSelect(int i){
        if (i<dict.length)
        
            try {
                entryKata(cfg.getValuesOfKey(dict[i], "config")[0]);
            } catch(Exception ex) {}
        
        else if (i==dict.length)
        
            showForm("Help", "Tulis teks lalu tekan tombol Cari, maka hasil pencarian akan tampil\n\nUntuk beberapa jenis HP gunakan OK/Call untuk memilih list",
                     cmdToMenu, null);
        
        else if (i==dict.length+1)
        
            showForm("About Kabayan", "Light Edition v" + VERSION+
                     "\n© 2012 Sofyan\n\nUrl:\nhttp://code.google.com/p/kabayan" +
                     "\n\n" + DESCRIP, 
                     cmdToMenu, null);
        
        else if (i==dict.length+2)
            notifyDestroyed();
            
    }
    
    private void showForm(String j, String s, Command c1, Command c2){
        Form f = new Form(j);
        
        f.append(new StringItem("", s));
        f.addCommand(c1);
        if (c2!=null) f.addCommand(c2);
        f.setCommandListener(this);
        
        display.setCurrent(f);
    }
    
    private void entryKata(String idxfile){
        try{
            paluruh.setIndexFile(idxfile);
        } catch (Exception ex) {ex.printStackTrace();};
        
        display.setCurrent(tbCari);
    }
    
    private void showKata(String s){
        
        if (s.indexOf(" ")!=-1) s=splitString(s, " ")[0];
        
        hasil = paluruh.startSearch(s.toLowerCase());
        
        if (hasil==null || hasil.length<1) {
            while (s.length()>1){
              
                s = s.substring(0, s.length()-1);
                hasil = paluruh.startSearch(s.toLowerCase());
              
                if (hasil!=null && hasil.length>0) break;
            }
        }
        
        if (hasil!=null && hasil.length>0) {
        
            ret = new String[hasil.length];
            
            for (int i=0; i<ret.length; i++)
                ret[i] = realKata(hasil[i]);
            
            lsKata = new List("Result", List.IMPLICIT, ret, null);

            lsKata.addCommand(cmdToEntryWord);
            lsKata.setCommandListener(this);

            display.setCurrent(lsKata);
        } else {
            Alert alResult = new Alert("Result", "Tidak ada!", null, AlertType.CONFIRMATION);
            
            alResult.addCommand(cmdToEntryWord);
            alResult.setTimeout(5000);
            
            display.setCurrent(alResult);
        }
    }
    
    private static String realKata(String s){
        if (s.indexOf("|") != -1) 
            return s.substring(s.indexOf("|")+1);
        else
            return s;
    }
    
    //private void tampilkanArti(String s){
    private void showArti(String word){
        try{
            showForm(realKata(word), paluruh.searchExactWord(word), cmdToListWord, null);
        } catch (Exception ex) {ex.printStackTrace();}
        
    }
    
    private static String[] splitString(String s, String pemisah) {
        Vector nodes = new Vector();
        
        // Parse node ke vektor
        int idx = s.indexOf(pemisah);
        int l = pemisah.length();
        
        while(idx>=0)
        {
            nodes.addElement(s.substring(0, idx));
            s = s.substring(idx+l);
            idx = s.indexOf(pemisah);
        }
        
        // sesa node terakhir
        nodes.addElement(s);

        // Buat split string array
        int size = nodes.size();
        String[] ret = new String[size];
        if (size>0) {
            for(int i=0; i<size; i++)
                ret[i] = (String)nodes.elementAt(i);
        }
        
        nodes = null;
        
        return ret;
    }
    
    
    
}
