import java.io.File;
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
        byte[] b = new byte[4];
        b[0] = 'a';
        b[1] = 'b';
        b[3] = 'g';
        int i = 0;

        while (b[i] != 0){
            System.out.println((char) b[i]);
            i++;
        }
    }


}
