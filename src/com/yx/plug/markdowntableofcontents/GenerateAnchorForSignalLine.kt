package com.yx.plug.markdowntableofcontents

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener

class GenerateAnchorForSignalLine : BaseSelectionAction() {

    override fun actionPerformed(event: AnActionEvent?) {

        super.actionPerformed(event)

        event ?: return

        val editor = event.getRequiredData(CommonDataKeys.EDITOR)

        val document = editor.document

        val temp = document.text.substring(0, editor.selectionModel.selectionStart)

        val index = temp.indexOfLast({ item -> item == '\n' }) + 1

        val text = editor.selectionModel.selectedText

        val anchor = MyUtil.getHeaderAnchor(text)

        val anchorLine = buildAnchor(anchor)

        val headerLine = MyUtil.createHeaderLine(text, anchor)

        document.addDocumentListener(object : DocumentListener {
            override fun documentChanged(event: DocumentEvent?) {
                super.documentChanged(event)
            }

            override fun beforeDocumentChange(event: DocumentEvent?) {
                super.beforeDocumentChange(event)
            }
        })

        WriteCommandAction.runWriteCommandAction(event.getRequiredData(CommonDataKeys.PROJECT)) { document.insertString(index, headerLine + "\n" + anchorLine + "\n") }
    }

}