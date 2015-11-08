package com.unascribed.lanthanoid.transform;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;

import net.malisis.core.asm.AsmHook;
import net.malisis.core.asm.MalisisClassTransformer;
import net.malisis.core.asm.mappings.McpFieldMapping;
import net.malisis.core.asm.mappings.McpMethodMapping;

public class LanthanoidTransformer extends MalisisClassTransformer {

	@Override
	public void registerHooks() {
		register(skyColorHook());
		register(renderDistanceHook());
		register(trackingDistanceHook());
	}

	public AsmHook trackingDistanceHook() {
		McpMethodMapping updatePlayerEntity = new McpMethodMapping("tryStartWachingThis", "func_73117_b", 
				"net.minecraft.entity.EntityTrackerEntry", "(Lnet/minecraft/entity/player/EntityPlayerMP;)V");
		
		AsmHook ah = new AsmHook(updatePlayerEntity);
		
		InsnList insert = new InsnList();
		
		McpFieldMapping trackedEntity = new McpFieldMapping("myEntity", "field_73132_a", 
				"net.minecraft.entity.EntityTrackerEntry", "Lnet/minecraft/entity/Entity;");
		
		// if (Lanthanoid.forceTrackingFor(p_73117_1_, this.myEntity) || 
		insert.add(new VarInsnNode(ALOAD, 1));
		insert.add(new VarInsnNode(ALOAD, 0));
		insert.add(trackedEntity.getInsnNode(GETFIELD));
		insert.add(new MethodInsnNode(INVOKESTATIC, "com/unascribed/lanthanoid/Lanthanoid", "forceTrackingFor",
				"(Lnet/minecraft/entity/player/EntityPlayerMP;Lnet/minecraft/entity/Entity;)Z", false));
		LabelNode falseLabel = new LabelNode();
		insert.add(new JumpInsnNode(IFNE, falseLabel));
		
		McpFieldMapping trackingDistanceThreshold = new McpFieldMapping("blocksDistanceThreshold", "field_73130_b", 
				"net.minecraft.entity.EntityTrackerEntry", "I");
		
		// d0 >= (double)(-this.blocksDistanceThreshold)
		InsnList match = new InsnList();
		match.add(new VarInsnNode(DLOAD, 2));
		match.add(new VarInsnNode(ALOAD, 0));
		match.add(trackingDistanceThreshold.getInsnNode(GETFIELD));
		
		McpFieldMapping trackingPlayers = new McpFieldMapping("trackingPlayers", "field_73134_o",
				"net.minecraft.entity.EntityTrackerEntry", "Ljava/util/Set;");
		
		// else if (this.trackingPlayers.contains(p_73117_1_))
		InsnList match2 = new InsnList();
		match2.add(new VarInsnNode(ALOAD, 0));
		match2.add(trackingPlayers.getInsnNode(GETFIELD));
		match2.add(new VarInsnNode(ALOAD, 1));
		match2.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Set", "contains", "(Ljava/lang/Object;)Z", true));
		
		ah.jumpTo(match).insert(insert).jumpTo(match2).insert(falseLabel);
		return ah;
	}
	
	public AsmHook renderDistanceHook() {
		McpMethodMapping isInRangeToRenderDist = new McpMethodMapping("isInRangeToRenderDist", "func_70112_a", 
				"net.minecraft.entity.Entity", "(D)Z");
		
		AsmHook ah = new AsmHook(isInRangeToRenderDist);
		
		InsnList insert = new InsnList();
		
		// d1 *= 64.0D * Lanthanoid.getDistanceWeight(this.renderDistanceWeight);
		insert.add(new MethodInsnNode(INVOKESTATIC, "com/unascribed/lanthanoid/Lanthanoid", "getDistanceWeight", "(D)D", false));
		
		McpFieldMapping renderDistanceWeight = new McpFieldMapping("renderDistanceWeight", "field_70155_l", 
				"net.minecraft.entity.Entity", "D");
		
		InsnList match = new InsnList();
		match.add(renderDistanceWeight.getInsnNode(GETFIELD));
		
		ah.jumpAfter(match).insert(insert);
		
		return ah;
	}

	public AsmHook skyColorHook() {
		McpMethodMapping skyColor = new McpMethodMapping("getSkyColor", "func_72833_a", 
				"net.minecraft.world.World", "(Lnet/minecraft/entity/Entity;F)Lnet/minecraft/util/Vec3;");
		
		AsmHook ah = new AsmHook(skyColor);
		
		InsnList insert = new InsnList();
		
		// return Lanthanoid.modifySkyColor(provider, entity, partialTicks);
		insert.add(new MethodInsnNode(INVOKESTATIC, "com/unascribed/lanthanoid/Lanthanoid", "modifySkyColor",
				"(Lnet/minecraft/world/WorldProvider;Lnet/minecraft/entity/Entity;F)Lnet/minecraft/util/Vec3;", false));
		insert.add(new InsnNode(ARETURN));
		
		InsnList match = new InsnList();
		match.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/world/WorldProvider", "getSkyColor",
				"(Lnet/minecraft/entity/Entity;F)Lnet/minecraft/util/Vec3;", false));
		
		ah.jumpTo(match).insert(insert);
		
		return ah;
	}
	
}
