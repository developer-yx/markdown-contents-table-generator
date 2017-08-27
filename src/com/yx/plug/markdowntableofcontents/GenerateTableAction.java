package com.yx.plug.markdowntableofcontents;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class GenerateTableAction extends AnAction
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
        final Editor editor = anActionEvent.getRequiredData(CommonDataKeys.EDITOR);

        final Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);

        //Access document, caret, and selection
        final Document document = editor.getDocument();

        final SelectionModel selectionModel = editor.getSelectionModel();

        final int index = selectionModel.getSelectionStart();

        String table = processFile(document);

        //Making the replacement
        WriteCommandAction.runWriteCommandAction(project, () -> document.insertString(index, table));

        selectionModel.removeSelection();
    }

    private String processFile(Document document)
    {
        BufferedReader bufferedReader = null;

        final List<Header> topLevelHeaders = new ArrayList<Header>();

        try
        {
            bufferedReader = new BufferedReader(new StringReader(document.getText()));

            checkTopLevelHeadline(bufferedReader, topLevelHeaders, 2);

            if (topLevelHeaders.size() <= 0)
            {
                return null;
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        finally
        {
            try
            {
                bufferedReader.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        for (Header header : topLevelHeaders)
        {
            try
            {
                bufferedReader = new BufferedReader(new StringReader(document.getText()));

                while (!bufferedReader.readLine().equals(header.getLine()))
                {

                }

                checkChildHeaders(bufferedReader, header);
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
            finally
            {
                try
                {
                    bufferedReader.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        StringBuilder builder = new StringBuilder();

        createContentsTable(builder, topLevelHeaders, 0);

        return builder.toString();
    }

    private void createContentsTable(StringBuilder stringBuilder, List<Header> headers, int tab)
    {
        if (headers == null || headers.size() <= 0)
        {
            return;
        }

        String prefix = "";

        for (int i = 0; i < tab; i++)
        {
            prefix += "\t";
        }

        for (Header header : headers)
        {
            stringBuilder.append(prefix + createCatalogue(header) + "\n");

            if (header.getChildHeadlins() != null)
            {
                createContentsTable(stringBuilder, header.getChildHeadlins(), tab + 1);
            }
        }
    }

    private static String createCatalogue(Header header)
    {
        return "- [" + header.getTitle() + "](#" + header.getAnchor() + ")";
    }

    private static void checkTopLevelHeadline(BufferedReader reader, List<Header> topLevel, int headlineLevel)
    {
        try
        {
            String line;

            while ((line = reader.readLine()) != null)
            {
                if (MyUtil.isHeader(line) && MyUtil.getHeaderLevel(line) == headlineLevel)
                {
                    topLevel.add(new Header(line));
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static String checkChildHeaders(BufferedReader reader, Header parentHeader)
    {
        try
        {
            String line = reader.readLine();

            while (line != null)
            {
                if (MyUtil.isHeader(line))
                {
                    if (MyUtil.getHeaderLevel(line) <= parentHeader.getLevel())
                    {
                        return line;
                    }

                    Header child = new Header(line);

                    parentHeader.addChild(child);

                    String lastLine = checkChildHeaders(reader, child);

                    if (lastLine != null)
                    {
                        line = lastLine;
                    }
                    else
                    {
                        line = reader.readLine();
                    }
                }
                else
                {
                    line = reader.readLine();
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
