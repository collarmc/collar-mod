package team.catgirl.collar.mod.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

@FunctionalInterface
public interface WorldLoadedCallback {

	Event<WorldLoadedCallback> EVENT = EventFactory.createArrayBacked(WorldLoadedCallback.class,
			(listeners) -> () -> {
				for (final WorldLoadedCallback listener : listeners) {
					ActionResult result = listener.onLoaded();
					if (result != ActionResult.PASS) {
						return result;
					}
				}
				return ActionResult.PASS;
			});

	ActionResult onLoaded();

}
