package de.JeterLP.CensorNizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

public class main extends JavaPlugin implements Listener{
    
    File list = new File(this.getDataFolder() + "\\blacklist.txt");

    public static ArrayList mutelist = new ArrayList();
    public ArrayList<String> blacklist = new ArrayList<String>();
    
    public FileConfiguration config; 
    
    private BukkitTask updateChecker;
    
    String version = getDescription().getVersion();
    
    public double currentversion;
    public double newversion;
    
        @Override
  public void onDisable() {
	System.out.println("[" + getDescription().getName() + " by JeterLP " + " Version: " + version + "] disabled");
        if(getConfig().getBoolean("Config.CheckForUpdates", true)){
        updateChecker.cancel();
        }else{}
        }
                        
	@Override
        public void onEnable() {            
            
        currentversion = Double.valueOf(getDescription().getVersion().split("b")[0].replaceFirst("\\.", ""));
            
        if(this.getConfig().getBoolean("Config.metrics", true)){
	try{
	Metrics metrics = new Metrics(this);
	metrics.start();
	}catch(IOException e){
	System.out.println("CensorNizer: Could not send information to: http://mcstats.org!");
	}
	} if(this.getConfig().getBoolean("Config.Metrics.enabled", false)){
	Metrics metrics;
	try {
	metrics = new Metrics(this);
	metrics.disable();
	} catch (IOException e) {
	System.out.println("CensorNizer: Could not send informations to: http://mcstats.org!");
        }
	}
            
        if (!(new File(this.getDataFolder(), "blacklist.txt")).exists()){
        try {
        defaultBlack();
        } catch (FileNotFoundException ex) {
        Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
        
	System.out.println("[" + getDescription().getName() + " by JeterLP" + " Version: " + version + "] enabling...");
        loadConfig();
        getServer().getPluginManager().registerEvents(this, this);
        File list = new File(this.getDataFolder() + "\\blacklist.txt");
        try {
        Scanner s = new Scanner(list);
        while(s.hasNextLine()){
        blacklist.add(s.nextLine());                
        }  
        System.out.println("[" + getDescription().getName() + " by JeterLP" + " Version: " + version + "] loaded " +  blacklist.size() + " words!");
        s.close();
        } catch (FileNotFoundException ex) {        
        System.err.println("[CensorNizer] The file: /plugins/CensorNizer/blacklist.txt is not found, please create it!");
        }
        currentversion = Double.valueOf(getDescription().getVersion().split("b")[0].replaceFirst("\\.", ""));      
        
        if(getConfig().getBoolean("Config.CheckForUpdates", true)){
        updateChecker = this.getServer().getScheduler().runTaskTimerAsynchronously(getPlugin(), new Runnable()
        {
	@Override
	public void run()
	{
	try
	{
	newversion = updateCheck(currentversion);						
	System.out.println("[CensorNizer] Actual version is " + version + " where repository version is " + newversion);						
	if(newversion > currentversion)
	{
	System.out.println("[CensorNizer] A new update has been released ! You can download it at http://dev.bukkit.org/server-mods/CensorNizer/");
	}
	else if(newversion < currentversion)
	{
	System.out.println("[CensorNizer] You are using a version that is higher than the repository version!");
	}
	else 
	{
	System.out.println("[CensorNizer] You are using the same version as the official repository.");
	}
	}
	catch (Exception e1)
	{
	e1.printStackTrace();
	}
        }
        }, 0, 24000);	                       
        }else{}
        
        }
             
        public void loadConfig(){
	if(new File("plugins/CensorNizer/config.yml").exists()){
        config = getConfig();
        config.options().copyDefaults(true);									
	}else{
	saveDefaultConfig();
        config = getConfig();
	config.options().copyDefaults(true);									
	}                                                  
        }
        @EventHandler(priority = EventPriority.LOWEST)
        
        public void onPlayerChat(AsyncPlayerChatEvent event){
        
        Player p = event.getPlayer();
        
        if(p.hasPermission("censornizer.bypass")){
        event.setCancelled(false);
        }else{
        String msg = event.getMessage().toLowerCase();
        
        for(int i = 0; i < blacklist.size(); i++){
        if(this.getConfig().getBoolean("Config.replace.use", true)){
        String replace = getConfig().getString("Config.replace.word");
        msg = msg.replace(blacklist.get(i), replace);       
        event.setMessage(msg);
        } 
        }
        
        if(this.getConfig().getBoolean("Config.kick", true)){
        for(int i = 0; i < blacklist.size(); i++){
        if(msg.contains(blacklist.get(i))){
        p.kickPlayer(getConfig().getString("Messages.kickmsg"));
        event.setCancelled(true);
        }
        }
        }
        
        if(this.getConfig().getBoolean("Config.mute", true)){
        for(int i = 0; i < blacklist.size(); i++){
        if(msg.contains(blacklist.get(i))){
        mutelist.add(p);    
        }    
        }
        }
        if(mutelist.contains(p)){
        event.setCancelled(true);
        p.sendMessage(ChatColor.DARK_RED + getConfig().getString("Messages.mutemsg"));
        }
        }
        if(blacklist.contains(" ")){
        blacklist.remove(" ");
        }
        
        }
        
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String commandlabel, String[] args){
        Player p = (Player) sender;
        if(cmd.getName().equalsIgnoreCase("censornizer")){
        if(args.length == 0){
        p.sendMessage(ChatColor.RED + "Too few arguments!"); 
        return true;
        }
        
        if(args[0].equalsIgnoreCase("unmute")){
        Player target = getServer().getPlayer(args[1]);
        try{
        if(p.hasPermission("censornizer.unmute")){     
        if(mutelist.contains(target)){
        p.sendMessage(ChatColor.DARK_RED + "The Player " + ChatColor.GREEN + target.getDisplayName() + ChatColor.DARK_RED + " is not muted!");  
        return true;
        }else{
        p.sendMessage(ChatColor.GREEN + "The Player " + ChatColor.GOLD + target.getDisplayName() + ChatColor.GREEN + " is now unmuted!");  
        mutelist.remove(target);
        return true; 
        }
        }else{
        p.sendMessage(ChatColor.DARK_RED + "You dont have permission!");
        return true;
        }
        }catch(NullPointerException e){
        p.sendMessage(ChatColor.DARK_RED + "The Player " + "is not online!");
        return true;
        }
        }
        }
        return false;    
        }
        
        private void defaultBlack() throws FileNotFoundException{
        try {
	PrintWriter stream = null;
	File folder = this.getDataFolder();
	if (folder != null) {
	folder.mkdirs();
	}
	String folderName = folder.getParent();

	stream = new PrintWriter(folderName + "/CensorNizer/blacklist.txt");						
        stream.println("motherfucker");
        stream.println("fucker");
        stream.println("dick");
        stream.println("asshole");
				
        stream.close();                                     
        }catch(FileNotFoundException exc){}
        }
        
        public double updateCheck(double currentVersion) throws Exception {
        String pluginUrlString = "http://dev.bukkit.org/server-mods/CensorNizer/files.rss";
        try {
        URL url = new URL(pluginUrlString);
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream());
        doc.getDocumentElement().normalize();
        NodeList nodes = doc.getElementsByTagName("item");
        Node firstNode = nodes.item(0);
        if (firstNode.getNodeType() == 1) {
        Element firstElement = (Element)firstNode;
        NodeList firstElementTagName = firstElement.getElementsByTagName("title");
        Element firstNameElement = (Element) firstElementTagName.item(0);
        NodeList firstNodes = firstNameElement.getChildNodes();
        return Double.valueOf(firstNodes.item(0).getNodeValue().replace("CensorNizer ", "").split("b")[0].replaceFirst("\\.", "").trim());
        }
        }
        catch(SocketException ex)
        {
        System.out.println("[CensorNizer] Could not get the informations about the latest version. Maybe your internet connection is broken?");
        }
        catch(UnknownHostException ex)
        {
        System.out.println("[CensorNizer] Could not find the host dev.bukkit. Maybe your server can't connect to the internet.");
        }
        catch(SAXParseException ex)
        {
        System.out.println("[CensorNizer] Could not connect to bukkitdev. It seems that something's blocking the plugin. Do you have an internet protection?");
        }
        return currentVersion;
        }
        
        public main getPlugin()
	{
        return this;
	}
        
        
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
    Player p = event.getPlayer();				
    if(p.isOp())
    {
    try
    {
    double newversion = updateCheck(currentversion);
				
    if(newversion > currentversion)
    {
    p.sendMessage(ChatColor.GREEN + "[CensorNizer] A new update has been released ! You can download it at http://dev.bukkit.org/server-mods/CensorNizer/");
    }
    }
    catch (Exception ex)
    {
    ex.printStackTrace();
    }
    }
    }
        
        
    }
