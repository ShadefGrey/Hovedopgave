import java.io.*;
import java.util.*;

/**
 * Created by l_ckha on 28-10-2016.
 */
public class WafToWarc {

    //TODO cate nul nul nul, er slutning, start på næste er lige efter
    private int contentLenght;
    private byte[] metaData = new byte[100];
    private int metaCounter = 0;
    private UUID responseId;
    byte[] warcFile = new byte[2000];
    int warcFilePointer = 0;

    public byte[] readWaf(File srcFile, UUID infoId, String date) {

        byte[] inputBytes = new byte[1000];

        try (FileInputStream fInputStream = new FileInputStream(srcFile)) {

            //counts up for every byte in the file you read
            int whenDoesItStop = 0;

            //total lenght of the file
            int fileLenght = (int) srcFile.length() - 1;
            boolean cutPost = false;
            boolean stop = false;
            boolean start = false;

            int content;
            //while loop that goes through the waf file and create warc records from the data
            while ((content = fInputStream.read()) != -1) {


                if (!start) {
                    if (metaData.length <= metaCounter) {
                        metaData = Service.growByteArray(metaData);
                    }
                    metaData[metaCounter] = (byte) content;
                    metaCounter++;
                }

                //this method runs on the last byte of the file
                if (fileLenght == whenDoesItStop) {
                    System.out.println("ITS STOPPING");
                    byte[] lastMeta = metaDataRecord(date);
                    addToWarcFile(lastMeta);
                }
                whenDoesItStop++;

                //starts when the inputstream is at the content of what will be a WARC record.
                //Adds the content to an array
                if (start) {
                    if (inputBytes.length <= contentLenght) {
                        inputBytes = Service.growByteArray(inputBytes);
                    }
                    inputBytes[contentLenght] = (byte) content;
                    contentLenght++;
                    int metaEndToAdd = 0;

                    if (contentLenght >= 7 && inputBytes[contentLenght - 1] == 0 && inputBytes[contentLenght - 2] == 0 &&
                            inputBytes[contentLenght - 3] == 0 && inputBytes[contentLenght - 4] == 'e' && inputBytes[contentLenght - 5] == 't' && inputBytes[contentLenght - 6] == 'a' &&
                            inputBytes[contentLenght - 7] == 'c') {
                        contentLenght = contentLenght - 6;
                        metaEndToAdd = metaEndToAdd + 6;
                        for (int i = contentLenght; i >= 4 && !cutPost; i--) {
                            contentLenght--;
                            metaEndToAdd++;
                            if (inputBytes[i - 1] == 't' && inputBytes[i - 2] == 's' && inputBytes[i - 3] == 'o' && inputBytes[i - 4] == 'p') {
                                cutPost = true;
                                stop = true;
                                contentLenght = contentLenght - 4;
                                metaEndToAdd = metaEndToAdd + 4;
                            }
                        }

                        int i = contentLenght;

                        //adds the data after content, but before the next record, to metadata
                        while (metaEndToAdd > 0) {
                            if (metaData.length <= metaCounter) {
                                metaData = Service.growByteArray(metaData);
                            }
                            metaData[metaCounter] = inputBytes[i];
                            metaCounter++;
                            i++;
                            metaEndToAdd--;
                        }

                    }
                }


                //adds the data before content to metadata
                if (!start && metaCounter > 9 && metaData[metaCounter - 1] == 0 && metaCounter > 9 && metaData[metaCounter - 2] == 0 &&
                        metaData[metaCounter - 3] == 0 && metaData[metaCounter - 4] == 0 && metaData[metaCounter - 5] == 'a' &&
                        metaData[metaCounter - 6] == 't' && metaData[metaCounter - 7] == 'a' && metaData[metaCounter - 8] == 'd') {
                    start = true;
                }

                //this runs when all the content of a single record has been found.
                //it adds response and metadata Warc record to the WARC file
                if (stop) {
                    byte[] contentArray = new byte[contentLenght];

                    //copies the content from the response WARC record array into a new array that does not have the extra NULs
                    System.arraycopy(inputBytes, 0, contentArray, 0, contentLenght);

                    byte[] tmpWarcFile = warcRecord(infoId, date, contentArray);

                    //runs a method that adds the tmpWarcFile to the WARC file that will be returned.
                    addToWarcFile(tmpWarcFile);

                    //if there is any metadata add it to the WARC file, concurrent to the above response record
                    if (metaCounter > 0) {
                        byte[] metaDataContent = metaDataRecord(date);
                        addToWarcFile(metaDataContent);

                    }

                    //resets, what needs to be resat before finding the data for the next WARC record in the waf file.
                    cutPost = false;
                    start = false;
                    stop = false;
                    contentLenght = 0;
                    inputBytes = new byte[1000];
                    contentLenght = 0;
                    metaCounter = 0;
                    metaData = new byte[100];
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] warcFileToReturn = new byte[warcFilePointer];

        for (int i = 0; i < warcFilePointer; i++) {
            warcFileToReturn[i] = warcFile[i];
        }
        return warcFileToReturn;
    }

    private void addToWarcFile(byte[] bytesToAdd) {
        for (int i = 0; i < bytesToAdd.length; i++) {
            if (warcFile.length <= bytesToAdd.length + warcFilePointer) {
                warcFile = Service.growByteArray(warcFile);
            }
            warcFile[warcFilePointer] = bytesToAdd[i];
            warcFilePointer++;
        }

        //\r\n\r\n added to the WARC file så the next record starts at the right place
        warcFile[warcFilePointer] = '\r';
        warcFilePointer++;
        warcFile[warcFilePointer] = '\n';
        warcFilePointer++;
        warcFile[warcFilePointer] = '\r';
        warcFilePointer++;
        warcFile[warcFilePointer] = '\n';
        warcFilePointer++;
    }

    //returns a byte[] consisting of a metadata WARC record with the non content data with WARC-Concurrent-To the relevant response WARC record
    public byte[] metaDataRecord(String date) {

        byte[] metaHeader = ("WARC/1.0\r\n" +
                "WARC-Type: metadata\r\n" +
                "WARC-Concurrent-To: <urn:uuid:" + responseId + ">\r\n" +
                "WARC-Date: " + date + "\r\n" +
                "WARC-IP-Address: 0.0.0.0\r\n" +
                "WARC-Record-ID: <urn:uuid:" + UUID.randomUUID() + ">\r\n" +
                "Content-Type: application/warc-fields\r\n" +
                "Content-Length: " + metaCounter +
                "\r\n" +
                "\r\n").getBytes();

        byte[] bytesToReturn = new byte[metaCounter + metaHeader.length];
        for (int i = 0; i < metaHeader.length; i++) {
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
        for (i = 0; i < metaData.length; i++) {
            if (http) {
                if (metaData[i] != 0) {
                    if (stringToReturn[0] == null) { //fills the first space in the string with the relevant char instead of a null
                        stringToReturn[0] = (char) metaData[i] + "";
                    } else {
                        stringToReturn[0] += (char) metaData[i];
                    }
                } else {
                    http = false;
                }

            }

            if (i > 2 && metaData[i] == 'l' && metaData[i - 1] == 'r' && metaData[i - 2] == 'u') {
                i = i + 5;
                http = true;
            }

            if (mime) {
                if (metaData[i] != 0) {
                    if (stringToReturn[1] == null) {
                        stringToReturn[1] = (char) metaData[i] + "";
                    } else {
                        stringToReturn[1] += (char) metaData[i];
                    }
                } else {
                    mime = false;
                }
            }

            if (i >= 3 && !http && metaData[i] == 'e' && metaData[i - 1] == 'm' && metaData[i - 2] == 'i' && metaData[i - 3] == 'm') {
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
