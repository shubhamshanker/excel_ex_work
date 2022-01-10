package com.example.excel_ex_work.service.impl;

import com.example.excel_ex_work.dto.AdvanceDetailDto;
import com.example.excel_ex_work.dto.AdvanceSummaryDto;
import com.example.excel_ex_work.dto.ExcelReadErrorWrapper;
import com.example.excel_ex_work.dto.PilotDetailDto;
import com.example.excel_ex_work.exception.PilotPayoutError;
import com.example.excel_ex_work.service.UtilService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


@Slf4j
@Service
public class UtilServiceImpl implements UtilService {

    @Override
    public List<PilotDetailDto> convertToUploadPilotDetailTransactionFromInputFile(MultipartFile inputFile, List<String> mandatoryColumns) throws Exception {
        Workbook workbook = convertFileToWorkbook(new ArrayList<>(), inputFile, false);
        Sheet sheet = workbook.getSheet("pilot_payout");
        sheet = removeEmptyRowsAndColumnsFromExcelFile(sheet);
        if (!isFileFormatValid(sheet, mandatoryColumns, PilotDetailDto.class)) {
            throw new PilotPayoutError("Invalid file format for banking transaction");
        }
        List<ExcelReadErrorWrapper> errorList = new ArrayList<>();
        log.info("sheet lastrownum: {}", sheet.getLastRowNum());
        if(sheet.getLastRowNum()>100){
            throw new PilotPayoutError("Excel size limit is less than or equals to 100");
        }
        Collection<PilotDetailDto> collection = GenericReportReaderImpl.read
                (sheet, PilotDetailDto.class, "dd-MMM-yy HH:mm:ss", "HH:mm:ss", errorList);

        return new ArrayList<>(collection);
    }

    @Override
    public List<AdvanceDetailDto> convertToUploadAdvanceDetailTransactionFromInputFile(MultipartFile inputFile, List<String> mandatoryColumns) throws Exception {
        Workbook workbook = convertFileToWorkbook(new ArrayList<>(), inputFile, false);
        Sheet sheet = workbook.getSheet("advance_details");
        sheet = removeEmptyRowsAndColumnsFromExcelFile(sheet);
        if (!isFileFormatValid(sheet, mandatoryColumns, AdvanceDetailDto.class)) {
            throw new PilotPayoutError("Invalid file format for banking transaction");
        }
        List<ExcelReadErrorWrapper> errorList = new ArrayList<>();
        log.info("sheet lastrownum: {}", sheet.getLastRowNum());
        if(sheet.getLastRowNum()>100){
            throw new PilotPayoutError("Excel size limit is less than or equals to 100");
        }
        Collection<AdvanceDetailDto> collection = GenericReportReaderImpl.read
                (sheet, AdvanceDetailDto.class, "dd-MMM-yy HH:mm:ss", "HH:mm:ss", errorList);

        return new ArrayList<>(collection);
    }

    @Override
    public List<AdvanceSummaryDto> convertToUploadAdvanceSummaryTransactionFromInputFile(MultipartFile inputFile, List<String> mandatoryColumns) throws Exception {
        Workbook workbook = convertFileToWorkbook(new ArrayList<>(), inputFile, false);
        Sheet sheet = workbook.getSheet("advance_summary");
        sheet = removeEmptyRowsAndColumnsFromExcelFile(sheet);
        if (!isFileFormatValid(sheet, mandatoryColumns, AdvanceSummaryDto.class)) {
            throw new PilotPayoutError("Invalid file format for banking transaction");
        }
        List<ExcelReadErrorWrapper> errorList = new ArrayList<>();
        log.info("sheet lastrownum: {}", sheet.getLastRowNum());
        if(sheet.getLastRowNum()>100){
            throw new PilotPayoutError("Excel size limit is less than or equals to 100");
        }
        Collection<AdvanceSummaryDto> collection = GenericReportReaderImpl.read
                (sheet, AdvanceSummaryDto.class, "dd-MMM-yy HH:mm:ss", "HH:mm:ss", errorList);

        return new ArrayList<>(collection);
    }

    @Override
    public Workbook convertFileToWorkbook(List<String> header, MultipartFile inputFile, Boolean convertHeaders)
            throws Exception {
        try (Workbook workbook = WorkbookFactory.create(inputFile.getInputStream())) {
            if (convertHeaders) {
                int sheetNumber = 0;

                Sheet sheet = workbook.getSheetAt(sheetNumber);
                Row row = sheet.getRow(0);
                for (int i = 0; i < header.size(); i++) {
                    row.getCell(i).setCellValue(header.get(i));
                }

            }
            return workbook;
        } catch (Exception ex) {
            log.error("Exception: {}", ex);
            throw new PilotPayoutError("Invalid File Format");
        }
    }

    @Override
    public Sheet removeEmptyRowsAndColumnsFromExcelFile(Sheet sheet) throws Exception {

        try {
            Row firstRow = sheet.getRow(0);
            if (firstRow != null) {
                int rowSize = firstRow.getLastCellNum();
                int colSize = sheet.getLastRowNum(); //gives the last row number excluding headers row
                boolean[] columnToDelete = new boolean[rowSize];
                Arrays.fill(columnToDelete, Boolean.TRUE);

                for (int r = colSize; r >= 0; r--) {
                    Row row = sheet.getRow(r);

                    // if no row exists here; then nothing to do; next!
                    if (row == null)
                        continue;

                    int lastColumn = row.getLastCellNum();
                    boolean rowToDelete = true;
                    if (lastColumn > -1) {
                        for (int x = 0; x < lastColumn; x++) {
                            if (columnToDelete[x] || rowToDelete) {
                                Cell cell = row.getCell(x);
                                if (cell != null && Cell.CELL_TYPE_BLANK != cell.getCellType()) {
                                    if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
                                        String cellTrimValue = cell.getStringCellValue().trim();
                                        if (!cellTrimValue.isEmpty()) {
                                            rowToDelete = false;
                                            columnToDelete[x] = false;
//                                        break;
                                        }
                                    } else {
                                        rowToDelete = false;
                                        columnToDelete[x] = false;
//                                    break;
                                    }
                                }
                            }
                        }
                    }

                    if (rowToDelete) {
                        if (r == sheet.getLastRowNum()) {
                            sheet.removeRow(row);
                        } else {
                            sheet.removeRow(row);
                            for (int j = r + 1; j <= sheet.getLastRowNum(); j++) {
                                Row rowToShift = sheet.getRow(j);
                                rowToShift.setRowNum(j - 1);
                            }
                        }
                    }
                }
                colSize = sheet.getLastRowNum();
                for (int i = rowSize - 1; i >= 0; i--) {
                    if (columnToDelete[i] == true) {
                        for (int r = colSize; r >= 0; r--) {
                            Row row = sheet.getRow(r);
                            Cell cell = row.getCell(i);
                            if (i == row.getLastCellNum() - 1) {
                                row.removeCell(cell);
                            } else {
                                throw new PilotPayoutError("Empty column in between table");
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.error("Exception {}", ex);
            throw new PilotPayoutError("Invalid File Format!");
        }
        return sheet;
    }

    public <T> Boolean isFileFormatValid(Sheet sheet, List<String> mandatoryColumns, Class<T> schemaClass) {
        int headerRowNum = 0;
        List<String> sheetFieldName = new ArrayList<>();
        Row row = sheet.getRow(headerRowNum);
        for (Cell cell : row) {
            if (Cell.CELL_TYPE_STRING == cell.getCellType() && "NULL".equalsIgnoreCase(cell.getStringCellValue())) {
                continue;
            }
            sheetFieldName.add(sheet.getRow(headerRowNum).getCell(cell.getColumnIndex())
                    .getStringCellValue().replaceAll("\\s+", ""));
        }

        if (sheetFieldName.containsAll(mandatoryColumns)) {
            for (String columns : mandatoryColumns) {
                try {
                    new PropertyDescriptor(columns, schemaClass).getWriteMethod();
                } catch (IntrospectionException ex) {
                    log.error("Exception: ", ex);
                    throw new PilotPayoutError("Invalid column name: " + columns);
                } catch (Exception ex) {
                    log.error("Exception: {}", ex);
                    return false;
                }
            }

        } else {
            log.error("Excel does not contains all the mandatory columns : Mandatory columns are {}", mandatoryColumns.toString());
            throw new PilotPayoutError("Excel does not contains all the mandatory columns");
        }
        return true;
    }

    @Override
    public <T> Boolean isFileFormatValid(Sheet sheet, Class<T> schemaClass) throws Exception {

        int headerRowNum = 0;
        log.debug("Header Row number: {}", headerRowNum);
        String sheetFieldName = null;
        try {
            Row row = sheet.getRow(headerRowNum);
            for (Cell cell : row) {
                if (Cell.CELL_TYPE_STRING == cell.getCellType() && "NULL".equalsIgnoreCase(cell.getStringCellValue())) {
                    continue;
                }
                sheetFieldName = sheet.getRow(headerRowNum).getCell(cell.getColumnIndex()).getStringCellValue();
                Method writeMethod = new PropertyDescriptor(sheetFieldName, schemaClass).getWriteMethod();
            }
        } catch (IntrospectionException ex) {
            log.error("Exception: {}", ex);
            throw new PilotPayoutError("Invalid column name: " + sheetFieldName);
        } catch (Exception ex) {
            log.error("Exception: {}", ex);
            return false;
        }
        return true;
    }
}
