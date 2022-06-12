package org.samo_lego.commandspy.permission;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.level.ServerPlayer;

/**
 * Permission checker.
 *
 * In its own class since we do not want to depend
 * on it fully, but use it just if luckperms mod is loaded.
 */
public class PermissionHelper {

    /**
     * Checks permission for player using Lucko's
     * permission API.
     *
     * @param player
     * @param permission
     * @return
     */
    public static boolean checkPermission(ServerPlayer player, String permission) {
        return Permissions.check(player, permission, false);
    }
}
