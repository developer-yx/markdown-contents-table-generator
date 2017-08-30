package com.yx.plug.markdowntableofcontents

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.project.Project
import java.io.BufferedReader
import java.io.IOException
import java.io.StringReader
import java.util.*


class GenerateAnchorAction : BaseSelectionAction() {

    override fun actionPerformed(anActionEvent: AnActionEvent?) {

        anActionEvent ?: return

        val editor = anActionEvent.getRequiredData(CommonDataKeys.EDITOR)

        val project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT)

        val headers = findHeaders(editor)

        addAnchor(project, editor.document, headers.iterator())
    }

    fun addAnchor(project: Project, document: Document, iterator: MutableIterator<Header>) {

        if (!iterator.hasNext()) {
            return
        }

        val header = iterator.next()

        val index = findIndexOfHeader(document.text, header)

        if (index <= 0) {

            addAnchor(project, document, iterator)

            return
        }

        document.addDocumentListener(object : DocumentListener {
            override fun beforeDocumentChange(event: DocumentEvent?) {
                super.beforeDocumentChange(event)
            }

            override fun documentChanged(event: DocumentEvent?) {
                super.documentChanged(event)

                document.removeDocumentListener(this)

                ApplicationManager.getApplication().invokeLater(Runnable { addAnchor(project, document, iterator) })
            }
        })

        WriteCommandAction.runWriteCommandAction(project) { document.insertString(index, "\n" + buildAnchor(header)) }
    }

    fun buildAnchor(header: Header): String {
        return "<a name=\"${header.anchor}\"></a>"
    }

    fun findIndexOfHeader(content: String, header: Header): Int {
        var bufferedReader: BufferedReader? = null

        try {
            bufferedReader = BufferedReader(StringReader(content))

            var index = -1

            for (line in bufferedReader.lines()) {

                if (line.trim().startsWith("#") && line.contains(header.title)) {
                    return index
                }

                index += line.length + 1
            }

        } catch (e: Exception) {
            println(e)
        } finally {
            try {
                bufferedReader!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        return -1
    }
}


fun findHeaders(editor: Editor): MutableList<Header> {
    val document = editor.document
    val selectionModel = editor.selectionModel

    val start = selectionModel.selectionStart
    val end = selectionModel.selectionEnd

    val fileStr = document.text

    val selectionStr = fileStr.substring(start, end)

    var bufferedReader: BufferedReader? = null

    val headers: MutableList<Header> = mutableListOf()

    try {

        bufferedReader = BufferedReader(StringReader(selectionStr))

        for (line in bufferedReader.lines()) {
            var newLine: String = line.trim()

            if (!newLine.startsWith("- ") || !newLine.contains("#")) {
                continue
            }

            newLine = newLine.replace("- ", "").replace("[", "").replace("]", "")
                    .replace("(", "").replace(")", "")

            var arr = newLine.split("#")

            if (arr.size != 2) {
                continue
            }

            headers.add(Header(arr[0].trim(), arr[1].trim()))
        }

    } catch (e: Exception) {
        println(e)
    } finally {
        try {
            bufferedReader!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    return headers
}
