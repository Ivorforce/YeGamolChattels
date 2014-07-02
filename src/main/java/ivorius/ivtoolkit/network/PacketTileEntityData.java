package ivorius.ivtoolkit.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by lukas on 01.07.14.
 */
public class PacketTileEntityData implements IMessage
{
    private int x, y, z;
    private String context;
    private ByteBuf payload;

    public PacketTileEntityData()
    {
    }

    public PacketTileEntityData(int x, int y, int z, String context, ByteBuf payload)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.context = context;
        this.payload = payload;
    }

    public static <UTileEntity extends TileEntity & PartialUpdateHandler> PacketTileEntityData packetEntityData(UTileEntity entity, String context)
    {
        ByteBuf buf = Unpooled.buffer();
        entity.writeUpdateData(buf, context);
        return new PacketTileEntityData(entity.xCoord, entity.yCoord, entity.zCoord, context, buf);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        context = ByteBufUtils.readUTF8String(buf);
        payload = IvPacketHelper.readByteBuffer(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        ByteBufUtils.writeUTF8String(buf, context);
        IvPacketHelper.writeByteBuffer(buf, payload);
    }

    public static class Handler implements IMessageHandler<PacketTileEntityData, IMessage>
    {
        @Override
        public IMessage onMessage(PacketTileEntityData message, MessageContext ctx)
        {
            World world = Minecraft.getMinecraft().theWorld;
            TileEntity entity = world.getTileEntity(message.x, message.y, message.z);

            if (entity != null)
                ((PartialUpdateHandler) entity).readUpdateData(message.payload, message.context);

            return null;
        }
    }
}
