package com.theincgi.advancedmacros.mixin;

import com.theincgi.advancedmacros.access.ICommandBlockScreen;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.client.gui.screen.ingame.AbstractCommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.world.CommandBlockExecutor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CommandBlockScreen.class)
public abstract class MixinCommandBlockScreen extends AbstractCommandBlockScreen implements ICommandBlockScreen {

    @Shadow
    protected abstract void syncSettingsToServer(CommandBlockExecutor commandExecutor);

    @Shadow
    private boolean conditional;

    @Shadow
    abstract CommandBlockExecutor getCommandExecutor();

    @Shadow
    @Final
    private CommandBlockBlockEntity blockEntity;

    @Shadow
    private CommandBlockBlockEntity.Type mode;

    @Shadow
    private boolean autoActivate;

    @Override
    public void am_syncSettingsToServer() {
        syncSettingsToServer(getCommandExecutor());
    }

    @Override
    public CommandBlockBlockEntity am_getCommandBlockEntity() {
        return blockEntity;
    }

    @Override
    public CommandBlockBlockEntity.Type am_getCommandBlockType() {
        return mode;
    }

    @Override
    public TextFieldWidget am_getCommandField() {
        return consoleCommandTextField;
    }

    @Override
    public void am_setCommandBlockType(CommandBlockBlockEntity.Type type) {
        mode = type;
    }

    @Override
    public void am_setConditional(boolean conditional) {
        this.conditional = conditional;
    }

    @Override
    public void am_setAuto(boolean autoActivate) {
        this.autoActivate = autoActivate;
    }

}
