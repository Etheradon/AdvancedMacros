package com.theincgi.advancedmacros.access;

public interface IBookEditScreen {

    void am_markDirty();

    void am_updateButtons();

    void am_finalizeBook(boolean sign);

    void am_setTitle(String title);

    void am_setCurrentPage(int page);

    int am_getCurrentPage();

    int am_getPageCount();

    String am_getPageContent();

    void am_setPageContent(String content);

    void am_appendNewPage();

}
