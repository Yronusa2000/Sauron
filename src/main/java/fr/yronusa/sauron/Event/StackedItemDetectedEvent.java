package fr.yronusa.sauron.Event;

import fr.yronusa.sauron.TrackedItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class StackedItemDetectedEvent extends Event implements Cancellable {

    private final TrackedItem item;

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public StackedItemDetectedEvent(TrackedItem item) {
        this.item = item;
    }


    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public TrackedItem getTrackedItem() {
        return this.item;
    }

    public ItemStack getItemStack(){
        return this.item.getItem();
    }

    public UUID getUUID(){
        return this.item.getOriginalID();
    }
    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean b) {
    }

    public Player getPlayer() {
        return this.getTrackedItem().getPlayer();
    }
}
