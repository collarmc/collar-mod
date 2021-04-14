package team.catgirl.collar.mod.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

@FunctionalInterface
public interface ClientLoginCallback {

	Event<ClientLoginCallback> EVENT = EventFactory.createArrayBacked(ClientLoginCallback.class,
			(listeners) -> () -> {
				for (final ClientLoginCallback listener : listeners) {
					ActionResult result = listener.onLogin();
					
					if (result != ActionResult.PASS) {
						return result;
					}
				}

				return ActionResult.PASS;
			});

	ActionResult onLogin();

}
