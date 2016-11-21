import org.jwat.tools.JWATTools;

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

    private boolean encodeUrl = false;

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
    public void writeFile(File srcFile, File dirToMake, String warcFileName, boolean toEncode) {
        try {
            String wfn = warcFileName;
            File dir = dirToMake;
            dir.mkdir();

            if (!wfn.substring(dirToMake.getName().lastIndexOf(".") + 1).equals("warc")) {
                wfn += ".warc";
            }

            encodeUrl = toEncode;

            String date = dateFormat.format(new Date(srcFile.lastModified()));

            if (!dir.exists()) {
                dir.createNewFile();
            }

            outputStream = new FileOutputStream(dir.toString() + "\\" + wfn);
            outputStream.write(warcInfo());

            if (srcFile.isDirectory()) {
                recursiveConvert(srcFile, new File(dir.toString() + "\\" + wfn));
            } else {
                byte[] b1 = wafToWarc.readWaf(srcFile, warcInfoId, date, encodeUrl);
                outputStream.write(b1);
            }

            jwattWarcTest(dirToMake, wfn);

            outputStream.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Runs the JWAT tools warc test on the directory with the newly created WARC files
    private void jwattWarcTest(File dirToMake, String fileName) {
        try {
            //The arguments needed, that normally comes from the shell command
            String[] argmnts = new String[3];
            argmnts[0] = "test";
            argmnts[1] = "-e";
            argmnts[2] = dirToMake.toString();
            JWATTools.main(argmnts);

            //Takes the error messages from the file created by JWAT tools and write it to a file we create
            FileInputStream ifile = new FileInputStream("i.out");
            FileOutputStream outputfile = new FileOutputStream(dirToMake + "\\" + fileName + ".i.out");
            int content;
            while ((content = ifile.read()) != -1) {
                outputfile.write((byte) content);
            }

            outputfile.close();
            ifile.close();

            //deletes the files that jwatt tools automatically creates. Instead a file with the error messages can be found in the directory of the newly created WARC files
            if (new File("e.out").exists()) {
                new File("e.out").delete();
            }
            if (new File("i.out").exists()) {
                new File("i.out").delete();
            }
            if (new File("v.out").exists()) {
                new File("v.out").delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //A recursive function that checks every file and directory inside it and uses wafToWarc on any waf file found.
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
                                byte[] b1 = wafToWarc.readWaf(file, warcInfoId, date, encodeUrl);

                                warcFileSize = warcFileSize + b1.length;

                                if (warcFileSize > 107374182) { //1073741824 should be 1 GB
                                    //TODO might need better file names
                                    outputStream = new FileOutputStream(fToMake.toString().substring(0, fToMake.toString().length() - 5) + warcFileNumber + ".warc");
                                    warcFileNumber++;
                                    warcFileSize = b1.length;
                                    outputStream.write(warcInfo());
                                }

                                outputStream.write(b1);

                            }
                        }
                        fInput.close();
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
