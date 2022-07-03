package fathertoast.specialmobs.common.entity.blaze;

import fathertoast.specialmobs.common.bestiary.BestiaryInfo;
import fathertoast.specialmobs.common.bestiary.MobFamily;
import fathertoast.specialmobs.common.bestiary.SpecialMob;
import fathertoast.specialmobs.common.util.AttributeHelper;
import fathertoast.specialmobs.common.util.References;
import fathertoast.specialmobs.datagen.loot.LootTableBuilder;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SpecialMob
public class WildfireBlazeEntity extends _SpecialBlazeEntity {
    
    //--------------- Static Special Mob Hooks ----------------
    
    @SpecialMob.SpeciesReference
    public static MobFamily.Species<WildfireBlazeEntity> SPECIES;
    
    @SpecialMob.BestiaryInfoSupplier
    public static BestiaryInfo bestiaryInfo( EntityType.Builder<LivingEntity> entityType ) {
        entityType.sized( 0.9F, 2.7F );
        return new BestiaryInfo( 0xF4EE32 );
    }
    
    @SpecialMob.AttributeCreator
    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return AttributeHelper.of( _SpecialBlazeEntity.createAttributes() )
                .addAttribute( Attributes.MAX_HEALTH, 20.0 )
                .build();
    }
    
    @SpecialMob.LanguageProvider
    public static String[] getTranslations( String langKey ) {
        return References.translations( langKey, "Wildfire",
                "", "", "", "", "", "" );//TODO
    }
    
    @SpecialMob.LootTableProvider
    public static void buildLootTable( LootTableBuilder loot ) {
        addBaseLoot( loot );
        loot.addCommonDrop( "common", Items.COAL, 1 );
        loot.addUncommonDrop( "uncommon", Items.BLAZE_SPAWN_EGG );
    }
    
    @SpecialMob.Factory
    public static EntityType.IFactory<WildfireBlazeEntity> getVariantFactory() { return WildfireBlazeEntity::new; }
    
    
    //--------------- Variant-Specific Implementations ----------------
    
    /** The number of babies spawned on death. */
    private int babies;
    /** The number of extra babies that can be spawned by attacks. */
    private int summons;
    
    public WildfireBlazeEntity( EntityType<? extends _SpecialBlazeEntity> entityType, World world ) {
        super( entityType, world );
        getSpecialData().setBaseScale( 1.5F );
        getSpecialData().setRegenerationTime( 40 );
        xpReward += 2;
        
        babies = 3 + random.nextInt( 4 );
        summons = 4 + random.nextInt( 7 );
    }
    
    /** Override to change this entity's AI goals. */
    @Override
    protected void registerVariantGoals() {
        getSpecialData().rangedAttackSpread *= 0.1F;
        setRangedAI( 1, 0, 30, 50, 20.0F );
    }
    
    /** Override to apply effects when this entity hits a target with a melee attack. */
    protected void onVariantAttack( Entity target ) {
        target.setSecondsOnFire( 8 );
    }
    
    /** Called to attack the target with a ranged attack. */
    @Override
    public void performRangedAttack( LivingEntity target, float damageMulti ) {
        if( !level.isClientSide() && summons > 0 && random.nextInt( 2 ) == 0 ) {
            summons--;
            
            final double vX = target.getX() - getX();
            final double vZ = target.getZ() - getZ();
            final double vH = Math.sqrt( vX * vX + vZ * vZ );
            spawnBaby( vX / vH * 0.8 + getDeltaMovement().x * 0.2, vZ / vH * 0.8 + getDeltaMovement().z * 0.2, null );
            spawnAnim();
            if( !isSilent() ) level.levelEvent( null, References.EVENT_BLAZE_SHOOT, blockPosition(), 0 );
        }
        else {
            super.performRangedAttack( target, damageMulti );
        }
    }
    
    /** Called to remove this entity from the world. Includes death, unloading, interdimensional travel, etc. */
    @Override
    public void remove( boolean keepData ) {
        //noinspection deprecation
        if( isDeadOrDying() && !removed && level instanceof IServerWorld ) { // Same conditions as slime splitting
            // Spawn babies on death
            ILivingEntityData groupData = null;
            for( int i = 0; i < babies; i++ ) {
                groupData = spawnBaby( (random.nextDouble() - 0.5) * 0.3, (random.nextDouble() - 0.5) * 0.3, groupData );
            }
            spawnAnim();
            if( !isSilent() ) level.levelEvent( null, References.EVENT_BLAZE_SHOOT, blockPosition(), 0 );
        }
        super.remove( keepData );
    }
    
    /** Helper method to simplify spawning babies. */
    @Nullable
    private ILivingEntityData spawnBaby( double vX, double vZ, @Nullable ILivingEntityData groupData ) {
        final CinderBlazeEntity baby = CinderBlazeEntity.SPECIES.entityType.get().create( level );
        if( baby == null ) return groupData;
        
        baby.copyPosition( this );
        baby.yHeadRot = yRot;
        baby.yBodyRot = yRot;
        groupData = baby.finalizeSpawn( (IServerWorld) level, level.getCurrentDifficultyAt( blockPosition() ),
                SpawnReason.MOB_SUMMONED, groupData, null );
        baby.setTarget( getTarget() );
        
        baby.setDeltaMovement( vX, 0.0, vZ );
        baby.setOnGround( false );
        
        level.addFreshEntity( baby );
        return groupData;
    }
    
    /** Override to save data to this entity's NBT data. */
    @Override
    public void addVariantSaveData( CompoundNBT saveTag ) {
        saveTag.putByte( References.TAG_BABIES, (byte) babies );
        saveTag.putByte( References.TAG_SUMMONS, (byte) summons );
    }
    
    /** Override to load data from this entity's NBT data. */
    @Override
    public void readVariantSaveData( CompoundNBT saveTag ) {
        if( saveTag.contains( References.TAG_BABIES, References.NBT_TYPE_NUMERICAL ) )
            babies = saveTag.getByte( References.TAG_BABIES );
        if( saveTag.contains( References.TAG_SUMMONS, References.NBT_TYPE_NUMERICAL ) )
            summons = saveTag.getByte( References.TAG_SUMMONS );
    }
    
    private static final ResourceLocation[] TEXTURES = {
            GET_TEXTURE_PATH( "wildfire" )
    };
    
    /** @return All default textures for this entity. */
    @Override
    public ResourceLocation[] getDefaultTextures() { return TEXTURES; }
}