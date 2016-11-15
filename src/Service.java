import java.io.*;
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
    private FileOutputStream outputStream;
    private long warcFileSize = 0;

    private UUID warcInfoId;
    private String warcInfoDate;

    public Service() {
        wafToWarc = new WafToWarc();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        warcInfoDate = dateFormat.format(new Date());
    }


    //Takes an array and returns a new array twice the size, with the same content
    public static byte[] growByteArray(byte[] bArray) {
        byte[] arrayToReturn = new byte[bArray.length * 2];
        for (int i = 0; i < bArray.length; i++) {
            arrayToReturn[i] = bArray[i];
        }
        return arrayToReturn;
    }

    //The warcinfo, to be added at the start of every WARC file
    private byte[] warcInfo() {

        warcInfoId = UUID.randomUUID();
        String s = "WARC/1.0\r\n" +
                "WARC-Type: warcinfo\r\n" +
                "WARC-Record-ID: <urn:uuid:" + warcInfoId + ">\r\n" +
                "WARC-Date: " + warcInfoDate + "\r\n" +
                "Content-Length: 0\r\n" +
                "Content-Type: application/warc-fields\r\n" +
                "\r\n" +
                "\r\n" +
                "\r\n";

        return s.getBytes();
    }


    //TODO only for waf files
    public void writeFile(File fToMake, File srcFile) {
        try {
            String date = dateFormat.format(new Date(srcFile.lastModified()));

            if (!fToMake.exists()) {
                fToMake.createNewFile();
            }
            outputStream = new FileOutputStream(fToMake);
            outputStream.write(warcInfo());

            if (srcFile.isDirectory()) {
                recursiveConvert(srcFile, fToMake);
            } else {
                byte[] b1 = wafToWarc.readWaf(srcFile, warcInfoId, date);
                outputStream.write(b1);
            }
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void recursiveConvert(File dirPath, File fToMake) {
        try {
            File f = dirPath;
            File[] files = f.listFiles();
            int warcFileNumber = 1;

            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    if (file.isFile()) {
                        FileInputStream fInput = new FileInputStream(file);
                        int content;
                        int counter = 0;
                        byte[] findAffix = new byte[4];
                        while ((content = fInput.read()) != -1 && counter < 4) {
                            findAffix[counter] = (byte) content;

                            counter++;

                            if (counter == 4 && (char) findAffix[counter - 1] == 'F' && (char) findAffix[counter - 2] == 'A' &&
                                    (char) findAffix[counter - 3] == 'W' && (char) findAffix[counter - 4] == '.') {

                                System.out.println(file.getName());
                                String date = dateFormat.format(new Date(file.lastModified()));

                                wafToWarc = new WafToWarc();
                                byte[] b1 = wafToWarc.readWaf(file, warcInfoId, date);

                                warcFileSize = warcFileSize + b1.length;

                                if (warcFileSize > 107374182) { //1073741824 should be 1 GB
                                    //TODO might need better file names
                                    outputStream = new FileOutputStream(fToMake.toString().substring(0, (int) fToMake.length() - 5) + warcFileNumber + ".warc");
                                    warcFileNumber++;
                                    warcFileSize = b1.length;
                                    outputStream.write(warcInfo());
                                }

                                outputStream.write(b1);

                            }
                        }
                    }

                    if (file.isDirectory()) {
                        recursiveConvert(file, fToMake);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
