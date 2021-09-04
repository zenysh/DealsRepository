package com.data.helper;

import com.data.Model.Currency;
import com.data.Model.Deals;
import com.data.Repository.CurrencyRepository;
import com.data.Repository.DealRepository;
import com.data.dealrequest.DealRequest;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Component
public class ExcelHelper {
    CurrencyRepository currencyRepository;
    DealRepository dealRepository;

    @Autowired
    ExcelHelper(CurrencyRepository currencyRepository, DealRepository dealRepository) {
        this.currencyRepository = currencyRepository;
        this.dealRepository = dealRepository;
    }

    public List<Deals> getDeals(MultipartFile multipartFile) throws IOException {
        boolean duplicatedRow = false;
        List<Deals> deals = new LinkedList<>();
        List<String> columndata = new LinkedList<>();
        XSSFWorkbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        String dealid, fromisocode, toisocode, dealdate, dealamount;
        for (int index = 0; index < worksheet.getPhysicalNumberOfRows(); index++) {
            if (index > 0) {
                Deals d = new Deals();
                XSSFRow row = worksheet.getRow(index);
                DataFormatter formatter = new DataFormatter();
                dealid = formatter.formatCellValue(row.getCell(0));
                fromisocode = formatter.formatCellValue(row.getCell(1));
                toisocode = formatter.formatCellValue(row.getCell(2));
                dealdate = formatter.formatCellValue(row.getCell(3));
                dealamount = formatter.formatCellValue(row.getCell(4));
                if (columndata.contains(dealid) && columndata.contains(fromisocode) &&
                        columndata.contains(toisocode) && columndata.contains(dealdate)
                        && columndata.contains(dealamount)) {
                    duplicatedRow = true; //avoid duplicate rows.
                }

                columndata.add(dealid);
                columndata.add(fromisocode);
                columndata.add(toisocode);
                columndata.add(dealdate);
                columndata.add(dealamount);
                /*--------------------------------------------------------------------*/
                //Trim to avoid whitespaces..
                //RemoveAnySpecialCharacters
                DealRequest deallist = getDeals(removeSpecialCharacters(dealid.trim()),
                        removeSpecialCharacters(fromisocode.trim()), removeSpecialCharacters(toisocode.trim()),
                        removeSpecialCharactersDate(dealdate.trim()), removeSpecialCharactersAmount(dealamount.trim()));
                System.out.println("deadlist is " + deallist);
                if (!duplicatedRow) { //avoid duplication
                    if (deallist != null) {
                        d.setDealid(deallist.getDealid());
                        d.setFromISOCODE(deallist.getFromISOCODE());
                        d.setToISOCODE(deallist.getToISOCODE());
                        d.setDealamount(deallist.getDealamount());
                        d.setDealtime(deallist.getDealtime());
                        deals.add(d);
                    }
                }
            }
        }
        return deals;
    }

    private boolean checkIfRowIsEmpty(Row row) {
        if (row == null) {
            return true;
        }
        if (row.getLastCellNum() <= 0) {
            return true;
        }
        for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null) {
                return false;
            }
        }
        return true;
    }

    public DealRequest getDeals(String dealid, String fromisocode, String toisocode, String dealdate, String dealamount) {
        System.out.println("we reached dealrequest");
        boolean redundantValue = false;
        Deals dealsfound = dealRepository.findByDealid(dealid);
        if (dealsfound != null) {  //omit redundant values
            if (dealsfound.getDealid().equals(dealid) && dealsfound.getFromISOCODE().equals(fromisocode) &&
                    dealsfound.getToISOCODE().equals(toisocode) && dealsfound.getDealtime().equals(dealdate) &&
                    dealsfound.getDealamount().equals(dealamount)) {
                redundantValue = true;
            }
        }
        Currency crFrom = currencyRepository.findCurrencyByCurrencycode(fromisocode);
        Currency crTo = currencyRepository.findCurrencyByCurrencycode(toisocode);
        if (dealid.isEmpty() || fromisocode.isEmpty() || toisocode.isEmpty() || dealdate.isEmpty() || dealamount.isEmpty()) {
            return null; /*If any Row is empty then omit it, avoid insert*/
        } else if (crFrom == null || crTo == null) { //currencyCode Should be exact Same.
            return null;
        } else if (!dealamount.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+") || checkfordoubledots(dealamount)) {
            return null; //because amount either is not numeric or has more than one dots, or contains illegal characters
        } else if (!validateJavaDate(dealdate)) {//Date should be in dd/MM/yyyy format
            return null;
        } else if (redundantValue) {
            return null;
        } else {
            DealRequest dl = new DealRequest();
            dl.setDealid(dealid);
            dl.setFromISOCODE(fromisocode);
            dl.setToISOCODE(toisocode);
            dl.setDealtime(dealdate);
            dl.setDealamount(dealamount);
            return dl;
        }
    }

    public static boolean validateJavaDate(String strDate) {
        /* Check if date is 'null' */
        if (!strDate.trim().equals("")) {
            /*
             * Set preferred date format,
             * For example MM-dd-yyyy, MM.dd.yyyy,dd.MM.yyyy etc.*/
            SimpleDateFormat sdfrmt = new SimpleDateFormat("dd/MM/yyyy");
            sdfrmt.setLenient(false);
            /* Create Date object
             * parse the string into date
             */
            try {
                Date javaDate = sdfrmt.parse(strDate);
            } catch (ParseException e) {
                return false;
            }
            /* Return true if date format is valid */
        }
        return true;
    }

    public String removeSpecialCharacters(String value) {
        return value.replaceAll("[^a-zA-Z0-9]", "");
    }

    public String removeSpecialCharactersAmount(String value) {
        return value.replaceAll("[^.a-zA-Z0-9]", ""); //Amount contains '.', so exclude
    }

    public String removeSpecialCharactersDate(String value) {
        return value.replaceAll("[^/a-zA-Z0-9]", ""); //Date contains '/' so, exclude
    }

    public boolean checkfordoubledots(String value) {
        return (value.substring(value.indexOf('.') + 1).contains("."));
    }

    public String validatebeforeInsertString(String value) {
        return "edited";
    }

    public int ValidatebeforeInsertInt(int value) {
        return 0;
    }

    public boolean checkIfColumnsAreinOrder(MultipartFile multipartFile) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        XSSFRow row = worksheet.getRow(0);
        boolean column = false;
        if (row.getCell(0).getStringCellValue().equals("dealid") &&
                row.getCell(1).getStringCellValue().equals("fromISOCODE")
                && row.getCell(2).getStringCellValue().equals("toISOCODE")
                && row.getCell(3).getStringCellValue().equals("dealtime")
                && row.getCell(4).getStringCellValue().equals("dealamount")) {
            column = true;
        }
        return column;
    }

}
