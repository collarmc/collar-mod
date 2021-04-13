package team.catgirl.collar.mod.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

@FunctionalInterface
public interface ClientDisconnectCallback {

	Event<ClientDisconnectCallback> EVENT = EventFactory.createArrayBacked(ClientDisconnectCallback.class,
			(listeners) -> () -> {
				for (final ClientDisconnectCallback listener : listeners) {
					ActionResult result = listener.onDisconnected();
					if (result != ActionResult.PASS) {
						return result;
					}
				}
				return ActionResult.PASS;
			});

	ActionResult onDisconnected();

}
