package gtPlusPlus.core.material.state;

import gtPlusPlus.core.block.base.BasicBlock.BlockTypes;
import gtPlusPlus.core.block.base.BlockBaseModular;
import gtPlusPlus.core.item.base.bolts.BaseItemBolt;
import gtPlusPlus.core.item.base.dusts.BaseItemDust;
import gtPlusPlus.core.item.base.gears.BaseItemGear;
import gtPlusPlus.core.item.base.ingots.BaseItemIngot;
import gtPlusPlus.core.item.base.ingots.BaseItemIngotHot;
import gtPlusPlus.core.item.base.nugget.BaseItemNugget;
import gtPlusPlus.core.item.base.plates.BaseItemPlate;
import gtPlusPlus.core.item.base.plates.BaseItemPlateDouble;
import gtPlusPlus.core.item.base.rings.BaseItemRing;
import gtPlusPlus.core.item.base.rods.BaseItemRod;
import gtPlusPlus.core.item.base.rods.BaseItemRodLong;
import gtPlusPlus.core.item.base.rotors.BaseItemRotor;
import gtPlusPlus.core.item.base.screws.BaseItemScrew;
import gtPlusPlus.core.material.Material;
import gtPlusPlus.core.material.nuclear.NUCLIDE;
import gtPlusPlus.core.util.Utils;
import gtPlusPlus.core.util.item.ItemUtils;
import gtPlusPlus.xmod.gregtech.loaders.*;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class ItemSets {
	
	 private enum ItemComponents{		
			DUST,
			INGOT,
			HOTINGOT,
			BLOCK,
			FRAMEBOX,
			NUGGET,
			PLATE,
			PLATEDOUBLE,
			ROD,
			RODLONG,
			GEAR,
			SCREW,
			BOLT,
			RING,
			ROTOR,
			CELL,
			PLASMACELL;
		}
	
	public enum MaterialSet{
		STANDARD(ItemComponents.values()),
		RADIOACTIVE(new ItemComponents[]{ItemComponents.DUST, ItemComponents.ROD, ItemComponents.CELL, ItemComponents.INGOT}),
		GAS(new ItemComponents[]{}),
		FLUID(new ItemComponents[]{ItemComponents.DUST}),
		NONE(new ItemComponents[]{}),
		MINIMAL(new ItemComponents[]{ItemComponents.DUST, ItemComponents.INGOT, ItemComponents.PLATE}),
		LITHIUM7(NUCLIDE.LITHIUM7, new ItemComponents[]{ItemComponents.DUST});
		
		private Material materialToGenerateFrom;
		private final ItemComponents[] componentsToGenerate;

		MaterialSet(ItemComponents[] components) {
	        this.componentsToGenerate = components;
	    }
		
		MaterialSet(Material material, ItemComponents[] components) {
			this.materialToGenerateFrom = material;
	        this.componentsToGenerate = components;
	    }
		
		public Material getMaterialForSet(){
			return this.materialToGenerateFrom;
		}
	    
	    public ItemComponents[] getComponentsToGenerate() {
	        return this.componentsToGenerate;
	    }
		
	   
	    
	}
	

	public static boolean generateItemsFromMaterialSet(Material matInfo){
		
		Utils.LOG_INFO("using new method");
		MaterialSet currentSet = matInfo.materialSet;
		
		if (currentSet == null){
			return false;
		}
		
		ItemComponents[] toGenerate = currentSet.getComponentsToGenerate();
		String unlocalizedName = matInfo.getUnlocalizedName();
		String materialName = matInfo.getLocalizedName();
		short[] C = matInfo.getRGBA();
		int Colour = Utils.rgbtoHexValue(C[0], C[1], C[2]);
		boolean hotIngot = matInfo.requiresBlastFurnace();
		int materialTier = matInfo.vTier;
		int sRadiation = matInfo.vRadioationLevel;
		if (materialTier > 10 || materialTier <= 0){
			materialTier = 2;
		}	
		
		//Some Temp items.
		Item tempItem;
		Block tempBlock;
		
		if (toGenerate.length > 0){
		for (ItemComponents x : toGenerate){
			if (x == ItemComponents.INGOT){
				tempItem = new BaseItemIngot(matInfo); 
			}
			else if (x == ItemComponents.DUST){
				tempItem = new BaseItemDust("itemDust"+unlocalizedName, materialName, matInfo, Colour, "Dust", materialTier, sRadiation);
				tempItem = new BaseItemDust("itemDustTiny"+unlocalizedName, materialName, matInfo, Colour, "Tiny", materialTier, sRadiation);
				tempItem = new BaseItemDust("itemDustSmall"+unlocalizedName, materialName, matInfo, Colour, "Small", materialTier, sRadiation);
			}
			else if (x == ItemComponents.HOTINGOT){
				if (hotIngot){
					tempItem = new BaseItemIngotHot(matInfo);
				}
			}
			else if (x == ItemComponents.NUGGET){
				tempItem = new BaseItemNugget(matInfo);
			}
			else if (x == ItemComponents.PLATE){
				tempItem = new BaseItemPlate(matInfo);
			}
			else if (x == ItemComponents.PLATEDOUBLE){
				tempItem = new BaseItemPlateDouble(matInfo);
			}
			else if (x == ItemComponents.ROD){
				tempItem = new BaseItemRod(matInfo);
			}
			else if (x == ItemComponents.RODLONG){
				tempItem = new BaseItemRodLong(matInfo);
			}
			else if (x == ItemComponents.GEAR){
				tempItem = new BaseItemGear(matInfo);
			}
			else if (x == ItemComponents.BOLT){
				tempItem = new BaseItemBolt(matInfo);
			}
			else if (x == ItemComponents.SCREW){
				tempItem = new BaseItemScrew(matInfo);
			}
			else if (x == ItemComponents.RING){
				tempItem = new BaseItemRing(matInfo);
			}
			else if (x == ItemComponents.ROTOR){
				tempItem = new BaseItemRotor(matInfo);
			}
			else if (x == ItemComponents.BLOCK){
				tempBlock = new BlockBaseModular(unlocalizedName, materialName,BlockTypes.STANDARD, Colour);
			}
			else if (x == ItemComponents.FRAMEBOX){
				tempBlock = new BlockBaseModular(unlocalizedName, materialName,BlockTypes.FRAME, Colour);
			}			
		}
		
		try{
		RecipeGen_AlloySmelter.generateRecipes(matInfo);
		RecipeGen_Assembler.generateRecipes(matInfo);
		RecipeGen_BlastSmelter.generateARecipe(matInfo);
		RecipeGen_DustGeneration.generateRecipes(matInfo);	
		RecipeGen_Extruder.generateRecipes(matInfo);
		RecipeGen_Fluids.generateRecipes(matInfo);
		RecipeGen_Plates.generateRecipes(matInfo);
		RecipeGen_ShapedCrafting.generateRecipes(matInfo);
		} catch (Throwable T){}
		
		return true;
		}
		return false;
	}	
	
}
