import com.aspose.cells.PdfCompliance;
import com.aspose.cells.PdfSaveOptions;
import com.aspose.cells.Workbook;

public class AsposeExcel2PdfConverter {
    public static void main(String[] args) throws Exception {
        for (Sources xls : Sources.values()) {
            convert(xls);
        }
    }

    private static void convert(Sources xls) throws Exception {
        Workbook workbook = new Workbook(xls.getPath());
        PdfSaveOptions options = new PdfSaveOptions();
        options.setCompliance(PdfCompliance.PDF_A_1_A);
        workbook.save(Util.getOutputPath(xls.getPath()), options);
    }
}
