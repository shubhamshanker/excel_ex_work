package com.example.excel_ex_work.service.impl;

import com.example.excel_ex_work.dto.AdvanceDetailDto;
import com.example.excel_ex_work.dto.AdvanceSummaryDto;
import com.example.excel_ex_work.dto.ExcelUploadResponseDto;
import com.example.excel_ex_work.dto.PilotDetailDto;
import com.example.excel_ex_work.service.ExcelService;
import com.example.excel_ex_work.service.UtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ExcelServiceImpl implements ExcelService {

    private final UtilService utilService;

    @Override
    public ExcelUploadResponseDto bulkuploadExcel(MultipartFile multipartFile, Date startDate, Date endDate) throws Exception {
        List<PilotDetailDto> pilotPayoutList = utilService.convertToUploadPilotDetailTransactionFromInputFile(multipartFile, Arrays.asList("pilotCode"));
        List<AdvanceSummaryDto> advanceSummaryDtoList = utilService.convertToUploadAdvanceSummaryTransactionFromInputFile(multipartFile, Arrays.asList("pilotCode"));
        List<AdvanceDetailDto> advanceDetailDtoList = utilService.convertToUploadAdvanceDetailTransactionFromInputFile(multipartFile, Arrays.asList("pilotCode"));


        ExcelUploadResponseDto excelUploadResponseDto = null;
        return excelUploadResponseDto;
    }
}
