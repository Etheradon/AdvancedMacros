package com.theincgi.advancedmacros.mixin;

import net.minecraft.client.gui.screen.ingame.BookEditScreen;

import com.theincgi.advancedmacros.access.IBookEditScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


@Mixin(BookEditScreen.class)
public abstract class MixinBookEditScreen implements IBookEditScreen {

    @Shadow private boolean dirty;

    @Shadow protected abstract void updateButtons();

    @Shadow protected abstract void finalizeBook(boolean signBook);

    @Shadow private String title;

    @Shadow private int currentPage;

    @Shadow protected abstract void changePage();

    @Shadow protected abstract int countPages();

    @Shadow protected abstract String getCurrentPageContent();

    @Shadow protected abstract void setPageContent(String newContent);

    @Shadow protected abstract void appendNewPage();

    @Override
    public void am_markDirty() {
        dirty = true;
    }

    @Override
    public void am_updateButtons() {
        updateButtons();
    }

    @Override
    public void am_finalizeBook(boolean sign) {
        finalizeBook(sign);
    }

    @Override
    public void am_setTitle(String newTitle) {
        title = newTitle;
    }

    @Override
    public void am_setCurrentPage(int page) {
        if (page < 0 || page >= countPages()) {
            return;
        }
        currentPage = page;
        updateButtons();
        changePage();
    }

    @Override
    public int am_getCurrentPage() {
        return currentPage;
    }

    @Override
    public int am_getPageCount() {
        return countPages();
    }

    @Override
    public String am_getPageContent() {
        return getCurrentPageContent();
    }

    @Override
    public void am_setPageContent(String content) {
        setPageContent(content);
    }

    @Override
    public void am_appendNewPage() {
        appendNewPage();
    }
}
