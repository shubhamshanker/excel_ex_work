package com.example.excel_ex_work.service.impl;

import com.example.excel_ex_work.dto.ExcelUploadResponseDto;
import com.example.excel_ex_work.service.ExcelService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Service
public class ExcelServiceImpl implements ExcelService {


    @Override
    public ExcelUploadResponseDto bulkuploadExcel(MultipartFile multipartFile, Date startDate, Date endDate) {

    }
}
