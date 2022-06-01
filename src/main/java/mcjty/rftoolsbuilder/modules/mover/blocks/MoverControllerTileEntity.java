package mcjty.rftoolsbuilder.modules.mover.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.bindings.Value;
import mcjty.lib.blockcommands.Command;
import mcjty.lib.blockcommands.ISerializer;
import mcjty.lib.blockcommands.ListCommand;
import mcjty.lib.blockcommands.ServerCommand;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.varia.OrientationTools;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsbuilder.compat.RFToolsBuilderTOPDriver;
import mcjty.rftoolsbuilder.modules.mover.MoverConfiguration;
import mcjty.rftoolsbuilder.modules.mover.MoverModule;
import mcjty.rftoolsbuilder.modules.mover.client.GuiMoverController;
import mcjty.rftoolsbuilder.modules.mover.items.VehicleCard;
import mcjty.rftoolsbuilder.modules.mover.logic.MoverGraphNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static mcjty.lib.api.container.DefaultContainerProvider.empty;
import static mcjty.lib.builder.TooltipBuilder.*;

public class MoverControllerTileEntity extends GenericTileEntity {

    @Cap(type = CapType.ENERGY)
    private final GenericEnergyStorage energyStorage = new GenericEnergyStorage(this, true, MoverConfiguration.MAXENERGY.get(), MoverConfiguration.RECEIVEPERTICK.get());

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<MenuProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Mover")
            .containerSupplier(empty(MoverModule.CONTAINER_MOVER_CONTROLLER, this))
            .energyHandler(() -> energyStorage)
            .setupSync(this));


    @Cap(type = CapType.INFUSABLE)
    private final IInfusable infusable = new DefaultInfusable(MoverControllerTileEntity.this);

    public static final int MAXSCAN = 512;  //@todo configurable
    private final Map<BlockPos, MoverGraphNode> nodes = new HashMap<>();
    private MoverGraphNode graph;

    // For the gui: the selected vehicle
    @GuiValue
    public static final Value<?, String> VALUE_SELECTED_VEHICLE = Value.create("selectedVehicle", Type.STRING, MoverControllerTileEntity::getSelectedVehicle, MoverControllerTileEntity::setSelectedVehicle);
    private String selectedVehicle;

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .tileEntitySupplier(MoverControllerTileEntity::new)
                .topDriver(RFToolsBuilderTOPDriver.DRIVER)
                .infusable()
                .manualEntry(ManualHelper.create("rftoolsbuilder:todo"))
                .info(key("message.rftoolsbuilder.shiftmessage"))
                .infoShift(header(), gold()));
    }


    public MoverControllerTileEntity(BlockPos pos, BlockState state) {
        super(MoverModule.TYPE_MOVER_CONTROLLER.get(), pos, state);
    }

    @Override
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);
        if (graph == null) {
            graph = new MoverGraphNode(worldPosition);
        }
        nodes.clear();
        ListTag graphTag = tagCompound.getList("graph", Tag.TAG_COMPOUND);
        for (Tag tag : graphTag) {
            CompoundTag nodeTag = ((CompoundTag) tag);
            BlockPos pos = new BlockPos(nodeTag.getInt("x"), nodeTag.getInt("y"), nodeTag.getInt("z"));
            MoverGraphNode childNode = new MoverGraphNode(pos);
            nodes.put(pos, childNode);
            CompoundTag childrenTag = nodeTag.getCompound("c");
            for (Direction direction : OrientationTools.DIRECTION_VALUES) {
                if (childrenTag.contains(direction.name())) {
                    CompoundTag childTag = childrenTag.getCompound(direction.name());
                    BlockPos childpos = new BlockPos(childTag.getInt("x"), childTag.getInt("y"), childTag.getInt("z"));
                    childNode.add(direction, childpos);
                }
            }

        }
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tagCompound) {
        super.saveAdditional(tagCompound);
        ListTag graphTag = new ListTag();
        nodes.forEach((pos, node) -> {
            CompoundTag nodeTag = new CompoundTag();
            nodeTag.putInt("x", pos.getX());
            nodeTag.putInt("y", pos.getY());
            nodeTag.putInt("z", pos.getZ());

            CompoundTag childrenTag = new CompoundTag();
            node.getChildren().forEach((direction, childpos) -> {
                CompoundTag childTag = new CompoundTag();
                childTag.putInt("x", childpos.getX());
                childTag.putInt("y", childpos.getY());
                childTag.putInt("z", childpos.getZ());
                childrenTag.put(direction.name(), childTag);
            });
            nodeTag.put("c", childrenTag);

            graphTag.add(nodeTag);
        });
        tagCompound.put("graph", graphTag);
    }

    private void selectNode(BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof MoverTileEntity mover) {
            ItemStack card = mover.getCard();
            if (card.isEmpty()) {
                selectedVehicle = null;
            } else {
                selectedVehicle = VehicleCard.getVehicleName(card);
            }
        } else {
            selectedVehicle = null;
        }
    }

    public String getSelectedVehicle() {
        return selectedVehicle;
    }

    public void setSelectedVehicle(String vehicle) {
        this.selectedVehicle = selectedVehicle;
        if (level.isClientSide) {
            GuiMoverController.setSelectedVehicle(vehicle);
        }
    }

    private void doMove(BlockPos pos, String vehicle) {

    }

    private void doScan() {
        setChanged();
        nodes.clear();
        for (Direction direction : OrientationTools.DIRECTION_VALUES) {
            BlockPos moverPos = worldPosition.relative(direction);
            if (level.getBlockEntity(moverPos) instanceof MoverTileEntity) {
                // Find the first mover
                graph = new MoverGraphNode(moverPos);
                nodes.put(moverPos, graph);
                doScan(moverPos, graph);
                return;
            }
        }
    }

    private void doScan(BlockPos moverPos, MoverGraphNode moverNode) {
        for (Direction direction : OrientationTools.DIRECTION_VALUES) {
            for (int distance = 1 ; distance <= MAXSCAN ; distance++) {
                BlockPos newPos = moverPos.relative(direction, distance);
                // If we have already handled this position we can stop for this direction
                if (!nodes.containsKey(newPos)) {
                    if (level.getBlockEntity(newPos) instanceof MoverTileEntity) {
                        MoverGraphNode child = new MoverGraphNode(newPos);
                        nodes.put(newPos, child);
                        moverNode.add(direction, newPos);
                        child.add(direction.getOpposite(), moverNode.getPos());
                        doScan(newPos, child);
                    }
                }
            }
        }
    }

    private List<String> getVehicles() {
        List<String> vehicles = new ArrayList<>();
        nodes.forEach((pos, node) -> {
            if (level.getBlockEntity(pos) instanceof MoverTileEntity mover) {
                ItemStack card = mover.getCard();
                if (!card.isEmpty()) {
                    String name = VehicleCard.getVehicleName(card);
                    if (mover.isMoving()) {
                        name += " (M)";
                    }
                    vehicles.add(name);
                }
            }
        });
        return vehicles;
    }

    private List<Pair<BlockPos, String>> getNodes() {
        List<Pair<BlockPos, String>> nodeNames = new ArrayList<>();
        nodes.forEach((pos, node) -> {
            if (level.getBlockEntity(pos) instanceof MoverTileEntity mover) {
                String name = mover.getName();
                if (name == null || name.trim().isEmpty()) {
                    name = pos.getX() + "," + pos.getY() + "," + pos.getZ();
                }
                nodeNames.add(Pair.of(pos, name));
            } else {
                nodeNames.add(Pair.of(pos, "<INVALID>"));
            }
        });
        return nodeNames;
    }

    public static final Key<BlockPos> SELECTED_NODE = new Key<>("node", Type.BLOCKPOS);
    public static final Key<String> SELECTED_VEHICLE = new Key<>("vehicle", Type.STRING);

    @ServerCommand
    public static final Command<?> CMD_SCAN = Command.<MoverControllerTileEntity>create("scan", (te, player, params) -> te.doScan());
    @ServerCommand
    public static final Command<?> CMD_MOVE = Command.<MoverControllerTileEntity>create("move", (te, player, params) -> te.doMove(params.get(SELECTED_NODE), params.get(SELECTED_VEHICLE)));

    @ServerCommand
    public static final Command<?> CMD_SELECTNODE = Command.<MoverControllerTileEntity>create("selectNode", (te, player, params) -> te.selectNode(params.get(SELECTED_NODE)));

    @ServerCommand(type = String.class)
    public static final ListCommand<?, ?> CMD_GETVEHICLES = ListCommand.<MoverControllerTileEntity, String>create("rftoolsbuilder.movercontroller.getVehicles",
            (te, player, params) -> te.getVehicles(),
            (te, player, params, list) -> GuiMoverController.setVehiclesFromServer(list));

    @ServerCommand(type = Pair.class, serializer = NodePairSerializer.class)
    public static final ListCommand<?, ?> CMD_GETNODES = ListCommand.<MoverControllerTileEntity, Pair<BlockPos, String>>create("rftoolsbuilder.movercontroller.getNodes",
            (te, player, params) -> te.getNodes(),
            (te, player, params, list) -> GuiMoverController.setNodesFromServer(list));

    public static class NodePairSerializer implements ISerializer<Pair<BlockPos, String>> {
        @Override
        public Function<FriendlyByteBuf, Pair<BlockPos, String>> getDeserializer() {
            return buf -> Pair.of(buf.readBlockPos(), buf.readUtf(32767));
        }

        @Override
        public BiConsumer<FriendlyByteBuf, Pair<BlockPos, String>> getSerializer() {
            return (buf, pair) -> {
                buf.writeBlockPos(pair.getLeft());
                buf.writeUtf(pair.getRight());
            };
        }
    }
}