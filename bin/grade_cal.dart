import 'dart:io';
import 'package:excel/excel.dart';

void main() {
  final inputFile = File('student.xlsx');

  print('Looking for file at: ${inputFile.absolute.path}');
  if (!inputFile.existsSync()) {
    print('❌ student.xlsx not found');
    return;
  }

  try {
    final bytes = inputFile.readAsBytesSync();
    
    // Create new Excel file
    final newExcel = Excel.createExcel();
    final newSheet = newExcel['Grades'];
    
    // Try to read old file
    Excel oldExcel;
    try {
      oldExcel = Excel.decodeBytes(bytes);
    } catch (e) {
      print('❌ Cannot read Excel file. Please save it as a new .xlsx file.');
      print('💡 Open in Excel → Save As → Excel Workbook (.xlsx)');
      return;
    }

    final oldSheetName = oldExcel.tables.keys.first;
    final oldSheet = oldExcel.tables[oldSheetName]!;

    // Add headers
    newSheet.appendRow([
      TextCellValue('Student ID'),
      TextCellValue('Student Name'),
      TextCellValue('Marks'),
      TextCellValue('Grade'),
    ]);

    // Process each student
    int processed = 0;
    for (int i = 1; i < oldSheet.rows.length; i++) {
      final row = oldSheet.rows[i];
      if (row.length < 3) continue;

      final id = row[0]?.value?.toString() ?? '';
      final name = row[1]?.value?.toString() ?? '';
      final marksStr = row[2]?.value?.toString() ?? '0';
      final marks = double.tryParse(marksStr) ?? 0;
      final grade = calculateGrade(marks);

      newSheet.appendRow([
        TextCellValue(id),
        TextCellValue(name),
        TextCellValue(marksStr),
        TextCellValue(grade),
      ]);
      processed++;
    }

    // Save new file
    final outputBytes = newExcel.encode();
    if (outputBytes == null) {
      print('❌ Failed to create output file');
      return;
    }

    File('student_with_grades.xlsx')
      ..createSync()
      ..writeAsBytesSync(outputBytes);

    print('✅ File saved: student_with_grades.xlsx');
    print('✅ Processed $processed students');
    print('\nGrade Distribution:');
    print('A (90-100), B (80-89), C (70-79), D (60-69), F (0-59)');
  } catch (e) {
    print('❌ Error: $e');
  }
}

String calculateGrade(double marks) {
  if (marks >= 90) return 'A';
  if (marks >= 80) return 'B';
  if (marks >= 70) return 'C';
  if (marks >= 60) return 'D';
  return 'F';
}