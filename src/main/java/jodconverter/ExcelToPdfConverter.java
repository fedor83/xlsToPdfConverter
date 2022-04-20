package jodconverter;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OLE2NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.core.office.OfficeUtils;
import org.jodconverter.local.JodConverter;
import org.jodconverter.local.office.LocalOfficeManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class ExcelToPdfConverter {

    private String path = null;
    private OfficeManager officeManager = null;
    private IPrintAction printAction;
    private static final ExcelToPdfConverter INSTANCE = new ExcelToPdfConverter();
    private static final String SOURCE_NAME = "libreoffice";

    private ExcelToPdfConverter() {
        this.path = System.getProperty(SOURCE_NAME);
        if (this.path == null) {
            if (new File(SOURCE_NAME).exists()) {
                path = new File(SOURCE_NAME).getAbsolutePath();
            } else {
                String office = getClass().getResource("/" + SOURCE_NAME).getPath();
                File file = new File(office);
                path = file.getAbsolutePath();
            }
        }
    }

    public static ExcelToPdfConverter getInstance() {
        return INSTANCE;
    }

    public void registerStatusAction(IPrintAction action) {
        printAction = action;
    }

    public void JodConvertFileToSheetFiles(File inputDocument, File outputDir) throws Exception {

        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        File[] files = outputDir.listFiles();
        Arrays.stream(files).forEach(f -> f.delete());
        try {
            // Start an office process and connect to the started instance (on port 2002).
            officeManager = LocalOfficeManager.builder().install().officeHome(path).build();
            officeManager.start();
            Workbook sourceWorkbook = createWorkbook(inputDocument);
            ByteArrayOutputStream baout = new ByteArrayOutputStream();
            sourceWorkbook.write(baout);
            InputStream bais = new ByteArrayInputStream(baout.toByteArray());
            String fileName = getFileName(inputDocument.getName());
            File outputFile = new File(outputDir.getAbsolutePath() + File.separatorChar + fileName);
            JodConverter.convert(bais).to(outputFile).execute();
            if (printAction != null) {
                printAction.print("The " + HtmlUtils.getLinkFromPath(outputFile.getAbsolutePath()) + " was created");
            }
            sourceWorkbook.close();
        } finally {
            // Stop the office process
            OfficeUtils.stopQuietly(officeManager);
        }
    }

    private Workbook createWorkbook(File inputDocument) throws IOException, InvalidFormatException {
        Workbook sourceWorkbook;
        try {
            sourceWorkbook = new XSSFWorkbook(inputDocument);
        } catch (OLE2NotOfficeXmlFileException e) {
            try (FileInputStream fileInputStream = new FileInputStream(inputDocument.getAbsoluteFile())) {
                sourceWorkbook = new HSSFWorkbook(fileInputStream);
            }
        }
        return sourceWorkbook;
    }

    private String getFileName(String name) {
        String fileName = name.substring(0, name.lastIndexOf(".")) + ".pdf";
        String invalidCharRemoved = fileName.replaceAll("[\\\\/:*?\"<>|]", "");
        System.out.println(invalidCharRemoved);
        return invalidCharRemoved;
    }
}