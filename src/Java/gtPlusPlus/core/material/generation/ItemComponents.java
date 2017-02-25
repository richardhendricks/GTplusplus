package gtPlusPlus.core.material.generation;

import gtPlusPlus.core.material.Material;
import net.minecraft.item.ItemStack;

public class ItemComponents {

	private ItemStack DUST;
	private ItemStack INGOT;
	private ItemStack HOTINGOT;
	private ItemStack BLOCK;
	private ItemStack NUGGET;
	private ItemStack PLATE;
	private ItemStack PLATEDOUBLE;
	private ItemStack ROD;
	private ItemStack RODLONG;
	private ItemStack GEAR;
	private ItemStack SCREW;
	private ItemStack BOLT;
	private ItemStack RING;
	private ItemStack ROTOR;
	private ItemStack CELL;
	private ItemStack PLASMACELL;
	
	public ItemComponents(Material M){
		this.DUST = M.getDust(1);
		this.INGOT = M.getIngot(1);
		//this.HOTINGOT = M.getI(1);
		this.BLOCK = M.getBlock(1);
		this.NUGGET = M.getNugget(1);
		this.PLATE = M.getPlate(1);
		this.PLATEDOUBLE = M.getPlateDouble(1);
		this.ROD = M.getRod(1);
		this.RODLONG = M.getLongRod(1);
		this.GEAR = M.getGear(1);
		this.SCREW = M.getScrew(1);
		this.BOLT = M.getBolt(1);
		this.RING = M.getRing(1);
		this.ROTOR = M.getRotor(1);
		this.CELL = M.getCell(1);
		this.PLASMACELL = M.getPlasmaCell(1);
	}
	
}
