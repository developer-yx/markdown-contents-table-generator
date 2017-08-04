package com.yx.plug.markdowntableofcontents;

import java.util.ArrayList;
import java.util.List;

public class Header
{
    private final String line;

    private final int level;

    private final String title;

    private final String anchor;

    private List<Header> childHeaders;

    public Header(String line)
    {
        this.line = line;

        this.level = MyUtil.getHeaderLevel(line);

        this.title = MyUtil.getHeaderTitle(line);

        this.anchor = MyUtil.getHeaderAnchor(line);
    }

    public String getLine()
    {
        return line;
    }

    public int getLevel()
    {
        return level;
    }

    public String getAnchor()
    {
        return anchor;
    }

    public String getTitle()
    {
        return title;
    }

    public void addChild(Header header)
    {
        if (childHeaders == null)
        {
            childHeaders = new ArrayList<Header>();
        }

        childHeaders.add(header);
    }

    public List<Header> getChildHeadlins()
    {
        return childHeaders;
    }

    @Override
    public String toString()
    {
        return "Headline{" +
                "line='" + line + '\'' +
                ", level=" + level +
                ", title='" + title + '\'' +
                ", anchor='" + anchor + '\'' +
                ", childHeadlines=" + childHeaders +
                '}';
    }
}