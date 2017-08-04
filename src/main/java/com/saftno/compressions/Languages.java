//==================================================================================

    package com.saftno.compressions;

//==================================================================================

    import net.minecraft.client.Minecraft;
    import net.minecraft.client.resources.Language;
    import net.minecraft.client.resources.LanguageManager;
    import net.minecraft.item.ItemStack;
    import net.minecraft.util.ResourceLocation;
    import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
    import org.apache.commons.io.IOUtils;
    import org.apache.commons.lang3.StringUtils;

//==================================================================================

    import java.io.IOException;
    import java.io.InputStream;
    import java.nio.file.FileSystem;
    import java.nio.file.Files;

//==================================================================================
    @SuppressWarnings( { "WeakerAccess" , "unused" } )
//==================================================================================

    public class Languages {

    //==============================================================================

        public static String fNew = "";
        public static String fOld = "";

    //==============================================================================

        static /* set file locations */ {
        //--------------------------------------------------------------------------
            Minecraft minecraft = Minecraft.getMinecraft();
        //--------------------------------------------------------------------------

            LanguageManager manager  = minecraft.getLanguageManager();
            Language        language = manager.getCurrentLanguage();
            String          code     = language.getLanguageCode();

        //--------------------------------------------------------------------------

            String front = code.split( "_" )[0];
            String back  = code.split( "_" )[1];

        //--------------------------------------------------------------------------

            code = front + "_" + back.toLowerCase();

            fNew = "/assets/" + Base.modId + "/lang/" + code + ".lang";

        //--------------------------------------------------------------------------

            code = front + "_" + back.toUpperCase();

            fOld = "/assets/" + Base.modId + "/lang/" + code + ".lang";

        //--------------------------------------------------------------------------
        }

    //==============================================================================

        public static class Generation {

        //==========================================================================

            public static void LANG() { try {
            //----------------------------------------------------------------------
                if( Blocks.compressions.isEmpty() ) return;
            //----------------------------------------------------------------------

                FileSystem mod = Resources.mod;
                FileSystem tmp = Resources.tmp;

            //----------------------------------------------------------------------

                String previous = "";

            //----------------------------------------------------------------------
                if( null != mod ) { if( Files.exists( mod.getPath( fNew ) ) ) {
            //----------------------------------------------------------------------

                    InputStream input = Files.newInputStream( mod.getPath( fNew ) );

                    previous = IOUtils.toString( input , "utf-8" );

                    input.close();

            //----------------------------------------------------------------------
                } }
            //----------------------------------------------------------------------

                String entries = "";

            //----------------------------------------------------------------------
                for( Blocks.Compressed block : Blocks.compressions ) {
            //----------------------------------------------------------------------

                    ItemStack base = block.stem;

                //------------------------------------------------------------------

                    ResourceLocation loc = block.getRegistryName();

                    if( null == loc ) return;

                    String name = loc.getResourcePath();
                    String desc = block.level + "x " + base.getDisplayName();

                //------------------------------------------------------------------

                    if( previous.contains( "tile." + name + ".name=" ) ) continue;

                //------------------------------------------------------------------

                    entries = entries.concat( "\ntile." + name + ".name=" + desc );

            //----------------------------------------------------------------------
                }
            //----------------------------------------------------------------------

                if( entries.isEmpty() ) return;

                entries = "\n" + entries + "\n\n#" + StringUtils.repeat( "=" , 99 );

            //----------------------------------------------------------------------

                if( null != mod ) Resources.Append( entries , mod.getPath( fNew ) );
                if( null != mod ) Resources.Append( entries , mod.getPath( fOld ) );

                if( null != tmp ) Resources.Append( entries , tmp.getPath( fNew ) );
                if( null != tmp ) Resources.Append( entries , tmp.getPath( fOld ) );

            //----------------------------------------------------------------------
            } catch ( IOException e ) { e.printStackTrace(); } }

        //==========================================================================

        }

    //==============================================================================

    }

//==================================================================================

