import org.jwat.warc.WarcRecord;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by l_ckha on 28-10-2016.
 */
public class WafToWarc {

    //TODO cate nul nul nul, er slutning, start på næste er lige efter
    private int contentLenght;
    private byte[] contentCutOff;
    private byte[] metaData = new byte[100];
    private int metaCounter = 0;
    private UUID responseId;

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

                    if (metaData.length <= metaCounter) {
                        metaData = Service.growByteArray(metaData);
                    }
                    metaData[metaCounter] = (byte) content;
                    metaCounter++;
                }

                if (start) {
                    if (inputBytes.length <= counter) {
                        inputBytes = Service.growByteArray(inputBytes);
                    }
                    inputBytes[counter] = (byte) content;
                    counter++;

                    if (counter >= 7 && inputBytes[counter - 1] == 0 && inputBytes[counter - 2] == 0 &&
                            inputBytes[counter - 3] == 0 && inputBytes[counter - 4] == 'e' && inputBytes[counter - 5] == 't' && inputBytes[counter - 6] == 'a' &&
                            inputBytes[counter - 7] == 'c') {
                        counter = counter - 6;
                        for (int i = counter; i >= 4 && !cutPost; i--) {
                            counter--;
                            if (inputBytes[i - 1] == 't' && inputBytes[i - 2] == 's' && inputBytes[i - 3] == 'o' && inputBytes[i - 4] == 'p') {
                                cutPost = true;
                                stop = true;
                                counter = counter - 4;
                            }
                        }

                        int i = counter;
                        while (inputBytes[i] != 0) {
                            if (metaData.length <= metaCounter) {
                                metaData = Service.growByteArray(metaData);
                            }
                            metaData[metaCounter] = inputBytes[i];
                            metaCounter++;
                            i++;
                        }

                    }
                }

                initialCounter++;

                inputStartList.add((byte) content);

                //Find the place to start //TODO behøves denne?
                if (!start && initialCounter > 9 && inputStartList.get(initialCounter - 1) == 0 && inputStartList.get(initialCounter - 2) == 0 &&
                        inputStartList.get(initialCounter - 3) == 0 && inputStartList.get(initialCounter - 4) == 0 && inputStartList.get(initialCounter - 5) == 'a' &&
                        inputStartList.get(initialCounter - 6) == 't' && inputStartList.get(initialCounter - 7) == 'a' && inputStartList.get(initialCounter - 8) == 'd') {
                    start = true;
                }

                if (stop) {
                    byte[] contentArray = new byte[counter];
                    System.out.println("input lenght: " + inputBytes.length);
                    System.out.println("array lenght: " + contentArray.length);

                    System.arraycopy(inputBytes, 0, contentArray, 0, counter);

//                    String s = "";
//                    for (int i = 0; i < contentArray.length; i++) {
//                        s += (char) contentArray[i];
//                    }
                    contentLenght = counter;
                    byte[] tmpWarcFile = warcRecord(infoId, date, contentArray);
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
                    //TODO tilføj metadata
                    if (metaCounter > 0) {
                        byte[] metaData = metaDataRecord();

                        for (int i = 0; i < metaData.length; i++) {
                            if (warcFile.length <= metaData.length + warcFilePointer) {
                                warcFile = Service.growByteArray(warcFile);
                            }
                            warcFile[warcFilePointer] = metaData[i];
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
                    }

                    cutPost = false;
                    start = false;
                    stop = false;
                    counter = 0;
                    initialCounter = 0;
                    inputBytes = new byte[1000];
                    contentCutOff = new byte[100];
                    contentLenght = 0;
                    metaCounter = 0;
                    metaData = new byte[100];
                    inputStartList.clear();

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] warcFileToReturn = new byte[warcFilePointer];
//        System.out.println(warcFile.length);
//        System.out.println(warcFilePointer);

        for (int i = 0; i < warcFilePointer; i++) {
            warcFileToReturn[i] = warcFile[i];
        }
        return warcFileToReturn;
    }

    //returns a byte[] consisting of a metadata WARC record with the non content data with WARC-Concurrent-To the relevant response WARC record
    public byte[] metaDataRecord() {

        byte[] metaHeader = ("WARC/1.0\r\n" +
                "WARC-Type: metadata\r\n" +
                "WARC-Concurrent-To: <urn:uuid:" + responseId + ">\r\n" +
                "WARC-IP-Address: 0.0.0.0\r\n" +
                "WARC-Record-ID: <urn:uuid:" + UUID.randomUUID() + ">\r\n" +
                "Content-Type: application/http;msgtype=response\r\n" +
                "Content-Length: " + metaCounter +
                "\r\n" +
                "\r\n").getBytes();

        byte[] bytesToReturn = new byte[metaCounter + metaHeader.length];
        for(int i = 0; i<metaHeader.length;i++){
            bytesToReturn[i] = metaHeader[i];
        }
        System.arraycopy(metaData, 0, bytesToReturn, metaHeader.length, metaCounter);

        return bytesToReturn;
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

            if (i >= 3 && !http && contentCutOff[i] == 'e' && contentCutOff[i - 1] == 'm' && contentCutOff[i - 2] == 'i' && contentCutOff[i - 3] == 'm') {
                i = i + 4;
                mime = true;
            }

        }
        return stringToReturn;
    }

    //returns a byte[] consisting of a response WARC record with header and content
    public byte[] warcRecord(UUID infoId, String date, byte[] content) {
        byte[] warcRecord;
        responseId = UUID.randomUUID();
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
                "WARC-Record-ID: <urn:uuid:" + responseId + ">\r\n" +
                "Content-Type: application/http;msgtype=response\r\n" +
                "WARC-Identified-Payload-Type: " + urlAndMime[1] + "\r\n" +
                "Content-Length: " + contentLenght +
                "\r\n" +
                "\r\n";

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
