package maker;

import maker.utils.Range;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Utils {

    public static float rand_range(float low,float high){
        return (float) (Math.random()*(high-low)+low);
    }

    public static float rand_range(Range range){
        return rand_range(range.low,range.high);
    }

    public static String readFile(String path){
        try {
            FileInputStream fin=new FileInputStream(path);
            byte[] bys=new byte[fin.available()];
            fin.read(bys);
            return new String(bys);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isnum(char ch){
        return (ch>='0' && ch<='9');
    }

    public static boolean isnum_or_alpha(char ch){
        return (ch>='a' && ch<='z') || (ch>='A' && ch<='Z') || (ch>='0' && ch<='9');
    }
}
