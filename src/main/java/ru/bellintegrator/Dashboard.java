package ru.bellintegrator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.abs;
import static ru.bellintegrator.Constants.*;
import static ru.bellintegrator.Editor.HEAD;
import static ru.bellintegrator.Editor.TAIL;

public class Dashboard {
    public LinkedList<Order> orderList = new LinkedList<>();
    public LinkedList<String> typeList = new LinkedList<>();
    public LinkedList<File> fileList = new LinkedList<>();
    public LinkedList<String> bigBigFileList = new LinkedList<>();
    public LinkedList<String> bigFileList = new LinkedList<>();
    public LinkedList<String> smallBigFileList = new LinkedList<>();
    public LinkedList<String> smallFileList = new LinkedList<>();

    public String html ="";
    public String editor = "";

    private String initialEditor = "";
    private LinkedList<Order> initialOrderList;

    private static boolean isEdited = false;

    public Dashboard(){
        typeList.add("tv");
        typeList.add("megogo");
        typeList.add("prosense");
    }

    public void update(){
        updateEditor();
        updateHtml();
    }

    public void updateEditor(){

        listFilesForFolder(new File(IMG_PATH));
        fillFileList();


        StringBuilder sb = new StringBuilder();
        sb.append(HEAD);
        sb.append("<body>");

        sb.append("<script type=\"text/javascript\">");
        sb.append("        function inputHandler(id, maxVal) {");
        sb.append("    document.getElementById('p'+id).innerHTML = document.getElementById(id).value.length + '/'+maxVal");
        sb.append("}</script>");
        sb.append("<div class=\"topBar\">");

        //если были изменения
        sb.append("<table cellpadding='20'><tr>");
        if (isEdited) {
            sb.append("<td><a href='/dashboard/save'>Подтвердить</a></td>");
            sb.append("<td><a href='/dashboard/cancel'>Отменить</a></td>");

        }
        sb.append("<td><a href='/dashboard/get'>Просмотр</a></td>");
        sb.append("<td><a href='/dashboard/upload'>Загрузка фотоархива</a></td>");
        sb.append("</tr></table>");

        sb.append("</div>");
        sb.append("<div class=\"content\">");
        sb.append("<h2 class=\"special-title\">Редактор дашборда</h2>");

        sb.append("<form action='/dashboard' method='POST'> ");

        for(int i=0; i<MAX_CNT; i++){
            sb.append("<p class=\"highlight\">Позиция №"+i+getPositionDescription(i)+"</p>");
            sb.append("<div class=\"table\">");
            sb.append("<div class=\"table-tr\">");
            sb.append("<table class='tbl'>");
            sb.append("<tr>");
            sb.append("<th align='center'>"+getBig(i)+"</th>");
            sb.append("<th align='center'>"+getSmall(i)+"</th>");
            sb.append("<th align='center'>Наименование</th>");
            sb.append("<th align='center'>ID контента</th>");
            sb.append("<th align='center'>Тип</th>");
            sb.append("<th align='center'>Заголовок</th>");
            sb.append("<th align='center'>символов/всего</th>");
            sb.append("<th align='center'>Текст</th>");
            sb.append("<th align='center'>символов/всего</th>");
            sb.append("</tr>");
            Order o = orderList.get(i);
            LinkedList<String> big, small;
            for(int j=0;j<MAX_SL_CNT ;j++) {
                Afisha a = o.orderList.get(j);
                sb.append("<tr>");

                if (i==0) {
                    big = bigBigFileList;
                } else {
                    big = bigFileList;
                }

                if (i<2) {
                    small = smallBigFileList;
                } else {
                    small = smallFileList;
                }

                sb.append("<td align='center'><select id='b" + i + j + "' name='b" + i + j + "' type='text'>"+getOption(a.big, big)+"</select></td>");
                sb.append("<td align='center'><select id='s" + i + j + "' name='s" + i + j + "' type='text'>"+getOption(a.small, small)+"</select></td>");
                sb.append("<td align='center'><input id='n" + i + j + "' name='n" + i + j + "' type='text' value='"+a.name+"' /></td>");
                sb.append("<td align='center'><input id='i" + i + j + "' name='i" + i + j + "' type='text' value='"+a.id+"' /></td>");
                sb.append("<td align='center'><select id='t" + i + j + "' name='t" + i + j + "' type='text'>"+getOption(a.type, typeList)+"</select></td>");
                sb.append("<td align='center'><input id='c" + i + j + "' name='c" + i + j + "' type='text' value='"+a.capture+"'  oninput=\"inputHandler('c"+i+j+"',27)\"/></td>");
                sb.append("<td align='center'><p id='pc" + i + j + "'>"+a.capture.length()+"/"+MAX_CAPTURE_LEN+"</p></td>");
                sb.append("<td align='center'><input id='f" + i + j + "' name='f" + i + j + "' type='text' value='"+a.info+"'  oninput=\"inputHandler('f"+i+j+"',35)\"/></td>");
                sb.append("<td align='center'><p id='pf"+i+j+"'>"+a.info.length()+"/"+MAX_INFO_LEN+"</p></td>");
                sb.append("</tr>");
            }
            sb.append("</table>");

        }

        sb.append("<input type=\"submit\" value=\"Сохранить изменения\" class=\"btn-link\"/></form>");

        sb.append(TAIL);

        editor = sb.toString();
    }

    private String getBig(int i) {
        if(i==0){
            return "1150x575";
        }
        return "575x575";
    }

    private String getSmall(int i) {
        if (i<2){
            return "600x300";
        }
        return "300x300";
    }

    private String getPositionDescription(int i){
        switch (i){
            case 0: return " - верхняя строка, афиша слева";
            case 1: return " - верхняя строка, афиша справа";
            case 2: return " - средняя строка, афиша слева";
            case 3: return " - средняя строка, афиша посередине";
            case 4: return " - средняя строка, афиша справа";
            case 5: return " - нижняя строка, афиша слева";
            case 6: return " - нижняя строка, афиша посередине";
            case 7: return " - нижняя строка, афиша справа";
            default: return "";
        }
    }

    public void remember(){
        if (!isEdited){
            initialEditor = editor;
            initialOrderList = cloneOrderList(orderList);
            isEdited = true;
        }
    }

    public void orderListclear(){
        orderList.forEach((o)->{
            o.orderList.clear();
        });
        orderList.clear();
    }

    public void updateHtml(){
        //update html
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<html lang='ru'>");
        sb.append("<head>");
        sb.append("<meta charset='utf-8'>");
        sb.append("<title></title>");
        sb.append("<meta name='robots' content='noindex, nofollow'>");
        sb.append("<meta http-equiv='X-UA-Compatible' content='IE=edge'>");
        sb.append("<meta name='viewport' content='width=device-width, initial-scale=1'>");
        sb.append("<link rel='stylesheet' href='css/posters.css'>");
        sb.append("<script>");
        sb.append("document.createElement( 'picture' );");
        sb.append("</script>");
        sb.append("<script src='js/picturefill.min.js' async></script>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<div class='app'>");
        sb.append("<div class='posters js-posters'>");

        orderList.forEach((o)->{
            sb.append(o.toString());
        });

        sb.append("</div>");
        sb.append("</div>");
        sb.append("<script src='js/jquery-3.2.1.min.js'></script>");
        sb.append("<script src='js/slick.min.js'></script>");
        sb.append("<script src='js/posters.js'></script>");
        sb.append("</body>");
        sb.append("</html>");

        html = sb.toString();
    }

    public void listFilesForFolder(final File folder) {
        fileList.clear();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                fileList.add(fileEntry);
            }
        }
    }

    private void fillFileList(){
        bigBigFileList.clear();
        bigFileList.clear();
        smallBigFileList.clear();
        smallFileList.clear();

        fileList.forEach((f)->{
            try {
                BufferedImage bimg = ImageIO.read(f);
                int width = bimg.getWidth();
                int height = bimg.getHeight();

                int i = abs(width - height);

                switch (i) {
                    case 575:
                        bigBigFileList.add(f.getName());
                        break;
                    case 300:
                        smallBigFileList.add(f.getName());
                        break;
                    case 0:
                        if (width == 575) {
                            bigFileList.add(f.getName());
                        } else {
                            if (width == 300) {
                                smallFileList.add(f.getName());
                            } else {
                                System.out.println("Error: file " + f.getName() + " has wrong size");
                            }
                        }
                        break;

                    default:
                        System.out.println("Error: file " + f.getName() + " has wrong size");
                }
            } catch (IOException ioe){
                ioe.printStackTrace();
            }

        });
    }

    public void save(){
        isEdited = false;
    }

    public void cancel(){
        editor = initialEditor;
        orderList = cloneOrderList(initialOrderList);
        isEdited = false;
    }

    public static LinkedList<Order> cloneOrderList(LinkedList<Order> list) {

        LinkedList<Order> clone = new LinkedList<Order>();

        list.forEach((item)->{
            Order o = new Order();
            o.postersType = item.postersType;
            o.orderList = cloneAfishaList(item.orderList);
            clone.add(o);
        });

        return clone;
    }

    public static LinkedList<Afisha> cloneAfishaList(LinkedList<Afisha> list) {
        LinkedList<Afisha> clone = new LinkedList<Afisha>();
        for (Afisha item : list) clone.add(item.clone());
        return clone;
    }

    public String getOption(String name, LinkedList<String> list){
        StringBuilder sb = new StringBuilder();
        list.forEach((t)->{
            sb.append("<option ");
            if(name.equals(t)){
                sb.append("selected ");
            }
            sb.append("value='");
            sb.append(t);
            sb.append("'>");
            sb.append(t);
            sb.append("</option>");
        });

        return sb.toString();
    }

    public void clear(){
        ArrayList exists = new ArrayList();
        orderList.forEach((o)->{
            o.orderList.forEach((l)->{
                if(l.big.length()>0){
                    exists.add(l.big);
                }

                if(l.small.length()>0){
                    exists.add(l.small);
                }

            });
        });

        fileList.forEach((f)->{

            if(!(exists.contains(f.getName()))){
                if (f.getAbsoluteFile().exists()) {
                    System.out.println("["+f.getName()+"] - " + f.getAbsoluteFile().delete());
                } else {
                    System.out.println("File " + f.getAbsoluteFile().getAbsolutePath() + " not exist");
                }

            }
        });

        listFilesForFolder(new File(IMG_PATH));
        fillFileList();
    }
}
