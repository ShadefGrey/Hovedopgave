import org.jwat.tools.JWATTools;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private int warcFileNumber = 1;
    private UUID warcInfoId;
    private String warcInfoDate;
    //    private ArrayList<String> allWafUrls = new ArrayList<>();
    private String allWafUrls = "";

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


    private byte[] getWarcUrls(File dirFile) throws IOException {
        //TODO could change it so it will skip looking for however long the content length is, since the next target url cant be in content.
        //TODO should check on date besides from just url to ensure that multiple of the same urls from different dates get checkd properly
        byte[] warcUrls = new byte[1000];
        int urlIndex = 0;
        int numberofurls = 0;
        if (dirFile.isDirectory()) {
            File[] files = dirFile.listFiles();

            for (File f : files) {
                if (f.getName().substring(f.getName().length() - 4).equals("warc")) {
                    System.out.println(f.getName());
                    boolean urlFound = false;
                    byte[] byteUrl = new byte[1000];
                    int byteIndex = 0;

                    int content;
                    FileInputStream fi = new FileInputStream(f);

                    while ((content = fi.read()) != -1) {
                        if (urlFound) {
                            if ((char) content != '\r') {
                                if (urlIndex >= warcUrls.length) {
                                    warcUrls = growByteArray(warcUrls);
                                }
                                warcUrls[urlIndex] = (byte) content;
                                urlIndex++;
                            } else {
                                urlFound = false;
                                if (urlIndex >= warcUrls.length) {
                                    warcUrls = growByteArray(warcUrls);
                                }
                                warcUrls[urlIndex] = '\n';
                                urlIndex++;
                                numberofurls++;
                                if (numberofurls % 100 == 0) {
                                    System.out.println("WARC urls found: " + numberofurls);
                                }
                            }
                        }
                        if (byteIndex >= byteUrl.length) {
                            byteUrl = growByteArray(byteUrl);
                        }
                        byteUrl[byteIndex] = (byte) content;
                        byteIndex++;

                        if (!urlFound && (char) content == ' ' && byteIndex >= 17 && (char) byteUrl[byteIndex - 2] == ':' && (char) byteUrl[byteIndex - 3] == 'I' && (char) byteUrl[byteIndex - 4] == 'R' &&
                                (char) byteUrl[byteIndex - 5] == 'U' && (char) byteUrl[byteIndex - 6] == '-' && (char) byteUrl[byteIndex - 7] == 't' && (char) byteUrl[byteIndex - 8] == 'e' &&
                                (char) byteUrl[byteIndex - 9] == 'g' && (char) byteUrl[byteIndex - 10] == 'r' && (char) byteUrl[byteIndex - 11] == 'a' && (char) byteUrl[byteIndex - 12] == 'T' &&
                                (char) byteUrl[byteIndex - 13] == '-' && (char) byteUrl[byteIndex - 14] == 'C' && (char) byteUrl[byteIndex - 15] == 'R' && (char) byteUrl[byteIndex - 16] == 'A' && (char) byteUrl[byteIndex - 17] == 'W') {
                            urlFound = true;
                            byteUrl = new byte[1000];
                            byteIndex = 0;
                        }
                    }

                }
            }
        }

        byte[] urlsToReturn = new byte[urlIndex];
        for (int i = 0; i < urlIndex; i++) {
            urlsToReturn[i] = warcUrls[i];
        }
        return urlsToReturn;
    }

    private void compareUrls(File dirfile) throws IOException {
        String missingUrls = "";
        byte[] warcUrlBytes = getWarcUrls(dirfile);
        String allWarcUrls = "";

        for (byte b : warcUrlBytes) {
            allWarcUrls += (char) b;
        }

        String[] splitWarcUrls = allWarcUrls.split("\n");
        String[] splitWafUrls = allWafUrls.split("\n");

        System.out.println("WarcUrls: " + splitWafUrls.length + ", WafUrls: " + splitWafUrls.length);

        System.out.println("Comparing urls");

        for (String wafUrl : splitWafUrls) {
            boolean found = false;
            int i = 0;

            while (!found && i < splitWarcUrls.length) {
                if (wafUrl.equals(splitWarcUrls[i])) {
                    found = true;
                }
                i++;
            }
            if (!found) {
                missingUrls += wafUrl + "\n";
            }
        }
        System.out.println("Comparison complete");

        FileOutputStream fo = new FileOutputStream(dirfile.getPath() + "/missingUrls.txt");
        if (missingUrls.equals("")) {
            missingUrls = "There were no missing urls";
        }
        fo.write(missingUrls.getBytes());
        fo.close();
    }

    public void compareUrls(File dirfile, File wafUrlFile) throws IOException {
        String missingUrls = "";
        byte[] warcUrlBytes = getWarcUrls(dirfile);
        String allWarcUrls = "";

        for (byte b : warcUrlBytes) {
            allWarcUrls += (char) b;
        }

        FileInputStream fInSt = new FileInputStream(wafUrlFile);
        int content;
        String wafUrls = "";
        while ((content = fInSt.read()) != -1) {
            wafUrls += (char) content;
        }



        String[] splitWarcUrls = allWarcUrls.split("\n");
        String[] splitWafUrls = wafUrls.split("\n");

        System.out.println("WarcUrls: " + splitWafUrls.length + ", WafUrls: " + splitWafUrls.length);

        System.out.println("Comparing urls");

        for (String wafUrl : splitWafUrls) {
            boolean found = false;
            int i = 0;

            while (!found && i < splitWarcUrls.length) {
                if (wafUrl.equals(splitWarcUrls[i])) {
                    found = true;
                }
                i++;
            }
            if (!found) {
                missingUrls += wafUrl + "\n";
            }
        }
        System.out.println("Comparison complete");

        FileOutputStream fo = new FileOutputStream(dirfile.getPath() + "/missingUrls.txt");
        if (missingUrls.equals("")) {
            missingUrls = "There were no missing urls";
        }
        fo.write(missingUrls.getBytes());
        fo.close();
    }


    //Only for waf files
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
                allWafUrls += wafToWarc.getWafUrls();
                outputStream.write(b1);
            }

            jwattWarcTest(dirToMake, wfn);

            //Skriver alle de fundne WAF URL'er ud i en fil så hvis den ikke når færdig med at finde alle WARC URL'er skal man ikke starte forfra
            outputStream = new FileOutputStream(dirToMake.getPath() + "\\wafUrls.txt");
            outputStream.write(allWafUrls.getBytes());

            compareUrls(dirToMake);

            outputStream.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //Runs the JWAT tools warc test on the directory with the newly created WARC files
    private void jwattWarcTest(File dirToMake, String fileName) {
        try {
            //The arguments needed for JWAT test, that normally comes from the shell command
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

            //TODO url output
//            File urlfile = new File(dirToMake.toString() + "\\urls.txt");
//            FileOutputStream urlout = new FileOutputStream(urlfile);
//            urlout.write(allWafUrls.getBytes());


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

                                String warcFileName = fToMake.toString();

                                System.out.println(file.getName());
                                String date = dateFormat.format(new Date(file.lastModified()));

                                wafToWarc = new WafToWarc();
                                byte[] b1 = wafToWarc.readWaf(file, warcInfoId, date, encodeUrl);
                                allWafUrls += wafToWarc.getWafUrls();

                                warcFileSize = warcFileSize + b1.length;

                                if (warcFileSize > 1073741824) { //1073741824 should be 1 GB
                                    warcFileName = fToMake.toString().substring(0, fToMake.toString().length() - 5) + "(" + warcFileNumber + ").warc";

                                    outputStream = new FileOutputStream(warcFileName);
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
