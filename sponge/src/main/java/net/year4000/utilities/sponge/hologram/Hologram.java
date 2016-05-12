package net.year4000.utilities.sponge.hologram;

import static net.year4000.utilities.sponge.protocol.PacketTypes.V1_8.PLAY_CLIENT_DESTROY_ENTITIES;
import static net.year4000.utilities.sponge.protocol.PacketTypes.V1_8.PLAY_CLIENT_SPAWN_MOB;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import net.year4000.utilities.Conditions;
import net.year4000.utilities.reflection.Reflections;
import net.year4000.utilities.sponge.protocol.Packet;
import net.year4000.utilities.tuple.Pair;
import net.year4000.utilities.value.Value;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.ArmorStand;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import java.util.List;
import java.util.Optional;

/**
 * A light weight and fast system to get the holograms displayed.
 * This class will be returned from Holograms but must be passed through
 * the manager to do anything with it. It is only exposed to allow
 * the hologram to be stored as a reference somewhere else.
 */
public class Hologram {
    private static final double OFFSET = 0.25;
    private final HologramManager manager;
    private final Vector3d origin;
    private final Extent extent;
    private final List<Pair<ArmorStand, Packet>> armorStands;
    private Packet destroyPacket;
    private FrameBuffer buffer;

    /** Create the hologram at the location and with the buffer*/
    Hologram(HologramManager manager, Location<World> location, FrameBuffer buffer) {
        this.manager = Conditions.nonNull(manager, "manager");
        Conditions.nonNull(location, "location");
        this.origin = location.getPosition();
        this.extent = location.getExtent();
        this.buffer = Conditions.nonNull(buffer, "buffer");
        this.armorStands = Lists.newArrayListWithCapacity(buffer.size());
    }

    /** Send the hologram to the player */
    void send(Player player) {
        if (armorStands.size() == 0) {
            generate(); // Only create the packets once we need to send them
        }
        armorStands.forEach(pair -> manager.packets.sendPacket(player, pair.b.get()));
    }

    /** Destroy the hologram for the player */
    void destroy(Player player) {
        if (armorStands.size() > 0 && destroyPacket != null) {
            manager.packets.sendPacket(player, destroyPacket);
        }
    }

    /** Generate the packets to send and destroy the hologram */
    private void generate() {
        double y = (buffer.size() / 2) * -OFFSET; // Shift origin so hologram's origin is in the center
        for (Text line : buffer) {
            line(y += OFFSET, line).ifPresent(entity -> {
                Packet packet = new Packet(PLAY_CLIENT_SPAWN_MOB).inject(clazz -> {
                    try { // Swap out the default packet with the one of the entity
                        Class<?> entityClass = Reflections.clazz("net.minecraft.entity.EntityLivingBase").get();
                        return clazz.getConstructor(entityClass).newInstance(entity);
                    } catch (Exception error) {
                        throw Throwables.propagate(error);
                    }
                });
                armorStands.add(new Pair<>(entity, packet));
            });
        }
        // Destroy packet
        destroyPacket = new Packet(PLAY_CLIENT_DESTROY_ENTITIES).inject(clazz -> {
            int[] ids = new int[buffer.size()];
            for (int i = 0 ; i < buffer.size() ; i++) {
                ids[i] = armorStands.get(i).a.hashCode();
            }
            return Reflections.instance(clazz, ids).get();
        });
    }

    /** Create the armor stand entity */
    private Value<ArmorStand> line(double offset, Text text) {
        Optional<Entity> entityOptional = extent.createEntity(EntityTypes.ARMOR_STAND, origin.sub(0, offset, 0));
        if (entityOptional.isPresent()) {
            ArmorStand armorStand = (ArmorStand) entityOptional.get();
            armorStand.offer(Keys.ARMOR_STAND_HAS_GRAVITY, false);
            armorStand.offer(Keys.ARMOR_STAND_IS_SMALL, true);
            armorStand.offer(Keys.CUSTOM_NAME_VISIBLE, true);
            armorStand.offer(Keys.DISPLAY_NAME, text);
            armorStand.offer(Keys.INVISIBLE, true);
            return Value.of(armorStand);
        }
        return Value.empty();
    }
}
