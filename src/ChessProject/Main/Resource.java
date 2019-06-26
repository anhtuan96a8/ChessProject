package ChessProject.Main;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class Resource {
    protected static ResourceBundle resources;
    static{
        try{
            resources = ResourceBundle.getBundle("ChessProject.ChessProjectProperties",Locale.getDefault());
        }catch(Exception e){
            System.out.println("Mychessmate properties not found");
            javax.swing.JOptionPane.showMessageDialog(null,
                    "MyChessmate properties not found",
                    "Error",javax.swing.JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    public String getResourceString(String key){
        String str;
        try{
            str = resources.getString(key);
        }catch(Exception e){
            str = null;
        }
        return str;
    }
    public URL getResource(String key){
        String name = getResourceString(key);
        
        if(name != null){
            URL url = this.getClass().getResource(name);
            return url;
        }
        return null;
    }
}
