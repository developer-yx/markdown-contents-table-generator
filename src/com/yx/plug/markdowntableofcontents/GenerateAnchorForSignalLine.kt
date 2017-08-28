package com.yx.plug.markdowntableofcontents

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor

class GenerateAnchorForSignalLine : BaseSelectionAction() {

    override fun update(event: AnActionEvent?) {
        super.update(event)

        event ?: return

        val editor: Editor? = event.getData(CommonDataKeys.EDITOR)

//        val project: Project? = event.getData(CommonDataKeys.PROJECT)

        event.presentation.setEnabledAndVisible((editor ?: return).selectionModel.hasSelection())

    }

}