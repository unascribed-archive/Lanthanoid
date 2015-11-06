package com.unascribed.lanthanoid.init;


import com.unascribed.lanthanoid.function.Consumer;
import com.unascribed.lanthanoid.gen.Generate;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

public class LCommands {

	public static void register(Consumer<ICommand> register) {
		register.accept(new CommandBase() {
			
			@Override
			public void processCommand(ICommandSender sender, String[] args) {
				EntityPlayer p = ((EntityPlayer)sender);
				Vec3 look = p.getLookVec();
				Block block = getBlockByText(sender, args[0]);
				int meta = parseIntBounded(sender, args[1], 0, 15);
				int length = parseIntBounded(sender, args[2], 1, 150);
				func_152373_a(sender, this, "command.lanspike.start", length);
				int changed = Generate.spike(p.worldObj, block, meta,
						(int)p.posX, (int)p.posY, (int)p.posZ,
						(float)look.xCoord, (float)look.yCoord, (float)look.zCoord,
						length);
				func_152373_a(sender, this, "command.lanspike.end", changed);
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
	}

}
