//==================================================================================================

    package compressions;

//==================================================================================================

    import compressions.Configurations.Entry;

//==================================================================================================

    import net.minecraft.advancements.CriteriaTriggers;
    import net.minecraft.block.state.IBlockState;
    import net.minecraft.creativetab.CreativeTabs;
    import net.minecraft.entity.player.EntityPlayer;
    import net.minecraft.entity.player.EntityPlayerMP;
    import net.minecraft.item.Item;
    import net.minecraft.item.ItemBlock;
    import net.minecraft.item.ItemStack;
    import net.minecraft.nbt.NBTTagCompound;
    import net.minecraft.util.EnumFacing;
    import net.minecraft.util.NonNullList;
    import net.minecraft.util.math.BlockPos;
    import net.minecraft.world.World;
    import net.minecraftforge.event.RegistryEvent.Register;
    import net.minecraftforge.fml.common.Mod;
    import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
    import net.minecraftforge.fml.common.registry.ForgeRegistries;
    import org.apache.commons.lang3.tuple.ImmutablePair;
    import org.apache.commons.lang3.tuple.Pair;

    import java.util.*;

//==================================================================================================
    @Mod.EventBusSubscriber
//==================================================================================================

    public class MainItem extends ItemBlock {

    //==============================================================================================

        public static MainItem instance = new MainItem();

        public static Map<Entry , ItemStack> entries = new HashMap<>();

    //==============================================================================================

        public static class Position {

        //==========================================================================================
        // Structure
        //==========================================================================================

            Integer x;
            Integer y;
            Integer z;

        //==========================================================================================
        // Unique Identification
        //==========================================================================================

            @Override public boolean equals( Object object ) {
                //--------------------------------------------------------------------------------------
                if( !( object instanceof Position ) ) return false;
                //--------------------------------------------------------------------------------------

                Position other = (Position) object;

                //--------------------------------------------------------------------------------------

                if( !this.x.equals( other.x ) ) return false;
                if( !this.y.equals( other.y ) ) return false;
                if( !this.z.equals( other.z ) ) return false;

                //--------------------------------------------------------------------------------------
                return true;
                //--------------------------------------------------------------------------------------
            }

            @Override public int hashCode() {
            //--------------------------------------------------------------------------------------

                Integer hash = Base.Hash( this.x ) ^ Base.Hash( this.y ) ^ Base.Hash( this.z );

            //--------------------------------------------------------------------------------------
                return hash;
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================
        // Usage
        //==========================================================================================

            Position( BlockPos pos ) {
            //--------------------------------------------------------------------------------------

                this.x = pos.getX();
                this.y = pos.getY();
                this.z = pos.getZ();

            //--------------------------------------------------------------------------------------
            }

            Position( int x , int y , int z ) {
            //--------------------------------------------------------------------------------------

                this.x = x;
                this.y = y;
                this.z = z;

            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================

        }

        public static Map<Position , Pair<World , ItemStack>> placed = new HashMap<>();

    //==============================================================================================

        public MainItem() {
        //------------------------------------------------------------------------------------------
            super( MainBlock.controlCMP );
        //------------------------------------------------------------------------------------------

            this.setRegistryName( Base.modId , "mainitem" );
            this.setUnlocalizedName( "mainitem" );

            this.setCreativeTab( CreativeTabs.MISC );
            this.setHasSubtypes( true );

        //------------------------------------------------------------------------------------------
        }

    //==============================================================================================
        @SubscribeEvent
    //==============================================================================================

        public static void Register( Register<Item> event ) {
        //------------------------------------------------------------------------------------------

            event.getRegistry().register( instance );

        //------------------------------------------------------------------------------------------
        }

        public static void Generate() {
        //--------------------------------------------------------------------------------------
            for( Entry entry : Configurations.getEntries() ) {
        //--------------------------------------------------------------------------------------

                List<Item> items = new ArrayList<>( ForgeRegistries.ITEMS.getValues() );

            //----------------------------------------------------------------------------------

                if( null != entry.Mod ) items.removeIf( s -> !s.getRegistryName()
                        .getResourceDomain()
                        .equals( entry.Mod ) );

                if( null != entry.Entry ) items.removeIf( s -> !s.getRegistryName()
                        .getResourcePath()
                        .equals( entry.Entry ) );

            //----------------------------------------------------------------------------------
                for( Item item : items ) {
            //----------------------------------------------------------------------------------

                    CreativeTabs      tab = item.getCreativeTab();
                    if( null == tab ) tab = CreativeTabs.CREATIVE_TAB_ARRAY[0];

                //----------------------------------------------------------------------------------

                    NonNullList<ItemStack> stacks = NonNullList.create();
                    item.getSubItems( tab , stacks );

                    if( null != entry.Meta )
                        stacks.removeIf( s -> !entry.Meta.equals( s.getMetadata() ) );

                    if( null != entry.NBT ) stacks.removeIf( s -> !s.hasTagCompound() );
                    if( null != entry.NBT )
                        stacks.removeIf( s -> !s.getTagCompound().toString()
                                .replace( " " , "" ).toLowerCase()
                                .equals( entry.NBT.replace( " " , "" )
                                        .toLowerCase() ) );

                //------------------------------------------------------------------------------
                    for( ItemStack stack : stacks ) {
                //------------------------------------------------------------------------------

                        entry.Mod   = stack.getItem().getRegistryName().getResourceDomain();
                        entry.Entry = stack.getItem().getRegistryName().getResourcePath();
                        entry.Meta  = stack.getMetadata();
                        entry.NBT   = stack.hasTagCompound() ? stack.getTagCompound().toString() : "";

                        entries.put( new Entry( entry ) , stack );

        //--------------------------------------------------------------------------------------
            } } }
        //--------------------------------------------------------------------------------------
        }

    //==============================================================================================

        @Override public void getSubItems( CreativeTabs tab , NonNullList<ItemStack> items ) {
        //------------------------------------------------------------------------------------------

            if( entries.isEmpty() ) Generate();

        //------------------------------------------------------------------------------------------
            for( Entry entry : entries.keySet() ) { for( int i = 0; i < entry.Height; i++ ) {
        //------------------------------------------------------------------------------------------

                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger( "Width"  , entry.Width  );
                tag.setInteger( "Height" , i            );
                tag.setString ( "Mod"    , entry.Mod    );
                tag.setString ( "Entry"  , entry.Entry  );
                tag.setInteger( "Meta"   , entry.Meta   );
                tag.setString ( "NBT"    , entry.NBT    );

                ItemStack stack = new ItemStack( this , 1 , 0 );
                stack.setTagCompound( tag );

                items.add( stack );

        //------------------------------------------------------------------------------------------
            } }
        //------------------------------------------------------------------------------------------
        }

        @Override public boolean placeBlockAt( ItemStack stack,
                                     EntityPlayer player,
                                     World world,
                                     BlockPos pos,
                                     EnumFacing side,
                                     float hitX,
                                     float hitY,
                                     float hitZ,
                                     IBlockState newState ) {
        //------------------------------------------------------------------------------------------

            if( world.isRemote ) return false;

            placed.put( new Position( pos ) , new ImmutablePair<>( world , stack ) );

            return super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);

        //------------------------------------------------------------------------------------------
        }
    //==============================================================================================

    }

//==================================================================================================
