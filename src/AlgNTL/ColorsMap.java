package AlgNTL;

import java.awt.*;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Jakub on 08.05.2017.
 */
public class ColorsMap extends HashMap<Integer,String> {
    private static  final Random rand = new Random();

    public ColorsMap(){
        this.put (1, "0xFFB300");
        this.put (2, "0x803E75");
        this.put (3, "0xFF6800");
        this.put (4, "0xA6BDD7");
        this.put (5, "0xC10020");
        this.put (6, "0xCEA262");
        this.put (7, "0x817066");
        this.put (8, "0x007D34");
        this.put (9, "0xF6768E");
        this.put (10, "0x00538A");
        this.put (11, "0xFF7A5C");
        this.put (12, "0x53377A");
        this.put (13, "0xFF8E00");
        this.put (14, "0xB32851");
        this.put (15, "0xF4C800");
        this.put (16, "0x7F180D");
        this.put (17, "0x93AA00");
        this.put (18, "0x593315");
        this.put (19, "0xF13A13");
        this.put (20, "0x232C16");
    }

    @Override
    public String get(Object key){
        Integer index = (Integer)key;
        if(this.containsKey(key)){
            return super.get(key);
        }
        else{
            String hex = String.format("#%02x%02x%02x", rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
            this.put(index, hex);
            return  hex;
        }
    }
}
