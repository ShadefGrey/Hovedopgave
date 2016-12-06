import org.jwat.tools.JWATTools;
import org.jwat.tools.core.CommandLine;
import org.jwat.tools.tasks.test.TestFileResult;
import org.jwat.tools.tasks.test.TestTask;
import org.jwat.warc.WarcRecord;

import java.io.*;
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

    //    public static void main(String[] args) {
//        Service s = new Service();
//        File f = new File("C:\\Users\\ckha\\Desktop\\En waf fil");
//        s.recursiveConvert(f);
//    }


//    public void compareUrls(File warc, File waf) throws IOException {
//        String missingUrls = "";
//
//        FileInputStream fit = new FileInputStream(waf);
//        int content;
//        String wafUrls = "";
//
//        while ((content = fit.read()) != -1) {
//            wafUrls += (char) content;
//        }
//
//        fit = new FileInputStream(warc);
//        int content2;
//        String warcUrls = "";
//
//        while ((content2 = fit.read()) != -1) {
//            warcUrls += (char) content2;
//        }
//
//
//        String[] splitWarcUrls = warcUrls.split("\n");
//        String[] splitWafUrls = wafUrls.split("\n");
//
//        System.out.println("WarcUrls: " + splitWafUrls.length + ", WafUrls: " + splitWafUrls.length);
//
//        System.out.println("Comparing urls");
//
//        for (String wafUrl : splitWafUrls) {
//            boolean found = false;
//            int i = 0;
//
//            while (!found && i < splitWarcUrls.length) {
//                if (wafUrl.equals(splitWarcUrls[i])) {
//                    found = true;
//                }
//                i++;
//            }
//            if (!found) {
//                missingUrls += wafUrl + "\n";
//            }
//        }
//        System.out.println("Comparison complete");
//
//        FileOutputStream fo = new FileOutputStream(warc.getPath() + ".missingUrls.txt");
//        if (missingUrls.equals("")) {
//            missingUrls = "There were no missing urls";
//        }
//        fo.write(missingUrls.getBytes());
//        fo.close();
//    }


    public static void main(String[] args) {

//        try {
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


//        String fPath = "C:\\Users\\ckha\\Desktop\\WAFtoWarcTest\\test2.warc";
//        String[] argmnts = new String[3];
//        argmnts[0] = "test";
//        argmnts[1] = "-e";
//        argmnts[2] = fPath;


//        Pattern pattern = Pattern.compile("([!#$&%-;=@?_a-zA-Z~])");
//
//        Pattern mimePattern = Pattern.compile("([a-z])+/([a-z+-])+(\\d){0,2}");
//
//        Matcher matcher;
//        matcher = mimePattern.matcher("gfh/ert");
//        System.out.println(matcher.matches());

//       String s = "C:\\Users\\ckha\\Desktop\\WAFtoWarcTest\\testTheThing\\test";
//        try {
//            FileInputStream fi = new FileInputStream(new File(s));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

//        Service s = new Service();
//        File dir = new File("C:/Users/ckha/Desktop/WAFtoWarcTest/testTheOtherThing");
//        try {
//            FileOutputStream fo = new FileOutputStream(dir.getPath()+"/warcUrls");
//            fo.write(s.getWarcUrls(dir).getBytes());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


//        File warc = new File("C:\\Users\\ckha\\Desktop\\WAFtoWarcTest\\testTheOtherThing\\warc.txt");
//        File waf = new File("C:\\Users\\ckha\\Desktop\\WAFtoWarcTest\\testTheOtherThing\\waf.txt");
//        TestStuff t = new TestStuff();
//        try {
//            t.compareUrls(warc, waf);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }

}
