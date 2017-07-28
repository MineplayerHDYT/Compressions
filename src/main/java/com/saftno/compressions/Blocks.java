//==================================================================================

    package com.saftno.compressions;

//==================================================================================

    import mcp.MethodsReturnNonnullByDefault;
    import net.minecraft.block.Block;
    import net.minecraft.block.SoundType;
    import net.minecraft.block.material.Material;
    import net.minecraft.block.state.IBlockState;
    import net.minecraft.creativetab.CreativeTabs;
    import net.minecraft.entity.Entity;
    import net.minecraft.item.Item;
    import net.minecraft.item.ItemBlock;
    import net.minecraft.item.ItemStack;
    import net.minecraft.item.crafting.FurnaceRecipes;
    import net.minecraft.item.crafting.IRecipe;
    import net.minecraft.nbt.NBTTagCompound;
    import net.minecraft.util.BlockRenderLayer;
    import net.minecraft.util.EnumFacing;
    import net.minecraft.util.NonNullList;
    import net.minecraft.util.ResourceLocation;
    import net.minecraft.util.math.BlockPos;
    import net.minecraft.world.IBlockAccess;
    import net.minecraft.world.World;
    import net.minecraftforge.client.event.ModelRegistryEvent;
    import net.minecraftforge.event.RegistryEvent.Register;
    import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
    import net.minecraftforge.fml.common.registry.ForgeRegistries;
    import net.minecraftforge.fml.relauncher.Side;
    import net.minecraftforge.fml.relauncher.SideOnly;
    import net.minecraftforge.registries.IForgeRegistry;

//==================================================================================

    import javax.annotation.ParametersAreNonnullByDefault;
    import java.util.ArrayList;

//==================================================================================
    @SuppressWarnings( { "WeakerAccess" , "unused" } )
//==================================================================================

    public class Blocks {

    //==============================================================================

        public static ArrayList<Stem> blocks;
        public static ArrayList<Compressed> compressions;

    //==============================================================================

        public static class Initialization {

        //==========================================================================

            static void Pre( FMLPreInitializationEvent event ) {
            //----------------------------------------------------------------------

                blocks       = new ArrayList<>();
                compressions = new ArrayList<>();

            //----------------------------------------------------------------------
            }

        //==========================================================================

        }

    //==============================================================================

        public static class Registration {

        //==========================================================================

            public static void Blocks( Register<Block> event ) {
            //----------------------------------------------------------------------
                IForgeRegistry<Block> registry = event.getRegistry();
            //----------------------------------------------------------------------

                for( Stem block : blocks ) registry.register( block );

            //----------------------------------------------------------------------
            }

        //==========================================================================

            public static void Items( Register<Item> event ) {
            //----------------------------------------------------------------------

                Generation.Compressed();

            //----------------------------------------------------------------------
                IForgeRegistry<Item> registry = event.getRegistry();
            //----------------------------------------------------------------------

                for( Stem block : blocks ) registry.register( block.getAsItem() );

            //--------------------------------------------------------------------------
            }

        //==========================================================================

            public static void Recipes( Register<IRecipe> event ) {
            //----------------------------------------------------------------------
                IForgeRegistry<IRecipe> registry = event.getRegistry();
            //----------------------------------------------------------------------

                ArrayList<IRecipe> crafting = new ArrayList<>();

                if(null==Resources.mod)crafting.addAll(Recipes.Generation.Compression()  );
                if(null==Resources.mod)crafting.addAll(Recipes.Generation.Decompression());

                for( IRecipe recipe : crafting ) { event.getRegistry().register( recipe ); }

            //--------------------------------------------------------------------------

                ArrayList<Recipes.Furnace> furnace = new ArrayList<>();

                furnace.addAll( Recipes.Generation.NonOreFurnace() );

                for( Recipes.Furnace recipe : furnace ) FurnaceRecipes.instance()
                        .addSmeltingRecipe(
                                recipe.input ,
                                recipe.output ,
                                recipe.experience );

            //--------------------------------------------------------------------------
            }

        //==========================================================================

            public static void Models( ModelRegistryEvent event ) {
            //----------------------------------------------------------------------

                for( Stem block: blocks ) Base.proxy.registerBlockRenderer( block );

            //----------------------------------------------------------------------
            }

        //==========================================================================

        }

        public static class Generation {

        //==========================================================================

            public static NonNullList<ItemStack> getAllItems( String ID ) {
            //----------------------------------------------------------------------

                NonNullList<ItemStack> items = NonNullList.create();

            //----------------------------------------------------------------------

                String[] id = ID.split( ":" );

                String  modID  = 2 > id.length ? ID   : id[0];
                String  itemID = 2 > id.length ? null : id[1];
                Integer varID  = 3 > id.length ? -1   : Integer.parseInt( id[2] );

            //----------------------------------------------------------------------

                NonNullList<ResourceLocation> locations = NonNullList.create();

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

                    item.getSubItems( tab , items );

            //----------------------------------------------------------------------
                } return items;
            //----------------------------------------------------------------------
            }

            public static Compressed getCompressed(int stage, ItemStack itemStack) {
            //----------------------------------------------------------------------

                Item  item  = itemStack.getItem();
                Block block = Block.getBlockFromItem( item );

            //----------------------------------------------------------------------
                Block AIR = net.minecraft.init.Blocks.AIR;
            //----------------------------------------------------------------------

                Material material;

                if( AIR == block ) material = Material.WOOD;
                else material = block.getBlockState().getBaseState().getMaterial();

            //----------------------------------------------------------------------

                return new Compressed( stage , material , itemStack );

            //----------------------------------------------------------------------
            }

        //==========================================================================

            public static void Compressed() {
            //----------------------------------------------------------------------

                NonNullList<ItemStack> entries = NonNullList.create();

            //----------------------------------------------------------------------
                String[] IDs = Configurations.getIDs();
            //----------------------------------------------------------------------

                for( String ID : IDs ) entries.addAll( getAllItems( ID ) );

            //----------------------------------------------------------------------

                int L1 = entries.size();
                int L2 = Configurations.getDepth();

            //----------------------------------------------------------------------
                for( int y = 0; y < L1; y++ ) { for( int x = 0; x < L2; x++ ) {
            //----------------------------------------------------------------------

                    Compressed block = getCompressed( x + 1 , entries.get( y ) );

                //----------------------------------------------------------------------

                    Blocks.blocks.add( block );
                    Blocks.compressions.add( block );

                //----------------------------------------------------------------------

                    ForgeRegistries.BLOCKS.register( block );

            //----------------------------------------------------------------------
                } }
            //----------------------------------------------------------------------
            }

        //==========================================================================

        }

    //==============================================================================

        public static class Stem extends Block {

        //==========================================================================

            public static String getItemFullName( ItemStack item ) {
            //----------------------------------------------------------------------
                String error1 = "'ItemStack' has invalid 'ResourceLocation'";
            //----------------------------------------------------------------------

                ResourceLocation loc = item.getItem().getRegistryName();

                if( null == loc ) throw new NullPointerException( error1 );

            //----------------------------------------------------------------------

                String name = loc.getResourceDomain() + '_' + loc.getResourcePath();

            //----------------------------------------------------------------------

                if( item.getHasSubtypes() ) name = name + '_' + item.getMetadata();

            //----------------------------------------------------------------------
                NBTTagCompound tag = item.getTagCompound();
            //----------------------------------------------------------------------

                if( null == item.getTagCompound() ) return name;

            //----------------------------------------------------------------------

                String extra = item.getTagCompound().toString();

                extra = extra.replace( "\"", ""  ).replace( " " , ""  );
                extra = extra.replace( "{" , ""  ).replace( "}" , ""  );
                extra = extra.replace( ":" , "_" ).replace( "," , "_" );

                return name + '_' + extra;

            //----------------------------------------------------------------------
            }

        //==========================================================================

            String    name;
            ItemBlock item;

        //==========================================================================

            ItemBlock getAsItem() { return this.item; }

        //==========================================================================

            public void Setup( String name ) {
            //----------------------------------------------------------------------

                this.name = name;
                this.item = new ItemBlock( this );

            //----------------------------------------------------------------------

                this.setUnlocalizedName( this.name );
                this.setRegistryName( this.name );

            //----------------------------------------------------------------------

                this.item.setUnlocalizedName( this.name );
                this.item.setRegistryName( this.name );

            //----------------------------------------------------------------------
            }

        //==========================================================================

            Stem( Material material ) {
            //----------------------------------------------------------------------
                super( material );
            //----------------------------------------------------------------------
            }

            Stem( Material material , String name ) {
            //----------------------------------------------------------------------
                super( material );
            //----------------------------------------------------------------------

                this.Setup( name );

            //----------------------------------------------------------------------
            }

        //==========================================================================

        }

        public static class Compressed extends Stem {

        //==========================================================================

            static final Block LEAVES = net.minecraft.init.Blocks.LEAVES;

        //==========================================================================

            ItemStack stem  = null;
            Integer   level = 0;

        //==========================================================================

            Compressed( int level , Material material , ItemStack item ) {
            //----------------------------------------------------------------------
                super( material );
            //----------------------------------------------------------------------

                this.level = level;
                this.stem  = item;

                this.setCreativeTab( CreativeTabs.MATERIALS );

            //----------------------------------------------------------------------

                setHardness( 1.5f * level );
                setResistance( 30f * level * level );

            //----------------------------------------------------------------------

                this.Setup( Stem.getItemFullName( item ) + '_' + this.level );

            //----------------------------------------------------------------------
            }

        //==========================================================================
            @Override @SideOnly( Side.CLIENT ) @MethodsReturnNonnullByDefault
        //==========================================================================

            public BlockRenderLayer getBlockLayer() {
            //----------------------------------------------------------------------

                Block block = Block.getBlockFromItem( stem.getItem() );

            //----------------------------------------------------------------------
                Block AIR = net.minecraft.init.Blocks.AIR;
            //----------------------------------------------------------------------

                if( AIR != block ) return block.getBlockLayer();

            //----------------------------------------------------------------------
                return BlockRenderLayer.SOLID;
            //----------------------------------------------------------------------
            }

        //==================================================================================
/*
            @SubscribeEvent public void getBlockLayer( FurnaceFuelBurnTimeEvent event ) {
            //------------------------------------------------------------------------------

                Integer burnTime   = stem.getItem().getItemBurnTime( stem );
                Integer multiplier = (int) Math.pow( 9 , level );

                event.setBurnTime( multiplier * burnTime );

            //------------------------------------------------------------------------------
            }//*/

        //==========================================================================
            @Override @SideOnly( Side.CLIENT ) @MethodsReturnNonnullByDefault
            @ParametersAreNonnullByDefault
        //==========================================================================

            public int getLightValue(IBlockState s,IBlockAccess w,BlockPos p) {
            //----------------------------------------------------------------------
                if( null == stem ) return super.getLightValue( s , w , p );
            //----------------------------------------------------------------------

                Block block = Block.getBlockFromItem( stem.getItem() );

            //----------------------------------------------------------------------
                Block AIR = net.minecraft.init.Blocks.AIR;
            //----------------------------------------------------------------------

                if( AIR != block ) return block.getDefaultState().getLightValue();

            //----------------------------------------------------------------------
                return super.getLightValue( s , w , p );
            //----------------------------------------------------------------------
            }

        //==========================================================================
            @Override @SideOnly( Side.CLIENT )
        //==========================================================================

            public boolean doesSideBlockRendering(IBlockState s,IBlockAccess w,
            /*/////////////////////////////////*/ BlockPos    p,EnumFacing   f){
            //----------------------------------------------------------------------
                if( null == stem ) return true;
            //----------------------------------------------------------------------

                Block block = Block.getBlockFromItem( stem.getItem() );

            //----------------------------------------------------------------------
                Block AIR = net.minecraft.init.Blocks.AIR;
            //----------------------------------------------------------------------

                if( AIR != block ) return block.getDefaultState().isOpaqueCube();

            //----------------------------------------------------------------------
                return true;
            //----------------------------------------------------------------------
            }

        //==========================================================================
            @Override @MethodsReturnNonnullByDefault
        //==========================================================================

            public SoundType getSoundType( IBlockState s , World  w ,
            /*//////////////////////////*/ BlockPos    p , Entity e ) {
            //------------------------------------------------------------------
                if( null == stem ) return LEAVES.getSoundType( s , w , p , e );
            //------------------------------------------------------------------

                Block block = Block.getBlockFromItem( stem.getItem() );

            //----------------------------------------------------------------------
                Block AIR = net.minecraft.init.Blocks.AIR;
            //----------------------------------------------------------------------

                if( AIR != block ) return block.getSoundType( s , w , p , e );

            //----------------------------------------------------------------------
                return block.getSoundType( s , w , p , e );
            //----------------------------------------------------------------------
            }

        //==========================================================================

        }

    //==============================================================================

    }

//==================================================================================

