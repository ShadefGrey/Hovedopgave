import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public String readWaf(File srcFile) {
        String s = "";
//        int postIndex = -1; //needed if we work with array instead of arraylist
        ArrayList<Byte> inputByList = new ArrayList<>();
        ArrayList<Byte> inputStartList = new ArrayList<>();
        byte[] inputBytes;
        try (FileInputStream fInputStream = new FileInputStream(srcFile)) {

            //add the content from the inputstream to the outputbytes arraylist
            int content;
            int counter = 0;
            int initialCounter = 0;
            boolean cutPost = false;
            boolean stop = false;
            boolean start = false;
            while ((content = fInputStream.read()) != -1 && !stop) {
                if (start) {
                    inputByList.add((byte) content);
//                    System.out.print("" + (char) inputByList.get(counter));
//                    if (counter > 6) {  //Får den til at stoppe ved </html>
//                        if (inputBytes[counter] == '>' && inputBytes[counter - 1] == 'l' && inputBytes[counter - 2] == 'm' && inputBytes[counter - 3] == 't' && inputBytes[counter - 4] == 'h' && inputBytes[counter - 5] == '/' && inputBytes[counter - 6] == '<') {
//                            stop = true;
//                        }
//                    }

                    if (counter > 5 && inputByList.get(counter - 1) == 'X' && inputByList.get(counter - 2) == 'X' && inputByList.get(counter - 3) == 'X' && inputByList.get(counter - 4) == 'X') {
                        for (int i = counter; i >= 0 && !cutPost; i--) {
                            if (inputByList.get(i - 1) == 't' && inputByList.get(i - 2) == 's' && inputByList.get(i - 3) == 'o' && inputByList.get(i - 4) == 'p') {
                                cutPost = true;
//                                postIndex = i - 3;
                                stop = true;
                            }
                            inputByList.remove(i);
                            if (cutPost) {
                                inputByList.remove(inputByList.size() - 1);
                                inputByList.remove(inputByList.size() - 1);
                                inputByList.remove(inputByList.size() - 1);
                                inputByList.remove(inputByList.size() - 1);
                            }
                        }
                    }
                    counter++;
                }
                initialCounter++;

                inputStartList.add((byte) content);
//                if(initialCounter < 190){
//                    System.out.println(inputStartList.get(initialCounter-1));
//                }

                //Find the place to start
                if (!start && initialCounter > 9 && inputStartList.get(initialCounter - 1).intValue() == 0 && inputStartList.get(initialCounter - 2).intValue() == 0 && inputStartList.get(initialCounter - 3).intValue() == 0 && inputStartList.get(initialCounter - 4).intValue() == 0 && inputStartList.get(initialCounter - 5) == 'a' && inputStartList.get(initialCounter - 6) == 't' && inputStartList.get(initialCounter - 7) == 'a' && inputStartList.get(initialCounter - 8) == 'd') {
                    start = true;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println("THIS! " + (char) inputBytes[1]);
//        for (int i = 0; i<inputBytes.length && inputBytes[i] != '0'; i++) {
//            s += (char) inputBytes[i] +"";
//        }
//        System.out.println(inputBytes[10000]);
//        System.out.println(s.length());
//        System.out.println(s.charAt(1));
        inputBytes = new byte[inputByList.size()];
        for (int i = 0; i < inputByList.size(); i++) {
            inputBytes[i] = inputByList.get(i);
            s += (char) (byte) inputByList.get(i);
        }
        return s;
    }

    public static void main(String[] args) {
//        Main m = new Main();
//        File f = new File("C:\\Users\\ckha\\Desktop\\0QO35I~9");
//        System.out.println(m.readWaf(f));

        Service service = new Service(); //Converter en enkelt waf fil
        File f = new File("C:\\Users\\ckha\\Desktop\\En waf fil\\Enmandsavisen\\06RPOI~A");
        File writeToFile = new File("C:/Users/ckha/Desktop/WAFtoWarcTest/test5.warc");
        service.writeFile(writeToFile, f);


//        Service service = new Service();  //Converter et directory med waf filer
//        File f = new File("C:\\Users\\ckha\\Desktop\\En waf fil");
//        File writeToFile = new File("C:/Users/ckha/Desktop/WAFtoWarcTest/test4.warc");
//        service.writeFile(writeToFile, f);
    }


}
