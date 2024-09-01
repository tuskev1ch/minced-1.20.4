
package free.minced.mixin;

import com.mojang.authlib.GameProfile;
import free.minced.Minced;
import free.minced.modules.impl.combat.BackTrack;
import free.minced.primary.chat.ChatHandler;
import free.minced.systems.helpers.IEntityLiving;
import free.minced.systems.helpers.IOtherClientPlayerEntity;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import static free.minced.primary.IHolder.mc;

@Mixin(OtherClientPlayerEntity.class)
public class MixinOtherClientPlayerEntity extends AbstractClientPlayerEntity implements IOtherClientPlayerEntity {
    @Unique
    private double backUpX, backUpY, backUpZ;

    public MixinOtherClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    public void backTrackEntity() {

        backUpX = getX();
        backUpY = getY();
        backUpZ = getZ();

        if (Minced.getInstance().getModuleHandler().get(BackTrack.class).isEnabled()) {
            if (mc.player != null) {
                for (BackTrack.Position position : ((IEntityLiving) this).getBackTrack()) {
                    Vec3d pos = position.getPos();
                    setPosition(pos.x, pos.y, pos.z);
                }
            }
        }
    }

    public void resolve() {


        backUpX = getX();
        backUpY = getY();
        backUpZ = getZ();

        if (!Minced.getInstance().getModuleHandler().get(BackTrack.class).isEnabled()) {
            Vec3d from = new Vec3d(((IEntityLiving) this).getPrevServerX(), ((IEntityLiving) this).getPrevServerY(), ((IEntityLiving) this).getPrevServerZ());
            Vec3d to = new Vec3d(serverX, serverY, serverZ);

            if (mc.player != null && mc.player.squaredDistanceTo(from) > mc.player.squaredDistanceTo(to)) {
                setPosition(to.x, to.y, to.z);
            }
        }

    }

    public void releaseResolver() {
        if (backUpY != -999) {
            setPosition(backUpX, backUpY, backUpZ);
            backUpY = -999;
        }
    }
}