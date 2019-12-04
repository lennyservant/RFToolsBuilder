package mcjty.rftoolsbuilder.modules.shield.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nullable;

public class CamoShieldBlock extends AbstractShieldBlock {

    public static final String CAMO = "camo";
    public static final ModelProperty<BlockState> CAMO_PROPERTY = new ModelProperty<>();

    public CamoShieldBlock(boolean opaque) {
        super(opaque);
    }

    // @todo 1.14
//    @Override
//    @SideOnly(Side.CLIENT)
//    public void initModel() {
//        // To make sure that our ISBM model is chosen for all states we use this custom state mapper:
//        StateMapperBase ignoreState = new StateMapperBase() {
//            @Override
//            protected ModelResourceLocation getModelResourceLocation(BlockState BlockState) {
//                return CamoBakedModel.modelFacade;
//            }
//        };
//        ModelLoader.setCustomStateMapper(this, ignoreState);
//    }


    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TickShieldSolidBlockTileEntity();
    }

//    @Override
//    public boolean isFullBlock(BlockState state) {
//        return false;
//    }
//
//    @Override
//    public boolean isFullCube(BlockState state) {
//        return false;
//    }

    @Override
    public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer) {
        return true; // delegated to CamoBakedModel#getQuads
    }

    // @todo 1.14
//    @Override
//    public boolean shouldSideBeRendered(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
//        Block block = world.getBlockState(pos.offset(side)).getBlock();
//        if (block instanceof CamoShieldBlock) {
//            return false;
//        }
//        return true;
//    }

    @Nullable
    protected BlockState getMimicBlock(IBlockReader blockAccess, BlockPos pos) {
        TileEntity te = blockAccess.getTileEntity(pos);
        if (te instanceof NoTickShieldBlockTileEntity) {
            return ((NoTickShieldBlockTileEntity) te).getMimicBlock();
        } else {
            return null;
        }
    }

    // @todo 1.14
//    @SideOnly(Side.CLIENT)
//    public void initColorHandler(BlockColors blockColors) {
//        blockColors.registerBlockColorHandler((state, world, pos, tintIndex) -> {
//            BlockState mimicBlock = getMimicBlock(world, pos);
//            return mimicBlock != null ? blockColors.colorMultiplier(mimicBlock, world, pos, tintIndex) : -1;
//        }, this);
//    }

    // @todo 1.14
//    @Override
//    protected BlockStateContainer createBlockState() {
//        IProperty<?>[] listedProperties = new IProperty[] { };
//        IUnlistedProperty<?>[] unlistedProperties = new IUnlistedProperty[] { CAMOID };
//        return new ExtendedBlockState(this, listedProperties, unlistedProperties);
//    }
//
//    @Override
//    public BlockState getExtendedState(BlockState state, IBlockReader world, BlockPos pos) {
//        IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
//        BlockState mimicBlock = getMimicBlock(world, pos);
//        if (mimicBlock != null) {
//            return extendedBlockState.withProperty(CAMOID, new CamoBlockId(mimicBlock));
//        } else {
//            return extendedBlockState;
//        }
//    }
}
