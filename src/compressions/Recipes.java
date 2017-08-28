//==================================================================================================

    package compressions;

//==================================================================================================

    import mcp.MethodsReturnNonnullByDefault;
    import net.minecraft.inventory.InventoryCrafting;
    import net.minecraft.item.Item;
    import net.minecraft.item.ItemStack;
    import net.minecraft.item.crafting.IRecipe;
    import net.minecraft.item.crafting.Ingredient;
    import net.minecraft.item.crafting.ShapelessRecipes;
    import net.minecraft.nbt.NBTTagCompound;
    import net.minecraft.util.NonNullList;
    import net.minecraft.util.ResourceLocation;
    import net.minecraft.world.World;
    import net.minecraftforge.event.RegistryEvent.Register;
    import net.minecraftforge.fml.common.Mod;
    import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
    import org.apache.commons.lang3.tuple.ImmutablePair;
    import org.apache.commons.lang3.tuple.Pair;

//==================================================================================================

    import javax.annotation.ParametersAreNonnullByDefault;

//==================================================================================================
    @Mod.EventBusSubscriber @MethodsReturnNonnullByDefault @ParametersAreNonnullByDefault
    @SuppressWarnings( { "WeakerAccess" , "CanBeFinal" } )
//==================================================================================================

    public class Recipes {

    //==============================================================================================
    // Structure
    //==============================================================================================

        public static class Compressed extends ShapelessRecipes {

        //==========================================================================================
        // Setup
        //==========================================================================================

            public Compressed () {
            //--------------------------------------------------------------------------------------
                super( "Compression" , new ItemStack( Items.compressed ) , NonNullList.create() );
            //--------------------------------------------------------------------------------------

                this.setRegistryName( Base.modId , "compressed" );

            //--------------------------------------------------------------------------------------
            }

            public Compressed ( String group , ItemStack out , NonNullList<Ingredient> in ) {
            //--------------------------------------------------------------------------------------
                super( group , out , in );
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================
        // Usage
        //==========================================================================================

            public Pair<ItemStack , Integer> GridToStack( InventoryCrafting grid ) {
            //--------------------------------------------------------------------------------------
                Pair<ItemStack , Integer> empty = new ImmutablePair<>( ItemStack.EMPTY , 0 );
            //--------------------------------------------------------------------------------------

                Integer h = grid.getHeight();
                Integer w = grid.getWidth();

                Integer size  = 0;
                Integer count = 0;

            //--------------------------------------------------------------------------------------

                ItemStack previous = ItemStack.EMPTY;

            //--------------------------------------------------------------------------------------
                for( int row = 0; row < w; row++ ) { for( int col = 0; col < h; col++ ) {
            //--------------------------------------------------------------------------------------

                    ItemStack stack = grid.getStackInRowAndColumn( row , col );

                //----------------------------------------------------------------------------------
                    if( stack.isEmpty() ) continue;
                //----------------------------------------------------------------------------------

                    count += 1;
                    size  += stack.getCount();

                //----------------------------------------------------------------------------------
                    if( previous.isEmpty() ) { previous = stack; continue; }
                //----------------------------------------------------------------------------------

                    String name1 =    stack.getItem().getRegistryName().toString();
                    String name2 = previous.getItem().getRegistryName().toString();

                    if( !name1.equals( name2 ) ) return empty;

                //----------------------------------------------------------------------------------

                    if( stack.getMetadata() != previous.getMetadata() ) return empty;

                //----------------------------------------------------------------------------------
                    if( !stack.hasTagCompound() && !previous.hasTagCompound() ) continue;
                //----------------------------------------------------------------------------------

                    if( !stack.hasTagCompound() &&  previous.hasTagCompound() ) return empty;
                    if(  stack.hasTagCompound() && !previous.hasTagCompound() ) return empty;

                //----------------------------------------------------------------------------------

                    String tag1 =    stack.getTagCompound().toString();
                    String tag2 = previous.getTagCompound().toString();

                    if( !tag1.equals( tag2 ) ) return empty;

            //--------------------------------------------------------------------------------------
                } } if( previous.isEmpty() ) return empty;
            //--------------------------------------------------------------------------------------

                ItemStack all = Items.Compressed.copyStack( previous );

                all.setCount( size );

            //--------------------------------------------------------------------------------------
                return new ImmutablePair<>( all , count );
            //--------------------------------------------------------------------------------------
            }

        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

            @Override public boolean matches( InventoryCrafting grid , World world ) {
            //--------------------------------------------------------------------------------------
                Pair<ItemStack , Integer> pair = GridToStack( grid );
            //--------------------------------------------------------------------------------------

                ItemStack base = pair.getLeft();

            //--------------------------------------------------------------------------------------
                if( base.isEmpty() ) return false;
            //--------------------------------------------------------------------------------------

                Integer count = pair.getRight();

            //--------------------------------------------------------------------------------------
                if( base.getItem() instanceof Items.Compressed ) {
            //--------------------------------------------------------------------------------------

                    Integer width = base.getTagCompound().getInteger( "Width" );

                    if( width == count || 1 == count ) return true;

            //--------------------------------------------------------------------------------------
                } else {
            //--------------------------------------------------------------------------------------

                    base = Items.Compressed.getBaseFromRaw( base );

                //----------------------------------------------------------------------------------
                    if( null == base ) return false;
                //----------------------------------------------------------------------------------

                    Integer width = base.getSubCompound( "Compression" ).getInteger( "Width" );

                    if( count == width ) return true;

            //--------------------------------------------------------------------------------------
                } return false;
            //--------------------------------------------------------------------------------------
            }

        //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

            @Override public ItemStack getCraftingResult( InventoryCrafting grid ) {
            //--------------------------------------------------------------------------------------

                ItemStack stack = ItemStack.EMPTY;

                Integer count = 0;

                Integer h = grid.getHeight();
                Integer w = grid.getWidth();

            //--------------------------------------------------------------------------------------
                for( int row = 0; row < w; row++ ) { for( int col = 0; col < h; col++ ) {
            //--------------------------------------------------------------------------------------

                    ItemStack block = grid.getStackInRowAndColumn( row , col );

                //----------------------------------------------------------------------------------
                    if( block.isEmpty() ) continue;
                //----------------------------------------------------------------------------------

                    stack  = block;
                    count += 1;

            //--------------------------------------------------------------------------------------
                } } if( stack.getItem() instanceof Items.Compressed ) { if( 1 == count ) {
            //--------------------------------------------------------------------------------------

                    Integer height = stack.getTagCompound().getInteger( "Height" );
                    Integer width  = stack.getTagCompound().getInteger( "Width"  );

                //----------------------------------------------------------------------------------

                    ItemStack result = Items.Compressed.getRaw( stack );

                //----------------------------------------------------------------------------------
                    if( null == Items.Compressed.getBaseFromRaw( result ) ) return ItemStack.EMPTY;
                //----------------------------------------------------------------------------------

                    if( 0 != height ) result = Items.Compressed.getFromRaw( result , height - 1 );

                    result.setCount( width );

                //----------------------------------------------------------------------------------

                    return result.copy();

            //--------------------------------------------------------------------------------------
                } else {
            //--------------------------------------------------------------------------------------

                    ItemStack raw  = Items.Compressed.getRaw( stack );
                    ItemStack base = Items.Compressed.getBaseFromRaw( raw );

                //----------------------------------------------------------------------------------

                    Integer max    =  base.getSubCompound( "Compression" ).getInteger( "Height" );
                    Integer height = stack.getTagCompound().getInteger( "Height" );

                    if( height >= max - 1 ) return ItemStack.EMPTY;

                //----------------------------------------------------------------------------------

                    ItemStack result = Items.Compressed.getFromRaw( raw , height + 1 );

                //----------------------------------------------------------------------------------

                    return result.copy();

            //--------------------------------------------------------------------------------------
                } } else {
            //--------------------------------------------------------------------------------------

                    ItemStack result = Items.Compressed.getFromRaw( stack , 0 );

                    return result.copy();

            //--------------------------------------------------------------------------------------
                }
            //--------------------------------------------------------------------------------------
            }

        //==========================================================================================
        // Properties
        //==========================================================================================


        //==========================================================================================

        }

/*
        public static class Compressed3 extends ShapelessRecipes {

            //======================================================================================
            // Structure
            //======================================================================================

            public static Map<String , ArrayList<Compressed3>> connected = new HashMap<>();

            //======================================================================================

            String tags;

            //======================================================================================
            // Unique identification
            //======================================================================================

            @Override public boolean equals( Object object ) {
                //----------------------------------------------------------------------------------
                if( !( object instanceof Compressed ) ) return false;
                //----------------------------------------------------------------------------------

                Compressed other = (Compressed) object;

                //----------------------------------------------------------------------------------

                String regNameThis  = this.getRegistryName().toString();
                String regNameOther = other.getRegistryName().toString();

                if( !regNameThis.equals( regNameOther ) ) return false;

                //----------------------------------------------------------------------------------
                return true;
                //----------------------------------------------------------------------------------
            }

            @Override public int hashCode() {
                //---------------------------------------------------------------------------------

                return this.getRegistryName().toString().hashCode();

                //---------------------------------------------------------------------------------
            }

            //======================================================================================
            // Usage
            //======================================================================================

            public Compressed3(JsonObject content , JsonContext context ) {
                //----------------------------------------------------------------------------------
                super(  CraftingHelper.getRecipe( content , context ).getGroup()        ,
                        CraftingHelper.getRecipe( content , context ).getRecipeOutput() ,
                        CraftingHelper.getRecipe( content , context ).getIngredients()  ); try {
                //----------------------------------------------------------------------------------

                    ItemStack in = getIngredients().get( 0 ).getMatchingStacks()[0];

                //----------------------------------------------------------------------------------

                    tags = in.getItem().getRegistryName().toString() + in.getMetadata();

                //----------------------------------------------------------------------------------
                    if( !connected.containsKey( tags ) ) connected.put( tags , new ArrayList<>());
                //----------------------------------------------------------------------------------

                    connected.get( tags ).add( this );

                //----------------------------------------------------------------------------------
                    for( int i = 0; i < this.getIngredients().size(); i++ ) {
                //----------------------------------------------------------------------------------

                        NBTTagCompound tag = JsonToNBT.getTagFromJson( content.get( "ingredients" )
                                .getAsJsonArray()
                                .get( i )
                                .getAsJsonObject()
                                .get( "nbt" )
                                .toString() );

                        this.getIngredients().get( i ).getMatchingStacks()[0].setTagCompound( tag );

                //----------------------------------------------------------------------------------
                    } } catch( NBTException ex ) { ex.printStackTrace(); } }

            public Compressed3(String group , ItemStack out , NonNullList<Ingredient> in ) {
            //--------------------------------------------------------------------------------------
                super( group , out , in );
            //--------------------------------------------------------------------------------------

                ItemStack in2 = getIngredients().get( 0 ).getMatchingStacks()[0];

            //--------------------------------------------------------------------------------------

                tags = in2.getItem().getRegistryName().toString() + in2.getMetadata();

            //--------------------------------------------------------------------------------------
                if( !connected.containsKey( tags ) ) connected.put( tags , new ArrayList<>());
            //--------------------------------------------------------------------------------------

                connected.get( tags ).add( this );

            //--------------------------------------------------------------------------------------
            }

            //======================================================================================
            // Need this override to fix the NBT tag issues
            //======================================================================================

            @Override public ItemStack getCraftingResult( InventoryCrafting grid ) {
            //--------------------------------------------------------------------------------------

                ItemStack stack = ItemStack.EMPTY;

                Integer count = 0;
                Integer size  = 0;

                Integer h = grid.getHeight();
                Integer w = grid.getWidth();

            //--------------------------------------------------------------------------------------
                for( int row = 0; row < w; row++ ) { for( int col = 0; col < h; col++ ) {
            //--------------------------------------------------------------------------------------

                    ItemStack block = grid.getStackInRowAndColumn( row , col );

                //----------------------------------------------------------------------------------
                    if( block.isEmpty() ) continue;
                //----------------------------------------------------------------------------------

                    stack = block;

                    count += 1;
                    size  += stack.getCount();

            //--------------------------------------------------------------------------------------
                } } if( stack.isEmpty() ) return ItemStack.EMPTY;
            //--------------------------------------------------------------------------------------

                if( 1 == count ) {

                    String  mod   = stack.getItem().getRegistryName().getResourceDomain();
                    String  entry = stack.getItem().getRegistryName().getResourcePath();
                    Integer meta  = stack.getMetadata();

                    NBTTagCompound nbt = stack.hasTagCompound() ? stack.getTagCompound()
                            : new NBTTagCompound();

                    ResourceLocation loc   = new ResourceLocation( mod2 , entry2 );
                    ItemStack        base = new ItemStack( Item.REGISTRY.getObject(loc), 1, meta2 );

                    if( !nbt.hasNoTags() ) base.setTagCompound( nbt2 );

                    Integer width = stackInv.getTagCompound().getInteger( "Width" );
                    base.setCount( width );

                }

            //--------------------------------------------------------------------------------------

                NBTTagCompound data = null;

                Integer width  = 0;
                Integer height = 0;

                ItemStack base = null;

            //--------------------------------------------------------------------------------------
                if( !( stack.getItem() instanceof Items.Compressed ) ) {
            //--------------------------------------------------------------------------------------

                    String  mod   = stack.getItem().getRegistryName().getResourceDomain();
                    String  entry = stack.getItem().getRegistryName().getResourcePath();
                    Integer meta  = stack.getMetadata();

                    NBTTagCompound nbt = stack.hasTagCompound() ? stack.getTagCompound()
                            : new NBTTagCompound();

                //----------------------------------------------------------------------------------
                    List<ItemStack> in = new ArrayList<>( Configurations.entries );
                //----------------------------------------------------------------------------------

                    in.removeIf( s -> {
                        ResourceLocation loc = s.getItem().getRegistryName();
                        return null == loc || !loc.getResourceDomain().equals( mod );
                    } );

                    in.removeIf( s -> {
                        ResourceLocation loc = s.getItem().getRegistryName();
                        return null == loc || !loc.getResourcePath().equals( entry );
                    } );

                    in.removeIf( s -> !meta.equals( s.getMetadata() ) );

                    in.removeIf( s -> {
                        NBTTagCompound tag = s.getTagCompound();
                        return !tag.toString().replace( " ", "" ).toLowerCase().contains(
                                nbt.toString().replace( " ", "" ).toLowerCase() );
                    } );

                //----------------------------------------------------------------------------------

                    base  = in.get( 0 ).copy();
                    width = base.getSubCompound( "Compression" ).getInteger( "Width" );

            //--------------------------------------------------------------------------------------
                } else {
            //--------------------------------------------------------------------------------------

                    String  mod   = stack.getTagCompound().getString ( "Mod"   );
                    String  entry = stack.getTagCompound().getString ( "Entry" );
                    Integer meta  = stack.getTagCompound().getInteger( "Meta"  );

                    NBTTagCompound nbt = stack.getTagCompound().getCompoundTag( "NBT" );

                //----------------------------------------------------------------------------------
                    List<ItemStack> in = new ArrayList<>( Configurations.entries );
                //----------------------------------------------------------------------------------

                    in.removeIf( s -> {
                        ResourceLocation loc = s.getItem().getRegistryName();
                        return null == loc || !loc.getResourceDomain().equals( mod );
                    } );

                    in.removeIf( s -> {
                        ResourceLocation loc = s.getItem().getRegistryName();
                        return null == loc || !loc.getResourcePath().equals( entry );
                    } );

                    in.removeIf( s -> !meta.equals( s.getMetadata() ) );

                    in.removeIf( s -> {
                        NBTTagCompound tag = s.getTagCompound();
                        return null == tag || !tag.toString().replace( " ", "" ).toLowerCase()
                                .contains( nbt.toString().replace( " ", "" ).toLowerCase() );
                    } );

                //----------------------------------------------------------------------------------

                    base   = in.get( 0 ).copy();
                    height = stack.getTagCompound().getInteger( "Height" );
                    width  = stack.getTagCompound().getInteger( "Width"  );

            //--------------------------------------------------------------------------------------
                } try { if( !count.equals( width ) ) {
            //--------------------------------------------------------------------------------------

                    ItemStack output = new ItemStack( base.getItem() , 1 , base.getMetadata() );

                    if( stack.hasTagCompound() )
                        output.setTagCompound( JsonToNBT.getTagFromJson(
                                stack.getTagCompound().toString() ));

                    output.setCount( (int) Math.pow( width , height + 1 ) );

                    return output;

            //--------------------------------------------------------------------------------------
                } else {
            //--------------------------------------------------------------------------------------

                    Integer extraHeight = (int) ( Math.log( size ) / Math.log( width ) );

                //----------------------------------------------------------------------------------
                    if( 0 == extraHeight ) return ItemStack.EMPTY;
                //----------------------------------------------------------------------------------

                    return Items.Compressed.getFromRaw( base , height + extraHeight - 1 );

            //--------------------------------------------------------------------------------------
                } } catch( NBTException ex ) { ex.printStackTrace(); }
            //--------------------------------------------------------------------------------------

                return ItemStack.EMPTY;

            //--------------------------------------------------------------------------------------
            }


/*
            public ItemStack getCraftingResult2( InventoryCrafting inv ) {
            //──────────────────────────────────────────────────────────────────────────────────
                if( 1 == this.getIngredients().size() ) super.getCraftingResult( inv );
            //──────────────────────────────────────────────────────────────────────────────────

                ItemStack stackInv = inv.getStackInSlot( 0 );
                Integer count = 0;

                Integer h = inv.getHeight();
                Integer w = inv.getWidth();


            //--------------------------------------------------------------------------------------
                for( int row = 0; row < w; row++ ) { for( int col = 0; col < h; col++ ) {
            //--------------------------------------------------------------------------------------

                    ItemStack block = inv.getStackInRowAndColumn( row , col );

                //----------------------------------------------------------------------------------
                    if(block.isEmpty() || block.getItem() == net.minecraft.init.Items.AIR) continue;
                //----------------------------------------------------------------------------------

                    count += 1;

            //--------------------------------------------------------------------------------------
                } }
            //--------------------------------------------------------------------------------------

            //----------------------------------------------------------------------------------

                if( stackInv.getItem().equals( net.minecraft.init.Items.AIR ) )
                    for( int i = 1; i < 9; i++ )
                        if( !inv.getStackInSlot(i).getItem().equals(net.minecraft.init.Items.AIR) )
                            stackInv = inv.getStackInSlot( i );

            //----------------------------------------------------------------------------------

                final String  mod   = stackInv.getItem().getRegistryName().getResourceDomain();
                final String  entry = stackInv.getItem().getRegistryName().getResourcePath();
                final Integer meta  = stackInv.getMetadata();

                final NBTTagCompound nbt = stackInv.hasTagCompound() ? stackInv.getTagCompound()
                        : new NBTTagCompound();

            //----------------------------------------------------------------------------------
                List<ItemStack> in = new ArrayList<>( Configurations.entries );
            //----------------------------------------------------------------------------------

                in.removeIf( s -> {
                    ResourceLocation loc = s.getItem().getRegistryName();
                    return null == loc || !loc.getResourceDomain().equals( mod );
                } );

                in.removeIf( s -> {
                    ResourceLocation loc = s.getItem().getRegistryName();
                    return null == loc || !loc.getResourcePath().equals( entry );
                } );

                in.removeIf( s -> !meta.equals( s.getMetadata() ) );

                in.removeIf( s -> {
                    NBTTagCompound tag = s.getTagCompound();
                    return !tag.toString().replace( " ", "" ).toLowerCase().contains(
                            nbt.toString().replace( " ", "" ).toLowerCase() );
                } );

                Integer width  = in.get(0).getTagCompound().getInteger( "Width" );

            //----------------------------------------------------------------------------------

                if( !(stackInv.getItem() instanceof Items.Compressed) ) {

                    ItemStack stack = new ItemStack( Items.compressed , 1 , 0 );

                    stack.setTagCompound( in.get(0).getSubCompound( "Compression" ).copy() );
                    stack.getTagCompound().setInteger( "Height" , 0 );

                    stack.setStackDisplayName( "1x" + width + " " + in.get(0).getDisplayName());

                    return stack;
                } else if( 1 == count ) {

                //----------------------------------------------------------------------------------

                    Integer height = stackInv.getTagCompound().getInteger( "Height" );

                //----------------------------------------------------------------------------------

                    String mod2   = stackInv.getTagCompound().getString( "Mod" );
                    String entry2 = stackInv.getTagCompound().getString( "Entry" );
                    Integer meta2  = stackInv.getTagCompound().getInteger( "Meta" );
                    NBTTagCompound nbt2   = stackInv.getTagCompound().getCompoundTag( "NBT" );

                //----------------------------------------------------------------------------------

                    ResourceLocation loc   = new ResourceLocation( mod2 , entry2 );
                    ItemStack        base = new ItemStack( Item.REGISTRY.getObject(loc), 1, meta2 );

                    if( !nbt.hasNoTags() ) base.setTagCompound( nbt2 );

                    Integer width = stackInv.getTagCompound().getInteger( "Width" );
                    base.setCount( width );

                //----------------------------------------------------------------------------------

                    ItemStack stack = (0 == height) ? base.copy() : stackInv.copy();

                    if( 0 != height ) stack.getTagCompound().setInteger( "Height" , height - 1 );
                    if( 0 != height ) stack.setCount( width );

                    return stack;
                } else if( width == count) {

                }

                //──────────────────────────────────────────────────────────────────────────────────

                mod   = stackInv.getTagCompound().getString( "Mod" );
                entry = stackInv.getTagCompound().getString( "Entry" );
                meta  = stackInv.getTagCompound().getInteger( "Meta" );
                nbt   = stackInv.getTagCompound().getCompoundTag( "NBT" );

            //----------------------------------------------------------------------------------

                ResourceLocation loc   = new ResourceLocation( mod , entry );
                ItemStack        base = new ItemStack( Item.REGISTRY.getObject(loc), 1, meta );

                if( !nbt.hasNoTags() ) base.setTagCompound( nbt );

                Integer width = stackInv.getTagCompound().getInteger( "Width" );
                base.setCount( width );

            //----------------------------------------------------------------------------------

                Integer height = stackInv.getTagCompound().getInteger( "Height" );

                ItemStack stack = (0 == height) ? base.copy() : stackInv.copy();

                if( 0 != height ) stack.getTagCompound().setInteger( "Height" , height - 1 );
                if( 0 != height ) stack.setCount( width );

                return stack;

            //----------------------------------------------------------------------------------

                if( tagInv.equals( tagThis ) ) return super.getCraftingResult( inv );

                //----------------------------------------------------------------------------------
                for( Compressed recipe : connected.get( this.tags ) ) {
                //----------------------------------------------------------------------------------

                    if( recipe.equals( this ) ) continue;

                    //------------------------------------------------------------------------------

                    ItemStack stackAlt = recipe.getIngredients().get(0).getMatchingStacks()[0];
                    String    tagAlt   = stackAlt.getTagCompound().toString();

                    //------------------------------------------------------------------------------

                    if( tagInv.equals( tagAlt ) ) return recipe.getCraftingResult( inv );

                //----------------------------------------------------------------------------------
                }
                //──────────────────────────────────────────────────────────────────────────────────

                return new ItemStack( net.minecraft.init.Items.AIR , 1 , 0 );

                //----------------------------------------------------------------------------------
            }

            //======================================================================================

        }//*/


    //==============================================================================================
    // Controls
    //==============================================================================================

        public static Compressed compressed = new Compressed();

    //==============================================================================================
    // Setup
    //==============================================================================================

        @SubscribeEvent public static void Register( Register<IRecipe> event ) {
        //------------------------------------------------------------------------------------------
            /*
            NonNullList<ItemStack> items = NonNullList.create();
            Items.compressed.getSubItems( Items.compressed.getCreativeTab() , items );

        //------------------------------------------------------------------------------------------
            for( int i = 0; i < items.size(); i++ ) { String id = "compressing_" + i;
        //------------------------------------------------------------------------------------------

                ItemStack curr = items.get( i );
                ItemStack prev = i > 0 ? items.get( i - 1 ) : curr;

            //--------------------------------------------------------------------------------------

                Integer currH = curr.getTagCompound().getInteger( "Height" );
                Integer prevH = prev.getTagCompound().getInteger( "Height" );

            //--------------------------------------------------------------------------------------
                if( currH > prevH ) {
            //--------------------------------------------------------------------------------------

                    Integer  width = curr.getTagCompound().getInteger( "Width" );

                    ItemStack in;
                    ItemStack out;

                //----------------------------------------------------------------------------------

                    in  = prev.copy();
                    out = curr.copy();

                    IRecipe compr = new Compressed( "Compressing" , out ,
                            NonNullList.withSize( width , Ingredient.fromStacks( in ) ) );

                    compr.setRegistryName( new ResourceLocation( Base.modId , id ) );
                    //event.getRegistry().register( compr );

                //----------------------------------------------------------------------------------

                    in  = curr.copy();
                    out = prev.copy();

                    out.setCount( width );

                    IRecipe decom = new Compressed( "Decompressing" , out ,
                            NonNullList.withSize( 1 , Ingredient.fromStacks( in ) ) );

                    decom.setRegistryName( new ResourceLocation( Base.modId , "de" + id ) );
                    //event.getRegistry().register( decom );

            //--------------------------------------------------------------------------------------
                } else {
            //--------------------------------------------------------------------------------------

                    String  mod   = curr.getTagCompound().getString( "Mod" );
                    String  entry = curr.getTagCompound().getString( "Entry" );
                    Integer meta  = curr.getTagCompound().getInteger( "Meta" );

                    NBTTagCompound nbt = curr.getTagCompound().getCompoundTag( "NBT" );

                //----------------------------------------------------------------------------------

                    ResourceLocation loc   = new ResourceLocation( mod , entry );
                    ItemStack        stack = new ItemStack( Item.REGISTRY.getObject(loc), 1, meta );

                    if( !nbt.hasNoTags() ) stack.setTagCompound( nbt );

                //----------------------------------------------------------------------------------

                    Integer width = curr.getTagCompound().getInteger( "Width" );

                    ItemStack in;
                    ItemStack out;

                //----------------------------------------------------------------------------------

                    in  = stack.copy();
                    out = curr.copy();

                    IRecipe compr = new Compressed( "Compressing" , out ,
                            NonNullList.withSize( width , Ingredient.fromStacks( in ) ) );

                    compr.setRegistryName( new ResourceLocation( Base.modId , id ) );
                    //event.getRegistry().register( compr );

                //----------------------------------------------------------------------------------

                    in  = curr.copy();
                    out = stack.copy();

                    out.setCount( width );

                    IRecipe decom = new Compressed( "Decompressing" , out ,
                            NonNullList.withSize( 1 , Ingredient.fromStacks( in ) ) );

                    decom.setRegistryName( new ResourceLocation( Base.modId , "de" + id ) );
                    //event.getRegistry().register( decom );

            //--------------------------------------------------------------------------------------
                }
        //------------------------------------------------------------------------------------------
            }//*/

            event.getRegistry().register( compressed );

        //------------------------------------------------------------------------------------------
        }

    //==============================================================================================

    }

//==================================================================================================
