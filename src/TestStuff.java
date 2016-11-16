import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        byte[] line = "http://radio-tv.sparel.dk/search/searchadvanced.asp?ProduktGruppeID=2".getBytes();
        Pattern pattern = Pattern.compile("([!#$&%-;=?_a-zA-Z~])");
        Matcher matcher;
        try {
            for (int i = 0; i < line.length; i++) {
                matcher = pattern.matcher((char) line[i] + "");
                if (matcher.find()) {
                    System.out.print((char) line[i]);
                } else {
                    System.out.print(URLEncoder.encode((char) line[i] + "", "UTF-8"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}