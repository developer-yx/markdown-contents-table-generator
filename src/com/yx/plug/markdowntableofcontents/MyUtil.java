package com.yx.plug.markdowntableofcontents;

import com.intellij.openapi.ui.Messages;

import java.util.ArrayList;
import java.util.List;

public class MyUtil
{
    public static boolean isHeader(String line)
    {
        String[] temp = line.trim().split(" ");

        return temp != null && temp.length > 0 && temp[0].startsWith("#") && temp[0].endsWith("#");
    }

    public static int getHeaderLevel(String line)
    {
        return line.trim().split(" ")[0].length();
    }

    public static String getHeaderTitle(String line)
    {
        return line.trim().replaceAll("#", "").trim();
    }

    public static String getHeaderAnchor(String line)
    {
        line = line.trim().replaceAll("[^\\w]", "-").trim();

        char[] chars = line.toCharArray();

        List<Character> newChars = new ArrayList();

        for (int i = 0; i < chars.length; i++)
        {
            if ('-' == chars[i])
            {
                if (newChars.size() <= 0)
                {
                    continue;
                }
                else
                {
                    if (newChars.get(newChars.size() - 1) == '-')
                    {
                        continue;
                    }
                }
            }

            newChars.add(chars[i]);
        }

        if (newChars.size() > 0 && newChars.get(newChars.size() - 1) == '-')
        {
            newChars.remove(newChars.size() - 1);
        }

        return String.valueOf(newChars.toArray(new Character[newChars.size()]));
    }


    public static void showErrorMsg(String msg)
    {
        showMsg(true, msg);
    }

    public static void showInfoMsg(String msg)
    {
        showMsg(false, msg);
    }

    public static void showMsg(boolean isError, String msg)
    {
        Messages.showMessageDialog(msg, isError ? "Error" : "Info", isError ? Messages.getErrorIcon() : Messages.getInformationIcon());
    }
}
