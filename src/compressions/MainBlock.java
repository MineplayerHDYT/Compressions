//==================================================================================================

    package compressions;

//==================================================================================================

    import net.minecraft.block.Block;
    import net.minecraft.block.material.Material;
    import net.minecraft.block.properties.IProperty;
    import net.minecraft.block.state.BlockStateContainer;
    import net.minecraft.block.state.IBlockState;
    import net.minecraft.creativetab.CreativeTabs;
    import net.minecraft.item.Item;
    import net.minecraft.item.ItemStack;
    import net.minecraft.util.math.BlockPos;
    import net.minecraft.world.IBlockAccess;
    import net.minecraft.world.World;
    import net.minecraftforge.common.property.ExtendedBlockState;
    import net.minecraftforge.common.property.IExtendedBlockState;
    import net.minecraftforge.common.property.IUnlistedProperty;
    import net.minecraftforge.event.RegistryEvent.Register;
    import net.minecraftforge.fml.common.Mod;
    import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

    import java.util.HashMap;
    import java.util.Map;

//==================================================================================================
    @Mod.EventBusSubscriber
//==================================================================================================

    public class MainBlock extends Block {

    //==============================================================================================

        public static class UnlistedInteger implements IUnlistedProperty<Integer> {

            String name;

            UnlistedInteger( String name ) { this.name = name; }

            @Override public String getName() { return this.name; }

            @Override public boolean isValid(Integer value) { return true; }

            @Override public Class<Integer> getType() { return Integer.class; }

            @Override public String valueToString(Integer value) { return value.toString(); }
        }

    //==============================================================================================

        public static MainBlock instance = new MainBlock( Material.WOOD );

        public static UnlistedInteger PosX = new UnlistedInteger( "PosX" );
        public static UnlistedInteger PosY = new UnlistedInteger( "PosY" );
        public static UnlistedInteger PosZ = new UnlistedInteger( "PosZ" );

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

        @Override protected BlockStateContainer createBlockState() {

            //org.lwjgl.input.Mouse.setGrabbed(false);

            if( null == PosX ) PosX = new UnlistedInteger( "PosX" );
            if( null == PosY ) PosY = new UnlistedInteger( "PosY" );
            if( null == PosZ ) PosZ = new UnlistedInteger( "PosZ" );

            ExtendedBlockState ex = new ExtendedBlockState( this , new IProperty[0] ,
                    new IUnlistedProperty[] { PosX , PosY, PosZ });

            return ex;
        }

        @Override public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
        {

            //org.lwjgl.input.Mouse.setGrabbed(false);

            IExtendedBlockState exstate = (IExtendedBlockState) state;
            IUnlistedProperty posx = null;
            IUnlistedProperty posy = null;
            IUnlistedProperty posz = null;

            for( IUnlistedProperty prop : exstate.getUnlistedNames() ) {
                if( prop.getName().equals( "PosX") ) posx = prop;
                if( prop.getName().equals( "PosY") ) posy = prop;
                if( prop.getName().equals( "PosZ") ) posz = prop;
            }


            return ((IExtendedBlockState) state).withProperty( posx , pos.getX() )
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
