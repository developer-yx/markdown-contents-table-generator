package com.yx.plug.markdowntableofcontents;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.vfs.VirtualFile;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class MarkdownGroup extends ActionGroup
{
    @Override
    public void update(AnActionEvent e)
    {
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);

        boolean visible = file != null && file.getName().toLowerCase().endsWith(".md");

        e.getPresentation().setEnabledAndVisible(visible);
    }

    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent anActionEvent)
    {
        return new AnAction[0];
    }
}
