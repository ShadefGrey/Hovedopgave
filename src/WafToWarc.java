import org.jwat.warc.WarcRecord;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by l_ckha on 28-10-2016.
 */
public class WafToWarc {

    private int contentLenght;
    private byte[] contentCutOff;

    public byte[] readWaf(File srcFile, UUID infoId, String date) {
        byte[] warcFile = new byte[2000];
        int warcFilePointer = 0;
        int counter = 0;
        ArrayList<Byte> inputStartList = new ArrayList<>();
        byte[] inputBytes = new byte[1000];
        contentCutOff = new byte[100];

        try (FileInputStream fInputStream = new FileInputStream(srcFile)) {

            int content;
            int initialCounter = 0;
            boolean cutPost = false;
            boolean stop = false;
            boolean start = false;
            while ((content = fInputStream.read()) != -1 && counter < 100000) {
                if (!start) {
                    if (contentCutOff.length <= initialCounter) {
                        contentCutOff = Service.growByteArray(contentCutOff);
                    }
                    contentCutOff[initialCounter] = (byte) content;
                }

                if (start) {
                    if (inputBytes.length <= counter) {
                        inputBytes = Service.growByteArray(inputBytes);
                    }
                    inputBytes[counter] = (byte) content;
                    counter++;

                    if (counter > 7 && inputBytes[counter - 1] == 'X' && inputBytes[counter - 2] == 'X' && inputBytes[counter - 3] == 'X' &&
                            inputBytes[counter - 4] == 'X' && inputBytes[counter - 5] == 'X' && inputBytes[counter - 6] == 'X') {
                        counter = counter - 6;
                        for (int i = counter; i >= 0 && !cutPost; i--) {
                            counter--;
                            if (inputBytes[i - 1] == 't' && inputBytes[i - 2] == 's' && inputBytes[i - 3] == 'o' && inputBytes[i - 4] == 'p') {
                                cutPost = true;
                                stop = true;
                                counter = counter - 4;
                            }
                        }
                    }
                }

                initialCounter++;

                inputStartList.add((byte) content);

                //Find the place to start
                if (!start && initialCounter > 9 && inputStartList.get(initialCounter - 1) == 0 && inputStartList.get(initialCounter - 2) == 0 &&
                        inputStartList.get(initialCounter - 3) == 0 && inputStartList.get(initialCounter - 4) == 0 && inputStartList.get(initialCounter - 5) == 'a' &&
                        inputStartList.get(initialCounter - 6) == 't' && inputStartList.get(initialCounter - 7) == 'a' && inputStartList.get(initialCounter - 8) == 'd') {
                    start = true;
                }

                if (stop) {
                    byte[] conentArray = new byte[counter];
                    System.out.println("input lenght: " + inputBytes.length);
                    System.out.println("array lenght: " + conentArray.length);

                    System.arraycopy(inputBytes, 0, conentArray, 0, counter);

//                    String s = "";
//                    for (int i = 0; i < conentArray.length; i++) {
//                        s += (char) conentArray[i];
//                    }
                    contentLenght = counter;
                    byte[] tmpWarcFile = warcRecord(infoId, date, conentArray);
                    for (int i = 0; i < tmpWarcFile.length; i++) {
                        if (warcFile.length <= tmpWarcFile.length + warcFilePointer) {
                            warcFile = Service.growByteArray(warcFile);
                        }
                        warcFile[warcFilePointer] = tmpWarcFile[i];
                        warcFilePointer++;
                    }
                    warcFile[warcFilePointer] = '\r';
                    warcFilePointer++;
                    warcFile[warcFilePointer] = '\n';
                    warcFilePointer++;
                    warcFile[warcFilePointer] = '\r';
                    warcFilePointer++;
                    warcFile[warcFilePointer] = '\n';
                    warcFilePointer++;
                    cutPost = false;
                    start = false;
                    stop = false;
                    counter = 0;
                    initialCounter = 0;
                    inputBytes = new byte[1000];
                    contentCutOff = new byte[100];
                    contentLenght = 0;
                    inputStartList.clear();

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] warcFileToReturn = new byte[warcFilePointer];
        System.out.println(warcFile.length);
        System.out.println(warcFilePointer);

        for (int i = 0; i < warcFilePointer; i++) {
            warcFileToReturn[i] = warcFile[i];
        }
        return warcFileToReturn;
    }


    //Finds the url and mime type of the record from the cut off portion of the data that is not part of the content itself
    public String[] getUrlAndMime() {
        String[] stringToReturn = new String[2];
        boolean http = false;
        boolean mime = false;
        int i = 0;
        for (i = 0; i < contentCutOff.length; i++) {
            if (http) {
                if (contentCutOff[i] != 0) {
                    if (stringToReturn[0] == null) { //fills the first space in the string with the relevant char instead of a null
                        stringToReturn[0] = (char) contentCutOff[i] + "";
                    } else {
                        stringToReturn[0] += (char) contentCutOff[i];
                    }
                } else {
                    http = false;
                }

            }

            if (i > 2 && contentCutOff[i] == 'l' && contentCutOff[i - 1] == 'r' && contentCutOff[i - 2] == 'u') {
                i = i + 5;
                http = true;
            }

            if (mime) {
                if (contentCutOff[i] != 0) {
                    if (stringToReturn[1] == null) {
                        stringToReturn[1] = (char) contentCutOff[i] + "";
                    } else {
                        stringToReturn[1] += (char) contentCutOff[i];
                    }
                } else {
                    mime = false;
                }
            }

            if (!http && contentCutOff[i] == 'e' && contentCutOff[i - 1] == 'm' && contentCutOff[i - 2] == 'i' && contentCutOff[i - 3] == 'm') {
                i = i + 4;
                mime = true;
            }

        }
        return stringToReturn;
    }

    //returns a byte[] consisting of a WARC record with header and content
    public byte[] warcRecord(UUID infoId, String date, byte[] content) {
        byte[] warcRecord;

        String[] urlAndMime = getUrlAndMime();
        String http;

        if (contentLenght > 0) {
            http = "HTTP/1.1 200 OK\r\n" + "Content-Type: " +
                    urlAndMime[1] +
                    "\r\n" +
                    "\r\n";
        } else {
            http = "HTTP/1.1 404 Not found\r\n" + "Content-Type: " +
                    urlAndMime[1] +
                    "\r\n" +
                    "\r\n";
        }
        contentLenght = contentLenght + http.getBytes().length;

        String warcHeader = "WARC/1.0\r\n" +
                "WARC-Type: response\r\n" +
                "WARC-Target-URI: " + urlAndMime[0] + "\r\n" +
                "WARC-Warcinfo-ID: <urn:uuid:" + infoId + ">\r\n" +
                "WARC-Date: " + date + "\r\n" +
                "WARC-IP-Address: 0.0.0.0\r\n" +
                "WARC-Record-ID: <urn:uuid:" + UUID.randomUUID() + ">\r\n" +
                "Content-Type: application/http;msgtype=response\r\n" +
                "WARC-Identified-Payload-Type: " + urlAndMime[1] + "\r\n" +
                "Content-Length: " + contentLenght +
                "\r\n" +
                "\r\n";

        //TODO den viser ikke automatisk link i webarchiveplayer???
        warcHeader += http;

        warcRecord = new byte[content.length + warcHeader.getBytes().length];
        int i;

        for (i = 0; i < warcHeader.getBytes().length; i++) {
            warcRecord[i] = warcHeader.getBytes()[i];
        }

        for (int j = 0; j < content.length; j++) {
            warcRecord[i] = content[j];
            i++;
        }

        return warcRecord;
    }

}
