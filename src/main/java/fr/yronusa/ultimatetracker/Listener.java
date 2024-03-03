package fr.yronusa.ultimatetracker;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Array;

public class Listener implements org.bukkit.event.Listener {

    public static String base64;

    @EventHandler
    public void onProut(PlayerBedEnterEvent e){
        ItemStack[] i = new ItemStack[]{e.getPlayer().getInventory().getItemInMainHand()};
        if(i[0].getAmount() != 0){
            System.out.println(i);
           // base64 = ItemSaver.itemStackArrayToBase64(i);
            System.out.println(base64.length());



        }
    }

    @EventHandler
    public void onProut2(PlayerBedLeaveEvent e) throws IOException {
        ItemStack[] i = ItemSaver.itemStackArrayFromBase64(base64);
        System.out.println(base64);
        e.getPlayer().getInventory().setItemInMainHand(i[0]);

    }

    @EventHandler
    public void onHandlingItem(PlayerItemHeldEvent e){
        ItemMutable i = new ItemMutable(e.getPlayer());
        if(TrackedItem.shouldBeTrack(i)){
            ItemMutable item = new ItemMutable(e.getPlayer());
            TrackedItem.startTracking(item);

        }
    }
}
