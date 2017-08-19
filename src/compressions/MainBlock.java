//==================================================================================================

    package compressions;

//==================================================================================================

    import net.minecraft.block.Block;
    import net.minecraft.block.material.Material;
    import net.minecraft.block.properties.IProperty;
    import net.minecraft.block.properties.PropertyEnum;
    import net.minecraft.block.properties.PropertyInteger;
    import net.minecraft.block.state.BlockStateContainer;
    import net.minecraft.block.state.IBlockState;
    import net.minecraft.creativetab.CreativeTabs;
    import net.minecraft.util.math.BlockPos;
    import net.minecraft.world.IBlockAccess;
    import net.minecraftforge.common.property.ExtendedBlockState;
    import net.minecraftforge.common.property.IExtendedBlockState;
    import net.minecraftforge.common.property.IUnlistedProperty;
    import net.minecraftforge.common.property.PropertyFloat;
    import net.minecraftforge.event.RegistryEvent.Register;
    import net.minecraftforge.fml.common.Mod;
    import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

//==================================================================================================
    @Mod.EventBusSubscriber
//==================================================================================================

    public class MainBlock extends Block {

    //==============================================================================================

        public static MainBlock instance = new MainBlock( Material.WOOD );



    public static IUnlistedProperty<Integer> posx = new IUnlistedProperty<Integer>() {

        @Override public String getName() { return "PosX"; }

        @Override public boolean isValid(Integer value) { return true; }

        @Override public Class<Integer> getType() { return Integer.class; }

        @Override public String valueToString(Integer value) { return value.toString(); }
    };


    public static IUnlistedProperty<Integer> posy = new IUnlistedProperty<Integer>() {

        @Override public String getName() { return "PosY"; }

        @Override public boolean isValid(Integer value) { return true; }

        @Override public Class<Integer> getType() { return Integer.class; }

        @Override public String valueToString(Integer value) { return value.toString(); }
    };


    public static IUnlistedProperty<Integer> posz = new IUnlistedProperty<Integer>() {

        @Override public String getName() { return "PosZ"; }

        @Override public boolean isValid(Integer value) { return true; }

        @Override public Class<Integer> getType() { return Integer.class; }

        @Override public String valueToString(Integer value) { return value.toString(); }
    };

    //==============================================================================================

        public MainBlock( Material material ) {
        //------------------------------------------------------------------------------------------
            super( material );
        //------------------------------------------------------------------------------------------

            this.setRegistryName( Base.modId , "mainblock" );
            this.setUnlocalizedName( "mainblock" );

            this.setCreativeTab( CreativeTabs.MISC );

        //------------------------------------------------------------------------------------------
        }

        @Override public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
        {

            org.lwjgl.input.Mouse.setGrabbed(false);

            ExtendedBlockState exst = new ExtendedBlockState( instance , new IProperty[]{} ,
                    new IUnlistedProperty[] { posx , posy, posz });

            return ((IExtendedBlockState) exst).withProperty( posx , pos.getX() )
                    .withProperty( posy , pos.getY() )
                    .withProperty( posz , pos.getZ() );
        }


    //==============================================================================================
        @SubscribeEvent
    //==============================================================================================

        public static void Register( Register<Block> event ) {
        //------------------------------------------------------------------------------------------

            event.getRegistry().register( instance );

        //------------------------------------------------------------------------------------------
        }

    //==============================================================================================

    }

//==================================================================================================
