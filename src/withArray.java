//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//
///**
// * Created by l_ckha on 27-10-2016.
// */
//public class withArray {
//
//
//    public static byte[] readWaf(File srcFile) {
//        String s = "";
//        int counter = 0;
////        int postIndex = -1; //needed if we work with array instead of arraylist
//        ArrayList<Byte> inputStartList = new ArrayList<>();
//        byte[] inputBytes = new byte[1000];
//
//        try (FileInputStream fInputStream = new FileInputStream(srcFile)) {
//
//            //add the content from the inputstream to the outputbytes arraylist
//            int content;
//            int initialCounter = 0;
//            boolean cutPost = false;
//            boolean stop = false;
//            boolean start = false;
//            while ((content = fInputStream.read()) != -1 && counter < 10000 && !stop) {
//                if (start) {
//                    if (inputBytes.length <= counter) {
//                        inputBytes = Service.growByteArray(inputBytes);
//                    }
//                    inputBytes[counter] = (byte) content;
//                    counter++;
//
//                    if (counter > 7 && inputBytes[counter - 1] == 'X' && inputBytes[counter - 2] == 'X' && inputBytes[counter - 3] == 'X' &&
//                            inputBytes[counter - 4] == 'X' && inputBytes[counter - 5] == 'X' && inputBytes[counter - 6] == 'X') {
//                        counter = counter - 6;
//                        for (int i = counter; i >= 0 && !cutPost; i--) {
//                            counter--;
//                            if (inputBytes[i - 1] == 't' && inputBytes[i - 2] == 's' && inputBytes[i - 3] == 'o' && inputBytes[i - 4] == 'p') {
//                                cutPost = true;
//                                stop = true;
//                                counter = counter - 4;
////                                if (counter > 78 && inputBytes[i - 5] == '\n' && inputBytes[i - 6] == '>' && inputBytes[i - 7] == '-' && inputBytes[i - 8] == '-' && inputBytes[i - 9] == ' ') {
////                                    counter = counter - 77;
////                                }
//                            }
//                        }
//                    }
//                }
//
//                initialCounter++;
//
//                inputStartList.add((byte) content);
//
//                //Find the place to start
//                if (!start && initialCounter > 9 && inputStartList.get(initialCounter - 1).intValue() == 0 && inputStartList.get(initialCounter - 2).intValue() == 0 &&
//                        inputStartList.get(initialCounter - 3).intValue() == 0 && inputStartList.get(initialCounter - 4).intValue() == 0 && inputStartList.get(initialCounter - 5) == 'a' &&
//                        inputStartList.get(initialCounter - 6) == 't' && inputStartList.get(initialCounter - 7) == 'a' && inputStartList.get(initialCounter - 8) == 'd') {
//                    start = true;
//                }
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
////        System.out.println("THIS! " + (char) inputBytes[1]);
////        for (int i = 0; i<inputBytes.length && inputBytes[i] != '0'; i++) {
////            s += (char) inputBytes[i] +"";
////        }
////        System.out.println(inputBytes[10000]);
////        System.out.println(s.length());
////        System.out.println(s.charAt(1));
//        byte[] arrayToReturn = new byte[counter];
//        System.out.println("input lenght: " + inputBytes.length);
//        System.out.println("array lenght: " + arrayToReturn.length);
//
////        for (int i = 0; i < arrayToReturn.length; i++) {
////            arrayToReturn[i] = inputBytes[i];
////        }
//
//        System.arraycopy(inputBytes, 0, arrayToReturn, 0, counter);
//
//        for (int i = 0; i < arrayToReturn.length; i++) {
//            s += (char) arrayToReturn[i];
//        }
//        return arrayToReturn;
//    }
//
//    public static void main(String[] args) {
//        withArray w = new withArray();
//        File f = new File("C:\\Users\\ckha\\Desktop\\0QO35I~9");
//       w.readWaf(f);
//
//    }
//}
//
