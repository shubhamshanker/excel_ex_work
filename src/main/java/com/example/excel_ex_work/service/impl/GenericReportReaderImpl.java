package com.example.excel_ex_work.service.impl;

import com.example.excel_ex_work.dto.ExcelReadErrorWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class GenericReportReaderImpl {


    public static <T> Collection<T> read(Sheet sheet, Class<T> schemaClass, String dateAndTimeFormat, String timeOnlyFormat,
                                         List<ExcelReadErrorWrapper> errorList) {
        Collection<T> collection = new ArrayList();

        DateTimeFormatter dateAndTimeFormatter = DateTimeFormat.forPattern(dateAndTimeFormat);
        DateTimeFormatter timeOnlyFormatter = DateTimeFormat.forPattern(timeOnlyFormat);
        Pattern geoJsonPointPattern = Pattern.compile("Point \\[x=([0-9\\.]*?), y=([0-9\\.]*?)\\]");

        int headerRowNum = 0;
        log.debug("Header Row number: {}", headerRowNum);

        for (Row row : sheet) {
            if (row.getRowNum() != headerRowNum) {
                log.debug("---------------- Row {} ----------------", row.getRowNum());
                try {
                    T object = schemaClass.newInstance();

                    for (Cell cell : row) {
                        String sheetFieldName = null;
                        try {
                            if (Cell.CELL_TYPE_STRING == cell.getCellType() && "NULL".equalsIgnoreCase(cell.getStringCellValue())) {
                                continue;
                            }
                            sheetFieldName = sheet.getRow(headerRowNum).getCell(cell.getColumnIndex()).getStringCellValue();
                            Method writeMethod = new PropertyDescriptor(sheetFieldName, schemaClass).getWriteMethod();

                            Class<?> fieldType = writeMethod.getParameterTypes()[0];
                            if (Cell.CELL_TYPE_BLANK != cell.getCellType() && Cell.CELL_TYPE_ERROR != cell.getCellType()) {
                                if (fieldType.isAssignableFrom(String.class)) {
                                    if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
                                        DecimalFormat df = new DecimalFormat("#");
                                        df.setMaximumFractionDigits(3);
                                        writeMethod.invoke(object, df.format(cell.getNumericCellValue()));
                                    } else {
                                        writeMethod.invoke(object, cell.getStringCellValue());
                                    }
                                } else if (fieldType.isAssignableFrom(Integer.class) || fieldType.isAssignableFrom(int.class)) {
                                    if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
                                        writeMethod.invoke(object, (int) cell.getNumericCellValue());
                                    } else {
                                        writeMethod.invoke(object, Integer.parseInt(cell.getStringCellValue()));
                                    }
                                } else if (fieldType.isAssignableFrom(Long.class) || fieldType.isAssignableFrom(long.class)) {
                                    if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
                                        writeMethod.invoke(object, (long) cell.getNumericCellValue());
                                    } else {
                                        writeMethod.invoke(object, Long.parseLong(cell.getStringCellValue()));
                                    }
                                } else if (fieldType.isAssignableFrom(Double.class) || fieldType.isAssignableFrom(double.class)) {
                                    if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
                                        writeMethod.invoke(object, cell.getNumericCellValue());
                                    } else {
                                        writeMethod.invoke(object, Double.parseDouble(cell.getStringCellValue()));
                                    }
                                } else if (fieldType.isAssignableFrom(Boolean.class) || fieldType.isAssignableFrom(boolean.class)) {
                                    if (Cell.CELL_TYPE_BOOLEAN == cell.getCellType()) {
                                        writeMethod.invoke(object, cell.getBooleanCellValue());
                                    } else {
                                        if ("TRUE".equalsIgnoreCase(cell.getStringCellValue())) {
                                            writeMethod.invoke(object, true);
                                        } else if ("FALSE".equalsIgnoreCase(cell.getStringCellValue())) {
                                            writeMethod.invoke(object, false);
                                        }

                                    }
                                } else if (fieldType.isAssignableFrom(DateTime.class)) {
                                    if (Cell.CELL_TYPE_NUMERIC == cell.getCellType() && DateUtil.isCellDateFormatted(cell)) {
                                        writeMethod.invoke(
                                                object,
                                                new DateTime(cell.getDateCellValue())
                                                        .withZoneRetainFields(DateTimeZone.forOffsetHoursMinutes(5, 30))
                                        );
                                    } else {
                                        writeMethod.invoke(
                                                object,
                                                dateAndTimeFormatter.parseDateTime(cell.getStringCellValue())
                                                        .withZoneRetainFields(DateTimeZone.forOffsetHoursMinutes(5, 30))
                                        );
                                    }
                                } else if (fieldType.isAssignableFrom(LocalTime.class)) {
                                    if (Cell.CELL_TYPE_NUMERIC == cell.getCellType() && DateUtil.isCellDateFormatted(cell)) {
                                        Date d1 = cell.getDateCellValue();

                                        writeMethod.invoke(
                                                object,
                                                new LocalTime(d1.getHours(), d1.getMinutes(), d1.getSeconds())
                                        );
                                    } else {
                                        writeMethod.invoke(
                                                object,
                                                timeOnlyFormatter.parseLocalTime(cell.getStringCellValue())
                                        );
                                    }
                                } else if (fieldType.isEnum()) {
                                    Object[] enumConstants = fieldType.getEnumConstants();
                                    for (Object enumConstant : enumConstants) {
                                        if (cell.getStringCellValue().equals(enumConstant.toString())) {
                                            writeMethod.invoke(object, enumConstant);
                                        }
                                    }

                                }
                            }
                            else{
                                log.warn("Blank cell!");
                            }
                        } catch (Throwable th) {
                            if(null!=errorList) {
                                ExcelReadErrorWrapper errorWrapper = new ExcelReadErrorWrapper(th, object, sheetFieldName);
                                errorList.add(errorWrapper);
                            }
                            log.warn("Cell : {}", cell, th);
                        }
                    }
                    collection.add(object);
                } catch (Throwable th) {
                    log.warn("", th);
                }
            }
        }
        return collection;
    }

    public static <T> Collection<T> read(Sheet sheet, Class<T> schemaClass, String dateAndTimeFormat, String timeOnlyFormat,
                                         List<ExcelReadErrorWrapper> errorList, List<String> mandatoryColumns) {
        Collection<T> collection = new ArrayList();

        DateTimeFormatter dateAndTimeFormatter = DateTimeFormat.forPattern(dateAndTimeFormat);
        DateTimeFormatter timeOnlyFormatter = DateTimeFormat.forPattern(timeOnlyFormat);
        Pattern geoJsonPointPattern = Pattern.compile("Point \\[x=([0-9\\.]*?), y=([0-9\\.]*?)\\]");

        int headerRowNum = 0;
        log.debug("Header Row number: {}", headerRowNum);

        for (Row row : sheet) {
            if (row.getRowNum() != headerRowNum) {
                log.debug("---------------- Row {} ----------------", row.getRowNum());
                try {
                    T object = schemaClass.newInstance();

                    for (Cell cell : row) {
                        String sheetFieldName = null;
                        try {
                            if (Cell.CELL_TYPE_STRING == cell.getCellType() && "NULL".equalsIgnoreCase(cell.getStringCellValue())) {
                                continue;
                            }
                            sheetFieldName = sheet.getRow(headerRowNum).getCell(cell.getColumnIndex()).getStringCellValue().replaceAll("\\s+", "");
                            if (mandatoryColumns.contains(sheetFieldName)) {
                                Method writeMethod = new PropertyDescriptor(sheetFieldName, schemaClass).getWriteMethod();

                                Class<?> fieldType = writeMethod.getParameterTypes()[0];
                                if (Cell.CELL_TYPE_BLANK != cell.getCellType() && Cell.CELL_TYPE_ERROR != cell.getCellType()) {
                                    if (fieldType.isAssignableFrom(String.class)) {
                                        if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
                                            DecimalFormat df = new DecimalFormat("#");
                                            df.setMaximumFractionDigits(3);
                                            writeMethod.invoke(object, df.format(cell.getNumericCellValue()));
                                        } else {
                                            writeMethod.invoke(object, cell.getStringCellValue());
                                        }
                                    } else if (fieldType.isAssignableFrom(Integer.class) || fieldType.isAssignableFrom(int.class)) {
                                        if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
                                            writeMethod.invoke(object, (int) cell.getNumericCellValue());
                                        } else {
                                            writeMethod.invoke(object, Integer.parseInt(cell.getStringCellValue()));
                                        }
                                    } else if (fieldType.isAssignableFrom(Long.class) || fieldType.isAssignableFrom(long.class)) {
                                        if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
                                            writeMethod.invoke(object, (long) cell.getNumericCellValue());
                                        } else {
                                            writeMethod.invoke(object, Long.parseLong(cell.getStringCellValue()));
                                        }
                                    } else if (fieldType.isAssignableFrom(Double.class) || fieldType.isAssignableFrom(double.class)) {
                                        if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
                                            writeMethod.invoke(object, cell.getNumericCellValue());
                                        } else {
                                            writeMethod.invoke(object, Double.parseDouble(cell.getStringCellValue()));
                                        }
                                    } else if (fieldType.isAssignableFrom(Boolean.class) || fieldType.isAssignableFrom(boolean.class)) {
                                        if (Cell.CELL_TYPE_BOOLEAN == cell.getCellType()) {
                                            writeMethod.invoke(object, cell.getBooleanCellValue());
                                        } else {
                                            if ("TRUE".equalsIgnoreCase(cell.getStringCellValue())) {
                                                writeMethod.invoke(object, true);
                                            } else if ("FALSE".equalsIgnoreCase(cell.getStringCellValue())) {
                                                writeMethod.invoke(object, false);
                                            }

                                        }
                                    } else if (fieldType.isAssignableFrom(DateTime.class)) {
                                        if (Cell.CELL_TYPE_NUMERIC == cell.getCellType() && DateUtil.isCellDateFormatted(cell)) {
                                            writeMethod.invoke(
                                                    object,
                                                    new DateTime(cell.getDateCellValue())
                                                            .withZoneRetainFields(DateTimeZone.forOffsetHoursMinutes(5, 30))
                                            );
                                        } else {
                                            writeMethod.invoke(
                                                    object,
                                                    dateAndTimeFormatter.parseDateTime(cell.getStringCellValue())
                                                            .withZoneRetainFields(DateTimeZone.forOffsetHoursMinutes(5, 30))
                                            );
                                        }
                                    } else if (fieldType.isAssignableFrom(LocalTime.class)) {
                                        if (Cell.CELL_TYPE_NUMERIC == cell.getCellType() && DateUtil.isCellDateFormatted(cell)) {
                                            Date d1 = cell.getDateCellValue();

                                            writeMethod.invoke(
                                                    object,
                                                    new LocalTime(d1.getHours(), d1.getMinutes(), d1.getSeconds())
                                            );
                                        } else {
                                            writeMethod.invoke(
                                                    object,
                                                    timeOnlyFormatter.parseLocalTime(cell.getStringCellValue())
                                            );
                                        }
                                    } else if (fieldType.isEnum()) {
                                        Object[] enumConstants = fieldType.getEnumConstants();
                                        for (Object enumConstant : enumConstants) {
                                            if (cell.getStringCellValue().equals(enumConstant.toString())) {
                                                writeMethod.invoke(object, enumConstant);
                                            }
                                        }
                                    }
                                } else {
                                    log.warn("Blank cell!");
                                }
                            }
                        } catch (Throwable th) {
                            if(null!=errorList) {
                                ExcelReadErrorWrapper errorWrapper = new ExcelReadErrorWrapper(th, object, sheetFieldName);
                                errorList.add(errorWrapper);
                            }
                            log.warn("Cell : {}", cell, th);
                        }
                    }
                    collection.add(object);
                } catch (Throwable th) {
                    log.warn("", th);
                }
            }
        }
        return collection;
    }

    public static <T> Collection<T> read(Sheet sheet, Class<T> schemaClass, List<ExcelReadErrorWrapper> errorList, List<String> mandatoryColumns) {
        return read(sheet, schemaClass, "dd/MM/yyyy HH:mm:ss", "HH:mm:ss", errorList, mandatoryColumns);
    }

    public static <T> Collection<T> read(Sheet sheet, Class<T> schemaClass, List<ExcelReadErrorWrapper> errorList) {
        return read(sheet, schemaClass, "dd/MM/yyyy HH:mm:ss", "HH:mm:ss", errorList);
    }

    public static <T> Collection<T> readExcel(Sheet sheet, Class<T> schemaClass, List<ExcelReadErrorWrapper> errorList) {
        return read(sheet, schemaClass, "dd-MMM-yy HH:mm:ss", "HH:mm:ss", errorList);
    }
}
