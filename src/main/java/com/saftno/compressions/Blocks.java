//==========================================================================================

    package com.saftno.compressions;

//==========================================================================================

    import net.minecraft.block.Block;
    import net.minecraft.block.material.Material;
    import net.minecraft.creativetab.CreativeTabs;
    import net.minecraft.item.Item;
    import net.minecraft.item.ItemBlock;
    import net.minecraft.item.ItemStack;
    import net.minecraft.item.crafting.IRecipe;
    import net.minecraft.util.NonNullList;
    import net.minecraft.util.ResourceLocation;
    import net.minecraftforge.client.event.ModelRegistryEvent;
    import net.minecraftforge.event.RegistryEvent.Register;
    import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
    import net.minecraftforge.fml.common.registry.ForgeRegistries;

//==========================================================================================

    import org.jetbrains.annotations.NotNull;

//==========================================================================================

    import java.util.ArrayList;

//==========================================================================================

    @SuppressWarnings( "WeakerAccess" ) class Blocks {

    //======================================================================================

        static ArrayList<Stem> blocks;

    //======================================================================================

        static class Initialization {

        //==================================================================================

            static void Pre( @SuppressWarnings("unused") FMLPreInitializationEvent event ) {
            //------------------------------------------------------------------------------

                blocks = new ArrayList<>();

            //------------------------------------------------------------------------------
            }

        //==================================================================================

        }

        static class Registration {

        //==================================================================================

            static void Blocks( Register<Block> event ) {
            //------------------------------------------------------------------------------

                for( Stem block : blocks ) { event.getRegistry().register( block ); }

            //------------------------------------------------------------------------------
            }

        //==================================================================================

            static void Items( Register<Item> event ) {
            //------------------------------------------------------------------------------

                Generation.Compressed();

            //------------------------------------------------------------------------------

                for(Stem block : blocks) { event.getRegistry().register(block.getAsItem());}

            //------------------------------------------------------------------------------
            }

        //==================================================================================

            static void Recipes( Register<IRecipe> event ) {
            //------------------------------------------------------------------------------

                ArrayList<IRecipe> recipes = new ArrayList<>();

                if(null == Resources.mod)recipes.addAll(Recipes.Generation.Compression()  );
                if(null == Resources.mod)recipes.addAll(Recipes.Generation.Decompression());

            //------------------------------------------------------------------------------

                for( IRecipe recipe : recipes ) { event.getRegistry().register( recipe ); }

            //------------------------------------------------------------------------------
            }

        //==================================================================================

            static void Models( @SuppressWarnings("unused") ModelRegistryEvent event ) {
            //------------------------------------------------------------------------------

                for( Stem block : blocks ) { Base.proxy.registerBlockRenderer( block ); }

            //------------------------------------------------------------------------------
            }

        //==================================================================================

        }

        static class Generation {

        //==================================================================================

            static Compressed[][] blocks = null;

        //==================================================================================

            @NotNull static NonNullList<ItemStack> getVariants( String ID ) {
            //------------------------------------------------------------------------------
                NonNullList<ItemStack> items = NonNullList.create();
            //------------------------------------------------------------------------------

                String[] id   = ID.split( ":" );
                Integer  meta = id.length > 2 ? Integer.parseInt( id[2] ) : -1;

                ResourceLocation location = new ResourceLocation( id[0] , id[1] );

            //------------------------------------------------------------------------------

                Item  item  = ForgeRegistries.ITEMS .getValue( location );
                Block block = ForgeRegistries.BLOCKS.getValue( location );

                if( null == item && null == block ) return items;

            //------------------------------------------------------------------------------

                if( null == item ) item = Item.getItemFromBlock( block );
                if( net.minecraft.init.Items.AIR == item ) return items;

            //------------------------------------------------------------------------------

                if( !item.getHasSubtypes() ) items.add( new ItemStack( item , 1 , 0 ) );
                if( !item.getHasSubtypes() ) return items;

            //------------------------------------------------------------------------------

                if( meta >= 0 ) items.add( new ItemStack( item , 1 , meta ) );
                if( meta >= 0 ) return items;

            //------------------------------------------------------------------------------

                CreativeTabs tab = item.getCreativeTab();

                if( null == tab ) tab = CreativeTabs.CREATIVE_TAB_ARRAY[0];

                item.getSubItems( tab , items );

            //------------------------------------------------------------------------------
                return items;
            //------------------------------------------------------------------------------
            }

            @NotNull static Compressed getCompressed( int stage , ItemStack itemStack ) {
            //------------------------------------------------------------------------------

                Item  item  = itemStack.getItem();
                Block block = Block.getBlockFromItem( item );

            //------------------------------------------------------------------------------

                Material material;

                try { material = block.getBlockState().getBaseState().getMaterial(); }
                catch ( NullPointerException e ) { material = Material.LEAVES; }

            //------------------------------------------------------------------------------

                return new Compressed( stage , material , itemStack );

            //------------------------------------------------------------------------------
            }

        //==================================================================================

            static void Compressed() {
            //------------------------------------------------------------------------------

                NonNullList<ItemStack> entries = NonNullList.create();

                for( String ID : Configurations.getIDs() ) entries.addAll(getVariants(ID));

            //------------------------------------------------------------------------------

                int L1 = entries.size();
                int L2 = Configurations.getDepth() + 1;

                blocks = new Compressed[L1][L2];

            //------------------------------------------------------------------------------
                for( int y = 0; y < L1; y++ ) { for( int x = 1; x < L2; x++ ) {
            //------------------------------------------------------------------------------

                    blocks[y][x] = getCompressed( x , entries.get( y ) );

                    Blocks.blocks.add( blocks[y][x] );

                    ForgeRegistries.BLOCKS.register( blocks[y][x] );

            //------------------------------------------------------------------------------
                } }
            //------------------------------------------------------------------------------
            }

        //==================================================================================

        }

    //======================================================================================

        static class Stem extends Block {

        //==================================================================================

            String    name;
            ItemBlock item;

        //==================================================================================

            ItemBlock getAsItem() { return this.item; }

        //==================================================================================

            Stem( int level , Material material , ItemStack item ) {
            //------------------------------------------------------------------------------
                super( material );
            //------------------------------------------------------------------------------

                ResourceLocation loc = item.getItem().getRegistryName();

                if( null == loc ) return;

                this.name = loc.getResourceDomain() + '_' + loc.getResourcePath() + '_'
                          + item.getMetadata() + '_' + level + 'x';

            //------------------------------------------------------------------------------

                this.item = new ItemBlock( this );

            //------------------------------------------------------------------------------

                this.setUnlocalizedName( this.name );
                this.setRegistryName( this.name );

            //------------------------------------------------------------------------------

                this.item.setUnlocalizedName( this.name );
                this.item.setRegistryName( this.name );

            //------------------------------------------------------------------------------
            }

        //==================================================================================

        }

        static class Compressed extends Stem {

        //==================================================================================

            ItemStack stem = null;

        //==================================================================================

            Compressed( int level , Material material , ItemStack item ) {
            //------------------------------------------------------------------------------
                super( level , material , item );
            //------------------------------------------------------------------------------

                this.stem = item;
                this.setCreativeTab( CreativeTabs.MATERIALS );

            //------------------------------------------------------------------------------

                setHardness( 1.5f * level );
                setResistance( 30f * level * level );

            //------------------------------------------------------------------------------
            }

        //==================================================================================

        }

    //======================================================================================

    }

//==========================================================================================

