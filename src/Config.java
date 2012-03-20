/* 
 * Copyright (C) 2012 A. Sofyan Wahyudin
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
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

public class Config {

    public Config() { }
    
    public String[] getDictionaries(){
        
        try {
            String[] item = getKeys("config");
            
            for (int i=0; i<item.length; i++)
                if (item[i].indexOf("|") != -1)
                    item[i] = item[i].substring(0,item[i].indexOf("|"));
                    
            return item;
        } catch (IOException ex) {
            //ex.printStackTrace();
        }
        return null;
    }
    
    private String[] getKeys(String file) throws IOException{
        Vector v = new Vector(5, 2);
        StringBuffer read = new StringBuffer(13);

        int ch = 0;
        boolean b = true;
        
        InputStream is = this.getClass().getResourceAsStream("/" + file);
        InputStreamReader ir;
        
        try {
            ir = new InputStreamReader(is, "UTF8");
        } catch (UnsupportedEncodingException ex) {
            ir = new InputStreamReader(is, "UTF-8");
        }
        
        while ((ch = ir.read()) > -1){
            if (ch=='\n'){
                b = true;
                v.addElement(read.toString());
                read.setLength(0);
            }else{
                if (ch=='#') b = false;
                if (b == true) read.append((char)ch);
            }
        }
        
        if (read.length() != 0)
            v.addElement(read.toString());
        
        ir.close();
        
        String dicts[] = new String[v.size()];
        for(int i=0; i<v.size(); i++){
            dicts[i] = (String) v.elementAt(i);
        }
        
        return dicts;
    }
    
    public String[] getValuesOfKey(String key, String file) throws IOException{
        Vector v = new Vector (10, 3);
        StringBuffer read = new StringBuffer(13);
        StringBuffer readValue = new StringBuffer(5);
        int ch = 0;
        boolean b = true;
        
        InputStream is = this.getClass().getResourceAsStream("/" + file);
        InputStreamReader ir;
        
        try {
            ir = new InputStreamReader(is, "UTF8");
        } catch (UnsupportedEncodingException ex) {
            ir = new InputStreamReader(is, "UTF-8");
        }
        
        while ((ch = ir.read()) > -1){
            if (ch=='\n'){
                if (read.toString().compareTo(key)==0)
                    break;
                
                b = true;
                read.setLength(0);
            }else{
                if (ch=='#'){
                    b = false;
                }else{
                    if (b==false){ 
                        if (read.toString().compareTo(key)==0){

                            if (ch=='|'){
                                v.addElement(readValue.toString());
                                readValue.setLength(0);
                            }else
                                readValue.append((char) ch);
                            
                        }
                    }
                }
        
                if (b==true) read.append((char)ch);
            }
        }
        
        if (readValue.length() > 0) v.addElement(readValue.toString());
        
        ir.close();
        
        String ret[] = new String[v.size()];
        for(int i=0; i<v.size(); i++){
            ret[i] = (String) v.elementAt(i);
            
            if (ret[i].indexOf("|") != -1)
                ret[i] = ret[i].substring(0, ret[i].indexOf("|"));
        }
        
        return ret;
    
    }

    public String getMetaManifest(String key) throws IOException{
        StringBuffer read = new StringBuffer(13);
        StringBuffer readVlue = new StringBuffer(5);
        int ch = 0;
        boolean b = true;

        InputStream is = this.getClass().getResourceAsStream("/META-INF/MANIFEST.MF");
        InputStreamReader ir;

        try {
            ir = new InputStreamReader(is, "UTF8");
        } catch (UnsupportedEncodingException ex) {
            ir = new InputStreamReader(is, "UTF-8");
        }

        while ((ch = ir.read()) > -1){

            if (ch=='\n'){
                if (read.toString().compareTo(key)==0) break;

                b = true;

                read.setLength(0);
            }else{
                if (ch==':'){
                    b = false;
                }else{
                    if (b==false){
                        if (read.toString().compareTo(key)==0)
                            readVlue.append((char) ch);
                    }
                }

                if (b==true) read.append((char)ch);

            }
        }

        ir.close();
        
        return readVlue.toString().trim();
    }
    
}
