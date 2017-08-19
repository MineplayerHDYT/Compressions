//==================================================================================================

    package compressions;

//==================================================================================================

    import net.minecraft.block.Block;
    import net.minecraft.block.material.Material;
    import net.minecraft.block.properties.IProperty;
    import net.minecraft.block.state.BlockStateContainer;
    import net.minecraft.block.state.IBlockState;
    import net.minecraft.creativetab.CreativeTabs;
    import net.minecraft.util.math.BlockPos;
    import net.minecraft.world.IBlockAccess;
    import net.minecraftforge.common.property.ExtendedBlockState;
    import net.minecraftforge.common.property.IExtendedBlockState;
    import net.minecraftforge.common.property.IUnlistedProperty;
    import net.minecraftforge.event.RegistryEvent.Register;
    import net.minecraftforge.fml.common.Mod;
    import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

//==================================================================================================
    @Mod.EventBusSubscriber
//==================================================================================================

    public class MainBlock {

    //==============================================================================================
    // Structure
    //==============================================================================================

        public static class Compressed extends Block {

        //==========================================================================================
        // Structure
        //==========================================================================================

            public static class UnlistedInteger implements IUnlistedProperty<Integer> {

            //======================================================================================

                String name;

            //======================================================================================

                UnlistedInteger( String name ) { this.name = name; }

            //======================================================================================

                @Override public String         getName(               ) { return this.name;     }
                @Override public boolean        isValid( Integer value ) { return true;          }
                @Override public Class<Integer> getType(               ) { return Integer.class; }
                @Override public String         valueToString( Integer value ) { return "" +value; }

            //======================================================================================

            }

        //==========================================================================================
        // Usage
        //==========================================================================================

            public Compressed( Material material , String ID ) {
            //--------------------------------------------------------------------------------------
                super( material );
            //--------------------------------------------------------------------------------------

                this.setRegistryName( Base.modId , ID );
                this.setUnlocalizedName( ID );

            //--------------------------------------------------------------------------------------

                this.setCreativeTab( CreativeTabs.MISC );

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================
        // Setup
        //==========================================================================================

            @Override protected BlockStateContainer createBlockState() {
            //--------------------------------------------------------------------------------------

                return new ExtendedBlockState( this , new IProperty[0] , new IUnlistedProperty[] {
                        new UnlistedInteger( "PosX" ) ,
                        new UnlistedInteger( "PosY" ) ,
                        new UnlistedInteger( "PosZ" ) } );

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public IBlockState getExtendedState( IBlockState  state    ,
            /********************************************/ IBlockAccess world    ,
            /********************************************/ BlockPos     position ) {
            //--------------------------------------------------------------------------------------

                IExtendedBlockState extended = (IExtendedBlockState) state;

            //--------------------------------------------------------------------------------------
                for( IUnlistedProperty property : extended.getUnlistedNames() ) {
            //--------------------------------------------------------------------------------------

                    if( property.getName().equals( "PosX" ) )
                        extended = extended.withProperty( property , position.getX() );

                    if( property.getName().equals( "PosY" ) )
                        extended = extended.withProperty( property , position.getY() );

                    if( property.getName().equals( "PosZ" ) )
                        extended = extended.withProperty( property , position.getZ() );

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------

                return extended;

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

        }

    //==============================================================================================

        public static final Compressed controlCMP = new Compressed( Material.WOOD , "compressed" );

    //==============================================================================================
    // Setup
    //==============================================================================================

        @SubscribeEvent public static void Register( Register<Block> event ) {
        //------------------------------------------------------------------------------------------

            event.getRegistry().register( MainBlock. controlCMP );

        //------------------------------------------------------------------------------------------
        }

    //==============================================================================================

    }

//==================================================================================================
