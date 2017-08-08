//==================================================================================

    package com.saftno.compressions;

//==================================================================================

    import net.minecraft.block.Block;
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
    import java.util.*;

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

                Logging.file( "Languages - LANG: start" );

            //----------------------------------------------------------------------
                if( Blocks.blocks.values.isEmpty() ) return;
            //----------------------------------------------------------------------

                FileSystem mod = Resources.mod;
                FileSystem tmp = Resources.tmp;

            //----------------------------------------------------------------------

                String[] content = new String[0];

                Logging.file( "Languages - LANG - content: start" );
            //----------------------------------------------------------------------
                if( null != mod ) { if( Files.exists( mod.getPath( fNew ) ) ) {
            //----------------------------------------------------------------------

                    InputStream input = Files.newInputStream( mod.getPath( fNew ) );

                    content = IOUtils.toString( input , "utf-8" ).split("\n");

                    input.close();

            //----------------------------------------------------------------------
                } }
            //----------------------------------------------------------------------

                Logging.file( "Languages - LANG - content: end" );

                List<String> previous = new ArrayList<>( Arrays.asList( content ) );
                previous.removeIf( s -> !s.contains( "tile." ) );

                Set<String> existing = new HashSet<>();

                Logging.file( "Languages - LANG - existing: start" );
            //----------------------------------------------------------------------
                for( String line : previous ) {
            //----------------------------------------------------------------------

                    String name = line.split( "tile." )[1].split( ".name" )[0];

                    existing.add( name );

            //----------------------------------------------------------------------
                }
            //----------------------------------------------------------------------

                Logging.file( "Languages - LANG - existing: end" );

                String entries = "";

            //----------------------------------------------------------------------
                for( Block entry : Blocks.blocks ) {
            //----------------------------------------------------------------------

                    Logging.file( "Languages - LANG - block: " + entry.getUnlocalizedName() );

                    if( !( entry instanceof Blocks.Compressed) ) continue;

                    Blocks.Compressed block = (Blocks.Compressed) entry;

                    ItemStack base = block.stem;

                    Logging.file( "Languages - LANG - block: end" );

                //------------------------------------------------------------------

                    Logging.file( "Languages - LANG - loc: start" );

                    ResourceLocation loc = block.getRegistryName();

                    if( null == loc ) return;

                    Logging.file( "Languages - LANG - loc: end" );

                    String name = loc.getResourcePath();
                    String desc = block.level + "x " + base.getDisplayName();

                //------------------------------------------------------------------

                    if( existing.contains( name ) ) continue;

                //------------------------------------------------------------------

                    Logging.file( "Languages - LANG - entries: add" );

                    entries = entries.concat( "\ntile." + name + ".name=" + desc );

            //----------------------------------------------------------------------
                }
            //----------------------------------------------------------------------

                if( entries.isEmpty() ) return;

                entries = "\n" + entries + "\n\n#" + StringUtils.repeat( "=" , 99 );

                Logging.file( "Languages - LANG - entries: end" );

            //----------------------------------------------------------------------

                Logging.file( "Languages - LANG - mod: " + mod );
                Logging.file( "Languages - LANG - mod: start" );

                if( null != mod ) Resources.Append( entries , mod.getPath( fNew ) );
                if( null != mod ) Resources.Append( entries , mod.getPath( fOld ) );

                Logging.file( "Languages - LANG - mod: end" );

                Logging.file( "Languages - LANG - tmp: " + tmp );
                Logging.file( "Languages - LANG - tmp: start" );

                if( null != tmp ) Resources.Append( entries , tmp.getPath( fNew ) );
                if( null != tmp ) Resources.Append( entries , tmp.getPath( fOld ) );

                Logging.file( "Languages - LANG - tmp: end" );

            //----------------------------------------------------------------------
            } catch ( IOException e ) { e.printStackTrace(); } }

        //==========================================================================

        }

    //==============================================================================

    }

//==================================================================================

