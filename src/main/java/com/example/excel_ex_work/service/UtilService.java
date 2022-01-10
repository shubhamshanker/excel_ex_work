package com.example.excel_ex_work.service;

import com.example.excel_ex_work.dto.PilotDetailDto;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UtilService {
    List<PilotDetailDto> convertToUploadTransactionFromInputFile(MultipartFile inputFile, List<String> mandatoryColumns) throws Exception;

    Workbook convertFileToWorkbook(List<String> header, MultipartFile inputFile, Boolean convertHeaders)
            throws Exception;

    Sheet removeEmptyRowsAndColumnsFromExcelFile(Sheet sheet) throws Exception;

    <T> Boolean isFileFormatValid(Sheet sheet, Class<T> schemaClass) throws Exception;
}
