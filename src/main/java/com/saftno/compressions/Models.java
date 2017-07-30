//==================================================================================

    package com.saftno.compressions;

//==================================================================================

    import net.minecraft.block.Block;
    import net.minecraft.util.ResourceLocation;
    import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

//==================================================================================

    import java.nio.file.FileSystem;
    import java.nio.file.Files;

//==================================================================================
    @SuppressWarnings( { "WeakerAccess" , "unused" } )
//==================================================================================

    public class Models {

    //==============================================================================

        public static String cube;
        public static String column;

        public static String base;
        public static String model;

    //==============================================================================

        public static class Initialization {

        //==========================================================================

            public static void Pre( FMLPreInitializationEvent event ) {
            //----------------------------------------------------------------------

                cube = String.join( "\n" , new String[] {
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            "{ 'forge_marker' : 1                                                " ,
            ", 'defaults' : { 'model'     : 'cube_all'                           " ,
            "               , 'textures'  : { 'all' : '[MODID]:blocks/[TEX]' } } " ,
            ", 'variants' : { 'normal'    : [ { } ]                              " ,
            "               , 'inventory' : [ { } ]                              " ,
            "               , 'gui      ' : [ { } ] } }                          " ,
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                } ).replace( "'" , "\"" ).replace( "[MODID]" , Base.modId );

            //----------------------------------------------------------------------

                column = String.join( "\n" , new String[] {
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            "{ 'forge_marker' : 1                                                " ,
            ", 'defaults' : { 'model'     : 'cube_column'                        " ,
            "               , 'textures'  : { 'end' : '[MODID]:blocks/[TEX]'     " ,
            "                               , 'side': '[MODID]:blocks/[TEX]' } } " ,
            ", 'variants' : { 'normal'    : [ { } ]                              " ,
            "               , 'inventory' : [ { } ]                              " ,
            "               , 'gui      ' : [ { } ] } }                          " ,
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                } ).replace( "'" , "\"" ).replace( "[MODID]" , Base.modId );

            //----------------------------------------------------------------------

                base = String.join( "\n" , new String[] {
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            "{ 'parent'  : 'block/block'" ,
            ", 'elements': [ { 'from': [  0 ,  0 ,  0 ]" ,
            "                , 'to'  : [ 16 , 16 , 16 ]" ,
            "                , 'faces': { 'down' : { 'texture' : '#tex'          " ,
            "                                      , 'cullface': 'down' }        " ,
            "                           , 'up'   : { 'texture' : '#tex'          " ,
            "                                      , 'cullface': 'up'   }        " ,
            "                           , 'north': { 'texture' : '#tex'          " ,
            "                                      , 'cullface': 'north'}        " ,
            "                           , 'south': { 'texture' : '#tex'          " ,
            "                                      , 'cullface': 'south'}        " ,
            "                           , 'east' : { 'texture' : '#tex'          " ,
            "                                      , 'cullface': 'east' }        " ,
            "                           , 'west' : { 'texture' : '#tex'          " ,
            "                                      , 'cullface': 'west' } } } ] }" ,
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            } ).replace( "'" , "\"" ).replace( "[MODID]" , Base.modId );

            //----------------------------------------------------------------------

                model = String.join( "\n" , new String[] {
                //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    "{ 'parent'  : '[MODID]:block/base'                " ,
                    ", 'textures': { 'tex' : '[MODID]:blocks/[TEX]' } }" ,
                //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                } ).replace( "'" , "\"" ).replace( "[MODID]" , Base.modId );

            //----------------------------------------------------------------------
            }

        //==========================================================================

        }

        public static class Generation {

        //==========================================================================

            public static void Blockstates() {
            //----------------------------------------------------------------------
                if( Blocks.compressions.isEmpty() ) return;
            //----------------------------------------------------------------------

                FileSystem mod = Resources.mod;
                FileSystem tmp = Resources.tmp;

            //----------------------------------------------------------------------
                for( Block block: Blocks.compressions ) {
            //----------------------------------------------------------------------

                    ResourceLocation loc = block.getRegistryName();

                    if( null == loc ) throw new NullPointerException();

                //------------------------------------------------------------------

                    String tex = loc.getResourcePath();
                    String json = column.replace( "[TEX]" , tex );

                //------------------------------------------------------------------

                    String name = "/[A]/" + Base.modId + "/[BS]/" + tex + ".json";

                    name = name.replace( "[A]"  , "assets" );
                    name = name.replace( "[BS]" , "blockstates" );

                //------------------------------------------------------------------

                    if( null != mod )
                        if( !Files.exists( mod.getPath( name ) ) )
                            Resources.Write( json , mod.getPath( name ) );

                    if( null != tmp )
                        if( !Files.exists( tmp.getPath( name ) ) )
                            Resources.Write( json , tmp.getPath( name ) );

            //----------------------------------------------------------------------
            } }

            public static void Models() {
            //----------------------------------------------------------------------
                if( Blocks.compressions.isEmpty() ) return;
            //----------------------------------------------------------------------

                FileSystem mod = Resources.mod;
                FileSystem tmp = Resources.tmp;

            //----------------------------------------------------------------------

                String baseName = "/[A]/" + Base.modId + "/[M]/[B]/base.json";

                baseName = baseName.replace( "[A]" , "assets" );
                baseName = baseName.replace( "[M]" , "models" );
                baseName = baseName.replace( "[B]" , "block" );

                String baseJSON = base;

                if( null != mod )
                    if( !Files.exists( mod.getPath( baseName ) ) )
                        Resources.Write( baseJSON , mod.getPath( baseName ) );

                if( null != tmp )
                    if( !Files.exists( tmp.getPath( baseName ) ) )
                        Resources.Write( baseJSON , tmp.getPath( baseName ) );

            //----------------------------------------------------------------------
                for( Block block: Blocks.compressions ) {
            //----------------------------------------------------------------------

                    ResourceLocation loc = block.getRegistryName();

                    if( null == loc ) throw new NullPointerException();

                //------------------------------------------------------------------

                    String tex = loc.getResourcePath();
                    String json = model.replace( "[TEX]" , tex );

                //------------------------------------------------------------------

                    String nameB = "/[A]/" +Base.modId+ "/[M]/[B]/" + tex + ".json";
                    String nameI = "/[A]/" +Base.modId+ "/[M]/[I]/" + tex + ".json";

                    nameB = nameB.replace( "[A]" , "assets" );
                    nameB = nameB.replace( "[M]" , "models" );
                    nameB = nameB.replace( "[B]" , "block" );

                    nameI = nameI.replace( "[A]" , "assets" );
                    nameI = nameI.replace( "[M]" , "models" );
                    nameI = nameI.replace( "[I]" , "item" );

                //------------------------------------------------------------------

                    if( null != mod )
                        if( !Files.exists( mod.getPath( nameB ) ) )
                            Resources.Write( json , mod.getPath( nameB ) );

                    if( null != tmp )
                        if( !Files.exists( tmp.getPath( nameB ) ) )
                            Resources.Write( json , tmp.getPath( nameB ) );

                    if( null != mod )
                        if( !Files.exists( mod.getPath( nameI ) ) )
                            Resources.Write( json , mod.getPath( nameI ) );

                    if( null != tmp )
                        if( !Files.exists( tmp.getPath( nameI ) ) )
                            Resources.Write( json , tmp.getPath( nameI ) );

            //----------------------------------------------------------------------
            } }

        //==========================================================================

        }

    //==============================================================================

    }

//==================================================================================

