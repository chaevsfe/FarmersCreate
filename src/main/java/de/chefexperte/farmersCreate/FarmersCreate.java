package de.chefexperte.farmersCreate;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FarmersCreate implements ModInitializer {

    public static final String MOD_ID = "farmerscreate";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        FarmersCreateConfig config = FarmersCreateConfig.get();
        LOGGER.info("Config: {}={}, {}={}, {}={}",
                FarmersCreateConfig.REQUIRE_CONTAINER, config.requireContainer,
                FarmersCreateConfig.DAMAGE_TOOLS, config.damageTools,
                FarmersCreateConfig.FARMERS_DELIGHT_ROLLS, config.farmersDelightRolls);
    }
}
