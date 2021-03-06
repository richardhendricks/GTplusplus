package gtPlusPlus.xmod.eio.handler;

import java.lang.reflect.Field;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import gregtech.api.enums.Materials;
import gtPlusPlus.core.lib.LoadedMods;
import gtPlusPlus.core.util.Utils;
import gtPlusPlus.core.util.item.ItemUtils;
import gtPlusPlus.core.util.reflect.ReflectionUtils;
import gtPlusPlus.xmod.eio.material.MaterialEIO;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class HandlerTooltip_EIO {

	Item mIngot;
	ItemStack mPulsatingIron;
	ItemStack mConductiveIron;
	ItemStack mRedstoneAlloy;
	ItemStack mElectricalSteel;
	ItemStack mEnergeticAlloy;
	ItemStack mVibrantAlloy;
	ItemStack mSoularium;
	ItemStack mDarkIron;

	@SubscribeEvent
	public void onItemTooltip(ItemTooltipEvent event){
		//Is EIO loaded?
		if (LoadedMods.EnderIO){

			//Is the EIO Ingot Item null?
			//If it is, reflect in.
			if (mIngot == null){
				try {
					Class<?> oMainClass = Class.forName("crazypants.enderio.EnderIO");
					Class<?> oIngotClass = Class.forName("crazypants.enderio.material.ItemAlloy");
					if (oMainClass != null && oIngotClass != null){
						
						Field oAlloyField = oMainClass.getDeclaredField("itemAlloy");	
						oAlloyField.setAccessible(true);						
						Object oAlloy = oAlloyField.get(oMainClass);
						
						if (oAlloy != null){							
							if (oIngotClass.isInstance(oAlloy) || Item.class.isInstance(oAlloy)){
								mIngot = (Item) oAlloy;									
							}
								
						}
					}					
				}
				catch (Throwable e) {
				}
			}

			try {
				if (mIngot != null){
					//If the Item is an instance of ItemAlloy.class then proceed
					if (Class.forName("crazypants.enderio.material.ItemAlloy").isInstance(event.itemStack.getItem()) || event.itemStack.getUnlocalizedName().toLowerCase().contains("item.itemAlloy")){

						//If EIO Item Is not Null, see if the ItemStacks for the ingots are null
						//if they stacks are null, set the stack using the item set via reflection.
						//The meta data is based on the oridinals of the materials in the EIO enum.

						if (mElectricalSteel == null){
							mElectricalSteel = ItemUtils.simpleMetaStack(mIngot, 0, 1);
						}
						if (mEnergeticAlloy == null){
							mEnergeticAlloy = ItemUtils.simpleMetaStack(mIngot, 1, 1);
						}
						if (mVibrantAlloy == null){
							mVibrantAlloy = ItemUtils.simpleMetaStack(mIngot, 2, 1);
						}
						if (mRedstoneAlloy == null){
							mRedstoneAlloy = ItemUtils.simpleMetaStack(mIngot, 3, 1);
						}
						if (mConductiveIron == null){
							mConductiveIron = ItemUtils.simpleMetaStack(mIngot, 4, 1);
						}
						if (mPulsatingIron == null){
							mPulsatingIron = ItemUtils.simpleMetaStack(mIngot, 5, 1);
						}
						if (mDarkIron == null){
							mDarkIron = ItemUtils.simpleMetaStack(mIngot, 6, 1);
						}
						if (mSoularium == null){
							mSoularium = ItemUtils.simpleMetaStack(mIngot, 7, 1);
						}


						//If stacks match, add a tooltip.						
						if (this.mIngot != null){
							if (event.itemStack.getItem() == this.mIngot){
								if (event.itemStack.getItemDamage() == 0){
									event.toolTip.add(MaterialEIO.ELECTRICAL_STEEL.vChemicalFormula);										
								}
								else if (event.itemStack.getItemDamage() == 1){
									event.toolTip.add(MaterialEIO.ENERGETIC_ALLOY.vChemicalFormula);										
								}
								else if (event.itemStack.getItemDamage() == 2){
									event.toolTip.add(MaterialEIO.VIBRANT_ALLOY.vChemicalFormula);										
								}
								else if (event.itemStack.getItemDamage() == 3){
									event.toolTip.add(MaterialEIO.REDSTONE_ALLOY.vChemicalFormula);										
								}
								else if (event.itemStack.getItemDamage() == 4){
									event.toolTip.add(MaterialEIO.CONDUCTIVE_IRON.vChemicalFormula);										
								}
								else if (event.itemStack.getItemDamage() == 5){
									event.toolTip.add(MaterialEIO.PULSATING_IRON.vChemicalFormula);										
								}
								else if (event.itemStack.getItemDamage() == 6){
									event.toolTip.add(Materials.DarkSteel.mChemicalFormula);									
								}
								else if (event.itemStack.getItemDamage() == 7){
									event.toolTip.add(MaterialEIO.SOULARIUM.vChemicalFormula);											
								}
							}
						}						
					}
				}
			}
			catch (ClassNotFoundException e) {
			}
		}
	}

}
