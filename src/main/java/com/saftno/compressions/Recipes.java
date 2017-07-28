//==================================================================================

    package com.saftno.compressions;

//==================================================================================

    import net.minecraft.item.Item;
    import net.minecraft.item.ItemStack;
    import net.minecraft.item.crafting.*;
    import net.minecraft.util.NonNullList;
    import net.minecraft.util.ResourceLocation;
    import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

//==================================================================================

    import java.io.File;
    import java.nio.file.FileSystem;
    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.Set;

//==================================================================================
    @SuppressWarnings( { "WeakerAccess" , "unused" } )
//==================================================================================

    public class Recipes {

    //==============================================================================

        public static String compressing;
        public static String decompressing;

    //==============================================================================

        public static class Initialization {

        //==========================================================================

            public static void Pre( FMLPreInitializationEvent event ) {
            //----------------------------------------------------------------------

                compressing = String.join( "\n" , new String[] {
                //------------------------------------------------------------------
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
                //------------------------------------------------------------------
                } ).replace( "'" , "\"" );

            //----------------------------------------------------------------------

                decompressing = String.join( "\n" , new String[] {
                //------------------------------------------------------------------
                    "{ 'type'       : 'minecraft:crafting_shapeless' " ,
                    ", 'group'      : 'decompressing'                " ,
                    ", 'ingredients': [ { 'item': '[CMPR]' } ]       " ,
                    ", 'result'     : { 'item' : '[NONCMPR]'         " ,
                    "	              , 'data' : [VAR]               " ,
                    "	              , 'count': 9 } }               " ,
                //------------------------------------------------------------------
                } ).replace( "'" , "\"" );

            //----------------------------------------------------------------------
            }

        //==========================================================================

        }

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

                //------------------------------------------------------------------

                    prev = block;

            //----------------------------------------------------------------------
                } return recipes;
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
                } return recipes;
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
                    // compatibility;

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

                    results.removeIf( s -> s.level.equals( block.level ) );

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

                Blocks.Compressed prev = Blocks.compressions.get( 0 );

            //----------------------------------------------------------------------
                for( Blocks.Compressed blok : Blocks.compressions ) {
            //----------------------------------------------------------------------

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
                } } }

        //==========================================================================

        }

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

