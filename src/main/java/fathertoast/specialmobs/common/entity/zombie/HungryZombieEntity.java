package fathertoast.specialmobs.common.entity.zombie;

import fathertoast.specialmobs.common.bestiary.BestiaryInfo;
import fathertoast.specialmobs.common.bestiary.MobFamily;
import fathertoast.specialmobs.common.bestiary.SpecialMob;
import fathertoast.specialmobs.common.entity.MobHelper;
import fathertoast.specialmobs.common.util.References;
import fathertoast.specialmobs.datagen.loot.LootTableBuilder;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SpecialMob
public class HungryZombieEntity extends _SpecialZombieEntity {
    
    //--------------- Static Special Mob Hooks ----------------
    
    @SpecialMob.SpeciesReference
    public static MobFamily.Species<HungryZombieEntity> SPECIES;
    
    @SpecialMob.BestiaryInfoSupplier
    public static void getBestiaryInfo( BestiaryInfo.Builder bestiaryInfo ) {
        bestiaryInfo.color( 0xAB1518 )
                .uniqueTextureBaseOnly()
                .addExperience( 2 ).regen( 30 ).disableRangedAttack()
                .addToAttribute( Attributes.MAX_HEALTH, 10.0 )
                .multiplyAttribute( Attributes.MOVEMENT_SPEED, 1.3 );
    }
    
    @SpecialMob.LanguageProvider
    public static String[] getTranslations( String langKey ) {
        return References.translations( langKey, "Hungry Zombie",
                "", "", "", "", "", "" );//TODO
    }
    
    @SpecialMob.LootTableProvider
    public static void buildLootTable( LootTableBuilder loot ) {
        addBaseLoot( loot );
        loot.addCommonDrop( "common", Items.BONE );
        loot.addUncommonDrop( "uncommon", Items.BEEF, Items.CHICKEN, Items.MUTTON, Items.PORKCHOP, Items.RABBIT, Items.COOKIE );
    }
    
    @SpecialMob.Factory
    public static EntityType.IFactory<HungryZombieEntity> getVariantFactory() { return HungryZombieEntity::new; }
    
    /** @return This entity's mob species. */
    @SpecialMob.SpeciesSupplier
    @Override
    public MobFamily.Species<? extends HungryZombieEntity> getSpecies() { return SPECIES; }
    
    
    //--------------- Variant-Specific Implementations ----------------
    
    public HungryZombieEntity( EntityType<? extends _SpecialZombieEntity> entityType, World world ) { super( entityType, world ); }
    
    /** Called during spawn finalization to set starting equipment. */
    @Override
    protected void populateDefaultEquipmentSlots( DifficultyInstance difficulty ) {
        super.populateDefaultEquipmentSlots( difficulty );
        setCanPickUpLoot( false );
    }
    
    /** Override to apply effects when this entity hits a target with a melee attack. */
    @Override
    protected void onVariantAttack( Entity target ) {
        if( level.isClientSide() ) return;
        
        if( target instanceof PlayerEntity && ForgeEventFactory.getMobGriefingEvent( level, this ) ) {
            final ItemStack food = MobHelper.stealRandomFood( (PlayerEntity) target );
            if( !food.isEmpty() ) {
                final Food foodStats = food.getItem().getFoodProperties();
                heal( Math.max( foodStats == null ? 0.0F : foodStats.getNutrition(), 1.0F ) );
                playSound( SoundEvents.PLAYER_BURP, 0.5F, random.nextFloat() * 0.1F + 0.9F );
                return;
            }
        }
        // Take a bite out of the target if they have no food to eat
        if( target instanceof LivingEntity ) {
            MobHelper.stealLife( this, (LivingEntity) target, 2.0F );
        }
    }
}