package ru.bellintegrator;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class Constants {
    public static int PORT = 4646;
    public static int MAX_CNT = 8;
    public static int MAX_SL_CNT = 3;

    public static String IMG_PATH = "src"+ File.separator+"main"+ File.separator+"resources"+ File.separator+"public"+ File.separator+"dashboard"+ File.separator+"img";
    public static String ZIP_PATH ="src"+ File.separator+"main"+ File.separator+"resources"+ File.separator+"public"+ File.separator+"zip";

    public static int MAX_CAPTURE_LEN = 25;
    public static int MAX_INFO_LEN = 37;

    public int port = 4646;
    public int max_cnt = 8;
    public int max_sl_cnt = 3;
    public String img_path="src"+ File.separator+"main"+ File.separator+"resources"+ File.separator+"public"+ File.separator+"dashboard"+ File.separator+ File.separator+"dashboard"+ File.separator+"img";
    public String zip_path="src"+ File.separator+"main"+ File.separator+"resources"+ File.separator+"public"+ File.separator+"zip";
    public int max_capture_len = 25;
    public int max_info_len = 37;


    public static void init() {
        ObjectMapper mapper = new ObjectMapper();
        Constants constants = new Constants();
        try {
            constants = mapper.readValue(new File("config.json"), Constants.class);
            PORT = constants.port;
            MAX_CNT = constants.max_cnt;
            MAX_SL_CNT = constants.max_sl_cnt;
            IMG_PATH = constants.img_path;
            ZIP_PATH = constants.zip_path;
            MAX_CAPTURE_LEN = constants.max_capture_len;
            MAX_INFO_LEN = constants.max_info_len;
        } catch (IOException e) {
            try {
                mapper.writeValue(new File("config.json"), constants);
            } catch (IOException ioe) {
                e.printStackTrace();
            }
        }
    }
}
