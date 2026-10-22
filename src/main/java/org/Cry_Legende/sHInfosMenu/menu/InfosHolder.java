package org.Cry_Legende.sHInfosMenu.menu;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

/**
 * Marque l'inventaire du menu /infos et retient le joueur affiché : le listener reconnaît le menu
 * par son holder plutôt que par son titre, qui dépend de la config.
 */
public class InfosHolder implements InventoryHolder {

    private final OfflinePlayer target;
    private Inventory inventory;

    public InfosHolder(OfflinePlayer target) {
        this.target = target;
    }

    public OfflinePlayer getTarget() {
        return target;
    }

    void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
