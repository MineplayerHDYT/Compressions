//==================================================================================================

    package compressions;

//==================================================================================================

    import net.minecraft.block.Block;
    import net.minecraft.block.state.IBlockState;
    import net.minecraft.creativetab.CreativeTabs;
    import net.minecraft.entity.player.EntityPlayer;
    import net.minecraft.item.Item;
    import net.minecraft.item.ItemBlock;
    import net.minecraft.item.ItemStack;
    import net.minecraft.util.EnumFacing;
    import net.minecraft.util.NonNullList;
    import net.minecraft.util.math.BlockPos;
    import net.minecraft.world.World;
    import net.minecraftforge.event.RegistryEvent.Register;
    import net.minecraftforge.fml.common.Mod;
    import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
    import org.apache.commons.lang3.tuple.ImmutablePair;
    import org.apache.commons.lang3.tuple.Pair;

//==================================================================================================

    import java.util.*;

//==================================================================================================
    @Mod.EventBusSubscriber
//==================================================================================================

    public class MainItem {

    //==============================================================================================
    // Structure
    //==============================================================================================
        public static Map<Integer , Map<BlockPos , ItemStack>> placed = new HashMap<>();
    //==============================================================================================


        public static class Compressed extends ItemBlock {

        //==========================================================================================

            public Compressed( Block block , String ID ) {
            //--------------------------------------------------------------------------------------
                super( block );
            //--------------------------------------------------------------------------------------

                this.setRegistryName( Base.modId , ID );
                this.setUnlocalizedName( ID );

            //--------------------------------------------------------------------------------------

                this.setCreativeTab( CreativeTabs.MISC );
                this.setHasSubtypes( true );

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

            @Override public void getSubItems( CreativeTabs tab , NonNullList<ItemStack> items ) {
            //--------------------------------------------------------------------------------------
                for( ItemStack entry : Configurations.entries ) {
            //--------------------------------------------------------------------------------------

                    Integer height = entry.getSubCompound( "Compression" ).getInteger( "Height" );

                //----------------------------------------------------------------------------------
                    for( int i = 0; i < height; i++ ) {
                //----------------------------------------------------------------------------------

                    ItemStack stack = new ItemStack( this , 1 , 0 );

                    stack.setTagCompound( entry.getSubCompound( "Compression" ) );
                    stack.getTagCompound().setInteger( "Height" , i );

                    items.add( stack );

            //--------------------------------------------------------------------------------------
                } }
            //--------------------------------------------------------------------------------------
            }

            @Override public boolean placeBlockAt( ItemStack    stack
            /**********************************/ , EntityPlayer player
            /**********************************/ , World        world
            /**********************************/ , BlockPos     pos
            /**********************************/ , EnumFacing   side
            /**********************************/ , float        hitX
            /**********************************/ , float        hitY
            /**********************************/ , float        hitZ
            /**********************************/ , IBlockState  newState ) {
            //--------------------------------------------------------------------------------------
                if( world.isRemote ) return false;
            //--------------------------------------------------------------------------------------

                Integer dimID = world.provider.getDimension();

                if( !placed.containsKey( dimID ) ) placed.put( dimID , new HashMap<>() );

            //--------------------------------------------------------------------------------------

                placed.get( dimID ).put( pos , stack );

            //--------------------------------------------------------------------------------------
                return super.placeBlockAt( stack , player , world    ,
                                           pos   , side   , hitX     ,
                                           hitY  , hitZ   , newState );
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================
        }


    //==============================================================================================
        public static Compressed controlCMP = new Compressed( MainBlock.controlCMP , "compressed" );
    //==============================================================================================
        @SubscribeEvent
    //==============================================================================================

        public static void Register( Register<Item> event ) {
        //------------------------------------------------------------------------------------------

            event.getRegistry().register( MainItem.controlCMP );

        //------------------------------------------------------------------------------------------
        }

    //==============================================================================================

    }

//==================================================================================================
