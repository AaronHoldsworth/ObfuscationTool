/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tests;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.lang.reflect.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Aaron
 */
public class Test_TableLookup {

    static Main.TableLookup _tableLookup;

    private static ConcurrentHashMap<String, ConcurrentHashMap<String, String>> createHashMap() {

        ConcurrentHashMap<String, ConcurrentHashMap<String, String>> parentHashMap = new ConcurrentHashMap<String, ConcurrentHashMap<String, String>>();
        ConcurrentHashMap<String, String> valueHashMap = new ConcurrentHashMap<String, String>();
        ConcurrentHashMap<String, String> firstNamesHashMap = new ConcurrentHashMap<String, String>();
        ConcurrentHashMap<String, String> surnamesHashMap = new ConcurrentHashMap<String, String>();
        ConcurrentHashMap<String, String> streetsHashMap = new ConcurrentHashMap<String, String>();
        ConcurrentHashMap<String, String> postcodesHashMap = new ConcurrentHashMap<String, String>();

        PopulateValues(valueHashMap, "values.txt");
        PopulateValues(firstNamesHashMap, "firstNames.txt");
        PopulateValues(surnamesHashMap, "surnames.txt");
        PopulateValues(streetsHashMap, "Streetnames.txt");
        PopulateValues(postcodesHashMap, "postcodes.txt");

        parentHashMap.put("Values", valueHashMap);
        parentHashMap.put("FirstNames", firstNamesHashMap);
        parentHashMap.put("Surnames", surnamesHashMap);
        parentHashMap.put("Street", streetsHashMap);
        parentHashMap.put("Postcodes", postcodesHashMap);

        return parentHashMap;
    }

    private static void PopulateValues(ConcurrentHashMap<String, String> childHashMap, String fileName) {

        try {
            String thisLine = null;
            String fp = new File("").getAbsolutePath();
            fp = fp + "\\Test\\tests\\" + fileName;
            FileReader fr = new FileReader(fp);

            BufferedReader br = new BufferedReader(fr);
            while ((thisLine = br.readLine()) != null) {
                List<String> splitter = Arrays.asList(thisLine.split(","));
                childHashMap.put(splitter.get(0), splitter.get(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Test_TableLookup() {
    }

    @BeforeClass
    public static void setUpClass() {
        _tableLookup = new Main.TableLookup();
        ConcurrentHashMap<String, ConcurrentHashMap<String, String>> localHash = createHashMap();
        _tableLookup.SetLocalStorage(localHash);
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void TestTableLookup() {
        String SM = "UK";
        String test = _tableLookup.getValueOrDefault("FirstNames", SM + "100", "Venkat");

        assertEquals(test, "Andrew");
    }

    @Test
    public void TestGetObfuscatedID_FirstNames() {
        String testString = "Aaron";
        String bookingID = "20294488";
        String sm = "UK";
        String obfuscatedID = _tableLookup.getObfuscatedId("Values", "FirstNames", testString, bookingID, sm);

        assertTrue(!"Default".equals(obfuscatedID));
        assertTrue(!obfuscatedID.equals(testString));
    }

    @Test
    public void TestGetObfuscationID_Surnames() {
        String testString = "Holdsworth";
        String bookingID = "20294488";
        String sm = "UK";
        String obfuscatedID = _tableLookup.getObfuscatedId("Values", "Surnames", testString, bookingID, sm);

        assertTrue(!"Default".equals(obfuscatedID));
        assertTrue(!obfuscatedID.equals(testString));
    }
    
    @Test
    public void TestGetObfuscationID_StreetNames() {
        String testString = "Test Street";
        String bookingID = "20294488";
        String sm = "UK";
        String obfuscatedID = _tableLookup.getObfuscatedId("Values", "StreetNames", testString, bookingID, sm);

        assertTrue(!"Default".equals(obfuscatedID));
        assertTrue(!obfuscatedID.equals(testString));
    }
    
    @Test
    public void TestGetObfuscationID_Postcodes() {
        String testString = "LU1 1UL";
        String bookingID = "20294488";
        String sm = "UK";
        String obfuscatedID = _tableLookup.getObfuscatedId("Values", "Postcodes", testString, bookingID, sm);

        assertTrue(!"Default".equals(obfuscatedID));
        assertTrue(!obfuscatedID.equals(testString));
    }
    
    @Test
    public void TestSourceMarketChanger()
    {
        assertTrue(_tableLookup.GetSourceMarket("PP") == "DE");
        assertTrue(_tableLookup.GetSourceMarket("DK") == "NO");
        assertTrue(_tableLookup.GetSourceMarket("UK") == "UK");
        assertTrue(_tableLookup.GetSourceMarket("NL") == "NL");
        assertTrue(_tableLookup.GetSourceMarket("BE") == "BE");
    }
    
    @Test
    public void TestStreetWithHouseNumber()
    {
        String testString = "12 Eddington";
        String sm = "UK";
        String bookingID = "16100004";
        
        String obfuscatedID = _tableLookup.getObfuscatedId("Values", "Street", testString, bookingID, sm);
        
        assertTrue(!"Default".equals(obfuscatedID));
        assertTrue(obfuscatedID.substring(0,2).equals(testString.substring(0,2)));
        assertTrue(!obfuscatedID.equals(testString));
    }
   
    @Test
    public void TestPhoneNumbers()
    {
        String testString = "5728927532";
        String sm = "UK";
        String bookingID = "16100004";
        
        String obfuscatedID = _tableLookup.generatePhoneNumber("Values", testString, bookingID, sm);
        
        assertTrue(!obfuscatedID.equals(testString));
    }
    
    //@Test
    public void MassTestNames() {
        String thisLine = null;
        try {

            String fp = new File("").getAbsolutePath();
            fp = fp + "\\Test\\tests\\TestNames.txt";
            String wfp = new File("").getAbsolutePath();
            wfp = wfp + "\\Test\\tests\\results.csv";
            FileReader fr = new FileReader(fp);
            FileWriter fw = new FileWriter(wfp);
            BufferedWriter bw = new BufferedWriter(fw);
            BufferedReader br = new BufferedReader(fr);
            while ((thisLine = br.readLine()) != null) {
                String obfuscatedID = _tableLookup.getObfuscatedId("Values", "FirstNames", thisLine, "UK", "123456");

                bw.write(thisLine + "," + obfuscatedID);
                bw.newLine();
            }
            bw.flush();
        } catch (Exception e) {

            e.printStackTrace();
            fail();
        }
    }
}
