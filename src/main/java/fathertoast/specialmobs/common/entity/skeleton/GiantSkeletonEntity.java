package fathertoast.specialmobs.common.entity.skeleton;

import fathertoast.specialmobs.common.bestiary.BestiaryInfo;
import fathertoast.specialmobs.common.bestiary.SpecialMob;
import fathertoast.specialmobs.common.util.AttributeHelper;
import fathertoast.specialmobs.common.util.References;
import fathertoast.specialmobs.datagen.loot.LootTableBuilder;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.Items;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SpecialMob
public class GiantSkeletonEntity extends _SpecialSkeletonEntity {
    
    //--------------- Static Special Mob Hooks ----------------
    
    @SpecialMob.BestiaryInfoSupplier
    public static BestiaryInfo bestiaryInfo( EntityType.Builder<LivingEntity> entityType ) {
        entityType.sized( 0.9F, 2.7F );
        return new BestiaryInfo( 0x494949 );
    }
    
    @SpecialMob.AttributeCreator
    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return AttributeHelper.of( _SpecialSkeletonEntity.createAttributes() )
                .addAttribute( Attributes.MAX_HEALTH, 20.0 )
                .addAttribute( Attributes.ATTACK_DAMAGE, 2.0 )
                .build();
    }
    
    @SpecialMob.LanguageProvider
    public static String[] getTranslations( String langKey ) {
        return References.translations( langKey, "Giant Skeleton",
                "", "", "", "", "", "" );//TODO
    }
    
    @SpecialMob.LootTableProvider
    public static void buildLootTable( LootTableBuilder loot ) {
        addBaseLoot( loot );
        loot.addGuaranteedDrop( "base", Items.BONE, 2 );
    }
    
    @SpecialMob.Constructor
    public GiantSkeletonEntity( EntityType<? extends _SpecialSkeletonEntity> entityType, World world ) {
        super( entityType, world );
        getSpecialData().setBaseScale( 1.5F );
        maxUpStep = 1.0F;
        xpReward += 1;
    }
    
    
    //--------------- Variant-Specific Implementations ----------------
    
    /** Override to change this entity's AI goals. */
    @Override
    protected void registerVariantGoals() {
        getSpecialData().rangedAttackDamage += 2.0F;
    }
    
    /** Sets this entity as a baby. */
    @Override
    public void setBaby( boolean value ) { }
    
    /** @return True if this entity is a baby. */
    @Override
    public boolean isBaby() { return false; }
}