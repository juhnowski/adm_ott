package ru.bellintegrator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Order {
    public String postersType;
    public LinkedList<Afisha> orderList = new LinkedList<>();

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("<div class='posters__item "+postersType+"'>");
        sb.append("<div class='slick js-slider'>");
        orderList.forEach((a)->{
            sb.append(a.toString());
        });
        sb.append("</div></div>");

        return sb.toString();
    }
}
