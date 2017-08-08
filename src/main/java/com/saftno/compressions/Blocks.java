//==================================================================================

    package com.saftno.compressions;

//==================================================================================

    import com.saftno.compressions.Base.Entries;

//==================================================================================

    import mcp.MethodsReturnNonnullByDefault;

//==================================================================================

    import net.minecraft.block.Block;
    import net.minecraft.block.SoundType;
    import net.minecraft.block.material.Material;
    import net.minecraft.block.state.IBlockState;
    import net.minecraft.creativetab.CreativeTabs;
    import net.minecraft.entity.Entity;
    import net.minecraft.item.Item;
    import net.minecraft.item.ItemBlock;
    import net.minecraft.item.ItemStack;
    import net.minecraft.item.crafting.IRecipe;
    import net.minecraft.util.BlockRenderLayer;
    import net.minecraft.util.EnumFacing;
    import net.minecraft.util.math.BlockPos;
    import net.minecraft.world.IBlockAccess;
    import net.minecraft.world.World;
    import net.minecraftforge.common.MinecraftForge;
    import net.minecraftforge.event.RegistryEvent.Register;
    import net.minecraftforge.fml.common.Mod;
    import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
    import net.minecraftforge.fml.common.registry.ForgeRegistries;
    import net.minecraftforge.fml.relauncher.Side;
    import net.minecraftforge.fml.relauncher.SideOnly;
    import net.minecraftforge.oredict.OreDictionary;
    import net.minecraftforge.registries.IForgeRegistry;
    import org.apache.commons.lang3.StringUtils;

//==================================================================================

    import javax.annotation.ParametersAreNonnullByDefault;
    import java.util.List;

//==================================================================================
    @SuppressWarnings( { "WeakerAccess" , "unused" } ) @Mod.EventBusSubscriber
//==================================================================================

    public class Blocks {

    //==============================================================================

        public static Entries<Block> blocks = new Entries<>( Base::UID );

    //==========================================================================
        @SubscribeEvent
    //==========================================================================

        public static void Register( Register<IRecipe> event ) {
        //--------------------------------------------------------------------------

            Generate();

        //--------------------------------------------------------------------------
            IForgeRegistry<Block> reg = ForgeRegistries.BLOCKS;
        //--------------------------------------------------------------------------

            for( Block b : blocks ) if( !reg.containsValue( b ) ) reg.register( b );

        //--------------------------------------------------------------------------
            MinecraftForge.EVENT_BUS.unregister( Blocks.class );
        //--------------------------------------------------------------------------
        }

        public static void Generate() {
        //--------------------------------------------------------------------------
            String[]        singleIDs = Configurations.getSingleIDs();
            List<ItemStack> single    = Items.getAll( singleIDs );

        //--------------------------------------------------------------------------

            String[]        relatedIDs     = Configurations.getRelatedIDs();
            List<ItemStack> relatedItems   = Items.getAll( relatedIDs );
            List<IRecipe>   relatedRecipes = Recipes.getRelated( relatedItems );
            List<ItemStack> related1       = Items.getAll( relatedRecipes );
            List<ItemStack> related        = Recipes.getSmeltingRelated( related1 );

        //--------------------------------------------------------------------------

            Entries<ItemStack> entries = new Entries<>( Items::getID );

            for( ItemStack stack : single  ) entries.Add( stack );
            for( ItemStack stack : related ) entries.Add( stack );

            entries.values.removeIf( s -> s.getItem()
                                           .getRegistryName()
                                           .getResourceDomain()
                                           .equals( "compressions" ) );

        //--------------------------------------------------------------------------

            int L1 = entries.Size();
            int L2 = Configurations.getDepth();

        //--------------------------------------------------------------------------
            for( int y = 0; y < L1; y++ ) { for( int x = 0; x < L2; x++ ) {
        //--------------------------------------------------------------------------

                Item  item  = entries.Get( y ).getItem();
                Block block = Block.getBlockFromItem( item );

            //----------------------------------------------------------------------
                Block AIR = net.minecraft.init.Blocks.AIR;
            //----------------------------------------------------------------------

                Material material;

                if( AIR == block ) material = Material.GROUND;
                else material = block.getBlockState().getBaseState().getMaterial();

            //----------------------------------------------------------------------

                Compressed compr = new Compressed(x + 1, material, entries.Get(y));

                blocks.Add( compr );

        //--------------------------------------------------------------------------
            } }
        //--------------------------------------------------------------------------
        }

    //==============================================================================

        public static class Stem extends Block {

        //==========================================================================

            String    name;
            ItemBlock item;

        //==========================================================================

            ItemBlock getAsItem() { return this.item; }

        //==========================================================================

            public void Setup( String name , Compressed stem ) {
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

        //==========================================================================

        }

    //==============================================================================

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
                String name = Base.UID( item );
            //----------------------------------------------------------------------

                this.Setup( name + '_' + this.level + 'x' , this );

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

