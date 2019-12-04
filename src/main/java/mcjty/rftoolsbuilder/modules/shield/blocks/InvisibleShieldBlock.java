package mcjty.rftoolsbuilder.modules.shield.blocks;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;


public class InvisibleShieldBlock extends AbstractShieldBlock {

    public InvisibleShieldBlock(boolean opaque) {
        super(opaque);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TickShieldBlockTileEntity();
    }


    // @todo 1.14
//    @Override
//    public RayTraceResult collisionRayTrace(BlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
//        return null;
//    }
//
//    @SideOnly(Side.CLIENT)
//    @Override
//    public boolean shouldSideBeRendered(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
//        return false;
//    }
//
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
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }
}
