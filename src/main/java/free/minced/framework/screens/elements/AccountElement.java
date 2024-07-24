package free.minced.framework.screens.elements;

import free.minced.mixin.accesors.IMinecraftClient;
import lombok.Getter;
import free.minced.framework.screens.elements.util.AltManagerConfig;
import free.minced.framework.interfaces.api.CustomElement;
import free.minced.framework.screens.MainScreen;
import free.minced.framework.animation.color.ColorAnimation;
import free.minced.framework.animation.normal.Animation;
import free.minced.framework.animation.normal.Easing;
import free.minced.framework.render.DrawHandler;
import free.minced.framework.color.ClientColors;
import free.minced.framework.font.Fonts;
import net.fabricmc.fabric.mixin.networking.client.accessor.MinecraftClientAccessor;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.ReporterEnvironment;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Optional;
import java.util.UUID;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.UserApiService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.SocialInteractionsManager;


@Getter
public class AccountElement extends CustomElement {

    private final String username;
    private ColorAnimation firstColorAnimation;
    private ColorAnimation secondColorAnimation;
    private Animation hoverAnimation, alphaAnimation, yAnimation;
    private boolean removing;
    private float animatedY;
    private AltManagerElement parentElement;

    public AccountElement(String username) {
        this.username = username;
    }

    @Override
    public void init() {
        this.firstColorAnimation = new ColorAnimation(500);
        this.secondColorAnimation = new ColorAnimation(500);
        hoverAnimation = new Animation(Easing.LINEAR, 300);
        alphaAnimation = new Animation(Easing.LINEAR, 600);
        yAnimation = new Animation(Easing.EASE_IN_OUT_CUBIC, 400);

        super.init();
    }

    public void render(MatrixStack pPoseStack, AltManagerElement parentElement, float x, float y, float width, float height, int mouseX, int mouseY) {
        this.parentElement = parentElement;
        this.x = x;
        this.y = y + parentElement.getScrollHandler().getScroll();
        this.width = width;
        this.height = height;

        boolean selected = mc.getSession().getUsername().equals(username); // дада я умный


        yAnimation.run(y);
        if (yAnimation.isFinished()) {
            alphaAnimation.run(removing ? 0 : 1);
            secondColorAnimation.run(isHovered(x, animatedY + parentElement.getScrollHandler().getScroll(), width, height, mouseX, mouseY) || selected ? ClientColors.getSecondColor() : ClientColors.getBackgroundColor().withAlpha(255));
            firstColorAnimation.run(isHovered(x, animatedY + parentElement.getScrollHandler().getScroll(), width, height, mouseX, mouseY) || selected ? ClientColors.getFirstColor() : ClientColors.getBackgroundColor().withAlpha(255));
            hoverAnimation.run(isHovered(x, animatedY + parentElement.getScrollHandler().getScroll(), width, height, mouseX, mouseY) || selected ? 1 : 0);
        }
        animatedY = yAnimation.getValue();


        // бг
        DrawHandler.drawRound(pPoseStack, x, animatedY + parentElement.getScrollHandler().getScroll(), width, height,3,  ClientColors.getSecondaryBackgroundColor().withAlpha((155 * parentElement.getOpenAndCloseAnimation().getValue())));
        // оверлей, который увеличивает свою прозрачность при наводке на элемент
        if (isHovered(x, animatedY + parentElement.getScrollHandler().getScroll(), width, height, mouseX, mouseY) || selected) {
            if (MainScreen.altManagerElement.isDisplayingElement()) {
                   DrawHandler.drawRound(pPoseStack, x, animatedY + parentElement.getScrollHandler().getScroll(), width, height,3,  ClientColors.GRAY.withAlpha(((100 * hoverAnimation.getValue()) * alphaAnimation.getValue()) * parentElement.getOpenAndCloseAnimation().getValue()));
            }
        }

        // рендерим ник XYETA
        if (alphaAnimation.getValue() * parentElement.getOpenAndCloseAnimation().getValue() > 0.05) {
            Fonts.SEMI_16.drawCenteredString(pPoseStack, username, x + width / 2f, animatedY + 7.5F + parentElement.getScrollHandler().getScroll(), ClientColors.getFontColor().withAlpha((255 * alphaAnimation.getValue()) * parentElement.getOpenAndCloseAnimation().getValue()).getRGB());
        }


       // DrawHandler.drawRect(pPoseStack, x, animatedY + parentElement.getScrollHandler().getScroll(), width, height,  ClientColors.GRAY.withAlpha(((100 * hoverAnimation.getValue()) * alphaAnimation.getValue()) * parentElement.getOpenAndCloseAnimation().getValue()));
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {

        // позволяем менять юзернейм при нажатии на элемент
        if (isHovered(x, y, width, height, mouseX, mouseY)) {
            switch (button) {
                // позволяем менять юзернейм при нажатии на элемент левой кнопкой мыши
/*                Session session = new Session("scallydima123f", UUID.randomUUID().toString(), "", Optional.empty(), Optional.empty(), Session.AccountType.MOJANG);
                StringUtil.setSession(session);*/
                case 0 -> setSession(new Session(getUsername(), UUID.randomUUID(), "", Optional.of("1"), Optional.of("1"), Session.AccountType.MOJANG));                // позволяем удалять аккаунт при нажатии на элемент правой кнопкой мыши
                case 1 -> {
                    removing = true;
                    AltManagerConfig.saveAccounts();

                }
            }
        }
        super.mouseClicked(mouseX, mouseY, button);
    }
    public void setSession(Session session) {
        IMinecraftClient mca = (IMinecraftClient) mc;
        mca.setSessionT(session);
        mc.getGameProfile().getProperties().clear();
        UserApiService apiService;
        apiService = UserApiService.OFFLINE;
        mca.setUserApiService(apiService);
        mca.setSocialInteractionsManagerT(new SocialInteractionsManager(mc, apiService));
        mca.setProfileKeys(ProfileKeys.create(apiService, session, mc.runDirectory.toPath()));
        mca.setAbuseReportContextT(AbuseReportContext.create(ReporterEnvironment.ofIntegratedServer(), apiService));
    }
    @Override
    public boolean isDisplayingElement() {
        return true;
    }
}
