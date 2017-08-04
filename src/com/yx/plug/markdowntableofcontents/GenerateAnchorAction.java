package com.yx.plug.markdowntableofcontents;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.vfs.VirtualFile;

public class GenerateAnchorAction extends AnAction
{
    @Override
    public void update(AnActionEvent e)
    {
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);

        boolean visible = file != null && file.getName().toLowerCase().endsWith(".md");

        e.getPresentation().setEnabledAndVisible(visible);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent)
    {

    }
}
