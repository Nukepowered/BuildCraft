package buildcraft.api;


import net.minecraft.src.*;
import java.util.Random;

public class APIProxy {

    public static boolean isClient(World world) {
        return false;
    }

    public static boolean isServerSide() {
        return true;
    }

    public static boolean isRemote() {
        return false;
    }

    public static void removeEntity(Entity entity) {
        entity.setDead();
    }

    public static Random createNewRandom(World world) {
        return new Random(world.getSeed());
    }

    public static EntityPlayer createNewPlayer(World world) {
        return new EntityPlayerMP(ModLoader.getMinecraftServerInstance(), world, "FakePlayer", new ItemInWorldManager(world)) {

        };
    }
}
