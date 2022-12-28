package mcjty.rftoolsbuilder.modules.mover;

import net.minecraftforge.common.ForgeConfigSpec;

public class MoverConfiguration {

    public static final String CATEGORY_MOVER = "mover";

    public static ForgeConfigSpec.IntValue MAXENERGY;
    public static ForgeConfigSpec.IntValue RECEIVEPERTICK;
    public static ForgeConfigSpec.IntValue RF_PER_MOVE;

    public static ForgeConfigSpec.IntValue VEHICLE_CONTROL_RFPERTICK;
    public static ForgeConfigSpec.IntValue VEHICLE_STATUS_RFPERTICK;

    public static void init(ForgeConfigSpec.Builder SERVER_BUILDER, ForgeConfigSpec.Builder CLIENT_BUILDER) {
        SERVER_BUILDER.comment("Settings for the mover system").push(CATEGORY_MOVER);
        CLIENT_BUILDER.comment("Settings for the mover system").push(CATEGORY_MOVER);

        VEHICLE_CONTROL_RFPERTICK = SERVER_BUILDER
                .comment("RF per tick/per block for the vehicle control module")
                .defineInRange("vehicleControlRFPerTick", 0, 0, Integer.MAX_VALUE);
        VEHICLE_STATUS_RFPERTICK = SERVER_BUILDER
                .comment("RF per tick/per block for the vehicle status module")
                .defineInRange("vehicleStatusRFPerTick", 2, 0, Integer.MAX_VALUE);

        RF_PER_MOVE = SERVER_BUILDER
                .comment("Amount of RF used for one movement")
                .defineInRange("rfPerMove", 1000, 0, Integer.MAX_VALUE);
        MAXENERGY = SERVER_BUILDER
                .comment("Maximum RF storage that the mover controller can hold")
                .defineInRange("moverControllerMaxRF", 100000, 0, Integer.MAX_VALUE);
        RECEIVEPERTICK = SERVER_BUILDER
                .comment("RF per tick that the mover controller can receive")
                .defineInRange("moverControllerRFPerTick", 1000, 0, Integer.MAX_VALUE);

        SERVER_BUILDER.pop();
        CLIENT_BUILDER.pop();
    }
}
