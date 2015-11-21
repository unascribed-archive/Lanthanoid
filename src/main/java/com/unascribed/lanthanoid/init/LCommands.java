package com.unascribed.lanthanoid.init;

import com.unascribed.lanthanoid.Lanthanoid;
import com.unascribed.lanthanoid.function.Consumer;
import com.unascribed.lanthanoid.gen.Generate;
import com.unascribed.lanthanoid.network.SpaceShipCrashMessage;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

public class LCommands {

	public static void register(Consumer<ICommand> register) {
		register.accept(new CommandBase() {
			
			@Override
			public void processCommand(ICommandSender sender, String[] args) {
				if (args.length != 3) {
					throw new CommandException(getCommandUsage(sender));
				}
				EntityPlayer p = ((EntityPlayer)sender);
				Vec3 look = p.getLookVec();
				Block block = getBlockByText(sender, args[0]);
				int meta = parseIntBounded(sender, args[1], 0, 15);
				int length = parseIntBounded(sender, args[2], 1, 150);
				notifyOperators(sender, this, "command.lanspike.start", length);
				int changed = Generate.spike(p.worldObj, block, meta,
						(int)p.posX, (int)p.posY, (int)p.posZ,
						(float)look.xCoord, (float)look.yCoord, (float)look.zCoord,
						length);
				notifyOperators(sender, this, "command.gen.end", changed);
			}
			
			@Override
			public int getRequiredPermissionLevel() {
				return 4;
			}
			
			@Override
			public String getCommandUsage(ICommandSender sender) {
				return "/lanspike <TileName> <dataValue> <length>";
			}
			
			@Override
			public String getCommandName() {
				return "lanspike";
			}
		});
		register.accept(new CommandBase() {
			
			@Override
			public void processCommand(ICommandSender sender, String[] args) {
				if (args.length != 2) {
					throw new CommandException(getCommandUsage(sender));
				}
				EntityPlayer p = ((EntityPlayer)sender);
				Vec3 look = p.getLookVec();
				Block block = getBlockByText(sender, args[0]);
				int meta = parseIntBounded(sender, args[1], 0, 15);
				notifyOperators(sender, this, "command.lannacelle.start");
				int changed = Generate.nacelle(p.worldObj, block, meta,
						(int)p.posX, (int)p.posY, (int)p.posZ,
						(float)look.xCoord, (float)look.yCoord, (float)look.zCoord);
				notifyOperators(sender, this, "command.gen.end", changed);
			}
			
			@Override
			public int getRequiredPermissionLevel() {
				return 4;
			}
			
			@Override
			public String getCommandUsage(ICommandSender sender) {
				return "/lannacelle <TileName> <dataValue>";
			}
			
			@Override
			public String getCommandName() {
				return "lannacelle";
			}
		});
		register.accept(new CommandBase() {
			
			@Override
			public void processCommand(ICommandSender sender, String[] args) {
				if (args.length != 0) {
					throw new CommandException(getCommandUsage(sender));
				}
				EntityPlayer p = ((EntityPlayer)sender);
				Vec3 look = p.getLookVec();
				notifyOperators(sender, this, "command.lancell.start");
				int changed = Generate.powerCell(p.worldObj,
						(int)p.posX, (int)p.posY, (int)p.posZ,
						(float)look.xCoord, (float)look.yCoord, (float)look.zCoord);
				notifyOperators(sender, this, "command.gen.end", changed);
			}
			
			@Override
			public int getRequiredPermissionLevel() {
				return 4;
			}
			
			@Override
			public String getCommandUsage(ICommandSender sender) {
				return "/lancell";
			}
			
			@Override
			public String getCommandName() {
				return "lancell";
			}
		});
		register.accept(new CommandBase() {
			
			@Override
			public void processCommand(ICommandSender sender, String[] args) {
				if (args.length != 1) {
					throw new CommandException(getCommandUsage(sender));
				}
				SpaceShipCrashMessage.Type type = SpaceShipCrashMessage.Type.valueOf(args[0]);
				SpaceShipCrashMessage msg = new SpaceShipCrashMessage(type);
				Lanthanoid.inst.network.sendToDimension(msg, sender.getEntityWorld().provider.dimensionId);
			}
			
			@Override
			public int getRequiredPermissionLevel() {
				return 4;
			}
			
			@Override
			public String getCommandUsage(ICommandSender sender) {
				return "/lanship <Type>";
			}
			
			@Override
			public String getCommandName() {
				return "lanship";
			}
		});
	}

}
