package fathertoast.specialmobs.common.config;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.specialmobs.common.bestiary.MobFamily;
import fathertoast.specialmobs.common.core.SpecialMobs;

/**
 * Used as the sole hub for all config access from outside the config package.
 * <p>
 * Contains references to all config files used in this mod, which in turn provide direct 'getter' access to each
 * configurable value.
 */
public class Config {
    
    public static final ConfigManager MANAGER;
    public static final MainConfig MAIN;


    static {
        MANAGER = ConfigManager.create( "SpecialMobs" );
        MAIN = new MainConfig( MANAGER, "main" );

        ReadMeConfig.makeReadMe( MANAGER );
        MAIN.SPEC.initialize();
        MobFamily.initBestiary(); // Just make sure this class gets loaded
    }

    /** Called from {@link SpecialMobs#SpecialMobs()} to load this class. */
    public static void initialize() {

    }
}