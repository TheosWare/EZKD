package com.ezkd.main;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.profiler.bases.Client;
import com.profiler.bases.Profile;
import com.profiler.main.Profiler;

public class EZKD extends JavaPlugin implements Listener{
	
	//Profiler plugin instance
	private Profiler profiler;
	
	@Override
	public void onEnable()
	{
		
		//Get the profiler plugin instance
		this.profiler = (Profiler) this.getServer().getPluginManager().getPlugin("Profiler");
		
		//Check if the profiler plugin is running
		if(this.profiler == null)
		{
			//Profiler plugin not running.
			this.getLogger().info("Profiler plugin not loaded, please try again.");
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		//Register event listener.
		this.getServer().getPluginManager().registerEvents(this, this);
		
		
	}
	
	private String getFM(String message)
	{
		return ChatColor.translateAlternateColorCodes('&', "&7[&8EZKD&7]&r " + message);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	
		if(cmd.getName().equalsIgnoreCase("kd"))
		{
			if(sender.hasPermission("ezkd.kd"))
			{
				if(args.length == 0)
				{
					if(sender instanceof Player)
					{
						Player p = (Player)sender;
						Client client = this.profiler.getClientManager().getClient(p);
						
						Profile profile = client.getProfile(this);
						
						if(profile == null)
						{
							profile = client.addProfile(this);
							
							profile.setValue("Kills", 0);
							profile.setValue("Deaths", 0);
							client.save();
						}
						
						p.sendMessage(getFM("&7Kills: &a" + profile.getValue("Kills") + " &7Deaths: &c" + profile.getValue("Deaths")));
						
					}else {
						sender.sendMessage(getFM("You must be a player to check your own KD."));
					}
				}else {
					String target = args[0];
					OfflinePlayer targp = this.getServer().getOfflinePlayer(target);
					
					if(targp == null)
					{
						sender.sendMessage(getFM("&7Unable to find: &8" + target));
						return true;
					}
					
					if(!targp.hasPlayedBefore())
					{
						sender.sendMessage(getFM("&7Unable to find: &8" + target));
						return true;
					}
					
					Client client = this.profiler.getClientManager().getClient(targp.getUniqueId());
					
					Profile profile = client.getProfile(this);
					
					if(profile == null)
					{
						profile = client.addProfile(this);
						profile.setValue("Kills", 0);
						profile.setValue("Deaths", 0);
						client.save();
					}
					
					sender.sendMessage(getFM("&7Kills: &a" + profile.getValue("Kills") + " &7Deaths: &c" + profile.getValue("Deaths")));
				}
			}
			return true;
		}
		
		return false;
	}
	
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e)
	{
		
		
		Player killed = e.getEntity(); //Victim object.
		
		//Get the client object of the victim.
		Client killedClient = this.profiler.getClientManager().getClient(killed);
		
		//Get the profile object of the victim.
		Profile killedProfile = killedClient.getProfile("EZKD");
		
		//Check if profile exists.
		if(killedProfile == null)
		{
			//It doesnt, so add a new profile.
			killedProfile = killedClient.addProfile("EZKD");
			
			//Set the default values.
			killedProfile.setValue("Kills", 0);
			killedProfile.setValue("Deaths", 0);
			
			//Save the client.
			killedClient.save();
		}
		
		//Get the current deaths.
		int killedDeaths = (int) killedProfile.getValue("Deaths");
		
		//Set the deaths.
		killedProfile.setValue("Deaths", killedDeaths + 1);
		
		//Save the client.
		killedClient.save();
		
		//Check if player was killed by another player
		if(e.getEntity().getKiller() == null)
		{
			return;
		}
		
		//Get killer
		Player killer = e.getEntity().getKiller(); //Killer object.
		
		//Get the client object of the killer.
		Client killerClient = this.profiler.getClientManager().getClient(killer);
		
		//Get the profile object of the killer
		Profile killerProfile = killerClient.getProfile("EZKD");
		
		//check if profile exists
		if(killerProfile == null)
		{
			//It doesnt, so add a new profile.
			killerProfile = killerClient.addProfile("EZKD");
			
			//Set default values.
			killerProfile.setValue("Kills", 0);
			killerProfile.setValue("Deaths", 0);
			
			//Save the client.
			killerClient.save();
		}
		
		//Get the current kills.
		int killerKills = (int) killerProfile.getValue("Kills");
		
		//Set the kills.
		killerProfile.setValue("Kills", killerKills + 1);
		
		//Save the client.
		killerClient.save();
		
		
	}

}
