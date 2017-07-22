//==========================================================================================

    package com.saftno.compressions;

//==========================================================================================

    import net.minecraft.item.Item;
    import net.minecraft.item.ItemStack;
    import net.minecraft.item.crafting.IRecipe;
    import net.minecraft.item.crafting.Ingredient;
    import net.minecraft.item.crafting.ShapedRecipes;
    import net.minecraft.item.crafting.ShapelessRecipes;
    import net.minecraft.util.NonNullList;
    import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

//==========================================================================================

    import java.io.File;
    import java.io.IOException;
    import java.io.OutputStream;
    import java.nio.file.*;
    import java.util.ArrayList;

//==========================================================================================

    public class Recipes {

    //======================================================================================

        static String compressing;
        static String decompressing;

    //======================================================================================

        static class Initialization {

        //==================================================================================

            static void Pre( FMLPreInitializationEvent event ) {
            //------------------------------------------------------------------------------

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
                } );

                compressing = compressing.replace( "'" , "\"" );

            //------------------------------------------------------------------------------

                decompressing = String.join( "\n" , new String[] {
                //--------------------------------------------------------------------------
                    "{ 'type'       : 'minecraft:crafting_shapeless' " ,
                    ", 'group'      : 'decompressing'                " ,
                    ", 'ingredients': [ { 'item': '[CMPR]' } ]       " ,
                    ", 'result'     : { 'item' : '[NONCMPR]'         " ,
                    "	              , 'data' : [VAR]               " ,
                    "	              , 'count': 9 } }               " ,
                //--------------------------------------------------------------------------
                } );

                decompressing = decompressing.replace( "'" , "\"" );

            //------------------------------------------------------------------------------
            }

        //==================================================================================

        }

        static class Generation {

        //==================================================================================

            static ArrayList<IRecipe> Compression() {
            //------------------------------------------------------------------------------

                boolean ready = true;
                File[] mods = new File( Base.root + "/resourcepacks/" ).listFiles();

            //------------------------------------------------------------------------------
                for( File file : mods ) { if( file.getName().contains( "Compressions" ) ) {
            //------------------------------------------------------------------------------

                    ready = false;

            //------------------------------------------------------------------------------
                } }
            //------------------------------------------------------------------------------

                if( ready ) return new ArrayList<>();

            //------------------------------------------------------------------------------

                int L1 = Blocks.Generation.blocks.length;
                int L2 = Configurations.getDepth() + 1;

                ArrayList<IRecipe> recipes = new ArrayList<>();

            //------------------------------------------------------------------------------
                for( int y = 0; y < L1; y++ ) { for( int x = 1; x < L2; x++ ) {
            //------------------------------------------------------------------------------

                    Blocks.Compressed prev  = Blocks.Generation.blocks[y][x - 1];
                    Blocks.Compressed stack = Blocks.Generation.blocks[y][  x  ];

                    ItemStack stem = stack.stem;

                //--------------------------------------------------------------------------

                    NonNullList<Ingredient> grid = NonNullList.create();

                //--------------------------------------------------------------------------
                    for( int i = 0; i < 9; i++ ) {
                //--------------------------------------------------------------------------

                        if( 1 == x ) grid.add( Ingredient.fromStacks( stem ) );
                        if( 1 != x ) grid.add( Ingredient.fromItem( prev.getAsItem() ) );

                //--------------------------------------------------------------------------
                    }
                //--------------------------------------------------------------------------

                    ItemStack result = new ItemStack( stack , 1 , 0 );

                //--------------------------------------------------------------------------

                    String group = "compressing";
                    int w = 3;
                    int h = 3;

                    ShapedRecipes recipe = new ShapedRecipes( group, w, h, grid, result );

                    recipe.setRegistryName(stack.getRegistryName().toString() +"_"+ group);

                    recipes.add( recipe );

            //------------------------------------------------------------------------------
                } }
            //------------------------------------------------------------------------------
                return recipes;
            //------------------------------------------------------------------------------
            }

            static ArrayList<IRecipe> Decompression() {
            //------------------------------------------------------------------------------

                boolean ready = true;
                File[] mods = new File( Base.root + "/resourcepacks/" ).listFiles();

            //------------------------------------------------------------------------------
                for( File file : mods ) { if( file.getName().contains( "Compressions" ) ) {
            //------------------------------------------------------------------------------

                    ready = false;

            //------------------------------------------------------------------------------
                } }
            //------------------------------------------------------------------------------

                if( ready ) return new ArrayList<>();

            //------------------------------------------------------------------------------

                int L1 = Blocks.Generation.blocks.length;
                int L2 = Configurations.getDepth() + 1;

                ArrayList<IRecipe> recipes = new ArrayList<>();

            //------------------------------------------------------------------------------
                for( int y = 0; y < L1; y++ ) { for( int x = 1; x < L2; x++ ) {
            //------------------------------------------------------------------------------

                    Blocks.Compressed prev  = Blocks.Generation.blocks[y][x - 1];
                    Blocks.Compressed stack = Blocks.Generation.blocks[y][  x  ];

                    Item stem = stack.stem.getItem();
                    int  var  = stack.stem.getMetadata();

                //--------------------------------------------------------------------------

                    NonNullList<Ingredient> grid = NonNullList.create();

                    grid.add( Ingredient.fromItem( stack.getAsItem() ) );

                //--------------------------------------------------------------------------

                    ItemStack result = null;

                    if( 1 == x ) result = new ItemStack( stem , 9 ,   var   );
                    if( 1 != x ) result = new ItemStack( prev , 9 , 0 );

                //--------------------------------------------------------------------------

                    String group = "decompressing";

                    ShapelessRecipes recipe = new ShapelessRecipes( group , result , grid );

                    recipe.setRegistryName(stack.getRegistryName().toString() +"_"+ group);

                    recipes.add( recipe );

            //------------------------------------------------------------------------------
                } }
            //------------------------------------------------------------------------------
                return recipes;
            //------------------------------------------------------------------------------
            }

        //==================================================================================

            static void JSON() {
            //------------------------------------------------------------------------------
                if( null == Resources.tmp ) return;
            //------------------------------------------------------------------------------

                FileSystem mod = Resources.mod;
                FileSystem tmp = Resources.tmp;

            //------------------------------------------------------------------------------

                int L1 = Blocks.Generation.blocks.length;
                int L2 = Configurations.getDepth() + 1;

            //------------------------------------------------------------------------------
                for( int y = 0; y < L1; y++ ) { for( int x = 1; x < L2; x++ ) {
            //------------------------------------------------------------------------------

                    Blocks.Compressed prev  = Blocks.Generation.blocks[y][x - 1];
                    Blocks.Compressed stack = Blocks.Generation.blocks[y][  x  ];
                    ItemStack         stem  = stack.stem;

                //--------------------------------------------------------------------------

                    String stemID  = stack.stem.getItem().getRegistryName().toString();
                    String prevID  = null != prev ? prev .getRegistryName().toString() : "";
                    String stackID = stack.getRegistryName().toString();

                    String stemVarID  = String.valueOf( stem.getMetadata() );
                    String stackVarID = String.valueOf( 0 );

                //--------------------------------------------------------------------------

                    String   coJson = compressing;
                    String decoJson = decompressing;

                    if( 1 == x ) coJson   = coJson  .replace("[NONCMPR]", stemID );
                    if( 1 == x ) decoJson = decoJson.replace("[NONCMPR]", stemID );

                    if( 1 != x ) coJson   = coJson  .replace("[NONCMPR]", prevID );
                    if( 1 != x ) decoJson = decoJson.replace("[NONCMPR]", prevID );

                    coJson   = coJson  .replace( "[CMPR]" , stackID );
                    decoJson = decoJson.replace( "[CMPR]" , stackID );

                    if( 1 == x ) coJson   = coJson  .replace("[VAR]", stemVarID );
                    if( 1 == x ) decoJson = decoJson.replace("[VAR]", stemVarID );

                    if( 1 != x ) coJson   = coJson  .replace("[VAR]", stackVarID );
                    if( 1 != x ) decoJson = decoJson.replace("[VAR]", stackVarID );

                //--------------------------------------------------------------------------

                    String base = "/assets/" + Base.modId + "/recipes/";
                    String name = stack.getRegistryName().getResourcePath();

                    String   coJsonName = base + name + "_compressing.json";
                    String decoJsonName = base + name + "_decompressing.json";

                    String[] jsons = new String[] { coJson , decoJson };
                    String[] files = new String[] { coJsonName , decoJsonName };

                //--------------------------------------------------------------------------
                    for( int i = 0; i < files.length; i++ ) {
                //--------------------------------------------------------------------------

                        String data = jsons[i];

                        if( null != mod ) Resources.Write( data , mod.getPath( files[i] ) );
                        if( null != tmp ) Resources.Write( data , tmp.getPath( files[i] ) );

            //------------------------------------------------------------------------------
            } } } }

        //==================================================================================

        }

    //======================================================================================

    }

//==========================================================================================

