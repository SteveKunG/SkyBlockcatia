package com.stevekung.skyblockcatia.unused;
//package com.stevekung.skyblockcatia.mixin;
//
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//import com.stevekung.indicatia.config.ExtendedConfig;
//import com.stevekung.indicatia.config.IndicatiaConfig;
//import com.stevekung.indicatia.gui.screen.IndicatiaChatScreen.ChatMode;
//import com.stevekung.indicatia.hud.InfoUtils;
//
//import net.minecraft.client.gui.screen.ChatScreen;
//import net.minecraft.client.gui.screen.Screen;
//import net.minecraft.client.gui.widget.button.Button;
//import net.minecraft.util.text.ITextComponent;
//import net.minecraft.util.text.TextFormatting;
//
//@Mixin(ChatScreen.class)
//public abstract class MixinChatScreen extends Screen
//{
//    private static final ChatMode SKYBLOCK_COOP = ChatMode.create("SKYBLOCK_COOP", "/cc", "Coop Chat", TextFormatting.AQUA);
//
//    static
//    {
//        System.out.println(SKYBLOCK_COOP.toString());
//    }
//
//    protected MixinChatScreen(ITextComponent title)
//    {
//        super(title);
//    }
//
////    @Inject(method = "init()V", at = @At("RETURN"))
////    private void init(CallbackInfo info)
////    {
//////        if (InfoUtils.INSTANCE.isHypixel())
////        {
//////            if (IndicatiaConfig.GENERAL.enableHypixelChatMode.get())
////            {
////                this.addButton(new Button(width - 31, height - 98, 28, 20, "COOP", button ->
////                {
////                    //                    this.mode = ChatMode.GUILD;
//////                    this.minecraft.player.sendChatMessage("/chat g");
////                    ExtendedConfig.INSTANCE.chatMode = 3;
////                }));
////            }
////        }
////    }
//
//    @Override
//    public void sendMessage(String msg)
//    {
//        this.sendMessage(this.getNewMessage(msg), true);
//
//        //        if (!msg.startsWith("/"))TODO
//        //        {
//        //            for (IGuiChat chat : GuiChatRegistry.getGuiChatList())
//        //            {
//        //                this.sendChatMessage(chat.sendChatMessage(msg), true);
//        //            }
//        //        }
//        //        else
//        //        {
//        //            super.sendChatMessage(msg);
//        //        }
//
//        ////                case 203:
//        //        this.mode = ChatMode.SKYBLOCK_COOP;
//        //        ExtendedConfig.instance.chatMode = 3;
//        //        break;
//    }
//
//    private String getNewMessage(String original)
//    {
//        if (ChatMode.values()[ExtendedConfig.INSTANCE.chatMode] == MixinChatScreen.SKYBLOCK_COOP)
//        {
//            return MixinChatScreen.SKYBLOCK_COOP.getCommand() + " " + original;
//        }
//        else
//        {
//            return original;
//        }
//    }
//}