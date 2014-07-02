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
import net.minecraftforge.common.IExtendedEntityProperties;

/**
 * Created by lukas on 01.07.14.
 */
public class PacketExtendedEntityPropertiesData implements IMessage
{
    private int entityID;
    private String context;
    private String eepKey;
    private ByteBuf payload;

    public PacketExtendedEntityPropertiesData()
    {
    }

    public PacketExtendedEntityPropertiesData(int entityID, String context, String eepKey, ByteBuf payload)
    {
        this.entityID = entityID;
        this.context = context;
        this.eepKey = eepKey;
        this.payload = payload;
    }

    public static PacketExtendedEntityPropertiesData packetEntityData(Entity entity, String eepKey, String context)
    {
        IExtendedEntityProperties eep = entity.getExtendedProperties(eepKey);

        if (!(eep instanceof PartialUpdateHandler))
            throw new IllegalArgumentException("IExtendedEntityProperties must implement IExtendedEntityPropertiesUpdateData to send update packets!");

        ByteBuf buf = Unpooled.buffer();
        ((PartialUpdateHandler) eep).writeUpdateData(buf, context);

        return new PacketExtendedEntityPropertiesData(entity.getEntityId(), context, eepKey, buf);
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        entityID = buf.readInt();
        context = ByteBufUtils.readUTF8String(buf);
        eepKey = ByteBufUtils.readUTF8String(buf);
        payload = IvPacketHelper.readByteBuffer(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(entityID);
        ByteBufUtils.writeUTF8String(buf, context);
        ByteBufUtils.writeUTF8String(buf, eepKey);
        IvPacketHelper.writeByteBuffer(buf, payload);
    }

    public static class Handler implements IMessageHandler<PacketExtendedEntityPropertiesData, IMessage>
    {
        @Override
        public IMessage onMessage(PacketExtendedEntityPropertiesData message, MessageContext ctx)
        {
            World world = Minecraft.getMinecraft().theWorld;
            Entity entity = world.getEntityByID(message.entityID);

            if (entity != null)
            {
                IExtendedEntityProperties eep = entity.getExtendedProperties(message.eepKey);

                if (eep != null)
                    ((PartialUpdateHandler) eep).readUpdateData(message.payload, message.context);
            }

            return null;
        }
    }
}
