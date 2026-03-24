package com.example.student_grade_app.utils

import android.content.Context
import com.example.student_grade_app.model.Student
import java.io.File

object XmlHelper {
    fun writeResultsToCache(context: Context, students: List<Student>): File? {
        val outputFile = File(context.cacheDir, "Grade_Results.xml")
        if (outputFile.exists()) outputFile.delete()

        return try {
            val xml = StringBuilder()
            xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
            xml.append("<GradeResults>\n")
            students.forEach { student ->
                xml.append("  <Student>\n")
                xml.append("    <Name>${student.name}</Name>\n")
                xml.append("    <Average>${"%.1f".format(student.average ?: 0.0)}</Average>\n")
                xml.append("    <Grade>${student.grade ?: "-"}</Grade>\n")
                xml.append("    <Status>${if (student.passed == true) "PASS" else "FAIL"}</Status>\n")
                xml.append("  </Student>\n")
            }
            xml.append("</GradeResults>")

            outputFile.writeText(xml.toString())
            outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
