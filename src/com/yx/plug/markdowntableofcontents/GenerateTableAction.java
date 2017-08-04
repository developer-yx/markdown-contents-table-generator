package com.yx.plug.markdowntableofcontents;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
        Editor editor = (Editor) anActionEvent.getDataContext().getData(CommonDataKeys.EDITOR.getName());

        FileEditorManager manager = FileEditorManager.getInstance(anActionEvent.getProject());

        VirtualFile[] files = manager.getSelectedFiles();

        if (files == null || files.length <= 0)
        {
            MyUtil.showErrorMsg("Can NOT file the file !!!");

            return;
        }

        File file = new File(files[0].getPath());

        if (!checkMarkdownFile(file))
        {
            MyUtil.showErrorMsg("This Plugin is only available for Markdown file !!!");

            return;
        }

        processFile(file);
    }

    private boolean checkMarkdownFile(File file)
    {
        return file.getName().toLowerCase().endsWith(".md");
    }

    private void processFile(File file)
    {
        BufferedReader bufferedReader = null;

        final List<Header> topLevelHeaders = new ArrayList<Header>();

        try
        {
            bufferedReader = new BufferedReader(new FileReader(file));

            checkTopLevelHeadline(bufferedReader, topLevelHeaders, 2);

            if (topLevelHeaders.size() <= 0)
            {
                return;
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
                bufferedReader = new BufferedReader(new FileReader(file));

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

        writeFile(file, builder.toString());
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

    private void writeFile(File file, String table)
    {
        BufferedReader bufferedReader = null;

        BufferedWriter bufferedWriter = null;

        StringBuilder builder = new StringBuilder();

        try
        {
            bufferedReader = new BufferedReader(new FileReader(file));

            String line;

            while ((line = bufferedReader.readLine()) != null)
            {
                builder.append(line + java.security.AccessController.doPrivileged(
                        new sun.security.action.GetPropertyAction("line.separator")));
            }

            bufferedWriter = new BufferedWriter(new FileWriter(file));

            bufferedWriter.write(table + java.security.AccessController.doPrivileged(
                    new sun.security.action.GetPropertyAction("line.separator")));

            bufferedWriter.write(builder.toString());

            bufferedWriter.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                bufferedReader.close();

                bufferedWriter.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
