package fathertoast.specialmobs.common.core.register;

import fathertoast.specialmobs.common.bestiary.MobFamily;
import fathertoast.specialmobs.common.config.util.ConfigDrivenAttributeModifierMap;
import fathertoast.specialmobs.common.core.SpecialMobs;
import fathertoast.specialmobs.common.entity.projectile.BoneShrapnelEntity;
import fathertoast.specialmobs.common.entity.projectile.IncorporealFireballEntity;
import fathertoast.specialmobs.common.util.AnnotationHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SMEntities {
    
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create( ForgeRegistries.ENTITIES, SpecialMobs.MOD_ID );
    
    // Misc entities
    public static final RegistryObject<EntityType<IncorporealFireballEntity>> CORPOREAL_FIREBALL = register( "incorporeal_fireball",
            EntityType.Builder.<IncorporealFireballEntity>of( IncorporealFireballEntity::new, EntityClassification.MISC ).sized( 1.0F, 1.0F ).clientTrackingRange( 4 ).updateInterval( 3 ) );
    public static final RegistryObject<EntityType<BoneShrapnelEntity>> BONE_SHRAPNEL = register("bone_shrapnel",
            EntityType.Builder.<BoneShrapnelEntity>of( BoneShrapnelEntity::new, EntityClassification.MISC ).sized(0.5F, 0.5F ).clientTrackingRange( 4 ));

    /** Registers an entity type to the deferred register. */
    public static <T extends Entity> RegistryObject<EntityType<T>> register( String name, EntityType.Builder<T> builder ) {
        return REGISTRY.register( name, () -> builder.build( name ) );
    }
    
    /** Sets the default attributes for entity types, such as max health, attack damage etc. */
    public static void createAttributes( EntityAttributeCreationEvent event ) {
        // Bestiary-generated entities
        for( MobFamily.Species<?> species : MobFamily.getAllSpecies() ) {
            event.put( species.entityType.get(), new ConfigDrivenAttributeModifierMap(
                    species.config.GENERAL.attributeChanges, AnnotationHelper.createAttributes( species ) ) );
        }
    }
    
    /** Sets the natural spawn placement rules for entity types. */
    public static void registerSpawnPlacements() {
        //TODO
    }
}