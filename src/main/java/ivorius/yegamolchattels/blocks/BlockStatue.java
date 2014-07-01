/***************************************************************************************************
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 **************************************************************************************************/

package ivorius.yegamolchattels.blocks;

import ivorius.ivtoolkit.blocks.IvBlockMultiblock;
import ivorius.ivtoolkit.blocks.IvTileEntityMultiBlock;
import ivorius.yegamolchattels.YeGamolChattels;
import ivorius.yegamolchattels.items.ItemStatue;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockStatue extends IvBlockMultiblock
{
    public static Object[] statueCrafting;

    public int statueMaterial;

    public static void initStatueCrafting()
    {
        statueCrafting = new Object[]{
                Items.quartz, "ygcGhost",
                Items.gunpowder, "Creeper",
                Items.bow, "Skeleton",
//                new ItemStack(Items.skull), Integer.valueOf(51), //Wither Skeleton // Doesn't have his own ID
                Items.string, "Spider",
                new ItemStack(Items.dye, 1, 15), "Giant",
                Items.rotten_flesh, "Zombie",
                Items.slime_ball, "Slime",
                Items.ghast_tear, "Ghast",
                Items.cooked_porkchop, "PigZombie",
                Items.ender_pearl, "Enderman",
                Items.spider_eye, "CaveSpider",
                Blocks.gravel, "Silverfish",
                Items.blaze_rod, "Blaze",
                Items.magma_cream, "LavaSlime",
                Items.ender_eye, "EnderDragon",
                Items.nether_star, "WitherBoss",
                Blocks.stone, "Bat",
                Items.potionitem, "Witch",
                Items.porkchop, "Pig",
                Blocks.wool, "Sheep",
                Items.milk_bucket, "Cow",
                Items.feather, "Chicken",
                new ItemStack(Items.dye, 1, 0), "Squid",
                Items.bone, "Wolf",
                Blocks.red_mushroom, "MushroomCow",
                Items.snowball, "SnowMan",
                Items.fish, "Ozelot",
                Blocks.iron_block, "VillagerGolem",
                Blocks.hay_block, "EntityHorse",
                Items.leather, "Villager"
        };
    }

    public BlockStatue(Material material, int statueMaterial)
    {
        super(material);
        this.statueMaterial = statueMaterial;
    }

    @Override
    public void parentBlockHarvestItem(World world, IvTileEntityMultiBlock tileEntity, int x, int y, int z, Block block, int metadata)
    {
        if (tileEntity instanceof TileEntityStatue)
        {
            Entity statueEntity = ((TileEntityStatue) tileEntity).getStatueEntity();

            if (statueEntity != null)
            {
                Item item = null;

                if (statueMaterial == 0)
                    item = Item.getItemFromBlock(YGCBlocks.statueStone);
                else if (statueMaterial == 1)
                    item = Item.getItemFromBlock(YGCBlocks.statuePlanks);
                else if (statueMaterial == 2)
                    item = Item.getItemFromBlock(YGCBlocks.statueGold);

                if (item != null)
                {
                    ItemStack stack = new ItemStack(item);
                    ItemStatue.setStatueEntity(stack, statueEntity);

                    dropBlockAsItem(world, x, y, z, stack);
                }
            }
        }
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public int getRenderType()
    {
        return -1;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        super.onNeighborBlockChange(world, x, y, z, block);

        if (world.isBlockIndirectlyGettingPowered(x, y, z))
        {
            IvTileEntityMultiBlock parent = getValidatedTotalParent(this, world, x, y, z);

            if (parent instanceof TileEntityStatue)
            {
                if (((TileEntityStatue) parent).letStatueComeAlive())
                    world.setBlock(x, y, z, Blocks.air, 0, 3);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
    {
        IvTileEntityMultiBlock parent = getValidatedTotalParent(this, world, x, y, z);

        if (parent instanceof TileEntityStatue)
        {
            TileEntityStatue tileEntityStatue = (TileEntityStatue) parent;
            if (tileEntityStatue.tryEquipping(player.getHeldItem()))
            {
                return true;
            }
            else
            {
                tileEntityStatue.dropEquipment();
                return true;
            }
        }

        return super.onBlockActivated(world, x, y, z, player, par6, par7, par8, par9);
    }

    @Override
    public IIcon getIcon(int par1, int par2)
    {
        if (statueMaterial == 0)
            return Blocks.stone.getIcon(0, 0);
        if (statueMaterial == 1)
            return Blocks.planks.getIcon(0, 0);
        if (statueMaterial == 2)
            return Blocks.gold_block.getIcon(0, 0);

        return Blocks.stone.getIcon(0, 0);
    }

    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {

    }

    @Override
    public String getItemIconName()
    {
        return YeGamolChattels.textureBase + getTextureName();
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int i)
    {
        return new TileEntityStatue();
    }
}
