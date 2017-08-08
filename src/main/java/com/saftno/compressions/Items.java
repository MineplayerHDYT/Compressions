//==================================================================================

    package com.saftno.compressions;

//==================================================================================

    import com.saftno.compressions.Base.Entries;

//==================================================================================

    import net.minecraft.block.Block;
    import net.minecraft.block.material.Material;
    import net.minecraft.creativetab.CreativeTabs;
    import net.minecraft.item.Item;
    import net.minecraft.item.ItemBlock;
    import net.minecraft.item.ItemStack;
    import net.minecraft.item.crafting.FurnaceRecipes;
    import net.minecraft.item.crafting.IRecipe;
    import net.minecraft.item.crafting.Ingredient;
    import net.minecraft.nbt.NBTTagCompound;
    import net.minecraft.tileentity.TileEntityFurnace;
    import net.minecraft.util.NonNullList;
    import net.minecraft.util.ResourceLocation;
    import net.minecraftforge.common.MinecraftForge;
    import net.minecraftforge.event.ForgeEventFactory;
    import net.minecraftforge.event.RegistryEvent.Register;
    import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
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

        public static Entries<Item> items = new Entries<>( Base::UID );

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
            MinecraftForge.EVENT_BUS.unregister( Blocks.class );
        //--------------------------------------------------------------------------
        }

        public static void Generate() {
        //--------------------------------------------------------------------------
            if( Blocks.blocks.isEmpty() ) return;
        //--------------------------------------------------------------------------

            for( Block block : Blocks.blocks ) items.Add( new Compressed( block ) );

        //--------------------------------------------------------------------------

            Compressed bucket1 = null;
            Compressed bucket2 = null;
            Compressed bucket3 = null;

        //--------------------------------------------------------------------------
            for( Item entry : items ) {
        //--------------------------------------------------------------------------

                if( !( entry instanceof Compressed ) ) continue;

            //----------------------------------------------------------------------

                Compressed item = (Compressed) entry;
                Item       stem = item.stem.getItem();

            //----------------------------------------------------------------------
                if( stem.getRegistryName().getResourcePath().equals( "bucket" ) ) {
            //----------------------------------------------------------------------

                    if( 1 == item.level ) bucket1 = item;
                    if( 2 == item.level ) bucket1 = item;
                    if( 3 == item.level ) bucket1 = item;

            //----------------------------------------------------------------------
                }
        //--------------------------------------------------------------------------
            } for( Item entry : items ) {
        //--------------------------------------------------------------------------

                if( !( entry instanceof Compressed ) ) continue;

            //----------------------------------------------------------------------

                Compressed item = (Compressed) entry;
                Item       stem = item.stem.getItem();

            //----------------------------------------------------------------------
                if( stem.getRegistryName().getResourcePath().contains( "bucket" ) ){
            //----------------------------------------------------------------------

                    if( 1 == item.level && item != bucket1 )
                        item.setContainerItem( bucket1 );

                    if( 2 == item.level && item != bucket2 )
                        item.setContainerItem( bucket2 );

                    if( 3 == item.level && item != bucket3 )
                        item.setContainerItem( bucket3 );

            //----------------------------------------------------------------------
                }
        //--------------------------------------------------------------------------
            }
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

            @Override public int getItemBurnTime( ItemStack item ) {
            //----------------------------------------------------------------------
                Block AIR = net.minecraft.init.Blocks.AIR;
            //----------------------------------------------------------------------

                Block    block    = Block.getBlockFromItem( this.stem.getItem() );
                Material material = block.getDefaultState().getMaterial();

                if( AIR != block && !material.getCanBurn() ) return 0;

                if( AIR == block && !TileEntityFurnace.isItemFuel(this.stem) )
                    return 0;

            //----------------------------------------------------------------------
                String ID = this.stem.getItem().getRegistryName().toString();
            //----------------------------------------------------------------------

                Integer count = item.getCount();
                Integer exp   = (int) Math.pow( 9 , level );

                Integer defBurnTime = TileEntityFurnace.getItemBurnTime( this.stem);
                Integer stmBurnTime =this.stem.getItem().getItemBurnTime(this.stem);

            //----------------------------------------------------------------------

                     if( stmBurnTime >  0 ) return count * exp * stmBurnTime;
                else if( stmBurnTime == 0 ) return 0;
                else if( 0 != defBurnTime ) return count * exp * defBurnTime;

                return  count * exp * 300;

            //----------------------------------------------------------------------
            }

        //==========================================================================

        }

    //==============================================================================

    }

//==================================================================================

