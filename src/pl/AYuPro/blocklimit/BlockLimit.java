package pl.AYuPro.blocklimit;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;



public class BlockLimit extends JavaPlugin implements Listener {
	
	Plugin bllim = this;
	public static List<Integer> blist;
	String arg;
	WorldGuardPlugin wgPlugin = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
	
	public void onEnable(){
		bllim.getConfig().options().copyDefaults(true);
		bllim.saveDefaultConfig();
		blist = this.getConfig().getIntegerList("blockid");
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
	}
	
	public void onDisable(){
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String CommandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("blocklimlist")) {
			if (!(sender.isOp())){
				sender.sendMessage(ChatColor.RED + "У Вас не достаточно прав.");
				return false;
			} else {
				sender.sendMessage(blist.toString());
			}
			
		}
		return true;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (blist.contains(event.getBlock().getTypeId())){
			if (!(event.getPlayer() instanceof Player)){
				return;
			}
			if (wgPlugin.canBuild(event.getPlayer(), event.getBlock().getLocation())){
				Chunk ch = event.getBlock().getLocation().getChunk();
				if (NumBlocks(ch)>64){
					event.getPlayer().sendMessage("Запрещено ставить более 64 блоков в одном чанке указанных ID: " + blist.toString());
					event.setCancelled(true);
				} else {
					event.getPlayer().sendMessage("Лимит: " + (64 - NumBlocks(ch)));
					return;
				}
			} else {
				event.setCancelled(true);
			} 
		} 
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockMove(BlockPistonEvent event) {
		if (blist.contains(event.getBlock().getTypeId())){
				event.setCancelled(true);
		} 
	}

	private int NumBlocks(Chunk ch){
		int summ = 0;
		for(int y=1; y<=255; y++){
			for(int x=0; x<=15; x++){
				for(int z=0; z<=15; z++){
					int id = ch.getBlock(x, y, z).getTypeId();
					if ((blist.contains(id))&& (!(id == 0))){
						summ = summ + 1;
					}
				}
			}
		}
		return summ;
	}
	
	
}