import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by l_ckha on 27-10-2016
 */
public class Service {

    private DateFormat dateFormat;
    private WafToWarc wafToWarc;
//    public static File srcFile;
//    public static File fileToMake;

    private UUID warcInfoId = UUID.randomUUID();
    private String warcInfoDate;

    public Service() {
        wafToWarc = new WafToWarc();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        warcInfoDate = dateFormat.format(new Date());
    }


    public static byte[] growByteArray(byte[] bArray) {
        byte[] arrayToReturn = new byte[bArray.length * 2];
        for (int i = 0; i < bArray.length; i++) {
            arrayToReturn[i] = bArray[i];
        }
        return arrayToReturn;
    }

    public byte[] warcInfo() {

        String s = "WARC/1.0\r\n" +
                "WARC-Type: warcinfo\r\n" +
                "WARC-Record-ID: <urn:uuid:" + warcInfoId + ">\r\n" +
                "WARC-Date: " + warcInfoDate +"\r\n" +
                "Content-Length: 0\r\n" +
                "Content-Type: application/warc-fields\r\n" +
                "\r\n" +
                "\r\n" +
                "\r\n";

        return s.getBytes();
    }

    public void writeWarc(File srcFile){

    }

    //TODO only for waf files
    public void writeFile(File fToMake, File srcFile) {
        try {
            String date = dateFormat.format(
                    new Date(srcFile.lastModified())
            );

            byte[] b1 = wafToWarc.readWaf(srcFile, warcInfoId, date);
            if (!fToMake.exists()) {
                fToMake.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(fToMake);
            outputStream.write(warcInfo());
//            outputStream.write(wafToWarc.warcRecord(warcInfoId, date, srcFile));
            outputStream.write(b1);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
