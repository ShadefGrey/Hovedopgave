import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.UUID;

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

        try {
            //TODO er muligvis ikke et problem for programmet der skal vise WARC filer at der kommer[DC_KEYWORD] men det er en ugyldig url så måske skal den kigges på
            String s1 = "http://ad.se.doubleclick.net/adi/idg.dk.cw/Samfund;sz=336x280;pos=10;tile=10;ord=9816706;kw=";
            String s2 = "[DC_KEYWORD]";
            System.out.println(s1+s2);
            System.out.println(s1+URLEncoder.encode(s2, "UTF-8"));
            System.out.println("http://www.a-paere.dk/mail_friend_form.asp?Redirect=%2FQuery%2FPaereWiz%2Fliste%2Easp%3F&Link=http%3A%2F%2Fwww%2Ea%2Dpaere%2Edk%2FQuery%2FPaereWiz%2Fliste%2Easp%3FPaereID%3D%26Info%3D%26Lampe%3D%26Search%3D%26PaereTypeID%3D%26Link%3D1%26SokkelTypeID%3D%26Effekt%3D%26Laengd%3D%26Diameter%3D%26todo%3D%26row%3D");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


}
