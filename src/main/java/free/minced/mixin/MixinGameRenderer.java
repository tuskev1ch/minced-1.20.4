package free.minced.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import free.minced.Minced;
import free.minced.modules.impl.render.NoRender;
import net.minecraft.client.gl.ShaderStage;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import free.minced.events.EventCollects;
import free.minced.events.impl.render.Render3DEvent;
import free.minced.framework.render.DrawHandler;
import free.minced.framework.render.shaders.GlProgram;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Consumer;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.resource.ResourceFactory;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
    @Shadow
    public abstract void render(float tickDelta, long startTime, boolean tick);

    @Shadow
    private float zoom;

    @Shadow
    private float zoomX;

    @Shadow
    private float zoomY;

    @Shadow
    private float viewDistance;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V", shift = At.Shift.BEFORE), method = "render")
    void postHudRenderHook(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        //FrameRateCounter.INSTANCE.recordFrame();
    }
    @Inject(method = "loadPrograms", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    void loadAllTheShaders(ResourceFactory factory, CallbackInfo ci, List<ShaderStage> stages, List<Pair<ShaderProgram, Consumer<ShaderProgram>>> shadersToLoad) {
        GlProgram.forEachProgram(loader -> shadersToLoad.add(new Pair<>(loader.getLeft().apply(factory), loader.getRight())));
    }

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z", opcode = Opcodes.GETFIELD, ordinal = 0), method = "renderWorld")
    void render3dHook(float tickDelta, long limitTime, @NotNull MatrixStack matrix, CallbackInfo ci) {
        DrawHandler.lastProjMat.set(RenderSystem.getProjectionMatrix());
        DrawHandler.lastModMat.set(RenderSystem.getModelViewMatrix());
        DrawHandler.lastWorldSpaceMatrix.set(matrix.peek().getPositionMatrix());
        EventCollects.call(new Render3DEvent(matrix, tickDelta));

    }
    @Inject(method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true)
    private void removeHurtCam(MatrixStack matrices, float delta, CallbackInfo ci) {
        NoRender noRender = Minced.getInstance().getModuleHandler().get(NoRender.class);

        if (noRender.canRemoveHurtCam()) {
            ci.cancel();
        }
    }
}