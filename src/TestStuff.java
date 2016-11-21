import org.jwat.tools.JWATTools;
import org.jwat.tools.core.CommandLine;
import org.jwat.tools.tasks.test.TestFileResult;
import org.jwat.tools.tasks.test.TestTask;
import org.jwat.warc.WarcRecord;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jwat.tools.tasks.test.TestFileResult.showDiagnosisList;

/**
 * Created by l_ckha on 02-11-2016.
 */
public class TestStuff {
//    public static UUID u = UUID.randomUUID();
//    public static DateFormat dateFormat;
//    public static void main(String[] args) {
//        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//
//        File f = new File("C:\\Users\\ckha\\Desktop\\0QO35I~9");
//        String a = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(
//                new Date(f.lastModified())
//        );
//        System.out.println(a);
//        try {
//            Date date = dateFormat.parse(a);
//            System.out.println(date);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }
//    String s = "HTTP/1.1 200 OK\n" +
//            "Content-Type: text/html" +
//            "\n" +
//            "\n";

    //    public static void main(String[] args) { //TODO check rekursiv (skal gøres public før tjek)
//        Service s = new Service();
//        File f = new File("C:\\Users\\ckha\\Desktop\\En waf fil");
//        s.recursiveConvert(f);
//    }


    public static void main(String[] args) {

//        try {
//            //TODO er muligvis ikke et problem for programmet der skal vise WARC filer at der kommer[DC_KEYWORD] men det er en ugyldig url så måske skal den kigges på
//            String s1 = "http://www.ask-alex.dk/read￥er/a\\a_print.html?ID=￥359&cat=B";
//            String s2 = s1.replace("￥", URLEncoder.encode("￥", "UTF-8"));
//            s2.replace('\\', '/');
//            System.out.println(s1);
//            System.out.println(s2);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        //"^([!#$&-;=?-\[\]_a-z~]|%[0-9a-fA-F]{2})+$"
        //"^([!#$&-;=?-[]_a-z~]|%[0-9a-fA-F]{2})+$"


        String fPath = "C:\\Users\\ckha\\Desktop\\testingstuffdir";
        String fPath2 = "C:\\Users\\ckha\\Desktop\\WAFtoWarcTest\\test2.warc";
        String[] paths = new String[2];
        paths[0] = fPath;
        paths[1] = fPath2;
        String[] argmnts = new String[3];
        argmnts[0] = "test";
        argmnts[1] = "-e";
        argmnts[2] = fPath;
        JWATTools.main(argmnts);

//        File f = new File(fPath);
//        f.mkdir();
//        Thread t1 = new Thread(() -> JWATTools.main(argmnts));
//        t1.start();

        System.out.println("WHATUP");

//        JWATTools.main(argmnts);


    }

}
