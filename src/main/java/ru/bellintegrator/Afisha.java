package ru.bellintegrator;

public class Afisha implements Cloneable{
    public String big = "";
    public String small = "";
    public String name = "";
    public String id = "";
    public String type = "";
    public String capture = "";
    public String info = "";

    public Afisha(){}

    public Afisha(String small, String big, String name, String id, String type, String capture, String info){
        this.big = big;
        this.small = small;
        this.name = name;
        this.id = id;
        this.type = type;
        this.capture = capture;
        this.info = info;
    }

    @Override
    public Afisha clone(){
        try {
            return (Afisha)super.clone();
        }
        catch( CloneNotSupportedException ex ) {
            throw new InternalError();
        }
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("<picture class='poster js-slider__item'>");
        sb.append("<source srcset='img/"+small+"' media='(max-width: 767px)'>");
        sb.append("<img ");
        sb.append("src='img/"+big+"'");
        sb.append("alt='"+name+"'");
        sb.append("data-id='"+id+"'");
        sb.append("data-type='"+type+"'");
        sb.append("><div class='poster__header'>");
        sb.append("<span class='poster__title'>"+capture+"</span>");
        sb.append("<span class='poster__info'>"+info+"</span>");
        sb.append("</div>");
        sb.append("</picture>");
        return sb.toString();
    }
}
