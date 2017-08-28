package com.yx.plug.markdowntableofcontents

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.SelectionModel
import com.intellij.openapi.project.Project

import java.io.BufferedReader
import java.io.IOException
import java.io.StringReader
import java.lang.reflect.Array

class GenerateAnchorAction : BaseSelectionAction() {

    override fun actionPerformed(anActionEvent: AnActionEvent?) {
        //Get all the required data from data keys
        val editor = anActionEvent!!.getRequiredData(CommonDataKeys.EDITOR)
        val project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT)
        //Access document, caret, and selection
        val document = editor.document
        val selectionModel = editor.selectionModel

        val start = selectionModel.selectionStart
        val end = selectionModel.selectionEnd
        //        //New instance of Runnable to make a replacement
        //        Runnable runnable = new Runnable()
        //        {
        //            @Override
        //            public void run()
        //            {
        //                document.replaceString(start, end, "Replacement1231231");
        //            }
        //        };
        //        //Making the replacement
        //        WriteCommandAction.runWriteCommandAction(project, runnable);
        //        selectionModel.removeSelection();

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

                newLine = newLine.replace("-", "").replace("[", "").replace("]", "")
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
                (bufferedReader ?: return).close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        for (header in headers) {
            println(header)
        }
    }
}
