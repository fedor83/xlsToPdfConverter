public class Util {
    public static String getOutputPath(String sourcePath) {
        int index = sourcePath.lastIndexOf(".");
        return sourcePath.substring(0, ++index) + "pdf";
    }
}
