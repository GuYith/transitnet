//package whu.edu.cs.transitnet.utils;
//
//import whu.edu.cs.transitnet.Torch.base.model.ShapeData;
//import whu.edu.cs.transitnet.Torch.base.model.TrajEntry;
//
//import java.io.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class DataProcess {
//    private static String encoding = "utf-8";
//    public static final String filepath = "GTFSData\\gtfs-MTA-Brooklyn\\shapes.txt";
//    public static final String savepath = "ProcessedData\\gtfs-MTA-Brooklyn";
//    public static void GTFSShapes2Json(String path) {
//        try{
//            File file = new File(path);
//            if(file.isFile() && file.exists()) {
//                InputStreamReader reader = new InputStreamReader(new FileInputStream(file), encoding);
//                BufferedReader bufferedReader = new BufferedReader(reader);
//                String lineTxT = null;
//                String rawId = null;
//                List<TrajEntry> trajs = new ArrayList<TrajEntry>();
//                lineTxT = bufferedReader.readLine();
//                saveJsonData("[", savepath);
//                while((lineTxT = bufferedReader.readLine()) != null) {
//                    ShapeData sd = new ShapeData(lineTxT);
//                    if(rawId == null) {
//                        rawId = sd.getRawId();
//                        trajs.add(sd);
//                    } else if(rawId.equals(sd.getRawId()) != true) {
//                        String jsonData = Formater.toMapVJSON(trajs) + ",";
//                        saveJsonData(jsonData, savepath);
//                        rawId = sd.getRawId();
//                        trajs.clear();
//                        trajs.add(sd);
//                    } else trajs.add(sd);
//                }
//                if (trajs.size() > 0) {
//                    String jsonData = Formater.toMapVJSON(trajs) + "]";
//                    saveJsonData(jsonData, savepath);
//                }
//                bufferedReader.close();
//            } else {
//                System.out.println("The file is not exist");
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    public static void saveJsonData(String res, String path) {
//        File fileDirectory = new File(path);
//        if (!fileDirectory.exists()) {
//            System.out.println("fileDirectory not exits, create it ...");
//            try {
//                fileDirectory.mkdir();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        File file = new File(path + "\\geojson.txt");
//        try( BufferedWriter bw = new BufferedWriter(
//                new OutputStreamWriter(
//                        new FileOutputStream(file, true)))) {
//            bw.write(res);
//            bw.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void main(String[] args) {
//        GTFSShapes2Json(filepath);
//    }
//}
