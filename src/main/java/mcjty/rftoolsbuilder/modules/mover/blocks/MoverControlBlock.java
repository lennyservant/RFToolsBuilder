package mcjty.rftoolsbuilder.modules.mover.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.rftoolsbuilder.compat.RFToolsBuilderTOPDriver;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

import javax.annotation.Nullable;

import static mcjty.lib.builder.TooltipBuilder.*;

public class MoverControlBlock extends BaseBlock {

    public static final DirectionProperty HORIZ_FACING = DirectionProperty.create("horizfacing", Direction.Plane.HORIZONTAL);
    private final int page;

    public MoverControlBlock(int page) {
        super(new BlockBuilder()
                .topDriver(RFToolsBuilderTOPDriver.DRIVER)
                .info(key("message.rftoolsbuilder.shiftmessage"))
                .infoShift(header(), gold()));
        this.page = page;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(HORIZ_FACING, context.getPlayer().getDirection().getOpposite());
    }

    public int getPage() {
        return page;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        state = super.rotate(state, rot);
        Direction facing = state.getValue(BlockStateProperties.FACING);
        if (facing.getStepY() == 0) {
            // It's horizontal
            return state.setValue(HORIZ_FACING, facing);
        } else {
            Direction horizFacing = state.getValue(HORIZ_FACING);
            return state.setValue(HORIZ_FACING, rot.rotate(horizFacing));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HORIZ_FACING);
    }
}
