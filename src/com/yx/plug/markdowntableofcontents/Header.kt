package com.yx.plug.markdowntableofcontents

import java.util.ArrayList

class Header {

    val line: String?

    val level: Int

    val title: String

    val anchor: String

    private val childHeaders: MutableList<Header>

    init {

        this.childHeaders = ArrayList()
    }

    constructor(line: String) {

        this.line = line

        this.level = MyUtil.getHeaderLevel(line)

        this.title = MyUtil.getHeaderTitle(line)

        this.anchor = MyUtil.getHeaderAnchor(line)
    }

    constructor(title: String, anchor: String) {

        this.line = null

        this.level = 0

        this.title = title

        this.anchor = anchor
    }

    fun addChild(header: Header) {

        childHeaders.add(header)
    }

    val childHeadlins: List<Header>?
        get() = childHeaders

    override fun toString(): String {
        return "Headline{" +
                ", level=" + level +
                ", title='" + title + '\'' +
                ", anchor='" + anchor + '\'' +
                ", childHeadlines=" + childHeaders +
                '}'
    }
}