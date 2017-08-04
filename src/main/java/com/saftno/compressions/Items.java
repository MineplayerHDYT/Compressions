//==================================================================================

    package com.saftno.compressions;

//==================================================================================

    import com.saftno.compressions.Base.Entries;

//==================================================================================

    import net.minecraft.block.Block;
    import net.minecraft.creativetab.CreativeTabs;
    import net.minecraft.item.Item;
    import net.minecraft.item.ItemBlock;
    import net.minecraft.item.ItemStack;
    import net.minecraft.item.crafting.IRecipe;
    import net.minecraft.item.crafting.Ingredient;
    import net.minecraft.nbt.NBTTagCompound;
    import net.minecraft.util.NonNullList;
    import net.minecraft.util.ResourceLocation;
    import net.minecraftforge.event.RegistryEvent.Register;
    import net.minecraftforge.fml.common.Mod;
    import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
    import net.minecraftforge.fml.common.registry.ForgeRegistries;
    import net.minecraftforge.registries.IForgeRegistry;

//==================================================================================

    import java.util.ArrayList;
    import java.util.HashSet;
    import java.util.List;
    import java.util.Set;

//==================================================================================
    @SuppressWarnings( { "WeakerAccess" , "unused" } ) @Mod.EventBusSubscriber
//==================================================================================

    public class Items {

    //==============================================================================
    // Setup
    //==============================================================================

        public static Entries<Item> items;

    //==============================================================================

        static /* creates arrays */ {
        //--------------------------------------------------------------------------

            items = new Entries<>( s -> s.getRegistryName().toString() );

        //--------------------------------------------------------------------------
        }

    //==============================================================================
        @SubscribeEvent
    //==============================================================================

        public static void Register( Register<IRecipe> event ) {
        //--------------------------------------------------------------------------
            if( Blocks.blocks.isEmpty() ) Blocks.Register( event );
        //--------------------------------------------------------------------------

            Generate();

        //--------------------------------------------------------------------------
            IForgeRegistry<Item> reg = ForgeRegistries.ITEMS;
        //--------------------------------------------------------------------------

            for( Item i : items ) if( !reg.containsValue( i ) ) reg.register( i );

        //--------------------------------------------------------------------------
        }

        public static void Generate() {
        //--------------------------------------------------------------------------

            for( Block block : Blocks.blocks ) items.Add( new Compressed( block ) );

        //--------------------------------------------------------------------------
        }

    //==============================================================================
    // usage
    //==============================================================================

        public static String getID( ItemStack item ) {
        //--------------------------------------------------------------------------
            String error1 = "'ItemStack' has invalid 'ResourceLocation'";
        //--------------------------------------------------------------------------

            ResourceLocation loc = item.getItem().getRegistryName();

            if( null == loc ) throw new NullPointerException( error1 );

        //--------------------------------------------------------------------------

            String name = loc.getResourceDomain() + '_' + loc.getResourcePath();

        //--------------------------------------------------------------------------

            if( !item.getHasSubtypes() ) return name;

        //--------------------------------------------------------------------------

            name = name + '_' + item.getMetadata();

        //--------------------------------------------------------------------------
            NBTTagCompound tag = item.getTagCompound();
        //--------------------------------------------------------------------------

            if( null == item.getTagCompound() ) return name;

        //--------------------------------------------------------------------------

            String extra = item.getTagCompound().toString();

            extra = extra.replace( "\"", ""  ).replace( " " , ""  );
            extra = extra.replace( "{" , ""  ).replace( "}" , ""  );
            extra = extra.replace( ":" , "_" ).replace( "," , "_" );

            return name + '_' + extra;

        //--------------------------------------------------------------------------
        }

    //==============================================================================

        public static List<ItemStack> getAll( List<IRecipe>recipes ) {
        //--------------------------------------------------------------------------
            List<ItemStack> items = new ArrayList<>();
        //--------------------------------------------------------------------------

            Set<String> IDs = new HashSet<>();

        //--------------------------------------------------------------------------
            for( IRecipe recipe : recipes ) {
        //--------------------------------------------------------------------------

                ItemStack output = recipe.getRecipeOutput();

                if( !IDs.contains( getID( output ) ) ) items.add( output );
                if( !IDs.contains( getID( output ) ) ) IDs.add( getID( output ) );

            //----------------------------------------------------------------------
                for( Ingredient input : recipe.getIngredients() ) {
            //----------------------------------------------------------------------
                    for( ItemStack stack : input.getMatchingStacks() ) {
                //------------------------------------------------------------------

                        if( IDs.contains( getID( stack ) ) ) continue;

                        IDs.add( getID( stack ) );
                        items.add( stack );

        //--------------------------------------------------------------------------
            } } } return items;
        //--------------------------------------------------------------------------
    }

        public static List<ItemStack> getAll( String[] IDs ) {
        //--------------------------------------------------------------------------

            List<ItemStack> items = new ArrayList<>();

        //--------------------------------------------------------------------------
            for( String ID : IDs ) { String[] id = ID.split( ":" );
        //--------------------------------------------------------------------------

                String  modID  = 2 > id.length ? ID   : id[0];
                String  itemID = 2 > id.length ? null : id[1];
                Integer varID  = 3 > id.length ? -1   : Integer.parseInt( id[2] );

            //----------------------------------------------------------------------

                List<ResourceLocation> locations = new ArrayList<>();

            //----------------------------------------------------------------------
                IForgeRegistry<Item> Items = ForgeRegistries.ITEMS;
            //----------------------------------------------------------------------
                if( 1 == id.length ) { for( Item item : Items.getValues() ) {
            //----------------------------------------------------------------------

                    ResourceLocation loc = item.getRegistryName();

                //------------------------------------------------------------------
                    if( null == loc ) continue;
                //------------------------------------------------------------------
                    if( !loc.getResourceDomain().equals( modID ) ) continue;
                //------------------------------------------------------------------

                    locations.add( item.getRegistryName() );

            //----------------------------------------------------------------------
                } }
            //----------------------------------------------------------------------

                if(1 < id.length) locations.add(new ResourceLocation(modID,itemID));

            //----------------------------------------------------------------------
                IForgeRegistry<Block> Blocks = ForgeRegistries.BLOCKS;
            //----------------------------------------------------------------------
                for( ResourceLocation loc : locations ) {
            //----------------------------------------------------------------------

                    Item  item  = Items.getValue( loc );
                    Block block = Blocks.getValue( loc );

                    if( null == item && null == block ) continue;

                //------------------------------------------------------------------

                    if( null == item ) item = Item.getItemFromBlock( block );
                    if( net.minecraft.init.Items.AIR == item ) continue;

                //------------------------------------------------------------------

                    if( !item.getHasSubtypes() ) items.add(new ItemStack(item,1,0));
                    if( !item.getHasSubtypes() ) continue;

                //------------------------------------------------------------------

                    if( varID >= 0 ) items.add( new ItemStack( item , 1 , varID ) );
                    if( varID >= 0 ) continue;

                //------------------------------------------------------------------

                    CreativeTabs tab = item.getCreativeTab();

                    if( null == tab ) tab = CreativeTabs.CREATIVE_TAB_ARRAY[0];

                //------------------------------------------------------------------

                    NonNullList<ItemStack> subItems = NonNullList.create();

                    item.getSubItems( tab , subItems );

                    items.addAll( subItems );

        //--------------------------------------------------------------------------
            } } return items;
        //--------------------------------------------------------------------------
    }

    //==============================================================================

        public static class Stem extends ItemBlock {

        //==========================================================================

            public Stem( Block block ) {
            //----------------------------------------------------------------------
                super( block );
            //----------------------------------------------------------------------

                this.setUnlocalizedName( block.getUnlocalizedName() );
                this.setRegistryName( block.getRegistryName() );

            //----------------------------------------------------------------------
            }

        //==========================================================================

        }

        public static class Compressed extends Stem {

        //==========================================================================

            ItemStack stem  = null;
            Integer   level = 0;

        //==========================================================================

            public Compressed( Block block ) {
            //----------------------------------------------------------------------
                super( block );
            //----------------------------------------------------------------------

                if( !( block instanceof Blocks.Compressed ) ) return;

            //----------------------------------------------------------------------

                Blocks.Compressed compressed = (Blocks.Compressed) block;

                this.stem  = compressed.stem;
                this.level = compressed.level;

            //----------------------------------------------------------------------
            }

        //==========================================================================

        }

    //==============================================================================

    }

//==================================================================================

