package com.data.helper;

import com.data.Model.Deals;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ExcelWriter {
    public ByteArrayInputStream dealsToExcel(List<Deals> dealsList) throws IOException {
        String[] COLUMNs = { "id", "dealid", "fromISOCODE", "toISOCODE","dealtime","dealamount" };
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream();
             ){
            CreationHelper createHelper = workbook.getCreationHelper();
            Sheet sheet = workbook.createSheet("DealLists");
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.BLUE.getIndex());
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            // Row for Header
            Row headerRow = sheet.createRow(0);
            // Header
            for (int col = 0; col < COLUMNs.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(COLUMNs[col]);
                cell.setCellStyle(headerCellStyle);
            }

            // CellStyle for Age
            //CellStyle ageCellStyle = workbook.createCellStyle();
           // ageCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#"));

            int rowIdx = 1;
            for (Deals deal : dealsList) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(deal.getId());
                row.createCell(1).setCellValue(deal.getDealid());
                row.createCell(2).setCellValue(deal.getFromISOCODE());
                row.createCell(3).setCellValue(deal.getToISOCODE());
                row.createCell(4).setCellValue(deal.getDealtime());
                row.createCell(5).setCellValue(deal.getDealamount());
                Cell ageCell = row.createCell(6);
            //    ageCell.setCellValue(deal.getId());
             //  ageCell.setCellStyle(ageCellStyle);
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
