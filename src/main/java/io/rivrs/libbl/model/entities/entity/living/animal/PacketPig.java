package io.rivrs.libbl.model.entities.entity.living.animal;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.UUID;

@Getter
public class PacketPig extends PacketAnimal {

    private int boost = 0;

    public PacketPig(Location location) {
        super(EntityType.PIG, location);
    }

    public PacketPig(UUID uniqueId, Location location) {
        super(uniqueId, EntityType.PIG, location);
    }

    public PacketPig(int id, UUID uniqueId, Location location) {
        super(id, uniqueId, EntityType.PIG, location);
    }

    public void boost(int boost) {
        this.boost = boost;

        this.sendPacket(this.buildMetadataPacket());
    }

//    @Override
//    public List<EntityData> entityData(ClientVersion clientVersion) {
//        List<EntityData> entityData = super.entityData(clientVersion);
//        entityData.add(new EntityData(17, EntityDataTypes.INT, this.boost));
//        return entityData;
//    }
}
