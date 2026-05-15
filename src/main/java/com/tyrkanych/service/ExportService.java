package com.tyrkanych.service;

import com.tyrkanych.entity.Match;
import com.tyrkanych.entity.Message;
import com.tyrkanych.entity.User;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class ExportService {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    // ══ EXCEL ══
    public File exportToExcel(User user, List<Match> matches,
            List<Message> messages, String savePath) throws IOException {

        Workbook workbook = new XSSFWorkbook();

        // Стилі
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle titleStyle = createTitleStyle(workbook);

        // Аркуш 1 — Профіль
        Sheet profileSheet = workbook.createSheet("Профіль");
        createProfileSheet(profileSheet, user, titleStyle, headerStyle, dataStyle);

        // Аркуш 2 — Збіги
        Sheet matchesSheet = workbook.createSheet("Збіги");
        createMatchesSheet(matchesSheet, matches, user, titleStyle, headerStyle, dataStyle);

        // Аркуш 3 — Повідомлення
        Sheet messagesSheet = workbook.createSheet("Повідомлення");
        createMessagesSheet(messagesSheet, messages, user, titleStyle, headerStyle, dataStyle);

        // Зберігаємо файл
        File file = new File(savePath);
        file.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        }
        workbook.close();
        return file;
    }

    private void createProfileSheet(Sheet sheet, User user,
            CellStyle titleStyle, CellStyle headerStyle, CellStyle dataStyle) {

        int row = 0;

        // Заголовок
        Row titleRow = sheet.createRow(row++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("HeartSync — Профіль користувача");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 3));

        row++; // пустий рядок

        // Дані профілю
        String[][] data = {
                {"Ім'я", user.getName() != null ? user.getName() : "—"},
                {"Email", user.getEmail() != null ? user.getEmail() : "—"},
                {"Стать", user.getGender() != null ? user.getGender() : "—"},
                {"Місто", user.getCity() != null ? user.getCity() : "—"},
                {"Про себе", user.getBio() != null ? user.getBio() : "—"},
                {"Дата реєстрації", LocalDateTime.now().format(FORMATTER)}
        };

        for (String[] pair : data) {
            Row dataRow = sheet.createRow(row++);
            Cell labelCell = dataRow.createCell(0);
            labelCell.setCellValue(pair[0]);
            labelCell.setCellStyle(headerStyle);

            Cell valueCell = dataRow.createCell(1);
            valueCell.setCellValue(pair[1]);
            valueCell.setCellStyle(dataStyle);
        }

        sheet.setColumnWidth(0, 5000);
        sheet.setColumnWidth(1, 10000);
    }

    private void createMatchesSheet(Sheet sheet, List<Match> matches, User user,
            CellStyle titleStyle, CellStyle headerStyle, CellStyle dataStyle) {

        int row = 0;

        Row titleRow = sheet.createRow(row++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("HeartSync — Збіги");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 3));

        row++;

        // Заголовки таблиці
        Row headerRow = sheet.createRow(row++);
        String[] headers = {"#", "Партнер ID", "Сумісність", "Дата"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Дані
        int num = 1;
        for (Match match : matches) {
            Row dataRow = sheet.createRow(row++);
            Long partnerId = match.getUser1Id().equals(user.getId())
                    ? match.getUser2Id() : match.getUser1Id();

            Cell c0 = dataRow.createCell(0);
            c0.setCellValue(num++);
            c0.setCellStyle(dataStyle);

            Cell c1 = dataRow.createCell(1);
            c1.setCellValue(partnerId);
            c1.setCellStyle(dataStyle);

            Cell c2 = dataRow.createCell(2);
            c2.setCellValue(match.getCompatibilityScore() != null
                    ? match.getCompatibilityScore() + "%" : "—");
            c2.setCellStyle(dataStyle);

            Cell c3 = dataRow.createCell(3);
            c3.setCellValue(LocalDateTime.now().format(FORMATTER));
            c3.setCellStyle(dataStyle);
        }

        if (matches.isEmpty()) {
            Row emptyRow = sheet.createRow(row);
            Cell emptyCell = emptyRow.createCell(0);
            emptyCell.setCellValue("Збігів поки немає");
            emptyCell.setCellStyle(dataStyle);
        }

        sheet.setColumnWidth(0, 2000);
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 4000);
        sheet.setColumnWidth(3, 6000);
    }

    private void createMessagesSheet(Sheet sheet, List<Message> messages, User user,
            CellStyle titleStyle, CellStyle headerStyle, CellStyle dataStyle) {

        int row = 0;

        Row titleRow = sheet.createRow(row++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("HeartSync — Повідомлення");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 3));

        row++;

        Row headerRow = sheet.createRow(row++);
        String[] headers = {"#", "Від кого", "Кому", "Повідомлення"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int num = 1;
        for (Message msg : messages) {
            Row dataRow = sheet.createRow(row++);

            Cell c0 = dataRow.createCell(0);
            c0.setCellValue(num++);
            c0.setCellStyle(dataStyle);

            Cell c1 = dataRow.createCell(1);
            c1.setCellValue(msg.getSenderId().equals(user.getId()) ? "Я" : "Партнер");
            c1.setCellStyle(dataStyle);

            Cell c2 = dataRow.createCell(2);
            c2.setCellValue(msg.getSenderId().equals(user.getId()) ? "Партнер" : "Я");
            c2.setCellStyle(dataStyle);

            Cell c3 = dataRow.createCell(3);
            c3.setCellValue(msg.getMessageText());
            c3.setCellStyle(dataStyle);
        }

        sheet.setColumnWidth(0, 2000);
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 4000);
        sheet.setColumnWidth(3, 15000);
    }

    // ══ PDF ══
    public File exportToPdf(User user, List<Match> matches,
            List<Message> messages, String savePath) throws IOException {

        PDDocument document = new PDDocument();

        // Сторінка 1 — Профіль
        addProfilePage(document, user, matches, messages);

        // Сторінка 2 — Збіги
        if (!matches.isEmpty()) {
            addMatchesPage(document, matches, user);
        }

        File file = new File(savePath);
        file.getParentFile().mkdirs();
        document.save(file);
        document.close();
        return file;
    }

    private void addProfilePage(PDDocument document, User user,
            List<Match> matches, List<Message> messages) throws IOException {

        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDPageContentStream cs = new PDPageContentStream(document, page);
        PDType1Font boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        PDType1Font regularFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

        float margin = 50;
        float y = 780;
        float lineHeight = 22;

        // Заголовок
        cs.beginText();
        cs.setFont(boldFont, 20);
        cs.newLineAtOffset(margin, y);
        cs.showText("HeartSync - Звіт користувача");
        cs.endText();
        y -= lineHeight * 2;

        // Дата
        cs.beginText();
        cs.setFont(regularFont, 10);
        cs.newLineAtOffset(margin, y);
        cs.showText("Згенеровано: " + LocalDateTime.now().format(FORMATTER));
        cs.endText();
        y -= lineHeight * 2;

        // Профіль
        cs.beginText();
        cs.setFont(boldFont, 14);
        cs.newLineAtOffset(margin, y);
        cs.showText("Профіль:");
        cs.endText();
        y -= lineHeight;

        String[][] profileData = {
                {"Ім'я:", user.getName() != null ? user.getName() : "—"},
                {"Email:", user.getEmail() != null ? user.getEmail() : "—"},
                {"Стать:", user.getGender() != null ? user.getGender() : "—"},
                {"Місто:", user.getCity() != null ? user.getCity() : "—"},
        };

        for (String[] pair : profileData) {
            cs.beginText();
            cs.setFont(boldFont, 11);
            cs.newLineAtOffset(margin, y);
            cs.showText(pair[0]);
            cs.endText();

            cs.beginText();
            cs.setFont(regularFont, 11);
            cs.newLineAtOffset(margin + 100, y);
            cs.showText(pair[1]);
            cs.endText();
            y -= lineHeight;
        }

        y -= lineHeight;

        // Статистика
        cs.beginText();
        cs.setFont(boldFont, 14);
        cs.newLineAtOffset(margin, y);
        cs.showText("Статистика:");
        cs.endText();
        y -= lineHeight;

        String[][] statsData = {
                {"Збіги:", String.valueOf(matches.size())},
                {"Повідомлення:", String.valueOf(messages.size())},
        };

        for (String[] pair : statsData) {
            cs.beginText();
            cs.setFont(boldFont, 11);
            cs.newLineAtOffset(margin, y);
            cs.showText(pair[0]);
            cs.endText();

            cs.beginText();
            cs.setFont(regularFont, 11);
            cs.newLineAtOffset(margin + 100, y);
            cs.showText(pair[1]);
            cs.endText();
            y -= lineHeight;
        }

        cs.close();
    }

    private void addMatchesPage(PDDocument document, List<Match> matches,
            User user) throws IOException {

        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDPageContentStream cs = new PDPageContentStream(document, page);
        PDType1Font boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        PDType1Font regularFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

        float margin = 50;
        float y = 780;
        float lineHeight = 22;

        cs.beginText();
        cs.setFont(boldFont, 16);
        cs.newLineAtOffset(margin, y);
        cs.showText("Список збігів:");
        cs.endText();
        y -= lineHeight * 2;

        int num = 1;
        for (Match match : matches) {
            if (y < 50) break;
            Long partnerId = match.getUser1Id().equals(user.getId())
                    ? match.getUser2Id() : match.getUser1Id();

            cs.beginText();
            cs.setFont(regularFont, 11);
            cs.newLineAtOffset(margin, y);
            cs.showText(num++ + ". Партнер ID: " + partnerId +
                    " | Сумісність: " +
                    (match.getCompatibilityScore() != null
                            ? match.getCompatibilityScore() + "%" : "—"));
            cs.endText();
            y -= lineHeight;
        }

        cs.close();
    }

    // ══ Стилі Excel ══
    private CellStyle createHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.VIOLET.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDataStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createTitleStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
}