package com.stevekung.skyblockcatia.utils.skyblock.api;

import java.util.Locale;

import com.stevekung.skyblockcatia.utils.RenderUtils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public enum RuneSacks
{
    UNKNOWN(RenderUtils.getSkullItemStack("cef15551-b5e8-4073-be03-44c377a1638d", "daa09c57968d4af3c7b9c732bf2070c1a91984c35b3e6a3fab6677bbbd7a5479"), EnumChatFormatting.RED + "Unknown Rune"),
    BLOOD_2(RenderUtils.getSkullItemStack("5d10b937-4eca-4328-9879-5f7b29f51101", "e02677053dc54245dac4b399d14aae21ee71a010bd9c336c8ecee1a0dbe8f58b"), EnumChatFormatting.RED + "◆ Blood Rune"),
    GEM(RenderUtils.getSkullItemStack("ae4828fa-239b-4b52-a25d-abd9d2be74fa", "43a1ad4fcc42fb63c681328e42d63c83ca193b333af2a426728a25a8cc600692"), EnumChatFormatting.DARK_GREEN + "◆ Gem Rune"),
    JERRY(RenderUtils.getSkullItemStack("c1cea5cd-e75c-4149-8d16-697888e8726d", "a1353ba65b6521f922b6464d17af39bddd43c984b8a227d99161594eba7a14c4"), EnumChatFormatting.GREEN + "◆ Jerry Rune"),
    LAVA(RenderUtils.getSkullItemStack("0026c733-562b-4287-8c69-fca9eb6c6821", "b13d903f601034ac3400d2625fef104e9b0940746c554193f6d9e85a84a966a1"), EnumChatFormatting.DARK_RED + "◆ Lava Rune"),
    SMOKEY(RenderUtils.getSkullItemStack("021cd9a0-3c5d-475c-9679-dd5e76b036aa", "e4d8a8d527f65a4f434f894f7ee42eb843015bda7927c63c6ea8a754afe9bb1b"), EnumChatFormatting.GRAY + "◆ Smokey Rune"),
    SNOW(RenderUtils.getSkullItemStack("919f138c-4d6a-4b0e-a149-572d483a5dab", "cdc57c75adf39ec6f0e0916049dd9671e98a8a1e600104e84e645c988950bd7"), EnumChatFormatting.WHITE + "◆ Snow Rune"),
    HOT(RenderUtils.getSkullItemStack("8a5e0f99-e60d-4a56-82ac-5894f996127b", "26660b016d05645fffd1f48b792d1abe5d8f30dc96567569ae1d982d250b693c"), EnumChatFormatting.RED + "◆ Hot Rune"),
    WHITE_SPIRAL(RenderUtils.getSkullItemStack("32342846-313d-477e-84f4-b3670671216c", "3ef2432ef305361384d4318df5bda5bd1ac2d9bea06d1f5cfead6dd87e37ddf5"), EnumChatFormatting.WHITE + "◆ White Spiral Rune"),
    ZAP(RenderUtils.getSkullItemStack("1aa3c21e-bf92-4ac1-af37-e7fa43df12df", "ed0947c40de6789f6cfa2370add2a04c9855e45fde9483d655101e9510288ee8"), EnumChatFormatting.YELLOW + "◆ Zap Rune"),
    FIRE_SPIRAL(RenderUtils.getSkullItemStack("1046d4f8-ed88-48bb-b43c-d6ac1a1cc6cc", "8301aa86cafd4b2d732a9b4894cfcfc65edc828e8571b45dbf0a3ba96575cccf"), EnumChatFormatting.GOLD + "◆ Fire Spiral Rune"),
    HEARTS(RenderUtils.getSkullItemStack("b42de589-036e-4a18-bc1d-6535da4d2ad2", "2c1c179ad51955f1522c48ea9931f09c162741b45e22e9d3feb682c7e5ed8274"), EnumChatFormatting.LIGHT_PURPLE + "◆ Hearts Rune"),
    ICE(RenderUtils.getSkullItemStack("d2d2c089-f346-43b9-be26-e8db4d040b7f", "cdc57c75adf39ec6f0e0916049dd9671e98a8a1e600104e84e645c988950bd7"), EnumChatFormatting.WHITE + "◆ Ice Rune"),
    MAGIC(RenderUtils.getSkullItemStack("a20a870b-27b2-4724-a9fe-16312b97b4a3", "24480e39ea63e347d268de83090d09984bf34394118848348bf4eb57490ce9d2"), EnumChatFormatting.LIGHT_PURPLE + "◆ Magical Rune"),
    ZOMBIE_SLAYER(RenderUtils.getSkullItemStack("4e667c5e-0bb2-4051-8743-ba41febbf1c2", "a8c4811395fbf7f620f05cc3175cef1515aaf775ba04a01045027f0693a90147"), EnumChatFormatting.DARK_GREEN + "◆ Pestilence Rune"),
    REDSTONE(RenderUtils.getSkullItemStack("55172d9f-ff31-4c91-b736-544c03704498", "87a7a894057d4a1ff22a161d76600f719da57916633f683808cf4d358bb73a21"), EnumChatFormatting.RED + "◆ Redstone Rune"),
    SPARKLING(RenderUtils.getSkullItemStack("42385eb4-18ee-40fa-9e26-bbd89eecfade", "f1e2428cb359988f4c4ff0e61de21385c62269de19a69762d773223b75dd1666"), EnumChatFormatting.WHITE + "◆ Sparkling Rune"),
    SPIRIT(RenderUtils.getSkullItemStack("7f3ab7da-b528-432d-b7d2-bc712c6fe33b", "c738b8af8d7ce1a26dc6d40180b3589403e11ef36a66d7c4590037732829542e"), EnumChatFormatting.AQUA + "◆ Spirit Rune"),
    BITE(RenderUtils.getSkullItemStack("1e948352-f0da-4c46-aabc-78e38548aa98", "43a1ad4fcc42fb63c681328e42d63c83ca193b333af2a426728a25a8cc600692"), EnumChatFormatting.GREEN + "◆ Bite Rune"),
    CLOUDS(RenderUtils.getSkullItemStack("53a5fbf8-3827-4273-856a-6e9f9804b1cd", "2273740d454de962484712f9835e35119b37ab867fa6982d5cc1f333c2334e59"), EnumChatFormatting.WHITE + "◆ Clouds Rune"),
    GOLDEN(RenderUtils.getSkullItemStack("18054871-4f7d-419b-8554-e3ff24bb159b", "35f4861aa5b22ee28a90e75dab45d221efd14c0b1ecc8ee998fb67e43bb8f3de"), EnumChatFormatting.YELLOW + "◆ Golden Rune"),
    MUSIC(RenderUtils.getSkullItemStack("3bd0d0b7-b884-40c2-8f30-ecab89cf6bcd", "3b481c31dc683bdcb7d375a7c5db7ac7adf9e9fe8b6c04a64931613e29fe470e"), EnumChatFormatting.AQUA + "◆ Music Rune"),
    COUTURE(RenderUtils.getSkullItemStack("16c96e6b-e03a-485f-80c3-6071311584b7", "734fb3203233efbae82628bd4fca7348cd071e5b7b52407f1d1d2794e31799ff"), EnumChatFormatting.GOLD + "◆ Couture Rune"),
    LIGHTNING(RenderUtils.getSkullItemStack("bf9a5bf2-27ca-4830-9425-a32c3f694b0e", "b85bcf7f82d34db89a95addf8e53253e2d9554c6fd2f2e39e24362d243a0ccf7"), EnumChatFormatting.BLUE + "◆ Lightning Rune"),
    SNAKE(RenderUtils.getSkullItemStack("5330ba61-6c91-4398-ad54-92bfea4b2c7f", "2c4a65c689b2d36409100a60c2ab8d3d0a67ce94eea3c1f7ac974fd893568b5d"), EnumChatFormatting.GREEN + "◆ Snake Rune"),
    WAKE(RenderUtils.getSkullItemStack("5e1846b5-674c-4b94-8165-d55fc29ca17c", "977c1fc93216e96d435cf962e1173de8d1a249b644894d72676eba732fcd56e7"), EnumChatFormatting.BLUE + "◆ Wake Rune"),
    TIDAL(RenderUtils.getSkullItemStack("7b793b14-f85d-47a3-9309-e0d687d74317", "69671047c6ade8a9c4d6a581bc26d284a54ae32e85c34ce69d81f92799bf3fbb"), EnumChatFormatting.DARK_BLUE.toString() + EnumChatFormatting.BOLD + "◆ Tidal Rune"),
    RAINBOW(RenderUtils.getSkullItemStack("729e8429-f1e4-4d24-bb56-c01fdc5ac5a2", "72114a80357463fe2f59e397aab9fc66d482a65d524f8870d21c724c18ecf757"), "§d◆ §cR§6a§ei§an§bb§9o§dw§9 Rune");

    private final ItemStack baseItem;
    private final String displayName;

    private RuneSacks(ItemStack baseItem, String displayName)
    {
        this.baseItem = baseItem;
        this.displayName = displayName;
    }

    public ItemStack getBaseItem()
    {
        return this.baseItem;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public static RuneSacks byName(String name)
    {
        name = name.substring(5);

        for (RuneSacks rune : values())
        {
            if (rune.name().toLowerCase(Locale.ROOT).equals(name))
            {
                return rune;
            }
        }
        return UNKNOWN;
    }
}