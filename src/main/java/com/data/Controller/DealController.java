package com.data.Controller;

import com.data.Exception.InvalidException;
import com.data.Model.Deals;
import com.data.Service.DealsService;
import com.data.helper.ExcelHelper;
import com.data.response.DealResponse;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/rest/deal")
public class DealController {
    ExcelHelper excelHelper;
    DealsService dealsService;

    @Autowired
    DealController(ExcelHelper excelHelper, DealsService dealsService) {
        this.excelHelper = excelHelper;
        this.dealsService = dealsService;
    }

    @RequestMapping(value = "/post/deal/excel",method = RequestMethod.POST)
    public ResponseEntity<Object> uploadExcel(@RequestParam("file") MultipartFile multipartFile,
                                              @RequestParam("BatchNo") String batchNo) throws IOException {
        //     InputStream stream = multipartFile.getInputStream();
        List<Deals> deals = new LinkedList<>();
        String filename = multipartFile.getOriginalFilename();
        if (!filename.endsWith(".xlsx")) {
            throw new InvalidException("Invalid file type. Only Excel file accepted");
        } else {
            if (!excelHelper.checkIfColumnsAreinOrder(multipartFile)) {
                return new ResponseEntity<>("Invalid Columns", HttpStatus.BAD_REQUEST);
            } else {
                dealsService.insertDeal(multipartFile);
                return new ResponseEntity<>("Posted Successfully", HttpStatus.OK);
            }
        }
    }

    @ManagedOperation(description = "Get Deals pagewise")
    @RequestMapping(value = "/getDealsPageWise", method = RequestMethod.GET)
    public ResponseEntity<Object> getDealsWithPages(
            @RequestParam(defaultValue = "09/01/2021") String DateFrom,
            @RequestParam(defaultValue = "10/30/2021") String DateTo,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(defaultValue = "dealid") String sortBy,
            @RequestParam(defaultValue = "asc") String OrderBy,
            @RequestParam(defaultValue = "no") String download
    ) {
        List<DealResponse> ProductResponseList = dealsService.getProductWithPage(pageNo, pageSize, sortBy, OrderBy, DateFrom, DateTo);
        if (download.equals("yes")) {
            return ResponseEntity.ok().headers(new HttpHeaders())
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(new InputStreamResource(dealsService.loadFile(ProductResponseList)));
        } else if (download.equals("no")) {
            if (!ProductResponseList.isEmpty()) {
                return new ResponseEntity<Object>(ProductResponseList, HttpStatus.OK);
            } else {
                return new ResponseEntity<Object>("Not Found", HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<Object>("Error", HttpStatus.BAD_REQUEST);
    }

    @ManagedOperation(description = "Get Deals pagewise")
    @RequestMapping(value = "/exportToExcel", method = RequestMethod.GET)
    public ResponseEntity<Object> downloadFile() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=DealLists.xlsx");
        return ResponseEntity.ok().headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(new InputStreamResource(dealsService.loadFile(null)));
    }
}
