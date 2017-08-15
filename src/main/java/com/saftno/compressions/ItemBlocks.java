//==============================================================================================

    package com.saftno.compressions;

//==============================================================================================

    import com.saftno.compressions.Configurations.Entry;

//==============================================================================================

    import mcp.MethodsReturnNonnullByDefault;
    import net.minecraft.block.Block;
    import net.minecraft.block.SoundType;
    import net.minecraft.block.material.Material;
    import net.minecraft.block.state.IBlockState;
    import net.minecraft.creativetab.CreativeTabs;
    import net.minecraft.entity.Entity;
    import net.minecraft.init.Blocks;
    import net.minecraft.item.Item;
    import net.minecraft.item.ItemBlock;
    import net.minecraft.item.ItemStack;
    import net.minecraft.tileentity.TileEntityFurnace;
    import net.minecraft.util.BlockRenderLayer;
    import net.minecraft.util.EnumFacing;
    import net.minecraft.util.NonNullList;
    import net.minecraft.util.math.BlockPos;
    import net.minecraft.world.IBlockAccess;
    import net.minecraft.world.World;
    import net.minecraftforge.fml.common.registry.ForgeRegistries;
    import net.minecraftforge.fml.relauncher.Side;
    import net.minecraftforge.fml.relauncher.SideOnly;

//==============================================================================================

    import javax.annotation.ParametersAreNonnullByDefault;
    import java.util.ArrayList;
    import java.util.HashSet;
    import java.util.List;
    import java.util.Set;

//==============================================================================================

    public class ItemBlocks {

    //==========================================================================================
    // Structure
    //==========================================================================================

        public static Integer count = 0;

    //==========================================================================================

        public static class Compressed {

        //======================================================================================
        // Structure
        //======================================================================================

            public static class ItemX  extends ItemBlock {
            //==================================================================================

                Integer  level;
                ItemStack base;
                Integer  multi;

            //==================================================================================

                public ItemX( Block block, String name , ItemStack base , Integer level ) {
                //------------------------------------------------------------------------------
                    super(block);
                //------------------------------------------------------------------------------

                    this.setRegistryName( name );
                    this.setUnlocalizedName( name );

                    this.setCreativeTab( CreativeTabs.MATERIALS );

                //------------------------------------------------------------------------------

                    this.level = level;
                    this.base  = base;

                    this.multi = (int) Math.pow( base.getCount() , level );

                //------------------------------------------------------------------------------
                    ItemBlocks.count++;
                //------------------------------------------------------------------------------
                }

            //==================================================================================
            // Properties
            //==================================================================================

                @Override public int getItemBurnTime( ItemStack item ) {
                //------------------------------------------------------------------------------
                    Block AIR = Blocks.AIR;
                //------------------------------------------------------------------------------

                    Block    block    = Block.getBlockFromItem( base.getItem() );
                    Material material = block.getDefaultState().getMaterial();

                    if( AIR != block && !material.getCanBurn() ) return 0;
                    if( AIR == block && !TileEntityFurnace.isItemFuel( base ) ) return 0;

                //------------------------------------------------------------------------------

                    Integer defBurnTime = TileEntityFurnace.getItemBurnTime( base );
                    Integer stmBurnTime = base.getItem().getItemBurnTime( base );

                         if( stmBurnTime >  0 ) return stmBurnTime * item.getCount() * multi;
                    else if( stmBurnTime == 0 ) return 0;
                    else if( 0 != defBurnTime ) return defBurnTime * item.getCount() * multi;

                //------------------------------------------------------------------------------

                    return 300 * item.getCount() * multi;

                //------------------------------------------------------------------------------
                }

            //==================================================================================
            }

            public static class BlockX extends Block {
            //==================================================================================

                Integer  level;
                ItemStack base;
                Integer  multi;

            //==================================================================================

                public BlockX( String name , ItemStack base , Integer level ) {
                //------------------------------------------------------------------------------
                    super(  Blocks.AIR == Block.getBlockFromItem( base.getItem() ) ?
                            Material.GROUND :
                            Block.getBlockFromItem( base.getItem() )
                                    .getBlockState()
                                    .getBaseState()
                                    .getMaterial() );
                //------------------------------------------------------------------------------

                    this.setRegistryName( name );
                    this.setUnlocalizedName( name );

                    this.setCreativeTab( CreativeTabs.MATERIALS );

                //------------------------------------------------------------------------------

                    this.setHardness( 1.5f * level );
                    this.setResistance( 30f * level * level );

                //------------------------------------------------------------------------------

                    this.level = level;
                    this.base  = base;

                    this.multi = (int) Math.pow( base.getCount() , level );

                //------------------------------------------------------------------------------
                }

            //==================================================================================
                @Override @SideOnly( Side.CLIENT ) @MethodsReturnNonnullByDefault
            //==================================================================================

                public BlockRenderLayer getBlockLayer() {
                //----------------------------------------------------------------------

                    Block block = Block.getBlockFromItem( base.getItem() );

                //----------------------------------------------------------------------
                    Block AIR = net.minecraft.init.Blocks.AIR;
                //----------------------------------------------------------------------

                    if( AIR != block ) return block.getBlockLayer();

                //----------------------------------------------------------------------
                    return BlockRenderLayer.SOLID;
                //----------------------------------------------------------------------
                }

            //==================================================================================
                @Override @SideOnly( Side.CLIENT ) @MethodsReturnNonnullByDefault
                @ParametersAreNonnullByDefault
            //==================================================================================

                public int getLightValue(IBlockState s, IBlockAccess w, BlockPos p) {
                //----------------------------------------------------------------------
                    if( null == base ) return super.getLightValue( s , w , p );
                //----------------------------------------------------------------------

                    Block block = Block.getBlockFromItem( base.getItem() );

                //----------------------------------------------------------------------
                    Block AIR = net.minecraft.init.Blocks.AIR;
                //----------------------------------------------------------------------

                    if( AIR != block ) return block.getDefaultState().getLightValue();

                //----------------------------------------------------------------------
                    return super.getLightValue( s , w , p );
                //----------------------------------------------------------------------
                }

            //==================================================================================
                @Override @SideOnly( Side.CLIENT )
            //==================================================================================

                public boolean doesSideBlockRendering(IBlockState s,IBlockAccess w,
                /*/////////////////////////////////*/ BlockPos    p,EnumFacing f){
                //----------------------------------------------------------------------
                    if( null == base ) return true;
                //----------------------------------------------------------------------

                    Block block = Block.getBlockFromItem( base.getItem() );

                //----------------------------------------------------------------------
                    Block AIR = net.minecraft.init.Blocks.AIR;
                //----------------------------------------------------------------------

                    if( AIR != block ) return block.getDefaultState().isOpaqueCube();

                //----------------------------------------------------------------------
                    return true;
                //----------------------------------------------------------------------
                }

            //==================================================================================
                @Override @MethodsReturnNonnullByDefault
            //==================================================================================

                public SoundType getSoundType(IBlockState s , World w ,
                /*//////////////////////////*/ BlockPos    p , Entity e ) {
                //------------------------------------------------------------------
                    if( null == base ) return Blocks.LEAVES.getSoundType( s , w , p , e );
                //------------------------------------------------------------------

                    Block block = Block.getBlockFromItem( base.getItem() );

                //----------------------------------------------------------------------
                    Block AIR = net.minecraft.init.Blocks.AIR;
                //----------------------------------------------------------------------

                    if( AIR != block ) return block.getSoundType( s , w , p , e );

                //----------------------------------------------------------------------
                    return block.getSoundType( s , w , p , e );
                //----------------------------------------------------------------------
                }

            //==================================================================================

            }

        //======================================================================================

            List<ItemX>  items  = new ArrayList<>();
            List<BlockX> blocks = new ArrayList<>();

        //======================================================================================

            ItemStack stack;

        //======================================================================================
        // Unique identification
        //======================================================================================

            public Entry getEntry() {
            //----------------------------------------------------------------------------------
                Entry entry = new Entry();
            //----------------------------------------------------------------------------------

                entry.Width  = stack.getCount();
                entry.Height = items.size();

                entry.Mod   = stack.getItem().getRegistryName().getResourceDomain();
                entry.Entry = stack.getItem().getRegistryName().getResourcePath();

                entry.Meta = stack.getMetadata();
                entry.NBT  = stack.hasTagCompound() ? stack.getTagCompound().toString() : "";

            //----------------------------------------------------------------------------------
                return entry;
            //----------------------------------------------------------------------------------
            }

        //======================================================================================

            @Override public boolean equals( Object object ) {
            //---------------------------------------------------------------------------------
                if( !( object instanceof Compressed ) ) return false;
            //---------------------------------------------------------------------------------

                Compressed other = (Compressed) object;

            //---------------------------------------------------------------------------------

                return this.getEntry().equals( other.getEntry() );

            //---------------------------------------------------------------------------------
            }

            @Override public int hashCode() {
            //---------------------------------------------------------------------------------

                return this.getEntry().hashCode();

            //---------------------------------------------------------------------------------
            }

        //======================================================================================
        // Usage
        //======================================================================================

            public Compressed( Entry entry , ItemStack stack ) {
            //----------------------------------------------------------------------------------

                this.stack = stack;

            //----------------------------------------------------------------------------------

                String  modID = stack.getItem().getRegistryName().getResourceDomain();
                String  name  = stack.getItem().getRegistryName().getResourcePath();
                Integer meta  = stack.getMetadata();
                String  NBT   = stack.hasTagCompound() ? stack.getTagCompound().toString() : "";

            //----------------------------------------------------------------------------------

                String nameBase = "";

                nameBase += modID + "_";
                nameBase += name  + "_";
                nameBase += meta  + "_";

            //----------------------------------------------------------------------------------

                NBT = NBT.replace( " " , ""  );
                NBT = NBT.replace( "{" , "_" );
                NBT = NBT.replace( "}" , "_" );
                NBT = NBT.replace( "[" , "_" );
                NBT = NBT.replace( "]" , "_" );
                NBT = NBT.replace( ":" , "_" );
                NBT = NBT.replace( "," , "_" );
                NBT = NBT.replace( "\"" , "_" );

                NBT = NBT.replace( "__" , "_" );
                NBT = NBT.replace( "__" , "_" );
                NBT = NBT.replace( "__" , "_" );
                NBT = NBT.replace( "__" , "_" );
                NBT = NBT.replace( "__" , "_" );
                NBT = NBT.replace( "__" , "_" );
                NBT = NBT.replace( "__" , "_" );

                nameBase += NBT + "_";
                nameBase  = nameBase.replace( "__" , "_" );
                nameBase  = nameBase.toLowerCase();

            //----------------------------------------------------------------------------------
                for( int h = 1; h <= entry.Height; h++ ) {
            //----------------------------------------------------------------------------------

                    String ID = nameBase + h + "x" + entry.Width;

                    BlockX block = new BlockX(         ID , stack , h );
                    ItemX  item  = new ItemX ( block , ID , stack , h );

                    blocks.add( block );
                    items.add ( item  );

            //----------------------------------------------------------------------------------
                }
            //----------------------------------------------------------------------------------
            }

        //======================================================================================

        }

    //==========================================================================================

        public static List<Compressed> entries = new ArrayList<>();

    //==========================================================================================
    // Setup
    //==========================================================================================

        public static void Register() {
        //--------------------------------------------------------------------------------------

            Generate();

        //--------------------------------------------------------------------------------------
            for( Compressed compressed : entries ) {
        //--------------------------------------------------------------------------------------

                for( Block block : compressed.blocks ) ForgeRegistries.BLOCKS.register( block );
                for( Item  item  : compressed.items  ) ForgeRegistries.ITEMS.register ( item  );

        //--------------------------------------------------------------------------------------
            }
        //--------------------------------------------------------------------------------------
        }

        public static void Generate() {
        //--------------------------------------------------------------------------------------

            Set<Compressed> entries = new HashSet<>();

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

                        stack.setCount( entry.Width );

                        entries.add( new Compressed( entry , stack ) );

        //--------------------------------------------------------------------------------------
            } } }
        //--------------------------------------------------------------------------------------

            ItemBlocks.entries.addAll( entries );

            ItemBlocks.entries.sort(
                ( a , b ) ->
                a.stack.getItem().getRegistryName().getResourcePath().compareToIgnoreCase(
                b.stack.getItem().getRegistryName().getResourcePath() )
            );

        //--------------------------------------------------------------------------------------
        }

    //==========================================================================================

    }

//==============================================================================================
