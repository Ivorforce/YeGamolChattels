package ivorius.ivtoolkit.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/**
 * Created by lukas on 01.07.14.
 */
public class PacketEntityData implements IMessage
{
    private int entityID;
    private String context;
    private ByteBuf payload;

    public PacketEntityData()
    {
    }

    public PacketEntityData(int entityID, String context, ByteBuf payload)
    {
        this.entityID = entityID;
        this.context = context;
        this.payload = payload;
    }

    public static <UEntity extends Entity & PartialUpdateHandler> PacketEntityData packetEntityData(UEntity entity, String context)
    {
        ByteBuf buf = Unpooled.buffer();
        entity.writeUpdateData(buf, context);
        return new PacketEntityData(entity.getEntityId(), context, buf);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        entityID = buf.readInt();
        context = ByteBufUtils.readUTF8String(buf);
        payload = IvPacketHelper.readByteBuffer(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(entityID);
        ByteBufUtils.writeUTF8String(buf, context);
        IvPacketHelper.writeByteBuffer(buf, payload);
    }

    public static class Handler implements IMessageHandler<PacketEntityData, IMessage>
    {
        @Override
        public IMessage onMessage(PacketEntityData message, MessageContext ctx)
        {
            World world = Minecraft.getMinecraft().theWorld;
            Entity entity = world.getEntityByID(message.entityID);

            if (entity != null)
                ((PartialUpdateHandler) entity).readUpdateData(message.payload, message.context);

            return null;
        }
    }
}
