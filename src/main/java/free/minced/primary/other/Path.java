package free.minced.primary.other;

import net.minecraft.block.AirBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class Path {
    public Vec3d start;
    public Vec3d end;
    public List<Vec3d> path = new ArrayList<>();

    public Path(Vec3d from, Vec3d to) {
        this.start = from;
        this.end = to;
    }

    public Vec3d getStart() {
        return start;
    }

    public Vec3d getEnd() {
        return end;
    }

    public List<Vec3d> getPath() {
        return path;
    }

    public void calculatePath(float step, boolean enabled) {
        for (float i = 0; i < start.distanceTo(end); i += step) {
            float x = (float) (start.x + i * (end.x - start.x) / start.distanceTo(end));
            float y = enabled ? (float) (start.y + i * (end.y - start.y) / start.distanceTo(end)) : (float) start.y;
            float z = (float) (start.z + i * (end.z - start.z) / start.distanceTo(end));

            if (MinecraftClient.getInstance().world.getBlockState(new BlockPos((int) x, (int) y, (int) z)).getBlock() instanceof AirBlock) {
                path.add(new Vec3d(x, y, z));
            } else {
                path.add(new Vec3d(x, y + 1, z));
            }
        }
    }


}
