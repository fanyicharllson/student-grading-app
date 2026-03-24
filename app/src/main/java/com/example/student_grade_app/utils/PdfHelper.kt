package com.example.student_grade_app.utils

import android.content.Context
import com.example.student_grade_app.model.Student
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream

object PdfHelper {

    fun writeResultsToCache(context: Context, students: List<Student>): File? {
        val outputFile = File(context.cacheDir, "Grade_Results.pdf")
        if (outputFile.exists()) outputFile.delete()

        try {
            val document = Document()
            PdfWriter.getInstance(document, FileOutputStream(outputFile))
            document.open()

            // Title
            val titleFont = Font(Font.FontFamily.HELVETICA, 18f, Font.BOLD)
            val title = Paragraph("Student Grade Results", titleFont)
            title.alignment = Element.ALIGN_CENTER
            title.spacingAfter = 20f
            document.add(title)

            // Table
            val table = PdfPTable(4) // 4 columns
            table.widthPercentage = 100f
            table.setWidths(floatArrayOf(3f, 1.5f, 1f, 1.5f))

            // Headers
            val headerFont = Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD, BaseColor.WHITE)
            val headers = listOf("Name", "Average", "Grade", "Status")
            headers.forEach { text ->
                val cell = PdfPCell(Phrase(text, headerFont))
                cell.backgroundColor = BaseColor(0, 51, 102) // Dark Blue
                cell.horizontalAlignment = Element.ALIGN_CENTER
                cell.setPadding(8f)
                table.addCell(cell)
            }

            // Data
            val dataFont = Font(Font.FontFamily.HELVETICA, 10f)
            students.forEach { student ->
                table.addCell(PdfPCell(Phrase(student.name, dataFont)).apply { setPadding(5f) })
                table.addCell(PdfPCell(Phrase("%.1f".format(student.average ?: 0.0), dataFont)).apply { 
                    horizontalAlignment = Element.ALIGN_CENTER
                    setPadding(5f)
                })
                table.addCell(PdfPCell(Phrase(student.grade ?: "-", dataFont)).apply { 
                    horizontalAlignment = Element.ALIGN_CENTER
                    setPadding(5f)
                })
                val status = if (student.passed == true) "PASS" else "FAIL"
                table.addCell(PdfPCell(Phrase(status, dataFont)).apply { 
                    horizontalAlignment = Element.ALIGN_CENTER
                    setPadding(5f)
                })
            }

            document.add(table)
            document.close()
            return outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
