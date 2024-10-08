package free.minced.systems.draggable;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import net.minecraft.client.gui.screen.ChatScreen;
import free.minced.framework.render.DrawHandler;
import free.minced.modules.Module;

import static free.minced.primary.IHolder.mc;

/**
 * @author jbk
 * Draggable handler
 */

@Data
public class Draggable {

	@Expose
	@SerializedName("x")
	// current draggable x position
	private float x;
	@Expose
	@SerializedName("y")
	// current draggable y position
	private float y;

	// default draggable x, y position
	public float initX, initY;

	// position of draggable when dragging is started
	private float startX, startY;
	private boolean dragging;
	private int draggingValues;

	// dimensions of the draggable
	private float width, height;

	@Expose
	@SerializedName("name")
	// name of the draggable
	private final String name;

	// parent of the draggable
	private final Module parent;

	private static Draggable currentDraggable = null;
	/**
	 * constructor
	 *
	 * @param parent module parent of the draggable
	 * @param name   name of the draggable
	 * @param initX  default x position of the draggable
	 * @param initY  default y position of the draggable
	 * @param width  width of the draggable
	 * @param height height of the draggable
	 */
	public Draggable(Module parent, String name, float initX, float initY, float width, float height) {
		this.parent = parent;
		this.name = name;

		// position
		this.x = initX;
		this.y = initY;
		this.initX = initX;
		this.initY = initY;

		// dimensions
		this.width = width;
		this.height = height;
	}

	/**
	 * constructor
	 *
	 * @param parent module parent of the draggable
	 * @param name   name of the draggable
	 * @param initX  default x position of the draggable
	 * @param initY  default y position of the draggable
	 */
	public Draggable(Module parent, String name, float initX, float initY) {
		this.parent = parent;
		this.name = name;

		// position
		this.x = initX;
		this.y = initY;
		this.initX = initX;
		this.initY = initY;
	}

	public static float scrollAnimate(float endPoint, float current, float speed) {
		return current + (endPoint - current) * speed;
	}

	public final void onRender(int mouseX, int mouseY) {
		if (!(mc.currentScreen instanceof ChatScreen)) {
			dragging = false;
			return;
		}

		if (dragging) {
			this.x = scrollAnimate((normaliseX() - startX), getX(), .15f);
			this.y = scrollAnimate((normaliseY() - startY), getY(), .15f);
		}
	}


	public final void onClick() {
		if (isHovering() && currentDraggable == null) {
			dragging = true;
			startX = (int) (normaliseX() - x);
			startY = (int) (normaliseY() - y);
			currentDraggable = this;
		}
	}

	public boolean isHovering() {
		return normaliseX() > Math.min(x, x + width) && normaliseX() < Math.max(x, x + width) && normaliseY() > Math.min(y, y + height) && normaliseY() < Math.max(y, y + height);
	}
	public int normaliseX() {
		return (int) (mc.mouse.getX() / DrawHandler.getScaleFactor());
	}

	public int normaliseY() {
		return (int) (mc.mouse.getY() / DrawHandler.getScaleFactor());
	}

	// handle mouse release
	public final void onRelease(int button) {
		dragging = false;
		currentDraggable = null;
	}
}