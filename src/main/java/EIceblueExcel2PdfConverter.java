import com.spire.xls.FileFormat;
import com.spire.xls.Workbook;

import java.io.File;

public class EIceblueExcel2PdfConverter {
    public static void main(String[] args) {
        for (Sources xls : Sources.values()) {
            if (isFileExists(xls)) convert(xls);
        }
    }

    private static boolean isFileExists(Sources xls) {
        File file = new File(xls.getPath());
        return file.exists() && file.isFile();
    }

    private static void convert(Sources xls) {
        Workbook workbook = new Workbook();
        workbook.loadFromFile(xls.getPath());
//        workbook.getConverterSetting().setSheetFitToPage(true);
        workbook.getConverterSetting().setSheetFitToWidth(true);
        workbook.saveToFile(Util.getOutputPath(xls.getPath()), FileFormat.PDF);
    }
}