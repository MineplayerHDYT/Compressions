//==============================================================================================

    package com.saftno.compressions;

//==============================================================================================

    import com.saftno.compressions.ItemBlocks.Compressed;
    import com.saftno.compressions.ItemBlocks.Compressed.ItemX;
    import com.saftno.compressions.ResourcePacks.Type;

//==============================================================================================

    import net.minecraft.client.Minecraft;
    import net.minecraft.client.renderer.block.model.ModelResourceLocation;
    import net.minecraft.util.ResourceLocation;
    import org.apache.commons.io.IOUtils;

//==============================================================================================

    import java.io.IOException;
    import java.io.InputStream;
    import java.nio.file.Files;
    import java.nio.file.Path;

//==============================================================================================

    public class Models {

    //==========================================================================================

        public static void Register() {
        //--------------------------------------------------------------------------------------

            Generate();

        //--------------------------------------------------------------------------------------
            for(Compressed compressed: ItemBlocks.entries) { for(ItemX item: compressed.items) {
        //--------------------------------------------------------------------------------------

                ResourceLocation      rLoc  = item.getRegistryName();
                ModelResourceLocation mrLoc = new ModelResourceLocation( rLoc , "inventory" );

                Minecraft.getMinecraft()
                         .getRenderItem()
                         .getItemModelMesher()
                         .register( item , 0 , mrLoc );

        //--------------------------------------------------------------------------------------
            } }
        //--------------------------------------------------------------------------------------
        }

        public static void Generate() { try {
        //--------------------------------------------------------------------------------------
            final String column = String.join( "\n" , new String[] {
        //--------------------------------------------------------------------------------------

                "{ 'forge_marker' : 1                                                  ",
                ", 'defaults' : { 'model'     : 'cube_column'                          ",
                "               , 'textures'  : { 'end' : '[MODID]:blocks/[ENTRY]'     ",
                "                               , 'side': '[MODID]:blocks/[ENTRY]' } } ",
                ", 'variants' : { 'normal'    : [ { } ]                                ",
                "               , 'inventory' : [ { } ] } }                            "

        //--------------------------------------------------------------------------------------
            } ).replace( "'" , "\"" ).replace( "[MODID]" , Base.modId );
        //--------------------------------------------------------------------------------------

        //--------------------------------------------------------------------------------------
            for(Compressed compressed: ItemBlocks.entries) { for(ItemX item: compressed.items) {
        //--------------------------------------------------------------------------------------

                String name = item.getRegistryName().getResourcePath();
                String json = column.replace( "[ENTRY]" , name );

            //--------------------------------------------------------------------------------------

                Path        path   = ResourcePacks.Path( Type.MODEL , name );
                InputStream stream = Files.exists(path)? Files.newInputStream(path): null;
                String      data   = null == stream ? "" : IOUtils.toString( stream , "utf8" );

                if( !data.equals( json ) ) ResourcePacks.Write( json , Type.MODEL , name );

        //--------------------------------------------------------------------------------------
            } }
        //--------------------------------------------------------------------------------------
        } catch( IOException ex ) { ex.printStackTrace(); } }

    //==========================================================================================

    }
//==============================================================================================

