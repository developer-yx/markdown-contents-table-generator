package com.yx.plug.markdowntableofcontents;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainAction extends AnAction
{
    @Override
    public void actionPerformed(AnActionEvent anActionEvent)
    {
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

        final List<Header> topLevelHeaders = new ArrayList<>();

        try
        {
            bufferedReader = new BufferedReader(new FileReader(file));

            bufferedReader.mark(0);

            checkTopLevelHeadline(bufferedReader, topLevelHeaders, 2);

            if (topLevelHeaders.size() <= 0)
            {
                bufferedReader.reset();

                checkTopLevelHeadline(bufferedReader, topLevelHeaders, 3);
            }

            bufferedReader.reset();

            if (topLevelHeaders.size() <= 0)
            {
                MyUtil.showErrorMsg("Can NOT find top level headline !!!");

                return;
            }

            for (Header header : topLevelHeaders)
            {
                checkChildHeaders(bufferedReader, header);

                bufferedReader.reset();
            }

            System.out.println(topLevelHeaders);

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

    private void checkTopLevelHeadline(BufferedReader reader, List<Header> topLevel, int headlineLevel)
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

    private String checkChildHeaders(BufferedReader reader, Header parentHeader)
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
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
