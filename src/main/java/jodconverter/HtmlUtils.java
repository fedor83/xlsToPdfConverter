package jodconverter;

public class HtmlUtils {
    public static String getLinkFromPath(String path) {
        return "<a href=\"" + path + "\">" + path + "</a>";
    }
}
