package com.notbestlord.core.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import imgui.ImColor;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.io.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

public class Utils {

    public static FloatBuffer storeDataInFloatBuffer(float[] data){
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    public static IntBuffer storeDataInIntBuffer(int[] data){
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        buffer.put(data).flip();
        return buffer;
    }


    public static String loadResource(String fileName) throws Exception{
        String result;
        try(InputStream in = Utils.class.getResourceAsStream(fileName);
            Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name())){
            result = scanner.useDelimiter("\\A").next();
        }
        return result;
    }

    public static List<String> readAllLines(String fileName){
        List<String> list = new ArrayList<>();
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((Class.forName(Utils.class.getName()).getResourceAsStream(fileName))))){
            String line;
            while((line = bufferedReader.readLine()) != null){
                list.add(line);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static float difference(float a, float b){
        float Aa = Math.abs(a);
        float Ab = Math.abs(b);
        return Math.abs(Aa - Ab);
    }

    public static String UpperCaseStart(String in){
        in = in.replace("_"," ");
        String out = "";
        for(String s : in.split(" ")){
            out += s.toUpperCase().charAt(0) + s.toLowerCase().substring(1, s.length()) + " ";
        }
        out = out.substring(0, out.length()-1);
        return out;
    }

    public static boolean isInRange(float num, float min, float max){
        return num >= min && num <= max;
    }

    public static int getSign(float num){
        return num == 0 ? 1 : (int) (num / Math.abs(num));
    }

    public static int getImColor(Color color){
        return ImColor.intToColor(color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha());
    }

    public static String getPercent(float current, float max){
        if(((current / max) * 100) % 1 == 0){
            return "" + ((int)((current / max) * 100));
        }
        return "" + ((current / max) * 100);
    }

    public static String getPercent(float current, float max, String format){
        if(((current / max) * 100) % 1 == 0){
            return "" + ((int)((current / max) * 100));
        }
        return String.format(format, ((current / max) * 100));
    }

    public static Map<Integer, Float> skillExpRequirements;

    public static void addFilesFromDir(List<String> lst, File dir) {
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) {
                addFilesFromDir(lst, file);
            } else {
                lst.add(file.getPath());
            }
        }
    }

    public static void addFilesFromDir(List<String> lst, String exceptions, File dir) {
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) {
                addFilesFromDir(lst,exceptions, file);
            } else if (except(file.getPath(), exceptions)){
                lst.add(file.getPath());
            }
        }
    }

    private static boolean except(String filePath, String exceptions){
        String[] excepts = exceptions.split(",");
        for(String ex : excepts){
            if (filePath.contains(ex)) {
                return false;
            }
        }
        return true;
    }

}
