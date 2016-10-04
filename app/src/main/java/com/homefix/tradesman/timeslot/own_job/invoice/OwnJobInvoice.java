package com.homefix.tradesman.timeslot.own_job.invoice;

/**
 * Created by samuel on 8/2/2016.
 */

import com.homefix.tradesman.common.ListConverter;
import com.homefix.tradesman.common.file.BasePdf;
import com.homefix.tradesman.model.Charge;
import com.homefix.tradesman.model.Customer;
import com.homefix.tradesman.model.Property;
import com.homefix.tradesman.model.Service;
import com.homefix.tradesman.model.ServiceSet;
import com.homefix.tradesman.model.Tradesman;
import com.homefix.tradesman.model.TradesmanPrivate;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.samdroid.string.Strings;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OwnJobInvoice extends BasePdf {

    private Tradesman tradesman;
    private Service service;
    private ServiceSet serviceSet;
    private Customer customer;
    private Property property;
    private TradesmanPrivate tradesmanPrivate;
    private String customerFirstName, customerEmail;

    public OwnJobInvoice(Tradesman tradesman, Service service, ServiceSet serviceSet, Customer customer, Property property, TradesmanPrivate tradesmanPrivate) {
        super("Service_Invoice_" + service.getId() + "_" + System.currentTimeMillis(), "Homefix Invoice: " + service.getId(), "Homefix Invoice: " + service.getId());

        this.tradesman = tradesman;
        this.service = service;
        this.serviceSet = serviceSet;
        this.customer = customer;
        this.property = property;
        this.tradesmanPrivate = tradesmanPrivate;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    @Override
    protected void addContent(Document document) throws DocumentException {
        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        document.add(preface);

        // write a big header
        Paragraph p1 = new Paragraph("Invoice", catFont);
        addEmptyLine(p1, 1);
        document.add(p1);

        document.add(new Paragraph("Date: " + SimpleDateFormat.getDateInstance().format(new Date()), subFont));

        // Tradesman
        String name = tradesmanPrivate != null ? tradesmanPrivate.getBusinessName() : null;
        if (Strings.isEmpty(name)) name = tradesman != null ? tradesman.getName() : null;
        if (Strings.isEmpty(name))
            name = tradesmanPrivate != null ? tradesmanPrivate.getNameOnAccount() : null;
        String email = tradesman != null ? tradesman.getEmail() : null;
        if (!Strings.isEmpty(name) || !Strings.isEmpty(email)) {
            Paragraph pFrom = new Paragraph("From", subFont);
            document.add(pFrom);

            if (!Strings.isEmpty(name)) {
                Paragraph nameFrom = new Paragraph(name, subGrayFont);
                document.add(nameFrom);
            }

            if (!Strings.isEmpty(email)) {
                Paragraph emailFrom = new Paragraph(email, subGrayFont);
                document.add(emailFrom);
            }
        }

        // Customer details
        Paragraph pCustomer = new Paragraph("To:", subFont);

        if (customer != null) {
            customerFirstName = customer.getFirstName();
            if (Strings.isEmpty(customerFirstName))
                customerFirstName = customer.getName();

            if (!Strings.isEmpty(customer.getName()))
                pCustomer.add(new Paragraph(customer.getName(), subGrayFont));
        }

        if (property != null) {
            if (!Strings.isEmpty(property.getAddressLine1()))
                pCustomer.add(new Paragraph(property.getAddressLine1(), subGrayFont));
            if (!Strings.isEmpty(property.getAddressLine2()))
                pCustomer.add(new Paragraph(property.getAddressLine2(), subGrayFont));
            if (!Strings.isEmpty(property.getAddressLine3()))
                pCustomer.add(new Paragraph(property.getAddressLine3(), subGrayFont));
            if (!Strings.isEmpty(property.getCountry()))
                pCustomer.add(new Paragraph(property.getCountry(), subGrayFont));
            if (!Strings.isEmpty(property.getPostcode()))
                pCustomer.add(new Paragraph(property.getPostcode(), subGrayFont));
        }

        if (customer != null) {
            if (!Strings.isEmpty(customer.getMobilePhone()))
                pCustomer.add(new Paragraph(customer.getMobilePhone(), subGrayFont));
            if (!Strings.isEmpty(customer.getEmail())) {
                customerEmail = customer.getEmail();
                pCustomer.add(new Paragraph(customer.getEmail(), subGrayFont));
            }
        }

        document.add(pCustomer);

        // add a table with all the charges
        createTable(document);

        double totalCost = serviceSet != null ? serviceSet.getTotalFromCharges() : 0d;
        if (totalCost == 0d) totalCost = serviceSet != null ? serviceSet.getTotalCost() : 0d;
        if (totalCost > 0) {
            addEmptyLine(document);
            Paragraph pTotalCost = new Paragraph("Total Cost: £" + Strings.priceToString(totalCost), subFont);
            document.add(pTotalCost);
        }

        // add Tradesman bank details
        Paragraph pBank = new Paragraph("Bank Information", subFont);
        addEmptyLine(document);

        if (!Strings.isEmpty(tradesmanPrivate.getAccountName())) {
            pBank.add(new Paragraph("Account Name", smallBold));
            pBank.add(new Paragraph(tradesmanPrivate.getAccountName(), subGrayFont));
        }
        if (!Strings.isEmpty(tradesmanPrivate.getAccountNumber())) {
            pBank.add(new Paragraph("Account Number", smallBold));
            pBank.add(new Paragraph(tradesmanPrivate.getAccountNumber(), subGrayFont));
        }
        if (!Strings.isEmpty(tradesmanPrivate.getSortCode())) {
            pBank.add(new Paragraph("Sort Code", smallBold));
            pBank.add(new Paragraph(tradesmanPrivate.getSortCode(), subGrayFont));
        }
        if (!Strings.isEmpty(tradesmanPrivate.getVatNumber())) {
            pBank.add(new Paragraph("VAT Number", smallBold));
            pBank.add(new Paragraph(tradesmanPrivate.getVatNumber(), subGrayFont));
        }
        if (!Strings.isEmpty(tradesmanPrivate.getNameOnAccount())) {
            pBank.add(new Paragraph("Name on Account", smallBold));
            pBank.add(new Paragraph(tradesmanPrivate.getNameOnAccount(), subGrayFont));
        }

        if (pBank.size() > 0) {
            addEmptyLine(document);
            document.add(pBank);
        }
    }

    private void createTable(Document document)
            throws DocumentException {
        List<Charge> charges = new ListConverter<>(serviceSet.getCharges()).toList();
        if (charges == null || charges.size() == 0) return;

        addEmptyLines(document, 2);

        PdfPTable table = createTable(4);

        // t.setBorderColor(BaseColor.GRAY);
        // t.setPadding(4);
        // t.setSpacing(4);
        // t.setBorderWidth(1);

        // setup the table column headers
        table.addCell(getCell("Description", columnHeaderFont));
        table.addCell(getCell("Quantity", columnHeaderFont));
        table.addCell(getCell("Rate", columnHeaderFont));
        table.addCell(getCell("Cost", columnHeaderFont));
        table.setHeaderRows(1);

        // add all the charges into the table cells
        Charge charge;
        for (int i = 0; i < charges.size(); i++) {
            charge = charges.get(i);

            table.addCell(getCell(charge.getDescription(), subGrayFont));
            table.addCell(getCell(String.format("%s", Strings.priceToString(charge.getQuantity())), subGrayFont));
            table.addCell(getCell(String.format("£%s", Strings.priceToString(charge.getAmountWithVatAndMarkup())), subGrayFont));
            table.addCell(getCell(String.format("£%s", Strings.priceToString(charge.getTotalCost())), subGrayFont));
        }

        document.add(table);
    }

}