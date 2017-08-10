//==================================================================================

    package com.saftno.compressions;

//==================================================================================

    //==================================================================================

    import net.minecraft.block.Block;
    import net.minecraft.client.Minecraft;
    import net.minecraft.client.renderer.ItemModelMesher;
    import net.minecraft.client.renderer.block.model.IBakedModel;
    import net.minecraft.client.renderer.block.model.ModelResourceLocation;
    import net.minecraft.item.Item;
    import net.minecraft.item.ItemStack;
    import net.minecraft.util.ResourceLocation;
    import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
    import net.minecraftforge.common.MinecraftForge;

//==================================================================================

    import java.nio.file.FileSystem;
    import java.nio.file.Files;
    import java.util.HashSet;
    import java.util.Set;

//==================================================================================
    @SuppressWarnings( { "WeakerAccess" , "unused" } ) // @Mod.EventBusSubscriber
//==================================================================================

    public class Models {

    //==============================================================================

        public static Boolean over = false;

    //==============================================================================

        public static Set<String> models = new HashSet<>();

    //==============================================================================

        public static String cube;
        public static String column;

    //==============================================================================

        static /* Set cube and column */ {
        //--------------------------------------------------------------------------
            cube = String.join( "\n" , new String[] {
        //--------------------------------------------------------------------------

            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            "{ 'forge_marker' : 1                                                " ,
            ", 'defaults' : { 'model'     : 'cube_all'                           " ,
            "               , 'textures'  : { 'all' : '[MODID]:blocks/[TEX]' } } " ,
            ", 'variants' : { 'normal'    : [ { } ]                              " ,
            "               , 'inventory' : [ { } ]                              " ,
            "               , 'gui      ' : [ { } ] } }                          " ,
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        //--------------------------------------------------------------------------
            } ).replace( "'" , "\"" ).replace( "[MODID]" , Base.modId );
        //--------------------------------------------------------------------------

        //--------------------------------------------------------------------------
            column = String.join( "\n" , new String[] {
        //--------------------------------------------------------------------------

            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            "{ 'forge_marker' : 1                                                " ,
            ", 'defaults' : { 'model'     : 'cube_column'                        " ,
            "               , 'textures'  : { 'end' : '[MODID]:blocks/[TEX]'     " ,
            "                               , 'side': '[MODID]:blocks/[TEX]' } } " ,
            ", 'variants' : { 'normal'    : [ { } ]                              " ,
            "               , 'inventory' : [ { } ]                              " ,
            "               , 'gui      ' : [ { } ] } }                          " ,
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        //--------------------------------------------------------------------------
            } ).replace( "'" , "\"" ).replace( "[MODID]" , Base.modId );
        //--------------------------------------------------------------------------
        }

    //==========================================================================
        // @SubscribeEvent
    //==========================================================================

        public static void Register( DrawScreenEvent event ) {
        //--------------------------------------------------------------------------
            if( !Resources.storage.isOpen() ) return;
            //if( null != Resources.mod ) if( !Resources.mod.isOpen()
            //        ) return;
        //--------------------------------------------------------------------------

            Models.Generation.Blockstates();

        //--------------------------------------------------------------------------

            ItemModelMesher mesher =  Minecraft.getMinecraft()
                                               .getRenderItem()
                                               .getItemModelMesher();

        //--------------------------------------------------------------------------
            for( Item item : Items.items ) {
        //--------------------------------------------------------------------------


                ResourceLocation rLoc = item.getRegistryName();
                ModelResourceLocation mrLoc = new ModelResourceLocation(rLoc,
                        "inventory");

            //----------------------------------------------------------------------

                ItemStack stack = new ItemStack( item , 1 , 0 );

                String ID = Base.UID( stack );
                if( models.contains( ID ) ) continue;

                IBakedModel model = mesher.getItemModel( stack );

                String name = model.toString();
                if( name.contains( "FancyMissingModel" ) ) continue;

                mesher.register( item , 0 , mrLoc );

                models.add( ID );

        //--------------------------------------------------------------------------
            }
        //----------------------------------------------------------------------

            over = true;

        //----------------------------------------------------------------------

            MinecraftForge.EVENT_BUS.unregister( Models.class );
        //--------------------------------------------------------------------------
        }

    //==============================================================================

        public static class Generation {

        //==========================================================================

            public static void Blockstates() {
            //----------------------------------------------------------------------
                if( Blocks.blocks.values.isEmpty() ) return;
            //----------------------------------------------------------------------

                //FileSystem mod = Resources.mod;
                FileSystem tmp = Resources.storage;

            //----------------------------------------------------------------------
                for( Block block: Blocks.blocks ) {
            //----------------------------------------------------------------------

                    ResourceLocation loc = block.getRegistryName();

                    if( null == loc ) return;

                //------------------------------------------------------------------

                    String tex = loc.getResourcePath();
                    String json = column.replace( "[TEX]" , tex );

                //------------------------------------------------------------------

                    String name = "/[A]/" + Base.modId + "/[BS]/" + tex + ".json";

                    name = name.replace( "[A]"  , "assets" );
                    name = name.replace( "[BS]" , "blockstates" );

                //------------------------------------------------------------------

                    //if( null != mod )
                    //    if( !Files.exists( mod.getPath( name ) ) )
                    //        Resources.Write( json , mod.getPath( name ) );

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

