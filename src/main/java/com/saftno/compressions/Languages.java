//==========================================================================================

    package com.saftno.compressions;

//==========================================================================================

    import net.minecraft.client.Minecraft;
    import net.minecraft.client.resources.LanguageManager;
    import net.minecraft.item.ItemStack;
    import net.minecraft.util.ResourceLocation;
    import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
    import org.apache.commons.io.IOUtils;
    import org.apache.commons.lang3.StringUtils;

//==========================================================================================

    import java.io.IOException;
    import java.io.InputStream;
    import java.nio.file.FileSystem;
    import java.nio.file.Files;

//==========================================================================================

    @SuppressWarnings( "WeakerAccess" ) class Languages {

    //======================================================================================

        static String file = "";
        static String fileLegacy = "";

    //======================================================================================

        static class Initialization {

        //==================================================================================

            static void Pre( @SuppressWarnings("unused") FMLPreInitializationEvent event ) {
            //------------------------------------------------------------------------------

                LanguageManager manager = Minecraft.getMinecraft().getLanguageManager();
                String          code    = manager.getCurrentLanguage().getLanguageCode();

            //------------------------------------------------------------------------------

                code = code.split( "_" )[0] + "_" + code.split( "_" )[1].toLowerCase();

                file = "/assets/" + Base.modId + "/lang/" + code + ".lang";

            //------------------------------------------------------------------------------

                code = code.split( "_" )[0] + "_" + code.split( "_" )[1].toUpperCase();

                fileLegacy = "/assets/" + Base.modId + "/lang/" + code + ".lang";

            //------------------------------------------------------------------------------
            }

        //==================================================================================

        }

        static class Generation {

        //==================================================================================

            static void LANG() { try {
            //------------------------------------------------------------------------------

                FileSystem mod = Resources.mod;
                FileSystem tmp = Resources.tmp;

            //------------------------------------------------------------------------------

                String previous = "";

            //------------------------------------------------------------------------------
                if( null != mod ) { if( Files.exists( mod.getPath( file ) ) ) {
            //------------------------------------------------------------------------------

                    InputStream input = Files.newInputStream( mod.getPath( file ) );

                    previous = IOUtils.toString( input , "utf-8" );

                    input.close();

            //------------------------------------------------------------------------------
                } }
            //------------------------------------------------------------------------------

                String entries = "";

                int L1 = Blocks.Generation.blocks.length;
                int L2 = Configurations.getDepth() + 1;

            //------------------------------------------------------------------------------
                for( int y = 0; y < L1; y++ ) { for( int x = 1; x < L2; x++ ) {
            //------------------------------------------------------------------------------

                    Blocks.Compressed stack = Blocks.Generation.blocks[y][x];
                    ItemStack base  = stack.stem;

                //--------------------------------------------------------------------------

                    ResourceLocation loc = stack.getRegistryName();

                    if( null == loc ) return;

                    String name = loc.getResourcePath();
                    String desc = x + "x " + base.getDisplayName();

                //--------------------------------------------------------------------------

                    if( previous.contains( "tile." + name + ".name=" ) ) continue;

                //--------------------------------------------------------------------------

                    entries = entries.concat( "\n" + "tile." + name + ".name=" + desc );

            //------------------------------------------------------------------------------
                } }
            //------------------------------------------------------------------------------

                if( entries.isEmpty() ) return;

                entries = "\n" + entries + "\n\n#" + StringUtils.repeat("=" , 99);

            //------------------------------------------------------------------------------

                if( null != mod ) Resources.Append( entries , mod.getPath( file ) );
                if( null != mod ) Resources.Append( entries , mod.getPath( fileLegacy ) );

                if( null != tmp ) Resources.Append( entries , tmp.getPath( file ) );
                if( null != tmp ) Resources.Append( entries , tmp.getPath( fileLegacy ) );

            //------------------------------------------------------------------------------
            } catch ( IOException e ) { e.printStackTrace(); } }

        //==================================================================================

        }

    //======================================================================================

    }

//==========================================================================================

