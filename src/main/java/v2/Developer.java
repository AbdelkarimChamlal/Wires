package v2;

import v2.helpers.Values;
import v2.services.CompatibilityDetector;

import java.io.IOException;

/**
 * This is the play ground while am developing this project
 */
public class Developer {

    public static void main(String[] args) {

        // let us develop some CompatibilityDetector

        try{
            // initialize that badBoy
            CompatibilityDetector compatibilityDetector = new CompatibilityDetector(Values.RESOURCES_FOLDER +"max.xlsx",Values.RESOURCES_FOLDER+"plausibilityList.xlsm",Values.RESOURCES_FOLDER+"crimping.xlsx");

            //start the comparing process
            compatibilityDetector.startComparing();

            //get list of invalid doubles

            //take actions

        }catch(IOException error_message){
            System.out.println("Failed to read files\n"+error_message);
        }

    }
}
