import org.jwat.tools.JWATTools;
import org.jwat.tools.tasks.test.TestFileResult;
import org.jwat.tools.tasks.test.TestTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Main {


    public static void main(String[] args) {

//        Service service = new Service(); //Converter en enkelt waf fil
//        File f = new File("Z:\\fra_Niels_Brugger_2009_06_15\\Leveret fra Niels Bruegger, 090611\\Arkiverede netsteder (primær opbevaring)\\Arkiveret\\Arkiveret 020215-020612\\Diverse\\Prissammenligninger\\0BKOIP~P.DK");
//        File writeToFile = new File("C:/Users/ckha/Desktop/WAFtoWarcTest/testDBackslashEncode.wubwub");
//        service.writeFile(writeToFile, f, true);


//        Service service = new Service();  //Converter et directory med waf filer
//        File f = new File("C:\\Users\\ckha\\Desktop\\En waf fil");
//        File writeToFile = new File("C:/Users/ckha/Desktop/WAFtoWarcTest/test7.warc");
//        service.writeFile(writeToFile, f);

//        Service service = new Service();  //Converter et directory med waf filer fra netarkiv-old
//        File f = new File("Y:\\fra_Niels_Brugger_2009_06_15\\Leveret fra Niels Bruegger, 090611\\Arkiverede netsteder (primær opbevaring)\\Arkiveret\\Arkiveret 020215-020612\\Diverse\\Prissammenligninger");
//        File writeToFile = new File("C:/Users/ckha/Desktop/WAFtoWarcTest/test9.warc");
//        service.writeFile(writeToFile, f);

        Service service = new Service();  //Converter et directory med waf filer fra netarkiv-old
        File f = new File("Z:\\\\fra_Niels_Brugger_2009_06_15\\\\Leveret fra Niels Bruegger, 090611\\\\Arkiverede netsteder (primær opbevaring)\\\\Arkiveret\\\\Arkiveret 020215-020612\\\\Diverse\\\\Prissammenligninger");
        File dirToMake = new File("C:/Users/ckha/Desktop/WAFtoWarcTest/testDir");
        String warFileName = "testNoEncode";
        service.writeFile(f, dirToMake, warFileName, false);


    }


}
