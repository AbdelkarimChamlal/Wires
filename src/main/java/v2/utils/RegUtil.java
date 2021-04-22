package v2.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class responsible for checking if two strings have the same pattern
 */
public class RegUtil {
    public static boolean sameModelCode(String original,String modified){

        // remove the M litter from the modified String
        String modifiedWithoutM = modified.replace("M","").toUpperCase();
        String originalModified = original.replace("HAB","").toUpperCase();

        StringBuilder modifiedLettersPart = new StringBuilder();
        StringBuilder modifiedNumberPart = new StringBuilder();
        int lettersStopAt = 0;
        for(int i = 0; i<modifiedWithoutM.length() ; i++){
            Character character = modifiedWithoutM.charAt(i);
            if(!(character<65 || character>90)){
                modifiedLettersPart.append(character);
            }else{
                lettersStopAt=i;
                break;
            }
        }

        for(int i = lettersStopAt; i<modifiedWithoutM.length() ; i++){
            Character character = modifiedWithoutM.charAt(i);
            if(!(character<48 || character>57)){
                modifiedNumberPart.append(character);
            }
        }

        String pattern = modifiedLettersPart.toString() + "0*"+modifiedNumberPart.toString();
        Pattern patternCompiled = Pattern.compile(pattern,Pattern.CASE_INSENSITIVE);
        Matcher matcherOriginal = patternCompiled.matcher(originalModified);
        Matcher matcherModified = patternCompiled.matcher(modifiedWithoutM);


        return matcherOriginal.find() && matcherModified.find();
    }

    public static String extractRevision(String PM){
        Pattern p = Pattern.compile("([^_]*)_([A-Za-z0-9]*)");
        Matcher m = p.matcher(PM);

        if(m.find())return m.group(2);
        return "";
    }

    public static String extractCustomerPart(String PM){
        Pattern p = Pattern.compile("([^_]*)_([A-Za-z0-9]*)");
        Matcher m = p.matcher(PM);

        if(m.find())return m.group(1);
        return "";
    }
}
