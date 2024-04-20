package com.theincgi.advancedmacros.lua.functions;

import com.theincgi.advancedmacros.misc.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Arm;
import org.luaj.vm2_v3_0_1.LuaError;
import org.luaj.vm2_v3_0_1.LuaTable;
import org.luaj.vm2_v3_0_1.Varargs;
import org.luaj.vm2_v3_0_1.lib.VarArgFunction;

import java.util.Arrays;
import java.util.List;

public class MinecraftSettings extends LuaTable {

    public MinecraftSettings() {
        for (OpCode code : OpCode.values()) {
            set(code.name(), new DoOp(code)); //TODO document me
        }
    }

    private static final String[] GUI_SCALE = new String[]{"auto", "small", "normal", "large"};
    private static final String[] PERSPECTIVE = new String[]{"first", "front", "back"};

    private static class DoOp extends VarArgFunction {

        OpCode code;

        public DoOp(OpCode code) {
            this.code = code;
        }

        @Override
        public Varargs invoke(Varargs args) {
            MinecraftClient mc = MinecraftClient.getInstance();
            switch (code) {
                case getFov:
                    return valueOf(mc.options.getFov().getValue());
                case getRenderDistance:
                    return valueOf(mc.options.getViewDistance().getValue());
                case getSkinCustomization: {
                    List<PlayerModelPart> enabledBodyParts = Arrays.stream(PlayerModelPart.values()).filter(mc.options::isPlayerModelPartEnabled).toList();
                    if (args.arg1().isnil()) {
                        LuaTable out = new LuaTable();
                        for (PlayerModelPart part : PlayerModelPart.values()) {
                            out.set(part.name().toLowerCase().replace('_', ' '), false);
                        }
                        for (PlayerModelPart part : enabledBodyParts) {
                            out.set(part.name().toLowerCase().replace('_', ' '), true);
                        }
                    } else {
                        String key = args.checkjstring(1);
                        if (key.equals("helmet")) {
                            key = "hat";
                        } else if (key.equals("jacket")) {
                            key = "chest";
                        }
                        key = key.toUpperCase();
                        key = key.replace(' ', '_');
                        return valueOf(enabledBodyParts.contains(PlayerModelPart.valueOf(key)));
                    }
                }
                case getVolume:
                    return valueOf(mc.options.getSoundVolume(SoundCategory.valueOf(args.checkjstring(1).toUpperCase())));
                case isFullscreen:
                    return valueOf(mc.getWindow().isFullscreen());
                case setFov:
                    mc.options.getFov().setValue(args.checkint(1));
                    return NONE;
                case setFullscreen: //CHECKME
                    if (mc.getWindow().isFullscreen() != args.optboolean(1, true)) {
                        mc.getWindow().toggleFullscreen();
                    }
                    return NONE;
                case setRenderDistance:
                    mc.options.getViewDistance().setValue(Math.max(2, Math.min(args.checkint(1), 32)));
                    return NONE;
                case setVolume:
                    mc.options.getSoundVolumeOption(SoundCategory.valueOf(args.checkjstring(1).toUpperCase())).setValue(args.checkdouble(2));
                    return NONE;
                case getMaxFps:
                    return valueOf(mc.options.getMaxFps().getValue());
                case setMaxFps:
                    mc.options.getMaxFps().setValue(Math.max(1, args.checkint(1)));
                    return NONE;
                case getSmoothLighting: {
                    name = mc.options.getAo().getValue().toString();
                    return valueOf(name);
                }
                case getChatHeightFocused:
                    return valueOf(mc.options.getChatHeightFocused().getValue());
                case getChatHeightUnfocused:
                    return valueOf(mc.options.getChatHeightUnfocused().getValue());
                case getChatOpacity:
                    return valueOf(mc.options.getChatOpacity().getValue());
                case getChatScale:
                    return valueOf(mc.options.getChatScale().getValue());
                case getChatWidth:
                    return valueOf(mc.options.getChatWidth().getValue());
                case getCloudsMode:
                    return valueOf(mc.options.getCloudRenderMode().getValue().name().toLowerCase());
/*                case getDifficulty:
                    return valueOf(mc.options.difficulty.name().toLowerCase());*/
                case getGuiScale:
                    return valueOf(mc.options.getGuiScale().getValue());
                case getLanguage:
                    return valueOf(mc.options.language);
                case getLanguages: {
                    LuaTable langs = new LuaTable();
                    for (LanguageDefinition l : mc.getLanguageManager().getAllLanguages().values()) {
                        langs.set(langs.length() + 1, l.region());
                    }
                }
                case getLastServer:
                    return valueOf(mc.options.lastServer);
                case getMainHandSide:
                    return valueOf(mc.options.getMainArm().getValue().name().toLowerCase());
                case getMipmapLevels:
                    return valueOf(mc.options.getMipmapLevels().getValue());
                case getMouseSensitivity:
                    return valueOf(mc.options.getMouseSensitivity().getValue());
                case getParticleLevel:
                    return valueOf(mc.options.getParticles().getValue().name().toLowerCase());
                case getPerspective:
                    return valueOf(PERSPECTIVE[mc.options.getPerspective().ordinal()]);
                case isAdvancedItemTooltips:
                    return valueOf(mc.options.advancedItemTooltips);
                case isAutoJump:
                    return valueOf(mc.options.getAutoJump().getValue());
                case isEntityShadows:
                    return valueOf(mc.options.getEntityShadows().getValue());
/*                case isFancyGraphics:
                    return valueOf(mc.options.fancyGraphics);*/
                case isHeldItemTooltips:
                    return valueOf(mc.options.advancedItemTooltips);
                case isInvertMouse:
                    return valueOf(mc.options.getInvertYMouse().getValue());
                case isPauseOnLostFocus:
                    return valueOf(mc.options.pauseOnLostFocus);
                case isSmoothCamera:
                    return valueOf(mc.options.smoothCameraEnabled);
                case isTouchscreenMode:
                    return valueOf(mc.options.getTouchscreen().getValue());
                case isViewBobbing:
                    return valueOf(mc.options.getBobView().getValue());
                case isVsync:
                    return valueOf(mc.options.getEnableVsync().getValue());
                case setAdvancedItemTooltips:
                    mc.options.advancedItemTooltips = args.arg1().checkboolean();
                    return NONE;
                case setSmoothLighting: {
                    mc.options.getAo().setValue(args.arg1().checkboolean());
                    return NONE;
                }
                case setAutoJump:
                    mc.options.getAutoJump().setValue(args.arg1().checkboolean());
                    return NONE;

                case setChatHeightFocused:
                    mc.options.getChatHeightFocused().setValue(Utils.clamp(0.0, args.arg1().checkdouble(), 1.0));
                    return NONE;

                case setChatHeightUnfocused:
                    mc.options.getChatHeightUnfocused().setValue(Utils.clamp(0.0, args.arg1().checkdouble(), 1.0));
                    return NONE;

                case setChatOpacity:
                    mc.options.getChatOpacity().setValue(Utils.clamp(0, args.arg1().checkdouble(), 1));
                    return NONE;

                case setChatScale:
                    mc.options.getGuiScale().setValue(Utils.clamp(0, args.arg1().checkint(), 1));
                    return NONE;

                case setChatWidth:
                    mc.options.getChatWidth().setValue(Utils.clamp(0, args.arg1().checkdouble(), 1));
                    return NONE;

                case setCloudsMode:
                    CloudRenderMode co = CloudRenderMode.valueOf(args.arg1().checkjstring().toUpperCase());
                    mc.options.getCloudRenderMode().setValue(co);
                    return NONE;
/*                case setDifficulty:
                    mc.options.difficulty = args.arg1().isnumber() ? Difficulty.byId(args.arg1().checkint()) : Difficulty.valueOf(args.arg1().checkjstring().toUpperCase());
                    return NONE;*/
                case setEntityShadows:
                    mc.options.getEntityShadows().setValue(args.arg1().checkboolean());
                    return NONE;
/*                case setFancyGraphics:
                    mc.options.fancyGraphics = args.checkboolean(1);
                    return NONE;*/
                case setGuiScale:
                    switch (args.checkjstring(1).toLowerCase()) {
                        case "auto":
                            mc.options.getGuiScale().setValue(0);
                            break;
                        case "small":
                            mc.options.getGuiScale().setValue(1);
                            break;
                        case "normal":
                            mc.options.getGuiScale().setValue(2);
                            break;
                        case "large":
                            mc.options.getGuiScale().setValue(3);
                            break;
                        default:
                            throw new LuaError("Invalid scale [auto/small/normal/large]");
                    }
                    return NONE;
                case setHeldItemTooltips:
                    mc.options.advancedItemTooltips = args.checkboolean(1);
                    return NONE;
                case setInvertMouse:
                    mc.options.getInvertYMouse().setValue(args.checkboolean(1));
                    return NONE;
                case setLanguage:
                    for (LanguageDefinition l : mc.getLanguageManager().getAllLanguages().values()) {
                        if (l.region().toLowerCase() == args.checkjstring(1).toLowerCase()) {
                            mc.options.language = l.region();
                            return NONE;
                        }
                    }
                    throw new LuaError("Invalid languge code (" + args.checkjstring(1) + ")");
                case setMainHandSide:
                    mc.options.getMainArm().setValue(Arm.valueOf(args.checkjstring(1).toUpperCase()));
                    return NONE;
                case setMipmapLevels:
                    mc.options.getMipmapLevels().setValue(Utils.clamp(0, args.checkint(1), 4));
                    return NONE;
                case setMouseSensitivity:
                    mc.options.getMouseSensitivity().setValue(Utils.clamp(0.0, args.checkdouble(1), 1.0));
                    return NONE;
                case setParticleLevel:
                    ParticlesMode s = ParticlesMode.valueOf(args.checkjstring(1).toLowerCase());
                    mc.options.getParticles().setValue(s);
                    return NONE;
                case setPauseOnLostFocus:
                    mc.options.pauseOnLostFocus = args.checkboolean(1);
                    return NONE;
                case setSmoothCamera:
                    mc.options.smoothCameraEnabled = args.checkboolean(1);
                    return NONE;
                case setPerspective:
                    switch (args.checkjstring(1).toLowerCase()) {
                        case "first":
                            mc.options.setPerspective(Perspective.FIRST_PERSON);
                            break;
                        case "front":
                            mc.options.setPerspective(Perspective.THIRD_PERSON_FRONT);
                            break;
                        case "back":
                            mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
                            break;

                        default:
                            throw new LuaError("Unknown perspective '" + args.checkjstring(1) + "' (use first/front/back)");
                    }
                    return NONE;
                case setTouchscreenMode:
                    mc.options.getTouchscreen().setValue(args.checkboolean(1));
                    return NONE;
                case setViewBobbing:
                    mc.options.getBobView().setValue(args.checkboolean(1));
                    return NONE;
                case setVsync:
                    mc.options.getEnableVsync().setValue(args.checkboolean(1));
                    return NONE;
                default:
                    throw new LuaError("Undefined op " + code.name());
            }
        }

    }

    private enum OpCode {
        getFov,
        setFov,
        getVolume,
        setVolume,
        setRenderDistance,
        getRenderDistance,
        setFullscreen,
        isFullscreen,
        getSkinCustomization,
        getMaxFps,
        setMaxFps,
        setAdvancedItemTooltips,
        isAdvancedItemTooltips,
        getSmoothLighting,
        setSmoothLighting,
        setAutoJump,
        isAutoJump,
        getChatOpacity,
        setChatOpacity,
        getChatScale,
        setChatScale,
        getChatHeightFocused,
        setChatHeightFocused,
        getChatHeightUnfocused,
        setChatHeightUnfocused,
        setChatWidth,
        getChatWidth,
        setCloudsMode,
        getCloudsMode,
        setDifficulty,
        getDifficulty,
        setVsync,
        isVsync,
        setEntityShadows,
        isEntityShadows,
        setFancyGraphics,
        isFancyGraphics,
        setGuiScale,
        getGuiScale,
        setHeldItemTooltips,
        isHeldItemTooltips,
        setInvertMouse,
        isInvertMouse,
        getLanguage,
        getLanguages,
        setLanguage,
        getLastServer,
        getMainHandSide,
        setMainHandSide,
        getMipmapLevels,
        setMipmapLevels,
        getMouseSensitivity,
        setMouseSensitivity,
        setParticleLevel,
        getParticleLevel,
        setPauseOnLostFocus,
        isPauseOnLostFocus,
        setSmoothCamera,
        isSmoothCamera,
        setPerspective,
        getPerspective,
        setTouchscreenMode,
        isTouchscreenMode,
        setViewBobbing,
        isViewBobbing,

    }

}
