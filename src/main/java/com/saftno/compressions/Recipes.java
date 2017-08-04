//==================================================================================

    package com.saftno.compressions;

//==================================================================================

    import com.saftno.compressions.Base.Entries;
    import com.saftno.compressions.Items.Compressed;

//==================================================================================

    import net.minecraft.block.Block;
    import net.minecraft.item.Item;
    import net.minecraft.item.ItemStack;
    import net.minecraft.item.crafting.*;
    import net.minecraft.util.NonNullList;
    import net.minecraft.util.ResourceLocation;
    import net.minecraftforge.event.RegistryEvent;
    import net.minecraftforge.fml.common.Mod;
    import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
    import net.minecraftforge.fml.common.registry.ForgeRegistries;
    import net.minecraftforge.registries.IForgeRegistry;

//==================================================================================

    import java.io.File;
    import java.nio.file.FileSystem;
    import java.util.*;

//==================================================================================
    @SuppressWarnings( { "WeakerAccess" , "unused" } ) @Mod.EventBusSubscriber
//==================================================================================

    public class Recipes {

    //==============================================================================
    // Setup
    //==============================================================================

        public static Entries<IRecipe> recipes;

        public static String compressing;
        public static String decompressing;

    //==============================================================================

        static {
        //--------------------------------------------------------------------------

            recipes = new Base.Entries<>( s -> s.getRegistryName().toString() );

        //--------------------------------------------------------------------------
            compressing = String.join( "\n" , new String[] {
        //--------------------------------------------------------------------------

            "{ 'type'   : 'minecraft:crafting_shaped'      " ,
            ", 'group'  : 'compressing'                    " ,
            ", 'pattern': [ '###'                          " ,
            "             , '###'                          " ,
            "             , '###' ]                        " ,
            ", 'key'    : { '#'    : { 'item': '[NONCMPR]' " ,
            "                        , 'data': [VAR] } }   " ,
            ", 'result' : { 'item' : '[CMPR]'              " ,
            "             , 'data' : 0                     " ,
            "             , 'count': 1 } }                 " ,

        //--------------------------------------------------------------------------
            } ).replace( "'" , "\"" );
        //--------------------------------------------------------------------------

        //--------------------------------------------------------------------------
            decompressing = String.join( "\n" , new String[] {
        //--------------------------------------------------------------------------

            "{ 'type'       : 'minecraft:crafting_shapeless' " ,
            ", 'group'      : 'decompressing'                " ,
            ", 'ingredients': [ { 'item': '[CMPR]' } ]       " ,
            ", 'result'     : { 'item' : '[NONCMPR]'         " ,
            "	              , 'data' : [VAR]               " ,
            "	              , 'count': 9 } }               " ,

        //--------------------------------------------------------------------------
            } ).replace( "'" , "\"" );
        //--------------------------------------------------------------------------
        }

    //==============================================================================

        public static class Generation {

        //==========================================================================

            public static List<IRecipe> Compressing() {
            //----------------------------------------------------------------------
                List<IRecipe> recipes = new ArrayList<>();
            //----------------------------------------------------------------------

                Compressed previous = (Compressed) Items.items.values.get( 0 );

            //----------------------------------------------------------------------
                for( Item entry : Items.items ) {
            //----------------------------------------------------------------------

                    if( !( entry instanceof Compressed ) ) continue;

                //------------------------------------------------------------------

                    Compressed item = (Compressed) entry;

                    Item stem = item.stem.getItem();
                    int  var  = item.stem.getMetadata();

                //------------------------------------------------------------------
                // compressing, decompressing, base extension
                //------------------------------------------------------------------

                    ItemStack bot = null;

                    if( 1 == item.level ) bot = new ItemStack(   stem   , 9 , var );
                    if( 1 != item.level ) bot = new ItemStack( previous , 9 ,  0  );

                    ItemStack top = new ItemStack( item , 1 , 0 );

                //------------------------------------------------------------------

                    NonNullList<Ingredient> g1 = NonNullList.create();
                    NonNullList<Ingredient> g2 = NonNullList.create();

                    bot.setCount( 1 );
                    for( int i = 0; i < 9; i++ ) g1.add(Ingredient.fromStacks(bot));
                    bot.setCount( 9 );

                    g2.add( Ingredient.fromStacks( top ) );

                //------------------------------------------------------------------

                    String n1 = "compressing";
                    String n2 = "decompressing";

                    int w = 3;
                    int h = 3;

                    ShapelessRecipes recipeCo = new ShapelessRecipes( n1, top, g1 );
                    ShapelessRecipes recipeDe = new ShapelessRecipes( n2, bot, g2 );

                //------------------------------------------------------------------

                    ResourceLocation loc = item.getRegistryName();

                    recipeCo.setRegistryName( loc.toString() + "_compressing" );
                    recipeDe.setRegistryName( loc.toString() + "_decompressing" );

                    recipes.add( recipeCo );
                    recipes.add( recipeDe );

                //------------------------------------------------------------------
                    previous = item;
            //----------------------------------------------------------------------
                } return recipes;
            //----------------------------------------------------------------------
            }

            public static List<IRecipe> Crafting() {
            //----------------------------------------------------------------------

                Entries<IRecipe> related = new Entries<>(s->""+s.getRegistryName());

            //----------------------------------------------------------------------
                for( IRecipe recipe : ForgeRegistries.RECIPES.getValues() ) {
            //----------------------------------------------------------------------
                    for( Ingredient input : recipe.getIngredients() ) {
                //------------------------------------------------------------------
                        for( ItemStack stack : input.getMatchingStacks() ) {
                    //--------------------------------------------------------------
                            for( Item entry : Items.items ) {
                        //----------------------------------------------------------

                                if( !( entry instanceof Compressed ) ) continue;

                            //------------------------------------------------------

                                Compressed item = (Compressed) entry;
                                ItemStack  stem = item.stem;

                            //------------------------------------------------------

                                String name1 = Base.UID( stack );
                                String name2 = Base.UID( stem );

                                if( !name1.equals( name2 ) ) continue;

                            //------------------------------------------------------

                                related.Add( recipe );

            //----------------------------------------------------------------------
                } } } }
            //----------------------------------------------------------------------

                Entries<IRecipe> base = new Entries<>(s->""+s.getRegistryName());

            //----------------------------------------------------------------------
                for( IRecipe recipe : related ) {
            //----------------------------------------------------------------------

                    boolean isCompressible = true;

            //----------------------------------------------------------------------
                    for( Ingredient input : recipe.getIngredients() ) {
                //------------------------------------------------------------------
                        for( ItemStack stack : input.getMatchingStacks() ) {
                    //--------------------------------------------------------------

                            boolean isCompressed = false;

                        //--------------------------------------------------------------
                            for( Item entry : Items.items ) {
                        //----------------------------------------------------------

                                if( !( entry instanceof Compressed ) ) continue;

                            //------------------------------------------------------

                                Compressed item = (Compressed) entry;
                                ItemStack  stem = item.stem;

                            //------------------------------------------------------

                                String name1 = Base.UID( stack );
                                String name2 = Base.UID( stem );

                                if( name1.equals( name2 ) ) isCompressed = true;

                        //----------------------------------------------------------
                            }
                        //----------------------------------------------------------

                            isCompressible = isCompressible && isCompressed;

                //------------------------------------------------------------------
                    } }
                //------------------------------------------------------------------

                    if( isCompressible ) base.Add( recipe );

            //----------------------------------------------------------------------
                }
            //----------------------------------------------------------------------

                List<IRecipe> recipes = new ArrayList<>();

            //----------------------------------------------------------------------
                for( int i = 1; i <= Configurations.getDepth(); i++ ) {
            //----------------------------------------------------------------------
                    for( IRecipe recipe : base ) {
                //------------------------------------------------------------------

                        ItemStack result = recipe.getRecipeOutput();
                        NonNullList<Ingredient> ingredients = NonNullList.create();

                    //--------------------------------------------------------------
                        for( Ingredient input : recipe.getIngredients() ) {
                    //--------------------------------------------------------------

                            Integer     pos    = 0;
                            Integer     size   = input.getMatchingStacks().length;
                            ItemStack[] stacks = new ItemStack[size];

                        //----------------------------------------------------------
                            for( ItemStack stack : input.getMatchingStacks() ) {
                        //----------------------------------------------------------
                                for( Item entry : Items.items ) {
                            //------------------------------------------------------

                                    if( !( entry instanceof Compressed ) ) continue;

                                //--------------------------------------------------

                                    Compressed item = (Compressed) entry;
                                    ItemStack  stem = item.stem;

                                    if( i != item.level ) continue;

                                //--------------------------------------------------

                                    ItemStack itSt = new ItemStack( item , 1 , 0 );

                                //--------------------------------------------------

                                    String name1 = Base.UID( result );
                                    String name2 = Base.UID( stem );

                                    if( name1.equals( name2 ) ) result = itSt;

                                //--------------------------------------------------

                                    name1 = Base.UID( stack );
                                    name2 = Base.UID( stem );

                                    if( name1.equals( name2 )) stacks[pos++] = itSt;

                        //----------------------------------------------------------
                            } }
                        //----------------------------------------------------------

                            ingredients.add( Ingredient.fromStacks( stacks ) );

                    //--------------------------------------------------------------
                        }
                    //--------------------------------------------------------------

                        if( result == recipe.getRecipeOutput() ) continue;

                    //--------------------------------------------------------------

                        String n = recipe.getGroup();

                        NonNullList<Ingredient> in = ingredients;
                        ItemStack o = result;

                    //--------------------------------------------------------------
                        if( recipe instanceof ShapedRecipes ) {
                    //--------------------------------------------------------------

                            ShapedRecipes recShaped = ( ShapedRecipes ) recipe;

                            int w = recShaped.getWidth();
                            int h = recShaped.getHeight();

                        //----------------------------------------------------------

                            ShapedRecipes newRecipe = new ShapedRecipes(n,w,h,in,o);

                        //----------------------------------------------------------

                            ResourceLocation loc  = recipe.getRegistryName();
                            String           name = loc.toString().replace(':','_');

                            newRecipe.setRegistryName( name + "_" + i );

                        //----------------------------------------------------------

                            recipes.add( newRecipe );

                    //--------------------------------------------------------------
                        } if( recipe instanceof ShapelessRecipes ) {
                    //--------------------------------------------------------------

                            ShapelessRecipes newR = new ShapelessRecipes(n,o,in);

                        //----------------------------------------------------------

                            ResourceLocation loc  = recipe.getRegistryName();
                            String           name = loc.toString().replace(':','_');

                            newR.setRegistryName( "compressions" , name + "_" + i );

                        //----------------------------------------------------------

                            recipes.add( newR );

            //----------------------------------------------------------------------
                } } } return recipes;
            //----------------------------------------------------------------------
            }

            public static List<IRecipe> Smelting() {
            //----------------------------------------------------------------------
                for( Block entry : Blocks.blocks ) {
            //----------------------------------------------------------------------

                    if( !( entry instanceof Blocks.Compressed ) ) continue;

                //------------------------------------------------------------------

                    Blocks.Compressed block = (Blocks.Compressed) entry;
                    ItemStack stem = block.stem;

                    FurnaceRecipes furnace = FurnaceRecipes.instance();

                //------------------------------------------------------------------

                    ItemStack      key  = null;
                    Set<ItemStack> keys = furnace.getSmeltingList().keySet();

                //------------------------------------------------------------------
                    for( ItemStack stack : keys ) {
                //------------------------------------------------------------------

                        String eName = Base.UID( stack );
                        String sName = Base.UID( stem );

                    //--------------------------------------------------------------

                        if( eName.equals( sName ) ) key = stack;
                        if( eName.equals( sName ) ) break;

                //------------------------------------------------------------------
                    } if( null == key ) continue;
                //------------------------------------------------------------------

                    ItemStack result = furnace.getSmeltingResult( key );
                    Float experience = furnace.getSmeltingExperience( key );

                //------------------------------------------------------------------

                    ArrayList<Blocks.Compressed> results = new ArrayList<>();

                //------------------------------------------------------------------
                    for( Block subEntry : Blocks.blocks ) {
                //------------------------------------------------------------------

                        if( !( subEntry instanceof Blocks.Compressed ) ) continue;

                    //--------------------------------------------------------------

                        Blocks.Compressed subBlock = (Blocks.Compressed) subEntry;
                        ItemStack  subStem = subBlock.stem;

                        String sName = Base.UID( subStem );
                        String rName = Base.UID( result );

                    //--------------------------------------------------------------

                        if( sName.equals( rName ) ) results.add( subBlock );

                //------------------------------------------------------------------
                    }
                //------------------------------------------------------------------

                    results.removeIf( s -> !s.level.equals( block.level ) );

                    if( 1 != results.size() ) continue;

                //------------------------------------------------------------------

                    ItemStack input  = new ItemStack(       block      , 1 , 0 );
                    ItemStack output = new ItemStack( results.get( 0 ) , 1 , 0 );

                    Float extra = (float) Math.pow( 9 , block.level );

                    FurnaceRecipes.instance().addSmeltingRecipe( input , output ,
                        experience * extra );

            //----------------------------------------------------------------------
                } return new ArrayList<>();
            //----------------------------------------------------------------------
            }

        //==========================================================================

            public static void JSON() {
            //----------------------------------------------------------------------
                if( null == Resources.tmp ) return;
            //----------------------------------------------------------------------

                FileSystem mod = Resources.mod;
                FileSystem tmp = Resources.tmp;

            //----------------------------------------------------------------------

                Block prev = Blocks.blocks.values.get( 0 );

            //----------------------------------------------------------------------
                for( Block block : Blocks.blocks ) {
            //----------------------------------------------------------------------
                    if( !( block instanceof Blocks.Compressed ) ) continue;
                //------------------------------------------------------------------

                    Blocks.Compressed blok = (Blocks.Compressed) block;

                    Boolean   start = ( 1 == blok.level );
                    ItemStack stem  = blok.stem;

                //------------------------------------------------------------------

                    ResourceLocation stemLoc = stem.getItem().getRegistryName();
                    ResourceLocation prevLoc = prev.getRegistryName();
                    ResourceLocation blokLoc = blok.getRegistryName();

                    if( null == blokLoc ) continue;
                    if( null == stemLoc ) continue;
                    if( null == prevLoc ) continue;

                //------------------------------------------------------------------

                    String stemID = stemLoc.toString();
                    String prevID = prevLoc.toString();
                    String blokID = blokLoc.toString();

                    String stemVarID = String.valueOf( stem.getMetadata() );
                    String blokVarID = String.valueOf( 0 );

                    String   coJson = compressing;
                    String decoJson = decompressing;

                //------------------------------------------------------------------

                    if(  start ) coJson   = coJson  .replace( "[NONCMPR]", stemID );
                    if(  start ) decoJson = decoJson.replace( "[NONCMPR]", stemID );

                    if( !start ) coJson   = coJson  .replace( "[NONCMPR]", prevID );
                    if( !start ) decoJson = decoJson.replace( "[NONCMPR]", prevID );

                    coJson   = coJson  .replace( "[CMPR]" , blokID );
                    decoJson = decoJson.replace( "[CMPR]" , blokID );

                    if(  start ) coJson   = coJson  .replace( "[VAR]", stemVarID );
                    if(  start ) decoJson = decoJson.replace( "[VAR]", stemVarID );

                    if( !start ) coJson   = coJson  .replace( "[VAR]", blokVarID );
                    if( !start ) decoJson = decoJson.replace( "[VAR]", blokVarID );

                //------------------------------------------------------------------

                    String base = "/assets/" + Base.modId + "/recipes/";
                    String name = blok.getRegistryName().getResourcePath();

                    String   coJsonName = base + name + "_compressing.json";
                    String decoJsonName = base + name + "_decompressing.json";

                    String[] jsons = new String[] { coJson , decoJson };
                    String[] files = new String[] { coJsonName , decoJsonName };

                //------------------------------------------------------------------
                    for( int i = 0; i < files.length; i++ ) {
                //------------------------------------------------------------------

                        String data = jsons[i];

                        if(null != mod) Resources.Write(data,mod.getPath(files[i]));
                        if(null != tmp) Resources.Write(data,tmp.getPath(files[i]));

            //----------------------------------------------------------------------
            } prev = blok; } }

        //==========================================================================

        }

        public static void Generate() {
        //--------------------------------------------------------------------------

            for( IRecipe recipe : Generation.Compressing() ) recipes.Add( recipe );
            for( IRecipe recipe : Generation.Crafting()    ) recipes.Add( recipe );
            for( IRecipe recipe : Generation.Smelting()    ) recipes.Add( recipe );

        //--------------------------------------------------------------------------
        }

    //==============================================================================
        @SubscribeEvent
    //==============================================================================

        public static void Register( RegistryEvent.Register<IRecipe> event ) {
        //--------------------------------------------------------------------------
            if( Items.items.isEmpty() ) Items.Register( event );
        //--------------------------------------------------------------------------

            Generate();

        //--------------------------------------------------------------------------
            IForgeRegistry<IRecipe> reg = ForgeRegistries.RECIPES;
        //--------------------------------------------------------------------------

            for( IRecipe r : recipes ) if( !reg.containsValue(r) ) reg.register(r);

        //--------------------------------------------------------------------------
        }

    //==============================================================================
    // Structure
    //==============================================================================

        public static List<IRecipe> getRelated( List<ItemStack> items ) {
        //--------------------------------------------------------------------------
            List<IRecipe> recipes = new ArrayList<>();
        //--------------------------------------------------------------------------

            Set<String> IDs = new HashSet<>();

        //--------------------------------------------------------------------------
            for( IRecipe recipe : ForgeRegistries.RECIPES.getValues() ) {
        //--------------------------------------------------------------------------
                for( Ingredient input : recipe.getIngredients() ) {
            //----------------------------------------------------------------------
                    for( ItemStack stack : input.getMatchingStacks() ) {
                //------------------------------------------------------------------
                        for( ItemStack item : items ) {
                    //--------------------------------------------------------------

                            String name1 = Blocks.Stem.getItemFullName( stack );
                            String name2 = Blocks.Stem.getItemFullName( item );

                            if( !name1.equals( name2 ) ) continue;

                        //--------------------------------------------------------------

                            String ID = recipe.getRegistryName().toString();

                            if(  IDs.contains( ID ) ) continue;
                            if( !IDs.contains( ID ) ) IDs.add( ID );

                        //--------------------------------------------------------------

                            recipes.add( recipe );

        //--------------------------------------------------------------------------
            } } } } return recipes;
        //--------------------------------------------------------------------------
        }

    //==============================================================================
/*
        public static class Generation {

        //==========================================================================

            public static ArrayList<IRecipe> Compression() {
            //----------------------------------------------------------------------

                ArrayList<IRecipe> recipes = new ArrayList<>();

            //----------------------------------------------------------------------
                if( Blocks.compressions.isEmpty() ) return recipes;
            //----------------------------------------------------------------------

                File[] files = new File( Base.root +"/resourcepacks/" ).listFiles();

            //----------------------------------------------------------------------
                if( null == files ) return recipes;
            //----------------------------------------------------------------------

                ArrayList<File> mods = new ArrayList<>( Arrays.asList( files ) );
                mods.removeIf( mod -> !mod.getName().contains( "Compressions" ) );

            //----------------------------------------------------------------------
                if( mods.isEmpty() ) return recipes;
            //----------------------------------------------------------------------

                Blocks.Compressed prev = Blocks.compressions.get( 0 );

            //----------------------------------------------------------------------
                for( Blocks.Compressed block : Blocks.compressions ) {
            //----------------------------------------------------------------------

                    NonNullList<Ingredient> grid = NonNullList.create();

                    Ingredient iBase = Ingredient.fromStacks( block.stem );
                    Ingredient iPrev = Ingredient.fromItem( prev.getAsItem() );

                //------------------------------------------------------------------
                    for( int i = 0; i < 9; i++ ) {
                //------------------------------------------------------------------

                        grid.add( 1 == block.level ? iBase : iPrev );

                //------------------------------------------------------------------
                    }
                //------------------------------------------------------------------

                    ItemStack res = new ItemStack( block , 1 , 0 );

                //------------------------------------------------------------------

                    String gr = "compressing";
                    int w = 3;
                    int h = 3;

                    ShapedRecipes recipe = new ShapedRecipes(gr,w,h,grid,res);
                    ResourceLocation loc = block.getRegistryName();

                //------------------------------------------------------------------
                    if( null == loc ) continue;
                //------------------------------------------------------------------

                    recipe.setRegistryName( loc.toString() + "_" + gr );

                    recipes.add( recipe );

            //----------------------------------------------------------------------
                prev = block; } return recipes;
            //----------------------------------------------------------------------
            }

            public static ArrayList<IRecipe> Decompression() {
            //----------------------------------------------------------------------

                ArrayList<IRecipe> recipes = new ArrayList<>();

            //----------------------------------------------------------------------
                if( Blocks.compressions.isEmpty() ) return recipes;
            //----------------------------------------------------------------------

                File[] files = new File( Base.root +"/resourcepacks/" ).listFiles();

            //----------------------------------------------------------------------
                if( null == files ) return recipes;
            //----------------------------------------------------------------------

                ArrayList<File> mods = new ArrayList<>( Arrays.asList( files ) );
                mods.removeIf( mod -> !mod.getName().contains( "Compressions" ) );

            //----------------------------------------------------------------------
                if( mods.isEmpty() ) return recipes;
            //----------------------------------------------------------------------

                Blocks.Compressed prev = Blocks.compressions.get( 0 );

            //----------------------------------------------------------------------
                for( Blocks.Compressed block : Blocks.compressions ) {
            //----------------------------------------------------------------------

                    Item stem = block.stem.getItem();
                    int  var  = block.stem.getMetadata();

                //------------------------------------------------------------------

                    NonNullList<Ingredient> grid = NonNullList.create();

                    grid.add( Ingredient.fromItem( block.getAsItem() ) );

                //------------------------------------------------------------------

                    ItemStack result = null;

                    if( 1 == block.level ) result = new ItemStack( stem , 9 , var );
                    if( 1 != block.level ) result = new ItemStack( prev , 9 ,  0  );

                //------------------------------------------------------------------

                    String gr = "decompressing";

                    ShapelessRecipes recipe = new ShapelessRecipes(gr, result, grid);
                    ResourceLocation loc = block.getRegistryName();

                //------------------------------------------------------------------
                    if ( null == loc ) continue;
                //------------------------------------------------------------------

                    recipe.setRegistryName(loc.toString() + "_" + gr);

                    recipes.add( recipe );

            //----------------------------------------------------------------------
                prev = block; } return recipes;
            //----------------------------------------------------------------------
            }

        //==========================================================================

            public static ArrayList<Furnace> NonOreFurnace() {
            //----------------------------------------------------------------------

                ArrayList<Furnace> recipes = new ArrayList<>();

            //----------------------------------------------------------------------
                if( Blocks.compressions.isEmpty() ) return recipes;
            //----------------------------------------------------------------------
                for( Blocks.Compressed block : Blocks.compressions ) {
            //----------------------------------------------------------------------

                    FurnaceRecipes furnace = FurnaceRecipes.instance();

                //------------------------------------------------------------------

                    ItemStack      key  = null;
                    Set<ItemStack> keys = furnace.getSmeltingList().keySet();

                //------------------------------------------------------------------
                    for( ItemStack entry : keys ) {
                //------------------------------------------------------------------

                        String eName = Blocks.Stem.getItemFullName( entry );
                        String sName = Blocks.Stem.getItemFullName( block.stem );

                    //--------------------------------------------------------------

                        if( eName.equals( sName ) ) key = entry;
                        if( eName.equals( sName ) ) break;

                //------------------------------------------------------------------
                    } if( null == key ) continue;
                //------------------------------------------------------------------

                    ItemStack result = furnace.getSmeltingResult( key );
                    Float experience = furnace.getSmeltingExperience( key );

                //------------------------------------------------------------------

                    ArrayList<Blocks.Compressed> results = new ArrayList<>();

                //------------------------------------------------------------------
                    for( Blocks.Compressed entry : Blocks.compressions ) {
                //------------------------------------------------------------------

                        String sName = Blocks.Stem.getItemFullName( entry.stem );
                        String rName = Blocks.Stem.getItemFullName( result );

                    //--------------------------------------------------------------

                        if( sName.equals( rName ) ) results.add( entry );

                //------------------------------------------------------------------
                    }
                //------------------------------------------------------------------

                    results.removeIf( s -> !s.level.equals( block.level ) );

                    if( 1 != results.size() ) continue;

                //------------------------------------------------------------------

                    ItemStack input  = new ItemStack(       block      , 1 , 0 );
                    ItemStack output = new ItemStack( results.get( 0 ) , 1 , 0 );

                    Float extra = (float) Math.pow( 9 , block.level );

                    recipes.add( new Furnace( input, output, experience * extra ) );

            //----------------------------------------------------------------------
                } return recipes;
            //----------------------------------------------------------------------
            }

        //==========================================================================

            public static void JSON() {
            //----------------------------------------------------------------------
                if( null == Resources.tmp ) return;
            //----------------------------------------------------------------------

                FileSystem mod = Resources.mod;
                FileSystem tmp = Resources.tmp;

            //----------------------------------------------------------------------

                Block prev = Blocks.blocks.values.get( 0 );

            //----------------------------------------------------------------------
                for( Block block : Blocks.blocks ) {
            //----------------------------------------------------------------------
                    if( !( block instanceof Blocks.Compressed ) ) continue;
                //----------------------------------------------------------------------

                    Blocks.Compressed blok = (Blocks.Compressed) block;

                    Boolean   start = ( 1 == blok.level );
                    ItemStack stem  = blok.stem;

                //------------------------------------------------------------------

                    ResourceLocation stemLoc = stem.getItem().getRegistryName();
                    ResourceLocation prevLoc = prev.getRegistryName();
                    ResourceLocation blokLoc = blok.getRegistryName();

                    if( null == blokLoc ) continue;
                    if( null == stemLoc ) continue;
                    if( null == prevLoc ) continue;

                //------------------------------------------------------------------

                    String stemID = stemLoc.toString();
                    String prevID = prevLoc.toString();
                    String blokID = blokLoc.toString();

                    String stemVarID = String.valueOf( stem.getMetadata() );
                    String blokVarID = String.valueOf( 0 );

                    String   coJson = compressing;
                    String decoJson = decompressing;

                //------------------------------------------------------------------

                    if(  start ) coJson   = coJson  .replace( "[NONCMPR]", stemID );
                    if(  start ) decoJson = decoJson.replace( "[NONCMPR]", stemID );

                    if( !start ) coJson   = coJson  .replace( "[NONCMPR]", prevID );
                    if( !start ) decoJson = decoJson.replace( "[NONCMPR]", prevID );

                    coJson   = coJson  .replace( "[CMPR]" , blokID );
                    decoJson = decoJson.replace( "[CMPR]" , blokID );

                    if(  start ) coJson   = coJson  .replace( "[VAR]", stemVarID );
                    if(  start ) decoJson = decoJson.replace( "[VAR]", stemVarID );

                    if( !start ) coJson   = coJson  .replace( "[VAR]", blokVarID );
                    if( !start ) decoJson = decoJson.replace( "[VAR]", blokVarID );

                //------------------------------------------------------------------

                    String base = "/assets/" + Base.modId + "/recipes/";
                    String name = blok.getRegistryName().getResourcePath();

                    String   coJsonName = base + name + "_compressing.json";
                    String decoJsonName = base + name + "_decompressing.json";

                    String[] jsons = new String[] { coJson , decoJson };
                    String[] files = new String[] { coJsonName , decoJsonName };

                //------------------------------------------------------------------
                    for( int i = 0; i < files.length; i++ ) {
                //------------------------------------------------------------------

                        String data = jsons[i];

                        if(null != mod) Resources.Write(data,mod.getPath(files[i]));
                        if(null != tmp) Resources.Write(data,tmp.getPath(files[i]));

            //----------------------------------------------------------------------
                } prev = blok; } }

        //==========================================================================


        //==========================================================================

        }//*/

    //==============================================================================

        public static class Furnace {

        //==========================================================================

            public ItemStack input;
            public ItemStack output;
            public Float     experience;

        //==========================================================================

            Furnace( ItemStack input , ItemStack output , Float experience ) {
            //----------------------------------------------------------------------

                this.input = input;
                this.output = output;
                this.experience = experience;

            //----------------------------------------------------------------------
            }

        //==========================================================================

        }

    //==============================================================================

    }

//==================================================================================

