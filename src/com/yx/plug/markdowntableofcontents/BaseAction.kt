package com.yx.plug.markdowntableofcontents

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys


open class BaseAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent?) {
        val file = (event ?: return).getData(CommonDataKeys.VIRTUAL_FILE)

        val visible = file != null && file!!.getName().toLowerCase().endsWith(".md")

        event.presentation.setEnabledAndVisible(visible)
    }
}