package gtPlusPlus.xmod.gregtech.common.tileentities.machines.multi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import gregtech.api.GregTech_API;
import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_OutputBus;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Utility;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.core.lib.CORE;
import gtPlusPlus.core.util.Utils;
import gtPlusPlus.core.util.math.MathUtils;
import gtPlusPlus.xmod.gregtech.api.gui.GUI_MultiMachine;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.base.GregtechMeta_MultiBlockBase;
import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class GregtechMetaTileEntity_IndustrialSifter
extends GregtechMeta_MultiBlockBase {
	private boolean controller;

	public GregtechMetaTileEntity_IndustrialSifter(final int aID, final String aName, final String aNameRegional) {
		super(aID, aName, aNameRegional);
	}

	public GregtechMetaTileEntity_IndustrialSifter(final String aName) {
		super(aName);
	}

	@Override
	public IMetaTileEntity newMetaEntity(final IGregTechTileEntity aTileEntity) {
		return new GregtechMetaTileEntity_IndustrialSifter(this.mName);
	}

	@Override
	public String[] getDescription() {
		return new String[]{
				"Controller Block for the Industrial Sifter",
				"Size[WxHxL]: 5x3x5",
				"Controller (Center Bottom)",
				"1x Input Bus (Any top or bottom edge casing)",
				"4x Output Bus (Any top or bottom edge casing)",
				"1x Maintenance Hatch (Any top or bottom edge casing)",
				"1x Energy Hatch (Any top or bottom edge casing)",
				"18x Sieve Grate (Top and Middle 3x3)",
				"Sieve Casings for the rest (47)",
				CORE.GT_Tooltip};
	}

	@Override
	public ITexture[] getTexture(final IGregTechTileEntity aBaseMetaTileEntity, final byte aSide, final byte aFacing, final byte aColorIndex, final boolean aActive, final boolean aRedstone) {
		if (aSide == aFacing) {
			return new ITexture[]{Textures.BlockIcons.CASING_BLOCKS[TAE.GTPP_INDEX(21)], new GT_RenderedTexture(aActive ? Textures.BlockIcons.OVERLAY_FRONT_VACUUM_FREEZER_ACTIVE : Textures.BlockIcons.OVERLAY_FRONT_VACUUM_FREEZER)};
		}
		return new ITexture[]{Textures.BlockIcons.CASING_BLOCKS[TAE.GTPP_INDEX(21)]};
	}

	@Override
	public Object getClientGUI(final int aID, final InventoryPlayer aPlayerInventory, final IGregTechTileEntity aBaseMetaTileEntity) {
		return new GUI_MultiMachine(aPlayerInventory, aBaseMetaTileEntity, this.getLocalName(), "MacerationStack.png");
	}

	@Override
	public GT_Recipe.GT_Recipe_Map getRecipeMap() {
		return GT_Recipe.GT_Recipe_Map.sSifterRecipes;
	}

	/*@Override
	public boolean isCorrectMachinePart(ItemStack aStack) {
		return true;
	}*/

	@Override
	public boolean isFacingValid(final byte aFacing) {
		return aFacing > 1;
	}

	@Override
	public void onPreTick(final IGregTechTileEntity aBaseMetaTileEntity, final long aTick) {
		super.onPreTick(aBaseMetaTileEntity, aTick);
		if ((aBaseMetaTileEntity.isClientSide()) && (aBaseMetaTileEntity.isActive()) && (aBaseMetaTileEntity.getFrontFacing() != 1) && (aBaseMetaTileEntity.getCoverIDAtSide((byte) 1) == 0) && (!aBaseMetaTileEntity.getOpacityAtSide((byte) 1))) {
			if (MathUtils.randInt(0, 5) == 5){
				final Random tRandom = aBaseMetaTileEntity.getWorld().rand;
				aBaseMetaTileEntity.getWorld().spawnParticle("reddust", (aBaseMetaTileEntity.getXCoord() + 0.8F) - (tRandom.nextFloat() * 0.6F), aBaseMetaTileEntity.getYCoord() + 0.3f + (tRandom.nextFloat() * 0.2F), (aBaseMetaTileEntity.getZCoord() + 1.2F) - (tRandom.nextFloat() * 1.6F), 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	public void startSoundLoop(final byte aIndex, final double aX, final double aY, final double aZ) {
		super.startSoundLoop(aIndex, aX, aY, aZ);
		if (aIndex == 1) {
			GT_Utility.doSoundAtClient(GregTech_API.sSoundList.get(Integer.valueOf(201)), 10, 1.0F, aX, aY, aZ);
		}
	}

	@Override
	public void startProcess() {
		this.sendLoopStart((byte) 1);
	}

	ItemStack[] mInputStacks;
	int[] cloneChances;
	GT_Recipe baseRecipe;
	GT_Recipe cloneRecipe;

	@Override
	public boolean checkRecipe(final ItemStack aStack) {

		Utils.LOG_WARNING("1");

		//Get inputs.
		final ArrayList<ItemStack> tInputList = this.getStoredInputs();
		for (int i = 0; i < (tInputList.size() - 1); i++) {
			for (int j = i + 1; j < tInputList.size(); j++) {
				if (GT_Utility.areStacksEqual(tInputList.get(i), tInputList.get(j))) {
					if (tInputList.get(i).stackSize >= tInputList.get(j).stackSize) {
						tInputList.remove(j--);
					} else {
						tInputList.remove(i--);
						break;
					}
				}
			}
		}

		Utils.LOG_WARNING("2");

		//Temp var
		final ItemStack[] tInputs = Arrays.copyOfRange(tInputList.toArray(new ItemStack[tInputList.size()]), 0, 2);

		//Don't check the recipe if someone got around the output bus size check.
		if (this.mOutputBusses.size() != 4){
			return false;
		}


		Utils.LOG_WARNING("3");

		//Make a recipe instance for the rest of the method.
		final GT_Recipe tRecipe = GT_Recipe.GT_Recipe_Map.sSifterRecipes.findRecipe(this.getBaseMetaTileEntity(), false, 9223372036854775807L, null, tInputs);

		if (tRecipe != null) {
			this.baseRecipe = tRecipe.copy();
		}

		if ((this.cloneRecipe != tRecipe) || (this.cloneRecipe == null)){
			this.cloneRecipe = tRecipe.copy();
			Utils.LOG_WARNING("Setting Recipe");
		}
		if ((this.mInputStacks != tRecipe.mInputs) || (this.mInputStacks == null)){
			this.mInputStacks = tRecipe.mInputs;
			Utils.LOG_WARNING("Setting Recipe Inputs");
		}
		if ((this.cloneChances != tRecipe.mChances) || (this.cloneChances == null)){
			this.cloneChances = tRecipe.mChances.clone();
			Utils.LOG_WARNING("Setting Chances");
		}

		for (int r=0;r<this.cloneChances.length;r++){
			Utils.LOG_WARNING("Original map Output["+r+"] chance = "+this.cloneChances[r]);
		}

		Utils.LOG_WARNING("3.1");

		//Change bonus chances
		int[] outputChances;

		Utils.LOG_WARNING("3.2");

		if (this.cloneRecipe.mChances != null){
			outputChances = this.cloneRecipe.mChances.clone();

			Utils.LOG_WARNING("3.3");

			for (int r=0;r<outputChances.length;r++){
				Utils.LOG_WARNING("Output["+r+"] chance = "+outputChances[r]);
				if (outputChances[r]<10000){
					final int temp = outputChances[r];
					if ((outputChances[r] < 8000) && (outputChances[r] >= 1)){
						outputChances[r] = temp+1200;
						Utils.LOG_WARNING("Output["+r+"] chance now = "+outputChances[r]);
					}
					else if ((outputChances[r] < 9000) && (outputChances[r] >= 8000)){
						outputChances[r] = temp+400;
						Utils.LOG_WARNING("Output["+r+"] chance now = "+outputChances[r]);
					}
					else if ((outputChances[r] <= 9900) && (outputChances[r] >= 9000)){
						outputChances[r] = temp+100;
						Utils.LOG_WARNING("Output["+r+"] chance now = "+outputChances[r]);
					}
				}
			}

			Utils.LOG_WARNING("3.4");

			//Rebuff Drop Rates for % output
			this.cloneRecipe.mChances = outputChances;

		}


		Utils.LOG_WARNING("4");


		final int tValidOutputSlots = this.getValidOutputSlots(this.getBaseMetaTileEntity(), this.cloneRecipe, tInputs);
		Utils.LOG_WARNING("Sifter - Valid Output Hatches: "+tValidOutputSlots);

		//More than or one input
		if ((tInputList.size() > 0) && (tValidOutputSlots >= 1)) {
			if ((this.cloneRecipe != null) && (this.cloneRecipe.isRecipeInputEqual(true, null, tInputs))) {
				Utils.LOG_WARNING("Valid Recipe found - size "+this.cloneRecipe.mOutputs.length);
				this.mEfficiency = (10000 - ((this.getIdealStatus() - this.getRepairStatus()) * 1000));
				this.mEfficiencyIncrease = 10000;


				this.mEUt = (-this.cloneRecipe.mEUt);
				this.mMaxProgresstime = Math.max(1, (this.cloneRecipe.mDuration/5));
				final ItemStack[] outputs = new ItemStack[this.cloneRecipe.mOutputs.length];
				for (int i = 0; i < this.cloneRecipe.mOutputs.length; i++){
					if (this.getBaseMetaTileEntity().getRandomNumber(7500) < this.cloneRecipe.getOutputChance(i)){
						Utils.LOG_WARNING("Adding a bonus output");
						outputs[i] = this.cloneRecipe.getOutput(i);
					}
					else {
						Utils.LOG_WARNING("Adding null output");
						outputs[i] = null;
					}
				}

				this.mOutputItems = outputs;
				this.sendLoopStart((byte) 20);
				this.updateSlots();
				//tRecipe.mChances = baseRecipe.mChances;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean checkMachine(final IGregTechTileEntity aBaseMetaTileEntity, final ItemStack aStack) {
		Utils.LOG_MACHINE_INFO("Checking structure for Industrial Sifter.");
		final int xDir = ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()).offsetX * 2;
		final int zDir = ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()).offsetZ * 2;

		int tAmount = 0;
		this.controller = false;
		for (int i = -2; i < 3; i++) {
			for (int j = -2; j < 3; j++) {
				for (int h = 0; h < 3; h++) {

					final IGregTechTileEntity tTileEntity = aBaseMetaTileEntity.getIGregTechTileEntityOffset(xDir + i, h, zDir + j);

					String sHeight = "";
					if (h == 2){
						sHeight = "top";
					}
					else if (h == 1){
						sHeight = "middle";
					}
					else {
						sHeight = "bottom";
					}

					// Sifter Floor/Roof inner 3x3
					if (((i != -2) && (i != 2)) && ((j != -2) && (j != 2))) {
						if (h != 0){
							if (!this.addToMachineList(tTileEntity, TAE.GTPP_INDEX(21))) {
								if (aBaseMetaTileEntity.getBlockOffset(xDir + i, h, zDir + j) != ModBlocks.blockCasings2Misc) {
									Utils.LOG_MACHINE_INFO("Sifter Casing(s) Missing from one of the "+sHeight+" layers inner 3x3.");
									Utils.LOG_MACHINE_INFO("Instead, found "+aBaseMetaTileEntity.getBlockOffset(xDir + i, h, zDir + j).getLocalizedName());
									return false;
								}
								if (aBaseMetaTileEntity.getMetaIDOffset(xDir + i, h, zDir + j) != 6) {
									Utils.LOG_MACHINE_INFO("Sifter Casing(s) Missing from one of the "+sHeight+" layers inner 3x3. Wrong Meta for Casing. Found:"+aBaseMetaTileEntity.getBlockOffset(xDir + i, h, zDir + j).getLocalizedName()+" with meta:"+aBaseMetaTileEntity.getMetaIDOffset(xDir + i, h, zDir + j));
									return false;
								}
							}
						}
						else {
							if (!this.addToMachineList(tTileEntity, TAE.GTPP_INDEX(21))) {
								if (aBaseMetaTileEntity.getBlockOffset(xDir + i, h, zDir + j) != ModBlocks.blockCasings2Misc) {
									Utils.LOG_MACHINE_INFO("Sifter Casing(s) Missing from one of the "+sHeight+" layers inner 3x3.");
									Utils.LOG_MACHINE_INFO("Instead, found "+aBaseMetaTileEntity.getBlockOffset(xDir + i, h, zDir + j).getLocalizedName());
									return false;
								}
								if (aBaseMetaTileEntity.getMetaIDOffset(xDir + i, h, zDir + j) != 5) {
									Utils.LOG_MACHINE_INFO("Sifter Casing(s) Missing from one of the "+sHeight+" layers inner 3x3. Wrong Meta for Casing. Found:"+aBaseMetaTileEntity.getBlockOffset(xDir + i, h, zDir + j).getLocalizedName()+" with meta:"+aBaseMetaTileEntity.getMetaIDOffset(xDir + i, h, zDir + j));
									return false;
								}
								tAmount++;
							}
						}
					}
					else {
						//Dealt with inner 5x5, now deal with the exterior.
						//Deal with all 4 sides (Sifter walls)
						boolean checkController = false;
						if (((xDir + i) != 0) || (((zDir + j) != 0) && (h == 0))) {//no controller
							checkController = true;
						}
						else {
							checkController = false;
						}

						if (!this.addToMachineList(tTileEntity, TAE.GTPP_INDEX(21))) {
							if (!checkController){
								if (aBaseMetaTileEntity.getBlockOffset(xDir + i, h, zDir + j) != ModBlocks.blockCasings2Misc) {
									if ((tTileEntity instanceof GregtechMetaTileEntity_IndustrialSifter) || (aBaseMetaTileEntity.getBlockOffset(xDir + i, h, zDir + j) == GregTech_API.sBlockMachines)){
										if (h != 0){
											Utils.LOG_MACHINE_INFO("Found a secondary controller at the wrong Y level.");
											return false;
										}
									}
									else {
										Utils.LOG_MACHINE_INFO("Sifter Casings Missing from somewhere in the "+sHeight+" layer edge.");
										Utils.LOG_MACHINE_INFO("Instead, found "+aBaseMetaTileEntity.getBlockOffset(xDir + i, h, zDir + j).getLocalizedName());
										return false;
									}
								}

								if (aBaseMetaTileEntity.getMetaIDOffset(xDir + i, h, zDir + j) != 5) {
									if ((tTileEntity instanceof GregtechMetaTileEntity_IndustrialSifter) || (aBaseMetaTileEntity.getBlockOffset(xDir + i, h, zDir + j) == GregTech_API.sBlockMachines)){

									}
									else {
										Utils.LOG_MACHINE_INFO("Sifter Casings Missing from somewhere in the "+sHeight+" layer edge.");
										Utils.LOG_MACHINE_INFO("Incorrect Meta value for block, expected 5.");
										Utils.LOG_MACHINE_INFO("Instead, found "+aBaseMetaTileEntity.getMetaIDOffset(xDir + i, h, zDir + j)+".");
										return false;
									}
								}
							}
							tAmount++;
						}
						else {
							tAmount++;
						}
					}
				}
			}
		}
		if ((this.mInputBusses.size() != 1) || (this.mOutputBusses.size() != 4)
				|| (this.mMaintenanceHatches.size() != 1) || (this.mEnergyHatches.size() < 1)) {
			Utils.LOG_INFO("Returned False 3");
			Utils.LOG_INFO("Input Buses: "+this.mInputBusses.size()+" | expected: 1");
			Utils.LOG_INFO("Output Buses: "+this.mOutputBusses.size()+" | expected: 4");
			Utils.LOG_INFO("Energy Hatches: "+this.mEnergyHatches.size()+" | expected: 1");
			Utils.LOG_INFO("Maint. hatches: "+this.mMaintenanceHatches.size()+" | expected: 1");
			return false;
		}
		final int height = this.getBaseMetaTileEntity().getYCoord();

		final GT_MetaTileEntity_Hatch_OutputBus[] tmpHatches = new GT_MetaTileEntity_Hatch_OutputBus[4];
		for (int i = 0; i < this.mOutputBusses.size(); i++) {
			final int hatchNumber = this.mOutputBusses.get(i).getBaseMetaTileEntity().getYCoord() - 1 - height;
			if (tmpHatches[i] == null) {
				tmpHatches[i] = this.mOutputBusses.get(i);
			} else {
				Utils.LOG_MACHINE_INFO("Returned False 5 - "+this.mOutputBusses.size());
				return false;
			}
		}
		this.mOutputBusses.clear();
		for (int i = 0; i < tmpHatches.length; i++) {
			this.mOutputBusses.add(tmpHatches[i]);
		}

		Utils.LOG_INFO("Industrial Sifter - Structure Built? "+(tAmount>=35));

		return tAmount >= 35;
	}

	public boolean ignoreController(final Block tTileEntity) {
		if (!this.controller && (tTileEntity == GregTech_API.sBlockMachines)) {
			return true;
		}
		return false;
	}

	@Override
	public int getMaxEfficiency(final ItemStack aStack) {
		return 10000;
	}

	@Override
	public int getPollutionPerTick(final ItemStack aStack) {
		return 0;
	}

	@Override
	public int getAmountOfOutputs() {
		return 16;
	}

	@Override
	public boolean explodesOnComponentBreak(final ItemStack aStack) {
		return false;
	}

	@Override
	public boolean isOverclockerUpgradable() {
		return true;
	}
}