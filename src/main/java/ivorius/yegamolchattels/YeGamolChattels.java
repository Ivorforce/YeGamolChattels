/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import ivorius.ivtoolkit.network.*;
import ivorius.yegamolchattels.achievements.YGCAchievementList;
import ivorius.yegamolchattels.blocks.*;
import ivorius.yegamolchattels.crafting.YGCCrafting;
import ivorius.yegamolchattels.events.YGCFMLEventHandler;
import ivorius.yegamolchattels.events.YGCForgeEventHandler;
import ivorius.yegamolchattels.gui.YGCGuiHandler;
import ivorius.yegamolchattels.items.*;
import ivorius.yegamolchattels.materials.YGCMaterials;
import ivorius.yegamolchattels.worldgen.WorldGenFlax;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Logger;

@Mod(modid = YeGamolChattels.MODID, version = YeGamolChattels.VERSION, name = YeGamolChattels.NAME, guiFactory = "ivorius.yegamolchattels.gui.YGCConfigGuiFactory",
        dependencies = "required-after:ivtoolkit")
public class YeGamolChattels
{
    public static final String MODID = "yegamolchattels";
    public static final String VERSION = "1.1.1.1";
    public static final String NAME = "Ye Gamol Chattels";

    @Instance(value = MODID)
    public static YeGamolChattels instance;

    @SidedProxy(clientSide = "ivorius.yegamolchattels.client.ClientProxy", serverSide = "ivorius.yegamolchattels.server.ServerProxy")
    public static YGCProxy proxy;

    public static String filePathTexturesFull = "yegamolchattels:textures/mod/";
    public static String filePathTextures = "textures/mod/";
    public static String filePathOther = "other/";
    public static String textureBase = "yegamolchattels:";
    public static String soundBase = "yegamolchattels:";
    public static String otherBase = "yegamolchattels:";

    public static Logger logger;
    public static Configuration config;

    public static YGCGuiHandler guiHandler;

    public static SimpleNetworkWrapper network;

    public static YGCFMLEventHandler fmlEventHandler;
    public static YGCForgeEventHandler forgeEventHandler;

    public static int entityGhostGlobalID;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        YGCConfig.loadConfig(null);
        config.save();

        guiHandler = new YGCGuiHandler();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);

        fmlEventHandler = new YGCFMLEventHandler();
        fmlEventHandler.register();
        forgeEventHandler = new YGCForgeEventHandler();
        forgeEventHandler.register();

        YGCMaterials.init();

        YGCRegistryHandler.init();
    }

    @EventHandler
    public void load(FMLInitializationEvent event)
    {
        network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        network.registerMessage(PacketExtendedEntityPropertiesDataHandler.class, PacketExtendedEntityPropertiesData.class, 0, Side.CLIENT);
        network.registerMessage(PacketEntityDataHandler.class, PacketEntityData.class, 1, Side.CLIENT);
        network.registerMessage(PacketTileEntityDataHandler.class, PacketTileEntityData.class, 3, Side.CLIENT);
        network.registerMessage(PacketGuiActionHandler.class, PacketGuiAction.class, 4, Side.SERVER);
        network.registerMessage(PacketTileEntityClientEventHandler.class, PacketTileEntityClientEvent.class, 5, Side.SERVER);

        proxy.registerRenderers();

        YGCCrafting.init();
        YGCAchievementList.init();

        if (YGCConfig.genFlax)
            GameRegistry.registerWorldGenerator(new WorldGenFlax(), 100);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        YGCOutboundCommunicationHandler.init();
    }
}