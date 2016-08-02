package com.homefix.tradesman.common.file;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.samdroid.common.MyLog;
import com.samdroid.string.Strings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Base class to handle creating and view PDFs
 * <p>
 * http://www.vogella.com/tutorials/JavaPDF/article.html
 */
public abstract class BasePdf {

    protected String fileName, title, subject;
    private File myFile;

    static final protected Font catFont = new Font(Font.FontFamily.HELVETICA, 18,
            Font.BOLD);
    static final protected Font redFont = new Font(Font.FontFamily.HELVETICA, 12,
            Font.NORMAL, BaseColor.RED);
    static final protected Font subFont = new Font(Font.FontFamily.HELVETICA, 12,
            Font.BOLD);
    static final protected Font subGrayFont = new Font(Font.FontFamily.HELVETICA, 12,
            Font.NORMAL, BaseColor.GRAY);
    static final protected Font smallBold = new Font(Font.FontFamily.HELVETICA, 10,
            Font.BOLD);
    static final protected Font columnHeaderFont = new Font(Font.FontFamily.HELVETICA, 14,
            Font.BOLD);

    public BasePdf(String fileName, String title, String subject) {
        this.fileName = fileName;
        this.title = title;
        this.subject = subject;
    }

    public boolean generate() {
        try {
            File pdfFolder;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                pdfFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "homefix_invoices");
            } else {
                pdfFolder = new File(Environment.getExternalStorageDirectory(), "homefix_invoices");
            }

            // make sure the folder exists
            if (!pdfFolder.exists()) {
                pdfFolder.mkdirs();
                MyLog.i("BasePdf", "Pdf Directory created");
            }

            myFile = new File(pdfFolder, fileName + ".pdf");

            if (!myFile.exists()) myFile.createNewFile();

            OutputStream output = new FileOutputStream(myFile);

            //Step 1
            Document document = new Document();

            //Step 2
            PdfWriter.getInstance(document, output);

            //Step 3
            document.open();

            //Step 4 Add content
            addMetaData(document);
            addTitlePage(document);
            addContent(document);

            //Step 5: Close the document
            document.close();

            return true;

        } catch (Exception e) {
            MyLog.printStackTrace(e);
            return false;
        }
    }

    public void view(Context context) {
        openPdf(context, myFile);
    }

    public static void openPdf(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    // iText allows to add metadata to the PDF which can be viewed in your Adobe
    // Reader
    // under File -> Properties
    protected void addMetaData(Document document) throws DocumentException {
        document.addTitle(Strings.returnSafely(title));
        document.addSubject(Strings.returnSafely(subject));
    }

    protected void addTitlePage(Document document) throws DocumentException {
        // can be overridden to include a title page
    }

    protected abstract void addContent(Document document) throws DocumentException;

    protected PdfPTable createTable(int numberColumns)
            throws BadElementException {
        return new PdfPTable(numberColumns);
    }

    protected void addEmptyLine(Paragraph paragraph, int number) {
        if (paragraph == null) return;

        for (int i = 0; i < number; i++) paragraph.add(new Paragraph(" "));
    }

    protected void addEmptyLine(Document document) throws DocumentException {
        Paragraph pSpacing = new Paragraph();
        addEmptyLine(pSpacing, 1);
        document.add(pSpacing);
    }

    protected void addEmptyLines(Document document, int numberLines) throws DocumentException {
        for (int i = 0; i < numberLines; i++) addEmptyLine(document);
    }

    protected PdfPCell getCell(String contents) {
        PdfPCell c = new PdfPCell(new Phrase(Strings.returnSafely(contents)));
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        return c;
    }

    protected PdfPCell getCell(String contents, Font font) {
        PdfPCell c = new PdfPCell(new Phrase(Strings.returnSafely(contents), font));
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        return c;
    }

}