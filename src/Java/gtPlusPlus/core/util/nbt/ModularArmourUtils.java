package gtPlusPlus.core.util.nbt;

import baubles.api.BaubleType;
import gregtech.api.util.GT_Utility;
import net.minecraft.block.Block.SoundType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ModularArmourUtils {

	public static ItemStack addComponent(ItemStack tArmour, ItemStack[] tComponents){
		if (tArmour != null){
			ItemStack rArmour = NBTUtils.writeItemsToGtCraftingComponents(tArmour, tComponents, true);
			if (rArmour != null){

			}
		}
		return null;
	}

	public static enum Modifiers {
		BOOST_HP("skill.hpboost"),
		BOOST_DEF("skill.defenceboost"),
		BOOST_SPEED("skill.speedboost"),
		BOOST_MINING("skill.miningboost"),
		BOOST_DAMAGE("skill.damageboost"),
		BOOST_HOLY("skill.holyboost");
		private String MODIFIER_NAME;
		private Modifiers (final String mModifier){
			this.MODIFIER_NAME = mModifier;
		}
		public String getModifier() {
			return this.MODIFIER_NAME;
		}		
		public boolean isValidLevel(int i){
			if (i >= 0 && i <= 10){
				return true;
			}
			return false;
		}
	}
	
	public static enum BT {
		TYPE_AMULET(BaubleType.AMULET, 0),
		TYPE_RING(BaubleType.RING, 1),
		TYPE_BELT(BaubleType.BELT, 2);
		private final BaubleType mType;
		private final int mID;
		private final String mBaubleType;
		private BT (final BaubleType tType, int tID){
			this.mType = tType;
			this.mID = tID;
			this.mBaubleType = tType.name().toLowerCase();
		}
		public BaubleType getType(){
			return this.mType;
		}
		public BaubleType getBaubleByID(int tID){
			if (tID == 0){
				return BaubleType.AMULET;
			}
			else if (tID == 1){
				return BaubleType.RING;
			}
			else if (tID == 2){
				return BaubleType.BELT;
			}
			else {
				return BaubleType.RING;
			}
		}
		
		public int getID(){
			return this.mID;
		}
		
		public String getTypeAsString(){
			return this.mBaubleType;
		}
	}
	
	public static void setModifierLevel(ItemStack aStack, Modifiers aMod, int aInt) {
		NBTTagCompound tNBT = NBTUtils.getNBT(aStack);
		if (aMod.isValidLevel(aInt)){
			tNBT.setInteger(aMod.getModifier(), aInt);
			GT_Utility.ItemNBT.setNBT(aStack, tNBT);			
		}
	}

	public static int getModifierLevel(ItemStack aStack, Modifiers aMod) {
		NBTTagCompound tNBT = NBTUtils.getNBT(aStack);
		return tNBT.getInteger(aMod.getModifier());
	}
	
	public static void setBaubleType(ItemStack aStack, BT aMod) {
		NBTTagCompound tNBT = NBTUtils.getNBT(aStack);
		if (aMod != null){
			tNBT.setInteger("mBaubleType", aMod.getID());
			GT_Utility.ItemNBT.setNBT(aStack, tNBT);			
		}
	}

	public static int getBaubleTypeID(ItemStack aStack) {
		NBTTagCompound tNBT = NBTUtils.getNBT(aStack);
		return tNBT.getInteger("mBaubleType");
	}
	
	public static BaubleType getBaubleType(ItemStack aStack) {
		NBTTagCompound tNBT = NBTUtils.getNBT(aStack);
		return getBaubleByID(tNBT.getInteger("mBaubleType"));
	}
	
	public static BaubleType getBaubleByID(int tID){
		if (tID == 0){
			return BaubleType.AMULET;
		}
		else if (tID == 1){
			return BaubleType.RING;
		}
		else if (tID == 2){
			return BaubleType.BELT;
		}
		else {
			return BaubleType.RING;
		}
	}
	
}