package fathertoast.specialmobs.common.entity.ai;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

/**
 * This AI goal tells the ninja when players are looking in its direction so it can 'freeze' in place and act like a block.
 * <p>
 * It also contains static methods to help ninjas choose a block to disguise as.
 */
public class NinjaGoal<T extends MobEntity & INinja> extends Goal {
    
    private final T ninja;
    
    public NinjaGoal( T entity ) {
        ninja = entity;
        setFlags( EnumSet.of( Goal.Flag.MOVE, Flag.LOOK, Goal.Flag.JUMP ) );
    }
    
    /** @return Returns true if this AI can be activated. */
    @Override
    public boolean canUse() {
        if( ninja.getHiddenDragon() == null || ninja.getHiddenDragon().getRenderShape() == BlockRenderType.INVISIBLE )
            return false;
        
        final List<PlayerEntity> players = new ArrayList<>( ninja.level.players() );
        for( PlayerEntity player : players ) {
            final float dX = (float) (ninja.getX() - player.getX());
            final float dZ = (float) (ninja.getZ() - player.getZ());
            final float angleFromPlayer = (float) Math.atan2( dX, -dZ ) * 180.0F / (float) Math.PI;
            
            if( Math.abs( angleFromPlayer - MathHelper.wrapDegrees( player.yHeadRot ) ) > 90.0F ) {
                return true; // A player is looking in the ninja's general direction!
            }
        }
        return false;
    }
    
    /** Called when this AI is activated. */
    @Override
    public void start() {
        ninja.getNavigation().stop();
        ninja.clearFire();
        ninja.moveTo( Math.floor( ninja.getX() ) + 0.5, Math.floor( ninja.getY() ), Math.floor( ninja.getZ() ) + 0.5 );
        ninja.setDeltaMovement( 0.0, 0.0, 0.0 );
        
        ninja.setCrouchingTiger( true );
    }
    
    /** @return Called each update while active and returns true if this AI can remain active. */
    @Override
    public boolean canContinueToUse() { return canUse(); }
    
    /** @return Whether this AI can be interrupted by a lower priority AI while running. */
    @Override
    public boolean isInterruptable() { return false; }
    
    /** Called when this AI is deactivated. */
    @Override
    public void stop() { ninja.setCrouchingTiger( false ); }
    
    
    //--------------- Static Disguise Helper ----------------
    
    /** Finds a nearby block for the entity to hide as and flags it to start hiding. */
    public static <T extends MobEntity & INinja> BlockState pickDisguise( T entity ) {
        final Random random = entity.getRandom();

        switch( random.nextInt( 200 ) ) {
            case 0: return Blocks.CHEST.defaultBlockState().setValue( BlockStateProperties.HORIZONTAL_FACING,
                    Direction.Plane.HORIZONTAL.getRandomDirection( random ) );
            case 1: return Blocks.OAK_LOG.defaultBlockState();
            case 2: return Blocks.SPONGE.defaultBlockState();
            case 3: return Blocks.DEAD_BUSH.defaultBlockState();
            case 4: return Blocks.OAK_LEAVES.defaultBlockState();
            case 5: return Blocks.BEE_NEST.defaultBlockState();
            case 6: return Blocks.CAKE.defaultBlockState();
            case 7: return Blocks.CRAFTING_TABLE.defaultBlockState();
            case 8: return Blocks.FURNACE.defaultBlockState();
            case 9: return Blocks.ANVIL.defaultBlockState();
            case 10: return Blocks.BREWING_STAND.defaultBlockState();
        }

        final BlockPos posUnderFeet = entity.blockPosition().below();
        final BlockState blockUnderFeet = entity.level.getBlockState( posUnderFeet );
        if( !blockUnderFeet.getBlock().isAir( blockUnderFeet, entity.level, posUnderFeet ) ) {
            // Options available based on the block we are standing on
            
            if( blockUnderFeet.is( Blocks.STONE ) || blockUnderFeet.is( Blocks.STONE_BRICKS ) ) {
                // Cave theme
                switch( random.nextInt( 30 ) ) {
                    case 0: return blockUnderFeet;
                    case 1: return Blocks.MOSSY_COBBLESTONE.defaultBlockState();
                    case 2: return Blocks.CLAY.defaultBlockState();
                    case 3: return Blocks.GRAVEL.defaultBlockState();
                    case 4: return Blocks.COAL_ORE.defaultBlockState();
                    case 5: return Blocks.IRON_ORE.defaultBlockState();
                    case 6: return Blocks.LAPIS_ORE.defaultBlockState();
                    case 7: return Blocks.GOLD_ORE.defaultBlockState();
                    case 8: return Blocks.REDSTONE_ORE.defaultBlockState();
                    case 9: return Blocks.DIAMOND_ORE.defaultBlockState();
                    case 10: return Blocks.EMERALD_ORE.defaultBlockState();
                }
            }
            else if( blockUnderFeet.is( Blocks.GRASS ) || blockUnderFeet.is( Blocks.DIRT ) || blockUnderFeet.is( Blocks.PODZOL ) ) {
                // Field theme
                switch( random.nextInt( 15 ) ) {
                    case 0: return blockUnderFeet;
                    case 1: return Blocks.OAK_LOG.defaultBlockState();
                    case 2: return Blocks.OAK_LEAVES.defaultBlockState();
                    case 3: return Blocks.PUMPKIN.defaultBlockState();
                    case 4: return Blocks.MELON.defaultBlockState();
                    case 5: return Blocks.TALL_GRASS.defaultBlockState();
                    case 6: return Blocks.FERN.defaultBlockState();
                    case 7: return Blocks.BEE_NEST.defaultBlockState();
                }
            }
            else if( blockUnderFeet.is( Blocks.SAND ) || blockUnderFeet.is( Blocks.RED_SAND ) ||
                    blockUnderFeet.is( Blocks.SANDSTONE ) || blockUnderFeet.is( Blocks.RED_SANDSTONE ) ) {
                // Desert theme
                switch( random.nextInt( 5 ) ) {
                    case 0: return blockUnderFeet;
                    case 1: return Blocks.CACTUS.defaultBlockState();
                    case 2: return Blocks.DEAD_BUSH.defaultBlockState();
                }
            }
            else if( blockUnderFeet.is( Blocks.NETHERRACK ) || blockUnderFeet.is( Blocks.NETHER_BRICKS ) ||
                    blockUnderFeet.is( Blocks.SOUL_SAND ) || blockUnderFeet.is( Blocks.SOUL_SOIL ) ||
                    blockUnderFeet.is( Blocks.CRIMSON_NYLIUM ) || blockUnderFeet.is( Blocks.WARPED_NYLIUM ) ) {
                // Nether theme
                switch( random.nextInt( 25 ) ) {
                    case 0: return blockUnderFeet;
                    case 1: return Blocks.GRAVEL.defaultBlockState();
                    case 2: return Blocks.SOUL_SAND.defaultBlockState();
                    case 3: return Blocks.GLOWSTONE.defaultBlockState();
                    case 4: return Blocks.NETHER_QUARTZ_ORE.defaultBlockState();
                    case 5: return Blocks.NETHER_GOLD_ORE.defaultBlockState();
                    case 6: return Blocks.ANCIENT_DEBRIS.defaultBlockState();
                    case 7: return Blocks.BROWN_MUSHROOM.defaultBlockState();
                    case 8: return Blocks.RED_MUSHROOM.defaultBlockState();
                    case 9: return Blocks.CRIMSON_STEM.defaultBlockState();
                    case 10: return Blocks.WARPED_STEM.defaultBlockState();
                }
            }
            else if( blockUnderFeet.is( Blocks.END_STONE ) || blockUnderFeet.is( Blocks.END_STONE_BRICKS ) ||
                    blockUnderFeet.is( Blocks.PURPUR_BLOCK ) ) {
                // End theme
                switch( random.nextInt( 10 ) ) {
                    case 0: return blockUnderFeet;
                    case 1: return Blocks.CHORUS_PLANT.defaultBlockState();
                    case 2: return Blocks.PURPUR_PILLAR.defaultBlockState();
                    case 3: return Blocks.END_ROD.defaultBlockState();
                }
            }
        }
        
        // Pick a random nearby render-able block
        final BlockPos.Mutable randPos = new BlockPos.Mutable();
        for( int i = 0; i < 16; i++ ) {
            randPos.set(
                    posUnderFeet.getX() + random.nextInt( 17 ) - 8,
                    posUnderFeet.getY() + random.nextInt( 5 ) - 2,
                    posUnderFeet.getZ() + random.nextInt( 17 ) - 8 );
            
            final BlockState randBlock = entity.level.getBlockState( randPos );
            if( randBlock.getRenderShape() != BlockRenderType.INVISIBLE ) return randBlock;
        }
        
        // Hide as a log if none of the other options are chosen
        return Blocks.OAK_LOG.defaultBlockState();
    }
}