//==============================================================================================

    package com.saftno.compressions;

//==============================================================================================

    import com.google.common.collect.*;
    import com.saftno.compressions.Configurations.Entry;
    import com.saftno.compressions.ItemBlocks.Compressed.ItemX;
    import com.saftno.compressions.ResourcePacks.Type;

//==============================================================================================

    import net.minecraft.client.Minecraft;
    import net.minecraft.client.resources.LanguageManager;

//==============================================================================================

    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.util.*;
    import java.util.stream.Collectors;

//==============================================================================================

    public class Languages {
    //==========================================================================================
    // Setup
    //==========================================================================================

        public static void Register() {
        //--------------------------------------------------------------------------------------

            Generate();

        //--------------------------------------------------------------------------------------
        }

        public static void Generate() { try {
        //--------------------------------------------------------------------------------------

            LanguageManager manager  = Minecraft.getMinecraft().getLanguageManager();
            String          code     = manager.getCurrentLanguage().getLanguageCode();

            String front = code.split( "_" )[0];
            String back  = code.split( "_" )[1];

            Path lower = ResourcePacks.Path( Type.LANGUAGE , front + "_" + back.toLowerCase() );
            Path upper = ResourcePacks.Path( Type.LANGUAGE , front + "_" + back.toUpperCase() );

        //--------------------------------------------------------------------------------------

            Map<String , String> lines = new HashMap<>();

            if( Files.exists( lower ) )
                lines = Files.newBufferedReader( lower )
                             .lines()
                             .collect( Collectors.toMap( s -> s.split( "=" )[0] ,
                                                         s -> s.split( "=" )[1] ) );

        //--------------------------------------------------------------------------------------
            for( ItemBlocks.Compressed compressed : ItemBlocks.entries ) {
        //--------------------------------------------------------------------------------------

                Entry  entry = compressed.getEntry();

                String extra = entry.NBTAsExtraDescription();

            //----------------------------------------------------------------------------------
                for( ItemX item : compressed.items ) {
            //----------------------------------------------------------------------------------

                    String name = item.getRegistryName().getResourcePath().toString();
                    String desc = item.level+ "x" +entry.Width+ " " +item.base.getDisplayName();

                    lines.put( "tile." + name + ".name" , desc + extra );
                    lines.put( "item." + name + ".name" , desc + extra );

        //--------------------------------------------------------------------------------------
            } }
        //--------------------------------------------------------------------------------------

            List<String> keys = new ArrayList<>( lines.keySet() );
            Collections.sort( keys );

            String content = "";

            for( String key : keys ) content += key + "=" + lines.get( key ) + "\n";

        //--------------------------------------------------------------------------------------

            ResourcePacks.Write( content , Type.LANGUAGE , front + "_" + back.toLowerCase() );

        //--------------------------------------------------------------------------------------
        } catch( IOException ex ) { ex.printStackTrace(); } }

    //==========================================================================================

    }

//==============================================================================================
