package Main;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class TableLookup {

    private static ConcurrentHashMap<String, ConcurrentHashMap<String, String>> localStorage = new ConcurrentHashMap<String, ConcurrentHashMap<String, String>>(100);

    public TableLookup() {
        singleton = this;
    }

    protected static TableLookup singleton = new TableLookup();

    public static TableLookup getInstance() {
        return singleton;
    }
    
    public static String generatePhoneNumber(String valueAlias, String phoneNumber, String bookingId, String sm) {
        Long multipliedNum = 1L;
         String _sm = GetSourceMarket(sm);
         String obfuscatedNumber = "";
         multipliedNum = LoopThroughString(phoneNumber, valueAlias, multipliedNum);
         multipliedNum = LoopThroughString(bookingId, valueAlias, multipliedNum);

         switch (_sm) {
             case "UK":
                 obfuscatedNumber = "4444 " + (Long.toString(multipliedNum)).substring(1, 7);
                 break;
             case "DE":
                 obfuscatedNumber = "9999 " + (Long.toString(multipliedNum)).substring(1, 7);
                 break;
             case "NO":
                 obfuscatedNumber = "77777 " + (Long.toString(multipliedNum)).substring(1, 6);
                 break;
             case "BE":
                 obfuscatedNumber = "299" + (Long.toString(multipliedNum)).substring(1, 6);
                 break;
             case "NL":
                 obfuscatedNumber = "666 " + (Long.toString(multipliedNum)).substring(1, 7);
                 break;
         }

         return obfuscatedNumber;
     }

    public static String putValue(String alias, String key,String value)
       {
         
              String SeparationK_V = "^";
              String SeparationKV_KV = "~";
              
              if(null == localStorage)
             throw new RuntimeException("TRS001: localStorage is not initialized");

         ConcurrentHashMap<String,String> aliasList=localStorage.get(alias);
         if(null == aliasList)
         {
             aliasList=localStorage.put(alias, new ConcurrentHashMap<String,String>(50000));
             aliasList=localStorage.get(alias);
             aliasList.put(key,value); 
             return "OK";
         }
         
         Boolean keyExists = aliasList.containsKey(key);
         if(keyExists!= true)
         {
               aliasList.put(key,value);  
         }
         else{                      
                             
              String OldValue =  aliasList.get(key);
              String NewValue = OldValue+SeparationKV_KV+value;
              aliasList.put(key,NewValue); 
         }
        
         return "OK";
         
         
         
       } // end
    
    public static String getValueOrDefault(String alias, String key, String defaultValue) {
        String value = null;
        if (null == localStorage) {
            throw new RuntimeException("TRS001: localStorage is not initialized");
        }

        ConcurrentHashMap<String, String> aliasList = localStorage.get(alias);
        if (null == aliasList) {
            return defaultValue;
        };

        synchronized (aliasList) {
            value = aliasList.get(key);
        }
        if (null == value) {
            return defaultValue;
        }
        return value;
    }

    public static String resetAllValues() {
        if (null == localStorage) {
            throw new RuntimeException("TRS001: localStorage is not initialized");
        }
        localStorage.clear();
        return "OK";
    }

    public static String getObfuscatedId(String valueAlias, String piiAlias, String name, String bookingID, String sm) {

        Long multipliedNum = 1L;
        String _sm = GetSourceMarket(sm);

        if (piiAlias == "StreetNames") {
            List<String> splitAddress = Arrays.asList(name.split(" "));
            boolean containsHouseNumber = splitAddress.get(0).matches("^\\d+$");

            if (containsHouseNumber) {
                Integer.parseInt(splitAddress.get(0));

                multipliedNum = LoopThroughString(splitAddress.get(1), valueAlias, multipliedNum);
                multipliedNum = LoopThroughString(bookingID, valueAlias, multipliedNum);

                String subStr = _sm + (Long.toString(multipliedNum)).substring(1, 4);

                return splitAddress.get(0) + " " + TableLookup.getValueOrDefault(piiAlias, subStr,
                        "Default");
            } else {
                multipliedNum = LoopThroughString(splitAddress.get(0), valueAlias, multipliedNum);
                multipliedNum = LoopThroughString(bookingID, valueAlias, multipliedNum);

                String subStr = _sm + (Long.toString(multipliedNum)).substring(1, 4);

                return TableLookup.getValueOrDefault(piiAlias, subStr,
                        "Default");
            }

        } else {
            multipliedNum = LoopThroughString(name, valueAlias, multipliedNum);
            multipliedNum = LoopThroughString(bookingID, valueAlias, multipliedNum);

            String subStr = _sm + (Long.toString(multipliedNum)).substring(1, 4);

            return TableLookup.getValueOrDefault(piiAlias, subStr,
                    "Default");
        }
    }

    private static Long LoopThroughString(String stringToParse, String valueAlias, Long multipliedNum) throws NumberFormatException {
        String randomNum;

        for (int i = 0;
                i < stringToParse.length();
                i++) {

            randomNum = TableLookup.getValueOrDefault(valueAlias, (String.valueOf(stringToParse.charAt(i))).toUpperCase(), "9");
            multipliedNum *= Integer.parseInt(randomNum.trim());
        }
        return multipliedNum;
    }

    public static String GetSourceMarket(String sm) {
        if (sm.equals("NO") || sm.equals("SE") || sm.equals("FI") || sm.equals("DK")) {
            return "NO";
        } else if (sm.equals("DE") || sm.equals("CH") || sm.equals("PP") || sm.equals("AT")) {
            return "DE";
        } else {
            return sm;
        }
    }

    //    USED FOR TESTING
    public static void SetLocalStorage(ConcurrentHashMap<String, ConcurrentHashMap<String, String>> storage) {
        if (storage == null) {

        } else {
            localStorage = storage;
        }
    }
}
