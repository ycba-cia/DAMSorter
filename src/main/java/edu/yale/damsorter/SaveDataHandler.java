/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.yale.damsorter;

import com.artesia.security.SecuritySession;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.log4j.Logger;

/**
 *
 * @author acdr4
 */
public class SaveDataHandler {

    static Logger log = Logger.getLogger(SaveDataHandler.class.getName());
    
    //stores json data received from client side post as string
    private String json_data;

    public SaveDataHandler() {
        json_data = null;
    }

    /**
     * @param json the json data to set
     */
    public void setjson_data(String json) {
        this.json_data = json;
    }

    /**
     * @return the json_data
     */
    public String getjson_data() {
        return json_data;
    }

    public void saveJson() throws org.json.JSONException {

        // get DAM login credentials from ConfigParser
        String[] credentials = ConfigParser.getConfig();
        String userid = credentials[0];
        String password = credentials[1];
        
        
        //create JSON object from json string received from client side
        JSONObject json = new JSONObject(json_data);
        //System.out.println(json.toString());
        //String primaryIdx = json.getString("primaryIdx");
        JSONArray jsonArr = json.getJSONArray("recordsArr");

        // create a log in session of DAM
        //System.out.println("Loggin in DAM...");
        log.info("Loggin in DAM...");
        SecuritySession session = SessionHandler.login(userid, password);

        //System.out.println("Saving to DAM...");
        log.info("Saving to DAM...");
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject record = jsonArr.getJSONObject(i);
            //make a SaveParams object for compactness of data to write to DAM
            SaveParams saveObj = new SaveParams();
            saveObj.setAssetId(record.getString("id"));
            saveObj.setRank(record.getString("rank"));
            saveObj.setKeywords(record.getString("keywords"));
            saveObj.setDescCaption(record.getString("descCaption"));
            saveObj.setIsPrimary(record.getString("primary"));

            //saveObj.setCdsLevel(record.getString("cdsLevel"));
            //if (i == Integer.parseInt(primaryIdx)) {
            //    saveObj.setIsPrimary("Y");
            //} else {
            //    saveObj.setIsPrimary("N");
            //}
            //invoke the class that writes data to DAM
            DAMWrite dw = new DAMWrite();
            dw.writeData(session, saveObj);
        }
        //System.out.println("Saving data completed!");
        log.info("Saving data completed!");
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        //System.out.println("Time to save data = " + duration + "ms");
        log.info("Time to save data = " + duration + "ms");

        /*System.out.print("Primary index at " + primaryIdx);
         System.out.print(".. rank = ");
         for (int i = 0; i < jsonArr.length(); i++) {
         JSONObject record = jsonArr.getJSONObject(i);
         System.out.print(record.getString("rank") + ", ");
         }
         System.out.print(".. cds level = ");
         for (int i = 0; i < jsonArr.length(); i++) {
         JSONObject record = jsonArr.getJSONObject(i);
         System.out.print(record.getString("cdsLevel") + ", ");
         }
         System.out.println();
         */

        // logout from DAM before returning
        SessionHandler.logout(session);
        //System.out.println("Logged out of DAM");
        log.info("Logged out of DAM");
    }
}
