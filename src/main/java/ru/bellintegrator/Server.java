package ru.bellintegrator;

import static ru.bellintegrator.Constants.*;
import static spark.Spark.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.SerializationFeature;
import spark.ModelAndView;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import spark.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.nio.file.*;
import static spark.Spark.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Server {
    public static Dashboard dashboard = new Dashboard();
    public static Uploader uploader = new Uploader();

    public static void main(String[] args) {
        Constants.init();
        port(PORT);

        staticFiles.externalLocation("src"+ File.separator +"main"+ File.separator +"resources"+ File.separator +"public"+ File.separator+"dashboard"+ File.separator);
        staticFiles.externalLocation("src"+ File.separator +"main"+ File.separator +"resources"+ File.separator +"public"+ File.separator+"dashboard"+File.separator +"css");
        staticFiles.externalLocation("src"+ File.separator +"main"+ File.separator +"resources"+ File.separator +"public"+ File.separator+"dashboard"+File.separator +"fonts");
        staticFiles.externalLocation("src"+ File.separator +"main"+ File.separator +"resources"+ File.separator +"public"+ File.separator+"dashboard"+File.separator +"image");
        staticFiles.externalLocation("src"+ File.separator +"main"+ File.separator +"resources"+ File.separator +"public"+ File.separator+"dashboard"+File.separator +"img");
        staticFiles.externalLocation("src"+ File.separator +"main"+ File.separator +"resources"+ File.separator +"public"+ File.separator+"dashboard"+File.separator +"js");
        staticFiles.externalLocation("src"+ File.separator +"main"+ File.separator +"resources"+ File.separator +"public"+ File.separator+"dashboard"+File.separator +"md");

        get("/dashboard/get", (req,res)->{
            if(dashboard.html.length()==0){
                load();
            }
            return dashboard.html;
        });

        redirect.get("/dashboard", "/dashboard/");

        get("/dashboard/", (req,res)->{
            if(dashboard.html.length()==0){
                load();
            }
            return dashboard.html;
        });

        get("/dashboard/save", (req,res)->{
            dashboard.save();
            dashboard.update();
            save();
            System.out.println("saved");
            return dashboard.editor;
        });

        get("/dashboard/clear", (req,res)->{
            dashboard.clear();
            return uploader.html;
        });

        get("/dashboard/cancel", (req,res)->{
            dashboard.cancel();
            dashboard.update();
            save();
            System.out.println("canceled");
            return dashboard.editor;
        });

        get("/dashboard/edit", (req,res)->{
            System.out.println("edit");
            if(dashboard.editor.length()==0){
                System.out.println("edit - load");
                load();
            }

            return dashboard.editor;
        });

        get("/dashboard/upload", (req,res)->{
            return uploader.html;
        });

        post("/dashboard/upload", (req,res)->{
            File uploadDir = new File(ZIP_PATH);
            Path tempFile = Files.createTempFile(uploadDir.toPath(), "", "");

            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
//            System.out.println();
            try (InputStream input = req.raw().getPart("uploaded_file").getInputStream()) { // getPart needs to use same "name" as input field in form
                Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("File has been uploaded: "+tempFile.getFileName());
            unZipIt(ZIP_PATH+ File.separator +tempFile.getFileName(), IMG_PATH);
            dashboard.update();
            save();
            return dashboard.editor;
        });

        post("/dashboard", (req, res) -> {
            String result="";
            dashboard.remember();
            dashboard.orderListclear();
            for (int i=0; i<MAX_CNT; i++){
                Order order = new Order();
                String strPosterType;
                switch (i){
                    case 0:
                        strPosterType = "posters__item-l";
                        break;
                    case 1:
                        strPosterType = "posters__item-mobile-xl";
                        break;
                    default:
                        strPosterType ="";
                }
                order.postersType = strPosterType;

                for(int j=0;j<MAX_SL_CNT ;j++) {
                    Afisha a = new Afisha();
                    a.big = req.queryParams("b"+i+j);
                    a.small = req.queryParams("s"+i+j);
                    a.name = req.queryParams("n"+i+j);
                    a.id = req.queryParams("i"+i+j);
                    a.type = req.queryParams("t"+i+j);
                    a.capture = req.queryParams("c"+i+j);
                    a.info = req.queryParams("f"+i+j);
                    if(a.name.length()>0){
                        order.orderList.add(a);
                    }
                }
                dashboard.orderList.add(order);
            }
            dashboard.update();
            save();
            return dashboard.editor;
        });
    }

    public static void save(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File("dashboard.json"), dashboard);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            dashboard = mapper.readValue(new File("dashboard.json"), Dashboard.class);
        } catch (IOException e) {
            e.printStackTrace();
            init();
            save();
        }
    }

    public static void init() {
        Afisha a11 = new Afisha("bol_600x300.jpg","bol_1150x575.jpg","Большой", "2968071", "megogo","Большой", "Смотри и наслаждайся - 14 дней бесплатно!");
        Afisha a12 = new Afisha("TLC1_600x300.jpg","TLC1_1150x575.jpg","Монстры внутри меня", "55", "tv","Монстры внутри меня", "С 3 января по средам в 23:00");
        Afisha a13 = new Afisha("Detki_600x300.jpg","Detki_1150x575.jpg","Детки напрокат", "3273291", "megogo","Детки напрокат", "Смотри лучшие фильмы от Мегого, 14 дней бесплатно");
        Order order1 = new Order();
        order1.orderList.add(a11);
        order1.orderList.add(a12);
        order1.orderList.add(a13);
        order1.postersType = "posters__item-l";


        Afisha a21 = new Afisha("Gogol_600x300.jpg", "Gogol_575x575.jpg", "Гоголь", "2993411", "megogo", "Гоголь. Начало", "Gogol. The Beginning");
        Afisha a22 = new Afisha("AN1_600x300.jpg", "AN1_575x575.jpg", "AN1", "54", "tv", "Спасая слонов", "С 12 января по пятницам в 21:00");
        Afisha a23 = new Afisha("AN2_600x300.jpg", "AN2_575x575.jpg", "AN2", "54", "tv", "Вторжение", "С 29 января по понедельникам в 21:00");
        Order order2 = new Order();
        order2.orderList.add(a21);
        order2.orderList.add(a22);
        order2.orderList.add(a23);
        order2.postersType = "posters__item-mobile-xl";

        Afisha a31 = new Afisha("ES_1_300x300.jpg", "ES_1_575x575.jpg", "Eurosport", "73", "tv", "Eurosport", "Весь год в прямом эфире на Eurosport");
        Afisha a32 = new Afisha("40_300.jpg", "40_575.jpg", "Байкал на авто", "40", "prosense", "Байкал", "на автомобиле");
        Afisha a33 = new Afisha("41_300.jpg", "41_575.jpg", "Пролет над Москвой", "41", "prosense", "Пролет", "над Москвой");
        Order order3 = new Order();
        order3.orderList.add(a31);
        order3.orderList.add(a32);
        order3.orderList.add(a33);
        order3.postersType =" ";

        Afisha a41 = new Afisha("FOX2_300x300.jpg", "FOX2_575x575.jpg", "СИМПСОНЫ 1-6", "83", "tv", "СИМПСОНЫ 1-6", "ПРЕМЬЕРА НА FOX");
        Afisha a42 = new Afisha("45_300.jpg", "45_575.jpg", "Александринский театр", "45", "prosense", "Александринский театр", "новая сцена");
        Afisha a43 = new Afisha("44_300.jpg", "44_575.jpg", "Bently Bentayga", "44", "prosense", "Bently Bentayga", "Обзорная презентация автомобиля");
        Order order4 = new Order();
        order4.orderList.add(a41);
        order4.orderList.add(a42);
        order4.orderList.add(a43);
        order4.postersType =" ";

        Afisha a51 = new Afisha("ES_2_300x300.jpg", "ES_2_575x575.jpg", "Eurosport", "73", "tv", "Eurosport", "Весь год в прямом эфире на Eurosport");
        Afisha a52 = new Afisha("47_300.jpg", "47_575.jpg", "Санкт-Петербург", "47", "prosense", "Санкт-Петербург", "Обзорная экскурсия");
        Afisha a53 = new Afisha("48_300.jpg", "48_575.jpg", "Burning Man 2016", "48", "prosense", "Burning Man 2016", "Отчетный ролик о мировом фестивале");
        Order order5 = new Order();
        order5.orderList.add(a51);
        order5.orderList.add(a52);
        order5.orderList.add(a53);
        order5.postersType =" ";

        Afisha a61 = new Afisha("Kamchatka_new_full_prorez_01_300x300.png", "Kamchatka_new_full_prorez_01_575x575.png", "Камчатка", "64", "prosense", "Камчатка", "Камчатка");
        Afisha a62 = new Afisha("50_300.jpg", "50_575.jpg", "Dance Open Malambo", "50", "prosense", "Dance Open", "Malambo");
        Afisha a63 = new Afisha("51_300.jpg", "51_575.jpg", "Dance Open Padede", "51", "prosense", "Dance Open", "Padede");
        Order order6 = new Order();
        order6.orderList.add(a61);
        order6.orderList.add(a62);
        order6.orderList.add(a63);
        order6.postersType =" ";

        Afisha a71 = new Afisha("LenVR_Sweden_02_300x300.png", "LenVR_Sweden_02_575x575.png", "Швеция", "65", "prosense", "Швеция", "Швеция");
        Afisha a72 = new Afisha("53_300.jpg", "53_575.jpg", "Dance Open Corsar", "53", "prosense", "Dance Open", "Corsar");
        Afisha a73 = new Afisha("54_300.jpg", "54_575.jpg", "Dance Open Summary", "54", "prosense", "Dance Open", "Summary");
        Order order7 = new Order();
        order7.orderList.add(a71);
        order7.orderList.add(a72);
        order7.orderList.add(a73);
        order7.postersType =" ";

        Afisha a81 = new Afisha("GreatGonzo_mejdu_Petrovim_i_Vodkinim_300x300.png", "GreatGonzo_mejdu_Petrovim_i_Vodkinim_575x575.png", "Great Gonzo", "66", "prosense", "Great Gonzo", "между Петровым и Водкиным");
        Afisha a82 = new Afisha("Out_of_Ted_300x300.png", "Out_of_Ted_575x575.png", "Out_of_Ted", "69", "prosense", "Вне тела", "Out of Ted");
        Afisha a83 = new Afisha("58_300.jpg", "58_575.jpg", "M-1 75", "58", "prosense", "M-1 75", "Шлеменко VS Брэдли");
        Order order8 = new Order();
        order8.orderList.add(a81);
        order8.orderList.add(a82);
        order8.orderList.add(a83);
        order8.postersType =" ";


        dashboard.orderList.clear();
        dashboard.orderList.add(order1);
        dashboard.orderList.add(order2);
        dashboard.orderList.add(order3);
        dashboard.orderList.add(order4);
        dashboard.orderList.add(order5);
        dashboard.orderList.add(order6);
        dashboard.orderList.add(order7);
        dashboard.orderList.add(order8);

        dashboard.update();
    }

    public static void unZipIt(String zipFile, String outputFolder){

        byte[] buffer = new byte[1024];

        try{

            //create output directory is not exists
            File folder = new File(IMG_PATH);
            if(!folder.exists()){
                folder.mkdir();
            }

            //get the zip file content
            ZipInputStream zis =
                    new ZipInputStream(new FileInputStream(zipFile));
            //get the zipped file list entry
            ZipEntry ze;
            try {
                ze = zis.getNextEntry();
            } catch (Exception e){
                ze = null;
                e.printStackTrace();
            }

            while(ze!=null){

                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separator + fileName);

                System.out.println("file unzip : "+ newFile.getAbsoluteFile());

                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                try {
                    ze = zis.getNextEntry();
                } catch (Exception e){
                    ze = null;
                    e.printStackTrace();
                }
            }

            zis.closeEntry();
            zis.close();

            System.out.println("Done");

            System.out.println("Init Done");
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
}
