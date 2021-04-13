package team.catgirl.collar.mod.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

@FunctionalInterface
public interface ClientConnectCallback {

	Event<ClientConnectCallback> EVENT = EventFactory.createArrayBacked(ClientConnectCallback.class,
			(listeners) -> () -> {
				for (final ClientConnectCallback listener : listeners) {
					ActionResult result = listener.onConnected();
					
					if (result != ActionResult.PASS) {
						return result;
					}
				}

				return ActionResult.PASS;
			});

	ActionResult onConnected();

}
