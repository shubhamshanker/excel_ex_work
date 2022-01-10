package com.example.excel_ex_work.controller;

import com.example.excel_ex_work.dto.ExcelUploadResponseDto;
import com.example.excel_ex_work.service.ExcelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "api/v1")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ExcelController {

    private final ExcelService excelService;

    @PostMapping(value = "/upload")
    public ExcelUploadResponseDto excepload(@RequestParam MultipartFile multipartFile, @RequestParam(value = "startDate")
                                            @DateTimeFormat(pattern = "dd.MM.yyyy") Date startDate,
                                            @RequestParam(value = "endDate") @DateTimeFormat(pattern = "dd.MM.yyyy") Date endDate) {

        ExcelUploadResponseDto excelUploadResponseDto = null;
        try {
            excelUploadResponseDto = excelService.bulkuploadExcel(multipartFile, startDate, endDate);
        } catch (Exception e) {
            log.error("Excel not able to be uploaded.", e);
        } finally {
            if(excelUploadResponseDto == null)
            {
                log.error("Unknown error experienced on pilot payout upload");
            }
            return excelUploadResponseDto;
        }
    }
}
