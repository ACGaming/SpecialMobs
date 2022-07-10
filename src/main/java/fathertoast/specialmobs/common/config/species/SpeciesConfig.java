package fathertoast.specialmobs.common.config.species;

import fathertoast.specialmobs.common.bestiary.BestiaryInfo;
import fathertoast.specialmobs.common.bestiary.MobFamily;
import fathertoast.specialmobs.common.config.Config;
import fathertoast.specialmobs.common.config.family.FamilyConfig;
import fathertoast.specialmobs.common.config.field.*;
import fathertoast.specialmobs.common.config.file.ToastConfigSpec;
import fathertoast.specialmobs.common.config.util.ConfigUtil;
import net.minecraft.block.Block;
import net.minecraft.potion.Effect;

/**
 * This is the base config for mob species. This may be extended to add categories specific to the species, but all
 * options that are used by all species should be defined in this class.
 */
public class SpeciesConfig extends Config.AbstractConfig {
    
    public static final String SPECIAL_DATA_SUBCAT = "special_data.";
    
    protected static String fileName( MobFamily.Species<?> species ) {
        return (species.specialVariantName == null ? "_normal" : ConfigUtil.camelCaseToLowerUnderscore( species.specialVariantName ))
                + "_" + ConfigUtil.noSpaces( species.family.configName );
    }
    
    protected static String variantName( MobFamily.Species<?> species ) {
        return species.specialVariantName == null ? "vanilla replacement" : ConfigUtil.camelCaseToLowerSpace( species.specialVariantName );
    }
    
    /** The full readable name for the species in lower space case; e.g., "baby cave spiders". */
    protected final String speciesName;
    
    /** Category containing all options applicable to mob species as a whole; i.e. not specific to any particular species. */
    public final General GENERAL;
    
    /** Builds the config spec that should be used for this config. */
    public SpeciesConfig( MobFamily.Species<?> species ) {
        super( FamilyConfig.dir( species.family ), fileName( species ),
                String.format( "This config contains options that apply only to the %s %s species.",
                        species.specialVariantName, ConfigUtil.camelCaseToLowerSpace( species.family.name ) ) );
        SPEC.newLine();
        SPEC.describeAttributeList();
        SPEC.describeRegistryEntryList();
        
        speciesName = variantName( species ) + " " + species.family.configName;
        
        GENERAL = new General( SPEC, species, speciesName );
    }
    
    public static class General extends Config.AbstractCategory {
        
        public final DoubleField randomScaling;
        
        public final AttributeListField attributeChanges;
        
        public final IntField experience;
        public final IntField healTime;
        public final DoubleField fallDamageMultiplier;
        public final BooleanField isImmuneToFire;
        public final BooleanField isImmuneToBurning;
        public final BooleanField canBreatheInWater;
        public final BooleanField ignoreWaterPush;
        public final BooleanField isDamagedByWater;
        public final BooleanField allowLeashing;
        public final BooleanField ignorePressurePlates;
        public final RegistryEntryListField<Block> immuneToStickyBlocks;
        public final RegistryEntryListField<Effect> immuneToPotions;
        
        // These are at the end because they may or may not be present (not applicable for all families)
        public final DoubleField rangedAttackDamage;
        public final DoubleField rangedAttackSpread;
        public final DoubleField rangedWalkSpeed;
        public final IntField rangedAttackCooldown;
        public final IntField rangedAttackMaxCooldown;
        public final DoubleField rangedAttackMaxRange;
        
        General( ToastConfigSpec parent, MobFamily.Species<?> species, String speciesName ) {
            super( parent, "general",
                    "Options standard to all mob species (that is, not specific to any particular mob species)." );
            final BestiaryInfo info = species.bestiaryInfo;
            
            randomScaling = SPEC.define( new DoubleField( "random_scaling", -1.0, DoubleField.Range.SIGNED_PERCENT,
                    "When greater than 0, " + speciesName + " will have a random render scale applied. This is a visual effect only.",
                    "If this is set to a non-negative value, it overrides the value set for both \"master_random_scaling\" and",
                    "\"family_random_scaling\". The priority is species value > family value > master value." ) );
            
            SPEC.newLine();
            
            attributeChanges = SPEC.define( new AttributeListField( "attributes", info.defaultAttributes,
                    "Attribute modifiers for " + speciesName + ". If no attribute changes are defined here, " + speciesName,
                    "will have the exact same attributes as their parent vanilla mob." ) );
            
            SPEC.newLine();
            
            experience = SPEC.define( new IntField( SPECIAL_DATA_SUBCAT + "experience", info.experience, IntField.Range.NON_NEGATIVE,
                    "The amount of experience " + speciesName + " drop when killed by a player. Multiplied by 2.5 for babies.",
                    "Extra experience may drop based on equipment. Slime-style mobs also drop experience equal to slime size." ) );
            healTime = SPEC.define( new IntField( SPECIAL_DATA_SUBCAT + "heal_time", info.healTime, IntField.Range.NON_NEGATIVE,
                    "If greater than 0, " + speciesName + " will heal 1 half-heart of health every \"heal_time\" ticks. (20 ticks = 1 second)" ) );
            fallDamageMultiplier = SPEC.define( new DoubleField( SPECIAL_DATA_SUBCAT + "fall_damage_multiplier", info.fallDamageMultiplier, DoubleField.Range.NON_NEGATIVE,
                    "Fall damage taken by " + speciesName + " is multiplied by this value. 0 is fall damage immunity." ) );
            isImmuneToFire = SPEC.define( new BooleanField( SPECIAL_DATA_SUBCAT + "immune_to_fire", info.isImmuneToFire,
                    "If true, " + speciesName + " will take no fire damage. Does not affect spawn restrictions." ) );
            isImmuneToBurning = SPEC.define( new BooleanField( SPECIAL_DATA_SUBCAT + "immune_to_burning", info.isImmuneToBurning,
                    "If true, " + speciesName + " cannot be set on fire (this setting only matters if not immune to fire)." ) );
            canBreatheInWater = SPEC.define( new BooleanField( SPECIAL_DATA_SUBCAT + "immune_to_drowning", info.canBreatheInWater,
                    "If true, " + speciesName + " can breathe in water." ) );
            ignoreWaterPush = SPEC.define( new BooleanField( SPECIAL_DATA_SUBCAT + "immune_to_fluid_push", info.ignoreWaterPush,
                    "If true, " + speciesName + " will ignore forces applied by flowing fluids." ) );
            isDamagedByWater = SPEC.define( new BooleanField( SPECIAL_DATA_SUBCAT + "sensitive_to_water", info.isDamagedByWater,
                    "If true, " + speciesName + " will be continuously damaged while wet." ) );
            allowLeashing = SPEC.define( new BooleanField( SPECIAL_DATA_SUBCAT + "allow_leashing", info.allowLeashing,
                    "If true, " + speciesName + " can be leashed. (Note: Leashed mobs can still attack you.)" ) );
            ignorePressurePlates = SPEC.define( new BooleanField( SPECIAL_DATA_SUBCAT + "immune_to_pressure_plates", info.ignorePressurePlates,
                    "If true, " + speciesName + " will not trigger pressure plates." ) );
            immuneToStickyBlocks = SPEC.define( new RegistryEntryListField<>( SPECIAL_DATA_SUBCAT + "immune_to_sticky_blocks", info.immuneToStickyBlocks,
                    ConfigUtil.properCase( speciesName ) + " will not be 'trapped' in any blocks specified here (e.g. \"cobweb\" or \"sweet_berry_bush\")." ) );
            immuneToPotions = SPEC.define( new RegistryEntryListField<>( SPECIAL_DATA_SUBCAT + "immune_to_effects", info.immuneToPotions,
                    ConfigUtil.properCase( speciesName ) + " cannot be inflicted with any effects specified here (e.g. \"instant_damage\" or \"regeneration\")." ) );
            
            SPEC.newLine();
            
            rangedAttackDamage = info.rangedAttackDamage < 0.0F ? null :
                    SPEC.define( new DoubleField( SPECIAL_DATA_SUBCAT + "ranged_attack.damage", info.rangedAttackDamage, DoubleField.Range.NON_NEGATIVE,
                            "" ) );
            rangedAttackSpread = info.rangedAttackSpread < 0.0F ? null :
                    SPEC.define( new DoubleField( SPECIAL_DATA_SUBCAT + "ranged_attack.spread", info.rangedAttackSpread, DoubleField.Range.NON_NEGATIVE,
                            "." ) );
            rangedWalkSpeed = info.rangedWalkSpeed < 0.0F ? null :
                    SPEC.define( new DoubleField( SPECIAL_DATA_SUBCAT + "ranged_attack.walk_speed", info.rangedWalkSpeed, DoubleField.Range.NON_NEGATIVE,
                            "." ) );
            rangedAttackCooldown = info.rangedAttackCooldown < 0 ? null :
                    SPEC.define( new IntField( SPECIAL_DATA_SUBCAT + "ranged_attack.charge_time", info.rangedAttackCooldown, IntField.Range.NON_NEGATIVE,
                            "The delay (in ticks) to 'charge up' and perform a ranged attack. (20 ticks = 1 second)" ) );
            rangedAttackMaxCooldown = info.rangedAttackMaxCooldown < 0 ? null :
                    SPEC.define( new IntField( SPECIAL_DATA_SUBCAT + "ranged_attack.refire_time", info.rangedAttackMaxCooldown, IntField.Range.NON_NEGATIVE,
                            "The total delay (in ticks) between each ranged attack. (20 ticks = 1 second)" ) );
            rangedAttackMaxRange = info.rangedAttackMaxRange < 0.0F ? null :
                    SPEC.define( new DoubleField( SPECIAL_DATA_SUBCAT + "ranged_attack.max_range", info.rangedAttackMaxRange, DoubleField.Range.NON_NEGATIVE,
                            "" ) );
        }
    }
}