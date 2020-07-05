package ru.bellintegrator;

import static ru.bellintegrator.Editor.HEAD;
import static ru.bellintegrator.Editor.TAIL;

public class Uploader {
    public String html;

    public Uploader() {
        StringBuilder sb = new StringBuilder();
        sb.append(HEAD);
        sb.append("<body>");
        sb.append("<div class=\"topBar\"><table cellpadding='20'><tr><td><a href='/dashboard/edit'>Назад</a></td><td><a href='/dashboard/clear'>Удалить неиспользуемые файлы</a></td></tr></table></div>");
        sb.append("<div class=\"content\">");
        sb.append("<h2 class=\"special-title\">Загрузить афишу</h2>");
        sb.append("<p class=\"highlight\">Выберите файл с фотоархивом:</p>");
        sb.append("<div class=\"table\">");
        sb.append("<form action=\"/dashboard/upload\" enctype=\"multipart/form-data\" method=\"POST\">");
        sb.append("<div class=\"table-tr\">");
        sb.append("<div class = \"table-td\"><input id=\"file\" name='uploaded_file' accept='.zip' type=\"file\"/></div>");
        sb.append("</div>");
        sb.append("<div class=\"table-tr\">");
        sb.append("<div class = \"table-td\"><input type=\"submit\" value=\"Загрузить\" class=\"btn-link\"/></div>");
        sb.append("</div>");
        sb.append("</form>");
        sb.append(TAIL);
        html = sb.toString();
    }
}
