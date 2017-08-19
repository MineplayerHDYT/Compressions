//==================================================================================================

    package compressions;

//==================================================================================================

    import compressions.Configurations.Entry;

//==================================================================================================

    import net.minecraft.creativetab.CreativeTabs;
    import net.minecraft.item.Item;
    import net.minecraft.item.ItemBlock;
    import net.minecraft.item.ItemStack;
    import net.minecraft.nbt.NBTTagCompound;
    import net.minecraft.util.NonNullList;
    import net.minecraftforge.event.RegistryEvent.Register;
    import net.minecraftforge.fml.common.Mod;
    import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
    import net.minecraftforge.fml.common.registry.ForgeRegistries;

    import java.util.*;

//==================================================================================================
    @Mod.EventBusSubscriber
//==================================================================================================

    public class MainItem extends ItemBlock {

    //==============================================================================================

        public static MainItem instance = new MainItem();

        public static  Map<Entry , ItemStack> entries = new HashMap<>();

    //==============================================================================================

        public MainItem() {
        //------------------------------------------------------------------------------------------
            super( MainBlock.instance );
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

    //==============================================================================================

    }

//==================================================================================================
