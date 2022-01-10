package com.example.excel_ex_work.service;

import com.example.excel_ex_work.dto.ExcelUploadResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

public interface ExcelService {

    ExcelUploadResponseDto bulkuploadExcel(MultipartFile multipartFile, Date startDate, Date endDate) throws Exception;

}
