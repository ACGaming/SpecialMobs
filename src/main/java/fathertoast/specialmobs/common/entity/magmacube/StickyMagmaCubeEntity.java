package fathertoast.specialmobs.common.entity.magmacube;

import fathertoast.specialmobs.common.bestiary.BestiaryInfo;
import fathertoast.specialmobs.common.bestiary.MobFamily;
import fathertoast.specialmobs.common.bestiary.SpecialMob;
import fathertoast.specialmobs.common.util.References;
import fathertoast.specialmobs.datagen.loot.LootTableBuilder;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SpecialMob
public class StickyMagmaCubeEntity extends _SpecialMagmaCubeEntity {
    
    //--------------- Static Special Mob Hooks ----------------
    
    @SpecialMob.SpeciesReference
    public static MobFamily.Species<StickyMagmaCubeEntity> SPECIES;
    
    @SpecialMob.BestiaryInfoSupplier
    public static BestiaryInfo bestiaryInfo( EntityType.Builder<LivingEntity> entityType ) {
        return new BestiaryInfo( 0x9D733F );
    }
    
    @SpecialMob.LanguageProvider
    public static String[] getTranslations( String langKey ) {
        return References.translations( langKey, "Sticky Magma Cube",
                "", "", "", "", "", "" );//TODO
    }
    
    @SpecialMob.LootTableProvider
    public static void buildLootTable( LootTableBuilder loot ) {
        addBaseLoot( loot );
        loot.addCommonDrop( "common", Items.SLIME_BALL );
    }
    
    @SpecialMob.Factory
    public static EntityType.IFactory<StickyMagmaCubeEntity> getVariantFactory() { return StickyMagmaCubeEntity::new; }
    
    
    //--------------- Variant-Specific Implementations ----------------
    
    public StickyMagmaCubeEntity( EntityType<? extends _SpecialMagmaCubeEntity> entityType, World world ) {
        super( entityType, world );
        slimeExperienceValue += 2;
    }
    
    private final DamageSource grabDamageSource = DamageSource.mobAttack( this ).bypassArmor().bypassMagic();
    
    private int grabTime;
    
    /** Override to modify this slime's base attributes by size. */
    @Override
    protected void modifyVariantAttributes( int size ) {
        addAttribute( Attributes.MAX_HEALTH, 4.0 * size );
    }
    
    /** Override to apply effects when this entity hits a target with a melee attack. */
    @Override
    protected void onVariantAttack( Entity target ) {
        if( grabTime <= -20 && getPassengers().isEmpty() ) {
            if( target.startRiding( this, true ) ) grabTime = 20;
        }
    }
    
    /** Called each tick to update this entity. */
    @Override
    public void tick() {
        super.tick();
        
        grabTime--;
        final List<Entity> riders = getPassengers();
        if( grabTime <= 0 && !riders.isEmpty() ) {
            for( Entity rider : riders ) {
                if( rider instanceof LivingEntity ) {
                    rider.hurt( grabDamageSource, 1.0F );
                    grabTime = 10;
                }
            }
        }
    }
    
    private static final ResourceLocation[] TEXTURES = {
            GET_TEXTURE_PATH( "sticky" )
    };
    
    /** @return All default textures for this entity. */
    @Override
    public ResourceLocation[] getDefaultTextures() { return TEXTURES; }
}