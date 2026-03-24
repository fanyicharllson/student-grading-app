package com.example.student_grade_app.utils

import android.content.Context
import com.example.student_grade_app.model.Student
import java.io.File

object HtmlHelper {
    fun writeResultsToCache(context: Context, students: List<Student>): File? {
        val outputFile = File(context.cacheDir, "Grade_Results.html")
        if (outputFile.exists()) outputFile.delete()

        return try {
            val html = StringBuilder()
            html.append("<!DOCTYPE html><html><head><title>Grade Results</title>")
            html.append("<style>")
            html.append("body { font-family: sans-serif; padding: 20px; background-color: #f4f4f4; }")
            html.append("table { width: 100%; border-collapse: collapse; background-color: white; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }")
            html.append("th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }")
            html.append("th { background-color: #003366; color: white; }")
            html.append("tr:hover { background-color: #f5f5f5; }")
            html.append(".pass { color: green; font-weight: bold; }")
            html.append(".fail { color: red; font-weight: bold; }")
            html.append("h1 { color: #333; text-align: center; }")
            html.append("</style></head><body>")
            html.append("<h1>Student Grade Results</h1>")
            html.append("<table>")
            html.append("<thead><tr><th>Name</th><th>Average</th><th>Grade</th><th>Status</th></tr></thead>")
            html.append("<tbody>")
            
            students.forEach { student ->
                val statusClass = if (student.passed == true) "pass" else "fail"
                val statusText = if (student.passed == true) "PASS" else "FAIL"
                html.append("<tr>")
                html.append("<td>${student.name}</td>")
                html.append("<td>${"%.1f".format(student.average ?: 0.0)}</td>")
                html.append("<td>${student.grade ?: "-"}</td>")
                html.append("<td class=\"$statusClass\">$statusText</td>")
                html.append("</tr>")
            }
            
            html.append("</tbody></table></body></html>")

            outputFile.writeText(html.toString())
            outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
