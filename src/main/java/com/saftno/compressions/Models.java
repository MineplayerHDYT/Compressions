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
            "               , 'inventory' : [ { } ] } }                          " ,
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
            "               , 'inventory' : [ { } ] } }                          " ,
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
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

        //==========================================================================

        }

    //==============================================================================

    }

//==================================================================================

